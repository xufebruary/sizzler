package com.sizzler.common.monitor.jvm.util;

import java.lang.management.*;

public class JmxTest {
  public static void main(String[] args) {
    testThreadMXBean();
    testCpu();
    testGetMemoryUsage();
  }

  public static void test() {
    testThreadMXBean();
    testCpu();
    testGetMemoryUsage();
  }

  public static void testGetMemoryUsage() {
    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();

    long max = memoryUsage.getMax();
    long init = memoryUsage.getInit();
    long used = memoryUsage.getUsed();
    long committed = memoryUsage.getCommitted();

    System.out.println("max:" + convertByteToMB(max) + "MB");
    System.out.println("init:" + convertByteToMB(init) + "MB");
    System.out.println("used:" + convertByteToMB(used) + "MB");
    System.out.println("committed:" + convertByteToMB(committed) + "MB");

  }

  public static void testThreadMXBean() {
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    // 当前的线程总数
    int threadCount = threadMXBean.getThreadCount();
    System.out.println("threadCount:" + threadCount);
    int peakThreadCount = threadMXBean.getPeakThreadCount();
    System.out.println("peakThreadCount:" + peakThreadCount);
  }

  public static void testCpu() {
    OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    double loadAverage = operatingSystemMXBean.getSystemLoadAverage();
    System.out.println("loadAverage:" + loadAverage);
  }

  public static long convertByteToMB(long tmpByte) {
    return tmpByte / 1024 / 1024;
  }
}
