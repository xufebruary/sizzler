package com.sizzler.dexcoder.persistence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;

import com.sizzler.dexcoder.annotation.PK;
import com.sizzler.dexcoder.utils.NameUtils;

/**
 * 默认名称处理handler
 */
public class DefaultNameHandler implements NameHandler {

  /** 主键后缀 */
  private static final String PRI_SUFFIX = "_ID";

  /**
   * 根据实体名获取表名
   * 
   * @param entityClass
   * @return
   */
  public String getTableName(Class<?> entityClass) {
    // Java属性的骆驼命名法转换回数据库下划线“_”分隔的格式
    return NameUtils.getUnderlineName(entityClass.getSimpleName());
  }

  /**
   * 根据表名获取主键名
   * 
   * @param entityClass
   * @return
   */
  public String getPKName(Class<?> entityClass) {
    // 主键以表名加上“_id” 如user表主键即“user_id”
    String underlineName = NameUtils.getUnderlineName(entityClass.getSimpleName()) + PRI_SUFFIX;
    Field[] fields = entityClass.getDeclaredFields();
    for (Field filed : fields) {
      Annotation annotation = filed.getAnnotation(PK.class);
      String filedName = filed.getName();
      if (null != annotation) {
        underlineName = NameUtils.getUnderlineName(filedName);
      }
    }
    return underlineName;
  }

  /**
   * 根据属性名获取列名
   * 
   * @param fieldName
   * @return
   */
  public String getColumnName(String fieldName) {
    String underlineName = NameUtils.getUnderlineName(fieldName);
    return underlineName;
  }

  /**
   * 根据实体名获取主键值 自增类主键数据库直接返回null即可
   * 
   * @param entityClass
   *          the entity class
   * @param dialect
   *          the dialect
   * @return pK value
   */
  public String getPKValue(Class<?> entityClass, String dialect) {
    if (StringUtils.equalsIgnoreCase(dialect, "oracle")) {
      // 获取序列就可以了，默认seq_加上表名为序列名
      String tableName = this.getTableName(entityClass);
      return String.format("SEQ_%s.NEXTVAL", tableName);
    }
    return null;
  }
}
