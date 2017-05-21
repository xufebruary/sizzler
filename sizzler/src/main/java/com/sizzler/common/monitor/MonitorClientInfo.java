package com.sizzler.common.monitor;

import java.sql.Timestamp;

/**
 * 监控客户端信息实体
 */
public class MonitorClientInfo implements Comparable<MonitorClientInfo> {

  /** 客户端名称 */
  private String clientName;

  /** 操作系统. */
  private String osName;

  /** 总的物理内存 （KB） */
  private double totalMemorySize;

  /** 已使用的物理内存（KB） */
  private double usedMemory;

  /** cpu使用率 （%） */
  private double cpuRatio;

  /** 主机IP地址 */
  private String ipAddress;

  /** 数据时间 */
  private String datetime;

  /** 内存使用率 （%） */
  private double memoryRatio = 0;

  /** linux下Buffers内存（KB） */
  private double buffersMemory = 0;

  /** linux下Cached内存 （KB） */
  private double cachedMemory = 0;

  /** linux下网络IO情況 ： 接收字节数（byte）、接收包数、接收错误包数、接收丢弃包数、发送字节数、发送包数、发送错误包数、发送丢弃包数 */
  private long netInBytes = 0;
  private long netInPackets = 0;
  private long netInErrs = 0;
  private long netInDrop = 0;
  private long netOutBytes = 0;
  private long netOutPackets = 0;
  private long netOutErrs = 0;
  private long netOutDrop = 0;

  /** linux下磁盘IO使用率 （%） */
  private double deskIORatio = 0.0;

  /** linux下磁盘使用率 （%） */
  private double deskRatio = 0.0;

  /** linux下磁盘使用量（MB） */
  private double deskUsed = 0.0;

  /** linux下磁盘总量（MB） */
  private double deskTotal = 0.0;

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getOsName() {
    return osName;
  }

  public void setOsName(String osName) {
    this.osName = osName;
  }

  public double getTotalMemorySize() {
    return totalMemorySize;
  }

  public void setTotalMemorySize(double totalMemorySize) {
    this.totalMemorySize = totalMemorySize;
  }

  public double getUsedMemory() {
    return usedMemory;
  }

  public void setUsedMemory(double usedMemory) {
    this.usedMemory = usedMemory;
  }

  public double getCpuRatio() {
    return cpuRatio;
  }

  public void setCpuRatio(double cpuRatio) {
    this.cpuRatio = cpuRatio;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getDatetime() {
    return datetime;
  }

  public void setDatetime(String datetime) {
    this.datetime = datetime;
  }

  public double getMemoryRatio() {
    return memoryRatio;
  }

  public void setMemoryRatio(double memoryRatio) {
    this.memoryRatio = memoryRatio;
  }

  public double getBuffersMemory() {
    return buffersMemory;
  }

  public void setBuffersMemory(double buffersMemory) {
    this.buffersMemory = buffersMemory;
  }

  public double getCachedMemory() {
    return cachedMemory;
  }

  public void setCachedMemory(double cachedMemory) {
    this.cachedMemory = cachedMemory;
  }

  public long getNetInBytes() {
    return netInBytes;
  }

  public void setNetInBytes(long netInBytes) {
    this.netInBytes = netInBytes;
  }

  public long getNetInPackets() {
    return netInPackets;
  }

  public void setNetInPackets(long netInPackets) {
    this.netInPackets = netInPackets;
  }

  public long getNetInErrs() {
    return netInErrs;
  }

  public void setNetInErrs(long netInErrs) {
    this.netInErrs = netInErrs;
  }

  public long getNetInDrop() {
    return netInDrop;
  }

  public void setNetInDrop(long netInDrop) {
    this.netInDrop = netInDrop;
  }

  public long getNetOutBytes() {
    return netOutBytes;
  }

  public void setNetOutBytes(long netOutBytes) {
    this.netOutBytes = netOutBytes;
  }

  public long getNetOutPackets() {
    return netOutPackets;
  }

  public void setNetOutPackets(long netOutPackets) {
    this.netOutPackets = netOutPackets;
  }

  public long getNetOutErrs() {
    return netOutErrs;
  }

  public void setNetOutErrs(long netOutErrs) {
    this.netOutErrs = netOutErrs;
  }

  public long getNetOutDrop() {
    return netOutDrop;
  }

  public void setNetOutDrop(long netOutDrop) {
    this.netOutDrop = netOutDrop;
  }

  public double getDeskIORatio() {
    return deskIORatio;
  }

  public void setDeskIORatio(double deskIORatio) {
    this.deskIORatio = deskIORatio;
  }

  public double getDeskRatio() {
    return deskRatio;
  }

  public void setDeskRatio(double deskRatio) {
    this.deskRatio = deskRatio;
  }

  public double getDeskUsed() {
    return deskUsed;
  }

  public void setDeskUsed(double deskUsed) {
    this.deskUsed = deskUsed;
  }

  public double getDeskTotal() {
    return deskTotal;
  }

  public void setDeskTotal(double deskTotal) {
    this.deskTotal = deskTotal;
  }

  public int compareTo(MonitorClientInfo m) {

    String stra = this.getDatetime();
    String strb = m.getDatetime();
    Timestamp a = Timestamp.valueOf(stra);
    Timestamp b = Timestamp.valueOf(strb);

    if (a.before(b)) {
      return -1;
    } else if (a.after(b)) {
      return 1;
    } else {
      return 0;
    }

  }
}
