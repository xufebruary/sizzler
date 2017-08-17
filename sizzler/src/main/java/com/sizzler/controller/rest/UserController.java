package com.sizzler.controller.rest;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.MediaType;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.common.utils.CodecUtil;
import com.sizzler.common.utils.DateUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.pmission.PtoneSysPermission;
import com.sizzler.domain.pmission.PtoneSysRole;
import com.sizzler.domain.session.dto.PtoneSession;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.space.dto.SpaceInfoDto;
import com.sizzler.domain.sys.SysMetaLog;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.user.PtoneUserBasicSetting;
import com.sizzler.domain.user.dto.PtoneBasicUser;
import com.sizzler.domain.user.dto.PtoneShareUserInfo;
import com.sizzler.domain.user.dto.SignInUser;
import com.sizzler.domain.user.vo.PtoneUserVo;
import com.sizzler.system.Constants;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.annotation.MethodRemark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Scope("prototype")
@RequestMapping("/users")
public class UserController {

  private Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private ServiceFactory serviceFactory;

  /**
   * 是否存在此email.
   */
  @RequestMapping(value = "exists/{email:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  public @ResponseBody
  JsonView checkEmailIsExists(@PathVariable("email") String email) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("status", new Object[] { Constants.validate });
      paramMap.put("userEmail", new Object[] { email });
      PtoneUser loginUser = serviceFactory.getUserService().getByWhere(paramMap);
      if (null != loginUser) {
        jsonView.messagePack(Constants.validate);
      } else {
        jsonView.messagePack(Constants.inValidate);
      }
    } catch (Exception e) {
      jsonView.errorPack("exists user error.", e);
    }
    return jsonView;
  }

  /**
   * 检查用户密码是否正确.
   */
  @RequestMapping(value = "password/check", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  public @ResponseBody
  JsonView checkUserPassword(@RequestBody PtoneUser user, HttpServletRequest request,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser sessionUser = serviceFactory.getSessionContext().getLoginUser(sid);
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("status", new Object[] { Constants.validate });
      paramMap.put("userEmail", new Object[] { sessionUser.getUserEmail() });
      PtoneUser dbUser = serviceFactory.getUserService().getByWhere(paramMap);
      if (null != user.getUserPassword() && dbUser.getUserPassword().equals(user.getUserPassword())) {
        jsonView.messagePack(Constants.validate);
      } else {
        jsonView.messagePack(Constants.inValidate);
      }
    } catch (Exception e) {
      jsonView.errorPack("check user password error.", e);
    }
    return jsonView;
  }

  /**
   * 得到当前用户的个人信息.
   */
  @RequestMapping(value = "u", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  public @ResponseBody
  JsonView getUserInfo(HttpServletRequest request,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("ptId", new Object[] { loginPtoneUser.getPtId() });
      paramMap.put("status", new Object[] { Constants.validate });
      loginPtoneUser = serviceFactory.getUserService().getByWhere(paramMap);
      if (null != loginPtoneUser) {
        loginPtoneUser.setUserPassword(null);
        PtoneUserBasicSetting setting = serviceFactory.getUserService().getUserSetting(
            loginPtoneUser.getPtId(), null);
        List<SpaceInfoDto> spaceList = serviceFactory.getSpaceService().getUserSpaceList(
            loginPtoneUser.getPtId());
        List<PtoneSysPermission> sysPermissions = serviceFactory.getPtonePermissionManagerService()
            .findUserPermissionByUid(loginPtoneUser.getPtId());
        List<PtoneSysRole> sysRoles = serviceFactory.getPtonePermissionManagerService()
            .findUserSysRoleByUid(loginPtoneUser.getPtId());
        session.setSysPermissions(sysPermissions);
        session.setSysRoles(sysRoles);
        List<PtoneDsInfo> dsList = serviceFactory.getPtoneDsService().getServiceDatasource(
            loginPtoneUser.getSource());
        // dsList = PermissionUtil.validateDataSourcePermission(dsList,
        // sysPermissions, "code",null);
        serviceFactory.getSessionContext().saveSession(session);
        Map<String, Object> param = new HashMap<>();
        param.put("userInfo", loginPtoneUser);
        param.put("setting", setting);
        param.put("permissions", sysPermissions);// 权限列表
        param.put("roles", sysRoles);// 角色列表
        param.put("space", spaceList);// 用户空间列表
        param.put("serviceDsList", dsList);// 高级服务和内测服务数据源列表
        jsonView.successPack(param);
      } else {
        jsonView.failedPack("user not find.");
      }
    } catch (Exception e) {
      jsonView.errorPack(" get user info error.", e);
    }
    return jsonView;
  }

  /**
   * 更新用户权限和空间拥有者保持一致.
   * 
   * @param request
   * @return
   * @author: zhangli
   * @date: 2016-10-19
   */
  @RequestMapping(value = "permission/update/{spaceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.User.SIGNIN, domain = OpreateConstants.BusinessDomain.USER)
  public @ResponseBody
  JsonView updateUserSpacePermission(HttpServletRequest request,
      @PathVariable("spaceId") String spaceId,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
      PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
      List<PtoneSysPermission> sysPermissions = serviceFactory.getPtonePermissionManagerService()
          .findUserPermissionByUid(spaceInfo.getOwnerId());
      List<PtoneSysRole> sysRoles = serviceFactory.getPtonePermissionManagerService()
          .findUserSysRoleByUid(spaceInfo.getOwnerId());
      List<PtoneDsInfo> dsList = serviceFactory.getPtoneDsService().getServiceDatasource(
          loginPtoneUser.getSource());
      // dsList = PermissionUtil.validateDataSourcePermission(dsList,
      // sysPermissions, "code",null);
      session.setSysPermissions(sysPermissions);
      session.setSysRoles(sysRoles);
      Map<String, Object> param = new HashMap<>();
      param.put("permissions", sysPermissions);// 权限列表
      param.put("roles", sysRoles);// 角色列表
      param.put("serviceDsList", dsList);// 高级服务和内测服务数据源列表
      jsonView.successPack(param);
    } catch (Exception e) {
      jsonView.errorPack("update User Space Permission error.", e);
    }
    return jsonView;
  }

  /**
   * 更新用户的个人信息.
   * 
   * @param request
   * @return
   * @author: zhangli
   * @date: 2015-07-1
   */
  @RequestMapping(value = "update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  public @ResponseBody
  JsonView updateUser(HttpServletRequest request, @RequestBody PtoneUserVo user,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // 更新session
      PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
      PtoneUser sessionUser = session.getAttribute(Constants.Current_Ptone_User);

      // 此处增加ptId判断，用户只能修改自己的用户信息更新用户信息
      // TODO: 此处判断user.ptId为了兼容以前代码，下次上线后去掉
      if (StringUtil.isBlank(user.getPtId()) || sessionUser.getPtId().equals(user.getPtId())) {

        if (user.getUserName() != null) {
          sessionUser.setUserName(user.getUserName());
          session.setAttribute(Constants.Current_Ptone_User, sessionUser);
          serviceFactory.getSessionContext().saveSession(session);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("updatePassword", false);
        PtoneUser ptoneUser = new PtoneUser();
        BeanUtils.copyProperties(user, ptoneUser);
        ptoneUser.setPtId(sessionUser.getPtId());
        ptoneUser.setUserEmail(null); // 系统不允许修改userEmail

        // 增加密码修改校验
        // TODO: 此处判断UserOldPassword为了兼容以前代码，下次上线后去掉
        if (StringUtil.isNotBlank(user.getUserOldPassword())) {
          if (ptoneUser.getUserPassword() != null) {
            PtoneUser dbUser = serviceFactory.getUserService().getPtoneUser(sessionUser.getPtId());
            // 校验旧密码是否正确，如果不正确不修改旧密码
            if (!dbUser.getUserPassword().equals(user.getUserOldPassword())) {
              logger.warn("user<" + sessionUser.getPtId()
                  + "> change password failed, old password not match !");
              ptoneUser.setUserPassword(null);
            } else {
              // 成修改密码后清空所有此email Session
              serviceFactory.getSessionContext().clearSession(dbUser.getUserEmail());
              resultMap.put("updatePassword", true);
            }
          }
        }
        serviceFactory.getUserService().update(ptoneUser);
        jsonView.successPack(resultMap);
      } else {
        logger.warn("update user failed, user.ptId<" + user.getPtId()
            + "> is not match session.ptId<" + sessionUser.getPtId() + "> !");
        jsonView.failedPack("update user failed, user.ptId is not match session.ptId ");
      }

    } catch (Exception e) {
      logger.error(" update user error.", e);
      jsonView.errorPack("update user error.", e);
    }
    return jsonView;
  }

  /**
   * 得到当前用户的基本信息.
   * 
   * @param request
   * @return
   * @author: zhangli
   * @date: 2015-07-1
   */
  @RequestMapping(value = "settings/info/{spaceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  public @ResponseBody
  JsonView getUserSettings(HttpServletRequest request, @PathVariable("spaceId") String spaceId,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      PtoneUserBasicSetting setting = serviceFactory.getUserService().getUserSetting(
          loginPtoneUser.getPtId(), spaceId);
      jsonView.successPack(setting);
    } catch (Exception e) {
      jsonView.errorPack("get user settings error.", e);
    }
    return jsonView;
  }

  /**
   * 登录验证
   * 
   * @param request
   * @param response
   * @return
   * @author: zhangli
   * @date: 2015-07-1
   */
  @RequestMapping(value = "signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.User.SIGNIN, domain = OpreateConstants.BusinessDomain.USER)
  public @ResponseBody
  JsonView signinUser(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(value = "userCookieTime", required = false) boolean userCookieTime,
      @RequestParam(value = "community", required = false) String community,
      @RequestBody SignInUser user) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    String userEmail = null;
    try {
      userEmail = user.getUserEmail();
      String password = user.getUserPassword();
      if (!StringUtil.hasText(userEmail)) {
        jsonView.failedPackRequest("LOGIN.ERROR_TIP.EMAIL_NULL", request);
        return jsonView;
      }
      if (!StringUtil.hasText(password)) {
        jsonView.failedPackRequest("LOGIN.ERROR_TIP.PASSWORD_NULL", request);
        return jsonView;
      }
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("status", new Object[] { Constants.validate });
      paramMap.put("userEmail", new Object[] { userEmail });
      PtoneUser loginUser = serviceFactory.getUserService().getByWhere(paramMap);
      if (loginUser == null) {
        jsonView.failedPackRequest("email_error", request);
        logger.info(userEmail + " signin failed,email not exists.");
        return jsonView;
      }
      request.setAttribute(Constants.Current_Ptone_User, loginUser);
      boolean isLogin = serviceFactory.getUserService().login(userEmail, password);
      Map<String, String> resultMap = new HashMap<>();
      if (!isLogin ) {
        logger.info(userEmail + " signin failed,password error");
        jsonView.failedPackRequest("password_error", request);
        return jsonView;
      } else {
        resultMap.put("uid", loginUser.getPtId());
        resultMap.put("ptEmail", loginUser.getUserEmail());
     // ptone登录(非社区)
          String sessionId = serviceFactory.getSessionContext().addSession(loginUser,user.getRememberMe());
          loginUser.setLoginCount(loginUser.getLoginCount() + 1);
          loginUser.setLastLoginDate(DateUtil.getDateTime());
          serviceFactory.getUserService().update(loginUser);
          resultMap.put("sid", sessionId);
        resultMap.put("type", Constants.OFFICIAL_USER);
        jsonView.successPack(resultMap);
      }
    } catch (Exception e) {
      jsonView.errorPackRequest(userEmail + " sign in error.", e, request);
    }
    return jsonView;
  }

  /**
   * 登出
   * 
   * @param request
   * @param response
   * @return
   * @author: zhangli
   * @date: 2015-07-1
   */
  @RequestMapping(value = "signout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  // @MethodRemark(remark=OpreateConstants.User.SIGNOUT,domain=OpreateConstants.BusinessDomain.USER)
  public JsonView signout(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    LogMessage logMessage = new LogMessage();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      if (loginPtoneUser != null) {
        logMessage.setUid(loginPtoneUser.getPtId());
        logMessage.setOperate(OpreateConstants.User.SIGNOUT);
        logMessage.addOperateInfo("userEmail", loginPtoneUser.getUserEmail());
      }
      serviceFactory.getSessionContext().delSession(sid);
      jsonView.messagePack("sign out success.");
    } catch (Exception e) {
      jsonView.errorPack("sign out error.", e);
    } finally {
      logger.info(logMessage.toString());
    }
    return jsonView;
  }

  public void clearSessionAndCookie(HttpServletRequest request, HttpServletResponse response) {
    HttpSession session = request.getSession();
    if (session != null) {
      session.removeAttribute(Constants.Current_Ptone_User);
      session.invalidate();
    }
  }

  /**
   * 验证请求,跳到密码重置页面.
   * 
   * @return
   * @author: zhangli
   * @date: 2015-08-9
   */
  @RequestMapping(value = "getPassword/{domain}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public void getPassword(@PathVariable("domain") String domain,
      @RequestParam(value = "email", required = true) String email,
      @RequestParam(value = "digitallySigned", required = true) String digitallySigned,
      HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Map<String, String> productParam =
    // Constants.getProductParamByDomain(domain,null);
    // String productDomain =
    // productParam.get(Constants.PRODUCT_PARAM_PRODUCT_DOMAIN);
    //
    // if (email == null || digitallySigned == null) {
    // response.sendRedirect("https://" + productDomain + "/signin");
    // return;
    // }
    // String forgotPasswordKey = "forgotPasswordKey:" +
    // CodecUtil.base64decode(email);
    // String reidsDigitallySigned =
    // serviceFactory.getRedisService().getValueByKey(forgotPasswordKey);
    // if (reidsDigitallySigned == null) {
    // // key过期跳到登录页或其它页
    // response.sendRedirect("https://" + productDomain + "/signin");
    // return;
    // }
    // // 数字签名不符合
    // if (!(reidsDigitallySigned.replaceAll("\"", "").equals(digitallySigned)))
    // {
    // response.sendRedirect("https://" + productDomain + "/signin");
    // return;
    // }
    // // 通过跳到密码修改
    // response.sendRedirect("https://" + productDomain + "/resetPassword?e="
    // + URLEncoder.encode(URLEncoder.encode(email, "utf-8"), "utf-8"));
    return;
  }

  /**
   * 单独启线程预制面板.
   * 
   * @return
   * @author: zhangli
   */
  /*
   * public void saveTempletByUserSource(final PtoneUser user,final String
   * spaceId){ new Thread(new Runnable() {
   * 
   * @Override public void run() { try { Map<String,Object[]> paramMap = new
   * HashMap<>(); paramMap.put("ptId",new Object[]{user.getPtId()});
   * PtoneUserBasicSetting setting =
   * serviceFactory.getUserSettingService().getByWhere(paramMap);
   * serviceFactory.getUserService().saveTempletByUserSource(user,
   * setting.getLocale(), spaceId); } catch (Exception e) {
   * logger.error("save Default Templet error from reset pwd.", e);
   * serviceFactory
   * .getMailFactory().sendEmail("save Default Templet error from reset pwd.",
   * user.getUserEmail(), OpreateConstants.PTONE_GROUP_EMAIL);
   * e.printStackTrace(); } } }).start(); }
   */

  /**
   * 得到当前用户上次所选的profile信息.
   * 
   * @param request
   * @return
   * @author: peng.xu
   */
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "settings/profileSelected", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  public @ResponseBody
  @Deprecated
  JsonView getUserProfileSelected(HttpServletRequest request,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      PtoneUserBasicSetting setting = serviceFactory.getUserService().getUserSetting(
          loginPtoneUser.getPtId(), null);
      String profileSelected = setting.getProfileSelected();
      Map<String, String> profileSelectedMap = null;
      if (StringUtil.isNotBlank(profileSelected)) {
        profileSelectedMap = JSON.parseObject(profileSelected, HashMap.class);
      }
      jsonView.successPack(profileSelectedMap);
    } catch (Exception e) {
      jsonView.errorPack(" get user profile selected settings error.", e);
    }
    return jsonView;
  }

  /**
   * 得到分享用户的基本信息.
   */
  @RequestMapping(value = "shareUserInfo/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getMetrics(@PathVariable("id") String id) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser user = serviceFactory.getUserService().getPtoneUser(id);
      PtoneUserBasicSetting setting = serviceFactory.getUserService().getUserSetting(id, null);
      PtoneShareUserInfo shareUser = new PtoneShareUserInfo();
      shareUser.setPtId(id);
      shareUser.setUserName(user.getUserName());
      shareUser.setWeekStart(setting.getWeekStart());
      shareUser.setLocale(setting.getLocale());

      jsonView.successPack(shareUser);
    } catch (Exception e) {
      jsonView.errorPack(" get share user info error.", e);
    }
    return jsonView;
  }

  /**
   * 单个修改用户权限
   */
  @RequestMapping(value = "getUserPermission/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getUserPermission(@PathVariable("userId") String userId,
      @RequestParam(value = "sid", required = true) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      jsonView.successPack(serviceFactory.getPtonePermissionManagerService().findUserSysRoleByUid(
          userId));
    } catch (Exception e) {
      jsonView.errorPack(" getUserPermission error.", e);
    }
    return jsonView;
  }

  public String createSpaceRedirectUrl(String email, String str)
      throws UnsupportedEncodingException {
    // 密钥key
    String key = UUID.randomUUID().toString();
    long invalidTime = System.currentTimeMillis() / 1000;
    // 数字签名
    String digitallySigned = CodecUtil.getMD5ofStr(email + "$" + invalidTime + key);
    String resetPasswordKey = "resetDataDeckPasswordKey:" + email;
    // serviceFactory.getRedisService().setKey(resetPasswordKey, -1,
    // digitallySigned);
    return Constants.webUIUrl.replaceFirst(".jp", str) + "/create-password?e="
        + URLEncoder.encode(URLEncoder.encode(CodecUtil.base64encode(email), "utf-8"), "utf-8");
  }

  public void sendSysMetaLogForPTESignup(PtoneUser user, PtoneUserBasicSetting setting) {
    try {
      PtoneBasicUser basicUser = new PtoneBasicUser();
      BeanUtils.copyProperties(user, basicUser);
      BeanUtils.copyProperties(setting, basicUser);
      SysMetaLog sysMetaLog = new SysMetaLog();
      sysMetaLog.setUid(user.getPtId());
      sysMetaLog.setServerTime(System.currentTimeMillis());
      sysMetaLog.setServerDate(DateUtil.getDateTime());
      sysMetaLog.setContent(JSON.toJSONString(basicUser));
      sysMetaLog.setOperate("middle-signup");
      // serviceFactory.getLogCollectUtil().sendData(sysMetaLog);
    } catch (BeansException e) {
      logger.error(user.getUserEmail() + " | send SysMetaLog For PTE Signup error");
      e.printStackTrace();
    }
  }

  /**
   * 分享链接的登录校验
   *
   * @param request
   * @param response
   * @return
   * @author:
   * @date: 2015-07-1
   */
  @RequestMapping(value = "shareSignin/{type}/{panelId}", method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON)
  public @ResponseBody JsonView shareSigninValidate(HttpServletRequest request,
                                                    HttpServletResponse response, @PathVariable("type") String type,
                                                    @PathVariable("panelId") String panelId,
                                                    @RequestParam(value = "password", required = false) String password) {
    JsonView jsonView = JsonViewFactory.createJsonView();

    try {
      Map<String, Object> param = new HashMap<>();
      String result = serviceFactory.getPanelService().validateSharePanel(panelId, password);
      if (PtonePanelInfo.PANEL_STATUS_VALIDATE.equals(result)) {
        Map<String, Object[]> paramMap = new HashMap<>();
        paramMap.put("panelId", new Object[] {panelId});
        PtonePanelInfo panelInfo = serviceFactory.getPanelService().getByWhere(paramMap);
        PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(panelInfo.getSpaceId());
        List<PtoneSysPermission> sysPermissions =
                serviceFactory.getPtonePermissionManagerService().findUserPermissionByUid(spaceInfo.getOwnerId());
        List<PtoneSysRole> sysRoles =
                serviceFactory.getPtonePermissionManagerService().findUserSysRoleByUid(spaceInfo.getOwnerId());
        param.put("permissions", sysPermissions);// 权限列表
        param.put("roles", sysRoles);// 角色列表

        HttpSession session = request.getSession();
        String sid = session.getId();
        String accessToken = CodecUtil.getMD5ofStr(sid + "_" + panelId);
        //serviceFactory.getRedisService().setKey(accessToken, 5 * 60, sid + "_" + panelId);// token有效时间为5分钟
        //session.setAttribute(Constants.Current_Ptone_Anonymous, new PtoneUser());
        //session.setAttribute(Constants.PT_ACCESS_TOKEN, accessToken);
        param.put("accessToken", accessToken);
        jsonView.successPack(param);
      } else{
        jsonView.failedPack(result, param);
        return jsonView;
      }
    } catch (Exception e) {
      jsonView.errorPack(type + "<" + panelId + "> share sign in error.", e);
    }
    return jsonView;
  }

}
