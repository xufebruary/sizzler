package com.sizzler.provider.common.impl;

import java.util.List;
import java.util.Map;

import org.apache.metamodel.schema.MutableSchema;

import com.sizzler.common.sizzler.UserConnection;

/**
 * Created by ptmind on 2015/12/26.
 */
public class ExcelUpdateDataRequest extends DefaultUpdateDataRequest {

  private String fileId;
  private Long lastModifiedDate;

  // save:第一次打开某个文件，进入到在线excel中，然后点击 save 按钮
  // update:直接更新某个文件
  // edit_save:点击某个已经保存的文件，然后进入到在线excel中，然后点击save按钮
  private String operateType;

  // save和edit_save时，只有一个schema，当直接点击文件进行update时，会把该文件所对应的所有schema都进行更新
  private List<MutableSchema> schemaList;

  private Map<String, Map<String, Integer[]>> schemaSkipRowArrayMap;

  private Map<String, Map<String, Integer[]>> schemaSkipColArrayMap;

  private Map<String, Map<String, Integer>> schemaTitleRowMap;

  private Map<String, Map<String, Integer>> schemaIgnoreRowStartMap;

  private Map<String, Map<String, Integer>> schemaIgnoreRowEndMap;

  public ExcelUpdateDataRequest(UserConnection userConnection) {
    super(userConnection);
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public Long getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Long lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public List<MutableSchema> getSchemaList() {
    return schemaList;
  }

  public void setSchemaList(List<MutableSchema> schemaList) {
    this.schemaList = schemaList;
  }

  public String getOperateType() {
    return operateType;
  }

  public void setOperateType(String operateType) {
    this.operateType = operateType;
  }

  public Map<String, Map<String, Integer[]>> getSchemaSkipRowArrayMap() {
    return schemaSkipRowArrayMap;
  }

  public void setSchemaSkipRowArrayMap(Map<String, Map<String, Integer[]>> schemaSkipRowArrayMap) {
    this.schemaSkipRowArrayMap = schemaSkipRowArrayMap;
  }

  public Map<String, Map<String, Integer[]>> getSchemaSkipColArrayMap() {
    return schemaSkipColArrayMap;
  }

  public void setSchemaSkipColArrayMap(Map<String, Map<String, Integer[]>> schemaSkipColArrayMap) {
    this.schemaSkipColArrayMap = schemaSkipColArrayMap;
  }

  public Map<String, Map<String, Integer>> getSchemaTitleRowMap() {
    return schemaTitleRowMap;
  }

  public void setSchemaTitleRowMap(Map<String, Map<String, Integer>> schemaTitleRowMap) {
    this.schemaTitleRowMap = schemaTitleRowMap;
  }

  public Map<String, Map<String, Integer>> getSchemaIgnoreRowStartMap() {
    return schemaIgnoreRowStartMap;
  }

  public void setSchemaIgnoreRowStartMap(Map<String, Map<String, Integer>> schemaIgnoreRowStartMap) {
    this.schemaIgnoreRowStartMap = schemaIgnoreRowStartMap;
  }

  public Map<String, Map<String, Integer>> getSchemaIgnoreRowEndMap() {
    return schemaIgnoreRowEndMap;
  }

  public void setSchemaIgnoreRowEndMap(Map<String, Map<String, Integer>> schemaIgnoreRowEndMap) {
    this.schemaIgnoreRowEndMap = schemaIgnoreRowEndMap;
  }
}
