package com.sizzler.common.store.file.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.metamodel.util.HdfsResource;
import org.apache.metamodel.util.HdfsUtil;

import com.sizzler.common.store.file.FileStoreStrategy;

public class HdfsFileStoreStrategy implements FileStoreStrategy {

  private static final Logger log = Logger.getLogger(HdfsFileStoreStrategy.class);
  private String hdfsPath = "hdfs://192.168.18.73:8020/sizzler";
  private int ha = 0;// 0 代表非HA，1代表HA
  private Map<String, String> hdfsHAConfig = new HashMap<>();
  private String defaultFS = "hdfs://ptmind-bak-cluster";
  private String nameservices = "ptmind-bak-cluster";
  private String nodeConfigList = "nn1;mn-5-219.ptfuture.com:8020,nn2;sn-5-220.ptfuture.com:8020";
  public boolean hasInit = false;

  public void init() {
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

  public HdfsResource createHdfsResource(String path) {
    HdfsResource hdfsResource = null;
    if (ha == 0) {
      hdfsResource = new HdfsResource(path);
    } else {
      if (!hasInit) {
        init();
      }
      hdfsResource = new HdfsResource(hdfsHAConfig, path);
    }
    return hdfsResource;
  }

  @Override
  public String buildFilePath(String filePath, String fileId) {
    return this.buildFilePath(filePath, fileId, "_data.xls");
  }

  @Override
  public String buildFilePath(String filePath, String fileId, String fileExtension) {
    StringBuilder pathBuilder = new StringBuilder(hdfsPath);
    pathBuilder.append((hdfsPath.endsWith("/") || filePath.startsWith("/")) ? "" : "/");
    pathBuilder.append(filePath);
    pathBuilder.append(filePath.endsWith("/") ? "" : "/");
    pathBuilder.append(fileId).append(fileExtension);
    return pathBuilder.toString();
  }

  @Override
  public void uploadFile(InputStream inputStream, String path) throws Exception {
    HdfsResource hdfsResource = this.createHdfsResource(path);
    HdfsUtil.uploadHdfsResource(inputStream, hdfsResource);
  }

  @Override
  public InputStream getReadFileInputStream(String path) throws Exception {
    HdfsResource hdfsResource = this.createHdfsResource(path);
    // 判断文件是否存在，如果不存在，则直接返回null
    if (!hdfsResource.isExists() || hdfsResource.getSize() == 0) {
      log.warn(path + " not Exists or size = 0");
      return null;
    }
    InputStream inputStream = hdfsResource.read();
    return inputStream;
  }

  @Override
  public OutputStream getWriteFileOutputStream(String path) throws Exception {
    HdfsResource hdfsResource = this.createHdfsResource(path);
    return hdfsResource.write();
  }

  // //////////////////////////////////////////////////

  public String getHdfsPath() {
    return hdfsPath;
  }

  public void setHdfsPath(String hdfsPath) {
    this.hdfsPath = hdfsPath;
  }

  public int getHa() {
    return ha;
  }

  public void setHa(int ha) {
    this.ha = ha;
  }

  public Map<String, String> getHdfsHAConfig() {
    return hdfsHAConfig;
  }

  public void setHdfsHAConfig(Map<String, String> hdfsHAConfig) {
    this.hdfsHAConfig = hdfsHAConfig;
  }

  public String getDefaultFS() {
    return defaultFS;
  }

  public void setDefaultFS(String defaultFS) {
    this.defaultFS = defaultFS;
  }

  public String getNameservices() {
    return nameservices;
  }

  public void setNameservices(String nameservices) {
    this.nameservices = nameservices;
  }

  public String getNodeConfigList() {
    return nodeConfigList;
  }

  public void setNodeConfigList(String nodeConfigList) {
    this.nodeConfigList = nodeConfigList;
  }

  public boolean isHasInit() {
    return hasInit;
  }

  public void setHasInit(boolean hasInit) {
    this.hasInit = hasInit;
  }

}
