package com.sizzler.proxy.dispatcher;

public interface PtoneChartPluginHandler<T extends PtoneChartPluginDesc> {

  public PtoneWidgetChartData handle(T ptoneChartPluginDesc);

}
