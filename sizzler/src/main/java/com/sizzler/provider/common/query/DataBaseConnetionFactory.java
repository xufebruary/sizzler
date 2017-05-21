package com.sizzler.provider.common.query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sizzler.common.exception.ErrorCode;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.sizzler.DataBaseConnection;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.provider.common.db.DataBaseType;
import com.sizzler.provider.common.query.connection.pool.DataBaseConnectionInfo;
import com.sizzler.provider.common.query.connection.pool.DataBaseConnectionPool;

/**
 * Created by ptmind on 2015/11/11.
 */
public class DataBaseConnetionFactory {
  private static final Logger log = Logger.getLogger(DataBaseConnetionFactory.class);

  /*
   * public static Connection createConnection(UserConnection userConnection, String databaseName)
   * throws JSchException { DataBaseConnection dataBaseConnection =
   * MetaUtil.createDataBaseConnectionByUserConnection(userConnection); if
   * (dataBaseConnection.getDataBaseName() == null ||
   * dataBaseConnection.getDataBaseName().equals("")) {
   * dataBaseConnection.setDataBaseName(databaseName); } return
   * createConnection(dataBaseConnection); }
   */

  /**
   * 对外提供创建连接的入口，目前当出现异常进行JDBC直连
   * @author shaoqiang.guo
   * @date 2016年12月28日 下午5:13:56
   * @param dataBaseConnection
   * @return dataBaseConnectionInfo
   * @throws JSchException
   */
  public static DataBaseConnectionInfo createConnection(DataBaseConnection dataBaseConnection)
      throws JSchException {
    DataBaseConnectionPool dataBaseConnectionPool =
        DataBaseConnectionPool.getDataBaseConnectionPool();
    DataBaseConnectionInfo dataBaseConnectionInfo = null;
    try {
      if (DataBaseConnectionPool.isSSh(dataBaseConnection)) {
        dataBaseConnectionInfo = createConnectionJDBC(dataBaseConnection);
      } else {
        dataBaseConnectionInfo =
            dataBaseConnectionPool.createQueryDataBaseConnection(dataBaseConnection);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      // 没有创建Session，修改
      dataBaseConnectionInfo = createConnectionJDBC(dataBaseConnection);
      if (dataBaseConnectionInfo != null) {
        log.info("create jdbc connection successful!");
        return dataBaseConnectionInfo;
      }
      throw DataBaseConnetionFactory.buildServiceException(e.getMessage(), e);
    }
    return dataBaseConnectionInfo;
  }

  /**
   *
   * 获取数据库连接，没有连接池
   * @author shaoqiang.guo
   * @date 2016年12月26日 上午10:19:15
   * @param dataBaseConnection
   * @return dataBaseConnectionInfo
   * @throws JSchException
   */
  public static DataBaseConnectionInfo createConnectionJDBC(DataBaseConnection dataBaseConnection)
      throws JSchException {
    DataBaseConnectionInfo dataBaseConnectionInfo = new DataBaseConnectionInfo();
    Connection connection = null;
    Session session = null;
    ConnectionInfo connectionInfo = new ConnectionInfo();
    try {
      session = DataBaseConnectionPool.sshConnection(dataBaseConnection);
      DataBaseType dataBaseType =
          DataBaseType.fromValue(dataBaseConnection.getDataBaseType().toUpperCase());
      DataBaseDriverInfo dataBaseDriverInfo =
          DataBaseDriverCache.getDataBaseDriverInfo(dataBaseType);
      String driverClass = dataBaseDriverInfo.getDriver();
      connectionInfo.setDriverClass(driverClass);

      StringBuilder urlBuilder = new StringBuilder();
      urlBuilder.append(dataBaseDriverInfo.getUrlPrefix()).append(dataBaseConnection.getHost())
          .append(":");
      if (dataBaseConnection.getPort() == null || dataBaseConnection.getPort().equals("")) {
        urlBuilder.append(dataBaseDriverInfo.getDefaultPort());
      } else {
        urlBuilder.append(dataBaseConnection.getPort());
      }
      if (dataBaseConnection.getDataBaseName() == null) {
        dataBaseConnection.setDataBaseName("");
      }
      // http://blog.csdn.net/renfufei/article/details/39316751
      // oracle 是通过 : 来连接数据库的
      // sqlserver2005 是通过 ; DatabaseName= 来连接的
      String type = dataBaseType.name();
      if (StringUtil.isNotBlank(type)) {
        if (type.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
          urlBuilder.append(";DatabaseName=").append(dataBaseConnection.getDataBaseName());
        } else {
          urlBuilder.append("/").append(dataBaseConnection.getDataBaseName());
        }
      }
      if (StringUtil.isNotBlank(dataBaseDriverInfo.getUrlParam())) {
        urlBuilder.append(dataBaseDriverInfo.getUrlParam());
      }
      String url = urlBuilder.toString();
      log.info("database-url:" + url);

      connectionInfo.setUrl(url);
      connectionInfo.setUser(dataBaseConnection.getUser());
      connectionInfo.setPassword(dataBaseConnection.getPassword());

      dataBaseConnectionInfo.setSession(session);
      connection = createConnection(connectionInfo);
      dataBaseConnectionInfo.setConnection(connection);
    } catch (Exception e) {
      DataBaseConnectionPool.closeSession(session);
      LogMessage logMessage = new LogMessage();
      logMessage.setUid(dataBaseConnection.getUid());
      logMessage.setOperate("Connect DB");
      LinkedHashMap<String, Object> operateInfo = new LinkedHashMap<String, Object>();
      operateInfo.put("connectionId", dataBaseConnection.getConnectionId());
      operateInfo.put("connectionName", dataBaseConnection.getConnectionName());
      operateInfo.put("driverClass", connectionInfo.getDriverClass());
      operateInfo.put("url", connectionInfo.getUrl());
      operateInfo.put("user", connectionInfo.getUser());
      operateInfo.put("password", connectionInfo.getPassword());
      logMessage.setOperateInfo(operateInfo);
      logMessage.setExceptionMessage(e.toString());

      log.error(logMessage.toString(), e);
      throw DataBaseConnetionFactory.buildServiceException(e.getMessage(), e);
    }
    return dataBaseConnectionInfo;
  }

  public static Connection createConnection(ConnectionInfo connectionInfo) throws Exception {
    Connection connection = null;
    try {
      Class.forName(connectionInfo.getDriverClass());
      connection =
          DriverManager.getConnection(connectionInfo.getUrl(), connectionInfo.getUser(),
              connectionInfo.getPassword());
    } catch (Exception e) {
      e.printStackTrace();
      throw DataBaseConnetionFactory.buildServiceException(e.getMessage(), e);
    }

    return connection;
  }

  public static void closeConnection(Connection connection) {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static ServiceException buildServiceException(String errorMsg, Exception e) {
    ServiceException se = new ServiceException(errorMsg);
    errorMsg = (errorMsg == null ? "" : errorMsg);
    if (errorMsg.startsWith("Access denied for user")) {
      se.setErrorCode(ErrorCode.CODE_DB_ACCESS_DENIED);
      se.setErrorMsg(ErrorCode.MSG_DB_ACCESS_DENIED);
    } else if (errorMsg.startsWith("Communications link failure")) {
      se.setErrorCode(ErrorCode.CODE_DB_LINK_FAILURE);
      se.setErrorMsg(ErrorCode.MSG_DB_LINK_FAILURE);
    } else if (errorMsg.startsWith("Unknown database")) {
      se.setErrorCode(ErrorCode.CODE_DB_UNKNOWN_DATABASE);
      se.setErrorMsg(ErrorCode.MSG_DB_UNKNOWN_DATABASE);
    } else if (errorMsg.matches("Table(.*)doesn't exist(.*)")) {
      se.setErrorCode(ErrorCode.CODE_DB_UNKNOWN_TABLE);
      se.setErrorMsg(ErrorCode.MSG_DB_UNKNOWN_TABLE);
    } else if (errorMsg.startsWith("Invalid object name")) {
      se.setErrorCode(ErrorCode.CODE_DB_UNKNOWN_TABLE);
      se.setErrorMsg(ErrorCode.MSG_DB_UNKNOWN_TABLE);
    } else if (errorMsg.startsWith("Unknown column")) {
      se.setErrorCode(ErrorCode.CODE_DB_UNKNOWN_COLUMN);
      se.setErrorMsg(ErrorCode.MSG_DB_UNKNOWN_COLUMN);
    } else {
      se.setErrorCode(ErrorCode.CODE_FAILED);
      se.setErrorMsg(ErrorCode.MSG_FAILED);
    }

    return se;
  }


}
