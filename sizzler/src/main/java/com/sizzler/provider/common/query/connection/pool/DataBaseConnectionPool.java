package com.sizzler.provider.common.query.connection.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.log.LogMessageUtil;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.sizzler.DataBaseConnection;
import com.sizzler.common.utils.Base64;
import com.sizzler.common.utils.GetFreePortUtil;
import com.sizzler.common.utils.SpringContextUtil;
import com.sizzler.common.utils.SshUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.provider.common.db.DataBaseType;
import com.sizzler.provider.common.query.DataBaseConnetionFactory;
import com.sizzler.provider.common.query.DataBaseDriverCache;
import com.sizzler.provider.common.query.DataBaseDriverInfo;

public class DataBaseConnectionPool {

  private static final Logger log = LoggerFactory.getLogger(DataBaseConnectionPool.class);

  private final static String druidDataSourceConfig = "druidDataSourceConfig";
  private final static String VERTICAL_LINE = "|";
  private final static String MANDATORY = "Mandatory";
  private final static String NORMAL = "Normal";

  /** 标识ssh是否有效 */
  private final static String VALID = "1";

  private static DataBaseConnectionPool dataBaseConnectionPool = new DataBaseConnectionPool();
  /** 调度任务对象 */
  private static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
  /** 标记为active状态，避免初始化多次 */
  private static boolean isActive = false;
  /** 连接池对象集合 */
  private static Map<String, DruidDataSourceInfo> connectionPoolMap =
      new ConcurrentHashMap<String, DruidDataSourceInfo>();

  /** 读取相关配置 */
  private static DruidDataSourceConfig config = SpringContextUtil.getBean(druidDataSourceConfig);

  /** 单例对象 */
  public static DataBaseConnectionPool getDataBaseConnectionPool() {
    return dataBaseConnectionPool;
  }

  /** 存放连接池Key的集合，用于同步 */
  private static Map<String, String> connectionPoolKeyMap = new HashMap<String, String>();

  /**
   * 初始化 connectionPoolMap维护任务
   * @author shaoqiang.guo
   * @date: 2017年1月3日11:09:30
   */
  static {
    if (!isActive) {
      maintainConnectionPool();
      isActive = true;
    }
  }

