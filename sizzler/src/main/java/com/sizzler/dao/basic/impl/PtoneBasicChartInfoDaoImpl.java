package com.sizzler.dao.basic.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.dao.basic.PtoneBasicChartInfoDao;
import com.sizzler.domain.basic.PtoneBasicChartInfo;

@Repository("ptoneBasicChartInfoDao")
public class PtoneBasicChartInfoDaoImpl extends DaoBaseInterfaceImpl<PtoneBasicChartInfo, Long>
    implements PtoneBasicChartInfoDao {

  @Override
  public List<Map<String, Object>> findChartInfoMap() {
    String sql = "select * from ptone_basic_chart_info where is_delete = 0 order by order_number";
    return DataOperationUtils.queryForMap(sql);
  }
}
