package com.sizzler.domain.space.vo;

import java.io.Serializable;

public class SpaceVo implements Serializable {

  private static final long serialVersionUID = -9024980748712090789L;
  
  private String spaceId;
  private String name;
  private String domain;

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


}
