package com.sizzler.datasource.proxy.upload;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.log.LogMessageUtil;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.datasource.proxy.ExcelDataSourceHandle;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.domain.dto.UIEditorData;
import com.sizzler.provider.common.SourceType;
import com.sizzler.provider.common.UpdateDataResponse;
import com.sizzler.provider.common.impl.ExcelEditorDataRequest;
import com.sizzler.provider.common.impl.ExcelEditorDataResponse;
import com.sizzler.provider.domain.request.UploadFileMetaRequest;
import com.sizzler.provider.domain.response.UploadFileMetaResponse;

public class UploadHandle extends ExcelDataSourceHandle {

  private Logger logger = LoggerFactory.getLogger(UploadHandle.class);

  @Override
  public String getAccountSchema(UserConnection userConnection, String folderId) {
    // return super.getAccountSchema(userConnection, folderId);
    return null;
  }

  @Override
  public UserConnectionSourceVo pullRemoteData(UserConnection userConnection, String folderId,
      String fileId) {
    UploadFileMetaRequest metaRequest = new UploadFileMetaRequest(userConnection);
    UploadFileMetaResponse uploadFileMetaResponse = (UploadFileMetaResponse) serviceFactory
        .getMetaProvider().getMeta(metaRequest);
    UserConnectionSourceVo sourceVo = serviceFactory.getDataSourceBuild().buildSourceVoByPtoneFile(
        uploadFileMetaResponse.getFile(), userConnection);
    return sourceVo;
  }

  @Override
  public UpdateDataResponse saveFileDataToDB(UserConnection userConnection,
      UserConnectionSourceVo sourceVo, UserConnectionSourceDto sourceDto, Boolean isAutoUpdate)
      throws Exception {
    return super.saveFileDataToDB(userConnection, sourceVo, sourceDto, isAutoUpdate);
  }

  @Override
  public UIEditorData getEditData(UserConnection userConnection, UserConnectionSourceDto sourceDto,
      Boolean isAutoUpdate, Boolean isUpdate) throws Exception {
    UIEditorData uiEditorData = null;
    LogMessage logMessage = new LogMessage();
    try {
      String dsCode = userConnection.getDsCode();
      String uid = userConnection.getUid();
      String fileId = sourceDto.getFileId();
      LogMessageUtil.addBasicInfo(logMessage, uid, "UploadHandle.getEditData");
      LogMessageUtil.addOperateInfoOfExcel(logMessage, userConnection.getConnectionId(),
          sourceDto.getSourceId(), dsCode);
      LogMessageUtil.addOperateInfoOfFile(logMessage, fileId);
      LogMessageUtil.addOperateInfoOfFlag(logMessage, false, isAutoUpdate);
      ExcelEditorDataRequest editorDataRequest = new ExcelEditorDataRequest(userConnection);
      editorDataRequest.setFileId(fileId);
      editorDataRequest.setLastModifiedDate(sourceDto.getLastModifiedDate());
      if (isAutoUpdate) {
        editorDataRequest.setSourceType(SourceType.AUTO_UPDATE.getValue());
      }
      ExcelEditorDataResponse excelEditorDataResponse = (ExcelEditorDataResponse) serviceFactory
          .getEditorDataProvider().getEditorData(editorDataRequest);

      // 如果不是自动更新，或者文件有修改
      List<UserConnectionSourceDto> sourceDtoList = new ArrayList<UserConnectionSourceDto>();
      sourceDtoList.add(sourceDto);

      if (isUpdate) {
        // 只有更新的时候才需要走processSheet方法
        Long processSheetStartTime = System.currentTimeMillis();
        serviceFactory.getDataSourceBuild().processSheet(
            excelEditorDataResponse.getSchema().getTables(), sourceDtoList, userConnection,
            sourceDto);
        logMessage.addOperateInfo("processSheetUsedTime",
            (System.currentTimeMillis() - processSheetStartTime));
      }
      LinkedHashMap<String, List<List>> editorData = excelEditorDataResponse.getEditorData();
      uiEditorData = new UIEditorData(sourceDtoList, editorData);
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
