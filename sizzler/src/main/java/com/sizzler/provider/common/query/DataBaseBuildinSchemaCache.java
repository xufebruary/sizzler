package com.sizzler.provider.common.query;

import com.sizzler.provider.common.db.DataBaseType;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ptmind on 2016/1/21.
 */
public class DataBaseBuildinSchemaCache {
  private static Map<DataBaseType, Set<String>> buildinSchemaMap = new LinkedHashMap<>();

  static {
    Set<String> mysqlBuildinSchemaSet = new HashSet<>();
    mysqlBuildinSchemaSet.add("information_schema");
    mysqlBuildinSchemaSet.add("mysql");
    mysqlBuildinSchemaSet.add("performance_schema");
    buildinSchemaMap.put(DataBaseType.MYSQL, mysqlBuildinSchemaSet);

    Set<String> mysqlAmazonRdsBuildinSchemaSet = new HashSet<>();
    mysqlAmazonRdsBuildinSchemaSet.add("information_schema");
    mysqlAmazonRdsBuildinSchemaSet.add("mysql");
    mysqlAmazonRdsBuildinSchemaSet.add("performance_schema");
    mysqlAmazonRdsBuildinSchemaSet.add("innodb");
    buildinSchemaMap.put(DataBaseType.MYSQLAMAZONRDS, mysqlAmazonRdsBuildinSchemaSet);

    Set<String> postgreBuildinSchemaSet = new HashSet<>();
    postgreBuildinSchemaSet.add("information_schema");
    postgreBuildinSchemaSet.add("pg_catalog");
    buildinSchemaMap.put(DataBaseType.POSTGRE, postgreBuildinSchemaSet);

    Set<String> redshiftBuildinSchemaSet = new HashSet<>();
    redshiftBuildinSchemaSet.add("information_schema");
    redshiftBuildinSchemaSet.add("pg_catalog");
    redshiftBuildinSchemaSet.add("pg_internal");
    buildinSchemaMap.put(DataBaseType.REDSHIFT, redshiftBuildinSchemaSet);
    buildinSchemaMap.put(DataBaseType.STANDARDREDSHIFT, redshiftBuildinSchemaSet);

    Set<String> sqlserverBuildinSchemaSet = new HashSet<String>();
    sqlserverBuildinSchemaSet.add("db_accessadmin");
    sqlserverBuildinSchemaSet.add("db_backupoperator");
    sqlserverBuildinSchemaSet.add("db_datareader");
    sqlserverBuildinSchemaSet.add("db_datawriter");
    sqlserverBuildinSchemaSet.add("db_ddladmin");
    sqlserverBuildinSchemaSet.add("db_denydatareader");
    sqlserverBuildinSchemaSet.add("db_denydatawriter");
    sqlserverBuildinSchemaSet.add("db_owner");
    sqlserverBuildinSchemaSet.add("db_securityadmin");
    sqlserverBuildinSchemaSet.add("INFORMATION_SCHEMA");
    sqlserverBuildinSchemaSet.add("sys");
    buildinSchemaMap.put(DataBaseType.SQLSERVER, sqlserverBuildinSchemaSet);

  }

  public static boolean isBuildinSchema(String dataBaseType, String schema) {
    Set<String> buildinSchemaSet = getBuildinSchemaSet(dataBaseType);
    if (buildinSchemaSet == null) {
      return false;
    } else {
      return buildinSchemaSet.contains(schema);
    }
  }

  public static Set<String> getBuildinSchemaSet(String dataBaseType) {
    return getBuildinSchemaSet(DataBaseType.fromValue(dataBaseType));
  }

  public static Set<String> getBuildinSchemaSet(DataBaseType dataBaseType) {
    return buildinSchemaMap.get(dataBaseType);
  }

  public Map<DataBaseType, Set<String>> getBuildinSchemaMap() {
    return buildinSchemaMap;
  }

  public void setBuildinSchemaMap(Map<DataBaseType, Set<String>> buildinSchemaMap) {
    DataBaseBuildinSchemaCache.buildinSchemaMap = buildinSchemaMap;
  }
}
