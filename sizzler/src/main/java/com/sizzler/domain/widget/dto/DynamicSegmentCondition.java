package com.sizzler.domain.widget.dto;

import java.io.Serializable;

/**
 * 动态Segment中的具体关系表达式
 * 
 * @author peng.xu
 */
public class DynamicSegmentCondition implements Serializable {

  private static final long serialVersionUID = -5243200553767878103L;

  private String id; // dimension id || metrics id
  private String name; // dimension name || metrics name
  private String code; // dimension code || metrics code
  private String queryCode; // dimension queryCode || metrics queryCode （修正filter查询的code）
  private String i18nCode;
  private String dataType;
  private String type; // metrics || dimension
  private String checked;
  private String op; // == || != || =@ || ....
  private String value;
  private String rel; // or || and

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

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getQueryCode() {
    return queryCode;
  }

  public void setQueryCode(String queryCode) {
    this.queryCode = queryCode;
  }

  public String getI18nCode() {
    return i18nCode;
  }

  public void setI18nCode(String i18nCode) {
    this.i18nCode = i18nCode;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getOp() {
    return op;
  }

  public void setOp(String op) {
    this.op = op;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getRel() {
    return rel;
  }

  public void setRel(String rel) {
    this.rel = rel;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getChecked() {
    return checked;
  }

  public void setChecked(String checked) {
    this.checked = checked;
  }

}
