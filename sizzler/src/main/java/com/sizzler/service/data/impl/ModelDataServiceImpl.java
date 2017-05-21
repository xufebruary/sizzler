/**
 * Project Name:ptone-ui-backgroud File Name:DataServiceImpl.java Package
 * Name:com.ptmind.ptone.rest.service.impl Date:2015年4月20日下午12:39:02 Copyright (c) 2015,
 * peng.xu@ptthink.com All Rights Reserved.
 * 
 */

package com.sizzler.service.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.schema.MutableSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptmind.common.utils.CollectionUtil;
import com.ptmind.common.utils.StringUtil;
import com.sizzler.cache.CurrentUserCache;
import com.sizzler.cache.PtoneBasicChartInfoCache;
import com.sizzler.common.exception.ErrorCode;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.PtoneDateUtil;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.JodaDateUtil;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableDto;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.widget.GaWidgetInfo;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.proxy.common.CommonDataUtil;
import com.sizzler.proxy.common.model.ModelData;
import com.sizzler.proxy.dispatcher.ChartDataType;
import com.sizzler.proxy.dispatcher.GraphType;
import com.sizzler.proxy.dispatcher.PtoneDispatcher;
import com.sizzler.proxy.dispatcher.PtoneVariableData;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;
import com.sizzler.proxy.model.ModelDataUtil;
import com.sizzler.proxy.model.model.ModelQueryParam;
import com.sizzler.proxy.variable.model.GraphVariableDataDesc;
import com.sizzler.service.DataSourceManagerService;
import com.sizzler.service.data.ModelDataService;
import com.sizzler.service.ds.PtoneDsService;
import com.sizzler.service.ds.UserConnectionSourceService;
import com.sizzler.service.ds.UserConnectionSourceTableColumnService;
import com.sizzler.system.Constants;

/**
 * 从数据源取数service类 <br/>
 * 
 * @author peng.xu
 * @version
 * @since JDK 1.6
 * @see
 */
@Service("modelDataService")
public class ModelDataServiceImpl implements ModelDataService {

  @Autowired
  private PtoneBasicChartInfoCache ptoneBasicChartInfoCache;

  @Autowired
  private CommonDataUtil commonDataUtil;

  @Autowired
  private ModelDataUtil modelDataUtil;

  @Autowired
  private DataSourceManagerService dataSourceManagerService;
  
  @Autowired
  private UserConnectionSourceTableColumnService userConnectionSourceTableColumnService;

  @Autowired
  private UserConnectionSourceService userConnectionSourceService;

  @Autowired
  private PtoneDsService ptoneDsService;


