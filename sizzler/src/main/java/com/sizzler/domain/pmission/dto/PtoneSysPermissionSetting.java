package com.sizzler.domain.pmission.dto;

import java.util.List;

/**
 * @ClassName: PtoneSysPermissionSetting
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2016/5/17
 * @author: zhangli
 */
public class PtoneSysPermissionSetting {

  private String roleId;
  private List<String> permissionIds;

  public String getRoleId() {
    return roleId;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public List<String> getPermissionIds() {
    return permissionIds;
  }

  public void setPermissionIds(List<String> permissionIds) {
    this.permissionIds = permissionIds;
  }
}
