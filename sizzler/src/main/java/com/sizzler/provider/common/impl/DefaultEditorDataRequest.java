package com.sizzler.provider.common.impl;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.EditorDataRequest;

/**
 * Created by ptmind on 2015/12/23.
 */
public class DefaultEditorDataRequest implements EditorDataRequest {

  private String hdfsPath;
  private String fileType;
  private String dsCode;
  private UserConnection userConnection;
  private String sourceType;

  public DefaultEditorDataRequest(UserConnection userConnection) {
    this.userConnection = userConnection;
  }
  
  @Override
  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  public String getHdfsPath() {
    return hdfsPath;
  }

  public void setHdfsPath(String hdfsPath) {
    this.hdfsPath = hdfsPath;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String getDsCode() {
    return dsCode;
  }

  public void setDsCode(String dsCode) {
    this.dsCode = dsCode;
  }


  @Override
  public UserConnection getUserConnection() {
    return userConnection;
  }
}
