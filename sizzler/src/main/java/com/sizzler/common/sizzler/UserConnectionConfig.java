package com.sizzler.common.sizzler;

public class UserConnectionConfig {

  public static class UploadConfig {
    public static final String PATH = "path";
    public static final String TYPE = "type";
    public static final String SPLITER = "spliter";
    public static final String FILE_ID = "fileId";
    public static final String FILE_NAME = "fileName";

    public static final String SUFFIX_CSV = ".csv";
    public static final String SUFFIX_XLSX = ".xlsx";
    public static final String SUFFIX_XLS = ".xls";
    public static final String SUFFIX_TXT = ".txt";

  }

  public static class Oauth2Config {
    public static final String REFRESHTOKEN = "refreshToken";
    public static final String ACCESSTOKEN = "accessToken";
    public static final String CLIENTID = "clientId";
    public static final String CLIENTSECRET = "clientSecret";
    public static final String EXPIRETIME = "expireTime";
  }

  public static class DbConfig {
    public static final String DATABASETYPE = "dataBaseType";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String DATABASENAME = "dataBaseName";
  }

  public static class SshConfig {
    // 是否开启了ssh，1为开启，0为未开启，默认为0
    public static final String SSH = "ssh";
    public static final String SSH_HOST = "sshHost";
    public static final String SSH_PORT = "sshPort";
    public static final String SSH_USER = "sshUser";
    // 两类 password和private_key
    public static final String SSH_AUTH_METHOD = "sshAuthMethod";
    public static final String SSH_PASSWORD = "sshPassword";
    public static final String SSH_KEY_PATH = "sshKeyPath";
    public static final String SSH_PASSPHRASE = "sshPassphrase";
  }

  public static class SslConfig {
    // 是否开启了ssl，默认为false
    public static final String SSL = "ssl";
  }

}
