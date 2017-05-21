package com.sizzler.provider.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.MutableSchema;

import com.sizzler.provider.common.DataResponse;

/**
 * Created by ptmind on 2015/12/12.
 */
public class DefaultDataResponse implements DataResponse {

  private boolean isEmpty;
  private MutableSchema schema;
  private List<Row> rowList;
  private List<List> objetRowList;
  private Row totalRow;
  private List<Object> totalRowList;
  private List<String> objectRowColumnList = new ArrayList<String>();
  private boolean isDetail;

  @Override
  public List<Row> getRowList() {
    return this.rowList;
  }

  @Override
  public Row getTotalRow() {
    return this.totalRow;
  }

  @Override
  public MutableSchema getSchema() {
    return this.schema;
  }

  public void setEmpty(boolean isEmpty) {
    this.isEmpty = isEmpty;
  }

  public void setSchema(MutableSchema schema) {
    this.schema = schema;
  }

  public void setRowList(List<Row> rowList) {
    this.rowList = rowList;
  }

  public boolean isEmpty() {
    return isEmpty;
  }

  public List<String> getRowColumnList() {
    return objectRowColumnList;
  }

  public void setTotalRow(Row totalRow) {
    this.totalRow = totalRow;
  }

  public List<String> getObjectRowColumnList() {
    return objectRowColumnList;
  }

  public void setObjectRowColumnList(List<String> objectRowColumnList) {
    this.objectRowColumnList = objectRowColumnList;
  }

  public void setDetail(boolean isDetail) {
    this.isDetail = isDetail;
  }

  @Override
  public boolean isDetail() {
    return isDetail;
  }

  @Override
  public List<List> getList() {
    if (objetRowList != null) {
      return objetRowList;
    } else {
      if (rowList != null) {
        objetRowList = new ArrayList<>();
        for (Row row : rowList) {
          List<Object> columnList = new ArrayList<>();
          for (Object o : row.getValues()) {
            if (o != null) {
              columnList.add(o.toString());
            } else {
              columnList.add("");
            }

          }
          // objetRowList.add(Arrays.asList(row.getValues().toString()));
          objetRowList.add(columnList);
        }

        return objetRowList;
      }
    }
    return new ArrayList<>();
  }

  @Override
  public List<Object> getTotalRowList() {
    if (totalRowList != null) {
      return totalRowList;
    } else {
      if (totalRow != null) {
        totalRowList = new ArrayList<Object>();

        for (int i = 0; i < totalRow.getSelectItems().length; i++) {
          Object columnValue = totalRow.getValue(i);
          totalRowList.add(columnValue);
        }
        return totalRowList;
      }
    }
    return new ArrayList<Object>();
  }

  public void setTotalRowList(List<Object> totalRowList) {
    this.totalRowList = totalRowList;
  }

  public void setObjetRowList(List<List> objetRowList) {
    this.objetRowList = objetRowList;
  }

}
