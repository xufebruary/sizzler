package com.sizzler.proxy.dispatcher;

public interface PtoneChartPluginGraphHandler<T extends PtoneChartPluginGraphDesc> {

  public PtoneVariableChartData handle(T ptoneChartPluginGraphDesc);

}
