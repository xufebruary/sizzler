package com.sizzler.provider.common.util.excel;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.metamodel.MetaModelHelper;
import org.apache.metamodel.data.CachingDataSetHeader;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.DataSetHeader;
import org.apache.metamodel.data.DefaultRow;
import org.apache.metamodel.data.InMemoryDataSet;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.MutableColumn;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.schema.TableType;
import org.apache.metamodel.util.FileHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.provider.common.util.SchemaUtil;
import com.sizzler.provider.domain.PtoneSheetFile;

public class ExcelUtil {

  private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

  public static PtoneSheetFile convertExcelToPtoneFile(String fileName, InputStream inputStream,
      boolean maxRowLimit) throws Exception {
    PtoneSheetFile ptoneSheetFile = new PtoneSheetFile();
    ptoneSheetFile.setName(fileName);

    // Map<String,Object> resultMap= createSchema(fileName,inputStream,maxRowLimit);



    Map<String, Object> resultMap = ExcelUtil2.createSchema(fileName, inputStream, maxRowLimit);

    MutableSchema schema = (MutableSchema) resultMap.get("schema");
    ptoneSheetFile.setSchema(schema);
    /*
     * 判断文件是否为空即不包含table
     */
    if (schema.getTables().length == 0) {
      ptoneSheetFile.setEmpty(true);
    } else {
      LinkedHashMap<String, DataSet> dataSetMap =
          (LinkedHashMap<String, DataSet>) resultMap.get("dataSetMap");
      // SchemaUtil.determineColumnType 用于修正schema中每个table的column的类型
      // 由于在 SchemaUtil.determineColumnType 方法中对DataSet进行了循环，所以 再调用 DataSet.toRows()方法时
      // 不会返回数据（toRows中调用next，而next方法返回了false）
      LinkedHashMap<String, List<org.apache.metamodel.data.Row>> fileDataMap =
          SchemaUtil.determineColumnType(schema, dataSetMap, maxRowLimit);
      ptoneSheetFile.setFileDataMap(fileDataMap);
    }


    return ptoneSheetFile;
  }

