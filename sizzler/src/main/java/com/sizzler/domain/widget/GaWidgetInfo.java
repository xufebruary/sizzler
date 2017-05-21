package com.sizzler.domain.widget;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class GaWidgetInfo implements Serializable {

  private static final long serialVersionUID = 6121251389838073100L;

  @PK
  private String variableId;
  private String widgetId;
  private String panelId;
  private String accountId;
  private String propertyId;
  private String profileId;
  private String metrics; // 指标（包含过滤器信息、是否显示指标总量设置信息）
  private String ignoreNullMetrics; // 是否忽略为空的指标值, 默认为0-不忽略
  private String metricsId; // 用逗号分隔多个指标
  private String dimensions; // 维度
  private String ignoreNullDimension; // 是否忽略为空的维度值, 默认为0-不忽略
  private String dimensionsId;// 用逗号分隔多个维度
  private String segment;
  private String filters;
  private String sort;
  private String maxResult;
  private String accountName; // ga授权账号
  private String uid;
  private Long dsId;
  private String connectionId;
  private String status;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getPanelId() {
    return panelId;
  }

  public void setPanelId(String panelId) {
    this.panelId = panelId;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public Long getDsId() {
    return dsId;
  }

  public void setDsId(Long dsId) {
    this.dsId = dsId;
  }

  public String getVariableId() {
    return variableId;
  }

  public void setVariableId(String variableId) {
    this.variableId = variableId;
  }

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getPropertyId() {
    return propertyId;
  }

  public void setPropertyId(String propertyId) {
    this.propertyId = propertyId;
  }

  public String getProfileId() {
    return profileId;
  }

  public void setProfileId(String profileId) {
    this.profileId = profileId;
  }

  public String getMetrics() {
    return metrics;
  }

  public void setMetrics(String metrics) {
    this.metrics = metrics;
  }

  public String getMetricsId() {
    return metricsId;
  }

  public void setMetricsId(String metricsId) {
    this.metricsId = metricsId;
  }

  public String getDimensions() {
    return dimensions;
  }

  public void setDimensions(String dimensions) {
    this.dimensions = dimensions;
  }

  public String getDimensionsId() {
    return dimensionsId;
  }

  public void setDimensionsId(String dimensionsId) {
    this.dimensionsId = dimensionsId;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getFilters() {
    return filters;
  }

  public void setFilters(String filters) {
    this.filters = filters;
  }

  public String getSegment() {
    return segment;
  }

  public void setSegment(String segment) {
    this.segment = segment;
  }

  public String getMaxResult() {
    return maxResult;
  }

  public void setMaxResult(String maxResult) {
    this.maxResult = maxResult;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getIgnoreNullMetrics() {
    return ignoreNullMetrics;
  }

  public void setIgnoreNullMetrics(String ignoreNullMetrics) {
    this.ignoreNullMetrics = ignoreNullMetrics;
  }

  public String getIgnoreNullDimension() {
    return ignoreNullDimension;
  }

  public void setIgnoreNullDimension(String ignoreNullDimension) {
    this.ignoreNullDimension = ignoreNullDimension;
  }

  @Override
  public String toString() {
    return "GaWidgetInfo [variableId=" + variableId + ", widgetId=" + widgetId + ", accountId="
        + accountId + ", propertyId=" + propertyId + ", profileId=" + profileId + ", metrics="
        + metrics + ", metricsId=" + metricsId + ", dimensions=" + dimensions + ", dimensionsId="
        + dimensionsId + ", segment=" + segment + ", sort=" + sort + ", filters=" + filters
        + ", maxResult=" + maxResult + ", accountName=" + accountName + "]";
  }

}
