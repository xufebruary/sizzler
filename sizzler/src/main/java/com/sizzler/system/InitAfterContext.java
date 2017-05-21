package com.sizzler.system;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.sizzler.common.utils.SpringContextUtil;

public class InitAfterContext extends HttpServlet {

  private static final long serialVersionUID = 1053284986969204316L;

  private static final Logger logger = LoggerFactory.getLogger(InitAfterContext.class);

  /**
   * 系统常量初始化
   */
  @Override
  public void init() throws ServletException {
    initWebappPath();
    initApplicationContext();
    initGaClientId();
    initProviderInfo();
  }


  /**
   * 初始化应用程序上下文
   */
  private void initApplicationContext() throws ServletException {
    logger.debug("Init Application Context--Starting");
    try {
      WebApplicationContext context =
          WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
      Constants.setApplicationContext(context);
      ServletContext servletContext = getServletContext();
      Constants.setServletContext(servletContext);
    } catch (IllegalStateException e) {
      logger.error("Spring Init Application Context Error", e);
      throw new ServletException("Spring Init Application Context Error", e);
    }
    logger.debug("Init Application Context--End");
  }

  /**
   * 初始化应用程序根目录
   *
   * @throws javax.servlet.ServletException
   */
  private void initWebappPath() throws ServletException {
    String realPath = getServletContext().getRealPath("/");
    if (realPath == null || realPath.isEmpty()) {
      realPath = this.getClass().getClassLoader().getResource("/").getPath();
    }
    String webappPath = null;
    webappPath = realPath.replace('\\', '/').replaceAll("%20", " ");
    if (!webappPath.endsWith("/")) {
      webappPath = webappPath + "/";
    }

    Constants.setWebappPath(webappPath);
  }

  private void initGaClientId() throws ServletException {
//    GoogleService googleService = SpringContextUtil.getBean("googleService");
//    Constants.GA_CLIENT_AUTH_LIST = googleService.findGaClientAuth();

  }

  @SuppressWarnings("unchecked")
  private void initProviderInfo() {
//    ProviderInfoService providerInfoService = SpringContextUtil.getBean("providerInfoService");
//    providerInfoService.initProviderInfo();
  }

}
