package com.sizzler.common.service;

import java.util.List;
import java.util.Map;

import com.sizzler.common.exception.ServiceException;

/**
 * 基于mybatis的service实现基础接口
 */
public interface ServiceBaseInterface<Entity, PK> {

  public boolean save(Entity entity) throws ServiceException;

  public boolean update(Entity entity) throws ServiceException;

  public boolean deleteById(PK id) throws ServiceException;

  public Entity findById(PK id) throws ServiceException;

  public List<Entity> findAll() throws ServiceException;

  public List<Entity> findByMap(Map<String, Object> map) throws ServiceException;

  public Integer getCount(Map<String, Object> map) throws ServiceException;

  public List<Entity> findByPage(Map<String, Object> map) throws ServiceException;
}
