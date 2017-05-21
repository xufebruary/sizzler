package com.sizzler.dao.ds.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.dao.ds.UserCompoundMetricsDimensionDao;
import com.sizzler.domain.ds.UserCompoundMetricsDimension;
import com.sizzler.system.Constants;

@Repository
public class UserCompoundMetricsDimensionDaoImpl extends
    DaoBaseInterfaceImpl<UserCompoundMetricsDimension, String> implements
    UserCompoundMetricsDimensionDao {

  @Override
  public UserCompoundMetricsDimension get(String id) {
    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("id", new Object[] {id});
    paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
    return getByWhere(paramMap);
  }

  @Override
  public long getUseCompoundMetricsWidgetCount(String id) {
    long count = 0;
    if (StringUtil.isNotBlank(id)) {
      StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) AS count ");
      sqlBuilder.append(" FROM ga_widget_info t1 ");
      sqlBuilder.append(" INNER JOIN ptone_widget_info t2 ON t1.widget_id = t2.widget_id ");
      sqlBuilder.append(" INNER JOIN ptone_panel_info t3 ON t2.panel_id = t3.panel_id ");
      sqlBuilder.append(" WHERE  t2.status = '").append(Constants.validate).append("' ");
      sqlBuilder.append("   AND t3.status = '").append(Constants.validate).append("' ");
      sqlBuilder.append("   AND ( t1.metrics like '%").append(id).append("%' ");
      sqlBuilder.append("     OR t1.filters like '%").append(id).append("%' )");
      Object param[] = {};
      count = DataOperationUtils.queryForObject(sqlBuilder.toString(), param, Long.class);
    }
    return count;
  }

  @Override
  public List<String> getUserCompoundMetricsNameList(String name, String spaceId, String dsId) {
    // 使用sql正则查询出 名称为 name 及 name + (n) 的名称列表
    String nameRegexp = "'^" + StringUtil.escapeRegexp(name) + "(\\\\([0-9]\\\\))?$'";
    String sql =
        "SELECT name FROM user_compound_metrics_dimension "
            + " WHERE space_id = ? AND ds_id = ? AND is_delete = ? " + " AND name REGEXP "
            + nameRegexp; // 不区分大小写
    // + " AND name REGEXP BINARY " + nameRegexp; // 区分大小写
    List<Map<String, Object>> resultList =
        DataOperationUtils.queryForMap(sql, spaceId, dsId, Constants.inValidateInt);
    List<String> nameList = new ArrayList<String>();
    if (resultList != null && !resultList.isEmpty()) {
      for (Map<String, Object> map : resultList) {
        nameList.add(String.valueOf(map.get("name")));
      }
    }
    return nameList;
  }

}
