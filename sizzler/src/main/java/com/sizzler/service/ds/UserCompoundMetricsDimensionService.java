package com.sizzler.service.ds;

import java.util.List;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.ds.UserCompoundMetricsDimension;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionDto;

/**
 * 复合指标、维度
 * 
 * @date: 2016年7月13日
 * @author peng.xu
 */
public interface UserCompoundMetricsDimensionService extends
    ServiceBaseInterface<UserCompoundMetricsDimension, String> {

  /**
   * 新增复合指标、维度
   * @param userCompoundMetricsDimension
   * @return
   * @date: 2016年7月14日
   * @author peng.xu
   */
  public UserCompoundMetricsDimensionDto addUserCompoundMetricsDimension(
      UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto);

  /**
   * 修改复合指标、维度
   * @param userCompoundMetricsDimension
   * @return
   * @date: 2016年7月14日
   * @author peng.xu
   */
  public UserCompoundMetricsDimensionDto updateUserCompoundMetricsDimension(
      UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto);

  /**
   * 删除复合指标、维度
   * @param id
   * @param uid
   * @date: 2016年7月14日
   * @author peng.xu
   */
  public void deleteUserCompoundMetricsDimension(String id, String uid);

  /**
   * 获取空间下、某个数据源下的复合指标、维度列表
   * @param spaceId
   * @param dsId
   * @param typeArray: compoundMetrics || compoundDimension
   * @return
   * @date: 2016年7月14日
   * @author peng.xu
   */
  public List<UserCompoundMetricsDimensionDto> findUserCompoundMetricsDimensionList(String spaceId,
      String dsId, String tableId, String[] typeArray);

  /**
   * 获取使用复合指标的widget数量
   * @param id
   * @return
   * @date: 2016年7月15日
   * @author peng.xu
   */
  public long getUseCompoundMetricsWidgetCount(String id);

  /**
   * 根据ID获取复合指标、维度
   * @param userCompoundMetricsDimension
   * @return
   * @date: 2016年7月14日
   * @author peng.xu
   */
  public UserCompoundMetricsDimensionDto getUserCompoundMetricsDimension(String id);

  /**
   * 获取空间下某数据源下复合指标唯一名称
   * @param name
   * @param spaceId
   * @param dsId
   * @return
   * @date: 2016年7月23日
   * @author peng.xu
   */
  public String getUniqueCompoundMetricsName(String name, String spaceId, String dsId);

}
