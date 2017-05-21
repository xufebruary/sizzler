/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.metamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.convert.ConvertedDataSetInterceptor;
import org.apache.metamodel.convert.Converters;
import org.apache.metamodel.convert.HasReadTypeConverters;
import org.apache.metamodel.convert.TypeConverter;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.DataSetHeader;
import org.apache.metamodel.data.DefaultRow;
import org.apache.metamodel.data.EmptyDataSet;
import org.apache.metamodel.data.FirstRowDataSet;
import org.apache.metamodel.data.InMemoryDataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.data.SimpleDataSetHeader;
import org.apache.metamodel.query.FilterItem;
import org.apache.metamodel.query.FromItem;
import org.apache.metamodel.query.GroupByItem;
import org.apache.metamodel.query.JoinType;
import org.apache.metamodel.query.OperatorType;
import org.apache.metamodel.query.OrderByItem;
import org.apache.metamodel.query.Query;
import org.apache.metamodel.query.ScalarFunction;
import org.apache.metamodel.query.SelectClause;
import org.apache.metamodel.query.SelectItem;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.MutableColumn;
import org.apache.metamodel.schema.MutableRelationship;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.schema.Relationship;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.schema.TableType;
import org.apache.metamodel.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract DataContext for data sources that do not support SQL queries natively.
 * <p/>
 * Instead this superclass only requires that a subclass can materialize a single table at a time.
 * Then the query will be executed by post processing the datasets client-side.
 */
