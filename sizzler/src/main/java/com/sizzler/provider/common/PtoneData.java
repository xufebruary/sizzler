package com.sizzler.provider.common;

import java.util.List;
import java.util.Map;

/**
 * Created by ptmind on 2015/10/21.
 */
public class PtoneData {

  private List<PtoneColumnHeaders> columnHeaders;
  /**
   * Result data rows, where each row contains a list of dimension values followed by the metric
   * values. The order of dimensions and metrics is same as specified in the request. The value may
   * be {@code null}.
   */
  private List<List<String>> rows;

  /**
   * Total values for the requested metrics over all the results, not just the results returned in
   * this response. The order of the metric totals is same as the metric order specified in the
   * request. The value may be {@code null}.
   */
  private Map<String, String> totalsForAllResults;

  public List<PtoneColumnHeaders> getColumnHeaders() {
    return columnHeaders;
  }

  public void setColumnHeaders(List<PtoneColumnHeaders> columnHeaders) {
    this.columnHeaders = columnHeaders;
  }

  public List<List<String>> getRows() {
    return rows;
  }

  public void setRows(List<List<String>> rows) {
    this.rows = rows;
  }

  public Map<String, String> getTotalsForAllResults() {
    return totalsForAllResults;
  }

  public void setTotalsForAllResults(Map<String, String> totalsForAllResults) {
    this.totalsForAllResults = totalsForAllResults;
  }
}
