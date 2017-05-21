package com.sizzler.common.sizzler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sizzler.common.utils.JodaDateUtil;

public class PtoneDateUtil {

  public static final int COMPARE_TYPE_QOQ = 10; // 环比
  public static final int COMPARE_TYPE_YOY = 20; // 同比

  public static final String DATE_KEY_TODAY = "today"; // 今天
  public static final String DATE_KEY_ALLTIME = "all_time";// 全部时间
  public static final String DATE_KEY_YESTERDAY = "yesterday"; // 昨天
  public static final String DATE_KEY_THISWEEK = "this_week"; // 本周
  public static final String DATE_KEY_LASTWEEK = "last_week"; // 上周
  public static final String DATE_KEY_THISMONTH = "this_month"; // 本月
  public static final String DATE_KEY_LASTMONTH = "last_month"; // 上月
  public static final String DATE_KEY_LASTXXHOUR_REG = "^last[1-9]\\d*hour$"; // 最近XX小时
  public static final String DATE_KEY_PASTXXHOUR_REG = "^past[1-9]\\d*hour$"; // 过去XX小时
  public static final String DATE_KEY_LASTXXDAY_REG = "^last[1-9]\\d*day$"; // 最近XX天
  public static final String DATE_KEY_PASTXXDAY_REG = "^past[1-9]\\d*day$"; // 过去XX天
  public static final String DATE_KEY_LASTXXWEEK_REG = "^last[1-9]\\d*week$"; // 最近XX周
  public static final String DATE_KEY_PASTXXWEEK_REG = "^past[1-9]\\d*week$"; // 过去XX周
  public static final String DATE_KEY_LASTXXMONTH_REG = "^last[1-9]\\d*month$"; // 最近XX月
  public static final String DATE_KEY_PASTXXMONTH_REG = "^past[1-9]\\d*month$"; // 过去XX月
  public static final String DATE_KEY_FROM_DATE_UNTILE_TODAY_REG =
      "^(\\d{4})-(0\\d{1}|1[0-2])-(0\\d{1}|[12]\\d{1}|3[01])\\|today$"; // 至今，"yyyy-MM-dd|today"
  public static final String DATE_KEY_FROM_DATE_UNTILE_YESTERDAY_REG =
      "^(\\d{4})-(0\\d{1}|1[0-2])-(0\\d{1}|[12]\\d{1}|3[01])\\|yesterday$"; // 至昨天，"yyyy-MM-dd|yesterday"
  public static final String DATE_KEY_FROM_DATE_TO_DATE_REG =
      "^(\\d{4})-(0\\d{1}|1[0-2])-(0\\d{1}|[12]\\d{1}|3[01])\\|(\\d{4})-(0\\d{1}|1[0-2])-(0\\d{1}|[12]\\d{1}|3[01])$"; // 自定义时间，"yyyy-MM-dd|yyyy-MM-dd"

  public static final String DATE_PERIOD_HOUR = "hour"; // 按小时
  public static final String DATE_PERIOD_DAY = "day"; // 按天
  public static final String DATE_PERIOD_WEEK = "week"; // 按周
  public static final String DATE_PERIOD_MONTH = "month"; // 按月
  public static final String DATE_PERIOD_YEAR = "year"; // 按年

  public static final int FIRST_DAY_OF_WEEK_SUNDAY = JodaDateUtil.FIRST_DAY_OF_WEEK_SUNDAY;
  public static final int FIRST_DAY_OF_WEEK_MONDAY = JodaDateUtil.FIRST_DAY_OF_WEEK_MONDAY;

  private static PtoneDateUtil ptoneDateUtilWeekStartSunday = new PtoneDateUtil(
      FIRST_DAY_OF_WEEK_SUNDAY);
  private static PtoneDateUtil ptoneDateUtilWeekStartMonday = new PtoneDateUtil(
      FIRST_DAY_OF_WEEK_MONDAY);

  // 默认周的第一天为周日
  private int firstDayOfWeek = JodaDateUtil.FIRST_DAY_OF_WEEK_SUNDAY;

  private PtoneDateUtil() {}

  private PtoneDateUtil(int firstDayOfWeek) {
    this.firstDayOfWeek = firstDayOfWeek;
  }

  /**
   * 修正dateKey
   * 
   * @return
   */
  private static String fixDateKey(String dateKey) {
    // 如果dateKey为空，设置默认为lastWeek
    if (dateKey == null || "".equals(dateKey) || PtoneDateUtil.DATE_KEY_ALLTIME.equals(dateKey)) {
      // dateKey = DATE_KEY_LASTWEEK;
      dateKey = "";
    }
    return dateKey.toLowerCase();// 转小写
  }

