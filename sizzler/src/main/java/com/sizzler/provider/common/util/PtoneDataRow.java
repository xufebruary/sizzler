package com.sizzler.provider.common.util;

import java.util.Comparator;
import java.util.List;

/**
 * Created by ptmind on 2015/10/23.
 */
public class PtoneDataRow implements Comparable {

  private List<String> row;
  private String date;


  public List<String> getRow() {
    return row;
  }

  public void setRow(List<String> row) {
    this.row = row;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  @Override
  // 小于-1，大于1，等于0
  public int compareTo(Object o) {
    PtoneDataRow otherRow = (PtoneDataRow) o;
    return this.getDate().compareTo(otherRow.getDate());
  }



}
