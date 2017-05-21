package com.sizzler.provider.common.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.data.Row;
import org.apache.metamodel.util.FileHelper;
import org.apache.metamodel.util.HdfsResource;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.utils.ExcelTool;
import com.sizzler.provider.common.file.PtoneFile;

/**
 * Created by ptmind on 2015/12/25.
 */
public class PtoneHdfsUtil {

  private static final Logger log = LoggerFactory.getLogger(PtoneHdfsUtil.class);

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

  private static boolean hasInit = false;

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

  public static PtoneFile downloadPtoneFileFromHdfs(String uid, String dsCode, String fileId) {
    return downloadPtoneFileFromHdfs(uid, dsCode, fileId, ".xls");
  }

  public static PtoneFile downloadPtoneFileFromHdfs(String uid, String dsCode, String fileId,
      String suffix) {
    PtoneFile ptoneFile = new PtoneFile();
    ptoneFile.setId(fileId);
    String destPath = createHdfsPath(uid, dsCode, fileId, suffix);

    try {

      HdfsResource hdfsResource = null;
      if (ha == 0) {
        hdfsResource = new HdfsResource(destPath);
      } else {
        if (!hasInit) {
          init();
        }
        hdfsResource = new HdfsResource(hdfsHAConfig, destPath);
      }

      // 判断文件是否存在，如果不存在，则直接返回null

      if (!hdfsResource.isExists() || hdfsResource.getSize() == 0) {
        log.info(destPath + " not Exists or size=0");
        return null;
      }

      InputStream inputStream = hdfsResource.read();
      LinkedHashMap<String, List<List>> fileListDataMap = ExcelTool.getExcelContent(inputStream);

      ptoneFile.setFileListDataMap(fileListDataMap);
    } catch (Exception e) {
      e.printStackTrace();
      log.info("read '" + destPath + "' from HDFS error");
      return null;
    }

    return ptoneFile;
  }


  public static void uploadPtoneFileToHdfs(PtoneFile ptoneFile, String uid, String dsCode) {
    // 默认情况下 同步上传到HDFS上面
    uploadPtoneFileToHdfs(ptoneFile, uid, dsCode, true);
  }

  public static void uploadPtoneFileToHdfs(PtoneFile ptoneFile, String uid, String dsCode,
      boolean syn) {
    // 默认的文件名后缀为.xls
    uploadPtoneFileToHdfs(ptoneFile, uid, dsCode, ".xls", syn);
  }

  public static HdfsResource createHdfsResource(String path) {
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

  public static String createHdfsPath(String uid, String dsCode, String fileId, String fileExtension) {
    StringBuilder destPathBuilder = new StringBuilder(hdfsPath);
    destPathBuilder.append("/").append(uid).append("/").append(dsCode).append("/").append(fileId)
        .append(fileExtension);

    return destPathBuilder.toString();

  }

  public static void uploadPtoneFileToHdfs(PtoneFile ptoneFile, String uid, String dsCode,
      String fileExtension, boolean syn) {
    StringBuilder destPathBuilder = new StringBuilder(hdfsPath);
    destPathBuilder.append("/").append(uid).append("/").append(dsCode).append("/");

    if (dsCode.equalsIgnoreCase(DsConstants.DS_CODE_GOOGLESHEET)
        || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_GOOGLEDRIVE)
        || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_S3)) {
      destPathBuilder.append(ptoneFile.getId());
    } else {
      destPathBuilder.append(ptoneFile.getName());
    }
    destPathBuilder.append(fileExtension);
    String destPath = destPathBuilder.toString();

    // writeMetaModelRowToHdfs(destPath, ptoneFile.getFileDataMap());
    LinkedHashMap<String, List<List>> fileListDataMap = new LinkedHashMap<>();

