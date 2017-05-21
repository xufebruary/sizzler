package com.sizzler.common.utils;

import java.lang.annotation.Annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class AspectUtil {

  @SuppressWarnings("unchecked")
  public static <T> T getParamValue(ProceedingJoinPoint point, String paramName,
      Class<T> paramValueClass) {
    T paramValue = null;
    MethodSignature methodSignature = (MethodSignature) point.getStaticPart().getSignature();
    String[] paramNames = methodSignature.getParameterNames();
    int paramIndex = -1;
    String tmpParamName = "";
    for (int index = 0; index < paramNames.length; index++) {
      tmpParamName = paramNames[index];
      if (tmpParamName.equalsIgnoreCase(paramName)) {
        paramIndex = index;
        break;
      }
    }
    if (paramIndex != -1) {
      paramValue = (T) point.getArgs()[paramIndex];
    }
    return paramValue;
  }

  public static <T extends Annotation> T getMethodAnnotation(ProceedingJoinPoint point, Class<T> annotationClass) {
    MethodSignature methodSignature = (MethodSignature) point.getStaticPart().getSignature();
    T annotation = methodSignature.getMethod().getAnnotation(annotationClass);
    return annotation;
  }

  public static String getJoinPointClassName(ProceedingJoinPoint point) {
    return point.getTarget().getClass().getName();
  }

  public static String getJoinPointMethodName(ProceedingJoinPoint point) {
    MethodSignature methodSignature = (MethodSignature) point.getSignature();
    return methodSignature.getName();
  }

}
