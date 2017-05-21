package com.sizzler.provider.common.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.MetaModelHelper;
import org.apache.metamodel.data.CachingDataSetHeader;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.DataSetHeader;
import org.apache.metamodel.data.DefaultRow;
import org.apache.metamodel.data.InMemoryDataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.MutableColumn;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.schema.TableType;

import com.sizzler.common.sizzler.DsConstants;

/**
 * 2003及以前版本excel的解析
 * 
 * @date: 2016年6月24日
 * @author peng.xu
 */
public class XlsExcelReaderDelegate extends XlsExcelReaderDelegateAbstract implements
    ExcelReaderDelegate {

  private String fileName;
  private boolean maxRowLimit;
  private Map<String, List<List<String>>> tableDataMap =
      new LinkedHashMap<String, List<List<String>>>();

  public XlsExcelReaderDelegate(String fileName, InputStream inputStream, boolean maxRowLimit)
      throws IOException, FileNotFoundException {
    super(inputStream);
  }

  public XlsExcelReaderDelegate(InputStream inputStream) throws IOException, FileNotFoundException {
    super(inputStream);
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public boolean isMaxRowLimit() {
    return maxRowLimit;
  }

  public void setMaxRowLimit(boolean maxRowLimit) {
    this.maxRowLimit = maxRowLimit;
  }

  public Map<String, List<List<String>>> getTableDataMap() {
    return tableDataMap;
  }

  public void setTableDataMap(Map<String, List<List<String>>> tableDataMap) {
    this.tableDataMap = tableDataMap;
  }

  @Override
  public void optRows(int sheetIndex, String sheetName, int curRow, List<String> rowlist)
      throws SQLException {

    // 构建Sheet数据
    List<List<String>> curDataTable = null;
    if (tableDataMap.containsKey(sheetName)) {
      curDataTable = tableDataMap.get(sheetName);
    } else {
      curDataTable = new ArrayList<List<String>>();
      tableDataMap.put(sheetName, curDataTable);
    }
    List<String> curRowList = new ArrayList<String>();
    curRowList.addAll(rowlist);
    curDataTable.add(curRowList);
  }

  public Map<String, Object> createSchema(String fileName, InputStream inputStream,
      boolean maxRowLimit) {
    this.setFileName(fileName);
    this.setMaxRowLimit(maxRowLimit);

    Map<String, Object> resultMap = new HashMap<String, Object>();
    MutableSchema schema = new MutableSchema(fileName);
    LinkedHashMap<String, DataSet> dataSetMap = new LinkedHashMap<String, DataSet>();
    resultMap.put("schema", schema);
    resultMap.put("dataSetMap", dataSetMap);

    try {
      this.process();
      Map<String, List<List<String>>> tableDataMap = this.getTableDataMap();

      for (Map.Entry<String, List<List<String>>> entry : tableDataMap.entrySet()) {
        String tableName = entry.getKey();
        List<List<String>> tableData = entry.getValue();

        // 修正行、列
        int firstRowNum = 0;
        int lastRowNum = tableData.size() - 1;
        long realLastRowNum = lastRowNum;
        boolean findNotEmptyRow = false;
        int realLastColumnNum = 0;

        // 计算实际行数
        for (int i = lastRowNum; i >= 0; i--) {
          List<String> row = tableData.get(i);
          int lastCellNum = row.size() - 1;
          if (row != null && lastCellNum >= 0) {
            // 判断当前行是否为空行
            for (int j = 0; j <= lastCellNum; j++) {
              String cell = row.get(j);
              if (cell != null && !"".equals(cell.trim())) {
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

        // 如果sheet为空则跳过
        if (findNotEmptyRow) {
          // 计算出实际列数，包含数据的cell的最大值
          for (int i = firstRowNum; i < realLastRowNum + 1; i++) {
            List<String> row = tableData.get(i);
            int lastCellNum = row.size() - 1;
            if (row != null && lastCellNum >= 0) {
              // 从每一行的最后一列向前面找到 第一个不为空的cell
              for (int j = lastCellNum; j >= 0; j--) {
                String cell = row.get(j);
                if (cell != null && !"".equals(cell.trim())) {
                  if (j > realLastColumnNum) {
                    realLastColumnNum = j;
                  }
                }
                cell = null;
              }
            }
            row = null;
          }

          // 构建列信息
          Column[] columnArray = new Column[realLastColumnNum + 1];
          List<String> firstRow = tableData.get(firstRowNum);
          for (int colIndex = 0; colIndex < columnArray.length; colIndex++) {
            String columnName = "";
            if (firstRow != null && firstRow.size() > colIndex) {
              columnName = firstRow.get(colIndex);
            }
            // 只设置了Column的名称和index（从0开始），没有设置Column的Type
            MutableColumn column = new MutableColumn(columnName);
            column.setColumnNumber(colIndex);
            column.setType(ColumnType.STRING);
            columnArray[colIndex] = column;
          }

          // 构建schema信息, 每个sheet的第一行作为表头
          MutableTable table = new MutableTable(tableName);
          table.setType(TableType.TABLE);
          table.setSchema(schema);
          table.setRowCount(realLastRowNum + 1); // 设置实际行数
          table.setColumns(columnArray);
          schema.addTable(table);

          DataSetHeader dataSetHeader =
              new CachingDataSetHeader(MetaModelHelper.createSelectItems(columnArray));

          // 设置限制返回数据行数
          long returnLastRowNum = realLastRowNum;
          long returnLastColumnNum = realLastColumnNum;
          if (maxRowLimit) {
            if (returnLastRowNum + 1 >= DsConstants.DATA_TYPE_DETERMINE_ROW_LIMIT) {
              returnLastRowNum = DsConstants.DATA_TYPE_DETERMINE_ROW_LIMIT - 1;
            }
            // if (returnLastColumnNum + 1 >=
            // DsConstants.EXCEL_EDITOR_COLUMN_LIMIT) {
            // returnLastColumnNum =
            // DsConstants.EXCEL_EDITOR_COLUMN_LIMIT.intValue() - 1;
            // }
          }

          // 构建 DataSet
          List<Row> defaultRowList = new ArrayList<Row>();
          for (int i = 0; i <= returnLastRowNum; i++) {
            List<String> rowData = tableData.get(i);
            if (rowData.size() <= returnLastColumnNum) {
              for (int j = rowData.size(); j <= returnLastColumnNum; j++) {
                rowData.add("");
              }
            } else {
              int maxSize = rowData.size();
              for (int j = maxSize - 1; j > returnLastColumnNum; j--) {
                rowData.remove(j);
              }
            }
            DefaultRow defaultRow = new DefaultRow(dataSetHeader, rowData.toArray());
            defaultRowList.add(defaultRow);
          }
          DataSet dataSet = new InMemoryDataSet(dataSetHeader, defaultRowList);
          dataSetMap.put(tableName, dataSet);

        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resultMap;
  }

  public static void main(String[] args) {
    FileInputStream inputStream = null;
    try {
      String file = "C:/Users/peng.xu/Desktop/_test/10mb.xls";
      file = "C:/Users/peng.xu/Desktop/_test/各种基本数据类型.xls";
      file = "C:/Users/peng.xu/Desktop/_test/testDate.xls";
      file = "C:/Users/peng.xu/Desktop/_test/testNumber.xls";
      file = "C:/Users/peng.xu/Desktop/_test/数据格式.xls";
      file = "C:/Users/peng.xu/Desktop/_test/testExcel4.csv";
      file = "C:/Users/peng.xu/Desktop/_test/数据时间日期格式 - 副本.xlsx";
      inputStream = new FileInputStream(new File(file));

      Map<String, Object> resultMap = ExcelUtil.createSchema("fileName", inputStream, false);
      System.out.println(resultMap);

      // Map<String, Object> resultMap2 = ExcelUtil2.createSchema("fileName", inputStream, false);
      // System.out.println(resultMap2);


    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    }

  }

  // for test
  private FileWriter writer;
  private String currentLogFileName;

  public void optRowsPrint(int sheetIndex, String sheetName, int curRow, List<String> rowlist)
      throws SQLException {
    String rowStr = curRow + "";
    for (int i = 0; i < rowlist.size(); i++) {
      rowStr = rowStr + "'" + rowlist.get(i) + "', ";
    }

    try {
      String logPath = "C:/Users/peng.xu/Desktop/_test/log/";
      String fileName =
          logPath + (logPath.endsWith("/") ? "" : "/") + sheetIndex + "-" + sheetName + ".log";
      if (writer == null || !fileName.equals(currentLogFileName)) {
        if (writer != null) {
          writer.close();
          Thread.sleep(200);
        }
        try {
          writer = new FileWriter(fileName, true);
          // currentLogFileName = fileName;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (writer != null) {
        writer.write(rowStr + "\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // for test
  private List<List<List<String>>> sheets = new ArrayList<List<List<String>>>();

  public void optRowsToList(int sheetIndex, String sheetName, int curRow, List<String> rowlist)
      throws SQLException {
    List<List<String>> curSheet = null;
    if (sheets.size() > sheetIndex) {
      curSheet = sheets.get(sheetIndex);
    } else {
      curSheet = new ArrayList<List<String>>();
      sheets.add(curSheet);
    }
    curSheet.add(rowlist);
  }
}
