package com.sizzler.service.ds.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptmind.common.utils.CollectionUtil;
import com.ptmind.common.utils.StringUtil;
import com.sizzler.cache.PtoneDsInfoCache;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.expr.FuncExpression;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionDto;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionObject;
import com.sizzler.domain.ds.vo.DimensionVo;
import com.sizzler.service.PtoneUserConnectionService;
import com.sizzler.service.UserService;
import com.sizzler.service.ds.PtoneDsInfoService;
import com.sizzler.service.ds.PtoneDsService;
import com.sizzler.service.ds.UserCompoundMetricsDimensionService;
import com.sizzler.service.space.SpaceService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

@Service("ptoneDsService")
public class PtoneDsServiceImpl implements PtoneDsService {

  private Logger logger = LoggerFactory.getLogger(PtoneDsServiceImpl.class);

  @Autowired
  private PtoneDsInfoCache ptoneDsInfoCache;

  @Autowired
  private PtoneDsInfoService ptoneDsInfoService;

  @Autowired
  private UserService userService;

  @Autowired
  private PtoneUserConnectionService ptoneUserConnectionService;

  @Autowired
  private SpaceService spaceService;

  @Autowired
  private ServiceFactory serviceFactory;

  @Autowired
  private UserCompoundMetricsDimensionService userCompoundMetricsDimensionService;

  @Override
  public List<PtoneDsInfo> getAllDsInfoList() {
    return this.ptoneDsInfoCache.getPtoneDsInfoList();
  }

  @Override
  public PtoneDsInfo getDsInfoByDsCode(String dsCode) {
    return this.ptoneDsInfoCache.getPtoneDsInfoByCode(dsCode);
  }

  @Override
  public UserCompoundMetricsDimensionDto addUserCompoundMetricsDimension(
      UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto) {
    return userCompoundMetricsDimensionService
        .addUserCompoundMetricsDimension(userCompoundMetricsDimensionDto);
  }

  @Override
  public UserCompoundMetricsDimensionDto updateUserCompoundMetricsDimension(
      UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto) {
    return userCompoundMetricsDimensionService
        .updateUserCompoundMetricsDimension(userCompoundMetricsDimensionDto);
  }

  @Override
  public void deleteUserCompoundMetricsDimension(String id, String uid) {
    userCompoundMetricsDimensionService.deleteUserCompoundMetricsDimension(id, uid);
  }

  @Override
  public List<UserCompoundMetricsDimensionDto> findUserCompoundMetricsDimensionList(String spaceId,
      String dsId, String tableId, String[] typeArray) {
    return userCompoundMetricsDimensionService.findUserCompoundMetricsDimensionList(spaceId, dsId,
        tableId, typeArray);
  }

