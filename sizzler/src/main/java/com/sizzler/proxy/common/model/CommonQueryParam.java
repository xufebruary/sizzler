package com.sizzler.proxy.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ptmind.common.utils.StringUtil;
import com.sizzler.cache.CurrentUserCache;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;

public class CommonQueryParam implements Serializable {

  private static final long serialVersionUID = 2574001068273408367L;

  private String profileId;// 档案ID or tableID
  private String metrics; // code
  private String metricsId;
  private List<PtoneMetricsDimension> metricsList;
  private String dimensions; // code
  private String dimensionsId;
  private List<PtoneMetricsDimension> dimensionsList;
  private List<PtoneMetricsDimension> fixedDimensionsList;
  private List<PtoneMetricsDimension> xAxisDateDimensionList =
      new ArrayList<PtoneMetricsDimension>(); // x轴对应的时间维度
  private PtoneMetricsDimension xAxisDateDimension; // x轴对应的时间维度
  private boolean useDateDimensionInSelect; // 判断时间维度是否作为查询维度（在使用时间轴时，用户没有时间维度将时间维度增加到select中）
  private String filters;
  private String segment;
  private String sort;
  private Integer startIndex;
  private Integer maxResults;
  private String startDate;
  private String endDate;
  private String datePeriod;
  private String dateKey;
  private CurrentUserCache currentUserCache;
  private Map<String, PtoneMetricsDimension> metricsDimensionMap =
      new LinkedHashMap<String, PtoneMetricsDimension>();
  private boolean useDatetimeAxis; // create by you.zou 用于在API的日期处理时，判断返回的数据类型是yyyy-MM-dd hh:mm:ss
                                   // 或者 yyyy-MM-dd-hh-mm-ss

  // /////////////////////////////////////////////////////////

  public List<String> getMetricsIdList() {
    List<String> mList = new ArrayList<String>();
    if (metricsList != null) {
      for (PtoneMetricsDimension m : metricsList) {
        mList.add(m.getId());
      }
    }
    return mList;
  }

  public String getMetricsId() {
    if (this.metricsId == null && metricsList != null) {
      List<String> mList = this.getMetricsIdList();
      this.setMetricsId(StringUtil.join(mList, ","));
    }
    return this.metricsId;
  }

  public List<String> getMetricsCodeList() {
    List<String> mList = new ArrayList<String>();
    if (metricsList != null) {
      for (PtoneMetricsDimension m : metricsList) {
        mList.add(m.getCode());
      }
    }
    return mList;
  }

  public String getMetrics() {
    if (this.metrics == null && metricsList != null) {
      List<String> mList = this.getMetricsCodeList();
      this.setMetrics(StringUtil.join(mList, ","));
    }
    return this.metrics;
  }

  public List<String> getDimensionsIdList() {
    List<String> dList = new ArrayList<String>();
    if (dimensionsList != null) {
      for (PtoneMetricsDimension d : dimensionsList) {
        dList.add(d.getId());
      }
    }
    return dList;
  }

  public String getDimensionsId() {
    if (this.dimensionsId == null && dimensionsList != null) {
      List<String> dList = this.getDimensionsIdList();
      this.setDimensionsId(StringUtil.join(dList, ","));
    }
    return this.dimensionsId;
  }

  public List<String> getDimensionsCodeList() {
    List<String> dList = new ArrayList<String>();
    if (dimensionsList != null) {
      for (PtoneMetricsDimension d : dimensionsList) {
        dList.add(d.getCode());
      }
    }
    return dList;
  }

  public String getDimensions() {
    if (this.dimensions == null && dimensionsList != null) {
      List<String> dList = this.getDimensionsCodeList();
      this.setDimensions(StringUtil.join(dList, ","));
    }
    return this.dimensions;
  }

  public List<String> getXAxisDateDimensionCodeList() {
    List<String> xAxisDateDimensionCodeList = new ArrayList<String>();
    if (this.xAxisDateDimensionList != null && !this.xAxisDateDimensionList.isEmpty()) {
      for (PtoneMetricsDimension d : this.xAxisDateDimensionList) {
        xAxisDateDimensionCodeList.add(d.getCode());
      }
    } else if (this.xAxisDateDimension != null) {
      xAxisDateDimensionCodeList.add(this.xAxisDateDimension.getCode());
    }
    return xAxisDateDimensionCodeList;
  }

  public List<String> getQueryDimensionsList() {
    List<String> queryDimensionsList = new ArrayList<String>();
    List<String> dimensionsCodeList = this.getDimensionsCodeList();
    List<String> xAxisDateDimensionCodeList = this.getXAxisDateDimensionCodeList();
    if (xAxisDateDimensionCodeList != null && !xAxisDateDimensionCodeList.isEmpty()
        && this.isUseDateDimensionInSelect()) {
      for (String dCode : xAxisDateDimensionCodeList) {
        if (!dimensionsCodeList.contains(dCode)) {
          queryDimensionsList.add(dCode);
        }
      }
    }
    if (dimensionsCodeList != null && !dimensionsCodeList.isEmpty()) {
      queryDimensionsList.addAll(dimensionsCodeList);
    }
    return queryDimensionsList;
  }

