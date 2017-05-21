package com.sizzler.provider.common;

import java.util.List;

public class PtoneDataTable extends PtoneEntity {

  private static final long serialVersionUID = -3206082435999259253L;

  private List<PtoneColumnHeaders> columnHeaders;

  /**
   * Result data rows, where each row contains a list of dimension values
   * followed by the metric values. The order of dimensions and metrics is same
   * as specified in the request. The value may be {@code null}.
   */
  private List<List<Object>> rows;

  public List<List<Object>> getRows() {
    return rows;
  }

  public void setRows(List<List<Object>> rows) {
    this.rows = rows;
  }

  public List<PtoneColumnHeaders> getColumnHeaders() {
    return columnHeaders;
  }

  public void setColumnHeaders(List<PtoneColumnHeaders> columnHeaders) {
    this.columnHeaders = columnHeaders;
  }

}
