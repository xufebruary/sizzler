package com.sizzler.metamodel.pojo;

import org.apache.metamodel.MetaModelException;
import org.apache.metamodel.QueryPostprocessDataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.MaxRowsDataSet;
import org.apache.metamodel.schema.*;
import org.apache.metamodel.util.SimpleTableDef;

import java.io.Serializable;
import java.util.*;

public class PojoDataContext extends QueryPostprocessDataContext implements Serializable {

  private String schemaName;

  private Map<String, TableDataProvider> tableMap;


  public PojoDataContext() {
    this(new ArrayList<TableDataProvider>());
  }

  public PojoDataContext(List<TableDataProvider> tableDataProviderList) {
    this("schema", tableDataProviderList);
  }

  public PojoDataContext(TableDataProvider... tableDataProviderArray) {
    this("schema", tableDataProviderArray);
  }

  public PojoDataContext(String schemaName, TableDataProvider... tableDataProviderArray) {
    this(schemaName, Arrays.asList(tableDataProviderArray));
  }

  public PojoDataContext(String schemaName, List<TableDataProvider> tableDataProviderList) {
    this.schemaName = schemaName;
    tableMap = new TreeMap<String, TableDataProvider>();
    for (TableDataProvider tableDataProvider : tableDataProviderList) {
      addTableDataProvider(tableDataProvider);
    }

  }

  public void addTableDataProvider(TableDataProvider tableDataProvider) {
    tableMap.put(tableDataProvider.getName(), tableDataProvider);
  }


  @Override
  protected Schema getMainSchema() throws MetaModelException {
    MutableSchema mainSchema = new MutableSchema(getMainSchemaName());

    for (TableDataProvider tableDataProvider : tableMap.values()) {
      SimpleTableDef tableDef = tableDataProvider.getTableDef();
      MutableTable table = tableDef.toTable();
      table.setSchema(mainSchema);
      mainSchema.addTable(table);
    }
    return mainSchema;
  }

  @Override
  protected String getMainSchemaName() throws MetaModelException {
    return schemaName;
  }

  @Override
  protected DataSet materializeMainSchemaTable(Table table, Column[] columns, int maxRows) {

    TableDataProvider tableDataProvider = tableMap.get(table.getName());
    DataSet dataSet = new PojoDataSet(tableDataProvider, columns);
    if (maxRows > 0) {
      dataSet = new MaxRowsDataSet(dataSet, maxRows);
    }
    return dataSet;
  }
}
