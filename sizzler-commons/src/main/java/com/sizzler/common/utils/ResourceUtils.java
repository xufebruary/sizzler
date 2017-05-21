package com.sizzler.common.utils;

public class ResourceUtils {

  public static String getFilePath(String filePath) {
    int startIdx = filePath.lastIndexOf('/');
    return filePath.substring(0, startIdx);
  }

  /**
   * Who call this, and then use the caller's class path.
   * 
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
