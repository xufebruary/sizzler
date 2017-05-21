package com.sizzler.domain.widget;

import java.io.Serializable;

public class PtoneWidgetVariable implements Serializable {

  private static final long serialVersionUID = -6581395280019070024L;

  private Long ptoneWidgetVariableId;
  private String widgetId;
  private String variableId;
  /**软删除标识符 by shaoqiang.guo*/
  private Integer isDelete;
  
  public Long getPtoneWidgetVariableId() {
    return ptoneWidgetVariableId;
  }

  public void setPtoneWidgetVariableId(Long ptoneWidgetVariableId) {
    this.ptoneWidgetVariableId = ptoneWidgetVariableId;
  }

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public String getVariableId() {
    return variableId;
  }

  public void setVariableId(String variableId) {
    this.variableId = variableId;
  }

  public Integer getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(Integer isDelete) {
    this.isDelete = isDelete;
  }
  
}
