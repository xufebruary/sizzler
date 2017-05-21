package com.sizzler.provider.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.apache.metamodel.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.provider.common.file.PtoneFile;
import com.sizzler.provider.domain.PtoneSpliterFile;

public class SpliterFileUtil {
  private static final Logger log = LoggerFactory.getLogger(SpliterFileUtil.class);

  public static PtoneFile convertSpliterFileToPtoneFile(String fileName, InputStream inputStream,
      String spliter, boolean maxRowLimit, Charset charset) {
    PtoneSpliterFile ptoneSpliterFile = new PtoneSpliterFile();
    ptoneSpliterFile.setSpliter(spliter);
    ptoneSpliterFile.setName(fileName);

    MutableSchema schema = new MutableSchema(fileName);

    ptoneSpliterFile.setSchema(schema);

    MutableTable table = null;
    /*
     * 以第一行 做为Header，但是第一行所包含的列数不一定是最多的,应该以列数最多的行来进行创建 Column
     */
    List<Row> defaultRowList = new ArrayList<>();
    DataSetHeader dataSetHeader = null;


    try {
      String charsetName = "UTF-8";
      if (charset != null) {
        charsetName = charset.name();
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charsetName));

      List<List> rowList = new ArrayList<>();

      String tmpLine = null;

      List<String> maxFieldRow = null;

      int maxFiledLineIndex = -1;
      int maxFieldNum = -1;
      int index = -1;
      String cell;
      // String csvPattern="((\"[^\"]*(\"{2})*[^\"]*\")|('[^']*('{2})*[^']*'))*[^,]*,";
      // 该正则用于校验字符串是否符合csv的规范
      // 参考：http://stackoverflow.com/questions/8493195/how-can-i-parse-a-csv-string-with-javascript
      // String
      // csvPattern="^\\s*(?:'[^'\\\\]*(?:\\\\[\\S\\s][^'\\\\]*)*'|\"[^\"\\\\]*(?:\\\\[\\S\\s][^\"\\\\]*)*\"|[^,'\"\\s\\\\]*(?:\\s+[^,'\"\\s\\\\]+)*)\\s*(?:,\\s*(?:'[^'\\\\]*(?:\\\\[\\S\\s][^'\\\\]*)*'|\"[^\"\\\\]*(?:\\\\[\\S\\s][^\"\\\\]*)*\"|[^,'\"\\s\\\\]*(?:\\s+[^,'\"\\s\\\\]+)*)\\s*)*$";
      String csvPattern =
          "(?!\\s*$)\\s*(?:'([^'\\\\]*(?:\\\\[\\S\\s][^'\\\\]*)*)'|\"([^\"\\\\]*(?:\\\\[\\S\\s][^\"\\\\]*)*)\"|([^,'\"\\s\\\\]*(?:\\s+[^,'\"\\s\\\\]+)*))\\s*(?:,|$)";
      while ((tmpLine = reader.readLine()) != null) {
        index++;

        Pattern pCells = Pattern.compile(csvPattern);

        Matcher mCells = pCells.matcher(tmpLine);
        List<String> cells = new ArrayList<String>();// 每行记录一个list

        // 读取每个单元格
        while (mCells.find()) {
          cell = mCells.group();
          /*
           * cell = cell.replaceAll( "(?sm)\"?([^\"]*(\"{2})*[^\"]*)\"?.*,", "$1"); cell =
           * cell.replaceAll("(?sm)(\"(\"))", "$2");
           */
          if (cell.endsWith(",")) {
            cell = cell.substring(0, cell.length() - 1);
          }
          cells.add(cell);
        }

        rowList.add(cells);

        if (cells.size() > maxFieldNum) {
          maxFieldNum = cells.size();
          maxFieldRow = cells;
          maxFiledLineIndex = index;
        }
      }
      if (maxFiledLineIndex > 0)// 不是第一行的列数最多
      {
        // 按道理应该以该行做为表头，目前暂未处理
      }

      List<String> firstRow = null;
      if (rowList.size() > 0) {
        firstRow = rowList.get(0);
      }
      // 文件为空
      if (firstRow == null) {
        ptoneSpliterFile.setEmpty(true);
      } else {
        table = new MutableTable(fileName);
        table.setType(TableType.TABLE);
        table.setSchema(schema);
        schema.addTable(table);
        long rowCount = 0;

        // String[] columnNameArray=firstLine.split(spliter);

        Column[] columnArray = new Column[maxFieldNum];
        for (int i = 0; i < columnArray.length; i++) {
          MutableColumn column = new MutableColumn();
          column.setColumnNumber(i);
          column.setType(ColumnType.STRING);
          if (i >= firstRow.size()) {
            column.setName("");
          } else {
            String columneName = firstRow.get(i);
            // 去掉双引号 和 单引号
            column.setName(removeQuotation(columneName));
          }

          columnArray[i] = column;
        }

        table.setColumns(columnArray);
        dataSetHeader = new CachingDataSetHeader(MetaModelHelper.createSelectItems(columnArray));

        for (List row : rowList) {
          rowCount++;
          if (maxRowLimit)// 有最大行限制时，返回MAX_ROWS
          {
            if (rowCount <= DsConstants.DATA_TYPE_DETERMINE_ROW_LIMIT) {
              defaultRowList.add(createDefaultRow(dataSetHeader, row));
            }
          } else {
            defaultRowList.add(createDefaultRow(dataSetHeader, row));
          }
        }

        table.setRowCount(rowCount);

        DataSet dataSet = new InMemoryDataSet(dataSetHeader, defaultRowList);

        LinkedHashMap<String, DataSet> dataSetMap = new LinkedHashMap<>();
        dataSetMap.put(table.getName(), dataSet);

        /*
         * 决定每一行的数据类型
         */
        LinkedHashMap<String, List<Row>> fileDataMap =
            SchemaUtil.determineColumnType(schema, dataSetMap, maxRowLimit);

        ptoneSpliterFile.setFileDataMap(fileDataMap);
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      FileHelper.safeClose(inputStream);
    }

    return ptoneSpliterFile;
  }

