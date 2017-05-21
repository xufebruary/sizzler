package com.sizzler.proxy.standardModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.metamodel.util.CommonQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ptmind.common.utils.StringUtil;
import com.sizzler.cache.CurrentUserCache;
import com.sizzler.cache.DataCacheService;
import com.sizzler.common.exception.ErrorCode;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.PtoneDateUtil;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.JodaDateUtil;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionDto;
import com.sizzler.domain.sys.SysMetaLog;
import com.sizzler.domain.widget.dto.DynamicSegmentCondition;
import com.sizzler.domain.widget.dto.DynamicSegmentData;
import com.sizzler.domain.widget.dto.SegmentData;
import com.sizzler.provider.common.DataProvider;
import com.sizzler.provider.common.DataResponse;
import com.sizzler.provider.domain.request.DataBaseDataRequest;
import com.sizzler.provider.domain.request.ExcelDataRequest;
import com.sizzler.proxy.common.CommonDataUtil;
import com.sizzler.proxy.common.model.ModelData;
import com.sizzler.proxy.common.model.ModelSqlObj;
import com.sizzler.proxy.dispatcher.GraphType;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;
import com.sizzler.proxy.model.ModelDataUtil;
import com.sizzler.proxy.standardModel.model.StandardModelQueryParam;
import com.sizzler.service.DataSourceManagerService;
import com.sizzler.service.ds.PtoneDsService;
import com.sizzler.service.ds.UserConnectionSourceTableColumnService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

@Component("standardModelDataUtil")
public class StandardModelDataUtil {

  private Logger log = LoggerFactory.getLogger(StandardModelDataUtil.class);

  private static Map<String, Map<String, String>> dateTransformMap =
      new HashMap<String, Map<String, String>>();
  private static Map<String, Map<String, Map<String, String>>> dateGroupbyMap =
      new HashMap<String, Map<String, Map<String, String>>>();
  private static long tableLimit = 0; // sql table中默认限制, 0 无限制
  private static long resultLimit = 0; // sql 结果集返回限制， 0 无限制

  /**
   * 时间轴
   */
  private static final String TIME_LINE = "timeline";
  /**
   * 数据轴
   * @author you.zou by 2016.2.23
   */
  private static final String DATA_LINE = "dataline";

  /**
   * 日
   * @author you.zou by 2016.2.23
   */
  private static final String DAY = "day";

  /**
   * 周
   * @author you.zou by 2016.2.23
   */
  private static final String WEEK = "week";
  /**
   * 季度
   * @author you.zou by 2016.2.23
   */
  private static final String QUARTER = "quarter";
  /**
   * 月份
   * @author you.zou by 2016.2.24
   */
  private static final String MONTH = "month";
  /**
   * 年
   * @author you.zou by 2016.2.24
   */
  private static final String YEAR = "year";
  /**
   * 时间类型的时间粒度格式化选择前缀，如：time_hour
   */
  private static final String TIME = "time_";
  /**
   * 带有AM PM的时间类型标示
   */
  private static final String FORMATE_A = "a";

  @Autowired
  private DataProvider dataProvider;

  @Autowired
  private DataCacheService dataCacheService;

  @Autowired
  private CommonDataUtil commonDataUtil;

  @Autowired
  private DataSourceManagerService dataSourceManagerService;

  @Autowired
  private UserConnectionSourceTableColumnService userConnectionSourceTableColumnService;

  @Autowired
  private ServiceFactory serviceFactory;

  @Autowired
  private PtoneDsService ptoneDsService;

  public static Map<String, Map<String, String>> getDateTransformMap() {
    return dateTransformMap;
  }

  public static void setDateTransformMap(Map<String, Map<String, String>> dateTransformMap) {
    StandardModelDataUtil.dateTransformMap = dateTransformMap;
  }

  public static Map<String, Map<String, Map<String, String>>> getDateGroupbyMap() {
    return dateGroupbyMap;
  }

  public static void setDateGroupbyMap(Map<String, Map<String, Map<String, String>>> dateGroupbyMap) {
    StandardModelDataUtil.dateGroupbyMap = dateGroupbyMap;
  }

  public static long getTableLimit() {
    return tableLimit;
  }

  public static void setTableLimit(long tableLimit) {
    StandardModelDataUtil.tableLimit = tableLimit;
  }


  public static long getResultLimit() {
    return resultLimit;
  }

  public static void setResultLimit(long resultLimit) {
    StandardModelDataUtil.resultLimit = resultLimit;
  }

  // /////////////////////////////////////////////////////////////////////////////////

