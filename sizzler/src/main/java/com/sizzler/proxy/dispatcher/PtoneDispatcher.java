package com.sizzler.proxy.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

import com.sizzler.common.utils.SpringContextUtil;

public class PtoneDispatcher {

  private Logger log = LoggerFactory.getLogger(PtoneDispatcher.class);

  private static PtoneDispatcher dispatcher;

  private Map<String, PtoneDatasourceGraphHandler> ptoneDatasourceGraphHandlers; // 数据源对应图形的处理类映射关系
  private Map<String, PtoneChartPluginGraphHandler> ptoneChartPluginGraphHandlers; // 图表插件对应图形的处理类映射关系
  private Map<String, PtoneDatasourceHandler> ptoneDatasourceHandlers;// 数据源对应处理类映射关系
  private Map<String, PtoneChartPluginHandler> ptoneChartPluginHandlers; // 图表插件对应处理类映射关系
  private Map<String, PtoneGraphWidgetDataHandler> ptoneGraphWidgetDataHandlers; // widget数据对应图表的格式化处理类映射关系
  private Map<String, PtoneGraphVariableDataHandler> ptoneGraphVariableDataHandlers; // variable数据对应图表的格式化处理类映射关系(数据请求数据返回后，公共处理handler)

  private WebApplicationContext wac = null;
  private String handlerSuffix = "Handler";

  public static PtoneDispatcher getInstance() {
    if (dispatcher == null) {
      dispatcher = new PtoneDispatcher();
      // dispatcher.initRegister(); // 修改为在第一次调用到handler时初始化
    }
    return dispatcher;
  }

  private PtoneDispatcher() {
    this.ptoneDatasourceGraphHandlers = new HashMap<String, PtoneDatasourceGraphHandler>();
    this.ptoneChartPluginGraphHandlers = new HashMap<String, PtoneChartPluginGraphHandler>();
    this.ptoneDatasourceHandlers = new HashMap<String, PtoneDatasourceHandler>();
    this.ptoneChartPluginHandlers = new HashMap<String, PtoneChartPluginHandler>();
    this.ptoneGraphWidgetDataHandlers = new HashMap<String, PtoneGraphWidgetDataHandler>();
    this.ptoneGraphVariableDataHandlers = new HashMap<String, PtoneGraphVariableDataHandler>();
  }

  private Object loadHandler(String beanName) {
    beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1); // 修正beanName的首字母为小写
    Object handler = null;
    try {
      handler = SpringContextUtil.getBean(beanName);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    if (handler != null) {
      log.info(">>> Load Handler: " + beanName);
    } else {
      log.info(">>> Not exists Handler: " + beanName);
    }
    return handler;
  }

  /**
   * Description: 注册不同数据源对应图表类型的 handler<br>
   */
  public void register(String key, PtoneDatasourceGraphHandler ptoneDatasourceGraphHandler) {
    if (ptoneDatasourceGraphHandler != null) {
      ptoneDatasourceGraphHandlers.put(key, ptoneDatasourceGraphHandler);
    }
  }

  /**
   * Description: 注册不同数据源对应图表类型的 handler<br>
   */
  public void register(String key, PtoneChartPluginGraphHandler ptoneChartPluginGraphHandler) {
    if (ptoneChartPluginGraphHandler != null) {
      ptoneChartPluginGraphHandlers.put(key, ptoneChartPluginGraphHandler);
    }
  }

  /**
   * Description: 注册不同数据源对应的 handler<br>
   */
  public void register(String key, PtoneDatasourceHandler ptoneDatasourceHandler) {
    if (ptoneDatasourceHandler != null) {
      ptoneDatasourceHandlers.put(key, ptoneDatasourceHandler);
    }
  }

  /**
   * Description: 注册不同图表插件对应的 handler<br>
   */
  public void register(String key, PtoneChartPluginHandler ptoneChartPluginHandler) {
    if (ptoneChartPluginHandler != null) {
      ptoneChartPluginHandlers.put(key, ptoneChartPluginHandler);
    }
  }

  /**
   * Description: 注册不同图表类型对应的widget数据处理 handler<br>
   */
  public void register(String key, PtoneGraphWidgetDataHandler ptoneGraphWidgetDataHandler) {
    if (ptoneGraphWidgetDataHandler != null) {
      ptoneGraphWidgetDataHandlers.put(key, ptoneGraphWidgetDataHandler);
    }
  }

  /**
   * Description: 注册不同图表类型对应的variable数据处理 handler<br>
   */
  public void register(String key, PtoneGraphVariableDataHandler ptoneGraphVariableDataHandler) {
    if (ptoneGraphVariableDataHandler != null) {
      ptoneGraphVariableDataHandlers.put(key, ptoneGraphVariableDataHandler);
    }
  }

  /**
   * Description: 触发不同数据源对应图表类型的 handler<br>
   */
  public List<PtoneVariableData> dispatch(PtoneDatasourceGraphDesc ptoneDatasourceGraphDesc) {
    String key = ptoneDatasourceGraphDesc.getKey();
    PtoneDatasourceGraphHandler ptoneHandler = null;
    if (ptoneDatasourceGraphHandlers.containsKey(key)) {
      ptoneHandler = ptoneDatasourceGraphHandlers.get(key);
    } else {
      String beanName = key + handlerSuffix;
      ptoneHandler = (PtoneDatasourceGraphHandler<?>) this.loadHandler(beanName);
      this.register(key, ptoneHandler);
    }

    if (ptoneHandler != null) {
      return ptoneHandler.handle(ptoneDatasourceGraphDesc);
    } else {
      return new ArrayList<PtoneVariableData>();
    }
  }

