package com.sizzler.domain.pmission;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneSysOperation implements Serializable {

  private static final long serialVersionUID = 6260957742435440021L;
  
  private String id;
  @PK
  private String operationId;
  private String name;
  private String code;
  private String handler;
  private String status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setOperationId(String operationId) {
    this.operationId = operationId;
  }

  public String getOperationId() {
    return operationId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public void setHandler(String handler) {
    this.handler = handler;
  }

  public String getHandler() {
    return handler;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
