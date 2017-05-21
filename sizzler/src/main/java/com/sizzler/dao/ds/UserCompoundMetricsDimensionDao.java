package com.sizzler.dao.ds;

import java.util.List;

import com.sizzler.common.base.dao.DaoBaseInterface;
import com.sizzler.domain.ds.UserCompoundMetricsDimension;

/**
 * 复合指标、维度
 */
public interface UserCompoundMetricsDimensionDao extends
    DaoBaseInterface<UserCompoundMetricsDimension, String> {

  /**
   * 获取使用复合指标的widget数量
   */
  public long getUseCompoundMetricsWidgetCount(String id);

  /**
   * 获取空间下某数据源下复合指标名称列表（以name为基准）
   */
  public List<String> getUserCompoundMetricsNameList(String name, String spaceId, String dsId);

}
