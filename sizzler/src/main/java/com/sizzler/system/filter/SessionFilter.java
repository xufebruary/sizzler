package com.sizzler.system.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sizzler.common.utils.SpringContextUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.common.utils.WebUtil;
import com.sizzler.domain.session.dto.PtoneSession;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.service.UserService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

public class SessionFilter implements Filter {

  private Logger logger = LoggerFactory.getLogger(SessionFilter.class);

  @Autowired
  private UserService userService;

  @Autowired
  private ServiceFactory serviceFactory;

  // 不进行拦截的URL
  private String[] paths = {Constants.LOGIN_URL, Constants.LOGIN_URL_GA, Constants.LOGOUT_URL,
      Constants.PTENGINE_LOGIN_VERIFY, Constants.PTENGINE_AUTH, Constants.LOGOUT_VERIFY,
      Constants.REGISTER, Constants.CHECK_EMAIL, Constants.SEND_EMAIL_PASSWORD,
      Constants.DATADECK_VERIFY_RESET_PASSWORD_REQUEST, Constants.GET_PASSWORD,
      Constants.UPDATE_PASSWORD, Constants.GET_ACCESS_TOKEN_VERIFY, Constants.SHARE_SIGNIN_VERIFY,
      Constants.EXCEL_FILE_UPLOAD, Constants.EXCEL_FILE_UPDATE, Constants.UPDATE_FORWARD_COUNT,
      Constants.TEST, Constants.DISCOURSE, Constants.SPACE_INVITE,
      Constants.SPACE_CHECK_INVITE_URL, Constants.SPACE_ACCEPT_INVITE,
      Constants.SPACE_CHECK_DOMAIN, Constants.IMG_UPLOAD, Constants.SIGNUP_USER_BY_PTENGINE,
      Constants.PANELS_SHARE_VERIFICATION, Constants.SIGNUP_USER_BY_PTENGINE,
      Constants.PTENGINE_HEATMAP_DATA};

  // 不进行拦截的URL
  private String[] newApiSkipMethodAndPaths = {Constants.API_PANELS_SHARE_VERIFICATION,Constants.API_SEND_PASSWORD_EMAIL,Constants.API_VALIDATE_FORGOT_PASSWORD,
  Constants.API_REPEAT_SEND_ACTIVE_USER_EMAIL,Constants.API_ACTIVE_NEW_USER,Constants.API_VALIDATE_RESET_PASSWORD, Constants.API_COLLECT_JSON};

  // share页面通过accessToken校验的URL
  private String[] sharePaths = {Constants.SHARE_USER_INFO, Constants.SHARE_GET_PANEL,
      Constants.SHARE_GET_WIDGETS_WITH_LAYOUT, Constants.SHARE_GET_WIDGETS,
      Constants.SHARE_GET_WIDGET_DATA, Constants.SHARE_GET_BATCH_WIDGET_DATA,
      Constants.SHARE_GET_WIDGET_BY_ID};

  // share页面通过accessToken校验的URL
  private String[] newApiShareMethodAndPaths = {Constants.API_GET_PANEL};

  public SessionFilter() {}

  public void init(FilterConfig fConfig) throws ServletException {}

  public void destroy() {}

  /**
   * 根据session及session里的user情况进行过滤.
   * 
   * @param request
   * @param response
   * @return
   * @author: zhangli
   * @date: 2015-07-1
   */
  @SuppressWarnings("unchecked")
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;
    serviceFactory = SpringContextUtil.getBean("serviceFactory");

    String requestMethod = req.getMethod();
    String requestUri = req.getRequestURI();
    String requestUrl = req.getRequestURL().toString();
    logger.info(requestUrl);
    
    // 处理短链转发
    // for (String shortDomain : Constants.shortUrlDomainArray) {
    // if (requestUrl.contains("//" + shortDomain)) {
    // int shortKeyStartIndex = requestUrl.indexOf("/", requestUrl.indexOf(shortDomain));
    // String shortKey = requestUrl.substring(shortKeyStartIndex, requestUrl.length());
    // request.getRequestDispatcher("/pt/public/short-url/" + shortKey).forward(request, response);
    // return;
    // }
    // }

