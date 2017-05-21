package com.sizzler.domain.basic.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sizzler.domain.basic.PtoneBasicDictCategory;

public class PtoneBasicDictCategoryWithDefine extends PtoneBasicDictCategory implements
    Serializable {

  private static final long serialVersionUID = -4217748068338816L;

  private List<PtoneBasicDictDefineWithItem> dictDefineList = new ArrayList<PtoneBasicDictDefineWithItem>();

  public PtoneBasicDictCategoryWithDefine() {
  }

  public PtoneBasicDictCategoryWithDefine(PtoneBasicDictCategory dictCategory) {
    this.setId(dictCategory.getId());
    this.setName(dictCategory.getName());
    this.setCode(dictCategory.getCode());
    this.setDescription(dictCategory.getDescription());
    this.setOrderNumber(dictCategory.getOrderNumber());
    this.setIsDelete(dictCategory.getIsDelete());
  }

  public List<PtoneBasicDictDefineWithItem> getDictDefineList() {
    return dictDefineList;
  }

  public void setDictDefineList(List<PtoneBasicDictDefineWithItem> dictDefineList) {
    this.dictDefineList = dictDefineList;
  }

}
