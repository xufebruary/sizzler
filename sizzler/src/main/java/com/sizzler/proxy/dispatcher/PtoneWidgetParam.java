package com.sizzler.proxy.dispatcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ptmind.common.utils.StringUtil;
import com.sizzler.cache.CurrentUserCache;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionDto;
import com.sizzler.domain.widget.dto.SegmentData;

/**
 * widget获取数据，所需参数
 */
public class PtoneWidgetParam implements Serializable {

  private static final long serialVersionUID = -8836704042752438149L;

  private Boolean isCacheData = false; // 标记返回数据是否走从缓存获取的

  private String uid;
  private String spaceId;
  private String panelId;
  private String widgetId;
  private String connectionId;
  private long dsId;
  private String dsCode;
  private String dsType;
  private String graphType;
  private String originalGraphType; // 原始图形类型， 复合指标查询拆分数据时会修改graphType为table，此字段记录原始类型
  private String dateKey;
  private String startDate;
  private String endDate;
  private String dateDimensionId; // 时间范围对应的时间维度列ID
  private String datePeriod; // 时间粒度
  private String profileId;
  private String sort; // 排序 add by you.zou 2016.2.22 (用于数据源端排序，目前只应用于关系型数据库的table排序)
  private PtoneMetricsDimension variableSortDimension;
  private Map<String, String> variableSort; // {"sortBy":"", sortOrder:"","sortColumn":""}
  private Integer variableMax;
  private String variableShowOthers;
  private Map<String, String> widgetSort; // {"sortBy":"", sortOrder:"", "sortColumn":""}
  private Integer widgetMax;
  private String widgetShowOthers;
  private boolean judgeMulitY; // 是否判断双轴，即双轴开启
  private boolean noCache; // 标记是否使用缓存数据（noCache==true， 不走缓存）

  private UserConnection userConnection;

  private CurrentUserCache currentUserCache;
  

  private Map<String, Object> otherInfo = new LinkedHashMap<>();

  private List<PtoneMetricsDimension> metrics = new ArrayList<PtoneMetricsDimension>();

  private boolean ignoreNullMetrics;
  
  //用户设置的时区，取数时如果选择了时间戳类型的字段时使用
  private String timeZone;

  //是否是默认时区，缓存key使用
  private String defaultTimezone;
  
  private List<String> metricsKeyList = new ArrayList<String>();

  private Map<String, String> metricsKeyToName = new HashMap<String, String>();

  @SuppressWarnings("rawtypes")
  private List<Map> yAxis = new ArrayList<Map>();

  private Map<String, Object> metricsToY = new HashMap<String, Object>();

  private List<PtoneMetricsDimension> dimensions = new ArrayList<PtoneMetricsDimension>();

  private boolean ignoreNullDimension;

  private List<String> dimensionsKeyList = new ArrayList<String>();

  private Map<String, String> dimensionsKeyToName = new HashMap<String, String>();

  private String dataKey; // 每次查询都生成一个UUID

  private SegmentData segment;

  private SegmentData filters;

  private boolean returnTableDataForCompoundMetrics = false; // 是否返回为复合指标查询返回TableData
  private boolean isGetDetail = false; // modelData是否返回明细数据

  private String mapCode;
  
  // 复合指标缓存map ，<metricsId, compoundMetrics>
  private Map<String, UserCompoundMetricsDimensionDto> compoundMetricsMap =
      new HashMap<String, UserCompoundMetricsDimensionDto>();

