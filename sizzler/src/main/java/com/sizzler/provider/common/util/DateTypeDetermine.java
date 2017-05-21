package com.sizzler.provider.common.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by ptmind on 2015/11/9.
 */
public class DateTypeDetermine {

  // 匹配前十行，匹配占比超过 80% 才算成功
  // 匹配原则：
  // (1)检查是否以am或pm结尾
  // (2)检查是否包含：Sun，Mon，Tue，Wed，Thu，Fri，Sat
  // (3)检查是否包含：Jan，Feb，Mar，Apr，May，June，July，Aug，Sept，Oct，Nov，Dec
  static Set<String> weekSet = new HashSet<String>();
  static Set<String> monthSet = new HashSet<String>();
  static {
    weekSet.add("SUN");
    weekSet.add("MON");
    weekSet.add("TUE");
    weekSet.add("WED");
    weekSet.add("THU");
    weekSet.add("FRI");
    weekSet.add("SAT");

    monthSet.add("JAN");
    monthSet.add("FEB");
    monthSet.add("MAR");
    monthSet.add("APR");
    monthSet.add("MAY");
    monthSet.add("JUNE");
    monthSet.add("JULY");
    monthSet.add("AUG");
    monthSet.add("SEPT");
    monthSet.add("OCT");
    monthSet.add("NOV");
    monthSet.add("DEC");
  }

  private static Pattern YYYY_MM_DD =
      Pattern
          .compile("^(?:(?:(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))(\\/|-|\\.)(?:0?2\\1(?:29)))|(?:(?:(?:1[6-9]|[2-9]\\d)?\\d{2})(\\/|-|\\.|\\x20*)(?:(?:(?:0?[13578]|1[02])\\2(?:31))|(?:(?:0?[1,3-9]|1[0-2])\\2(29|30))|(?:(?:0?[1-9])|(?:1[0-2]))\\2(?:0?[1-9]|1\\d|2[0-8]))))$");

  // 2015年12月01日
  private static Pattern YYYY_MM_DD_CN = Pattern.compile("^\\s*\\d{4}年\\d{1,2}月\\d{1,2}日\\s*$");


  private static Pattern YYYY_MM_DD_T_HH_MM_SS =
      Pattern
          .compile("^\\d{4}(\\/|-|\\.|\\x20*)((0[1-9])|(1[0-2]))(\\/|-|\\.|\\x20*)((0[1-9])|([1-2][0-9])|(3[0-1]))(T|\\s)(([0-1][0-9])|(2[0-3]))(:|\\x20*)([0-5][0-9])(((:|\\x20*)([0-5][0-9]))?)$");

  /*
   * Date： dd/mm/yyyy dd-mm-yyyy dd.mm.yyyy dd/mm/yy dd-mm-yy dd.mm.yy Time： HH:MM:SS HH:MM
   * DateTime： dd/mm/yyyy HH:MM:SS（HH:MM）
   */
  private static Pattern DD_MM_YYYY =
      Pattern
          .compile("^(?=\\d)(?:(?:31(?!.(?:0?[2469]|11))|(?:30|29)(?!.0?2)|29(?=.0?2.(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))(?:\\x20|$))|(?:2[0-8]|1\\d|0?[1-9]))(\\/|-|\\.|\\x20*)(?:1[012]|0?[1-9])\\1(?:1[6-9]|[2-9]\\d)?\\d\\d(?:(?=\\x20\\d)\\x20|$))?(((0?[1-9]|1[012])(:[0-5]\\d){0,2}(\\x20[AP]M))|([01]\\d|2[0-3])(:[0-5]\\d){1,2})?$");

  /*
   * Date： mm/dd/yyyy mm-dd-yyyy mm.dd.yyyy mm/dd/yy mm-dd-yy mm.dd.yy Time： HH:MM:SS HH:MM
   * DateTime： mm/dd/yyyy HH:MM:SS（HH:MM）
   */
  private static Pattern MM_DD_YYYY =
      Pattern
          .compile("^(?=\\d)(?:(?:(?:(?:(?:0?[13578]|1[02])(\\/|-|\\.)31)\\1|(?:(?:0?[1,3-9]|1[0-2])(\\/|-|\\.)(?:29|30)\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})|(?:0?2(\\/|-|\\.)29\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))|(?:(?:0?[1-9])|(?:1[0-2]))(\\/|-|\\.)(?:0?[1-9]|1\\d|2[0-8])\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2}))($|\\ (?=\\d)))?(((0?[1-9]|1[012])(:[0-5]\\d){0,2}(\\ [AP]M))|([01]\\d|2[0-3])(:[0-5]\\d){1,2})?$");

  /*
   * Date： mm-yyyy mm/yyyy mmyyyy myyyy m-yyyy m/yyyy
   */
  private static Pattern YYYY_MM = Pattern
      .compile("^\\d{4}(\\/|-|\\.|\\x20*)((0?[1-9])|(1[0-2]))$");


  private static Pattern QNYYYY = Pattern.compile("^[Qq][1-4](\\s*)\\d{2,4}$");

  /*
   * +0500或者GMT+0500或者GMTE0500或者(UTC) UTC结尾
   */
  private static Pattern TIMEZONE = Pattern
      .compile("^.*([a-zA-Z]{0}|[a-zA-Z]{3}|[a-zA-Z]{4})\\+?\\s*\\d{4}\\s*(\\(?(UTC|utc)?\\)?)?$");


