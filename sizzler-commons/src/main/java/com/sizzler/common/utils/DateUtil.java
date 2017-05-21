package com.sizzler.common.utils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
  private static final SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");

  public static String replaceDateString(String string) {
    return string.replaceAll("-", "");
  }

  public static Boolean isSameDay(String sDate, String eDate) {
    return !(!CommonUtil.isValid(sDate) || !CommonUtil.isValid(eDate))
        && DateUtil.replaceDateString(sDate).equalsIgnoreCase(DateUtil.replaceDateString(eDate));
  }

  public static String convertToMySqlDate(String dateStr) {
    String str = replaceDateString(dateStr);
    String year = str.substring(0, 4);
    String month = str.substring(4, 6);
    String day = str.substring(6, 8);
    str = year + "-" + month + "-" + day;
    return str;
  }

  public static Integer convertMySqlDateToZDate(Date date) {
    String dateStr = formater.format(date);
    return Integer.parseInt(dateStr);
  }

  public static String getStartThisWeek(String dateStr) {
    Calendar cale = convertStringDate2Calendar(dateStr);
    int dayOfWeek = cale.get(Calendar.DAY_OF_WEEK) - cale.getFirstDayOfWeek();
    cale.add(Calendar.DATE, 1 - dayOfWeek);
    Date date = cale.getTime();
    String result = new SimpleDateFormat("yyyy-MM-dd").format(date);
    return result;
  }

  public static Integer getMonthBefore(Integer month, Integer monthNum) {
    return getMonthBefore(month.toString(), monthNum);
  }

  public static Integer getMonthBefore(String monthStr, Integer monthNum) {
    Calendar cale = convertStringMonth2Calendar(monthStr);
    cale.add(Calendar.MONTH, monthNum);
    StringBuffer sb = new StringBuffer();
    sb.append(cale.get(Calendar.YEAR));
    Integer month = cale.get(Calendar.MONTH) + 1;
    sb.append(month > 9 ? month : "0" + month);
    return Integer.parseInt(sb.toString());
  }

  public static Integer getLastMonth() {
    return getMonthBefore(getCurrentMonth() + "", -1);
  }

  public static Integer getLastMonth(Integer date) {
    return getMonthBefore(getCurrentMonth(date) + "", -1);
  }

  public static Integer getCurrentMonth() {
    Calendar cale = Calendar.getInstance();
    StringBuffer sb = new StringBuffer();
    sb.append(cale.get(Calendar.YEAR));
    Integer month = cale.get(Calendar.MONTH) + 1;
    sb.append(month > 9 ? month : "0" + month);
    return Integer.parseInt(sb.toString());
  }

  public static Integer getCurrentMonth(Integer leastDay) {
    Calendar cale = convertStringMonth2Calendar(leastDay.toString());
    StringBuffer sb = new StringBuffer();
    sb.append(cale.get(Calendar.YEAR));
    Integer month = cale.get(Calendar.MONTH) + 1;
    sb.append(month > 9 ? month : "0" + month);
    return Integer.parseInt(sb.toString());
  }

  public static Integer getToday() {
    Calendar cale = Calendar.getInstance();
    StringBuffer sb = new StringBuffer();
    sb.append(cale.get(Calendar.YEAR));
    Integer month = cale.get(Calendar.MONTH) + 1;
    sb.append(month > 9 ? month : "0" + month);
    Integer day = cale.get(Calendar.DATE);
    sb.append(day > 9 ? day : "0" + day);
    return Integer.parseInt(sb.toString());
  }

  public static String getEndThisWeek(String dateStr) {
    Calendar cale = convertStringDate2Calendar(dateStr);
    int dayofweek = cale.get(Calendar.DAY_OF_WEEK) - cale.getFirstDayOfWeek();
    cale.add(Calendar.DATE, 7 - dayofweek);
    Date date = cale.getTime();
    String result = new SimpleDateFormat("yyyy-MM-dd").format(date);
    return result;
  }

  public static String getStartThisMonth(String dateStr) {
    Calendar cale = convertStringDate2Calendar(dateStr);
    int dayofmonth = cale.get(Calendar.DATE);
    cale.add(Calendar.DATE, 1 - dayofmonth);
    Date date = cale.getTime();
    String result = new SimpleDateFormat("yyyy-MM-dd").format(date);
    return result;
  }

  public static String getEndThisMonth(String dateStr) {
    Calendar cale = convertStringDate2Calendar(dateStr);
    cale.add(Calendar.MONTH, 1);
    int dayofmonth = cale.get(Calendar.DATE);
    cale.add(Calendar.DATE, -dayofmonth);
    Date date = cale.getTime();
    String result = new SimpleDateFormat("yyyy-MM-dd").format(date);
    return result;
  }

  public static String getStartThisYear(String dateStr) {
    Calendar cale = convertStringDate2Calendar(dateStr);
    cale.set(cale.get(Calendar.YEAR), 0, 1);
    Date date = cale.getTime();
    String result = new SimpleDateFormat("yyyy-MM-dd").format(date);
    return result;
  }

  public static String getEndThisYear(String dateStr) {
    Calendar cale = convertStringDate2Calendar(dateStr);
    cale.set(cale.get(Calendar.YEAR), 11, 31);
    Date date = cale.getTime();
    String result = new SimpleDateFormat("yyyy-MM-dd").format(date);
    return result;
  }

  public static Integer convertStr2Int(String dateStr) {
    String str = replaceDateString(dateStr);
    return Integer.parseInt(str);
  }

  public static String formatMonth(String str) {
    Calendar time = convertStringMonth2Calendar(str);
    Date date = time.getTime();
    String result = new SimpleDateFormat("MMM ,yyyy").format(date);
    return result;
  }

  public static String formatNumMonth(String str) {
    Calendar time = convertStringMonth2Calendar(str);
    Date date = time.getTime();
    String result = new SimpleDateFormat("yyyy-MM").format(date);
    return result;
  }

  public static String formatMonth1(String str) {
    Calendar time = convertStringMonth2Calendar(str);
    Date date = time.getTime();
    String result = new SimpleDateFormat("yyyyMM").format(date);
    return result;
  }

  public static String formatDate(String str) {
    Calendar time = convertStringDate2Calendar(str);
    Date date = time.getTime();
    String result = new SimpleDateFormat("MMM dd,yyyy z").format(date);
    return result;
  }

  public static String formatDate(Date dDate, String sFormat) {
    SimpleDateFormat formatter = new SimpleDateFormat(sFormat);
    String dateString = formatter.format(dDate);
    return dateString;
  }

  public static String formatNumDate(String str) {
    Calendar time = convertStringDate2Calendar(str);
    Date date = time.getTime();
    String result = new SimpleDateFormat("yyyy-MM-dd").format(date);
    return result;
  }

  public static Calendar convertStringMonth2Calendar(String dateStr) {
    if (dateStr.contains("-"))
      dateStr = replaceDateString(dateStr);
    String year = dateStr.substring(0, 4);
    String month = dateStr.substring(4, 6);
    String day = "01";
    Calendar time = Calendar.getInstance();
    time.clear();
    time.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
    return time;
  }

  public static Calendar convertStringDate2Calendar(String dateStr) {
    if (dateStr.contains("-"))
      dateStr = replaceDateString(dateStr);
    String year = dateStr.substring(0, 4);
    String month = dateStr.substring(4, 6);
    String day = dateStr.substring(6);
    Calendar time = Calendar.getInstance();
    time.clear();
    time.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
    return time;
  }

  public static List<Integer> getDayDuration(String sDate, String eDate) {
    if (!CommonUtil.isValid(eDate))
      eDate = sDate;
    Calendar sC = convertStringDate2Calendar(sDate);
    Calendar eC = convertStringDate2Calendar(eDate);
    List<Integer> days = new ArrayList<Integer>();
    Integer dayNum = getDaysBetween(sC, eC);
    for (int i = 0; i <= dayNum; i++) {
      String dayStr = getDayBefore(eDate, i);
      Integer day = convertStr2Int(dayStr);
      days.add(day);
    }
    Collections.sort(days, new Comparator<Integer>() {

      @Override
      public int compare(Integer o1, Integer o2) {
        return o1.compareTo(o2);
      }
    });
    return days;
  }

  public static List<String> getDayDurationStr(String sDate, String eDate, String leastDate) {
    List<String> list = getDayDurationStr(sDate, eDate);
    Integer least = Integer.parseInt(leastDate);
    List<String> removeList = new ArrayList<String>();
    for (String dateStr : list) {
      Integer date = Integer.parseInt(dateStr);
      if (date > least)
        removeList.add(dateStr);
    }
    list.removeAll(removeList);
    return list;
  }

  public static List<String> getDayDurationStr(String sDate, String eDate) {
    if (!CommonUtil.isValid(eDate))
      eDate = sDate;
    Calendar sC = convertStringDate2Calendar(sDate);
    Calendar eC = convertStringDate2Calendar(eDate);
    List<String> days = new ArrayList<String>();
    Integer dayNum = getDaysBetween(sC, eC);
    for (int i = 0; i <= dayNum; i++) {
      String dayStr = getDayBefore(eDate, i);
      days.add(dayStr);
    }
    Collections.sort(days, new Comparator<String>() {

      @Override
      public int compare(String o1, String o2) {
        Integer n1 = Integer.parseInt(o1);
        Integer n2 = Integer.parseInt(o2);
        return n1.compareTo(n2);
      }
    });
    return days;
  }

  public static String getYestoday(String dateStr) {
    return getDayBefore(dateStr, 1);
  }

  public static String getOneWeekAgo(String dateStr) {
    return getDayBefore(dateStr, 7);
  }

  public static String getOneMonthAgo(String dateStr) {
    return getMonthAgo(dateStr, 1);
  }

  public static String getYearAgo(String dateStr, Integer years) {
    Calendar time = convertStringDate2Calendar(dateStr);
    time.add(Calendar.YEAR, -years);
    Integer year = time.get(Calendar.YEAR);
    Integer month = time.get(Calendar.MONTH) + 1;
    Integer day = time.get(Calendar.DATE);
    return MessageFormat.format("{0}{1}{2}", year.toString(), (month < 10 ? "0" + month.toString()
        : month.toString()), (day < 10 ? "0" + day.toString() : day.toString()));
  }

  public static String getMonthAgo(String dateStr, Integer months) {
    Calendar time = convertStringDate2Calendar(dateStr);
    time.add(Calendar.MONTH, -months);
    Integer year = time.get(Calendar.YEAR);
    Integer month = time.get(Calendar.MONTH) + 1;
    Integer day = time.get(Calendar.DATE);
    return MessageFormat.format("{0}{1}{2}", year.toString(), (month < 10 ? "0" + month.toString()
        : month.toString()), (day < 10 ? "0" + day.toString() : day.toString()));
  }

  public static String getDayBefore(String dateStr, Integer days) {
    Calendar time = convertStringDate2Calendar(dateStr);
    time.add(Calendar.DATE, -days);
    Integer year = time.get(Calendar.YEAR);
    Integer month = time.get(Calendar.MONTH) + 1;
    Integer day = time.get(Calendar.DATE);
    return MessageFormat.format("{0}{1}{2}", year.toString(), (month < 10 ? "0" + month.toString()
        : month.toString()), (day < 10 ? "0" + day.toString() : day.toString()));
  }

  public static Integer computeDay(String sDate, String eDate) {
    Calendar sTime = convertStringDate2Calendar(sDate);
    Calendar eTime = convertStringDate2Calendar(eDate);
    if (sTime.equals(eTime))
      return 1;
    return Math.abs(new Long((eTime.getTimeInMillis() - sTime.getTimeInMillis())
        / (1000 * 60 * 60 * 24)).intValue());
  }

  public static List<Integer> getDayList(String sDate, String eDate) {
    Integer days = getDaysBetween(sDate, eDate);
    List<Integer> list = new ArrayList<Integer>();
    for (int index = 0; index <= days; index++) {
      Calendar beginCalendar = convertStringDate2Calendar(sDate);
      beginCalendar.add(Calendar.DATE, index);
      String dateStr = formater.format(beginCalendar.getTime());
      Integer zdate = Integer.parseInt(dateStr);
      list.add(zdate);
    }
    return list;
  }

  public static Integer getDaysBetween(String sDate, String eDate) {
    Calendar beginCalendar = convertStringDate2Calendar(sDate);
    Calendar endCalendar = convertStringDate2Calendar(eDate);
    return getDaysBetween(beginCalendar, endCalendar);
  }

  public static Integer computeWeek(String sDate, String eDate) {
    if (convertStr2Int(sDate) > convertStr2Int(eDate)) {
      String temp = eDate;
      eDate = sDate;
      sDate = temp;
    }
    Calendar beginCalendar = convertStringDate2Calendar(sDate);
    Calendar endCalendar = convertStringDate2Calendar(eDate);
    if (beginCalendar.equals(endCalendar))
      return 1;
    int weeks = 0;
    while (beginCalendar.before(endCalendar)) {

      // 如果开始日期和结束日期在同年、同月且当前月的同一周时结束循环
      if (beginCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
          && beginCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)
          && beginCalendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) == endCalendar
              .get(Calendar.DAY_OF_WEEK_IN_MONTH)) {
        break;

      } else {

        beginCalendar.add(Calendar.DAY_OF_YEAR, 7);
        weeks += 1;
      }
    }

    return weeks > 0 ? weeks : 1;
  }

  public static Integer computeMonth(String sDate, String eDate) {
    if (convertStr2Int(sDate) > convertStr2Int(eDate)) {
      String temp = eDate;
      eDate = sDate;
      sDate = temp;
    }
    Calendar beginCalendar = convertStringDate2Calendar(sDate);
    Calendar endCalendar = convertStringDate2Calendar(eDate);
    if (beginCalendar.equals(endCalendar))
      return 1;
    int months = 0;
    while (beginCalendar.before(endCalendar)) {

      // 如果开始日期和结束日期在同年、同月且当前月的同一周时结束循环
      if (beginCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)
          && beginCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)) {
        break;

      } else {

        beginCalendar.add(Calendar.MONTH, 1);
        months += 1;
      }
    }

    return months > 0 ? months : 1;
  }

  public static int getDaysBetween(Calendar d1, Calendar d2) {
    if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
      Calendar swap = d1;
      d1 = d2;
      d2 = swap;
    }
    int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
    int y2 = d2.get(Calendar.YEAR);
    if (d1.get(Calendar.YEAR) != y2) {
      d1 = (Calendar) d1.clone();
      do {
        days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
        d1.add(Calendar.YEAR, 1);
      } while (d1.get(Calendar.YEAR) != y2);
    }
    return days;
  }

  /**
   * 组合一个日期查询字符串.
   * 
   * @return <br>
   * <br>
   *         author zl<BR>
   *         date 2013-4-27<br>
   *         remark <br>
   */
  public static List<Integer> getDayStrByBean(String sDate, String eDate) {
    List<Integer> dateList = new ArrayList<Integer>();
    String date = eDate;
    dateList.add(convertStr2Int(eDate));
    do {
      date = DateUtil.getDayBefore(date, 1);
      dateList.add(convertStr2Int(date));
    } while (convertStr2Int(date) > convertStr2Int(sDate));

    Collections.sort(dateList, new Comparator<Integer>() {

      @Override
      public int compare(Integer o1, Integer o2) {
        return o1.compareTo(o2);
      }
    });
    return dateList;
  }

  public static Integer getWeekOfDate(String formatDate) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    Date date = null;
    try {
      date = dateFormat.parse(formatDate);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Calendar cd = Calendar.getInstance();
    cd.setTime(date);
    int week = cd.get(Calendar.DAY_OF_WEEK);
    return week - 1;
  }

  // 得到从指定日期到到当前日期的月集合 List<yyyy-MM>
  public static List<String> getMonthList(String startDate) {
    // startDate format is yyyyMMdd
    List<String> monthList = new ArrayList<String>();

    SimpleDateFormat sdfId = new SimpleDateFormat("yyyyMM");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Calendar start = Calendar.getInstance();

    try {
      start.setTime(sdf.parse(startDate));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    Calendar end = Calendar.getInstance();
    while (!end.before(start)) {
      monthList.add(sdfId.format(end.getTime()));
      end.add(Calendar.MONTH, -1);
    }

    return monthList;
  }

  public static String getDateTime(String format) {
    return new SimpleDateFormat(format).format(new Date());
  }

  public static String getDateTime() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
  }

  public static void main(String[] args) {
    // System.out.println(getDayStrByBean("20130326","20130427"));
    /*
     * String today = "20100310"; System.out.println(convertToMySqlDate(today));
     * System.out.println(getLastMonth());
     * System.out.println(getStartThisWeek(today));
     * System.out.println(getEndThisWeek(today));
     * System.out.println(getStartThisMonth(today));
     * System.out.println(getEndThisMonth(today));
     * System.out.println(getStartThisYear(today));
     * System.out.println(getEndThisYear(today));
     * System.out.println(getYearAgo(today, 1));
     * System.out.println(getYearAgo(today, -1));
     * System.out.println(DateUtil.getStartThisYear(getYearAgo(today, 1)));
     * System.out.println(DateUtil.getEndThisYear(getYearAgo(today, 1)));
     * System.out.println(DateUtil.getStartThisYear(getYearAgo(today, -1)));
     * System.out.println(DateUtil.getEndThisYear(getYearAgo(today, -1)));
     * System.out.println(formatNumDate("20100112")); String sDate = "20101122";
     * String eDate = "20101218"; System.out.println(getDayDuration(sDate,
     * eDate)); System.out.println(getDayList(sDate, eDate)); int
     * currentMonth=201202; Integer firstMonth=getMonthBefore(currentMonth,1);
     * Integer secondMonth=getMonthBefore(currentMonth,2); Integer
     * thirdMonth=getMonthBefore(currentMonth,3);
     * System.out.println(firstMonth); System.out.println(secondMonth);
     * System.out.println(thirdMonth); double a=0/0;
     */
  }

}
