package com.sizzler.provider.common.query;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.metamodel.MetaModelHelper;
import org.apache.metamodel.data.CachingDataSetHeader;
import org.apache.metamodel.data.DataSetHeader;
import org.apache.metamodel.data.DefaultRow;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.ColumnTypeImpl;
import org.apache.metamodel.schema.MutableColumn;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.util.FileHelper;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.provider.common.db.DataBaseType;

/**
 * Created by ptmind on 2015/11/11.
 *
 * Catalog和Schema的区别 参考：http://blog.sina.com.cn/s/blog_707a9f0601014y1a.html
 *
 * schema--对应着数据库的用户登录名（比如mysql的root用户，oracle的用户名 必须是 大写的）
 */
public class DataBaseService {

  private static final Logger log = Logger.getLogger(DataBaseService.class);

  @SuppressWarnings("resource")
  public static List<MutableSchema> getSchemaList(String dsCode, Connection connection,
      String databaseName, boolean widthTable, boolean widthColumn) throws Exception {
    // connection.getCatalog();
    ResultSet databaseRs = null;
    List<MutableSchema> schemaList = new ArrayList<>();
    try {
      DatabaseMetaData databaseMetaData = getDatabaseMetaData(connection);
      // databaseMetaData.getSchemas();
      // 对postgre进行特殊处理
      // 首先 判断 databaseMetaData.getSchemas()是否为空，如果不为空 则首先按照 databaseMetaData.getSchemas()来创建
      // MutableSchema；
      // 否则 按照 databaseMetaData.getCatalogs()来创建 MutableSchema
      databaseRs = databaseMetaData.getSchemas();
      boolean hasSchemas = false;
      while (databaseRs.next()) {
        hasSchemas = true;
        String schemaName = databaseRs.getString(1); // "TABLE_SCHEM"
        // 内置的schema不进行返回
        if (!DataBaseBuildinSchemaCache.isBuildinSchema(dsCode, schemaName)) {
          getSchemaList(dsCode, schemaName, databaseMetaData, schemaList, widthTable, widthColumn);
        }
      }
      // 如果getSchemas()不为空
      if (!hasSchemas) {
        // 指定了数据库，则只查询该数据库下面的表结构信息
        /*
         * if(!connection.getCatalog().equalsIgnoreCase("")) { String schemaName =
         * connection.getCatalog(); MutableSchema schema = new MutableSchema(schemaName);
         * List<MutableTable> tableList = new ArrayList<MutableTable>(); if (widthTable) { tableList
         * = getTableList(dsCode, schema, databaseMetaData, widthColumn); }
         * schema.setTables(tableList); schemaList.add(schema); } else {
         */
        databaseRs = databaseMetaData.getCatalogs();
        if (StringUtil.isNotBlank(databaseName)) {
          boolean isContainDatabase = false;
          while (databaseRs.next()) {
            // 数据库名称
            String schemaName = databaseRs.getString("TABLE_CAT");

            if (databaseName.equalsIgnoreCase(schemaName)) {
              isContainDatabase = true;
              getSchemaList(dsCode, schemaName, databaseMetaData, schemaList, widthTable,
                  widthColumn);
            }

          }
          // 如果用户指定的数据库，需要判断是否有权限（返回的数据库列表包含用户指定的数据库）。
          if (!isContainDatabase) {
            throw new ServiceException("exception no auth!");
          }
        } else {
          while (databaseRs.next()) {
            // 数据库名称
            String schemaName = databaseRs.getString("TABLE_CAT");

            if (!DataBaseBuildinSchemaCache.isBuildinSchema(dsCode, schemaName)) {
              getSchemaList(dsCode, schemaName, databaseMetaData, schemaList, widthTable,
                  widthColumn);
            }

          }
        }
      }
    } finally {
      FileHelper.safeClose(databaseRs);
    }
    /*
     * if(dsCode.equalsIgnoreCase("postgre")) { dataBaseRs=databaseMetaData.getSchemas(); while
     * (dataBaseRs.next()) { String schemaName = dataBaseRs.getString(1); // "TABLE_SCHEM" String
     * tableCatalog = dataBaseRs.getString(2); //"TABLE_CATALOG" //内置的schema不进行返回
     * if(!DataBaseBuildinSchemaCache.isBuildinSchema(dsCode,schemaName)) { MutableSchema schema =
     * new MutableSchema(schemaName); List<MutableTable> tableList = new ArrayList<MutableTable>();
     * if(widthTable){ tableList = getTableList(schema, databaseMetaData, widthColumn); }
     * schema.setTables(tableList); schemaList.add(schema); }
     * 
     * } }else { //指定了数据库，则只查询该数据库下面的表结构信息 if(!connection.getCatalog().equalsIgnoreCase("")) {
     * 
     * String schemaName=connection.getCatalog(); MutableSchema schema=new
     * MutableSchema(schemaName); List<MutableTable> tableList = new ArrayList<MutableTable>();
     * if(widthTable){ tableList = getTableList(schema, databaseMetaData, widthColumn); }
     * schema.setTables(tableList); schemaList.add(schema); }else {
     * 
     * try { dataBaseRs = databaseMetaData.getCatalogs(); while (dataBaseRs.next()) { String
     * schemaName = dataBaseRs.getString("TABLE_CAT");
     * if(!DataBaseBuildinSchemaCache.isBuildinSchema(dsCode,schemaName)) { MutableSchema schema =
     * new MutableSchema(schemaName); List<MutableTable> tableList = new ArrayList<MutableTable>();
     * if(widthTable){ tableList = getTableList(schema, databaseMetaData, widthColumn); }
     * schema.setTables(tableList); schemaList.add(schema); }
     * 
     * }
     * 
     * }finally { FileHelper.safeClose(dataBaseRs); } } }
     */

    return schemaList;
  }

