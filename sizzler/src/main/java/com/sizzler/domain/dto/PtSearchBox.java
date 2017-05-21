package com.sizzler.domain.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangli
 * @version 2.1
 * @ClassName PtSearchBox
 * @Description .
 * @Date 2015/7/21
 */
public class PtSearchBox implements Serializable {

  private static final long serialVersionUID = -3099260985999172633L;

  private String $$hashKey;
  private String id;
  private String name;
  private String code;
  private List<String> type;

  public String get$$hashKey() {
    return $$hashKey;
  }

  public void set$$hashKey(String $$hashKey) {
    this.$$hashKey = $$hashKey;
  }

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

  public List<String> getType() {
    return type;
  }

  public void setType(List<String> type) {
    this.type = type;
  }
}
