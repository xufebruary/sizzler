package com.sizzler.provider.common.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.util.CommonQueryRequest;
import org.apache.metamodel.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.SpringContextUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.provider.common.file.PtoneFile;

/**
 * Created by ptmind on 2015/12/31.
 */
public class QueryDataBaseUtil {

  private static final Logger log = LoggerFactory.getLogger(QueryDataBaseUtil.class);

  // 本地测试
  private static String queryDataBaseHost = "";
  private static String queryDataBasePort = "";
  private static String queryDataBaseUser = "";
  private static String queryDataBasePassword = "";

  public static String CurrencyReplaceRegex = "[\\$€¥￥円元(USD)(EUR)(JPY)(JP)(RMB)]";
  // 千位分隔符
  public static String NumberReplaceRegex = ",";
  public static String PercentReplaceRegex = "%";
  public static String DurationReplaceRegex = "(s|S|h|H|m|M)";

  /** 小数点 */
  public static String THE_DECIMAL_POINT = ".";
  /** 结构使字符(`) */
  public static String DS_SINGLE_QUOTES = "`";
  /** 组成 `.` 字符串 */
  public static String BUILD_SINGLE_QUOTES_AND_POINT = DS_SINGLE_QUOTES + THE_DECIMAL_POINT
      + DS_SINGLE_QUOTES;

  public static String createQueryDataBaseConnection = "createQueryDataBaseConnection";
  public static String queryDataBase = "queryDataBase";

  public static final String DB_DEFAULT_CHARSET = "utf8mb4";
  public static final String DB_DEFAULT_COLLATE = "utf8mb4_general_ci";
  /** 每次切分的最大行数 */
  public static final Integer ROW_MAX_COUNT = 1000;

  // 线上
  /*
   * private static String queryDataBaseHost="172.17.2.103"; private static String
   * queryDataBasePort="33306"; private static String queryDataBaseUser="ptone"; private static
   * String queryDataBasePassword="ptone";
   */
  /*
   * private static String queryDataBaseHost; private static String queryDataBasePort; private
   * static String queryDataBaseUser; private static String queryDataBasePassword;
   */

  public static boolean createDataBase(String queryDataBaseName, Connection connection)
      throws Exception {
    // CREATE DATABASE IF NOT EXISTS my_db;
    Statement statement = null;
    try {
      StringBuilder sqlBuilder = new StringBuilder();
      sqlBuilder.append("CREATE DATABASE IF NOT EXISTS ").append(queryDataBaseName);
      // sqlBuilder.append(" default charset ").append(DB_DEFAULT_CHARSET);
      // sqlBuilder.append(" collate ").append(DB_DEFAULT_COLLATE);

      statement = connection.createStatement();
      int result = statement.executeUpdate(sqlBuilder.toString());
      if (result != 1) {
        return false;
      }
    } finally {
      FileHelper.safeClose(statement);
    }
    return true;
  }