  /**
   * 从dateKey字符串中提取数字
   * @param dateKey
   * @return
   */
  private static int pickNumberFromString(String dateKey) {
    Pattern p = Pattern.compile("[^0-9]");
    Matcher m = p.matcher(dateKey);
    String numStr = m.replaceAll("").trim();
    int num = Integer.valueOf(numStr);
    return num;
  }

  /**
   * 设置周的第一天为firstDayOfWeek <br>
   * 值为JodaDateUtil.FIRST_DAY_OF_WEEK_SUNDAY 、 FIRST_DAY_OF_WEEK_MONDAY
   * 
   * @param firstDayOfWeek
   * @return
   */
  public static PtoneDateUtil getInstance(int firstDayOfWeek) {
    if (firstDayOfWeek == FIRST_DAY_OF_WEEK_MONDAY) {
      return ptoneDateUtilWeekStartMonday;
    } else {
      return ptoneDateUtilWeekStartSunday;
    }
  }

  /**
   * 根据dateKey和时间格式，计算返回的开始时间和结束时间 以map返回：startDate、endDate
   */
  public Map<String, String> getStartEndDate(String dateKey, String dateFormat) {
    Map<String, String> dateMap = new HashMap<String, String>();
    dateKey = fixDateKey(dateKey);
    if (DATE_KEY_TODAY.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getCurrentDayStartAndEnd(dateFormat);
    } else if (DATE_KEY_YESTERDAY.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getLastDayStartAndEnd(dateFormat);
    } else if (DATE_KEY_THISWEEK.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getCurrentWeekStartAndEnd(dateFormat, firstDayOfWeek);
    } else if (DATE_KEY_LASTWEEK.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getLastWeekStartAndEnd(dateFormat, firstDayOfWeek);
    } else if (DATE_KEY_THISMONTH.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getCurrentMonthStartAndEnd(dateFormat);
    } else if (DATE_KEY_LASTMONTH.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getLastMonthStartAndEnd(dateFormat);
    } else if (dateKey != null && dateKey.matches(DATE_KEY_PASTXXDAY_REG)) {
      int days = pickNumberFromString(dateKey);
      dateMap = JodaDateUtil.getLastDaysStartAndEnd(days, dateFormat, false);
    } else if (dateKey != null && dateKey.matches(DATE_KEY_LASTXXDAY_REG)) {
      int days = pickNumberFromString(dateKey);
      dateMap = JodaDateUtil.getLastDaysStartAndEnd(days, dateFormat, true);
    } else if (dateKey != null && dateKey.matches(DATE_KEY_PASTXXWEEK_REG)) {
      int weeks = pickNumberFromString(dateKey);
      dateMap = JodaDateUtil.getLastWeeksStartAndEnd(weeks, dateFormat, firstDayOfWeek, false);
    } else if (dateKey != null && dateKey.matches(DATE_KEY_LASTXXWEEK_REG)) {
      int weeks = pickNumberFromString(dateKey);
      dateMap = JodaDateUtil.getLastWeeksStartAndEnd(weeks, dateFormat, firstDayOfWeek, true);
    } else if (dateKey != null && dateKey.matches(DATE_KEY_FROM_DATE_UNTILE_TODAY_REG)) {
      String[] date = dateKey.split("\\|");
      dateMap.put(JodaDateUtil.START_DATE, date[0]);
      dateMap.put(JodaDateUtil.END_DATE, JodaDateUtil.getCurrentDate());
    } else if (dateKey != null && dateKey.matches(DATE_KEY_FROM_DATE_UNTILE_YESTERDAY_REG)) {
      String[] date = dateKey.split("\\|");
      dateMap.put(JodaDateUtil.START_DATE, date[0]);
      dateMap.put(JodaDateUtil.END_DATE, JodaDateUtil.minusDays(1));
    } else if (dateKey != null && dateKey.matches(DATE_KEY_FROM_DATE_TO_DATE_REG)) {
      String[] date = dateKey.split("\\|");
      dateMap.put(JodaDateUtil.START_DATE, date[0]);
      dateMap.put(JodaDateUtil.END_DATE, date[1]);
    } else {
      // dateMap = JodaDateUtil.getLastDaysStartAndEnd(15, dateFormat, true); // 默认返回15天数据
      dateMap.put(JodaDateUtil.START_DATE, "");
      dateMap.put(JodaDateUtil.END_DATE, "");
    }
    return dateMap;
  }

