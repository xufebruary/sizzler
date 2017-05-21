package com.sizzler.common.base;

import net.sf.log4jdbc.Slf4jSpyLogDelegator;
import net.sf.log4jdbc.Spy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeautifulSqlFormatter extends Slf4jSpyLogDelegator {

  private static final Logger log = LoggerFactory.getLogger(BeautifulSqlFormatter.class);
  private static final BasicSqlFormatter formatter = new BasicSqlFormatter();

  private String sqlPrefix = "SQL:";
  private Boolean formatSql = Boolean.TRUE;

  public BeautifulSqlFormatter() {
  }

  @Override
  public String sqlOccured(Spy spy, String methodCall, String rawSql) {
    String sql = null;
    if (formatSql)
      sql = formatter.format(rawSql);
    else
      sql = rawSql;
    log.debug(sqlPrefix + sql);
    return sql;
  }

  @Override
  public String sqlOccured(Spy spy, String methodCall, String[] sqls) {
    String s = "";
    for (int i = 0; i < sqls.length; i++) {
      s += sqlOccured(spy, methodCall, sqls[i]) + String.format("%n");
    }
    return s;
  }

  public Boolean getFormatSql() {
    return formatSql;
  }

  public void setFormatSql(Boolean formatSql) {
    this.formatSql = formatSql;
  }

  public String getSqlPrefix() {
    return sqlPrefix;
  }

  public void setSqlPrefix(String sqlPrefix) {
    this.sqlPrefix = sqlPrefix;
  }
}
