package com.sizzler.datasource.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.metamodel.schema.MutableSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.log.LogMessageUtil;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.domain.dto.UIEditorData;
import com.sizzler.provider.common.SourceType;
import com.sizzler.provider.common.UpdateDataResponse;
import com.sizzler.provider.common.impl.ExcelEditorDataRequest;
import com.sizzler.provider.common.impl.ExcelEditorDataResponse;
import com.sizzler.provider.common.impl.ExcelUpdateDataRequest;
import com.sizzler.provider.domain.request.GoogleDriveFileMetaRequest;
import com.sizzler.provider.domain.request.GoogleDriveMetaFolderRequest;
import com.sizzler.provider.domain.response.GoogleDriveFileMetaResponse;
import com.sizzler.provider.domain.response.GoogleDriveMetaFolderResponse;
import com.sizzler.system.Constants;

/**
 * @ClassName: ExcelDataSourceHandle
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2015/12/24
 * @author: zhangli
 */
public class ExcelDataSourceHandle extends DataSourceHandle {

  private Logger logger = LoggerFactory.getLogger(ExcelDataSourceHandle.class);

  @Override
  public String getAccountSchema(UserConnection userConnection, String folderId) {
    GoogleDriveMetaFolderRequest googleDriveMetaFolderRequest =
        new GoogleDriveMetaFolderRequest(userConnection);
    googleDriveMetaFolderRequest.setAccount(userConnection.getName());
    googleDriveMetaFolderRequest.setFolderId(folderId);
    GoogleDriveMetaFolderResponse googleDriveMetaFolderResponse =
        (GoogleDriveMetaFolderResponse) serviceFactory.getMetaProvider().getMeta(
            googleDriveMetaFolderRequest);
    return googleDriveMetaFolderResponse.getContent();
  }

  @Override
  public UserConnectionSourceVo pullRemoteData(UserConnection userConnection, String folderId, String fileId) {
    GoogleDriveFileMetaRequest metaRequest = new GoogleDriveFileMetaRequest(userConnection);
    metaRequest.setFileId(fileId);
    GoogleDriveFileMetaResponse googleDriveFileMetaResponse =
        (GoogleDriveFileMetaResponse) serviceFactory.getMetaProvider().getMeta(metaRequest);
    UserConnectionSourceVo sourceVo =
        serviceFactory.getDataSourceBuild().buildSourceVoByPtoneFile(
            googleDriveFileMetaResponse.getFile(), userConnection);
    // 第一次增加文件时，如果远端已删除，则更改状态
    if (googleDriveFileMetaResponse.hasDeleted()) {
      sourceVo.setRemoteStatus(Constants.inValidate);
    }
    return sourceVo;
  }

