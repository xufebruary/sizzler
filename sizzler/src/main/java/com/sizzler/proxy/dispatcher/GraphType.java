package com.sizzler.proxy.dispatcher;

public enum GraphType {

  LINE("Line"), AREA("Area"), // 废弃
  AREASPLINE("Areaspline"), COLUMN("Column"), BAR("Bar"), PIE("Pie"), HOLLOWPIE("Hollowpie"), // 废弃
  SIMPLENUMBER("Simplenumber"), // 废弃
  NUMBER("Number"), CIRCLEPERCENT("Circlepercent"), // 废弃
  PROGRESSBAR("Progressbar"), TABLE("Table"), MAP("Map");

  private String name;

  GraphType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  public static boolean isHighchartsGraph(GraphType graph) {
    return GraphType.LINE.equals(graph) || GraphType.AREA.equals(graph)
        || GraphType.AREASPLINE.equals(graph) || GraphType.COLUMN.equals(graph)
        || GraphType.BAR.equals(graph) || GraphType.PIE.equals(graph);
  }

}
