package com.sizzler.datasource.proxy.mysql;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.datasource.proxy.RdsDataSourceHandle;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.domain.dto.UIEditorData;
import com.sizzler.provider.common.UpdateDataResponse;

public class MysqlHandle extends RdsDataSourceHandle {

  @Override
  public String getAccountSchema(UserConnection userConnection, String folderId) {
    return super.getAccountSchema(userConnection, folderId);
  }

  @Override
  public UserConnectionSourceVo pullRemoteData(UserConnection userConnection, String folderId, String fileId) {
    return super.pullRemoteData(userConnection, folderId, fileId);
  }

  @Override
  public UpdateDataResponse saveFileDataToDB(UserConnection userConnection,
      UserConnectionSourceVo sourceVo, UserConnectionSourceDto sourceDto, 
      Boolean isAutoUpdate) throws Exception {
    return null;
  }

  @Override
  public UIEditorData getEditData(UserConnection userConnection,
      UserConnectionSourceDto sourceDto, Boolean isAutoUpdate, Boolean isUpdate) {
    return super.getEditData(userConnection, sourceDto, isAutoUpdate, isUpdate);
  }
}
