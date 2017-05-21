package com.sizzler.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户操作日志的打印工具类
 */
public class UserOperateLogUtil {

  private static Logger log = LoggerFactory.getLogger(UserOperateLogUtil.class);

  public static void info(String jsonStr) {
    log.info(jsonStr);
  }

}
