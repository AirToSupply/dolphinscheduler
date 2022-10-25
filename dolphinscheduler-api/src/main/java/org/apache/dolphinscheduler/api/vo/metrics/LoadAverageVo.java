package org.apache.dolphinscheduler.api.vo.metrics;

import org.apache.dolphinscheduler.dao.entity.NodeMetricsSnapshot;

import java.util.Date;

public class LoadAverageVo {

    private int id;

    private Date reportTime;

    private Double loadAverage;

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

    public Double getLoadAverage() {
        return loadAverage;
    }

    public void setLoadAverage(Double loadAverage) {
        this.loadAverage = loadAverage;
    }

    public LoadAverageVo(int id, Date reportTime, Double loadAverage) {
        this.id = id;
        this.reportTime = reportTime;
        this.loadAverage = loadAverage;
    }

    public static LoadAverageVo of(NodeMetricsSnapshot snapshot) {
        return new LoadAverageVo(snapshot.getId(), snapshot.getReportTime(), snapshot.getLoadAverage());
    }
}
