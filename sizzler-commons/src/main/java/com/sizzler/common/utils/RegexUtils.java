package com.sizzler.common.utils;

import java.util.regex.Pattern;

/**
 * 对正则表达式的处理方法
 * 
 */
public class RegexUtils {

  /**
   * 是否包含数字
   * 
   * @param str
   * @return
   */
  public static boolean isInteger(String str) {
    return isMatch("^-?\\d+$", str);
  }

  /**
   * 是否包含浮点数据
   * 
   * @param str
   * @return
   */
  public static boolean isFloat(String str) {
    return isMatch("^(-?\\d+)(\\.\\d+)?$", str);
  }

  /**
   * 指定的字符串是否为正确的日期时间格式
   * 
   * @param str
   * @return
   */
  public static boolean isDateTime(String str) {
    String patten = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
    return isMatch(patten, str);
  }

  public static boolean isDate(String str) {
    String patten = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))$";
    return isMatch(patten, str);
  }

  /**
   * 验证字符串是否是科学计数法格式
   * 
   * @author you.zou
   * @date 2016年11月15日 下午3:49:33
   * @param str
   * @return
   */
  public static boolean isScientificNotation(String str) {
    // String patten = "^-?\\d+(\\.\\d+)?(E-?\\d+)?$";
    String patten = "^((-?\\d+\\.?\\d+)[Ee]{1}([-+]?\\d+))$";
    return isMatch(patten, str);
  }

  public static boolean isBoolean(String str) {
    String[] values = { "true", "false", "yes", "no", "on", "off", "1", "0" };
    for (String value : values) {
      if (value.equalsIgnoreCase(str))
        return true;
    }
    return false;
  }

  private static boolean isMatch(String ms, String str) {
    Pattern p = Pattern.compile(ms);
    return p.matcher(str).matches();
  }
}
