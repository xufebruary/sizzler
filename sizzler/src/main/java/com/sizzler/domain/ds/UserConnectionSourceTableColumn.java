package com.sizzler.domain.ds;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class UserConnectionSourceTableColumn implements Serializable {

  private static final long serialVersionUID = 3878438039516971087L;

  @PK
  private String colId;
  private String name; // 列名称
  private String code; // 列code， file： uuid， db： column_name
  private String dataType; // 数据类型
  private String dataFormat;// 数据格式
  private String unit; // 单位 （目前没有用）
  private String type; // 类型： metrics || dimension || ignore
  private String tableId;
  private String sourceId;
  private String connectionId;
  private Long dsId;
  private String dsCode;
  private Long uid; // 用户id
  private String isIgnore; // 是否忽略
  private String isCustom; // 是否用户自定义
  private String columnType;// 数据源原始列类型
  private Long colIndex; // 列索引顺序号， 起始值 0
  private String createTime;
  private String modifyTime;
  private String status;
  private String spaceId;

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

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getTableId() {
    return tableId;
  }

  public void setTableId(String tableId) {
    this.tableId = tableId;
  }

  public String getColId() {
    return colId;
  }

  public void setColId(String colId) {
    this.colId = colId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(String connectionId) {
    this.connectionId = connectionId;
  }

  public Long getDsId() {
    return dsId;
  }

  public void setDsId(Long dsId) {
    this.dsId = dsId;
  }

  public String getDsCode() {
    return dsCode;
  }

  public void setDsCode(String dsCode) {
    this.dsCode = dsCode;
  }

  public Long getUid() {
    return uid;
  }

  public void setUid(Long uid) {
    this.uid = uid;
  }

  public String getDataFormat() {
    return dataFormat;
  }

  public void setDataFormat(String dataFormat) {
    this.dataFormat = dataFormat;
  }

  public Long getColIndex() {
    return colIndex;
  }

  public void setColIndex(Long colIndex) {
    this.colIndex = colIndex;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(String modifyTime) {
    this.modifyTime = modifyTime;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

}
