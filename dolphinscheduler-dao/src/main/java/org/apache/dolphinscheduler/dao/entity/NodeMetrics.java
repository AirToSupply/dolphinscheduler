package org.apache.dolphinscheduler.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.dolphinscheduler.common.enums.ReleaseState;
import org.apache.dolphinscheduler.common.utils.OSUtils;

import java.util.Date;
import java.util.Objects;

@TableName("t_ds_node_metrics")
public class NodeMetrics {
    /**
     * id
     */
    @TableId(value="id", type= IdType.AUTO)
    private int id;

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
     * state : online/offline
     */
    @TableField("state")
    private ReleaseState state;

    /**
     * Memory (GB)
     */
    @TableField("memory")
    private Double memory = 0.0;

    /**
     * CPU
     */
    @TableField("cpu")
    private Integer cpu = 0;

    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * description
     */
    @TableField("description")
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ReleaseState getState() {
        return state;
    }

    public void setState(ReleaseState state) {
        this.state = state;
    }

    public Double getMemory() {
        return memory;
    }

    public void setMemory(Double memory) {
        this.memory = memory;
    }

    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "NodeMetrics{" +
            "id=" + id +
            ", ip='" + ip + '\'' +
            ", hostname='" + hostname + '\'' +
            ", state=" + state +
            ", memory=" + memory +
            ", cpu=" + cpu +
            ", updateTime=" + updateTime +
            ", description='" + description + '\'' +
            '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeMetrics that = (NodeMetrics) o;
        return id == that.id &&
                Objects.equals(ip, that.ip) &&
                Objects.equals(hostname, that.hostname) &&
                Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ip, hostname, updateTime);
    }

    public static NodeMetrics export() {
        NodeMetrics metrics = new NodeMetrics();
        metrics.setIp(OSUtils.ip());
        metrics.setHostname(OSUtils.hostname());
        metrics.setState(ReleaseState.ONLINE);
        metrics.setMemory(OSUtils.physicalMemorySize());
        metrics.setCpu(OSUtils.logicalProcessorCount());
        metrics.setUpdateTime(new Date());
        return metrics;
    }
}