    // 放过api的session校验(/pt为系统默认添加， api-docs为swagger默认api文档访问路径)
    if (StringUtil.startsWithIgnoreCase(requestUri, "/api/api-docs")) {
      chain.doFilter(req, response);
      return;
    }

    // TODO: 所有不包含/pt的请求都看作为短链的根目录请求
    if (!requestUrl.contains("/pt/") && !requestUrl.contains("/api/")) {
      String shortKey = requestUrl.substring(requestUrl.lastIndexOf("/") + 1);
      shortKey = (StringUtil.isNotBlank(shortKey) ? shortKey : "null");
      request.getRequestDispatcher("/pt/public/short-url/" + shortKey).forward(request, response);
      return;
    }

    // 放过不需要校验session的访问
    for (String path : paths) {
      if (StringUtil.startsWithIgnoreCase(requestUri, path)) {
        chain.doFilter(req, response);
        return;
      }
    }
    for (String methodAndPath : newApiSkipMethodAndPaths) {
      String[] url = methodAndPath.split("\\|");
      String urlMethod = url[0];
      String urlPath = url[1];
      if (urlMethod.equalsIgnoreCase(requestMethod)
          && StringUtil.startsWithIgnoreCase(requestUri, urlPath)) {
        chain.doFilter(req, response);
        return;
      }
    }

    // 通过accessToken判断分享链接的访问权限
    for (String path : sharePaths) {
      if (StringUtil.startsWithIgnoreCase(requestUri, path)) {
        String accessToken = request.getParameter(Constants.PT_ACCESS_TOKEN);
        if (validateAccessToken(accessToken)) {
          chain.doFilter(req, response);
          return;
        }
      }
    }
    for (String methodAndPath : newApiShareMethodAndPaths) {
      String[] url = methodAndPath.split("\\|");
      String urlMethod = url[0];
      String urlPath = url[1];
      if (StringUtil.startsWithIgnoreCase(requestUri, urlPath)) {
        String accessToken = request.getParameter(Constants.PT_ACCESS_TOKEN);
        if (urlMethod.equalsIgnoreCase(requestMethod) && validateAccessToken(accessToken)) {
          chain.doFilter(req, response);
          return;
        }
      }
    }


    // 指定后缀文件直接放行
    /*
     * if (url.endsWith("signup.html") || url.endsWith("forgot.html") || url.endsWith(".css") ||
     * url.endsWith(".js") || url.endsWith(".jpg") || url.endsWith(".woff2") || url.endsWith(".gif")
     * || url.endsWith(".ico")) { chain.doFilter(req, response); return; }
     */

