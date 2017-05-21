package com.sizzler.domain.ds.dto;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.ds.UserCompoundMetricsDimension;

public class UserCompoundMetricsDimensionDto extends UserCompoundMetricsDimension {

  private static final long serialVersionUID = 3529834075205270234L;

  private List<UserCompoundMetricsDimensionObject> objects;

  public List<UserCompoundMetricsDimensionObject> getObjects() {
    return objects;
  }

  public void setObjects(List<UserCompoundMetricsDimensionObject> objects) {
    this.objects = objects;
  }

  public UserCompoundMetricsDimensionDto() {}

  public UserCompoundMetricsDimensionDto(UserCompoundMetricsDimension userCompoundMetricsDimension) {
    if (userCompoundMetricsDimension != null) {
      BeanUtils.copyProperties(userCompoundMetricsDimension, this);
      String objectsData = userCompoundMetricsDimension.getObjectsData();
      if (StringUtil.isNotBlank(objectsData)) {
        this.setObjects(JSON.parseArray(objectsData, UserCompoundMetricsDimensionObject.class));
      }
    }
  }

  public UserCompoundMetricsDimension parseToUserCompoundMetricsDimension() {
    UserCompoundMetricsDimension userCompoundMetricsDimension = new UserCompoundMetricsDimension();
    BeanUtils.copyProperties(this, userCompoundMetricsDimension);
    userCompoundMetricsDimension.setObjectsData(JSON.toJSONString(this.getObjects()));
    return userCompoundMetricsDimension;
  }

  public PtoneMetricsDimension parseToPtoneMetricsDimension() {
    PtoneMetricsDimension metricsDimension = new PtoneMetricsDimension();
    BeanUtils.copyProperties(this, metricsDimension);
    return metricsDimension;
  }

  /**
   * 判断object（原始指标、维度）是否在code中使用中
   * @param obj
   * @return
   * @date: 2016年7月29日
   * @author peng.xu
   */
  public boolean usingMetricsObject(UserCompoundMetricsDimensionObject obj) {
    String formulaId = obj.getFormulaId();
    String formulaKey = UserCompoundMetricsDimensionDto.getFormulaKey(formulaId);
    String formula = this.getFormula();
    return formula != null && formula.contains(formulaKey);
  }

  /**
   * 复合指标计算公式中是否使用了计算函数
   * @return
   * @date: 2016年8月4日
   * @author peng.xu
   */
  public boolean isContainsFunc() {
    String[] suportFuncArray = DataBaseConfig.suportFuncArray;
    String formula = this.getFormula();
    if (StringUtil.isNotBlank(formula)) {
      formula = formula.toUpperCase();
      for (String func : suportFuncArray) {
        if (formula.contains(func.toUpperCase())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 根据formulaId生成公式中对应的标记字符串 [uuid]
   * @param formulaId : uuid
   * @return
   * @date: 2016年7月18日
   * @author peng.xu
   */
  public static String getFormulaKey(String formulaId) {
    return "[" + formulaId + "]";
  }

  /**
   * 根据formulaId生成queryCode中对应的column标记字符串 `uuid`
   * @param formulaId : uuid
   * @return
   * @date: 2016年7月26日
   * @author peng.xu
   */
  public static String getQueryCodeColumnKey(String formulaId) {
    return "`" + formulaId + "`";
  }

}
