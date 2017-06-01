package com.sizzler.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class CollectLogUtil {

  private static Logger log = LoggerFactory.getLogger(CollectLogUtil.class);

  public static void info(String jsonStr) {
    log.info(jsonStr);
  }
  
  public static void info(Object obj) {
    log.info(JSON.toJSONString(obj));
  }

}
