package com.sizzler.common.reflect;

import java.lang.reflect.Method;

public class ReflectUtil {

  private static ParameterNameDiscoverWrapper parameterNameDiscover = new ParameterNameDiscoverWrapper();

  public static String[] getParameterNames(Method method) {
    return parameterNameDiscover.getParameterNames(method);
  }

  public static String methodKeyString(Method method) {
    StringBuilder stringBuilder = new StringBuilder("");
    stringBuilder.append(method.getDeclaringClass().getName()).append("#").append(method.getName());
    return stringBuilder.toString();
  }
}
