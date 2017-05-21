package com.sizzler.system;

import org.springframework.stereotype.Component;

@Component("threadPoolConstants")
public class ThreadPoolConstants {

  public static boolean useThreadPool = true; // 是否使用线程池

  /**
   * 默认线程池
   */
  public static int defaultThreadPoolMinSize = 30; // 最小线程数
  public static int defaultThreadPoolMaxSize = 175; // 最大线程数
  public static long defaultThreadPoolKeepAliveTime = 2; // 线程空闲时间，单位分钟
  public static int defaultThreadPoolQueueSize = 1000; // 缓冲队列大小

  public static int defaultAddTaskWaitingTime = 1000; // 缓冲队列满了后，默认的等待重试时间 ： 1s

  // //////////////////////////////////////////////////////////////////

  public boolean isUseThreadPool() {
    return useThreadPool;
  }

  public void setUseThreadPool(boolean useThreadPool) {
    ThreadPoolConstants.useThreadPool = useThreadPool;
  }

  public int getDefaultThreadPoolMinSize() {
    return ThreadPoolConstants.defaultThreadPoolMinSize;
  }

  public void setDefaultThreadPoolMinSize(int defaultThreadPoolMinSize) {
    ThreadPoolConstants.defaultThreadPoolMinSize = defaultThreadPoolMinSize;
  }

  public int getDefaultThreadPoolMaxSize() {
    return ThreadPoolConstants.defaultThreadPoolMaxSize;
  }

  public void setDefaultThreadPoolMaxSize(int defaultThreadPoolMaxSize) {
    ThreadPoolConstants.defaultThreadPoolMaxSize = defaultThreadPoolMaxSize;
  }

  public long getDefaultThreadPoolKeepAliveTime() {
    return ThreadPoolConstants.defaultThreadPoolKeepAliveTime;
  }

  public void setDefaultThreadPoolKeepAliveTime(long defaultThreadPoolKeepAliveTime) {
    ThreadPoolConstants.defaultThreadPoolKeepAliveTime = defaultThreadPoolKeepAliveTime;
  }

  public int getDefaultThreadPoolQueueSize() {
    return ThreadPoolConstants.defaultThreadPoolQueueSize;
  }

  public void setDefaultThreadPoolQueueSize(int defaultThreadPoolQueueSize) {
    ThreadPoolConstants.defaultThreadPoolQueueSize = defaultThreadPoolQueueSize;
  }

  public int getDefaultAddTaskWaitingTime() {
    return ThreadPoolConstants.defaultAddTaskWaitingTime;
  }

  public void setDefaultAddTaskWaitingTime(int defaultAddTaskWaitingTime) {
    ThreadPoolConstants.defaultAddTaskWaitingTime = defaultAddTaskWaitingTime;
  }

}
