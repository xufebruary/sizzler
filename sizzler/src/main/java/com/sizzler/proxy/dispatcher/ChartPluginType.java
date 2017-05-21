package com.sizzler.proxy.dispatcher;

public enum ChartPluginType {

  HIGHCHARTS("Highcharts");

  private String name;

  ChartPluginType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

}
