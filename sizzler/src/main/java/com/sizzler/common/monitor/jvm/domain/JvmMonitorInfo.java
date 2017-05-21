package com.sizzler.common.monitor.jvm.domain;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class JvmMonitorInfo implements Serializable {

  private static final long serialVersionUID = 1360806530513198415L;

  @PK
  private long id;
  private String applicationName;
  private String ip;
  // memory
  private long memoryUsageMax; // MB
  private long memoryUsageInit; // MB
  private long memoryUsageUsed; // MB
  private long memoryUsageCommitted; // MB
  // thread
  private int threadCount; // 当前的线程总数
  private int peakThreadCount;
  // cpu
  private double cpuLoadAverage;

  private String statTime;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public long getMemoryUsageMax() {
    return memoryUsageMax;
  }

  public void setMemoryUsageMax(long memoryUsageMax) {
    this.memoryUsageMax = memoryUsageMax;
  }

  public long getMemoryUsageInit() {
    return memoryUsageInit;
  }

  public void setMemoryUsageInit(long memoryUsageInit) {
    this.memoryUsageInit = memoryUsageInit;
  }

  public long getMemoryUsageUsed() {
    return memoryUsageUsed;
  }

  public void setMemoryUsageUsed(long memoryUsageUsed) {
    this.memoryUsageUsed = memoryUsageUsed;
  }

  public long getMemoryUsageCommitted() {
    return memoryUsageCommitted;
  }

  public void setMemoryUsageCommitted(long memoryUsageCommitted) {
    this.memoryUsageCommitted = memoryUsageCommitted;
  }

  public int getThreadCount() {
    return threadCount;
  }

  public void setThreadCount(int threadCount) {
    this.threadCount = threadCount;
  }

  public int getPeakThreadCount() {
    return peakThreadCount;
  }

  public void setPeakThreadCount(int peakThreadCount) {
    this.peakThreadCount = peakThreadCount;
  }

  public double getCpuLoadAverage() {
    return cpuLoadAverage;
  }

  public void setCpuLoadAverage(double cpuLoadAverage) {
    this.cpuLoadAverage = cpuLoadAverage;
  }

  public String getStatTime() {
    return statTime;
  }

  public void setStatTime(String statTime) {
    this.statTime = statTime;
  }

  @Override
  public String toString() {
    return "JvmMonitorInfo [id=" + id + ", applicationName=" + applicationName + ", ip=" + ip
        + ", memoryUsageMax=" + memoryUsageMax + ", memoryUsageInit=" + memoryUsageInit
        + ", memoryUsageUsed=" + memoryUsageUsed + ", memoryUsageCommitted=" + memoryUsageCommitted
        + ", threadCount=" + threadCount + ", peakThreadCount=" + peakThreadCount
        + ", cpuLoadAverage=" + cpuLoadAverage + ", statTime=" + statTime + "]";
  }

}
