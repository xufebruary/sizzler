package com.sizzler.provider.common.util;

import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.query.SelectItem;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.MutableColumn;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by ptmind on 2015/11/9.
 */
public class DataTypeDetermine {

  public static List<Row> determineDataSetColumnType(DataSet dataSet) {
    List<Row> rowList = dataSet.toRows();
    SelectItem[] selectItems = dataSet.getSelectItems();
    for (SelectItem selectItem : selectItems) {
      /*
       * 取得某一列的所有值
       */
      Column column = selectItem.getColumn();
      List<String> columnValueList = new ArrayList<>();

      for (Row row : rowList) {
        Object object = (row != null ? row.getValue(column) : "");
        columnValueList.add(object != null ? object.toString() : "");
      }
      /*
       * 判断某一列的数据类型
       */
      determineColumnType(column, columnValueList);
    }
    return rowList;
  }

  /*
   * 为空的cell的类型设置为了String，之后在判断 类型时 需要把 为空的 cell的去掉
   */
  public static void determineColumnType(Column column, List<String> columnValueList) {
    Map<ColumnType, Integer> columnTypeMap = new HashMap<>();
    Map<String, Integer> columnFormatMap = new HashMap<>();

    int index = 0;
    for (String columnValue : columnValueList) {
      // 空的cell 不参与 字段类型的判断, 如果数据大于1行每一列的第一行（表头）不参与判断
      //修改了判断逻辑，将(index++ == 0 && columnValueList.size() > 1)判断提到了columnValue判断的前面
      //为了解决当两行数据时，第一行数据为空，第二行数据不会进行数据类型校验的问题 modify by you.zou
      if ((index++ == 0 && columnValueList.size() > 1) || 
          columnValue == null || columnValue.trim().equals("")) {
        continue;
      }
      ColumnType columnType = DataTypeDetermine.determineColumnType(columnValue);
      String dataFormat = null;
      // 对于货币的类型，需要进一步的设置 货币的符号，$ ￥ 等
      if (columnType.getName().equalsIgnoreCase("CURRENCY")) {
        dataFormat = CurrencyTypeDetermine.determineCurrencyFormat(columnValue);
      } else if (columnType.getName().equalsIgnoreCase("DATE")) // 对日期类型，进一步设置日期格式
      {
        dataFormat = DateTypeDetermine.determineDateFormat(columnValue);
        if (dataFormat.equals("")) {
          dataFormat = null;
        }
      } else if (columnType.getName().equalsIgnoreCase("DATETIME"))// 对于日期时间类型，进一步设置 日期时间的格式
      {
        String[] dateTimeArray = columnValue.split(" ");
        if (dateTimeArray != null && dateTimeArray.length > 0) {
          String date = dateTimeArray[0];
          String dateFormat = DateTypeDetermine.determineDateFormat(date);
          if (dateFormat.equals("")) {
            dataFormat = null;
          } else {
            dataFormat = dateFormat + " HH:mm:ss";
          }
        }
      }

      // 设置Column的format
      if (dataFormat != null) {
        if (columnFormatMap.containsKey(dataFormat)) {
          int tmpCount = columnFormatMap.get(dataFormat).intValue();
          tmpCount = tmpCount + 1;
          columnFormatMap.put(dataFormat, tmpCount);
        } else {
          columnFormatMap.put(dataFormat, 1);
        }
      }

      // 设置column的type
      if (columnTypeMap.containsKey(columnType)) {
        int tmpCount = columnTypeMap.get(columnType).intValue();
        tmpCount = tmpCount + 1;
        columnTypeMap.put(columnType, tmpCount);
      } else {
        columnTypeMap.put(columnType, 1);
      }

    }

    ColumnType columnType = ColumnType.STRING;
    int maxCount = 0;
    for (ColumnType tmpColumnType : columnTypeMap.keySet()) {
      int count = columnTypeMap.get(tmpColumnType);
      if (count >= maxCount) {
        maxCount = count;
        columnType = tmpColumnType;
      }
    }

    // 判断数据的format
    maxCount = 0;
    String resultDataFormat = "";

    for (String dataFormat : columnFormatMap.keySet()) {
      int count = columnFormatMap.get(dataFormat);
      if (count >= maxCount) {
        maxCount = count;
        resultDataFormat = dataFormat;
      }
    }


    MutableColumn mutableColumn = (MutableColumn) column;
    // 日期类型，但是format没有得到，则需要将类型设置为Text
    if (columnType.getName().equals("DATE")) {
      if (resultDataFormat.equals("")) {
        columnType = ColumnType.STRING;
      }
    }
    mutableColumn.setType(columnType);
    mutableColumn.setFromat(resultDataFormat);


  }

  public static ColumnType determineColumnType(String inputStr) {
    DataType dataType = determineDataType(inputStr);
    switch (dataType) {
      case PERCENT:
        return ColumnType.PERCENT;
      case CURRENCY:
        return ColumnType.CURRENCY;
      case LOCATION_COUNTRY:
        return ColumnType.LOCATION_COUNTRY;
      case LOCATION_REGION:
        return ColumnType.LOCATION_REGION;
      case LOCATION_CITY:
        return ColumnType.LOCATION_CITY;
      case TEXT:
        return ColumnType.STRING;
      case DATE:
        return ColumnType.DATE;
      case TIME:
        return ColumnType.TIME;
      case DATETIME:
        return ColumnType.DATETIME;
        /*
         * case INTEGER: return ColumnType.INTEGER; case FLOAT: case DOUBLE: return
         * ColumnType.DOUBLE; case LONG: return ColumnType.LONG;
         */
      case INTEGER:
      case FLOAT:
      case DOUBLE:
      case LONG:
      case NUMBER:
        return ColumnType.NUMBER;
      case TIMESTAMP:
        return ColumnType.TIMESTAMP;
      default:
        return ColumnType.STRING;
    }
  }