public abstract class QueryPostprocessDataContext extends AbstractDataContext implements
    HasReadTypeConverters {

  private static final Logger logger = LoggerFactory.getLogger(QueryPostprocessDataContext.class);

  public static final String INFORMATION_SCHEMA_NAME = "information_schema";

  private final Map<Column, TypeConverter<?, ?>> _converters;

  public QueryPostprocessDataContext() {
    super();
    _converters = new HashMap<Column, TypeConverter<?, ?>>();
  }

  @Override
  public DataSet executeQuery(final Query query) {
    final List<SelectItem> selectItems = query.getSelectClause().getItems();
    final List<FromItem> fromItems = query.getFromClause().getItems();
    final List<FilterItem> whereItems = query.getWhereClause().getItems();
    final List<SelectItem> whereSelectItems = query.getWhereClause().getEvaluatedSelectItems();
    final List<GroupByItem> groupByItems = query.getGroupByClause().getItems();
    final List<SelectItem> groupBySelectItems = query.getGroupByClause().getEvaluatedSelectItems();
    final List<SelectItem> havingSelectItems = query.getHavingClause().getEvaluatedSelectItems();
    final List<SelectItem> orderBySelectItems = query.getOrderByClause().getEvaluatedSelectItems();

    final List<FilterItem> havingItems = query.getHavingClause().getItems();
    final List<OrderByItem> orderByItems = query.getOrderByClause().getItems();

    final int firstRow = (query.getFirstRow() == null ? 1 : query.getFirstRow());
    final int maxRows = (query.getMaxRows() == null ? -1 : query.getMaxRows());

    if (maxRows == 0) {
      // no rows requested - no reason to do anything
      return new EmptyDataSet(selectItems);
    }

    // check certain common query types that can often be optimized by
    // subclasses

    final boolean singleFromItem = fromItems.size() == 1;
    final boolean noGrouping = groupByItems.isEmpty() && havingItems.isEmpty();
    // 只从一个table进行查询，而且没有group by
    if (singleFromItem && noGrouping) {

      final FromItem fromItem = query.getFromClause().getItem(0);
      final Table table = fromItem.getTable();
      if (table != null) {

        // check for SELECT COUNT(*) queries
        // 检查是否是 SELECT COUNT(*) 查询
        if (selectItems.size() == 1) {
          final SelectItem selectItem = query.getSelectClause().getItem(0);
          if (SelectItem.isCountAllItem(selectItem)) {
            final boolean functionApproximationAllowed =
                selectItem.isFunctionApproximationAllowed();
            if (isMainSchemaTable(table)) {
              logger.debug(
                  "Query is a COUNT query with {} where items. Trying executeCountQuery(...)",
                  whereItems.size());
              final Number count =
                  executeCountQuery(table, whereItems, functionApproximationAllowed);
              if (count == null) {
                logger
                    .debug("DataContext did not return any count query results. Proceeding with manual counting.");
              } else {
                List<Row> data = new ArrayList<Row>(1);
                final DataSetHeader header = new SimpleDataSetHeader(new SelectItem[] {selectItem});
                data.add(new DefaultRow(header, new Object[] {count}));
                return new InMemoryDataSet(header, data);
              }
            }
          }
        }
        // 检查是否为simple select 的查询（select部分不包含 distinct、sum等函数）
        final boolean isSimpleSelect = isSimpleSelect(query.getSelectClause());
        if (isSimpleSelect) {
          // check for lookup query by primary key
          if (whereItems.size() == 1) {
            final FilterItem whereItem = whereItems.get(0);
            final SelectItem selectItem = whereItem.getSelectItem();
            if (!whereItem.isCompoundFilter() && selectItem != null
                && selectItem.getColumn() != null) {
              final Column column = selectItem.getColumn();
              if (column.isPrimaryKey() && OperatorType.EQUALS_TO.equals(whereItem.getOperator())) {
                logger
                    .debug("Query is a primary key lookup query. Trying executePrimaryKeyLookupQuery(...)");
                if (table != null) {
                  if (isMainSchemaTable(table)) {
                    final Object operand = whereItem.getOperand();
                    final Row row =
                        executePrimaryKeyLookupQuery(table, selectItems, column, operand);
                    if (row == null) {
                      logger
                          .debug("DataContext did not return any GET query results. Proceeding with manual lookup.");
                    } else {
                      final DataSetHeader header = new SimpleDataSetHeader(selectItems);
                      return new InMemoryDataSet(header, row);
                    }
                  }
                }
              }
            }
          }

          // check for simple queries with or without simple criteria
          if (orderByItems.isEmpty()) {
            // no WHERE criteria set
            if (whereItems.isEmpty()) {
              final DataSet dataSet = materializeTable(table, selectItems, firstRow, maxRows);
              return dataSet;
            }

            final DataSet dataSet =
                materializeTable(table, selectItems, whereItems, firstRow, maxRows);
            return dataSet;
          }
        }
      }
    }

    // Creates a list for all select items that are needed to execute query
    // (some may only be used as part of a filter, but not shown in result)
    // 将select、where、groupby、having、orderby部分所涉及到的字段都 拼接到一起
    List<SelectItem> workSelectItems =
        CollectionUtils.concat(true, selectItems, whereSelectItems, groupBySelectItems,
            havingSelectItems, orderBySelectItems);

    // Materialize the tables in the from clause
    // 填充 from中指定的table的数据
    final DataSet[] fromDataSets = new DataSet[fromItems.size()];
    for (int i = 0; i < fromDataSets.length; i++) {
      FromItem fromItem = fromItems.get(i);
      fromDataSets[i] = materializeFromItem(fromItem, workSelectItems);
    }

    // Execute the query using the raw data
    DataSet dataSet = MetaModelHelper.getCarthesianProduct(fromDataSets, whereItems);

    // we can now exclude the select items imposed by the WHERE clause (and
    // should, to make the aggregation process faster)
    workSelectItems =
        CollectionUtils.concat(true, selectItems, groupBySelectItems, havingSelectItems,
            orderBySelectItems);

    if (groupByItems.size() > 0) {
      dataSet = MetaModelHelper.getGrouped(workSelectItems, dataSet, groupByItems);
    } else {
      dataSet = MetaModelHelper.getAggregated(workSelectItems, dataSet);
    }
    dataSet = MetaModelHelper.getFiltered(dataSet, havingItems);

    if (query.getSelectClause().isDistinct()) {
      dataSet = MetaModelHelper.getSelection(selectItems, dataSet);
      dataSet = MetaModelHelper.getDistinct(dataSet);
      dataSet = MetaModelHelper.getOrdered(dataSet, orderByItems);
    } else {
      dataSet = MetaModelHelper.getOrdered(dataSet, orderByItems);
      dataSet = MetaModelHelper.getSelection(selectItems, dataSet);
    }

    dataSet = MetaModelHelper.getPaged(dataSet, firstRow, maxRows);
    return dataSet;
  }

  /**
   * Determines if all the select items are 'simple' meaning that they just represent scans of
   * values in columns.
   *
   * @param clause
   * @return
   */
  private boolean isSimpleSelect(SelectClause clause) {
    if (clause.isDistinct()) {
      return false;
    }
    for (SelectItem item : clause.getItems()) {
      if (item.getAggregateFunction() != null || item.getExpression() != null) {
        return false;
      }
    }
    return true;
  }

  /**
   * Executes a simple count query, if possible. This method is provided to allow subclasses to
   * optimize count queries since they are quite common and often a datastore can retrieve the count
   * using some specialized means which is much more performant than counting all records manually.
   *
   * @param table the table on which the count is requested.
   * @param whereItems a (sometimes empty) list of WHERE items.
   * @param functionApproximationAllowed whether approximation is allowed or not.
   * @return the count of the particular table, or null if not available.
   */
  protected Number executeCountQuery(Table table, List<FilterItem> whereItems,
      boolean functionApproximationAllowed) {
    return null;
  }

  /**
   * Executes a query which obtains a row by primary key (as defined by
   * {@link Column#isPrimaryKey()}). This method is provided to allow subclasses to optimize lookup
   * queries since they are quite common and often a datastore can retrieve the row using some
   * specialized means which is much more performant than scanning all records manually.
   *
   * @param table the table on which the lookup is requested.
   * @param selectItems the items to select from the lookup query.
   * @param primaryKeyColumn the column that is the primary key
   * @param keyValue the primary key value that is specified in the lookup query.
   * @return the row if the particular table, or null if not available.
   */
  protected Row executePrimaryKeyLookupQuery(Table table, List<SelectItem> selectItems,
      Column primaryKeyColumn, Object keyValue) {
    return null;
  }

  protected DataSet materializeFromItem(final FromItem fromItem, final List<SelectItem> selectItems) {
    DataSet dataSet;
    JoinType joinType = fromItem.getJoin();
    if (fromItem.getTable() != null) {
      // We need to materialize a single table
      final Table table = fromItem.getTable();
      final List<SelectItem> selectItemsToMaterialize = new ArrayList<SelectItem>();

      for (final SelectItem selectItem : selectItems) {
        final FromItem selectedFromItem = selectItem.getFromItem();
        // 包含表名 person.name
        if (selectedFromItem != null) {
          if (selectedFromItem.equals(fromItem)) {
            selectItemsToMaterialize.add(selectItem.replaceFunction(null));
          }
        } else {
          // 只有列名 name，而且该列 属于 该表
          // the select item does not specify a specific
          // from-item
          final Column selectedColumn = selectItem.getColumn();
          if (selectedColumn != null) {
            // we assume that if the table matches, we will use the
            // column
            if (selectedColumn.getTable() != null && selectedColumn.getTable().equals(table)) {
              selectItemsToMaterialize.add(selectItem.replaceFunction(null));
            }
          }
        }
      }

      if (logger.isDebugEnabled()) {
        logger.debug("calling materializeTable(" + table.getName() + "," + selectItemsToMaterialize
            + ",1,-1");
      }

      // Dispatching to the concrete subclass of
      // QueryPostprocessDataContextStrategy
      dataSet = materializeTable(table, selectItemsToMaterialize, 1, -1);

    } else if (joinType != null) {
      // We need to (recursively) materialize a joined FromItem
      if (fromItem.getLeftSide() == null || fromItem.getRightSide() == null) {
        throw new IllegalArgumentException("Joined FromItem requires both left and right side: "
            + fromItem);
      }
      final DataSet[] fromItemDataSets = new DataSet[2];

      // materialize left side
      final List<SelectItem> leftOn = Arrays.asList(fromItem.getLeftOn());
      fromItemDataSets[0] =
          materializeFromItem(fromItem.getLeftSide(),
              CollectionUtils.concat(true, selectItems, leftOn));

      // materialize right side
      final List<SelectItem> rightOn = Arrays.asList(fromItem.getRightOn());
      fromItemDataSets[1] =
          materializeFromItem(fromItem.getRightSide(),
              CollectionUtils.concat(true, selectItems, rightOn));

      final FilterItem[] onConditions = new FilterItem[leftOn.size()];
      for (int i = 0; i < onConditions.length; i++) {
        final FilterItem whereItem =
            new FilterItem(leftOn.get(i), OperatorType.EQUALS_TO, rightOn.get(i));
        onConditions[i] = whereItem;
      }

      switch (joinType) {
        case INNER:
          dataSet = MetaModelHelper.getCarthesianProduct(fromItemDataSets, onConditions);
          break;
        case LEFT:
          dataSet =
              MetaModelHelper.getLeftJoin(fromItemDataSets[0], fromItemDataSets[1], onConditions);
          break;
        case RIGHT:
          dataSet =
              MetaModelHelper.getRightJoin(fromItemDataSets[0], fromItemDataSets[1], onConditions);
          break;
        default:
          throw new IllegalArgumentException("FromItem type not supported: " + fromItem);
      }
    } else if (fromItem.getSubQuery() != null) {
      // We need to (recursively) materialize a subquery
      dataSet = executeQuery(fromItem.getSubQuery());
    } else {
      throw new IllegalArgumentException("FromItem type not supported: " + fromItem);
    }
    if (dataSet == null) {
      throw new IllegalStateException("FromItem was not succesfully materialized: " + fromItem);
    }
    return dataSet;
  }

  protected DataSet materializeTable(final Table table, final List<SelectItem> selectItems,
      final List<FilterItem> whereItems, final int firstRow, final int maxRows) {
    if (table == null) {
      throw new IllegalArgumentException("Table cannot be null");
    }

    if (selectItems == null || selectItems.isEmpty()) {
      // add any column (typically this occurs because of COUNT(*)
      // queries)
      Column[] columns = table.getColumns();
      if (columns.length == 0) {
        logger.warn("Queried table has no columns: {}", table);
      } else {
        selectItems.add(new SelectItem(columns[0]));
      }
    }

    final Schema schema = table.getSchema();
    final String schemaName;
    if (schema == null) {
      schemaName = null;
    } else {
      schemaName = schema.getName();
    }

    final DataSet dataSet;
    // schema包括两类：information_schema，用户自定义的schema
    if (INFORMATION_SCHEMA_NAME.equals(schemaName)) {
      DataSet informationDataSet =
          materializeInformationSchemaTable(table, buildWorkingSelectItems(selectItems, whereItems));
      informationDataSet = MetaModelHelper.getFiltered(informationDataSet, whereItems);
      informationDataSet = MetaModelHelper.getSelection(selectItems, informationDataSet);
      informationDataSet = MetaModelHelper.getPaged(informationDataSet, firstRow, maxRows);
      dataSet = informationDataSet;
    } else {
      final DataSet tableDataSet =
          materializeMainSchemaTable(table, selectItems, whereItems, firstRow, maxRows);

      // conversion is done at materialization time, since it enables
      // the refined types to be used also in eg. where clauses.
      // 对返回的结果进行 数据类型的转换
      dataSet = new ConvertedDataSetInterceptor(_converters).intercept(tableDataSet);
    }

    return dataSet;
  }

  private List<SelectItem> buildWorkingSelectItems(List<SelectItem> selectItems,
      List<FilterItem> whereItems) {
    final List<SelectItem> primarySelectItems = new ArrayList<>(selectItems.size());
    for (SelectItem selectItem : selectItems) {
      final ScalarFunction scalarFunction = selectItem.getScalarFunction();
      if (scalarFunction == null || isScalarFunctionMaterialized(scalarFunction)) {
        primarySelectItems.add(selectItem);
      } else {
        final SelectItem copySelectItem = selectItem.replaceFunction(null);
        primarySelectItems.add(copySelectItem);
      }
    }
    final List<SelectItem> evaluatedSelectItems =
        MetaModelHelper.getEvaluatedSelectItems(whereItems);
    return CollectionUtils.concat(true, primarySelectItems, evaluatedSelectItems);
  }

  /**
   * Determines if the subclass of this class can materialize {@link SelectItem}s with the given
   * {@link ScalarFunction}. Usually scalar functions are applied by MetaModel on the client side,
   * but when possible they can also be handled by e.g.
   * {@link #materializeMainSchemaTable(Table, List, int, int)} and
   * {@link #materializeMainSchemaTable(Table, List, List, int, int)} in which case MetaModel will
   * not evaluate it client-side.
   *
   * @param function
   * @return
   */
  protected boolean isScalarFunctionMaterialized(ScalarFunction function) {
    return false;
  }

  @Deprecated
  protected DataSet materializeTable(final Table table, final List<SelectItem> selectItems,
      final int firstRow, final int maxRows) {
    return materializeTable(table, selectItems, Collections.<FilterItem>emptyList(), firstRow,
        maxRows);
  }

  protected boolean isMainSchemaTable(Table table) {
    Schema schema = table.getSchema();
    if (INFORMATION_SCHEMA_NAME.equals(schema.getName())) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  protected final String[] getSchemaNamesInternal() throws MetaModelException {
    final String[] schemaNames = new String[2];
    schemaNames[0] = INFORMATION_SCHEMA_NAME;
    schemaNames[1] = getMainSchemaName();
    return schemaNames;
  }

  @Override
  protected String getDefaultSchemaName() throws MetaModelException {
    return getMainSchemaName();
  }

  @Override
  protected final Schema getSchemaByNameInternal(final String name) throws MetaModelException {
    final String mainSchemaName = getMainSchemaName();
    if (name == null) {
      if (mainSchemaName == null) {
        return getMainSchema();
      }
      return null;
    }

    if (name.equalsIgnoreCase(mainSchemaName)) {
      return getMainSchema();
    } else if (name.equals(INFORMATION_SCHEMA_NAME)) {
      return getInformationSchema();
    }

    logger.warn(
        "Could not find matching schema of name '{}'. Main schema name is: '{}'. Returning null.",
        name, mainSchemaName);
    return null;
  }

  private Schema getInformationSchema() {
    // Create schema
    MutableSchema informationSchema = new MutableSchema(INFORMATION_SCHEMA_NAME);
    MutableTable tablesTable = new MutableTable("tables", TableType.TABLE, informationSchema);
    MutableTable columnsTable = new MutableTable("columns", TableType.TABLE, informationSchema);
    MutableTable relationshipsTable =
        new MutableTable("relationships", TableType.TABLE, informationSchema);
    informationSchema.addTable(tablesTable).addTable(columnsTable).addTable(relationshipsTable);

    // Create "tables" table: name, type, num_columns, remarks
    tablesTable.addColumn(new MutableColumn("name", ColumnType.VARCHAR, tablesTable, 0, false));
    tablesTable.addColumn(new MutableColumn("type", ColumnType.VARCHAR, tablesTable, 1, true));
    tablesTable
        .addColumn(new MutableColumn("num_columns", ColumnType.INTEGER, tablesTable, 2, true));
    tablesTable.addColumn(new MutableColumn("remarks", ColumnType.VARCHAR, tablesTable, 3, true));

    // Create "columns" table: name, type, native_type, size, nullable,
    // indexed, table, remarks
    columnsTable.addColumn(new MutableColumn("name", ColumnType.VARCHAR, columnsTable, 0, false));
    columnsTable.addColumn(new MutableColumn("type", ColumnType.VARCHAR, columnsTable, 1, true));
    columnsTable.addColumn(new MutableColumn("native_type", ColumnType.VARCHAR, columnsTable, 2,
        true));
    columnsTable.addColumn(new MutableColumn("size", ColumnType.INTEGER, columnsTable, 3, true));
    columnsTable
        .addColumn(new MutableColumn("nullable", ColumnType.BOOLEAN, columnsTable, 4, true));
    columnsTable.addColumn(new MutableColumn("indexed", ColumnType.BOOLEAN, columnsTable, 5, true));
    columnsTable.addColumn(new MutableColumn("table", ColumnType.VARCHAR, columnsTable, 6, false));
    columnsTable.addColumn(new MutableColumn("remarks", ColumnType.VARCHAR, columnsTable, 7, true));

    // Create "relationships" table: primary_table, primary_column,
    // foreign_table, foreign_column
    relationshipsTable.addColumn(new MutableColumn("primary_table", ColumnType.VARCHAR,
        relationshipsTable, 0, false));
    relationshipsTable.addColumn(new MutableColumn("primary_column", ColumnType.VARCHAR,
        relationshipsTable, 1, false));
    relationshipsTable.addColumn(new MutableColumn("foreign_table", ColumnType.VARCHAR,
        relationshipsTable, 2, false));
    relationshipsTable.addColumn(new MutableColumn("foreign_column", ColumnType.VARCHAR,
        relationshipsTable, 3, false));

    MutableRelationship.createRelationship(tablesTable.getColumnByName("name"),
        columnsTable.getColumnByName("table"));
    MutableRelationship.createRelationship(tablesTable.getColumnByName("name"),
        relationshipsTable.getColumnByName("primary_table"));
    MutableRelationship.createRelationship(tablesTable.getColumnByName("name"),
        relationshipsTable.getColumnByName("foreign_table"));
    MutableRelationship.createRelationship(columnsTable.getColumnByName("name"),
        relationshipsTable.getColumnByName("primary_column"));
    MutableRelationship.createRelationship(columnsTable.getColumnByName("name"),
        relationshipsTable.getColumnByName("foreign_column"));

    return informationSchema;
  }

  private DataSet materializeInformationSchemaTable(final Table table,
      final List<SelectItem> selectItems) {
    final String tableName = table.getName();
    final SelectItem[] columnSelectItems = MetaModelHelper.createSelectItems(table.getColumns());
    final SimpleDataSetHeader header = new SimpleDataSetHeader(columnSelectItems);
    final Table[] tables = getDefaultSchema().getTables();
    final List<Row> data = new ArrayList<Row>();
    if ("tables".equals(tableName)) {
      // "tables" columns: name, type, num_columns, remarks
      for (Table t : tables) {
        String typeString = null;
        if (t.getType() != null) {
          typeString = t.getType().toString();
        }
        data.add(new DefaultRow(header, new Object[] {t.getName(), typeString, t.getColumnCount(),
            t.getRemarks()}));
      }
    } else if ("columns".equals(tableName)) {
      // "columns" columns: name, type, native_type, size, nullable,
      // indexed, table, remarks
      for (Table t : tables) {
        for (Column c : t.getColumns()) {
          String typeString = null;
          if (t.getType() != null) {
            typeString = c.getType().toString();
          }
          data.add(new DefaultRow(header, new Object[] {c.getName(), typeString, c.getNativeType(),
              c.getColumnSize(), c.isNullable(), c.isIndexed(), t.getName(), c.getRemarks()}));
        }
      }
    } else if ("relationships".equals(tableName)) {
      // "relationships" columns: primary_table, primary_column,
      // foreign_table, foreign_column
      for (Relationship r : getDefaultSchema().getRelationships()) {
        Column[] primaryColumns = r.getPrimaryColumns();
        Column[] foreignColumns = r.getForeignColumns();
        Table pTable = r.getPrimaryTable();
        Table fTable = r.getForeignTable();
        for (int i = 0; i < primaryColumns.length; i++) {
          Column pColumn = primaryColumns[i];
          Column fColumn = foreignColumns[i];
          data.add(new DefaultRow(header, new Object[] {pTable.getName(), pColumn.getName(),
              fTable.getName(), fColumn.getName()}));
        }
      }
    } else {
      throw new IllegalArgumentException("Cannot materialize non information_schema table: "
          + table);
    }

    DataSet dataSet;
    if (data.isEmpty()) {
      dataSet = new EmptyDataSet(selectItems);
    } else {
      dataSet = new InMemoryDataSet(header, data);
    }

    // Handle column subset
    final DataSet selectionDataSet = MetaModelHelper.getSelection(selectItems, dataSet);
    dataSet = selectionDataSet;

    return dataSet;
  }

  /**
   * @return
   * @deprecated use {@link #getDefaultSchema()} instead
   */
  @Deprecated
  protected Schema getMainSchemaInternal() {
    return getDefaultSchema();
  }

  /**
   * Adds a {@link TypeConverter} to this DataContext's query engine (Query Postprocessor) for read
   * operations. Note that this method should NOT be invoked directly by consuming code. Rather use
   * {@link Converters#addTypeConverter(DataContext, Column, TypeConverter)} to ensure conversion on
   * both reads and writes.
   */
  @Override
  public void addConverter(Column column, TypeConverter<?, ?> converter) {
    _converters.put(column, converter);
  }

  /**
   * @return the main schema that subclasses of this class produce
   */
  protected abstract Schema getMainSchema() throws MetaModelException;

  /**
   * @return the name of the main schema that subclasses of this class produce
   */
  protected abstract String getMainSchemaName() throws MetaModelException;

  /**
   * Execute a simple one-table query against a table in the main schema of the subclasses of this
   * class. This default implementation will delegate to
   * {@link #materializeMainSchemaTable(Table, List, int, int)} and apply WHERE item filtering
   * afterwards.
   *
   * @param table
   * @param selectItems
   * @param whereItems
   * @param firstRow
   * @param maxRows
   * @return
   */
  protected DataSet materializeMainSchemaTable(Table table, List<SelectItem> selectItems,
      List<FilterItem> whereItems, int firstRow, int maxRows) {
    final List<SelectItem> workingSelectItems = buildWorkingSelectItems(selectItems, whereItems);
    DataSet dataSet;
    if (whereItems.isEmpty()) {// 不包含 where,直接让 具体的DataContext的 materializeMainSchemaTable 来管理分页
      // paging is pushed down to materializeMainSchemaTable
      dataSet = materializeMainSchemaTable(table, workingSelectItems, firstRow, maxRows);
      dataSet = MetaModelHelper.getSelection(selectItems, dataSet);
    } else {// 包含 where时，具体的DataContext的 materializeMainSchemaTable 不管理分页，直接将全部结果进行返回，然后再进行过滤、分页、和投影
      // do not push down paging, first we have to apply filtering
      dataSet = materializeMainSchemaTable(table, workingSelectItems, 1, -1);
      dataSet = MetaModelHelper.getFiltered(dataSet, whereItems);
      dataSet = MetaModelHelper.getPaged(dataSet, firstRow, maxRows);
      dataSet = MetaModelHelper.getSelection(selectItems, dataSet);
    }
    return dataSet;
  }

  /**
   * Executes a simple one-table query against a table in the main schema of the subclasses of this
   * class. This default implementation will delegate to
   * {@link #materializeMainSchemaTable(Table, Column[], int, int)}.
   *
   * @param table
   * @param selectItems
   * @param firstRow
   * @param maxRows
   * @return
   */
  protected DataSet materializeMainSchemaTable(Table table, List<SelectItem> selectItems,
      int firstRow, int maxRows) {
    Column[] columns = new Column[selectItems.size()];
    for (int i = 0; i < columns.length; i++) {
      columns[i] = selectItems.get(i).getColumn();
    }
    DataSet dataSet = materializeMainSchemaTable(table, columns, firstRow, maxRows);

    dataSet = MetaModelHelper.getSelection(selectItems, dataSet);

    return dataSet;
  }

  /**
   * Executes a simple one-table query against a table in the main schema of the subclasses of this
   * class. This default implementation will delegate to
   * {@link #materializeMainSchemaTable(Table, Column[], int)} and apply a {@link FirstRowDataSet}
   * if necessary.
   *
   * @param table
   * @param columns
   * @param firstRow
   * @param maxRows
   * @return
   */
  protected DataSet materializeMainSchemaTable(Table table, Column[] columns, int firstRow,
      int maxRows) {
    final int rowsToMaterialize;
    if (firstRow == 1) {
      rowsToMaterialize = maxRows;
    } else {
      rowsToMaterialize = maxRows + (firstRow - 1);
    }
    DataSet dataSet = materializeMainSchemaTable(table, columns, rowsToMaterialize);
    if (firstRow > 1) {
      dataSet = new FirstRowDataSet(dataSet, firstRow);
    }
    return dataSet;
  }

  /**
   * Executes a simple one-table query against a table in the main schema of the subclasses of this
   * class.
   *
   * @param table the table to query
   * @param columns the columns of the table to query
   * @param maxRows the maximum amount of rows needed or -1 if all rows are wanted.
   * @return a dataset with the raw table/column content.
   */
  protected abstract DataSet materializeMainSchemaTable(Table table, Column[] columns, int maxRows);
}
