package com.sizzler.provider.domain;

import com.sizzler.provider.common.file.PtoneFile;

public class PtoneDriveFile extends PtoneFile {

  private static final long serialVersionUID = 7045058820302504996L;

  private Boolean shared = false;
  private String sharingUserName;
  private Long sharedWithMeDate;

  public Boolean getShared() {
    return shared;
  }

  public void setShared(Boolean shared) {
    this.shared = shared;
  }

  public String getSharingUserName() {
    return sharingUserName;
  }

  public void setSharingUserName(String sharingUserName) {
    this.sharingUserName = sharingUserName;
  }

  public Long getSharedWithMeDate() {
    return sharedWithMeDate;
  }

  public void setSharedWithMeDate(Long sharedWithMeDate) {
    this.sharedWithMeDate = sharedWithMeDate;
  }
}
