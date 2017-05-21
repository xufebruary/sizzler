package com.sizzler.common.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.quartz.base.QuartzTask;

public class QuartzManager {

  private static Logger log = LoggerFactory.getLogger(QuartzManager.class);

  private static final String LOG_PREFIX = "[QuartzManager] ";

  private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();

  private static QuartzManager instance = new QuartzManager();

  public static QuartzManager getInstance() {
    return instance;
  }

  /**
   * 添加一个定时任务
   * @param task
   * @date 2016年11月2日
   * @author peng.xu
   */
  public void addCronTask(QuartzTask task) {
    if (task == null) {
      log.warn(LOG_PREFIX + "add a null task");
      return;
    }

    try {
      // 获取调度器
      Scheduler scheduler = schedulerFactory.getScheduler();

      JobKey jobKey = JobKey.jobKey(task.getTaskName(), task.getJobName());
      if (!scheduler.checkExists(jobKey)) {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(QuartzConstants.QUARTZ_TASK, task);

        // 构建调度任务：任务名，任务组，任务执行类
        JobDetail jobDetail =
            JobBuilder.newJob(task.getJobClazz()).setJobData(dataMap)
                .withIdentity(task.getTaskName(), task.getJobName()).build();

        // 表达式调度构建器：设定触发器时间cron表达式
        CronScheduleBuilder cronScheduleBuilder =
            CronScheduleBuilder.cronSchedule(task.getTaskCronExpr());

        // 构建调度任务触发器：任务名，任务组，触发器时间
        CronTrigger trigger =
            TriggerBuilder.newTrigger().withIdentity(task.getTaskName(), task.getJobName())
                .withSchedule(cronScheduleBuilder).build();

        // 添加调度任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 启动调度器:
        // When a scheduler is first created it is in "stand-by" mode, and will not fire triggers.
        if (scheduler.isInStandbyMode()) {
          scheduler.start();
        }
        log.info(LOG_PREFIX + "add new task success ： " + task.toString());
      } else {
        log.info(LOG_PREFIX + "add task has exist, execute update ： " + task.toString());
        this.updateCronTask(task);
      }

    } catch (Exception e) {
      log.error(LOG_PREFIX + "add new task error: " + e.getMessage() + " , " + task.toString(), e);
      throw new RuntimeException(e);
    }
  }

  /**
   * 修改一个任务的触发时间(使用默认的任务组名, 触发器名, 触发器组名)
   * @param task
   * @date 2016年11月2日
   * @author peng.xu
   */
  public void updateCronTask(QuartzTask task) {
    if (task == null) {
      log.warn(LOG_PREFIX + "update a null task");
      return;
    }

    try {
      Scheduler scheduler = schedulerFactory.getScheduler();
      TriggerKey triggerKey = TriggerKey.triggerKey(task.getTaskName(), task.getJobName());
      CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
      if (trigger == null) {
        JobKey jobKey = JobKey.jobKey(task.getTaskName(), task.getJobName());
        if (scheduler.checkExists(jobKey)) {
          CronScheduleBuilder cronScheduleBuilder =
              CronScheduleBuilder.cronSchedule(task.getTaskCronExpr());
          trigger =
              TriggerBuilder.newTrigger().withIdentity(task.getTaskName(), task.getJobName())
                  .withSchedule(cronScheduleBuilder).build();
          // 按新的trigger重新设置job执行
          scheduler.rescheduleJob(triggerKey, trigger);
        } else {
          log.info(LOG_PREFIX + "update task not exist, execute add ： " + task.toString());
          this.addCronTask(task);
        }
      } else {
        String oldCronExpr = trigger.getCronExpression();
        String newCronExpr = task.getTaskCronExpr();
        if (!oldCronExpr.equalsIgnoreCase(newCronExpr)) {
          // trigger已存在，则更新相应的定时设置
          CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(newCronExpr);
          // 按新的cronExpression表达式重新构建trigger
          trigger =
              trigger.getTriggerBuilder().withIdentity(triggerKey)
                  .withSchedule(cronScheduleBuilder).build();
          // 按新的trigger重新设置job执行
          scheduler.rescheduleJob(triggerKey, trigger);
        }
      }

      log.info(LOG_PREFIX + "updete task success ： " + task.toString());

    } catch (Exception e) {
      log.error(LOG_PREFIX + "update task error: " + e.getMessage() + " , " + task.toString(), e);
      throw new RuntimeException(e);
    }
  }

  /**
   * 移除一个任务(使用默认的任务组名, 触发器名, 触发器组名)
   * @param task
   * @date: 2016年11月2日
   * @author peng.xu
   */
  public void removeCronTask(QuartzTask task) {
    if (task == null) {
      log.warn(LOG_PREFIX + "remove a null task");
      return;
    }
    try {
      Scheduler scheduler = schedulerFactory.getScheduler();
      TriggerKey triggerKey = TriggerKey.triggerKey(task.getTaskName(), task.getJobName());
      JobKey jobKey = JobKey.jobKey(task.getTaskName(), task.getJobName());

      scheduler.pauseTrigger(triggerKey);// 停止触发器
      scheduler.unscheduleJob(triggerKey);// 移除触发器

      scheduler.pauseJob(jobKey);// 停止任务
      scheduler.deleteJob(jobKey);// 删除任务

      log.info(LOG_PREFIX + "remove task success ： " + task.toString());

    } catch (Exception e) {
      log.info(LOG_PREFIX + "remove task error: " + e.getMessage() + " , " + task.toString(), e);
      throw new RuntimeException(e);
    }
  }

  /**
   * 启动所有定时任务
   * @date 2016年11月2日
   * @author peng.xu
   */
  public void startAllTasks() {
    try {
      Scheduler scheduler = schedulerFactory.getScheduler();
      scheduler.start();
      log.info(LOG_PREFIX + "start all tasks success ");
    } catch (Exception e) {
      log.info(LOG_PREFIX + "start all tasks error: " + e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  /**
   * 关闭所有定时任务
   * @date 2016年11月2日
   * @author peng.xu
   */
  public void shutdownAllTasks() {
    try {
      Scheduler scheduler = schedulerFactory.getScheduler();
      if (!scheduler.isShutdown()) {
        scheduler.shutdown();
      }
      log.info(LOG_PREFIX + "shutdown all tasks success ");
    } catch (Exception e) {
      log.info(LOG_PREFIX + "start all tasks error: " + e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

}
