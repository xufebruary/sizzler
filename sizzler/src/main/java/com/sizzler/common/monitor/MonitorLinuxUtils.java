package com.sizzler.common.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * linux 下cpu 内存 磁盘 jvm的使用监控
 */
public class MonitorLinuxUtils {

  private MonitorLinuxUtils() {}

  public static MonitorLinuxUtils build() {
    return new MonitorLinuxUtils();
  }

  /**
   * 获取cpu使用率（%）
   * 
   * @return
   * @throws Exception
   */
  public double getCpuUsage() throws Exception {
    double cpuUsed = 0;

    Runtime rt = Runtime.getRuntime();
    Process p = rt.exec("top -b -n 1");// 调用系统的“top"命令

    BufferedReader in = null;
    try {
      in = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String str = null;
      String[] strArray = null;

      while ((str = in.readLine()) != null) {
        int m = 0;

        if (str.indexOf(" R ") != -1) {// 只分析正在运行的进程，top进程本身除外 &&

          strArray = str.split(" ");
          for (String tmp : strArray) {
            if (tmp.trim().length() == 0)
              continue;
            if (++m == 9) {// 第9列为CPU的使用百分比(RedHat

              cpuUsed += Double.parseDouble(tmp);

            }

          }

        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      in.close();
    }
    return cpuUsed * 100;
  }

  /**
   * 内存使用率（%）
   * 
   * @return
   * @throws Exception
   */
  public double getMemUsage() throws Exception {

    double menUsed = 0;
    Runtime rt = Runtime.getRuntime();
    Process p = rt.exec("top -b -n 1");// 调用系统的“top"命令

    BufferedReader in = null;
    try {
      in = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String str = null;
      String[] strArray = null;

      while ((str = in.readLine()) != null) {
        int m = 0;

        if (str.indexOf(" R ") != -1) {// 只分析正在运行的进程，top进程本身除外 &&
          //
          // System.out.println("------------------3-----------------");
          strArray = str.split(" ");
          for (String tmp : strArray) {
            if (tmp.trim().length() == 0)
              continue;

            if (++m == 10) {
              // 9)--第10列为mem的使用百分比(RedHat 9)

              menUsed += Double.parseDouble(tmp);

            }
          }

        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      in.close();
    }
    return menUsed * 100;
  }

  /**
   * 获取磁盘空间使用率（%）、使用量 、总量
   * 
   * @return
   * @throws Exception
   */
  public double[] getDeskUsageRate() throws Exception {
    double totalHD = 0;
    double usedHD = 0;
    double result[] = new double[3];
    Runtime rt = Runtime.getRuntime();
    Process p = rt.exec("df -hl");// df -hl 查看硬盘空间

    BufferedReader in = null;
    try {
      in = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String str = null;
      String[] strArray = null;
      @SuppressWarnings("unused")
      int flag = 0;
      while ((str = in.readLine()) != null) {
        int m = 0;
        // if (flag > 0) {
        // flag++;
        strArray = str.split(" ");
        for (String tmp : strArray) {
          if (tmp.trim().length() == 0)
            continue;
          ++m;
          // System.out.println("----tmp----" + tmp);
          if (tmp.indexOf("G") != -1) {
            if (m == 2) {
              // System.out.println("---G----" + tmp);
              if (!tmp.equals("") && !tmp.equals("0"))
                totalHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1)) * 1024;

            }
            if (m == 3) {
              // System.out.println("---G----" + tmp);
              if (!tmp.equals("none") && !tmp.equals("0"))
                usedHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1)) * 1024;

            }
          }
          if (tmp.indexOf("M") != -1) {
            if (m == 2) {
              // System.out.println("---M---" + tmp);
              if (!tmp.equals("") && !tmp.equals("0"))
                totalHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1));

            }
            if (m == 3) {
              // System.out.println("---M---" + tmp);
              if (!tmp.equals("none") && !tmp.equals("0"))
                usedHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1));
              // System.out.println("----3----" + usedHD);
            }
          }

        }

        // }

        result[0] = (usedHD / totalHD) * 100;
        result[1] = usedHD;
        result[2] = totalHD;

      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      in.close();
    }
    return result;
  }

  /**
   * 收集网络带宽使用量 (接收字节数（byte）、接收包数、接收错误包数、接收丢弃包数、发送字节数、发送包数、发送错误包数、发送丢弃包数) 多块网卡则返回总数（eth0、eth1 ... ）
   */
  public long[] getNetUsage() {

    long[] result = new long[8];

    long inBytes = 0;
    long inPackets = 0;
    long inErrs = 0;
    long inDrop = 0;
    long outBytes = 0;
    long outPackets = 0;
    long outErrs = 0;
    long outDrop = 0;

    Process pro;
    Runtime r = Runtime.getRuntime();

    try {
      String command = "cat /proc/net/dev";
      // 采集流量数据
      pro = r.exec(command);
      BufferedReader in1 = new BufferedReader(new InputStreamReader(pro.getInputStream()));
      String line = null;
      while ((line = in1.readLine()) != null) {
        line = line.trim();
        if (line.startsWith("eth")) {
          // System.out.println(line);
          String[] temp = line.substring("eth0:".length()).trim().split("\\s+");

          // Receive bytes,单位为Byte
          inBytes += Long.parseLong(temp[0]);
          inPackets += Long.parseLong(temp[1]);
          inErrs += Long.parseLong(temp[2]);
          inDrop += Long.parseLong(temp[3]);

          // Transmit bytes,单位为Byte
          outBytes += Long.parseLong(temp[8]);
          outPackets += Long.parseLong(temp[9]);
          outErrs += Long.parseLong(temp[10]);
          outDrop += Long.parseLong(temp[11]);
          continue;
        }
      }
      in1.close();
      pro.destroy();
    } catch (IOException e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      System.out.println("NetUsage发生InstantiationException. " + e.getMessage());
      System.out.println(sw.toString());
    }

    result[0] = inBytes;
    result[1] = inPackets;
    result[2] = inErrs;
    result[3] = inDrop;
    result[4] = outBytes;
    result[5] = outPackets;
    result[6] = outErrs;
    result[7] = outDrop;

    // for (int i = 0; i < result.length; i++) {
    // System.out.println(result[i]);
    // }

    return result;
  }

  /**
   * @Purpose:采集磁盘IO使用率 （%）
   * @param args
   * @return double,磁盘IO使用率
   */
  public double getDeskIoUsageRate() {
    // System.out.println("开始收集磁盘IO使用率");
    double ioUsage = 0.0;

    Process pro = null;
    Runtime r = Runtime.getRuntime();
    try {
      String command = "iostat -d -x";
      pro = r.exec(command);
      BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
      String line = null;
      int count = 0;
      while ((line = in.readLine()) != null) {
        if (++count >= 4) {
          // System.out.println(line);
          String[] temp = line.split("\\s+");
          if (temp.length > 1) {
            double util = Double.parseDouble(temp[temp.length - 1]);
            ioUsage = (ioUsage > util) ? ioUsage : util;
          }
        }
      }
      // System.out.println("本节点磁盘IO使用率为: " + ioUsage);
      in.close();
      pro.destroy();
    } catch (IOException e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      System.out.println("IoUsage发生InstantiationException. " + e.getMessage());
      System.out.println(sw.toString());
    }
    return ioUsage;
  }

  public static void main(String[] args) throws Exception {
    MonitorLinuxUtils monitor = MonitorLinuxUtils.build();
    System.out.println("---------------cpu used rate:" + monitor.getCpuUsage() + "%");
    System.out.println("---------------mem used rate:" + monitor.getMemUsage() + "%");
    System.out.println("---------------HD used rate:" + monitor.getDeskUsageRate() + "%");
    System.out.println("---------------Net usage:" + monitor.getNetUsage());

    // System.out.println("------------jvm监控----------------------");
    // Runtime lRuntime = Runtime.getRuntime();
    // System.out.println("--------------Free Momery:" +
    // lRuntime.freeMemory()+ "K");
    // System.out.println("--------------Max Momery:" +
    // lRuntime.maxMemory()+ "K");
    // System.out.println("--------------Total Momery:"+
    // lRuntime.totalMemory() + "K");
    // System.out.println("---------------Available Processors :"+
    // lRuntime.availableProcessors());
  }
}