  @Override
  public List<PtoneVariableData> getData(PtoneWidgetInfo ptoneWidgetInfo,
      GaWidgetInfo gaWidgetInfo, PtoneVariableInfo ptoneVariableInfo,
      PtoneWidgetParam ptoneWidgetParam, Map<String, String> webParamMap) {

    CurrentUserCache currentUserCache = ptoneWidgetParam.getCurrentUserCache();
    String dsCode = ptoneWidgetParam.getDsCode();
    String graphType = ptoneWidgetParam.getGraphType();
    String chartDataType = ptoneBasicChartInfoCache.getDataTypeByChartCode(graphType);
    String dateKey = ptoneWidgetInfo.getDateKey();
    List<PtoneMetricsDimension> metricsData = ptoneWidgetParam.getMetrics();
    List<PtoneMetricsDimension> dimensionData = ptoneWidgetParam.getDimensions();
    Map<String, Object> extInfo = new HashMap<String, Object>();
    Map<String, PtoneMetricsDimension> metricsDimensionMap =
        new LinkedHashMap<String, PtoneMetricsDimension>();

    UserConnection userConnection = ptoneWidgetParam.getUserConnection();
    String tableId = gaWidgetInfo.getProfileId();
    UserConnectionSourceDto sourceDto =
        dataSourceManagerService.getSourceDtoByTableIdIncludeTable(tableId);
    if (sourceDto == null || CollectionUtil.isEmpty(sourceDto.getTables())) {
      //如果sourceDto是空，或者sourceDto下的tableDtos是空，都抛出异常
      String message = "Not find auth for this source, Please grant auth to ptone first !";
      ServiceException se = new ServiceException(message);
      if (userConnection.getDsId() == DsConstants.DS_ID_GOOGLEDRIVE
          || userConnection.getDsId() == DsConstants.DS_ID_S3) {
        se.setErrorCode(ErrorCode.CODE_NO_SOURCE_AUTH);
        se.setErrorMsg(ErrorCode.MSG_NO_SOURCE_AUTH);
      } else if (DataBaseConfig.isDatabase(dsCode)) {
        se.setErrorCode(ErrorCode.CODE_NO_SOURCE_AUTH);
        se.setErrorMsg(ErrorCode.MSG_NO_SOURCE_AUTH);
      } else {
        se.setErrorCode(ErrorCode.CODE_FAILED);
        se.setErrorMsg(ErrorCode.MSG_FAILED);
      }
      throw se;
    } else if (Constants.inValidate.equals(sourceDto.getRemoteStatus())
        && (dsCode.equalsIgnoreCase(DsConstants.DS_CODE_UPLOAD)
            || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_GOOGLESHEET)
            || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_GOOGLEDRIVE) || dsCode
              .equalsIgnoreCase(DsConstants.DS_CODE_S3))) {
      String message = "This remote source doesn't exists !";
      ServiceException se = new ServiceException(message);
      if (userConnection.getDsId() == DsConstants.DS_ID_GOOGLEDRIVE
          || userConnection.getDsId() == DsConstants.DS_ID_S3) {
        se.setErrorCode(ErrorCode.CODE_NO_SOURCE_EXISTS);
        se.setErrorMsg(ErrorCode.MSG_NO_SOURCE_EXISTS);
      } else {
        se.setErrorCode(ErrorCode.CODE_FAILED);
        se.setErrorMsg(ErrorCode.MSG_FAILED);
      }
      throw se;
    }

    if (sourceDto != null) {
      if (dsCode.equalsIgnoreCase(DsConstants.DS_CODE_UPLOAD)
          || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_GOOGLESHEET)
          || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_GOOGLEDRIVE)
          || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_S3)) {
        extInfo.put("dataUpdateTime", sourceDto.getUpdateTime());
      }
    }

    PtoneMetricsDimension dateDimension = null;
    String datePeriod = ptoneWidgetInfo.getDatePeriod();
    String dateDimensionId = ptoneWidgetParam.getDateDimensionId();
    // 设置时间维度，并且设置的的维度是时间类型
    if (StringUtil.isNotBlank(dateDimensionId)) {
      UserConnectionSourceTableColumn column =
          userConnectionSourceTableColumnService.getAvailableColumn(dateDimensionId);
      if (column != null && tableId.equals(column.getTableId())
          && PtoneMetricsDimension.isDateDimension(column.getDataType())) {
        dateDimension =
            CommonDataUtil.copyUserConnectionMetricsDimensionToPtoneMetricsDimension(column);
      }
    }

    // 修正维度，设置x轴时间维度
    PtoneMetricsDimension xAxisDateDimension = null;
    List<PtoneMetricsDimension> dimensionsList =
        commonDataUtil.fixModelDimensionList(dimensionData, ptoneWidgetParam, xAxisDateDimension,
            metricsDimensionMap);
    List<String> dimensionsKeyList = ptoneWidgetParam.getDimensionsKeyList();

    // 修正指标列表
    List<PtoneMetricsDimension> metricsList =
        commonDataUtil.fixModelMetricsList(metricsData, ptoneWidgetParam, metricsDimensionMap);
    List<String> metricsKeyList = ptoneWidgetParam.getMetricsKeyList();

    if (dimensionsKeyList.isEmpty() && metricsKeyList.isEmpty()) {
      return new ArrayList<PtoneVariableData>();
    }

    // 根据dateKey计算开始日期和结束日期： 请求的时间格式为 yyyy-MM-dd
    Map<String, String> dateMap =
        PtoneDateUtil.getInstance(currentUserCache.getCurrentUserWeekStartSetting())
            .getStartEndDate(dateKey, "yyyy-MM-dd");
    String startDate = dateMap.get(JodaDateUtil.START_DATE);
    String endDate = dateMap.get(JodaDateUtil.END_DATE);
    String filters = gaWidgetInfo.getFilters();
    String sort = gaWidgetInfo.getSort();

    // 目标值
    Number goals =
        (ptoneWidgetInfo.getTargetValue() == null || ptoneWidgetInfo.getTargetValue()
            .equalsIgnoreCase("")) ? null : Double.parseDouble(ptoneWidgetInfo.getTargetValue());

    // 为line时使用datetime类型x轴
    boolean useDatetimeAxis =
        (GraphType.LINE.equals(GraphType.valueOf(graphType.toUpperCase()))
            || GraphType.AREA.equals(GraphType.valueOf(graphType.toUpperCase())) || GraphType.AREASPLINE
            .equals(GraphType.valueOf(graphType.toUpperCase())));


    // 开启双轴后处理，开启双轴后如果没有选择维度默认增加时间，如果有维度则不增加时间粒度
    // 是否根据双轴判断（开启双轴、且轴图形不同）
    boolean judgeMulitY =
        String.valueOf(true).equals(webParamMap.get(Constants.PARAM_JUDGE_MULTI_Y));
    boolean mulitYUseDatePeriod = false;
    if (judgeMulitY
        && (GraphType.LINE.equals(GraphType.valueOf(graphType.toUpperCase())) || GraphType.COLUMN
            .equals(GraphType.valueOf(graphType.toUpperCase())))) {
      // 开启双轴时，如果用户没有设置维度则，则时间粒度生效，选择维度则时间粒度不生效
      // mulitYUseDatePeriod = (dimensionsKeyList != null && dimensionsKeyList.size() > 0);
      useDatetimeAxis = mulitYUseDatePeriod; // 开启双轴没有，没有选择时间维度增加时间粒度，则使用datetime类型x轴
    }

    // 使用时间轴并且有时间维度
    useDatetimeAxis = (useDatetimeAxis && xAxisDateDimension != null);

    String fileId = sourceDto.getFileId();
    String hdfsPath =
        Constants.buildHdfsDataPath(userConnection.getUid(), dsCode, sourceDto.getFileId());
    MutableSchema schema = dataSourceManagerService.getMutableSchemaByTableId(tableId);
    UserConnectionSourceTableDto tableDto = sourceDto.getTables().get(0);
    String tableName = tableDto.getName();
    String tableCode = tableDto.getCode();
    

    // table的sort处理
    if (GraphType.TABLE.equals(GraphType.valueOf(graphType.toUpperCase()))) {
      String sortParam = webParamMap.get(Constants.PARAM_SORT);
      if (StringUtil.isNotBlank(sortParam)) {
        sort = modelDataUtil.parseSort(sortParam, dsCode);
      } else if (StringUtil.isNotBlank(sort)) {
        sort = modelDataUtil.parseSort(sort, dsCode);
      }
      if (!StringUtil.isBlank(sort)) {
        boolean sortFlag = false;
        // 验证sort排序字段是否存在于指标、维度中，如果不存在则取消sort排序 add by you.zou 2016.3.1
        for (String dimension : dimensionsKeyList) {
          if (sort.contains(dimension)) {
            sortFlag = true;
            break;
          }
        }
        if (!sortFlag) {
          for (String metrics : metricsKeyList) {
            if (sort.contains(metrics)) {
              sortFlag = true;
              break;
            }
          }
        }
        if (!sortFlag) {
          sort = "";
        }
      }
    } else {
      sort = "";
    }

    // 创建查询参数对象
    ModelQueryParam modelQueryParam = new ModelQueryParam();
    modelQueryParam.setFileId(fileId);
    modelQueryParam.setTableId(tableId);
    modelQueryParam.setTableName(tableName);
    modelQueryParam.setTableCode(tableCode);
    modelQueryParam.setMetricsList(metricsList);
    modelQueryParam.setDimensionsList(dimensionsList);
    modelQueryParam.setMetricsDimensionMap(metricsDimensionMap);
    modelQueryParam.setDateDimension(dateDimension);
    modelQueryParam.setxAxisDateDimension(xAxisDateDimension);
    modelQueryParam.setFilters(filters);
    modelQueryParam.setSort(sort);
    modelQueryParam.setStartDate(startDate);
    modelQueryParam.setEndDate(endDate);
    modelQueryParam.setUseDateDimensionInSelect(useDatetimeAxis); // 判断时间维度是否作为查询维度（在使用时间轴时，用户没有时间维度将时间维度增加到select中）
    modelQueryParam.setDateKey(dateKey);
    modelQueryParam.setDatePeriod(datePeriod);
    modelQueryParam.setCurrentUserCache(currentUserCache);
    modelQueryParam.setSource(sourceDto.parseToSource());
    modelQueryParam.setHdfsPath(hdfsPath);
    modelQueryParam.setSchema(schema);

    // 查询数据，并对查询到的结果数据进行处理
    ModelData modelData = null;
    ModelData lastModelData = null;
    try {
      modelData = modelDataUtil.getModelData(modelQueryParam, ptoneWidgetParam);
      if (chartDataType.equalsIgnoreCase(ChartDataType.QOQNUMBER.toString())) {
        // 如果是number环比类型图表，需要获取上一期数值
        lastModelData =
            modelDataUtil.getLastData(dateKey, currentUserCache, ptoneWidgetParam, modelQueryParam);
      }

      // 查询成功，将source中删除标记改为未删除
      if (sourceDto != null && !Constants.validate.equals(sourceDto.getRemoteStatus())
          && (DataBaseConfig.isDatabase(dsCode))) {
        userConnectionSourceService.updateConnectionSourceRemoteStatusBySourceId(
            sourceDto.getSourceId(), Constants.validate);
      }
    } catch (ServiceException se) {
      String errorCode = se.getErrorCode();
      if (sourceDto != null && Constants.validate.equals(sourceDto.getRemoteStatus())
          && (DataBaseConfig.isDatabase(dsCode))) {
        if (ErrorCode.CODE_DB_LINK_FAILURE.equals(errorCode)) {
          userConnectionSourceService.updateConnectionSourceRemoteStatusBySourceId(
              sourceDto.getSourceId(), UserConnectionSource.REMOTE_STATUS_DB_LINK_FAILURE);
        } else if (ErrorCode.CODE_DB_ACCESS_DENIED.equals(errorCode)) {
          userConnectionSourceService.updateConnectionSourceRemoteStatusBySourceId(
              sourceDto.getSourceId(), UserConnectionSource.REMOTE_STATUS_DB_ACCESS_DENIED);
        } else if (ErrorCode.CODE_DB_UNKNOWN_DATABASE.equals(errorCode)) {
          userConnectionSourceService.updateConnectionSourceRemoteStatusBySourceId(
              sourceDto.getSourceId(), UserConnectionSource.REMOTE_STATUS_DB_UNKNOWN_DATABASE);
        } else if (ErrorCode.CODE_DB_UNKNOWN_TABLE.equals(errorCode)) {
          userConnectionSourceService.updateConnectionSourceRemoteStatusBySourceId(
              sourceDto.getSourceId(), UserConnectionSource.REMOTE_STATUS_DB_UNKNOWN_TABLE);
        }
      }
      throw se;
    } catch (Exception e) {
      throw e;
    }
    Map<String, Map<String, Object>> metricsTotalsMap =
        commonDataUtil.getModelMetricsTotalsMap(modelData, ptoneWidgetParam);

    // 设置单位、 数据类型
    Map<String, String> unitMap = new HashMap<String, String>();
    Map<String, String> dataTypeMap = new HashMap<String, String>();
    Map<String, String> dataFormatMap = new HashMap<String, String>();
    commonDataUtil
        .buildModelDataTypeFormatUnitMap(dataTypeMap, dataFormatMap, unitMap, metricsData);

    // 数据时间范围处理
    List<String> dateRangeList = CommonDataUtil.buildDataRangeList(startDate, endDate);

    // 将返回的数据 转换为对应的图表的格式
    GraphVariableDataDesc graphDesc =
        new GraphVariableDataDesc(ChartDataType.valueOf(chartDataType.toUpperCase()), modelData);
    graphDesc.setGraphType(GraphType.valueOf(graphType.toUpperCase()));
    graphDesc.setVariableName(ptoneVariableInfo.getVariableName());
    graphDesc.setPtoneWidgetParam(ptoneWidgetParam);
    graphDesc.setDateKey(dateKey);
    graphDesc.setGoals(goals);
    graphDesc.setCurrentUserCache(currentUserCache);
    graphDesc.setUseDatetimeAxis(useDatetimeAxis);
    graphDesc.setQueryParam(modelQueryParam);
    graphDesc.setLastModelData(lastModelData);
    List<PtoneVariableData> ptoneVariableDataList =
        PtoneDispatcher.getInstance().dispatch(graphDesc);

    boolean multiMetrics = (metricsKeyList.size() > 1); // 多个指标

    // 修正返回的变量数据 variableId 、variableName
    for (PtoneVariableData ptoneVariableData : ptoneVariableDataList) {
      ptoneVariableData.setDataKey(ptoneWidgetParam.getDataKey());
      String variableName = ptoneVariableData.getVariableName(); // dimensionValue
      String variableId = ptoneVariableInfo.getVariableId();
      String metricsName = ptoneVariableData.getMetricsName();

      ptoneVariableData.setVariableId(variableId + "-" + ptoneVariableData.getMetricsKey()
          + metricsName + variableName);
      if (variableName == null || "".equals(variableName)) {
        ptoneVariableData.setVariableName(metricsName);
      } else if (multiMetrics) {
        ptoneVariableData.setVariableName(metricsName + "-" + variableName);
      }

      // 设置单位、 数据类型
      ptoneVariableData.setUnitMap(unitMap);
      ptoneVariableData.setDataTypeMap(dataTypeMap);
      ptoneVariableData.setDataFormatMap(dataFormatMap);
      ptoneVariableData.setDateRange(dateRangeList);

      // 设置默认排序规则
      boolean containsDateDimensions = false;
      for (PtoneMetricsDimension d : dimensionsList) {
        if (PtoneMetricsDimension.isDateDimension(d.getDataType())) {
          containsDateDimensions = true;
          break;
        }
      }
      if (useDatetimeAxis || containsDateDimensions) {
        ptoneVariableData.setOrderType(PtoneMetricsDimension.SORT_BY_DATE);
      }

      // 验证当前X轴是否为时间轴，并且是否是周、季度
      // 如果是，则在数据处理中不以时间轴的方式处理
      boolean isWeekOrQuarter =
          xAxisDateDimension != null ? ModelDataUtil.notUsexAxisDate(xAxisDateDimension
              .getDatePeriod()) : false;
      if (useDatetimeAxis && isWeekOrQuarter) {
        useDatetimeAxis = false;
      }
      // 设置是否为datetime类型x轴
      ptoneVariableData.setUseDatetimeAxis(useDatetimeAxis);

      // 设置用户设置的sort、max
      commonDataUtil.setUserSortAndMax(ptoneVariableData, ptoneWidgetParam);

      if (useDatetimeAxis) {
        ptoneVariableData.setShowOthers(Constants.inValidate);
      }

    }

    if (ptoneVariableDataList.size() > 0) {
      ptoneVariableDataList.get(0).setMetricsTotalsMap(metricsTotalsMap);
      ptoneVariableDataList.get(0).getExtInfo().putAll(extInfo);
    }

    return ptoneVariableDataList;
  }

}
