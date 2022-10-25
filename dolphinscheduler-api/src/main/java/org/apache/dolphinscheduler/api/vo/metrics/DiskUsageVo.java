package org.apache.dolphinscheduler.api.vo.metrics;

import org.apache.dolphinscheduler.dao.entity.NodeMetricsSnapshot;

import java.util.Date;

public class DiskUsageVo {

    private int id;

    private Date reportTime;

    private Double diskUsage;

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

    public Double getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(Double diskUsage) {
        this.diskUsage = diskUsage;
    }

    public DiskUsageVo(int id, Date reportTime, Double diskUsage) {
        this.id = id;
        this.reportTime = reportTime;
        this.diskUsage = diskUsage;
    }

    public static DiskUsageVo of(NodeMetricsSnapshot snapshot) {
        return new DiskUsageVo(snapshot.getId(), snapshot.getReportTime(), snapshot.getDiskUsage());
    }
}
