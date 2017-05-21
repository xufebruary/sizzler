package com.sizzler.domain.ds.dto;

import java.io.Serializable;

public class DsContentView implements Serializable {

  private static final long serialVersionUID = 2991443169439102947L;

  private String dsCode;
  private String dsName;
  private String dsId;
  private String dsOrderNumber;
  private String dsConfig;
  private String isPlus;

  private Integer nameNum = 0;
  private Integer accountNum = 0;

  public String getIsPlus() {
    return isPlus;
  }

  public void setIsPlus(String isPlus) {
    this.isPlus = isPlus;
  }

  public String getDsConfig() {
    return dsConfig;
  }

  public void setDsConfig(String dsConfig) {
    this.dsConfig = dsConfig;
  }

  public String getDsOrderNumber() {
    return dsOrderNumber;
  }

  public void setDsOrderNumber(String dsOrderNumber) {
    this.dsOrderNumber = dsOrderNumber;
  }

  public String getDsName() {
    return dsName;
  }

  public void setDsName(String dsName) {
    this.dsName = dsName;
  }

  public String getDsId() {
    return dsId;
  }

  public void setDsId(String dsId) {
    this.dsId = dsId;
  }

  public String getDsCode() {
    return dsCode;
  }

  public void setDsCode(String dsCode) {
    this.dsCode = dsCode;
  }

  public Integer getNameNum() {
    return nameNum;
  }

  public void setNameNum(Integer nameNum) {
    this.nameNum = nameNum;
  }

  public Integer getAccountNum() {
    return accountNum;
  }

  public void setAccountNum(Integer accountNum) {
    this.accountNum = accountNum;
  }
}
