package com.sizzler.service;

import java.util.List;
import java.util.Map;

import com.sizzler.domain.basic.PtoneBasicDictItem;

public interface PtoneDictService {

  public List<PtoneBasicDictItem> getDictByCode(String dictCode);

  public List<Map<String, Object>> getDictByCode(String dictCode, String locale);

}
