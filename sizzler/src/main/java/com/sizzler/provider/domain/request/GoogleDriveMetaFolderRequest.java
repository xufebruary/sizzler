package com.sizzler.provider.domain.request;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.impl.DefaultMetaRequest;

public class GoogleDriveMetaFolderRequest extends DefaultMetaRequest {

  private static final long serialVersionUID = -235522023330696331L;

  // google email(通过该字段来确定Owner等是否为me)
  private String account;

  private String folderId;

  public GoogleDriveMetaFolderRequest(UserConnection userConnection) {
    super(userConnection);
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }
}
