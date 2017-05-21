package com.sizzler.domain.ds.vo;

import java.io.Serializable;
import java.util.List;

import com.sizzler.domain.ds.dto.PtoneMetricsDimension;

public class DimensionVo extends PtoneMetricsDimension implements Serializable {

  private static final long serialVersionUID = -6835582222330219759L;

  private Boolean isLeaf = false; // 默认为false
  private List<DimensionVo> children;


  public Boolean getIsLeaf() {
    return isLeaf;
  }

  public void setIsLeaf(Boolean isLeaf) {
    this.isLeaf = isLeaf;
  }

  public List<DimensionVo> getChildren() {
    return children;
  }

  public void setChildren(List<DimensionVo> children) {
    this.children = children;
  }

}
