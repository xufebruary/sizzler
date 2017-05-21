package com.sizzler.system.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

public class ServiceLogAspect {

  private static Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

  @SuppressWarnings("unused")
  public Object methodExecuteTime(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getSignature().getDeclaringType().getName();
    Object params[] = joinPoint.getArgs();
    StopWatch stopWatch = new StopWatch(methodName);
    stopWatch.start();
    try {
      return joinPoint.proceed(joinPoint.getArgs());
    } catch (Exception err) {
      throw err;
    } finally {
      stopWatch.stop();
      Long respTime = stopWatch.getTotalTimeMillis();
      logger.info("ServiceMethodExecuteTime:" + className + ":" + methodName + " takes " + respTime
          + "ms");
    }
  }

}
