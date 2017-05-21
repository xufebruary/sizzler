package com.sizzler.provider.domain.request;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.impl.DefaultMetaRequest;

public class DataBaseMetaFolderRequest extends DefaultMetaRequest {

  private static final long serialVersionUID = -604481738669466779L;

  public static final String ROOT_FOLDER_ID = "ptoneRootFolderID::connection";
  public static final String TYPE_DATABASE = "database";
  public static final String TYPE_TABLE = "table";

  private String folderId;

  public DataBaseMetaFolderRequest(UserConnection userConnection) {
    super(userConnection);
  }

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

}
