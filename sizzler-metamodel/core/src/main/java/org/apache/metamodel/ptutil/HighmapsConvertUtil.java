package org.apache.metamodel.ptutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Highmaps 国家信息转换工具类
 *
 * @author peng.xu
 */
public class HighmapsConvertUtil {

  private static Map<String, HighmapsCountryInfo> isoA2ToCountryInfoMaps =
      new HashMap<String, HighmapsCountryInfo>();
  private static Map<String, HighmapsCountryInfo> isoA3ToCountryInfoMaps =
      new HashMap<String, HighmapsCountryInfo>();
  private static Map<String, HighmapsCountryInfo> nameToCountryInfoMaps =
      new HashMap<String, HighmapsCountryInfo>();
  private static Map<String, HighmapsCountryInfo> abbrevToCountryInfoMaps =
      new HashMap<String, HighmapsCountryInfo>();
  public static boolean hasInit = false;

  public static void init() {
    InputStream inputStream = null;
    CSVReader csvReader = null;
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      inputStream = classLoader.getResourceAsStream("Highmaps.custom-world.1.0.0.properties");
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      csvReader = new CSVReader(reader);
      List<String[]> strList = csvReader.readAll();
      if (null != strList && !strList.isEmpty()) {
        for (int i = 1; i < strList.size(); i++) { // 第一行为表头
          String[] strArray = strList.get(i);
          if (strArray != null && strArray.length > 0) {
            HighmapsCountryInfo info = new HighmapsCountryInfo();
            info.setId(strArray[0]);
            info.setIsoA2(strArray[1]);
            info.setIsoA3(strArray[2]);
            info.setName(strArray[3]);
            info.setCountryAbbrev(strArray[4]);
            info.setWoeId(strArray[5]);
            if (!"-99".equals(info.getIsoA2())) {
              isoA2ToCountryInfoMaps.put(info.getIsoA2(), info);
            }
            if (!"-99".equals(info.getIsoA3())) {
              isoA3ToCountryInfoMaps.put(info.getIsoA3(), info);
            }
            if (StringUtil.isNotBlank(info.getName())) {
              nameToCountryInfoMaps.put(info.getName(), info);
            }
            if (StringUtil.isNotBlank(info.getCountryAbbrev())) {
              abbrevToCountryInfoMaps.put(info.getCountryAbbrev(), info);
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (inputStream != null) {
          inputStream.close();
        }
        if (csvReader != null) {
          csvReader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    hasInit = true;
  }

  public static HighmapsCountryInfo getCountryInfoByIsoA2(String isoA2) {
    if (isoA2ToCountryInfoMaps.isEmpty()) {
      init();
    }
    return isoA2ToCountryInfoMaps.get(isoA2);
  }

  public static HighmapsCountryInfo getCountryInfoByIsoA3(String isoA3) {
    if (isoA3ToCountryInfoMaps.isEmpty()) {
      init();
    }
    return isoA3ToCountryInfoMaps.get(isoA3);
  }

  public static HighmapsCountryInfo getCountryInfoByName(String name) {
    if (nameToCountryInfoMaps.isEmpty()) {
      init();
    }
    return nameToCountryInfoMaps.get(name);
  }

  public static HighmapsCountryInfo getCountryInfoByAbbrev(String abbrev) {
    if (abbrevToCountryInfoMaps.isEmpty()) {
      init();
    }
    return abbrevToCountryInfoMaps.get(abbrev);
  }

}
