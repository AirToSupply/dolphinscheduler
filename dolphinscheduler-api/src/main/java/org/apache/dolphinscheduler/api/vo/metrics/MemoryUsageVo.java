package org.apache.dolphinscheduler.api.vo.metrics;

import org.apache.dolphinscheduler.dao.entity.NodeMetricsSnapshot;

import java.util.Date;

public class MemoryUsageVo {

    private int id;

    private Date reportTime;

    private Double memoryUsage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public MemoryUsageVo(int id, Date reportTime, Double memoryUsage) {
        this.id = id;
        this.reportTime = reportTime;
        this.memoryUsage = memoryUsage;
    }

    public static MemoryUsageVo of(NodeMetricsSnapshot snapshot) {
        return new MemoryUsageVo(snapshot.getId(), snapshot.getReportTime(), snapshot.getMemoryUsage());
    }
}
