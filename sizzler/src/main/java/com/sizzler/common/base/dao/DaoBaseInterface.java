package com.sizzler.common.base.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基于dexcoder的dao基础接口
 */
public interface DaoBaseInterface<T, PK extends Serializable> {

  public abstract List<T> findAll();

  public abstract List<T> findById(PK id);

  public abstract void save(T t);

  public abstract Long insert(T t);

  public abstract void delete(T t);

  public abstract void delete(Map<String, Object[]> paramMap);

  public abstract T get(PK id);

  public abstract T getByWhere(Map<String, Object[]> paramMap);

  public abstract List<T> findByWhere(Map<String, Object[]> paramMap);

  public abstract List<T> findByWhere(Map<String, Object[]> paramMap, Map<String, String> orderMap);

  public abstract List<T> findByWhere(String fieldName, String fieldOperator, Object[] values);

  public abstract void update(T t);

  public abstract void update(Map<String, Object[]> paramMap, Map<String, String> updateMap);

  public abstract Integer queryCount(Map<String, Object[]> paramMap);

}
