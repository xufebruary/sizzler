package com.sizzler.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * url地址解析的工具类
 */
public class UrlUtil {

  /** 键索引 */
  private static final Integer KEY_INDEX = 0;

  /** 值索引 */
  private static final Integer VALUE_INDEX = 1;

  /** 地址与参数之间的连接符 */
  private static final String PATH_PARAM_CONCAT = "[?]";

  /** 参数与参数之间的连接符 */
  private static final String PARAM_CONCAT = "[&]";

  /** 参数的key与value之间的连接符 */
  private static final String PARAM_KEY_VALUE_CONCAT = "[=]";

  /**
   * 解析出URL的所有参数的键值对
   */
  public static Map<String, String> decodeUrlParamToMap(String url) {
    Map<String, String> paramMap = new HashMap<String, String>();
    if (StringUtil.isBlank(url)) {
      return paramMap;
    }
    String urlParam = truncateUrlParam(url);
    if (StringUtil.isBlank(urlParam)) {
      return paramMap;
    }
    String[] paramArray = urlParam.split(PARAM_CONCAT);
    if (paramArray == null || paramArray.length == 0) {
      return paramMap;
    }
    for (String param : paramArray) {
      String[] paramKeyValue = param.split(PARAM_KEY_VALUE_CONCAT);
      if (paramKeyValue != null && paramKeyValue.length > 1) {
        paramMap.put(paramKeyValue[KEY_INDEX], paramKeyValue[VALUE_INDEX]);
      }
    }
    return paramMap;
  }

  /**
   * 截断url，只提取参数部分<br>
   * 例如：http://www.xxx.com?a=1&b=3<br>
   * 返回：a=1&b=3<br>
   */
  public static String truncateUrlParam(String url) {
    if (StringUtil.isBlank(url)) {
      return null;
    }
    String urlParam = null;
    String[] pathAndParam = null;
    pathAndParam = url.split(PATH_PARAM_CONCAT);
    if (pathAndParam != null && pathAndParam.length > 1) {
      urlParam = pathAndParam[1];// ?后面的都作为参数
    }
    return urlParam;
  }

}
