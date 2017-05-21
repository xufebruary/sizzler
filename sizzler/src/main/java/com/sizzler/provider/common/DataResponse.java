package com.sizzler.provider.common;

import java.util.List;

import org.apache.metamodel.data.Row;

public interface DataResponse extends Schemable {

  public List<Row> getRowList();

  public Row getTotalRow();

  public List<String> getRowColumnList();

  @SuppressWarnings("rawtypes")
  public List<List> getList();

  public List<Object> getTotalRowList();

  public boolean isDetail();

}
