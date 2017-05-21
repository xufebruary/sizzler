package com.sizzler.provider.common;

/**
 * Created by ptmind on 2015/10/21.
 */
public class PtoneColumnHeaders {
  /**
   * Column Type. Either DIMENSION or METRIC. The value may be {@code null}.
   */
  private String columnType;
  /**
   * Data type. Dimension column headers have only STRING as the data type. Metric column headers
   * have data types for metric values such as INTEGER, DOUBLE, CURRENCY etc. The value may be
   * {@code null}.
   */
  private String dataType;
  /**
   * Column name. The value may be {@code null}.
   */
  private String name;

  public String getColumnType() {
    return columnType;
  }

  public void setColumnType(String columnType) {
    this.columnType = columnType;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
