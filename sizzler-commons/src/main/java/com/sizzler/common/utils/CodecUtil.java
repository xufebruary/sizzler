package com.sizzler.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CodecUtil {
  /**
   * BASE64解码
   * 
   * @param string
   * @return
   */
  public static final String base64decode(String string) {
    if (!CommonUtil.isValid(string))
      return "";
    return new String(base64decode(string.getBytes()));
  }

  /**
   * BASE64解码
   * 
   * @param inByte
   * @return
   */
  public static final byte[] base64decode(byte inByte[]) {
    byte[] decodeTable = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, 62, -1, -1, -1, 63, // +, /
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, // 0-9,
        // =
        -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, // A-O
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, // P-Z
        -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // a-o
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 }; // p-z

    if (inByte.length == 0)
      return inByte;

    int length = (inByte.length / 4) * 3;
    if (inByte[inByte.length - 2] == '=') {
      length -= 2;
    } else if (inByte[inByte.length - 1] == '=') {
      length--;
    }
    byte outByte[] = new byte[length];

    int i = 0;
    int j = 0;
    byte byte1, byte2, byte3, byte4;
    int block = inByte.length / 4;
    for (int k = 0; k < block - 1; k++) {
      byte1 = decodeTable[inByte[i++] & 0xff];
      byte2 = decodeTable[inByte[i++] & 0xff];
      byte3 = decodeTable[inByte[i++] & 0xff];
      byte4 = decodeTable[inByte[i++] & 0xff];
      outByte[j++] = (byte) (byte1 << 2 & 0xfc | byte2 >> 4 & 0x03);
      outByte[j++] = (byte) (byte2 << 4 & 0xf0 | byte3 >> 2 & 0x0f);
      outByte[j++] = (byte) (byte3 << 6 & 0xc0 | byte4 & 0x3f);
    }

    byte1 = decodeTable[inByte[i++] & 0xff];
    byte2 = decodeTable[inByte[i++] & 0xff];
    byte3 = decodeTable[inByte[i++] & 0xff];
    byte4 = decodeTable[inByte[i++] & 0xff];

    if (byte3 == -2) {
      outByte[j++] = (byte) (byte1 << 2 & 0xfc | byte2 >> 4 & 0x03);
    } else if (byte4 == -2) {
      outByte[j++] = (byte) (byte1 << 2 & 0xfc | byte2 >> 4 & 0x03);
      outByte[j++] = (byte) (byte2 << 4 & 0xf0 | byte3 >> 2 & 0x0f);
    } else {
      outByte[j++] = (byte) (byte1 << 2 & 0xfc | byte2 >> 4 & 0x03);
      outByte[j++] = (byte) (byte2 << 4 & 0xf0 | byte3 >> 2 & 0x0f);
      outByte[j++] = (byte) (byte3 << 6 & 0xc0 | byte4 & 0x3f);
    }

    return outByte;
  }

  /**
   * BASE64编码
   * 
   * @param s
   * @return
   */
  public static final String base64encode(String s) {
    try {
      return new String(base64encode(s.getBytes("UTF-8")));
    } catch (Exception e) {
      return new String(base64encode(s.getBytes()));
    }
  }

  /**
   * BASE64编码
   * 
   * @param inByte
   * @return
   */
  public static final byte[] base64encode(byte inByte[]) {
    byte encodeTable[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

    if (inByte.length == 0)
      return inByte;

    byte outByte[] = new byte[((inByte.length) / 3) * 4 + ((((inByte.length) % 3) == 0) ? 0 : 4)];
    int i = 0;
    int j = 0;

    byte byte1, byte2, byte3;
    int block = inByte.length / 3;
    for (int k = 0; k < block; k++) {
      byte1 = inByte[i++];
      byte2 = inByte[i++];
      byte3 = inByte[i++];
      outByte[j++] = (byte) encodeTable[byte1 >> 2 & 0x3f];
      outByte[j++] = (byte) encodeTable[(byte1 << 4 & 0x30) | (byte2 >> 4 & 0x0f)];
      outByte[j++] = (byte) encodeTable[(byte2 << 2 & 0x3c) | (byte3 >> 6 & 0x03)];
      outByte[j++] = (byte) encodeTable[byte3 & 0x3f];
    }

    if ((inByte.length % 3) == 1) {
      byte1 = inByte[i++];
      outByte[j++] = (byte) encodeTable[byte1 >> 2 & 0x3f];
      outByte[j++] = (byte) encodeTable[(byte1 << 4 & 0x30)];
      outByte[j++] = '=';
      outByte[j++] = '=';
    } else if ((inByte.length % 3) == 2) {
      byte1 = inByte[i++];
      byte2 = inByte[i++];
      outByte[j++] = (byte) encodeTable[byte1 >> 2 & 0x3f];
      outByte[j++] = (byte) encodeTable[(byte1 << 4 & 0x30) | (byte2 >> 4 & 0x0f)];
      outByte[j++] = (byte) encodeTable[(byte2 << 2 & 0x3c)];
      outByte[j++] = '=';
    }

    return outByte;
  }

  /**
   * 获得MD5加密密码的方法
   */
  public static String getMD5ofStr(String origString) {
    return DigestUtils.md5Hex(origString);
  }

  /**
   * 编码MD5
   * 
   * @param plaintext
   * @return
   */
  public static final String toMD5(String plaintext) {
    StringBuffer sb = new StringBuffer();
    try {
      MessageDigest algorithm = MessageDigest.getInstance("MD5");
      algorithm.reset();
      algorithm.update(plaintext.getBytes());
      byte digested[] = algorithm.digest();

      for (int i = 0; i < digested.length; i++) {
        String hex = Integer.toHexString(0xff & digested[i]);
        if (hex.length() == 1)
          sb.append("0");
        sb.append(hex);
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  /**
   * 编码MD5
   * 
   * @param message
   * @return
   */
  public static final byte[] toMD5(byte[] message) {

    try {
      MessageDigest algorithm = MessageDigest.getInstance("MD5");
      algorithm.reset();
      algorithm.update(message);
      return algorithm.digest();

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 3DES加密
   * 
   * @param key
   * @param inByte
   * @return
   */
  public static final byte[] tripleDESEncrypt(byte[] key, byte[] inByte) {
    return tripleDESCryptor(key, inByte, Cipher.ENCRYPT_MODE);
  }

  /**
   * 3DES解密
   * 
   * @param key
   * @param inByte
   * @return
   */
  public static final byte[] tripleDESDecrypt(byte[] key, byte[] inByte) {
    return tripleDESCryptor(key, inByte, Cipher.DECRYPT_MODE);
  }

  /**
   * 3DES编码器
   * 
   * @param key
   * @param inByte
   * @param direction
   * @return
   */
  static final public byte[] tripleDESCryptor(byte[] key, byte[] inByte, int direction) {
    try {
      SecretKeySpec skeySpec = new SecretKeySpec(key, "DESede");
      Cipher cipher = Cipher.getInstance("DESede");
      cipher.init(direction, skeySpec);
      return cipher.doFinal(inByte);
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
    String line = null;
    while ((line = br.readLine()) != null) {
      if (line.contains("BASE64")) {
        String[] values = line.split(",");
        String nvalue = values[0].replaceAll("SEARCH_KEY_BASE64_", "");
        String newLine = nvalue + "," + values[values.length - 1];
        bw.write(newLine);
      } else
        bw.write(line);
    }
    br.close();
    bw.flush();
    bw.close();
  }
}
