package com.sizzler.provider.common.entity;

import java.io.Serializable;

/**
 * 过滤器值列表实体类
 * @author you.zou on 2016-07-14
 *
 */
public class FilterValueMetaEntity implements Serializable {
  /**
	 * 
	 */
  private static final long serialVersionUID = 6572613488580806301L;
  private String id;
  private String name;
  private String code;
  private String type;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