  private static void getSchemaList(String dsCode, String schemaName,
      DatabaseMetaData databaseMetaData, List<MutableSchema> schemaList, boolean widthTable,
      boolean widthColumn) throws Exception {
    MutableSchema schema = new MutableSchema(schemaName);
    List<MutableTable> tableList = new ArrayList<MutableTable>();
    if (widthTable) {
      tableList = getTableList(dsCode, schema, databaseMetaData, widthColumn);
    }
    schema.setTables(tableList);
    schemaList.add(schema);
  }

  public static List<MutableTable> getTableList(String dsCode, MutableSchema schema,
      DatabaseMetaData databaseMetaData, boolean widthColumn) throws Exception {
    List<MutableTable> tableList = new ArrayList<>();
    ResultSet tableRs = null;
    try {
      //对数据库都支持视图
      //String[] tableTypeArray = new String[] {"TABLE"};
      String[] tableTypeArray = new String[] {"TABLE", "VIEW"};
      if (dsCode != null) {
        /*
         * sqlserver新增对视图的支持
         * if (dsCode.equalsIgnoreCase(DataBaseType.SQLSERVER.toString())) {
         * tableTypeArray = new String[] {"TABLE", "VIEW"}; 
         * }
         */
        if (dsCode.equalsIgnoreCase(DataBaseType.POSTGRE.toString())
            || dsCode.equalsIgnoreCase(DataBaseType.SQLSERVER.toString())) {
          tableRs = databaseMetaData.getTables(null, schema.getName(), null, tableTypeArray);
        } else {
          tableRs = databaseMetaData.getTables(schema.getName(), null, null, tableTypeArray);
        }
      }

      while (tableRs.next()) {
        String tableName = tableRs.getString("TABLE_NAME");
        MutableTable table = new MutableTable(tableName);
        // 取得该表下的所有Column，其中Column中包含了 列的类型

        List<Column> columnList = new ArrayList<Column>();

        if (widthColumn) {
          columnList = getColumnList(schema, table, databaseMetaData);
        }
        // 列为空的表不返回
        /*
         * columnList = getColumnList(schema, table, databaseMetaData); if(columnList.size()>0) {
         * table.setSchema(schema); table.setColumns(columnList); tableList.add(table); }
         */

        table.setSchema(schema);
        table.setColumns(columnList);
        tableList.add(table);
      }
    } finally {
      // FileHelper是metamodel提供的工具类
      FileHelper.safeClose(tableRs);
    }

    return tableList;
  }

