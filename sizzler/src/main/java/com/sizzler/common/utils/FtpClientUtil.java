package com.sizzler.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpClientUtil implements Serializable {

  private static final long serialVersionUID = -3511418507081051477L;

  private static final Logger logger = LoggerFactory.getLogger(FtpClientUtil.class);

  private String server;
  private Integer port;
  private String user;
  private String password;
  private String basePath;

  public FtpClientUtil() {
  }

  public FtpClientUtil(String server, Integer port, String user, String password) {
    this.server = server;
    this.port = port;
    this.user = user;
    this.password = password;
  }

  public FTPClient connectionFTP() throws IOException {
    FTPClient ftpClient = new FTPClient();
    int reply;
    ftpClient.connect(server, port);
    ftpClient.login(user, password);
    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    // After connection, should check the reply code to verify success.
    reply = ftpClient.getReplyCode();
    if (!FTPReply.isPositiveCompletion(reply)) {
      ftpClient.disconnect();
    }
    return ftpClient;
  }

  public void changeWorkingDirectory(FTPClient ftpClient, String path) throws IOException {
    if ("/".contains(path)) {
      ftpClient.changeWorkingDirectory("/"); // 回到根目录
    } else if (path != null) {
      String[] paths = path.split("/");
      for (String p : paths) {
        if (p != null && !"".equals(p.trim())) {
          if (!ftpClient.changeWorkingDirectory(p)) {
            ftpClient.mkd(p);
            ftpClient.changeWorkingDirectory(p);
          }
        }
      }
    }
  }

  public boolean upload(InputStream inputStream, String remoteFileName, String remotePath) {
    boolean done = false;
    FTPClient ftpClient = null;
    try {
      ftpClient = this.connectionFTP();
      ftpClient.changeWorkingDirectory("/"); // 回到根目录
      this.changeWorkingDirectory(ftpClient, this.basePath);
      this.changeWorkingDirectory(ftpClient, remotePath);
      done = ftpClient.storeFile(remoteFileName, inputStream);
      inputStream.close();
    } catch (IOException e) {
      logger.error("Upload file to FTP-Server Error:" + e.getMessage(), e);
    } finally {
      if (ftpClient != null && ftpClient.isConnected()) {
        try {
          ftpClient.logout();
          ftpClient.disconnect();
        } catch (IOException ioe) {
          logger.error("FTP-Client disconnect  Error:" + ioe.getMessage(), ioe);
        }
      }
    }
    return done;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public static void main(String[] args) throws FileNotFoundException {
    FtpClientUtil client = new FtpClientUtil("192.168.1.2", 21, "ptone", "ptone");
    client.setBasePath("test/images/test");
    File file = new File("C:/Users/peng.xu/Desktop/default2.jpg");
    client.upload(new FileInputStream(file), UuidUtil.generateUuid() + ".jpg", "2016/06/14");
    client.upload(new FileInputStream(file), UuidUtil.generateUuid() + ".jpg", "2016/06/14");
    client.upload(new FileInputStream(file), UuidUtil.generateUuid() + ".jpg", "2016/06/15");
    client.upload(new FileInputStream(file), UuidUtil.generateUuid() + ".jpg", "2016/06/15");
  }

}
