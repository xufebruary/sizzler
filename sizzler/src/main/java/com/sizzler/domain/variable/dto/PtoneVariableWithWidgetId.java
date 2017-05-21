package com.sizzler.domain.variable.dto;

import java.io.Serializable;

import com.sizzler.domain.variable.PtoneVariableInfo;

public class PtoneVariableWithWidgetId extends PtoneVariableInfo implements Serializable {

  private static final long serialVersionUID = -7286059100345137384L;

  private String widgetId;

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }
}
