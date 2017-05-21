package com.sizzler.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElkLogUtil {

  private static Logger log = LoggerFactory.getLogger(ElkLogUtil.class);

  public static void info(String jsonStr) {
    log.info(jsonStr);
  }

}
