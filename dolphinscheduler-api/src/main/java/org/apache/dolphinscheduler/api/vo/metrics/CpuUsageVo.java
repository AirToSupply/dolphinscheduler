package org.apache.dolphinscheduler.api.vo.metrics;

import org.apache.dolphinscheduler.dao.entity.NodeMetricsSnapshot;

import java.util.Date;

public class CpuUsageVo {

    private int id;

    private Date reportTime;

    private Double cpuUsage;

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

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public CpuUsageVo(int id, Date reportTime, Double cpuUsage) {
        this.id = id;
        this.reportTime = reportTime;
        this.cpuUsage = cpuUsage;
    }

    public static CpuUsageVo of(NodeMetricsSnapshot snapshot) {
        return new CpuUsageVo(snapshot.getId(), snapshot.getReportTime(), snapshot.getCpuUsage());
    }
}
