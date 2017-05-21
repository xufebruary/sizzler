package com.sizzler.provider.common.exception;

import java.util.Map;

import org.apache.metamodel.util.Oauth2Token;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.DataRequest;
import com.sizzler.provider.common.MetaRequest;
import com.sizzler.provider.common.Oauth2CommonRequest;

/**
 * 用于Gather数据源的日志对象初始化
 * @author xin.zhang
 * 
 * @modifyBy you.zou
 * @modifyDate 2016-09-13 11:09
 * @modifyDesc 用于Gather时，需要把类放到ServiceDomain中才可以被Gather使用
 */
public class DataSourceLogMessageUtil {

  private static final String GET_META_OPERATE = "getMeta";
  private static final String GET_DATA_OPERATE = "getData";
  private static final String DS_CODE = "dsCode";
  private static final String CONNECTION_CONFIG = "connectionConfig";
  private static final String CONNECTION_ID = "connectionId";
  /** 用于标识程序运行过程中打印的日志内容 */
  private static final String INFO_KEY = "info";
  /** 日志内容分割符 */
  private static final String INFO_SPLIT = "; ";

  /**
   * 针对DataRequest类型的参数来创建并初始化LogMessage(主要为了向之前的数据源的请求接口进行兼容)
   * 同时将CommonQueryRequest中的信息设置到LogMessage中
   * @param dataRequest
   * @return
   */
  @SuppressWarnings("unchecked")
  public static LogMessage buildLogMessage(DataRequest dataRequest) {
    LogMessage logMessage = new LogMessage();
    logMessage.setOperate(GET_DATA_OPERATE);
    if (dataRequest == null) {
      return logMessage;
    }
    UserConnection userConnection = dataRequest.getUserConnection();
    if (userConnection != null) {
      addUserConnectionToLogMessage(userConnection, logMessage);
    }
    // 将CommonQueryRequest对象转换为Map
    if (dataRequest.getQueryRequest() != null) {
      Map<String, Object> queryMap =
          (Map<String, Object>) JSON.parse(JSON.toJSONString(dataRequest.getQueryRequest()));
      logMessage.addOperateInfo(queryMap);
    }
    return logMessage;
  }

  /**
   * 针对MetaRequest类型的参数来创建并初始化LogMessage（主要为了向之前的数据源的请求接口进行兼容）
   * @param metaRequest
   * @return
   */
  public static LogMessage buildLogMessage(MetaRequest metaRequest) {
    LogMessage logMessage = new LogMessage();
    logMessage.setOperate(GET_META_OPERATE);
    if (metaRequest == null) {
      return logMessage;
    }
    UserConnection userConnection = metaRequest.getUserConnection();
    if (userConnection != null) {
      addUserConnectionToLogMessage(userConnection, logMessage);
    }
    return logMessage;
  }

  /**
   * 针对MetaRequest类型的参数来创建并初始化LogMessage（主要为了向之前的数据源的请求接口进行兼容）
   * 部分数据源有多个getMeta的方法，故将调用的方法传入设置到logMessage中
   * @param metaRequest
   * @return
   */
  public static LogMessage buildLogMessage(MetaRequest metaRequest, String operate) {
    LogMessage logMessage = buildLogMessage(metaRequest);
    logMessage.setOperate(operate);
    return logMessage;
  }

  /**
   * 针对DataRequest类型的参数来创建并初始化LogMessage(主要为了向之前的数据源的请求接口进行兼容)
   * 同时将CommonQueryRequest中的信息设置到LogMessage中 部分数据源有多个getData的方法，故将调用的方法传入设置到logMessage中
   * @param dataRequest
   * @return
   */
  public static LogMessage buildLogMessage(DataRequest dataRequest, String operate) {
    LogMessage logMessage = buildLogMessage(dataRequest);
    logMessage.setOperate(operate);
    return logMessage;
  }

  /**
   * 设置UserConnection到LogMessage
   * @param userConnection
   * @param logMessage
   */
  public static void addUserConnectionToLogMessage(UserConnection userConnection,
      LogMessage logMessage) {
    if (userConnection == null) {
      return;
    }
    logMessage.setUid(userConnection.getUid());
    logMessage.addOperateInfo(DS_CODE, userConnection.getDsCode());
    logMessage.addOperateInfo(CONNECTION_ID, userConnection.getConnectionId());
//    logMessage.addOperateInfo(CONNECTION_CONFIG, userConnection.getConfig()); // 去掉config信息（因包含用户密码等敏感数据）
  }

  /**
   * 针对Oauth2CommonRequest类型的参数来创建并初始化LogMessage（主要为了向之前的数据源的请求接口进行兼容）
   * @param request
   * @param operate
   * @return
   * 
   * @modifyBy shaoqiang.guo
   * @modifyDate 2016年9月13日11:26:53
   * @modifyDesc Gather中多个数据源的operate不一致，故将operate以参数传入
   */
  public static LogMessage buildLogMessage(Oauth2CommonRequest request, String operate) {
    LogMessage logMessage = new LogMessage();
    logMessage.setOperate(operate);
    if (request == null) {
      return logMessage;
    }
    addOauth2TokenToLogMessage(request.getOauth2Token(), logMessage);
    return logMessage;
  }

  /**
   * 
   * @description 设置Oauth2Token
   * @author：shaoqiang.guo
   * @date：2016年9月13日 上午11:36:46
   * @param oauth2Token
   * @param logMessage
   */
  @SuppressWarnings("unchecked")
  private static void addOauth2TokenToLogMessage(Oauth2Token oauth2Token, LogMessage logMessage) {
    if (oauth2Token != null) {
      Map<String, Object> oauth2TokenMap =
          (Map<String, Object>) JSON.parse(JSON.toJSONString(oauth2Token));
//      logMessage.addOperateInfo(oauth2TokenMap);
    }
  }

  /**
   * 在方法执行过程中可能会产生一些需要打印出来的信息，放到OperateInfo中去<br>
   * 插入前会先检查是否已存在info信息，如果已存在，则提取出来，将新的拼接在info信息后面
   * @author you.zou
   * @date 2016年9月14日 下午8:38:50
   * @param info
   * @param logMessage
   */
  public static void addInfoToOperateInfo(String info, LogMessage logMessage) {
    Map<String, Object> operateInfo = logMessage.getOperateInfo();
    if (operateInfo.containsKey(INFO_KEY)) {
      StringBuilder infoBuilder = new StringBuilder(String.valueOf(operateInfo.get(INFO_KEY)));
      infoBuilder.append(INFO_SPLIT).append(info);
      logMessage.addOperateInfo(INFO_KEY, infoBuilder.toString());
    } else {
      logMessage.addOperateInfo(INFO_KEY, info);
    }
  }

}
