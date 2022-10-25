package org.apache.dolphinscheduler.api.service;

import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.dao.entity.NodeMetrics;

import java.util.Map;

/**
 * node metrics service
 */
public interface NodeMetricsService {
    /**
     * query node metrics paging
     *
     * @param pageNo page number
     * @param pageSize page size
     * @param ip node ip
     * @param state node state
     * @return node metrics list page
     */
    Result queryNodeMetricsListPaging(Integer pageNo, Integer pageSize, String ip, Integer state);

    /**
     * upsert node metrics
     *
     * @param nodeMetrics
     * @return upsert result code
     */
    Map<String, Object> upsertNodeMetrics(NodeMetrics nodeMetrics);

    /**
     * flush node state
     * @param timeoutSeconds timeout
     * @return update result code
     */
    Map<String, Object> updateNodeState(Long timeoutSeconds);
}
