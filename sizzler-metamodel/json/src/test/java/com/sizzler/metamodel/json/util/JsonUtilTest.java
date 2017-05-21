package com.sizzler.metamodel.json.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * JsonColumnUtil测试用例类
 * @author you.zou
 * @date 2017年1月18日 下午4:41:17
 */
public class JsonUtilTest {

  /**
   * validJsonColumnArrayIsComplete方法的测试用例
   * @author you.zou
   * @date 2017年1月18日 下午5:27:27
   * @throws Exception
   */
  @Test
  public void testValidJsonColumnArrayIsComplete() throws Exception{
    String[] jsonColumnArray = new String[]{"a1", "b2"};
    String[] paramColumnArray = new String[]{"a1", "b2"};
    String[] resultArray = null;
    //json列表为空
    resultArray = JsonUtil.validJsonColumnArrayIsComplete(null, paramColumnArray);
    Assert.assertArrayEquals(paramColumnArray, resultArray);
    
    //param列表为空
    resultArray = JsonUtil.validJsonColumnArrayIsComplete(jsonColumnArray, null);
    Assert.assertArrayEquals(jsonColumnArray, resultArray);
    
    //两者都为空
    resultArray = JsonUtil.validJsonColumnArrayIsComplete(null, null);
    Assert.assertArrayEquals(null, resultArray);
    
    //两者不为空，但两者内容相同
    resultArray = JsonUtil.validJsonColumnArrayIsComplete(jsonColumnArray, paramColumnArray);
    Assert.assertArrayEquals(jsonColumnArray, resultArray);
    
    //两者不为空，且两者内容不同
    paramColumnArray = new String[]{"a1", "c2"};
    resultArray = JsonUtil.validJsonColumnArrayIsComplete(jsonColumnArray, paramColumnArray);
    Assert.assertArrayEquals(new String[]{"a1", "b2", "c2"}, resultArray);
  }
  
  /**
   * validRowListByJsonColumnArray的测试用例
   * @author you.zou
   * @date 2017年1月18日 下午6:41:23
   * @throws Exception
   */
  @Test
  public void testValidRowListByJsonColumnArray() throws Exception{
    List<Object[]> rowList = new ArrayList<Object[]>();
    rowList.add(new Object[]{1, 2});
    rowList.add(new Object[]{3, 4});
    rowList.add(new Object[]{5, 6});
    rowList.add(new Object[]{7, 8});
    String[] jsonColumnArray = new String[]{"a1", "b2", "c3"};
    List<Object[]> resultList = null;
    
    resultList = JsonUtil.validRowListByJsonColumnArray(null, jsonColumnArray);
    Assert.assertEquals(null, resultList);
    
    resultList = JsonUtil.validRowListByJsonColumnArray(rowList, null);
    Assert.assertEquals(4, resultList.size());
    Assert.assertEquals(2, resultList.get(0).length);
    
    resultList = JsonUtil.validRowListByJsonColumnArray(null, null);
    Assert.assertEquals(null, resultList);
    
    resultList = JsonUtil.validRowListByJsonColumnArray(rowList, jsonColumnArray);
    Assert.assertEquals(4, resultList.size());
    Assert.assertEquals(3, resultList.get(0).length);
    
  }
  
}
