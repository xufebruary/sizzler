/**
 * Project Name:ptone-ui-backgroud File Name:VariableDataService.java Package
 * Name:com.ptmind.ptone.rest.service Date:2015年4月20日下午12:38:33 Copyright (c) 2015,
 * peng.xu@ptthink.com All Rights Reserved.
 *
 */

package com.sizzler.service;

import java.util.List;
import java.util.Map;

import com.sizzler.cache.CurrentUserCache;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.proxy.dispatcher.PtoneVariableData;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;

/**
 * ClassName:VariableDataService <br/>
 * Date: 2015年4月20日 下午12:38:33 <br/>
 * 
 * @author peng.xu
 * @version
 * @since JDK 1.6
 * @see
 */
public interface VariableDataService {

  public List<PtoneVariableData> getVariableData(String dsName, PtoneWidgetInfo ptoneWidgetInfo,
      PtoneVariableInfo ptoneVariableInfo, String graphType, PtoneWidgetParam ptoneWidgetParam,
      Map<String, String> webParamMap, CurrentUserCache currentUserCache);

}
