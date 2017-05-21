package com.sizzler.common.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.StringTokenizer;

import com.sizzler.common.utils.StringUtil;
import com.sun.management.OperatingSystemMXBean;

/**
 * 获取客户端主机信息
 */
@SuppressWarnings("restriction")
public class MonitorUtils {

  private static final int CPUTIME = 30;
  private static final int PERCENT = 100;
  private static final int FAULTLENGTH = 10;
  private static String osVersion = null;

  private String ipAddress = null;
  private String datetime = null;
  private double totalMemorySize = 0.0;
  private double buffersMemory = 0.0;
  private double cachedMemory = 0.0;
  private double usedMemory = 0.0;
  private double memoryRatio;

  private long netInBytes = 0;
  private long netInPackets = 0;
  private long netInErrs = 0;
  private long netInDrop = 0;
  private long netOutBytes = 0;
  private long netOutPackets = 0;
  private long netOutErrs = 0;
  private long netOutDrop = 0;

  private double deskIORatio = 0.0;
  private double deskRatio = 0.0;
  private double deskUsed = 0.0;
  private double deskTotal = 0.0;

  private MonitorUtils() {
  }

  public static MonitorUtils build() {
    return new MonitorUtils();
  }

  /**
   * 获得当前的监控对象.
   * 
   * @return 返回构造好的监控对象
   */
  public MonitorClientInfo getMonitorClientInfo() throws Exception {
    int kb = 1024;
    osVersion = System.getProperty("os.version");
    OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory
        .getOperatingSystemMXBean();
    // 操作系统
    String osName = System.getProperty("os.name");
    // 主机IP
    if (osName.toLowerCase().startsWith("windows")) {
      ipAddress = this.getWindowsIp();
    } else {
      ipAddress = this.getLinuxIP();
    }
    if (osName.toLowerCase().startsWith("windows")) {
      // 总的物理内存
      double totalPhysicalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb;
      double usedPhysicalMemorySize = (osmxb.getTotalPhysicalMemorySize() - osmxb
          .getFreePhysicalMemorySize()) / kb;
      totalMemorySize = Double.parseDouble(String.format("%.1f", totalPhysicalMemorySize));
      // 已使用的物理内存
      usedMemory = Double.parseDouble(String.format("%.1f", usedPhysicalMemorySize));
      // windows内存使用率
      memoryRatio = Double.parseDouble(String.format("%.1f", (usedMemory / totalMemorySize) * 100));
    } else {
      double[] result = null;
      result = getLinuxMemInfo();

      totalMemorySize = Double.parseDouble(String.format("%.1f", result[0]));
      buffersMemory = Double.parseDouble(String.format("%.1f", result[1]));
      cachedMemory = Double.parseDouble(String.format("%.1f", result[2]));
      usedMemory = totalMemorySize - result[3];
      // linux内存使用率
      memoryRatio = Double.parseDouble(String.format("%.1f",
          ((usedMemory - (cachedMemory + buffersMemory)) / totalMemorySize) * 100));
    }

    // 获得cpu使用率
    double cpuRatio = 0;
    if (osName.toLowerCase().startsWith("windows")) {
      cpuRatio = this.getCpuRatioForWindows();
    } else {
      cpuRatio = this.getCpuRateForLinux();

      // 获取磁盘信息
      deskIORatio = MonitorLinuxUtils.build().getDeskIoUsageRate();
      double deskResult[] = MonitorLinuxUtils.build().getDeskUsageRate();
      deskRatio = deskResult[0];
      deskUsed = deskResult[1];
      deskTotal = deskResult[2];

      // 获取网络IO信息
      long netResult[] = MonitorLinuxUtils.build().getNetUsage();
      netInBytes = netResult[0];
      netInPackets = netResult[1];
      netInErrs = netResult[2];
      netInDrop = netResult[3];
      netOutBytes = netResult[4];
      netOutPackets = netResult[5];
      netOutErrs = netResult[6];
      netOutDrop = netResult[7];
    }

    /* 取得数据时间 */
    datetime = new Timestamp(System.currentTimeMillis()).toString();

    // 构造返回对象
    MonitorClientInfo info = new MonitorClientInfo();
    info.setOsName(osName);
    info.setCpuRatio(cpuRatio);
    info.setIpAddress(ipAddress);
    info.setDatetime(datetime);
    info.setBuffersMemory(buffersMemory);
    info.setCachedMemory(cachedMemory);
    info.setUsedMemory(usedMemory);
    info.setTotalMemorySize(totalMemorySize);
    info.setMemoryRatio(memoryRatio);
    info.setNetInBytes(netInBytes);
    info.setNetInPackets(netInPackets);
    info.setNetInErrs(netInErrs);
    info.setNetInDrop(netInDrop);
    info.setNetOutBytes(netOutBytes);
    info.setNetOutPackets(netOutPackets);
    info.setNetOutErrs(netOutErrs);
    info.setNetOutDrop(netOutDrop);
    info.setDeskIORatio(deskIORatio);
    info.setDeskRatio(deskRatio);
    info.setDeskUsed(deskUsed);
    info.setDeskTotal(deskTotal);
    return info;
  }