  public static List<Column> getColumnList(MutableSchema schema, MutableTable table,
      DatabaseMetaData databaseMetaData) throws Exception {
    List<Column> columnList = new ArrayList<>();
    ResultSet columnRs = null;
    try {
      columnRs = databaseMetaData.getColumns(schema.getName(), null, table.getName(), null);
      while (columnRs.next()) {
        MutableColumn column = new MutableColumn();
        String columnName = columnRs.getString("COLUMN_NAME");
        Integer jdbcType = columnRs.getInt("DATA_TYPE");
        column.setName(columnName);
        // 将jdbc的类型转换为metamodel的ColumnType
        ColumnType columnType = ColumnTypeImpl.convertColumnType(jdbcType);
        column.setType(columnType);
        column.setTable(table);
        columnList.add(column);
      }
    } finally {
      FileHelper.safeClose(columnRs);
    }

    return columnList;
  }

  public static List<Column> getColumnList(Connection connection, String databaseName,
      String tableName, String dsCode) throws Exception {
    String enclose = DataBaseConfig.getDatabaseEnclose(dsCode);
    StringBuilder queryBuilder = new StringBuilder();
    if (dsCode.equalsIgnoreCase(DataBaseType.SQLSERVER.toString())) {
      queryBuilder.append("select top 1 * from ").append(enclose).append(databaseName)
          .append(enclose).append(".").append(enclose).append(tableName).append(enclose);
    } else {
      queryBuilder.append("select * from ").append(enclose).append(databaseName).append(enclose)
          .append(".").append(enclose).append(tableName).append(enclose).append(" limit 1");
    }

    // String query="select * from `"+tableName + "`";
    PreparedStatement pstmt = null;
    List<Column> columnList = new ArrayList<>();
    try {
      pstmt = connection.prepareStatement(queryBuilder.toString());
      if(pstmt != null){
        ResultSetMetaData rsmd = pstmt.getMetaData();
        if(rsmd != null){
          int columnCount = rsmd.getColumnCount();
          for (int i = 0; i < columnCount; i++) {
            String columnName = rsmd.getColumnName(i + 1);
            MutableColumn column = new MutableColumn();
            column.setName(columnName);
            column.setType(ColumnType.STRING); // 设置类型为字符串，通过数据判断类型和格式
            columnList.add(column);
          }
        }
      }
    } finally {
      FileHelper.safeClose(pstmt);
    }
    return columnList;

  }


  public static List<Row> getRowList(Connection connection, String query, MutableTable table)
      throws Exception {
    List<Row> defaultRowList = new ArrayList<>();
    DataSetHeader dataSetHeader =
        new CachingDataSetHeader(MetaModelHelper.createSelectItems(table.getColumns()));
    Statement statement = null;
    ResultSet resultSet = null;
    try {
      statement = connection.createStatement();
      resultSet = statement.executeQuery(query);
      while (resultSet.next()) {
        List<Object> rowData = new ArrayList<Object>();
        for (int i = 0; i < table.getColumnCount(); i++) {
          rowData.add(resultSet.getString(i + 1));
        }
        DefaultRow row = new DefaultRow(dataSetHeader, rowData.toArray());
        defaultRowList.add(row);
      }

    } finally {
      FileHelper.safeClose(statement, resultSet);
    }
    return defaultRowList;
  }

