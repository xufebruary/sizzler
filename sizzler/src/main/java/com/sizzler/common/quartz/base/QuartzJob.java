package com.sizzler.common.quartz.base;

import org.quartz.Job;

public abstract class QuartzJob implements Job {

  public abstract String getJobName();

}
