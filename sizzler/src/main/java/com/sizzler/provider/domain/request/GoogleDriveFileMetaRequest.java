package com.sizzler.provider.domain.request;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.impl.DefaultMetaRequest;

public class GoogleDriveFileMetaRequest extends DefaultMetaRequest {
  
  private static final long serialVersionUID = 8464545976039338989L;
  
  private String fileId;

  public GoogleDriveFileMetaRequest(UserConnection userConnection) {
    super(userConnection);
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }
}
