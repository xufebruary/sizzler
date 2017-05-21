/**
 * Project Name:ptone-ui-backgroud File Name:VariableDataServiceImpl.java Package
 * Name:com.ptmind.ptone.rest.service.impl Date:2015年4月20日下午12:39:02 Copyright (c) 2015,
 * peng.xu@ptthink.com All Rights Reserved.
 *
 */

package com.sizzler.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.cache.CurrentUserCache;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.widget.GaWidgetInfo;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.proxy.dispatcher.PtoneDatasourceDesc;
import com.sizzler.proxy.dispatcher.PtoneDispatcher;
import com.sizzler.proxy.dispatcher.PtoneVariableData;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;
import com.sizzler.service.PtoneUserConnectionService;
import com.sizzler.service.VariableDataService;
import com.sizzler.system.Constants;

/**
 * 获取variable数据服务类 <br/>
 */
@Service("variableDataService")
public class VariableDataServiceImpl implements VariableDataService {

  @Autowired
  private PtoneUserConnectionService ptoneUserConnectionService;

  @Override
  /**
   * 从数据源中获取数据，根据数据源类型获取对应数据源的数据
   */
  public List<PtoneVariableData> getVariableData(String dsType, PtoneWidgetInfo ptoneWidgetInfo,
      PtoneVariableInfo ptoneVariableInfo, String graphType, PtoneWidgetParam ptoneWidgetParam,
      Map<String, String> webParamMap, CurrentUserCache currentUserCache) {

    GaWidgetInfo gaWidgetInfo = new GaWidgetInfo();
    gaWidgetInfo.setVariableId(ptoneVariableInfo.getVariableId());
    gaWidgetInfo.setWidgetId(ptoneWidgetInfo.getWidgetId());
    gaWidgetInfo.setAccountId("");
    gaWidgetInfo.setPropertyId("");
    gaWidgetInfo.setProfileId(webParamMap.get(Constants.PARAM_PROFILE));
    gaWidgetInfo.setMetricsId(webParamMap.get(Constants.PARAM_METRICS_ID));
    gaWidgetInfo.setDimensionsId(webParamMap.get(Constants.PARAM_DIMENSIONS_ID));
    gaWidgetInfo.setSort(ptoneWidgetParam.getSort());
    gaWidgetInfo.setFilters("");
    gaWidgetInfo.setMaxResult("");
    gaWidgetInfo.setAccountName(webParamMap.get(Constants.PARAM_ACCOUNT_NAME));
    gaWidgetInfo.setConnectionId(ptoneWidgetParam.getConnectionId());
    gaWidgetInfo.setDsId(ptoneWidgetParam.getDsId());

    // 获取当前用户，查询出GoogleAPI对应的refreshToken
    PtoneUser loginPtoneUser = currentUserCache.getCurrentUser();
    String uid = loginPtoneUser.getPtId();
    String spaceId = ptoneWidgetParam.getSpaceId();
    long dsId = ptoneWidgetParam.getDsId();
    String dsCode = ptoneWidgetParam.getDsCode();
    
    // 注意：GA存在历史数据connectionId与email不匹配问题，此处不能使用connectionId作为判断
    // 对与API类型数据源：为实现解绑账号后重新授权继续使用，此处使用accountName而不是connectionId
    boolean useConnectionId = !DsConstants.isApiDs(dsCode);// 非account类型的数据查询使用connectionId判断，如：gd、mysql等
    String accountName = gaWidgetInfo.getAccountName();
    UserConnection userConnection =
        ptoneUserConnectionService.getSpaceUserConnection(spaceId, dsId,
            ptoneWidgetParam.getConnectionId(), accountName, useConnectionId);

    // 根据数据源类型获取对应数据源的数据
    PtoneDatasourceDesc ptoneDatasourceDesc = new PtoneDatasourceDesc(dsType);
    ptoneDatasourceDesc.setPtoneWidgetInfo(ptoneWidgetInfo);
    ptoneDatasourceDesc.setPtoneVariableInfo(ptoneVariableInfo);
    ptoneDatasourceDesc.setGraphType(graphType);
    ptoneDatasourceDesc.setPtoneWidgetParam(ptoneWidgetParam);
    ptoneDatasourceDesc.setWebParamMap(webParamMap);
    ptoneDatasourceDesc.setCurrentUserCache(currentUserCache);
    ptoneDatasourceDesc.setGaWidgetInfo(gaWidgetInfo);
    ptoneDatasourceDesc.setUserConnection(userConnection);

    List<PtoneVariableData> ptoneVariableDataList =
        PtoneDispatcher.getInstance().dispatch(ptoneDatasourceDesc);
    return ptoneVariableDataList;
  }

}
