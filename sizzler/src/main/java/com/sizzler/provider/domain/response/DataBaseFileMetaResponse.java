package com.sizzler.provider.domain.response;

import com.sizzler.provider.common.MetaResponse;
import com.sizzler.provider.common.file.PtoneFile;

public class DataBaseFileMetaResponse implements MetaResponse {

  private static final long serialVersionUID = -7825839471278381217L;

  private PtoneFile file;

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

}
