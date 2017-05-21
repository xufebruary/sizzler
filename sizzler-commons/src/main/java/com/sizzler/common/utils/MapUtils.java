package com.sizzler.common.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtils {
  /**
   * 将Map<String, String[]>转为Map<String, String>
   * 
   * @param dest
   * @param src
   */
  public static void populate(Map<String, String> dest, Map<String, String[]> src) {
    Iterator<Entry<String, String[]>> it = src.entrySet().iterator();
    while (it.hasNext()) {
      Entry<String, String[]> e = it.next();
      dest.put(e.getKey(), e.getValue()[0]);
    }
  }

  /**
   * 将a=2&b=3这种String转换为map
   * 
   * @param q
   * @return
   */
  public static Map<String, String> string2Map(String q) {
    Map<String, String> params = new HashMap<String, String>();
    if (q != null && q.length() > 0) {
      String[] pairs = q.split("&");
      for (String s : pairs) {
        String[] kv = s.split("=");
        if (kv != null && kv.length == 2) {
          params.put(kv[0], kv[1]);
        } else {
          params.put(kv[0], null);
        }
      }
    }
    return params;
  }
}
