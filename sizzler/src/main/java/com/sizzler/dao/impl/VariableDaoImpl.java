/**
 * Project Name:ptone-ui-backgroud File Name:VariableDaoImpl.java Package
 * Name:com.ptmind.ptone.rest.dao.impl Date:2015年4月20日下午4:45:03 Copyright (c) 2015,
 * peng.xu@ptthink.com All Rights Reserved.
 *
 */

package com.sizzler.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.ptmind.common.utils.StringUtil;
import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.dao.VariableDao;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.variable.dto.PtoneVariableWithWidgetId;
import com.sizzler.system.Constants;

@Repository("variableDao")
public class VariableDaoImpl extends DaoBaseInterfaceImpl<PtoneVariableInfo, String> implements
    VariableDao {

  @Override
  public List<PtoneVariableWithWidgetId> findVariableByPanelId(String pid) {
//    String sql =
//        "SELECT * FROM ptone_variable_info,"
//            + "        (SELECT Variable_ID,Widget_ID FROM ptone_widget_variable WHERE Widget_ID IN ("
//            + "        SELECT Widget_ID FROM ptone_panel_widget WHERE Panel_ID = :Panel_ID)) t WHERE t.Variable_ID = ptone_variable_info.Variable_ID ";
    String sql =
        "SELECT * FROM ptone_variable_info v, "
            + "     (SELECT wv.Variable_ID, wv.Widget_ID FROM ptone_widget_variable wv INNER JOIN ptone_panel_widget pw ON wv.Widget_ID = pw.Widget_ID "
            + "        WHERE pw.Panel_ID = :Panel_ID  and pw.is_delete = :is_delete AND wv.is_delete = :is_delete) t "
            + " WHERE v.Variable_ID = t.Variable_ID  and v.status = :status";
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("Panel_ID", pid);
    paramMap.put("is_delete", Constants.inValidate);
    paramMap.put("status", Constants.validate);
    return (List<PtoneVariableWithWidgetId>) DataOperationUtils.queryForList(sql, paramMap,
        PtoneVariableWithWidgetId.class);
  }

  @Override
  public List<PtoneVariableWithWidgetId> findVariableByWidgetId(List<String> widgetIdList) {
    List<PtoneVariableWithWidgetId> variableList = new ArrayList<PtoneVariableWithWidgetId>();
    if (widgetIdList != null && widgetIdList.size() > 0) {
      String sql =
          "SELECT * FROM ptone_variable_info v,"
              + "        (SELECT wv.Variable_ID,wv.Widget_ID FROM ptone_widget_variable wv WHERE wv.Widget_ID IN ('"
              + StringUtil.join(widgetIdList, "','")
              + "') AND wv.is_delete = :is_delete) t WHERE t.Variable_ID = v.Variable_ID and v.status = :status ";
      Map<String, String> paramMap = new HashMap<>();
      paramMap.put("is_delete", Constants.inValidate);
      paramMap.put("status", Constants.validate);
      variableList =
          (List<PtoneVariableWithWidgetId>) DataOperationUtils.queryForList(sql, paramMap,
              PtoneVariableWithWidgetId.class);
    }
    return variableList;
  }

}
