package com.sizzler.common.store.file;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileStoreStrategy {

  /**
   * 构建文件存储路径（拼接根目录和默认文件_data.xls）
   * 
   * @param filePath
   *          文件路径
   * @param fileId
   *          文件id
   */
  public String buildFilePath(String filePath, String fileId);

  /**
   * 构建文件存储路径（拼接根目录）
   * 
   * @param filePath
   *          文件路径
   * @param fileId
   *          文件id
   * @param fileExtension
   *          文件后缀
   */
  public String buildFilePath(String filePath, String fileId, String fileExtension);

  /**
   * 上传文件
   */
  public void uploadFile(InputStream inputStream, String path) throws Exception;

  /**
   * 获取从文件存储读文件的流
   * 
   * @param path
   */
  public InputStream getReadFileInputStream(String path) throws Exception;

  /**
   * 获取向文件存储中写文件的流
   * 
   * @param path
   */
  public OutputStream getWriteFileOutputStream(String path) throws Exception;

}
