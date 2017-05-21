package com.sizzler.domain.basic.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sizzler.domain.basic.PtoneBasicChartInfo;

public class PtoneBasicChartInfoDto implements Serializable {

  private static final long serialVersionUID = -1157273839216762642L;

  private long id;
  private String name;
  private String code;
  private String dataType;
  private String type;// chart or tool
  private Object config;
  private String description;
  private Integer orderNumber;
  private int isDelete;

  public PtoneBasicChartInfoDto() {
  }

  @SuppressWarnings("unchecked")
  public PtoneBasicChartInfoDto(PtoneBasicChartInfo chartInfo) {
    Map<String, Object> configObj = new HashMap<String, Object>();
    String configStr = chartInfo.getConfig();
    if (configStr != null && !"".equals(configStr)) {
      configObj = JSON.parseObject(configStr, HashMap.class);
    }

    this.setId(chartInfo.getId());
    this.setName(chartInfo.getName());
    this.setCode(chartInfo.getCode());
    this.setDataType(chartInfo.getDataType());
    this.setType(chartInfo.getType());
    this.setConfig(configObj);
    this.setDescription(chartInfo.getDescription());
    this.setOrderNumber(chartInfo.getOrderNumber());
    this.setIsDelete(chartInfo.getIsDelete());

  }

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

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Object getConfig() {
    return config;
  }

  public void setConfig(Object config) {
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

  public int getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(int isDelete) {
    this.isDelete = isDelete;
  }

}
