package com.sizzler.proxy.dispatcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * widget数据结构对象
 * 
 * @author peng.xu
 * 
 */
public class PtoneWidgetData implements Serializable {

  private static final long serialVersionUID = -2324634393660208452L;

  private String widgetId;
  private List<String> categories = new ArrayList<String>(); // 只有column、bar图需要categories
  private List<Object> data = new ArrayList<Object>();
  private String datePeriod;
  private List<String> availableDatePeriod = new ArrayList<String>();
  private Number maxValue;
  private Number minValue;
  private Number goals;
  private GraphType graphType;
  private String status = "success";
  private String errorCode;
  private String errorMsg;
  private String errorLogs;
  private String sortType; // default || metricsValue || dimensionValue
  private String orderType; // defaut (默认)、 date(日期)、dataValue（数值）
  private String orderRule; // asc、 desc
  private String orderColumn;// 排序的列（指标列）
  private Integer max;
  private String showOthers;
  private String dsCode;
  private String startDate;
  private String endDate;
  private Boolean isCacheData = false;
  
  private Map<String, Map<String, Object>> metricsAmountsMap =
      new HashMap<String, Map<String, Object>>(); // <metricsKey,Amount>
  private Map<String, Object> extInfo = new HashMap<String, Object>();

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public List<Object> getData() {
    return data;
  }

  public void setData(List<Object> data) {
    this.data = data;
  }

  public void addData(Object value) {
    this.data.add(value);
  }

  public String getDatePeriod() {
    return datePeriod;
  }

  public void setDatePeriod(String datePeriod) {
    this.datePeriod = datePeriod;
  }

  public List<String> getAvailableDatePeriod() {
    return availableDatePeriod;
  }

  public void setAvailableDatePeriod(List<String> availableDatePeriod) {
    this.availableDatePeriod = availableDatePeriod;
  }

  public Number getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(Number maxValue) {
    this.maxValue = maxValue;
  }

  public Number getMinValue() {
    return minValue;
  }

  public void setMinValue(Number minValue) {
    this.minValue = minValue;
  }

  public Number getGoals() {
    return goals;
  }

  public void setGoals(Number goals) {
    this.goals = goals;
  }

  public GraphType getGraphType() {
    return graphType;
  }

  public void setGraphType(GraphType graphType) {
    this.graphType = graphType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public String getErrorLogs() {
    return errorLogs;
  }

  public void setErrorLogs(String errorLogs) {
    this.errorLogs = errorLogs;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public String getOrderRule() {
    return orderRule;
  }

  public void setOrderRule(String orderRule) {
    this.orderRule = orderRule;
  }

  public Map<String, Map<String, Object>> getMetricsAmountsMap() {
    return metricsAmountsMap;
  }

  public void setMetricsAmountsMap(Map<String, Map<String, Object>> metricsAmountsMap) {
    this.metricsAmountsMap = metricsAmountsMap;
  }

  public Map<String, Object> getExtInfo() {
    return extInfo;
  }

  public void setExtInfo(Map<String, Object> extInfo) {
    this.extInfo = extInfo;
  }

  public String getOrderColumn() {
    return orderColumn;
  }

  public void setOrderColumn(String orderColumn) {
    this.orderColumn = orderColumn;
  }

  public Integer getMax() {
    return max;
  }

  public void setMax(Integer max) {
    this.max = max;
  }

  public String getSortType() {
    return sortType;
  }

  public void setSortType(String sortType) {
    this.sortType = sortType;
  }

  public String getShowOthers() {
    return showOthers;
  }

  public void setShowOthers(String showOthers) {
    this.showOthers = showOthers;
  }
  
  public String getDsCode() {
    return dsCode;
  }

  public void setDsCode(String dsCode) {
    this.dsCode = dsCode;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }
  
  public Boolean getIsCacheData() {
    return isCacheData;
  }

  public void setIsCacheData(Boolean isCacheData) {
    this.isCacheData = isCacheData;
  }

  @Override
  public String toString() {
    return "PtoneWidgetData [widgetId=" + widgetId + ", data=" + JSON.toJSONString(data)
        + ", datePeriod=" + datePeriod + ", availableDatePeriod=" + availableDatePeriod
        + ", maxValue=" + maxValue + ", minValue=" + minValue + ", goals=" + goals + ", graphType="
        + graphType + ", status=" + status + ", errorCode=" + errorCode + ", errorMsg=" + errorMsg
        + ", errorLogs=" + errorLogs + ", orderType=" + orderType + ", orderRule=" + orderRule
        + ", metricsAmountsMap=" + JSON.toJSONString(metricsAmountsMap) + "]";
  }

}