  /**
   * 返回环比对应的开始结束日期 eg: 按天顺延，本期14天，上一期就是从本期开始往前再顺延14天（按天为粒度）
   * 
   * @param dateKey
   * @param dateFormat
   * @return
   */
  public Map<String, String> getQoqStartEndDate(String dateKey, String dateFormat) {
    Map<String, String> dateMap = new HashMap<String, String>();
    dateKey = fixDateKey(dateKey);
    Map<String, String> currentDateMap = getStartEndDate(dateKey, dateFormat);
    String startDate = currentDateMap.get(JodaDateUtil.START_DATE);
    String endDate = currentDateMap.get(JodaDateUtil.END_DATE);
    if (startDate != null && !"".equals(startDate) && endDate != null && !"".equals(endDate)) {
      int interval = JodaDateUtil.getBetweenDays(startDate, endDate);
      String lastStartDate = JodaDateUtil.minusDays(startDate, interval + 1);
      String lastEndDate = JodaDateUtil.minusDays(endDate, interval + 1);
      dateMap.put(JodaDateUtil.START_DATE, lastStartDate);
      dateMap.put(JodaDateUtil.END_DATE, lastEndDate);
    }
    return dateMap;
  }

  /**
   * 根据dateKey和时间格式，计算上一周期数据的计算基准时间 eg: 上期的同一天
   */
  private String getLastBaseDate(String dateKey, String dateFormat, int compareType) {
    String baseDate = "";
    dateKey = fixDateKey(dateKey);
    if (compareType == COMPARE_TYPE_QOQ) { // 环比
      if (DATE_KEY_TODAY.equalsIgnoreCase(dateKey)) {
        baseDate = JodaDateUtil.minusDays(1); // 返回昨天的日期
      } else if (DATE_KEY_YESTERDAY.equalsIgnoreCase(dateKey)) {
        baseDate = JodaDateUtil.minusDays(1); // 返回昨天的日期
      } else if (DATE_KEY_THISWEEK.equalsIgnoreCase(dateKey)) {
        baseDate = JodaDateUtil.minusDays(7); // 返回上周的今天
      } else if (DATE_KEY_LASTWEEK.equalsIgnoreCase(dateKey)) {
        baseDate = JodaDateUtil.minusDays(7); // 返回上周的今天
      } else if (DATE_KEY_THISMONTH.equalsIgnoreCase(dateKey)) {
        baseDate = JodaDateUtil.minusDays(30); // 返回上月的今天
      } else if (DATE_KEY_LASTMONTH.equalsIgnoreCase(dateKey)) {
        baseDate = JodaDateUtil.minusDays(30); // 返回上月的今天
      } else if (dateKey != null && dateKey.matches(DATE_KEY_PASTXXDAY_REG)) {
        int days = pickNumberFromString(dateKey);
        baseDate = JodaDateUtil.minusDays(days);
      } else if (dateKey != null && dateKey.matches(DATE_KEY_LASTXXDAY_REG)) {
        int days = pickNumberFromString(dateKey);
        baseDate = JodaDateUtil.minusDays(days);
      } else if (dateKey != null && dateKey.matches(DATE_KEY_PASTXXWEEK_REG)) {
        int weeks = pickNumberFromString(dateKey);
        baseDate = JodaDateUtil.minusDays(7 * weeks);
      } else if (dateKey != null && dateKey.matches(DATE_KEY_LASTXXWEEK_REG)) {
        int weeks = pickNumberFromString(dateKey);
        baseDate = JodaDateUtil.minusDays(7 * weeks);
      } else if (dateKey != null && dateKey.matches(DATE_KEY_FROM_DATE_UNTILE_TODAY_REG)) {
        baseDate = JodaDateUtil.getCurrentDate();
      } else if (dateKey != null && dateKey.matches(DATE_KEY_FROM_DATE_UNTILE_YESTERDAY_REG)) {
        baseDate = JodaDateUtil.minusDays(1);
      } else if (dateKey != null && dateKey.matches(DATE_KEY_FROM_DATE_TO_DATE_REG)) {
        baseDate = JodaDateUtil.getCurrentDate();
      } else {
        // baseDate = JodaDateUtil.getCurrentDate();
        baseDate = "";
      }
    }
    return baseDate;
  }

