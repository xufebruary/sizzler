package com.sizzler.service.impl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.PtoneDateUtil;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.JodaDateUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.ds.vo.ConnectionTimezoneVo;
import com.sizzler.domain.ds.vo.TimezoneVo;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.variable.dto.PtVariables;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.domain.widget.dto.AcceptWidget;
import com.sizzler.domain.widget.dto.PtoneWidgetChartSettingDto;
import com.sizzler.service.DataService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.ThreadPoolConstants;

@Service("dataService")
public class DataServiceImpl implements DataService {

  private static String LOG_PREFIX = "[WidgetDataService] ";
  private static Logger log = LoggerFactory.getLogger(DataServiceImpl.class);

  @Autowired
  private ServiceFactory serviceFactory;

  @Autowired
  private ThreadPoolConstants threadPoolConstants; // 为保证先初始化ThreadPoolConstants的配置

  /**
   * ptengine线程池
   */
  private ThreadPoolExecutor ptengineThreadPoolExecutor; // ptengine线程池
  private String ptengineThreadPoolName = "ptengineThreadPoolExecutor"; // ptengine线程池名称
  /**
   * 默认线程池
   */
  private ThreadPoolExecutor defaultThreadPoolExecutor; // 默认线程池
  private String defaultThreadPoolName = "defaultThreadPoolExecutor";// 默认线程池名称

  private final static String TIMEZONE ="timeZone";
  private final static String DEFAULT_TIMEZONE ="defaultTimezone";
  /**
   * 
   * 创建线程池，最小线程数为minSize，最大线程数为maxSize，线程池维护线程的空闲时间为keepAliveTime分钟， 使用队列大小为queuesize的有界队列
   * 
   * @date: 2016年9月12日
   * @author peng.xu
   */
  @PostConstruct
  public void initThreadPool() {
//    // 初始化ptengine查询线程池
//    if (ptengineThreadPoolExecutor == null) {
//      this.ptengineThreadPoolExecutor =
//          new ThreadPoolExecutor(ThreadPoolConstants.ptengineThreadPoolMinSize,
//              ThreadPoolConstants.ptengineThreadPoolMaxSize,
//              ThreadPoolConstants.ptengineThreadPoolKeepAliveTime, TimeUnit.MINUTES,
//              new ArrayBlockingQueue<Runnable>(ThreadPoolConstants.ptengineThreadPoolQueueSize),
//              new NamedThreadFactory(this.ptengineThreadPoolName, true),
//              new ThreadPoolExecutor.DiscardOldestPolicy());
//      log.info(LOG_PREFIX + "init ptengineThreadPoolExecutor, minPoolSize="
//          + ThreadPoolConstants.ptengineThreadPoolMinSize + ", maxPoolSize="
//          + ThreadPoolConstants.ptengineThreadPoolMaxSize + ", keepAliveTime="
//          + ThreadPoolConstants.ptengineThreadPoolKeepAliveTime + ", queueSize="
//          + ThreadPoolConstants.ptengineThreadPoolQueueSize);
//    }
//
//    // 初始化默认查询线程池
//    if (defaultThreadPoolExecutor == null) {
//      this.defaultThreadPoolExecutor =
//          new ThreadPoolExecutor(ThreadPoolConstants.defaultThreadPoolMinSize,
//              ThreadPoolConstants.defaultThreadPoolMaxSize,
//              ThreadPoolConstants.defaultThreadPoolKeepAliveTime, TimeUnit.MINUTES,
//              new ArrayBlockingQueue<Runnable>(ThreadPoolConstants.defaultThreadPoolQueueSize),
//              new NamedThreadFactory(this.defaultThreadPoolName, true),
//              new ThreadPoolExecutor.DiscardOldestPolicy());
//      log.info(LOG_PREFIX + "init defaultThreadPoolExecutor, minPoolSize="
//          + ThreadPoolConstants.defaultThreadPoolMinSize + ", maxPoolSize="
//          + ThreadPoolConstants.defaultThreadPoolMaxSize + ", keepAliveTime="
//          + ThreadPoolConstants.defaultThreadPoolKeepAliveTime + ", queueSize="
//          + ThreadPoolConstants.defaultThreadPoolQueueSize);
//    }

  }

