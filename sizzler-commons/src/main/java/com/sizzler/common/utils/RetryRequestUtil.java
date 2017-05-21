package com.sizzler.common.utils;

/**
 * 提供统一重试检查方法<br>
 * 固定最多重试3次，每隔1秒重试一次<br>
 */
public class RetryRequestUtil {
  /**
   * 最大可重试次数
   */
  private static final int maxTryCount = 3;
  /**
   * 重试间隔毫秒数
   */
  private static final long intervalTime = 1000l;

  /**
   * 检查重试次数是否超出次数，如果超出次数，抛出异常<br>
   * 没有超出，则数值计数+1
   * 
   * @param tryCount
   * @param logTitle
   * @return
   */
  public static int checkRetryCountAndSleep(String logTitle, int tryCount) throws Exception {
    if (maxTryCount - tryCount <= 0) {
      throw new Exception(logTitle + "::::::::retry request count out");
    }
    Thread.sleep(intervalTime);
    tryCount++;
    return tryCount;
  }
}