  private static DefaultRow createDefaultRow(DataSetHeader dataSetHeader, List<String> columnList) {
    List<Object> rowData = new ArrayList<>();

    for (String columnValue : columnList) {
      rowData.add(removeQuotation(columnValue));
    }
    int headerLength = dataSetHeader.getSelectItems().length;
    if (rowData.size() < headerLength) {
      int k = headerLength - rowData.size();
      for (int i = 0; i < k; i++) {
        rowData.add("");
      }
    }
    DefaultRow row = new DefaultRow(dataSetHeader, rowData.toArray());
    return row;
  }

  private static DefaultRow createDefaultRow(DataSetHeader dataSetHeader, String line,
      String spliter) {
    List<Object> rowData = new ArrayList<>();

    String[] columnValueArray = line.split(spliter);
    for (String columnValue : columnValueArray) {
      rowData.add(removeQuotation(columnValue));
    }
    int headerLength = dataSetHeader.getSelectItems().length;
    if (rowData.size() < headerLength) {
      int k = headerLength - rowData.size();
      for (int i = 0; i < k; i++) {
        rowData.add("");
      }
    }
    DefaultRow row = new DefaultRow(dataSetHeader, rowData.toArray());
    return row;
  }

  public static String removeQuotation(String originalValue) {
    String result = originalValue;

    if ((originalValue.startsWith("\"") && originalValue.endsWith("\""))
        || (originalValue.startsWith("'") && originalValue.endsWith("'"))) {
      // 当只有一个 双引号 或者 只有一个单双引号时
      if (originalValue.length() - 1 > 1) {
        result = originalValue.substring(1, originalValue.length() - 1);
      } else if (originalValue.equalsIgnoreCase("\"\"") || originalValue.equalsIgnoreCase("''"))// 只包含两个
                                                                                                // 双引号或者单引号
      {
        result = "";
      } else if (originalValue.equalsIgnoreCase("\"") || originalValue.equalsIgnoreCase("'")) // 只包含一个双引号或者一个单引号
      {
        result = "";
      }

    }
    return result;
  }

  private static String findFirstLine(BufferedReader reader) {
    String firstLine = null;
    try {
      while ((firstLine = reader.readLine()) != null) {
        if (firstLine.trim().equals("")) {
          firstLine = null;
          continue;
        } else {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return firstLine;
  }
}
