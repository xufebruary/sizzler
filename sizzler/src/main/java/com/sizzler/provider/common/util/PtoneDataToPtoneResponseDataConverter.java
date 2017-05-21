package com.sizzler.provider.common.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sizzler.provider.common.PtoneColumnHeaders;
import com.sizzler.provider.common.PtoneData;
import com.sizzler.provider.common.PtoneDataTable;
import com.sizzler.provider.common.PtoneResponseData;

public abstract class PtoneDataToPtoneResponseDataConverter {


  // 将PtoneData 转换为 PtoneResponseData
  public void convertPtoneDataToPtoneResponseData(PtoneData ptoneData,
      PtoneResponseData ptoneResponseData) {
    PtoneDataTable dataTable = new PtoneDataTable();

    Map<String, PtoneColumnHeaders> columnHeadersMap = new LinkedHashMap<>();
    List<PtoneColumnHeaders> columnHeaders = ptoneData.getColumnHeaders();
    initColumnHeadersMap(columnHeaders, columnHeadersMap);

    dataTable.setColumnHeaders(columnHeaders);

    List<List<String>> rows = ptoneData.getRows();
    List<List<Object>> responseRows = new ArrayList<>();
    if (rows != null) {
      for (List<String> row : rows) {
        List<Object> responseRow = new ArrayList<>();
        for (int i = 0; i < row.size(); i++) {
          String columnValue = row.get(i);
          PtoneColumnHeaders columnHeader = columnHeaders.get(i);
          // 如果是指标，需要将其转换为对应的数据类型
          if (columnHeader.getColumnType().equals(ColunmType.METRIC.toString())) {
            // responseRow.add(StringUtil.convertStringToDataType(columnValue,columnHeader.getDataType()));
            responseRow.add(convertStringToDataType(columnValue, columnHeader.getDataType()));
          } else {
            responseRow.add(columnValue);
          }

        }

        responseRows.add(responseRow);
      }
      dataTable.setRows(responseRows);
      ptoneResponseData.setDataTable(dataTable);
      // ptoneResponseData.setRows(responseRows);
    }

    Map<String, String> totalsForAllResults = ptoneData.getTotalsForAllResults();
    Map<String, Object> responseTotals = new LinkedHashMap<>();

    if (totalsForAllResults != null) {
      for (Map.Entry<String, String> entry : totalsForAllResults.entrySet()) {
        String metricsName = entry.getKey();
        String metricsValue = entry.getValue();
        PtoneColumnHeaders metricsColumnHeader = columnHeadersMap.get(metricsName);
        Object metricsValueObject =
            convertStringToDataType(metricsValue, metricsColumnHeader.getDataType());
        responseTotals.put(metricsName, metricsValueObject);
      }

      ptoneResponseData.setTotals(responseTotals);
    }


  }

  private void initColumnHeadersMap(List<PtoneColumnHeaders> columnHeaders,
      Map<String, PtoneColumnHeaders> columnHeadersMap) {
    for (PtoneColumnHeaders columnHeader : columnHeaders) {
      columnHeadersMap.put(columnHeader.getName(), columnHeader);
    }

  }

  // 不同的数据源返回的数据的格式可能不同，需要单独的处理
  public abstract Object convertStringToDataType(String value, String dataType);



}
