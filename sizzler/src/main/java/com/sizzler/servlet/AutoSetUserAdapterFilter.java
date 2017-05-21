/*
 * package com.ptmind.ptone.servlet;
 * 
 * 
 * import com.ptmind.ptone.model.session.PTSession; import com.ptmind.ptone.model.user.PtoneUser;
 * import com.ptmind.ptone.rest.service.UserService; import
 * com.ptmind.ptone.util.DubboServiceFactory; import org.jasig.cas.client.validation.Assertion;
 * 
 * import javax.servlet.*; import javax.servlet.http.Cookie; import
 * javax.servlet.http.HttpServletRequest; import javax.servlet.http.HttpServletResponse; import
 * java.io.IOException; import java.util.HashMap; import java.util.Map;
 */
/**
 * @ClassName: AutoSetUserAdapterFilter
 * @Description: cas
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2015-04-16
 * @author: zhangli
 */
/*
 * 
 * public class AutoSetUserAdapterFilter implements Filter {
 * 
 * 
 * private static String casServerLogoutUrl;
 */
/**
 * Method description
 */
/*
 * 
 * public void destroy() { }
 */
/**
 * Method description: 获取用户名,新建Session存入redis
 *
 * @param request
 * @param response
 * @param chain
 * @throws java.io.IOException
 * @throws javax.servlet.ServletException
 * @author: zhangli
 * @date: 2015-04-16
 */
/*
 * 
 * public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
 * IOException, ServletException { HttpServletResponse httpResponse = (HttpServletResponse)
 * response; HttpServletRequest httpRequest = (HttpServletRequest) request;
 * if(httpRequest.getParameter("logout")!=null){
 * if("true".equals(httpRequest.getParameter("logout").toString())){
 * httpRequest.getSession().invalidate();
 * 
 * httpResponse.sendRedirect(AutoSetUserAdapterFilter.casServerLogoutUrl); return; } } UserService
 * userService = DubboServiceFactory.getInstance().getUserService(); Object object =
 * httpRequest.getSession().getAttribute("_const_cas_assertion_");
 * 
 * if (object != null) { Assertion assertion = (Assertion) object; String loginName =
 * assertion.getPrincipal().getName();
 * 
 * //没登录 if (!userService.isLogin(loginName)) { System.out.println(loginName + " login...");
 * Map<String,Object[]> paramMap = new HashMap<>(); paramMap.put("userEmail", new
 * Object[]{loginName}); PtoneUser loginPtoneUser = userService.getByWhere(paramMap);
 * request.setAttribute("userID",loginPtoneUser.getPtId()); PTSession session = new PTSession();
 * session.setUser(loginPtoneUser); userService.createNewSession(loginPtoneUser.getUserEmail(),
 * 6*7200, session); System.out.println(loginPtoneUser.getUserEmail() + " into redis..."); }
 * 
 * httpRequest.setAttribute("ptOneUserEmail",loginName);
 * 
 * } else { httpRequest.getSession(false).invalidate();
 * httpResponse.sendRedirect(httpRequest.getRequestURI()); }
 * 
 * chain.doFilter(request, response); }
 */
/**
 * Method description
 *
 * @param fConfig
 * @throws javax.servlet.ServletException
 * @author: zhangli
 * @date: 2015-04-16
 */
/*
 * 
 * public void init(FilterConfig fConfig) throws ServletException {
 * AutoSetUserAdapterFilter.casServerLogoutUrl = fConfig.getInitParameter("casServerLogoutUrl"); } }
 */
