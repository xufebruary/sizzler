package com.sizzler.provider.common.query;

import com.sizzler.provider.common.db.DataBaseType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ptmind on 2015/11/11.
 */
public class DataBaseDriverCache {

  private static Map<DataBaseType, DataBaseDriverInfo> driverInfoMap = new LinkedHashMap<>();

  static {
    DataBaseDriverInfo mysqlDriverInfo = new DataBaseDriverInfo();
    mysqlDriverInfo.setDefaultPort("3306");
    mysqlDriverInfo.setDriver("com.mysql.jdbc.Driver");
    mysqlDriverInfo.setUrlPrefix("jdbc:mysql://");
    mysqlDriverInfo.setUrlParam("?zeroDateTimeBehavior=convertToNull");

    driverInfoMap.put(DataBaseType.MYSQL, mysqlDriverInfo);
    driverInfoMap.put(DataBaseType.MYSQLAMAZONRDS, mysqlDriverInfo);
    driverInfoMap.put(DataBaseType.AURORAAMAZONRDS, mysqlDriverInfo);

    DataBaseDriverInfo postgreDriverInfo = new DataBaseDriverInfo();
    postgreDriverInfo.setDefaultPort("5432");
    postgreDriverInfo.setDriver("org.postgresql.Driver");
    postgreDriverInfo.setUrlPrefix("jdbc:postgresql://");
    postgreDriverInfo.setUrlParam("?OpenSourceSubProtocolOverride=true");

    driverInfoMap.put(DataBaseType.POSTGRE, postgreDriverInfo);

    DataBaseDriverInfo redshiftDriverInfo = new DataBaseDriverInfo();
    redshiftDriverInfo.setDefaultPort("5439");
    redshiftDriverInfo.setDriver("com.amazon.redshift.jdbc41.Driver");
    redshiftDriverInfo.setUrlPrefix("jdbc:redshift://");

    driverInfoMap.put(DataBaseType.REDSHIFT, redshiftDriverInfo);
    driverInfoMap.put(DataBaseType.STANDARDREDSHIFT, redshiftDriverInfo);

    // SQL Server
    DataBaseDriverInfo sqlServerInfo = new DataBaseDriverInfo();
    sqlServerInfo.setDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    sqlServerInfo.setDefaultPort("1433");
    sqlServerInfo.setUrlPrefix("jdbc:sqlserver://");
    driverInfoMap.put(DataBaseType.SQLSERVER, sqlServerInfo);

  }

  public static DataBaseDriverInfo getDataBaseDriverInfo(DataBaseType dataBaseType) {
    return driverInfoMap.get(dataBaseType);
  }

  public Map<DataBaseType, DataBaseDriverInfo> getDriverInfoMap() {
    return driverInfoMap;
  }

  public void setDriverInfoMap(Map<DataBaseType, DataBaseDriverInfo> driverInfoMap) {
    DataBaseDriverCache.driverInfoMap = driverInfoMap;
  }


}
