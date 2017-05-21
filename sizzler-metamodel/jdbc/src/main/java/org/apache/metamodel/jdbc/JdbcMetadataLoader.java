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
package org.apache.metamodel.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.metamodel.MetaModelException;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.MutableColumn;
import org.apache.metamodel.schema.MutableRelationship;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.schema.TableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link MetadataLoader} for JDBC metadata loading.
 */
final class JdbcMetadataLoader implements MetadataLoader {

  private static final Logger logger = LoggerFactory.getLogger(JdbcMetadataLoader.class);

  private final JdbcDataContext _dataContext;
  private final boolean _usesCatalogsAsSchemas;
  private final String _identifierQuoteString;

  // these three sets contains the system identifies of whether specific items
  // have been loaded for tables/schemas. Using system identities avoid having
  // to call equals(...) method etc. while doing lazy loading of these items.
  // Invoking equals(...) would be prone to stack overflows ...
  private final Set<Integer> _loadedRelations;
  private final Set<Integer> _loadedColumns;
  private final Set<Integer> _loadedIndexes;
  private final Set<Integer> _loadedPrimaryKeys;

  public JdbcMetadataLoader(JdbcDataContext dataContext, boolean usesCatalogsAsSchemas,
      String identifierQuoteString) {
    _dataContext = dataContext;
    _usesCatalogsAsSchemas = usesCatalogsAsSchemas;
    _identifierQuoteString = identifierQuoteString;
    _loadedRelations = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
    _loadedColumns = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
    _loadedIndexes = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
    _loadedPrimaryKeys = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
  }

  @Override
  public void loadTables(JdbcSchema schema) {
    final Connection connection = _dataContext.getConnection();
    try {
      loadTables(schema, connection);
    } finally {
      _dataContext.close(connection, null, null);
    }
  }

  @Override
  public void loadTables(JdbcSchema schema, Connection connection) {
    try {
      final DatabaseMetaData metaData = connection.getMetaData();

      // Creates string array to represent the table types
      final String[] types = JdbcUtils.getTableTypesAsStrings(_dataContext.getTableTypes());
      loadTables(schema, metaData, types);
    } catch (SQLException e) {
      throw JdbcUtils.wrapException(e, "retrieve table metadata for " + schema.getName());
    }
  }

  private void loadTables(JdbcSchema schema, DatabaseMetaData metaData, String[] types) {
    String catalogName = _dataContext.getCatalogName();

    ResultSet rs = null;
    try {
      if (logger.isDebugEnabled()) {
        logger.debug("Querying for table types " + Arrays.toString(types) + " in catalog: "
            + catalogName + ", schema: " + schema.getName());
      }
      if (_usesCatalogsAsSchemas) {
        rs = metaData.getTables(schema.getName(), null, null, types);
      } else {
        rs = metaData.getTables(catalogName, schema.getName(), null, types);
      }
      schema.clearTables();
      int tableNumber = -1;
      while (rs.next()) {
        tableNumber++;
        String tableCatalog = rs.getString(1);
        String tableSchema = rs.getString(2);
        String tableName = rs.getString(3);
        String tableTypeName = rs.getString(4);
        TableType tableType = TableType.getTableType(tableTypeName);
        String tableRemarks = rs.getString(5);

        if (logger.isDebugEnabled()) {
          logger.debug("Found table: tableCatalog=" + tableCatalog + ",tableSchema=" + tableSchema
              + ",tableName=" + tableName);
        }

        if (tableSchema == null) {
          tableSchema = tableCatalog;
        }

        JdbcTable table = new JdbcTable(tableName, tableType, schema, this);
        table.setRemarks(tableRemarks);
        table.setQuote(_identifierQuoteString);
        schema.addTable(table);
      }

      final int tablesReturned = tableNumber + 1;
      if (tablesReturned == 0) {
        logger.info("No table metadata records returned for schema '{}'", schema.getName());
      } else {
        logger.debug("Returned {} table metadata records for schema '{}'", new Object[] {
            tablesReturned, schema.getName()});
      }

    } catch (SQLException e) {
      throw JdbcUtils.wrapException(e, "retrieve table metadata for " + schema.getName());
    } finally {
      _dataContext.close(null, rs, null);
    }
  }

  @Override
  public void loadIndexes(JdbcTable jdbcTable) {
    final Connection connection = _dataContext.getConnection();
    try {
      loadIndexes(jdbcTable, connection);
    } finally {
      _dataContext.close(connection, null, null);
    }
  }

  @Override
  public void loadIndexes(JdbcTable table, Connection connection) {
    final int identity = System.identityHashCode(table);
    if (_loadedIndexes.contains(identity)) {
      return;
    }
    synchronized (this) {
      if (_loadedIndexes.contains(identity)) {
        return;
      }

      try {
        DatabaseMetaData metaData = connection.getMetaData();
        loadIndexes(table, metaData);
        _loadedIndexes.add(identity);
      } catch (SQLException e) {
        throw JdbcUtils.wrapException(e, "load indexes");
      }
    }
  }