  /**
   * 
   * 根据数据源是否支持时区，是否使用数据集默认时区，在缓存Map，和  webParamMap设置时区
   * @author shaoqiang.guo
   * @date 2017年3月20日 下午6:49:40
   * @param webParamMap
   * @param widget
   * @param cacheKeyMap
   */
  public void setTimeZone(Map<String, String> webParamMap, AcceptWidget widget,
      Map<String, String> cacheKeyMap) {
    List<PtVariables> ptVariables = widget.getVariables();

    if (CollectionUtil.isEmpty(ptVariables) || ptVariables.get(0) == null) {
      return;
    }

    PtVariables ptVariable = ptVariables.get(0);

    String profileId = ptVariable.getProfileId();
    String connectionId = ptVariable.getConnectionId();

    if (StringUtil.isBlank(profileId) || StringUtil.isBlank(connectionId)) {
      return;
    }
    String dsCode = ptVariable.getDsCode();
    if (StringUtil.isBlank(dsCode)) {
      return;
    }

    PtoneDsInfo ptoneDsInfo = serviceFactory.getPtoneDsService().getDsInfoByDsCode(dsCode);
    if (ptoneDsInfo == null) {
      return;
    }
    String supportTimeZone = ptoneDsInfo.getSupportTimezone();
    if (StringUtil.isBlank(supportTimeZone)
        || supportTimeZone.equalsIgnoreCase(Constants.inValidate)) {
      return;
    }

    // 不是api类型的数据源，根据ProfileId去查询SourceId
    String sourceId = null;
    if (!DsConstants.isApiDs(dsCode)) {
      sourceId = serviceFactory.getPtoneUserConnectionService().getSourceIdByTableId(profileId);
    }
    ConnectionTimezoneVo connectionTimezoneVo =
        serviceFactory.getPtoneUserConnectionService().getConnectionTimezone(ptoneDsInfo.getId(),
            connectionId, sourceId);
    if (connectionTimezoneVo == null) {
      return;
    }
    String defaultTimezone = connectionTimezoneVo.getIsDefaultTimezone();
    if (StringUtil.isBlank(defaultTimezone) || defaultTimezone.equalsIgnoreCase(Constants.validate)) {
      return;
    }
    TimezoneVo timezoneVo = connectionTimezoneVo.getDataTimezone();
    if (timezoneVo == null) {
      return;
    }
    webParamMap.put(TIMEZONE, timezoneVo.getCode());
    webParamMap.put(DEFAULT_TIMEZONE, defaultTimezone);
    cacheKeyMap.put(TIMEZONE, timezoneVo.getCode());
    cacheKeyMap.put(DEFAULT_TIMEZONE, defaultTimezone);
  }
  
