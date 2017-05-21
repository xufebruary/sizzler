package com.sizzler.common.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sizzler.common.lock.annotation.DistributedLock;
import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.utils.AspectUtil;
import com.sizzler.domain.panel.PtonePanelLayout;

/**
 * 分布式锁的AOP实现
 */
@Component
@Aspect
public class DistributedLockAspect {

  private static Logger logger = LoggerFactory.getLogger(DistributedLockAspect.class);

  private static final String LOG_PREFIX = "[DistributedLock] ";

  // @Autowired
  // private RedisService redisService;

  public DistributedLockAspect() {
    logger.info(LOG_PREFIX + "DistributedLockAspect init ...");
  }

  @Pointcut("@annotation(com.sizzler.common.lock.annotation.DistributedLock)")
  public void distributedLockPointcut() {
  }

  @Around("distributedLockPointcut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    LogMessage logMessage = new LogMessage();
    logMessage.setOperate("DistributedLock");
    String executeStatus = "success";

    DistributedLock distributedLock = null;
    String lockName = null;
    String lockKey = "";
    int lockTimeout = 0;
    long lockInterval = 0;
    long waitTime = 0;
    Object result = null;

    try {
      distributedLock = this.getDistributedLockAnnotation(point);
      lockName = distributedLock.name();
      lockTimeout = distributedLock.timeout();
      lockInterval = distributedLock.interval();

      lockKey = this.buildLockKey(lockName, point);

      // 加锁
      if (lockKey != null) {
        logger.info(LOG_PREFIX + "add distributed lock<" + lockName + "> lockKey:" + lockKey
            + ", timeout:" + lockTimeout + ", interval:" + lockInterval);
//        int waitCount = 1;
//        while (!redisService.addLockIfNotExsit(lockKey, lockKey, lockTimeout)) {
//          waitTime = waitCount * lockInterval;
//          logger.info(LOG_PREFIX + "waitting for distributed lock<" + lockName + "> lockKey:"
//              + lockKey + " count:" + waitCount + "  , time:" + waitTime);
//          waitCount++;
//          Thread.sleep(lockInterval); // 休眠等待锁
//        }
      } else {
        logger.warn(LOG_PREFIX + "not add distributed lock<" + lockName
            + "> lockKey is null, timeout:" + lockTimeout + ", interval:" + lockInterval);
      }

      // 执行程序
      result = point.proceed(point.getArgs());

    } catch (Exception e) {
      executeStatus = "error";
      logger.error(LOG_PREFIX + "Occur exception during distributed lock<" + lockName
          + "> lockKey:" + lockKey + ", timeout:" + lockTimeout + ", interval:" + lockInterval, e);
      throw e;
    } finally {
      try {
        // 释放锁
        logger.info(LOG_PREFIX + "release distributed lock<" + lockName + "> lockKey:" + lockKey
            + ", timeout:" + lockTimeout + ", interval:" + lockInterval);
//        if (redisService.existsKey(lockKey)) {
//          redisService.remove(lockKey);
//        }
      } catch (Exception e) {
        executeStatus = "error";
        logger
            .error(LOG_PREFIX + "Occur exception when release distributed lock<" + lockName
                + "> lockKey:" + lockKey + ", timeout:" + lockTimeout + ", interval:"
                + lockInterval, e);
      } finally {
        logMessage.addOperateInfo("className", AspectUtil.getJoinPointClassName(point));
        logMessage.addOperateInfo("methodName", AspectUtil.getJoinPointMethodName(point));
        logMessage.addOperateInfo("executeStatus", executeStatus);
        logMessage.addOperateInfo("lockName", lockName);
        logMessage.addOperateInfo("lockKey", lockKey);
        logMessage.addOperateInfo("lockTimeout", lockTimeout);
        logMessage.addOperateInfo("lockInterval", lockInterval);
        logMessage.addOperateInfo("waitTime", waitTime);
        logger.info(logMessage.toString());
        ElkLogUtil.info(logMessage.generateJsonString());
      }
    }

    return result;
  }

  /**
   * 创建分布式锁的key
   */
  private String buildLockKey(String lockName, ProceedingJoinPoint point) {
    String lockKey = null;
    if (DistributedLockConstants.DISTRIBUTED_LOCK_PANEL_LAYOUT.equalsIgnoreCase(lockName)) {
      String spaceId = AspectUtil.getParamValue(point, "spaceId", String.class);
      lockKey = PtonePanelLayout.getPanelLayoutKey(spaceId);
    }
    return lockKey;
  }

  private DistributedLock getDistributedLockAnnotation(ProceedingJoinPoint point) {
    return AspectUtil.getMethodAnnotation(point, DistributedLock.class);
  }

}
