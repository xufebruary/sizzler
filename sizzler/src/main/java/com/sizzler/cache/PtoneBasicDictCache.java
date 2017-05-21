package com.sizzler.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.sizzler.domain.basic.PtoneBasicDictCategory;
import com.sizzler.domain.basic.PtoneBasicDictDefine;
import com.sizzler.domain.basic.PtoneBasicDictItem;
import com.sizzler.domain.basic.dto.PtoneBasicDictCategoryWithDefine;
import com.sizzler.domain.basic.dto.PtoneBasicDictDefineWithItem;
import com.sizzler.service.basic.PtoneBasicDictCategoryService;
import com.sizzler.service.basic.PtoneBasicDictDefineService;
import com.sizzler.service.basic.PtoneBasicDictItemService;

/**
 * 缓存字典表
 * 
 * @author peng.xu
 * 
 */
@Component("ptoneBasicDictCache")
public class PtoneBasicDictCache {

  @Autowired
  private PtoneBasicDictCategoryService ptoneBasicDictCategoryService;

  @Autowired
  private PtoneBasicDictDefineService ptoneBasicDictDefineService;

  @Autowired
  private PtoneBasicDictItemService ptoneBasicDictItemService;

  private static List<PtoneBasicDictCategoryWithDefine> cacheDictCategoryList;

  private static List<PtoneBasicDictDefineWithItem> cacheDictDefineList;

  private static Map<Long, PtoneBasicDictCategoryWithDefine> cacheIdDictCategoryMap;

  private static Map<Long, PtoneBasicDictDefineWithItem> cacheIdDictDefineMap;

  private static Map<Long, PtoneBasicDictItem> cacheIdDictItemMap;

  private static Map<String, PtoneBasicDictDefineWithItem> cacheCodeDictDefineMap;

  private static Map<String, PtoneBasicDictItem> cacheCodeDictItemMap;

  /**
   * 初始化字典表缓存数据，在spring构建对象后自动初始化
   */
  @PostConstruct
  public void init() {
    List<PtoneBasicDictCategory> dictCategoryList = ptoneBasicDictCategoryService.findAll();
    List<PtoneBasicDictDefine> dictDefineList = ptoneBasicDictDefineService.findAll();
    List<PtoneBasicDictItem> dictItemList = ptoneBasicDictItemService.findAll();

    List<PtoneBasicDictCategoryWithDefine> newCacheDictCategoryList =
        new ArrayList<PtoneBasicDictCategoryWithDefine>();
    List<PtoneBasicDictDefineWithItem> newCacheDictDefineList =
        new ArrayList<PtoneBasicDictDefineWithItem>();
    Map<Long, PtoneBasicDictCategoryWithDefine> newCacheIdDictCategoryMap =
        new LinkedHashMap<Long, PtoneBasicDictCategoryWithDefine>();
    Map<Long, PtoneBasicDictDefineWithItem> newCacheIdDictDefineMap =
        new LinkedHashMap<Long, PtoneBasicDictDefineWithItem>();
    Map<Long, PtoneBasicDictItem> newCacheIdDictItemMap =
        new LinkedHashMap<Long, PtoneBasicDictItem>();
    Map<String, PtoneBasicDictDefineWithItem> newCacheCodeDictDefineMap =
        new LinkedHashMap<String, PtoneBasicDictDefineWithItem>();
    Map<String, PtoneBasicDictItem> newCacheCodeDictItemMap =
        new LinkedHashMap<String, PtoneBasicDictItem>();

    for (PtoneBasicDictCategory dictCategory : dictCategoryList) {
      PtoneBasicDictCategoryWithDefine dictCategoryWithDefine =
          new PtoneBasicDictCategoryWithDefine(dictCategory);
      newCacheIdDictCategoryMap.put(dictCategory.getId(), dictCategoryWithDefine);
    }

    for (PtoneBasicDictDefine dictDefine : dictDefineList) {
      PtoneBasicDictDefineWithItem dictDefineWithItem =
          new PtoneBasicDictDefineWithItem(dictDefine);
      newCacheIdDictDefineMap.put(dictDefine.getId(), dictDefineWithItem);
      newCacheCodeDictDefineMap.put(dictDefine.getCode(), dictDefineWithItem);
    }

    for (PtoneBasicDictItem dictItem : dictItemList) {
      newCacheIdDictItemMap.put(dictItem.getId(), dictItem);
      newCacheCodeDictItemMap.put(dictItem.getDictCode() + "_" + dictItem.getCode(), dictItem);

      // 注意：newCacheIdDictDefineMap 和 newCacheCodeDictDefineMap 引用的是相同的DictItemList，添加一次就可以了
      newCacheIdDictDefineMap.get(dictItem.getDictId()).getDictItemList().add(dictItem);
      // newCacheCodeDictDefineMap.get(dictItem.getDictCode()).getDictItemList().add(dictItem);
    }

    for (PtoneBasicDictDefineWithItem dictDefineWithItem : newCacheIdDictDefineMap.values()) {
      newCacheDictDefineList.add(dictDefineWithItem);
      newCacheIdDictCategoryMap.get(dictDefineWithItem.getCategoryId()).getDictDefineList()
          .add(dictDefineWithItem);
    }

    for (PtoneBasicDictCategoryWithDefine dictCategory : newCacheIdDictCategoryMap.values()) {
      newCacheDictCategoryList.add(dictCategory);
    }

    cacheDictCategoryList = newCacheDictCategoryList;
    cacheDictDefineList = newCacheDictDefineList;
    cacheIdDictCategoryMap = newCacheIdDictCategoryMap;
    cacheIdDictDefineMap = newCacheIdDictDefineMap;
    cacheIdDictItemMap = newCacheIdDictItemMap;
    cacheCodeDictDefineMap = newCacheCodeDictDefineMap;
    cacheCodeDictItemMap = newCacheCodeDictItemMap;

  }

