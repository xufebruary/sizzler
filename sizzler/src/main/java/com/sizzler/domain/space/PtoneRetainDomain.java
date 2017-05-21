package com.sizzler.domain.space;

import java.io.Serializable;

public class PtoneRetainDomain implements Serializable {

  private static final long serialVersionUID = 8348237866201463390L;

  public static final String TYPE_STRING = "string"; // 字符串匹配
  public static final String TYPE_REGEXP = "regexp"; // 正则匹配

  private String id;
  private String type;
  private String code;
  private String status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
