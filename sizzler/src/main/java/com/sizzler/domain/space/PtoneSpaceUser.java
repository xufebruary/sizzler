package com.sizzler.domain.space;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneSpaceUser implements Serializable {

  private static final long serialVersionUID = 5278469520578783629L;

  public static final String TYPE_OWNDER = "owner"; // 所有者
  public static final String TYPE_FOLLOWER = "follower"; // 参与者

  public static final String STATUS_INVITING = "inviting"; // 邀请中
  public static final String STATUS_ACCEPTED = "accepted"; // 已接受
  public static final String STATUS_REFUSED = "refused"; // 已拒绝

  public static final String INVITE_URL_STATUS_SIGNIN = "signin"; // 登录进入
  public static final String INVITE_URL_STATUS_SIGNUP = "signup"; // 注册进入
  public static final String INVITE_URL_STATUS_NOT_ACTIVE = "not_active"; // 注册未激活进入
  public static final String INVITE_URL_STATUS_ACCEPTED = "invalidate"; // = "accepted"; // 已接受
  public static final String INVITE_URL_STATUS_INVITE_REMOVED = "invalidate"; // =
                                                                              // "invite_removed";//
                                                                              // 邀请已删除
  public static final String INVITE_URL_STATUS_SPACE_REMOVED = "invalidate"; // = "space_removed";//
                                                                             // 空间已删除
  public static final String INVITE_URL_STATUS_INVALIDATE = "invalidate"; // 无效

  @PK
  private long id;
  private String spaceId;
  private String uid;
  private String userEmail;
  private String type; // follower || owner
  private String status; // inviting: 邀请中， accepted：已接受
  private String creatorId;
  private Long createTime;
  private Integer isDelete;

  public void setId(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getUid() {
    return uid;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setIsDelete(Integer isDelete) {
    this.isDelete = isDelete;
  }

  public Integer getIsDelete() {
    return isDelete;
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

}