  /**
   * 获得Linux下IP地址.
   * 
   * @return 返回Linux下IP地址
   */
  public String getLinuxIP() {
    String ip = "";
    try {
      Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
      while (e1.hasMoreElements()) {
        NetworkInterface ni = (NetworkInterface) e1.nextElement();
        if (!ni.getName().equals("eth0")) {
          continue;
        } else {
          Enumeration<?> e2 = ni.getInetAddresses();
          while (e2.hasMoreElements()) {
            InetAddress ia = (InetAddress) e2.nextElement();
            if (ia instanceof Inet6Address)
              continue;
            ip = ia.getHostAddress();
          }
          break;
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    return ip;
  }

  public String getWindowsIp() {
    String ip = "";
    try {
      ip = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return ip;
  }

  /**
   * 获得Linux下CPU使用率（%）.
   * 
   * @return 返回Linux下cpu使用率
   */
  private double getCpuRateForLinux() {
    InputStream is = null;
    InputStreamReader isr = null;
    BufferedReader brStat = null;
    StringTokenizer tokenStat = null;
    try {

      Process process = Runtime.getRuntime().exec("top -b -n 1");
      is = process.getInputStream();
      isr = new InputStreamReader(is);
      brStat = new BufferedReader(isr);

      if (osVersion.startsWith("2.4")) {
        brStat.readLine();
        brStat.readLine();
        brStat.readLine();
        brStat.readLine();

        tokenStat = new StringTokenizer(brStat.readLine());
        tokenStat.nextToken();
        tokenStat.nextToken();
        String user = tokenStat.nextToken();
        tokenStat.nextToken();
        String system = tokenStat.nextToken();
        tokenStat.nextToken();
        String nice = tokenStat.nextToken();

        user = user.substring(0, user.indexOf("%"));
        system = system.substring(0, system.indexOf("%"));
        nice = nice.substring(0, nice.indexOf("%"));

        double userUsage = new Double(user).doubleValue();
        double systemUsage = new Double(system).doubleValue();
        double niceUsage = new Double(nice).doubleValue();

        return (userUsage + systemUsage + niceUsage) / 100;
      } else {
        brStat.readLine();
        brStat.readLine();

        tokenStat = new StringTokenizer(brStat.readLine());
        tokenStat.nextToken();
        tokenStat.nextToken();
        tokenStat.nextToken();
        tokenStat.nextToken();
        String cpuUsage = tokenStat.nextToken();

        Double usage = new Double(cpuUsage.substring(0, cpuUsage.indexOf("%")));

        return ((1 - usage.doubleValue() / 100) * 100) * 100;
      }

    } catch (IOException ioe) {
      System.out.println(ioe.getMessage());
      freeResource(is, isr, brStat);
      return 1;
    } finally {
      freeResource(is, isr, brStat);
    }

  }

  private static void freeResource(InputStream is, InputStreamReader isr, BufferedReader br) {
    try {
      if (is != null)
        is.close();
      if (isr != null)
        isr.close();
      if (br != null)
        br.close();
    } catch (IOException ioe) {
      System.out.println(ioe.getMessage());
    }
  }

  /**
   * 获得windows下CPU使用率.
   * 
   * @return 返回windows下cpu使用率
   */
  private double getCpuRatioForWindows() {
    try {
      ipAddress = InetAddress.getLocalHost().getHostAddress();
      String procCmd = System.getenv("windir")
          + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,"
          + "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
      // 取进程信息
      long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
      Thread.sleep(CPUTIME);
      long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
      if (c0 != null && c1 != null) {
        long idletime = c1[0] - c0[0];
        long busytime = c1[1] - c0[1];
        return Double.valueOf(PERCENT * (busytime) / (busytime + idletime)).doubleValue();
      } else {
        return 0.0;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return 0.0;
    }
  }

  /**
   * 读取CPU信息.
   */
  private long[] readCpu(final Process proc) {
    long[] retn = new long[2];
    try {
      proc.getOutputStream().close();
      InputStreamReader ir = new InputStreamReader(proc.getInputStream());
      LineNumberReader input = new LineNumberReader(ir);
      String line = input.readLine();
      if (line == null || line.length() < FAULTLENGTH) {
        return null;
      }
      int capidx = line.indexOf("Caption");
      int cmdidx = line.indexOf("CommandLine");
      int rocidx = line.indexOf("ReadOperationCount");
      int umtidx = line.indexOf("UserModeTime");
      int kmtidx = line.indexOf("KernelModeTime");
      int wocidx = line.indexOf("WriteOperationCount");
      long idletime = 0;
      long kneltime = 0;
      long usertime = 0;
      while ((line = input.readLine()) != null) {
        if (line.length() < wocidx) {
          continue;
        }
        // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
        // ThreadCount,UserModeTime,WriteOperation
        String caption = StringUtil.substring(line, capidx, cmdidx - 1).trim();
        String cmd = StringUtil.substring(line, cmdidx, kmtidx - 1).trim();
        if (cmd.indexOf("wmic.exe") >= 0) {
          continue;
        }
        // log.info("line="+line);
        if (caption.equals("System Idle Process") || caption.equals("System")) {
          idletime += Long.valueOf(StringUtil.substring(line, kmtidx, rocidx - 1).trim())
              .longValue();
          idletime += Long.valueOf(StringUtil.substring(line, umtidx, wocidx - 1).trim())
              .longValue();
          continue;
        }

        kneltime += Long.valueOf(StringUtil.substring(line, kmtidx, rocidx - 1).trim()).longValue();
        usertime += Long.valueOf(StringUtil.substring(line, umtidx, wocidx - 1).trim()).longValue();
      }
      retn[0] = idletime;
      retn[1] = kneltime + usertime;
      return retn;
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      try {
        proc.getInputStream().close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public double[] getLinuxMemInfo() {
    File file = new File("/proc/meminfo");
    double result[] = new double[4];
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      String str = null;
      StringTokenizer token = null;
      while ((str = br.readLine()) != null) {

        token = new StringTokenizer(str);
        if (!token.hasMoreTokens()) {
          continue;
        }
        str = token.nextToken();

        if (!token.hasMoreTokens()) {
          continue;
        }
        if (str.equalsIgnoreCase("MemTotal:")) {
          result[0] = Long.parseLong(token.nextToken());

        }
        if (str.equalsIgnoreCase("Buffers:")) {
          result[1] = Long.parseLong(token.nextToken());

        }
        if (str.equalsIgnoreCase("Cached:")) {
          result[2] = Long.parseLong(token.nextToken());

        }
        if (str.equalsIgnoreCase("MemFree:")) {
          result[3] = Long.parseLong(token.nextToken());

        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (br != null) {
          br.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * 测试方法.
   */
  public static void information() {
    MonitorClientInfo monitorInfo = null;

    try {
      monitorInfo = MonitorUtils.build().getMonitorClientInfo();
    } catch (Exception e) {
      e.printStackTrace();
    }

    String clientName = monitorInfo.getClientName();
    String ipAddress = monitorInfo.getIpAddress();
    String datetime = monitorInfo.getDatetime();
    String osName = monitorInfo.getOsName();
    String cpuRatio = String.valueOf(monitorInfo.getCpuRatio());
    String memoryRatio = String.valueOf(monitorInfo.getMemoryRatio());
    String totalMemory = String.valueOf(monitorInfo.getTotalMemorySize());
    String usedMemory = String.valueOf(monitorInfo.getUsedMemory());

    System.out.println("客户端名称：" + clientName);
    System.out.println("主机IP地址:" + ipAddress);
    System.out.println("获取数据时间:" + datetime);
    System.out.println("操作系统名称：" + osName);
    System.out.println("cpu使用率:" + cpuRatio + "%");
    System.out.println("内存使用率:" + memoryRatio + "%");
    System.out.println("内存使用量:" + usedMemory);
    System.out.println("内存总量:" + totalMemory);
  }

  public static void main(String[] args) {
    MonitorUtils.information();
  }

}
