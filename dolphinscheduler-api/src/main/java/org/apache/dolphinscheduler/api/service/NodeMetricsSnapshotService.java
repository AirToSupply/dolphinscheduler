package org.apache.dolphinscheduler.api.service;

import org.apache.dolphinscheduler.dao.entity.NodeMetricsSnapshot;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * node metrics snapshot service
 */
public interface NodeMetricsSnapshotService {
    /**
     * query node metrics snapshot
     *
     * @param ip host ip
     * @param startTime start time
     * @param endTime end time
     * @return node metrics snapshot list
     */
    List<NodeMetricsSnapshot> queryNodeMetricsSnapshot(String ip, Date startTime, Date endTime);

    /**
     * insert node metrics snapshot
     *
     * @param nodeMetricsSnapshot node metrics snapshot
     * @return insert result code
     */
    Map<String, Object> insertNodeMetricsSnapshot(NodeMetricsSnapshot nodeMetricsSnapshot);

    /**
     * clear expired node monitoring indicators
     *
     * @param retentionSeconds retention seconds
     * @return delete result code
     */
    Map<String, Object> deleteNodeMetricsSnapshot(long retentionSeconds);
}
