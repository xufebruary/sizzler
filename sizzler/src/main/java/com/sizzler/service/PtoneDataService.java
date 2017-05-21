package com.sizzler.service;

import java.util.Map;

import com.sizzler.common.restful.JsonView;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.widget.dto.AcceptWidget;

public interface PtoneDataService {

  /**
   * widget取数
   * @date: 2016年9月12日
   * @author peng.xu
   */
  public JsonView getWidgetData(String widgetId, Map<String, String> webParamMap,
      AcceptWidget widget, String widgetDataCacheKey);

  /**
   * 推送widgetData取数失败异常数据到websocket
   * @date: 2016年9月12日
   * @author peng.xu
   */
  public void pushErrorWidgetData(String widgetId, Map<String, String> webParamMap,
      AcceptWidget widget, String errorMsg, String errorCode);

  /**
   * 构建WidgetData缓存的key
   * @return
   * @date: 2016年9月13日
   * @author peng.xu
   */
  public String buildWidgetDataCacheKey(Map<String, String> cacheKeyMap);

  /**
   * 是否为WidgetData历史数据
   * @return
   * @date: 2016年9月13日
   * @author peng.xu
   */
  public boolean isHistoryData(String dsCode, String endDate);

  /**
   * 是否是用widgetData缓存
   * @return
   * @date: 2016年9月13日
   * @author peng.xu
   */
  public boolean isUseWidgetDataCache(String dsCode, boolean isHistoryData);
  
  /**
   * widgetData缓存时间
   * @return
   * @date: 2016年9月13日
   * @author peng.xu
   */
  public int getWidgetDataCacheTime(String dsCode, boolean isHistoryData);

  /**
   * 从缓存获取widgetData数据，并推送
   * @date: 2016年9月13日
   * @author peng.xu
   */
  public void pushWidgetDataFromCache(String widgetId, Map<String, String> webParamMap,
      AcceptWidget widget, String widgetDataCacheKey);

  /**
   * 获取影响widgetData数据的userConnection、connectionSource关键信息key
   * @return
   * @date: 2016年9月13日
   * @author peng.xu
   */
  public String getConnectionSourceInfoKey(String dsCode, String connectionId, String account,
      String profileId);
  
  
  /**
   * 根据AcceptWidget获取对应的spaceInfo
   * @param widget
   * @return
   * @date: 2016年9月23日
   * @author peng.xu
   */
  public PtoneSpaceInfo getWidgetSpaceInfo(AcceptWidget widget);

}
