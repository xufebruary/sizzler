package com.sizzler.domain.basic.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sizzler.domain.basic.PtoneBasicDictDefine;
import com.sizzler.domain.basic.PtoneBasicDictItem;

public class PtoneBasicDictDefineWithItem extends PtoneBasicDictDefine implements Serializable {

  private static final long serialVersionUID = 6679998126566934273L;

  private List<PtoneBasicDictItem> dictItemList = new ArrayList<PtoneBasicDictItem>();

  public PtoneBasicDictDefineWithItem() {
  }

  public PtoneBasicDictDefineWithItem(PtoneBasicDictDefine dictDefine) {
    this.setId(dictDefine.getId());
    this.setName(dictDefine.getName());
    this.setCode(dictDefine.getCode());
    this.setDescription(dictDefine.getDescription());
    this.setCategoryCode(dictDefine.getCategoryCode());
    this.setCategoryId(dictDefine.getCategoryId());
    this.setOrderNumber(dictDefine.getOrderNumber());
    this.setIsDelete(dictDefine.getIsDelete());
  }

  public List<PtoneBasicDictItem> getDictItemList() {
    return dictItemList;
  }

  public void setDictItemList(List<PtoneBasicDictItem> dictItemList) {
    this.dictItemList = dictItemList;
  }

}
