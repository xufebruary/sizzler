package com.sizzler.provider.common.query.connection.pool;

import java.util.concurrent.TimeUnit;

import com.alibaba.druid.pool.DruidAbstractDataSource;

/**
 * 公用的DruidDataSource的相关属性信息; <br>
 * 此处用基本数据类型。
 * @name DruidDataSourceConfig
 * @author shaoqiang.guo
 * @data：2016年12月28日 下午3:21:13
 * @see DruidAbstractDataSource
 */
public class DruidDataSourceConfig {

  public final static int INITIAL_DELAY = 1;
  public final static int DELAY = 1;
  public final static long MAX_MAP_SIZE = 100L;
  /** 24H */
  public final static long DESTROY_TIME = 86400000L;
  /** 1H */
  public final static long MANDATORY_DESTROY_TIME = 3600000L;

  /* DruidDataSource中需要的参数 */
  protected int maxActive;
  protected int initialSize;
  protected long maxWait;
  protected int minIdle;
  protected long timeBetweenEvictionRunsMillis;
  protected long minEvictableIdleTimeMillis;
  protected String validationQuery;
  protected boolean testWhileIdle;
  protected boolean testOnBorrow;
  protected boolean testOnReturn;
  protected boolean poolPreparedStatements;
  protected int maxPoolPreparedStatementPerConnectionSize;
  protected String filters;
  protected String connectionProperties;
  protected boolean useGloalDataSourceStat;

  /* 维护连接池Map的线程相关参数，且 为了防止出现没有正常设值，均设值默认值 */
  /** 线程首次执行的延迟时间 默认为 1 */
  protected int initialDelay = INITIAL_DELAY;
  /** 一次执行终止和下一次执行开始之间的延迟 默认为 1 */
  protected int delay = DELAY;
  /** initialDelay 和 delay 参数的时间单位 默认为TimeUnit.HOUR */
  protected TimeUnit unit = TimeUnit.MINUTES;

  /** 连接池的最大数量 */
  protected long conectionPoolMapMaxSize = MAX_MAP_SIZE;

  /** 正常连接池超时销毁时间 */
  protected long destroyTime = DESTROY_TIME;

  /** 连接池数量到达指定数量，强制连接池超时销毁时间 */
  protected long mandatoryDestroyTime = MANDATORY_DESTROY_TIME;
  /** 是否使用连接池 */
  protected boolean isUseConectionPool;

  public int getMaxActive() {
    return maxActive;
  }

  public void setMaxActive(int maxActive) {
    this.maxActive = maxActive;
  }

  public int getInitialSize() {
    return initialSize;
  }

  public void setInitialSize(int initialSize) {
    this.initialSize = initialSize;
  }

  public long getMaxWait() {
    return maxWait;
  }

  public void setMaxWait(long maxWait) {
    this.maxWait = maxWait;
  }

  public int getMinIdle() {
    return minIdle;
  }

  public void setMinIdle(int minIdle) {
    this.minIdle = minIdle;
  }

  public long getTimeBetweenEvictionRunsMillis() {
    return timeBetweenEvictionRunsMillis;
  }

  public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
    this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
  }

  public long getMinEvictableIdleTimeMillis() {
    return minEvictableIdleTimeMillis;
  }

  public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
    this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
  }

  public String getValidationQuery() {
    return validationQuery;
  }

  public void setValidationQuery(String validationQuery) {
    this.validationQuery = validationQuery;
  }

  public boolean isTestWhileIdle() {
    return testWhileIdle;
  }

  public void setTestWhileIdle(boolean testWhileIdle) {
    this.testWhileIdle = testWhileIdle;
  }

  public boolean isTestOnBorrow() {
    return testOnBorrow;
  }

  public void setTestOnBorrow(boolean testOnBorrow) {
    this.testOnBorrow = testOnBorrow;
  }

  public boolean isTestOnReturn() {
    return testOnReturn;
  }

  public void setTestOnReturn(boolean testOnReturn) {
    this.testOnReturn = testOnReturn;
  }

  public boolean isPoolPreparedStatements() {
    return poolPreparedStatements;
  }

  public void setPoolPreparedStatements(boolean poolPreparedStatements) {
    this.poolPreparedStatements = poolPreparedStatements;
  }

  public int getMaxPoolPreparedStatementPerConnectionSize() {
    return maxPoolPreparedStatementPerConnectionSize;
  }

  public void setMaxPoolPreparedStatementPerConnectionSize(
      int maxPoolPreparedStatementPerConnectionSize) {
    this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
  }

  public String getFilters() {
    return filters;
  }

  public void setFilters(String filters) {
    this.filters = filters;
  }

  public String getConnectionProperties() {
    return connectionProperties;
  }

  public void setConnectionProperties(String connectionProperties) {
    this.connectionProperties = connectionProperties;
  }

  public boolean isUseGloalDataSourceStat() {
    return useGloalDataSourceStat;
  }

  public void setUseGloalDataSourceStat(boolean useGloalDataSourceStat) {
    this.useGloalDataSourceStat = useGloalDataSourceStat;
  }

  public int getInitialDelay() {
    return initialDelay;
  }

  public void setInitialDelay(int initialDelay) {
    this.initialDelay = initialDelay;
  }

  public int getDelay() {
    return delay;
  }

  public void setDelay(int delay) {
    this.delay = delay;
  }

  public TimeUnit getUnit() {
    return unit;
  }

  public void setUnit(TimeUnit unit) {
    this.unit = unit;
  }

  public long getConectionPoolMapMaxSize() {
    return conectionPoolMapMaxSize;
  }

  public void setConectionPoolMapMaxSize(long conectionPoolMapMaxSize) {
    this.conectionPoolMapMaxSize = conectionPoolMapMaxSize;
  }

  public long getDestroyTime() {
    return destroyTime;
  }

  public void setDestroyTime(long destroyTime) {
    this.destroyTime = destroyTime;
  }

  public long getMandatoryDestroyTime() {
    return mandatoryDestroyTime;
  }

  public void setMandatoryDestroyTime(long mandatoryDestroyTime) {
    this.mandatoryDestroyTime = mandatoryDestroyTime;
  }

  public boolean isUseConectionPool() {
    return isUseConectionPool;
  }

  public void setIsUseConectionPool(boolean isUseConectionPool) {
    this.isUseConectionPool = isUseConectionPool;
  }

}