  /**
   * 获取所有字典类型、字典声明以及字典项组成的字典树
   * 
   * @return
   */
  public List<PtoneBasicDictCategoryWithDefine> getAllDictCategoryTree() {
    return cacheDictCategoryList;
  }

  /**
   * 获取所有字典表声明及字典项组成的字典树
   * 
   * @return
   */
  public List<PtoneBasicDictDefineWithItem> getAllDictDefineTree() {
    return cacheDictDefineList;
  }

  /**
   * 根据分类ID，获取相应分类下的所有字典表及字字典项
   * 
   * @param dictCategoryId
   * @return
   */
  public PtoneBasicDictCategoryWithDefine getDictCategoryById(Long dictCategoryId) {
    return cacheIdDictCategoryMap.get(dictCategoryId);
  }

  /**
   * 根据字典ID，获取相应字典下的所有字典项
   * 
   * @param dictDefineId
   * @return
   */
  public PtoneBasicDictDefineWithItem getDictDefineById(Long dictDefineId) {
    return cacheIdDictDefineMap.get(dictDefineId);
  }

  /**
   * 根据字典Code，获取相应字典下的所有字典项
   * 
   * @param dictDefineCode
   * @return
   */
  public PtoneBasicDictDefineWithItem getDictDefineByCode(String dictDefineCode) {
    return cacheCodeDictDefineMap.get(dictDefineCode);
  }

  /**
   * 根据字典项ID，获取相应字典项
   * 
   * @param dictItemId
   * @return
   */
  public PtoneBasicDictItem getDictItemById(Long dictItemId) {
    return cacheIdDictItemMap.get(dictItemId);
  }

  /**
   * 根据字典项Code，获取相应字典项
   * 
   * @param dictItemCode (dictCode + itemCode)
   * @return
   */
  public PtoneBasicDictItem getDictItemByCode(String dictItemCode) {
    return cacheCodeDictItemMap.get(dictItemCode);
  }

  /**
   * 将字典项name转换为多语言对应的Map格式
   * 
   * @param dictName
   * @return
   */
  @SuppressWarnings("unchecked")
  public Map<String, String> getNameMap(String dictName) {
    if (dictName == null || "".equals(dictName)) {
      return new HashMap<String, String>();
    }
    return JSON.parseObject(dictName, HashMap.class);
  }

  /**
   * 从字典项name中获取对应语言的显示名称
   * 
   * @param dictName
   * @param langue
   * @return
   */
  public String getLocaleName(String dictName, String langue) {
    String name = this.getNameMap(dictName).get(langue);
    if (name != null) {
      return name;
    } else {
      return this.getNameMap(dictName).get(PtoneBasicDictItem.DICT_NAME_DEFAULT_KEY);
    }
  }

}
