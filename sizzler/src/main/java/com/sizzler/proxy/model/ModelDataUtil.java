package com.sizzler.proxy.model;

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
import com.ptmind.common.utils.CollectionUtil;
import com.ptmind.common.utils.StringUtil;
import com.sizzler.cache.CurrentUserCache;
import com.sizzler.cache.DataCacheService;
import com.sizzler.common.exception.ErrorCode;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.sizzler.DataBaseConnection;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.PtoneDateUtil;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.JodaDateUtil;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionDto;
import com.sizzler.domain.sys.SysMetaLog;
import com.sizzler.domain.widget.dto.DynamicSegmentCondition;
import com.sizzler.domain.widget.dto.DynamicSegmentData;
import com.sizzler.domain.widget.dto.SegmentData;
import com.sizzler.provider.common.DataProvider;
import com.sizzler.provider.common.DataResponse;
import com.sizzler.provider.common.MetaProvider;
import com.sizzler.provider.common.query.MetaUtil;
import com.sizzler.provider.domain.request.DataBaseDataRequest;
import com.sizzler.provider.domain.request.ExcelDataRequest;
import com.sizzler.proxy.common.CommonDataUtil;
import com.sizzler.proxy.common.model.ModelData;
import com.sizzler.proxy.common.model.ModelSqlObj;
import com.sizzler.proxy.dispatcher.GraphType;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;
import com.sizzler.proxy.model.model.ModelQueryParam;
import com.sizzler.service.DataSourceManagerService;
import com.sizzler.service.ds.PtoneDsService;
import com.sizzler.service.ds.UserConnectionSourceTableColumnService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

@Component("modelDataUtil")
public class ModelDataUtil {

  private Logger log = LoggerFactory.getLogger(ModelDataUtil.class);

  private static Map<String, Map<String, String>> dateTransformMap = new HashMap<String, Map<String, String>>();
  private static Map<String, Map<String, Map<String, String>>> dateGroupbyMap = new HashMap<String, Map<String, Map<String, String>>>();
  private static long tableLimit = 0; // sql table中默认限制, 0 无限制
  private static long resultLimit = 0; // sql 结果集返回限制， 0 无限制
  private static boolean useRdbDateRegexValidate = true; // 关系型数据库是否使用日期格式正则表达式校验，
                                                         // true: 校验 ， false:
                                                         // 不校验

  /**
   * 时间轴
   * 
   * @author you.zou by 2016.2.23
   */
  private static final String TIME_LINE = "timeline";
  /**
   * 数据轴
   * 
   * @author you.zou by 2016.2.23
   */
  private static final String DATA_LINE = "dataline";

  /**
   * 日
   * 
   * @author you.zou by 2016.2.23
   */
  private static final String DAY = "day";

