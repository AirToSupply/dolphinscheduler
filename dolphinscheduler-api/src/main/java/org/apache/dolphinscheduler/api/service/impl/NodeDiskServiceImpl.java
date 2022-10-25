package org.apache.dolphinscheduler.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.service.NodeDiskService;
import org.apache.dolphinscheduler.dao.entity.NodeDisk;
import org.apache.dolphinscheduler.dao.mapper.NodeDiskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * node disk service impl
 */
@Service
public class NodeDiskServiceImpl extends BaseServiceImpl implements NodeDiskService {

    @Autowired
    private NodeDiskMapper nodeDiskMapper;

    @Override
    public Map<String, Object> saveNodeDisk(String ip, List<NodeDisk> diskstore) {
        Map<String, Object> result = new HashMap<>();
        nodeDiskMapper.delete(new QueryWrapper<NodeDisk>().lambda().eq(NodeDisk::getIp, ip));
        diskstore.stream().forEach(ds -> nodeDiskMapper.insert(ds));
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @Override
    public List<NodeDisk> queryNodeDisk(String ip) {
        return nodeDiskMapper.selectList(new QueryWrapper<NodeDisk>()
            .lambda()
            .eq(NodeDisk::getIp, ip)
            .orderByDesc(NodeDisk::getDiskUsage));
    }
}
