package com.sizzler.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptmind.common.utils.CollectionUtil;
import com.ptmind.common.utils.RegexUtils;
import com.ptmind.common.utils.StringUtil;
import com.sizzler.cache.CurrentUserCache;
import com.sizzler.cache.PtoneBasicChartInfoCache;
import com.sizzler.cache.PtoneDsInfoCache;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.user.PtoneUserBasicSetting;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.variable.dto.PtVariables;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.domain.widget.dto.AcceptWidget;
import com.sizzler.domain.widget.dto.DynamicSegmentCondition;
import com.sizzler.domain.widget.dto.DynamicSegmentData;
import com.sizzler.domain.widget.dto.PtoneWidgetChartSettingDto;
import com.sizzler.domain.widget.dto.SegmentData;
import com.sizzler.proxy.dispatcher.ChartPluginType;
import com.sizzler.proxy.dispatcher.GraphType;
import com.sizzler.proxy.dispatcher.PtoneChartPluginDesc;
import com.sizzler.proxy.dispatcher.PtoneDispatcher;
import com.sizzler.proxy.dispatcher.PtoneGraphWidgetDataDesc;
import com.sizzler.proxy.dispatcher.PtoneVariableData;
import com.sizzler.proxy.dispatcher.PtoneWidgetChartData;
import com.sizzler.proxy.dispatcher.PtoneWidgetData;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;
import com.sizzler.service.PanelService;
import com.sizzler.service.PtoneUserConnectionService;
import com.sizzler.service.UserService;
import com.sizzler.service.VariableDataService;
import com.sizzler.service.VariableService;
import com.sizzler.service.WidgetDataService;
import com.sizzler.service.WidgetService;
import com.sizzler.service.WidgetVariableService;
import com.sizzler.service.ds.PtoneDsService;
import com.sizzler.service.ds.UserCompoundMetricsDimensionService;
import com.sizzler.service.ds.UserConnectionSourceService;
import com.sizzler.service.space.SpaceService;
import com.sizzler.system.Constants;

/**
 * 获取widget数据服务类 <br/>
 */
@Service("widgetDataService")
public class WidgetDataServiceImpl implements WidgetDataService {

  private static final String METRICS_LIMIT_KEY = "metricsLimit";
  private static final String DIMENSIONS_LIMIT_KEY = "dimensionsLimit";

  @Autowired
  private WidgetService widgetService;

  @Autowired
  private WidgetVariableService widgetVariableService;

  @Autowired
  private VariableService variableService;

  @Autowired
  private VariableDataService variableDataService;

  @Autowired
  private PtoneDsInfoCache ptoneDsInfoCache;

  @Autowired
  private PtoneBasicChartInfoCache ptoneBasicChartInfoCache;

  @Autowired
  private UserService userService;

  @Autowired
  private SpaceService spaceService;

  @Autowired
  private PanelService panelService;

  @Autowired
  private PtoneDsService ptoneDsService;

  @Autowired
  private UserCompoundMetricsDimensionService userCompoundMetricsDimensionService;

  @Autowired
  private UserConnectionSourceService userConnectionSourceService;

  @Autowired
  private PtoneUserConnectionService ptoneUserConnectionService;

  /**
   * 获取Widget的数据：根据图表控件类型转换好的json串
   */
  @Deprecated
  @Override
  public PtoneWidgetChartData getPtoneWidgetChartData(String widgetId,
      Map<String, String> webParamMap) {

    String pluginType = "highcharts"; // TODO: 获取图表插件类型

    ChartPluginType chartPluginType = ChartPluginType.valueOf(pluginType.toUpperCase());
    PtoneChartPluginDesc ptoneChartPluginDesc = new PtoneChartPluginDesc(chartPluginType);
    ptoneChartPluginDesc.setWidgetId(widgetId);
    ptoneChartPluginDesc.setWebParamMap(webParamMap);
    PtoneWidgetChartData ptoneWidgetChartData =
        PtoneDispatcher.getInstance().dispatch(ptoneChartPluginDesc);
    return ptoneWidgetChartData;
  }

