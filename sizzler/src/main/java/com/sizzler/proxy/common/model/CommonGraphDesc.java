package com.sizzler.proxy.common.model;

import java.io.Serializable;

import com.sizzler.cache.CurrentUserCache;
import com.sizzler.proxy.dispatcher.ChartDataType;
import com.sizzler.proxy.dispatcher.PtoneDatasourceGraphDesc;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;

public class CommonGraphDesc extends PtoneDatasourceGraphDesc implements Serializable {

  private static final long serialVersionUID = -1694863580832844943L;

  private String variableName;
  private String dateKey;
  private Number goals;
  private CurrentUserCache currentUserCache;
  private Boolean useDatetimeAxis; // 是否使用datetime类型x轴
  private PtoneWidgetParam ptoneWidgetParam;
  private ModelData modelData;

  public CommonGraphDesc(String datasourceType, ChartDataType chartDataType) {
    super(datasourceType, chartDataType);
  }

  public String getVariableName() {
    return variableName;
  }

  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }

  public String getDateKey() {
    return dateKey;
  }

  public void setDateKey(String dateKey) {
    this.dateKey = dateKey;
  }

  public Number getGoals() {
    return goals;
  }

  public void setGoals(Number goals) {
    this.goals = goals;
  }

  public CurrentUserCache getCurrentUserCache() {
    return currentUserCache;
  }

  public void setCurrentUserCache(CurrentUserCache currentUserCache) {
    this.currentUserCache = currentUserCache;
  }

  public Boolean getUseDatetimeAxis() {
    return useDatetimeAxis;
  }

  public void setUseDatetimeAxis(Boolean useDatetimeAxis) {
    this.useDatetimeAxis = useDatetimeAxis;
  }

  public PtoneWidgetParam getPtoneWidgetParam() {
    return ptoneWidgetParam;
  }

  public void setPtoneWidgetParam(PtoneWidgetParam ptoneWidgetParam) {
    this.ptoneWidgetParam = ptoneWidgetParam;
  }

  public ModelData getModelData() {
    return modelData;
  }

  public void setModelData(ModelData modelData) {
    this.modelData = modelData;
  }
}