  /**
   * 从连接池中获取连接
   * @author shaoqiang.guo
   * @date 2016年12月26日 上午10:57:59
   * @param dataBaseConnection
   * @return dataBaseConnectionInfo
   * @throws Exception
   */
  public DataBaseConnectionInfo createQueryDataBaseConnection(DataBaseConnection dataBaseConnection)
      throws Exception {
    LogMessage logMessage = new LogMessage();
    logMessage.setUid(dataBaseConnection.getUid());
    DataBaseConnectionInfo dataBaseConnectionInfo = new DataBaseConnectionInfo();

    try {
      // 使用连接池
      if (config.isUseConectionPool()) {
        // 构建唯一的Key
        String connectionPoolkey = buildConnectionPoolKey(dataBaseConnection);
        putKeyToConnectionPoolKeyMap(connectionPoolkey);
        // 获取连接池对象
        DruidPooledConnection connection = null;
        DruidDataSourceInfo druidDataSource = null;
        druidDataSource = createConnectionPool(connectionPoolkey, dataBaseConnection, logMessage);
        connection = druidDataSource.getConnection();
        dataBaseConnectionInfo.setConnection(connection);
        if (connection != null && connection.getConnectionHolder() != null) {
          DruidConnectionHolder druidConnectionHolder = connection.getConnectionHolder();
          logMessage.addOperateInfo("druidPooledConnectionInfo", druidConnectionHolder.toString());
        }
        // 设置最后活跃时间，方便维护connectionPoolMap
        druidDataSource.setLastActiveTime(System.currentTimeMillis());
        // 日志相关
        buildLogMessage(druidDataSource, logMessage);
      } else {
        // 不使用连接池
        dataBaseConnectionInfo = DataBaseConnetionFactory.createConnectionJDBC(dataBaseConnection);
        logMessage.setOperate("Don't use the connection pool to create the connection!");
      }
    } catch (Exception e) {
      LogMessageUtil.addErrorExceptionMessage(logMessage, e.getMessage());
      log.error(e.getMessage(), e);
      throw e;
    } finally {
      // 打印日志
      log.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return dataBaseConnectionInfo;
  }

  /**
   * 判断connectionPoolKeyMap中是否存在该connectionPoolkey， 不存在时将该connectionPoolkey
   * put到connectionPoolKeyMap中
   * @author shaoqiang.guo
   * @date 2016年12月30日 下午5:50:39
   * @param connectionPoolkey
   */
  private static void putKeyToConnectionPoolKeyMap(String connectionPoolkey) {
    if (!connectionPoolKeyMap.containsKey(connectionPoolkey)) {
      connectionPoolKeyMap.put(connectionPoolkey, connectionPoolkey);
    }
  }

  /**
   * 该方法有两种操作，创建、获取：<br>
   * 1、连接池已经存在，直接从connectionPoolMap中获取连接池对象<br>
   * 2、连接池不存在创建连接池对象。
   * @author shaoqiang.guo
   * @date 2016年12月29日 上午10:19:37
   * @param dataBaseConnection
   * @return key
   * @throws Exception
   */
  private DruidDataSourceInfo createConnectionPool(String connectionPoolKey,
      DataBaseConnection dataBaseConnection, LogMessage logMessage) throws Exception {
    synchronized (connectionPoolKeyMap.get(connectionPoolKey)) {
      // 先判断连接池Map集合中containsKey(key)，存在则直接拿
      if (connectionPoolMap.containsKey(connectionPoolKey)) {
        logMessage.setOperate("get connectionPool successful!");
        return connectionPoolMap.get(connectionPoolKey);
      } else {
        // 对不同的数据库生成不同的dataBaseDriver
        DataBaseType dataBaseType =
            DataBaseType.fromValue(dataBaseConnection.getDataBaseType().toUpperCase());
        DataBaseDriverInfo dataBaseDriverInfo =
            DataBaseDriverCache.getDataBaseDriverInfo(dataBaseType);
        String url = buildConnectionPoolUrl(dataBaseDriverInfo, dataBaseConnection, dataBaseType);
        DruidDataSourceInfo druidDataSource = new DruidDataSourceInfo();
        // 将config中的配置属性赋值给druidDataSource
        BeanUtils.copyProperties(config, druidDataSource);
        // setKey
        druidDataSource.setName(connectionPoolKey);
        // set对应的链接信息
        druidDataSource.setDriverClassName(dataBaseDriverInfo.getDriver());
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(dataBaseConnection.getUser());
        druidDataSource.setPassword(ConfigTools.encrypt(dataBaseConnection.getPassword()));
        connectionPoolMap.put(connectionPoolKey, druidDataSource);
        logMessage.setOperate("create connectionPool successful!");
        return druidDataSource;
      }
    }
  }

  /**
   * 
   * 定时执行维护连接池
   * @author shaoqiang.guo
   * @date 2016年12月30日 下午5:53:54
   */
  private static void maintainConnectionPool() {
    log.info("start the destruction ConnectionPool task");
    scheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        try {
          // 执行强制关闭连接池的任务
          mandatoryDestroyMaintainTask();
          // 执行正常维护连接池的任务
          normalDestroyMaintainTask();
        } catch (Exception e) {
          log.error("start the destruction ConnectionPool task error:" + e.getMessage(), e);
        }
      }
    }, config.getInitialDelay(), config.getDelay(), config.getUnit());
    log.info("start the destruction ConnectionPool task success");
  }

  /***
   * 输出connectionPoolMap信息
   * @author shaoqiang.guo
   * @date 2017年1月18日 下午6:26:24
   */
  private static void printConnectionPoolMapInfo() {
    LogMessage logMessage = new LogMessage();
    logMessage.setOperate("printConnectionPoolMapInfo");
    for (Map.Entry<String, DruidDataSourceInfo> entry : connectionPoolMap.entrySet()) {
      buildConnectionPoolInfo(entry.getValue(), logMessage);
    }
    logMessage.addOperateInfo("connectionPoolCount", connectionPoolMap.size());
    log.info(logMessage.toString());
  }

  /**
   * 
   * 初始化正常维护连接池的任务
   * @author shaoqiang.guo
   * @date 2017年1月3日11:08:58
   */
  private static void normalDestroyMaintainTask() {
    List<String> destroyKeyList = maintainTask(NORMAL);
    LogMessage logMessage = buildLogMessageByDestroy(NORMAL, destroyKeyList);
    log.info(logMessage.toString());
    printConnectionPoolMapInfo();
  }

  /**
   * 当连接池数量大于一定数量时，开始按照连接时间开始强制销毁连接池的任务
   * @author shaoqiang.guo
   * @date 2017年1月3日 上午9:23:25
   */
  private static void mandatoryDestroyMaintainTask() {
    if (connectionPoolMap.size() < config.getConectionPoolMapMaxSize()) {
      return;
    }
    List<String> destroyKeyList = maintainTask(MANDATORY);
    LogMessage logMessage = buildLogMessageByDestroy(MANDATORY, destroyKeyList);
    log.info(logMessage.toString());
  }

  /**
   * 遍历出所需要销毁的连接池
   * @author shaoqiang.guo
   * @date 2017年1月5日 下午7:21:01
   * @param task
   * @return destroyKeyList
   */
  private static List<String> maintainTask(String task) {
    long destroyTime = 0L;
    if (task.equalsIgnoreCase(NORMAL)) {
      destroyTime = config.getDestroyTime();
    } else {
      destroyTime = config.getMandatoryDestroyTime();
    }
    List<String> destroyKeyList = new ArrayList<String>();
    Iterator<Map.Entry<String, DruidDataSourceInfo>> it = connectionPoolMap.entrySet().iterator();
    // 遍历出所以需要销毁的连接池。
    while (it.hasNext()) {
      Map.Entry<String, DruidDataSourceInfo> entry = it.next();
      String key = entry.getKey();
      synchronized (connectionPoolKeyMap.get(key)) {
        DruidDataSourceInfo dsInfo = entry.getValue();
        if (dsInfo != null) {
          long timeDifference = System.currentTimeMillis() - dsInfo.getLastActiveTime();
          if (timeDifference >= destroyTime) {
            // 关闭连接池
            dsInfo.close();
            // 关闭session
            // closeSession(dsInfo.getSession());
            connectionPoolMap.remove(key);
            // 用于记录日志
            destroyKeyList.add(key);
          }
        }
      }
    }
    return destroyKeyList;
  }

  /**
   * 构建唯一的连接池的Key
   * @author shaoqiang.guo
   * @date 2016年12月28日 下午4:48:22
   * @param isSshConnection
   * @param url
   * @param userName
   * @return connectionPoolkey
   */
  private static String buildConnectionPoolKey(DataBaseConnection dataBaseConnection) {
    String dataBaseType = dataBaseConnection.getDataBaseType();
    StringBuilder connectionPoolkey = new StringBuilder(dataBaseType);
    connectionPoolkey.append(VERTICAL_LINE).append(dataBaseConnection.getHost());
    connectionPoolkey.append(":").append(dataBaseConnection.getPort());
    // postgre必须包括数据库名
    if (dataBaseType.equalsIgnoreCase(DataBaseConfig.DB_CODE_POSTGRE)) {
      connectionPoolkey.append(VERTICAL_LINE).append(dataBaseConnection.getDataBaseName());
    }
    connectionPoolkey.append(VERTICAL_LINE).append(dataBaseConnection.getUser());
    if (dataBaseConnection.getSsh() != null && dataBaseConnection.getSsh().equals(VALID)) {
      connectionPoolkey.append(VERTICAL_LINE).append("ssh");
      connectionPoolkey.append(VERTICAL_LINE).append(dataBaseConnection.getSshHost());
      connectionPoolkey.append(VERTICAL_LINE).append(dataBaseConnection.getSshUser());
      connectionPoolkey.append(VERTICAL_LINE).append(
          Base64.byteArrayToBase64(dataBaseConnection.getSshPassword().getBytes()));
    }
    return connectionPoolkey.toString();
  }

  /**
   * 
   * 对ssh连接支持;
   * @author shaoqiang.guo
   * @date 2016年12月26日 上午10:46:50
   * @param dataBaseConnection
   * @throws JSchException
   */
  public static Session sshConnection(DataBaseConnection dataBaseConnection) throws JSchException {
    Session session = null;
    if (isSSh(dataBaseConnection)) {
      session = SshUtil.createSession(dataBaseConnection);
      session.connect();
      Integer freePort = GetFreePortUtil.findFreePort();
      Integer mysqlPort = Integer.valueOf(dataBaseConnection.getPort());
      // 设置端口的转发
      Integer assignedPort =
          session.setPortForwardingL(freePort, dataBaseConnection.getHost(), mysqlPort);
      dataBaseConnection.setPort(assignedPort + "");
      dataBaseConnection.setHost("localhost");
      log.info("creat session successful!");
    }
    return session;
  }

  /**
   * 是否是ssh连接
   * @author shaoqiang.guo
   * @date 2017年2月22日 下午6:28:06
   * @param dataBaseConnection
   * @return
   */
  public static boolean isSSh(DataBaseConnection dataBaseConnection) {
    return dataBaseConnection.getSsh() != null && dataBaseConnection.getSsh().equals(VALID);
  }

  /**
   * 
   * 关闭session
   * @author shaoqiang.guo
   * @date 2017年1月18日 上午10:41:01
   * @param session
   */
  public static void closeSession(Session session) {
    if (session == null || !session.isConnected()) {
      return;
    }
    session.disconnect();
    log.info("close session successful!");
  }

  /**
   * 
   * 构建数据库url
   * @author shaoqiang.guo
   * @date 2016年12月26日 上午10:55:28
   * @param dataBaseDriverInfo
   * @param dataBaseConnection
   * @param dataBaseType
   * @return urlBuilder
   * @throws JSchException
   */
  private static String buildConnectionPoolUrl(DataBaseDriverInfo dataBaseDriverInfo,
      DataBaseConnection dataBaseConnection, DataBaseType dataBaseType) throws JSchException {

    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(dataBaseDriverInfo.getUrlPrefix()).append(dataBaseConnection.getHost())
        .append(":");
    String port = dataBaseConnection.getPort();
    if (StringUtil.isBlank(port)) {
      urlBuilder.append(dataBaseDriverInfo.getDefaultPort());
    } else {
      urlBuilder.append(port);
    }

    // postgre必须包含数据库
    String type = dataBaseConnection.getDataBaseType();
    if (StringUtil.isNotBlank(type)) {
      if (type.equalsIgnoreCase(DataBaseConfig.DB_CODE_POSTGRE)) {
        urlBuilder.append("/").append(dataBaseConnection.getDataBaseName());
      }
    }

    if (StringUtil.isNotBlank(dataBaseDriverInfo.getUrlParam())) {
      urlBuilder.append(dataBaseDriverInfo.getUrlParam());
    }
    return urlBuilder.toString();
  }

  /**
   * 创建维护连接池线程线程的日志对象
   * @author shaoqiang.guo
   * @date 2017年1月3日 上午11:03:23
   * @param taskName
   * @param destroyKeyList
   * @return logMessage
   */
  private static LogMessage buildLogMessageByDestroy(String taskName, List<String> destroyKeyList) {
    LogMessage logMessage = new LogMessage();
    logMessage.setOperate(taskName + "_DestroyConnectionPool");
    logMessage.addOperateInfo(taskName + "_DestroyCount", destroyKeyList.size());
    logMessage.addOperateInfo(taskName + "_DestroyConnectionPoolKey",
        JSON.toJSONString(destroyKeyList));
    return logMessage;
  }

  /**
   * 
   * @description 创建日志对象
   * @author shaoqiang.guo
   * @date 2016年10月20日 上午10:29:38
   * @param druidDataSource
   * @param logMessage
   */
  private static void buildLogMessage(DruidDataSourceInfo druidDataSource, LogMessage logMessage) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("key", druidDataSource.getName());
    map.put("id", druidDataSource.getID());
    map.put("createTime", druidDataSource.getCreatedTime());
    map.put("lastActiveTime", druidDataSource.getLastActiveTime());
    map.put("activeCount", druidDataSource.getActiveCount());
    map.put("poolingCount", druidDataSource.getPoolingCount());
    map.put("createCount", druidDataSource.getCreateCount());
    map.put("activeConnectionStackTrace", druidDataSource.getActiveConnectionStackTrace());
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
    logMessage.addOperateInfo(druidDataSource.getName(), map);
  }

  /**
   * 
   * @description 打印连接池Map中所有的连接池对象
   * @author shaoqiang.guo
   * @date 2016年10月20日 上午10:29:38
   * @param druidDataSource
   * @param logMessage
   */
  private static void buildConnectionPoolInfo(DruidDataSourceInfo druidDataSource,
      LogMessage logMessage) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("id", druidDataSource.getID());
    map.put("createTime", druidDataSource.getCreatedTime());
    map.put("lastActiveTime", druidDataSource.getLastActiveTime());
    map.put("createCount", druidDataSource.getCreateCount());
    map.put("connectCount", druidDataSource.getConnectCount());
    logMessage.addOperateInfo(druidDataSource.getName(), map);
  }
}
