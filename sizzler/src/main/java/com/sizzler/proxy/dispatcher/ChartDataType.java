package com.sizzler.proxy.dispatcher;

public enum ChartDataType {

  LINE("Line"), PIE("Pie"), SIMPLENUMBER("SimpleNumber"), QOQNUMBER("QoqNumber"), // 环比number类型
  TABLE("Table"); // table 和 map使用相同的数据结构，返回数据在前端做相应处理

  private String name;

  ChartDataType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

}
