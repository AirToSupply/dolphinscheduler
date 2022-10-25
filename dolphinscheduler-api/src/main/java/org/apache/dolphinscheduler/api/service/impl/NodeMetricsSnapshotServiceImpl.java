package org.apache.dolphinscheduler.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.exceptions.ServiceException;
import org.apache.dolphinscheduler.api.service.NodeMetricsSnapshotService;
import org.apache.dolphinscheduler.dao.entity.NodeMetricsSnapshot;
import org.apache.dolphinscheduler.dao.mapper.NodeMetricsSnapshotMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * node metrics snapshot service impl
 */
@Service
public class NodeMetricsSnapshotServiceImpl extends BaseServiceImpl implements NodeMetricsSnapshotService {

    private static final Logger logger = LoggerFactory.getLogger(NodeMetricsSnapshotServiceImpl.class);

    @Autowired
    private NodeMetricsSnapshotMapper nodeMetricsSnapshotMapper;

    @Override
    public List<NodeMetricsSnapshot> queryNodeMetricsSnapshot(String ip, Date startTime, Date endTime) {
        return nodeMetricsSnapshotMapper.selectList(new QueryWrapper<NodeMetricsSnapshot>()
            .lambda()
            .eq(NodeMetricsSnapshot::getIp, ip)
            .between(NodeMetricsSnapshot::getReportTime, startTime, endTime));
    }

    @Override
    public Map<String, Object> insertNodeMetricsSnapshot(NodeMetricsSnapshot nodeMetricsSnapshot) {
        Map<String, Object> result = new HashMap<>();
        int insertRow = nodeMetricsSnapshotMapper.insert(nodeMetricsSnapshot);
        if (insertRow == 0) {
            putMsg(result, Status.NODE_EXPORT_ERROR);
            throw new ServiceException(Status.NODE_EXPORT_ERROR);
        }
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> deleteNodeMetricsSnapshot(long retentionSeconds) {
        Map<String, Object> result = new HashMap<>();
        nodeMetricsSnapshotMapper.delete(
            new QueryWrapper<NodeMetricsSnapshot>().lambda()
                .lt(NodeMetricsSnapshot::getReportTime,
                    new Date(System.currentTimeMillis() - 1000 * retentionSeconds)));
        putMsg(result, Status.SUCCESS);
        return result;
    }


}
