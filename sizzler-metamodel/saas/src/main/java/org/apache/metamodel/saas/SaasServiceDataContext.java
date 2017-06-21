package org.apache.metamodel.saas;

import com.sizzler.metamodel.pojo.PojoDataSet;
import com.sizzler.metamodel.pojo.TableDataProvider;

import org.apache.metamodel.MetaModelException;
import org.apache.metamodel.QueryPostprocessDataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.MaxRowsDataSet;
import org.apache.metamodel.ptutil.CollectionUtil;
import org.apache.metamodel.schema.*;
import org.apache.metamodel.util.ColumnTypeUtil;
import org.apache.metamodel.util.CommonQueryRequest;
import org.apache.metamodel.util.SimpleTableDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SaasServiceDataContext extends QueryPostprocessDataContext {
  protected String schemaName;
  protected String tableName;
  protected CommonQueryRequest queryRequest;
  protected Map<String, String> metricsDataTypeMap;
  protected TableDataProvider tableDataProvider = null;

  public SaasServiceDataContext(CommonQueryRequest queryRequest) {
    this.queryRequest = queryRequest;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public CommonQueryRequest getQueryRequest() {
    return queryRequest;
  }

  public void setQueryRequest(CommonQueryRequest queryRequest) {
    this.queryRequest = queryRequest;
  }

  public Map<String, String> getMetricsDataTypeMap() {
    return metricsDataTypeMap;
  }

  public void setMetricsDataTypeMap(Map<String, String> metricsDataTypeMap) {
    this.metricsDataTypeMap = metricsDataTypeMap;
  }

  public TableDataProvider getTableDataProvider() {
    return tableDataProvider;
  }

  public void setTableDataProvider(TableDataProvider tableDataProvider) {
    this.tableDataProvider = tableDataProvider;
  }

  @Override
  protected Schema getMainSchema() throws MetaModelException {
    MutableSchema schema = new MutableSchema(getMainSchemaName());
    List<String> columnNameList = new ArrayList<>();
    List<ColumnType> columnTypeList = new ArrayList<>();

    /*
     * 创建 Column
     */
    createColumns(columnNameList, columnTypeList);

    /*
     * 创建 Table
     */
    String[] columnNames = new String[columnNameList.size()];
    ColumnType[] columnTypes = new ColumnType[columnTypeList.size()];

    SimpleTableDef tableDef = new SimpleTableDef(getTableName(),
        columnNameList.toArray(columnNames), columnTypeList.toArray(columnTypes));

    MutableTable table = tableDef.toTable();
    table.setSchema(schema);
    schema.addTable(table);

    initTableDataProvider(tableDef);

    return schema;
  }

  @Override
  protected DataSet materializeMainSchemaTable(Table table, Column[] columns, int maxRows) {
    if (tableDataProvider == null) {
      throw new RuntimeException("tableDataProvider do not init !");
    }
    DataSet dataSet = new PojoDataSet(tableDataProvider, columns);
    if (maxRows > 0) {
      dataSet = new MaxRowsDataSet(dataSet, maxRows);
    }
    return dataSet;

  }

  // 默认的创建Column的方法（Dimension + Metrics）
  protected void createColumns(List<String> columnNameList, List<ColumnType> columnTypeList)
      throws MetaModelException {
    /*
     * 首先添加 Dimension 列
     */
    String dimensions = queryRequest.getDimensions();
    if (dimensions != null && !dimensions.equals("")) {
      String[] dimensionArray = dimensions.split(",");
      for (String dimension : dimensionArray) {
        columnNameList.add(dimension);
        if (metricsDataTypeMap.containsKey(dimension)) {
          columnTypeList.add(ColumnTypeUtil.convertToColumnType(metricsDataTypeMap.get(dimension)));
        } else {
          columnTypeList.add(ColumnType.VARCHAR);
        }
      }
    }

    /*
     * 然后添加 Metrics 列
     */
    String metrics = queryRequest.getMetrics();
    String[] metricArray = metrics.split(",");

    for (String metric : metricArray) {
      columnNameList.add(metric);
      if (metricsDataTypeMap.containsKey(metric)) {
        columnTypeList.add(ColumnTypeUtil.convertToColumnType(metricsDataTypeMap.get(metric)));
      } else {
        columnTypeList.add(ColumnType.VARCHAR);
      }
    }

  }

  // 默认的创建Column的方法（ Metrics）
  protected void createTotalColumns(List<String> columnNameList, List<ColumnType> columnTypeList)
      throws MetaModelException {
    /*
     * 然后添加 Metrics 列
     */
    String metrics = queryRequest.getMetrics();
    String[] metricArray = metrics.split(",");

    for (String metric : metricArray) {
      columnNameList.add(metric);
      if (!CollectionUtil.isEmpty(metricsDataTypeMap) && metricsDataTypeMap.containsKey(metric)) {
        columnTypeList.add(ColumnTypeUtil.convertToColumnType(metricsDataTypeMap.get(metric)));
      } else {
        columnTypeList.add(ColumnType.VARCHAR);
      }
    }

  }

  protected abstract void initTableDataProvider(SimpleTableDef tableDef) throws MetaModelException;
}
