package com.sizzler.common.base.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.dao.DaoBaseInterface;

@Service
public abstract class ServiceBaseInterfaceImpl<T, PK extends Serializable> implements
    ServiceBaseInterface<T, PK> {

  @Autowired
  private DaoBaseInterface<T, PK> daoBaseInterface;

  @Override
  public List<T> findById(PK id) {
    return daoBaseInterface.findById(id);
  }

  @Override
  public List<T> findAll() {
    return daoBaseInterface.findAll();
  }

  @Override
  public void save(T t) {
    daoBaseInterface.save(t);
  }

  @Override
  public Long insert(T t) {
    return daoBaseInterface.insert(t);
  }

  @Override
  public void delete(T t) {
    daoBaseInterface.delete(t);
  }

  @Override
  public void delete(Map<String, Object[]> paramMap) {
    daoBaseInterface.delete(paramMap);
  }

  @Override
  public T get(PK id) {
    return daoBaseInterface.get(id);
  }

  @Override
  public T getByWhere(Map<String, Object[]> paramMap) {
    return daoBaseInterface.getByWhere(paramMap);
  }

  @Override
  public List<T> findByWhere(Map<String, Object[]> paramMap) {
    return daoBaseInterface.findByWhere(paramMap);
  }

  @Override
  public List<T> findByWhere(Map<String, Object[]> paramMap, Map<String, String> orderMap) {
    return daoBaseInterface.findByWhere(paramMap, orderMap);
  }

  @Override
  public List<T> findByWhere(String fieldName, String fieldOperator, Object[] values) {
    return daoBaseInterface.findByWhere(fieldName, fieldOperator, values);
  }

  @Override
  public void update(T t) {
    daoBaseInterface.update(t);
  }

  @Override
  public void update(Map<String, Object[]> paramMap, Map<String, String> updateMap) {
    daoBaseInterface.update(paramMap, updateMap);
  }

  @Override
  public Integer queryCount(Map<String, Object[]> paramMap) {
    return daoBaseInterface.queryCount(paramMap);
  }
}
