package com.sizzler.common.cache.expression;

import java.lang.reflect.Method;

public class CacheExpressionRoot {
  private Method method;
  private Object[] args;

  public CacheExpressionRoot(Method method, Object[] args) {
    this.method = method;
    this.args = args;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public Object[] getArgs() {
    return args;
  }

  public void setArgs(Object[] args) {
    this.args = args;
  }

}