    try {
      if (StringUtil.hasText(Constants.middleVersionStatus)
          && Constants.middleVersionStatus.equals("true")) {
        String uiVersion = request.getParameter("uiVersion");
        if (!Constants.buildTimeStamp.equals(uiVersion)) {
          // goErrorVersionURL(resp);
          // return;
        }
      }
      String token = req.getHeader("token");
      String sid = request.getParameter("sid");
      if (StringUtil.isBlank(token)) {
        token = sid;
      }
      PtoneSession session = serviceFactory.getSessionContext().getSession(token);
      if (session != null) {
        PtoneUser sessionUser = session.getAttribute(Constants.Current_Ptone_User);
        if (null != sessionUser) {
          String uid = req.getHeader(Constants.CURRENT_UID);
          // 如果uid不为空，则校验uid与session中的userId是否匹配
          if (StringUtil.isNotBlank(uid) && !uid.equals(sessionUser.getPtId())) {
            goNotMatchUserSessionURL(resp, uid, sessionUser.getPtId());
          } else {
            serviceFactory.getSessionContext().updateSessionTime(token);
            chain.doFilter(req, response);
            return;
          }
        } else {
          goNoSessionURL(resp);
          // return;
        }
      } else {
        goNoSessionURL(resp);
        // return;
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   * 返回错误信息
   * 
   * @param resp
   * @param resp
   * @return
   * @author: zhangli
   * @date: 2015-07-1
   */
  private void goNoSessionURL(HttpServletResponse resp) throws IOException {
    String result = "noSession";
    logger.info("noSession,log in again.---------------------------------------------");
    resp.setStatus(460);
    WebUtil.sendMessage(resp, result);
  }

  // 自动登录时，只登录一次
  private synchronized void goNoSessionURL(HttpServletResponse resp, String sid) throws IOException {
//    String invalidateSid = "invalidateSid:" + sid;
//    if (StringUtil.isNotBlank(sid)) {
//      // redis分布式锁,锁住此key 60秒,其它过期请求直接return
//      if (serviceFactory.getRedisService().addLockIfNotExsit(invalidateSid, sid, 60)) {
//        logger.info("invalidateSid:" + invalidateSid);
//        goNoSessionURL(resp);
//      } else {
//        return;
//      }
//    } else {
//      goNoSessionURL(resp);
//    }
  }

  /**
   * 用户请求UID与session中userId不匹配时的处理
   * @param resp
   * @param uid
   * @throws IOException
   * @date: 2016年12月1日
   * @author peng.xu
   */
  private synchronized void goNotMatchUserSessionURL(HttpServletResponse resp, String uid,
      String sessionUseId) throws IOException {
    String result = "Forbidden, Not match UserSession !";
    logger.warn("Forbidden, UID<" + uid + "> Not Macth UserSession<" + sessionUseId + "> !");
    resp.setStatus(403);
    WebUtil.sendMessage(resp, result);
  }

  private void goErrorVersionURL(HttpServletResponse resp) throws IOException {
    String result = "errorVersion";
    logger.info("error version.---------------------------------------------");
    resp.setStatus(470);
    WebUtil.sendMessage(resp, result);
  }

  /**
   * 从cookie中获取用户名和密码.
   * 
   * @param request
   * @param resp
   * @return
   * @author: zhangli
   * @date: 2015-07-1
   */
  private PtoneUser getUserByCookie(HttpServletRequest request, HttpServletResponse resp) {
    String username = null;
    String password = null;
    Cookie cookies[] = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (Constants.PT_USERNAME.equals(cookie.getName())) {
          username = cookie.getValue();
        }
        if (Constants.PT_PASSWORD.equals(cookie.getName())) {
          password = cookie.getValue();
        }
      }
      if (StringUtil.hasText(username) && StringUtil.hasText(password)) {
        Map<String, Object[]> paramMap = new HashMap<>();
        try {
          paramMap.put("userEmail", new Object[] {URLDecoder.decode(username, "utf-8")});
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
        paramMap.put("userPassword", new Object[] {password});
        userService = SpringContextUtil.getBean("userService");
        PtoneUser loginUser = userService.getByWhere(paramMap);
        if (loginUser != null)
          return loginUser;
        else {
          return null;
        }
      }
    }
    return null;
  }

  private String getAccessUrl(HttpServletRequest request) {
    String requestUrl = request.getRequestURI();
    StringBuilder accessUrl = new StringBuilder();
    if (requestUrl.startsWith("/")) {
      if (requestUrl.length() > 1)
        accessUrl.append(requestUrl.substring(1));
    } else
      accessUrl.append(requestUrl);
    Enumeration iter = request.getParameterNames();
    boolean flag = true;
    while (iter.hasMoreElements()) {
      Object obj = iter.nextElement();
      String value = request.getParameter(obj.toString());
      if (flag) {
        flag = false;
        accessUrl.append("?");
      } else {
        accessUrl.append("&");
      }
      accessUrl.append(obj.toString()).append("=").append(value);
    }
    try {
      return URLEncoder.encode(accessUrl.toString(), "UTF8");
    } catch (UnsupportedEncodingException e) {
      return Constants.INDEX;
    }
  }

  private boolean validateAccessToken(String accessToken) {
    boolean result = false;
//    if (accessToken != null && !"".equals(accessToken)) {
//      redisService = SpringContextUtil.getBean("redisService");
//      result = redisService.existsKey(accessToken);
//    }
    return result;
  }

}
