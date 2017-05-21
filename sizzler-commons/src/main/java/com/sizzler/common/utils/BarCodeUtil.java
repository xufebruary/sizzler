package com.sizzler.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class BarCodeUtil {

  public static void generate2DBarCode(String text, OutputStream stream, String format, int width,
      int height) throws Exception {
    Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
    hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
    hints.put(EncodeHintType.MARGIN, "0");
    BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width,
        height, hints);
    MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
  }

  public static void main(String[] args) {
    String text = "http://blog.csdn.net/huakaihualuo1223/article/details/7910232";
    int width = 100;
    int height = 100;
    String format = "png";
    OutputStream stream = null;
    try {
      File outputFile = new File("d:/bar_code." + System.currentTimeMillis() + ".png");
      stream = new FileOutputStream(outputFile);

      BarCodeUtil.generate2DBarCode(text, stream, format, width, height);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }

}
