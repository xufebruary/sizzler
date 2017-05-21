package com.sizzler.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ptmind.common.utils.StringUtil;
import com.sizzler.cache.DataCacheService;
import com.sizzler.cache.PtoneDsInfoCache;
import com.sizzler.cache.SysConfigParamCache;
import com.sizzler.common.Constants.JsonViewConstants;
import com.sizzler.common.exception.ErrorCode;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.log.ElkLogInfo;
import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.log.LogMessageUtil;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.JodaDateUtil;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.widget.dto.AcceptWidget;
import com.sizzler.proxy.common.CommonDataUtil;
import com.sizzler.proxy.dispatcher.PtoneWidgetData;
import com.sizzler.service.DataSourceManagerService;
import com.sizzler.service.PtoneDataService;
import com.sizzler.service.PtoneUserConnectionService;
import com.sizzler.service.UserService;
import com.sizzler.service.WidgetDataService;
import com.sizzler.system.Constants;

@Service("ptoneDataService")
public class PtoneDataServiceImpl implements PtoneDataService {

  private Logger logger = LoggerFactory.getLogger(PtoneDataServiceImpl.class);

  private static String LOG_PREFIX = "[PtoneDataService] ";

  @Autowired
  private WidgetDataService widgetDataService;

  @Autowired
  private CommonDataUtil commonDataUtil;

  @Autowired
  private DataCacheService dataCacheService;

  @Autowired
  private SysConfigParamCache sysConfigParamCache;

  @Autowired
  private PtoneDsInfoCache ptoneDsInfoCache;

  @Autowired
  private PtoneUserConnectionService ptoneUserConnectionService;

  @Autowired
  private DataSourceManagerService dataSourceManagerService;

  @Autowired
  private UserService userService;

  /**
   * widget取数
   * @date: 2016年9月12日
   * @author peng.xu
   */
  @Override
  public JsonView getWidgetData(String widgetId, Map<String, String> webParamMap,
      AcceptWidget widget, String widgetDataCacheKey) {

    JsonView jsonView = JsonViewFactory.createJsonView();
    long start = System.currentTimeMillis();
    String queryStatus = "";
    String queryMsg = "UseDsDataCache";
    boolean isCacheData = false;
    //增加取数的日志
    LogMessage logMessage = new LogMessage();
    try {
      logMessage.setOperate("getWidgetData");
      if (StringUtil.isBlank(widgetId) || widget != null && widget.getBaseWidget() != null
          && StringUtil.isBlank(widget.getBaseWidget().getWidgetId())) {
        logger.error(LOG_PREFIX + "get widget data error, widgetId is null !");
      }else{
        logMessage.addOperateInfo("widgetId", widgetId);
        logMessage.setUid(widget.getBaseWidget().getCreatorId());
      }

      // 返回ptone原始数据结构的json数据
      PtoneWidgetData ptoneWidgetData =
          widgetDataService.getPtoneWidgetData(widgetId, webParamMap, widget);
      if (ptoneWidgetData == null || ptoneWidgetData.getData() == null
          || ptoneWidgetData.getData().size() == 0) {
        if (ptoneWidgetData == null) {
          ptoneWidgetData = new PtoneWidgetData();
        }
        ptoneWidgetData.setWidgetId(widget.getBaseWidget().getWidgetId());
        ptoneWidgetData.setStatus("noData");
        ptoneWidgetData.setErrorCode(ErrorCode.CODE_NO_DATA);
        ptoneWidgetData.setErrorMsg(ErrorCode.MSG_NO_DATA);
      }
      queryStatus = ElkLogInfo.STATUS_SUCCESS;
      // isCacheData = ptoneWidgetData.getIsCacheData();
      jsonView.successPack(JSON.toJSON(ptoneWidgetData)); // JsonView设置对象在前端反序列化时，需要有对应实体类，所以此处直接转为json串

      // 缓存widgetData
      if (StringUtil.isNotBlank(widgetDataCacheKey)) {
        String dsCode = ptoneWidgetData.getDsCode();
        String endDate = ptoneWidgetData.getEndDate();
        boolean isHistoryData = this.isHistoryData(dsCode, endDate);
        boolean useCache = this.isUseWidgetDataCache(dsCode, isHistoryData);
        if (useCache) {
          ptoneWidgetData.setIsCacheData(true);
          int cacheTime = this.getWidgetDataCacheTime(dsCode, isHistoryData);
          dataCacheService.cacheData(widgetDataCacheKey, ptoneWidgetData, cacheTime);
        }
      }

    } catch (ServiceException e) {
      PtoneWidgetData ptoneWidgetData = new PtoneWidgetData();
      ptoneWidgetData.setWidgetId(widget.getBaseWidget().getWidgetId());
      ptoneWidgetData.setStatus("failed");
      ptoneWidgetData.setErrorCode(e.getErrorCode());
      ptoneWidgetData.setErrorMsg(e.getErrorMsg());
      ptoneWidgetData.setErrorLogs(e.getMessage());
      jsonView.successPack(JSON.toJSON(ptoneWidgetData));
      logger.error(LOG_PREFIX + "get WidgetData<" + widgetId + "> error, widgetId = " + widgetId
          + " :", e);
      queryMsg = e.getMessage();
      LogMessageUtil.addErrorExceptionMessage(logMessage, queryMsg);
    } catch (Exception e) {
      PtoneWidgetData ptoneWidgetData = new PtoneWidgetData();
      ptoneWidgetData.setWidgetId(widget.getBaseWidget().getWidgetId());
      ptoneWidgetData.setStatus("failed");
      ptoneWidgetData.setErrorCode(ErrorCode.CODE_FAILED);
      ptoneWidgetData.setErrorMsg(ErrorCode.MSG_FAILED);
      ptoneWidgetData.setErrorLogs(e.getMessage());
      jsonView.successPack(JSON.toJSON(ptoneWidgetData));
      logger.error(LOG_PREFIX + "get WidgetData<" + widgetId + "> error, widgetId = " + widgetId
          + " :", e);
      queryMsg = e.getMessage();
      LogMessageUtil.addErrorExceptionMessage(logMessage, queryMsg);
    }finally{
      logger.info(logMessage.toString());
    }


    this.pushWidgetDataToSocket(jsonView, widgetId, webParamMap, widget);

    long end = System.currentTimeMillis();
    logger.info(LOG_PREFIX + "PtoneDataServiceImpl::get WidgetData<" + widgetId
        + "> to Websocket -->" + (end - start));

    this.printWidgetDataElkLog(widget, webParamMap, start, end, queryStatus, queryMsg, isCacheData);

    return jsonView;
  }

