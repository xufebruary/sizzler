package com.sizzler.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class IpAddressUtil {

  public static Set<InetAddress> resolveLocalAddresses() {
    Set<InetAddress> addrs = new HashSet<>();
    Enumeration<NetworkInterface> ns = null;
    try {
      ns = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException e) {
      // ignored...
    }
    while (ns != null && ns.hasMoreElements()) {
      NetworkInterface n = ns.nextElement();
      Enumeration<InetAddress> is = n.getInetAddresses();
      while (is.hasMoreElements()) {
        InetAddress i = is.nextElement();
        if (!i.isLoopbackAddress() && !i.isLinkLocalAddress() && !i.isMulticastAddress()
            && !isSpecialIp(i.getHostAddress()))
          addrs.add(i);
      }
    }
    return addrs;
  }

  public static Set<String> resolveLocalIps() {
    Set<InetAddress> addrs = resolveLocalAddresses();
    Set<String> ret = new TreeSet<>();
    for (InetAddress addr : addrs)
      ret.add(addr.getHostAddress());
    return ret;
  }

  public static String getLocalIps() {
    Set<InetAddress> addrs = resolveLocalAddresses();
    StringBuffer ipStr = new StringBuffer();
    for (InetAddress addr : addrs) {
      ipStr.append(addr.getHostAddress());
      ipStr.append("-");
    }
    return ipStr.toString();
  }

  @SuppressWarnings("static-access")
  public static String getHostName() {
    Set<InetAddress> addrs = resolveLocalAddresses();
    try {
      return addrs.iterator().next().getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      String host = e.getMessage(); // host = "hostname: hostname"
      if (host != null) {
        int colon = host.indexOf(':');
        if (colon > 0) {
          return host.substring(0, colon);
        }
      }
      return "UnknownHost";
    }
  }

  private static boolean isSpecialIp(String ip) {
    if (ip.contains(":"))
      return true;
    if (ip.startsWith("127."))
      return true;
    if (ip.startsWith("169.254."))
      return true;
    if (ip.equals("255.255.255.255"))
      return true;
    return false;
  }
}
