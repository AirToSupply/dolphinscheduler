package org.apache.dolphinscheduler.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.apache.dolphinscheduler.common.utils.OSUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@TableName("t_ds_node_disk")
public class NodeDisk {
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
     * volume
     */
    @TableField("volume")
    private String volume;

    /**
     * mount directory
     */
    @TableField("mount")
    private String mount;

    /**
     * total space
     */
    @TableField("total")
    private Long total;

    /**
     * used space
     */
    @TableField("used")
    private Long used;

    /**
     * available space
     */
    @TableField("available")
    private Long available;

    /**
     * usage (usable rate)
     */
    @TableField("disk_usage")
    private Double diskUsage;

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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getMount() {
        return mount;
    }

    public void setMount(String mount) {
        this.mount = mount;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Long getAvailable() {
        return available;
    }

    public void setAvailable(Long available) {
        this.available = available;
    }

    public Double getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(Double diskUsage) {
        this.diskUsage = diskUsage;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeDisk nodeDisk = (NodeDisk) o;
        return id == nodeDisk.id &&
                Objects.equals(ip, nodeDisk.ip) &&
                Objects.equals(hostname, nodeDisk.hostname) &&
                Objects.equals(volume, nodeDisk.volume) &&
                Objects.equals(mount, nodeDisk.mount) &&
                Objects.equals(updateTime, nodeDisk.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ip, hostname, volume, mount, updateTime);
    }

    @Override
    public String toString() {
        return "NodeDisk{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", hostname='" + hostname + '\'' +
                ", volume='" + volume + '\'' +
                ", mount='" + mount + '\'' +
                ", total=" + total +
                ", used=" + used +
                ", available=" + available +
                ", diskUsage=" + diskUsage +
                ", updateTime=" + updateTime +
                '}';
    }

    public static List<NodeDisk> export() {
        List<NodeDisk> fileStores = OSUtils.getFileStores().stream().map(fs -> {
            NodeDisk disk = new NodeDisk();
            disk.setIp(OSUtils.ip());
            disk.setHostname(OSUtils.hostname());
            disk.setVolume(fs.getVolume());
            disk.setMount(fs.getMount());
            disk.setTotal(fs.getTotalSpace());
            disk.setUsed(fs.getTotalSpace() - fs.getFreeSpace());
            disk.setAvailable(fs.getUsableSpace());
            disk.setDiskUsage(
                fs.getTotalSpace() == 0L ? -1. :
                    format((fs.getTotalSpace() - fs.getFreeSpace()) * 1.0 / fs.getTotalSpace()));
            disk.setUpdateTime(new Date());
            return disk;
        }).collect(Collectors.toList());
        return fileStores;
    }

    private static final String TWO_DECIMAL = "0.00";

    private static Double format(double value) {
        DecimalFormat df = new DecimalFormat(TWO_DECIMAL);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return Double.parseDouble(df.format(value));
    }
}
