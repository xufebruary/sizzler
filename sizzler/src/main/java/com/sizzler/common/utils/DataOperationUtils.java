package com.sizzler.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * 数据操作工具集.
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: lanStar
 * </p>
 *
 * @version 1.0
 */
@Component
public class DataOperationUtils {

  private static Logger logger = LoggerFactory.getLogger(DataOperationUtils.class);

  private static JdbcTemplate jdbcTemplate;
  private static NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Resource
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    DataOperationUtils.jdbcTemplate = jdbcTemplate;
  }

  @Resource
  public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    DataOperationUtils.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public DataOperationUtils() {

  }

  public static <T> T get(String sql, Object param[], Class<T> type) {
    List<T> list = null;
    try {
      list = jdbcTemplate.query(sql, param, new BeanPropertyRowMapper(type));
    } catch (Exception e) {
      if (e.getMessage().indexOf("Incorrect result size:") != -1) {
        logger.info(type.getSimpleName() + " not find.");
      } else {
        throw new RuntimeException(e);
      }
    }
    if (CollectionUtils.isEmpty(list)) {
      return null;
    }
    return list.iterator().next();
  }

  public static <T> T queryForObject(String sql, Object param[], Class<T> type) {
    return jdbcTemplate.queryForObject(sql, param, type);
  }

  public static List<Map<String, Object>> queryForMap(String sql) {
    return jdbcTemplate.queryForList(sql);
  }

  public static List<Map<String, Object>> queryForMap(String sql, Object... args) {
    return jdbcTemplate.queryForList(sql, args);
  }

  public static List<?> queryForList(String sql, Map<String, String> paramMap, Class type) {
    return namedParameterJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper(type));
  }

  public static List<?> queryForList(String sql, Class type) {
    return namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper(type));
  }

  public static Integer insert(String sql, Object param[]) {
    return jdbcTemplate.update(sql, param);
  }

  /**
   * 查询记录集.
   *
   * @param sql 查询SQL
   * @param args 查询参数
   * @return 查询结果记录集<br>
   * <br>
   *         date 2012-5-15<br>
   *         remark <br>
   */
  public static SqlRowSet queryForRowSet(String sql, Object[] args) {
    SqlRowSet rs = null;
    if (null != args) {
      rs = jdbcTemplate.queryForRowSet(sql, args);
    } else {
      rs = jdbcTemplate.queryForRowSet(sql);
    }
    return rs;
  }

  /**
   * 执行SQL.
   *
   * @param sqlStatement SQL
   * @param object 参数列表
   * @return 执行结果影响的记录数 <br>
   */
  public static int executeSql(String sqlStatement, Object object) {
    int result = 0;
    SqlParameterSource params = null;
    try {
      params = new BeanPropertySqlParameterSource(object);
      result = namedParameterJdbcTemplate.update(sqlStatement, params);
    } catch (Exception e) {
      if (e.getCause() instanceof SQLException) {
        SQLException sql = (SQLException) e.getCause();
        if ((sql.getErrorCode() == 1366)
            && (sql.getMessage().indexOf("Incorrect string value:") == 0)) {
          try {
            DataOperationUtils.executeSQL(" SET NAMES 'utf8'; ");
            DataOperationUtils.executeSQL("SET SESSION sql_mode=''; ");
            result = namedParameterJdbcTemplate.update(sqlStatement, params);
          } catch (Exception ee) {
            throw new RuntimeException(ee);
          } finally {
            DataOperationUtils.executeSQL("SET SESSION sql_mode='TRADITIONAL' ");
          }
        } else {
          throw new RuntimeException(e);
        }
      } else {
        throw new RuntimeException(e);
      }
    }
    return result;
  }

  /**
   * 执行SQL.
   *
   * @param sqlStatement SQL
   * @return 执行结果影响的记录数 <br>
   * <br>
   *         date 2012-5-16<br>
   *         remark <br>
   */
  public static int executeSQL(String sqlStatement) {
    int result = 0;
    try {
      result = jdbcTemplate.update(sqlStatement);
    } catch (Exception e) {
      if (e.getCause() instanceof SQLException) {
        SQLException sql = (SQLException) e.getCause();
        if ((sql.getErrorCode() == 1366)
            && (sql.getMessage().indexOf("Incorrect string value:") == 0)) {
          try {
            executeSQL(" SET NAMES 'utf8'; ");
            executeSQL("SET SESSION sql_mode=''; ");
            result = jdbcTemplate.update(sqlStatement);
          } catch (Exception ee) {
            throw new RuntimeException(ee);
          } finally {
            executeSQL("SET SESSION sql_mode='TRADITIONAL' ");
          }
        } else {
          throw new RuntimeException(e);
        }
      } else {
        throw new RuntimeException(e);
      }
    }

    return result;
  }

  /**
   * 批量执行SQL.
   *
   * @param batchSQL SQL
   * @return 执行结果影响的记录数数组<br>
   * <br>
   *         date 2012-5-22<br>
   *         remark <br>
   */
  public static int[] batchUpdate(String[] batchSQL) {
    return jdbcTemplate.batchUpdate(batchSQL);
  }
}
