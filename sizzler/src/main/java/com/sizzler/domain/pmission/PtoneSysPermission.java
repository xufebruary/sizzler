package com.sizzler.domain.pmission;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneSysPermission implements Serializable {

  private static final long serialVersionUID = 6592648560163444878L;
  
  private String id;
  @PK
  private String permissionId;
  private String resourceId;
  private String operationId;
  private String spaceId;
  private String name;
  private String code;
  private String status;
  private String url;
  private String description;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setPermissionId(String permissionId) {
    this.permissionId = permissionId;
  }

  public String getPermissionId() {
    return permissionId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setOperationId(String operationId) {
    this.operationId = operationId;
  }

  public String getOperationId() {
    return operationId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
