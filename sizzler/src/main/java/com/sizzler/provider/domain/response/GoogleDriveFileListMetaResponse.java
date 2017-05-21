package com.sizzler.provider.domain.response;

import java.util.List;

import com.sizzler.provider.common.MetaResponse;
import com.sizzler.provider.domain.PtoneDriveFile;

public class GoogleDriveFileListMetaResponse implements MetaResponse {

  private static final long serialVersionUID = 2131184901993897505L;

  private PtoneDriveFile currentPtoneDriveFile;
  private List<PtoneDriveFile> fileList;

  public List<PtoneDriveFile> getFileList() {
    return fileList;
  }

  public void setFileList(List<PtoneDriveFile> fileList) {
    this.fileList = fileList;
  }

  public PtoneDriveFile getCurrentPtoneDriveFile() {
    return currentPtoneDriveFile;
  }

  public void setCurrentPtoneDriveFile(PtoneDriveFile currentPtoneDriveFile) {
    this.currentPtoneDriveFile = currentPtoneDriveFile;
  }

  @Override
  public String getContent() {
    return null;
  }
}