  public String getQueryDimensions() {
    String queryDimensions = "";
    List<String> queryDimensionsList = this.getQueryDimensionsList();
    if (queryDimensionsList != null && !queryDimensionsList.isEmpty()) {
      queryDimensions = StringUtil.join(queryDimensionsList, ",");
    }
    return queryDimensions;
  }

  /**
   * 获取查询用xAxis时间维度列表
   * @return
   * @date: 2016年7月25日
   * @author peng.xu
   */
  public List<PtoneMetricsDimension> getQueryXAxisDateDimension() {
    List<PtoneMetricsDimension> dimensionsList = new ArrayList<PtoneMetricsDimension>();
    if (this.xAxisDateDimensionList != null && !this.xAxisDateDimensionList.isEmpty()) {
      dimensionsList.addAll(this.xAxisDateDimensionList);
    } else if (this.xAxisDateDimension != null) {
      dimensionsList.add(this.xAxisDateDimension);
    }
    return dimensionsList;
  }

  // //////////////////////////////////////////////////////////////////

  private void setMetrics(String metrics) {
    this.metrics = metrics;
  }

  private void setMetricsId(String metricsId) {
    this.metricsId = metricsId;
  }

  private void setDimensions(String dimensions) {
    this.dimensions = dimensions;
  }

  private void setDimensionsId(String dimensionsId) {
    this.dimensionsId = dimensionsId;
  }

  public boolean isUseDateDimensionInSelect() {
    return useDateDimensionInSelect;
  }

  public void setUseDateDimensionInSelect(boolean useDateDimensionInSelect) {
    this.useDateDimensionInSelect = useDateDimensionInSelect;
  }

  public CurrentUserCache getCurrentUserCache() {
    return currentUserCache;
  }

  public void setCurrentUserCache(CurrentUserCache currentUserCache) {
    this.currentUserCache = currentUserCache;
  }

  public List<PtoneMetricsDimension> getMetricsList() {
    return metricsList;
  }

  public void setMetricsList(List<PtoneMetricsDimension> metricsList) {
    this.metricsList = metricsList;
  }

  public List<PtoneMetricsDimension> getDimensionsList() {
    return dimensionsList;
  }

  public void setDimensionsList(List<PtoneMetricsDimension> dimensionsList) {
    this.dimensionsList = dimensionsList;
  }

  public List<PtoneMetricsDimension> getFixedDimensionsList() {
    return fixedDimensionsList;
  }

  public void setFixedDimensionsList(List<PtoneMetricsDimension> fixedDimensionsList) {
    this.fixedDimensionsList = fixedDimensionsList;
  }

  public PtoneMetricsDimension getxAxisDateDimension() {
    return xAxisDateDimension;
  }

  public void setxAxisDateDimension(PtoneMetricsDimension xAxisDateDimension) {
    this.xAxisDateDimension = xAxisDateDimension;
  }

  public List<PtoneMetricsDimension> getxAxisDateDimensionList() {
    return xAxisDateDimensionList;
  }

  public void setxAxisDateDimensionList(List<PtoneMetricsDimension> xAxisDateDimensionList) {
    this.xAxisDateDimensionList = xAxisDateDimensionList;
  }

  public String getFilters() {
    return filters;
  }

  public void setFilters(String filters) {
    this.filters = filters;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
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

  public String getDatePeriod() {
    return datePeriod;
  }

  public void setDatePeriod(String datePeriod) {
    this.datePeriod = datePeriod;
  }

  public String getDateKey() {
    return dateKey;
  }

  public void setDateKey(String dateKey) {
    this.dateKey = dateKey;
  }

  public Map<String, PtoneMetricsDimension> getMetricsDimensionMap() {
    return metricsDimensionMap;
  }

  public void setMetricsDimensionMap(Map<String, PtoneMetricsDimension> metricsDimensionMap) {
    this.metricsDimensionMap = metricsDimensionMap;
  }

  public String getSegment() {
    return segment;
  }

  public void setSegment(String segment) {
    this.segment = segment;
  }

  public boolean isUseDatetimeAxis() {
    return useDatetimeAxis;
  }

  public void setUseDatetimeAxis(boolean useDatetimeAxis) {
    this.useDatetimeAxis = useDatetimeAxis;
  }

  public Integer getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(Integer startIndex) {
    this.startIndex = startIndex;
  }

  public Integer getMaxResults() {
    return maxResults;
  }

  public void setMaxResults(Integer maxResults) {
    this.maxResults = maxResults;
  }

  public String getProfileId() {
    return profileId;
  }

  public void setProfileId(String profileId) {
    this.profileId = profileId;
  }
  
}
