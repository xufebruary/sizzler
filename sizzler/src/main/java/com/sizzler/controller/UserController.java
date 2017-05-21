package com.sizzler.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sizzler.common.MediaType;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.user.vo.ActiveUserVo;

/**
 * @ClassName: UserController
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2017/2/8
 * @author: zhangli
 */
@RestController("userApiController")
@RequestMapping("{version}/users")
public class UserController extends BaseController {

	private static final String ACTIVE_USER_REDIS_KEY = "resetDataDeckPasswordKey:";
	private static final String RESET_USER_PASSWORD_REDIS_KEY = "forgotPasswordKey:";

	/**
	 * 生成数字key,并且发送email.
	 *
	 * @return
	 * @author: zhangli
	 * @date: 2017-02-14
	 */
	@RequestMapping(value = "forgot/{email:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ResponseStatus(HttpStatus.CREATED)
	public void sendPasswordEmail(@PathVariable("email") String email,
																@RequestParam("domain") String domain, HttpServletRequest request) {
//		String fromUrl = request.getRequestURL().toString();
//		PtoneUser user = serviceFactory.getUserService().getUser(email);
//		if (null == user) {
//			throw new BusinessException(BusinessErrorCode.User.USER_EMAIL_NOT_EXISTS, "reset user pwd failed,caused by the input email not exists.");
//		} else if (user.getIsActivited().equals(Constants.inValidate)) {
//			throw new BusinessException(BusinessErrorCode.User.USER_NOT_ACTIVE_ERROR, "reset user pwd failed,caused by user was not active.");
//		}
//		PtoneUserBasicSetting setting = serviceFactory.getUserService().getUserSetting(user.getPtId(), null);
//		String propertyKey = (setting.getLocale() == null || setting.getLocale().equals("")) ? "ja_JP" : setting.getLocale();
//		// 修改密码时间过期间隔 24小时
//		int invalidInterval = 24 * 60 * 60;
//		// 具体过期时间
//		long invalidTime = System.currentTimeMillis() / 1000 + invalidInterval;
//		// 密钥key
//		String key = UUID.randomUUID().toString();
//		// 数字签名
//		String digitallySigned = CodecUtil.getMD5ofStr(email + "$" + invalidTime + key);
//		// 密码修改url
//		String sendUrl = fromUrl.replace("forgot/" + email, "forgot/validate") + "?email=" + CodecUtil.base64encode(email) + "&digitallySigned=" + digitallySigned + "&domain=" + domain;
//		String forgotPasswordKey = RESET_USER_PASSWORD_REDIS_KEY + email;
////		serviceFactory.getRedisService().setKey(forgotPasswordKey, invalidInterval, digitallySigned);
//		sendForgotPwdEmail(domain, user, sendUrl, propertyKey);
	}

	/**
	 * 重新发送用户激活邮件
	 *
	 * @return
	 * @author li.zhang
	 * @date 2017/2/14
	 */
	@RequestMapping(value = "active/repeat/{email:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ResponseStatus(HttpStatus.CREATED)
	public void repeatSendActiveUserEmail(HttpServletRequest request,@PathVariable("email") String email){
//		PtoneUser user = serviceFactory.getUserService().getUser(email);
//		if (null == user) {
//			throw new BusinessException(BusinessErrorCode.User.USER_EMAIL_NOT_EXISTS, "reset user pwd failed,caused by the input email not exists.");
//		}
//		PtoneUserBasicSetting setting = serviceFactory.getUserService().getUserSetting(user.getPtId(), null);
//		StrategyModel strategyModel = new StrategyModel();
//		strategyModel.setRequest(request);
//		strategyModel.setUser(user);
//		strategyModel.setSetting(setting);
//		strategyModel.setReplaceUrl("active/repeat/" + email);
//		serviceFactory.getStrategyFactoryContext().signUp(strategyModel, user.getSource());
	}

	/**
	 * 验证请求,跳到密码重置页面.
	 *
	 * @return
	 * @author: zhangli
	 * @date: 2017-02-14
	 */
	@RequestMapping(value = "forgot/validate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public void validateForgotPassword(@RequestParam("domain") String domain, @RequestParam(value = "email",
					required = true) String email,
													@RequestParam(value = "digitallySigned", required = true) String digitallySigned,
													HttpServletRequest request, HttpServletResponse response) throws IOException {

//		Map<String, String> productParam = Constants.getProductParamByDomain(domain, null);
//		String productDomain = productParam.get(Constants.PRODUCT_PARAM_PRODUCT_DOMAIN);
//    String signURL = "https://" + productDomain + "/signin";
//		if (email == null || digitallySigned == null) {
//			response.sendRedirect(signURL);
//			return;
//		}
//		String forgotPasswordKey = RESET_USER_PASSWORD_REDIS_KEY + CodecUtil.base64decode(email);
//		String redisDigitallySigned = serviceFactory.getRedisService().getValueByKey(forgotPasswordKey);
//		if (redisDigitallySigned == null) {
//			// key过期跳到登录页或其它页
//			response.sendRedirect(signURL);
//			return;
//		}
//		// 数字签名不符合
//		if (!(redisDigitallySigned.replaceAll("\"", "").equals(digitallySigned))) {
//			response.sendRedirect(signURL);
//			return;
//		}
//		// 通过跳到密码修改
//		response.sendRedirect("https://" + productDomain + "/resetPassword?e=" + URLEncoder.encode(URLEncoder.encode(email, "utf-8"), "utf-8"));
		return;
	}

	/**
	 * 验证datadeck重置密码请求,跳到密码重置页面.
	 *
	 * @return
	 * @author: zhangli
	 * @date: 2016-05-31
	 */
	@RequestMapping(value = "password/reset/validate", method = RequestMethod.GET,
					produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	public void verifyResetPasswordRequest(
					@RequestParam(value = "email", required = true) String email, @RequestParam(value = "source",
					required = true) String source,
					@RequestParam(value = "digitallySigned", required = true) String digitallySigned,
					HttpServletRequest request, HttpServletResponse response) throws IOException {

//		String urlPre = null;
//		if (source.contains("en")) {
//			urlPre = "https://dash.datadeck.com";
//		} else if (source.contains("jp")) {
//			urlPre = "https://dash.datadeck.jp";
//		} else if (source.contains("cn")) {
//			urlPre = "https://dash.datadeck.cn";
//		}
//
//		String logoutUrl = urlPre + "/signin";
//		logger.info("logoutUrl:" + logoutUrl);
//		if (email == null || digitallySigned == null) {
//			response.sendRedirect(logoutUrl);
//			return;
//		}
//		String decodeEmail = CodecUtil.base64decode(email);
//		PtoneUser dbUser = serviceFactory.getUserService().getUser(decodeEmail);
//		if(null == dbUser || dbUser.getIsActivited().equals(Constants.validate)){
//			//用户已激活
//			response.sendRedirect(logoutUrl);
//			return;
//		}
//		String resetPasswordKey = ACTIVE_USER_REDIS_KEY + decodeEmail;
//		String redisDigitallySigned = serviceFactory.getRedisService().getValueByKey(resetPasswordKey);
//		if (redisDigitallySigned == null) {
//			// key过期跳到登录页或其它页
//			response.sendRedirect(logoutUrl);
//			return;
//		}
//		// 数字签名不符合
//		if (!(redisDigitallySigned.replaceAll("\"", "").equals(digitallySigned))) {
//			response.sendRedirect(logoutUrl);
//			return;
//		}
//		String redirectUrl =
//						urlPre + "/create-password?e="
//										+ URLEncoder.encode(URLEncoder.encode(email, "utf-8"), "utf-8");
//		logger.info("redirectUrl:" + redirectUrl);
//		// 通过跳到密码修改
//		response.sendRedirect(redirectUrl);
		return;
	}

//	/**
//	 * 通过邮件激活用户并重置密码
//	 *
//	 * @return
//	 * @author li.zhang
//	 * @date 2017/2/8
//	 */
//	@RequestMapping(value = "active", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
//	@ResponseStatus(HttpStatus.CREATED)
//	public ResponseResult activeNewUser(@Valid @RequestBody ActiveUserVo activeUserVo, HttpServletRequest request) throws UnsupportedEncodingException {
//		String email = CodecUtil.base64decode(URLDecoder.decode(URLDecoder.decode(activeUserVo.getUserEmail(), "utf-8"), "utf-8"));
//		String redis_key = ACTIVE_USER_REDIS_KEY + email;
//		String redisDigitallySigned = serviceFactory.getRedisService().getValueByKey(redis_key);
//		if (redisDigitallySigned == null) {
//			throw new BusinessException(BusinessErrorCode.User.USER_ACTIVE_REDIS_KEY_VALIDATE, "active user failed,caused by redis key is validate.");
//		}
//		PtoneUser dbUser = serviceFactory.getUserService().getUser(email);
//		if (null == dbUser) {
//			throw new BusinessException(BusinessErrorCode.User.USER_EMAIL_NOT_EXISTS, "active user failed,the input email not exists.");
//		}
//		dbUser.setUserPassword(activeUserVo.getUserPassword());
//		// 更新修改密码次数
//		dbUser.setTotalPasswordChanges((dbUser.getTotalPasswordChanges() == null ? 0 : dbUser.getTotalPasswordChanges()) + 1);
//		dbUser.setIsActivited(Constants.validate);
//		// 激活时间
//		dbUser.setActiviteDate(DateUtil.getDateTime());
//		//setting的weekStart信息来自于用户注册时的设置，统一从用户取
//		PtoneUserBasicSetting setting = serviceFactory.getUserService().getUserSetting(dbUser.getPtId(), null);
//		// 发送激活成功邮件
//		StrategyModel strategyModel = new StrategyModel();
//		strategyModel.setUser(dbUser);
//		strategyModel.setSetting(setting);
//		serviceFactory.getStrategyFactoryContext().afterActive(strategyModel, dbUser.getSource());
//		String sessionId = serviceFactory.getPtoneSessionContext().addSession(dbUser);
//		// 登录次数+1
//		dbUser.setLoginCount((dbUser.getLoginCount() == null ? 0 : dbUser.getLoginCount()) + 1);
//		// 最后登录时间
//		dbUser.setLastLoginDate(dbUser.getActiviteDate());
//		SpaceInfoDto spaceDto = serviceFactory.getUserService().createDefaultSpaceAndResetPwd(dbUser, setting);
//		ActiveUserVo returnVo = new ActiveUserVo();
//		returnVo.setSid(sessionId);
//		returnVo.setSpaceId(spaceDto.getSpaceId());
//		returnVo.setSpaceName(spaceDto.getName());
//		returnVo.setSpaceDomain(spaceDto.getDomain());
//		request.setAttribute("active", "active");// 标识是新用户第一次重置密码激活，发日志用
//		request.setAttribute(Constants.Current_Ptone_User, dbUser);
////		// 修改成功移除redis key
////		serviceFactory.getRedisService().remove(redis_key);
//		return RestResultGenerator.genResult(returnVo);
//	}

	/**
	 * 忘记密码时找回密码.
	 * 未启用 todo
	 *
	 * @param request
	 * @return
	 * @author: zhangli
	 * @date: 2017-02-14
	 */
	@RequestMapping(value = "password/reset", method = RequestMethod.PUT,
					produces = MediaType.APPLICATION_JSON)
	@ResponseStatus(HttpStatus.CREATED)
	public void resetPassword(HttpServletRequest request,
														HttpServletResponse response,
														@RequestBody ActiveUserVo activeUserVo) throws UnsupportedEncodingException {
//		String email =
//						CodecUtil.base64decode(URLDecoder.decode(
//										URLDecoder.decode(activeUserVo.getUserEmail(), "utf-8"), "utf-8"));
//		String forgotPasswordKey = RESET_USER_PASSWORD_REDIS_KEY + email;
//		String redisDigitallySigned = serviceFactory.getRedisService().getValueByKey(forgotPasswordKey);
//		if (redisDigitallySigned == null) {
//			// key过期
//			throw new BusinessException(BusinessErrorCode.User.USER_ACTIVE_REDIS_KEY_VALIDATE, "reset user pwd failed,caused by redis key is validate.");
//		}
//		PtoneUser dbUser = serviceFactory.getUserService().getUser(email);
//		if (null == dbUser) {
//			throw new BusinessException(BusinessErrorCode.User.USER_EMAIL_NOT_EXISTS, "reset user pwd failed,the input email not exists.");
//		}
//		dbUser.setUserPassword(activeUserVo.getUserPassword());
//		// 更新修改密码次数
//		dbUser.setTotalPasswordChanges((dbUser.getTotalPasswordChanges() == null ? 0 : dbUser.getTotalPasswordChanges()) + 1);
//		serviceFactory.getUserService().update(dbUser);
//		// 修改成功移除redis key
//		serviceFactory.getRedisService().remove(forgotPasswordKey);
//		request.setAttribute(Constants.Current_Ptone_User, dbUser);
	}

	/**
	 * 忘记密码时发送重置密码邮件.
	 *
	 * @author: zhangli
	 * @date: 2017-02-14
	 */
	public void sendForgotPwdEmail(String domain, PtoneUser user, String content, String propertyKey) {
//		Map<String, String> productParam = Constants.getProductParamByDomain(domain, user.getSource());
//		String productDomain = productParam.get(Constants.PRODUCT_PARAM_PRODUCT_DOMAIN);
//		String supportEmail = productParam.get(Constants.PRODUCT_PARAM_SUPPORT_EMAIL);
//		String productLogo = productParam.get(Constants.PRODUCT_PARAM_PRODUCT_LOGO);
//		String productName = productParam.get(Constants.PRODUCT_PARAM_PRODUCT_NAME);
//		String senderEmail = productParam.get(Constants.PRODUCT_PARAM_SENDER_EMAIL);
//		String facebookIconLink = productParam.get(Constants.PRODUCT_PARAM_FACEBOOK_ICON_LINK);
//		String twitterIconLink = productParam.get(Constants.PRODUCT_PARAM_TWITTER_ICON_LINK);
//		String propertiesPath = "reset-password-info.properties";
//		if (DeployConstants.deployType.equals(Constants.LOCAL_DEPLOY_TYPE)) {
//			propertiesPath = "localDeploy/" + propertiesPath;
//		}
//
//		Map<String, String> emailParamMap = new HashMap<>();
//		emailParamMap.put("productDomain", productDomain);
//		emailParamMap.put("productName", productName);
//		emailParamMap.put("productLogo", productLogo);
//		emailParamMap.put("supportEmail", supportEmail);
//		emailParamMap.put("senderEmail", senderEmail);
//		emailParamMap.put("datadeck_year", DateUtil.getDateTime("yyyy"));
//		emailParamMap.put("sendUrl", content);
//		emailParamMap.put("facebook_link", facebookIconLink);
//		emailParamMap.put("twitter_link", twitterIconLink);
//		serviceFactory.getMailFactory().sendEmailUseTemplet(user.getUserEmail(), propertyKey,
//						propertiesPath, emailParamMap, productName, senderEmail);
	}

//	/**
//	 * 注册时发送重置密码邮件.
//	 *
//	 * @author: zhangli
//	 * @date: 2016-05-31
//	 */
//	public void sendResetPasswordEmail(Map<String, String> emailParamMap, StrategyModel model) {
//		String fromUrl = model.getRequest().getRequestURL().toString();
//		String fromUri = model.getRequest().getRequestURI();
//		String urlPre = fromUrl.replace(fromUri,"");
//		long invalidTime = System.currentTimeMillis() / 1000;
//		String source = model.getUser().getSource();
//		// 密钥key
//		String key = UUID.randomUUID().toString();
//		// 数字签名
//		String digitallySigned =
//						CodecUtil.getMD5ofStr(model.getUser().getUserEmail() + "$" + invalidTime + key);
//		// 密码修改url
//		String url = urlPre + "/api/" + Constants.API_VERSION_PERFIX + Constants.API_VERSION_1 + "/users/password/reset/validate" + "?email="
//										+ CodecUtil.base64encode(model.getUser().getUserEmail()) + "&digitallySigned="
//										+ digitallySigned + "&source=" + source;
//
//		String resetPasswordKey = ACTIVE_USER_REDIS_KEY + model.getUser().getUserEmail();
//		serviceFactory.getRedisService().setKey(resetPasswordKey, -1, digitallySigned);
//
//		try {
//			emailParamMap.put("facebook_link", model.getFacebookIconLink());
//			emailParamMap.put("twitter_link", model.getTwitterIconLink());
//			emailParamMap.put("datadeck_year", DateUtil.getDateTime("yyyy"));
//			emailParamMap.put("productLogo", Constants.DATADECK_MAIL_LOGO);
//			emailParamMap.put("sendUrl", url);
//			serviceFactory.getMailFactory().sendEmailUseTemplet(model.getUser().getUserEmail(),
//							model.getSetting().getLocale(), "datadeck-reset-password-info.properties", emailParamMap,
//							model.getSenderName(), model.getSenderEmail());
//			logger.info("send " + model.getUser().getUserEmail() + " datadeck reset password email.");
//		} catch (Exception e) {
//			logger.error("send email error.", e);
//		}
//	}

//	/**
//	 * 用户重置密码后发送激活成功邮件.
//	 *
//	 * @param emailParamMap
//	 * @param model
//	 * @return
//	 * @author: zhangli
//	 * @date: 2015-11-29
//	 */
//	public void sendEmailByActiveSuccess(Map<String, String> emailParamMap, StrategyModel model){
//		try {
//			serviceFactory.getMailFactory().sendEmailUseTemplet(model.getUser().getUserEmail(),
//							model.getSetting().getLocale(), "datadeck-reset-password-success-info.properties", emailParamMap,
//							model.getSenderName(), model.getSenderEmail());
//		} catch (Exception e) {
//			logger.error("send send Email By Active Success error.", e);
//		}
//	}

}
