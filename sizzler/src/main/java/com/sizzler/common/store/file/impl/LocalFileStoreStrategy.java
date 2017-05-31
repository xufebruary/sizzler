package com.sizzler.common.store.file.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.hadoop.io.IOUtils;

import com.sizzler.common.store.file.FileStoreStrategy;

public class LocalFileStoreStrategy implements FileStoreStrategy {

  private String localPath = "/data/sizzler/files";

  @Override
  public String buildFilePath(String filePath, String fileId) {
    return this.buildFilePath(filePath, fileId, "_data.xls");
  }

  @Override
  public String buildFilePath(String filePath, String fileId, String fileExtension) {
    StringBuilder pathBuilder = new StringBuilder(localPath);
    pathBuilder.append((localPath.endsWith("/") || filePath.startsWith("/")) ? "" : "/");
    pathBuilder.append(filePath);
    pathBuilder.append(filePath.endsWith("/") ? "" : "/");
    pathBuilder.append(fileId).append(fileExtension);
    return pathBuilder.toString();
  }

  @Override
  public void uploadFile(InputStream inputStream, String path) throws Exception {
    OutputStream out = this.getWriteFileOutputStream(path);
    IOUtils.copyBytes(inputStream, out, 4096, true);

    // File destFile = new File(path);
    // BufferedOutputStream out = null;
    // try {
    // out = new BufferedOutputStream(new FileOutputStream(destFile));
    // byte[] b = new byte[1024];
    // while (inputStream.read(b) != -1) {
    // out.write(b);
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // } finally {
    // if (inputStream != null) {
    // try {
    // inputStream.close();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // if (out != null) {
    // try {
    // out.close();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // }
  }

  @Override
  public InputStream getReadFileInputStream(String path) throws Exception {
    File file = new File(path);
    InputStream in = new FileInputStream(file);
    return in;
  }

  @Override
  public OutputStream getWriteFileOutputStream(String path) throws Exception {
    File file = new File(path);
    if (!file.exists()) {
      // 如果目标文件所在的目录不存在，则创建父目录
      if (!file.getParentFile().exists()) {
        if (file.getParentFile().mkdirs()) {
          file.createNewFile();
        }
      }
    }
    OutputStream out = new FileOutputStream(file);
    return out;
  }

  // /////////////////////////////////////////////////////

  public String getLocalPath() {
    return localPath;
  }

  public void setLocalPath(String localPath) {
    this.localPath = localPath;
  }

}
