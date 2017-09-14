package com.sizzler.common.sizzler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sizzler.common.utils.StringUtil;

/**
 * 默认使用mysql
 */
public class DataBaseConfig {

  // dbCode 数据库code （同dsCode）
  public static final String DB_CODE_MYSQL = "mysql";
  public static final String DB_CODE_MYSQLAMAZONRDS = "mysqlAmazonRds";
  public static final String DB_CODE_POSTGRE = "postgre";
  public static final String DB_CODE_REDSHIFT = "redshift";
  public static final String DB_CODE_STANDARDREDSHIFT = "standardRedshift";
  public static final String DB_CODE_AURORAAMAZONRDS = "auroraAmazonRds"; // 兼容mysql
  public static final String DB_CODE_BIGQUERY = "bigquery";// 非关系型数据库，但类似
  public static final String DB_CODE_SQLSERVER = "sqlserver";

  public static final String DB_OPERATOR_EQ = "=";
  public static final String DB_OPERATOR_NE = "<>";
  public static final String DB_OPERATOR_GT = ">";
  public static final String DB_OPERATOR_GE = ">=";
  public static final String DB_OPERATOR_LT = "<";
  public static final String DB_OPERATOR_LE = "<=";


  public static final String FUNC_SUM = "SUM";
  public static final String FUNC_MAX = "MAX";
  public static final String FUNC_MIN = "MIN";
  public static final String FUNC_AVERAGE = "AVERAGE";
  public static final String FUNC_COUNTA = "COUNTA";
  public static final String FUNC_COUNTUNIQUE = "COUNTUNIQUE";
  public static final String FUNC_STDEV = "STDEV"; // 标准差
  public static final String FUNC_VARIANCE = "VAR"; // 方差
  // public static final String FUNC_MEDIAN = "MEDIAN"; // 中位数 (不支持)

  // 支持函数列表
  public static final String[] suportFuncArray = new String[] {FUNC_SUM, FUNC_MAX, FUNC_MIN,
      FUNC_AVERAGE, FUNC_COUNTA, FUNC_COUNTUNIQUE, FUNC_STDEV, FUNC_VARIANCE};

  // 聚合函数列表（聚合函数不能嵌套使用）
  public static final String[] groupFuncArray = new String[] {FUNC_SUM, FUNC_MAX, FUNC_MIN,
      FUNC_AVERAGE, FUNC_COUNTA, FUNC_COUNTUNIQUE, FUNC_STDEV, FUNC_VARIANCE};

  /**
   * 关系型数据库列表
   */
  private static List<String> dbList = new ArrayList<String>();
  static {
    dbList.add(DB_CODE_MYSQL);
    dbList.add(DB_CODE_MYSQLAMAZONRDS);
    dbList.add(DB_CODE_POSTGRE);
    dbList.add(DB_CODE_REDSHIFT);
    dbList.add(DB_CODE_STANDARDREDSHIFT);
    dbList.add(DB_CODE_AURORAAMAZONRDS);
    dbList.add(DB_CODE_SQLSERVER);
  }

  /**
   * 是否为数据库类型数据源（Bigquery不是数据库）
   * @param dbCode
   * @return
   * @date: 2016年8月30日
   * @author peng.xu
   */
  public static boolean isDatabase(String dbCode) {
    return dbList.contains(dbCode);
  }

  // ////////////////////////////////////////////////////////////////////////////////////

  /**
   * 关系型数据库, 库名、表名、字段名的分界符
   */
  private static Map<String, String> databaseEncloseConfig = new HashMap<>();
  static {
    databaseEncloseConfig.put(DB_CODE_MYSQL, "`");
    databaseEncloseConfig.put(DB_CODE_MYSQLAMAZONRDS, "`");
    databaseEncloseConfig.put(DB_CODE_POSTGRE, "\"");
    databaseEncloseConfig.put(DB_CODE_REDSHIFT, "\"");
    databaseEncloseConfig.put(DB_CODE_STANDARDREDSHIFT, "\"");
    databaseEncloseConfig.put(DB_CODE_AURORAAMAZONRDS, "`");
    databaseEncloseConfig.put(DB_CODE_SQLSERVER, "\"");
  }

