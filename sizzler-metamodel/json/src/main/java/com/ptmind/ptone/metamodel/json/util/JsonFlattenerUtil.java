package com.ptmind.ptone.metamodel.json.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by ptmind on 2016/6/13.
 */
public class JsonFlattenerUtil {

  private final static Logger logger = LoggerFactory.getLogger(JsonFlattenerUtil.class);

  public static List<LinkedHashMap<String, Object>> parseJson(String json) {
    List<LinkedHashMap<String, Object>> rowList = new ArrayList<LinkedHashMap<String, Object>>();
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonNode = mapper.readTree(json);
      if (jsonNode.isArray()) {
        rowList = parseArray(jsonNode);
      } else {
        rowList.add(parse(jsonNode));
      }

    } catch (Exception e) {
      logger.info("parse json error: {}", json);
      e.printStackTrace();
    }
    return rowList;
  }

  public static LinkedHashMap<String, Object> parse(JsonNode jsonObject) {
    LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
    row =
        (LinkedHashMap) com.github.wnameless.json.flattener.JsonFlattener.flattenAsMap(jsonObject
            .toString());
    return row;
  }

  public static List<LinkedHashMap<String, Object>> parseArray(JsonNode jsonArray) {
    List<LinkedHashMap<String, Object>> rowList = new ArrayList<LinkedHashMap<String, Object>>();
    int length = jsonArray.size();
    for (int i = 0; i < length; i++) {
      JsonNode jsonObject = jsonArray.get(i);
      LinkedHashMap<String, Object> row = parse(jsonObject);
      rowList.add(row);
    }
    return rowList;
  }

  public static LinkedHashSet<String> getColumnNameSet(List<LinkedHashMap<String, Object>> rowList) {
    LinkedHashSet<String> columnNameSet = new LinkedHashSet<String>();
    for (LinkedHashMap<String, Object> row : rowList) {
      for (Map.Entry<String, Object> entry : row.entrySet()) {
        String key = entry.getKey();
        columnNameSet.add(key);
      }
    }

    return columnNameSet;
  }

  public static List<Object[]> getRowList(List<LinkedHashMap<String, Object>> rowList,
      LinkedHashSet<String> columnNameSet) {
    // List<ArrayList<String>> resultRowList=new ArrayList<ArrayList<String>>();
    List<Object[]> resultRowList = new ArrayList<Object[]>();
    for (LinkedHashMap<String, Object> row : rowList) {
      ArrayList<Object> tmpRowList = new ArrayList<Object>();
      for (String columnName : columnNameSet) {
        Object val = null;
        if (row.containsKey(columnName)) {
          if (row.get(columnName) != null && !row.get(columnName).equals("")) {
            // System.out.println(row.get(columnName));
            val = row.get(columnName);
          }
        } else {
          val = "";
        }
        tmpRowList.add(val);
      }
      resultRowList.add(tmpRowList.toArray());
    }
    return resultRowList;
  }
}
