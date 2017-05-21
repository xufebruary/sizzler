package com.sizzler.common.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtil {

  private static Logger logger = LoggerFactory.getLogger(WebUtil.class);

  public static void sendMessage(HttpServletResponse response, String message) {
    response.setContentType("text/html;charset=UTF-8");
    PrintWriter writer;
    try {
      writer = response.getWriter();
      writer.write(message);
      writer.close();
    } catch (IOException e) {
      logger.error("Error: " + e.getMessage());
    }
  }

}
