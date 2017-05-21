package com.sizzler.common.reflect;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

public class ParameterNameDiscoverWrapper {

  private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

  public String[] getParameterNames(Method method) {

    return parameterNameDiscoverer.getParameterNames(method);
  }
}
