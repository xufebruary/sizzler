package com.sizzler.domain.widget.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态Segment数据
 * 
 * @author peng.xu
 */
public class DynamicSegmentData implements Serializable {

  private static final long serialVersionUID = 3158563737902989180L;

  private boolean onlyShow;
  private String type; // User || session
  private List<DynamicSegmentCondition> condition = new ArrayList<DynamicSegmentCondition>();

  public boolean isOnlyShow() {
    return onlyShow;
  }

  public void setOnlyShow(boolean onlyShow) {
    this.onlyShow = onlyShow;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<DynamicSegmentCondition> getCondition() {
    return condition;
  }

  public void setCondition(List<DynamicSegmentCondition> condition) {
    this.condition = condition;
  }

}
