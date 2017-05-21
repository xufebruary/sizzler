package com.sizzler.provider.domain.request;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.impl.DefaultMetaRequest;

public class GoogleDriveFileListMetaRequest extends DefaultMetaRequest {

  private static final long serialVersionUID = -1612534534521802154L;

  // 默认情况下为root，即代表根目录
  private String dirId = "root";

  public GoogleDriveFileListMetaRequest(UserConnection userConnection) {
    super(userConnection);
  }

  public String getDirId() {
    return dirId;
  }

  public void setDirId(String dirId) {
    this.dirId = dirId;
  }

}
