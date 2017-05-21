package com.sizzler.proxy.variable.model;

import com.sizzler.cache.CurrentUserCache;
import com.sizzler.proxy.common.model.CommonQueryParam;
import com.sizzler.proxy.common.model.ModelData;
import com.sizzler.proxy.dispatcher.ChartDataType;
import com.sizzler.proxy.dispatcher.PtoneGraphVariableDataDesc;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;

public class GraphVariableDataDesc extends PtoneGraphVariableDataDesc {

  private static final long serialVersionUID = 4851500677185631968L;

  private String variableName;
  private String dateKey;
  private Number goals;
  private CurrentUserCache currentUserCache;
  private Boolean useDatetimeAxis; // 是否使用datetime类型x轴
  private PtoneWidgetParam ptoneWidgetParam;
  private CommonQueryParam queryParam;
  private ModelData modelData;
  private ModelData lastModelData;

  public GraphVariableDataDesc(ChartDataType chartDataType, ModelData modelData) {
    super(chartDataType);
    this.modelData = modelData;
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

  public CommonQueryParam getQueryParam() {
    return queryParam;
  }

  public void setQueryParam(CommonQueryParam queryParam) {
    this.queryParam = queryParam;
  }

  public ModelData getModelData() {
    return modelData;
  }

  public void setModelData(ModelData modelData) {
    this.modelData = modelData;
  }

  public ModelData getLastModelData() {
    return lastModelData;
  }

  public void setLastModelData(ModelData lastModelData) {
    this.lastModelData = lastModelData;
  }

}
