package com.sizzler.common.cache.expression;

import java.lang.reflect.Method;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.sizzler.common.reflect.ReflectUtil;

public class CacheEvaluationContext extends StandardEvaluationContext {

  private Method method;
  private Object[] args;

  public CacheEvaluationContext(Object root, Method method, Object[] args) {
    super(root);
    this.method = method;
    this.args = args;
  }

  @Override
  public Object lookupVariable(String name) {
    Object value = super.lookupVariable(name);
    return value;
  }

  public void loadArgsAsVariable() {
    if (args != null && args.length > 0) {
      String[] parameterNames = ReflectUtil.getParameterNames(method);
      for (int i = 0; i < parameterNames.length; i++) {
        super.setVariable(parameterNames[i], args[i]);
      }
    }
  }
}