  /*
   * 日期时间格式的正则表达式
   */
  private static String dateTimeRegexStr =
      "^.* ([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-9]|[0-5][0-9]):([0-9]|[0-5][0-9])(\\.([0-9]*))?$";

  private static Pattern dataTimePattern = Pattern.compile(dateTimeRegexStr);


  private static String yyyy = "yyyy";
  private static String yy = "yy";
  private static String MM = "MM";
  private static String dd = "dd";


  /*
   * 
   * //YYYY_MM_DD yyyy/MM/dd' yyyy-MM-dd' yyyy.MM.dd'
   * 
   * //YYYY_MM_DD_CN yyyy年MM月dd日
   * 
   * //下面的目前没有找到匹配方法 MM/dd, MM-dd, MM.dd, MM月dd日,
   * 
   * //MM_DD_YYYY MM/dd/yyyy MM-dd-yyyy MM.dd.yyyy
   * 
   * //MM_DD_YYYY MM/dd/yy MM-dd-yy MM.dd.yy
   */


  public static boolean isDate(String inputStr) {

    if (isYYYYMMDD(inputStr) || isYYYYMMDDCN(inputStr) || isMMDDYYYY(inputStr)) {
      return true;
    }
    return false;
  }

  // 匹配 (日期 hh:mm:ss)的格式

  public static boolean isDateTime(String inputStr) {
    return dataTimePattern.matcher(inputStr).matches();
  }

  public static String determineDateFormat(String inputStr) {
    inputStr = inputStr.trim();
    StringBuilder stringBuilder = new StringBuilder("");
    // 判断分隔符
    String splitChar = determineSplitChar(inputStr);

    if (isYYYYMMDD(inputStr)) {

      if (!splitChar.equals("")) {

        stringBuilder.append(yyyy).append(splitChar).append(MM).append(splitChar).append(dd);
        return stringBuilder.toString();
      }
    } else if (isYYYYMMDDCN(inputStr)) {
      stringBuilder.append(yyyy).append("年").append(MM).append("月").append(dd).append("日");
      return stringBuilder.toString();
    } else if (isMMDDYYYY(inputStr)) {
      if (!splitChar.equals("")) {
        String[] splitResult = null;
        if (splitChar.equals(".")) {
          splitResult = inputStr.split("\\.");
        } else {
          splitResult = inputStr.split(splitChar);
        }

        if (splitResult != null && splitResult.length == 3) {
          String tmpYyyy = splitResult[2];
          // 判断年的长度
          stringBuilder.append(MM).append(splitChar).append(dd).append(splitChar);
          if (tmpYyyy.length() == 2) {
            stringBuilder.append(yy);
          } else {
            stringBuilder.append(yyyy);
          }
          return stringBuilder.toString();
        }
      }
    }
    return "";
  }

  private static String determineSplitChar(String inputStr) {
    if (inputStr.contains("-")) {
      return "-";
    } else if (inputStr.contains("/")) {
      return "/";
    } else if (inputStr.contains(".")) {
      return ".";
    }
    return "";
  }

  public static boolean isYYYYMMDD(String inputStr) {
    if (!inputStr.contains("-") && !inputStr.contains(".") && !inputStr.contains("/")) {
      return false;
    }
    return YYYY_MM_DD.matcher(inputStr).matches();
  }

  public static boolean isYYYYMMDDCN(String inputStr) {
    return YYYY_MM_DD_CN.matcher(inputStr).matches();
  }

  public static boolean isMMDDYYYY(String inputStr) {
    if (!inputStr.contains("-") && !inputStr.contains(".") && !inputStr.contains("/")) {
      return false;
    }
    return MM_DD_YYYY.matcher(inputStr).matches();
  }



  /*
   * public static boolean isDate(String inputStr) { String upperCaseStr=inputStr.toUpperCase();
   * 
   * if(endWithAmOrPm(upperCaseStr)) { return true; }else if(containWeekStr(upperCaseStr)) { return
   * true; }else if(containMonthStr(upperCaseStr)) { return true; }else
   * if(isMatchDatePattern(inputStr)) { return true; }
   * 
   * return false; }
   */

  private static boolean isMatchDatePattern(String inputStr) {
    if (YYYY_MM_DD.matcher(inputStr).matches()) {
      return true;
    } else if (YYYY_MM_DD_T_HH_MM_SS.matcher(inputStr).matches()) {
      return true;
    } else if (DD_MM_YYYY.matcher(inputStr).matches()) {
      return true;
    } else if (MM_DD_YYYY.matcher(inputStr).matches()) {
      return true;
    } else if (YYYY_MM.matcher(inputStr).matches()) {
      return true;
    } else if (QNYYYY.matcher(inputStr).matches()) {
      return true;
    } else if (TIMEZONE.matcher(inputStr).matches()) {
      return true;
    }


    return false;
  }

  private static boolean endWithAmOrPm(String str) {
    return str.endsWith("AM") || str.endsWith("PM");
  }

  private static boolean containWeekStr(String str) {
    return DataTypeDetermine.containWordSetInString(weekSet, str);
  }

  private static boolean containMonthStr(String str) {
    return DataTypeDetermine.containWordSetInString(monthSet, str);
  }

  public static void main(String[] args) {
    System.out.println(DateTypeDetermine.isDateTime("2016-04-07 04:17:56.0"));
  }


}
