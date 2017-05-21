package com.sizzler.common.quartz.base;

public abstract class QuartzTask {

  public abstract String getTaskName();

  public abstract String getTaskCronExpr();

  public abstract String getJobName();

  public abstract Class<? extends QuartzJob> getJobClazz();

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("QuartzTask [");
    sb.append("taskName=").append(this.getTaskName()).append(",");
    sb.append("taskCronExpr=").append(this.getTaskCronExpr()).append(",");
    sb.append("jobName=").append(this.getJobName()).append(",");
    sb.append("]");
    return sb.toString();
  }

}
