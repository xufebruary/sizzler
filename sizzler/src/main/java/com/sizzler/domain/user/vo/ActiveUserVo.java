package com.sizzler.domain.user.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ActiveUserVo implements Serializable {

  private static final long serialVersionUID = 5416157571197641297L;
  private String uid; // 用户id(暂时备用)
  private String userEmail; // 当前用户的email
  private String userPassword; // 用户激活时输入的密码
  private String weekStart; // 用户周起始日（前台传入)
  private String spaceId;// 后台生成
  private String spaceName;// 后台生成
  private String spaceDomain;// 后台生成
  private String sid;// 后台生成

  public String getSid() {
    return sid;
  }

  public void setSid(String sid) {
    this.sid = sid;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public String getSpaceName() {
    return spaceName;
  }

  public void setSpaceName(String spaceName) {
    this.spaceName = spaceName;
  }

  public String getSpaceDomain() {
    return spaceDomain;
  }

  public void setSpaceDomain(String spaceDomain) {
    this.spaceDomain = spaceDomain;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public String getWeekStart() {
    return weekStart;
  }

  public void setWeekStart(String weekStart) {
    this.weekStart = weekStart;
  }
}
