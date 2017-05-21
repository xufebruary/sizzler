package com.sizzler.common.monitor.jvm.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sizzler.common.monitor.jvm.domain.JvmMonitorInfo;

public class JvmUtil {

  private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static String localIp;
  static {
    try {
      localIp = InetAddress.getLocalHost().getHostAddress();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static JvmMonitorInfo getJvmInfo() {
    JvmMonitorInfo currentJvmInfo = new JvmMonitorInfo();
    currentJvmInfo.setIp(localIp);
    currentJvmInfo.setStatTime(dateFormat.format(new Date()));

    // memory
    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
    long max = memoryUsage.getMax();
    long init = memoryUsage.getInit();
    long used = memoryUsage.getUsed();
    long committed = memoryUsage.getCommitted();
    currentJvmInfo.setMemoryUsageMax(convertByteToMB(max));
    currentJvmInfo.setMemoryUsageInit(convertByteToMB(init));
    currentJvmInfo.setMemoryUsageUsed(convertByteToMB(used));
    currentJvmInfo.setMemoryUsageCommitted(convertByteToMB(committed));

    // thread
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    int threadCount = threadMXBean.getThreadCount(); // 当前的线程总数
    int peakThreadCount = threadMXBean.getPeakThreadCount();
    currentJvmInfo.setThreadCount(threadCount);
    currentJvmInfo.setPeakThreadCount(peakThreadCount);

    // cpu
    OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    double cpuLoadAverage = operatingSystemMXBean.getSystemLoadAverage();
    currentJvmInfo.setCpuLoadAverage(cpuLoadAverage);

    return currentJvmInfo;
  }

  public static long convertByteToMB(long tmpByte) {
    return tmpByte / 1024 / 1024;
  }

}
