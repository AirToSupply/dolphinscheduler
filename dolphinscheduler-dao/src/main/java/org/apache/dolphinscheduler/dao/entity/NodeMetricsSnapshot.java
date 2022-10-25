package org.apache.dolphinscheduler.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.dolphinscheduler.common.utils.OSUtils;

import java.util.Date;
import java.util.Objects;

@TableName("t_ds_node_metrics_snapshot")
public class NodeMetricsSnapshot {
    /**
     * id
     */
    @TableId(value="id", type= IdType.AUTO)
    private int id;

    /**
     * update time
     */
    @TableField("report_time")
    private Date reportTime;

    /**
     * host ip
     */
    @TableField("ip")
    private String ip;

    /**
     * host name
     */
    @TableField("hostname")
    private String hostname;

    /**
     * cpu usage
     */
    @TableField("cpu_usage")
    private Double cpuUsage = 0.0;

    /**
     * memory usage
     */
    @TableField("memory_usage")
    private Double memoryUsage = 0.0;

    /**
     * disk usage
     */
    @TableField("disk_usage")
    private Double diskUsage = 0.0;

    /**
     * load average
     */
    @TableField("load_average")
    private Double loadAverage = 0.0;

    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Double getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(Double diskUsage) {
        this.diskUsage = diskUsage;
    }

    public Double getLoadAverage() {
        return loadAverage;
    }

    public void setLoadAverage(Double loadAverage) {
        this.loadAverage = loadAverage;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "NodeMetricsSnapshot{" +
            "id=" + id +
            ", reportTime=" + reportTime +
            ", ip='" + ip + '\'' +
            ", hostname='" + hostname + '\'' +
            ", cpuUsage=" + cpuUsage +
            ", memoryUsage=" + memoryUsage +
            ", diskUsage=" + diskUsage +
            ", loadAverage=" + loadAverage +
            ", updateTime=" + updateTime +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeMetricsSnapshot that = (NodeMetricsSnapshot) o;
        return id == that.id &&
                Objects.equals(reportTime, that.reportTime) &&
                Objects.equals(ip, that.ip) &&
                Objects.equals(hostname, that.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reportTime, ip, hostname);
    }

    public static NodeMetricsSnapshot export() {
        NodeMetricsSnapshot snapshot = new NodeMetricsSnapshot();
        snapshot.setReportTime(new Date());
        snapshot.setIp(OSUtils.ip());
        snapshot.setHostname(OSUtils.hostname());
        snapshot.setCpuUsage(OSUtils.cpuUsage());
        snapshot.setMemoryUsage(OSUtils.memoryUsage());
        snapshot.setDiskUsage(OSUtils.diskUsage());
        snapshot.setLoadAverage(OSUtils.loadAverage());
        snapshot.setUpdateTime(new Date());
        return snapshot;
    }
}