  /**
   * 打印widgetData取数Elk日志
   * @date: 2016年9月14日
   * @author peng.xu
   */
  public void printWidgetDataElkLog(AcceptWidget widget, Map<String, String> webParamMap,
      Long queryStart, Long queryEnd, String queryStatus, String queryMsg, Boolean isCacheData) {
    try {
      if (widget != null && widget.getBaseWidget() != null
          && (widget.get_ext() == null || widget.get_ext().get(Constants.PT_USERNAME) == null)) {
        String uid = widget.getBaseWidget().getCreatorId();
        PtoneUser user = userService.getPtoneUser(uid);
        if (user != null) {
          if (widget.get_ext() == null) {
            widget.set_ext(new HashMap<String, Object>());
          }
          widget.get_ext().put(Constants.PT_USERNAME, user.getUserEmail());
        }
      }
    } catch (Exception e) {
      logger.error(LOG_PREFIX + "printWidgetDataElkLog error: " + e.getMessage(), e);
    }
  }

  public void pushWidgetDataToSocket(JsonView jsonView, String widgetId,
      Map<String, String> webParamMap, AcceptWidget widget) {
    try {
      jsonView.setDataVersion(webParamMap.get(JsonViewConstants.PARAM_DATA_VERSION));
      String widgetDataSign = webParamMap.get("sign");
      if (StringUtil.isNotBlank(widgetDataSign)) {
//        dynamicDubboConsumer.tellMessage(widgetDataSign, jsonView);
      } else {
        logger.warn(LOG_PREFIX + "push widget<" + widgetId
            + "> Data to socket error, not find socket sign.");
      }
    } catch (Exception e) {
      logger.error(LOG_PREFIX + "push widget<" + widgetId + "> Data to socket error", e);
    }
  }

  @Override
  public void pushErrorWidgetData(String widgetId, Map<String, String> webParamMap,
      AcceptWidget widget, String errorMsg, String errorCode) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    PtoneWidgetData ptoneWidgetData = new PtoneWidgetData();
    ptoneWidgetData.setWidgetId(widget.getBaseWidget().getWidgetId());
    ptoneWidgetData.setStatus("failed");
    ptoneWidgetData.setErrorCode(ErrorCode.CODE_FAILED);
    ptoneWidgetData.setErrorMsg(ErrorCode.MSG_FAILED);
    ptoneWidgetData.setErrorLogs(errorMsg);
    jsonView.successPack(JSON.toJSON(ptoneWidgetData));
    logger.error(LOG_PREFIX + "get widget data error: " + errorMsg);
    this.pushWidgetDataToSocket(jsonView, widgetId, webParamMap, widget);

