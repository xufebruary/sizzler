package com.sizzler.provider.common.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.MetaModelHelper;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;

import com.sizzler.common.sizzler.DsConstants;

/**
 * Created by ptmind on 2015/12/5.
 */
public class SchemaUtil {

  /*
   * Map<String,DataSet> tableDataSetMap 中的key为Table的名称，value为该Table中所包含的所有数据
   */
  public static LinkedHashMap<String, List<Row>> determineColumnType(MutableSchema schema,
      Map<String, DataSet> tableDataSetMap, boolean excelEditorRowsLimit) {
    LinkedHashMap<String, List<Row>> fileDataMap = new LinkedHashMap<>();

    for (Map.Entry<String, DataSet> entry : tableDataSetMap.entrySet()) {
      DataSet dataSet = entry.getValue();
      List<Row> rowList = DataTypeDetermine.determineDataSetColumnType(dataSet);
      MutableTable table = (MutableTable) schema.getTableByName(entry.getKey());
      Column[] columns = MetaModelHelper.getDataSetColumns(dataSet);
      table.setColumns(columns);

      // 限制返回前台excel编辑器中的数据条数
      if (excelEditorRowsLimit && rowList != null
          && rowList.size() > DsConstants.EXCEL_EDITOR_ROW_LIMIT) {
        List<Row> fixRowList = new ArrayList<Row>();
        for (int i = 0; i < DsConstants.EXCEL_EDITOR_ROW_LIMIT; i++) {
          fixRowList.add(rowList.get(i));
        }
        rowList = fixRowList;
      }

      fileDataMap.put(entry.getKey(), rowList);
    }

    return fileDataMap;
  }
}
