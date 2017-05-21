package com.sizzler.provider.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ptmind on 2015/11/10.
 */
public class ResourceFileReadTest {

  public static void main(String[] args) throws Exception {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream("locations/region.properties");
    System.out.println(inputStream);
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    String line = "";

    while ((line = reader.readLine()) != null) {
      System.out.println(line);
    }
  }


}
