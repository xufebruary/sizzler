package com.sizzler.domain.panel;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PanelGlobalComponent implements Serializable {

  private static final long serialVersionUID = -2445540869762463282L;

  public static final String COMPONENT_ITEM_ID_GLOBAL_TIME = "16"; // 全局时间组件id
  public static final String COMPONENT_ITEM_CODE_GLOBAL_TIME = "GLOBAL_TIME"; // 全局时间组件code
  public static final String COMPONENT_GLOBAL_TIME_DEFAULT_VALUE = "widgetTime"; // 全局时间组件默认值

  @PK
  private Long id;
  private String panelId;
  private String itemId;
  private String code;
  private String name;
  private String value;
  private String status;
  private String uid;

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPanelId() {
    return panelId;
  }

  public void setPanelId(String panelId) {
    this.panelId = panelId;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
