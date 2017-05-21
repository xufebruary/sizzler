package com.sizzler.provider.common.util;

/**
 * Created by ptmind on 2015/10/22.
 */
public enum DataType {

  PERCENT("PERCENT"), CURRENCY("CURRENCY"), LOCATION_COUNTRY("LOCATION_COUNTRY"), LOCATION_REGION(
      "LOCATION_REGION"), LOCATION_CITY("LOCATION_CITY"), TEXT("TEXT"), DATE("DATE"), DATETIME(
      "DATETIME"), TIME("TIME"), INTEGER("INTEGER"), FLOAT("FLOAT"), LONG("LONG"), DOUBLE("DOUBLE"), NUMBER(
      "NUMBER"), DURATION("DURATION"), TIMESTAMP("TIMESTAMP");
  private String name;

  DataType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
