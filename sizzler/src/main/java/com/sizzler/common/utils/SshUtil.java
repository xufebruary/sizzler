package com.sizzler.common.utils;

import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sizzler.common.sizzler.DataBaseConnection;

public class SshUtil {

  public static Session createSession(DataBaseConnection dataBaseConnection) throws JSchException {
    Session session = null;

    if (dataBaseConnection.getSsh().equals("1")) {
      JSch jSch = new JSch();
      String sshAuthMethod = dataBaseConnection.getSshAuthMethod();
      if (sshAuthMethod.equals("private_key")) {
        // String sshKeyPath=dataBaseConnection.getSshKeyPath();
        String sshPassphrase = dataBaseConnection.getSshPassphrase();
        // jSch.addIdentity(sshKeyPath,sshPassphrase);

        String sshPrivateKey = dataBaseConnection.getSshPrivateKey();
        String name = UuidUtil.generateUuid();
        byte[] prvkey = sshPrivateKey.getBytes();
        byte[] pubkey = null;
        if (sshPassphrase == null) {
          sshPassphrase = "";
        }
        byte[] passphraseByteArray = sshPassphrase.getBytes();

        jSch.addIdentity(name, prvkey, pubkey, passphraseByteArray);
      }

      int port = Integer.valueOf(dataBaseConnection.getSshPort());
      session = jSch.getSession(dataBaseConnection.getSshUser(), dataBaseConnection.getSshHost(),
          port);

      if (sshAuthMethod.equals("password")) {
        session.setPassword(dataBaseConnection.getSshPassword());
      }

      Properties config = new Properties();
      config.put("StrictHostKeyChecking", "no");

      session.setConfig(config);

      // session.connect();

    }
    return session;
  }
}
