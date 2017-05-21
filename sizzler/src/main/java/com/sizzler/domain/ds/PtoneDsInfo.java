package com.sizzler.domain.ds;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneDsInfo implements Serializable {

  private static final long serialVersionUID = 778818558970165857L;
  public static final String IS_PLUS = "2";

  @PK
  private Long id;
  private String name;
  private String code;
  private String type;
  private String category;
  private String queryType;
  private String config;
  private String isSupportTemplet;
  private String description;
  private Integer orderNumber;
  private Integer isDelete;
  private String isShow;
  private String isPlus;
  private Integer orderCn;
  private Integer orderCom;
  private Integer orderJp;
  private String supportTimezone;

  public String getSupportTimezone() {
    return supportTimezone;
  }

  public void setSupportTimezone(String supportTimezone) {
    this.supportTimezone = supportTimezone;
  }

  public Integer getOrderCn() {
    return orderCn;
  }

  public void setOrderCn(Integer orderCn) {
    this.orderCn = orderCn;
  }

  public Integer getOrderCom() {
    return orderCom;
  }

  public void setOrderCom(Integer orderCom) {
    this.orderCom = orderCom;
  }

  public Integer getOrderJp() {
    return orderJp;
  }

  public void setOrderJp(Integer orderJp) {
    this.orderJp = orderJp;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIsShow() {
    return isShow;
  }

  public void setIsShow(String isShow) {
    this.isShow = isShow;
  }

  public String getIsPlus() {
    return isPlus;
  }

  public void setIsPlus(String isPlus) {
    this.isPlus = isPlus;
  }

  public Integer getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(Integer isDelete) {
    this.isDelete = isDelete;
  }

  public String getQueryType() {
    return queryType;
  }

  public void setQueryType(String queryType) {
    this.queryType = queryType;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
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

  public String getIsSupportTemplet() {
    return isSupportTemplet;
  }

  public void setIsSupportTemplet(String isSupportTemplet) {
    this.isSupportTemplet = isSupportTemplet;
  }

  @Override
  public int hashCode() {
    return code.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PtoneDsInfo) {
      PtoneDsInfo ds = (PtoneDsInfo) obj;
      return (code.equals(ds.code));
    }
    return super.equals(obj);
  }
}
