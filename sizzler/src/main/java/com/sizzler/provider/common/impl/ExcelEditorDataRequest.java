package com.sizzler.provider.common.impl;

import com.sizzler.common.sizzler.UserConnection;

/**
 * 用于统一Upload、S3、GD等文件数据源的请求对象
 * @author you.zou
 * @date 2016年11月4日 下午12:01:32
 */
public class ExcelEditorDataRequest extends DefaultEditorDataRequest {
  
  private static final long serialVersionUID = 1735053562017660157L;
  private String fileId;
  private Long lastModifiedDate;

  public ExcelEditorDataRequest(UserConnection userConnection) {
    super(userConnection);
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public Long getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Long lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }
}
