package com.sizzler.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.WidgetExtendDao;
import com.sizzler.domain.widget.PtoneWidgetInfoExtend;

@Repository("widgetExtendDao")
public class WidgetExtendDaoImpl extends DaoBaseInterfaceImpl<PtoneWidgetInfoExtend, String>
    implements WidgetExtendDao {

  @Override
  public PtoneWidgetInfoExtend get(String id) {
    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("widgetId", new Object[] {id});
    return getByWhere(paramMap);
  }

}
