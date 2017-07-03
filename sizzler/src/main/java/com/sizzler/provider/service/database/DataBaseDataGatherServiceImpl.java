package com.sizzler.provider.service.database;

import com.sizzler.common.exception.DataSourceExceptionUtil;
import com.sizzler.common.exception.DataSourceOperateException;
import com.sizzler.common.exception.ErrorCode;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.sizzler.DataBaseConnection;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.provider.common.MetaRequest;
import com.sizzler.provider.common.db.DataBaseType;
import com.sizzler.provider.common.exception.DataSourceLogMessageUtil;
import com.sizzler.provider.common.file.PtoneFile;
import com.sizzler.provider.common.impl.DefaultDataResponse;
import com.sizzler.provider.common.impl.DefaultMetaResponse;
import com.sizzler.provider.common.query.DataBaseConnetionFactory;
import com.sizzler.provider.common.query.DataBaseService;
import com.sizzler.provider.common.query.MetaUtil;
import com.sizzler.provider.common.query.QueryDataBaseUtil;
import com.sizzler.provider.common.query.connection.pool.DataBaseConnectionInfo;
import com.sizzler.provider.common.query.connection.pool.DataBaseConnectionPool;
import com.sizzler.provider.common.util.SchemaUtil;
import com.sizzler.provider.domain.request.DataBaseDataRequest;
import com.sizzler.provider.domain.request.DataBaseEditorDataRequest;
import com.sizzler.provider.domain.request.DataBaseFileMetaRequest;
import com.sizzler.provider.domain.request.DataBaseMetaFolderRequest;
import com.sizzler.provider.domain.response.DataBaseEditorDataResponse;
import com.sizzler.provider.domain.response.DataBaseFileMetaResponse;
import com.sizzler.provider.domain.response.DataBaseMetaFolderResponse;
import com.sizzler.provider.domain.response.DataBaseMetaResponse;
import org.apache.metamodel.MetaModelHelper;
import org.apache.metamodel.data.*;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service("dataBaseGatherService")
public class DataBaseDataGatherServiceImpl implements DataBaseGatherService {

  private static final Logger log = LoggerFactory.getLogger(DataBaseDataGatherServiceImpl.class);
  // 操作异常常量
  private static final String GET_META = "getMeta";
  private static final String GET_FOLDERMETA = "getFolderMeta";
  private static final String GET_TABLESAMPLEDATA = "getTablesampleData";
  private static final String GET_EDITORDATA = "getEditorData";
  private static final String GET_PTONEFILE = "getPtoneFile";
  private static final String GET_DATA = "getData";
  private static final String DATABASE_NAME = "databaseName";
  private static final String TABLE_NAME = "tableName";
  private long rowCountLimit;

  public long getRowCountLimit() {
    return rowCountLimit;
  }

  public void setRowCountLimit(long rowCountLimit) {
    this.rowCountLimit = rowCountLimit;
  }

