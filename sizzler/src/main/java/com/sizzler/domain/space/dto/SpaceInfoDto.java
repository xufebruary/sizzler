package com.sizzler.domain.space.dto;

import java.io.Serializable;

import com.sizzler.domain.space.PtoneSpaceInfo;

public class SpaceInfoDto extends PtoneSpaceInfo implements Serializable {

  private static final long serialVersionUID = -8981286748615028792L;

  private String type; // follower || owner
  private String uid;
  private String userEmail; // 当前用户的email
  private String ownerName;
  private String userPassword;

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

}