    for (Map.Entry<String, List<List>> entry : ptoneFile.getFileListDataMap().entrySet()) {
      String key = entry.getKey();
      List<List> value = entry.getValue();
      List<List> newResultList = new ArrayList<>();
      for (List list : value) {
        List tmpList = new ArrayList();
        tmpList.addAll(list);
        newResultList.add(tmpList);
      }
      fileListDataMap.put(key, newResultList);
    }
    if (syn) {
      writeExcelToHdfs(destPath, ptoneFile.getFileListDataMap());
    } else // 异步情况下，单独的启动一个线程来将文件上传到HDFS
    {
      // 单独的起一个进程来进行文件的上传
      new Thread(new WriteDataToHdfsThread(destPath, fileListDataMap)).start();
    }



  }

  public static class WriteDataToHdfsThread implements Runnable {
    String destPath;
    LinkedHashMap<String, List<List>> fileListDataMap;

    public WriteDataToHdfsThread(String destPath, LinkedHashMap<String, List<List>> fileListDataMap) {
      this.destPath = destPath;
      this.fileListDataMap = fileListDataMap;
    }

    @Override
    public void run() {
      log.info("开始异步的上传文件：" + destPath);
      writeExcelToHdfs(destPath, fileListDataMap);
    }
  }


  public static void writeMetaModelRowToHdfs(String filePath,
      LinkedHashMap<String, List<Row>> fileDataMap) {
    LinkedHashMap<String, List<List>> fileDataListMap = new LinkedHashMap<>();
    for (Map.Entry<String, List<Row>> entry : fileDataMap.entrySet()) {
      String key = entry.getKey();
      List<List> tmpRowList = new ArrayList<>();
      List<Row> rowList = entry.getValue();
      for (Row row : rowList) {
        tmpRowList.add(Arrays.asList(row.getValues()));
      }
      fileDataListMap.put(key, tmpRowList);
    }
    writeExcelToHdfs(filePath, fileDataListMap);
  }

  public static void writeExcelToHdfs(String filePath, LinkedHashMap<String, List<List>> fileDataMap) {
    OutputStream outputStream = null;
    HdfsResource hdfsResource = null;
    if (ha == 0) {
      hdfsResource = new HdfsResource(filePath);
    } else {
      if (!hasInit) {
        init();
      }
      hdfsResource = new HdfsResource(hdfsHAConfig, filePath);
    }

    // HdfsResource hdfsResource=new HdfsResource(filePath);

    outputStream = hdfsResource.write();
    XSSFWorkbook wb = new XSSFWorkbook();
    if (fileDataMap != null) {
      for (Map.Entry<String, List<List>> entry : fileDataMap.entrySet()) {
        String sheetName = entry.getKey();
        List<List> rowList = entry.getValue();
        XSSFSheet sheet = wb.createSheet(sheetName);

        int rowCount = rowList.size();
        for (int i = 0; i < rowCount; i++) {
          XSSFRow xssfrow = sheet.createRow(i);
          List row = rowList.get(i);
          for (int j = 0; j < row.size(); j++) {
            xssfrow.createCell(j).setCellValue(row.get(j).toString());
          }
        }

      }
    }

    try {
      wb.write(outputStream);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      FileHelper.safeClose(outputStream);
    }


  }

  public String getHdfsPath() {
    return hdfsPath;
  }

  public void setHdfsPath(String hdfsPath) {
    PtoneHdfsUtil.hdfsPath = hdfsPath;
  }

  public int getHa() {
    return ha;
  }

  public void setHa(int ha) {
    log.info("setHa:" + ha);
    PtoneHdfsUtil.ha = ha;
  }


  public String getDefaultFS() {
    return defaultFS;
  }

  public void setDefaultFS(String defaultFS) {
    log.info("setDefaultFS:" + defaultFS);
    PtoneHdfsUtil.defaultFS = defaultFS;
  }

  public String getNameservices() {
    return nameservices;
  }

  public void setNameservices(String nameservices) {
    log.info("setNameservices:" + nameservices);
    PtoneHdfsUtil.nameservices = nameservices;
  }

  public String getNodeConfigList() {
    return nodeConfigList;
  }

  public void setNodeConfigList(String nodeConfigList) {
    log.info("setNodeConfigList:" + nodeConfigList);
    PtoneHdfsUtil.nodeConfigList = nodeConfigList;
  }
}
