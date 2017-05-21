package com.sizzler.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class FileUtil {

  private static Logger LOG = Logger.getLogger(FileUtil.class);

  /**
   * 创建文件
   * 
   * @param pathStr 路径
   * @param fileName 文件名字
   * @return File实例
   */
  public static File createFile(String pathStr, String fileName) {
    File path = new File(pathStr);
    if (!path.exists()) {
      path.mkdirs();
    }
    File file = new File(pathStr + "/" + fileName);
    if (file.exists()) {
      int dotIndex = fileName.lastIndexOf(".");
      if (dotIndex != -1) {
        file =
            new File(pathStr + "/" + fileName.substring(0, dotIndex) + System.currentTimeMillis()
                + fileName.substring(dotIndex, fileName.length()));
      } else {
        file = new File(pathStr + "/" + fileName + System.currentTimeMillis());
      }
    }
    return file;
  }

  public static void delFile(File file) {
    if (file.exists()) {
      if (file.isFile()) {
        file.delete();
      } else {
        File[] files = file.listFiles();
        for (File f : files) {
          delFile(f);
        }
        file.delete();
      }
    }
  }

  public static void delFile(String path) {
    delFile(new File(path));
  }

  /**
   * copy 文件
   * 
   * @param sourceFile
   * @param targetFile
   * @throws IOException
   */
  public static void copyFile(File sourceFile, File targetFile) throws IOException {
    BufferedInputStream inBuff = null;
    BufferedOutputStream outBuff = null;
    try {
      // 新建文件输入流并对它进行缓冲
      inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

      // 新建文件输出流并对它进行缓冲
      outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

      // 缓冲数组
      byte[] b = new byte[1024 * 5];
      int len;
      while ((len = inBuff.read(b)) != -1) {
        outBuff.write(b, 0, len);
      }
      // 刷新此缓冲的输出流
      outBuff.flush();
    } finally {
      // 关闭流
      if (inBuff != null)
        inBuff.close();
      if (outBuff != null)
        outBuff.close();
    }
  }

  public static File getFile(String fileName, String propertyName) throws Exception {
    String filePath = null;
    if (propertyName != null && !"".equals(propertyName)) {
      filePath = System.getProperty(propertyName);
    }
    File file = null;

    if (filePath == null || "".equals(filePath)) {
      URL url = FileUtil.class.getClassLoader().getResource(propertyName + fileName);
      if (url == null) {
        throw new FileNotFoundException(fileName + " not found!");
      }
      file = new File(url.getPath());
    } else {
      filePath =
          filePath.endsWith("/") ? filePath.concat(fileName) : filePath.concat("/")
              .concat(fileName);
      file = new File(filePath);
    }
    return file;
  }

  public static String getFilePath(String fileName, String propertyName) throws Exception {
    String filePath = null;
    if (propertyName != null && !"".equals(propertyName)) {
      filePath = System.getProperty(propertyName);
    }

    if (filePath == null || "".equals(filePath)) {

      URL url = FileUtil.class.getClassLoader().getResource(propertyName + fileName);
      if (url == null) {
        throw new FileNotFoundException(fileName + " not found!");
      }
      filePath = url.getPath();
    } else {
      filePath =
          filePath.endsWith("/") ? filePath.concat(fileName) : filePath.concat("/")
              .concat(fileName);
    }
    return filePath;
  }

  public static String getFileDir(String fileName, String propertyName) throws Exception {
    String filePath = null;
    if (propertyName != null && !"".equals(propertyName)) {
      filePath = System.getProperty(propertyName);
    }

    if (filePath == null || "".equals(filePath)) {

      URL url = FileUtil.class.getClassLoader().getResource(propertyName + fileName);
      if (url == null) {
        throw new FileNotFoundException(fileName + " not found!");
      }
      filePath = url.getPath();
      filePath = filePath.replace(fileName, "");
    } else {
      filePath =
          filePath.endsWith("/") ? filePath.concat(fileName) : filePath.concat("/")
              .concat(fileName);
    }
    return filePath;
  }

  /**
   * @param file
   */
  public static String read(File file, String charset) {
    final byte[] content = read(file);
    return content == null ? "" : new String(content);
  }

  public static byte[] read(File file) {
    if (!(file.exists() && file.isFile())) {
      throw new IllegalArgumentException("The remote not exist or not a remote");
    }
    FileInputStream fis = null;
    byte[] content = null;
    try {
      fis = new FileInputStream(file);
      content = new byte[fis.available()];
      fis.read(content);
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage(), e);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          LOG.error(e.getMessage(), e);
        }
        fis = null;
      }
    }
    return content;
  }

  /**
   * 将saveProperties保存为文件
   * 
   * @param filePath
   * @param parameterName
   * @param parameterValue
   */
  public static void saveProperties(String filePath, String parameterName, String parameterValue) {
    Properties prop = new Properties();
    try {
      InputStream fis = new FileInputStream(filePath);
      prop.load(fis);
      OutputStream fos = new FileOutputStream(filePath);
      prop.setProperty(parameterName, parameterValue);
      prop.store(fos, "Update '" + parameterName + "' value");
      fis.close();
    } catch (IOException e) {
      System.err.println("Visit " + filePath + " for updating " + parameterName + " value error");
    }
  }

}