  /**
   * 
   * @description 创建用户级别的JDBC连接池
   * @author shaoqiang.guo
   * @date 2016年10月18日 上午9:20:21
   * @return connection
   */
  public static Connection createQueryDataBaseConnection() {
    // jdbc.url=jdbc:mysql://192.168.1.2:3306/?useUnicode=true&amp;characterEncoding=UTF-8
    DruidDataSource druidDataSource = SpringContextUtil.getBean(queryDataBase);
    LogMessage logMessage = buildLogMessage(druidDataSource);
    DruidPooledConnection connection = null;
    try {
      connection = druidDataSource.getConnection();
      if (connection != null && connection.getConnectionHolder() != null) {
        logMessage.addOperateInfo("druidPooledConnectionInfo", connection.getConnectionHolder()
            .toString());
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw DataBaseConnetionFactory.buildServiceException(e.getMessage(), e);
    } finally {
      log.info(logMessage.toString());
    }
    return connection;
  }

  /**
   * 
   * @description 释放连接
   * @author shaoqiang.guo
   * @date 2016年10月19日 上午11:10:56
   * @param connection
   */
  public static void releaseConnection(Connection connection) {
    // DruidDataSource druidDataSource = SpringContextUtil.getBean(queryDataBase);
    // DataSourceUtils.releaseConnection(connection, druidDataSource);
    try {
      connection.close();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      FileHelper.safeClose(connection);
    }
  }

  /**
   * 
   * @description 创建日志对象
   * @author shaoqiang.guo
   * @date 2016年10月20日 上午10:29:38
   * @param druidDataSource
   * @return logMessage
   */
  private static LogMessage buildLogMessage(DruidDataSource druidDataSource) {
    LogMessage logMessage = new LogMessage();
    logMessage.setOperate(createQueryDataBaseConnection);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("id", druidDataSource.getID());
    map.put("activeCount", druidDataSource.getActiveCount());
    map.put("poolingCount", druidDataSource.getPoolingCount());
    map.put("activeConnectionStackTrace", druidDataSource.getActiveConnectionStackTrace());
    map.put("createCount", druidDataSource.getCreateCount());
    map.put("createErrorCount", druidDataSource.getCreateErrorCount());
    map.put("connectCount", druidDataSource.getConnectCount());
    map.put("connectErrorCount", druidDataSource.getConnectErrorCount());
    map.put("waitThreadCount", druidDataSource.getWaitThreadCount());
    map.put("closeCount", druidDataSource.getCloseCount());
    map.put("errorCount", druidDataSource.getErrorCount());
    map.put("destroyCount", druidDataSource.getDestroyCount());
    map.put("discardCount", druidDataSource.getDiscardCount());
    map.put("resetCount", druidDataSource.getResetCount());
    map.put("recycleCount", druidDataSource.getRecycleCount());
    map.put("recycleErrorCount", druidDataSource.getRecycleErrorCount());
    logMessage.addOperateInfo(map);
    return logMessage;
  }

  /**
   * 
   * @description 执行SQL语句
   * @author shaoqiang.guo
   * @date 2016年10月18日 上午9:48:01
   * @param query
   * @param dataBaseName
   * @return
   */
  public static List<List> excuteQuery(String query, String dataBaseName) {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    Statement totalStatement = null;
    ResultSet totalResultSet = null;
    List<List> result = new ArrayList<List>();
    try {
      connection = QueryDataBaseUtil.createQueryDataBaseConnection();
      statement = connection.createStatement();
      resultSet = statement.executeQuery(query);
      result = QueryDataBaseUtil.buildResultRowList(resultSet);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      FileHelper.safeClose(statement, resultSet, totalStatement, totalResultSet);
      releaseConnection(connection);
    }
    return result;
  }

  /**
   * 
   * @description 将返回的数据构建转换到List中
   * @author shaoqiang.guo
   * @date 2016年10月18日 上午9:48:41
   * @param resultSet
   * @return
   * @throws SQLException
   */
  public static List<List> buildResultRowList(ResultSet resultSet) throws SQLException {
    List<List> resultRowList = new ArrayList<>();

    ResultSetMetaData rsmd = resultSet.getMetaData();
    // List<String> resultSetColNameList = new ArrayList<>();
    // for (int c = 1; c <= rsmd.getColumnCount(); c++) {
    // resultSetColNameList.add(rsmd.getColumnName(c));
    // }

    while (resultSet.next()) {
      List<String> tmpRowList = new ArrayList<String>();
      // 按索引获取数据，name会有重名出现
      for (int c = 1; c <= rsmd.getColumnCount(); c++) {
        tmpRowList.add(resultSet.getString(c));
      }
      resultRowList.add(tmpRowList);
    }
    return resultRowList;
  }

  /**
   * 
   * @description 构建查询总数的SQL，新增参数dataBaseName
   * @author shaoqiang.guo
   * @date 2016年10月18日 上午9:49:24
   * @param queryRequest
   * @param tableName
   * @param dsCode
   * @param dataBaseName
   * @return
   */
  public static String createTotalSql(CommonQueryRequest queryRequest, String tableName,
      String dsCode, String dataBaseName) {
    String enclose = DataBaseConfig.getDatabaseEnclose(dsCode);
    if (enclose == null || enclose.equals("")) {
      enclose = "`";
    }
    StringBuilder sqlBuilder = new StringBuilder(" ");
    StringBuilder selectBuilder = new StringBuilder(" select ");
    // String from=" from `"+tableName+"`";
    StringBuilder fromBuilder = new StringBuilder();
    fromBuilder.append(" from ").append(enclose).append(dataBaseName)
        .append(BUILD_SINGLE_QUOTES_AND_POINT).append(tableName).append(enclose);

    boolean hasWhere = false;

    String where = "";

    String metrics = queryRequest.getMetrics();

    String filters = queryRequest.getFilters();


    if (filters != null && !filters.trim().equals("")) {
      hasWhere = true;
      where = filters;
    }

    if (metrics != null && !metrics.trim().equals("")) {
      String[] metricArray = metrics.split(",");
      for (int i = 0; i < metricArray.length; i++) {
        // selectBuilder.append(" sum(").append(enclose).append(metricArray[i]).append(enclose).append(") as ").append(enclose).append(metricArray[i]).append(enclose).append(" ");
        selectBuilder.append(createSum(metricArray[i], dsCode, enclose)).append(" as ")
            .append(enclose).append(metricArray[i]).append(enclose).append(" ");
        if (i < metricArray.length - 1) {
          selectBuilder.append(", ");
        }
      }
    }

    sqlBuilder.append(selectBuilder.toString()).append(" ").append(fromBuilder.toString())
        .append(" ");
    if (hasWhere) {
      sqlBuilder.append(" where ").append(filters);
    }
    return sqlBuilder.toString();
  }


  /**
   * 
   * @description 删除表的操作，新增参数dataBaseName
   * @author shaoqiang.guo
   * @date 2016年10月18日 上午9:50:08
   * @param fileId
   * @param schema
   * @param dataBaseName
   * @throws Exception
   */
  public static void dropTable(String fileId, MutableSchema schema, Connection connection,
      String dataBaseName) throws Exception {

    Statement dropTableStatement = null;
    try {
      // 首先生成删除表的语句
      List<String> dropTableSqlList = generateDropTableSqlList(fileId, schema, dataBaseName);
      dropTableStatement = connection.createStatement();
      for (String dropTableSql : dropTableSqlList) {
        dropTableStatement.executeUpdate(dropTableSql);
      }
    } finally {
      FileHelper.safeClose(dropTableStatement);
    }
  }

  /**
   * 
   * @description 创建表的操作，新增参数dataBaseName
   * @author shaoqiang.guo
   * @date 2016年10月18日 上午10:05:42
   * @param fileId
   * @param schema
   * @param connection
   * @param dataBaseName
   * @throws Exception
   */
  public static void createTable(String fileId, MutableSchema schema, Connection connection,
      String dataBaseName) throws Exception {

    Statement createTableStatement = null;
    try {
      // 首先生成创建表的语句
      List<String> createTableSqlList = generateCreateTableSqlList(fileId, schema, dataBaseName);
      createTableStatement = connection.createStatement();
      for (String createTableSql : createTableSqlList) {
        createTableStatement.executeUpdate(createTableSql);
      }
    } finally {
      FileHelper.safeClose(createTableStatement);
    }

  }


  /**
   * 
   * @description 清空表的操作
   * @author shaoqiang.guo
   * @date 2016年10月17日 下午7:58:22
   * @param fileId
   * @param schema
   * @param dataBaseName
   * @throws Exception
   */
  public static void clearTable(String fileId, MutableSchema schema, String dataBaseName)
      throws Exception {
    Connection connection = null;
    Statement clearTableStatement = null;
    try {
      // 首先生成清空表的语句
      List<String> createTableSqlList = generateClearTableSqlList(fileId, schema, dataBaseName);
      connection = QueryDataBaseUtil.createQueryDataBaseConnection();
      clearTableStatement = connection.createStatement();
      /*
       * for(String clearTableSql:createTableSqlList) { clearTableStatement.addBatch(clearTableSql);
       * } clearTableStatement.executeBatch();
       */

    } finally {
      FileHelper.safeClose(clearTableStatement);
      releaseConnection(connection);
    }
  }


  /**
   * 
   * @description 插入数据库的操作，新增参数dataBaseName
   * @author shaoqiang.guo
   * @date 2016年10月18日 上午9:38:15
   * @param fileId
   * @param ptoneFile
   * @param schema
   * @param connection
   * @param dataBaseName
   * @throws Exception
   */
  public static void insertPtoneFileToTable(String fileId, PtoneFile ptoneFile,
      MutableSchema schema, Connection connection, String dataBaseName) throws Exception {

    Statement insertTableStatement = null;
    try {
      // 生成插入的语句
      LinkedHashMap<String, List<List>> fileListDataMap = ptoneFile.getFileListDataMap();

      for (Table table : schema.getTables()) {
        String tableName = table.getName();

        List<List> tableData = fileListDataMap.get(tableName);
        if (CollectionUtil.isEmpty(tableData)) {
          // 如果该表不存在数据，则不需要执行插入语句了
          continue;
        }

        List<String> insertTableSqlList = generateInsertSql(fileId, tableData, table, dataBaseName);
        insertTableStatement = connection.createStatement();
        for (String insertTableSql : insertTableSqlList) {
          insertTableStatement.addBatch(insertTableSql);
        }
        insertTableStatement.executeBatch();

      }
    } finally {
      FileHelper.safeClose(insertTableStatement);
    }

  }

  /**
   * 
   * @description 新增参数dataBaseName
   * @author shaoqiang.guo
   * @date 2016年10月18日 上午9:39:05
   * @param fileId
   * @param ptoneFile
   * @param dataBaseName
   * @return
   */
  public static List<String> generateInsertTableSqlList(String fileId, PtoneFile ptoneFile,
      String dataBaseName) {
    List<String> insertTableSqlList = new ArrayList<>();

    LinkedHashMap<String, List<List>> fileListDataMap = ptoneFile.getFileListDataMap();

    for (Map.Entry<String, List<List>> entry : fileListDataMap.entrySet()) {
      String tableName = entry.getKey();
      List<List> tableData = entry.getValue();

      Table table = ptoneFile.getSchema().getTableByName(tableName);

      List<String> insertSqlList = generateInsertSql(fileId, tableData, table, dataBaseName);
      insertTableSqlList.addAll(insertSqlList);
    }

    return insertTableSqlList;
  }

  /**
   * 
   * @description 新增参数dataBaseName
   * @author shaoqiang.guo
   * @date 2016年10月17日 下午8:02:01
   * @param fileId
   * @param rowList
   * @param table
   * @param dataBaseName
   * @return
   */
  public static List<String> generateInsertSql(String fileId, List<List> rowList, Table table,
      String dataBaseName) {
    List<String> insertSqlList = new ArrayList<>();
    List<List<List>> splitRowList = new ArrayList<List<List>>();
    if(CollectionUtil.isNotEmpty(rowList)){
      Integer rowSize = rowList.size();
      if (rowSize - ROW_MAX_COUNT > 0) {
        // 将rowList按照最大分割限度切分成多个List
        List<List> splitRows = new ArrayList<List>();
        for (int i = 0; i < rowSize; i++) {
          splitRows.add(rowList.get(i));
          if ((i + 1) % ROW_MAX_COUNT == 0) {
            insertSqlList.add(generateQuickInsertSql(splitRows, table, dataBaseName));
            splitRows = new ArrayList<List>();
          } else if ((i + 1) == rowSize) {
            insertSqlList.add(generateQuickInsertSql(splitRows, table, dataBaseName));
            }
          }
      } else {
        insertSqlList.add(generateQuickInsertSql(rowList, table, dataBaseName));
      }
    }
    return insertSqlList;
  }

  /**
   * 处理ColValue数据，增加异常处理
   * @author you.zou
   * @date 2017年1月3日 下午3:02:21
   * @param colValue
   * @param column
   * @return
   */
  private static String fixColValue(String colValue, Column column){
    try{
      if (colValue.contains("\\")) {
        colValue = colValue.replaceAll("\\\\", "\\\\\\\\'");
      }
      if (colValue.contains("'")) {
        colValue = colValue.replaceAll("'", "\\\\'");
      }

      if (column != null && column.getType() != null) {
        // CURRENCY NUMBER PERCENT
        String columnType = column.getType().getName();

        if (columnType.equalsIgnoreCase("CURRENCY")) {
          colValue = colValue.replaceAll(CurrencyReplaceRegex, "");
        } else if (columnType.equalsIgnoreCase("NUMBER")) {
          colValue = colValue.replaceAll(NumberReplaceRegex, "");
        } else if (columnType.equalsIgnoreCase("PERCENT")) {
          colValue = colValue.replaceAll(PercentReplaceRegex, "");
        } else if (columnType.equalsIgnoreCase("DURATION")) {
          colValue = colValue.replaceAll(DurationReplaceRegex, "");
        }

      }
      colValue = StringUtil.scientificNotationToString(colValue);
    }catch(Exception e){
      log.error("fix colValue("+colValue+") error", e);
    }
    return colValue;
  }
  
  public static String generateQuickInsertSql(List<List> rowList, Table table, String dataBaseName) {
    String tableName = table.getId();
    int realColCount = table.getColumns().length;
    
    if (CollectionUtil.isEmpty(rowList)) {
      return null;
    }

    StringBuilder insertSqlBuilder = new StringBuilder();
    insertSqlBuilder.append(" insert into  `").append(dataBaseName)
        .append(BUILD_SINGLE_QUOTES_AND_POINT).append(tableName).append("` values");
    int nowCount = 1;
    String nowValue = null;
    int nowColListSize = 0;
    try {
      for (List colList : rowList) {
        insertSqlBuilder.append("(");
        nowColListSize = colList.size();
        List realColList = colList.subList(0, realColCount);

        for (int i = 0; i < realColList.size(); i++) {
          Object col = realColList.get(i);
          if (col != null) {
            String colValue = col.toString();
            nowValue = colValue;//用于出错时打印错误数据
            String str = "'";
            Column column = table.getColumn(i);
            colValue = fixColValue(colValue, column);
            insertSqlBuilder.append(str).append(colValue).append(str);
          } else {
            insertSqlBuilder.append("null"); // 插入null值
          }

          if (i < realColList.size() - 1) {
            insertSqlBuilder.append(",");
          }
        }
        insertSqlBuilder.append(")");
        if (nowCount == rowList.size()) {
          insertSqlBuilder.append(";");
        } else {
          insertSqlBuilder.append(",");
        }
        nowCount++;
      }
    } catch (Exception e) {
      //出现错误的时候打印出当前坐标
      LogMessage logMessage = new LogMessage();
      logMessage.setOperate("generateQuickInsertSql");
      logMessage.setExceptionMessage(e.getMessage());
      logMessage.addOperateInfo("tableName", table.getName()).
      addOperateInfo("nowCount", nowCount).addOperateInfo("nowValue", nowValue)
      .addOperateInfo("realColCount", realColCount).addOperateInfo("nowColListSize", nowColListSize);
      log.info(logMessage.toString());
      throw e;
    }
    return insertSqlBuilder.toString();
  }

  /**
   * 
   * @description 新增参数dataBaseName
   * @author shaoqiang.guo
   * @date 2016年10月18日 上午9:40:04
   * @param fileId
   * @param schema
   * @param dataBaseName
   * @return
   */
  public static List<String> generateClearTableSqlList(String fileId, MutableSchema schema,
      String dataBaseName) {
    List<String> clearTableSqlList = new ArrayList<>();
    for (MutableTable table : schema.getTables()) {
      String tableName = table.getId();
      String clearTableSql =
          "TRUNCATE `" + dataBaseName + BUILD_SINGLE_QUOTES_AND_POINT + tableName + "`;";
      clearTableSqlList.add(clearTableSql);
    }

    return clearTableSqlList;
  }


  /**
   * 
   * @description 新增参数dataBaseName
   * @author shaoqiang.guo
   * @date 2016年10月17日 下午7:14:21
   * @param fileId
   * @param schema
   * @param dataBaseName
   * @return
   */
  public static List<String> generateCreateTableSqlList(String fileId, MutableSchema schema,
      String dataBaseName) {
    List<String> createTableSqlList = new ArrayList<>();

    for (MutableTable table : schema.getTables()) {
      String tableName = table.getId();
      StringBuilder createTableSqlBuilder = new StringBuilder("create table  IF NOT EXISTS `");
      createTableSqlBuilder.append(dataBaseName + BUILD_SINGLE_QUOTES_AND_POINT + tableName)
          .append("` ( \n");
      Column[] columns = table.getColumns();

      for (int i = 0; i < columns.length; i++) {
        Column column = columns[i];
        String columnName = column.getId();

        createTableSqlBuilder.append("`").append(columnName).append("` ");
        createTableSqlBuilder.append(" LONGTEXT ");
        // createTableSqlBuilder.append(" CHARACTER SET ").append(DB_DEFAULT_CHARSET);
        // createTableSqlBuilder.append(" COLLATE ").append(DB_DEFAULT_COLLATE);

        if (i < columns.length - 1) {
          createTableSqlBuilder.append(", \n");
        }
      }
      createTableSqlBuilder.append(" \n ) ENGINE=MyISAM ");
      createTableSqlBuilder.append(" DEFAULT CHARSET=").append(DB_DEFAULT_CHARSET);
      createTableSqlBuilder.append(" COLLATE ").append(DB_DEFAULT_COLLATE).append(" ;");

      createTableSqlList.add(createTableSqlBuilder.toString());
    }
    return createTableSqlList;
  }

  /**
   * 
   * @description 新增参数dataBaseName
   * @author shaoqiang.guo
   * @date 2016年10月17日 下午7:14:58
   * @param fileId
   * @param schema
   * @param dataBaseName
   * @return
   */
  public static List<String> generateDropTableSqlList(String fileId, MutableSchema schema,
      String dataBaseName) {
    List<String> dropTableSqlList = new ArrayList<>();

    for (MutableTable table : schema.getTables()) {
      String tableName = table.getId();
      StringBuilder dropTableSqlBuilder = new StringBuilder("drop table  IF  EXISTS `");
      dropTableSqlBuilder.append(
          dataBaseName + DS_SINGLE_QUOTES + THE_DECIMAL_POINT + DS_SINGLE_QUOTES + tableName)
          .append("`; ");

      dropTableSqlList.add(dropTableSqlBuilder.toString());
    }
    return dropTableSqlList;
  }

  // 根据不同的数据源来构建sum
  public static String createSum(String field, String dsCode, String enclose) {
    StringBuilder sumBuilder = new StringBuilder("");
    if (dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_MYSQL)
        || dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_MYSQL)) {
      sumBuilder.append(" sum(").append(enclose).append(field).append(enclose).append(") ");
    } else if (dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_POSTGRE)) {
      // 首先将字符串转换为 numeric
      sumBuilder.append(" sum(").append(" cast(").append(enclose).append(field).append(enclose)
          .append(" as numeric )) ");
    } else {
      sumBuilder.append(" sum(").append(enclose).append(field).append(enclose).append(") ");
    }
    return sumBuilder.toString();
  }

  public String getQueryDataBaseHost() {
    return queryDataBaseHost;
  }

  public void setQueryDataBaseHost(String queryDataBaseHost) {
    this.queryDataBaseHost = queryDataBaseHost;
  }

  public String getQueryDataBasePort() {
    return queryDataBasePort;
  }

  public void setQueryDataBasePort(String queryDataBasePort) {
    this.queryDataBasePort = queryDataBasePort;
  }

  public String getQueryDataBaseUser() {
    return queryDataBaseUser;
  }

  public void setQueryDataBaseUser(String queryDataBaseUser) {
    this.queryDataBaseUser = queryDataBaseUser;
  }

  public String getQueryDataBasePassword() {
    return queryDataBasePassword;
  }

  public void setQueryDataBasePassword(String queryDataBasePassword) {
    this.queryDataBasePassword = queryDataBasePassword;
  }

  public static void main(String[] args) {
    String sum = createSum("a0ab420a-906d-4a0c-9b2c-b373b2ae4deb", "postgre", "\"");
    System.out.println(sum);
    sum = createSum("a0ab420a-906d-4a0c-9b2c-b373b2ae4deb", "mysql", "`");
    System.out.println(sum);
  }
}
