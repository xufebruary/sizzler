package com.sizzler.service.ds;

import java.util.List;
import java.util.Map;

import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionDto;
import com.sizzler.domain.ds.vo.DimensionVo;

public interface PtoneDsService {

  public List<PtoneDsInfo> getAllDsInfoList();

  /**
   * 通过数据源code从缓存中找到dsInfo
   */
  public PtoneDsInfo getDsInfoByDsCode(String dsCode);

  /**
   * 新增复合指标、维度
   * @param userCompoundMetricsDimensionDto
   * @return
   * @date: 2016年7月14日
   * @author peng.xu
   */
  public UserCompoundMetricsDimensionDto addUserCompoundMetricsDimension(
      UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto);

  /**
   * 修改复合指标、维度
   * @param userCompoundMetricsDimensionDto
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
   * 校验复合指标是否有效
   * @param userCompoundMetricsDimensionDto
   * @return
   * @date: 2016年7月14日
   * @author peng.xu
   */
  public boolean validateCompoundMetrics(
      UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto, boolean isValidateFormula);

  /**
   * 根据选择指标列表和公式，构建复合指标取数相关属性
   * 
   * @param userCompoundMetricsDimensionDto
   * @return
   * @date: 2016年7月14日
   * @author peng.xu
   */
  public void buildCompoundMetrics(UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto,
      boolean isValidateFormula);

  /**
   * 获取当前使用复合指标的widget数量
   * @param id
   * @return
   * @date: 2016年7月15日
   * @author peng.xu
   */
  public long getUseCompoundMetricsWidgetCount(String id);


  /**
   * 获取复合指标：优先从复合指标map中获取，如果没有则从库中查询
   * 
   * @param metricsId
   * @param compoundMetricsMap 复合指标缓存map
   * @return
   * @date: 2016年7月23日
   * @author peng.xu
   */
  public UserCompoundMetricsDimensionDto getCompoundMetrics(String metricsId,
      Map<String, UserCompoundMetricsDimensionDto> compoundMetricsMap);

  /**
   * 得到高级服务和内测服务数据源列表
   * @return
   * @date: 2016年9月13日
   * @author li.zhang
   */
  public List<PtoneDsInfo> getServiceDatasource(String userSource);

  /**
   * 得到所有可见数据源
   * @return
   * @date: 2016年9月13日
   * @author li.zhang
   */
  public List<PtoneDsInfo> getIsShowDatasource();

  /**
   * 得到open数据源
   * @return
   * @date: 2016年9月22日
   * @author li.zhang
   */
  public List<PtoneDsInfo> getOpenDatasource();

  /**
   * 所有数据源获取维度列表接口(返回结构为树形结构的维度列表，需要展示几级返回几级)
   * @param dsId
   * @param connectionId
   * @param accountName
   * @param profileId
   * @return
   * @date: 2016年11月29日
   * @author peng.xu
   */
  public List<DimensionVo> getDimensionList(long dsId, String connectionId,
      String accountName, String profileId);
}