  /**
   * 统一增加从后台service取数接口（便于增加缓存）
   * 
   * @param
   * @return
   */
  public ModelData getModelData(StandardModelQueryParam modelQueryParam,
      PtoneWidgetParam ptoneWidgetParam) {
    ModelData modelData = null;
    UserConnection userConnection = ptoneWidgetParam.getUserConnection();
    String dsCode = userConnection.getDsCode();

    // 设置发送的数据内容SysMetaLog
    SysMetaLog sysMetaLog = commonDataUtil.buildCommonSysMetaLog(ptoneWidgetParam);
    Map<String, Object> operateContent =
        commonDataUtil.buildCommonOperateContent(ptoneWidgetParam, modelQueryParam);

    try {
      CommonQueryRequest queryRequest = new CommonQueryRequest();
      queryRequest.setMetrics(modelQueryParam.getMetrics());
      queryRequest.setDimensions(modelQueryParam.getDimensions());
      queryRequest.setFilters(modelQueryParam.getFilters());
      queryRequest.setSort(modelQueryParam.getSort());
      queryRequest.setStartDate(modelQueryParam.getStartDate());
      queryRequest.setEndDate(modelQueryParam.getEndDate());

      if (StringUtil.isBlank(modelQueryParam.getDimensionsId())
          && StringUtil.isBlank(modelQueryParam.getMetricsId())) {
        log.info("no available metrics and dimensions !");
        return new ModelData();
      }

      // 生成查询sql
      ModelSqlObj sqlObj = this.buildQuerySql(modelQueryParam, ptoneWidgetParam);
      queryRequest.setQuery(sqlObj.getSql());
      queryRequest.setTotalQuery(sqlObj.getTotalSql());

      // 修正filter
      String sqlWhere = sqlObj.getWhere();
      if (StringUtil.isNotBlank(sqlWhere)) {
        queryRequest.setFilters(sqlWhere.substring(sqlWhere.indexOf("where") + "where".length()));
      }

      Map<String, String> cacheKeyMap = new LinkedHashMap<String, String>();
      cacheKeyMap.put("dsCode", ptoneWidgetParam.getDsCode());
      cacheKeyMap.put("fileId", modelQueryParam.getFileId());
      cacheKeyMap.put("metrics", queryRequest.getMetrics());
      cacheKeyMap.put("dimensions", queryRequest.getDimensions());
      cacheKeyMap.put("startDate", queryRequest.getStartDate());
      cacheKeyMap.put("endDate", queryRequest.getEndDate());
      cacheKeyMap.put("filters", queryRequest.getFilters());
      cacheKeyMap.put("sort", queryRequest.getSort());
      cacheKeyMap.put("query", queryRequest.getQuery());

      if (DataBaseConfig.isDatabase(dsCode)) {
        // build db connection key info
        String connectionInfo = commonDataUtil.buildDbConnectionKey(userConnection);
        cacheKeyMap.put("connectionInfo", connectionInfo);
      } else {
        cacheKeyMap.put("tableId", modelQueryParam.getTableId());
        cacheKeyMap.put("sourceDataTime",
            String.valueOf(modelQueryParam.getSource().getLastModifiedDate()));
      }

      String cacheKey =
          dataCacheService.buildCacheKey(DataCacheService.KEY_PREFIX_WIDGET_DATA, cacheKeyMap);

      boolean isHistoryData = commonDataUtil.isHistoryData(dsCode, queryRequest.getEndDate());
      boolean useCache = commonDataUtil.isUseDsDataCache(dsCode, isHistoryData);
      boolean isNoCache = ptoneWidgetParam.isNoCache();

      if (!isNoCache && useCache && dataCacheService.existsKey(cacheKey)) {
        operateContent.put(Constants.OperateLog.QUERY_CACHE, "1");
        String dataJson = dataCacheService.getDataFromCache(cacheKey);
        if (StringUtil.isNotBlank(dataJson)) {
          modelData = JSONObject.parseObject(dataJson, ModelData.class);
        }
      }

      if (modelData == null) {
        log.info("get Data from Remote <" + cacheKey + ">");
        log.info("query SQL::: " + queryRequest.getQuery());
        DataResponse response = null;
        if (dsCode.equalsIgnoreCase(DsConstants.DS_CODE_UPLOAD)
            || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_GOOGLESHEET)
            || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_GOOGLEDRIVE)
            || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_S3)) {
          ExcelDataRequest request = new ExcelDataRequest(userConnection, queryRequest);
          request.setHdfsPath(modelQueryParam.getHdfsPath());
          request.setSchema(modelQueryParam.getSchema());
          request.setTableName(modelQueryParam.getTableCode());
          response = dataProvider.getData(request);
        } else if (DataBaseConfig.isDatabase(dsCode)) {
          DataBaseDataRequest request = new DataBaseDataRequest(userConnection, queryRequest);
          request.setDatabaseName(modelQueryParam.getSource().getFolderId());
          request.setTableName(modelQueryParam.getSource().getFileId());
          response = dataProvider.getData(request);
        }