  /**
   * 周
   * 
   * @author you.zou by 2016.2.23
   */
  private static final String WEEK = "week";
  /**
   * 季度
   * 
   * @author you.zou by 2016.2.23
   */
  private static final String QUARTER = "quarter";
  /**
   * 月份
   * 
   * @author you.zou by 2016.2.24
   */
  private static final String MONTH = "month";
  /**
   * 年
   * 
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
  private MetaProvider metaProvider;

  @Autowired
  private DataCacheService dataCacheService;

  @Autowired
  private CommonDataUtil commonDataUtil;

  @Autowired
  private PtoneDsService ptoneDsService;

  @Autowired
  private DataSourceManagerService dataSourceManagerService;

  @Autowired
  private UserConnectionSourceTableColumnService userConnectionSourceTableColumnService;

  @Autowired
  private ServiceFactory serviceFactory;

  public static Map<String, Map<String, String>> getDateTransformMap() {
    return dateTransformMap;
  }

  public static void setDateTransformMap(Map<String, Map<String, String>> dateTransformMap) {
    ModelDataUtil.dateTransformMap = dateTransformMap;
  }

  public static Map<String, Map<String, Map<String, String>>> getDateGroupbyMap() {
    return dateGroupbyMap;
  }

  public static void setDateGroupbyMap(Map<String, Map<String, Map<String, String>>> dateGroupbyMap) {
    ModelDataUtil.dateGroupbyMap = dateGroupbyMap;
  }

  public static long getTableLimit() {
    return tableLimit;
  }

  public static void setTableLimit(long tableLimit) {
    ModelDataUtil.tableLimit = tableLimit;
  }

  public static long getResultLimit() {
    return resultLimit;
  }

  public static void setResultLimit(long resultLimit) {
    ModelDataUtil.resultLimit = resultLimit;
  }

  public static boolean isUseRdbDateRegexValidate() {
    return useRdbDateRegexValidate;
  }

  public static void setUseRdbDateRegexValidate(boolean useRdbDateRegexValidate) {
    ModelDataUtil.useRdbDateRegexValidate = useRdbDateRegexValidate;
  }

  public MetaProvider getMetaProvider() {
    return metaProvider;
  }

  // /////////////////////////////////////////////////////////////////////////////////

  public void setMetaProvider(MetaProvider metaProvider) {
    this.metaProvider = metaProvider;
  }

  /**
   * 统一增加从后台service取数接口（便于增加缓存）
   * 
   * @param
   * @return
   */
  public ModelData getModelData(ModelQueryParam modelQueryParam, PtoneWidgetParam ptoneWidgetParam) {
    ModelData modelData = null;
    UserConnection userConnection = ptoneWidgetParam.getUserConnection();
    String dsCode = userConnection.getDsCode();

    // 设置发送的数据内容SysMetaLog
    SysMetaLog sysMetaLog = commonDataUtil.buildCommonSysMetaLog(ptoneWidgetParam);
    Map<String, Object> operateContent = commonDataUtil.buildCommonOperateContent(ptoneWidgetParam,
        modelQueryParam);

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
      cacheKeyMap.put("defaultTimezone", ptoneWidgetParam.getDefaultTimezone());
      cacheKeyMap.put("timeZone", ptoneWidgetParam.getTimeZone());
      if (DataBaseConfig.isDatabase(dsCode)) {
        // build db connection key info
        String connectionInfo = commonDataUtil.buildDbConnectionKey(userConnection);
        cacheKeyMap.put("connectionInfo", connectionInfo);
        cacheKeyMap.put("updateTime", String.valueOf(modelQueryParam.getSource().getUpdateTime()));
      } else {
        cacheKeyMap.put("tableId", modelQueryParam.getTableId());
        cacheKeyMap.put("updateTime", String.valueOf(modelQueryParam.getSource().getUpdateTime()));
        cacheKeyMap.put("sourceDataTime",
            String.valueOf(modelQueryParam.getSource().getLastModifiedDate()));
      }

      String cacheKey = dataCacheService.buildCacheKey(DataCacheService.KEY_PREFIX_WIDGET_DATA,
          cacheKeyMap);

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
   * @return
   * @date: 2016年7月19日
   * @author peng.xu
   */
  public ModelData getLastData(String dateKey, CurrentUserCache currentUserCache,
      PtoneWidgetParam ptoneWidgetParam, ModelQueryParam queryParam) {

    // 根据dateKey计算上一期的开始日期和结束日期： 请求的时间格式为 yyyy-MM-dd
    Map<String, String> lastDateMap = PtoneDateUtil.getInstance(
        currentUserCache.getCurrentUserWeekStartSetting())
        .getQoqStartEndDate(dateKey, "yyyy-MM-dd");
    String lastStartDate = lastDateMap.get(JodaDateUtil.START_DATE);
    String lastEndDate = lastDateMap.get(JodaDateUtil.END_DATE);

    // 查询环比上一周期的数据
    queryParam.setStartDate(lastStartDate);
    queryParam.setEndDate(lastEndDate);
    return this.getModelData(queryParam, ptoneWidgetParam);
  }

  /**
   * 判断是否是SQL Server数据库 如果是，则拼接模式名
   * 
   * @param modelQueryParam
   * @param dsCode
   * @param enclose
   *          @return
   * @modifi shaoqiang.guo 因为添加了连接池，故在在tableName前面添加库名
   */
  public String buildFrom(ModelQueryParam modelQueryParam, String dsCode, String enclose,
      PtoneWidgetParam ptoneWidgetParam) {
    DataBaseConnection dataBaseConnection = MetaUtil
        .createDataBaseConnectionByUserConnection(ptoneWidgetParam.getUserConnection());
    String tableCode = modelQueryParam.getTableCode();
    UserConnectionSource source = modelQueryParam.getSource();
    String folderId = source.getFolderId();
    StringBuilder tableName = new StringBuilder();
    if (dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
      String databaseName = dataBaseConnection.getDataBaseName();
      tableName.append(enclose + databaseName + enclose + ".");
    }
    if (StringUtil.isNotBlank(folderId)) {
      tableName.append(enclose + folderId + enclose + ".");
    }
    return tableName.append(enclose + tableCode + enclose).toString();
  }

  /**
   * 生成查询sql
   * 
   * @param
   * @param
   * @param ptoneWidgetParam
   * @return
   */
  public ModelSqlObj buildQuerySql(ModelQueryParam modelQueryParam,
      PtoneWidgetParam ptoneWidgetParam) {

    String dsCode = ptoneWidgetParam.getDsCode();

    String enclose = DataBaseConfig.getDatabaseEnclose(dsCode);

    // 用户的时区，用于在时间戳转换日期时使用，后续在CurrentUserCache对象中可以添加时区字段， add by you.zou
    // 2016.2.24
    // 在ptoneWidgetParam添加了timeZone字段；2017年3月16日 16:40:55 by shaoqiang.guo
    String userTz = ptoneWidgetParam.getTimeZone();

    // 用户的周起始天，用于week时间粒度 add by you.zou 2016.2.23
    Integer firstWeekDay = 0;
    if (ptoneWidgetParam.getCurrentUserCache() != null) {
      firstWeekDay = ptoneWidgetParam.getCurrentUserCache().getCurrentUserWeekStartSetting();
    }

    String graphType = ptoneWidgetParam.getGraphType();

    // String tableName = "`" + modelQueryParam.getTableCode() + "`";
    String tableName = buildFrom(modelQueryParam, dsCode, enclose, ptoneWidgetParam);

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
    // 添加当dimensionsList和metricsList为NULL时直接抛出异常
    if (CollectionUtil.isEmpty(dimensionsList) && CollectionUtil.isEmpty(metricsList)) {
      throw new ServiceException("available dimensions and metrics is null !");
    }

    List<String> dimensionsKeyList = ptoneWidgetParam.getDimensionsKeyList();// 维度列表的Keys
                                                                             // add
                                                                             // by
                                                                             // you.zou
                                                                             // 2016-02-19
    List<PtoneMetricsDimension> dimensionDatas = ptoneWidgetParam.getDimensions();// 维度参数集合
                                                                                  // add
                                                                                  // by
                                                                                  // you.zou
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

    // 设置时间范围过滤
    PtoneMetricsDimension dateDimension = modelQueryParam.getDateDimension();
    if (dateDimension != null) {
      String colName = enclose + dateDimension.getCode() + enclose;
      String dataType = dateDimension.getDataType();
      String dateFormat = dateDimension.getDataFormat();
      String sqlDateFormat = ModelDataUtil.parseSqlDateFormat(dsCode, dateFormat);
      String sqlRegexp = ModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);

      // 设置时间范围过滤 (时间范围为空，则不过滤)
      if (StringUtil.isNotBlank(startDate) && StringUtil.isNotBlank(endDate)) {
        startDate = JodaDateUtil.parseDateFormate(startDate, Constants.COMMON_DATE_FORMAT,
            Constants.COMMON_START_DATETIME_FORMAT);
        endDate = JodaDateUtil.parseDateFormate(endDate, Constants.COMMON_DATE_FORMAT,
            Constants.COMMON_END_DATETIME_FORMAT);
        String commonDateFormat = ModelDataUtil.parseSqlDateFormat(dsCode,
            Constants.COMMON_DATETIME_FORMAT);
        if (PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equalsIgnoreCase(dataType)) {
          whereBuilder.append(" and ( ");
          whereBuilder.append(DataBaseConfig.timestampToDateByTimeZone(dsCode, colName,
              commonDateFormat, userTz));
          whereBuilder.append(" between ")
              .append(DataBaseConfig.strToDate(dsCode, "'" + startDate + "'", commonDateFormat))
              .append(" and ")
              .append(DataBaseConfig.strToDate(dsCode, "'" + endDate + "'", commonDateFormat))
              .append(" ) ");
        } else {
          whereBuilder.append(" and ( ");
          if (!dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
            whereBuilder.append(DataBaseConfig.strToDate(dsCode, colName, sqlDateFormat));
          } else {
            whereBuilder.append(DataBaseConfig.strToDate(dsCode, colName, commonDateFormat));
          }
          whereBuilder.append(" between ")
              .append(DataBaseConfig.strToDate(dsCode, "'" + startDate + "'", commonDateFormat))
              .append(" and ")
              .append(DataBaseConfig.strToDate(dsCode, "'" + endDate + "'", commonDateFormat))
              .append(" ) ");
        }

        if (StringUtil.isNotBlank(sqlRegexp)) {
          // todo 需要对SQL Server做单独处理 尚未完成
          if (!dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
            whereBuilder.append(" and ( ")
                .append(DataBaseConfig.getRegexpStr(dsCode, colName, sqlRegexp)).append(" ) ");
          }
        }
      }

    }

    // 设置时间轴时间维度查询
    PtoneMetricsDimension xAxisDateDimension = modelQueryParam.getxAxisDateDimension();
    boolean useDateDimensionInSelect = modelQueryParam.isUseDateDimensionInSelect();
    if (xAxisDateDimension != null && useDateDimensionInSelect) {
      String colName = enclose + xAxisDateDimension.getCode() + enclose;
      String ptColName = enclose + "fix_" + xAxisDateDimension.getCode() + enclose; // 修正查询结果的列名（修正group
                                                                                    // by中时间戳的问题）
      String dataType = xAxisDateDimension.getDataType();
      String dateFormat = xAxisDateDimension.getDataFormat();
      String sqlDateFormat = ModelDataUtil.parseSqlDateFormat(dsCode, dateFormat);
      String sqlRegexp = ModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);

      // 设置时间维度select
      String returnDateFormat = ModelDataUtil.parseSqlDateFormat(dsCode,
          Constants.X_AXIS_DATE_FORMAT);

      // 获取时间粒度
      String datePeriod = getDatePeriodByDimension(xAxisDateDimension.getCode(),
          xAxisDateDimension.getUuid(), currentDimensionData);
      // 设置时间粒度select add by you.zou
      String returnGroupByDateFormate = getReturnGroupByDateFormat(datePeriod, returnDateFormat,
          dsCode, useDateDimensionInSelect);

      String dateFormate = null;
      if (hasSelect) {
        selectBuilder.append(" , ");
      } else {
        hasSelect = true;
      }
      if (PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equalsIgnoreCase(dataType)) {
        String timeFormate = DataBaseConfig.timestampToDateByTimeZone(dsCode, colName,
            returnDateFormat, userTz);
        if (isWeekOrQuarter(datePeriod)) {
          dateFormate = getWeekOrQuarterFormat(datePeriod, dsCode, timeFormate,
              returnGroupByDateFormate, firstWeekDay);
        } else {
          if (dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
            dateFormate = DataBaseConfig.timestampToDateByTimeZone(dsCode, colName,
                returnGroupByDateFormate, userTz);
          } else {
            dateFormate = DataBaseConfig.formatDate(dsCode, timeFormate, returnGroupByDateFormate);
          }
        }
        selectBuilder.append(dateFormate).append(" as ").append(ptColName).append(" ");
      } else if (PtoneMetricsDimension.DATA_TYPE_DATE.equalsIgnoreCase(dataType)
          || PtoneMetricsDimension.DATA_TYPE_DATETIME.equalsIgnoreCase(dataType)) {

        returnDateFormat = ModelDataUtil.parseSqlDateFormat(dsCode, dateFormat);
        String stringToDate = DataBaseConfig.strToDateTime(dsCode, colName, returnDateFormat);

        if (isWeekOrQuarter(datePeriod)) {
          // 是有周、季度的，语句要做特殊处理
          dateFormate = getWeekOrQuarterFormat(datePeriod, dsCode, stringToDate,
              returnGroupByDateFormate, firstWeekDay);
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
      if (StringUtil.isNotBlank(sqlRegexp)) {
        if (!dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
          whereBuilder.append(" and ( ")
              .append(DataBaseConfig.getRegexpStr(dsCode, colName, sqlRegexp)).append(" ) ");
        }
      }

      // 设置group by（聚合相同时间数据）
      if (hasGroup) {
        groupBuilder.append(" , ");
      } else {
        groupBuilder.append(" group by ");
        hasGroup = true;
      }
      if (DataBaseConfig.DB_CODE_SQLSERVER.equalsIgnoreCase(dsCode)) {
        if (StringUtil.isNotBlank(dateFormate)) {
          groupBuilder.append(dateFormate).append(" ");
        } else {
          groupBuilder.append(colName).append(" ");
        }
      } else {
        groupBuilder.append(ptColName).append(" ");
      }

      // 设置按时间排序
      if (hasOrder) {
        orderBuilder.append(" , ");
      } else {
        orderBuilder.append(" order by ");
        hasOrder = true;
      }
      if (!DataBaseConfig.DB_CODE_SQLSERVER.equalsIgnoreCase(dsCode)) {
        orderBuilder.append(ptColName).append(" asc ");
      } else {
        if (StringUtil.isNotBlank(dateFormate)) {
          orderBuilder.append(dateFormate).append(" ASC ");
        } else {
          orderBuilder.append(colName).append(" ASC ");
        }
      }
    }

    // 设置维度
    boolean ignoreNullDimension = ptoneWidgetParam.isIgnoreNullDimension();
    if (dimensionsList != null) {
      for (int i = 0; i < dimensionsList.size(); i++) {
        PtoneMetricsDimension d = dimensionsList.get(i);
        String ptColName = enclose + PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(d)
            + enclose;
        String colName = enclose + d.getCode() + enclose;
        if (useDateDimensionInSelect && xAxisDateDimension != null
            && xAxisDateDimension.getUuid().equals(d.getUuid())) {
          continue; // 跳过已存在时间维度
        }

        if (hasSelect) {
          selectBuilder.append(" , ");
        } else {
          hasSelect = true;
        }
        String dataType = d.getDataType();
        String dateFormat = d.getDataFormat();
        String dateFormate = null;
        // 获取时间粒度
        String datePeriod = getDatePeriodByDimension(d.getCode(), d.getUuid(), currentDimensionData);
        if (PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equalsIgnoreCase(dataType)) {
          String sqlRegexp = ModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);
          String returnDateFormat = ModelDataUtil.parseSqlDateFormat(dsCode, "yyyy-MM-dd HH:mm:ss");
          // 设置时间粒度select add by you.zou
          String returnGroupByDateFormate = getReturnGroupByDateFormat(datePeriod,
              returnDateFormat, dsCode, false);
          String timeFormate = DataBaseConfig.timestampToDateByTimeZone(dsCode, colName,
              returnDateFormat, userTz);
          // String dateFormate = null;
          if (isWeekOrQuarter(datePeriod)) {
            // 是有周、季度的，语句要做特殊处理
            dateFormate = getWeekOrQuarterFormat(datePeriod, dsCode, timeFormate,
                returnGroupByDateFormate, firstWeekDay);
          } else {
            dateFormate = DataBaseConfig.formatDate(dsCode, timeFormate, returnGroupByDateFormate);
          }

          selectBuilder.append(dateFormate).append(" as ").append(ptColName).append(" ");
          // 设置根据时间格式过滤数据
          if (StringUtil.isNotBlank(sqlRegexp)) {
            // todo 需要对SQL Server做单独处理 尚未完成
            if (!dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
              whereBuilder.append(" and ( ")
                  .append(DataBaseConfig.getRegexpStr(dsCode, colName, sqlRegexp)).append(" ) ");
            }
          }
        } else if (PtoneMetricsDimension.DATA_TYPE_DATE.equalsIgnoreCase(dataType)
            || PtoneMetricsDimension.DATA_TYPE_DATETIME.equalsIgnoreCase(dataType)) {
          String sqlRegexp = ModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);
          String returnDateFormat = ModelDataUtil.parseSqlDateFormat(dsCode, dateFormat);

          // 设置时间粒度select add by you.zou
          String returnGroupByDateFormate = getReturnGroupByDateFormat(datePeriod,
              returnDateFormat, dsCode, false);

          // String dateFormate = null;
          String stringToDate = DataBaseConfig.strToDateTime(dsCode, colName, returnDateFormat);// DataBaseConfig.strToDate(dsCode,
                                                                                                // colName,
                                                                                                // returnDateFormat);

          if (isWeekOrQuarter(datePeriod)) {
            // 是有周、季度的，语句要做特殊处理
            dateFormate = getWeekOrQuarterFormat(datePeriod, dsCode, stringToDate,
                returnGroupByDateFormate, firstWeekDay);
          } else {
            dateFormate = DataBaseConfig.formatDate(dsCode, stringToDate, returnGroupByDateFormate);
          }

          selectBuilder.append(dateFormate).append(" as ").append(ptColName).append(" ");
          // 设置根据时间格式过滤数据
          if (StringUtil.isNotBlank(sqlRegexp)) {
            // todo 需要对SQL Server做单独处理 尚未完成
            if (!dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
              whereBuilder.append(" and ( ")
                  .append(DataBaseConfig.getRegexpStr(dsCode, colName, sqlRegexp)).append(" ) ");
            }
          }
        } else if (PtoneMetricsDimension.DATA_TYPE_TIME.equalsIgnoreCase(dataType)) {
          String sqlRegexp = ModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);
          String returnDateFormat = ModelDataUtil.parseSqlDateFormat(dsCode, dateFormat);
          String am = "";
          // 验证该时间类型是否有AM PM的分别
          if (dateFormat != null && StringUtils.contains(dateFormat, FORMATE_A)) {
            am = FORMATE_A;
          }
          // 时间类型的特殊列
          String timeDatePeriod = TIME + datePeriod + am;
          // 找到时间粒度格式化
          String returnGroupByDateFormate = getReturnGroupByDateFormat(timeDatePeriod,
              returnDateFormat, dsCode, false);

          dateFormate = DataBaseConfig.formatTime(dsCode, colName, returnGroupByDateFormate);

          selectBuilder.append(dateFormate).append(" as ").append(ptColName).append(" ");
          // 设置根据时间格式过滤数据
          if (StringUtil.isNotBlank(sqlRegexp)) {
            // todo 需要对SQL Server做单独处理 尚未完成
            if (!dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
              whereBuilder.append(" and ( ")
                  .append(DataBaseConfig.getRegexpStr(dsCode, colName, sqlRegexp)).append(" ) ");
            }
          }
        } else {
          selectBuilder.append(colName).append(" as ").append(ptColName);
        }

        if (hasGroup) {
          groupBuilder.append(" , ");
        } else {
          groupBuilder.append(" group by ");
          hasGroup = true;
        }
        if (DataBaseConfig.DB_CODE_SQLSERVER.equalsIgnoreCase(dsCode)) {
          if (StringUtil.isNotBlank(dateFormate)) {
            groupBuilder.append(dateFormate).append(" ");
          } else {
            groupBuilder.append(colName).append(" ");
          }
        } else {
          groupBuilder.append(ptColName).append(" ");
        }
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
        String ptColName = enclose
            + PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(metrics) + enclose;
        String colName = enclose + metrics.getCode() + enclose;
        if (hasSelect) {
          selectBuilder.append(" , ");
        } else {
          hasSelect = true;
        }

        // 修正字段列表达式
        String fixColName = commonDataUtil.fixMetricsSqlColumn(colName, metrics, ptoneWidgetParam,
            true);

        // build计算字段
        fixColName = CommonDataUtil.buildMetricsCalculate(fixColName, metrics,
            metricsKeyList.get(i), dsCode, ptoneWidgetParam);

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
        .append(whereBuilder).append(" ").append(groupBuilder).append(" ").append(orderBuilder);
    if (DataBaseConfig.DB_CODE_SQLSERVER.equalsIgnoreCase(dsCode)) {
      int index = sqlBuilder.indexOf("select");
      String topString = limitBuilder.toString();
      sqlBuilder = sqlBuilder.insert(index + 7, topString);
    } else {
      sqlBuilder.append(limitBuilder);
    }
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

  /**
   * 获取维度中的时间粒度
   * 
   * @param code
   *          维度code
   * @param uuid
   *          维度UUID
   * @param currentDimensionData
   *          当前维度列表
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
   * 
   * @param returnDateFormat
   *          当没有时间粒度时使用的日期格式
   * @param dsCode
   *          数据库code
   * @param useDateDimensionInSelect
   *          是否有使用时间轴
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
      lineType = (useDateDimensionInSelect == null || !useDateDimensionInSelect) ? DATA_LINE
          : TIME_LINE;
      returnGroupByDateFormate = parseGroupByDateFormate(dsCode, datePeriod, lineType);
      if (StringUtils.isBlank(returnGroupByDateFormate)) {
        returnGroupByDateFormate = returnDateFormat;
      }
    }
    return returnGroupByDateFormate;
  }

  /**
   * 验证该时间粒度是否是周、季度，如果是周季度，则X轴的数据处理不能通过时间轴的方式处理，可用户当X轴是时间轴并且有时间粒度的判断
   * 
   * @param datePeriod
   *          时间粒度
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
   * 
   * @param datePeriod
   *          时间粒度
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
   * 
   * @param datePeriod
   *          时间粒度
   * @param dbCode
   *          数据库CODE
   * @param column
   *          列名
   * @param dateFormat
   *          日期数据格式
   * @param addDay
   *          如果用户选择的周开始时间为周日，则需要+1，否则+0
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
   * 
   * @author you.zou by 2016.02.19
   * @param dsCode
   *          数据库Code
   * @param datePeriod
   *          时间粒度
   * @return 时间粒度格式
   */
  private static String parseGroupByDateFormate(String dsCode, String datePeriod, String lineType) {
    if (ModelDataUtil.getDateGroupbyMap() != null) {
      Map<String, Map<String, String>> dateFormateMap = null;
      for (Map.Entry<String, Map<String, Map<String, String>>> entry : ModelDataUtil
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
  public static String parseSqlDateFormat(String dsCode, String dateFormat) {
    if (ModelDataUtil.getDateTransformMap() != null
        && ModelDataUtil.getDateTransformMap().containsKey(dateFormat)
        && ModelDataUtil.getDateTransformMap().get(dateFormat) != null) {
      if (dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_POSTGRE)
          || dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_REDSHIFT)) {
        return ModelDataUtil.getDateTransformMap().get(dateFormat).get("postgreDateFormat");
      } else if (dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
        return ModelDataUtil.getDateTransformMap().get(dateFormat).get("sqlserverDateFormat");
      } else {
        // 默认返回mysql格式
        return ModelDataUtil.getDateTransformMap().get(dateFormat).get("mysqlDateFormat");
      }
    }
    return dateFormat;
  }

  public static String getDateRegexp(String dsCode, String dataType, String dateFormat) {
    // 如果不使用关系型数据库的date正则校验，则返回null
    if (!isUseDateRegexValidate(dsCode)) {
      return null;
    }
    // 修正时间戳类型日期的格式
    if (PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equalsIgnoreCase(dataType)) {
      dateFormat = PtoneMetricsDimension.DATA_FORMAT_TIMESTAMP;
    }
    if (ModelDataUtil.getDateTransformMap() != null
        && ModelDataUtil.getDateTransformMap().containsKey(dateFormat)
        && ModelDataUtil.getDateTransformMap().get(dateFormat) != null) {
      return ModelDataUtil.getDateTransformMap().get(dateFormat).get("dateRegexp");
    }
    return null;
  }

  /**
   * 生成过滤条件（ SQL的 where子句）
   * 
   * @param segmentData
   * @return
   */
  public String parseFilter(SegmentData segmentData, String dsCode, String userTz) {
    if (segmentData == null)
      return "";
    StringBuilder filterSB = new StringBuilder("");
    String segmentType = segmentData.getType();
    if ("new".equalsIgnoreCase(segmentType) && segmentData.getNewData() != null) { // DynamicSegment：解析生成相应的
                                                                                   // SQL
                                                                                   // 过滤条件
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

    String enclose = DataBaseConfig.getDatabaseEnclose(dsCode);

    StringBuilder itemSB = new StringBuilder("");

    String column = condition.getCode();
    String id = condition.getId();
    String dataType = condition.getDataType();
    String op = condition.getOp();
    String value = condition.getValue();
    String rel = condition.getRel() != null ? condition.getRel() : "";
    String type = condition.getType();

    // 判断column、op、value值不为空
    if (StringUtil.isNotBlank(column) && StringUtil.isNotBlank(op)) {
      column = enclose + column + enclose;
      String isOrNotNullFifterString = buildIsOrNotNullFifter(dsCode, column, op, rel, dataType);
      if (StringUtil.isNotBlank(isOrNotNullFifterString)) {
        return isOrNotNullFifterString.toString();
      }
      if (StringUtil.isNotBlank(value)) {
        // 修正复合指标的字段列表达式
        if (PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equalsIgnoreCase(type)) {
          UserCompoundMetricsDimensionDto compoundMetrics = ptoneDsService.getCompoundMetrics(id,
              null);
          column = commonDataUtil.fixCompoundMetricsColumn(compoundMetrics, true);
        }

        if (CommonDataUtil.FILTER_OP_CONTAIN.equals(op)) {
          itemSB.append(" ").append(column).append(" like '%").append(value).append("%' ")
              .append(rel);
        } else if (CommonDataUtil.FILTER_OP_NOT_CONTAIN.equals(op)) {
          itemSB.append(" ").append(column).append(" not like '%").append(value).append("%' ")
              .append(rel);
        } else if (CommonDataUtil.FILTER_OP_START.equals(op)) {
          itemSB.append(" ").append(column).append(" like '").append(value).append("%' ")
              .append(rel);
        } else if (CommonDataUtil.FILTER_OP_NOT_START.equals(op)) {
          itemSB.append(" ").append(column).append(" not like '").append(value).append("%' ")
              .append(rel);
        } else if (CommonDataUtil.FILTER_OP_END.equals(op)) {
          itemSB.append(" ").append(column).append(" like '%").append(value).append("' ")
              .append(rel);
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
            UserConnectionSourceTableColumn columnObj = userConnectionSourceTableColumnService
                .getAvailableColumn(id);
            String dateFormat = columnObj.getDataFormat();
            String sqlDateFormat = ModelDataUtil.parseSqlDateFormat(dsCode, dateFormat);
            String sqlRegexp = ModelDataUtil.getDateRegexp(dsCode, dataType, dateFormat);
            itemSB.append(" ( ");
            if (StringUtil.isNotBlank(sqlRegexp)) {
              // todo 需要对SQL Server做单独处理 尚未完成
              if (!dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
                itemSB.append(DataBaseConfig.getRegexpStr(dsCode, column, sqlRegexp)).append(
                    " and ");
              }
            }

            if (PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equalsIgnoreCase(columnObj.getDataType())) {

              // 包含时分秒
              String commonDateFormat = ModelDataUtil.parseSqlDateFormat(dsCode,
                  Constants.COMMON_DATETIME_FORMAT);

              if (dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
                if (value.length() == 4) {
                  itemSB
                      .append(
                          DataBaseConfig.formatSqlserverTimestampByTimezone(
                              getGroupByVaule(dsCode, "year"), column, userTz))
                      .append(op)
                      .append(
                          DataBaseConfig.formatDate(dsCode, "'" + value + "'",
                              getGroupByVaule(dsCode, "year")));
                } else if (value.length() == 10) {
                  itemSB
                      .append(
                          DataBaseConfig.formatSqlserverTimestampByTimezone(
                              getGroupByVaule(dsCode, "day"), column, userTz))
                      .append(op)
                      .append(
                          DataBaseConfig.formatDate(dsCode, "'" + value + "'",
                              getGroupByVaule(dsCode, "day")));
                } else {
                  itemSB
                      .append(
                          DataBaseConfig.formatSqlserverTimestampByTimezone(
                              getGroupByVaule(dsCode, "seconds"), column, userTz))
                      .append(op)
                      .append(DataBaseConfig.strToDate(dsCode, "'" + value + "'", commonDateFormat));
                }
              } else {
                itemSB
                    .append(
                        DataBaseConfig.formatDate(dsCode, DataBaseConfig.timestampToDateByTimeZone(
                            dsCode, column, commonDateFormat, userTz), commonDateFormat))
                    .append(op)
                    .append(DataBaseConfig.strToDate(dsCode, "'" + value + "'", commonDateFormat));
              }
            } else {
              if (dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_SQLSERVER)) {
                if (value.length() == 4) {
                  itemSB
                      .append(
                          DataBaseConfig.formatDate(dsCode,
                              DataBaseConfig.strToDate(dsCode, column, sqlDateFormat),
                              getGroupByVaule(dsCode, "year")))
                      .append(op)
                      .append(
                          DataBaseConfig.formatDate(dsCode, "'" + value + "'",
                              getGroupByVaule(dsCode, "year"))).append(" ");

                } else if (value.length() == 10) {
                  itemSB
                      .append(
                          DataBaseConfig.formatDate(dsCode,
                              DataBaseConfig.strToDate(dsCode, column, sqlDateFormat),
                              getGroupByVaule(dsCode, "day")))
                      .append(op)
                      .append(
                          DataBaseConfig.formatDate(dsCode, "'" + value + "'",
                              getGroupByVaule(dsCode, "day"))).append(" ");
                } else {
                  itemSB.append(DataBaseConfig.strToDate(dsCode, column, sqlDateFormat)).append(op)
                      .append(DataBaseConfig.strToDate(dsCode, "'" + value + "'", sqlDateFormat))
                      .append(" ");
                }
              } else {
                itemSB.append(DataBaseConfig.strToDate(dsCode, column, sqlDateFormat)).append(op)
                    .append(DataBaseConfig.strToDate(dsCode, "'" + value + "'", sqlDateFormat))
                    .append(" ");
              }
            }
            itemSB.append(" ) ").append(rel);

          } else if (PtoneMetricsDimension.DATA_TYPE_NUMBER.equals(dataType)
              || PtoneMetricsDimension.DATA_TYPE_PERCENT.equals(dataType)
              || PtoneMetricsDimension.DATA_TYPE_CURRENCY.equals(dataType)
              || PtoneMetricsDimension.DATA_TYPE_DURATION.equals(dataType)) {

            // 修正字段
            String fixColName = CommonDataUtil.fixColumnByDataType(column, dataType, dsCode);

            // 通过加法运算将字符串转为数字 TODO: 库中有字符串的数值类型有问题
            itemSB.append(DataBaseConfig.toNumber(dsCode, fixColName)).append(op)
                .append(DataBaseConfig.toNumber(dsCode, "'" + value + "'")).append(rel);
          } else {
            itemSB.append(" ").append(column).append(op).append(" '").append(value).append("' ")
                .append(rel);
          }
        }
      }
    }

    return itemSB.toString();
  }

  /**
   * 
   * 过滤器值支持Is Null 和 Is Not Null; posgre数值和日期格式不能使用 = '' 和 = ' '
   * 
   * @author shaoqiang.guo
   * @date 2017年4月14日 上午11:29:43
   * @param dsCode
   * @param column
   * @param op
   * @param rel
   * @param dataType
   * @return 因为此处不需要value，但是前端value会保存带过来，所以单独判断，满足条件即返回
   */
  public static String buildIsOrNotNullFifter(String dsCode, String column, String op, String rel,
      String dataType) {
    StringBuilder filter = new StringBuilder();
    if (CommonDataUtil.FILTER_OP_IS_NULL.equals(op)) {
      if (dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_POSTGRE) && isNumberOrDate(dataType)) {
        filter.append(buildIsNull(column) + ") " + rel);
      } else {
        filter.append(buildIsNull(column) + " or " + column + " = ''  or " + column + " = ' ') "
            + rel);
      }
    } else if (CommonDataUtil.FILTER_OP_IS_NOT_NULL.equals(op)) {
      if (dsCode.equalsIgnoreCase(DataBaseConfig.DB_CODE_POSTGRE) && isNumberOrDate(dataType)) {
        filter.append(buildIsNotNull(column) + " ) " + rel);
      } else {
        filter.append(buildIsNotNull(column) + " and " + column + " <> '' and " + column
            + " <> ' ') " + rel);
      }
    }
    return filter.toString();
  }

