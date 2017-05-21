package com.sizzler.common.monitor.dstat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * 获取客户端主机信息 (使用dstat工具进行性能监测信息收集)
 */
public class MonitorDstatUtils {

  private MonitorDstatUtils() {}

  public static MonitorDstatUtils build() {
    return new MonitorDstatUtils();
  }

  /**
   * 获得当前的监控对象.
   * 
   * @return 返回构造好的监控对象
   */
  public MonitorDstatClientInfo getMonitorClientInfo() throws Exception {

    MonitorDstatClientInfo info = new MonitorDstatClientInfo();

    String osName = System.getProperty("os.name"); // 操作系统
    String osVersion = System.getProperty("os.version");// 操作系统版本
    info.setOsName(osName + " " + osVersion);
    info.setDatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // 数据时间

    if (osName.toLowerCase().startsWith("linux")) {
      info.setIpAddress(this.getIpAddress()); // 获取ip地址

      info = execDstat(info); // 执行dstat命令, 获取 dstat 监控系统

    }

    return info;
  }

  public MonitorDstatClientInfo execDstat(MonitorDstatClientInfo info) {

    BufferedReader in = null;
    try {

      // 调用系统的 "dstat -cmdnrlpsy" 命令 (服务端需安装 dstat工具 )
      Runtime runtime = Runtime.getRuntime();
      Process process = runtime.exec("dstat -cmdnrlpsy");

      in = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line = null;
      int rowIndex = 0;
      while ((line = in.readLine()) != null) {
        rowIndex++;
        // System.out.println(rowIndex + " : " + line);
        if (rowIndex == 3) {
          // System.out.println(rowIndex + " : " + line);

          String str[] = line.trim().split("\\|");

          // for(int i = 0;i<str.length;i++){
          // System.out.println(str[i]);
          // }

          // str[0] 为 cpu信息
          String cpuStr[] = str[0].trim().split("\\s+");
          info.setCpuUsr(Double.valueOf(cpuStr[0].trim()));
          info.setCpuSys(Double.valueOf(cpuStr[1].trim()));
          info.setCpuIdl(Double.valueOf(cpuStr[2].trim()));
          info.setCpuWai(Double.valueOf(cpuStr[3].trim()));
          info.setCpuHiq(Double.valueOf(cpuStr[4].trim()));
          info.setCpuSiq(Double.valueOf(cpuStr[5].trim()));

          // str[1] 为内存信息
          String memStr[] = str[1].trim().split("\\s+");
          info.setMemUsed(parseValueToM(memStr[0].trim()));
          info.setMemBuff(parseValueToM(memStr[1].trim()));
          info.setMemCach(parseValueToM(memStr[2].trim()));
          info.setMemFree(parseValueToM(memStr[3].trim()));

          // str[2] 为磁盘信息
          String dskStr[] = str[2].trim().split("\\s+");
          info.setDskRead(parseValueToB(dskStr[0].trim()));
          info.setDskWrit(parseValueToB(dskStr[1].trim()));

          // str[3] 为网络信息
          String netStr[] = str[3].trim().split("\\s+");
          info.setNetRecv(parseValueToB(netStr[0].trim()));
          info.setNetSend(parseValueToB(netStr[1].trim()));

          // str[4] 为IO请求信息
          String ioStr[] = str[4].trim().split("\\s+");
          info.setIoRead(Double.valueOf(ioStr[0].trim()));
          info.setIoWrit(Double.valueOf(ioStr[1].trim()));

          // str[5] 为负载信息
          String loadStr[] = str[5].trim().split("\\s+");
          info.setLoadAvg1m(Double.valueOf(loadStr[0].trim()));
          info.setLoadAvg5m(Double.valueOf(loadStr[1].trim()));
          info.setLoadAvg15m(Double.valueOf(loadStr[2].trim()));

          // str[6] 为进程信息
          String procsStr[] = str[6].trim().split("\\s+");
          info.setProcsRun(Double.valueOf(procsStr[0].trim()));
          info.setProcsBlk(Double.valueOf(procsStr[1].trim()));
          info.setProcsNew(Double.valueOf(procsStr[2].trim()));

          // str[7] 为swap信息
          String swapStr[] = str[7].trim().split("\\s+");
          info.setSwapUsed(parseValueToM(swapStr[0].trim()));
          info.setSwapFree(parseValueToM(swapStr[1].trim()));

          // str[8] 为系统信息
          String sysStr[] = str[8].trim().split("\\s+");
          info.setSystemInt(Double.valueOf(sysStr[0].trim()));
          info.setSystemCsw(Double.valueOf(sysStr[1].trim()));

          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return info;
  }

  /**
   * 转换G、 M、K、B 为 B
   */
  public double parseValueToB(String val) {
    double result = 0;

    long b = 1;
    long kb = 1024;
    long mb = 1024 * 1024;
    long gb = 1024 * 1024 * 1024;
    if (val != null && !val.equals("")) {
      String tmp = val.trim().toLowerCase();
      if (tmp.endsWith("gb")) {
        result = Double.valueOf(tmp.substring(0, tmp.indexOf("gb"))) * gb;
      } else if (tmp.endsWith("g")) {
        result = Double.valueOf(tmp.substring(0, tmp.indexOf("g"))) * gb;
      } else if (tmp.endsWith("mb")) {
        result = Double.valueOf(tmp.substring(0, tmp.indexOf("mb"))) * mb;
      } else if (tmp.endsWith("m")) {
        result = Double.valueOf(tmp.substring(0, tmp.indexOf("m"))) * mb;
      } else if (tmp.endsWith("kb")) {
        result = Double.valueOf(tmp.substring(0, tmp.indexOf("kb"))) * kb;
      } else if (tmp.endsWith("k")) {
        result = Double.valueOf(tmp.substring(0, tmp.indexOf("k"))) * kb;
      } else if (tmp.endsWith("b")) {
        result = Double.valueOf(tmp.substring(0, tmp.indexOf("b"))) * b;
      }
    }
    return result;
  }

  /**
   * 转换G、 M、K、B 为 K
   */
  public double parseValueToK(String val) {
    double result = parseValueToB(val);

    return result / (10244);
  }

  /**
   * 转换G、 M、K、B 为 M
   */
  public double parseValueToM(String val) {
    double result = parseValueToB(val);
    return result / (1024 * 1024);
  }

  /**
   * 转换G、 M、K、B 为 G
   */
  public double parseValueToG(String val) {
    double result = parseValueToB(val);
    return result / (1024 * 1024 * 1024);
  }

  /**
   * 获得Linux下IP地址.
   */
  public String getIpAddress() {
    String ip = "";
    try {
      Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
      while (e1.hasMoreElements()) {
        NetworkInterface ni = (NetworkInterface) e1.nextElement();
        if (!ni.getName().startsWith("eth0")) {
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

}