  private void addWidgetDataTaskToThreadPool(ThreadPoolExecutor executor,
      WidgetDataTask widgetDataTask, String dsCode) throws Exception {
    int poolQueueSize = ThreadPoolConstants.defaultThreadPoolQueueSize;
    String threadPoolName = this.defaultThreadPoolName;

    while (executor.getQueue().size() >= poolQueueSize) {
      log.warn(LOG_PREFIX + threadPoolName + " queue is full(" + executor.getQueue().size()
          + " >= " + poolQueueSize + "), wait 1s tyr again add WdigetDataTask<"
          + widgetDataTask.getWidgetId() + ">");
      Thread.sleep(ThreadPoolConstants.defaultAddTaskWaitingTime);
    }
    log.info(LOG_PREFIX + "add new " + dsCode + "WidgetDataTask<" + widgetDataTask.getWidgetId()
        + ">, " + threadPoolName + " queue size is " + executor.getQueue().size() + " / "
        + poolQueueSize);

    executor.execute(widgetDataTask);

    String executorMsg =
        String
            .format(
                "Current Thread pool info: "
                    + " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),"
                    + " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s) !",
                threadPoolName, executor.getPoolSize(), executor.getActiveCount(),
                executor.getCorePoolSize(), executor.getMaximumPoolSize(),
                executor.getLargestPoolSize(), executor.getTaskCount(),
                executor.getCompletedTaskCount(), executor.isShutdown(), executor.isTerminated(),
                executor.isTerminating());
    log.info(LOG_PREFIX + executorMsg);

    LogMessage logMessage = new LogMessage();
    logMessage.setOperate("WidgetDataServiceThreadPoolInfo");
    logMessage.addOperateInfo("widgetDataThreadPoolName", threadPoolName);
    logMessage.addOperateInfo("widgetDataThreadPoolSize", executor.getPoolSize());
    logMessage.addOperateInfo("widgetDataThreadPoolActiveCount", executor.getActiveCount());
    logMessage.addOperateInfo("widgetDataThreadPoolCorePoolSize", executor.getCorePoolSize());
    logMessage.addOperateInfo("widgetDataThreadPoolMaxPoolSize", executor.getMaximumPoolSize());
    logMessage.addOperateInfo("widgetDataThreadPoolLargestPoolSize", executor.getLargestPoolSize());
    logMessage.addOperateInfo("widgetDataThreadPoolTaskCount", executor.getTaskCount());
    logMessage.addOperateInfo("widgetDataThreadPoolCompletedTaskCount",
        executor.getCompletedTaskCount());
    logMessage.addOperateInfo("widgetDataThreadPoolQueueSize", executor.getQueue().size());
    ElkLogUtil.info(logMessage.generateJsonString());
  }

