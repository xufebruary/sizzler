package com.ptmind.ptone.metamodel.json.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.metamodel.util.CollectionUtils;

/**
 * 列处理的工具类
 * @author you.zou
 * @date 2017年1月18日 下午4:17:11
 */
public class JsonUtil {

  /**
   * 验证从Json串中剥离出来的列列表是否完整的包含了paramColumnArray中的所有列<br>
   * 包含：直接返回jsonColumnArray<br>
   * 不包含：将没有包含的列从paramColumnArray中增加到jsonColumnArray中<br>
   * @author you.zou
   * @date 2017年1月18日 下午4:21:33
   * @param jsonColumnArray Json串中剥离出来的列列表
   * @param paramColumnArray 参数传递过来的列列表
   * @return
   */
  public static String[] validJsonColumnArrayIsComplete(String[] jsonColumnArray,
      String[] paramColumnArray) {
    if (ArrayUtils.isEmpty(paramColumnArray)) {
      // 当参数传递过来的列列表为空的时候，不需要再验证了，直接返回jsonColumnArray
      return jsonColumnArray;
    }
    if (ArrayUtils.isEmpty(jsonColumnArray)) {
      // 当Json串中剥离出来的列列表为空的时候，也不需要验证了，直接返回paramColumnArray
      return paramColumnArray;
    }
    List<String> tempJsonColumnArray = new ArrayList<String>(Arrays.asList(jsonColumnArray)); 
    // 开始验证
    for (String paramColumn : paramColumnArray) {
      boolean isContain = false;
      for (String jsonColumn : jsonColumnArray) {
        if (paramColumn.equals(jsonColumn)) {
          isContain = true;
          break;
        }
      }
      if(!isContain){
        //没有包含进去，需要添加到jsonColumnArray中
        tempJsonColumnArray.add(paramColumn);
      }
    }
    return tempJsonColumnArray.toArray(new String[]{});
  }

  /**
   * 验证每行数据中的长度是否与jsonColumnArray的长度一致<br>
   * 一致：该行数据不做处理<br>
   * 不一致：该行数据补空操作<br>
   * @author you.zou
   * @date 2017年1月18日 下午6:29:50
   * @param rowList
   * @param jsonColumnArray
   * @return
   */
  public static List<Object[]> validRowListByJsonColumnArray(List<Object[]> rowList, String[] jsonColumnArray){
    if (ArrayUtils.isEmpty(jsonColumnArray)) {
      return rowList;
    }
    if(CollectionUtils.isNullOrEmpty(rowList)){
      return rowList;
    }
    int jsonColumnLength = jsonColumnArray.length;
    int currentIndex = 0;
    for(Object[] row : rowList){
      int rowLength = row.length;
      int subLength = jsonColumnLength - rowLength;
      if(subLength != 0){
        List<Object> tempRow = new ArrayList<Object>(Arrays.asList(row)); 
        //根据subLength看看要补充几个数据
        for(int i=1; i<=subLength; i++){
          tempRow.add("");
        }
        rowList.set(currentIndex, tempRow.toArray(new Object[]{}));
      }
      currentIndex++;
    }
    
    return rowList;
  }
  
}
