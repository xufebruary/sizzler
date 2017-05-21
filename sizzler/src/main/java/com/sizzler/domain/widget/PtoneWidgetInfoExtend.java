package com.sizzler.domain.widget;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneWidgetInfoExtend implements Serializable {

  private static final long serialVersionUID = -8575913665781519445L;

  @PK
  private String widgetId;
  private String value;
  private String extend;
  private String layout;
  private int isDelete;

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getExtend() {
    return extend;
  }

  public void setExtend(String extend) {
    this.extend = extend;
  }

  public String getLayout() {
    return layout;
  }

  public void setLayout(String layout) {
    this.layout = layout;
  }

  public int getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(int isDelete) {
    this.isDelete = isDelete;
  }

}
