package com.sizzler.domain.widget.vo;

import java.io.Serializable;

public class MetricsDimensionsAliasVo implements Serializable {

  private static final long serialVersionUID = -1353392679485636506L;
  
  private String widgetId;
  private String type;
  private String uuid;
  private String alias;

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }
}
