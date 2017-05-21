package com.sizzler.common.mapper;

import java.util.List;
import java.util.Map;

/**
 * 基于mybatis的mapper基础接口
 */
public interface MapperBaseInterface<Entity, PK> {

  public boolean save(Entity entity);

  public void update(Entity entity);

  public void deleteById(PK id);

  public Entity findById(PK id);

  public List<Entity> findAll();

  public List<Entity> findByMap(Map<String, Object> map);

  public Integer getCount(Map<String, Object> map);

  public List<Entity> findByPage(Map<String, Object> map);

  public List<Entity> findByName(Map<String, Object> map);
}