  /**
   * 获取Widget的数据：直接返回原始数据结构,从数据库中查询widget配置信息
   */
  @Override
  public PtoneWidgetData getPtoneWidgetData(String widgetId, Map<String, String> webParamMap,
      AcceptWidget widget) {

    if (widget == null || widget.getBaseWidget() == null) {
      return null;
    }

    // 标记是否使用模板数据
    boolean isExample = (Constants.validateInt == widget.getBaseWidget().getIsExample());
    String isExampleParam = webParamMap.get(Constants.PARAM_IS_EXAMPLE);
    if (StringUtil.isNotBlank(isExampleParam)) {
      isExample = Boolean.valueOf(isExampleParam);
    }

    // 模板和根据模板直接添加使用demo数据的widget获取模板的example数据
    if (isExample) {
      return this.getPtoneWidgetTempletData(widget.getBaseWidget().getTempletId(), widget);
    }

    PtoneSpaceInfo spaceInfo = this.getWidgetSpaceInfo(widget);
    String weekStart = "";
    if (spaceInfo != null) {
      weekStart = spaceInfo.getWeekStart();
      // 修正widget上spaceId
      if (widget.getBaseWidget() != null) {
        widget.getBaseWidget().setSpaceId(spaceInfo.getSpaceId());
      }
    }

    // 缓存当前登录用户信息, 为支持用户panel分享，取数时根据widget的创建人获取用户
    PtoneUser currentUser = userService.getPtoneUser(widget.getBaseWidget().getCreatorId());
    PtoneUserBasicSetting currentUserSetting = null;
    if (currentUser != null) {
      currentUserSetting = userService.getUserSetting(currentUser.getPtId(), null);
      currentUserSetting.setWeekStart(weekStart);
      // 设置widget所有人的email信息， 为elk日志统计使用
      if (widget.get_ext() == null) {
        widget.set_ext(new HashMap<String, Object>());
      }
      widget.get_ext().put(Constants.PT_USERNAME, currentUser.getUserEmail());
    }
    CurrentUserCache currentUserCache = new CurrentUserCache();
    currentUserCache.setCurrentUser(currentUser != null ? currentUser : new PtoneUser());
    currentUserCache.setCurrentUserSetting(currentUserSetting != null ? currentUserSetting
        : new PtoneUserBasicSetting());
    currentUserCache.setCurrentSpaceInfo(spaceInfo);


    PtoneWidgetData ptoneWidgetData =
        this.getPtoneWidgetDataForEditor(widgetId, webParamMap, widget, currentUserCache);

    String datePeriod = null; // 默认时间粒度
    List<String> availableDatePeriod = null;// 可选时间粒度，根据指标设置

    if (ptoneWidgetData == null) {
      return new PtoneWidgetData();
    }

    // 设置widgetData中数据的最大值和最小值
    for (Object data : ptoneWidgetData.getData()) {

      PtoneVariableData variableData = (PtoneVariableData) data;

      // 设置排序类型（default、date、dataValue）
      // TODO:
      if (PtoneMetricsDimension.SORT_BY_DATE.equals(variableData.getOrderType())) {
        ptoneWidgetData.setOrderType(PtoneMetricsDimension.SORT_BY_DATE);
      }

      // 处理metricsAmount
      Map<String, Map<String, Object>> metricsTotalsMap = variableData.getMetricsTotalsMap();
      if (metricsTotalsMap != null) {
        ptoneWidgetData.getMetricsAmountsMap().putAll(metricsTotalsMap);
      }

      // 合并扩展信息
      Map<String, Object> extInfo = variableData.getExtInfo();
      if (extInfo != null) {
        ptoneWidgetData.getExtInfo().putAll(extInfo);
      }

      GraphType graphType = variableData.getGraphType();
      // 以下图形不需要最大、最小值
      if (GraphType.SIMPLENUMBER.equals(graphType) || GraphType.NUMBER.equals(graphType)
          || GraphType.TABLE.equals(graphType) || GraphType.MAP.equals(graphType)
          || GraphType.CIRCLEPERCENT.equals(graphType) || GraphType.PROGRESSBAR.equals(graphType)
          || GraphType.PIE.equals(graphType) || GraphType.HOLLOWPIE.equals(graphType)) {
        continue;
      }

      // 获取时间粒度
      if (datePeriod == null || "".equals(datePeriod)) {
        datePeriod = variableData.getDatePeriod();
      }
    }

    ptoneWidgetData.setDatePeriod(datePeriod);
    ptoneWidgetData.setAvailableDatePeriod(availableDatePeriod);

    // 格式化不同图表类型的widget数据
    PtoneGraphWidgetDataDesc ptoneGraphWidgetDataDesc =
        new PtoneGraphWidgetDataDesc(ptoneWidgetData.getGraphType());
    ptoneGraphWidgetDataDesc.setPtoneWidgetData(ptoneWidgetData);
    ptoneWidgetData = PtoneDispatcher.getInstance().dispatch(ptoneGraphWidgetDataDesc);

    if (ptoneWidgetData == null) {
      return new PtoneWidgetData();
    }

    Double maxValue = null;
    Double minValue = null;
    // 设置widgetData中数据的最大值和最小值
    for (Object data : ptoneWidgetData.getData()) {
      PtoneVariableData variableData = (PtoneVariableData) data;
      GraphType graphType = variableData.getGraphType();
      // 以下图形不需要最大、最小值
      if (GraphType.SIMPLENUMBER.equals(graphType) || GraphType.NUMBER.equals(graphType)
          || GraphType.TABLE.equals(graphType) || GraphType.MAP.equals(graphType)
          || GraphType.CIRCLEPERCENT.equals(graphType) || GraphType.PROGRESSBAR.equals(graphType)
          || GraphType.PIE.equals(graphType) || GraphType.HOLLOWPIE.equals(graphType)) {
        continue;
      }
      for (List<Object> row : variableData.getRows()) {
        Double tempValue = ((Number) row.get(1)).doubleValue(); // 0:name,1:value,2:goals(number等才有目标值)
        if (maxValue == null || maxValue < tempValue) {
          maxValue = tempValue;
        }
        if (minValue == null || minValue > tempValue) {
          minValue = tempValue;
        }
      }
    }
    ptoneWidgetData.setMaxValue(maxValue);
    ptoneWidgetData.setMinValue(minValue);

    return this.fixWidgetData(ptoneWidgetData);
  }

