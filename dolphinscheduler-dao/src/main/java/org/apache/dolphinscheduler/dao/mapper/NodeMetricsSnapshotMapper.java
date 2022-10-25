package org.apache.dolphinscheduler.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.dolphinscheduler.dao.entity.NodeMetricsSnapshot;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * node metrics snapshot interface
 */
public interface NodeMetricsSnapshotMapper extends BaseMapper<NodeMetricsSnapshot> {
}