  @Override
  public boolean validateCompoundMetrics(
      UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto, boolean isValidateFormula) {
    if (userCompoundMetricsDimensionDto == null) {
      return false;
    }
    String formula = userCompoundMetricsDimensionDto.getFormula();
    List<UserCompoundMetricsDimensionObject> metricsList =
        userCompoundMetricsDimensionDto.getObjects();

    // 表达式为空，则指标无效
    if (StringUtil.isBlank(formula)) {
      return false;
    }

    if (metricsList != null && metricsList.size() > 0) {
      // metrics列表不为空，则校验所有使用指标是否有效、并且数据类型为数值类型(数值、百分比、货币、持续时间类型)
      for (UserCompoundMetricsDimensionObject metrics : metricsList) {
        if (userCompoundMetricsDimensionDto.usingMetricsObject(metrics)) {
          String dsCode = metrics.getDsCode();
          // 非API类型的指标，校验colId、 tableId、sourceId、connectionId
          if (!DsConstants.isApiDs(dsCode)) {
            String connectionId = metrics.getConnectionId();
            String sourceId = metrics.getSourceId();
            String colId = metrics.getMetricsId();
            UserConnectionSourceTableColumn column =
                serviceFactory.getUserConnectionSourceTableColumnService()
                    .getAvailableColumn(colId);

            if (column == null
                || !PtoneMetricsDimension.isNumberDataType(column.getDataType())
                || serviceFactory.getDataSourceManagerService().getConnectionSourceById(sourceId) == null
                || serviceFactory.getPtoneUserConnectionService().get(connectionId) == null) {
              return false;
            } else {
              // 更新指标信息
              metrics.setDataType(column.getDataType());
              metrics.setDataFormat(column.getDataFormat());
              metrics.setUnit(column.getUnit());
            }
          }
        }
      }
    } else {
      // 如果metrics列表为空，则判断表达式code中是否包含指标 [uuid]
      if (formula.contains("[") && formula.contains("]")) {
        return false;
      }
    }

    // 校验公式，只有编辑时公式才会变，所以只需在编辑时校验
    if (isValidateFormula) {
      // 校验表达式中的函数
      String dbCode = userCompoundMetricsDimensionDto.getDsCode();
      FuncExpression funcExpr = new FuncExpression(formula, dbCode);
      if (!funcExpr.isFuncValidate()) {
        return false;
      }
      String testFormula = funcExpr.compile();


      for (UserCompoundMetricsDimensionObject metrics : metricsList) {
        String formulaKey = UserCompoundMetricsDimensionDto.getFormulaKey(metrics.getFormulaId());
        testFormula = testFormula.replace(formulaKey, "1");
      }

      // 校验表达式
      if (!validateFormula(testFormula)) {
        return false;
      }

      // 通过SQL查询来判断表达式语法是否正确
      String testSql = "select " + testFormula + " from ptone_ds_info limit 1";
      try {
        DataOperationUtils.queryForMap(testSql);
      } catch (Exception e) {
        return false;
      }
    }
    return true;
  }

