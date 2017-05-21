package com.sizzler.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sizzler.common.SourceType;
import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.common.exception.BusinessErrorCode;
import com.sizzler.common.exception.BusinessException;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.PtoneDateUtil;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.JodaDateUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.common.utils.UuidUtil;
import com.sizzler.dao.GaWidgetDao;
import com.sizzler.dao.VariableDao;
import com.sizzler.dao.WidgetDao;
import com.sizzler.dao.WidgetVariableDao;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.ds.UserCompoundMetricsDimension;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionDto;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.variable.dto.PtVariables;
import com.sizzler.domain.variable.dto.PtoneVariableWithWidgetId;
import com.sizzler.domain.widget.GaWidgetInfo;
import com.sizzler.domain.widget.PtonePanelWidget;
import com.sizzler.domain.widget.PtoneWidgetChartSetting;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.domain.widget.PtoneWidgetInfoExtend;
import com.sizzler.domain.widget.PtoneWidgetVariable;
import com.sizzler.domain.widget.dto.AcceptWidget;
import com.sizzler.domain.widget.dto.DynamicSegmentCondition;
import com.sizzler.domain.widget.dto.DynamicSegmentData;
import com.sizzler.domain.widget.dto.PtoneWidgetChartSettingDto;
import com.sizzler.domain.widget.dto.SegmentData;
import com.sizzler.domain.widget.vo.MetricsDimensionsAliasVo;
import com.sizzler.service.UserService;
import com.sizzler.service.WidgetChartSettingService;
import com.sizzler.service.WidgetDataService;
import com.sizzler.service.WidgetExtendService;
import com.sizzler.service.WidgetService;
import com.sizzler.service.ds.UserCompoundMetricsDimensionService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.util.CascadeDeleteUtil;

