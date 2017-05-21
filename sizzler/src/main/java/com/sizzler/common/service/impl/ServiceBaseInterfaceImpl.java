package com.sizzler.common.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.service.ServiceBaseInterface;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.mapper.MapperBaseInterface;

public abstract class ServiceBaseInterfaceImpl<Entity, PK> implements
    ServiceBaseInterface<Entity, PK> {

  protected Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public boolean save(Entity entity) throws ServiceException {
    boolean result = false;
    try {
      result = this.getMapperBaseInterface().save(entity);
    } catch (Exception e) {
      // TODO: handle exception
      logger.error("ServiceBaseInterfaceImpl save", e);
      throw new ServiceException(e.getMessage(), e);
    }
    return result;
  }

  @Override
  public boolean update(Entity entity) throws ServiceException {
    // TODO Auto-generated method stub
    boolean flag = false;
    try {
      this.getMapperBaseInterface().update(entity);
      flag = true;
    } catch (Exception e) {
      // TODO: handle exception
      logger.error("ServiceBaseInterfaceImpl update", e);
      throw new ServiceException(e.getMessage(), e);
    }
    return flag;
  }

  @Override
  public boolean deleteById(PK id) throws ServiceException {
    // TODO Auto-generated method stub
    boolean flag = false;
    try {
      this.getMapperBaseInterface().deleteById(id);
      flag = true;
    } catch (Exception e) {
      // TODO: handle exception
      logger.error("ServiceBaseInterfaceImpl deleteById", e);
      throw new ServiceException(e.getMessage(), e);
    }
    return flag;
  }

  @Override
  public Entity findById(PK id) throws ServiceException {
    // TODO Auto-generated method stub
    Entity entity = null;
    try {
      entity = this.getMapperBaseInterface().findById(id);
    } catch (Exception e) {
      // TODO: handle exception
      logger.error("ServiceBaseInterfaceImpl findById", e);
      throw new ServiceException(e.getMessage(), e);
    }
    return entity;
  }

  @Override
  public List<Entity> findAll() throws ServiceException {
    // TODO Auto-generated method stub
    List<Entity> entityList = null;
    try {
      entityList = this.getMapperBaseInterface().findAll();
    } catch (Exception e) {
      // TODO: handle exception
      logger.error("ServiceBaseInterfaceImpl findAll", e);
      throw new ServiceException(e.getMessage(), e);
    }
    return entityList;
  }

  @Override
  public List<Entity> findByMap(Map<String, Object> map) throws ServiceException {
    // TODO Auto-generated method stub
    List<Entity> entityList = null;
    try {
      entityList = this.getMapperBaseInterface().findByMap(map);
    } catch (Exception e) {
      // TODO: handle exception
      logger.error("ServiceBaseInterfaceImpl findByMap", e);
      throw new ServiceException(e.getMessage(), e);
    }
    return entityList;
  }

  @Override
  public Integer getCount(Map<String, Object> map) throws ServiceException {
    // TODO Auto-generated method stub
    int count = 0;
    try {
      count = this.getMapperBaseInterface().getCount(map);
    } catch (Exception e) {
      // TODO: handle exception
      logger.error("ServiceBaseInterfaceImpl getCount", e);
      throw new ServiceException(e.getMessage(), e);
    }
    return count;
  }



  @Override
  public List<Entity> findByPage(Map<String, Object> map) throws ServiceException {
    // TODO Auto-generated method stub
    List<Entity> entityList = null;
    try {
      entityList = this.getMapperBaseInterface().findByMap(map);
    } catch (Exception e) {
      // TODO: handle exception
      logger.error("ServiceBaseInterfaceImpl findByPage", e);
      throw new ServiceException(e.getMessage(), e);
    }
    return entityList;
  }

  public abstract MapperBaseInterface<Entity, PK> getMapperBaseInterface();
}
