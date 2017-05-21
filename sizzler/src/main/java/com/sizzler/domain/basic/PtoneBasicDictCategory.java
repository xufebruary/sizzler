package com.sizzler.domain.basic;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneBasicDictCategory implements Serializable {

  private static final long serialVersionUID = 4909635404636497306L;

  @PK
  private long id;
  private String name;
  private String code;
  private String description;
  private Integer orderNumber;
  private int isDelete;

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(Integer orderNumber) {
    this.orderNumber = orderNumber;
  }

  public int getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(int isDelete) {
    this.isDelete = isDelete;
  }

}
