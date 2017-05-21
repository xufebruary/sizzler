package com.sizzler.controller.rest;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sizzler.common.MediaType;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.common.utils.BarCodeUtil;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.annotation.MethodRemark;

/**
 * 公共资源
 * 
 * @date: 2016年12月13日
 * @author peng.xu
 */
@Controller
@Scope("prototype")
@RequestMapping("/public")
public class PublicController {

  private static Logger logger = LoggerFactory.getLogger(PublicController.class);

  @Autowired
  private ServiceFactory serviceFactory;

  /**
   * 二维码生成接口
   * @param url 需要生成二维码的url
   * @date: 2016年12月13日
   * @author peng.xu
   */
  @MethodRemark(remark = OpreateConstants.Public.GET_2D_BAR_CODE,
      domain = OpreateConstants.BusinessDomain.PUBLIC)
  @RequestMapping(value = "2d-bar-code", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  public @ResponseBody
  void get2DBarCode(@RequestParam(value = "url", required = true) String url,
      HttpServletRequest request, HttpServletResponse response) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      OutputStream outputStream = response.getOutputStream();
      BarCodeUtil.generate2DBarCode(url, outputStream, "jpg", 500, 500);
      logger.info("get 2d-bar-code<" + url + "> img success.");
    } catch (Exception e) {
      logger.error("get 2d-bar-code<" + url + "> img error.", e);
      jsonView.errorPack("get 2d-bar-code<" + url + "> img error.", e);
    }
  }

  /**
   * shortUrl访问接口（此接口为在后端解析短链后转发调用的接口，前端不使用）
   * 
   * @param shortKey 短链对应的 key
   * @date: 2016年12月13日
   * @author peng.xu
   */
  @MethodRemark(remark = OpreateConstants.Public.REQUEST_SHORT_URL,
      domain = OpreateConstants.BusinessDomain.PUBLIC)
  @RequestMapping(value = "short-url/{shortKey}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  public @ResponseBody
  void requestShortUrl(@PathVariable("shortKey") String shortKey, HttpServletRequest request,
      HttpServletResponse response) {
//    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      String redirectUrl = null;
//
//      // 获取当前请求的url
//      String requestUrl = request.getRequestURL().toString();
//
//      // 根据requestUrl截取域名
//      int shortDomainStartIndex = requestUrl.indexOf("//") + "//".length();
//      int shortDomainEndIndex = requestUrl.indexOf("/", shortDomainStartIndex);
//      String shortDomain = requestUrl.substring(shortDomainStartIndex, shortDomainEndIndex);
//
//      // 获取对应shortUrlDomain 的 longUrlDomain
//      String longDomain = Constants.getLongUrlDomain(shortDomain);
//
//      PtoneShortUrl ptoneShortUrl = serviceFactory.getPtoneShortUrlService().get(shortKey);
//      if (ptoneShortUrl != null) {
//        String longUrl = ptoneShortUrl.getUrl();
//        // 替换域名为当前访问短域名对应的长域名
//        if (StringUtil.isNotBlank(ptoneShortUrl.getDomain()) && StringUtil.isNotBlank(longDomain)) {
//          redirectUrl = longUrl.replaceFirst(ptoneShortUrl.getDomain(), longDomain);
//        } else {
//          redirectUrl = longUrl;
//        }
//      } else {
//        if (StringUtil.isBlank(longDomain)) {
//          longDomain = Constants.LONG_URL_DOMAIN_JP;
//        }
//        String urlProtocol = Constants.getUrlProtocol(longDomain);
//        redirectUrl = urlProtocol + longDomain + "/404?type=shorturl_not_found";
//      }
//
//      logger.info("request shortUrl::" + shortKey + " --> " + redirectUrl);
//      response.sendRedirect(redirectUrl);
//    } catch (Exception e) {
//      logger.error("request shortUrl<" + shortKey + "> error.", e);
//      jsonView.errorPack("request shortUrl<" + shortKey + "> error.", e);
//    }
  }

  /**
   * shortUrl生成接口
   * @param shortUrlVo
   * @return
   * @date: 2016年12月13日
   * @author peng.xu
   */
//  @MethodRemark(remark = OpreateConstants.Public.BUILD_SHORT_URL,
//      domain = OpreateConstants.BusinessDomain.PUBLIC)
//  @RequestMapping(value = "short-url", method = RequestMethod.POST,
//      produces = MediaType.APPLICATION_JSON)
//  public @ResponseBody
//  JsonView buildShortUrl(@RequestBody ShortUrlVo shortUrlVo, HttpServletRequest request,
//      HttpServletResponse response) {
//    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      String shortUrl = null;
//      String longUrl = shortUrlVo.getUrl();
//
//      // 根据longUrl截取域名
//      int longDomainStartIndex = longUrl.indexOf("//") + "//".length();
//      int longDomainEndIndex = longUrl.indexOf("/", longDomainStartIndex);
//      String longDomain = longUrl.substring(longDomainStartIndex, longDomainEndIndex);
//
//      // 获取对应longUrlDomain 的 shortUrlDomain
//      String shortDomain = Constants.getShortUrlDomain(longDomain);
//      if (StringUtil.isBlank(shortDomain)) {
//        shortDomain = Constants.SHORT_URL_DOMAIN_JP;
//      }
//
//      // 前端短链不显示 https://
//      // String urlProtocol = Constants.getUrlProtocol(shortDomain);
//      String urlProtocol = "";
//
//      // 判断是否已经存在当前链接的短链映射
//      PtoneShortUrl existPtoneShortUrl = serviceFactory.getPtoneShortUrlService().getByUrl(longUrl);
//      if (existPtoneShortUrl == null) {
//        String shortKey = UuidUtil.generateShortUuid().toLowerCase();
//        // 判断是否存在shortKey,如果存在则重新生成
//        while (serviceFactory.getPtoneShortUrlService().get(shortKey) != null) {
//          logger.warn("generate ShortKey for: " + longUrl);
//          shortKey = UuidUtil.generateShortUuid().toLowerCase();
//        }
//
//        // 保存短链映射记录
//        PtoneShortUrl ptoneShortUrl = new PtoneShortUrl();
//        ptoneShortUrl.setShortKey(shortKey);
//        ptoneShortUrl.setDomain(longDomain);
//        ptoneShortUrl.setUrl(longUrl);
//        ptoneShortUrl.setCreateTime(JodaDateUtil.getCurrentDateTime());
//        serviceFactory.getPtoneShortUrlService().save(ptoneShortUrl);
//
//        shortUrl = urlProtocol + shortDomain + "/" + shortKey;
//      } else {
//        shortUrl = urlProtocol + shortDomain + "/" + existPtoneShortUrl.getShortKey();
//      }
//      jsonView.successPack(shortUrl);
//      logger.info("build shortUrl::" + shortUrlVo.getUrl() + " ---> " + shortUrl);
//    } catch (Exception e) {
//      logger.error("build shortUrl<" + shortUrlVo.getUrl() + "> error.", e);
//      jsonView.errorPack("build shortUrl<" + shortUrlVo.getUrl() + "> error.", e);
//    }
//    return jsonView;
//  }

}