  // 连接测试的时候调用该接口
  @Override
  public DataBaseMetaResponse getMeta(MetaRequest request) throws ServiceException {
    DataBaseMetaResponse dbMetaResponse = new DataBaseMetaResponse();
    UserConnection userConnection = request.getUserConnection();
    String dsCode = userConnection.getDsCode();
    DataBaseConnection dataBaseConnection =
        MetaUtil.createDataBaseConnectionByUserConnection(userConnection);
    // 增加记录日志相关代码
    LogMessage logMessage = DataSourceLogMessageUtil.buildLogMessage(request);

    Connection connection = null;
    DataBaseConnectionInfo dataBaseConnectionInfo = null;
    try {
      dataBaseConnectionInfo = DataBaseConnetionFactory.createConnectionJDBC(dataBaseConnection);
      connection = dataBaseConnectionInfo.getConnection();
      List<MutableSchema> schemaList =
          DataBaseService.getSchemaList(dsCode, connection, dataBaseConnection.getDataBaseName(),
              false, false);
      dbMetaResponse.setSchemaList(schemaList);
    } catch (Exception e) {
      ServiceException se = DataBaseConnetionFactory.buildServiceException(e.getMessage(), e);
      // 创建数据源异常对象
      DataSourceOperateException dataSourceOperateException =
          DataSourceOperateException.builder(dsCode,
              DataSourceExceptionUtil.buildExceptionOperate(GET_META), se, request);
      // 将异常信息输出到日志文件
      log.error(dataSourceOperateException.generateLogMessage());
      // TODO 发送报警邮件

      throw se;
    } finally {
      // DataBaseConnetionFactory.closeConnection(connection);
      FileHelper.safeClose(connection);
      DataBaseConnectionPool.closeSession(dataBaseConnectionInfo.getSession());
      // 打印日志
      log.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return dbMetaResponse;
  }

  @Override
  public DataBaseMetaFolderResponse getFolderMeta(DataBaseMetaFolderRequest request)
      throws ServiceException {
    DataBaseMetaFolderResponse dbMetaResponse = new DataBaseMetaFolderResponse();

    List<PtoneFile> ptoneDataBaseFileList = new ArrayList<PtoneFile>();
    String folderId = request.getFolderId();
    UserConnection userConnection = request.getUserConnection();
    String dsCode = userConnection.getDsCode();
    // 增加记录日志相关代码
    LogMessage logMessage = DataSourceLogMessageUtil.buildLogMessage(request, GET_FOLDERMETA);

    DataBaseConnection dataBaseConnection =
        MetaUtil.createDataBaseConnectionByUserConnection(userConnection);

    Connection connection = null;

    DataBaseConnectionInfo dataBaseConnectionInfo = null;
    try {
      dataBaseConnectionInfo = DataBaseConnetionFactory.createConnectionJDBC(dataBaseConnection);
      connection = dataBaseConnectionInfo.getConnection();
      boolean widthTable = true;
      boolean widthColumn = true;
      if (DataBaseMetaFolderRequest.ROOT_FOLDER_ID.equalsIgnoreCase(folderId)) {
        widthTable = false;
        widthColumn = false;
      } else if (folderId != null) {
        widthTable = true;
        widthColumn = false;
      }

      List<MutableSchema> schemaList =
          DataBaseService.getSchemaList(dsCode, connection, dataBaseConnection.getDataBaseName(),
              widthTable, widthColumn);

      for (MutableSchema schema : schemaList) {
        if (DataBaseMetaFolderRequest.ROOT_FOLDER_ID.equalsIgnoreCase(folderId) || folderId == null
            || "".equals(folderId)) {
          PtoneFile file = new PtoneFile();
          file.setId(schema.getName());
          file.setName(schema.getName());
          file.setMimeType(DataBaseMetaFolderRequest.TYPE_DATABASE);
          file.setDirectory(true);
          file.setChild(new ArrayList<PtoneFile>());
          ptoneDataBaseFileList.add(file);
        } else if (folderId != null && folderId.equalsIgnoreCase(schema.getName())) {
          for (MutableTable mt : schema.getTables()) {
            PtoneFile file = new PtoneFile();
            file.setId(mt.getName());
            file.setName(mt.getName());
            file.setMimeType(DataBaseMetaFolderRequest.TYPE_TABLE);
            file.setDirectory(false);
            file.setChild(new ArrayList<PtoneFile>());
            ptoneDataBaseFileList.add(file);
          }
        }
      }

      dbMetaResponse.setFolderId(folderId);
      dbMetaResponse.setPtoneFileList(ptoneDataBaseFileList);
    } catch (ServiceException se) {
      // 创建数据源异常对象
      DataSourceOperateException dataSourceOperateException =
          DataSourceOperateException.builder(dsCode, se.getErrorMsg(), se, request);
      log.error(dataSourceOperateException.generateLogMessage());

      throw se;
    } catch (Exception e) {
      // catch 本类中发生的异常将异常转换成ServiceException抛出
      ServiceException se = DataBaseConnetionFactory.buildServiceException(e.getMessage(), e);
      // 创建数据源异常对象
      DataSourceOperateException dataSourceOperateException =
          DataSourceOperateException.builder(dsCode,
              DataSourceExceptionUtil.buildExceptionOperate(GET_FOLDERMETA), e, request);
      // 将异常信息输出到日志文件
      log.error(dataSourceOperateException.generateLogMessage());
      // 发送报警邮件

      throw se;
    } finally {
      DataBaseConnectionPool.closeSession(dataBaseConnectionInfo.getSession());
      FileHelper.safeClose(connection);
      // 打印日志
      log.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return dbMetaResponse;
  }

  public DefaultMetaResponse getTableSampleData(DataBaseEditorDataRequest request)
      throws ServiceException {
    DefaultMetaResponse response = new DefaultMetaResponse();
    UserConnection userConnection = request.getUserConnection();
    String dsCode = userConnection.getDsCode();
    DataBaseConnection dataBaseConnection =
        MetaUtil.createDataBaseConnectionByUserConnection(userConnection);
    // 当没有在config中指定databaseName时，才需要指定databaseName
    if (dataBaseConnection.getDataBaseName() == null
        || dataBaseConnection.getDataBaseName().equals("")) {
      dataBaseConnection.setDataBaseName(request.getDatabaseName());
    }
    // 增加记录日志相关代码
    LogMessage logMessage = buildLogMessage(request, GET_TABLESAMPLEDATA);

    Connection connection = null;

    DataBaseConnectionInfo dataBaseConnectionInfo = null;
    try {
      dataBaseConnectionInfo = DataBaseConnetionFactory.createConnectionJDBC(dataBaseConnection);
      connection = dataBaseConnectionInfo.getConnection();

      String enclose = DataBaseConfig.getDatabaseEnclose(dsCode);


      Long rowCount =
          DataBaseService.getRowCount(connection, request.getDatabaseName(),
              request.getTableName(), dsCode);
      // 数据量过大，禁止连接
      // rowCountLimit = 5; // for test
      if (rowCountLimit > 0 && rowCountLimit < rowCount) {
        ServiceException se =
            new ServiceException("rows count is " + rowCount + " , large than " + rowCountLimit
                + " !");
        se.setErrorCode(ErrorCode.CODE_DB_ROW_COUNT_BEYOND_LIMIT);
        se.setErrorMsg(ErrorCode.MSG_DB_ROW_COUNT_BEYOND_LIMIT);
        throw se;
      }

      StringBuilder queryBuilder = new StringBuilder();
      if (dsCode.equalsIgnoreCase(DataBaseType.SQLSERVER.toString())) {
        queryBuilder.append("select top ").append(DsConstants.DATA_TYPE_DETERMINE_ROW_LIMIT)
            .append(" * from ").append(enclose).append(request.getDatabaseName()).append(enclose)
            .append(".").append(enclose).append(request.getTableName()).append(enclose);
      } else {
        queryBuilder.append("select * from ").append(enclose).append(request.getDatabaseName())
            .append(enclose).append(".").append(enclose).append(request.getTableName())
            .append(enclose);
        queryBuilder.append(" limit ").append(DsConstants.DATA_TYPE_DETERMINE_ROW_LIMIT);
      }

      // String
      // query="select * from `"+request.getDatabaseName()+"`.`"+request.getTableName()+"` limit 200";
      String query = queryBuilder.toString();
      List<Column> columnList =
          DataBaseService.getColumnList(connection, request.getDatabaseName(),
              request.getTableName(), dsCode);
      MutableSchema schema = new MutableSchema(request.getDatabaseName());
      MutableTable table = new MutableTable(request.getTableName());
      table.setRowCount(rowCount);
      table.setColumns(columnList);
      schema.addTable(table);
      table.setSchema(schema);
      LinkedHashMap<String, List<Row>> tableDataMap = new LinkedHashMap<>();
      List<Row> rowList = DataBaseService.getRowList(connection, query, table);
      tableDataMap.put(request.getTableName(), rowList);

      // 根据数据判断数据类型和格式，修正schema和数据
      Column[] columnArray = new Column[columnList.size()];
      DataSetHeader dataSetHeader =
          new CachingDataSetHeader(MetaModelHelper.createSelectItems(columnList
              .toArray(columnArray)));
      DataSet dataSet = new InMemoryDataSet(dataSetHeader, rowList);
      LinkedHashMap<String, DataSet> dataSetMap = new LinkedHashMap<>();
      dataSetMap.put(table.getName(), dataSet);
      LinkedHashMap<String, List<Row>> fixDataMap =
          SchemaUtil.determineColumnType(schema, dataSetMap, true);

      response.setSchema(schema);
      response.setTableDataMap(fixDataMap);

    } catch (ServiceException se) {
      // 创建数据源异常对象
      DataSourceOperateException dataSourceOperateException =
          DataSourceOperateException.builder(dsCode, se.getErrorMsg(), se, request);
      // 打印错误日志
      log.error(dataSourceOperateException.generateLogMessage());
      // TODO 发送报警邮件

      throw se;
    } catch (Exception e) {
      // catch 本类中发生的异常 将异常转换成ServiceException抛出
      ServiceException se = DataBaseConnetionFactory.buildServiceException(e.getMessage(), e);
      // 创建数据源异常对象
      DataSourceOperateException dataSourceOperateException =
          DataSourceOperateException.builder(dsCode,
              DataSourceExceptionUtil.buildExceptionOperate(GET_TABLESAMPLEDATA), e, request);
      // 将异常信息输出到日志文件
      log.error(dataSourceOperateException.generateLogMessage());
      // TODO 发送报警邮件

      throw se;
    } finally {
      DataBaseConnectionPool.closeSession(dataBaseConnectionInfo.getSession());
      FileHelper.safeClose(connection);
      // 打印日志
      log.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return response;
  }

  @Override
  public DataBaseEditorDataResponse getEditorData(DataBaseEditorDataRequest request)
      throws ServiceException {

    DataBaseEditorDataResponse response = new DataBaseEditorDataResponse();
    String databaseName = request.getDatabaseName();
    String tableName = request.getTableName();
    UserConnection userConnection = request.getUserConnection();
    String dsCode = userConnection.getDsCode();
    // 增加记录日志相关代码
    LogMessage logMessage = buildLogMessage(request, GET_EDITORDATA);

    // 取得文件(此处会返回所有的文件内容)
    DataBaseEditorDataRequest sampleDataRequest = new DataBaseEditorDataRequest(userConnection);
    sampleDataRequest.setDatabaseName(databaseName);
    sampleDataRequest.setTableName(tableName);
    DefaultMetaResponse sampleDataResponse = this.getTableSampleData(sampleDataRequest);

    response.setEditorData(sampleDataResponse.getTableListDataMap());
    response.setSchema(sampleDataResponse.getSchema());

    // andy add 2016-01-20
    Connection connection = null;
    DataBaseConnection dataBaseConnection =
        MetaUtil.createDataBaseConnectionByUserConnection(userConnection);
    // 当没有在config中指定databaseName时，才需要指定databaseName
    if (dataBaseConnection.getDataBaseName() == null
        || dataBaseConnection.getDataBaseName().equals("")) {
      dataBaseConnection.setDataBaseName(request.getDatabaseName());
    }
    DataBaseConnectionInfo dataBaseConnectionInfo = null;
    try {
      dataBaseConnectionInfo = DataBaseConnetionFactory.createConnectionJDBC(dataBaseConnection);
      connection = dataBaseConnectionInfo.getConnection();

      Long rowCount =
          DataBaseService.getRowCount(connection, databaseName, tableName,
              userConnection.getDsCode());
      response.setRowCount(rowCount);
    } catch (ServiceException se) {
      // 下级的方法中已经处理了se并打印出se的异常，这里只需要传递即可

      throw se;
    } catch (Exception e) {
      // catch 本类中发生的异常将异常转换成ServiceException抛出
      ServiceException se = DataBaseConnetionFactory.buildServiceException(e.getMessage(), e);
      // 创建数据源异常对象
      DataSourceOperateException dataSourceOperateException =
          DataSourceOperateException.builder(dsCode,
              DataSourceExceptionUtil.buildExceptionOperate(GET_EDITORDATA), e, request);
      // 将异常信息输出到日志文件
      log.error(dataSourceOperateException.generateLogMessage());
      // TODO 发送报警邮件

      throw se;
    } finally {
      DataBaseConnectionPool.closeSession(dataBaseConnectionInfo.getSession());
      FileHelper.safeClose(connection);
      // 打印日志
      log.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());

    }

    return response;
  }

  @Override
  public DataBaseFileMetaResponse getPtoneFile(DataBaseFileMetaRequest request)
      throws ServiceException {


    DataBaseFileMetaResponse response = new DataBaseFileMetaResponse();
    String databaseName = request.getDatabaseName();
    String tableName = request.getTableName();
    UserConnection userConnection = request.getUserConnection();
    String dsCode = userConnection.getDsCode();
    // 增加记录日志相关
    LogMessage logMessage = DataSourceLogMessageUtil.buildLogMessage(request, GET_PTONEFILE);
    logMessage.addOperateInfo(DATABASE_NAME, databaseName);
    logMessage.addOperateInfo(TABLE_NAME, tableName);

    PtoneFile ptoneFile = new PtoneFile();
    ptoneFile.setId(tableName);
    ptoneFile.setName(tableName);
    ptoneFile.setFolderId(databaseName);
    ptoneFile.setMimeType(DataBaseMetaFolderRequest.TYPE_TABLE);
    try {
      DataBaseEditorDataRequest sampleDataRequest = new DataBaseEditorDataRequest(userConnection);
      sampleDataRequest.setDatabaseName(databaseName);
      sampleDataRequest.setTableName(tableName);
      DefaultMetaResponse sampleDataResponse = this.getTableSampleData(sampleDataRequest);

      ptoneFile.setSchema(sampleDataResponse.getSchema());
      ptoneFile.setFileDataMap(sampleDataResponse.getTableDataMap());

      response.setFile(ptoneFile);

    } catch (ServiceException se) {
      // 下级的方法中已经处理了se并打印出se的异常，这里只需要传递即可

      throw se;
    } catch (Exception e) {
      // catch 本类中发生的异常将异常转换成ServiceException抛出
      ServiceException se = DataBaseConnetionFactory.buildServiceException(e.getMessage(), e);
      // 创建数据源异常对象
      DataSourceOperateException dataSourceOperateException =
          DataSourceOperateException.builder(dsCode,
              DataSourceExceptionUtil.buildExceptionOperate(GET_PTONEFILE), e, request);
      // 将异常信息输出到日志文件
      log.error(dataSourceOperateException.generateLogMessage());
      // TODO 发送报警邮件

      throw se;
    } finally {
      // 打印日志
      log.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }

    return response;
  }

  @Override
  public DefaultDataResponse getData(DataBaseDataRequest dataRequest) throws ServiceException {

    DefaultDataResponse response = new DefaultDataResponse();
    String uid = dataRequest.getUserConnection().getUid();
    String query = dataRequest.getQueryRequest().getQuery();
    String dsCode = dataRequest.getUserConnection().getDsCode();
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    Statement totalStatement = null;
    ResultSet totalResultSet = null;

    String dataBaseName = dataRequest.getDatabaseName();
    UserConnection userConnection = dataRequest.getUserConnection();
    DataBaseConnection dataBaseConnection =
        MetaUtil.createDataBaseConnectionByUserConnection(userConnection);

    // 增加记录日志相关
    LogMessage logMessage = DataSourceLogMessageUtil.buildLogMessage(dataRequest);


    // 当没有在config中指定databaseName时，才需要指定databaseName
    if (dataBaseConnection.getDataBaseName() == null
        || dataBaseConnection.getDataBaseName().equals("")) {
      dataBaseConnection.setDataBaseName(dataBaseName);
    }

    DataBaseConnectionInfo dataBaseConnectionInfo = null;
    try {
      dataBaseConnectionInfo = DataBaseConnetionFactory.createConnection(dataBaseConnection);
      // connection = DataBaseConnetionFactory.createConnection(userConnection,dataBaseName);
      connection = dataBaseConnectionInfo.getConnection();
      statement = connection.createStatement();

      resultSet = statement.executeQuery(query);
      response.setObjetRowList(QueryDataBaseUtil.buildResultRowList(resultSet));

      List<Object> totalRowList = new ArrayList<Object>();

      if (dataRequest.getQueryRequest().getMetrics() != null
          && !dataRequest.getQueryRequest().getMetrics().equals("")) {
        String totalQuery = dataRequest.getQueryRequest().getTotalQuery();
        if (StringUtil.isBlank(totalQuery)) {
          totalQuery =
              QueryDataBaseUtil.createTotalSql(dataRequest.getQueryRequest(),
                  dataRequest.getTableName(), dsCode, dataBaseName);
        }

        totalStatement = connection.createStatement();
        totalResultSet = totalStatement.executeQuery(totalQuery);
        List<List> totalResultRowList = QueryDataBaseUtil.buildResultRowList(totalResultSet);
        if (totalResultRowList != null && totalResultRowList.size() > 0
            && totalResultRowList.get(0) != null) {
          totalRowList = totalResultRowList.get(0);
        }
      }

      response.setTotalRowList(totalRowList);

    } catch (ServiceException se) {
      // catch ServiceException 表示调用其他方法中抛出的异常
      // 创建数据源异常对象
      DataSourceOperateException dataSourceOperateException =
          DataSourceOperateException.builder(dsCode, se.getErrorMsg(), se, dataRequest);
      // 输出异常到日志中
      log.error(dataSourceOperateException.generateLogMessage());
      // TODO 发送报警邮件

      throw se;
    } catch (Exception e) {
      // catch 本类中发生的异常将异常转换成ServiceException抛出
      ServiceException se = DataBaseConnetionFactory.buildServiceException(e.getMessage(), e);
      // 创建数据源异常对象
      DataSourceOperateException dataSourceOperateException =
          DataSourceOperateException.builder(dsCode,
              DataSourceExceptionUtil.buildExceptionOperate(GET_DATA), e, dataRequest);
      // 将异常信息输出到日志文件
      log.error(dataSourceOperateException.generateLogMessage());
      // TODO 发送报警邮件

      throw se;
    } finally {
      FileHelper.safeClose(statement, resultSet, totalStatement, totalResultSet);
      QueryDataBaseUtil.releaseConnection(connection);
      DataBaseConnectionPool.closeSession(dataBaseConnectionInfo.getSession());
      // 打印日志
      log.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return response;
  }

  /**
   * 针对DataBaseEditorDataRequest类型的参数来创建并初始化LogMessage（主要为了向之前的数据源的请求接口进行兼容）
   * @param dataBaseEditorDataRequest
   * @return
   */
  private static LogMessage buildLogMessage(DataBaseEditorDataRequest dataBaseEditorDataRequest,
      String operate) {

    LogMessage logMessage = new LogMessage();
    // 设置操作
    logMessage.setOperate(operate);
    // 非空判断
    if (dataBaseEditorDataRequest == null) {
      return logMessage;
    }
    // 设置关键信息
    logMessage.addOperateInfo(DATABASE_NAME, dataBaseEditorDataRequest.getDatabaseName());
    logMessage.addOperateInfo(TABLE_NAME, dataBaseEditorDataRequest.getTableName());
    UserConnection userConnection = dataBaseEditorDataRequest.getUserConnection();
    if (userConnection != null) {
      DataSourceLogMessageUtil.addUserConnectionToLogMessage(userConnection, logMessage);
    }
    return logMessage;
  }

}
