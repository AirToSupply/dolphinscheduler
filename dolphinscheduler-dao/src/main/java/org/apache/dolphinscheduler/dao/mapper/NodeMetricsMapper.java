package org.apache.dolphinscheduler.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.dolphinscheduler.dao.entity.NodeMetrics;
import org.apache.ibatis.annotations.Param;

/**
 * node metrics interface
 */
public interface NodeMetricsMapper extends BaseMapper<NodeMetrics> {

    IPage<NodeMetrics> queryNodeMetricsPaging(IPage<NodeMetrics> page,
                                              @Param("ip") String ip,
                                              @Param("state") Integer state);
}
