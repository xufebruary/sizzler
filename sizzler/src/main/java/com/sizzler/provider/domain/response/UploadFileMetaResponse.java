package com.sizzler.provider.domain.response;

import com.sizzler.provider.common.MetaResponse;
import com.sizzler.provider.common.file.PtoneFile;

public class UploadFileMetaResponse implements MetaResponse {

  private static final long serialVersionUID = -8200802019337055211L;

  private PtoneFile file;
  private boolean deleted;

  public PtoneFile getFile() {
    return file;
  }

  public void setFile(PtoneFile file) {
    this.file = file;
  }

  @Override
  public String getContent() {
    return null;
  }

  public boolean hasDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
