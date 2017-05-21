package com.sizzler.provider.domain;

import com.sizzler.provider.common.file.PtoneFile;

public class PtoneSpliterFile extends PtoneFile {
  
  private static final long serialVersionUID = -7145880834197151028L;
  
  private String spliter;


  public String getSpliter() {
    return spliter;
  }

  public void setSpliter(String spliter) {
    this.spliter = spliter;
  }


}
