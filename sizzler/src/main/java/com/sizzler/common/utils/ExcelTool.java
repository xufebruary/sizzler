package com.sizzler.common.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ptmind on 2015/12/23.
 */
public class ExcelTool {

  // public static FormulaEvaluator evaluator;


  public static List<List> getSheetContent(InputStream inputStream, String sheetName) {
    List<List> rowList = new ArrayList<List>();
    Workbook wb = null;
    FormulaEvaluator evaluator = null;
    try {
      wb = WorkbookFactory.create(inputStream);
      evaluator = wb.getCreationHelper().createFormulaEvaluator();
      int sheetNumber = wb.getNumberOfSheets();
      for (int s = 0; s < sheetNumber; s++) {
        Sheet sheet = wb.getSheetAt(s);
        if (sheetName.equalsIgnoreCase(sheet.getSheetName())) {
          rowList = getSheetRowList(evaluator, sheet, sheet.getLastRowNum() + 1, null);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (wb != null) {
        try {
          wb.close();
        } catch (IOException e) {
          System.out.println("close wb error:" + e.getMessage());
        }

      }
      if (evaluator != null) {
        evaluator.clearAllCachedResultValues();
        evaluator = null;
      }

    }

    return rowList;
  }

  public static LinkedHashMap<String, List<List>> getExcelContent(InputStream inputStream) {
    return getExcelContent(inputStream, false);
  }

  public static LinkedHashMap<String, List<List>> getExcelContent(InputStream inputStream,
      boolean fix) {
    LinkedHashMap<String, List<List>> resultMap = new LinkedHashMap<String, List<List>>();
    Workbook wb = null;
    FormulaEvaluator evaluator = null;
    try {
      // 通过 WorkbookFactory 来统一的兼容 xls 和 xlsx
      wb = WorkbookFactory.create(inputStream);
      evaluator = wb.getCreationHelper().createFormulaEvaluator();
      int sheetNumber = wb.getNumberOfSheets();
      for (int s = 0; s < sheetNumber; s++) {
        Sheet sheet = wb.getSheetAt(s);
        List<List> rowList = new ArrayList<List>();
        if (!fix)// 如果不需要修正，即使用excel中默认的最大行和最大列
        {
          rowList = getSheetRowList(evaluator, sheet, sheet.getLastRowNum() + 1, null);
        } else// 如果需要修正，则需要计算出最大行和最大列
        {
          int realLastRowNum = getLastRowNum(sheet);
          if (realLastRowNum > 0) {
            int realLastCellNum = getLastCellNum(sheet, 0, realLastRowNum);
            rowList = getSheetRowList(evaluator, sheet, realLastRowNum, realLastCellNum);
          }
        }

        resultMap.put(sheet.getSheetName(), rowList);
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (wb != null) {
        try {
          wb.close();
        } catch (IOException e) {
          System.out.println("close wb error:" + e.getMessage());
        }
      }
      if (evaluator != null) {
        evaluator.clearAllCachedResultValues();
        evaluator = null;
      }
    }
    return resultMap;
  }

  public static Integer getLastRowNum(Sheet sheet) {
    int lastRowNum = sheet.getLastRowNum();
    if (lastRowNum == 0) {
      return 0;
    }
    int realLastRowNum = lastRowNum;
    boolean findNotEmptyRow = false;
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
    if (!findNotEmptyRow) {
      return 0;
    }
    return realLastRowNum + 1;
  }

  public static int getLastCellNum(Sheet sheet, Integer firstRowNum, Integer lastRowNum) {
    int realLastCellNum = 0;
    for (int i = firstRowNum; i < lastRowNum; i++) {
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
            cell = null;
          }

        }
      }
    }
    return realLastCellNum;

  }


  public static List<List> getSheetRowList(FormulaEvaluator evaluator, Sheet sheet, int lastRowNum,
      Integer assignedLastCellNum) {
    List<List> rowList = new ArrayList<List>();
    for (int r = 0; r < lastRowNum; r++) {
      List columnList = new ArrayList();
      Row row = sheet.getRow(r);
      if (row != null) {
        int lastCellNum = row.getLastCellNum();
        if (assignedLastCellNum != null) {
          lastCellNum = assignedLastCellNum;
        }

        for (int c = 0; c < lastCellNum; c++) {
          Cell cell = row.getCell(c);
          if (cell == null) {
            columnList.add("");
          } else {
            int cellType = cell.getCellType();
            String dataFormat = cell.getCellStyle().getDataFormatString();

            if (cellType == Cell.CELL_TYPE_FORMULA) {
              CellValue cellValue = evaluator.evaluate(cell);
              // columnList.add(removePointAndZero(cellValue.formatAsString()));
              columnList.add(formatValue(cellValue.getNumberValue(), dataFormat));
            } else if (cellType == Cell.CELL_TYPE_NUMERIC) {
              if (DateUtil.isCellDateFormatted(cell)) {
                Date dateCell = cell.getDateCellValue();
                SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
                String dateCellStr = dff.format(dateCell); // 日期转化
                columnList.add(dateCellStr);
              } else {
                // excel自动会在数据后面加上 “.0”，需要将其去掉
                /*
                 * String valueStr=cell.toString(); columnList.add(removePointAndZero(valueStr));
                 */
                columnList.add(formatValue(cell.getNumericCellValue(), dataFormat));
              }

            } else {
              columnList.add(cell.toString());
            }
            cell = null;

          }
        }
        row = null;
      }
      rowList.add(columnList);
    }
    return rowList;
  }



  private static String removePointAndZero(String input) {
    String outPut = input;
    if (input.endsWith(".0")) {
      int lastPointIndex = input.lastIndexOf(".");
      outPut = input.substring(0, lastPointIndex);
    }
    return outPut;

  }

  private static String formatValue(double value, String format) {
    if (format.equalsIgnoreCase("General")) {
      return removePointAndZero(value + "");
    } else {
      DecimalFormat df = new DecimalFormat(format);
      return df.format(value);
    }
  }

  public static void main(String[] args) {
    File excelFile = new File("d:/zx_2015_10_26_3.xlsx");
    try {
      FileInputStream inputStream = new FileInputStream(excelFile);
      LinkedHashMap<String, List<List>> resultMap = ExcelTool.getExcelContent(inputStream, true);
      System.out.println(resultMap);
    } catch (Exception e) {
      e.printStackTrace();
    }


  }
}
