package org.apache.metamodel.googlead.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.googlead.model.AdWordsAPILocation;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 地理Map数据
 * @author you.zou by 2016.2.26
 *
 */
public class AdWordsMap {
  private static Map<String, AdWordsAPILocation> locationMaps =
      new HashMap<String, AdWordsAPILocation>();
  public static boolean hasInit = false;


  public static void init() {
    CSVReader csvReader = null;
    InputStream inputStream = null;
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      inputStream =
          classLoader.getResourceAsStream("AdWords_API_Location_Criteria_2015-10-13.properties");
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      csvReader = new CSVReader(reader);
      List<String[]> strList = csvReader.readAll();
      if (null != strList && !strList.isEmpty()) {
        int i = 0;
        for (String[] strArray : strList) {
          if (i <= 0) {
            i++;
            continue;
          }
          if (strArray != null && strArray.length > 0 && strArray.length == 7) {
            AdWordsAPILocation bean = new AdWordsAPILocation();
            bean.setCriteriaID(strArray[0]);
            bean.setName(strArray[1]);
            bean.setCanonicalName(strArray[2]);
            bean.setParentID(strArray[3]);
            bean.setCountryCode(strArray[4]);
            bean.setTargetType(strArray[5]);
            bean.setStatus(strArray[6]);
            locationMaps.put(strArray[0], bean);
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

  public static AdWordsAPILocation findById(String id) {
    if (!hasInit) {
      init();
    }
    return AdWordsMap.getLocationMaps().get(id);
  }

  public static Map<String, AdWordsAPILocation> getLocationMaps() {
    return locationMaps;
  }

  public static void setLocationMaps(Map<String, AdWordsAPILocation> locationMaps) {
    AdWordsMap.locationMaps = locationMaps;
  }


}
