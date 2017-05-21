package com.sizzler.common.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class JodaDateUtil {

  public static final String YYYY_MM_DD = "yyyy-MM-dd";
  public static final String YYYY_MM_DD_HH = "yyyy-MM-dd:HH";

  public static final String START_DATE = "startDate";
  public static final String END_DATE = "endDate";

  // 设置一周的开始日期，值为 FIRST_DAY_OF_WEEK_SUNDAY、 FIRST_DAY_OF_WEEK_MONDAY
  public static final int FIRST_DAY_OF_WEEK_SUNDAY = 1;
  public static final int FIRST_DAY_OF_WEEK_MONDAY = 0;

  public static String getCurrentDate() {
    return getCurrentDate("yyyy-MM-dd");
  }

  /**
   * 获取当前时间，返回格式：2016-02-02 10:00:00
   * @return
   */
  public static String getCurrentDateTime() {
    return getCurrentDate("yyyy-MM-dd HH:mm:ss");
  }

  public static String getCurrentDate(String pattern) {
    DateTime currentDateTime = DateTime.now();
    String currentDateTimeStr = currentDateTime.toString(pattern);
    return currentDateTimeStr;
  }

  public static String getDateString(Date date, String pattern) {
    return getDateString(date.getTime(), pattern);
  }

  public static String getDateString(long millisecond, String pattern) {
    DateTime dateTime = new DateTime(millisecond);
    String dateTimeStr = dateTime.toString(pattern);
    return dateTimeStr;
  }

  public static Integer getWeekOfYear(String date) {
    DateTime dateTime = new DateTime(date);
    Integer weekOfYear = dateTime.getWeekOfWeekyear();
    return weekOfYear;
  }

  public static String getYearAndWeek(String date) {
    Integer weekOfYear = getWeekOfYear(date);
    String weekOfYearStr = weekOfYear + "";
    if (weekOfYear < 10) {
      weekOfYearStr = "0" + weekOfYearStr;
    }
    DateTime dateTime = new DateTime(date);
    Integer year = dateTime.getYear();
    return year + weekOfYearStr;
  }

  public static String addWeek(String date, Integer weeks) {
    DateTime dateTime = new DateTime(date);
    DateTime newDateTime = dateTime.plusWeeks(weeks);
    return newDateTime.toString(YYYY_MM_DD);
  }

  /**
   * 计算区间天数
   * 
   * @param startDate
   * @param endDate
   * @return
   */
  public static Integer getBetweenDays(String startDate, String endDate) {
    DateTime begin = new DateTime(startDate);
    DateTime end = new DateTime(endDate);
    // 计算区间天数
    Period p = new Period(begin, end, PeriodType.days());
    int days = p.getDays();
    return days;
  }

  /**
   * 
   * Description: 计算两个小时之间的所有小时数，包括当前<br>
   * 
   * @param startDateHour yyyy-MM-dd:HH 开始时间小时格式
   * @param endDateHour yyyy-MM-dd:HH 结束时间小时格式
   * @return
   */
  public static Integer getBetweenHours(String startDateHour, String endDateHour) {
    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd:HH"); // 时间解析
    DateTime begin = DateTime.parse(startDateHour, format);
    DateTime end = DateTime.parse(endDateHour, format);
    // 计算区间天数
    Period p = new Period(begin, end, PeriodType.hours());
    int hours = p.getHours();
    return hours;
  }

  public static List<String> getBetweenDaysList(String startDate, String endDate) {
    return getBetweenDaysList(startDate, endDate, YYYY_MM_DD);
  }

  public static List<String> getBetweenDaysList(String startDate, String endDate, String pattern) {
    DateTime begin = new DateTime(startDate);
    int days = getBetweenDays(startDate, endDate);
    List<String> dayList = new ArrayList<String>();
    DateTime tempEnd = null;
    for (int i = 0; i <= days; i++) {
      tempEnd = begin.plusDays(i);
      dayList.add(tempEnd.toString(pattern));
    }
    return dayList;
  }

  /**
   * 
   * Description: 计算两个小时之间的所有小时，包括当前<br>
   * 
   * @param startDateHour yyyy-MM-dd:HH 开始时间小时格式
   * @param endDateHour yyyy-MM-dd:HH 结束时间小时格式
   * @param pattern 返回小时的格式
   * @return List<String>
   */
  public static List<String> getBetweenHoursList(String startDateHour, String endDateHour,
      String pattern) {
    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd:HH"); // 时间解析
    DateTime begin = DateTime.parse(startDateHour, format);
    int hours = getBetweenHours(startDateHour, endDateHour);
    List<String> hourList = new ArrayList<String>();
    DateTime tempEnd = null;
    for (int i = 0; i <= hours; i++) {
      tempEnd = begin.plusHours(i);
      hourList.add(tempEnd.toString(pattern));
    }
    return hourList;
  }

  public static Map<String, String> getCurrentDayStartAndEnd(String pattern) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTime currentDateTime = DateTime.now();
    String currentDateTimeStr = currentDateTime.toString(pattern);
    startAndEndMap.put(START_DATE, currentDateTimeStr);
    startAndEndMap.put(END_DATE, currentDateTimeStr);
    return startAndEndMap;
  }

  public static Map<String, String> getCurrentDayStartAndEnd(String pattern, String baseDate) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd"); // 时间解析
    DateTime baseDateTime = DateTime.parse(baseDate, format);
    String baseDateTimeStr = baseDateTime.toString(pattern);
    startAndEndMap.put(START_DATE, baseDateTimeStr);
    startAndEndMap.put(END_DATE, baseDateTimeStr);
    return startAndEndMap;
  }

  public static Map<String, String> getCurrentWeekStartAndEnd(String pattern, int firstDayOfWeek) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTime currentDateTime = DateTime.now();
    String currentDateTimeStr = currentDateTime.toString(pattern);
    DateTime firstDayTimeOfWeek = getFirstDayOfWeek(currentDateTime, firstDayOfWeek);
    String startDay = firstDayTimeOfWeek.toString(pattern);
    startAndEndMap.put(START_DATE, startDay);
    startAndEndMap.put(END_DATE, currentDateTimeStr);
    return startAndEndMap;
  }

  public static Map<String, String> getCurrentWeekStartAndEnd(String pattern, int firstDayOfWeek,
      String baseDate) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd"); // 时间解析
    DateTime baseDateTime = DateTime.parse(baseDate, format);
    String baseDateTimeStr = baseDateTime.toString(pattern);
    DateTime firstDayTimeOfWeek = getFirstDayOfWeek(baseDateTime, firstDayOfWeek);
    String startDay = firstDayTimeOfWeek.toString(pattern);
    startAndEndMap.put(START_DATE, startDay);
    startAndEndMap.put(END_DATE, baseDateTimeStr);
    return startAndEndMap;
  }

  public static Map<String, String> getCurrentMonthStartAndEnd(String pattern) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTime currentDateTime = DateTime.now();
    String currentDateTimeStr = currentDateTime.toString(pattern);
    DateTime firstDayTimeOfMonth = getFirstDayOfMonth(currentDateTime);
    String startDay = firstDayTimeOfMonth.toString(pattern);
    startAndEndMap.put(START_DATE, startDay);
    startAndEndMap.put(END_DATE, currentDateTimeStr);
    return startAndEndMap;
  }

  public static Map<String, String> getCurrentMonthStartAndEnd(String pattern, String baseDate) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd"); // 时间解析
    DateTime baseDateTime = DateTime.parse(baseDate, format);
    String baseDateTimeStr = baseDateTime.toString(pattern);
    DateTime firstDayTimeOfMonth = getFirstDayOfMonth(baseDateTime);
    baseDate = JodaDateUtil.minusDays(1); // 返回昨天的日期
    String startDay = firstDayTimeOfMonth.toString(pattern);
    startAndEndMap.put(START_DATE, startDay);
    startAndEndMap.put(END_DATE, baseDateTimeStr);
    return startAndEndMap;
  }

  public static Map<String, String> getLastDayStartAndEnd(String pattern) {
    return getLastDaysStartAndEnd(1, pattern, false);
  }

  public static Map<String, String> getLastDayStartAndEnd(String pattern, String baseDate) {
    return getLastDaysStartAndEnd(1, pattern, false, baseDate);
  }

  public static Map<String, String> getLastDaysStartAndEnd(Integer days, String pattern,
      boolean containToday) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTime currentDateTime = DateTime.now();
    if (!containToday) {
      currentDateTime = minusDays(currentDateTime, 1);
    }
    String currentDateTimeStr = currentDateTime.toString(pattern);
    DateTime lastDaysDateTime = minusDays(currentDateTime, days - 1);
    String lastDaysDateTimeStr = lastDaysDateTime.toString(pattern);
    startAndEndMap.put(START_DATE, lastDaysDateTimeStr);
    startAndEndMap.put(END_DATE, currentDateTimeStr);
    return startAndEndMap;
  }

  public static Map<String, String> getLastDaysStartAndEnd(Integer days, String pattern,
      boolean containToday, String baseDate) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd"); // 时间解析
    DateTime baseDateTime = DateTime.parse(baseDate, format);
    if (!containToday) {
      baseDateTime = minusDays(baseDateTime, 1);
    }
    String baseDateTimeStr = baseDateTime.toString(pattern);
    DateTime lastDaysDateTime = minusDays(baseDateTime, days - 1);
    String lastDaysDateTimeStr = lastDaysDateTime.toString(pattern);
    startAndEndMap.put(START_DATE, lastDaysDateTimeStr);
    startAndEndMap.put(END_DATE, baseDateTimeStr);
    return startAndEndMap;
  }

  public static Map<String, String> getLastWeekStartAndEnd(String pattern, int firstDayOfWeek) {
    return getLastWeeksStartAndEnd(1, pattern, firstDayOfWeek, false);
  }

  public static Map<String, String> getLastWeekStartAndEnd(String pattern, int firstDayOfWeek,
      String baseDate) {
    return getLastWeeksStartAndEnd(1, pattern, firstDayOfWeek, false, baseDate);
  }

  public static Map<String, String> getLastWeeksStartAndEnd(Integer weeks, String pattern,
      int firstDayOfWeek, boolean containThisWeek) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTime currentDateTime = DateTime.now();
    if (!containThisWeek) {
      currentDateTime = currentDateTime.minusDays(currentDateTime.getDayOfWeek()); // 计算上周日
      currentDateTime = currentDateTime.minusDays(firstDayOfWeek); // 修正周的最后一天
    }

    String currentDateTimeStr = currentDateTime.toString(pattern);
    DateTime lastWeeksDateTime = minusWeeks(currentDateTime, weeks - 1);
    lastWeeksDateTime = minusDays(lastWeeksDateTime, lastWeeksDateTime.getDayOfWeek() - 1);// 计算周一
    lastWeeksDateTime = lastWeeksDateTime.minusDays(firstDayOfWeek); // 修正周的第一天
    String lastWeeksDateTimeStr = lastWeeksDateTime.toString(pattern);
    startAndEndMap.put(START_DATE, lastWeeksDateTimeStr);
    startAndEndMap.put(END_DATE, currentDateTimeStr);
    return startAndEndMap;
  }

  public static Map<String, String> getLastWeeksStartAndEnd(Integer weeks, String pattern,
      int firstDayOfWeek, boolean containThisWeek, String baseDate) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd"); // 时间解析
    DateTime baseDateTime = DateTime.parse(baseDate, format);
    if (!containThisWeek) {
      baseDateTime = baseDateTime.minusDays(baseDateTime.getDayOfWeek()); // 计算上周日
      baseDateTime = baseDateTime.minusDays(firstDayOfWeek); // 修正周的最后一天
    }
    String baseDateTimeStr = baseDateTime.toString(pattern);
    DateTime lastWeeksDateTime = minusWeeks(baseDateTime, weeks - 1);
    lastWeeksDateTime = minusDays(lastWeeksDateTime, lastWeeksDateTime.getDayOfWeek() - 1);// 计算周一
    lastWeeksDateTime = lastWeeksDateTime.minusDays(firstDayOfWeek); // 修正周的第一天
    String lastWeeksDateTimeStr = lastWeeksDateTime.toString(pattern);
    startAndEndMap.put(START_DATE, lastWeeksDateTimeStr);
    startAndEndMap.put(END_DATE, baseDateTimeStr);
    return startAndEndMap;
  }

  public static Map<String, String> getLastMonthStartAndEnd(String pattern) {
    return getLastMonthsStartAndEnd(1, pattern, false);
  }

  public static Map<String, String> getLastMonthStartAndEnd(String pattern, String baseDate) {
    return getLastMonthsStartAndEnd(1, pattern, false, baseDate);
  }

  public static Map<String, String> getLastMonthsStartAndEnd(Integer months, String pattern,
      boolean containThisMonth) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTime currentDateTime = DateTime.now();
    if (!containThisMonth) {
      currentDateTime = currentDateTime.minusDays(currentDateTime.getDayOfMonth()); // 计算上月末
    }
    String currentDateTimeStr = currentDateTime.toString(pattern);
    DateTime lastMonthsDateTime = minusMonths(currentDateTime, months - 1);
    lastMonthsDateTime = minusDays(lastMonthsDateTime, lastMonthsDateTime.getDayOfMonth() - 1);// 计算月初
    String lastMonthsDateTimeStr = lastMonthsDateTime.toString(pattern);
    startAndEndMap.put(START_DATE, lastMonthsDateTimeStr);
    startAndEndMap.put(END_DATE, currentDateTimeStr);
    return startAndEndMap;
  }

  public static Map<String, String> getLastMonthsStartAndEnd(Integer months, String pattern,
      boolean containThisMonth, String baseDate) {
    Map<String, String> startAndEndMap = new LinkedHashMap<String, String>();
    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd"); // 时间解析
    DateTime baseDateTime = DateTime.parse(baseDate, format);
    if (!containThisMonth) {
      baseDateTime = baseDateTime.minusDays(baseDateTime.getDayOfMonth()); // 计算上月末
    }
    String baseDateTimeStr = baseDateTime.toString(pattern);
    DateTime lastMonthsDateTime = minusMonths(baseDateTime, months - 1);
    lastMonthsDateTime = minusDays(lastMonthsDateTime, lastMonthsDateTime.getDayOfMonth() - 1);// 计算月初
    String lastMonthsDateTimeStr = lastMonthsDateTime.toString(pattern);
    startAndEndMap.put(START_DATE, lastMonthsDateTimeStr);
    startAndEndMap.put(END_DATE, baseDateTimeStr);
    return startAndEndMap;
  }

  /**
   * 返回 n天前的日期 返回日期格式为：yyyy-MM-dd
   */
  public static String minusDays(Integer days) {
    return minusDays(days, "yyyy-MM-dd");
  }

  /**
   * 按所需格式返回 n天前的日期
   */
  public static String minusDays(Integer days, String pattern) {
    DateTime currentDateTime = DateTime.now();
    DateTime beforeDateTime = minusDays(currentDateTime, days);
    return beforeDateTime.toString(pattern);
  }

  /**
   * 返回某一天n天前的日期 传入日期格式为： yyyy-MM-dd 返回日期格式为：yyyy-MM-dd
   */
  public static String minusDays(String currentDate, Integer days) {
    return minusDays(currentDate, days, "yyyy-MM-dd");
  }

  /**
   * 按所需格式返回某一天n天前的日期 传入日期格式为： yyyy-MM-dd
   */
  public static String minusDays(String currentDate, Integer days, String pattern) {
    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd"); // 时间解析
    DateTime currentDateTime = DateTime.parse(currentDate, format);
    DateTime beforeDateTime = minusDays(currentDateTime, days);
    return beforeDateTime.toString(pattern);
  }

  private static DateTime getFirstDayOfWeek(DateTime currentDateTime, int firstDayOfWeek) {
    DateTime firstDayTimeOfWeek = currentDateTime.dayOfWeek().withMinimumValue();
    firstDayTimeOfWeek = firstDayTimeOfWeek.minusDays(firstDayOfWeek); // 修正周的第一天
    return firstDayTimeOfWeek;
  }

  private static DateTime getFirstDayOfMonth(DateTime currentDateTime) {
    DateTime firstDayTimeOfMonth = currentDateTime.dayOfMonth().withMinimumValue();
    return firstDayTimeOfMonth;
  }

  private static DateTime minusDays(DateTime currentDateTime, Integer days) {
    DateTime resultDateTime = currentDateTime.minusDays(days);
    return resultDateTime;
  }

  private static DateTime minusWeeks(DateTime currentDateTime, Integer weeks) {
    DateTime resultDateTime = currentDateTime.minusWeeks(weeks);
    return resultDateTime;
  }

  private static DateTime minusMonths(DateTime currentDateTime, Integer months) {
    DateTime resultDateTime = currentDateTime.minusMonths(months);
    return resultDateTime;
  }

  public static List<String> getCurrentDayList(String pattern) {
    Map<String, String> startAndEndMap = getCurrentDayStartAndEnd(pattern);
    String startDate = startAndEndMap.get(START_DATE);
    String endDate = startAndEndMap.get(END_DATE);
    return getBetweenDaysList(startDate, endDate, pattern);
  }

  public static List<String> getCurrentWeekList(String pattern, int firstDayOfWeek) {
    Map<String, String> startAndEndMap = getCurrentWeekStartAndEnd(pattern, firstDayOfWeek);
    String startDate = startAndEndMap.get(START_DATE);
    String endDate = startAndEndMap.get(END_DATE);
    return getBetweenDaysList(startDate, endDate, pattern);
  }

  public static List<String> getCurrentMonthList(String pattern) {
    Map<String, String> startAndEndMap = getCurrentMonthStartAndEnd(pattern);
    String startDate = startAndEndMap.get(START_DATE);
    String endDate = startAndEndMap.get(END_DATE);
    return getBetweenDaysList(startDate, endDate, pattern);
  }

  public static List<String> getLastDayList(String pattern) {
    DateTime currentDateTime = DateTime.now();
    DateTime beginDateTime = minusHours(currentDateTime, 24);
    List<String> dayHourList = new ArrayList<String>();
    DateTime tempEnd = null;
    for (int i = 0; i <= 24; i++) {
      tempEnd = beginDateTime.plusHours(i);
      dayHourList.add(tempEnd.toString(pattern));
    }
    return dayHourList;

  }

  private static DateTime minusHours(DateTime currentDateTime, Integer hours) {
    DateTime resultDateTime = currentDateTime.minusHours(hours);
    return resultDateTime;
  }

  public static List<String> getLastWeekList(String pattern, int firstDayOfWeek) {
    Map<String, String> startAndEndMap = getLastWeekStartAndEnd(pattern, firstDayOfWeek);
    String startDate = startAndEndMap.get(START_DATE);
    String endDate = startAndEndMap.get(END_DATE);
    return getBetweenDaysList(startDate, endDate, pattern);
  }

  public static List<String> getLastMonthList(String pattern) {
    Map<String, String> startAndEndMap = getLastMonthStartAndEnd(pattern);
    String startDate = startAndEndMap.get(START_DATE);
    String endDate = startAndEndMap.get(END_DATE);
    return getBetweenDaysList(startDate, endDate, pattern);
  }

  public static String parseDateFormate(String date, String srcPattern, String destPattern) {
    DateTimeFormatter format = DateTimeFormat.forPattern(srcPattern);
    DateTime datetime = DateTime.parse(date, format);
    return datetime.toString(destPattern);
  }

  public static String parseDateFormate(String date, String srcPattern, String destPattern,
      Locale locale) {
    DateTimeFormatter format = DateTimeFormat.forPattern(srcPattern);
    DateTime datetime = DateTime.parse(date, format);
    return datetime.toString(destPattern, locale);
  }

  public static void main(String[] args) {

    Map<String, String> startAndEndMap = getLastDayStartAndEnd(YYYY_MM_DD);
    Set<String> keySet = startAndEndMap.keySet();
    for (String key : keySet) {
      System.out.println(startAndEndMap.get(key));
    }

    System.out.println("==========================================");

    List<String> dayHourList = getLastDayList(YYYY_MM_DD_HH);
    for (String dayHour : dayHourList) {
      System.out.println(dayHour);
    }

    /*
     * List<String> daysList=getLastMonthList(YYYY_MM_DD); for(String day:daysList) {
     * System.out.println(day); }
     */
    System.out.println("=============================");
    List<String> list = getBetweenHoursList("2015-01-02:00", "2015-01-02:23", "HH");
    for (String str : list) {
      System.out.println(str);
    }

    System.out.println("==========================================");

    startAndEndMap = getLastDayStartAndEnd(YYYY_MM_DD);
    keySet = startAndEndMap.keySet();
    for (String key : keySet) {
      System.out.println(startAndEndMap.get(key));
    }

    System.out.println("==========================================");

    startAndEndMap = getLastDaysStartAndEnd(3, YYYY_MM_DD, true);
    keySet = startAndEndMap.keySet();
    for (String key : keySet) {
      System.out.println(startAndEndMap.get(key));
    }

    System.out.println("==========================================");

    startAndEndMap =
        getLastWeeksStartAndEnd(2, YYYY_MM_DD, JodaDateUtil.FIRST_DAY_OF_WEEK_SUNDAY, true);
    keySet = startAndEndMap.keySet();
    for (String key : keySet) {
      System.out.println(startAndEndMap.get(key));
    }

    System.out.println("==========================================");

    startAndEndMap = getLastMonthsStartAndEnd(2, YYYY_MM_DD, false);
    keySet = startAndEndMap.keySet();
    for (String key : keySet) {
      System.out.println(startAndEndMap.get(key));
    }

    System.out.println("==========================================");

    startAndEndMap = getLastMonthStartAndEnd(YYYY_MM_DD, "2015-05-15");
    keySet = startAndEndMap.keySet();
    for (String key : keySet) {
      System.out.println(startAndEndMap.get(key));
    }

    System.out.println("==========================================");

    startAndEndMap =
        getLastWeekStartAndEnd(YYYY_MM_DD, JodaDateUtil.FIRST_DAY_OF_WEEK_SUNDAY, "2015-05-15");
    keySet = startAndEndMap.keySet();
    for (String key : keySet) {
      System.out.println(startAndEndMap.get(key));
    }

    System.out.println("=========================================");

    DateTime currentDateTime = DateTime.now();

    int firstDayOfWeek = FIRST_DAY_OF_WEEK_SUNDAY;
    System.out.println(getFirstDayOfWeek(currentDateTime, firstDayOfWeek).toString("yyyy-MM-dd"));
    firstDayOfWeek = FIRST_DAY_OF_WEEK_MONDAY;
    System.out.println(getFirstDayOfWeek(currentDateTime, firstDayOfWeek).toString("yyyy-MM-dd"));
  }
}
