package com.sizzler.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.metamodel.util.HdfsResource;
import org.apache.metamodel.util.HdfsUtil;

public class HdfsConstants {
  private static final Logger log = Logger.getLogger(HdfsConstants.class);
  // private static String hdfsPath="hdfs://192.168.18.73:8020/ptone";
  private static String hdfsPath = "hdfs://172.17.5.219:8020/ptone";
  // private static String hdfsPath="hdfs://ptmind-bak-cluster/ptone";
  // 0 代表非HA，1代表HA
  private static int ha = 0;
  private static Map<String, String> hdfsHAConfig = new HashMap<>();
  private static String defaultFS = "hdfs://ptmind-bak-cluster";
  private static String nameservices = "ptmind-bak-cluster";
  // nodeList的格式为 nn1;mn-5-219.ptfuture.com:8020,nn2;sn-5-220.ptfuture.com:8020
  private static String nodeConfigList =
      "nn1;mn-5-219.ptfuture.com:8020,nn2;sn-5-220.ptfuture.com:8020";
  public static boolean hasInit = false;

  public static void init() {
    if (ha == 1) {
      log.info("init hdfs client config....");
      hdfsHAConfig.put("fs.defaultFS", defaultFS);
      hdfsHAConfig.put("dfs.nameservices", nameservices);
      String[] nodeArray = nodeConfigList.split(",");
      StringBuilder nodeList = new StringBuilder("");

      for (int i = 0; i < nodeArray.length; i++) {
        String nodeInfo = nodeArray[i];
        String[] nodeConfig = nodeInfo.split(";");
        String nodeName = nodeConfig[0];
        String nodeHost = nodeConfig[1];
        nodeList.append(nodeName);
        if (i < nodeArray.length - 1) {
          nodeList.append(",");
        }

        hdfsHAConfig.put("dfs.namenode.rpc-address." + nameservices + "." + nodeName, nodeHost);
      }
      hdfsHAConfig.put("dfs.ha.namenodes." + nameservices, nodeList.toString());
      hdfsHAConfig.put("dfs.client.failover.proxy.provider." + nameservices,
          "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
    }
    hasInit = true;
  }

  public static String createHdfsPath(String uid, String dsCode, String fileId, String fileExtension) {
    StringBuilder destPathBuilder = new StringBuilder(hdfsPath);
    destPathBuilder.append("/").append(uid).append("/").append(dsCode).append("/").append(fileId)
        .append(fileExtension);

    return destPathBuilder.toString();
  }

  public static void uploadFileToHdfs(InputStream inputStream, String fileUploadPath)
      throws IOException {
    if (HdfsConstants.getHa() == 0) {
      // 非Ha的
      HdfsUtil.upload(inputStream, fileUploadPath);
    } else {
      if (!HdfsConstants.hasInit) {
        HdfsConstants.init();
      }
      HdfsResource hdfsResource = new HdfsResource(HdfsConstants.getHdfsHAConfig(), fileUploadPath);
      HdfsUtil.uploadHdfsResource(inputStream, hdfsResource);
    }
  }

  public static String getHdfsPath() {
    return hdfsPath;
  }

  public void setHdfsPath(String hdfsPath) {
    HdfsConstants.hdfsPath = hdfsPath;
  }

  public static int getHa() {
    return ha;
  }

  public void setHa(int ha) {
    HdfsConstants.ha = ha;
  }

  public static Map<String, String> getHdfsHAConfig() {
    return hdfsHAConfig;
  }

  public void setHdfsHAConfig(Map<String, String> hdfsHAConfig) {
    HdfsConstants.hdfsHAConfig = hdfsHAConfig;
  }

  public static String getDefaultFS() {
    return defaultFS;
  }

  public void setDefaultFS(String defaultFS) {
    HdfsConstants.defaultFS = defaultFS;
  }

  public static String getNameservices() {
    return nameservices;
  }

  public void setNameservices(String nameservices) {
    HdfsConstants.nameservices = nameservices;
  }

  public static String getNodeConfigList() {
    return nodeConfigList;
  }

  public void setNodeConfigList(String nodeConfigList) {
    HdfsConstants.nodeConfigList = nodeConfigList;
  }

}