  public static Map<String, Object> createSchema(String fileName, InputStream inputStream,
      boolean maxRowLimit) throws Exception {


    Map<String, Object> resultMap = new HashMap<>();

    /*
     * 创建 Schema对象
     */
    MutableSchema schema = new MutableSchema(fileName);

    LinkedHashMap<String, DataSet> dataSetMap = new LinkedHashMap<>();

    resultMap.put("schema", schema);
    resultMap.put("dataSetMap", dataSetMap);
    Workbook wb = null;
    FormulaEvaluator evaluator = null;
    try {
      // 通过 WorkbookFactory 来统一的兼容 xls 和 xlsx
      wb = WorkbookFactory.create(inputStream);
      evaluator = wb.getCreationHelper().createFormulaEvaluator();
      // evaluator.evaluateAll();
      int sheetNumber = wb.getNumberOfSheets();

      List<MutableTable> tableList = new ArrayList<>();

      /*
       * 针对每一个Sheet创建一个Table对象，并以第一行做为默认的Column 在创建Table之前，需要首先确定出 每个Sheet的 真正的 row*col (去掉最后的空行和空列)
       */

      for (int s = 0; s < sheetNumber; s++) {
        //
        Sheet sheet = wb.getSheetAt(s);
        String sheetName = sheet.getSheetName();
        boolean sheetHidden = wb.isSheetHidden(s);
        boolean sheetVeryHidden = wb.isSheetVeryHidden(s);
        // 跳过隐藏的sheet
        if (sheetHidden || sheetVeryHidden) {
          continue;
        }

        MutableTable table = null;
        /*
         * String tableName=sheet.getSheetName(); MutableTable table=new MutableTable(tableName);
         * table.setType(TableType.TABLE); table.setSchema(schema);
         */

        List<org.apache.metamodel.data.Row> defaultRowList = new ArrayList<>();
        DataSetHeader dataSetHeader = null;
        DataSet dataSet = null;
        Column[] columnArray = null;

        int firstRowNum = 0;
        int lastRowNum = sheet.getLastRowNum();
        boolean findNotEmptyRow = false;
        long realLastRowNum = lastRowNum;

        // 首先计算出最后一行的num值，为了去除Spreadsheet中的空行
        for (int i = lastRowNum; i >= 0; i--) {
          Row row = sheet.getRow(i);
          if (row != null) {
            // 获取当前行最后单元格列号
            int lastCellNum = row.getLastCellNum();
            for (int j = 0; j < lastCellNum; j++) {
              Cell cell = row.getCell(j);
              if (cell != null && !cell.toString().equals("")) {
                findNotEmptyRow = true;
              }
              cell = null;
            }
          }
          if (findNotEmptyRow) {
            realLastRowNum = i;
            break;
          }
          row = null;
        }

        if (findNotEmptyRow)// 如果sheet内容是空的，则直接跳过
        {

          String tableName = sheet.getSheetName();
          table = new MutableTable(tableName);
          table.setType(TableType.TABLE);
          table.setSchema(schema);
          table.setRowCount(Long.valueOf(realLastRowNum + 1));

          int realLastCellNum = 0;
          // 然后计算出，包含数据的cell的最大值
          for (int i = firstRowNum; i < realLastRowNum + 1; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
              // 获取当前行最后单元格列号
              int lastCellNum = row.getLastCellNum();
              // 从每一行的最后一列向前面找到 第一个不为空的cell
              for (int j = lastCellNum; j >= 0; j--) {
                Cell cell = row.getCell(j);
                if (cell != null && !cell.toString().equals("")) {
                  if (j > realLastCellNum) {
                    realLastCellNum = j;
                  }
                }
                cell = null;
              }
            }
            row = null;
          }

          // andy add 2015-12-22 用于限制返回的最大行和最大列
          //
          if (maxRowLimit) {
            if (realLastRowNum + 1 >= DsConstants.DATA_TYPE_DETERMINE_ROW_LIMIT) {
              realLastRowNum = DsConstants.DATA_TYPE_DETERMINE_ROW_LIMIT - 1;
            }

            if (realLastCellNum + 1 >= DsConstants.EXCEL_EDITOR_COLUMN_LIMIT) {
              realLastCellNum = DsConstants.EXCEL_EDITOR_COLUMN_LIMIT.intValue() - 1;
            }
          }


          /*
           * realLastRowNum * realLastCellNum 的矩阵
           */
          Row firstRow = sheet.getRow(firstRowNum);
          columnArray = new Column[realLastCellNum + 1];

          /*
           * 只设置了Column的名称和index（从0开始），没有设置Column的Type
           */
          for (int k = 0; k < columnArray.length; k++) {
            String columnName = "";
            if (firstRow == null) {

            } else {
              Cell cell = firstRow.getCell(k);

              if (cell != null) {
                columnName = getCellValue(cell, evaluator).toString();
                cell = null;
              }
            }

            MutableColumn column = new MutableColumn(columnName);
            column.setColumnNumber(k);
            column.setType(ColumnType.STRING);
            columnArray[k] = column;

          }
          table.setColumns(columnArray);

          dataSetHeader = new CachingDataSetHeader(MetaModelHelper.createSelectItems(columnArray));
          /*
           * 生成数据(包含 第一行)
           */

          for (int m = 0; m < realLastRowNum + 1; m++) {
            List<Object> rowData = new ArrayList<>();
            Row row = sheet.getRow(m);
            if (row != null) {
              int lastCellNum = realLastCellNum + 1;
              for (int j = 0; j < lastCellNum; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                  rowData.add("");
                } else {
                  rowData.add(getCellValue(cell, evaluator));
                }
                cell = null;

              }

            } else {
              for (int n = 0; n < realLastCellNum + 1; n++) {
                rowData.add("");
              }
            }
            row = null;


            DefaultRow defaultRow = new DefaultRow(dataSetHeader, rowData.toArray());

            defaultRowList.add(defaultRow);
          }
          dataSet = new InMemoryDataSet(dataSetHeader, defaultRowList);
          dataSetMap.put(tableName, dataSet);
          tableList.add(table);
        }

      }
      schema.setTables(tableList);


    } catch (Exception e) {
      throw e;
    } finally {
      FileHelper.safeClose(inputStream, wb);
      if (evaluator != null) {
        evaluator.clearAllCachedResultValues();
        evaluator = null;
      }

    }

    return resultMap;
  }


  private static Object getCellValue(Cell cell, FormulaEvaluator evaluator) {
    Object cellValueObject = "";
    String originalDataFormat = cell.getCellStyle().getDataFormatString();
    // int dataformatIndex=cell.getCellStyle().getDataFormat();
    String dataFormat = PoiBuiltinFormats.getDataFormat(originalDataFormat);
    if (dataFormat == null) {
      dataFormat = originalDataFormat;// cell.getCellStyle().getDataFormatString();
      String fixDataFormat = fixDataFormat(dataFormat);
      log.debug("poi-dataformat:" + originalDataFormat + "-->fix:" + fixDataFormat);
      dataFormat = fixDataFormat;
      PoiBuiltinFormats.addDataFormat(originalDataFormat, dataFormat);
    }
    if (dataFormat == null || dataFormat.equalsIgnoreCase("null")) {
      cellValueObject = cell.toString();
      return cellValueObject;
    }
    // 如果cell中是函数公式，则计算出结果
    int cellType = cell.getCellType();
    // 函数公式的结果也有可能是日期
    if (cellType == Cell.CELL_TYPE_FORMULA) {
      // 首先判断 POI是否支持该函数
      String cellFormula = cell.getCellFormula();
      String cellFormulaName = "";

      if (cellFormula != null) {
        int cellFormulaNameEndIndex = cellFormula.indexOf("(");
        if (cellFormulaNameEndIndex > 0) {
          cellFormulaName = cellFormula.substring(0, cellFormulaNameEndIndex);
        }
      }
      // 如果POI不支持该函数，则直接返回该函数的名称
      if (PoiNotSupportedFunctionSet.isPoiNotSupportedFunction(cellFormulaName)) {
        cellValueObject = cellFormula;
        log.info("find poi not supported function:" + cellFormulaName);
        return cellValueObject;
      }

      CellValue cellValue = null;
      try {
        cellValue = evaluator.evaluate(cell);
        int cellValueType = cellValue.getCellType();
        if (cellValueType == 1)// 字符串
        {
          cellValueObject = cellValue.getStringValue();
        } else if (cellValueType == 0) {
          // 增加对日期的处理
          if (dataFormat.equalsIgnoreCase("General")) {
            cellValueObject = formatValue(cellValue.getNumberValue(), dataFormat);
            return cellValueObject;
          }

          if (DateUtil.isValidExcelDate(cellValue.getNumberValue())) {
            try {
              boolean isCellDateFormatted = false;
              boolean isCellDateFormattedException = false;
              // 对于TODAY()等函数，DateUtil.isCellDateFormatted 会产生异常
              try {
                isCellDateFormatted = DateUtil.isCellDateFormatted(cell);
              } catch (Exception e) {
                // 在判断某个Cell是否为日期格式时 发生了异常
                // e.printStackTrace();
                log.error("execute isCellDateFormatted error:" + cellFormula);
                isCellDateFormattedException = true;
              }

              // 如果在判断某个cell是否为日期格式时，发生了异常
              if (isCellDateFormattedException) {
                if (!dataFormat.contains("#") && !dataFormat.contains("0")) {
                  Date dateCell = getDateFunctionDateCellValue(cellValue);
                  cellValueObject = getDateCellValue(dateCell, dataFormat, originalDataFormat);
                  return cellValueObject;
                } else {
                  cellValueObject = formatValue(cellValue.getNumberValue(), dataFormat);
                }
              } else if (isCellDateFormatted) {
                cellValueObject =
                    getDateCellValue(cell.getDateCellValue(), dataFormat, originalDataFormat);
              } else if (!dataFormat.contains("#") && !dataFormat.contains("0")) {
                cellValueObject =
                    getDateCellValue(cell.getDateCellValue(), dataFormat, originalDataFormat);
              } else {
                cellValueObject = formatValue(cellValue.getNumberValue(), dataFormat);
              }
            } catch (Exception e) {
              e.printStackTrace();
              cellValueObject = cell.toString();
            }

          } else {
            cellValueObject = formatValue(cellValue.getNumberValue(), dataFormat);
          }
        }

        // cellValueObject=formatValue(cellValue.getNumberValue(),dataFormat);
      } catch (Exception e) {
        // e.printStackTrace();
        cellValueObject = cell.toString();
        return cellValueObject;
      }
    } else if (cellType == Cell.CELL_TYPE_NUMERIC) {
      if (dataFormat.equalsIgnoreCase("General")) {
        // cellValueObject=formatValue(cell.getNumericCellValue(),dataFormat);
        cellValueObject = removePointAndZero(formatValue(cell.getNumericCellValue(), dataFormat));
        return cellValueObject;
      }
      // 需要首先判断是否为date格式
      // if(DateUtil.isCellDateFormatted(cell)) //某些格式poi不支持，所以 DateUtil.isCellDateFormatted 可能会报异常
      // if(DateUtil.isValidExcelDate(cell.getNumericCellValue()))
      // if(DateUtil.isCellDateFormatted(cell))
      if (DateUtil.isValidExcelDate(cell.getNumericCellValue())) {
        if (DateUtil.isCellDateFormatted(cell)) {
          cellValueObject =
              getDateCellValue(cell.getDateCellValue(), dataFormat, originalDataFormat);
        } else if (!dataFormat.contains("#") && !dataFormat.contains("0")) {
          cellValueObject =
              getDateCellValue(cell.getDateCellValue(), dataFormat, originalDataFormat);
        } else {
          cellValueObject = formatValue(cell.getNumericCellValue(), dataFormat);
        }
      } else {
        cellValueObject = formatValue(cell.getNumericCellValue(), dataFormat);
      }

    } else {
      cellValueObject = cell.toString();
    }
    return cellValueObject;
  }

  // 取得类似 TODAY()等函数的日期值
  public static Date getDateFunctionDateCellValue(CellValue cellValue) {
    double value = cellValue.getNumberValue();
    boolean date1904 = false;
    return DateUtil.getJavaDate(value, date1904);
  }

  private static Object getDateCellValue(Date dateCell, String dataFormat, String originalDataFormat) {
    Object cellValueObject = null;

    // Date dateCell=cell.getDateCellValue();
    // 需要指定时区信息
    // SimpleDateFormat dff = new SimpleDateFormat(dataFormat);
    //
    if (Locale.getDefault() == Locale.CHINA) {
      // 修正一下，将MMM的进行修正，否则为 十二月-12 ，修正之后为 12月-12
      if (originalDataFormat.equalsIgnoreCase("d-mmm-yy")
          || originalDataFormat.equalsIgnoreCase("d-mmm")
          || originalDataFormat.equalsIgnoreCase("mmm-yy")) {
        originalDataFormat = Locale.getDefault() + ":" + originalDataFormat;
        dataFormat = PoiBuiltinFormats.getDataFormat(originalDataFormat);
      }
    }
    Locale locale = Locale.getDefault();
    if (dataFormat.contains("AM") || dataFormat.contains("PM") || dataFormat.contains("am")
        || dataFormat.contains("pm")) {
      locale = Locale.US;
    }
    SimpleDateFormat dff = null;
    try {
      dff = new SimpleDateFormat(dataFormat, locale);
      cellValueObject = dff.format(dateCell); // 日期转化
    } catch (Exception e) {
      log.error("将" + dateCell + " 按照 " + dataFormat + " 格式进行日期格式转换时发生了错误");
      String errorDefaultDataFormat = "yyyy-MM-dd";

      if (dataFormat.contains("h") || dataFormat.contains("H")) {
        errorDefaultDataFormat = "HH:mm";
      }
      dff = new SimpleDateFormat(errorDefaultDataFormat, locale);
      cellValueObject = dff.format(dateCell); // 日期转化
    }


    return cellValueObject;
  }


  private static String formatValue(double value, String format) {
    if (format == null || format.equalsIgnoreCase("null")) {
      return value + "";
    }
    if (format.equalsIgnoreCase("General")) {
      try {
        BigDecimal bigDecimal = new BigDecimal(value + "");
        return bigDecimal.stripTrailingZeros().toPlainString();
      } catch (Exception e) {
        log.error("BigDecimal->" + value);
        return value + "";
      }

    } else {
      try {
        // 此处需要进行国际化处理
        // 参考：http://stackoverflow.com/questions/3424792/java-decimalformat-creates-error-when-enforcing-exponent-sign
        // 需要兼容 e 的情况,需要将e 转换为 E

        if (format.contains("e")) {
          format = format.replace("e", "E");
        }

        String newFormat = format;
        if (format.contains("E"))// 包含科学表达式,需要保留科学表达式
        {
          /*
           * BigDecimal bigDecimal = new BigDecimal(value+""); return
           * bigDecimal.stripTrailingZeros().toPlainString();
           */

          // 需要将E+和E-换成 E
          if (format.contains("E+")) {
            newFormat = format.replace("E+", "E");
          } else if (format.contains("E-")) {
            newFormat = format.replace("E-", "E");
          }
        }


        DecimalFormat df = new DecimalFormat(newFormat);
        String formatValue = df.format(value);

        if (!format.contains("E-")) {
          formatValue = formatValue.replace("E", "E+");
        }
        return formatValue;
      } catch (Exception e) {
        log.error("format:" + format + ",value:" + value);
        return value + "";
      }

    }

  }

  static String regx = "^((-?\\d+.?\\d*)[Ee]{1}(-?\\d+))$";// 科学计数法正则表达式
  static Pattern pattern = Pattern.compile(regx);

  public static boolean isENum(String input) {// 判断输入字符串是否为科学计数法
    return pattern.matcher(input).matches();
  }


  private static String fixDataFormat(String dataFormat) {
    String newDataFormat = dataFormat;
    if (dataFormat != null) {
      String[] dataFormatArray = dataFormat.split(";");
      if (dataFormatArray.length > 0) {
        String tmpDataFormat = dataFormatArray[0];
        newDataFormat =
            tmpDataFormat.replaceAll("_-", "").replace("_", "").replace("(", "").replace(")", "")
                .replace("\"", "").replace("\\", "").replace("*", "").replace("AM", "a")
                .replace("PM", "a").replace("AM/PM", "a")
                .replace("PM/AM", "a")
                // 以四位的方式来显示年份
                // .replace("yy","yyyy")
                .replace("AM(PM)", "a").replace("PM(AM)", "a").replace("am/pm", "a")
                .replace("pm/am", "a").replace("am(pm)", "a").replace("pm(am)", "a");
      }

      if (newDataFormat.contains("yy") && !newDataFormat.contains("yyyy")) {
        newDataFormat = newDataFormat.replace("yy", "yyyy");
      }

      // 分钟的不进行处理
      if (newDataFormat.contains(":mm") || newDataFormat.contains("mm:")) {

      } else // 其他情况的m需要转换为M
      {
        newDataFormat = newDataFormat.replace("m", "M");
      }
    }

    return newDataFormat;
  }

  private static String removePointAndZero(String input) {
    String outPut = input;
    if (input.endsWith(".0")) {
      int lastPointIndex = input.lastIndexOf(".");
      outPut = input.substring(0, lastPointIndex);
    }
    return outPut;
  }



}
