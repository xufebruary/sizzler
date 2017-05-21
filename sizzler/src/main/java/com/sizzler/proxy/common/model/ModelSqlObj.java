package com.sizzler.proxy.common.model;

import java.io.Serializable;

public class ModelSqlObj implements Serializable {

  private static final long serialVersionUID = 8236999061045464221L;

  private String sql;
  private String select;
  private String from;
  private String where;
  private String group;
  private String order;
  private String limit;
  private String totalSql;

  public String getSql() {
    if (this.sql != null && !"".equals(this.sql)) {
      return sql;
    } else {
      StringBuilder sqlSB = new StringBuilder();
      sqlSB.append(select).append(" ").append(from).append(" ").append(where).append(" ")
          .append(group).append(" ").append(order);
      return sqlSB.toString();
    }
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public String getSelect() {
    return select;
  }

  public void setSelect(String select) {
    this.select = select;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    this.where = where;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public String getLimit() {
    return limit;
  }

  public void setLimit(String limit) {
    this.limit = limit;
  }

  public String getTotalSql() {
    return totalSql;
  }

  public void setTotalSql(String totalSql) {
    this.totalSql = totalSql;
  }

}