  /**
   * 表达式的校验 (TODO: 临时处理)
   * 
   * @param formula
   * @return
   * @date: 2016年8月2日
   * @author peng.xu
   */
  public static boolean validateFormula(String formula) {

    // 判断除数为0
    if (formula.contains("/")) {
      String[] opArray = new String[] {"+", "-", "*", "/"};
      List<String> opList = Arrays.asList(opArray);
      String subFormula = formula;
      while (subFormula.contains("/")) {
        subFormula = subFormula.substring(subFormula.indexOf("/") + 1);
        String opNumber = "";
        for (int i = 0; i < subFormula.length(); i++) {
          String str = "" + subFormula.charAt(i);
          if (!opList.contains(str)) {
            opNumber += str;
          } else {
            break;
          }
        }
        opNumber = opNumber.replace(" ", ""); // 剔除所有空格
        if ("0".equals(opNumber) || "(0)".equals(opNumber)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public void buildCompoundMetrics(UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto,
      boolean isValidateFormula) {
    if (userCompoundMetricsDimensionDto == null) {
      return;
    }
    // 首先检查复合指标是否有效
    boolean isValidate =
        this.validateCompoundMetrics(userCompoundMetricsDimensionDto, isValidateFormula);
    userCompoundMetricsDimensionDto.setIsValidate(isValidate ? Constants.validateInt
        : Constants.inValidateInt);

    boolean isContainsFunc = userCompoundMetricsDimensionDto.isContainsFunc();
    userCompoundMetricsDimensionDto.setIsContainsFunc(isContainsFunc ? Constants.validate
        : Constants.inValidate);

    // 指标有效，则构建复合指标取数相关属性
    if (isValidate) {
      List<String> objectsIdList = new ArrayList<String>();
      List<String> dsIdList = new ArrayList<String>();
      List<String> dsCodeList = new ArrayList<String>();
      List<String> connectionIdList = new ArrayList<String>();
      List<String> sourceIdList = new ArrayList<String>();
      List<String> tableIdList = new ArrayList<String>();
      List<String> dataTypeList = new ArrayList<String>();
      List<String> unitList = new ArrayList<String>();
      List<String> dataFormatList = new ArrayList<String>();
      int metricsCount = 0;
      for (UserCompoundMetricsDimensionObject metrics : userCompoundMetricsDimensionDto
          .getObjects()) {
        if (userCompoundMetricsDimensionDto.usingMetricsObject(metrics)) {
          metricsCount++;
          objectsIdList.add(metrics.getMetricsId());
          dsIdList.add(metrics.getDsId() + "");
          dsCodeList.add(metrics.getDsCode());
          connectionIdList.add(metrics.getConnectionId());
          sourceIdList.add(metrics.getSourceId());
          tableIdList.add(metrics.getTableId());
          dataTypeList.add(metrics.getDataType());
          dataFormatList.add(metrics.getDataFormat());
          unitList.add(metrics.getUnit());
        }
      }

      userCompoundMetricsDimensionDto.setObjectsIdList(StringUtil.join(objectsIdList, ","));
      userCompoundMetricsDimensionDto.setDsIdList(StringUtil.join(dsIdList, ","));
      userCompoundMetricsDimensionDto.setDsCodeList(StringUtil.join(dsCodeList, ","));
      userCompoundMetricsDimensionDto.setConnectionIdList(StringUtil.join(connectionIdList, ","));
      userCompoundMetricsDimensionDto.setSourceIdList(StringUtil.join(sourceIdList, ","));
      userCompoundMetricsDimensionDto.setTableIdList(StringUtil.join(tableIdList, ","));
      userCompoundMetricsDimensionDto.setMetricsCount(metricsCount);

      String dataType = PtoneMetricsDimension.getCompoundMetricsDataType(dataTypeList);
      String dataFormat =
          PtoneMetricsDimension.getCompoundMetricsDataFormat(dataType, dataFormatList);
      String unit = PtoneMetricsDimension.getCompoundMetricsDataUnit(dataType, unitList);
      if (StringUtil.isBlank(unit) && StringUtil.isNotBlank(dataFormat)) {
        unit = PtoneMetricsDimension.getUnitByDataTypeAndFormat(dataType, dataFormat);
      }
      userCompoundMetricsDimensionDto.setDataType(dataType);
      userCompoundMetricsDimensionDto.setDataFormat(dataFormat);
      userCompoundMetricsDimensionDto.setUnit(unit);

      String queryCode = userCompoundMetricsDimensionDto.getFormula();

      // 对表达式中的函数进行转换
      if (FuncExpression.isContainsFunc(queryCode)) {
        String dbCode = userCompoundMetricsDimensionDto.getDsCode();
        FuncExpression funcExpr = new FuncExpression(queryCode, dbCode);
        queryCode = funcExpr.compile();
      }

      // 对单位进行进制转换
      for (UserCompoundMetricsDimensionObject metrics : userCompoundMetricsDimensionDto
          .getObjects()) {
        String formulaKey = UserCompoundMetricsDimensionDto.getFormulaKey(metrics.getFormulaId());
        String objDataType = metrics.getDataType();
        if (PtoneMetricsDimension.DATA_TYPE_PERCENT.equals(objDataType)) {
          // 对于百分比类型数据，直接除以一百
          queryCode = queryCode.replace(formulaKey, "(" + formulaKey + "/100)");
        } else if (PtoneMetricsDimension.DATA_TYPE_DURATION.equals(objDataType)) {
          // 对于持续时间类型的单位，需要根据单位进行进制转换
          String objUnit = metrics.getUnit();
          if (StringUtil.isBlank(objUnit)) {
            objUnit =
                PtoneMetricsDimension.getUnitByDataTypeAndFormat(metrics.getDataType(),
                    metrics.getDataFormat());
          }
          String parseUnitStr = PtoneMetricsDimension.getParseDurationStr(objUnit, unit);
          queryCode = queryCode.replace(formulaKey, "(" + formulaKey + parseUnitStr + ")");
        }
        String convertColumn =
            UserCompoundMetricsDimensionDto.getQueryCodeColumnKey(metrics.getFormulaId()); // 用于本地临时表查询复合结果结果使用
        queryCode = queryCode.replace(formulaKey, convertColumn);
      }
      userCompoundMetricsDimensionDto.setQueryCode(queryCode);
    }

  }

  @Override
  public long getUseCompoundMetricsWidgetCount(String id) {
    return userCompoundMetricsDimensionService.getUseCompoundMetricsWidgetCount(id);
  }

  /**
   * 获取实时build后的复合指标：优先从复合指标map中获取，如果没有则从库中查询然后放到map中
   * 
   * @param metricsId
   * @param compoundMetricsMap 复合指标缓存map
   * @return
   * @date: 2016年7月23日
   * @author peng.xu
   */
  @Override
  public UserCompoundMetricsDimensionDto getCompoundMetrics(String metricsId,
      Map<String, UserCompoundMetricsDimensionDto> compoundMetricsMap) {
    UserCompoundMetricsDimensionDto compoundMetrics = null;
    if (compoundMetricsMap != null && compoundMetricsMap.containsKey(metricsId)) {
      compoundMetrics = compoundMetricsMap.get(metricsId);
    } else {
      compoundMetrics =
          userCompoundMetricsDimensionService.getUserCompoundMetricsDimension(metricsId);
      this.buildCompoundMetrics(compoundMetrics, false);// 每次使用都需要重新构建复合指标（包含有效性校验）
      if (compoundMetricsMap != null) {
        compoundMetricsMap.put(metricsId, compoundMetrics);
      }
    }
    return compoundMetrics;
  }

  @Override
  public List<PtoneDsInfo> getServiceDatasource(String userSource) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("isShow", new Object[] {Constants.validate});
    paramMap.put("isPlus", new Object[] {Constants.validate, Constants.inValidate});
    return ptoneDsInfoService.findByWhere(paramMap);
  }

  @Override
  public List<PtoneDsInfo> getIsShowDatasource() {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("isShow", new Object[] {Constants.validate});
    return ptoneDsInfoService.findByWhere(paramMap);
  }

  @Override
  public List<PtoneDsInfo> getOpenDatasource() {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("isPlus", new Object[] {PtoneDsInfo.IS_PLUS});
    return ptoneDsInfoService.findByWhere(paramMap);
  }

  /**
   * 所有数据源获取维度列表接口(返回结构为树形结构的维度列表，需要展示几级返回几级)
   */
  @Override
  public List<DimensionVo> getDimensionList(long dsId, String connectionId, String accountName,
      String profileId) {
    if (DsConstants.isGetMetricsDimensionFormUserColumnDs(dsId)) {
      return this.getMetricsDimensionFromUserColumn(dsId, connectionId, accountName, profileId);
    } else {
      logger.error("lost ds config for get metrics and dimension");
      throw new ServiceException("lost ds<id=" + dsId + "> config for get metrics and dimension");
    }
  }

  /**
   * 从user_connection_source_table_column表获取用户列维度列表（gd、excel、mysql等）
   * @param dsId
   * @param connectionId
   * @param accountName
   * @param profileId
   * @return
   * @date: 2016年12月23日
   * @author peng.xu
   */
  private List<DimensionVo> getMetricsDimensionFromUserColumn(long dsId, String connectionId,
      String accountName, String profileId) {
    List<DimensionVo> dimensions = new ArrayList<DimensionVo>();
    List<PtoneMetricsDimension> dimensionList =
        serviceFactory.getDataSourceManagerService()
            .getUserMetricsDimensionList(
                dsId,
                profileId,
                new String[] {PtoneMetricsDimension.TYPE_METRICS,
                    PtoneMetricsDimension.TYPE_DIMENSION});
    if (CollectionUtil.isNotEmpty(dimensionList)) {
      for (PtoneMetricsDimension dimension : dimensionList) {
        DimensionVo dimensionVo = new DimensionVo();
        BeanUtils.copyProperties(dimension, dimensionVo);
        dimensionVo.setIsLeaf(true);
        dimensions.add(dimensionVo);
      }
    }
    return dimensions;
  }

}
