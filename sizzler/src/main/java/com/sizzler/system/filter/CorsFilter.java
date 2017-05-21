package com.sizzler.system.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: CorsFilter
 * @Description: 跨域
 */
@Component
public class CorsFilter implements Filter {
  
  private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

  /**
   * Default constructor.
   */
  public CorsFilter() {
    logger.info(">>>>>>>>>>>>>> init CorsFilter <<<<<<<<<<<<<<<<<");
  }

  /**
   * Method description
   */
  public void destroy() {}

  @SuppressWarnings("unchecked")
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;
    String methodType = req.getMethod();
    String host = req.getRemoteHost();
    String origin = "http://localhost:8081";
    resp.setHeader("Access-Control-Allow-Origin", origin);
    resp.setHeader("Access-Control-Allow-Headers",
        "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
    resp.setHeader("Access-Control-Allow-Headers", "Content-Type, *");
    resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
    resp.setHeader("Access-Control-Allow-Credentials", "true");
    resp.setHeader("Access-Control-Max-Age", "3600");
    if (methodType.equalsIgnoreCase("OPTIONS")) {
      return;
    }

    chain.doFilter(req, response);
  }

  public void init(FilterConfig fConfig) throws ServletException {}
}
