package com.sizzler.servlet;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PtoneServlet extends HttpServlet {

  private static final long serialVersionUID = 553652509912014023L;

  private static Logger logger = LoggerFactory.getLogger(HttpServlet.class);

  @Override
  public void init() throws ServletException {}

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    /*
     * String sid = req.getParameter("sid");
     *//*
        * HttpSession session = PtoneSessionContext.getSession(sid,req); PtoneUser user =
        * (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
        *//*
           * PtoneUser user = PtoneSessionContext.getLoginUser(sid); if(user == null){ return; }
           * String loginName = user.getUserEmail(); Map<String, String[]> jsonParams =
           * req.getParameterMap(); Map<String, String> userParams = new HashMap<>();
           * userParams.put("ptOneUserEmail",loginName);
           * 
           * // 获取url中的所有请求参数，加入到userParams中 Enumeration<String> enu = req.getParameterNames();
           * while (enu.hasMoreElements()) { String paramName = (String) enu.nextElement(); String
           * paramValue = req.getParameter(paramName); userParams.put(paramName, paramValue); }
           * 
           * String preUrl = ConfigHelper.getValue("ptone.ptengine.url"); String url =
           * req.getRequestURI(); url = url.replace(req.getContextPath(), ""); String method =
           * req.getMethod(); String result = ""; if(method.equalsIgnoreCase("get")){ result =
           * HttpClientUtil.doGet(preUrl + url, userParams); }else{ Iterator<String> iterator =
           * jsonParams.keySet().iterator(); String jsonKey = ""; while (iterator.hasNext()){
           * jsonKey = (String) iterator.next(); } logger.info("jsonKey:" + jsonKey);
           * if(url.contains("del")){ result =
           * HttpClientUtil.httpPostWithJSON(preUrl+url+"/"+loginName,jsonKey,userParams); }else{
           * result = HttpClientUtil.httpPostWithJSON(preUrl+url,jsonKey,userParams); } } if(null !=
           * result && !result.equalsIgnoreCase("")){
           * 
           * resp.reset(); resp.setContentType("application/json;charset=UTF-8");
           *//*
              * Cookie emailCookie = new Cookie("ptOneUserEmail",loginName);
              * emailCookie.setMaxAge(256000); emailCookie.setPath("/");
              * resp.addCookie(emailCookie);
              *//*
                 * PrintWriter writer; try { writer = resp.getWriter(); writer.write(result);
                 * writer.close(); writer.flush(); } catch (IOException e) { e.printStackTrace(); }
                 * }
                 */
  }
}
