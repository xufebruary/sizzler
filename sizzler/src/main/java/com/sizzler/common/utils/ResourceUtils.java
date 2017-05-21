package com.sizzler.common.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ResourceUtils {
  protected static Logger logger = LogManager.getLogger(ResourceUtils.class);

  public static String getFilePath(String filePath) {
    int startIdx = filePath.lastIndexOf('/');
    return filePath.substring(0, startIdx);
  }

  /**
   * Who call this, and then use the caller's class path.
   * @return
   */
  public static String getClassPath() {
    String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    return path.substring(0, path.length() - 1);
  }

  public static String getSecurityPath() {
    return getClassPath() + "/security";
  }

}
