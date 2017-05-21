package com.sizzler.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializeUtil {

  private static Logger logger = LoggerFactory.getLogger(SerializeUtil.class);

  /**
   * 序列化
   * 
   * @param object
   * @return
   */
  public static byte[] serialize(Object object) {
    ObjectOutputStream oos = null;
    ByteArrayOutputStream baos = null;
    try {
      // 序列化
      baos = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(baos);
      oos.writeObject(object);
      byte[] bytes = baos.toByteArray();
      return bytes;
    } catch (Exception e) {
      logger.error("serialize error");
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 反序列化
   * 
   * @param bytes
   * @return
   */
  public static Object unserialize(byte[] bytes) {
    ByteArrayInputStream bais = null;
    try {
      // 反序列化
      bais = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bais);
      return ois.readObject();
    } catch (Exception e) {
      logger.error("unserialize error");
    }
    return null;
  }
}