@Service("widgetService")
public class WidgetServiceImpl extends ServiceBaseInterfaceImpl<PtoneWidgetInfo, String> implements
    WidgetService {

  private static Logger logger = LoggerFactory.getLogger(WidgetServiceImpl.class);

  @Autowired
  private WidgetDao widgetDao;
  @Autowired
  private WidgetExtendService widgetExtendService;
  @Autowired
  private VariableDao variableDao;
  @Autowired
  private WidgetVariableDao widgetVariableDao;
  @Autowired
  private GaWidgetDao gaWidgetDao;
  @Autowired
  private UserService userService;
  @Autowired
  private WidgetDataService widgetDataService;
  @Autowired
  private WidgetChartSettingService widgetChartSettingService;

  @Autowired
  private ServiceFactory serviceFactory;

  @Override
  public List<PtoneWidgetInfo> findById(String id) {
    return widgetDao.findById(id);
  }

  @Override
  @Transactional
  public AcceptWidget save(AcceptWidget widget) {
    PtoneWidgetInfo baseWidget = widget.getBaseWidget();
    baseWidget.setPanelId(widget.getPanelId());
    PtoneWidgetChartSettingDto chartSettingDto = widget.getChartSetting();
    List<PtVariables> vars = widget.getVariables();
    PtonePanelWidget panelWidgetRelation = new PtonePanelWidget();
    panelWidgetRelation.setWidgetId(baseWidget.getWidgetId());
    if (null != baseWidget.getIsTemplate() && baseWidget.getIsTemplate().equals(Constants.validate)) {
      // PtoneWidgetTemplet templet = new PtoneWidgetTemplet();
      // BeanUtils.copyProperties(baseWidget, templet);
      // // 只添加第一个变量的指标和维度
      // templet.setMetricsJson(JSON.toJSONString(vars.get(0).getMetrics()));
      // templet.setDimensionsJson(JSON.toJSONString(vars.get(0).getDimensions()));
      // templet.setSegmentsJson(JSON.toJSONString(vars.get(0).getSegment()));
      // templet.setFiltersJson(JSON.toJSONString(vars.get(0).getFilters()));
      // templet.setDsCode(vars.get(0).getDsCode());
      // templet.setTempletId(templet.getWidgetId());
      // if (null != chartSettingDto && chartSettingDto.getShowMultiY() == 1) {
      // templet.setTempletGraphName("biaxial");
      // } else {
      // templet.setTempletGraphName(templet.getGraphName());
      // }
      // // 保存模板和标签的关系
      // widgetTempletDao.save(templet);
      // // if(Constants.published.equals(templet.getStatus())){
      // // widgetTempletService.createWidgetTempletIndex(widget);
      // // }
      // saveWidgetTempletData(widget);
      // if (null != widget.getTags() && !widget.getTags().isEmpty()) {
      // saveWidgetTagsRelations(widget.getTags(), baseWidget.getWidgetId());
      // }

      // // 保存widget gallery运营数据统计信息
      // try {
      // serviceFactory.getWidgetTempletStatService().saveTempletStatInfo(widget);
      // } catch (Exception e) {
      // logger.error("save widget templet stat info error, widgetId:: " +
      // baseWidget.getWidgetId(),
      // e);
      // }

    } else {
      panelWidgetRelation.setPanelId(widget.getPanelId());
      widgetDao.savePanelWidgetRelation(panelWidgetRelation);
      // 新增插入SourceType，add by you.zou on 2016-11-29
      // 用户手动创建
      baseWidget.setSourceType(SourceType.Widget.USER_CREATED);
      // 插入widget基本信息
      widgetDao.insert(baseWidget);
      // 插入panel和widget的关联关系
    }

    if (chartSettingDto != null) {
      chartSettingDto.setWidgetId(baseWidget.getWidgetId());
      widgetChartSettingService.saveOrUpdate(chartSettingDto.parseChartSetting());
    }

    String widgetType = baseWidget.getWidgetType();

    // tool图表处理 || 子widget的layout信息处理
    if (PtoneWidgetInfo.WIDGET_TYPE_TOOL.equals(widgetType)
        || StringUtil.isNotBlank(baseWidget.getParentId())) {

      // 处理tool类型图表的数据保存
      PtoneWidgetInfoExtend widgetExtend = widget.getToolData();
      if (widgetExtend == null) {
        widgetExtend = new PtoneWidgetInfoExtend();
      }

      // 处理子widget的layout信息的保存
      if (StringUtil.isNotBlank(baseWidget.getParentId())) {
        String layout = JSON.toJSONString(widget.getLayout());
        widgetExtend.setLayout(layout);
      }
      widgetExtend.setWidgetId(baseWidget.getWidgetId());
      widgetExtendService.save(widgetExtend);
    }

    // 自定义widget处理（父widget）
    if (PtoneWidgetInfo.WIDGET_TYPE_CUSTOM.equals(widget.getBaseWidget().getWidgetType())) {
      // 处理子widget的复制
      if (widget.getChildren() != null && !widget.getChildren().isEmpty()) {
        for (AcceptWidget subWidget : widget.getChildren()) {
          String subWidgetId = UuidUtil.generateUuid();
          subWidget.getBaseWidget().setWidgetId(subWidgetId);
          subWidget.getBaseWidget().setOwnerId(widget.getBaseWidget().getOwnerId());
          subWidget.getBaseWidget().setCreatorId(widget.getBaseWidget().getCreatorId());
          subWidget.getBaseWidget().setCreateTime(System.currentTimeMillis());
          subWidget.getBaseWidget().setModifierId(widget.getBaseWidget().getModifierId());
          subWidget.getBaseWidget().setModifyTime(System.currentTimeMillis());
          subWidget.getBaseWidget().setParentId(widget.getBaseWidget().getWidgetId());
          subWidget.getBaseWidget().setPanelId(widget.getPanelId());
          subWidget.setPanelId(widget.getPanelId());

          if (subWidget.getVariables() != null && !subWidget.getVariables().isEmpty()) {
            for (PtVariables subVariable : subWidget.getVariables()) {
              String subVariableId = UuidUtil.generateUuid();
              subVariable.setVariableId(subVariableId);
            }
          }
          this.save(subWidget);
        }
      }
    }

    // chart类型的widget处理，保存variable信息
    if (PtoneWidgetInfo.WIDGET_TYPE_CHART.equals(widget.getBaseWidget().getWidgetType())) {// chart类型图表处理
      saveVariables(vars, widget);
      // 返回前端保存后的widget信息
      widget.setBaseWidget(baseWidget);
      // saveUserSelected(baseWidget.getCreatorId(), baseWidget.getDateKey());
    }
    return widget;
  }

  /**
   * 保存widgetTemple的demo数据到数据库(先查询数据再保存)
   * 
   * @param widget
   * @author peng.xu
   */
  private void saveWidgetTempletData(AcceptWidget widget) {
    // if (widget == null || widget.getBaseWidget() == null
    // ||
    // PtoneWidgetInfo.WIDGET_TYPE_TOOL.equals(widget.getBaseWidget().getWidgetType())
    // ||
    // PtoneWidgetInfo.WIDGET_TYPE_CUSTOM.equals(widget.getBaseWidget().getWidgetType()))
    // {
    // return;
    // }
    //
    // String widgetId = widget.getBaseWidget().getWidgetId();
    // Map<String, String> webParamMap = new HashMap<String, String>();
    // webParamMap.put(Constants.PARAM_IS_EXAMPLE, "false");
    //
    // PtoneWidgetData widgetTempletData = null;
    // try {
    // widgetTempletData = widgetDataService.getPtoneWidgetData(widgetId,
    // webParamMap, widget);
    // } catch (Exception e) {
    // logger.error("get widget templet data error.", e);
    // }
    // PtoneWidgetTempletData templetData =
    // widgetTempletDataService.get(widgetId);
    // if (templetData == null) {
    // templetData = new PtoneWidgetTempletData();
    // templetData.setTempletId(widgetId);
    // templetData.setData(JSON.toJSONString(widgetTempletData,
    // SerializerFeature.DisableCircularReferenceDetect));
    // widgetTempletDataService.save(templetData);
    // } else {
    // String dataStr = JSON.toJSONString(widgetTempletData,
    // SerializerFeature.DisableCircularReferenceDetect);
    // templetData.setData(dataStr);
    // widgetTempletDataService.update(templetData);
    // }
  }

  /**
   * 保存模板与标签的关系.
   * 
   * @return
   * @author: zhangli
   * @date: 2016-08-10
   */
  public void saveWidgetTagsRelations(List<Long> tagIdList, String wid) {
  }

  public void saveVariables(List<PtVariables> vars, AcceptWidget widget) {
    for (PtVariables var : vars) {
      PtoneVariableInfo ptoneVariableInfo = new PtoneVariableInfo();
      BeanUtils.copyProperties(var, ptoneVariableInfo);
      ptoneVariableInfo.setPanelId(widget.getPanelId());
      ptoneVariableInfo.setWidgetId(widget.getBaseWidget().getWidgetId());
      // 插入variable信息
      variableDao.insert(ptoneVariableInfo);
      // 插入widget和variable的关联关系
      PtoneWidgetVariable widgetVariableRelation = new PtoneWidgetVariable();
      widgetVariableRelation.setWidgetId(widget.getBaseWidget().getWidgetId());
      widgetVariableRelation.setVariableId(var.getVariableId());
      widgetVariableDao.insert(widgetVariableRelation);

      GaWidgetInfo gaWidgetInfo = getGaWidgetInfo(var, widget.getBaseWidget());
      gaWidgetInfo.setPanelId(widget.getPanelId());
      gaWidgetDao.insert(gaWidgetInfo);

      String userId = widget.getBaseWidget().getCreatorId();
      long dsId = var.getPtoneDsInfoId();
      this.saveUserGaProfileSelected(userId, dsId, widget.getBaseWidget().getSpaceId(),
          gaWidgetInfo);
    }
  }

  @Override
  @Transactional
  public AcceptWidget updateWidgetWithVariables(AcceptWidget widget) {
    PtoneWidgetInfo baseWidget = widget.getBaseWidget();
    PtoneWidgetChartSettingDto chartSettingDto = widget.getChartSetting();
    List<PtVariables> vars = widget.getVariables();
    // 是模板
    if (null != baseWidget.getIsTemplate() && baseWidget.getIsTemplate().equals(Constants.validate)) {
      // PtoneWidgetTemplet templet = new PtoneWidgetTemplet();
      // BeanUtils.copyProperties(baseWidget, templet);
      // fixMetricsData(vars.get(0).getMetrics(),
      // serviceFactory.getPtoneDsInfoCache()
      // .getPtoneDsInfoById(vars.get(0).getPtoneDsInfoId()));
      // // 只添加第一个变量的指标和维度
      // templet.setMetricsJson(JSON.toJSONString(vars.get(0).getMetrics()));
      // templet.setDimensionsJson(JSON.toJSONString(vars.get(0).getDimensions()));
      // templet.setSegmentsJson(JSON.toJSONString(vars.get(0).getSegment()));
      // templet.setFiltersJson(JSON.toJSONString(vars.get(0).getFilters()));
      // templet.setDsCode(vars.get(0).getDsCode());
      // templet.setTempletId(templet.getWidgetId());
      // if (null != chartSettingDto && chartSettingDto.getShowMultiY() ==
      // Constants.validateInt) {
      // templet.setTempletGraphName("biaxial");
      // } else {
      // templet.setTempletGraphName(templet.getGraphName());
      // }
      // // 更新模板
      // widgetTempletDao.update(templet);
      // // 更新模板数据
      // saveWidgetTempletData(widget);
      //
      // // 删除标签关系
      // Map<String, Object[]> paramMap = new HashMap<>();
      // paramMap.put("templateWidgetId", new Object[] {
      // baseWidget.getWidgetId() });
      // tagWidgetTemplateDao.delete(paramMap);
      //
      // if (CollectionUtil.isNotEmpty(widget.getTags())) {
      // saveWidgetTagsRelations(widget.getTags(), baseWidget.getWidgetId());
      // }
      //
      // // 更新widget gallery运营数据统计信息
      // try {
      // serviceFactory.getWidgetTempletStatService().updateTempletStatInfo(widget);
      // } catch (Exception e) {
      // logger.error(
      // "update widget templet stat info error, widgetId:: " +
      // baseWidget.getWidgetId(), e);
      // }
    } else {
      // 更新基本信息
      widgetDao.update(baseWidget);
    }

    if (chartSettingDto != null) {
      chartSettingDto.setWidgetId(baseWidget.getWidgetId());
      widgetChartSettingService.saveOrUpdate(chartSettingDto.parseChartSetting());
    }

    String widgetType = baseWidget.getWidgetType();
    PtoneWidgetInfoExtend widgetExtend = widget.getToolData();
    if (PtoneWidgetInfo.WIDGET_TYPE_TOOL.equals(widgetType)) { // tool图表处理
      if (widgetExtend == null) {
        widgetExtend = new PtoneWidgetInfoExtend();
      }

      if (StringUtil.isNotBlank(baseWidget.getParentId())) {
        // 处理layout信息的保存
        String layout = JSON.toJSONString(widget.getLayout());
        widgetExtend.setLayout(layout);
      }

      widgetExtend.setWidgetId(baseWidget.getWidgetId());
      if (Constants.inValidate.equals(baseWidget.getStatus())) {
        widgetExtend.setIsDelete(Constants.validateInt);
      }
      widgetExtendService.update(widgetExtend);
    } else if (PtoneWidgetInfo.WIDGET_TYPE_CUSTOM.equals(widget.getBaseWidget().getWidgetType())) {

    } else if (widgetExtend != null && baseWidget.getIsDemo() == Constants.validateInt) {// demo数据
      /*
       * Map<String, Object[]> paramMap = new HashMap<>();
       * paramMap.put("widgetId",new Object[]{baseWidget.getWidgetId()});
       */
      widgetExtend.setWidgetId(baseWidget.getWidgetId());
      widgetExtend.setValue(widgetExtend.getExtend());
      PtoneWidgetInfoExtend widgetExt = widgetExtendService.get(baseWidget.getWidgetId());
      if (widgetExt == null) {
        widgetExtendService.save(widgetExtend);
      } else {
        widgetExtendService.update(widgetExtend);
      }
    } else { // chart类型图表处理

      if (StringUtil.isNotBlank(baseWidget.getParentId())) {
        // 处理layout信息的保存
        if (widgetExtend == null) {
          widgetExtend = new PtoneWidgetInfoExtend();
        }
        String layout = JSON.toJSONString(widget.getLayout());
        widgetExtend.setLayout(layout);
        widgetExtend.setWidgetId(baseWidget.getWidgetId());
        if (Constants.inValidate.equals(baseWidget.getStatus())) {
          widgetExtend.setIsDelete(Constants.validateInt);
        }
        widgetExtendService.update(widgetExtend);
      }

      if (null != vars && !vars.isEmpty()) {
        for (PtVariables var : vars) {
          PtoneVariableInfo ptoneVariableInfo = new PtoneVariableInfo();
          BeanUtils.copyProperties(var, ptoneVariableInfo);
          // 更新variable基本信息
          variableDao.update(ptoneVariableInfo);

          // 更新gaWidget信息
          GaWidgetInfo gaWidgetInfo = getGaWidgetInfo(var, baseWidget);
          gaWidgetDao.update(gaWidgetInfo);

          String userId = baseWidget.getCreatorId();
          long dsId = var.getPtoneDsInfoId();
          saveUserGaProfileSelected(userId, dsId, widget.getBaseWidget().getSpaceId(), gaWidgetInfo);
        }
      }
      // 返回前端保存后的widget信息
      widget.setBaseWidget(baseWidget);
      // saveUserSelected(baseWidget.getCreatorId(), baseWidget.getDateKey());
    }

    return widget;
  }

  public GaWidgetInfo getGaWidgetInfo(PtVariables var, PtoneWidgetInfo baseWidget) {
    GaWidgetInfo gaWidgetInfo = new GaWidgetInfo();
    BeanUtils.copyProperties(var, gaWidgetInfo);
    gaWidgetInfo.setWidgetId(baseWidget.getWidgetId());

    List<String> metricsId = new ArrayList<String>();
    List<String> dimensionsId = new ArrayList<String>();

    if (var.getMetrics() != null) {
      for (PtoneMetricsDimension m : var.getMetrics()) {
        String id = m.getId();
        metricsId.add(id);

        // 更新复合指标最后使用时间
        if (PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equalsIgnoreCase(m.getType())) {
          try {
            UserCompoundMetricsDimension compoundMetrics = new UserCompoundMetricsDimension();
            compoundMetrics.setId(id);
            compoundMetrics.setLastUseTime(JodaDateUtil.getCurrentDateTime());
            serviceFactory.getUserCompoundMetricsDimensionService().update(compoundMetrics);
          } catch (Exception e) {
            // 运营数据，执行失败不影响正常业务流程
            logger.error("stat CompoundMetrics last use time error: ", e);
          }
        }

        // 设置过一次全局过滤器， 就覆盖指标级过滤器历史数据
        if (var.getFilters() != null || var.getSegment() != null) {
          m.setSegment(null);
        }
      }
    }
    if (var.getDimensions() != null) {
      for (PtoneMetricsDimension d : var.getDimensions()) {
        dimensionsId.add(d.getId());
      }
    }

    if (null != var.getMetrics()) {
      gaWidgetInfo.setMetricsId(StringUtil.join(metricsId, ","));
    }
    if (null != var.getDimensions()) {
      gaWidgetInfo.setDimensionsId(StringUtil.join(dimensionsId, ","));
    }
    gaWidgetInfo.setMetrics(JSON.toJSONString(var.getMetrics()));
    gaWidgetInfo.setDimensions(JSON.toJSONString(var.getDimensions()));
    gaWidgetInfo.setSegment(JSON.toJSONString(var.getSegment()));
    gaWidgetInfo.setFilters(JSON.toJSONString(var.getFilters()));
    gaWidgetInfo.setUid(baseWidget.getCreatorId());
    gaWidgetInfo.setDsId(var.getPtoneDsInfoId());
    return gaWidgetInfo;
  }

  /**
   * 保存用户最近一次选择profile信息
   * 
   * @param userId
   * @param dsId
   * @param gaWidgetInfo
   * @author peng.xu
   */
  private void saveUserGaProfileSelected(String userId, Long dsId, String spaceId,
      GaWidgetInfo gaWidgetInfo) {
    if (dsId == null || dsId == 0 || gaWidgetInfo.getProfileId() == null
        || gaWidgetInfo.getAccountName() == null) {
      return;
    }
    Map<String, String> profileSelectedSetting = new HashMap<String, String>();
    profileSelectedSetting.put("dsId", dsId + "");
    profileSelectedSetting.put("dsCode",
        serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById(dsId).getCode());
    profileSelectedSetting.put("accountName", gaWidgetInfo.getAccountName());
    profileSelectedSetting.put("accountId", gaWidgetInfo.getAccountId());
    profileSelectedSetting.put("propertyId", gaWidgetInfo.getPropertyId());
    profileSelectedSetting.put("prfileId", gaWidgetInfo.getProfileId());
    profileSelectedSetting.put("connectionId", gaWidgetInfo.getConnectionId());

    userService.updateProfileSelectedSetting(userId, dsId + "", spaceId, profileSelectedSetting);
  }

  /**
   * 保存用户最近一次选择信息 用户维态的值设置在前台处理（后台提供接口持久化）
   * 
   * @param userId
   * @author peng.xu
   */
  @Deprecated
  private void saveUserSelected(String userId, String dateKey) {
    // all Time 不需要记住 20170208
    if (StringUtil.isBlank(dateKey) || PtoneDateUtil.DATE_KEY_ALLTIME.equals(dateKey)) {
      return;
    }
    Map<String, String> userSelectedSetting = new HashMap<String, String>();
    userSelectedSetting.put("dateKey", dateKey);
    userService.updateUserSelectedSetting(userId, userSelectedSetting);
  }

  private List<PtVariables> fillVariables(String widgetId,
      List<PtoneVariableWithWidgetId> variableDBList, List<GaWidgetInfo> gaWidgetInfoList) {
    List<PtVariables> variables = new ArrayList<>();
    for (PtoneVariableWithWidgetId variableDB : variableDBList) {
      if (variableDB.getWidgetId().equals(widgetId)) {
        PtVariables var = new PtVariables();
        if (null != gaWidgetInfoList && !gaWidgetInfoList.isEmpty()) {
          GaWidgetInfo info = getGaWidgetInfoByVariableId(variableDB.getVariableId(),
              gaWidgetInfoList);
          if (info != null) {
            BeanUtils.copyProperties(info, var);
            BeanUtils.copyProperties(variableDB, var);
            var = setGaConditions(var, info);
            var.setPtoneDsInfoId(variableDB.getPtoneDsInfoId());
            PtoneDsInfo dsInfo = serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById(
                variableDB.getPtoneDsInfoId());
            var.setDsCode(dsInfo != null ? dsInfo.getCode() : "");

            // 处理指标
            this.fixMetricsData(var.getMetrics(), dsInfo);

            // 处理维度
            this.fixDimensions(var.getDimensions(), dsInfo);

            // 处理过滤器
            this.fixFilters(var.getFilters(), dsInfo);

          }
        }
        variables.add(var);
      }
    }
    return variables;
  }

  /**
   * 修正已选指标信息
   * 
   * @date: 2016年7月27日
   * @author peng.xu
   */
  public void fixMetricsData(List<PtoneMetricsDimension> metricsDataList, PtoneDsInfo dsInfo) {
    if (null != metricsDataList && !metricsDataList.isEmpty()) {
      for (PtoneMetricsDimension data : metricsDataList) {
        if (data == null) {
          continue;
        }
        // 修正复合指标的有效性校验、以及名称 add by xupeng 20160720
        String type = data.getType();
        if (PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equalsIgnoreCase(type)) {
          String metricsId = data.getId();
          UserCompoundMetricsDimensionDto compoundMetrics = serviceFactory
              .getUserCompoundMetricsDimensionService().getUserCompoundMetricsDimension(metricsId);
          serviceFactory.getPtoneDsService().buildCompoundMetrics(compoundMetrics, false); // 包含校验
          if (compoundMetrics != null) {
            String metricsName = compoundMetrics.getName();
            String calculateType = data.getCalculateType();
            data.setIsValidate(compoundMetrics.getIsValidate().toString());
            data.setRealName(metricsName);
            data.setDataType(compoundMetrics.getDataType());
            data.setDataFormat(compoundMetrics.getDataFormat());
            data.setUnit(compoundMetrics.getUnit());
            data.setFormula(compoundMetrics.getFormula());
            data.setIsContainsFunc(compoundMetrics.getIsContainsFunc());
            if (StringUtil.isNotBlank(calculateType)
                && Constants.inValidate.equals(compoundMetrics.getIsContainsFunc())) {
              data.setName(calculateType + "(" + metricsName + ")");
            } else {
              data.setName(metricsName);
            }
          } else {
            // 标记指标已删除
            data.setIsValidate(PtoneMetricsDimension.VALIDATE_STATUS_DELETE);
          }
        } else if (dsInfo != null && !DsConstants.isApiDs(dsInfo.getCode())) {
          // 只有在type==model的数据源时，需要修正指标、维度的realname、name、name add by you.zou
          // 2016.2.26
          // 所有非Api类型的都需要修正 add by xupeng 20160727

          String colId = data.getId();
          // 获取数据库中的指标信息
          UserConnectionSourceTableColumn column = serviceFactory
              .getUserConnectionSourceTableColumnService().getAvailableColumn(colId);
          if (column == null) {
            // 标记指标已删除
            data.setIsValidate(PtoneMetricsDimension.VALIDATE_STATUS_DELETE);
          } else {
            String name = column.getName();
            if (!StringUtils.equals(data.getRealName(), name)) {
              data.setRealName(name);
              data.setName(data.getCalculateType() + "(" + name + ")");
            }
            data.setDataType(column.getDataType());
            data.setDataFormat(column.getDataFormat());
            data.setUnit(column.getUnit());
          }
        } else if (dsInfo != null && DsConstants.isSupportCalculateApiDs(dsInfo.getCode())) {
          String calculateType = data.getCalculateType();
          if (StringUtil.isNotBlank(calculateType)) {
            data.setName(data.getCalculateType() + "(" + data.getRealName() + ")");
          }
        }
      }
    }
  }

  /**
   * 修正已选维度信息
   * 
   * @param dimensionDataList
   * @param dsInfo
   * @date: 2016年8月3日
   * @author peng.xu
   */
  public void fixDimensions(List<PtoneMetricsDimension> dimensionDataList, PtoneDsInfo dsInfo) {
    if (null != dimensionDataList && !dimensionDataList.isEmpty()) {
      for (PtoneMetricsDimension data : dimensionDataList) {
        if (data == null) {
          continue;
        }
        if (dsInfo != null && !DsConstants.isApiDs(dsInfo.getCode())) {
          // 只有在type==model的数据源时，需要修正指标、维度的realname、name、name add by you.zou
          // 2016.2.26
          // 所有非Api类型的都需要修正 add by xupeng 20160727

          String colId = data.getId();
          // 获取数据库中的指标信息
          UserConnectionSourceTableColumn column = serviceFactory
              .getUserConnectionSourceTableColumnService().get(colId);
          if (column == null) {
            // 标记指标已删除
            data.setIsValidate(PtoneMetricsDimension.VALIDATE_STATUS_DELETE);
          } else {
            String name = column.getName();
            if (!StringUtils.equals(data.getRealName(), name)) {
              data.setRealName(name);
              data.setName(name);
            }
            data.setDataType(column.getDataType());
            data.setDataFormat(column.getDataFormat());
          }
        }
      }
    }
  }

  /**
   * 修正已选择过滤器
   * 
   * @param filters
   * @date: 2016年8月2日
   * @author peng.xu
   */
  public void fixFilters(SegmentData filters, PtoneDsInfo dsInfo) {
    if (filters != null) {
      String segmentType = filters.getType();
      if (SegmentData.TYPE_NEW.equalsIgnoreCase(segmentType) && filters.getNewData() != null) {
        List<DynamicSegmentData> dynamicSegmentDataList = filters.getNewData();
        if (dynamicSegmentDataList != null) {
          for (DynamicSegmentData dynamicSegmentData : dynamicSegmentDataList) {
            List<DynamicSegmentCondition> conditionList = dynamicSegmentData.getCondition();
            if (dynamicSegmentDataList != null) {
              for (DynamicSegmentCondition condition : conditionList) {
                String type = condition.getType();
                String metricsId = condition.getId();
                if (PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equalsIgnoreCase(type)) {
                  UserCompoundMetricsDimensionDto compoundMetrics = serviceFactory
                      .getUserCompoundMetricsDimensionService().getUserCompoundMetricsDimension(
                          metricsId);
                  if (compoundMetrics != null) {
                    condition.setName(compoundMetrics.getName());
                  }
                } else if (dsInfo != null && !DsConstants.isApiDs(dsInfo.getCode())) {
                  UserConnectionSourceTableColumn column = serviceFactory
                      .getUserConnectionSourceTableColumnService().get(metricsId);
                  if (column != null) {
                    condition.setName(column.getName());
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  public PtVariables setGaConditions(PtVariables var, GaWidgetInfo info) {
    if (StringUtil.hasText(info.getMetrics())) {
      List<PtoneMetricsDimension> metricsDataList = JSON.parseArray(info.getMetrics(),
          PtoneMetricsDimension.class);
      var.setMetrics(metricsDataList);
    }
    if (StringUtil.hasText(info.getDimensions())) {
      List<PtoneMetricsDimension> dimensionDataList = JSON.parseArray(info.getDimensions(),
          PtoneMetricsDimension.class);
      var.setDimensions(dimensionDataList);
    }
    if (StringUtil.hasText(info.getSegment())) {
      SegmentData segment = JSON.parseObject(info.getSegment(), SegmentData.class);
      var.setSegment(segment);
    }
    if (StringUtil.hasText(info.getFilters())) {
      SegmentData filters = JSON.parseObject(info.getFilters(), SegmentData.class);
      var.setFilters(filters);
    }
    return var;
  }

  @Override
  public AcceptWidget getWidgetById(String widgetId) {
    PtoneWidgetInfo widget = widgetDao.get(widgetId);
    List<String> widgetIdList = new ArrayList<String>();
    widgetIdList.add(widgetId);
    List<PtoneVariableWithWidgetId> variableDBList = variableDao
        .findVariableByWidgetId(widgetIdList);
    List<GaWidgetInfo> gaWidgetInfoList = gaWidgetDao.findGaWidgetInfoByWidgetId(widgetIdList);
    AcceptWidget acceptW = this.buildAcceptWidget(widget, variableDBList, gaWidgetInfoList, null,
        null);

    // 处理自定义widget的子widget列表
    if (PtoneWidgetInfo.WIDGET_TYPE_CUSTOM.equals(acceptW.getBaseWidget().getWidgetType())) {
      List<AcceptWidget> children = new ArrayList<AcceptWidget>();

      List<PtoneWidgetInfo> subWidgetList = this.getChildrenWidget(widgetId);
      List<String> subWidgetIdList = new ArrayList<String>();
      for (PtoneWidgetInfo subW : subWidgetList) {
        subWidgetIdList.add(subW.getWidgetId());
      }
      List<PtoneVariableWithWidgetId> subVariableDBList = variableDao
          .findVariableByWidgetId(subWidgetIdList);
      List<GaWidgetInfo> subGaWidgetInfoList = gaWidgetDao
          .findGaWidgetInfoByWidgetId(subWidgetIdList);

      for (PtoneWidgetInfo w : subWidgetList) {
        if (widget.getWidgetId().equals(w.getParentId())) {
          AcceptWidget subAcceptW = this.buildAcceptWidget(w, subVariableDBList,
              subGaWidgetInfoList, null, null);
          children.add(subAcceptW);
        }
      }
      acceptW.setChildren(children);
    }
    return acceptW;
  }

  @Override
  public List<AcceptWidget> findWidget(String pid) {
    List<PtoneWidgetInfo> widgetList = findById(pid);
    List<PtoneVariableWithWidgetId> variableDBList = variableDao.findVariableByPanelId(pid);
    List<GaWidgetInfo> gaWidgetInfoList = gaWidgetDao.findGaWidgetInfo(pid);

    List<AcceptWidget> acceptWidgets = new ArrayList<>();
    if (null != widgetList && !widgetList.isEmpty()) {
      for (int i = 0; i < widgetList.size(); i++) {
        PtoneWidgetInfo widget = widgetList.get(i);
        if (StringUtil.isNotBlank(widget.getParentId())) {
          continue; // 子widget在其父widget中处理
        }
        AcceptWidget acceptW = this.buildAcceptWidget(widget, variableDBList, gaWidgetInfoList,
            null, null);

        // 处理自定义widget的子widget列表
        if (PtoneWidgetInfo.WIDGET_TYPE_CUSTOM.equals(acceptW.getBaseWidget().getWidgetType())) {
          List<AcceptWidget> children = new ArrayList<AcceptWidget>();
          for (PtoneWidgetInfo w : widgetList) {
            if (widget.getWidgetId().equals(w.getParentId())) {
              AcceptWidget subAcceptW = this.buildAcceptWidget(w, variableDBList, gaWidgetInfoList,
                  null, null);
              children.add(subAcceptW);
            }
          }
          acceptW.setChildren(children);
        }
        acceptWidgets.add(acceptW);
      }
    }
    return acceptWidgets;
  }

  private AcceptWidget buildAcceptWidget(PtoneWidgetInfo widget,
      List<PtoneVariableWithWidgetId> variableDBList, List<GaWidgetInfo> gaWidgetInfoList,
      PtoneWidgetChartSetting chartSetting, PtoneWidgetInfoExtend widgetExtend) {
    AcceptWidget acceptW = new AcceptWidget();
    if (null != widget) {
      if (chartSetting == null) {
        chartSetting = widgetChartSettingService.get(widget.getWidgetId());
      }
      PtoneWidgetChartSettingDto chartSettingDto = chartSetting == null ? null
          : new PtoneWidgetChartSettingDto(chartSetting);

      acceptW.setPanelId(widget.getPanelId());
      acceptW.setBaseWidget(widget);
      acceptW.setChartSetting(chartSettingDto);

      String widgetType = widget.getWidgetType();

      // tool图表处理 || 子widget的layout信息处理
      if (PtoneWidgetInfo.WIDGET_TYPE_TOOL.equals(widgetType)
          || StringUtil.isNotBlank(widget.getParentId())) {
        if (widgetExtend == null) {
          widgetExtend = widgetExtendService.get(widget.getWidgetId());
        }
        if (widgetExtend != null) {
          acceptW.setToolData(widgetExtend);
          String layout = widgetExtend.getLayout();
          if (StringUtil.isNotBlank(layout)) {
            acceptW.setLayout(JSON.parseObject(layout));
          }
        }
      }

      // 自定义widget处理
      if (PtoneWidgetInfo.WIDGET_TYPE_CUSTOM.equals(widgetType)
          || PtoneWidgetInfo.WIDGET_TYPE_TOOL.equals(widgetType)) {
        // 兼容前台结构
        List<PtVariables> variables = new ArrayList<PtVariables>();
        variables.add(new PtVariables());
        acceptW.setVariables(variables);
      }

      // chart类型图表处理
      if (PtoneWidgetInfo.WIDGET_TYPE_CHART.equals(widgetType)) {
        List<PtVariables> variables = new ArrayList<>();
        if (null != variableDBList && !variableDBList.isEmpty()) {
          variables = fillVariables(widget.getWidgetId(), variableDBList, gaWidgetInfoList);
          acceptW.setVariables(variables);
        }
      }

      // is demo data
      if (widget.getIsDemo() == Constants.validateInt) {
        if (widgetExtend == null) {
          widgetExtend = widgetExtendService.get(widget.getWidgetId());
        }
        acceptW.setToolData(widgetExtend);
      }

    }
    return acceptW;
  }

  public GaWidgetInfo getGaWidgetInfoByVariableId(String variableId,
      List<GaWidgetInfo> gaWidgetInfoList) {
    GaWidgetInfo gaWidgetInfo = null;
    for (GaWidgetInfo gaWidget : gaWidgetInfoList) {
      if (gaWidget.getVariableId().equals(variableId)) {
        gaWidgetInfo = gaWidget;
      }
    }
    return gaWidgetInfo;
  }

  @Override
  @Transactional
  public void updateBaseWidget(PtoneWidgetInfo ptoneWidgetInfo) {
    widgetDao.update(ptoneWidgetInfo);
  }

  @Override
  @Transactional
  public void softDeletingWidget(String widgetId, boolean isDelete) {
    if (StringUtil.isBlank(widgetId)) {
      return;
    }
    Map<String, Map<String, String>> updateMap = CascadeDeleteUtil.buildParamMap(isDelete);

    Map<String, Object[]> paramMap = buildWidgetIdArray(widgetId);
    // ptone_widget_info 更新
    serviceFactory.getWidgetService().update(paramMap, updateMap.get(CascadeDeleteUtil.STATUS));
    // widget相关信息级联删除
    deleteWidgetCorrelation(paramMap, updateMap);
  }

  @Override
  @Transactional
  public void deleteWidgetCorrelation(Map<String, Object[]> paramMap,
      Map<String, Map<String, String>> updateMap) {
    if (CollectionUtil.isEmpty(paramMap) || CollectionUtil.isEmpty(updateMap)) {
      return;
    }
    Map<String, String> statusMap = updateMap.get(CascadeDeleteUtil.STATUS);

    Map<String, String> deleteMap = updateMap.get(CascadeDeleteUtil.IS_DELETE);

    // ptone_variable_info 更新
    serviceFactory.getVariableService().update(paramMap, statusMap);
    // ga_widget_info 更新
    serviceFactory.getGaWidgetService().update(paramMap, statusMap);
    // ptone_widget_variable
    serviceFactory.getWidgetVariableService().update(paramMap, deleteMap);
    // ptone_widget_chart_setting
    serviceFactory.getWidgetChartSettingService().update(paramMap, deleteMap);
    // ptone_widget_info_extend
    serviceFactory.getWidgetExtendService().update(paramMap, deleteMap);
    // ptone_panel_widget
    serviceFactory.getPanelWidgetService().update(paramMap, deleteMap);
  }

  /**
   * 
   * 根据widgetId去查询子widget， 并将所有的widgetId置入数组，返回Map
   * 
   * @author shaoqiang.guo
   * @date 2016年11月18日 下午4:26:23
   * @param widgetId
   * @return paramMap
   * @see Map
   */
  private Map<String, Object[]> buildWidgetIdArray(String widgetId) {
    Map<String, Object[]> paramMap = new HashMap<>(1);
    List<Object> widgetList = new ArrayList<Object>();
    widgetList.add(widgetId);
    List<PtoneWidgetInfo> widgetIdList = widgetDao.findChildWidgetById(widgetId);
    if (CollectionUtil.isNotEmpty(widgetIdList)) {
      for (PtoneWidgetInfo ptoneWidgetInfo : widgetIdList) {
        widgetList.add(ptoneWidgetInfo.getWidgetId());
      }
    }
    Object[] widgetIdArray = widgetList.toArray();

    paramMap.put("widgetId", widgetIdArray);
    return paramMap;
  }

  public List<PtoneWidgetInfo> getChildrenWidget(String parentId) {
    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("parentId", new Object[] { parentId });
    paramMap.put("status", new Object[] { Constants.validate });
    return widgetDao.findByWhere(paramMap);
  }

  @Override
  public List<PtoneMetricsDimension> getWidgetSelectedMetrics(String widgetId) {
    GaWidgetInfo gaWidgetInfo = gaWidgetDao.get(widgetId);
    List<PtoneMetricsDimension> metricsDataList = new ArrayList<PtoneMetricsDimension>();
    if (gaWidgetInfo != null) {
      String metricsJson = gaWidgetInfo.getMetrics();
      if (StringUtil.isNotBlank(metricsJson)) {
        metricsDataList = JSON.parseArray(metricsJson, PtoneMetricsDimension.class);
        PtoneDsInfo dsInfo = serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById(
            gaWidgetInfo.getDsId());
        this.fixMetricsData(metricsDataList, dsInfo);
      }
    }
    return metricsDataList;
  }

  @Override
  public List<PtoneMetricsDimension> getWidgetSelectedDimensions(String widgetId) {
    GaWidgetInfo gaWidgetInfo = gaWidgetDao.get(widgetId);
    List<PtoneMetricsDimension> dimensionsDataList = new ArrayList<PtoneMetricsDimension>();
    if (gaWidgetInfo != null) {
      String dimensionsJson = gaWidgetInfo.getDimensions();
      if (StringUtil.isNotBlank(dimensionsJson)) {
        dimensionsDataList = JSON.parseArray(dimensionsJson, PtoneMetricsDimension.class);
      }

      PtoneDsInfo dsInfo = serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById(
          gaWidgetInfo.getDsId());
      this.fixDimensions(dimensionsDataList, dsInfo);
    }
    return dimensionsDataList;
  }

  /**
   * 修正根据模板创建的widgetInfo信息
   * 
   * @return
   * @date: 2016年8月16日
   * @author peng.xu
   */
  @Override
  public PtoneWidgetInfo fixWidgetInfoByTemplet(PtoneWidgetInfo baseWidget, String spaceId,
      String panelId, String widgetId, String uid, String localLang, long createTime,
      boolean isTemplet) {
    baseWidget.setSpaceId(spaceId);
    baseWidget.setPanelId(panelId);
    baseWidget.setWidgetId(widgetId);
    baseWidget.setCreateTime(createTime);
    baseWidget.setModifyTime(null);
    baseWidget.setCreatorId(uid);
    baseWidget.setModifierId(uid);
    baseWidget.setOwnerId(uid);
    baseWidget.setStatus(Constants.validate);
    baseWidget.setDescription(null);
    baseWidget.setIsTemplate(Constants.inValidate);

    if (isTemplet) {
      baseWidget.setByTemplate(Constants.validate);
      baseWidget.setIsExample(Constants.validateInt);
      // title按国际化选择对应的文本
      String title = baseWidget.getWidgetTitle();
      title = StringUtil.getValueOfJson(title, localLang, ".*?zh_CN.*?en_US.*?ja_JP.*?");
      if (StringUtil.isNotBlank(title)) {
        baseWidget.setWidgetTitle(title);
      }
    }

    return baseWidget;
  }

  /**
   * 根据模板构建新的gaWidgetInfo信息
   * 
   * @param templetWidgetId
   * @param newWidgetId
   * @param newVariableId
   * @return
   * @date: 2016年7月23日
   * @author peng.xu
   */
  @Override
  public GaWidgetInfo buildGaWidgetInfo(String templetWidgetId, PtoneWidgetInfo baseWidget,
      List<GaWidgetInfo> gaWidgetTempletList, String newWidgetId, String newVariableId,
      String sourceType, boolean isTemplet) {

    GaWidgetInfo templetGaWidgetInfo = null;
    if (CollectionUtil.isNotEmpty(gaWidgetTempletList)) {
      for (GaWidgetInfo templet : gaWidgetTempletList) {
        if (templet != null && templetWidgetId.equals(templet.getWidgetId())) {
          templetGaWidgetInfo = templet;
          break;
        }
      }
    }

    if (templetGaWidgetInfo == null) {
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("widgetId", new Object[] { templetWidgetId });
      templetGaWidgetInfo = gaWidgetDao.getByWhere(paramMap);
    }

    GaWidgetInfo newGaWidgetInfo = new GaWidgetInfo();
    if (templetGaWidgetInfo != null) {
      BeanUtils.copyProperties(templetGaWidgetInfo, newGaWidgetInfo);
      newGaWidgetInfo.setWidgetId(newWidgetId);
      newGaWidgetInfo.setVariableId(newVariableId);
      newGaWidgetInfo.setPanelId(baseWidget.getPanelId());
      newGaWidgetInfo.setUid(baseWidget.getCreatorId());
      if (isTemplet) {
        newGaWidgetInfo.setAccountName("");
        newGaWidgetInfo.setConnectionId("");
        // newGaWidgetInfo.setProfileId(""); //
        // 对于stripe等数据源模板中需要profileId信息，所以保留
        newGaWidgetInfo.setMetrics(this.buildMetrics(templetGaWidgetInfo.getMetrics(), baseWidget,
            sourceType)); // 修正指标（主要是复合指标）
        newGaWidgetInfo.setFilters(this.buildFilters(templetGaWidgetInfo.getFilters(), baseWidget,
            sourceType)); // 修正过滤器（主要是复合指标）
      }
    }
    return newGaWidgetInfo;
  }

  /**
   * 构建指标，对于复合指标给用户重新生成新的复合指标
   * 
   * @param metricsData
   * @return
   * @date: 2016年7月23日
   * @author peng.xu
   */
  private String buildMetrics(String metricsData, PtoneWidgetInfo baseWidget, String sourceType) {
    if (StringUtil.isBlank(metricsData)) {
      return metricsData;
    }
    List<PtoneMetricsDimension> metrics = JSON.parseArray(metricsData, PtoneMetricsDimension.class);
    if (metrics != null) {
      for (PtoneMetricsDimension md : metrics) {
        // 对于复合指标，需要给用户重新生成新的复合指标
        if (PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equalsIgnoreCase(md.getType())) {
          UserCompoundMetricsDimension compoundMetrics = this.buildCompoundMetrics(md.getId(),
              baseWidget, sourceType);
          md.setId(compoundMetrics.getId());
          md.setName(compoundMetrics.getName());
        }
      }
    }
    return JSON.toJSONString(metrics);
  }

  /**
   * 构建过滤器，对于复合指标给用户重新生成新的复合指标
   * 
   * @param filtersData
   * @return
   * @date: 2016年7月23日
   * @author peng.xu
   */
  private String buildFilters(String filtersData, PtoneWidgetInfo baseWidget, String sourceType) {
    if (StringUtil.isBlank(filtersData)) {
      return filtersData;
    }
    SegmentData filters = JSON.parseObject(filtersData, SegmentData.class);
    if (filters != null) {
      if (filters.getNewData() != null) {
        List<DynamicSegmentData> dynamicSegmentDataList = filters.getNewData();
        for (DynamicSegmentData dynamicSegmentData : dynamicSegmentDataList) {
          List<DynamicSegmentCondition> conditionList = dynamicSegmentData.getCondition();
          for (DynamicSegmentCondition condition : conditionList) {
            if (PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equalsIgnoreCase(condition.getType())) {
              UserCompoundMetricsDimension compoundMetrics = this.buildCompoundMetrics(
                  condition.getId(), baseWidget, sourceType);
              condition.setId(compoundMetrics.getId());
              condition.setName(compoundMetrics.getName());
            }
          }
        }
      }
    }
    return JSON.toJSONString(filters);
  }

  /**
   * 根据模板复合指标ID，创建新的复合指标
   * 
   * @param templetMetricsId
   *          模板复合指标ID
   * @return
   * @date: 2016年7月23日
   * @author peng.xu
   */
  private UserCompoundMetricsDimension buildCompoundMetrics(String templetMetricsId,
      PtoneWidgetInfo baseWidget, String sourceType) {
    UserCompoundMetricsDimension newMetrics = null;
    UserCompoundMetricsDimensionService compoundMetricsService = serviceFactory
        .getUserCompoundMetricsDimensionService();
    UserCompoundMetricsDimension templetMetrics = compoundMetricsService.get(templetMetricsId);
    String spaceId = baseWidget.getSpaceId();
    String dsId = templetMetrics.getDsId();

    // 查询用户所在空间下是否已经创建了该复合指标
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("spaceId", new Object[] { spaceId });
    paramMap.put("dsId", new Object[] { dsId });
    paramMap.put("templetId", new Object[] { templetMetricsId });
    paramMap.put("formula", new Object[] { templetMetrics.getFormula() });
    paramMap.put("is_delete", new Object[] { Constants.inValidate });
    newMetrics = compoundMetricsService.getByWhere(paramMap);

    // 如果用户空间下没有该复合指标，则重新创建
    if (newMetrics == null) {
      // 复合指标名称唯一
      String name = compoundMetricsService.getUniqueCompoundMetricsName(templetMetrics.getName(),
          spaceId, dsId);

      // 构建新的复合指标对象
      newMetrics = new UserCompoundMetricsDimensionDto();
      BeanUtils.copyProperties(templetMetrics, newMetrics);
      newMetrics.setId(UuidUtil.generateUuid());
      newMetrics.setName(name);
      newMetrics.setUid(baseWidget.getCreatorId());
      newMetrics.setCreatorId(baseWidget.getCreatorId());
      newMetrics.setModifierId(baseWidget.getCreatorId());
      newMetrics.setSpaceId(baseWidget.getSpaceId());
      newMetrics.setTempletId(templetMetricsId);
      newMetrics.setSourceType(sourceType);
      compoundMetricsService.addUserCompoundMetricsDimension(new UserCompoundMetricsDimensionDto(
          newMetrics));
    }
    return newMetrics;
  }

  @Override
  public PtoneWidgetInfoExtend saveWidgetExtendByTemplet(String templetWidgetId,
      String newWidgetId, List<PtoneWidgetInfoExtend> widgetExtendTempletList) {
    PtoneWidgetInfoExtend widgetExtend = null;
    if (CollectionUtil.isNotEmpty(widgetExtendTempletList)) {
      for (PtoneWidgetInfoExtend templet : widgetExtendTempletList) {
        if (templet != null && templetWidgetId.equals(templet.getWidgetId())) {
          widgetExtend = templet;
          break;
        }
      }
    }

    if (widgetExtend == null) {
      widgetExtend = widgetExtendService.get(templetWidgetId);
    }

    if (widgetExtend != null) {
      widgetExtend.setWidgetId(newWidgetId);
      widgetExtendService.save(widgetExtend);
    }
    return widgetExtend;
  }

  @Override
  public PtoneWidgetChartSetting saveWidgetChartSettingByTemplet(String templetWidgetId,
      String newWidgetId, List<PtoneWidgetChartSetting> chartSettingTempletList) {

    PtoneWidgetChartSetting chartSetting = null;
    if (CollectionUtil.isNotEmpty(chartSettingTempletList)) {
      for (PtoneWidgetChartSetting templet : chartSettingTempletList) {
        if (templet != null && templetWidgetId.equals(templet.getWidgetId())) {
          chartSetting = templet;
          break;
        }
      }
    }

    if (chartSetting == null) {
      chartSetting = widgetChartSettingService.get(templetWidgetId);
    }

    if (chartSetting != null) {
      chartSetting.setWidgetId(newWidgetId);
      widgetChartSettingService.saveOrUpdate(chartSetting);
    }
    return chartSetting;
  }

  @Override
  public PtoneVariableInfo saveVariableInfoByTemplet(String templetWidgetId,
      PtoneWidgetInfo baseWidget) {
    // 找widget和variable的关系
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("widgetId", new Object[] { templetWidgetId });
    PtoneWidgetVariable widgetVariable = widgetVariableDao.getByWhere(paramMap);

    // 根据variableId 找到 variable 信息
    paramMap = new HashMap<>();
    paramMap.put("variableId", new Object[] { widgetVariable.getVariableId() });
    PtoneVariableInfo templetVariableInfo = variableDao.getByWhere(paramMap);

    PtoneVariableInfo newVariableInfo = new PtoneVariableInfo();
    BeanUtils.copyProperties(templetVariableInfo, newVariableInfo);
    // 设置newVariableInfo自己的属性
    String newVariableId = UUID.randomUUID().toString();
    newVariableInfo.setVariableId(newVariableId);
    newVariableInfo.setWidgetId(baseWidget.getWidgetId());
    newVariableInfo.setPanelId(baseWidget.getPanelId());

    variableDao.save(newVariableInfo);
    return newVariableInfo;
  }

  @Override
  public PtonePanelWidget savePanelWidgetRelation(String panelId, String widgeId) {
    PtonePanelWidget newPanelWidgetRelation = new PtonePanelWidget();
    newPanelWidgetRelation.setPanelId(panelId);
    newPanelWidgetRelation.setWidgetId(widgeId);
    serviceFactory.getPanelWidgetDao().save(newPanelWidgetRelation);
    return newPanelWidgetRelation;
  }

  @Override
  public PtoneWidgetVariable saveWidgetVariableRelation(String widgetId, String variableId) {
    PtoneWidgetVariable newWidgetVariable = new PtoneWidgetVariable();
    newWidgetVariable.setWidgetId(widgetId);
    newWidgetVariable.setVariableId(variableId);
    widgetVariableDao.save(newWidgetVariable);
    return newWidgetVariable;
  }

  @Override
  public boolean updateMetricsAlias(MetricsDimensionsAliasVo aliasVo) {
    String fieldContent = "";
    boolean result = false;
    GaWidgetInfo gaWidgetInfo = serviceFactory.getGaWidgetService().get(aliasVo.getWidgetId());
    if (null == gaWidgetInfo) {
      logger.warn("not find the GaWidgetInfo by widgetId:" + aliasVo.getWidgetId());
      return result;
    }
    if (aliasVo.getType().equals("metrics")) {
      fieldContent = gaWidgetInfo.getMetrics();
    } else if (aliasVo.getType().equals("dimensions")) {
      fieldContent = gaWidgetInfo.getDimensions();
    }
    if (StringUtil.isNotBlank(fieldContent)) {
      String uuid = aliasVo.getUuid();
      List<PtoneMetricsDimension> metricsDimensionList = JSONArray.parseArray(fieldContent,
          PtoneMetricsDimension.class);
      if (CollectionUtil.isNotEmpty(metricsDimensionList)) {
        for (int i = 0; i < metricsDimensionList.size(); i++) {
          PtoneMetricsDimension metricsDimension = metricsDimensionList.get(i);
          if (StringUtil.isNotBlank(metricsDimension.getUuid())
              && metricsDimension.getUuid().equals(uuid)) {
            metricsDimension.setAlias(aliasVo.getAlias() == null ? "" : aliasVo.getAlias());
            fieldContent = JSONArray.toJSONString(metricsDimensionList);
            GaWidgetInfo updateGaWidgetInfo = new GaWidgetInfo();
            updateGaWidgetInfo.setVariableId(gaWidgetInfo.getVariableId());
            if (aliasVo.getType().equals("metrics")) {
              updateGaWidgetInfo.setMetrics(fieldContent);
            } else if (aliasVo.getType().equals("dimensions")) {
              updateGaWidgetInfo.setDimensions(fieldContent);
            }
            serviceFactory.getGaWidgetService().update(updateGaWidgetInfo);
            result = true;
            break;
          }
        }
      }
    }
    return result;
  }

  @Override
  public void updateMetricsAndAlias(MetricsDimensionsAliasVo aliasVo) throws BusinessException {
    boolean result = updateMetricsAlias(aliasVo);
    if (!result) {
      throw new BusinessException(BusinessErrorCode.Widget.UPDATE_METRICS_DIMENSION_ALIAS_ERROR,
          "update Metrics or Dimension Alias failed.");
    }
  }

  @Override
  public void saveBaseWidget(PtoneWidgetInfo widget) {
    widgetDao.save(widget);
  }

  @Override
  public List<AcceptWidget> addWidgetByTemplet(List<String> templetIdList, String spaceId,
      String panelId, String uid, String isPreview) {
    // TODO Auto-generated method stub
    return null;
  }

}
