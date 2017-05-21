package com.sizzler.domain.basic;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneBasicDictItem implements Serializable {

  private static final long serialVersionUID = 5210614436747744640L;

  public static final String DICT_NAME_DEFAULT_KEY = "default";

  public static final int DICT_ID_GENDER = 1;
  public static final String DICT_CODE_GENDER = "gender";
  public static final String DICT_CODE_GENDER_MALE = "male";
  public static final String DICT_CODE_GENDER_FEMALE = "female";

  public static final int DICT_ID_WEEKSTART = 2;
  public static final String DICT_CODE_WEEKSTART = "week_start";
  public static final String DICT_CODE_WEEKSTART_SUNDAY = "sunday";
  public static final String DICT_CODE_WEEKSTART_MONDAY = "monday";

  public static final int DICT_ID_LANGUAGE = 3;
  public static final String DICT_CODE_LANGUAGE = "language";

  @PK
  private long id;
  private String name;
  private String code;
  private String description;
  private String dictCode;
  private long dictId;
  private Integer orderNumber;
  private int isDelete;

  public long getId() {
    return id;
  }

  public void setId(long id) {
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

  public String getDictCode() {
    return dictCode;
  }

  public void setDictCode(String dictCode) {
    this.dictCode = dictCode;
  }

  public long getDictId() {
    return dictId;
  }

  public void setDictId(long dictId) {
    this.dictId = dictId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(Integer orderNumber) {
    this.orderNumber = orderNumber;
  }

  public int getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(int isDelete) {
    this.isDelete = isDelete;
  }

}
