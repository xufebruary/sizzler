package com.sizzler.common.monitor.dstat;

import java.sql.Timestamp;

/**
 * 监控客户端信息实体(使用dstat工具进行性能监测信息收集)<br>
 * 
 * linux 下安装dstat工具<br>
 * 
 * 执行命令： dstat -cmdnrlpsy <br>
 * 
 * 显示结果示例：<br>
 * 
 * ----total-cpu-usage---- ------memory-usage----- -dsk/total- -net/total- --io/total-
 * ---load-avg--- ---procs--- ----swap--- ---system-- usr sys idl wai hiq siq| used buff cach free|
 * read writ| recv send| read writ| 1m 5m 15m |run blk new| used free| int csw 0 0 99 0 0 0| 494M
 * 36.0M 361M 98.7M|3876B 14k| 0 0 |0.14 1.02 | 0 0.01 0| 0 0 0.1| 99M 1885M| 94 196 1 0 99 0 0 0|
 * 494M 36.0M 361M 98.7M| 0 0 | 146B 1634B| 0 0 | 0 0.01 0| 0 0 0| 99M 1885M| 95 200 0 0 100 0 0 0|
 * 494M 36.0M 361M 98.7M| 0 0 | 146B 722B| 0 0 | 0 0.01 0| 0 0 0| 99M 1885M| 79 165 1 0 99 0 0 0|
 * 494M 36.0M 361M 98.7M| 0 0 | 284B 776B| 0 0 | 0 0.01 0| 0 0 0| 99M 1885M| 95 198 0 0 100 0 0 0|
 * 494M 36.0M 361M 98.7M| 0 0 | 298B 722B| 0 0 | 0 0.01 0| 0 0 0| 99M 1885M| 94 190 2 1 96 0 0 1|
 * 494M 36.0M 361M 98.7M| 0 4096B| 238B 722B| 0 1.00 | 0 0.01 0| 0 0 0| 99M 1885M| 113 219
 * 
 */
public class MonitorDstatClientInfo implements Comparable<MonitorDstatClientInfo> {

  /** 客户端名称 */
  private String clientName = "";

  /** 操作系统. */
  private String osName = "";

  /** 主机IP地址 */
  private String ipAddress = "";

  /** 数据时间 */
  private String datetime = "";

  /**
   * cpu 使用情况：user, system, idle（空闲等待时间百分比）, wait（等待磁盘IO）, hardware interrupt（硬件中断）, software
   * interrupt（软件中断）
   */
  private double cpuUsr = 0;
  private double cpuSys = 0;
  private double cpuIdl = 0;
  private double cpuWai = 0;
  private double cpuHiq = 0;
  private double cpuSiq = 0;

  /** memory 使用情况 (M) ： used, buffers, cache, free */
  private double memUsed = 0;
  private double memBuff = 0;
  private double memCach = 0;
  private double memFree = 0;

  /** 磁盘IO情况 (B)：读、写 */
  private double dskRead = 0;
  private double dskWrit = 0;

  /** 网络IO情况(B) ：接收和发送数据 */
  private double netRecv = 0;
  private double netSend = 0;

  /** I/O请求统计: 读、写请求 */
  private double ioRead = 0;
  private double ioWrit = 0;

  /** 负载情况:1分钟平均、5分钟平均、15分钟平均 */
  private double loadAvg1m = 0;
  private double loadAvg5m = 0;
  private double loadAvg15m = 0;

  /** 进程信息：runnable、uninterruptible、new */
  private double procsRun = 0;
  private double procsBlk = 0;
  private double procsNew = 0;

  /** swqp 情况(M): 已使用、空闲 */
  private double swapUsed = 0;
  private double swapFree = 0;

  /** 系统情况：中断、上下文切换 */
  private double systemInt = 0;
  private double systemCsw = 0;

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

  public double getCpuUsr() {
    return cpuUsr;
  }

  public void setCpuUsr(double cpuUsr) {
    this.cpuUsr = cpuUsr;
  }

