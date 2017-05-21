package com.sizzler.domain.pmission;

import java.io.Serializable;

public class PtoneSysRolePermission implements Serializable {

  private String id;
  private String roleId;
  private String permissionId;
  private String status;

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public String getRoleId() {
    return roleId;
  }

  public void setPermissionId(String permissionId) {
    this.permissionId = permissionId;
  }

  public String getPermissionId() {
    return permissionId;
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
