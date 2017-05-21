package com.sizzler.service.ds.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.common.utils.JodaDateUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.dao.ds.UserCompoundMetricsDimensionDao;
import com.sizzler.domain.ds.UserCompoundMetricsDimension;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionDto;
import com.sizzler.service.ds.PtoneDsService;
import com.sizzler.service.ds.UserCompoundMetricsDimensionService;
import com.sizzler.system.Constants;

@Service
public class UserCompoundMetricsDimensionServiceImpl extends
    ServiceBaseInterfaceImpl<UserCompoundMetricsDimension, String> implements
    UserCompoundMetricsDimensionService {

  @Autowired
  private UserCompoundMetricsDimensionDao userCompoundMetricsDimensionDao;

  @Autowired
  private PtoneDsService ptoneDsService;

  /**
   * 保存复合指标、维度前的处理： 主要是解析表达式，生成后台查询用的字段、校验指标是否有效
   * 
   * @param userCompoundMetricsDimension
   * @date: 2016年7月14日
   * @author peng.xu
   */
  public void dealUserCompoundMetricsDimensionBeforeSave(
      UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto) {
    if (PtoneMetricsDimension.TYPE_COMPOUND_METRICS
        .equalsIgnoreCase(userCompoundMetricsDimensionDto.getType())) {
      // 构建复合指标取数相关属性
      ptoneDsService.buildCompoundMetrics(userCompoundMetricsDimensionDto, true);
    } else if (PtoneMetricsDimension.TYPE_COMPOUND_DIMENSION
        .equalsIgnoreCase(userCompoundMetricsDimensionDto.getType())) {
      // 解析复合维度
    }
  }

  @Override
  public UserCompoundMetricsDimensionDto addUserCompoundMetricsDimension(
      UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto) {
    String currTime = JodaDateUtil.getCurrentDateTime();
    userCompoundMetricsDimensionDto.setCreateTime(currTime);
    userCompoundMetricsDimensionDto.setModifyTime(currTime);
    userCompoundMetricsDimensionDto.setIsDelete(Constants.inValidateInt);
    userCompoundMetricsDimensionDto.setDataType(PtoneMetricsDimension.DATA_TYPE_NUMBER); // 默认设置数据类型为Number

    this.dealUserCompoundMetricsDimensionBeforeSave(userCompoundMetricsDimensionDto);

    UserCompoundMetricsDimension userCompoundMetricsDimension =
        userCompoundMetricsDimensionDto.parseToUserCompoundMetricsDimension();
    userCompoundMetricsDimensionDao.save(userCompoundMetricsDimension);
    return userCompoundMetricsDimensionDto;
  }

  @Override
  public UserCompoundMetricsDimensionDto updateUserCompoundMetricsDimension(
      UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto) {
    userCompoundMetricsDimensionDto.setModifyTime(JodaDateUtil.getCurrentDateTime());
    this.dealUserCompoundMetricsDimensionBeforeSave(userCompoundMetricsDimensionDto);

    UserCompoundMetricsDimension userCompoundMetricsDimension =
        userCompoundMetricsDimensionDto.parseToUserCompoundMetricsDimension();
    userCompoundMetricsDimensionDao.update(userCompoundMetricsDimension);

    // 返回最新的复合指标
    UserCompoundMetricsDimensionDto newCompoundMetrics =
        this.getUserCompoundMetricsDimension(userCompoundMetricsDimensionDto.getId());
    ptoneDsService.buildCompoundMetrics(newCompoundMetrics, true);
    return newCompoundMetrics;
  }

  @Override
  public void deleteUserCompoundMetricsDimension(String id, String uid) {
    UserCompoundMetricsDimension userCompoundMetricsDimension = new UserCompoundMetricsDimension();
    userCompoundMetricsDimension.setId(id);
    userCompoundMetricsDimension.setModifierId(uid);
    userCompoundMetricsDimension.setModifyTime(JodaDateUtil.getCurrentDateTime());
    userCompoundMetricsDimension.setIsDelete(Constants.validateInt);
    userCompoundMetricsDimensionDao.update(userCompoundMetricsDimension);
  }

  @Override
  public List<UserCompoundMetricsDimensionDto> findUserCompoundMetricsDimensionList(String spaceId,
      String dsId, String tableId, String[] typeArray) {
    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("spaceId", new Object[] {spaceId});
    paramMap.put("dsId", new Object[] {dsId});
    paramMap.put("type", typeArray);
    paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
    if (StringUtil.isNotBlank(tableId) && !Constants.STR_ALL.equalsIgnoreCase(tableId)) {
      paramMap.put("tableId", new Object[] {tableId});
    }
    List<UserCompoundMetricsDimension> list = this.findByWhere(paramMap);
    List<UserCompoundMetricsDimensionDto> dtoList =
        new ArrayList<UserCompoundMetricsDimensionDto>();
    if (list != null && list.size() > 0) {
      for (UserCompoundMetricsDimension compoundMetrics : list) {
        if (compoundMetrics != null) {
          UserCompoundMetricsDimensionDto compoundMetricsDto =
              new UserCompoundMetricsDimensionDto(compoundMetrics);
          ptoneDsService.buildCompoundMetrics(compoundMetricsDto, false);
          dtoList.add(compoundMetricsDto);
        }
      }
    }
    return dtoList;
  }

  @Override
  public long getUseCompoundMetricsWidgetCount(String id) {
    return userCompoundMetricsDimensionDao.getUseCompoundMetricsWidgetCount(id);
  }

  @Override
  public UserCompoundMetricsDimensionDto getUserCompoundMetricsDimension(String id) {
    UserCompoundMetricsDimension userCompoundMetricsDimension = this.get(id);
    if (userCompoundMetricsDimension != null) {
      return new UserCompoundMetricsDimensionDto(userCompoundMetricsDimension);
    }
    return null;
  }

  @Override
  public String getUniqueCompoundMetricsName(String name, String spaceId, String dsId) {
    List<String> nameList =
        userCompoundMetricsDimensionDao.getUserCompoundMetricsNameList(name, spaceId, dsId);

    List<String> ignoreCaseNameList = new ArrayList<String>(); // 全部转小写的列表
    if (nameList != null) {
      for (String n : nameList) {
        ignoreCaseNameList.add(n.toLowerCase());
      }
    }

    int index = 1;
    String uniqueName = name;
    while (ignoreCaseNameList.contains(uniqueName.toLowerCase())) {
      uniqueName = name + "(" + index + ")";
      index++;
    }
    return uniqueName;
  }

}
