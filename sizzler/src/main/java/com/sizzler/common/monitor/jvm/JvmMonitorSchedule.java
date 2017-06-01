package com.sizzler.common.monitor.jvm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.sizzler.common.monitor.jvm.domain.JvmMonitorInfo;
import com.sizzler.common.monitor.jvm.util.JvmUtil;
import com.sizzler.common.monitor.service.MonitorStatService;

public class JvmMonitorSchedule extends TimerTask {

  private static MonitorStatService monitorStatService;
  private static String applicateName;
  private static long statInterval = 1000 * 10;
  private static boolean activeFlag = true;
  private static String logPath;

  private static SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyyMMdd");
  private static SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static FileWriter writer;
  private static long nextTryTime = 0;
  private static String currentLogFileName;

  public static void main(String[] args) {
    start();
  }

  public static void start() {
    if (activeFlag) {
      Timer timer = new Timer();
      timer.schedule(new JvmMonitorSchedule(), 0, statInterval);
    }
  }

  @Override
  public void run() {
    this.statJvmInfo();
  }

  public void statJvmInfo() {
    JvmMonitorInfo currentJvmInfo = null;
    try {
      currentJvmInfo = JvmUtil.getJvmInfo();
      if (currentJvmInfo == null) {
        return;
      }
      currentJvmInfo.setApplicationName(applicateName);
      if (monitorStatService != null) {
        monitorStatService.saveJvmMonitorInfo(currentJvmInfo);
      }
      if (logPath != null && !"".equals(logPath.trim()) && nextTryTime < System.currentTimeMillis()) {
        try {
          String dateStr = fileDateFormat.format(System.currentTimeMillis());
          String fileName = logPath + (logPath.endsWith("/") ? "" : "/") + "jvm-monitor-" + dateStr
              + ".log";
          if (writer == null || !fileName.equals(currentLogFileName)) {
            if (writer != null) {
              writer.close();
              Thread.sleep(200);
            }
            try {
              File file = new File(fileName);
              if (!file.exists()) {
                // 如果目标文件所在的目录不存在，则创建父目录
                if (!file.getParentFile().exists()) {
                  file.getParentFile().mkdirs();
                }
              }
              
              writer = new FileWriter(fileName, true);
              currentLogFileName = fileName;
            } catch (Exception e) {
              System.out.println(currentJvmInfo);
              e.printStackTrace();
              nextTryTime = System.currentTimeMillis() + 10 * 60 * 1000;
            }
          }
          if (writer != null) {
            String logTime = "[" + logDateFormat.format(System.currentTimeMillis()) + "] ";
            writer.write(logTime + currentJvmInfo.toString() + "\n");
          }
        } catch (IOException e) {
          System.out.println(currentJvmInfo);
          e.printStackTrace();
          nextTryTime = System.currentTimeMillis() + 10 * 60 * 1000;
        }
      }
    } catch (Exception e) {
      System.out.println(currentJvmInfo);
      e.printStackTrace();
    }
  }

  public static MonitorStatService getMonitorStatService() {
    return monitorStatService;
  }

  public static void setMonitorStatService(MonitorStatService monitorStatService) {
    JvmMonitorSchedule.monitorStatService = monitorStatService;
  }

  public static String getApplicateName() {
    return applicateName;
  }

  public static void setApplicateName(String applicateName) {
    JvmMonitorSchedule.applicateName = applicateName;
  }

  public static long getStatInterval() {
    return statInterval;
  }

  public static void setStatInterval(long statInterval) {
    JvmMonitorSchedule.statInterval = statInterval;
  }

  public static boolean isActiveFlag() {
    return activeFlag;
  }

  public static void setActiveFlag(boolean activeFlag) {
    JvmMonitorSchedule.activeFlag = activeFlag;
  }

  public static String getLogPath() {
    return logPath;
  }

  public static void setLogPath(String logPath) {
    JvmMonitorSchedule.logPath = logPath;
  }

}
