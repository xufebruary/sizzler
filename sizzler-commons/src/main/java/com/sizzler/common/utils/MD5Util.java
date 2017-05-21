package com.sizzler.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

  private static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b',
      'c', 'd', 'e', 'f' };

  public static String md5(String str) {
    MessageDigest md5 = null;
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    if (md5 != null) {
      md5.update(str.getBytes());
      byte[] encodeBytes = md5.digest();
      char encodeStr[] = new char[encodeBytes.length * 2];
      int k = 0;
      for (int i = 0; i < encodeBytes.length; i++) {
        byte encodeByte = encodeBytes[i];
        encodeStr[k++] = hexDigits[encodeByte >> 4 & 0xf];
        encodeStr[k++] = hexDigits[encodeByte & 0xf];
      }
      return new String(encodeStr);
    }
    return null;
  }

  public static void main(String[] args) {
    String str = "com.mysql.jdbc.Driver#jdbc:mysql://127.0.0.1:3306/datagather_db#root#root";
    System.out.println(MD5Util.md5(str));
    // a05ddb7b9a968ef7cc428128a2d72521
  }

}
