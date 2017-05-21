package com.sizzler.system.util;

import java.util.HashMap;
import java.util.Map;

import com.sizzler.system.Constants;

public class CascadeDeleteUtil {

  public static final String STATUS = "status";
  public static final String IS_DELETE = "isDelete";

  /**
   * 根据isDelete构建参数Map
   * 
   * @return
   */
  public static Map<String, Map<String, String>> buildParamMap(boolean isDelete) {
    Map<String, Map<String, String>> updateMap = new HashMap<String, Map<String, String>>();

    Map<String, String> statusMap = new HashMap<>(1);
    Map<String, String> deleteMap = new HashMap<>(1);
    statusMap.put(STATUS, setStatus(isDelete));
    deleteMap.put(IS_DELETE, setIsDelete(isDelete));

    updateMap.put(STATUS, statusMap);
    updateMap.put(IS_DELETE, deleteMap);
    return updateMap;
  }

  /**
   * 
   * 根据isDelete(true是删除，false是恢复)，设置字段的值
   * 
   * @return
   */
  public static String setStatus(boolean isDelete) {
    if (isDelete) {
      return Constants.inValidate;
    }
    return Constants.validate;
  }

  /**
   * 
   * 根据isDelete(true是删除，false是恢复)，设置字段的值
   * 
   * @return
   */
  public static String setIsDelete(boolean isDelete) {
    if (isDelete) {
      return Constants.validate;
    }
    return Constants.inValidate;
  }
}
