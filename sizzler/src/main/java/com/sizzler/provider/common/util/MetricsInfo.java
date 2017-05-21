package com.sizzler.provider.common.util;

/**
 * Created by ptmind on 2015/11/6.
 */
public class MetricsInfo {
  private String dataType;
  private boolean withPercentChar;

  public boolean isWithPercentChar() {
    return withPercentChar;
  }

  public void setWithPercentChar(boolean withPercentChar) {
    this.withPercentChar = withPercentChar;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }
}
