package com.sizzler.provider.common.db;

/**
 * Created by ptmind on 2015/11/11.
 */
public enum DataBaseType {

  MYSQL, MYSQLAMAZONRDS, MARIADB, ORACLE, SQLSERVER, POSTGRE, DB2, REDSHIFT, STANDARDREDSHIFT, AURORAAMAZONRDS, UNKNOWN;

  public static DataBaseType fromValue(String value) {
    for (DataBaseType dataBaseType : DataBaseType.values()) {
      if (dataBaseType.name().equalsIgnoreCase(value)) {
        return dataBaseType;
      }
    }
    return UNKNOWN;
  }
}