  // updateData时调用该方法
  @Override
  public UpdateDataResponse saveFileDataToDB(UserConnection userConnection,
      UserConnectionSourceVo sourceVo, UserConnectionSourceDto sourceDto, Boolean isAutoUpdate)
      throws Exception {
    LogMessage logMessage = new LogMessage();
    ExcelUpdateDataRequest updateDataRequest = new ExcelUpdateDataRequest(userConnection);
    UpdateDataResponse response = null;
    try {
      String fileId = sourceVo.getFileId();
      String logUid = String.valueOf(sourceVo.getUid());
      LogMessageUtil.addBasicInfo(logMessage, logUid, "ExcelDataSourceHandle.saveFileDataToDB");
      LogMessageUtil.addOperateInfoOfFile(logMessage, fileId);
      LogMessageUtil.addOperateInfoOfExcel(logMessage, userConnection.getConnectionId(),
          sourceDto.getSourceId(), userConnection.getDsCode());
      LogMessageUtil.addOperateInfoOfFlag(logMessage, false, isAutoUpdate);
      // 声明忽略行、忽略列、忽略行开始行、忽略行结束行的Map对象
      // 子Map以TableName作为Key
      Map<String, Map<String, Integer[]>> schemaSkipRowArrayMap = new TreeMap<>();
      Map<String, Map<String, Integer[]>> schemaSkipColArrayMap = new TreeMap<>();
      Map<String, Map<String, Integer>> schemaIgnoreRowStartMap = new TreeMap<>();
      Map<String, Map<String, Integer>> schemaIgnoreRowEndMap = new TreeMap<>();
      List<MutableSchema> schemaList = new ArrayList<MutableSchema>();
      serviceFactory.getDataSourceBuild().getSchemaListAndIgnoreList(sourceVo, sourceDto,
          schemaSkipRowArrayMap, schemaSkipColArrayMap, schemaIgnoreRowStartMap,
          schemaIgnoreRowEndMap, schemaList);
      updateDataRequest.setSchemaList(schemaList);
      updateDataRequest.setFileId(fileId);
      if (isAutoUpdate) {
        updateDataRequest.setSourceType(String.valueOf(SourceType.AUTO_UPDATE));
        sourceVo.setOperateType(Constants.UI_OPERATE_UPDATE);
      }
      updateDataRequest.setOperateType(sourceVo.getOperateType());
      updateDataRequest.setLastModifiedDate(sourceVo.getLastModifiedDate());
      updateDataRequest.setSchemaSkipColArrayMap(schemaSkipColArrayMap);
      updateDataRequest.setSchemaSkipRowArrayMap(schemaSkipRowArrayMap);
      updateDataRequest.setSchemaIgnoreRowStartMap(schemaIgnoreRowStartMap);
      updateDataRequest.setSchemaIgnoreRowEndMap(schemaIgnoreRowEndMap);

      response = serviceFactory.getUpdateDataProvider().updateData(updateDataRequest);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw e;
    } finally {
      logger.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return response;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public UIEditorData getEditData(UserConnection userConnection,
      UserConnectionSourceDto sourceDto, Boolean isAutoUpdate, Boolean isUpdate) throws Exception {
    UIEditorData uiEditorData = null;
    LogMessage logMessage = new LogMessage();
    try {
      String dsCode = userConnection.getDsCode();
      String uid = userConnection.getUid();
      String fileId = sourceDto.getFileId();
      String sourceId = sourceDto.getSourceId();
      LogMessageUtil.addBasicInfo(logMessage, uid, "ExcelDataSourceHandle.getEditData");
      LogMessageUtil.addOperateInfoOfExcel(logMessage, userConnection.getConnectionId(), sourceId,
          dsCode);
      LogMessageUtil.addOperateInfoOfFile(logMessage, fileId);
      LogMessageUtil.addOperateInfoOfFlag(logMessage, false, isAutoUpdate);
      ExcelEditorDataRequest editorDataRequest = new ExcelEditorDataRequest(userConnection);
      editorDataRequest.setFileId(fileId);
      editorDataRequest.setLastModifiedDate(sourceDto.getLastModifiedDate());
      if (isAutoUpdate) {
        editorDataRequest.setSourceType(SourceType.AUTO_UPDATE.getValue());
      }
      ExcelEditorDataResponse excelEditorDataResponse =
          (ExcelEditorDataResponse) serviceFactory.getEditorDataProvider().getEditorData(
              editorDataRequest);
      Boolean hasChanged = excelEditorDataResponse.hasChanged();
      Boolean hasDeleted = excelEditorDataResponse.hasDeleted();
      Boolean hasDisconnected = excelEditorDataResponse.hasDisconnected();

      LogMessageUtil.addOperateInfoOfFileFlag(logMessage, hasDeleted, hasChanged, hasDisconnected);

      List<UserConnectionSourceDto> sourceDtoList = new ArrayList<UserConnectionSourceDto>();
      // 如果文件已经更新，则需要更新 lastModifiedDate
      if (hasChanged) {
        serviceFactory.getUserConnectionSourceService().updateLastModifiedDate(sourceId,
            excelEditorDataResponse.getLastModifiedDate());
      }
      // 设置无效
      if (hasDeleted || hasDisconnected) {
        // 更新UserConnectionSource表中的remoteStatus状态
        serviceFactory.getUserConnectionSourceService().updateConnectionSourceRemoteStatus(
            Constants.inValidate, fileId);
      } else {// 为了处理已删除的文件从回收站恢复的情况

        // 更新UserConnectionSource表中的remoteStatus状态
        serviceFactory.getUserConnectionSourceService().updateConnectionSourceRemoteStatus(
            Constants.validate, fileId);
        if (!isAutoUpdate || hasChanged) {
          // 如果不是自动更新，或者文件有修改
          Map<String, Object[]> paramMap = new HashMap<>();
          if (isAutoUpdate) {
            sourceDtoList.add(sourceDto);
          } else {
            paramMap.put("uid", new Object[] {uid});
            paramMap.put("fileId", new Object[] {fileId});
            paramMap.put("status", new Object[] {Constants.validate});
            sourceDtoList =
                serviceFactory.getUserConnectionSourceService().findSourceDtoByWhereIncludeTables(
                    paramMap);
          }
          Long buildSourceMapStartTime = System.currentTimeMillis();
          logMessage.addOperateInfo("buildSourceMapUsedTime",
              (System.currentTimeMillis() - buildSourceMapStartTime));
          Long processSheetStartTime = System.currentTimeMillis();
          serviceFactory.getDataSourceBuild().processSheet(
              excelEditorDataResponse.getSchema().getTables(), sourceDtoList, userConnection,
              sourceDto);
          logMessage.addOperateInfo("processSheetUsedTime",
              (System.currentTimeMillis() - processSheetStartTime));
        }
      }
      LinkedHashMap<String, List<List>> editorData = excelEditorDataResponse.getEditorData();
      uiEditorData = new UIEditorData(hasChanged, hasDeleted, hasDisconnected, sourceDtoList, editorData);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw e;
    } finally {
      logger.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return uiEditorData;
  }

}