  /**
   * 修正ptoneWidgetData数据
   * @param ptoneWidgetData
   * @return
   * @date: 2016年8月2日
   * @author peng.xu
   */
  private PtoneWidgetData fixWidgetData(PtoneWidgetData ptoneWidgetData) {
    if (ptoneWidgetData == null) {
      return ptoneWidgetData;
    }

    // 修正table数据中的科学计数法问题
    if (ptoneWidgetData.getData() != null) {
      for (Object data : ptoneWidgetData.getData()) {
        PtoneVariableData variableData = (PtoneVariableData) data;
        if (variableData != null && variableData.getRows() != null) {
          for (List<Object> row : variableData.getRows()) {
            for (int i = 0; row != null && i < row.size(); i++) {
              String cell = String.valueOf(row.get(i));
              if (RegexUtils.isScientificNotation(cell)) {
                row.set(i, new BigDecimal(new BigDecimal(cell).toPlainString()));
              }
            }
          }
        }
      }
    }

    // 修正指标总量中的科学计数法
    Map<String, Map<String, Object>> metricsAmountsMap = ptoneWidgetData.getMetricsAmountsMap();
    if (metricsAmountsMap != null) {
      for (Map<String, Object> map : metricsAmountsMap.values()) {
        if (map != null) {
          String value = String.valueOf(map.get("value"));
          if (RegexUtils.isScientificNotation(value)) {
            map.put("value", new BigDecimal(new BigDecimal(value).toPlainString()));
          }
        }
      }
    }

    return ptoneWidgetData;
  }

