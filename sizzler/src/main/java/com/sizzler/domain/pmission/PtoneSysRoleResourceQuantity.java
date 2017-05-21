package com.sizzler.domain.pmission;

import java.io.Serializable;

public class PtoneSysRoleResourceQuantity implements Serializable {

  private String id;
  private String roleId;
  private String spaceId;
  private String resourceId;
  private Integer quantity;
  private String status;

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public String getRoleId() {
    return roleId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public int getQuantity() {
    return quantity;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
