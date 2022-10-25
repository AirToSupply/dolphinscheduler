package org.apache.dolphinscheduler.api.service;

import org.apache.dolphinscheduler.dao.entity.NodeDisk;

import java.util.List;
import java.util.Map;

/**
 * node disk service
 */
public interface NodeDiskService {
    /**
     * save node disk
     *
     * @param ip
     * @param diskstore
     *
     * @return save result code
     */
    Map<String, Object> saveNodeDisk(String ip, List<NodeDisk> diskstore);

    /**
     * query node disk
     *
     * @param ip
     *
     * @return query node disk
     */
    List<NodeDisk> queryNodeDisk(String ip);
}