  /**
   * 获取Widget的数据：直接返回原始数据结构,从前台widget编辑器传递的参数获取widget配置信息
   */
  @SuppressWarnings("rawtypes")
  private PtoneWidgetData getPtoneWidgetDataForEditor(String widgetId,
      Map<String, String> webParamMap, AcceptWidget widget, CurrentUserCache currentUserCache) {

    // 根据编辑器传递的AcceptWidget构建PtoneWidgetInfo
    PtoneWidgetInfo ptoneWidgetInfo = new PtoneWidgetInfo();
    ptoneWidgetInfo.setWidgetId(widgetId);
    ptoneWidgetInfo.setPtoneGraphInfoId(widget.getBaseWidget().getPtoneGraphInfoId());
    ptoneWidgetInfo.setGraphName(widget.getBaseWidget().getGraphName());
    ptoneWidgetInfo.setMapCode(widget.getBaseWidget().getMapCode());
    ptoneWidgetInfo.setDateKey(widget.getBaseWidget().getDateKey());
    ptoneWidgetInfo.setDatePeriod(widget.getBaseWidget().getDatePeriod());
    ptoneWidgetInfo.setTargetValue(widget.getBaseWidget().getTargetValue());

    String graphType =
        ptoneBasicChartInfoCache.getPtoneBasicChartInfoById(ptoneWidgetInfo.getPtoneGraphInfoId())
            .getCode();

    // chartSetting处理
    PtoneWidgetChartSettingDto chartSetting = widget.getChartSetting();
    String showMultiY = "" + chartSetting.getShowMultiY(); // 0 | 1

    // 验证webParamMap是否有设置死的graphName，如果有，则证明本次数据请求是从csv下载发送过来的，则设置graphType设置为table类型 add by you.zou
    // 2016.3.14
    if (webParamMap.containsKey("csvGraphName")) {
      graphType = webParamMap.get("csvGraphName");
      showMultiY = Constants.inValidate;
    }

    String y2Graph = null;
    List<Map> yAxis = chartSetting.getyAxis();
    if (Constants.validate.equals(showMultiY)) {
      if (yAxis.size() > 1) {
        y2Graph = (String) yAxis.get(1).get("chartType");
      }
    }

    // 判断双轴中，指标都使用哪个轴（处理所有指标在同一个轴上的问题）
    Map<String, Object> metricsToY = chartSetting.getMetricsToY();
    List<String> yAxisUsedList = new ArrayList<String>();
    if (metricsToY != null && !metricsToY.isEmpty()) {
      for (Object val : metricsToY.values()) {
        if (!yAxisUsedList.contains("" + val)) {
          yAxisUsedList.add("" + val);
        }
      }
    }

    // 如果开启双轴，根据指标对应轴只有一个轴时，修正图形的类型
    if (Constants.validate.equals(showMultiY) && yAxisUsedList.size() == 1) {
      String fixChartType =
          (String) yAxis.get(Integer.valueOf(yAxisUsedList.get(0))).get("chartType");
      graphType = fixChartType;
      ptoneWidgetInfo.setGraphName(fixChartType);
    }

    // 是否根据双轴判断（开启双轴、且轴图形不同）
    boolean isShowMultiY = Constants.validate.equals(showMultiY); // 开启双轴
    String yUsedList = StringUtil.join(yAxisUsedList, ",");
    boolean judgeMulitY = isShowMultiY; // 是否根据双轴判断（开启双轴、且轴图形不同）
    if (judgeMulitY) {
      if (yUsedList.contains(",")) { // 使用两个轴 ： 0,1 || 1,0
        judgeMulitY = (y2Graph != null && !"".equals(y2Graph) && !y2Graph.equals(graphType));
      } else {
        judgeMulitY = false;
      }
    }

    PtoneWidgetData ptoneWidgetData = new PtoneWidgetData();
    ptoneWidgetData.setWidgetId(widgetId);
    ptoneWidgetData.setGraphType(GraphType.valueOf(graphType.toUpperCase()));

    // 目标值处理
    Number goals =
        (ptoneWidgetInfo.getTargetValue() == null || ptoneWidgetInfo.getTargetValue()
            .equalsIgnoreCase("")) ? null : Double.parseDouble(ptoneWidgetInfo.getTargetValue());
    ptoneWidgetData.setGoals(goals);

    // 根据编辑器传递的AcceptWidget构建PtoneVariableInfo
    for (PtVariables ptVariable : widget.getVariables()) {

      // 获取variableInfo
      PtoneVariableInfo ptoneVariableInfo = new PtoneVariableInfo();
      ptoneVariableInfo.setVariableId(ptVariable.getVariableId());
      ptoneVariableInfo.setVariableName("");
      ptoneVariableInfo.setPtoneDsInfoId(ptVariable.getPtoneDsInfoId());
      ptoneVariableInfo.setVariableGraphId(ptVariable.getVariableGraphId());
      ptoneVariableInfo.setGraphName(graphType);
      ptoneVariableInfo.setVariableColor(ptVariable.getVariableColor());

      // 获取数据源对象
      PtoneDsInfo dsInfo =
          ptoneDsInfoCache.getPtoneDsInfoById(ptoneVariableInfo.getPtoneDsInfoId());

      String accountName = ptVariable.getAccountName();
      String profileId = ptVariable.getProfileId();

      /**
       * 根据图形类型，处理用户选择指标失效处理
       */
      List<PtoneMetricsDimension> metricsDataList = ptVariable.getMetrics();
      List<PtoneMetricsDimension> dimensionsDataList = ptVariable.getDimensions();
      int metricsLimit = metricsDataList != null ? metricsDataList.size() : 0;
      int dimensionsLimit = dimensionsDataList != null ? dimensionsDataList.size() : 0;

      Map<String, Integer> limitMap =
          getMetricsAndDimensionLimit(metricsLimit, dimensionsLimit, metricsDataList,
              dimensionsDataList, graphType, judgeMulitY);
      metricsLimit = limitMap.get(METRICS_LIMIT_KEY);
      dimensionsLimit = limitMap.get(DIMENSIONS_LIMIT_KEY);

      // 处理metrics限制
      List<String> metricsKeyList = new ArrayList<String>();
      List<String> metricsId = new ArrayList<String>();
      if (metricsDataList != null && !metricsDataList.isEmpty()) {
        for (int i = 0; i < metricsDataList.size() && i < metricsLimit; i++) {
          PtoneMetricsDimension md = metricsDataList.get(i);
          metricsKeyList.add(PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md));
          metricsId.add(md.getId());
        }
      }

      // 处理dimensions限制
      List<String> dimensionsKeyList = new ArrayList<String>();
      List<String> dimensionsId = new ArrayList<String>();
      if (dimensionsDataList != null && !dimensionsDataList.isEmpty()) {
        for (int i = 0; i < dimensionsDataList.size() && i < dimensionsLimit; i++) {
          PtoneMetricsDimension dd = dimensionsDataList.get(i);
          dimensionsKeyList.add(PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(dd));
          dimensionsId.add(dd.getId());
        }
      }

      PtoneWidgetParam ptoneWidgetParam = new PtoneWidgetParam();
      ptoneWidgetParam.setDsId(dsInfo.getId());
      ptoneWidgetParam.setDsCode(dsInfo.getCode());
      ptoneWidgetParam.setDsType(dsInfo.getType());
      ptoneWidgetParam.setMetrics(metricsDataList);
      ptoneWidgetParam.setMetricsKeyList(metricsKeyList);
      ptoneWidgetParam.setIgnoreNullMetrics(Constants.validate.equals(ptVariable
          .getIgnoreNullMetrics()));
      ptoneWidgetParam.setyAxis(yAxis);
      ptoneWidgetParam.setMetricsToY(metricsToY);
      ptoneWidgetParam.setDimensions(dimensionsDataList);
      ptoneWidgetParam.setDimensionsKeyList(dimensionsKeyList);
      ptoneWidgetParam.setIgnoreNullDimension(Constants.validate.equals(ptVariable
          .getIgnoreNullDimension()));
      ptoneWidgetParam.setGraphType(graphType);
      ptoneWidgetParam.setCurrentUserCache(currentUserCache);
      ptoneWidgetParam.setUid(widget.getBaseWidget().getCreatorId());
      ptoneWidgetParam.setSpaceId(widget.getBaseWidget().getSpaceId());
      ptoneWidgetParam.setPanelId(widget.getPanelId());
      ptoneWidgetParam.setConnectionId(ptVariable.getConnectionId());
      ptoneWidgetParam.setWidgetId(widget.getBaseWidget().getWidgetId());
      ptoneWidgetParam.setDateKey(widget.getBaseWidget().getDateKey());
      ptoneWidgetParam.setDateDimensionId(ptVariable.getDateDimensionId());
      ptoneWidgetParam.setDatePeriod(widget.getBaseWidget().getDatePeriod());
      ptoneWidgetParam.setProfileId(ptVariable.getProfileId());
      ptoneWidgetParam.setSort(ptVariable.getSort());
      ptoneWidgetParam.setSegment(ptVariable.getSegment());
      ptoneWidgetParam.setFilters(ptVariable.getFilters());
      ptoneWidgetParam.setSort(ptVariable.getSort());
      ptoneWidgetParam.setJudgeMulitY(judgeMulitY);
      ptoneWidgetParam.setMapCode(ptoneWidgetInfo.getMapCode());
      ptoneWidgetParam.setNoCache(Boolean.valueOf(webParamMap.get(Constants.PARAM_NO_CACHE)));

      Map<String, Object> otherInfoMap = ptoneWidgetParam.getOtherInfo();
      otherInfoMap.put(Constants.OperateLog.OperateLogContent.USER_EMAIL, currentUserCache
          .getCurrentUser().getUserEmail());

      webParamMap.put(Constants.PARAM_ACCOUNT_NAME, accountName);
      webParamMap.put(Constants.PARAM_PROFILE, profileId);
      webParamMap.put(Constants.PARAM_METRICS_ID, StringUtil.join(metricsId, ","));
      webParamMap.put(Constants.PARAM_DIMENSIONS_ID, StringUtil.join(dimensionsId, ","));
      webParamMap.put(Constants.PARAM_SHOW_MULTI_Y, showMultiY);
      webParamMap.put(Constants.PARAM_Y2_GRAPH, y2Graph);
      webParamMap.put(Constants.PARAM_Y_USED_LIST, StringUtil.join(yAxisUsedList, ","));
      webParamMap.put(Constants.PARAM_JUDGE_MULTI_Y, String.valueOf(judgeMulitY));

      // 处理widget的排序, dimensionData的sort、max、showOthers
      this.initWidgetSort(ptoneWidgetParam);

      // andy add 2015-12-21
      otherInfoMap.put(Constants.OperateLog.OperateLogContent.DS_ID,
          ptoneVariableInfo.getPtoneDsInfoId());
      otherInfoMap.put(Constants.OperateLog.OperateLogContent.DS_NAME, dsInfo.getCode());

      List<PtoneVariableData> ptoneVariableDataList = new ArrayList<PtoneVariableData>();

      // 对于不包含复合指标的widget直接取数，从数据源中获取数据，在此处不考虑从什么数据获取数据
      ptoneVariableDataList =
          variableDataService.getVariableData(dsInfo.getType(), ptoneWidgetInfo,
              ptoneVariableInfo, graphType, ptoneWidgetParam, webParamMap, currentUserCache);

      // 将variableData增加到widgetData中
      for (PtoneVariableData ptoneVariableData : ptoneVariableDataList) {
        ptoneWidgetData.addData(ptoneVariableData);
      }

      // 设置用户设置的排序规则
      Map<String, String> widgetSort = ptoneWidgetParam.getWidgetSort();
      if (widgetSort != null
          && !PtoneMetricsDimension.SORT_TYPE_DEFAULT.equals(widgetSort
              .get(PtoneMetricsDimension.SORT_ATTRIBUTE_TYPE))) {
        String sortType = widgetSort.get(PtoneMetricsDimension.SORT_ATTRIBUTE_TYPE);
        String sortBy = widgetSort.get(PtoneMetricsDimension.SORT_ATTRIBUTE_SORT_BY);
        String sortOrder = widgetSort.get(PtoneMetricsDimension.SORT_ATTRIBUTE_SORT_ORDER);
        String sortColumn = widgetSort.get(PtoneMetricsDimension.SORT_ATTRIBUTE_SORT_COLUMN);
        ptoneWidgetData.setSortType(sortType);
        ptoneWidgetData.setOrderType(sortBy);
        ptoneWidgetData.setOrderRule(sortOrder);
        ptoneWidgetData.setOrderColumn(sortColumn);
      }
      // 设置用户设置的max限制
      ptoneWidgetData.setMax(ptoneWidgetParam.getWidgetMax());
      ptoneWidgetData.setShowOthers(ptoneWidgetParam.getWidgetShowOthers());

      ptoneWidgetData.setDsCode(ptoneWidgetParam.getDsCode());
      ptoneWidgetData.setStartDate(ptoneWidgetParam.getStartDate());
      ptoneWidgetData.setEndDate(ptoneWidgetParam.getEndDate());
      ptoneWidgetData.setIsCacheData(ptoneWidgetParam.getIsCacheData());
    }

