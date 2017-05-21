package com.sizzler.provider.common;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ptmind on 2015/10/22.
 */
public class PtoneResponseData implements Serializable {

  private static final long serialVersionUID = -5898205990885619541L;

  // protected PtoneQueryRequest ptoneQueryRequest;
  protected PtoneDataTable dataTable;
  // protected List<List<Object>> rows;
  protected Map<String, Object> totals;

  /*
   * public PtoneQueryRequest getPtoneQueryRequest() { return ptoneQueryRequest; }
   * 
   * public void setPtoneQueryRequest(PtoneQueryRequest ptoneQueryRequest) { this.ptoneQueryRequest
   * = ptoneQueryRequest; }
   */

  /*
   * public List<List<Object>> getRows() { return rows; }
   * 
   * public void setRows(List<List<Object>> rows) { this.rows = rows; }
   */

  public PtoneDataTable getDataTable() {
    return dataTable;
  }

  public void setDataTable(PtoneDataTable dataTable) {
    this.dataTable = dataTable;
  }

  public Map<String, Object> getTotals() {
    return totals;
  }

  public void setTotals(Map<String, Object> totals) {
    this.totals = totals;
  }
}