  public static String getDatabaseEnclose(String databaseCode) {
    String enclose = databaseEncloseConfig.get(databaseCode);
    if (enclose == null) {
      enclose = databaseEncloseConfig.get(DB_CODE_MYSQL);
    }
    return enclose;
  }

  public static String encloseColumn(String dbCode, String column) {
    String fixColumn = column;
    if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      fixColumn = "[" + column + "]";
    } else {
      String enclose = getDatabaseEnclose(dbCode);
      fixColumn = enclose + column + enclose;
    }
    return fixColumn;

  }

  // ////////////////////////////////////////////////////////////////////////////////////

  /**
   * 设置字段的计算类型
   * @param dsCode
   * @param column
   * @param calculateType
   * @param isNeedConvertData 是否需要严格模式下执行，需要数据格式转换处理保证数据类型正确
   * @return
   * @date: 2016年7月28日
   * @author peng.xu
   */
  public static String buildCalculateColumn(String dbCode, String column, String calculateType,
      boolean isNeedConvertData) {
    String calculateColumn = column;

    if (DataBaseConfig.FUNC_SUM.equalsIgnoreCase(calculateType)) {
      calculateColumn = DataBaseConfig.sum(dbCode, column, isNeedConvertData);
    } else if (DataBaseConfig.FUNC_MAX.equalsIgnoreCase(calculateType)) {
      calculateColumn = DataBaseConfig.max(dbCode, column, isNeedConvertData);
    } else if (DataBaseConfig.FUNC_MIN.equalsIgnoreCase(calculateType)) {
      calculateColumn = DataBaseConfig.min(dbCode, column, isNeedConvertData);
    } else if (DataBaseConfig.FUNC_AVERAGE.equalsIgnoreCase(calculateType)) {
      calculateColumn = DataBaseConfig.avg(dbCode, column, isNeedConvertData);
    } else if (DataBaseConfig.FUNC_COUNTA.equalsIgnoreCase(calculateType)) {
      calculateColumn = DataBaseConfig.count(dbCode, column, isNeedConvertData);
    } else if (DataBaseConfig.FUNC_COUNTUNIQUE.equalsIgnoreCase(calculateType)) {
      calculateColumn = DataBaseConfig.countUnique(dbCode, column, isNeedConvertData);
    } else if (DataBaseConfig.FUNC_STDEV.equalsIgnoreCase(calculateType)) {
      calculateColumn = DataBaseConfig.stdevSamp(dbCode, column, isNeedConvertData);
    } else if (DataBaseConfig.FUNC_VARIANCE.equalsIgnoreCase(calculateType)) {
      calculateColumn = DataBaseConfig.varianceSamp(dbCode, column, isNeedConvertData);
    } else {
      // 默认sum
      calculateColumn = DataBaseConfig.sum(dbCode, column, isNeedConvertData);
    }

    return calculateColumn;
  }

  /**
   * sql函数转换
   */
  public static String count(String dbCode, String column, Boolean isNeedConvert) {
    String fixColumn = column;
    if (isNeedConvert) {
      fixColumn = fixBlankStr(dbCode, column);
    }
    return " count(" + fixColumn + ") ";
  }

  public static String countUnique(String dbCode, String column, Boolean isNeedConvert) {
    String fixColumn = column;
    if (isNeedConvert) {
      fixColumn = fixBlankStr(dbCode, column);
    }
    return " count( distinct " + fixColumn + ") ";
  }

  public static String max(String dbCode, String column, Boolean isNeedConvert) {
    String fixColumn = column;
    //andy 2017-09-14 修正max按照字符码的顺序进行排序
    isNeedConvert = true;
    if (isNeedConvert) {
      fixColumn = DataBaseConfig.toNumber(dbCode, column);
    }
    return " max(" + fixColumn + ") ";
  }

  public static String min(String dbCode, String column, Boolean isNeedConvert) {
    String fixColumn = column;
    //andy 2017-09-14 修正min按照字符码的顺序进行排序
    isNeedConvert = true;
    if (isNeedConvert) {
      fixColumn = DataBaseConfig.toNumber(dbCode, column);
    }
    return " min(" + fixColumn + ") ";
  }

  public static String sum(String dbCode, String column, Boolean isNeedConvert) {
    String fixColumn = column;
    if (isNeedConvert) {
      fixColumn = DataBaseConfig.toNumber(dbCode, column);
    }
    return " sum(" + fixColumn + ") ";
  }

  public static String avg(String dbCode, String column, Boolean isNeedConvert) {
    String fixColumn = column;
    if (isNeedConvert) {
      fixColumn = DataBaseConfig.toNumber(dbCode, column);
    }
    return " avg(" + fixColumn + ") ";
  }

  /**
   * 样本标准差
   */
  public static String stdevSamp(String dbCode, String column, Boolean isNeedConvert) {
    String fixColumn = column;
    if (isNeedConvert) {
      fixColumn = DataBaseConfig.toNumber(dbCode, column);
    }

    if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      return " stdev( " + fixColumn + " ) ";
    } else {
      return " stddev_samp(" + fixColumn + ") ";
    }
  }

  /**
   * 样本方差
   */
  public static String varianceSamp(String dbCode, String column, Boolean isNeedConvert) {
    String fixColumn = column;
    if (isNeedConvert) {
      fixColumn = DataBaseConfig.toNumber(dbCode, column);
    }

    if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      return " var(" + fixColumn + ") ";
    } else {
      return " var_samp(" + fixColumn + ") ";
    }
  }

  /**
   * 总体标准差, TODO: sqlserver未实现
   */
  public static String stdevPop(String dbCode, String column, Boolean isNeedConvert) {
    String fixColumn = column;
    if (isNeedConvert) {
      fixColumn = DataBaseConfig.toNumber(dbCode, column);
    }
    return " stddev_pop(" + fixColumn + ") ";
  }

  /**
   * 总体方差, TODO: sqlserver未实现
   */
  public static String variancePop(String dbCode, String column, Boolean isNeedConvert) {
    String fixColumn = column;
    if (isNeedConvert) {
      fixColumn = DataBaseConfig.toNumber(dbCode, column);
    }
    return " var_pop(" + fixColumn + ") ";
  }

  public static String replace(String dbCode, String column, String fromStr, String toStr) {
    String result = "";
    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      result = " replace(" + column + ", '" + fromStr + "', '" + toStr + "') ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      result =
          " replace(" + DataBaseConfig.toStr(dbCode, column) + ", '" + fromStr + "', '" + toStr
              + "') ";
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result =
          " replace(" + DataBaseConfig.toStr(dbCode, column) + ", '" + fromStr + "', '" + toStr
              + "') ";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      result =
          " replace(" + DataBaseConfig.toStr(dbCode, column) + ", '" + fromStr + "', '" + toStr
              + "') ";
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = column;
    } else {
      // 默认返回mysql的
      result = " replace(" + column + ", '" + fromStr + "', '" + toStr + "') ";
    }
    return result;
  }

  /**
   * 非数值转为null
   */
  public static String fixNumber(String dbCode, String column) {
    String result = "";
    String numberRegexp = "^((-?[0-9]+)(\\.[0-9]+)?)$";
    String isNumberStr = getRegexpStr(dbCode, column, numberRegexp);
    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      result = " if(" + isNumberStr + " , " + column + " , null) ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)
        || DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result = " (case when (" + isNumberStr + " ) then " + column + " else null end) ";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      result = " if(" + isNumberStr + " , " + column + " , null) ";
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = column; // TODO:
    } else {
      // 默认返回mysql的
      result = " if(" + isNumberStr + " , " + column + " , null) ";
    }

    return result;
  }

  /**
   * 空串转为null
   */
  public static String fixBlankStr(String dbCode, String column) {
    String result = "";
    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      result = " if( length(" + column + ")>0, " + column + ", null) ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)
        || DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result =
          " (case when ( length (" + toStr(dbCode, column) + ") > 0 ) then " + column
              + " else null end) ";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      result = " if( length(" + toStr(dbCode, column) + ")>0, " + column + ", null) ";
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = column; // TODO:
    } else {
      // 默认返回mysql的
      result = " if( length(" + toStr(dbCode, column) + ")>0, " + column + ", null) ";
    }

    return result;
  }

  public static String toNumber(String dbCode, String column) {

    //andy 2017-07-03 mysql直接返回
    if(DB_CODE_MYSQL.equalsIgnoreCase(dbCode))
    {
      return column;
    }
    String result = "";

    String fixColumn = fixNumber(dbCode, column);
    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      // result = " ( " + fixColumn + " + 0 ) "; // 加法实现字符串转数值类型,但是不支持科学计数法
      result = " cast(" + fixColumn + " as decimal(30,6) ) ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      result = " cast(" + fixColumn + " as numeric) ";
      // result = " numeric(" + fixColumn + ") ";
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      // result = " cast(" + fixColumn + " as numeric) ";
      result = " cast(" + fixColumn + " as float) ";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      // result = " cast(" + fixColumn + " as INTEGER) ";
      result = " float(" + fixColumn + ") ";
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = " cast(" + fixColumn + " AS FLOAT) ";
    } else {
      // 默认返回mysql的
      result = " cast(" + fixColumn + " as decimal(30,6) ) ";
    }

    return result;
  }

  public static String strToDate(String dbCode, String column, String dateFormat) {
    String result = "";

    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      result = " str_to_date(" + column + " , '" + dateFormat + "') ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      /**
       * 
       * 修复Postgre时间显示错误的问题
       * @author shaoqiang.guo
       */
      result =
          " to_timestamp(" + DataBaseConfig.toStr(dbCode, column) + " , '" + dateFormat + "') ";
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result = " to_date(" + DataBaseConfig.toStr(dbCode, column) + " , '" + dateFormat + "') ";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      result = " STRFTIME_UTC_USEC(cast(" + column + " as TIMESTAMP), '" + dateFormat + "' )";
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      if (StringUtil.isNotBlank(dateFormat)) {
        result = " CONVERT(varchar(23), CAST(" + column + " AS datetime)," + dateFormat + ") ";
      } else {
        result = column;
      }

    } else {
      // 默认返回mysql的
      result = " str_to_date(" + column + " , '" + dateFormat + "') ";
    }

    return result;
  }

  /**
   * 将字符串时间转成指定时区的时间
   * @author shaoqiang.guo
   * @date 2017年3月17日 下午3:27:14
   * @param dbCode
   * @param column
   * @param dateFormat
   * @param timeZone
   * @return
   */
  public static String strToDateByTimeZone(String dbCode, String column, String dateFormat,
      String timeZone) {
    String result = "";

    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {
      if (StringUtils.isNotBlank(timeZone)) {
        result += " convert_tz(";
      }
      result += " str_to_date(" + column + " , '" + dateFormat + "') ";
      if (StringUtils.isNotBlank(timeZone)) {
        result += " , @@session.time_zone , '" + timeZone + "')";
      }

    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      /**
       * 
       * 修复Postgre时间显示错误的问题
       * @author shaoqiang.guo
       */
      result = strToDate(dbCode, column, dateFormat);
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result = strToDate(dbCode, column, dateFormat);
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      result = strToDate(dbCode, column, dateFormat);
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = strToDate(dbCode, column, dateFormat);
    } else {
      // 默认返回mysql的
      if (StringUtils.isNotBlank(timeZone)) {
        result += " convert_tz(";
      }
      result += " str_to_date(" + column + " , '" + dateFormat + "') ";
      if (StringUtils.isNotBlank(timeZone)) {
        result += " , @@session.time_zone , '" + timeZone + "')";
      }
    }

    return result;
  }

  /**
   * 为SQLServer的过滤器的时间戳做单独处理
   * @param dateFormat
   * @param column
   * @return
   */
  public static String sqlserverTimestampToDate(String dateFormat, String column, String timezone) {
    String result = "";// 2017-04-13 20:52:11.0000000 +-06:00
    if (StringUtil.isNotBlank(dateFormat)) {
      if (StringUtil.isNotBlank(timezone)) {
        result += " switchoffset(";
      }
      result +=
          " CONVERT(varchar(23), DATEADD(S, CAST(CASE WHEN len("+column+") > 10 THEN left("+column+",10) ELSE "+column+" END as bigint), '1970-01-01 00:00:00'), " + dateFormat + ") ";
      if (StringUtil.isNotBlank(timezone)) {
        result += ",'" + timezone + "')";
      }
    } else {
      if (StringUtil.isNotBlank(timezone)) {
        result += "switchoffset(";
      }
      result = column;
      if (StringUtil.isNotBlank(timezone)) {
        result += ",'" + timezone + "')";
      }
    }
    return result;
  }

  /**
   * 为SQLServer的维度的时间戳做单独处理
   * @param dateFormat
   * @param column
   * @return
   */
  private static String formatSqlserverTimestamp(String dateFormat, String column) {

    StringBuilder columnBuilder = new StringBuilder("");
    columnBuilder.append(" CONVERT(VARCHAR(23), DATEADD(S, cast(").append(column)
        .append(" as bigint), '1970-01-01 00:00:00'), 20)  ");
    return dateFormat.replace("column", columnBuilder);

  }

  /**
   * 
   * 时间戳转date类型 update by you.zou 2016.2.24
   * @param dbCode 数据库类型
   * @param column 列名
   * @param dateFormat 日期格式
   * @param tz 时区，tz==null时不进行时区转换
   * @return
   */
  public static String timestampToDate(String dbCode, String column, String dateFormat, String tz) {
    String result = "";

    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {
      // if(StringUtils.isNotBlank(tz)){
      // result += " convert_tz(";
      // }
      result +=
          " from_unixtime( if(" + column + " >9999999999, " + column + "/1000, " + column + "), '"
              + dateFormat + "') ";
      // if(StringUtils.isNotBlank(tz)){
      // result += " , @@session.time_zone , '"+tz+"')";
      // }
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      result =
          " to_timestamp( case when (" + toNumber(dbCode, column) + " >9999999999) then "
              + toNumber(dbCode, column) + "/1000 else " + toNumber(dbCode, column) + " end) ";
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result =
          " ( case when (" + toNumber(dbCode, column) + " >9999999999) then "
              + " (timestamp with time zone 'epoch' + " + toNumber(dbCode, column)
              + " /1000 * interval '1 second')" + " else "
              + " (timestamp with time zone 'epoch' + " + toNumber(dbCode, column)
              + " * interval '1 second')" + " end) ";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      result =
          " STRFTIME_UTC_USEC( case when (INTEGER(" + column
              + ") >9999999999) then cast(concat(STRING(" + column
              + "), '000') as INTEGER)  else cast(concat(STRING(" + column
              + "), '000', '000') as INTEGER) end, '" + dateFormat + "' )";

    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = formatSqlserverTimestamp(dateFormat, column);
    } else {
      // 默认返回mysql的
      // if(StringUtils.isNotBlank(tz)){
      // result += " convert_tz(";
      // }
      result +=
          " from_unixtime( if(" + column + " >9999999999, " + column + "/1000, " + column + "), '"
              + dateFormat + "') ";
      // if(StringUtils.isNotBlank(tz)){
      // result += " , @@session.time_zone , '"+tz+"')";
      // }
    }

    return result;
  }

  /**
   * 
   * 添加时区转换, 只有select使用该方法
   * @param dbCode 数据库类型
   * @param column 列名
   * @param dateFormat 日期格式
   * @param tz 时区，tz==null时不进行时区转换
   * @return
   * @author shaoqiang.guo
   */
  public static String timestampToDateByTimeZone(String dbCode, String column, String dateFormat,
      String tz) {
    String result = "";

    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {
      if (StringUtils.isNotBlank(tz)) {
        result += " convert_tz(";
      }
      result +=
          " from_unixtime( if(" + column + " >9999999999, " + column + "/1000, " + column + "), '"
              + dateFormat + "') ";
      if (StringUtils.isNotBlank(tz)) {
        result += " , @@session.time_zone , '" + tz + "')";
      }
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      result = timestampToDate(dbCode, column, dateFormat, tz);
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result = timestampToDate(dbCode, column, dateFormat, tz);
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      result = timestampToDate(dbCode, column, dateFormat, tz);
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = sqlserverTimestampToDate(dateFormat, column, tz);
    } else {
      // 默认返回mysql的
      if (StringUtils.isNotBlank(tz)) {
        result += " convert_tz(";
      }
      result +=
          " from_unixtime( if(" + column + " >9999999999, " + column + "/1000, " + column + "), '"
              + dateFormat + "') ";
      if (StringUtils.isNotBlank(tz)) {
        result += " , @@session.time_zone , '" + tz + "')";
      }
    }

    return result;
  }

  public static String toStr(String dbCode, String column) {
    String result = "";

    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      result = " concat(" + column + " , '') ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      result = " cast(" + column + " as text) ";
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result = " cast(" + column + " as text) ";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      // result = " cast(" + column + " as STRING) ";
      result = " string(" + column + ") ";
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = " cast(" + column + " AS VARCHAR) ";
    } else {
      // 默认返回mysql的
      result = " concat(" + column + " , '') ";
    }

    return result;
  }

  /**
   * redshift从字符串类型转换为datetime类型，用于年月日时分秒的时间类型，to_date会忽略时分秒<br>
   * 适用于date、datetime类型<br>
   * 如果是mysql、postgre数据库，则继续使用strToDate函数处理
   * @param dbCode 数据库Code
   * @param column 列名
   * @param returnDateFormat 数据格式，适用于Mysql数据库
   * @return 转换语句
   * @author you.zou by 2016.3.2
   */
  public static String strToDateTime(String dbCode, String column, String returnDateFormat) {
    String result = "";

    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      result = strToDate(dbCode, column, returnDateFormat);
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      result = strToDate(dbCode, column, returnDateFormat);
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      // 如果是年月日中文本则使用strToDate
      if (StringUtils.contains(returnDateFormat, "年")) {
        result = strToDate(dbCode, column, returnDateFormat);
      } else {
        result = " cast(" + column + " as datetime) ";
      }
    } else {
      result = strToDate(dbCode, column, returnDateFormat);
    }

    return result;
  }

  public static String formatDate(String dbCode, String column, String dateFormat) {
    String result = "";

    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      result = " date_format(" + column + " , '" + dateFormat + "') ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      result = " to_char(" + column + " , '" + dateFormat + "') ";
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result = " to_char(" + column + " , '" + dateFormat + "') ";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      result = " STRFTIME_UTC_USEC(" + column + ", '" + dateFormat + "' )";
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = dateFormat.replace("column", column);
    } else {
      // 默认返回mysql的
      result = " date_format(" + column + " , '" + dateFormat + "') ";
    }

    return result;
  }

  /**
   * 时间格式化方法
   * @param dbCode 数据库CODe
   * @param column 列名
   * @param dateFormat 时间格式
   * @author you.zou by 2016.2.25
   * @return 时间格式化的语句
   */
  public static String formatTime(String dbCode, String column, String dateFormat) {
    String result = "";

    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      result = " time_format(" + column + " , '" + dateFormat + "') ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      result = column; // " to_char(" + column + " , '" + dateFormat + "') ";
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result = column; // " to_char(" + column + " , '" + dateFormat + "') ";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      result = " STRFTIME_UTC_USEC(" + column + ", '" + dateFormat + "' )";
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = column;// TODO 未实现
    } else {
      // 默认返回mysql的
      result = " time_format(" + column + " , '" + dateFormat + "') ";
    }

    return result;
  }

  /**
   * 格式化季度语句
   * @param dbCode 数据库code
   * @param column 列名||时间戳格式化
   * @param dateFormat 季度日期格式
   * @author you.zou by 2016.2.23
   * @return 取出的数据格式：2015 Q1
   */
  public static String formatQuarter(String dbCode, String column, String dateFormat) {
    String result = "";
    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      result =
          " CONCAT(date_format(" + column + ",'" + dateFormat + "'), CONCAT(' ', CONCAT(QUARTER( "
              + column + " ), 'Q')))  ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      result =
          " CONCAT(to_char(" + column + " , '" + dateFormat + "'), CONCAT(' ',EXTRACT(QUARTER from"
              + column + ") , 'Q')) ";
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result =
          " concat(to_char(to_date(" + column + ", '" + dateFormat + "'), '" + dateFormat
              + "'),  concat(' ', concat(EXTRACT('qtr' FROM " + column + "), 'Q')) )";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      result =
          " concat(STRFTIME_UTC_USEC(" + column + ", '" + dateFormat + "'), ' ', cast(QUARTER("
              + column + ") as STRING), 'Q')";
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result =
          " (cast(DATEPART(YEAR, " + column + ") AS varchar)+' '+ cast(DATEPART(qq, " + column
              + ") AS varchar) + 'Q') ";
    } else {
      // 默认返回mysql的
      result =
          " CONCAT(date_format(" + column + ",'" + dateFormat + "'), CONCAT(' ', CONCAT(QUARTER( "
              + column + " ), 'Q')))  ";
    }
    return result;
  }

  /**
   * 格式化周语句
   * @param dbCode 数据库code
   * @param column 列名||时间戳格式化
   * @param dateFormat 周日期格式
   * @param addDay 如果用户选择的周开始时间为周日，则需要+1，否则+0
   * @author you.zou by 2016.2.23
   * @return 取出的数据格式：2015 Q1
   */
  public static String formatWeek(String dbCode, String column, String dateFormat, Integer addDay) {
    if (addDay == null) {
      addDay = 0;
    }
    String result = "";
    // 只适用于Mysql
    String mysqlDateFormat = "DATE_FORMAT(" + column + ", '" + dateFormat + "')";
    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {
      result =
          "date_sub(" + mysqlDateFormat + ", INTERVAL if(1-" + addDay + "=0, if((WEEKDAY("
              + mysqlDateFormat + ")+1)=7, 0, WEEKDAY(" + mysqlDateFormat + ")+1), WEEKDAY("
              + mysqlDateFormat + ")) DAY)";
      // 有问题的版本 result =
      // " date_sub(DATE_FORMAT("+column+", '"+dateFormat+"'),  INTERVAL WEEKDAY(DATE_FORMAT("+column+" , '"+dateFormat+"')) + "+addDay+" DAY) ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      if (addDay - 1 == 0) {
        /*
         * PostGre 本身数据库不支持设置dateFirst 所以暂未使用该方法 result = " CASE EXTRACT ( DOW FROM " + column +
         * ") WHEN '0' THEN to_char( " + column + " ,'" + dateFormat +
         * "') else   CASE EXTRACT ( DOW FROM date_trunc('weeks',"+column+")) WHEN '0' THEN"
         * +" to_char(date_trunc('weeks',"+column+"),"+ dateFormat + ")"+
         * "ELSE to_char(date_trunc('weeks',"+column+
         * ") :: TIMESTAMP - INTERVAL '1 day',+cilum+) END END";
         */
        result =
            " CASE EXTRACT ( DOW FROM " + column + ") WHEN '0' THEN to_char( " + column + " ,'"
                + dateFormat + "') else to_char(date_trunc ('weeks', " + column
                + ")::TIMESTAMP - interval '1 day', '" + dateFormat + "') END";
      } else {
        // 周一开始
        result = " to_char(date_trunc ('weeks', " + column + "), '" + dateFormat + "')";
      } // dateFormat + "') ";
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      if (addDay - 1 == 0) {
        // 周日开始
        result =
            " to_char(DATEADD('day', 1, DATEADD('day', cast(CONCAT ('-', to_char(to_date(" + column
                + ", '" + dateFormat + "'), 'D')) as integer), to_date(" + column + ", '"
                + dateFormat + "'))) , '" + dateFormat + "')";
      } else {
        // 周一开始
        result =
            " to_char(date_trunc ('weeks', to_date(" + column + ", '" + dateFormat + "')), '"
                + dateFormat + "')";// " to_char(" + column + " , '" + dateFormat + "') ";
      }
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      Integer startIndex = 0;
      if (addDay - 1 == 0) {
        startIndex = 0;// bigQuery中0代表周日开始
      } else {
        startIndex = 1;// bigQuery中1代表周一开始
      }
      result =
          " STRFTIME_UTC_USEC(UTC_USEC_TO_WEEK(" + column + ", " + startIndex + "), '" + dateFormat
              + "')";
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      Integer startIndex = 0;
      if (addDay - 1 == 0) {
        startIndex = -1;// 周日
      } else {
        startIndex = 0;// 周一
      }
      result = " convert(varchar(23), DATEADD(week,DATEDIFF(week,0, " + column + " ),"+startIndex+") ,   23 ) ";
    } else {
      // 默认返回mysql的
      result =
          "date_sub(" + mysqlDateFormat + ", INTERVAL if(1-" + addDay + "=0, if((WEEKDAY("
              + mysqlDateFormat + ")+1)=7, 0, WEEKDAY(" + mysqlDateFormat + ")+1), WEEKDAY("
              + mysqlDateFormat + ")) DAY)";
    }
    return result;
  }

  public static String parseOperator(String dbCode, String srcOp) {
    String result = srcOp;
    if (DB_OPERATOR_NE.equals(srcOp)) {
      if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
        result = "!=";
      }
    }
    return result;
  }

  public static String getLimitStr(String dbCode, long offset, long count) {
    String result = "";
    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      result = " LIMIT " + offset + " , " + count + " ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      result = " LIMIT " + count + " OFFSET " + offset + " ";
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      result = " LIMIT " + count + " OFFSET " + offset + " ";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      result = " LIMIT " + count;
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = " TOP " + count + " ";
    } else {
      // 默认返回mysql的
      result = " LIMIT " + offset + " , " + count + " ";
    }

    return result;
  }

  public static String getRegexpStr(String dbCode, String column, String sqlRegexp) {
    String result = "";
    if (DB_CODE_MYSQL.equalsIgnoreCase(dbCode) || DB_CODE_MYSQLAMAZONRDS.equalsIgnoreCase(dbCode)) {

      result = column + " REGEXP '" + sqlRegexp + "' ";
    } else if (DB_CODE_POSTGRE.equalsIgnoreCase(dbCode)) {
      // 在postgresql中使用正则表达式时需要使用关键字“~”，以表示该关键字之前的内容需匹配之后的正则表达式，若匹配规则不需要区分大小写，可以使用组合关键字“~*”
      result = DataBaseConfig.toStr(dbCode, column) + " ~ '" + sqlRegexp + "' ";
    } else if (DB_CODE_REDSHIFT.equalsIgnoreCase(dbCode)
        || DB_CODE_STANDARDREDSHIFT.equalsIgnoreCase(dbCode)) {
      // 在redshift中使用正则表达式时需要使用关键字“~”，以表示该关键字之前的内容需匹配之后的正则表达式，若匹配规则不需要区分大小写，可以使用组合关键字“~*”
      result = DataBaseConfig.toStr(dbCode, column) + " ~ '" + sqlRegexp + "' ";
    } else if (DB_CODE_BIGQUERY.equalsIgnoreCase(dbCode)) {
      // 在bigQuery中使用正则表达式时需要使用关键字“r”
      result = "REGEXP_MATCH(" + DataBaseConfig.toStr(dbCode, column) + ", r'" + sqlRegexp + "')";
    } else if (DB_CODE_SQLSERVER.equalsIgnoreCase(dbCode)) {
      result = "";// TODO 未实现
    } else {
      // 默认返回mysql的
      result = column + " REGEXP '" + sqlRegexp + "' ";
    }

    return result;
  }

  /**
   * 为SQLServer的维度的时间戳做单独处理 添加时区的转换
   * @param dateFormat
   * @param column
   * @param timezone
   * @return
   */
  public static String formatSqlserverTimestampByTimezone(String dateFormat, String column,
      String timezone) {

    StringBuilder columnBuilder = new StringBuilder("");
    if (StringUtil.isNotBlank(timezone)) {
      columnBuilder.append(" switchoffset(");
    }               

    columnBuilder.append(" CONVERT(VARCHAR(23), DATEADD(S, CAST(CASE WHEN len("+column+") > 10 THEN left("+column+",10) ELSE "+column+" END as bigint), '1970-01-01 00:00:00'), 20)  ");

    if (StringUtil.isNotBlank(timezone)) {
      columnBuilder.append(",'" + timezone + "')");
    }
    return dateFormat.replace("column", columnBuilder);
  }
}
