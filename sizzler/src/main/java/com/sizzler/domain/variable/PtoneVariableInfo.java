package com.sizzler.domain.variable;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneVariableInfo implements Serializable {

  private static final long serialVersionUID = 9212096281427484048L;

  @PK
  private String variableId;
  private String variableName;
  private String graphName;
  private String variableColor;
  private Long variableGraphId;
  private Long ptoneDsInfoId;
  private String dateDimensionId; // 时间范围对应时间维度列ID
  private String widgetId;
  private String panelId;
  private String status;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public String getPanelId() {
    return panelId;
  }

  public void setPanelId(String panelId) {
    this.panelId = panelId;
  }

  public String getGraphName() {
    return graphName;
  }

  public void setGraphName(String graphName) {
    this.graphName = graphName;
  }

  public String getVariableId() {
    return variableId;
  }

  public void setVariableId(String variableId) {
    this.variableId = variableId;
  }

  public Long getVariableGraphId() {
    return variableGraphId;
  }

  public void setVariableGraphId(Long variableGraphId) {
    this.variableGraphId = variableGraphId;
  }

  public Long getPtoneDsInfoId() {
    return ptoneDsInfoId;
  }

  public void setPtoneDsInfoId(Long ptoneDsInfoId) {
    this.ptoneDsInfoId = ptoneDsInfoId;
  }

  public String getVariableName() {
    return variableName;
  }

  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }

  public String getVariableColor() {
    return variableColor;
  }

  public void setVariableColor(String variableColor) {
    this.variableColor = variableColor;
  }

  public String getDateDimensionId() {
    return dateDimensionId;
  }

  public void setDateDimensionId(String dateDimensionId) {
    this.dateDimensionId = dateDimensionId;
  }

}