  public static Long getRowCount(Connection connection, String databaseName, String tableName,
      String dsCode) throws Exception {
    String enclose = DataBaseConfig.getDatabaseEnclose(dsCode);
    StringBuilder countQueryBuilder = new StringBuilder();
    countQueryBuilder.append("select count(*) from ").append(enclose).append(databaseName)
        .append(enclose).append(".").append(enclose).append(tableName).append(enclose);
    // String query="select count(*) from `"+tableName + "`";

    Statement statement = null;
    ResultSet resultSet = null;
    Long rowCount = 0l;
    try {
      statement = connection.createStatement();
      resultSet = statement.executeQuery(countQueryBuilder.toString());
      while (resultSet.next()) {
        rowCount = resultSet.getLong(1);
      }
    } finally {
      FileHelper.safeClose(statement, resultSet);
    }
    return rowCount;
  }

  /*
   * public static List<PtoneDataBase> getDataBaseList(Connection connection)throws Exception {
   * DatabaseMetaData databaseMetaData=getDatabaseMetaData(connection); ResultSet
   * dataBaseRs=databaseMetaData.getCatalogs(); List<PtoneDataBase> dataBaseList=new ArrayList<>();
   * log.debug("List all database"); while(dataBaseRs.next()) { PtoneDataBase dataBase=new
   * PtoneDataBase(); String dataBaseName=dataBaseRs.getString("TABLE_CAT");
   * dataBase.setName(dataBaseName); List<PtoneDataBaseTable>
   * tableList=getTableList(databaseMetaData,dataBaseName); dataBase.setTableList(tableList);
   * dataBaseList.add(dataBase); }
   * 
   * log.debug(dataBaseList);
   * 
   * return dataBaseList;
   * 
   * }
   */

  /*
   * private static List<PtoneDataBaseTable> getTableList(DatabaseMetaData databaseMetaData,String
   * dataBase)throws Exception { ResultSet tableRs=databaseMetaData.getTables(dataBase, null, null,
   * new String[]{"TABLE"}); List<PtoneDataBaseTable> tableList=new ArrayList<>();
   * while(tableRs.next()) { PtoneDataBaseTable table=new PtoneDataBaseTable(); String
   * tableName=tableRs.getString("TABLE_NAME"); table.setName(tableName); List<PtoneDataBaseRow>
   * rowList=getRowList(databaseMetaData,dataBase,tableName); table.setRowList(rowList);
   * tableList.add(table); }
   * 
   * return tableList; }
   */

  /*
   * private static List<PtoneDataBaseRow> getRowList(DatabaseMetaData databaseMetaData,String
   * dataBase,String table)throws Exception { List<PtoneDataBaseRow> rowList=new ArrayList<>();
   * ResultSet rowRs=databaseMetaData.getColumns(dataBase, null, table, null); while(rowRs.next()) {
   * PtoneDataBaseRow row=new PtoneDataBaseRow(); String columnName=rowRs.getString("COLUMN_NAME");
   * Integer dataType=rowRs.getInt("DATA_TYPE"); row.setName(columnName);
   * row.setDataType(convertSqlTypeToPtoneDataType(dataType)); rowList.add(row); } return rowList; }
   */

  private static DatabaseMetaData getDatabaseMetaData(Connection connection) throws Exception {
    return connection.getMetaData();
  }

  /*
   * 用于判断 DatabaseMetaData的getSchemas() 方法是否有结果返回， mysql的getSchemas 不会返回结果
   */
  public boolean useCatalogsAsSchemas(DatabaseMetaData databaseMetaData) throws Exception {
    boolean result = true;
    ResultSet resultSet = null;
    try {
      resultSet = databaseMetaData.getSchemas();
      while (resultSet.next() && result) {
        result = false;
      }
    } finally {

    }
    return result;
  }

  public static String convertSqlTypeToPtoneDataType(int sqlDataType) {
    switch (sqlDataType) {
      case -7:
      case -6:
      case 5:
      case 4:
        return DataType.INTEGER.name();
      case -5:
        return DataType.LONG.name();
      case 6:
      case 7:
      case 8:
      case 2:
      case 3:
        return DataType.DOUBLE.name();
      case 1:
      case 12:
      case -1:
        return DataType.TEXT.name();
      case 91:
      case 92:
      case 93:
        return DataType.TIME.name();
        // binary varbinary没有进行处理
      default:
        return DataType.TEXT.name();

    }
  }



}
