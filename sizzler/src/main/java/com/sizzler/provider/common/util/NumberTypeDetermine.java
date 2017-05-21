package com.sizzler.provider.common.util;

import java.util.regex.Pattern;

/**
 * Created by ptmind on 2015/11/10.
 */
public class NumberTypeDetermine {
  // ^\s*(\+|-)?\d+\s*$
  private static Pattern INTEGER_PATTERN = Pattern.compile("^\\s*(\\+|-)?\\d+\\s*$");
  //

  // ^\s*(\+|-)?\d+\.\d+((e|E)(\+|-)?\d+)?\s*$
  private static Pattern DOUBLE_PATTERN = Pattern
      .compile("^\\s*(\\+|-)?\\d+\\.\\d+((e|E)(\\+|-)?[\\d]{1,2})?\\s*$");

  // ^\s*-?\d+(,\d+)*(\.?\d+((e|E)(\+|-)?\d+)?)?\s*$ 增加对 千位分隔符 , 的支持
  private static Pattern NUMBER_PATTERN = Pattern
      .compile("^\\s*-?\\d+(,\\d+)*(\\.?\\d+((e|E)(\\+|-)?[\\d]{1,2})?)?\\s*$");
  
  private static Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("^\\s*\\d+\\s*$");

  public static boolean isInteger(String inputStr) {
    return INTEGER_PATTERN.matcher(inputStr).matches();
  }

  public static boolean isDouble(String inputStr) {
    return DOUBLE_PATTERN.matcher(inputStr).matches();
  }

  public static boolean isNumber(String inputStr) {
    return NUMBER_PATTERN.matcher(inputStr).matches();
  }

  public static boolean isLong(String inputStr) {
    try {
      Long.valueOf(inputStr);
    } catch (Exception e) {
      return false;
    }
    return true;
  }
  
  /**
   * 验证是否是个正整数，用于时间戳的验证
   * @author you.zou
   * @date 2016年12月27日 下午2:48:51
   * @param inputStr
   * @return
   */
  public static boolean isPositiveInteger(String inputStr){
    return POSITIVE_INTEGER_PATTERN.matcher(inputStr).matches();
  }

}
