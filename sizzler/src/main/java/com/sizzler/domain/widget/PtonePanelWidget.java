package com.sizzler.domain.widget;

import java.io.Serializable;

public class PtonePanelWidget implements Serializable {

  private static final long serialVersionUID = -835460532597459482L;

  private Long ptonePanelWidgetId;
  private String panelId;
  private String widgetId;
  
  private Integer isDelete;

  public Long getPtonePanelWidgetId() {
    return ptonePanelWidgetId;
  }

  public void setPtonePanelWidgetId(Long ptonePanelWidgetId) {
    this.ptonePanelWidgetId = ptonePanelWidgetId;
  }

  public String getPanelId() {
    return panelId;
  }

  public void setPanelId(String panelId) {
    this.panelId = panelId;
  }

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public Integer getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(Integer isDelete) {
    this.isDelete = isDelete;
  }
  
}
