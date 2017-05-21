package com.sizzler.system.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sizzler.common.utils.WebUtil;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.auth.annotation.Auth;

public class AuthInterceptor extends HandlerInterceptorAdapter {

  @Autowired
  private ServiceFactory serviceFactory;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
      Auth auth = ((HandlerMethod) handler).getMethodAnnotation(Auth.class);
      // 没有声明需要权限
      if (auth == null || auth.validate() == false)
        return true;
      else {
        if (hasPermissions(request))// 如果验证成功返回true
          return true;
        else {// 如果验证失败
          response.setStatus(480);
          WebUtil.sendMessage(response, "noPermission");
          return false;
        }
      }
    } else
      return super.preHandle(request, response, handler);
  }

  public boolean hasPermissions(HttpServletRequest request) throws ClassNotFoundException {
//    boolean permission = false;
//    String sid = request.getParameter("sid");
//    String requestURI = request.getRequestURI();
//    if (StringUtil.hasText(sid) && StringUtil.hasText(requestURI)) {
//      // PtoneSession session =
//      // serviceFactory.getPtoneSessionContext().getSession(sid);
//      // List<PtoneSysPermission> sysPermissions = session.getSysPermissions();
//      // 实时取
//      PtoneUser user = serviceFactory.getPtoneSessionContext().getLoginUser(sid);
//      List<PtoneSysPermission> sysPermissions = serviceFactory.getPtonePermissionManagerService()
//          .findUserPermissionByUid(user.getPtId());
//      if (null != sysPermissions && !sysPermissions.isEmpty()) {
//        for (int i = 0; i < sysPermissions.size(); i++) {
//          PtoneSysPermission per = sysPermissions.get(i);
//          String url = per.getUrl();
//          if (StringUtil.hasText(url)) {
//            String urls[] = url.split(",");
//            for (String l : urls) {
//              if (requestURI.startsWith(l)) {
//                permission = true;
//                break;
//              }
//            }
//          }
//        }
//      }
//    }
//    return permission;
    return true;
  }
}