  /**
   * 根据dateKey和时间格式，计算返回的上一期开始时间和结束时间 以map返回：startDate、endDate baseDate： 基准日期 eg： 本周1-2 返回 上周1-2
   */
  private Map<String, String> getLastStartEndDate(String dateKey, String dateFormat, String baseDate) {
    Map<String, String> dateMap = new HashMap<String, String>();
    dateKey = fixDateKey(dateKey);
    if (DATE_KEY_TODAY.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getCurrentDayStartAndEnd(dateFormat, baseDate);
    } else if (DATE_KEY_YESTERDAY.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getLastDayStartAndEnd(dateFormat, baseDate);
    } else if (DATE_KEY_THISWEEK.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getCurrentWeekStartAndEnd(dateFormat, firstDayOfWeek, baseDate);
    } else if (DATE_KEY_LASTWEEK.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getLastWeekStartAndEnd(dateFormat, firstDayOfWeek, baseDate);
    } else if (DATE_KEY_THISMONTH.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getCurrentMonthStartAndEnd(dateFormat, baseDate);
    } else if (DATE_KEY_LASTMONTH.equalsIgnoreCase(dateKey)) {
      dateMap = JodaDateUtil.getLastMonthStartAndEnd(dateFormat, baseDate);
    } else if (dateKey.matches(DATE_KEY_PASTXXDAY_REG)) {
      int days = pickNumberFromString(dateKey);
      dateMap = JodaDateUtil.getLastDaysStartAndEnd(days, dateFormat, false, baseDate);
    } else if (dateKey.matches(DATE_KEY_LASTXXDAY_REG)) {
      int days = pickNumberFromString(dateKey);
      dateMap = JodaDateUtil.getLastDaysStartAndEnd(days, dateFormat, true, baseDate);
    } else if (dateKey.matches(DATE_KEY_PASTXXWEEK_REG)) {
      int weeks = pickNumberFromString(dateKey);
      dateMap =
          JodaDateUtil.getLastWeeksStartAndEnd(weeks, dateFormat, firstDayOfWeek, false, baseDate);
    } else if (dateKey.matches(DATE_KEY_LASTXXWEEK_REG)) {
      int weeks = pickNumberFromString(dateKey);
      dateMap =
          JodaDateUtil.getLastWeeksStartAndEnd(weeks, dateFormat, firstDayOfWeek, true, baseDate);
    } else if (dateKey != null && dateKey.matches(DATE_KEY_FROM_DATE_UNTILE_TODAY_REG)) {
      String[] date = dateKey.split("\\|");
      dateMap.put(JodaDateUtil.START_DATE, date[0]);
      dateMap.put(JodaDateUtil.END_DATE, JodaDateUtil.getCurrentDate());
    } else if (dateKey != null && dateKey.matches(DATE_KEY_FROM_DATE_UNTILE_YESTERDAY_REG)) {
      String[] date = dateKey.split("\\|");
      dateMap.put(JodaDateUtil.START_DATE, date[0]);
      dateMap.put(JodaDateUtil.END_DATE, JodaDateUtil.minusDays(1));
    } else if (dateKey != null && dateKey.matches(DATE_KEY_FROM_DATE_TO_DATE_REG)) {
      String[] date = dateKey.split("\\|");
      dateMap.put(JodaDateUtil.START_DATE, date[0]);
      dateMap.put(JodaDateUtil.END_DATE, date[1]);
    } else {
      // dateMap = JodaDateUtil.getLastDaysStartAndEnd(15, dateFormat, true); // 默认返回15天数据
      dateMap.put(JodaDateUtil.START_DATE, "");
      dateMap.put(JodaDateUtil.END_DATE, "");
    }
    return dateMap;
  }

  /**
   * 计算某年某周的开始日期
   * 
   * @param yearNum 格式 yyyy
   * @param weekNum 1到52或者53
   * @param dayOfWeek 周起始日 FIRST_DAY_OF_WEEK_SUNDAY FIRST_DAY_OF_WEEK_MONDAY
   * @return 日期，格式为yyyy-MM-dd
   */
  public static String getYearWeekFirstDay(int yearNo, int weekNo, int dayOfWeek) {
    return getYearWeekFirstDay(yearNo, weekNo, dayOfWeek, "yyyy-MM-dd");
  }

