package com.sizzler.common.cache.expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * 参照 Spring
 * Cache中的org.springframework.cache.interceptor.ExpressionEvaluator来实现的
 */
public class CacheExpressionProcessor {

  private ExpressionParser parser = new SpelExpressionParser();
  // key的表达式的缓存
  private Map<String, Expression> keyCache = new ConcurrentHashMap<String, Expression>();
  // condition的表达式的缓存
  private Map<String, Expression> conditionCache = new ConcurrentHashMap<String, Expression>();

  public EvaluationContext createExpressionContext(Method method, Object[] args) {
    CacheExpressionRoot cacheExpressionRoot = new CacheExpressionRoot(method, args);
    CacheEvaluationContext evaluationContext = new CacheEvaluationContext(cacheExpressionRoot,
        method, args);
    evaluationContext.loadArgsAsVariable();
    return evaluationContext;
  }

  public Object evaluateKey(String keyExpression, Method method, Object[] args) {
    return getExpression(keyCache, keyExpression, method).getValue(
        createExpressionContext(method, args));
  }

  public boolean evaluateCondition(String conditionExpression, Method method, Object[] args) {
    return getExpression(conditionCache, conditionExpression, method).getValue(
        createExpressionContext(method, args), Boolean.TYPE).booleanValue();
  }

  private Expression getExpression(Map<String, Expression> expressionCache, String keyExpression,
      Method method) {
    String key = keyString(keyExpression, method);
    Expression expression = expressionCache.get(key);
    if (expression == null) {
      expression = parser.parseExpression(keyExpression);
      expressionCache.put(key, expression);
    }
    return expression;

  }

  private String keyString(String keyExpression, Method method) {
    StringBuilder stringBuilder = new StringBuilder("");
    stringBuilder.append(method.getDeclaringClass().getName()).append("#")
        .append(method.toString()).append("#").append(keyExpression);
    return stringBuilder.toString();
  }

}
