package com.sizzler.common.log;

/**
 * LogMessage的工具类
 */
public class LogMessageUtil {

  /** 操作信息Key：connectionId */
  public static final String OPERATE_INFO_KEY_CONNECTION_ID = "connectionId";
  /** 操作信息Key：sourceId */
  public static final String OPERATE_INFO_KEY_SOURCE_ID = "sourceId";
  /** 操作信息Key：dsCode */
  public static final String OPERATE_INFO_KEY_DS_CODE = "dsCode";
  /** 操作信息Key：uid */
  public static final String OPERATE_INFO_KEY_UID = "uid";
  /** 操作信息Key：fileId */
  public static final String OPERATE_INFO_KEY_FILE_ID = "fileId";
  /** 操作信息Key：save_flag */
  public static final String OPERATE_INFO_KEY_SAVE_FLAG = "save_flag";
  /** 操作信息Key：update_flag */
  public static final String OPERATE_INFO_KEY_UPDATE_FLAG = "update_flag";
  /** 操作信息Key：edit_save_flag */
  public static final String OPERATE_INFO_KEY_EDIT_SAVE_FLAG = "edit_save_flag";
  /** 操作信息Key：innerFunction */
  public static final String OPERATE_INFO_KEY_INNER_FUNCTION = "innerFunction";
  /** 操作信息Key：isAutoUpdate */
  public static final String OPERATE_INFO_KEY_IS_AUTO_UPDATE = "isAutoUpdate";
  /** 操作信息Key：hasDeleted */
  public static final String OPERATE_INFO_KEY_HAS_DELETED = "hasDeleted";
  /** 操作信息Key：hasChanged */
  public static final String OPERATE_INFO_KEY_HAS_CHANGED = "hasChanged";
  /** 操作信息Key：hasDisconnected */
  public static final String OPERATE_INFO_KEY_HAS_DISCONNECTED = "hasDisconnected";

  /**
   * 设置日志的基本信息，UID+Operate
   */
  public static void addBasicInfo(LogMessage logMessage, String uid, String operate) {
    logMessage.setUid(uid);
    logMessage.setOperate(operate);
  }

  /**
   * 给日志添加connectionId、sourceId、dsCode、uid<br>
   * 主要用于Excel相关的数据源，例如：Upload、S3、GD在更新、保存、编辑保存操作时的日志记录
   */
  public static void addOperateInfoOfExcel(LogMessage logMessage, String connectionId,
      String sourceId, String dsCode) {
    logMessage.addOperateInfo(OPERATE_INFO_KEY_CONNECTION_ID, connectionId)
        .addOperateInfo(OPERATE_INFO_KEY_SOURCE_ID, sourceId)
        .addOperateInfo(OPERATE_INFO_KEY_DS_CODE, dsCode);
  }

  /**
   * 给日志添加文件相关信息
   */
  public static void addOperateInfoOfFile(LogMessage logMessage, String fileId) {
    logMessage.addOperateInfo(OPERATE_INFO_KEY_FILE_ID, fileId);
  }

  /**
   * 给日志添加操作Flag信息
   */
  public static void addOperateInfoOfFlag(LogMessage logMessage, Boolean saveFlag,
      Boolean updateFlag, Boolean editSaveFlag) {
    logMessage.addOperateInfo(OPERATE_INFO_KEY_SAVE_FLAG, saveFlag)
        .addOperateInfo(OPERATE_INFO_KEY_UPDATE_FLAG, updateFlag)
        .addOperateInfo(OPERATE_INFO_KEY_EDIT_SAVE_FLAG, editSaveFlag);
  }

  /**
   * 给日志添加Flag信息
   */
  public static void addOperateInfoOfFlag(LogMessage logMessage, Boolean innerFunction,
      Boolean isAutoUpdate) {
    logMessage.addOperateInfo(OPERATE_INFO_KEY_INNER_FUNCTION, innerFunction).addOperateInfo(
        OPERATE_INFO_KEY_IS_AUTO_UPDATE, isAutoUpdate);
  }

  /**
   * 给日志添加文件Flag信息
   */
  public static void addOperateInfoOfFileFlag(LogMessage logMessage, Boolean hasDeleted,
      Boolean hasChanged, Boolean hasDisconnected) {
    logMessage.addOperateInfo(OPERATE_INFO_KEY_HAS_DELETED, hasDeleted)
        .addOperateInfo(OPERATE_INFO_KEY_HAS_CHANGED, hasChanged)
        .addOperateInfo(OPERATE_INFO_KEY_HAS_DISCONNECTED, hasDisconnected);
  }

  /**
   * 给日志添加错误信息
   */
  public static void addErrorExceptionMessage(LogMessage logMessage, String exceptionMessage) {
    logMessage.setStatus(LogMessageConstants.STATUS_ERROR);
    logMessage.setExceptionMessage(exceptionMessage);
  }

}