  /**
   * 计算某年某周的开始日期
   * 
   * @param year 格式 yyyy
   * @param week 1到52或者53
   * @param dayOfWeek 周起始日 FIRST_DAY_OF_WEEK_SUNDAY、 FIRST_DAY_OF_WEEK_MONDAY
   * @return 日期,格式为 dateFormat
   */
  public static String getYearWeekFirstDay(int year, int week, int dayOfWeek, String dateFormat) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, 0, 0, 0, 0, 0);// 清空日月时分秒
    if (dayOfWeek == FIRST_DAY_OF_WEEK_MONDAY) {
      cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天
      cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 每周的第一天为周一
    } else {
      cal.setFirstDayOfWeek(Calendar.SUNDAY); // 设置每周的第一天
      cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 每周的第一天为周日
    }
    // 上面两句代码配合，才能实现，每年度的第一个周，是包含第一个星期一的那个周。
    // cal.setMinimalDaysInFirstWeek(7); // 设置第一周最少为7天（不足7天不算作第0周）
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.WEEK_OF_YEAR, week);
    // 分别取得当前日期的年、月、日
    return new SimpleDateFormat(dateFormat).format(cal.getTime());
  }


  // for teest
  public static void main(String[] args) {

    PtoneDateUtil util = PtoneDateUtil.getInstance(FIRST_DAY_OF_WEEK_SUNDAY);

    // util.testLastStartEndDate(DATE_KEY_TODAY);
    // util.testLastStartEndDate(DATE_KEY_YESTERDAY);
    // util.testLastStartEndDate(DATE_KEY_THISWEEK);
    // util.testLastStartEndDate(DATE_KEY_LASTWEEK);
    // util.testLastStartEndDate(DATE_KEY_THISMONTH);
    // util.testLastStartEndDate(DATE_KEY_LASTMONTH);
    // util.testLastStartEndDate(DATE_KEY_LAST7DAY);
    // util.testLastStartEndDate(DATE_KEY_PAST7DAY);
    // util.testLastStartEndDate(DATE_KEY_LAST30DAY);
    // util.testLastStartEndDate(DATE_KEY_PAST30DAY);
    // util.testLastStartEndDate(DATE_KEY_LAST12WEEK);
    // util.testLastStartEndDate(DATE_KEY_PAST12WEEK);

    util.testQoqStartEndDate(DATE_KEY_TODAY);
    util.testQoqStartEndDate(DATE_KEY_YESTERDAY);
    util.testQoqStartEndDate(DATE_KEY_THISWEEK);
    util.testQoqStartEndDate(DATE_KEY_LASTWEEK);
    util.testQoqStartEndDate(DATE_KEY_THISMONTH);
    util.testQoqStartEndDate(DATE_KEY_LASTMONTH);
    util.testQoqStartEndDate("last7day");
    util.testQoqStartEndDate("last3day");
    util.testQoqStartEndDate("last2day");
    util.testQoqStartEndDate("past3day");
    util.testQoqStartEndDate("past2day");
    util.testQoqStartEndDate("past7day");
    util.testQoqStartEndDate("last1week");
    util.testQoqStartEndDate("past1week");

  }

  // for test
  @SuppressWarnings("unused")
  private void testLastStartEndDate(String dateKey) {
    String dateFormat = "yyyy-MM-dd";
    int compareType = COMPARE_TYPE_QOQ;
    Map<String, String> currentDateMap = getStartEndDate(dateKey, dateFormat);
    String baseDate = getLastBaseDate(dateKey, dateFormat, compareType);
    Map<String, String> lastDateMap = getLastStartEndDate(dateKey, dateFormat, baseDate);

    System.out.println("dateKey ==> " + dateKey);
    System.out.println("currentDateMap ==> " + currentDateMap.get("startDate") + "|"
        + currentDateMap.get("endDate"));
    System.out.println("baseDate ==> " + baseDate);
    System.out.println("lastDateMap ==> " + lastDateMap.get("startDate") + "|"
        + lastDateMap.get("endDate"));
    System.out.println("=====================================");
  }

  // for test
  private void testQoqStartEndDate(String dateKey) {
    String dateFormat = "yyyy-MM-dd";
    Map<String, String> currentDateMap = getStartEndDate(dateKey, dateFormat);
    Map<String, String> lastDateMap = getQoqStartEndDate(dateKey, dateFormat);

    System.out.println("dateKey ==> " + dateKey);
    System.out.println("currentDateMap ==> " + currentDateMap.get("startDate") + "|"
        + currentDateMap.get("endDate"));
    System.out.println("lastDateMap ==> " + lastDateMap.get("startDate") + "|"
        + lastDateMap.get("endDate"));
    System.out.println("=====================================");
  }


}
