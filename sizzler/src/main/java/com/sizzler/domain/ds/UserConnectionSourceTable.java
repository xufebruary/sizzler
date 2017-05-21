package com.sizzler.domain.ds;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

/**
 * source下连接的表对应实体类
 */
public class UserConnectionSourceTable implements Serializable {

  private static final long serialVersionUID = 2236256446537152579L;

  @PK
  private String tableId;
  private String name; // 表名称
  private String code; // 表code： file： uuid ， db： table_name
  private String sourceId;
  private String connectionId;
  private Long dsId;
  private String dsCode;
  private Long uid;
  private String type; // 表类型： row (横向表，行作为表头) ||
  private String colSum; // 总列数
  private String rowSum;// 总行数
  private String headIndex; // 表头所在行数 （起始行0）
  private String headMode; // 表头类型： assign （自动分配） || custom（自定义）
  private String ignoreCol; // 忽略列列表
  private String ignoreRow; // 忽略行列表
  private String ignoreRowStart;// 忽略行起始值
  private String ignoreRowEnd;// 忽略行结束值
  private String createTime;
  private String modifyTime;
  private String status;
  private String spaceId;

  public String getTableId() {
    return tableId;
  }

  public void setTableId(String tableId) {
    this.tableId = tableId;
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

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getColSum() {
    return colSum;
  }

  public void setColSum(String colSum) {
    this.colSum = colSum;
  }

  public String getRowSum() {
    return rowSum;
  }

  public void setRowSum(String rowSum) {
    this.rowSum = rowSum;
  }

  public String getHeadIndex() {
    return headIndex;
  }

  public void setHeadIndex(String headIndex) {
    this.headIndex = headIndex;
  }

  public String getHeadMode() {
    return headMode;
  }

  public void setHeadMode(String headMode) {
    this.headMode = headMode;
  }

  public String getIgnoreCol() {
    return ignoreCol;
  }

  public void setIgnoreCol(String ignoreCol) {
    this.ignoreCol = ignoreCol;
  }

  public String getIgnoreRow() {
    return ignoreRow;
  }

  public void setIgnoreRow(String ignoreRow) {
    this.ignoreRow = ignoreRow;
  }

  public String getIgnoreRowStart() {
    return ignoreRowStart;
  }

  public void setIgnoreRowStart(String ignoreRowStart) {
    this.ignoreRowStart = ignoreRowStart;
  }

  public String getIgnoreRowEnd() {
    return ignoreRowEnd;
  }

  public void setIgnoreRowEnd(String ignoreRowEnd) {
    this.ignoreRowEnd = ignoreRowEnd;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }
}
