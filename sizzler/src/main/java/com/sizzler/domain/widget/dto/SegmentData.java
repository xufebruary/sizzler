package com.sizzler.domain.widget.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sizzler.common.utils.StringUtil;


/**
 * Segment数据
 */
public class SegmentData implements Serializable {

  private static final long serialVersionUID = -6340840097539183520L;

  public static final String TYPE_SAVED = "saved"; // segment 类型：已保存的segment
  public static final String TYPE_NEW = "new"; // segment 类型： 新建的segment

  private String metricsId;
  private String type; // saved || new
  private List<String> savedData = new ArrayList<String>(); // [segmentId]
  private List<DynamicSegmentData> newData = new ArrayList<DynamicSegmentData>();

  public String getMetricsId() {
    return metricsId;
  }

  public void setMetricsId(String metricsId) {
    this.metricsId = metricsId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<String> getSavedData() {
    return savedData;
  }

  public void setSavedData(List<String> savedData) {
    this.savedData = savedData;
  }

  public List<DynamicSegmentData> getNewData() {
    return newData;
  }

  public void setNewData(List<DynamicSegmentData> newData) {
    this.newData = newData;
  }
  
  public static String parseSegmenDataToStr(SegmentData segment) {
    String segmentStr = "";
    if (segment != null) {
      String segmentType = segment.getType();
      if (SegmentData.TYPE_SAVED.equalsIgnoreCase(segmentType)) { // 已有的Segment：直接使用segmentId
        segmentStr = StringUtil.join(segment.getSavedData(), ",");
      } else if (SegmentData.TYPE_NEW.equalsIgnoreCase(segmentType)) { // DynamicSegment：解析生成相应的condition
        List<DynamicSegmentData> dynamicSegmentDataList = segment.getNewData();
        StringBuilder segmentSb = new StringBuilder("");
        for (DynamicSegmentData dynamicSegmentData : dynamicSegmentDataList) {
          List<DynamicSegmentCondition> conditionList = dynamicSegmentData.getCondition();
          // fix最后一个condition条件的关系符为空
          if (conditionList.size() > 0) {
            conditionList.get(conditionList.size() - 1).setRel("");
          }
          if (dynamicSegmentData.isOnlyShow()) {
            for (DynamicSegmentCondition condition : conditionList) {
              String name = condition.getName();
              String op = condition.getOp();
              String value = condition.getValue();
              String rel = condition.getRel();
              segmentSb.append(name).append(" ").append(op).append(" ").append(value).append(" ")
                  .append(rel).append(" ");
            }
          }
        }
        segmentStr = segmentSb.toString();
      }
    }
    return segmentStr;
  }

}
