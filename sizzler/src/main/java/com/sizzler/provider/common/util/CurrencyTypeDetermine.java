package com.sizzler.provider.common.util;

import java.util.regex.Pattern;

/**
 * Created by ptmind on 2015/11/10.
 */
public class CurrencyTypeDetermine {

  // (\$\s*\.?\d+(,|\.|\d)*\s*(USD)?) 以$开头+数字+可选的USD结尾
  // ((\$|€|¥)\s*\.?\d+(,|\.|\d)*\s*(USD|EUR|JPY|RMB)?)
  // (\$?\s*\.?\d+(,|\.|\d)*\s*USD) 以可选的$开头+数字+USD结尾
  // ((\$|€|¥)?\s*\.?\d+(,|\.|\d)*\s*(USD|EUR|JPY|RMB))
  // (USD\s*\.?\d+(,|\.|\d)*\s*\$?) 以USD开头+数字+可选的$结尾
  // ((USD|EUR|JPY|RMB)\s*\.?\d+(,|\.|\d)*\s*(\$|€|¥)?)
  // ((USD)?\s*\.?\d+(,|\.|\d)*\s*\$) 以可选的USD开头+数字+$结尾
  // ((USD|EUR|JPY|RMB)?\s*\.?\d+(,|\.|\d)*\s*(\$|€|¥))
  // (\s*\.?\d+(,|\.|\d)*\s*(円|元))
  // RMB¥ (CNY ¥) (US$) (USD $) (JP¥) (JPY ¥) +数字
  // ^\s*(((\$|€|¥|￥)\s*\.?\d+(,|\.|\d)*\s*(USD|EUR|JPY|RMB)?)|((\$|€|¥|￥)?\s*\.?\d+(,|\.|\d)*\s*(USD|EUR|JPY|RMB))|((USD|EUR|JPY|RMB)\s*\.?\d+(,|\.|\d)*\s*(\$|€|¥|￥)?)|((USD|EUR|JPY|RMB)?\s*\.?\d+(,|\.|\d)*\s*(\$|€|¥|￥)))\s*$

  // ^\s*(((\$|€|¥|￥)\s*\.?\d+(,|\.|\d)*\s*(USD|EUR|JPY|JP|RMB|CNY)?)|((\s*\.?\d+(,|\.|\d)*\s*(円|元)))|((\$|€|¥|￥)?\s*\.?\d+(,|\.|\d)*\s*(USD|EUR|JPY|JP|RMB|CNY))|((USD|EUR|JPY|JP|RMB|CNY)\s*\.?\d+(,|\.|\d)*\s*(\$|€|¥|￥)?)|((USD|EUR|JPY|JP|RMB|CNY)?\s*\.?\d+(,|\.|\d)*\s*(\$|€|¥|￥)))\s*$
  // ((RMB|CNY|US|USD|JP|JPY)\s*(\$|€|¥|￥)\.?\d+(,|\.|\d)*\s*)
  // ^\s*(((\$|€|¥|￥)\s*\.?\d+(,|\.|\d)*\s*(USD|EUR|JPY|JP|RMB|CNY)?)|((\s*\.?\d+(,|\.|\d)*\s*(円|元)))|((\$|€|¥|￥)?\s*\.?\d+(,|\.|\d)*\s*(USD|EUR|JPY|JP|RMB|CNY))|((USD|EUR|JPY|JP|RMB|CNY)\s*\.?\d+(,|\.|\d)*\s*(\$|€|¥|￥)?)|((USD|EUR|JPY|JP|RMB|CNY)?\s*\.?\d+(,|\.|\d)*\s*(\$|€|¥|￥))|((RMB|CNY|US|USD|JP|JPY)\s*(\$|€|¥|￥)\.?\d+(,|\.|\d)*\s*))\s*$

  private static Pattern CURRENCY_PATTERN =
      Pattern
          .compile("^\\s*(((\\$|€|¥|￥)\\s*\\.?\\d+(,|\\.|\\d)*\\s*(USD|EUR|JPY|JP|RMB|CNY)?)|((\\s*\\.?\\d+(,|\\.|\\d)*\\s*(円|元)))|((\\$|€|¥|￥)?\\s*\\.?\\d+(,|\\.|\\d)*\\s*(USD|EUR|JPY|JP|RMB|CNY))|((USD|EUR|JPY|JP|RMB|CNY)\\s*\\.?\\d+(,|\\.|\\d)*\\s*(\\$|€|¥|￥)?)|((USD|EUR|JPY|JP|RMB|CNY)?\\s*\\.?\\d+(,|\\.|\\d)*\\s*(\\$|€|¥|￥))|((RMB|CNY|US|USD|JP|JPY)\\s*(\\$|€|¥|￥)\\.?\\d+(,|\\.|\\d)*\\s*))\\s*$");

  public static boolean isCurrency(String inputStr) {
    return CURRENCY_PATTERN.matcher(inputStr).matches();
  }

  public static void main(String[] args) {
    String test = "JPY ¥10";
    System.out.println(isCurrency(test));
  }

  public static String determineCurrencyFormat(String inputStr) {
    // 判断是否为美元
    inputStr = inputStr.toUpperCase();
    if (inputStr.contains("$") || inputStr.contains("USD") || inputStr.contains("US")) {
      return "$##";
    } else if (inputStr.contains("¥") || inputStr.contains("JPY") || inputStr.contains("円")
        || inputStr.contains("JP"))// 判断是否为日元
    {
      if (inputStr.contains("RMB") || inputStr.contains("CNY"))// 为了兼容 日元和人民币的 ¥相同的情况
      {
        return "¥###";
      }
      return "¥##";
    } else if (inputStr.contains("￥") || inputStr.contains("RMB") || inputStr.contains("元")
        || inputStr.contains("CNY"))// 判断是否为人民币
    {
      return "¥###";
    }
    return "$##";
  }

}
