package com.sizzler.domain.space;

import java.io.Serializable;

import com.sizzler.common.sizzler.PtoneDateUtil;
import com.sizzler.dexcoder.annotation.PK;
import com.sizzler.domain.basic.PtoneBasicDictItem;

public class PtoneSpaceInfo implements Serializable {

  private static final long serialVersionUID = 758307086643343348L;

  public static final String SPACE_STATUS_DELETE = "spaceDelete";
  public static final String SPACE_STATUS_NOT_IN = "spaceNotIn"; // 不在空间中
  public static final String SRC_SPACE_STATUS_DELETE = "srcSpaceDelete";
  public static final String TARGET_SPACE_STATUS_DELETE = "targetSpaceDelete";
  
  public static final String SPACE_STATUS_NOT_EXIST = "spaceNotExists"; // 空间不存在
  public static final String SPACE_STATUS_NO_AUTH = "spaceNoAuth"; // 没有权限访问空间
  public static final String SPACE_STATUS_AVAILABLE = "available"; // 空间有效且有权限访问
  

  @PK
  private String spaceId;
  private String name;
  private String domain;
  private String logo;
  private String description;
  private String ownerId;
  private String ownerEmail;
  private String creatorId;
  private Long createTime;
  private String modifierId;
  private Long modifyTime;
  private String weekStart;
  private Integer isDelete;
  private String spaceType;


  /**
   * 根据空间设置的周起始日获取PtoneDateUtil对应的周起始日信息
   * @param spaceInfo
   * @return
   * @date: 2016年9月23日
   * @author peng.xu
   */
  public static int getSpaceWeekStartSetting(PtoneSpaceInfo spaceInfo) {
    String weekStart = "";
    if (spaceInfo != null) {
      weekStart = spaceInfo.getWeekStart();
    }
    return PtoneSpaceInfo.getSpaceWeekStartSetting(weekStart);
  }

  /**
   * 根据空间设置的周起始日获取PtoneDateUtil对应的周起始日信息
   * @param weekStart
   * @return
   * @date: 2016年9月23日
   * @author peng.xu
   */
  public static int getSpaceWeekStartSetting(String weekStart) {
    int weekStartDay = PtoneDateUtil.FIRST_DAY_OF_WEEK_SUNDAY; // 默认为周日
    // 字典表中：sunday：周日，monday：周一
    if (PtoneBasicDictItem.DICT_CODE_WEEKSTART_MONDAY.equalsIgnoreCase(weekStart)) {
      weekStartDay = PtoneDateUtil.FIRST_DAY_OF_WEEK_MONDAY;
    }
    return weekStartDay;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getLogo() {
    return logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerEmail() {
    return ownerEmail;
  }

  public void setOwnerEmail(String ownerEmail) {
    this.ownerEmail = ownerEmail;
  }

  public String getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public String getModifierId() {
    return modifierId;
  }

  public void setModifierId(String modifierId) {
    this.modifierId = modifierId;
  }

  public Long getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Long modifyTime) {
    this.modifyTime = modifyTime;
  }

  public String getWeekStart() {
    return weekStart;
  }

  public void setWeekStart(String weekStart) {
    this.weekStart = weekStart;
  }

  public Integer getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(Integer isDelete) {
    this.isDelete = isDelete;
  }

  public String getSpaceType() {
    return spaceType;
  }

  public void setSpaceType(String spaceType) {
    this.spaceType = spaceType;
  }

}
