package com.sizzler.domain.pmission;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneSysResource implements Serializable {

  private static final long serialVersionUID = 7605949068615588471L;
  
  private String id;
  @PK
  private String resourceId;
  private String spaceId;
  private String name;
  private String code;
  private String link;
  private Integer parentId;
  private String type;
  private String status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public void setLink(String link) {
    this.link = link;
  }

  public String getLink() {
    return link;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
