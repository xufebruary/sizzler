package com.sizzler.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.cache.PtoneBasicDictCache;
import com.sizzler.domain.basic.PtoneBasicDictItem;
import com.sizzler.service.PtoneDictService;

@Service("ptoneDictService")
public class PtoneDictServiceImpl implements PtoneDictService {

  @Autowired
  private PtoneBasicDictCache ptoneBasicDictCache;

  public List<PtoneBasicDictItem> getDictByCode(String dictCode) {
    return ptoneBasicDictCache.getDictDefineByCode(dictCode).getDictItemList();
  }

  public List<Map<String, Object>> getDictByCode(String dictCode, String locale) {

    List<PtoneBasicDictItem> dictItemList = this.getDictByCode(dictCode);
    List<Map<String, Object>> dictList = new ArrayList<Map<String, Object>>();
    for (PtoneBasicDictItem dictItem : dictItemList) {
      Map<String, Object> item = new LinkedHashMap<String, Object>();
      item.put("id", dictItem.getId());
      item.put("name", ptoneBasicDictCache.getLocaleName(dictItem.getName(), locale)); // 获取对应语言的字典表显示名称
      item.put("code", dictItem.getCode());
      dictList.add(item);
    }

    return dictList;
  }

}