  /**
   * Description: 触发不同图表控件对应图表类型的 handler<br>
   */
  @Deprecated
  public PtoneVariableChartData dispatch(PtoneChartPluginGraphDesc ptoneChartPluginGraphDesc) {
    String key = ptoneChartPluginGraphDesc.getKey();
    PtoneChartPluginGraphHandler ptoneChartPluginGraphHandler = null;
    if (ptoneChartPluginGraphHandlers.containsKey(key)) {
      ptoneChartPluginGraphHandler = ptoneChartPluginGraphHandlers.get(key);
    } else {
      String beanName = key + handlerSuffix;
      ptoneChartPluginGraphHandler = (PtoneChartPluginGraphHandler<?>) this.loadHandler(beanName);
      this.register(key, ptoneChartPluginGraphHandler);
    }

    if (ptoneChartPluginGraphHandler != null) {
      return ptoneChartPluginGraphHandler.handle(ptoneChartPluginGraphDesc);
    } else {
      return null;
    }
  }

  /**
   * Description: 触发不同数据源对应的 handler<br>
   */
  public List<PtoneVariableData> dispatch(PtoneDatasourceDesc ptoneDatasourceDesc) {
    String key = ptoneDatasourceDesc.getKey();
    PtoneDatasourceHandler ptoneDatasourceHandler = null;
    if (ptoneDatasourceHandlers.containsKey(key)) {
      ptoneDatasourceHandler = ptoneDatasourceHandlers.get(key);
    } else {
      String beanName = key + handlerSuffix;
      ptoneDatasourceHandler = (PtoneDatasourceHandler<?>) this.loadHandler(beanName);
      this.register(key, ptoneDatasourceHandler);
    }

    if (ptoneDatasourceHandler != null) {
      return ptoneDatasourceHandler.handle(ptoneDatasourceDesc);
    } else {
      return new ArrayList<PtoneVariableData>();
    }
  }

  /**
   * Description: 触发不同图表控件对应的 handler<br>
   */
  @Deprecated
  public PtoneWidgetChartData dispatch(PtoneChartPluginDesc ptoneChartPluginDesc) {
    String key = ptoneChartPluginDesc.getKey();
    PtoneChartPluginHandler ptonePluginHandler = null;
    if (ptoneChartPluginHandlers.containsKey(key)) {
      ptonePluginHandler = ptoneChartPluginHandlers.get(key);
    } else {
      String beanName = key + handlerSuffix;
      ptonePluginHandler = (PtoneChartPluginHandler<?>) this.loadHandler(beanName);
      this.register(key, ptonePluginHandler);
    }

    if (ptonePluginHandler != null) {
      return ptonePluginHandler.handle(ptoneChartPluginDesc);
    } else {
      return null;
    }
  }

  /**
   * Description: 触发不同图表类型对应的widget数据处理 handler<br>
   */
  public PtoneWidgetData dispatch(PtoneGraphWidgetDataDesc ptoneGraphWidgetDataDesc) {
    String key = ptoneGraphWidgetDataDesc.getKey();
    PtoneGraphWidgetDataHandler ptoneGraphWidgetDataHandler = ptoneGraphWidgetDataHandlers.get(key);
    if (ptoneGraphWidgetDataHandlers.containsKey(key)) {
      ptoneGraphWidgetDataHandler = ptoneGraphWidgetDataHandlers.get(key);
    } else {
      String beanName = "ptone" + key + "WidgetData" + handlerSuffix;
      ptoneGraphWidgetDataHandler = (PtoneGraphWidgetDataHandler<?>) this.loadHandler(beanName);
      this.register(key, ptoneGraphWidgetDataHandler);
    }

    if (ptoneGraphWidgetDataHandler != null) {
      return ptoneGraphWidgetDataHandler.handle(ptoneGraphWidgetDataDesc);
    } else {
      return null;
    }
  }

  /**
   * Description: 触发不同图表类型对应的variable数据处理 handler<br>
   */
  public List<PtoneVariableData> dispatch(PtoneGraphVariableDataDesc ptoneGraphVariableDataDesc) {
    String key = ptoneGraphVariableDataDesc.getKey();
    PtoneGraphVariableDataHandler ptoneGraphVariableDataHandler = ptoneGraphVariableDataHandlers
        .get(key);
    if (ptoneGraphVariableDataHandlers.containsKey(key)) {
      ptoneGraphVariableDataHandler = ptoneGraphVariableDataHandlers.get(key);
    } else {
      String beanName = "ptone" + key + "VariableData" + handlerSuffix;
      ptoneGraphVariableDataHandler = (PtoneGraphVariableDataHandler<?>) this.loadHandler(beanName);
      this.register(key, ptoneGraphVariableDataHandler);
    }

    if (ptoneGraphVariableDataHandler != null) {
      return ptoneGraphVariableDataHandler.handle(ptoneGraphVariableDataDesc);
    } else {
      return null;
    }
  }
}
