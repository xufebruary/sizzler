package com.sizzler.common.utils;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA. User: zhangli Date: 14-6-12 Time: 下午3:54 To
 * change this template use File | Settings | File Templates.
 */
public class MathsUtil {

  /**
   * double 除法
   * 
   * @param d1
   * @param d2
   * @return
   */
  public static double div(double d1, double d2) {

    BigDecimal bd1 = new BigDecimal(Double.toString(d1));
    BigDecimal bd2 = new BigDecimal(Double.toString(d2));
    if (d2 == 0) {
      return 0;
    } else {
      return bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
  }
}
