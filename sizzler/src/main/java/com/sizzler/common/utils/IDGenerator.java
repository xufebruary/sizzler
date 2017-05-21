package com.sizzler.common.utils;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.time.DateFormatUtils;

public class IDGenerator {

  private static int COUNT = 0;

  private static IDGenerator generator = new IDGenerator();

  private IDGenerator() {
  }

  public String getID() {
    UUID uid = UUID.randomUUID();
    String id = uid.toString();
    id = id.toUpperCase();
    id = id.replace("-", "");
    return id;
  }

  public static IDGenerator getGenerator() {
    return generator;
  }

  /**
   * 生成随机字母
   */
  public String getRandomChar() {
    final int maxNum = 25;
    int i; // 生成的随机数
    int count = 0; // 生成的密码的长度
    char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
        'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

    StringBuffer pwd = new StringBuffer("");
    Random r = new Random();
    while (count < 12) {
      // 生成随机数，取绝对值，防止生成负数，
      i = Math.abs(r.nextInt(maxNum));
      if (i >= 0 && i < str.length) {
        pwd.append(str[i]);
        count++;
      }
    }
    return pwd.toString().toUpperCase();
  }

  /**
   * 获取26位唯一字符串,前15位距离19700101的时间毫秒数，后11单独计数值
   * 
   * @return
   */
  public static synchronized String getUniqueID() {
    long millis = System.currentTimeMillis();
    return num2Str(millis, 15) + num2Str(++COUNT, 11);
  }

  public static synchronized Long getDateUniqueID() {
    String uniqueStr = num2Str(17) + num2Str(++COUNT, 2);

    return new Long(uniqueStr);
  }

  /**
   * 数字按照指定长度
   * 
   * @param number
   * @param width
   * @return
   */
  public static String num2Str(long number, int width) {
    String numStr = String.valueOf(number);

    int len = numStr.length() - width;
    if (len > 0) {
      numStr = numStr.substring(len);
    }

    width -= numStr.length();
    StringBuffer zeroBuff = new StringBuffer();
    while (zeroBuff.length() < width) {
      zeroBuff.append("0");
    }
    return zeroBuff.toString() + numStr;
  }

  /**
   * 当前时间制定长度
   * 
   * @param width
   * @return
   */
  public static String num2Str(int width) {
    String numStr = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
    width -= numStr.length();
    StringBuffer zeroBuff = new StringBuffer();
    while (zeroBuff.length() < width) {
      zeroBuff.append("0");
    }
    return zeroBuff.toString() + numStr;
  }

  public static void main(String[] args) {

    // System.out.println(IDGenerator.getDateUniqueID());

    new IDGenerator().test();

    // for (int i = 0; i < 100; i++) {
    // System.out.println(IDGenerator.getGenerator().getRandomChar() + "   "
    // + IDGenerator.getGenerator().getID());
    // }

  }

  public void test() {
    for (int i = 0; i < 500; i++) {
      Thread thread = new Thread(new IDgen());
      thread.start();
    }
  }

  class IDgen implements Runnable {

    public void run() {
      System.out.println(Thread.currentThread().getName() + "--ID:" + getDateUniqueID());
    }

  }
}
