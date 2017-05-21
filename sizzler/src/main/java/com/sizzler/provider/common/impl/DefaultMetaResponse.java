package com.sizzler.provider.common.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.MutableSchema;

import com.sizzler.provider.common.MetaResponse;

/**
 * Created by ptmind on 2015/12/8.
 */
public class DefaultMetaResponse implements MetaResponse {
  private boolean isEmpty;
  private MutableSchema schema;
  private LinkedHashMap<String, List<Row>> tableDataMap = new LinkedHashMap<>();
  private LinkedHashMap<String, List<List>> tableListDataMap;
  private boolean updateTableListDataMap = false;


  public MutableSchema getSchema() {
    return schema;
  }

  public void setSchema(MutableSchema schema) {
    this.schema = schema;
  }

  public LinkedHashMap<String, List<Row>> getTableDataMap() {
    return tableDataMap;
  }

  public void setTableDataMap(LinkedHashMap<String, List<Row>> tableDataMap) {
    this.tableDataMap = tableDataMap;
    updateTableListDataMap = true;
    this.tableListDataMap = getTableListDataMap();
  }

  public boolean isEmpty() {
    return isEmpty;
  }

  public void setEmpty(boolean isEmpty) {
    this.isEmpty = isEmpty;
  }

  public LinkedHashMap<String, List<List>> getTableListDataMap() {
    if (tableListDataMap != null && !updateTableListDataMap) {
      return tableListDataMap;
    } else {
      // 根据fileDataMap 来创建 fileListDataMap
      tableListDataMap = new LinkedHashMap<String, List<List>>();
      if (tableDataMap != null) {
        for (Map.Entry<String, List<Row>> fileDataEntry : tableDataMap.entrySet()) {
          String key = fileDataEntry.getKey();
          List<Row> rowList = fileDataEntry.getValue();
          List<List> newRowList = new ArrayList<>();

          for (Row row : rowList) {
            newRowList.add(Arrays.asList(row.getValues()));
          }
          tableListDataMap.put(key, newRowList);
        }
      }
    }


    return tableListDataMap;
  }

  @Override
  public String getContent() {
    return null;
  }
}
