package com.sizzler.provider.domain.request;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.impl.DefaultMetaRequest;

public class UploadFileMetaRequest extends DefaultMetaRequest {

  private static final long serialVersionUID = 661828137734167657L;

  private String fileId;

  public UploadFileMetaRequest(UserConnection userConnection) {
    super(userConnection);
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }
}
