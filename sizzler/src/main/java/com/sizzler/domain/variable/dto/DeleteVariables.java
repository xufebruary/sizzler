package com.sizzler.domain.variable.dto;

import java.io.Serializable;

public class DeleteVariables implements Serializable {

  private static final long serialVersionUID = 1112915064634854239L;

  private String variableId;
  private Long ptoneDsInfoId;

  public String getVariableId() {
    return variableId;
  }

  public void setVariableId(String variableId) {
    this.variableId = variableId;
  }

  public Long getPtoneDsInfoId() {
    return ptoneDsInfoId;
  }

  public void setPtoneDsInfoId(Long ptoneDsInfoId) {
    this.ptoneDsInfoId = ptoneDsInfoId;
  }
}
