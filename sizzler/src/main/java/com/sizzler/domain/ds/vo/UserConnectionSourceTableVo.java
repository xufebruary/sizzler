package com.sizzler.domain.ds.vo;

import java.io.Serializable;
import java.util.List;

public class UserConnectionSourceTableVo implements Serializable {

  private static final long serialVersionUID = -611636626806906588L;

  private String id;
  private String code;
  private String name;
  private String headType;// 表头是列还是行
  private String colSum;// 原始数据总列
  private String rowSum;// 原始数据总行
  private Long headIndex;// 表头是第几列或行
  private List<Integer> ignoreRow;
  private Integer ignoreRowStart; // 头N行忽略
  private Integer ignoreRowEnd; // 尾N行忽略
  private List<Integer> ignoreCol;
  private Integer ignoreColStart; // 头N列忽略
  private Integer ignoreColEnd; // 尾N列忽略
  private String headMode;
  @SuppressWarnings("rawtypes")
  private List<List> data;
  private List<UserConnectionSourceTableColumnVo> schema;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getHeadMode() {
    return headMode;
  }

  public void setHeadMode(String headMode) {
    this.headMode = headMode;
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

  public String getHeadType() {
    return headType;
  }

  public void setHeadType(String headType) {
    this.headType = headType;
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

  public Long getHeadIndex() {
    return headIndex;
  }

  public void setHeadIndex(Long headIndex) {
    this.headIndex = headIndex;
  }

  public List<Integer> getIgnoreRow() {
    return ignoreRow;
  }

  public void setIgnoreRow(List<Integer> ignoreRow) {
    this.ignoreRow = ignoreRow;
  }

  public Integer getIgnoreRowStart() {
    return ignoreRowStart;
  }

  public void setIgnoreRowStart(Integer ignoreRowStart) {
    this.ignoreRowStart = ignoreRowStart;
  }

  public Integer getIgnoreRowEnd() {
    return ignoreRowEnd;
  }

  public void setIgnoreRowEnd(Integer ignoreRowEnd) {
    this.ignoreRowEnd = ignoreRowEnd;
  }

  public List<Integer> getIgnoreCol() {
    return ignoreCol;
  }

  public void setIgnoreCol(List<Integer> ignoreCol) {
    this.ignoreCol = ignoreCol;
  }

  public Integer getIgnoreColStart() {
    return ignoreColStart;
  }

  public void setIgnoreColStart(Integer ignoreColStart) {
    this.ignoreColStart = ignoreColStart;
  }

  public Integer getIgnoreColEnd() {
    return ignoreColEnd;
  }

  public void setIgnoreColEnd(Integer ignoreColEnd) {
    this.ignoreColEnd = ignoreColEnd;
  }

  @SuppressWarnings("rawtypes")
  public List<List> getData() {
    return data;
  }

  @SuppressWarnings("rawtypes")
  public void setData(List<List> data) {
    this.data = data;
  }

  public List<UserConnectionSourceTableColumnVo> getSchema() {
    return schema;
  }

  public void setSchema(List<UserConnectionSourceTableColumnVo> schema) {
    this.schema = schema;
  }
}