        modelData = new ModelData();
        modelData.setObjetRowList(response.getList());
        modelData.setTotalRowList(response.getTotalRowList());
        modelData.setObjectRowColumnList(response.getRowColumnList());
        if (useCache && modelData.getObjetRowList() != null
            && modelData.getObjetRowList().size() > 0) {
          int cacheTime = commonDataUtil.getDsDataCacheTime(dsCode, isHistoryData);
          dataCacheService.cacheData(cacheKey, modelData, cacheTime);
        }
      }

    } catch (ServiceException se) {
      throw se;
    } catch (Exception e) {
      String message = e.getMessage();
      ServiceException se = new ServiceException(message, e);
      se.setErrorCode(ErrorCode.CODE_FAILED);
      se.setErrorMsg(ErrorCode.MSG_FAILED);
      throw se;
    }
    return modelData;
  }

  /**
   * 对于number环比类型图表的数据处理: number类型的取数只取总数,和上一期数比较
   * 
   * @param dateKey
   * @param currentUserCache
   * @param ptoneWidgetParam
   * @param modelQueryParam
   * @return
   * @date: 2016年7月19日
   * @author peng.xu
   */
  public ModelData getLastData(String dateKey, CurrentUserCache currentUserCache,
      PtoneWidgetParam ptoneWidgetParam, StandardModelQueryParam queryParam) {

    // 对于number类型图表的数据处理: number类型的取数只取总数,和上一期数比较
    // 根据dateKey计算上一期的开始日期和结束日期： 请求的时间格式为 yyyy-MM-dd
    Map<String, String> lastDateMap =
        PtoneDateUtil.getInstance(currentUserCache.getCurrentUserWeekStartSetting())
            .getQoqStartEndDate(dateKey, "yyyy-MM-dd");
    String lastStartDate = lastDateMap.get(JodaDateUtil.START_DATE);
    String lastEndDate = lastDateMap.get(JodaDateUtil.END_DATE);

    // 查询环比上一周期的数据
    queryParam.setStartDate(lastStartDate);
    queryParam.setEndDate(lastEndDate);
    return this.getModelData(queryParam, ptoneWidgetParam);
  }

  /**
   * 生成查询sql
   * @param
   * @param
   * @param ptoneWidgetParam
   * @return
   */
  public ModelSqlObj buildQuerySql(StandardModelQueryParam modelQueryParam,
      PtoneWidgetParam ptoneWidgetParam) {

    String dsCode = ptoneWidgetParam.getDsCode();

    String enclose = DataBaseConfig.getDatabaseEnclose(dsCode);

    // 用户的时区，用于在时间戳转换日期时使用，后续在CurrentUserCache对象中可以添加时区字段， add by you.zou 2016.2.24
    String userTz = "+09:00";

    // 用户的周起始天，用于week时间粒度 add by you.zou 2016.2.23
    Integer firstWeekDay = 0;
    if (ptoneWidgetParam.getCurrentUserCache() != null) {
      firstWeekDay = ptoneWidgetParam.getCurrentUserCache().getCurrentUserWeekStartSetting();
    }

    String graphType = ptoneWidgetParam.getGraphType();

    // String tableName = "`" + modelQueryParam.getTableCode() + "`";
    String tableName = enclose + modelQueryParam.getTableCode() + enclose;

    StringBuilder sqlBuilder = new StringBuilder("");
    StringBuilder selectBuilder = new StringBuilder(" select ");
    StringBuilder fromBuilder = new StringBuilder(" from ").append(tableName);
    StringBuilder whereBuilder = new StringBuilder("where 1=1 ");
    StringBuilder groupBuilder = new StringBuilder("");
    StringBuilder orderBuilder = new StringBuilder("");
    StringBuilder limitBuilder = new StringBuilder("");
    StringBuilder totalSqlBuilder = new StringBuilder("");

    List<String> metricsKeyList = ptoneWidgetParam.getMetricsKeyList();
    List<PtoneMetricsDimension> metricsList = modelQueryParam.getMetricsList();
    List<PtoneMetricsDimension> dimensionsList = modelQueryParam.getDimensionsList();

    List<String> dimensionsKeyList = ptoneWidgetParam.getDimensionsKeyList();// 维度列表的Keys add by
                                                                             // you.zou 2016-02-19
    List<PtoneMetricsDimension> dimensionDatas = ptoneWidgetParam.getDimensions();// 维度参数集合 add by you.zou
                                                                          // 2016-02-19
    // 清理在keyList中没有的维度参数 add by you.zou 2016-02-19
    List<PtoneMetricsDimension> currentDimensionData = new ArrayList<PtoneMetricsDimension>();
    for (PtoneMetricsDimension data : dimensionDatas) {
      if (dimensionsKeyList.contains(data.getCode() + "-" + data.getUuid())) {
        currentDimensionData.add(data);
      }
    }

    String filters = modelQueryParam.getFilters();
    String sort = modelQueryParam.getSort();
    String startDate = modelQueryParam.getStartDate();
    String endDate = modelQueryParam.getEndDate();

    boolean hasSelect = false;
    boolean hasGroup = false;
    boolean hasOrder = false;

    // standardRedshift convert to redshift
    dsCode = convertDsCode(dsCode);

    // 设置时间范围过滤 暂不支持
    PtoneMetricsDimension dateDimension = modelQueryParam.getDateDimension();
    if (dateDimension != null) {
      String colName = enclose + dateDimension.getCode() + enclose;
      String dataType = dateDimension.getDataType();
      String dateFormat = dateDimension.getDataFormat();
      String sqlDateFormat = StandardModelDataUtil.parseSqlDateFormat(dsCode, dateFormat);
      String sqlRegexp = StandardModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);

      // 设置时间范围过滤 (时间范围为空，则不过滤)
      if (StringUtil.isNotBlank(startDate) && StringUtil.isNotBlank(endDate)) {
        startDate =
            JodaDateUtil.parseDateFormate(startDate, Constants.COMMON_DATE_FORMAT,
                Constants.COMMON_START_DATETIME_FORMAT);
        endDate =
            JodaDateUtil.parseDateFormate(endDate, Constants.COMMON_DATE_FORMAT,
                Constants.COMMON_END_DATETIME_FORMAT);
        String commonDateFormat =
            StandardModelDataUtil.parseSqlDateFormat(dsCode, Constants.COMMON_DATETIME_FORMAT);
        if (PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equalsIgnoreCase(dataType)) {
          whereBuilder
              .append(" and ( ")
              .append(
                  DataBaseConfig.strToDate(dsCode,
                      DataBaseConfig.timestampToDate(dsCode, colName, commonDateFormat, userTz),
                      commonDateFormat)).append(" between ")
              .append(DataBaseConfig.strToDate(dsCode, "'" + startDate + "'", commonDateFormat))
              .append(" and ")
              .append(DataBaseConfig.strToDate(dsCode, "'" + endDate + "'", commonDateFormat))
              .append(" ) ");
        } else {
          whereBuilder.append(" and ( ").append(colName).append(" between ")
              .append("'" + startDate + "'").append(" and ").append("'" + endDate + "'")
              .append(" ) ");
          /*
           * whereBuilder.append(" and ( ").append(DataBaseConfig.strToDate(dsCode, colName,
           * sqlDateFormat)).append(" between ") .append(DataBaseConfig.strToDate(dsCode, "'" +
           * startDate + "'", commonDateFormat)).append(" and ")
           * .append(DataBaseConfig.strToDate(dsCode, "'" + endDate + "'",
           * commonDateFormat)).append(" ) ");
           */
        }

        // if(StringUtil.isNotBlank(sqlRegexp)){
        // whereBuilder.append(" and ( ").append(DataBaseConfig.getRegexpStr(dsCode, colName,
        // sqlRegexp)).append(" ) ");
        // }
      }

    }

    // 设置时间轴时间维度查询 暂不支持
    PtoneMetricsDimension xAxisDateDimension = modelQueryParam.getxAxisDateDimension();
    boolean useDateDimensionInSelect = modelQueryParam.isUseDateDimensionInSelect();
    if (xAxisDateDimension != null && useDateDimensionInSelect) {
      String colName = enclose + xAxisDateDimension.getCode() + enclose;
      String ptColName = enclose + "fix_" + xAxisDateDimension.getCode() + enclose; // 修正查询结果的列名（修正group
                                                                                    // by中时间戳的问题）
      String dataType = xAxisDateDimension.getDataType();
      String dateFormat = xAxisDateDimension.getDataFormat();
      String sqlDateFormat = StandardModelDataUtil.parseSqlDateFormat(dsCode, dateFormat);
      String sqlRegexp = StandardModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);


      // 设置时间维度select
      String returnDateFormat =
          StandardModelDataUtil.parseSqlDateFormat(dsCode, Constants.X_AXIS_DATE_FORMAT);

      // 获取时间粒度
      String datePeriod =
          getDatePeriodByDimension(xAxisDateDimension.getCode(), xAxisDateDimension.getUuid(),
              currentDimensionData);
      // 设置时间粒度select add by you.zou
      String returnGroupByDateFormate =
          getReturnGroupByDateFormat(datePeriod, returnDateFormat, dsCode, useDateDimensionInSelect);


      if (hasSelect) {
        selectBuilder.append(" , ");
      } else {
        hasSelect = true;
      }
      if (PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equalsIgnoreCase(dataType)) {
        String timeFormate =
            DataBaseConfig.timestampToDate(dsCode, colName, returnDateFormat, userTz);
        String dateFormate = null;
        if (isWeekOrQuarter(datePeriod)) {
          dateFormate =
              getWeekOrQuarterFormat(datePeriod, dsCode, timeFormate, returnGroupByDateFormate,
                  firstWeekDay);
        } else {
          dateFormate = DataBaseConfig.formatDate(dsCode, timeFormate, returnGroupByDateFormate);
        }
        selectBuilder.append(dateFormate).append(" as ").append(ptColName).append(" ");
      } else if (PtoneMetricsDimension.DATA_TYPE_DATE.equalsIgnoreCase(dataType)
          || PtoneMetricsDimension.DATA_TYPE_DATETIME.equalsIgnoreCase(dataType)) {
        String dateFormate = null;
        returnDateFormat = StandardModelDataUtil.parseSqlDateFormat(dsCode, dateFormat);
        String stringToDate = DataBaseConfig.strToDateTime(dsCode, colName, returnDateFormat);// DataBaseConfig.strToDate(dsCode,
                                                                                              // colName,
                                                                                              // returnDateFormat);
        if (isWeekOrQuarter(datePeriod)) {
          // 是有周、季度的，语句要做特殊处理
          dateFormate =
              getWeekOrQuarterFormat(datePeriod, dsCode, stringToDate, returnGroupByDateFormate,
                  firstWeekDay);
        } else {
          dateFormate = DataBaseConfig.formatDate(dsCode, stringToDate, returnGroupByDateFormate);
        }
        selectBuilder.append(dateFormate).append(" as ").append(ptColName).append(" ");
      } else {
        selectBuilder
            .append(
                DataBaseConfig.formatDate(dsCode,
                    DataBaseConfig.strToDate(dsCode, colName, sqlDateFormat), returnDateFormat))
            .append(" as ").append(ptColName).append(" ");
      }

      // 设置根据时间格式过滤数据
      /*
       * if(StringUtil.isNotBlank(sqlRegexp)){
       * whereBuilder.append(" and ( ").append(DataBaseConfig.getRegexpStr(dsCode, colName,
       * sqlRegexp)).append(" ) "); }
       */

      // 设置group by（聚合相同时间数据）
      if (hasGroup) {
        groupBuilder.append(" , ");
      } else {
        groupBuilder.append(" group by ");
        hasGroup = true;
      }
      groupBuilder.append(ptColName).append(" ");

      // 设置按时间排序
      if (hasOrder) {
        orderBuilder.append(" , ");
      } else {
        orderBuilder.append(" order by ");
        hasOrder = true;
      }
      orderBuilder.append(ptColName).append(" asc ");
    }

    // 设置维度
    boolean ignoreNullDimension = ptoneWidgetParam.isIgnoreNullDimension();
    if (dimensionsList != null) {
      for (int i = 0; i < dimensionsList.size(); i++) {
        PtoneMetricsDimension d = dimensionsList.get(i);
        String ptColName =
            enclose + PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(d) + enclose;
        String colName = enclose + d.getCode() + enclose;
        if (useDateDimensionInSelect && xAxisDateDimension != null
            && xAxisDateDimension.getUuid().equals(d.getUuid())) {
          continue; // 跳过已存在时间维度 暂不支持
        }

        if (hasSelect) {
          selectBuilder.append(" , ");
        } else {
          hasSelect = true;
        }
        String dataType = d.getDataType();
        String dateFormat = d.getDataFormat();
        // 获取时间粒度
        String datePeriod =
            getDatePeriodByDimension(d.getCode(), d.getUuid(), currentDimensionData);
        if (PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equalsIgnoreCase(dataType)) {
          // 标准类型的时间不需要时间处理
          String sqlRegexp = StandardModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);
          String returnDateFormat =
              StandardModelDataUtil.parseSqlDateFormat(dsCode, "yyyy-MM-dd HH:mm:ss");
          // 设置时间粒度select add by you.zou
          String returnGroupByDateFormate =
              getReturnGroupByDateFormat(datePeriod, returnDateFormat, dsCode, false);
          String timeFormate =
              DataBaseConfig.timestampToDate(dsCode, colName, returnDateFormat, userTz);
          String dateFormate = null;
          if (isWeekOrQuarter(datePeriod)) {
            // 是有周、季度的，语句要做特殊处理
            dateFormate =
                getWeekOrQuarterFormat(datePeriod, dsCode, timeFormate, returnGroupByDateFormate,
                    firstWeekDay);
          } else {
            dateFormate = DataBaseConfig.formatDate(dsCode, timeFormate, returnGroupByDateFormate);
          }

          selectBuilder.append(dateFormate).append(" as ").append(ptColName).append(" ");
          /*
           * // 设置根据时间格式过滤数据 if(StringUtil.isNotBlank(sqlRegexp)){
           * whereBuilder.append(" and ( ").append(DataBaseConfig.getRegexpStr(dsCode, colName,
           * sqlRegexp)).append(" ) "); }
           */
        } else if (PtoneMetricsDimension.DATA_TYPE_DATE.equalsIgnoreCase(dataType)
            || PtoneMetricsDimension.DATA_TYPE_DATETIME.equalsIgnoreCase(dataType)) {
          String sqlRegexp = StandardModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);
          String returnDateFormat = StandardModelDataUtil.parseSqlDateFormat(dsCode, dateFormat);

          // 设置时间粒度select add by you.zou
          String returnGroupByDateFormate =
              getReturnGroupByDateFormat(datePeriod, returnDateFormat, dsCode, false);

          String dateFormate = null;
          String stringToDate = DataBaseConfig.strToDateTime(dsCode, colName, returnDateFormat);// DataBaseConfig.strToDate(dsCode,
                                                                                                // colName,
                                                                                                // returnDateFormat);

          if (isWeekOrQuarter(datePeriod)) {
            // 是有周、季度的，语句要做特殊处理
            dateFormate =
                getWeekOrQuarterFormat(datePeriod, dsCode, stringToDate, returnGroupByDateFormate,
                    firstWeekDay);
          } else {
            dateFormate = DataBaseConfig.formatDate(dsCode, stringToDate, returnGroupByDateFormate);
          }

          selectBuilder.append(dateFormate).append(" as ").append(ptColName).append(" ");
          /*
           * // 设置根据时间格式过滤数据 if(StringUtil.isNotBlank(sqlRegexp)){
           * whereBuilder.append(" and ( ").append(DataBaseConfig.getRegexpStr(dsCode, colName,
           * sqlRegexp)).append(" ) "); }
           */
        } else if (PtoneMetricsDimension.DATA_TYPE_TIME.equalsIgnoreCase(dataType)) {
          String sqlRegexp = StandardModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);
          String returnDateFormat = StandardModelDataUtil.parseSqlDateFormat(dsCode, dateFormat);
          String am = "";
          // 验证该时间类型是否有AM PM的分别
          if (dateFormat != null && StringUtils.contains(dateFormat, FORMATE_A)) {
            am = FORMATE_A;
          }
          // 时间类型的特殊列
          String timeDatePeriod = TIME + datePeriod + am;
          // 找到时间粒度格式化
          String returnGroupByDateFormate =
              getReturnGroupByDateFormat(timeDatePeriod, returnDateFormat, dsCode, false);

          String dateFormate = DataBaseConfig.formatTime(dsCode, colName, returnGroupByDateFormate);

          selectBuilder.append(dateFormate).append(" as ").append(ptColName).append(" ");
          /*
           * // 设置根据时间格式过滤数据 if(StringUtil.isNotBlank(sqlRegexp)){
           * whereBuilder.append(" and ( ").append(DataBaseConfig.getRegexpStr(dsCode, colName,
           * sqlRegexp)).append(" ) "); }
           */
        } else {
          selectBuilder.append(colName).append(" as ").append(ptColName);
        }

        if (hasGroup) {
          groupBuilder.append(" , ");
        } else {
          groupBuilder.append(" group by ");
          hasGroup = true;
        }
        groupBuilder.append(ptColName).append(" ");

        // 忽略为空的维度值
        if (ignoreNullDimension) {
          whereBuilder.append(" and ( ").append(colName).append(" is not null ").append(" ) ");
        }
      }
    }

    // 设置指标
    boolean ignoreNullMetrics = ptoneWidgetParam.isIgnoreNullMetrics();
    if (metricsList != null) {
      for (int i = 0; i < metricsList.size(); i++) {
        PtoneMetricsDimension metrics = metricsList.get(i);
        String ptColName =
            enclose + PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(metrics) + enclose;
        String colName = enclose + metrics.getCode() + enclose;
        if (hasSelect) {
          selectBuilder.append(" , ");
        } else {
          hasSelect = true;
        }

        // 修正字段列表达式
        String fixColName =
            commonDataUtil.fixMetricsSqlColumn(colName, metrics, ptoneWidgetParam, false);

        // build计算字段
        if (!Constants.validate.equals(metrics.getIsContainsFunc())) {
          String calculateType = DataBaseConfig.FUNC_SUM; // 默认计算类型为sum
          String dataType = null;
          String metricsKey = metricsKeyList.get(i);
          PtoneMetricsDimension md = ptoneWidgetParam.getMetricsByKey(metricsKey);
          if (md != null && StringUtil.isNotBlank(md.getCalculateType())) {
            calculateType = md.getCalculateType();
            dataType = md.getDataType();
          }
          
          // 如果计算百分比类型数据的方差、标准差需要对数据进行修正（转为小数）
          if (PtoneMetricsDimension.DATA_TYPE_PERCENT.equals(dataType)
              && (DataBaseConfig.FUNC_STDEV.equals(calculateType) || DataBaseConfig.FUNC_VARIANCE
                  .equals(calculateType))) {
            fixColName = "((" + fixColName + ")/100)";
          }
          
          fixColName =
              DataBaseConfig.buildCalculateColumn(dsCode, fixColName, calculateType, false);
        }

        selectBuilder.append(fixColName).append(" as ").append(ptColName).append(" ");

        // 组装totalSql的select
        if (totalSqlBuilder.length() == 0) {
          totalSqlBuilder.append(" select ");
        } else {
          totalSqlBuilder.append(" , ");
        }
        totalSqlBuilder.append(fixColName).append(" as ").append(colName).append(" ");

        // 忽略为空的指标值
        if (ignoreNullMetrics) {
          whereBuilder.append(" and ( ").append(colName).append(" is not null ").append(" ) ");
        }
      }
    }

    // 设置过滤器
    if (StringUtil.isNotBlank(filters)) {
      whereBuilder.append(" and ( ").append(filters).append(" ) ");
    }

    // 设置排序
    if (StringUtil.isNotBlank(sort)) {
      if (hasOrder) {
        orderBuilder.append(" , ");
      } else {
        orderBuilder.append(" order by ");
      }
      orderBuilder.append(sort).append(" ");
    }

    // 设置limit, mysql等database 的table限制数据
    if (tableLimit > 0 && DataBaseConfig.isDatabase(dsCode)
        && GraphType.TABLE.equals(GraphType.valueOf(graphType.toUpperCase()))) {
      limitBuilder.append(DataBaseConfig.getLimitStr(dsCode, 0, tableLimit));
    } else if (resultLimit > 0) {
      limitBuilder.append(DataBaseConfig.getLimitStr(dsCode, 0, resultLimit));
    }
    sqlBuilder.append(selectBuilder).append(" ").append(fromBuilder).append(" ")
        .append(whereBuilder).append(" ").append(groupBuilder).append(" ").append(orderBuilder)
        .append(limitBuilder);

    totalSqlBuilder.append(" ").append(fromBuilder).append(" ").append(whereBuilder);

    ModelSqlObj sqlObj = new ModelSqlObj();
    sqlObj.setSelect(selectBuilder.toString());
    sqlObj.setFrom(fromBuilder.toString());
    sqlObj.setWhere(whereBuilder.toString());
    sqlObj.setGroup(groupBuilder.toString());
    sqlObj.setOrder(orderBuilder.toString());
    sqlObj.setLimit(limitBuilder.toString());
    sqlObj.setSql(sqlBuilder.toString());
    sqlObj.setTotalSql(totalSqlBuilder.toString());

    return sqlObj;
  }

  private static String convertDsCode(String dsCode) {
    if (StringUtil.isBlank(dsCode)) {
      return dsCode;
    }
    // 如果是standardRedshift数据源，使用redshift的Limit
    if (dsCode != null && dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_STANDARDREDSHIFT)) {
      dsCode = DataBaseConfig.DB_CODE_REDSHIFT;
    }
    return dsCode;
  }

  /**
   * 获取维度中的时间粒度
   * @param code 维度code
   * @param uuid 维度UUID
   * @param currentDimensionData 当前维度列表
   * @return
   */
  private static String getDatePeriodByDimension(String code, String uuid,
      List<PtoneMetricsDimension> currentDimensionData) {
    String datePeriod = DAY;
    for (PtoneMetricsDimension data : currentDimensionData) {
      if (StringUtils.equals(code + uuid, data.getCode() + data.getUuid())) {
        datePeriod = data.getDatePeriod();
        break;
      }
    }
    if (StringUtil.isBlank(datePeriod)) {
      datePeriod = DAY;
    }
    return datePeriod;
  }

  /**
   * 通过验证当前时间维度中是否有时间粒度存在，如果有则根据时间粒度获取时间格式化字符串
   * @param returnDateFormat 当没有时间粒度时使用的日期格式
   * @param dsCode 数据库code
   * @param useDateDimensionInSelect 是否有使用时间轴
   * @author you.zou by 2016.2.22
   * @return 日期格式
   */
  private static String getReturnGroupByDateFormat(String datePeriod, String returnDateFormat,
      String dsCode, Boolean useDateDimensionInSelect) {
    if (StringUtils.isBlank(datePeriod)) {
      return returnDateFormat;
    }
    String returnGroupByDateFormate = null;
    String lineType = null;
    boolean hasDatePeriod = false;
    hasDatePeriod = StringUtils.isNotBlank(datePeriod) ? true : false;
    if (hasDatePeriod) {
      datePeriod = StringUtils.lowerCase(datePeriod);// 转成小写
      lineType =
          (useDateDimensionInSelect == null || !useDateDimensionInSelect) ? DATA_LINE : TIME_LINE;
      returnGroupByDateFormate = parseGroupByDateFormate(dsCode, datePeriod, lineType);
      if (StringUtils.isBlank(returnGroupByDateFormate)) {
        returnGroupByDateFormate = returnDateFormat;
      }
    }
    return returnGroupByDateFormate;
  }

  /**
   * 验证该时间粒度是否是周、季度，如果是周季度，则X轴的数据处理不能通过时间轴的方式处理，可用户当X轴是时间轴并且有时间粒度的判断
   * @param datePeriod 时间粒度
   * @author you.zou by 2016.2.23
   * @return
   */
  public static Boolean isWeekOrQuarter(String datePeriod) {
    datePeriod = StringUtils.lowerCase(datePeriod);
    if (StringUtils.isBlank(datePeriod)) {
      return false;
    } else if (StringUtils.equals(datePeriod, WEEK) || StringUtils.equals(datePeriod, QUARTER)) {
      return true;
    }
    return false;
  }

  /**
   * 当时间粒度等于周、季度、年、月时，X轴不使用时间轴的方式显示
   * @param datePeriod 时间粒度
   * @author you.zou by 2016.2.24
   * @return
   */
  public static Boolean notUsexAxisDate(String datePeriod) {
    datePeriod = StringUtils.lowerCase(datePeriod);
    if (StringUtils.isBlank(datePeriod)) {
      return false;
    } else if (StringUtils.equals(datePeriod, WEEK) || StringUtils.equals(datePeriod, QUARTER)
        || StringUtils.equals(datePeriod, MONTH) || StringUtils.equals(datePeriod, YEAR)) {
      return true;
    }
    return false;
  }

  /**
   * 获取周、季度的语句
   * @param datePeriod 时间粒度
   * @param dbCode 数据库CODE
   * @param column 列名
   * @param dateFormat 日期数据格式
   * @param addDay 如果用户选择的周开始时间为周日，则需要+1，否则+0
   * @author you.zou by 2016.2.23
   * @return
   */
  public static String getWeekOrQuarterFormat(String datePeriod, String dbCode, String column,
      String dateFormat, Integer addDay) {
    if (StringUtils.equals(datePeriod, WEEK)) {
      return DataBaseConfig.formatWeek(dbCode, column, dateFormat, addDay);
    }
    if (StringUtils.equals(datePeriod, QUARTER)) {
      return DataBaseConfig.formatQuarter(dbCode, column, dateFormat);
    }
    return column;
  }

  /**
   * 根据不同的关系型数据库获取对应的时间粒度格式
   * @author you.zou by 2016.02.19
   * @param dsCode 数据库Code
   * @param datePeriod 时间粒度
   * @param lineTYpe 轴类型，可选：时间轴，数据轴
   * @return 时间粒度格式
   */
  private static String parseGroupByDateFormate(String dsCode, String datePeriod, String lineType) {
    if (StandardModelDataUtil.getDateGroupbyMap() != null) {
      // 如果是standardRedshift数据源，使用redshift的时间粒度格式
      if (dsCode != null && dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_STANDARDREDSHIFT)) {
        dsCode = DataBaseConfig.DB_CODE_REDSHIFT;
      }
      Map<String, Map<String, String>> dateFormateMap = null;
      for (Map.Entry<String, Map<String, Map<String, String>>> entry : StandardModelDataUtil
          .getDateGroupbyMap().entrySet()) {
        String key = entry.getKey();
        if (StringUtils.contains(key, dsCode)) {
          dateFormateMap = entry.getValue();
          break;
        }
      }
      if (dateFormateMap == null) {
        return null;
      }
      return dateFormateMap.get(lineType).get(datePeriod);
    }
    return null;
  }

  /**
   * SQL时间格式转换
   */
  private static String parseSqlDateFormat(String dsCode, String dateFormat) {
    if (StandardModelDataUtil.getDateTransformMap() != null
        && StandardModelDataUtil.getDateTransformMap().containsKey(dateFormat)
        && StandardModelDataUtil.getDateTransformMap().get(dateFormat) != null) {
      if (dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_POSTGRE)
          || dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_REDSHIFT)
          || dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_STANDARDREDSHIFT)) {
        return StandardModelDataUtil.getDateTransformMap().get(dateFormat).get("postgreDateFormat");
      } else {
        // 默认返回mysql格式
        return StandardModelDataUtil.getDateTransformMap().get(dateFormat).get("mysqlDateFormat");
      }
    }
    return dateFormat;
  }

  public static String getDateRegexp(String dsCode, String dataType, String dateFormat) {
    // 修正时间戳类型日期的格式
    if (PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equalsIgnoreCase(dataType)) {
      dateFormat = PtoneMetricsDimension.DATA_FORMAT_TIMESTAMP;
    }
    // 如果是standardRedshift数据源，使用redshift的正则
    if (dsCode != null && dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_STANDARDREDSHIFT)) {
      dsCode = DataBaseConfig.DB_CODE_REDSHIFT;
    }
    if (StandardModelDataUtil.getDateTransformMap() != null
        && StandardModelDataUtil.getDateTransformMap().containsKey(dateFormat)
        && StandardModelDataUtil.getDateTransformMap().get(dateFormat) != null) {
      return StandardModelDataUtil.getDateTransformMap().get(dateFormat).get("dateRegexp");
    }
    return null;
  }

  /**
   * 生成过滤条件（ SQL的 where子句）
   * @param segmentData
   * @return
   */
  public String parseFilter(SegmentData segmentData, String dsCode, String userTz) {
    if (segmentData == null)
      return "";
    StringBuilder filterSB = new StringBuilder("");
    String segmentType = segmentData.getType();
    if ("new".equalsIgnoreCase(segmentType) && segmentData.getNewData() != null) { // DynamicSegment：解析生成相应的
                                                                                   // SQL 过滤条件
      List<DynamicSegmentData> dynamicSegmentDataList = segmentData.getNewData();
      for (DynamicSegmentData dynamicSegmentData : dynamicSegmentDataList) {
        List<DynamicSegmentCondition> conditionList = dynamicSegmentData.getCondition();
        // fix最后一个condition条件的关系符为空
        if (conditionList.size() > 0) {
          conditionList.get(conditionList.size() - 1).setRel("");
        }

        for (DynamicSegmentCondition condition : conditionList) {
          filterSB.append(parseFilterItem(condition, dsCode, userTz));
        }
      }
    }
    return filterSB.toString();
  }

  public String parseFilterItem(DynamicSegmentCondition condition, String dsCode, String userTz) {

    dsCode = convertDsCode(dsCode);

    String enclose = DataBaseConfig.getDatabaseEnclose(dsCode);

    StringBuilder itemSB = new StringBuilder("");

    String column = condition.getCode();
    String id = condition.getId();
    String dataType = condition.getDataType();
    String op = condition.getOp();op="is_null";
    String value = condition.getValue();
    String rel = condition.getRel() != null ? condition.getRel() : "";
    String type = condition.getType();
    // 判断column、op、value值不为空
    if (StringUtil.isNotBlank(column) && StringUtil.isNotBlank(op)) {
      column = enclose + column + enclose;
      String isOrNotNullFifterString = ModelDataUtil.buildIsOrNotNullFifter(dsCode,column, op, rel,dataType);
      if (StringUtil.isNotBlank(isOrNotNullFifterString)) {
        return isOrNotNullFifterString.toString();
      }
      if (StringUtil.isNotBlank(value)) {

      // 修正复合指标的字段列表达式
      if (PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equalsIgnoreCase(type)) {
        UserCompoundMetricsDimensionDto compoundMetrics =
            ptoneDsService.getCompoundMetrics(id, null);
        column = commonDataUtil.fixCompoundMetricsColumn(compoundMetrics, false);
      }

      if (CommonDataUtil.FILTER_OP_CONTAIN.equals(op)) {
        itemSB.append(" ").append(column).append(" like '%").append(value).append("%' ")
            .append(rel);
      } else if (CommonDataUtil.FILTER_OP_NOT_CONTAIN.equals(op)) {
        itemSB.append(" ").append(column).append(" not like '%").append(value).append("%' ")
            .append(rel);
      } else if (CommonDataUtil.FILTER_OP_START.equals(op)) {
        itemSB.append(" ").append(column).append(" like '").append(value).append("%' ").append(rel);
      } else if (CommonDataUtil.FILTER_OP_NOT_START.equals(op)) {
        itemSB.append(" ").append(column).append(" not like '").append(value).append("%' ")
            .append(rel);
      } else if (CommonDataUtil.FILTER_OP_END.equals(op)) {
        itemSB.append(" ").append(column).append(" like '%").append(value).append("' ").append(rel);
      } else if (CommonDataUtil.FILTER_OP_NOT_END.equals(op)) {
        itemSB.append(" ").append(column).append(" not like '%").append(value).append("' ")
            .append(rel);
      } else if (CommonDataUtil.FILTER_OP_IN.equals(op)
          || CommonDataUtil.FILTER_OP_NOT_IN.equals(op)) {
        String opStr = CommonDataUtil.FILTER_OP_IN.equals(op) ? " in " : " not in ";
        List<String> valueList = StringUtil.splitToList(value, ",");
        itemSB.append(" ").append(column).append(opStr).append(" ( '");
        itemSB.append(StringUtil.join(valueList, "', '"));
        itemSB.append("' ) ");
        itemSB.append(rel);
      } else if (CommonDataUtil.FILTER_OP_EQUAL.equals(op)) {
        op = DataBaseConfig.parseOperator(dsCode, DataBaseConfig.DB_OPERATOR_EQ);
        itemSB.append(" ").append(column).append(" " + op + " '").append(value).append("' ")
            .append(rel);
      } else if (CommonDataUtil.FILTER_OP_NOT_EQUAL.equals(op)) {
        op = DataBaseConfig.parseOperator(dsCode, DataBaseConfig.DB_OPERATOR_NE);
        itemSB.append(" ").append(column).append(" " + op + " '").append(value).append("' ")
            .append(rel);
      } else { // > 、 >= 、 < 、 <=
        op = DataBaseConfig.parseOperator(dsCode, op);
        if (PtoneMetricsDimension.DATA_TYPE_DATE.equals(dataType)
            || PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equals(dataType)
            || PtoneMetricsDimension.DATA_TYPE_TIME.equals(dataType)
            || PtoneMetricsDimension.DATA_TYPE_DATETIME.equals(dataType)) {
          UserConnectionSourceTableColumn columnObj =
              userConnectionSourceTableColumnService.getAvailableColumn(id);
          String dateFormat = columnObj.getDataFormat();
          String sqlRegexp = StandardModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);
          itemSB.append(" ( ");
          if (StringUtil.isNotBlank(sqlRegexp)) {
            itemSB.append(column).append(" and ");
          }

          if (PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equalsIgnoreCase(columnObj.getDataType())) {
            // yyyy-MM-dd ==> %Y-%m-%d
            String commonDateFormat =
                StandardModelDataUtil.parseSqlDateFormat(dsCode, Constants.COMMON_DATE_FORMAT);

            itemSB.append(DataBaseConfig.timestampToDate(dsCode, column, commonDateFormat, userTz))
                .append(op)
                .append(DataBaseConfig.strToDate(dsCode, "'" + value + "'", commonDateFormat));
          } else {
            itemSB.append(column).append(op).append("'" + value + "'").append(" ");
          }
          itemSB.append(" ) ").append(rel);

        } else if (PtoneMetricsDimension.DATA_TYPE_NUMBER.equals(dataType)
            || PtoneMetricsDimension.DATA_TYPE_PERCENT.equals(dataType)
            || PtoneMetricsDimension.DATA_TYPE_CURRENCY.equals(dataType)
            || PtoneMetricsDimension.DATA_TYPE_DURATION.equals(dataType)) {
          // 通过加法运算将字符串转为数字 TODO: 库中有字符串的数值类型有问题
          itemSB.append(column).append(op).append("'" + value + "'").append(rel);
        } else {
          itemSB.append(" ").append(column).append(op).append(" '").append(value).append("' ")
              .append(rel);
        }
      }
    }}

    return itemSB.toString();
  }

  public String parseSort(String sortData, String dsCode) {

    String enclose = DataBaseConfig.getDatabaseEnclose(dsCode);

    StringBuilder sort = new StringBuilder("");
    boolean isFirst = true;
    if (StringUtil.isNotBlank(sortData)) {
      List<Map> sortList = JSON.parseArray(sortData, Map.class);
      for (Map map : sortList) {
        for (Object key : map.keySet()) {
          String column = (String) key;
          String sortType = (String) map.get(key);
          if (StringUtil.isNotBlank(column) && StringUtil.isNotBlank(sortType)) {
            if (!isFirst) {
              sort.append(" , ");
            } else {
              isFirst = false;
            }
            sort.append(" ").append(enclose).append(column).append(enclose).append(" ")
                .append(sortType).append(" ");
          }
        }
      }
    }
    return sort.toString();
  }

}
