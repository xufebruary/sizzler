package com.sizzler.common.utils;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class DiscourseUtil {

  public static String key = "for_test";

  public static String checksum(String macData) throws NoSuchAlgorithmException,
      UnsupportedEncodingException, InvalidKeyException {
    Mac mac = Mac.getInstance("HmacSHA256");
    byte[] keyBytes = key.getBytes("UTF-8");
    byte[] dataBytes = macData.getBytes("UTF-8");
    SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    mac.init(secretKey);
    byte[] doFinal = mac.doFinal(dataBytes);
    byte[] hexBytes = new Hex().encode(doFinal);
    return new String(hexBytes);
  }

}
