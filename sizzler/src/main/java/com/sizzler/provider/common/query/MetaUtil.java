package com.sizzler.provider.common.query;

import java.util.HashMap;
import java.util.Map;

import org.apache.metamodel.util.Oauth2Token;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.sizzler.DataBaseConnection;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.sizzler.UserConnectionConfig;

/**
 * Created by ptmind on 2015/12/8.
 */
public class MetaUtil {

  public static Oauth2Token createOauth2TokenByUserConnection(UserConnection userConnection) {
    return createConfigObjectByUserConnection(userConnection, Oauth2Token.class);
  }

  public static DataBaseConnection createDataBaseConnectionByUserConnection(
      UserConnection userConnection) {
    return createConfigObjectByUserConnection(userConnection, DataBaseConnection.class);
  }

  public static <T> T createConfigObjectByUserConnection(UserConnection userConnection,
      Class<T> tClass) {
    return createObjectByMap((Map<String, Object>) JSON.parse(userConnection.getConfig()), tClass);
  }

  public static <T> T createObjectByMap(Map<String, Object> config, Class<T> tClass) {
    String configJson = JSON.toJSONString(config);
    return JSON.parseObject(configJson, tClass);
  }

  public static void main(String[] args) {
    UserConnection userConnection = new UserConnection();
    Map<String, Object> config = new HashMap<>();
    config.put(UserConnectionConfig.DbConfig.DATABASETYPE, "MYSQL");
    config.put(UserConnectionConfig.DbConfig.HOST, "192.168.1.2");
    config.put(UserConnectionConfig.DbConfig.PORT, "3306");
    config.put(UserConnectionConfig.DbConfig.USER, "ptone");
    config.put(UserConnectionConfig.DbConfig.PASSWORD, "ptone");

    userConnection.setConfig(JSON.toJSONString(config));

    DataBaseConnection dataBaseConnection =
        createDataBaseConnectionByUserConnection(userConnection);
    System.out.println(dataBaseConnection);

  }

}
