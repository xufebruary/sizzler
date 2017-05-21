package com.sizzler.proxy.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelData implements Serializable {

  private static final long serialVersionUID = 3739861877323137555L;

  private List<List> objetRowList = new ArrayList<List>();
  private List<Object> totalRowList = new ArrayList<Object>();
  private List<String> objectRowColumnList = new ArrayList<String>();

  // fastJSON 对于 boolean 类型的 isDetail() 和 setDetail()方法无法转换，需要增加 getIsDetail() 、setIsDetail()
  private boolean isDetail; // 返回的modelData是否为明细数据(部分数据源复合指标需要使用明细数据来进行运算)

  public List<List> getObjetRowList() {
    return objetRowList;
  }

  public void setObjetRowList(List<List> objetRowList) {
    this.objetRowList = objetRowList;
  }

  public List<Object> getTotalRowList() {
    return totalRowList;
  }

  public void setTotalRowList(List<Object> totalRowList) {
    this.totalRowList = totalRowList;
  }

  public List<String> getObjectRowColumnList() {
    return objectRowColumnList;
  }

  public void setObjectRowColumnList(List<String> objectRowColumnList) {
    this.objectRowColumnList = objectRowColumnList;
  }

  public boolean isDetail() {
    return isDetail;
  }

  public void setDetail(boolean isDetail) {
    this.isDetail = isDetail;
  }

  public boolean getIsDetail() {
    return isDetail;
  }

  public void setIsDetail(boolean isDetail) {
    this.isDetail = isDetail;
  }

}