  public List<PtoneMetricsDimension> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<PtoneMetricsDimension> metrics) {
    this.metrics = metrics;
  }

  public List<String> getMetricsKeyList() {
    return metricsKeyList;
  }

  public void setMetricsKeyList(List<String> metricsKeyList) {
    this.metricsKeyList = metricsKeyList;
  }

  public Map<String, String> getMetricsKeyToName() {
    if (this.metricsKeyToName.isEmpty() && this.metrics != null) {
      for (PtoneMetricsDimension md : this.metrics) {
        if (md != null) {
          this.metricsKeyToName.put(PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md),
              md.getName());
        }
      }
    }
    return this.metricsKeyToName;
  }

  @SuppressWarnings("unused")
  private void setMetricsKeyToName(Map<String, String> metricsKeyToName) {
    this.metricsKeyToName = metricsKeyToName;
  }

  @SuppressWarnings("rawtypes")
  public List<Map> getyAxis() {
    return yAxis;
  }

  @SuppressWarnings("rawtypes")
  public void setyAxis(List<Map> yAxis) {
    this.yAxis = yAxis;
  }

  public Map<String, Object> getMetricsToY() {
    return metricsToY;
  }

  public void setMetricsToY(Map<String, Object> metricsToY) {
    this.metricsToY = metricsToY;
  }

  public List<PtoneMetricsDimension> getDimensions() {
    return dimensions;
  }

  public void setDimensions(List<PtoneMetricsDimension> dimensions) {
    this.dimensions = dimensions;
  }

  public List<String> getDimensionsKeyList() {
    return dimensionsKeyList;
  }

  public void setDimensionsKeyList(List<String> dimensionsKeyList) {
    this.dimensionsKeyList = dimensionsKeyList;
  }

  public Map<String, String> getDimensionsKeyToName() {
    if (this.dimensionsKeyToName.isEmpty() && this.dimensions != null) {
      for (PtoneMetricsDimension dd : this.dimensions) {
        if (dd != null) {
          this.dimensionsKeyToName.put(PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(dd),
              dd.getName());
        }
      }
    }
    return this.dimensionsKeyToName;
  }

  @SuppressWarnings("unused")
  private void setDimensionsKeyToName(Map<String, String> dimensionsKeyToName) {
    this.dimensionsKeyToName = dimensionsKeyToName;
  }

  public PtoneMetricsDimension getMetricsByKey(String metricsKey) {
    PtoneMetricsDimension md = null;
    if (metricsKey != null) {
      for (PtoneMetricsDimension m : this.getMetrics()) {
        if (metricsKey.equals(PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(m))) {
          md = m;
          break;
        }
      }
    }
    return md;
  }

  public String getMetricsNameByKey(String metricsKey) {
    String metricsName = "";
    if (metricsKey != null) {
      List<String> metricsNameList = new ArrayList<String>();
      for (String mk : metricsKey.split(",")) {
        metricsNameList.add(this.getMetricsKeyToName().get(mk));
      }
      metricsName = StringUtil.join(metricsNameList, ",");
    }
    return metricsName;
  }

  public String getDimensionsNameByKey(String metricsKey) {
    String metricsName = "";
    if (metricsKey != null) {
      List<String> metricsNameList = new ArrayList<String>();
      for (String mk : metricsKey.split(",")) {
        metricsNameList.add(this.getMetricsKeyToName().get(mk));
      }
      metricsName = StringUtil.join(metricsNameList, ",");
    }
    return metricsName;
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

  public String getDateKey() {
    return dateKey;
  }

  public void setDateKey(String dateKey) {
    this.dateKey = dateKey;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public Map<String, Object> getOtherInfo() {
    return otherInfo;
  }

  public void setOtherInfo(Map<String, Object> otherInfo) {
    this.otherInfo = otherInfo;
  }

  public String getDataKey() {
    return dataKey;
  }

  public void setDataKey(String dataKey) {
    this.dataKey = dataKey;
  }

  public String getDateDimensionId() {
    return dateDimensionId;
  }

  public void setDateDimensionId(String dateDimensionId) {
    this.dateDimensionId = dateDimensionId;
  }

  public String getDatePeriod() {
    return datePeriod;
  }

  public void setDatePeriod(String datePeriod) {
    this.datePeriod = datePeriod;
  }

  public String getProfileId() {
    return profileId;
  }

  public void setProfileId(String profileId) {
    this.profileId = profileId;
  }

  public long getDsId() {
    return dsId;
  }

  public void setDsId(long dsId) {
    this.dsId = dsId;
  }

  public String getDsCode() {
    return dsCode;
  }

  public void setDsCode(String dsCode) {
    this.dsCode = dsCode;
  }

  public String getDsType() {
    return dsType;
  }

  public void setDsType(String dsType) {
    this.dsType = dsType;
  }

  public String getGraphType() {
    return graphType;
  }

  public void setGraphType(String graphType) {
    this.graphType = graphType;
  }

  public UserConnection getUserConnection() {
    return userConnection;
  }

  public void setUserConnection(UserConnection userConnection) {
    this.userConnection = userConnection;
  }

  public CurrentUserCache getCurrentUserCache() {
    return currentUserCache;
  }

  public void setCurrentUserCache(CurrentUserCache currentUserCache) {
    this.currentUserCache = currentUserCache;
  }

  public boolean isIgnoreNullMetrics() {
    return ignoreNullMetrics;
  }

  public void setIgnoreNullMetrics(boolean ignoreNullMetrics) {
    this.ignoreNullMetrics = ignoreNullMetrics;
  }

  public boolean isIgnoreNullDimension() {
    return ignoreNullDimension;
  }

  public void setIgnoreNullDimension(boolean ignoreNullDimension) {
    this.ignoreNullDimension = ignoreNullDimension;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public Map<String, String> getVariableSort() {
    return variableSort;
  }

  public void setVariableSort(Map<String, String> variableSort) {
    this.variableSort = variableSort;
  }

  public Integer getVariableMax() {
    return variableMax;
  }

  public void setVariableMax(Integer variableMax) {
    this.variableMax = variableMax;
  }

  public Map<String, String> getWidgetSort() {
    return widgetSort;
  }

  public void setWidgetSort(Map<String, String> widgetSort) {
    this.widgetSort = widgetSort;
  }

  public Integer getWidgetMax() {
    return widgetMax;
  }

  public void setWidgetMax(Integer widgetMax) {
    this.widgetMax = widgetMax;
  }

  public String getVariableShowOthers() {
    return variableShowOthers;
  }

  public void setVariableShowOthers(String variableShowOthers) {
    this.variableShowOthers = variableShowOthers;
  }

  public String getWidgetShowOthers() {
    return widgetShowOthers;
  }

  public void setWidgetShowOthers(String widgetShowOthers) {
    this.widgetShowOthers = widgetShowOthers;
  }

  public PtoneMetricsDimension getVariableSortDimension() {
    return variableSortDimension;
  }

  public void setVariableSortDimension(PtoneMetricsDimension variableSortDimension) {
    this.variableSortDimension = variableSortDimension;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(String connectionId) {
    this.connectionId = connectionId;
  }

  public SegmentData getSegment() {
    return segment;
  }

  public void setSegment(SegmentData segment) {
    this.segment = segment;
  }

  public SegmentData getFilters() {
    return filters;
  }

  public void setFilters(SegmentData filters) {
    this.filters = filters;
  }

  public boolean isReturnTableDataForCompoundMetrics() {
    return returnTableDataForCompoundMetrics;
  }

  public void setReturnTableDataForCompoundMetrics(boolean returnTableDataForCompoundMetrics) {
    this.returnTableDataForCompoundMetrics = returnTableDataForCompoundMetrics;
  }

  public Map<String, UserCompoundMetricsDimensionDto> getCompoundMetricsMap() {
    return compoundMetricsMap;
  }

  public void setCompoundMetricsMap(Map<String, UserCompoundMetricsDimensionDto> compoundMetricsMap) {
    this.compoundMetricsMap = compoundMetricsMap;
  }

  public String getOriginalGraphType() {
    return originalGraphType;
  }

  public void setOriginalGraphType(String originalGraphType) {
    this.originalGraphType = originalGraphType;
  }

  public boolean isJudgeMulitY() {
    return judgeMulitY;
  }

  public void setJudgeMulitY(boolean judgeMulitY) {
    this.judgeMulitY = judgeMulitY;
  }

  public boolean isGetDetail() {
    return isGetDetail;
  }

  public void setGetDetail(boolean isGetDetail) {
    this.isGetDetail = isGetDetail;
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

  public String getMapCode() {
    return mapCode;
  }

  public void setMapCode(String mapCode) {
    this.mapCode = mapCode;
  }

  public boolean isNoCache() {
    return noCache;
  }

  public void setNoCache(boolean noCache) {
    this.noCache = noCache;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public String getDefaultTimezone() {
    return defaultTimezone;
  }

  public void setDefaultTimezone(String defaultTimezone) {
    this.defaultTimezone = defaultTimezone;
  }
  
}