  public double getCpuSys() {
    return cpuSys;
  }

  public void setCpuSys(double cpuSys) {
    this.cpuSys = cpuSys;
  }

  public double getCpuIdl() {
    return cpuIdl;
  }

  public void setCpuIdl(double cpuIdl) {
    this.cpuIdl = cpuIdl;
  }

  public double getCpuWai() {
    return cpuWai;
  }

  public void setCpuWai(double cpuWai) {
    this.cpuWai = cpuWai;
  }

  public double getCpuHiq() {
    return cpuHiq;
  }

  public void setCpuHiq(double cpuHiq) {
    this.cpuHiq = cpuHiq;
  }

  public double getCpuSiq() {
    return cpuSiq;
  }

  public void setCpuSiq(double cpuSiq) {
    this.cpuSiq = cpuSiq;
  }

  public double getMemUsed() {
    return memUsed;
  }

  public void setMemUsed(double memUsed) {
    this.memUsed = memUsed;
  }

  public double getMemBuff() {
    return memBuff;
  }

  public void setMemBuff(double memBuff) {
    this.memBuff = memBuff;
  }

  public double getMemCach() {
    return memCach;
  }

  public void setMemCach(double memCach) {
    this.memCach = memCach;
  }

  public double getMemFree() {
    return memFree;
  }

  public void setMemFree(double memFree) {
    this.memFree = memFree;
  }

  public double getDskRead() {
    return dskRead;
  }

  public void setDskRead(double dskRead) {
    this.dskRead = dskRead;
  }

  public double getDskWrit() {
    return dskWrit;
  }

  public void setDskWrit(double dskWrit) {
    this.dskWrit = dskWrit;
  }

  public double getNetRecv() {
    return netRecv;
  }

  public void setNetRecv(double netRecv) {
    this.netRecv = netRecv;
  }

  public double getNetSend() {
    return netSend;
  }

  public void setNetSend(double netSend) {
    this.netSend = netSend;
  }

  public double getIoRead() {
    return ioRead;
  }

  public void setIoRead(double ioRead) {
    this.ioRead = ioRead;
  }

  public double getIoWrit() {
    return ioWrit;
  }

  public void setIoWrit(double ioWrit) {
    this.ioWrit = ioWrit;
  }

  public double getLoadAvg1m() {
    return loadAvg1m;
  }

  public void setLoadAvg1m(double loadAvg1m) {
    this.loadAvg1m = loadAvg1m;
  }

  public double getLoadAvg5m() {
    return loadAvg5m;
  }

  public void setLoadAvg5m(double loadAvg5m) {
    this.loadAvg5m = loadAvg5m;
  }

  public double getLoadAvg15m() {
    return loadAvg15m;
  }

  public void setLoadAvg15m(double loadAvg15m) {
    this.loadAvg15m = loadAvg15m;
  }

  public double getProcsRun() {
    return procsRun;
  }

  public void setProcsRun(double procsRun) {
    this.procsRun = procsRun;
  }

  public double getProcsBlk() {
    return procsBlk;
  }

  public void setProcsBlk(double procsBlk) {
    this.procsBlk = procsBlk;
  }

  public double getProcsNew() {
    return procsNew;
  }

  public void setProcsNew(double procsNew) {
    this.procsNew = procsNew;
  }

  public double getSwapUsed() {
    return swapUsed;
  }

  public void setSwapUsed(double swapUsed) {
    this.swapUsed = swapUsed;
  }

  public double getSwapFree() {
    return swapFree;
  }

  public void setSwapFree(double swapFree) {
    this.swapFree = swapFree;
  }

  public double getSystemInt() {
    return systemInt;
  }

  public void setSystemInt(double systemInt) {
    this.systemInt = systemInt;
  }

  public double getSystemCsw() {
    return systemCsw;
  }

  public void setSystemCsw(double systemCsw) {
    this.systemCsw = systemCsw;
  }

  public int compareTo(MonitorDstatClientInfo m) {

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
