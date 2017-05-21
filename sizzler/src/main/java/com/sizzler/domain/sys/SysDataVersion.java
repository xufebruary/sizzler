package com.sizzler.domain.sys;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class SysDataVersion implements Serializable {

  private static final long serialVersionUID = 4826151829089638886L;

  @PK
  private long id;
  private String name;
  private String code;
  private String version;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

}
