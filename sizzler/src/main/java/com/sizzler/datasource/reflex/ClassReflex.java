package com.sizzler.datasource.reflex;

import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;

/**
 * @ClassName: ClassReflex
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2015/12/25
 * @author: zhangli
 */
public class ClassReflex {

  public static Class bulidClass(String className) throws ClassNotFoundException {
    return Class.forName("com.sizzler.datasource.proxy." + className + "." + captureName(className) + "Handle");
  }

  public static Method bulidMethod(Class proxyClass, String methodName, Class clazz[]) throws NoSuchMethodException {
    return proxyClass.getDeclaredMethod(methodName, clazz);
  }

  public static <T> T invokeValue(String className, String methodName, Class clazz[], Object... args) throws Exception {
    Class proxyClass = bulidClass(className);
    Method proxyMethod = bulidMethod(proxyClass, methodName, clazz);
    return (T) proxyMethod.invoke(proxyClass.newInstance(), args);
  }

  public static String captureName(String name) {
    char[] cs = name.toCharArray();
    cs[0] -= 32;
    return String.valueOf(cs);

  }

}
