package com.sizzler.domain.ds.vo;

import java.io.Serializable;

public class UserConnectionSourceTableColumnVo implements Serializable {

  private static final long serialVersionUID = -6790827843188022994L;

  private String name;// 列名
  private String id;// uuid
  private String code;
  private String type;// 指标、维度、忽略
  private String dataType;// 数据类型
  private String columnType;// 数据源原始列类型
  private Long index;
  private String dataFormat;
  private String isIgnore = "0";
  private String isCustom;

  public String getColumnType() {
    return columnType;
  }

  public void setColumnType(String columnType) {
    this.columnType = columnType;
  }

  public String getIsCustom() {
    return isCustom;
  }

  public void setIsCustom(String isCustom) {
    this.isCustom = isCustom;
  }

  public String getIsIgnore() {
    return isIgnore;
  }

  public void setIsIgnore(String isIgnore) {
    this.isIgnore = isIgnore;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public Long getIndex() {
    return index;
  }

  public void setIndex(Long index) {
    this.index = index;
  }

  public String getDataFormat() {
    return dataFormat;
  }

  public void setDataFormat(String dataFormat) {
    this.dataFormat = dataFormat;
  }
}
