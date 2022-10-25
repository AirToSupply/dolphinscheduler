package org.apache.dolphinscheduler.server.worker.metrics;

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
import org.apache.dolphinscheduler.server.worker.config.WorkerConfig;
import org.apache.dolphinscheduler.service.registry.RegistryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * DolphinScheduler worker node exporter, used to process the system indicator monitoring component of the current node.
 */
@Component
public class WorkerNodeExporter implements AutoCloseable {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(WorkerNodeExporter.class);

    @Autowired
    private NodeMetricsService nodeMetricsService;

    @Autowired
    private NodeMetricsSnapshotService nodeMetricsSnapshotService;

    @Autowired
    private NodeDiskService nodeDiskService;

    /**
     * worker config
     */
    @Autowired
    private WorkerConfig workerConfig;

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

    private String workerAddress;

    private boolean includeMasterAddresses;

    private void init() {
        this.workerAddress = NetUtils.getAddr(workerConfig.getListenPort());
        this.nodeExportExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("NodeExportExecutor"));
        this.renewExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("RenewExecutor"));
    }

    public void start() {
        this.includeMasterAddresses = registryClient.checkNodeExists(NetUtils.getHost(), NodeType.MASTER);

        if (!workerConfig.isNodeExportEnable()) {
            logger.info("Worker Node Exporter is not enabled, please setting 'worker.node-export-enable'!");
            return;
        }

        if (this.includeMasterAddresses) {
            logger.info("When current Worker Node and Master Node run at the same node, the current worker node exporter can not startup!");
            return;
        }

        init();

        try {
            logger.info("Starting Worker Node Exporter ...");
            renew();
            export();
            logger.info("Started Worker Node Exporter ...");
        } catch (Exception e) {
            throw new RuntimeException("worker node exporter start up error", e);
        }
    }

    private void export() {
        this.nodeExportExecutor.scheduleAtFixedRate(() -> {
            nodeMetricsSnapshotService.insertNodeMetricsSnapshot(NodeMetricsSnapshot.export());
            nodeDiskService.saveNodeDisk(OSUtils.ip(), NodeDisk.export());
        }, 0L, workerConfig.getNodeExportInterval().getSeconds(), TimeUnit.SECONDS);
        logger.info("Started Worker Node Exporter from {}", workerAddress);
    }

    private void renew() {
        this.renewExecutor.scheduleAtFixedRate(() -> {
            nodeMetricsService.upsertNodeMetrics(NodeMetrics.export());
        }, 0L, workerConfig.getNodeExportInterval().getSeconds(), TimeUnit.SECONDS);
        logger.info("Started Worker Node Exporter [renew] from {}", workerAddress);
    }

    @Override
    public void close() {
        if (!workerConfig.isNodeExportEnable() && this.includeMasterAddresses) {
            return;
        }
        try {
            logger.info("Closing Worker Node Exporter ...");
            this.nodeExportExecutor.shutdown();
            this.renewExecutor.shutdown();
            logger.info("Closed Worker Node Exporter ...");
        } catch (Exception e) {
            logger.error("worker node exporter failed, host:{}", workerAddress, e);
        }
    }
}
