package org.apache.dolphinscheduler.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.exceptions.ServiceException;
import org.apache.dolphinscheduler.api.service.NodeMetricsService;
import org.apache.dolphinscheduler.api.utils.PageInfo;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.common.enums.ReleaseState;
import org.apache.dolphinscheduler.dao.entity.NodeMetrics;
import org.apache.dolphinscheduler.dao.mapper.NodeMetricsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * node metrics service impl
 */
@Service
public class NodeMetricsServiceImpl extends BaseServiceImpl implements NodeMetricsService {

    private static final Logger logger = LoggerFactory.getLogger(NodeMetricsServiceImpl.class);

    @Autowired
    private NodeMetricsMapper nodeMetricsMapper;

    @Override
    public Result queryNodeMetricsListPaging(Integer pageNo, Integer pageSize, String ip, Integer state) {
        Result result = new Result();
        Page<NodeMetrics> page = new Page<>(pageNo, pageSize);
        IPage<NodeMetrics> nodeMetricsIPage = nodeMetricsMapper.queryNodeMetricsPaging(page, ip, state);
        PageInfo<NodeMetrics> pageInfo = new PageInfo<>(pageNo, pageSize);
        pageInfo.setTotal((int) nodeMetricsIPage.getTotal());
        pageInfo.setTotalList(nodeMetricsIPage.getRecords());
        result.setData(pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> upsertNodeMetrics(NodeMetrics nodeMetrics) {
        Map<String, Object> result = new HashMap<>();
        NodeMetrics target = nodeMetricsMapper.selectOne(
            new QueryWrapper<NodeMetrics>().lambda().eq(NodeMetrics::getIp, nodeMetrics.getIp()));
        int upsertRow = 0;
        if (target == null) {
            upsertRow = nodeMetricsMapper.insert(nodeMetrics);
        } else {
            upsertRow = nodeMetricsMapper.update(nodeMetrics,
                new UpdateWrapper<NodeMetrics>().lambda().eq(NodeMetrics::getIp, nodeMetrics.getIp()));
        }
        if (upsertRow == 0) {
            putMsg(result, Status.UPDATE_NODE_METRICS_ERROR);
            throw new ServiceException(Status.UPDATE_NODE_METRICS_ERROR);
        }
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @Override
    public Map<String, Object> updateNodeState(Long timeoutSeconds) {
        Map<String, Object> result = new HashMap<>();
        List<NodeMetrics> nodeMetrics = nodeMetricsMapper.selectList(null);
        if (nodeMetrics == null) {
            putMsg(result, Status.SUCCESS);
            return result;
        }
        for (NodeMetrics node : nodeMetrics) {
            node.setState(
                System.currentTimeMillis() - node.getUpdateTime().getTime() > 1000 * timeoutSeconds?
                    ReleaseState.OFFLINE :
                    ReleaseState.ONLINE);
            nodeMetricsMapper.update(node,
                new UpdateWrapper<NodeMetrics>().lambda().eq(NodeMetrics::getIp, node.getIp()));
        }
        return result;
    }

}