  @Override
  public void addDataTask(String widgetId, Map<String, String> webParamMap, AcceptWidget widget) {
    LogMessage logMessage = new LogMessage();
    logMessage.setOperate("WidgetDataService-middle");
    String loginUid = null;
    String loginUserEmail = null;
    boolean noCache = false;
    try {
      // widget数据源设置相关
      String dsCode = null;
      String account = null;
      String profileId = null;
      String metrics = null;
      String dimensions = null;
      String dateDimensionId = null;
      String filters = null;
      String segment = null;
      String sort = null;
      String startIndex = null;
      String maxResults = null;
      // 数据源相关
      String connectionSourceInfoKey = null; // 包含connectionInfo、sourceUpdateTime、sourceDataUpdateTime
      // baseWidget设置相关
      String graph = null;
      String mapCode = null;
      String isDemo = null;
      String isExample = null;
      String datePeriod = null;
      String weekStart = null;// 空间上设置周起始日
      String startDate = null;
      String endDate = null;
      // chartSetting设置相关
      String yAxis = null;
      String showMultiY = null;
      String metricsToY = null;
      String hideCalculateName = null;

      if (webParamMap != null) {
        loginUid = webParamMap.get(Constants.PT_LOGIN_USER_ID);
        loginUserEmail = webParamMap.get(Constants.PT_LOGIN_USER_EMAIL);
        logMessage.addOperateInfo("queryUid", loginUid);
        logMessage.addOperateInfo("queryEmail", loginUserEmail);
        noCache = Boolean.valueOf(webParamMap.get(Constants.PARAM_NO_CACHE));
      }
      
      // baseWidget设置相关
      if (widget != null && widget.getBaseWidget() != null) {
        PtoneWidgetInfo baseWidget = widget.getBaseWidget();
        graph = baseWidget.getGraphName();
        mapCode = baseWidget.getMapCode();
        isDemo = String.valueOf(baseWidget.getIsDemo());
        isExample = String.valueOf(baseWidget.getIsExample());
        datePeriod = baseWidget.getDatePeriod();

        logMessage.setUid(baseWidget.getCreatorId());
        logMessage.addOperateInfo("widgetOwnerId", baseWidget.getCreatorId());
        logMessage.addOperateInfo("widgetId", widget.getBaseWidget().getWidgetId());

        // 获取空间的周起始日信息
        PtoneSpaceInfo spaceInfo = serviceFactory.getPtoneDataService().getWidgetSpaceInfo(widget);
        if (spaceInfo != null) {
          weekStart = spaceInfo.getWeekStart();
        }
        int weekStartDay = PtoneSpaceInfo.getSpaceWeekStartSetting(weekStart);

        // 根据dateKey计算开始日期和结束日期： 请求的时间格式为 yyyy-MM-dd
        String dateKey = baseWidget.getDateKey();
        Map<String, String> dateMap =
            PtoneDateUtil.getInstance(weekStartDay).getStartEndDate(dateKey, "yyyy-MM-dd");
        startDate = dateMap.get(JodaDateUtil.START_DATE);
        endDate = dateMap.get(JodaDateUtil.END_DATE);
      } else {
        log.error(LOG_PREFIX + "widget<" + widgetId + "> is null");
        throw new ServiceException("widget<" + widgetId + "> is null");
      }

      // widget数据源设置相关
      if (widget != null && widget.getVariables() != null && widget.getVariables().get(0) != null) {
        PtVariables variable = widget.getVariables().get(0);
        dsCode = variable.getDsCode();
        account = variable.getAccountName();
        profileId = variable.getProfileId();

        // 如果不是模板数据、demo数据，判断widget取数关键信息是否完整，否则抛出异常(profileId、 metrics、dimensions)
        if ((widget.getBaseWidget().getIsExample() == null || Constants.validateInt != widget
            .getBaseWidget().getIsExample())
            && (widget.getBaseWidget().getIsDemo() == null || Constants.validateInt != widget
                .getBaseWidget().getIsDemo())) {
          if (StringUtil.isBlank(profileId)) {
            log.error(LOG_PREFIX + "widget<" + widgetId + "> profileId is null");
            throw new ServiceException("widget<" + widgetId + "> profileId is null");
          } else if (CollectionUtil.isEmpty(variable.getMetrics())
              && CollectionUtil.isEmpty(variable.getDimensions())) {
            log.error("widget<" + widgetId + "> metrics and dimensions is null");
            throw new ServiceException("widget<" + widgetId + "> metrics and dimensions is null");
          }
        }

        metrics = variable.getMetricsDataCoreInfoKey();
        dimensions = variable.getDimensionDataCoreInfoKey();
        dateDimensionId = variable.getDateDimensionId();
        filters = JSON.toJSONString(variable.getFilters());
        segment = JSON.toJSONString(variable.getSegment());
        sort = variable.getSort();

        connectionSourceInfoKey =
            serviceFactory.getPtoneDataService().getConnectionSourceInfoKey(dsCode,
                variable.getConnectionId(), account, profileId);
      }

      // chartSetting设置相关
      if (widget != null && widget.getChartSetting() != null) {
        PtoneWidgetChartSettingDto chartSetting = widget.getChartSetting();
        yAxis = chartSetting.getYAxisCoreInfoKey();
        showMultiY = String.valueOf(chartSetting.getShowMultiY());
        metricsToY = JSON.toJSONString(chartSetting.getMetricsToY());
        hideCalculateName = chartSetting.getHideCalculateName();
      }

      // 构建 cacheKeyMap
      Map<String, String> cacheKeyMap = new LinkedHashMap<String, String>();
      cacheKeyMap.put("widgetId", widgetId);
      cacheKeyMap.put("dsCode", dsCode);
      cacheKeyMap.put("account", account);
      cacheKeyMap.put("profileId", profileId);
      cacheKeyMap.put("metrics", metrics);
      cacheKeyMap.put("dimensions", dimensions);
      cacheKeyMap.put("dateDimensionId", dateDimensionId);
      cacheKeyMap.put("weekStart", weekStart);
      cacheKeyMap.put("startDate", startDate);
      cacheKeyMap.put("endDate", endDate);
      cacheKeyMap.put("datePeriod", datePeriod);
      cacheKeyMap.put("filters", filters);
      cacheKeyMap.put("segment", segment);
      cacheKeyMap.put("sort", sort);
      cacheKeyMap.put("startIndex", startIndex);
      cacheKeyMap.put("maxResults", maxResults);
      cacheKeyMap.put("connectionSourceInfoKey", connectionSourceInfoKey);
      cacheKeyMap.put("graph", graph);
      cacheKeyMap.put("mapCode", mapCode);
      cacheKeyMap.put("yAxis", yAxis);
      cacheKeyMap.put("showMultiY", showMultiY);
      cacheKeyMap.put("metricsToY", metricsToY);
      cacheKeyMap.put("hideCalculateName", hideCalculateName);
      cacheKeyMap.put("isDemo", isDemo == null ? Constants.inValidate : isDemo); // isDemo添加时没有设置默认值，返回时会补充默认值0
      cacheKeyMap.put("isExample", isExample);
      
      //设置时区信息
      setTimeZone(webParamMap, widget, cacheKeyMap);
      
      String widgetDataCacheKey =
          serviceFactory.getPtoneDataService().buildWidgetDataCacheKey(cacheKeyMap);
      boolean isHistoryData = serviceFactory.getPtoneDataService().isHistoryData(dsCode, endDate);
      boolean useCache =
          serviceFactory.getPtoneDataService().isUseWidgetDataCache(dsCode, isHistoryData);
      
//      if (!noCache && useCache && serviceFactory.getRedisService().existsKey(widgetDataCacheKey)) {
//        logMessage.addOperateInfo("widgetDataUseCache", true);
//        log.info(LOG_PREFIX + "exist WidgetData in cache , widgetId::" + widgetId + ", queryUid::"
//            + loginUid + ", queryUserEmail:" + loginUserEmail + ", key:: " + widgetDataCacheKey);
//        serviceFactory.getPtoneDataService().pushWidgetDataFromCache(widgetId, webParamMap, widget,
//            widgetDataCacheKey);
//      } else {
//        logMessage.addOperateInfo("widgetDataUseCache", false);
//        log.info(LOG_PREFIX + "not exist WidgetData in cache , widgetId::" + widgetId + ", queryUid::"
//            + loginUid + ", queryUserEmail:" + loginUserEmail + ", key:: " + widgetDataCacheKey);
//        // 根据dsCode获取对应的线程池
//        ThreadPoolExecutor executor = this.getThreadPoolExecutorByDsCode(dsCode);
//        // 创建取数任务WidgetDataTask
//        WidgetDataTask task = new WidgetDataTask(widgetId, webParamMap, widget, widgetDataCacheKey);
//        this.addWidgetDataTaskToThreadPool(executor, task, dsCode);
//      }
    } catch (Exception e) {
      String errorMsg =
          LOG_PREFIX + "create " + " WidgetDataTask<" + widgetId + "> error, queryUid::" + loginUid
              + ", queryUserEmail:" + loginUserEmail + e.getMessage();
      log.error(errorMsg, e);
      serviceFactory.getPtoneDataService().pushErrorWidgetData(widgetId, webParamMap, widget,
          errorMsg, null);
    }
    ElkLogUtil.info(logMessage.generateJsonString());
  }