  @Override
  public void loadPrimaryKeys(JdbcTable jdbcTable) {
    final Connection connection = _dataContext.getConnection();
    try {
      loadPrimaryKeys(jdbcTable, connection);
    } finally {
      _dataContext.close(connection, null, null);
    }
  }

  @Override
  public void loadPrimaryKeys(JdbcTable table, Connection connection) {
    final int identity = System.identityHashCode(table);
    if (_loadedPrimaryKeys.contains(identity)) {
      return;
    }
    synchronized (this) {
      if (_loadedPrimaryKeys.contains(identity)) {
        return;
      }
      try {
        DatabaseMetaData metaData = connection.getMetaData();
        loadPrimaryKeys(table, metaData);
        _loadedPrimaryKeys.add(identity);
      } catch (SQLException e) {
        throw JdbcUtils.wrapException(e, "load primary keys");
      }
    }
  }

  private void loadPrimaryKeys(JdbcTable table, DatabaseMetaData metaData)
      throws MetaModelException {
    Schema schema = table.getSchema();
    ResultSet rs = null;

    try {
      if (_usesCatalogsAsSchemas) {
        rs = metaData.getPrimaryKeys(schema.getName(), null, table.getName());
      } else {
        rs =
            metaData.getPrimaryKeys(_dataContext.getCatalogName(), schema.getName(),
                table.getName());
      }
      while (rs.next()) {
        String columnName = rs.getString(4);
        if (columnName != null) {
          MutableColumn column = (MutableColumn) table.getColumnByName(columnName);
          if (column != null) {
            column.setPrimaryKey(true);
          } else {
            logger
                .error("Indexed column \"{}\" could not be found in table: {}", columnName, table);
          }
        }
      }
    } catch (SQLException e) {
      throw JdbcUtils.wrapException(e, "retrieve primary keys for " + table.getName());
    } finally {
      _dataContext.close(null, rs, null);
    }
  }

  private void loadIndexes(Table table, DatabaseMetaData metaData) throws MetaModelException {
    Schema schema = table.getSchema();
    ResultSet rs = null;
    // Ticket #170: IndexInfo is nice-to-have, not need-to-have, so
    // we will do a nice failover on SQLExceptions
    try {
      if (_usesCatalogsAsSchemas) {
        rs = metaData.getIndexInfo(schema.getName(), null, table.getName(), false, true);
      } else {
        rs =
            metaData.getIndexInfo(_dataContext.getCatalogName(), schema.getName(), table.getName(),
                false, true);
      }
      while (rs.next()) {
        String columnName = rs.getString(9);
        if (columnName != null) {
          MutableColumn column = (MutableColumn) table.getColumnByName(columnName);
          if (column != null) {
            column.setIndexed(true);
          } else {
            logger
                .error("Indexed column \"{}\" could not be found in table: {}", columnName, table);
          }
        }
      }
    } catch (SQLException e) {
      throw JdbcUtils.wrapException(e, "retrieve index information for " + table.getName());
    } finally {
      _dataContext.close(null, rs, null);
    }
  }

  @Override
  public void loadColumns(JdbcTable jdbcTable) {
    final Connection connection = _dataContext.getConnection();
    try {
      loadColumns(jdbcTable, connection);
    } finally {
      _dataContext.close(connection, null, null);
    }
  }

  /**
   * Loads column metadata (no indexes though) for a table
   * 
   * @param table
   */
  @Override
  public void loadColumns(JdbcTable table, Connection connection) {
    final int identity = System.identityHashCode(table);
    if (_loadedColumns.contains(identity)) {
      return;
    }
    synchronized (this) {
      if (_loadedColumns.contains(identity)) {
        return;
      }

      try {
        DatabaseMetaData metaData = connection.getMetaData();
        loadColumns(table, metaData);
        _loadedColumns.add(identity);
      } catch (Exception e) {
        logger.error("Could not load columns for table: " + table, e);
      }
    }
  }

  private boolean isLobConversionEnabled() {
    final String systemProperty = System.getProperty(JdbcDataContext.SYSTEM_PROPERTY_CONVERT_LOBS);
    return "true".equals(systemProperty);
  }

