package com.sizzler.common.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.Constants;
import com.sizzler.common.utils.FileUtil;

/**
 * 资源文件帮助类，加载配置信息
 */
public class ConfigHelper {
  private static final Logger log = LoggerFactory.getLogger(ConfigHelper.class);
  private static Properties properties;
  // 容器参数
  private static Properties serverProperties;
  private static String filePath;

  /*
   * static { System.out.println("["+
   * DateUtil.getDateTime()+"] Loading config.properties"); try { filePath =
   * FileUtil.getFilePath("config.properties", Constants.Env.BASE_HOME); } catch
   * (Exception e) { //ignore }
   * 
   * //容器参数配置 serverProperties = loadProperties("jettyServer.properties");
   * //系统参数配置 properties = loadProperties("config.properties"); }
   */

  public static synchronized Properties loadPropertiesContent(String configFile) {
    Properties prop = new Properties();
    try {
      InputStreamReader input = new InputStreamReader(ConfigHelper.class.getClassLoader()
          .getResourceAsStream(configFile), "UTF-8");
      if (input != null) {
        prop.load(input);

        log.info(">>> Load " + configFile + " ... ");
        for (Map.Entry<Object, Object> entry : prop.entrySet()) {
          log.info("\t" + entry.getKey() + "=" + entry.getValue());
        }
      }
    } catch (Exception e) {
      log.error("Loading config.properties fails", e);
    }

    return prop;
  }

  public static Properties loadProperties(String fileName) {
    Properties prop = new Properties();
    try {
      // filePath =
      // FileUtil.getFilePath("config.properties",Constants.Env.BASE_HOME);
      InputStream input = new FileInputStream(FileUtil.getFile(fileName, Constants.Env.BASE_HOME));
      prop.load(input);
    } catch (Exception e) {
      log.error("Loading config.properties fails", e);
    }

    return prop;
  }

  /**
   * 加载系统参数
   * 
   * @param key
   * @return
   */
  public static String getJettyParameter(String key) {
    return serverProperties.getProperty(key);
  }

  public static String getValue(String key) {
    String value = null;
    try {
      value = properties.getProperty(key);
    } catch (Exception e) {
      log.error("key:" + key + " 资源参数加载失败！", e);
    }
    return value;

  }

  /**
     */
  public static void setProperties(String key, String value) {
    try {
      FileInputStream input = new FileInputStream(filePath);
      SafeProperties safeProp = new SafeProperties();
      safeProp.load(input);
      input.close();
      if (!"".equals(value) && value != null) {
        // safeProp.addComment("New Comment测试");
        safeProp.put(key, value);
      }
      if (key != null) {
        if (value == null || "".equals(value)) {
          safeProp.remove(key);
        }
      }
      FileOutputStream output = new FileOutputStream(filePath);
      safeProp.store(output, null);
      output.close();
    } catch (Exception e) {
      log.error("Visit " + filePath + " for updating " + key + " value error", e.getMessage(), e);
    }

  }

  /**
   * 删除
   * 
   * @param key
   */
  public static void removeProperties(String key) {
    try {
      FileInputStream input = new FileInputStream(filePath);
      SafeProperties safeProp = new SafeProperties();
      safeProp.load(input);
      input.close();
      if (key != null) {
        safeProp.remove(key);
      }
      FileOutputStream output = new FileOutputStream(filePath);
      safeProp.store(output, null);
      output.close();
    } catch (Exception e) {
      log.error("Visit " + filePath + " for updating " + key + " value error", e.getMessage(), e);
    }

  }

}