    this.printWidgetDataElkLog(widget, webParamMap, null, null, ElkLogInfo.STATUS_ERROR, errorMsg,
        null);
  }

  @Override
  public void pushWidgetDataFromCache(String widgetId, Map<String, String> webParamMap,
      AcceptWidget widget, String widgetDataCacheKey) {
    long start = System.currentTimeMillis();
    String dataJson = dataCacheService.getDataFromCache(widgetDataCacheKey);
    PtoneWidgetData ptoneWidgetData = new PtoneWidgetData();
    if (StringUtil.isNotBlank(dataJson)) {
      ptoneWidgetData = JSONObject.parseObject(dataJson, PtoneWidgetData.class);
    }
    JsonView jsonView = JsonViewFactory.createJsonView();
    ptoneWidgetData.setWidgetId(widget.getBaseWidget().getWidgetId());
    jsonView.successPack(JSON.toJSON(ptoneWidgetData));
    this.pushWidgetDataToSocket(jsonView, widgetId, webParamMap, widget);
    long end = System.currentTimeMillis();
    logger.info(LOG_PREFIX + "get PtoneWidgetData<" + widgetId + "> from cache<"
        + widgetDataCacheKey + "> -->" + (end - start));

    this.printWidgetDataElkLog(widget, webParamMap, start, end, ElkLogInfo.STATUS_SUCCESS,
        "UseWidgetDataCache", true);
  }

  @Override
  public String buildWidgetDataCacheKey(Map<String, String> cacheKeyMap) {
    return dataCacheService.buildCacheKey(DataCacheService.KEY_PREFIX_PTONE_WIDGET_DATA,
        cacheKeyMap);
  }

  @Override
  public boolean isHistoryData(String dsCode, String endDate) {
    return commonDataUtil.isHistoryData(dsCode, endDate);
  }

  @Override
  public boolean isUseWidgetDataCache(String dsCode, boolean isHistoryData) {
    if (isHistoryData) {
      return sysConfigParamCache.isUseWidgetHistoryDataCache(dsCode);
    } else {
      return sysConfigParamCache.isUseWidgetRealtimeDataCache(dsCode);
    }
  }

  @Override
  public int getWidgetDataCacheTime(String dsCode, boolean isHistoryData) {
    if (isHistoryData) {
      return sysConfigParamCache.getWidgetHistoryDataCacheTime(dsCode);
    } else {
      return sysConfigParamCache.getWidgetRealtimeDataCacheTime(dsCode);
    }
  }

  @Override
  public String getConnectionSourceInfoKey(String dsCode, String connectionId, String account,
      String profileId) {
    PtoneDsInfo dsInfo = ptoneDsInfoCache.getPtoneDsInfoByCode(dsCode);
    String connectionSourceInfoKey = null;
    if (dsInfo != null && DsConstants.DS_TYPE_MODEL.equalsIgnoreCase(dsInfo.getType())) {
      if (DataBaseConfig.isDatabase(dsCode) || DsConstants.DS_CODE_BIGQUERY.equals(dsCode)) {
        UserConnection userConnection = ptoneUserConnectionService.get(connectionId);
        UserConnectionSource source =
            dataSourceManagerService.getUserConnectionSourceByTableId(profileId);
        // build db connection key info
        if (userConnection != null && source != null) {
          String connectionInfoKey = commonDataUtil.buildDbConnectionKey(userConnection);
          connectionSourceInfoKey = source.getUpdateTime() + "|" + connectionInfoKey;
        } else {
          connectionSourceInfoKey = connectionId + "|" + profileId;
        }
      } else {
        UserConnectionSource source =
            dataSourceManagerService.getUserConnectionSourceByTableId(profileId);
        if (source != null) {
          connectionSourceInfoKey =
              profileId + "|" + source.getUpdateTime() + "|" + source.getLastModifiedDate();
        } else {
          connectionSourceInfoKey = connectionId + "|" + profileId;
        }
      }
    }
    return connectionSourceInfoKey;
  }

  @Override
  public PtoneSpaceInfo getWidgetSpaceInfo(AcceptWidget widget) {
    return widgetDataService.getWidgetSpaceInfo(widget);
  }
}