  private void loadColumns(JdbcTable table, DatabaseMetaData metaData) {
    final boolean convertLobs = isLobConversionEnabled();
    final Schema schema = table.getSchema();
    ResultSet rs = null;
    try {
      if (logger.isDebugEnabled()) {
        logger.debug("Querying for columns in table: " + table.getName());
      }
      int columnNumber = -1;
      if (_usesCatalogsAsSchemas) {
        rs = metaData.getColumns(schema.getName(), null, table.getName(), null);
      } else {
        rs =
            metaData.getColumns(_dataContext.getCatalogName(), schema.getName(), table.getName(),
                null);
      }
      while (rs.next()) {
        columnNumber++;
        final String columnName = rs.getString(4);
        if (_identifierQuoteString == null && new StringTokenizer(columnName).countTokens() > 1) {
          logger.warn("column name contains whitespace: \"" + columnName + "\".");
        }

        final int jdbcType = rs.getInt(5);
        final String nativeType = rs.getString(6);
        final Integer columnSize = rs.getInt(7);

        if (logger.isDebugEnabled()) {
          logger.debug("Found column: table=" + table.getName() + ",columnName=" + columnName
              + ",nativeType=" + nativeType + ",columnSize=" + columnSize);
        }

        ColumnType columnType =
            _dataContext.getQueryRewriter().getColumnType(jdbcType, nativeType, columnSize);
        if (convertLobs) {
          if (columnType == ColumnType.CLOB || columnType == ColumnType.NCLOB) {
            columnType = JdbcDataContext.COLUMN_TYPE_CLOB_AS_STRING;
          } else if (columnType == ColumnType.BLOB) {
            columnType = JdbcDataContext.COLUMN_TYPE_BLOB_AS_BYTES;
          }
        }

        final int jdbcNullable = rs.getInt(11);
        final Boolean nullable;
        if (jdbcNullable == DatabaseMetaData.columnNullable) {
          nullable = true;
        } else if (jdbcNullable == DatabaseMetaData.columnNoNulls) {
          nullable = false;
        } else {
          nullable = null;
        }

        final String remarks = rs.getString(12);

        final JdbcColumn column =
            new JdbcColumn(columnName, columnType, table, columnNumber, nullable);
        column.setRemarks(remarks);
        column.setNativeType(nativeType);
        column.setColumnSize(columnSize);
        column.setQuote(_identifierQuoteString);
        table.addColumn(column);
      }

      final int columnsReturned = columnNumber + 1;
      if (columnsReturned == 0) {
        logger.info("No column metadata records returned for table '{}' in schema '{}'",
            table.getName(), schema.getName());
      } else {
        logger.debug("Returned {} column metadata records for table '{}' in schema '{}'",
            new Object[] {columnsReturned, table.getName(), schema.getName()});
      }

    } catch (SQLException e) {
      throw JdbcUtils.wrapException(e, "retrieve table metadata for " + table.getName());
    } finally {
      _dataContext.close(null, rs, null);
    }
  }

  @Override
  public void loadRelations(JdbcSchema jdbcSchema) {
    final Connection connection = _dataContext.getConnection();
    try {
      loadRelations(jdbcSchema, connection);
    } finally {
      _dataContext.close(connection, null, null);
    }
  }

  @Override
  public void loadRelations(JdbcSchema schema, Connection connection) {
    final int identity = System.identityHashCode(schema);
    if (_loadedRelations.contains(identity)) {
      return;
    }
    synchronized (this) {
      if (_loadedRelations.contains(identity)) {
        return;
      }
      try {
        final Table[] tables = schema.getTables();
        final DatabaseMetaData metaData = connection.getMetaData();
        for (Table table : tables) {
          loadRelations(table, metaData);
        }
        _loadedRelations.add(identity);
      } catch (Exception e) {
        logger.error("Could not load relations for schema: " + schema, e);
      }
    }
  }

  private void loadRelations(Table table, DatabaseMetaData metaData) {
    Schema schema = table.getSchema();
    ResultSet rs = null;
    try {
      if (_usesCatalogsAsSchemas) {
        rs = metaData.getImportedKeys(schema.getName(), null, table.getName());
      } else {
        rs =
            metaData.getImportedKeys(_dataContext.getCatalogName(), schema.getName(),
                table.getName());
      }
      loadRelations(rs, schema);
    } catch (SQLException e) {
      throw JdbcUtils.wrapException(e, "retrieve imported keys for " + table.getName());
    } finally {
      _dataContext.close(null, rs, null);
    }
  }

  private void loadRelations(ResultSet rs, Schema schema) throws SQLException {
    while (rs.next()) {
      String pkTableName = rs.getString(3);
      String pkColumnName = rs.getString(4);

      Column pkColumn = null;
      Table pkTable = schema.getTableByName(pkTableName);
      if (pkTable != null) {
        pkColumn = pkTable.getColumnByName(pkColumnName);
      }
      if (logger.isDebugEnabled()) {
        logger.debug("Found primary key relation: tableName=" + pkTableName + ",columnName="
            + pkColumnName + ", matching column: " + pkColumn);
      }

      String fkTableName = rs.getString(7);
      String fkColumnName = rs.getString(8);
      Column fkColumn = null;
      Table fkTable = schema.getTableByName(fkTableName);
      if (fkTable != null) {
        fkColumn = fkTable.getColumnByName(fkColumnName);
      }
      if (logger.isDebugEnabled()) {
        logger.debug("Found foreign key relation: tableName=" + fkTableName + ",columnName="
            + fkColumnName + ", matching column: " + fkColumn);
      }

      if (pkColumn == null || fkColumn == null) {
        logger
            .error(
                "Could not find relation columns: pkTableName={},pkColumnName={},fkTableName={},fkColumnName={}",
                new Object[] {pkTableName, pkColumnName, fkTableName, fkColumnName});
        logger.error("pkColumn={}", pkColumn);
        logger.error("fkColumn={}", fkColumn);
      } else {
        MutableRelationship.createRelationship(new Column[] {pkColumn}, new Column[] {fkColumn});
      }
    }
  }

}
