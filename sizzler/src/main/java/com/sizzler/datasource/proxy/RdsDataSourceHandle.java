package com.sizzler.datasource.proxy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.schema.MutableTable;

import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.log.LogMessageUtil;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.domain.dto.UIEditorData;
import com.sizzler.provider.domain.request.DataBaseEditorDataRequest;
import com.sizzler.provider.domain.request.DataBaseFileMetaRequest;
import com.sizzler.provider.domain.request.DataBaseMetaFolderRequest;
import com.sizzler.provider.domain.response.DataBaseEditorDataResponse;
import com.sizzler.provider.domain.response.DataBaseFileMetaResponse;
import com.sizzler.provider.domain.response.DataBaseMetaFolderResponse;
import com.sizzler.system.Constants;

public class RdsDataSourceHandle extends DataSourceHandle {

  @Override
  public String getAccountSchema(UserConnection userConnection, String folderId) {
    DataBaseMetaFolderRequest metaRequest = new DataBaseMetaFolderRequest(userConnection);
    metaRequest.setFolderId(folderId);
    DataBaseMetaFolderResponse dataBaseMetaResponse =
        (DataBaseMetaFolderResponse) serviceFactory.getMetaProvider().getMeta(metaRequest);
    return dataBaseMetaResponse.getContent();
  }

  @Override
  public UserConnectionSourceVo pullRemoteData(UserConnection userConnection, String folderId, String fileId) {
    DataBaseFileMetaRequest metaRequest = new DataBaseFileMetaRequest(userConnection);
    metaRequest.setDatabaseName(folderId);
    metaRequest.setTableName(fileId);
    DataBaseFileMetaResponse dataBaseMetaResponse =
        (DataBaseFileMetaResponse) serviceFactory.getMetaProvider().getMeta(metaRequest);
    UserConnectionSourceVo sourceVo =
        serviceFactory.getDataSourceBuild().buildSourceVoByPtoneFile(dataBaseMetaResponse.getFile(),
            userConnection);
    return sourceVo;
  }

  @Override
  public UIEditorData getEditData(UserConnection userConnection,
      UserConnectionSourceDto sourceDto, Boolean isAutoUpdate, Boolean isUpdate) {
    LogMessage logMessage = new LogMessage();
    UIEditorData uiEditorData = null;
    try{
      LogMessageUtil.addBasicInfo(logMessage, String.valueOf(sourceDto.getUid()), "RdsDataSourceHandle.getEditData");
      LogMessageUtil.addOperateInfoOfExcel(logMessage,
          userConnection.getConnectionId(), sourceDto.getSourceId(),
          sourceDto.getDsCode());
      LogMessageUtil.addOperateInfoOfFlag(logMessage, false, isAutoUpdate);
      DataBaseEditorDataRequest dataBaseEditorDataRequest =
          new DataBaseEditorDataRequest(userConnection);
      dataBaseEditorDataRequest.setDatabaseName(sourceDto.getFolderId());
      dataBaseEditorDataRequest.setTableName(sourceDto.getFileId());
      DataBaseEditorDataResponse dataBaseEditorDataResponse =
          (DataBaseEditorDataResponse) serviceFactory.getEditorDataProvider().getEditorData(
              dataBaseEditorDataRequest);
      // 如果文件已经更新，则需要更新 lastModifiedDate
      /*
       * if (dataBaseEditorDataResponse.hasChanged()) {
       * serviceFactory.getUserConnectionSourceService()
       * .updateLastModifiedDate(userConnectionSource.getSourceId(),
       * dataBaseEditorDataResponse.getLastModifiedDate()); }
       */
      LinkedHashMap<String, List<List>> editorData = dataBaseEditorDataResponse.getEditorData();
  
      // 更新总行数
      Map<String, Object> rowSumMap = new HashMap<>();
      rowSumMap.put(sourceDto.getName(), dataBaseEditorDataResponse.getRowCount());
      
      String uid = userConnection.getUid();
      String fileId = sourceDto.getFileId();
      Map<String, Object[]> paramMap = new HashMap<>();
      //只更新自己连接的关系型数据源
      paramMap.put("uid", new Object[] {uid});
      paramMap.put("fileId", new Object[] {fileId});
      paramMap.put("status", new Object[] {Constants.validate});
      List<UserConnectionSourceDto> sourceDtoList =
              serviceFactory.getUserConnectionSourceService().findSourceDtoByWhereIncludeTables(paramMap);
      
      SourceConfigProcess sourceConfigProcess = new SourceConfigProcess();
      MutableTable[] remoteTables = dataBaseEditorDataResponse.getSchema().getTables();
      if(remoteTables != null && remoteTables.length > 0){
        sourceConfigProcess.checkRdsColumn(remoteTables[0], sourceDtoList);
        sourceConfigProcess.updateAndSaveTableDto(remoteTables[0], sourceDtoList);
      }
      uiEditorData = new UIEditorData(sourceDtoList, dataBaseEditorDataResponse.hasDeleted(), editorData, rowSumMap);
    }catch(Exception e){
      logger.error(e.getMessage(), e);
      throw e;
    }finally{
      logger.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return uiEditorData;
  }
}
