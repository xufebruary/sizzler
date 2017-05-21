/**
 * Project Name:ptone-ui-backgroud File Name:GaWidgetDaoImpl.java Package
 * Name:com.ptmind.ptone.rest.dao.impl Date:2015年4月20日下午4:41:12 Copyright (c) 2015,
 * peng.xu@ptthink.com All Rights Reserved.
 *
 */

package com.sizzler.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.ptmind.common.utils.StringUtil;
import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.dao.GaWidgetDao;
import com.sizzler.domain.widget.GaWidgetInfo;
import com.sizzler.system.Constants;

@Repository("gaWidgetDao")
public class GaWidgetDaoImpl extends DaoBaseInterfaceImpl<GaWidgetInfo, String> implements
    GaWidgetDao {

  @Override
  public GaWidgetInfo get(String widgetId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("widgetId", new Object[] {widgetId});
    paramMap.put("status", new Object[] {Constants.validate});
    return this.getByWhere(paramMap);
  }

  @Override
  public List<GaWidgetInfo> findGaWidgetInfo(String pid) {
//    String sql =
//        "SELECT * FROM ga_widget_info WHERE Widget_ID IN (SELECT Widget_ID FROM ptone_panel_widget WHERE Panel_ID = :Panel_ID)";
    String sql =
        "SELECT w.* FROM ga_widget_info w INNER JOIN ptone_panel_widget pw ON w.Widget_ID = pw.Widget_ID "
            + " WHERE pw.Panel_ID = :Panel_ID and w.status = :status and pw.is_delete = :is_delete" ;
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("Panel_ID", pid);
    paramMap.put("status", Constants.validate);
    paramMap.put("is_delete", Constants.inValidate);
    return (List<GaWidgetInfo>) DataOperationUtils.queryForList(sql, paramMap, GaWidgetInfo.class);
  }

  @Override
  public List<GaWidgetInfo> findGaWidgetInfoByWidgetId(List<String> widgetIdList) {
    String sql =
        "SELECT * FROM ga_widget_info w WHERE w.Widget_ID IN ('" + StringUtil.join(widgetIdList, "','")
            + "') and w.status = :status";
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("status", Constants.validate);
    return (List<GaWidgetInfo>) DataOperationUtils.queryForList(sql, paramMap, GaWidgetInfo.class);
  }

  @Override
  public long queryWidgetCountOfAccount(String connectionId) {
    long count = 0;
    if (StringUtil.isNotBlank(connectionId)) {
      String sql =
          "select count(*) as count from ga_widget_info t1 inner JOIN ptone_widget_info t2 on t1.Widget_ID = t2.Widget_ID "
              + " inner JOIN ptone_panel_info t3 on t2.Panel_ID = t3.Panel_ID "
              + " where t1.connection_id = ? and t1.status = ? and t2.Status = ? and t3.Status = ? ";
      Object param[] = {connectionId,Constants.validate,Constants.validate,Constants.validate};
      count = DataOperationUtils.queryForObject(sql, param, Long.class);
    }
    return count;
  }

  @Override
  public long queryWidgetCountOfSource(List<String> tableIdList) {
    long count = 0;
    if (tableIdList != null && tableIdList.size() > 0) {
      String sql =
          "select count(*) as count from ga_widget_info t1 inner JOIN ptone_widget_info t2 on t1.Widget_ID = t2.Widget_ID "
              + " inner JOIN ptone_panel_info t3 on t2.Panel_ID = t3.Panel_ID "
              + "where t1.profile_id in ('"
              + StringUtil.join(tableIdList, "','")
              + "') and t1.status = ? and t2.Status = ? and t3.Status = ? ";
      Object param[] = {Constants.validate,Constants.validate,Constants.validate};
      count = DataOperationUtils.queryForObject(sql, param, Long.class);
    }
    return count;
  }

}
