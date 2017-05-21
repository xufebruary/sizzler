package com.sizzler.common.base.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sizzler.dexcoder.persistence.Criteria;
import com.sizzler.dexcoder.persistence.JdbcDao;

@Repository
public abstract class DaoBaseInterfaceImpl<T, PK extends Serializable> implements
    DaoBaseInterface<T, PK> {

  public static final String FIELD_DELETE = "isDelete"; // 删除标记字段
  public static final String FIELD_ORDER = "orderNumber"; // 排序字段

  @Autowired
  public JdbcDao jdbcDao;

  @Override
  public void update(T t) {
    jdbcDao.update(t);
  }

  @Override
  public void update(Map<String, Object[]> paramMap, Map<String, String> updateMap) {
    Criteria criteria = Criteria.create(getTClass()).multiplyCondition(paramMap);
    Iterator<String> iterator = updateMap.keySet().iterator();
    while (iterator.hasNext()) {
      String fieldName = iterator.next();
      String updateValue = updateMap.get(fieldName);
      criteria.set(fieldName, updateValue);
    }
    jdbcDao.update(criteria);
  }

  @Override
  public List<T> findAll() {
    Criteria criteria = Criteria.create(getTClass());
    // 判断是否包含删除标记字段
    if (isContainsField(criteria, FIELD_DELETE)) {
      criteria = criteria.and(FIELD_DELETE, new Object[] { 0 });
    }
    // 判断是否包含排序字段
    if (isContainsField(criteria, FIELD_ORDER)) {
      criteria = criteria.asc(FIELD_ORDER);
    }

    return jdbcDao.queryList(criteria);
  }

  @Override
  public List<T> findById(PK id) {
    // todo
    return null;
  }

  @Override
  public Long insert(T t) {
    return jdbcDao.insert(t);
  }

  @Override
  public void save(T t) {
    jdbcDao.save(t);
  }

  @Override
  public void delete(T t) {
    jdbcDao.delete(t);
  }

  @Override
  public void delete(Map<String, Object[]> paramMap) {
    jdbcDao.delete(Criteria.create(getTClass()).multiplyCondition(paramMap));
  }

  @Override
  public T get(PK id) {
    return jdbcDao.get(getTClass(), Long.valueOf(id.toString()));
  }

  @Override
  public T getByWhere(Map<String, Object[]> paramMap) {
    Criteria criteria = Criteria.create(getTClass()).multiplyCondition(paramMap);
    // 判断是否包含删除标记字段
    if (isContainsField(criteria, FIELD_DELETE)) {
      criteria = criteria.and(FIELD_DELETE, new Object[] { 0 });
    }
    // 判断是否包含排序字段
    if (isContainsField(criteria, FIELD_ORDER)) {
      criteria = criteria.asc(FIELD_ORDER);
    }

    return jdbcDao.querySingleResult(criteria);
  }

  @Override
  public List<T> findByWhere(Map<String, Object[]> paramMap, Map<String, String> orderMap) {
    Criteria criteria = Criteria.create(getTClass()).multiplyCondition(paramMap);
    Iterator<String> iterator = orderMap.keySet().iterator();
    while (iterator.hasNext()) {
      String filedName = iterator.next();
      String orderType = orderMap.get(filedName);
      if (orderType.equalsIgnoreCase("asc")) {
        criteria = criteria.asc(filedName);
      } else {
        criteria = criteria.desc(filedName);
      }
    }
    return jdbcDao.queryList(criteria);
  }

  @Override
  public List<T> findByWhere(Map<String, Object[]> paramMap) {
    Criteria criteria = Criteria.create(getTClass()).multiplyCondition(paramMap);
    // 判断是否包含删除标记字段
    if (isContainsField(criteria, FIELD_DELETE)) {
      criteria = criteria.and(FIELD_DELETE, new Object[] { 0 });
    }
    // 判断是否包含排序字段
    if (isContainsField(criteria, FIELD_ORDER)) {
      criteria = criteria.asc(FIELD_ORDER);
    }

    return jdbcDao.queryList(criteria);
  }

  @Override
  public List<T> findByWhere(String fieldName, String fieldOperator, Object[] values) {
    // like，可以换成!=、in、not in等,like 前后加% %value%
    Criteria criteria = Criteria.create(getTClass()).where(fieldName, fieldOperator, values);
    return jdbcDao.queryList(criteria);
  }

  @Override
  public Integer queryCount(Map<String, Object[]> paramMap) {
    return jdbcDao.queryCount(Criteria.create(getTClass()).multiplyCondition(paramMap));
  }

  /**
   * 得到当前的T class
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public Class<T> getTClass() {
    return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[0];
  }

  /**
   * 判断实体类中是否包含字段 field
   * 
   * @param criteria
   * @param field
   * @return
   */
  public boolean isContainsField(Criteria criteria, String field) {
    Field fields[] = criteria.getEntityClass().getDeclaredFields();
    for (Field f : fields) {
      if (f.getName().equals(field)) {
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings("rawtypes")
  protected String getSpringJDBCInsertSql(Class Clazz, String tableName) {
    String sql = "insert into " + tableName;
    String fieldSql = "";
    String bindSql = "";
    Field fields[] = Clazz.getDeclaredFields();
    for (Field field : fields) {
      fieldSql += field.getName() + ",";
      bindSql += ":" + field.getName() + ",";
    }
    fieldSql = fieldSql.substring(0, fieldSql.length() - 1);
    bindSql = bindSql.substring(0, bindSql.length() - 1);
    sql += " ( " + fieldSql + " ) VALUES ( " + bindSql + " ) ";
    return sql;
  }

  protected String getWhereSql(Map<String, String> paramMap) {
    String sql = " where ";
    Iterator<String> iterator = paramMap.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      sql += key + " = ? ";
    }
    return sql;
  }

}