    return ptoneWidgetData;
  }

  /**
   * 获取widget模板demo数据
   * 
   * @param templetId
   * @return
   */
  private PtoneWidgetData getPtoneWidgetTempletData(String templetId, AcceptWidget widget) {
//    PtoneWidgetTempletData templetData = widgetTempletDataService.get(templetId);
//    if (templetData == null) {
//      logger.error("Not find widget templet data for templetId : " + templetId);
//      throw new ServiceException("Not find widget templet data for templetId : " + templetId);
//    }
//    String dataJson = templetData.getData();
//    PtoneWidgetData ptoneWidgetData = JSON.parseObject(dataJson, PtoneWidgetData.class);
//
//    // 处理模板国际化，修正指标、维度名称
//    Map<String, String> metricsNameMap = new HashMap<String, String>();
//    Map<String, String> dimensionsNameMap = new HashMap<String, String>();
//    for (PtVariables varialbe : widget.getVariables()) {
//      for (PtoneMetricsDimension md : varialbe.getMetrics()) {
//        String metricsKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md);
//        metricsNameMap.put(metricsKey, md.getName());
//      }
//      for (PtoneMetricsDimension dd : varialbe.getDimensions()) {
//        String dimensionsKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(dd);
//        dimensionsNameMap.put(dimensionsKey, dd.getName());
//      }
//    }
//
//    List<Object> widgetData = new ArrayList<Object>();
//    for (Object data : ptoneWidgetData.getData()) {
//      PtoneVariableData variableData =
//          JSONObject.toJavaObject((JSONObject) data, PtoneVariableData.class);
//
//      GraphType graphType = variableData.getGraphType();
//
//      if (GraphType.TABLE.equals(graphType) || GraphType.MAP.equals(graphType)) {
//        String metricsKey = variableData.getMetricsKey();
//        String dimensionsKey = variableData.getDimensionsKey();
//        String oldMetricsName = variableData.getMetricsName();
//        String oldVariableName = variableData.getVariableName();
//        List<String> oldMetricsNameList = StringUtil.splitToList(oldMetricsName, ",");
//
//        List<String> newMetricsNameList = new ArrayList<String>();
//        if (StringUtil.isNotBlank(metricsKey)) {
//          for (String mk : metricsKey.split(",")) {
//            String newMetricsName = metricsNameMap.get(mk);
//            newMetricsName = newMetricsName == null ? "" : newMetricsName;
//            newMetricsNameList.add(newMetricsName);
//          }
//        }
//        List<String> newDimensionNameList = new ArrayList<String>();
//        if (StringUtil.isNotBlank(dimensionsKey)) {
//          for (String dk : dimensionsKey.split(",")) {
//            String newDimensionName = dimensionsNameMap.get(dk);
//            newDimensionName = newDimensionName == null ? "" : newDimensionName;
//            newDimensionNameList.add(newDimensionName);
//          }
//        }
//
//        List<String> newVariableNameList = new ArrayList<String>();
//        if (StringUtil.isNotBlank(oldVariableName)) {
//          List<String> oldVariableNameList = StringUtil.splitToList(oldVariableName, ",");
//          for (int i = 0; i < oldVariableNameList.size(); i++) {
//            String newVariableName =
//                newMetricsNameList.get(i)
//                    + oldVariableNameList.get(i).substring(oldMetricsNameList.get(i).length());
//            newVariableNameList.add(newVariableName);
//          }
//        }
//
//        List<List<Object>> rows = variableData.getRows();
//        if (rows != null && !rows.isEmpty()) {
//          List<Object> oldThList = rows.get(0);
//          List<Object> newThList = new ArrayList<Object>();
//          newThList.addAll(newDimensionNameList);
//
//          for (int i = 0; i < newMetricsNameList.size(); i++) {
//            String oldTh = (String) oldThList.get(newDimensionNameList.size() + i);
//            String newTh = "";
//            if (oldTh != null) {
//              newTh =
//                  newMetricsNameList.get(i) + oldTh.substring(oldMetricsNameList.get(i).length());
//            }
//            newThList.add(newTh);
//          }
//          rows.set(0, newThList); // 更新表头
//        }
//
//        variableData.setMetricsName(StringUtil.join(newMetricsNameList, ","));
//        variableData.setVariableName(StringUtil.join(newVariableNameList, ","));
//
//      } else {
//        String metricsKey = variableData.getMetricsKey();
//        String oldMetricsName = variableData.getMetricsName();
//        String oldVariableName = variableData.getVariableName();
//        String newMetridsName = metricsNameMap.get(metricsKey);
//        String newVariableName = oldVariableName;
//        if (oldVariableName.startsWith(oldMetricsName)) {
//          int index =
//              oldMetricsName.indexOf("(") > 0 ? oldMetricsName.indexOf("(") : oldMetricsName
//                  .length();
//          newVariableName = newMetridsName + oldVariableName.substring(index);
//        }
//
//        variableData.setMetricsName(newMetridsName);
//        variableData.setVariableName(newVariableName);
//      }
//      widgetData.add(variableData);
//    }
//
//
//    // 指标总量名称修正
//    Map<String, Map<String, Object>> metricsAmountsMap = ptoneWidgetData.getMetricsAmountsMap();
//    for (Map<String, Object> map : metricsAmountsMap.values()) {
//      map.put("showName", metricsNameMap.get(map.get("key")));
//    }
//    ptoneWidgetData.setData(widgetData);
//    ptoneWidgetData.setWidgetId(widget.getBaseWidget().getWidgetId()); // 修正数据的widgetId

//    return ptoneWidgetData;
    return null;
  }

  /**
   * 判断是否包含复合指标
   * 
   * @param metrics
   * @param filters
   * @return
   * @date: 2016年7月20日
   * @author peng.xu
   */
  public static boolean hasCompoundMetrics(List<PtoneMetricsDimension> metrics,
      List<String> metricsKeyList, SegmentData filters) {
    if (metrics != null && metrics.size() > 0) {
      for (PtoneMetricsDimension md : metrics) {
        String mKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md);
        if (metricsKeyList.contains(mKey)
            && PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equals(md.getType())) {
          return true;
        }
      }
    }
    if (filters != null) {
      String segmentType = filters.getType();
      if (SegmentData.TYPE_NEW.equalsIgnoreCase(segmentType) && filters.getNewData() != null) {
        for (DynamicSegmentData dynamicSegmentData : filters.getNewData()) {
          if (dynamicSegmentData.getCondition() != null) {
            for (DynamicSegmentCondition condition : dynamicSegmentData.getCondition()) {
              if (condition != null
                  && PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equals(condition.getType())) {
                return true;
              }
            }
          }
        }
      }
    }
    return false;
  }


  /**
   * 根据图形来判断指标、维度的限制个数
   * @param metricsLimit
   * @param dimensionsLimit
   * @param metricsDataList
   * @param dimensionsDataList
   * @param graphType
   * @param judgeMulitY 是否根据双轴判断（开启双轴、且轴图形不同）
   * @return
   */
  public Map<String, Integer> getMetricsAndDimensionLimit(int metricsLimit, int dimensionsLimit,
      List<PtoneMetricsDimension> metricsDataList, List<PtoneMetricsDimension> dimensionsDataList,
      String graphType, boolean judgeMulitY) {
    Map<String, Integer> limitMap = new HashMap<String, Integer>();
    GraphType graph = GraphType.valueOf(graphType.toUpperCase());
    if (GraphType.SIMPLENUMBER.equals(graph) || GraphType.NUMBER.equals(graph)
        || GraphType.CIRCLEPERCENT.equals(graph) || GraphType.PROGRESSBAR.equals(graph)
        || GraphType.MAP.equals(graph)) {
      metricsLimit = 1; // 限制为一个指标
      dimensionsLimit = 0;
    } else if (GraphType.PIE.equals(graph)) {
      metricsLimit = 1;
      dimensionsLimit = 1;
    } else if (!judgeMulitY
        && (GraphType.LINE.equals(graph) || GraphType.AREA.equals(graph) || GraphType.AREASPLINE
            .equals(graph))) {
      // dimensionsLimit = 1; // GA默认添加时间维度，只支持一个维度，在相关service中处理
      dimensionsLimit = 2; // 默认不添加时间维度，可以支持2个维度
    } else if (!judgeMulitY && (GraphType.COLUMN.equals(graph) || GraphType.BAR.equals(graph))) {
      if (metricsDataList != null && metricsDataList.size() > 1) {
        dimensionsLimit = 1;
      } else {
        dimensionsLimit = 2;
      }
    } else if (judgeMulitY) {
      dimensionsLimit = 2;
    }
    limitMap.put(METRICS_LIMIT_KEY, metricsLimit);
    limitMap.put(DIMENSIONS_LIMIT_KEY, dimensionsLimit);
    return limitMap;
  }


  /**
   * 处理widget的排序, dimensionData的sort、max、showOthers
   * 
   * @date: 2016年8月1日
   * @author peng.xu
   */
  public void initWidgetSort(PtoneWidgetParam ptoneWidgetParam) {
    List<String> dimensionsKeyList = ptoneWidgetParam.getDimensionsKeyList();
    List<PtoneMetricsDimension> dimensionsDataList = ptoneWidgetParam.getDimensions();
    String graphType = ptoneWidgetParam.getGraphType();
    String dsCode = ptoneWidgetParam.getDsCode();
    GraphType graph = GraphType.valueOf(graphType.toUpperCase());
    if (GraphType.PIE.equals(graph) || GraphType.COLUMN.equals(graph)
        || GraphType.BAR.equals(graph) || GraphType.LINE.equals(graph)
        || GraphType.AREA.equals(graph) || GraphType.AREASPLINE.equals(graph)) {
      int dimensionIndex = 0;
      for (PtoneMetricsDimension dd : dimensionsDataList) {
        if (dimensionsKeyList.contains(PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(dd))) {
          // 设置variable级，一条线内的数据排序和max
          if (dimensionIndex == 0) {
            this.fixDimensionSort(dd);
            ptoneWidgetParam.setVariableSortDimension(dd);
            ptoneWidgetParam.setVariableSort(dd.getSort());
            ptoneWidgetParam.setVariableMax(dd.getMax());
            ptoneWidgetParam.setVariableShowOthers(dd.getShowOthers());
          }
          // 设置widget级，各个线的排序和max
          if (dimensionIndex == this.getWidgetSortDimensionIndex(dsCode, graph)) {
            this.fixDimensionSort(dd);
            ptoneWidgetParam.setWidgetSort(dd.getSort());
            ptoneWidgetParam.setWidgetMax(dd.getMax());
            ptoneWidgetParam.setWidgetShowOthers(dd.getShowOthers());
          }
          dimensionIndex++;
        }
      }
    }
  }

  /**
   * 修正dimension设置的sort
   */
  private void fixDimensionSort(PtoneMetricsDimension dimension) {
    if (dimension != null && CollectionUtil.isNotEmpty(dimension.getSort())) {
      // sortBy 根据dataType来判断而不是依赖前台传递的参数
      String sortBy = PtoneMetricsDimension.SORT_BY_STRING;
      String dataType = dimension.getDataType();
      // time同date和datetime不同，所以没有包含在isDateDateType，time单独判断
      if (PtoneMetricsDimension.isDateDataType(dataType)
          || PtoneMetricsDimension.DATA_TYPE_TIME.equalsIgnoreCase(dataType)) {
        sortBy = PtoneMetricsDimension.SORT_BY_DATE;
      } else if (PtoneMetricsDimension.isNumberDataType(dataType)) {
        sortBy = PtoneMetricsDimension.SORT_BY_NUMBER;
      } else {
        sortBy = PtoneMetricsDimension.SORT_BY_STRING;
      }
      dimension.getSort().put(PtoneMetricsDimension.SORT_ATTRIBUTE_SORT_BY, sortBy);
    }
  }

  /**
   * widget的sort、max、showOthers设置的维度索引
   * @return
   * @date: 2016年8月1日
   * @author peng.xu
   */
  public int getWidgetSortDimensionIndex(String dsCode, GraphType graphType) {
    if ((DsConstants.DS_CODE_GA.equals(dsCode) || DsConstants.DS_CODE_PTENGINE.equals(dsCode))
        && GraphType.LINE.equals(graphType)) {
      return 0; // GA、ptengine 的线型图选择第一个维度(默认会增加时间维度)
    }
    return 1; // 默认选择第二个维度
  }

  @Override
  public PtoneSpaceInfo getWidgetSpaceInfo(AcceptWidget widget) {
    String panelId = widget.getPanelId();
    PtoneWidgetInfo baseWidget = widget.getBaseWidget();
    String spaceId = "";

    // 判断是否为widget模板
    boolean isTemplet = false;
    if (baseWidget != null) {
      isTemplet = Constants.validate.equals(baseWidget.getIsTemplate());
    }

    // panelId不为空，并且不是模板则查询panelInfo信息
    if (StringUtil.isNotBlank(panelId) && !isTemplet) {
      PtonePanelInfo panelInfo = panelService.get(panelId);
      // 查询模板
      if (panelInfo == null) {
//        PtonePanelTemplet panelTempletInfo = panelTempletService.get(panelId);
//        if (panelTempletInfo != null) {
//          spaceId = panelTempletInfo.getSpaceId();
//        }
      } else if (StringUtil.isNotBlank(panelInfo.getShareSourceId())) { // 分享的添加的panel获取原panel所在空间
        panelId = panelInfo.getShareSourceId();
        panelInfo = panelService.get(panelId);
        if (panelInfo != null) {
          spaceId = panelInfo.getSpaceId();
        }
      } else {
        spaceId = panelInfo.getSpaceId();
      }
    }

    if (StringUtil.isBlank(spaceId) && baseWidget != null) {
      spaceId = baseWidget.getSpaceId();
    }

    return spaceService.get(spaceId);
  }

}
