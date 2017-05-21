package com.sizzler.domain.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sizzler.domain.ds.dto.UserConnectionSourceDto;

public class UIEditorData implements Serializable {

  private static final long serialVersionUID = 6865561056278984320L;

  private Boolean hasChanged;// 文件在远端是否已被改变
  private Boolean hasDeleted;// 文件在远端是否已被删除
  private Boolean hasDisconnected;// 链接是否已被远程断开

  private List<UserConnectionSourceDto> sourceDtos;// SourceDto列表

  private LinkedHashMap<String, List<List>> editorData;// 文件对应的前200行数据，Key对应着TableName

  private Map<String, Object> rowSumMap;

  /**
   * 提供给Excel类型的使用的构造函数
   */
  public UIEditorData(Boolean hasChanged, Boolean hasDeleted, Boolean hasDisconnected,
      List<UserConnectionSourceDto> sourceDtos, LinkedHashMap<String, List<List>> editorData) {
    super();
    this.hasChanged = hasChanged;
    this.hasDeleted = hasDeleted;
    this.hasDisconnected = hasDisconnected;
    this.sourceDtos = sourceDtos;
    this.editorData = editorData;
  }

  /**
   * 提供给Upload使用的构造函数
   */
  public UIEditorData(List<UserConnectionSourceDto> sourceDtos,
      LinkedHashMap<String, List<List>> editorData) {
    super();
    this.sourceDtos = sourceDtos;
    this.editorData = editorData;
  }

  /**
   * 提供给关系型数据库使用的构造函数
   */
  public UIEditorData(List<UserConnectionSourceDto> sourceDtos, Boolean hasDeleted,
      LinkedHashMap<String, List<List>> editorData, Map<String, Object> rowSumMap) {
    super();
    this.sourceDtos = sourceDtos;
    this.editorData = editorData;
    this.rowSumMap = rowSumMap;
    this.hasDeleted = hasDeleted;
  }

  public UIEditorData() {
    super();
  }

  public Boolean getHasChanged() {
    return hasChanged;
  }

  public void setHasChanged(Boolean hasChanged) {
    this.hasChanged = hasChanged;
  }

  public Boolean getHasDeleted() {
    return hasDeleted;
  }

  public void setHasDeleted(Boolean hasDeleted) {
    this.hasDeleted = hasDeleted;
  }

  public Boolean getHasDisconnected() {
    return hasDisconnected;
  }

  public void setHasDisconnected(Boolean hasDisconnected) {
    this.hasDisconnected = hasDisconnected;
  }

  public List<UserConnectionSourceDto> getSourceDtos() {
    return sourceDtos;
  }

  public void setSourceDtos(List<UserConnectionSourceDto> sourceDtos) {
    this.sourceDtos = sourceDtos;
  }

  public LinkedHashMap<String, List<List>> getEditorData() {
    return editorData;
  }

  public void setEditorData(LinkedHashMap<String, List<List>> editorData) {
    this.editorData = editorData;
  }

  public Map<String, Object> getRowSumMap() {
    return rowSumMap;
  }

  public void setRowSumMap(Map<String, Object> rowSumMap) {
    this.rowSumMap = rowSumMap;
  }

}
