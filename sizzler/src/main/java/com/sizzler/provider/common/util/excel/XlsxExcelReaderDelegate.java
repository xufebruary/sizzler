package com.sizzler.provider.common.util.excel;

import java.io.InputStream;
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
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.MutableColumn;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.schema.TableType;
import org.apache.metamodel.util.FileHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.utils.CollectionUtil;

public class XlsxExcelReaderDelegate implements ExcelReaderDelegate {

  public Map<String, Object> createSchema(String fileName, InputStream inputStream,
      boolean maxRowLimit) throws Exception {

    /*
     * 创建 Schema对象
     */
    MutableSchema schema = new MutableSchema(fileName);

    Map<String, Object> resultMap = new HashMap<String, Object>();

    LinkedHashMap<String, DataSet> dataSetMap = new LinkedHashMap<String, DataSet>();

    resultMap.put("schema", schema);
    resultMap.put("dataSetMap", dataSetMap);

    OPCPackage opcPackage = OPCPackage.open(inputStream);
    XSSFReader xssfReader = new XSSFReader(opcPackage);
    Map<String, String> sheetNameToInternalId = new LinkedHashMap<String, String>();

    /*
     * 1、首先解析出sheet列表
     */
    XlsxWorkbookToTablesHandler workbookToTablesHandler = new XlsxWorkbookToTablesHandler(schema,
        sheetNameToInternalId);
    buildTables(xssfReader, workbookToTablesHandler);

    for (Map.Entry<String, String> sheetEntry : sheetNameToInternalId.entrySet()) {
      String sheetName = sheetEntry.getKey();
      String sheetId = sheetEntry.getValue();
      MutableTable mutableTable = (MutableTable) schema.getTableByName(sheetName);
      boolean isEmptyTable = buildColumns(mutableTable, sheetId, xssfReader, dataSetMap,
          maxRowLimit);
      if (isEmptyTable) {
        schema.removeTable(mutableTable);
      }
    }
    return resultMap;
  }

  private void buildTables(XSSFReader xssfReader,
      XlsxWorkbookToTablesHandler workbookToTablesHandler) throws Exception {
    InputStream workbookData = null;
    try {
      workbookData = xssfReader.getWorkbookData();
      XMLReader workbookParser = ExcelUtil2.createXmlReader();
      workbookParser.setContentHandler(workbookToTablesHandler);
      workbookParser.parse(new InputSource(workbookData));
    } finally {
      FileHelper.safeClose(workbookData);
    }

  }

  private boolean buildColumns(MutableTable table, String relationshipId, XSSFReader xssfReader,
      LinkedHashMap<String, DataSet> dataSetMap, boolean maxRowLimit) throws Exception {
    boolean isEmptyTable = true;

    InputStream sheetData = null;
    try {
      sheetData = xssfReader.getSheet(relationshipId);
      XlsxSheetToRowsHandler xlsxSheetToRowsHandler = new XlsxSheetToRowsHandler(xssfReader);
      XMLReader sheetParser = ExcelUtil2.createXmlReader();
      sheetParser.setContentHandler(xlsxSheetToRowsHandler);
      sheetParser.parse(new InputSource(sheetData));

      // 在这里sheet的数据已经准备好，现在开始组装DataSet及Table的Column信息
      List<List<String>> allRowList = xlsxSheetToRowsHandler.getAllRowList();

      if (CollectionUtil.isNotEmpty(allRowList)) {

        int firstRowNum = 0;

        // 先处理列数据，并提取出最大的有数据的列索引
        // 原提取最大列索引的代码：int realLastCellNum = xlsxSheetToRowsHandler.getMaxCols()
        // + 1;
        // 原代码会提取出无数据的最大列索引，所以导致列数据超出范围的问题，继而影响正常上传
        int realLastCellNum = ExcelUtil2.removeEmptyValueOfListThenReturnMaxSize(allRowList);

        // 然后处理行数据：判断allRowList是否是个空表，提取最大有数据的行索引
        Object[] object = ExcelUtil2.checkIsEmptyTableAndGetLastRowNum(allRowList);
        int lastRowNum = (int) object[0];
        isEmptyTable = (boolean) object[1];

        if (isEmptyTable) {
          return isEmptyTable;
        }

        List<org.apache.metamodel.data.Row> defaultRowList = new ArrayList<org.apache.metamodel.data.Row>();
        DataSetHeader dataSetHeader = null;
        DataSet dataSet = null;
        Column[] columnArray = null;

        long realLastRowNum = lastRowNum;

        table.setType(TableType.TABLE);
        table.setRowCount(Long.valueOf(lastRowNum));

        if (maxRowLimit) {
          // tool中没有DsConstants
          if (realLastRowNum >= DsConstants.DATA_TYPE_DETERMINE_ROW_LIMIT) {
            realLastRowNum = DsConstants.DATA_TYPE_DETERMINE_ROW_LIMIT;
          }

          /*
           * if(realLastCellNum>=DsConstants.EXCEL_EDITOR_COLUMN_LIMIT) {
           * realLastCellNum=DsConstants.EXCEL_EDITOR_COLUMN_LIMIT.intValue(); }
           */
        }
        List<String> firstRow = allRowList.get(firstRowNum);
        columnArray = new Column[realLastCellNum];

        /*
         * 只设置了Column的名称和index（从0开始），没有设置Column的Type
         */
        for (int k = 0; k < columnArray.length; k++) {
          String columnName = "";
          if (firstRow != null && !firstRow.isEmpty() && firstRow.size() > k) {
            String cellValue = firstRow.get(k);
            if (cellValue != null) {
              columnName = cellValue;
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
        for (int m = 0; m < realLastRowNum; m++) {
          List<Object> rowData = new ArrayList<Object>();
          List<String> row = allRowList.get(m);
          if (row != null && !row.isEmpty()) {
            int lastCellNum = realLastCellNum;
            for (int j = 0; j < lastCellNum; j++) {
              if (j > row.size() - 1) {
                rowData.add("");
              } else {
                String cell = row.get(j);
                rowData.add(cell);
              }
            }
          } else {
            for (int n = 0; n < realLastCellNum; n++) {
              rowData.add("");
            }
          }
          row = null;

          DefaultRow defaultRow = new DefaultRow(dataSetHeader, rowData.toArray());

          defaultRowList.add(defaultRow);
        }

        dataSet = new InMemoryDataSet(dataSetHeader, defaultRowList);
        dataSetMap.put(table.getName(), dataSet);

      }

    } finally {
      FileHelper.safeClose(sheetData);
    }

    return isEmptyTable;
  }

}
