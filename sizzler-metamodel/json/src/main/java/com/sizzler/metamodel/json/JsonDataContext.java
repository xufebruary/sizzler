package com.sizzler.metamodel.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.MetaModelException;
import org.apache.metamodel.QueryPostprocessDataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.MaxRowsDataSet;
import org.apache.metamodel.ptutil.CollectionUtil;
import org.apache.metamodel.ptutil.StringUtil;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.util.ColumnTypeUtil;
import org.apache.metamodel.util.SimpleTableDef;

import com.sizzler.metamodel.json.util.JsonFlattenerUtil;
import com.sizzler.metamodel.json.util.JsonUtil;
import com.sizzler.metamodel.pojo.ArrayTableDataProvider;
import com.sizzler.metamodel.pojo.PojoDataSet;
import com.sizzler.metamodel.pojo.TableDataProvider;

public abstract class JsonDataContext extends QueryPostprocessDataContext implements Serializable {

  private static final long serialVersionUID = 1976198035600088038L;

  private String jsonStr;
  private String schemaName = "json";
  private String tableName = "json";
  private SimpleTableDef tableDef;
  private TableDataProvider tableDataProvider;
  /**
   * 当实现类无法提供json数据时，为避免生成出来的表结构是空的（会导致query语句执行失败），所以增加一个columnMap作为默认的表结构<br>
   */
  private String[] columnArray;

  /**
   * 包含指标和维度的类型
   */
  protected Map<String, String> metricsDataTypeMap;

  public Map<String, String> getMetricsDataTypeMap() {
    return metricsDataTypeMap;
  }

  public void setMetricsDataTypeMap(Map<String, String> metricsDataTypeMap) {
    this.metricsDataTypeMap = metricsDataTypeMap;
  }

  public JsonDataContext(String jsonStr, String schemaName, String tableName) {
    this.schemaName = schemaName;
    this.tableName = tableName;
    this.jsonStr = jsonStr;
  }

  public JsonDataContext(String jsonStr, String schemaName, String tableName, String[] columnArray) {
    this.schemaName = schemaName;
    this.tableName = tableName;
    this.jsonStr = jsonStr;
    this.columnArray = columnArray;
  }

  public JsonDataContext(String schemaName, String tableName) {
    this.schemaName = schemaName;
    this.tableName = tableName;
  }

  public JsonDataContext(String schemaName, String tableName, String[] columnArray) {
    this.schemaName = schemaName;
    this.tableName = tableName;
    this.columnArray = columnArray;
  }

  private void init() {
    try {
      // 获取数据
      String _data = initData();
      String[] _columnArray = new String[] {};
      List<Object[]> _rowList = new ArrayList<Object[]>();
      List<ColumnType> columnTypeList = null;
      if (StringUtil.isBlank(_data)) {
        // 返回的数据是空的，则走实现类设置的columnArray
        _columnArray = this.columnArray;
      } else {
        List<LinkedHashMap<String, Object>> rowMapList = JsonFlattenerUtil.parseJson(_data);
        LinkedHashSet<String> columnNameSet = JsonFlattenerUtil.getColumnNameSet(rowMapList);
        _rowList = JsonFlattenerUtil.getRowList(rowMapList, columnNameSet);
        // 转成column数组。
        _columnArray = columnNameSet.toArray(new String[] {});
        // 验证columnArray中的字段是否都包含在了_columnArray中
        _columnArray = JsonUtil.validJsonColumnArrayIsComplete(_columnArray, columnArray);
        // 验证完列之后，还需要验证一下数据，看看是否需要填充缺失的列数据
        _rowList = JsonUtil.validRowListByJsonColumnArray(_rowList, _columnArray);
        columnTypeList = new ArrayList<ColumnType>(_columnArray.length);
        createColumnsTypeArray(_columnArray, columnTypeList);
      }
      // columnTypeList为null 就new一个新的list
      if (CollectionUtil.isEmpty(columnTypeList)) {
        columnTypeList = new ArrayList<ColumnType>();
      }
      ColumnType[] columnTypes = new ColumnType[columnTypeList.size()];
      /**
       * 新增设置columntype
       */
      tableDef = new SimpleTableDef(tableName, _columnArray, columnTypeList.toArray(columnTypes));
      tableDataProvider = new ArrayTableDataProvider(tableDef, _rowList);

    } catch (MetaModelException mme) {
      throw mme;
    } catch (Exception e) {
      e.printStackTrace();
      throw new MetaModelException(e);
    }
  }

  /**
   * 
   * @description 根据数据库dataType设置维度类型，如果没有类型，默认设置为VARCHAR。如果不适用，子类重写。
   * @author：shaoqiang.guo
   * @data：2016年8月19日 下午3:50:40
   * @param _columnArray
   * @param columnTypeList
   */
  protected void createColumnsTypeArray(String[] _columnArray, List<ColumnType> columnTypeList) {

    for (String columnName : _columnArray) {
      if (!CollectionUtil.isEmpty(metricsDataTypeMap) && metricsDataTypeMap.containsKey(columnName)) {
        columnTypeList.add(ColumnTypeUtil.convertToColumnType(metricsDataTypeMap.get(columnName)));
      } else {
        columnTypeList.add(ColumnType.VARCHAR);
      }

    }
  }

  @Override
  protected Schema getMainSchema() throws MetaModelException {
    try {
      init();
      MutableSchema mainSchema = new MutableSchema(getMainSchemaName());

      MutableTable table = tableDef.toTable();
      table.setSchema(mainSchema);
      mainSchema.addTable(table);

      return mainSchema;
    } catch (MetaModelException mme) {
      throw mme;
    } catch (Exception e) {
      throw new MetaModelException(e);
    }
  }

  @Override
  protected String getMainSchemaName() throws MetaModelException {
    return schemaName;
  }

  @Override
  protected DataSet materializeMainSchemaTable(Table table, Column[] columns, int maxRows) {
    DataSet dataSet = new PojoDataSet(tableDataProvider, columns);
    if (maxRows > 0) {
      dataSet = new MaxRowsDataSet(dataSet, maxRows);
    }
    return dataSet;
  }

  public SimpleTableDef getTableDef() {
    return tableDef;
  }

  public TableDataProvider getTableDataProvider() {
    return tableDataProvider;
  }

  protected abstract String initData();

}