  /**
   * 字段类型是数值或时间日期等格式
   * 
   * @author shaoqiang.guo
   * @date 2017年4月14日 上午11:08:01
   * @param dataType
   * @return
   */
  public static boolean isNumberOrDate(String dataType) {
    return PtoneMetricsDimension.DATA_TYPE_NUMBER.equals(dataType)
        || PtoneMetricsDimension.DATA_TYPE_PERCENT.equals(dataType)
        || PtoneMetricsDimension.DATA_TYPE_CURRENCY.equals(dataType)
        || PtoneMetricsDimension.DATA_TYPE_DURATION.equals(dataType)
        || PtoneMetricsDimension.DATA_TYPE_DATE.equals(dataType)
        || PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equals(dataType)
        || PtoneMetricsDimension.DATA_TYPE_TIME.equals(dataType)
        || PtoneMetricsDimension.DATA_TYPE_DATETIME.equals(dataType);
  }

  /**
   * is null
   * 
   * @author shaoqiang.guo
   * @date 2017年4月14日 上午11:10:37
   * @param column
   * @return
   */
  public static String buildIsNull(String column) {
    return "( " + column + " IS NULL";
  }

  /**
   * is not null
   * 
   * @author shaoqiang.guo
   * @date 2017年4月14日 上午11:10:53
   * @param column
   * @return
   */
  public static String buildIsNotNull(String column) {
    return "( " + column + " IS NOT NULL";
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

  /**
   * 是否使用日期正则表达式校验
   * 
   * @return
   * @date: 2016年11月28日
   * @author peng.xu
   */
  public static boolean isUseDateRegexValidate(String dsCode) {
    if (!isUseRdbDateRegexValidate() && DataBaseConfig.isDatabase(dsCode)) {
      return false;
    }
    return true;
  }

  /**
   * 获取filter中GroupBy的值
   * 
   * @author shaoqiang.guo
   * @date 2017年4月19日 下午12:19:31
   * @param dsCode
   * @param datePeriod
   * @return
   */
  public static String getGroupByVaule(String dsCode, String datePeriod) {
    return ModelDataUtil.getDateGroupbyMap().get(dsCode).get("dataline").get(datePeriod);
  }

}
