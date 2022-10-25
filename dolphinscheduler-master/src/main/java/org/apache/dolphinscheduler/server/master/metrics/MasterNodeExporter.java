package org.apache.dolphinscheduler.server.master.metrics;

import org.apache.dolphinscheduler.api.service.NodeDiskService;
import org.apache.dolphinscheduler.api.service.NodeMetricsService;
import org.apache.dolphinscheduler.api.service.NodeMetricsSnapshotService;
import org.apache.dolphinscheduler.common.enums.NodeType;
import org.apache.dolphinscheduler.common.utils.NetUtils;
import org.apache.dolphinscheduler.common.utils.OSUtils;
import org.apache.dolphinscheduler.dao.entity.NodeDisk;
import org.apache.dolphinscheduler.dao.entity.NodeMetrics;
import org.apache.dolphinscheduler.dao.entity.NodeMetricsSnapshot;
import org.apache.dolphinscheduler.remote.utils.NamedThreadFactory;
import org.apache.dolphinscheduler.server.master.config.MasterConfig;
import org.apache.dolphinscheduler.service.registry.RegistryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * DolphinScheduler master node exporter, used to process the system indicator monitoring component of the current node.
 * This component mainly has the following functions：
 *   (1) Monitor node system indicators and persistence;
 *   (2) Monitor the status of all deployment nodes and complete the status update;
 *   (3) Regularly clear the system indicators reported by all nodes.
 */
@Component
public class MasterNodeExporter implements AutoCloseable {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(MasterNodeExporter.class);

    @Autowired
    private NodeMetricsService nodeMetricsService;

    @Autowired
    private NodeMetricsSnapshotService nodeMetricsSnapshotService;

    @Autowired
    private NodeDiskService nodeDiskService;

    /**
     * master config
     */
    @Autowired
    private MasterConfig masterConfig;

    @Autowired
    private RegistryClient registryClient;

    /**
     * node export executor
     */
    private ScheduledExecutorService nodeExportExecutor;

    /**
     * node renew executor
     */
    private ScheduledExecutorService renewExecutor;

    /**
     * node state flush executor
     */
    private ScheduledExecutorService stateFlushExecutor;

    /**
     * node export snaphot retention executor
     */
    private ScheduledExecutorService retentionExecutor;

    private String masterAddress;

    private boolean includeWorkerAddresses;

    private void init() {
        this.masterAddress = NetUtils.getAddr(masterConfig.getListenPort());
        this.nodeExportExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("NodeExportExecutor"));
        this.renewExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("RenewExecutor"));
        this.stateFlushExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("StateFlushExecutor"));
        this.retentionExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("RetentionExecutor"));
    }

    public void start() {
        this.includeWorkerAddresses = registryClient.checkNodeExists(NetUtils.getHost(), NodeType.WORKER);

        if (!masterConfig.isNodeExportEnable()) {
            logger.info("Master Node Exporter is not enabled, please setting 'master.node-export-enable' !");
            return;
        }

        init();

        try {
            logger.info("Starting Master Node Exporter ...");

            if (this.includeWorkerAddresses) {
                logger.info("When current Master Node and Worker Node run at the same node, " +
                    "the master node exporter and renew service can not startup!");
            } else {
                renew();
                export();
            }

            TimeUnit.SECONDS.sleep(masterConfig.getHeartbeatInterval().getSeconds());

            stateChanged();
            retention();
            logger.info("Started Master Node Exporter ...");
        } catch (Exception e) {
            throw new RuntimeException("master node exporter start up error", e);
        }
    }

    private void export() {
        this.nodeExportExecutor.scheduleAtFixedRate(() -> {
            nodeMetricsSnapshotService.insertNodeMetricsSnapshot(NodeMetricsSnapshot.export());
            nodeDiskService.saveNodeDisk(OSUtils.ip(), NodeDisk.export());
        }, 0L, masterConfig.getNodeExportInterval().getSeconds(), TimeUnit.SECONDS);
        logger.info("Started Master Node Exporter from {}", masterAddress);
    }

    private void renew() {
        this.renewExecutor.scheduleAtFixedRate(() -> {
            nodeMetricsService.upsertNodeMetrics(NodeMetrics.export());
        }, 0L, masterConfig.getNodeExportInterval().getSeconds(), TimeUnit.SECONDS);
        logger.info("Started Master Node Exporter [renew] from {}", masterAddress);
    }

    private void stateChanged() {
        this.stateFlushExecutor.scheduleAtFixedRate(() -> {
            nodeMetricsService.updateNodeState(
                masterConfig.getHeartbeatInterval().getSeconds() * masterConfig.getNodeExportTick());
        },0L, masterConfig.getHeartbeatInterval().getSeconds(), TimeUnit.SECONDS);
        logger.info("Started Master Node Exporter [stateChanged] from {}", masterAddress);
    }

    private void retention() {
        this.retentionExecutor.scheduleAtFixedRate(() -> {
            nodeMetricsSnapshotService.deleteNodeMetricsSnapshot(masterConfig.getNodeExportRetentionSeconds().getSeconds());
        }, 0L, Duration.ofDays(1).getSeconds(), TimeUnit.SECONDS);
        logger.info("Started Master Node Exporter [retention] from {}", masterAddress);
    }


    @Override
    public void close() {
        if (!masterConfig.isNodeExportEnable()) {
            return;
        }
        try {
            logger.info("Closing Master Node Exporter ...");
            if (!this.includeWorkerAddresses) {
                this.nodeExportExecutor.shutdown();
                this.renewExecutor.shutdown();
            }
            this.stateFlushExecutor.shutdown();
            this.retentionExecutor.shutdown();
            logger.info("Closed Master Node Exporter ...");
        } catch (Exception e) {
            logger.error("master node exporter failed, host:{}", masterAddress, e);
        }
    }
}
