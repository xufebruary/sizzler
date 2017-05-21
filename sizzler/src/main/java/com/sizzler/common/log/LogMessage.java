package com.sizzler.common.log;


import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.utils.JodaDateUtil;
import com.sizzler.common.utils.StringUtil;

public class LogMessage {

  private String uid;
  private String operate;
  private Map<String, Object> operateInfo;
  private String exceptionMessage;
  private String status; //新增的状态字段，用于标识本次执行过程中是否成功
  private long startTime;
  private long endTime;
  private long executeTime;

  public LogMessage() {
    init();
  }

  private void init() {
    operateInfo = new LinkedHashMap<>();
    startTime = System.currentTimeMillis();
    status = LogMessageConstants.STATUS_SUCCESS;//日志默认都是成功状态，如果出现错误，可以再修改为错误状态
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getOperate() {
    return operate;
  }

  public void setOperate(String operate) {
    this.operate = operate;
  }

  public Map<String, Object> getOperateInfo() {
    return operateInfo;
  }

  public void setOperateInfo(Map<String, Object> operateInfo) {
    this.operateInfo = operateInfo;
  }

  public String getExceptionMessage() {
    return exceptionMessage;
  }

  public void setExceptionMessage(String exceptionMessage) {
    this.exceptionMessage = exceptionMessage;
  }

  public LogMessage addOperateInfo(String key, Object value) {
    operateInfo.put(key, value);
    return this;
  }

  public LogMessage addOperateInfo(Map<String, Object> allOperateInfo) {
    operateInfo.putAll(allOperateInfo);
    return this;
  }

  @Override
  public String toString() {
    setExecuteTime();
    StringBuilder logMessageBuilder = new StringBuilder("");
    logMessageBuilder.append("uid=").append(getUid()).append(",");
    logMessageBuilder.append("operate=").append(operate).append(",");
    if (operateInfo != null) {
      logMessageBuilder.append("operateInfo=[");
      for (Map.Entry<String, Object> entry : operateInfo.entrySet()) {
        logMessageBuilder.append(entry.getKey()).append("=")
            .append(entry.getValue() == null ? "" : entry.getValue().toString()).append(",");
      }
      logMessageBuilder.append("executeTime=" + executeTime);
      logMessageBuilder.append("]");
    }
    if(status != null){
      logMessageBuilder.append(",status=").append(status);
    }
    if (exceptionMessage != null) {
      logMessageBuilder.append(",exceptionMessage=").append(exceptionMessage);
    }

    return logMessageBuilder.toString();
  }

  public String generateJsonString() {
    LinkedHashMap<String, Object> tmpJsonMap = new LinkedHashMap<String, Object>();
    tmpJsonMap.put("ptoneUid", uid);
    tmpJsonMap.put("ptoneOperate", operate);
    tmpJsonMap.put("ptoneOperateTime", JodaDateUtil.getCurrentDateTime());
    if (operateInfo != null) {
      if (!operateInfo.containsKey("executeTime")) {
        setExecuteTime();
        operateInfo.put("executeTime", executeTime);
      }
      for (Map.Entry<String, Object> entry : operateInfo.entrySet()) {
        tmpJsonMap.put("ptone" + StringUtil.capitalize(entry.getKey()), entry.getValue());
      }
    }
    tmpJsonMap.put("ptoneStatus", status);
    tmpJsonMap.put("ptoneExceptionMessage", exceptionMessage);

    return JSON.toJSONString(tmpJsonMap);
  }

  private void setExecuteTime() {
    endTime = System.currentTimeMillis();
    executeTime = endTime - startTime;
  }

  public void setStatus(String status) {
    this.status = status;
  }
  
}