  // ////////////////////////////////////////////////////


  /**
   * widget取数任务
   * 
   * @date: 2016年9月12日
   * @author peng.xu
   */
  class WidgetDataTask implements Runnable, Serializable {

    private static final long serialVersionUID = -2853856815712590673L;

    private String widgetId;
    private Map<String, String> webParamMap;
    private AcceptWidget widget;
    private String widgetDataCacheKey;

    public WidgetDataTask(String widgetId, Map<String, String> webParamMap, AcceptWidget widget,
        String widgetDataCacheKey) {
      this.widgetId = widgetId;
      this.webParamMap = webParamMap;
      this.widget = widget;
      this.widgetDataCacheKey = widgetDataCacheKey;
    }

    public void run() {
      log.info(LOG_PREFIX + "run WidgetDataTask<" + this.getWidgetId() + ">");
      serviceFactory.getPtoneDataService().getWidgetData(this.widgetId, this.webParamMap,
          this.widget, this.widgetDataCacheKey);
    }

    public String getWidgetId() {
      return widgetId;
    }

    public void setWidgetId(String widgetId) {
      this.widgetId = widgetId;
    }

    public Map<String, String> getWebParamMap() {
      return webParamMap;
    }

    public void setWebParamMap(Map<String, String> webParamMap) {
      this.webParamMap = webParamMap;
    }

    public AcceptWidget getWidget() {
      return widget;
    }

    public void setWidget(AcceptWidget widget) {
      this.widget = widget;
    }

  }

}
