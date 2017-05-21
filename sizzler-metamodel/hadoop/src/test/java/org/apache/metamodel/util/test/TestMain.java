package org.apache.metamodel.util.test;

import org.apache.metamodel.util.HdfsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by ptmind on 2015/11/28.
 */
public class TestMain {
  private static String hdfsPath = "hdfs://192.168.18.73:8020";

  public static void main(String[] args) {
    try {
      testUpload();
      // testDownLoad();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void testUpload() throws Exception {
    String testFile = "d:\\zx_2015_10_26_2.xlsx";
    File file = new File(testFile);
    FileInputStream inputStream = new FileInputStream(file);
    // /ptone/uid/ds
    String destinationPath = "/ptone/1002/upload/" + file.getName();
    destinationPath = hdfsPath + destinationPath;
    HdfsUtil.upload(inputStream, destinationPath);
  }

  public static void testDownLoad() throws Exception {
    String testFile = "d:\\zx_2015_10_26_2_hdfs.xlsx";
    File file = new File(testFile);
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    String destinationPath = "/ptone/1002/upload/zx_2015_10_26_2.xlsx";

    HdfsUtil.download(fileOutputStream, getHdfsPath(destinationPath));
  }

  private static String getHdfsPath(String path) {
    StringBuilder stringBuilder = new StringBuilder(hdfsPath);
    stringBuilder.append(path);
    return stringBuilder.toString();
  }
}