  public static DataType determineDataType(String inputStr) {
    String upperCaseStr = inputStr.toUpperCase().trim();
    inputStr = inputStr.trim();
    DataType resultDataType = DataType.TEXT;
    if (inputStr == null || inputStr.trim().equals("")) {
      return DataType.TEXT;
    }
    // 1、判断是否以%结尾
    if (endWithChar(inputStr, "%")) {
      resultDataType = DataType.PERCENT;
      return resultDataType;
    }
    // 2、判断是否为currency
    else if (CurrencyTypeDetermine.isCurrency(upperCaseStr)) {
      resultDataType = DataType.CURRENCY;
      return resultDataType;
    } else if (NumberTypeDetermine.isNumber(inputStr)) {
      resultDataType = DataType.NUMBER;
      // 判断10位和13位的数字优先设为时间戳类型
      if ((inputStr.length() == 10 || inputStr.length() == 13)
          && NumberTypeDetermine.isLong(inputStr)) {
        Long value = Long.valueOf(inputStr);
        if ((1000000000L <= value && value <= 9999999999L)
            || (1000000000000L <= value && value <= 9999999999999L)) {
          resultDataType = DataType.TIMESTAMP;
        }
      }
      return resultDataType;
    }// 判断是否为日期时间格式
    else if (DateTypeDetermine.isDateTime(upperCaseStr)) {
      resultDataType = DataType.DATETIME;
      return resultDataType;
    } else if (DateTypeDetermine.isDate(upperCaseStr)) {
      resultDataType = DataType.DATE;
      return resultDataType;
    }

    // 3、判断是否为 2015 201501 20150101 数字格式的日期
    /*
     * //日期格式先不进行判断 andy 2015-12-29 else if(isIntegerFormatDate(inputStr)) {
     * resultDataType=DataType.TIME; }
     */
    // 4、判断是否为Integer
    /*
     * else if(NumberTypeDetermine.isInteger(inputStr)) { resultDataType=DataType.INTEGER; }
     * //5、判断是否为double else if(NumberTypeDetermine.isDouble(inputStr)) {
     * resultDataType=DataType.DOUBLE; }
     */


    // 6、判断是否为location
    /*
     * // location 类型也先暂时不进行判断 else if(LocationDetermine.isCountry(inputStr)) {
     * resultDataType=DataType.LOCATION_COUNTRY; }else if(LocationDetermine.isRegion(inputStr)) {
     * resultDataType=DataType.LOCATION_REGION; }else if(LocationDetermine.isCity(inputStr)) {
     * resultDataType=DataType.LOCATION_CITY; }
     */


    return resultDataType;
  }

  public static boolean endWithChar(String inputStr, String endChar) {
    return inputStr.trim().endsWith(endChar);
  }



  public static boolean isIntegerFormatDate(String inputStr) {
    inputStr = inputStr.trim();
    int strLen = inputStr.length();
    Calendar calendar = Calendar.getInstance();
    int currentYear = calendar.get(Calendar.YEAR);

    // ^\s*\d{4,8}\s*$
    Pattern INTEGER_DATE_PATTERN = Pattern.compile("^\\s*\\d{4,8}\\s*$");
    if (INTEGER_DATE_PATTERN.matcher(inputStr).matches()) {

      int inputIntValue = Integer.valueOf(inputStr);
      // strLen=4 1900<=xxxx<=current_year(2015)
      // 1900年1月
      // strLen=5 19001<=xxxxx<current_year12(201512)
      // strLen=6 190001<=xxxxxx<=current_year12(201512)
      // 1900年1月01日
      // strLen=7 1900101<=xxxxxxx<current_year1231(20151231)
      // strLen=8 19000101<=xxxxxxxx<=current_year1231(20151231)
      if (strLen == 4) {
        if (inputIntValue >= 1900 && inputIntValue <= currentYear) {
          return true;
        }
      } else if (strLen == 5) {
        if (inputIntValue >= 19001 && inputIntValue < Integer.valueOf(currentYear + "12")) {
          return true;
        }
      } else if (strLen == 6) {
        if (inputIntValue >= 190001 && inputIntValue <= Integer.valueOf(currentYear + "12")) {
          return true;
        }
      } else if (strLen == 7) {
        if (inputIntValue >= 1900101 && inputIntValue < Integer.valueOf(currentYear + "1231")) {
          return true;
        }
      } else if (strLen == 8) {
        if (inputIntValue >= 19000101 && inputIntValue <= Integer.valueOf(currentYear + "1231")) {
          return true;
        }
      }

      return false;

    } else {
      return false;
    }

  }

  public static boolean containWordSetInString(Set<String> wordSet, String str) {

    for (String word : wordSet) {
      if (str.contains(word)) {
        return true;
      }
    }

    return false;
  }


  public static void main(String[] args) {
    System.out.println(determineColumnType("2016-01-02 "));
    System.out.println(determineColumnType("2016.01.02 "));
    System.out.println(determineDataType("2016-01-02 "));
    System.out.println(determineDataType("2016.01.02 "));
    System.out.println(DateTypeDetermine.determineDateFormat("2016-01-02 "));
    System.out.println(DateTypeDetermine.determineDateFormat("2016.01.02 "));
  }


}
