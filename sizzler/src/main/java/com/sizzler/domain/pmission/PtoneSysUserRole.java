package com.sizzler.domain.pmission;

import java.io.Serializable;

public class PtoneSysUserRole implements Serializable {

  private String id;
  private String userId;
  private String spaceId;
  private String roleId;
  private String status;

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public String getRoleId() {
    return roleId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
