package com.sizzler.controller.rest;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.MediaType;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.common.utils.CodecUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.space.PtoneSpaceUser;
import com.sizzler.domain.space.dto.SpaceInfoDto;
import com.sizzler.domain.space.vo.SpacePanelVo;
import com.sizzler.domain.space.vo.SpaceVo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.system.Constants;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.annotation.MethodRemark;

@Controller
@Scope("prototype")
@RequestMapping("/space")
public class SpaceController {

  private Logger logger = LoggerFactory.getLogger(SpaceController.class);

  @Autowired
  private ServiceFactory serviceFactory;

  private static String urlKeySplitStr = "::";

  /**
   * 创建空间
   */
  @RequestMapping(value = "add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView addSpace(@RequestBody SpaceInfoDto space, HttpServletRequest request,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      SpaceInfoDto savedSpaceInfo =
          serviceFactory.getSpaceService().addSpace(space, loginPtoneUser);
      jsonView.successPack(savedSpaceInfo);
    } catch (Exception e) {
      jsonView.errorPack(JSON.toJSONString(space) + " | add space error.", e);
    }
    return jsonView;
  }

  /**
   * 检查域名是否可用
   */
  @RequestMapping(value = "checkDomain/{spaceId}/{domain:.+}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView checkDomain(@PathVariable("spaceId") String spaceId,
      @PathVariable("domain") String domain, HttpServletRequest request, @RequestParam(
          value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      boolean isValidate = !serviceFactory.getSpaceService().checkDomainExists(domain, spaceId);
      jsonView.successPack(isValidate);
    } catch (Exception e) {
      jsonView.errorPack(" check domain<" + domain + "> exists error.", e);
    }
    return jsonView;
  }

  /**
   * 修改空间
   */
  @MethodRemark(remark = OpreateConstants.Space.UPDATE_SPACE,
      domain = OpreateConstants.BusinessDomain.SPACE)
  @RequestMapping(value = "update", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView updateSpace(@RequestBody SpaceInfoDto space, HttpServletRequest request,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      space.setModifierId(loginPtoneUser.getPtId());
      space.setModifyTime(System.currentTimeMillis());
      serviceFactory.getSpaceService().updateSpace(space);
      jsonView.messagePack(" update space success.");
    } catch (Exception e) {
      jsonView.errorPack(JSON.toJSONString(space) + " | update space error.", e);
    }
    return jsonView;
  }

  /**
   * 删除空间
   */
  @MethodRemark(remark = OpreateConstants.Space.DEL_SPACE,
      domain = OpreateConstants.BusinessDomain.SPACE)
  @RequestMapping(value = "delete/{spaceId}/{domain}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView deleteSpace(@PathVariable("spaceId") String spaceId,
      @PathVariable("domain") String domain, HttpServletRequest request, @RequestParam(
          value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
//      PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
//      List<PtoneSpaceUser> spaceUserList =
//          serviceFactory.getSpaceService().getSpaceUserList(spaceId);
//
//      serviceFactory.getSpaceService().deleteById(spaceId, loginPtoneUser.getPtId(), true);
//
//      // 删除空间后发送邮件，通知所有成员空间已经删除
//      PtoneUserBasicSetting setting =
//          serviceFactory.getUserService().getUserSetting(loginPtoneUser.getPtId(), null);
//      String localeKey = StringUtil.isBlank(setting.getLocale()) ? "en_US" : setting.getLocale();
//      String sender = loginPtoneUser.getUserName();
//
//      Map<String, String> productParam =
//          Constants.getProductParamByDomain(domain, loginPtoneUser.getSource());
//      String productDomain = productParam.get(Constants.PRODUCT_PARAM_PRODUCT_DOMAIN);
//      String supportEmail = productParam.get(Constants.PRODUCT_PARAM_SUPPORT_EMAIL);
//      String productLogo = productParam.get(Constants.PRODUCT_PARAM_PRODUCT_LOGO);
//      String productName = productParam.get(Constants.PRODUCT_PARAM_PRODUCT_NAME);
//      String senderEmail = productParam.get(Constants.PRODUCT_PARAM_SENDER_EMAIL);
//      String propertiesPath = "delete-space-notify.properties";
//      if (DeployConstants.deployType.equals(Constants.LOCAL_DEPLOY_TYPE)) {
//        propertiesPath = "localDeploy/" + propertiesPath;
//      }
//
//      for (PtoneSpaceUser user : spaceUserList) {
//        if (PtoneSpaceUser.TYPE_FOLLOWER.equals(user.getType())
//            && PtoneSpaceUser.STATUS_ACCEPTED.equals(user.getStatus())) {
//          Map<String, String> paramMap = new HashMap<String, String>();
//          paramMap.put("receiver", user.getUserEmail());
//          paramMap.put("sender", sender);
//          paramMap.put("spaceName", spaceInfo.getName());
//          paramMap.put("productDomain", productDomain);
//          paramMap.put("productLogo", productLogo);
//          paramMap.put("productName", productName);
//          paramMap.put("supportEmail", supportEmail);
//          paramMap.put("spaceUrl", "https://" + productDomain + "/" + spaceInfo.getDomain() + "/");
//          serviceFactory.getMailFactory().sendEmailUseTemplet(user.getUserEmail(), localeKey,
//              propertiesPath, paramMap, sender, senderEmail);
//        }
//      }
//      jsonView.messagePack(" delete space success.");
//    } catch (Exception e) {
//      jsonView.errorPack(spaceId + " | delete space error.", e);
//    }
    return jsonView;
  }

  /**
   * 获取空间信息
   */
  @RequestMapping(value = "get/{spaceId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getSpaceById(@PathVariable("spaceId") String spaceId, HttpServletRequest request,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      SpaceInfoDto spaceInfo =
          serviceFactory.getSpaceService().getSpaceInfo(spaceId, loginPtoneUser.getPtId());
      jsonView.successPack(spaceInfo);
    } catch (Exception e) {
      jsonView.errorPack(" get space<" + spaceId + "> error.", e);
    }
    return jsonView;
  }

  /**
   * 获取用户所有空间列表（包括own和follow）
   */
  @RequestMapping(value = "list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getUserSpaceList(HttpServletRequest request, @RequestParam(value = "sid",
      required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      List<SpaceInfoDto> spaceList =
          serviceFactory.getSpaceService().getUserSpaceList(loginPtoneUser.getPtId());
      jsonView.successPack(spaceList);
    } catch (Exception e) {
      jsonView.errorPack(" get space list error.", e);
    }
    return jsonView;
  }

  /**
   * 获取空间下的用户列表
   */
  @RequestMapping(value = "users/{spaceId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getSpaceUserList(@PathVariable("spaceId") String spaceId,
      HttpServletRequest request, @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneSpaceUser> userList = serviceFactory.getSpaceService().getSpaceUserList(spaceId);
      jsonView.successPack(userList);
    } catch (Exception e) {
      jsonView.errorPack(" get space<" + spaceId + "> user list error.", e);
    }
    return jsonView;
  }

  /**
   * 邀请用户加入到空间
   */
  @RequestMapping(value = "inviteUsers/{spaceId}/{domain}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark = OpreateConstants.Space.INVITE_USER,
      domain = OpreateConstants.BusinessDomain.SPACE)
  public JsonView inviteUsers(@PathVariable("spaceId") String spaceId,
      @PathVariable("domain") String domain, @RequestBody List<String> emails,
      HttpServletRequest request, @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
//      PtoneUserBasicSetting setting =
//          serviceFactory.getUserService().getUserSetting(loginPtoneUser.getPtId(), null);
//      String localeKey = StringUtil.isBlank(setting.getLocale()) ? "en_US" : setting.getLocale();
//      String sender = loginPtoneUser.getUserName();
//
//      PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
//
//      List<PtoneSpaceUser> spaceUserList =
//          serviceFactory.getSpaceService().getSpaceUserList(spaceId);
//      List<String> userEmailList = new ArrayList<String>();
//      for (PtoneSpaceUser spaceUser : spaceUserList) {
//        if (spaceUser != null && PtoneSpaceUser.STATUS_ACCEPTED.equals(spaceUser.getStatus())) {
//          userEmailList.add(spaceUser.getUserEmail());
//        }
//      }
//
//      // 创建follower用户列表
//      serviceFactory.getSpaceService().inviteUsers(spaceId, emails, loginPtoneUser);
//
//      Map<String, String> productParam =
//          Constants.getProductParamByDomain(domain, loginPtoneUser.getSource());
//      String productDomain = productParam.get(Constants.PRODUCT_PARAM_PRODUCT_DOMAIN);
//      String officialDomain = productParam.get(Constants.PRODUCT_PARAM_OFFICIAL_DOMAIN);
//      String supportEmail = productParam.get(Constants.PRODUCT_PARAM_SUPPORT_EMAIL);
//      String productLogo = productParam.get(Constants.PRODUCT_PARAM_PRODUCT_LOGO);
//      String productName = productParam.get(Constants.PRODUCT_PARAM_PRODUCT_NAME);
//      String senderEmail = productParam.get(Constants.PRODUCT_PARAM_SENDER_EMAIL);
//      String propertiesPath = "invite-user-to-space.properties";
//      if (DeployConstants.deployType.equals(Constants.LOCAL_DEPLOY_TYPE)) {
//        propertiesPath = "localDeploy/" + propertiesPath;
//      }
//      for (String receiver : emails) {
//        if (userEmailList.contains(receiver)) {
//          continue; // 如果已经接受邀请不发送邮件
//        }
//        String urlKey = spaceId + urlKeySplitStr + receiver + urlKeySplitStr + sender;
//        String inviteUrl =
//            "https://" + productDomain + "/Invites/"
//                + URLEncoder.encode(CodecUtil.base64encode(urlKey), "utf-8");
//
//        Map<String, String> paramMap = new HashMap<String, String>();
//        paramMap.put("receiver", receiver);
//        paramMap.put("sender", sender);
//        paramMap.put("spaceName", spaceInfo.getName());
//        paramMap.put("productDomain", productDomain);
//        paramMap.put("officialDomain", officialDomain);
//        paramMap.put("productName", productName);
//        paramMap.put("productLogo", productLogo);
//        paramMap.put("supportEmail", supportEmail);
//        paramMap.put("spaceUrl", "https://" + productDomain + "/" + spaceInfo.getDomain() + "/");
//        paramMap.put("inviteUrl", inviteUrl);
//        serviceFactory.getMailFactory().sendEmailUseTemplet(receiver, localeKey, propertiesPath,
//            paramMap, sender, senderEmail);
//      }
//      jsonView.successPack("invite user success");
//    } catch (Exception e) {
//      jsonView.errorPack(" invite user to Space<" + spaceId + "> error.", e);
//    }
    return jsonView;
  }

  /**
   * 校验邀请用户加入到空间的链接
   */
  @RequestMapping(value = "checkInviteUrl/{urlId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView checkInviteUrl(@PathVariable("urlId") String urlId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      Map<String, String> resultMap = new HashMap<String, String>();
      resultMap.put("result", PtoneSpaceUser.INVITE_URL_STATUS_INVALIDATE);
      String urlKey = CodecUtil.base64decode(URLDecoder.decode(urlId, "utf-8"));
      if (StringUtil.isNotBlank(urlKey) && urlKey.contains(urlKeySplitStr)
          && urlKey.split(urlKeySplitStr).length == 3) {
        String spaceId = urlKey.split(urlKeySplitStr)[0];
        String userEmail = urlKey.split(urlKeySplitStr)[1];
        String sender = urlKey.split(urlKeySplitStr)[2];

        PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
        String result = serviceFactory.getSpaceService().checkInviteUrl(spaceInfo, userEmail);

        // add by li.zhang 20160920
        Map<String, Object[]> paramMap = new HashMap<>();
        paramMap.put("userEmail", new Object[] {spaceInfo.getOwnerEmail()});
        paramMap.put("status", new Object[] {Constants.validate});
        PtoneUser senderUser = serviceFactory.getUserService().getByWhere(paramMap);

        resultMap.put("spaceId", spaceId);
        resultMap.put("userEmail", userEmail);
        resultMap.put("result", result);
        resultMap.put("salesManager",
            senderUser.getSalesManager() == null ? "" : senderUser.getSalesManager());
        if (spaceInfo != null) {
          resultMap.put("spaceName", spaceInfo.getName());
        }
      }

      jsonView.successPack(resultMap);
    } catch (Exception e) {
      jsonView.errorPack(" check invite url<" + urlId + "> error.", e);
    }
    return jsonView;
  }

  /**
   * 接受用户邀请
   */
  @RequestMapping(value = "acceptInvite/{urlId}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView acceptInvite(@PathVariable("urlId") String urlId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      String urlKey = CodecUtil.base64decode(URLDecoder.decode(urlId, "utf-8"));
      if (StringUtil.isNotBlank(urlKey) && urlKey.contains(urlKeySplitStr)
          && urlKey.split(urlKeySplitStr).length == 3) {
        String spaceId = urlKey.split(urlKeySplitStr)[0];
        String userEmail = urlKey.split(urlKeySplitStr)[1];
        String sender = urlKey.split(urlKeySplitStr)[2];
        serviceFactory.getSpaceService().acceptInvite(spaceId, userEmail);
        jsonView.successPack("accept invite url<" + urlId + "> success.");
      } else {
        jsonView.failedPack("accept invite url<" + urlId + "> failed.");
      }

    } catch (Exception e) {
      jsonView.errorPack("accept invite url<" + urlId + "> error.", e);
    }
    return jsonView;
  }

  /**
   * 转让空间
   */
  @RequestMapping(value = "changeOwner/{spaceId}/{newOwnerId}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView changeSpaceOwner(@PathVariable("spaceId") String spaceId,
      @PathVariable("newOwnerId") String newOwnerId, HttpServletRequest request, @RequestParam(
          value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);

      serviceFactory.getSpaceService().changeSpaceOwner(spaceId, loginPtoneUser.getPtId(),
          newOwnerId);

      jsonView.successPack(" change space<" + spaceId + "> owner to " + newOwnerId + "success.");
    } catch (Exception e) {
      jsonView.errorPack(" change space<" + spaceId + "> owner to " + newOwnerId + "error.", e);
    }
    return jsonView;
  }

  /**
   * 删除空间用户
   */
  @RequestMapping(value = "deleteSpaceUser/{spaceId}/{email:.+}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView deleteSpaceUser(@PathVariable("spaceId") String spaceId,
      @PathVariable("email") String email, HttpServletRequest request, @RequestParam(value = "sid",
          required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);

      serviceFactory.getSpaceService().deleteSpaceUser(spaceId, email);

      jsonView.successPack(" delete space<" + spaceId + "> user<" + email + "> success.");
    } catch (Exception e) {
      jsonView.errorPack(" delete space<" + spaceId + "> user<" + email + "> error.", e);
    }
    return jsonView;
  }

  /**
   * 退出空间
   */
  @RequestMapping(value = "exitSpace/{spaceId}/{email:.+}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView exitSpace(@PathVariable("spaceId") String spaceId,
      @PathVariable("email") String email, HttpServletRequest request, @RequestParam(value = "sid",
          required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);

      serviceFactory.getSpaceService().deleteSpaceUser(spaceId, email);

      jsonView.successPack(" exit space<" + spaceId + "> user<" + email + "> success.");
    } catch (Exception e) {
      jsonView.errorPack(" exit space<" + spaceId + "> user<" + email + "> error.", e);
    }
    return jsonView;
  }


  /**
   * 校验空间及空间下的对应panel是否存在、是否有权限访问
   */
  @MethodRemark(remark = OpreateConstants.Space.VALIDATE_SPACE,
      domain = OpreateConstants.BusinessDomain.SPACE)
  @RequestMapping(value = "validate", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView validateSpace(@RequestBody SpacePanelVo spacePanelVo, @RequestParam(
      value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      String uid = null;
      if (loginPtoneUser != null) {
        uid = loginPtoneUser.getPtId();
      }

      if (spacePanelVo != null) {
        PtoneSpaceInfo spaceInfo = new PtoneSpaceInfo();
        spaceInfo.setDomain(spacePanelVo.getDomain());
        String result = serviceFactory.getSpaceService().validateSpacePanel(spaceInfo, spacePanelVo.getPanelId(), uid);
        jsonView.successPack(result);
      } else {
        logger.error("validate space failed spacePanelVo is null");
        jsonView.failedPack("validate space failed spacePanelVo is null");
      }
    } catch (Exception e) {
      logger.error("validate space <domain=" + spacePanelVo.getDomain() + ", panelId="
          + spacePanelVo.getPanelId() + ">  error.", e);
      jsonView.errorPack("validate space <domain=" + spacePanelVo.getDomain() + ", panelId="
          + spacePanelVo.getPanelId() + ">  error.", e);
    }
    return jsonView;
  }
  
  /**
   * 根据domain获取空间信息
   */
  @RequestMapping(value = "by-domain/{spaceDomain}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getSpaceByDomain(@PathVariable("spaceDomain") String spaceDomain,
      HttpServletRequest request, @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
      paramMap.put("domain", new Object[] {spaceDomain});
      paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
      PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().getByWhere(paramMap);
      if (spaceInfo != null) {
        SpaceVo spaceVo = new SpaceVo();
        BeanUtils.copyProperties(spaceInfo, spaceVo);
        jsonView.successPack(spaceVo);
      } else {
        jsonView.failedPack("space is not exists of domain : " + spaceDomain);
      }
    } catch (Exception e) {
      jsonView.errorPack("get space<" + spaceDomain + "> error.", e);
    }
    return jsonView;
  }

}
