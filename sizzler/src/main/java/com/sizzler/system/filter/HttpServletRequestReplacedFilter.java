package com.sizzler.system.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.sizzler.common.utils.HttpUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.system.Constants;

public class HttpServletRequestReplacedFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) servletRequest;
    String url = req.getRequestURI();
    if (StringUtil.startsWithIgnoreCase(url, Constants.EXCEL_FILE_UPLOAD)
        || StringUtil.startsWithIgnoreCase(url, Constants.EXCEL_FILE_UPDATE)
        || StringUtil.startsWithIgnoreCase(url, Constants.IMG_UPLOAD)) {
      filterChain.doFilter(req, servletResponse);
      return;
    } else {
      CustomBodyReaderHttpServletRequestWrapper requestWrapper =
          new CustomBodyReaderHttpServletRequestWrapper((HttpServletRequest) servletRequest);
      String body = HttpUtil.getBodyString(requestWrapper);
      requestWrapper.setAttribute(Constants.REQUEST_BODY, body);
      filterChain.doFilter(requestWrapper, servletResponse);
    }
  }

  @Override
  public void destroy() {

  }
}
