package com.sizzler.common.utils;

import java.io.*;

/**
 * 文件操作工具集.</p>
 * 
 * @version 1.0
 */
public class FileUtils {
  private FileUtils() {

  }

  /**
   * 将字符串写到文件中.
   * 
   * @param content
   *          字符串内容
   * @param filePath
   *          文件路径<br>
   * <br>
   *          date 2012-6-4<br>
   *          remark <br>
   */
  public static void string2File(String content, String filePath) {
    createFileDir(filePath);

    PrintWriter out = null;
    try {
      File file = new File(filePath);
      out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
          "utf-8")));
      if (content != null && content != "") {
        out.write(content);
        out.flush();
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      out.close();
    }
  }

  /**
   * 对文件内容到字符串.
   * 
   * @param file
   *          文件对象句柄
   * @return 文件内容字符串<br>
   *         date 2012-6-1<br>
   *         remark <br>
   */
  public static String readFile2String(File file) {
    InputStreamReader isr = null;
    BufferedReader br = null;
    StringBuffer contentBuffer = new StringBuffer();
    try {
      isr = new InputStreamReader(new FileInputStream(file), "utf-8");
      br = new BufferedReader(isr);

      String line = "";
      while ((line = br.readLine()) != null) {
        contentBuffer.append(line);
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          isr.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

    }

    return contentBuffer.toString();
  }

  /**
   * 对文件内容到字符串.
   * 
   * @param file
   *          文件对象句柄
   * @param encode
   *          编码格式
   * @return 文件内容字符串<br>
   * <br>
   *         date 2012-6-1<br>
   *         remark <br>
   */
  public static String readFile2String(File file, String encode) {
    InputStreamReader isr = null;
    BufferedReader br = null;
    StringBuffer contentBuffer = new StringBuffer();
    try {
      isr = new InputStreamReader(new FileInputStream(file), encode);
      br = new BufferedReader(isr);

      String line = "";
      while ((line = br.readLine()) != null) {
        contentBuffer.append(line);
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          isr.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

    }

    return contentBuffer.toString();
  }

  /**
   * 读文件内容到字符串.
   * 
   * @param filePath
   *          文件目录
   * @return 文件内容字符串<br>
   * <br>
   *         date 2012-6-1<br>
   *         remark <br>
   */
  public static String readFile2String(String filePath) {
    File file = new File(filePath);

    return readFile2String(file);
  }

  /**
   * 读文件内容到字符串.
   * 
   * @param filePath
   *          文件路径
   * @param encode
   *          编码格式
   * @return 文件内容字符串<br>
   * <br>
   *         date 2012-10-17<br>
   *         remark <br>
   */
  public static String readFile2String(String filePath, String encode) {
    File file = new File(filePath);

    return readFile2String(file, encode);
  }

  /**
   * 取一个文件夹下的所有文件列表.
   * 
   * @param path
   *          文件夹路径名称
   * @return 所有文件列表<br>
   * <br>
   *         date 2012-6-7<br>
   *         remark <br>
   */
  public static File[] fetchFilesByDir(String path) {
    File fileDir = new File(path);
    File[] textFiles = fileDir.listFiles();
    return textFiles;
  }

  /**
   * 迁移文件到指定路径.
   * 
   * @param sourceFilePath
   *          源文件路径及名称
   * @param destFilePath
   *          目标文件路径及名称<br>
   * <br>
   *          date 2012-7-23<br>
   *          remark <br>
   */
  public static void moveFile(String sourceFilePath, String destFilePath) {
    File old = new File(sourceFilePath);
    createFileDir(destFilePath);
    File dest = new File(destFilePath);
    if (dest.exists()) {
      dest.delete();
    }
    old.renameTo(dest);
  }

  /**
   * 创建文件目录.
   * 
   * @param filePath
   *          文件路径<br>
   * <br>
   *          date 2012-7-23<br>
   *          remark <br>
   */
  public static void createFileDir(String filePath) {
    String dirPath = "/";
    if (filePath.lastIndexOf("/") > 0) {
      dirPath = filePath.substring(0, filePath.lastIndexOf("/"));
    } else {
      if (filePath.lastIndexOf("\\") > 0) {
        dirPath = filePath.substring(0, filePath.lastIndexOf("\\"));
      }
    }

    File pathFile = new File(dirPath);
    if (pathFile.exists() == false) {
      pathFile.mkdirs();
    }
  }
}
