package com.sizzler.common.base.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基于dexcoder的service基础接口
 */
public interface ServiceBaseInterface<T, PK extends Serializable> {

  public abstract List<T> findById(PK id);

  public abstract List<T> findAll();

  public abstract void save(T t);

  public abstract Long insert(T t);

  public abstract void delete(T t);

  public abstract void delete(Map<String, Object[]> paramMap);

  public abstract T get(PK id);

  public abstract void update(T t);

  public abstract void update(Map<String, Object[]> paramMap, Map<String, String> updateMap);

  public abstract T getByWhere(Map<String, Object[]> paramMap);

  public abstract List<T> findByWhere(Map<String, Object[]> paramMap);

  public abstract List<T> findByWhere(Map<String, Object[]> paramMap, Map<String, String> orderMap);

  public abstract List<T> findByWhere(String fieldName, String fieldOperator, Object[] values);

  public abstract Integer queryCount(Map<String, Object[]> paramMap);

}
