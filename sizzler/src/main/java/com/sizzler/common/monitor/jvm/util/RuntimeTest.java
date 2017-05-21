package com.sizzler.common.monitor.jvm.util;

public class RuntimeTest {

  public static void main(String[] args) {
    Runtime runtime = Runtime.getRuntime();
    // 最大内存,启动JAVA虚拟机时使用参数-Xmx指定的内存
    long max = convertByteToMB(runtime.maxMemory());
    System.out.println("maxMemory:" + max + " MB");
    // 已经分配的内存,jvm使用的内存都是从本地系统获取的，但是通常jvm刚启动的时候，并不会向系统申请全部的内存。而是根据所加载的Class和相关资源的容量来决定的。
    long total = convertByteToMB(runtime.totalMemory());
    System.out.println("totalMemory:" + total + " MB");
    // 已分配内存中的剩余内存
    long free = convertByteToMB(runtime.freeMemory());
    System.out.println("freeMemory:" + free + " MB");
    // 最大可用内存
    long usable = max - total + free;
    System.out.println("usableMemory:" + usable + " MB");
  }

  public static long convertByteToMB(long tmpByte) {
    return tmpByte / 1024 / 1024;
  }
}
