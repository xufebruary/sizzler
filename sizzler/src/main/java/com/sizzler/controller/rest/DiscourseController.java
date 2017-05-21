package com.sizzler.controller.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.sizzler.common.utils.DiscourseUtil;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

/**
 * Created by ptmind on 2016/1/14.
 */
@Controller
@Scope("prototype")
@RequestMapping("/discourse")
public class DiscourseController {

  @Autowired
  private ServiceFactory serviceFactory;

  @RequestMapping(value = "sso", method = RequestMethod.GET)
  public RedirectView connect(@RequestParam(value = "sso", required = true) String sso,
      @RequestParam(value = "sig", required = true) String sig, @RequestParam(value = "ptSid",
          required = true) String sid, @RequestParam(value = "ptId", required = true) String ptId,
      @RequestParam(value = "ptEmail", required = true) String ptEmail, HttpServletRequest request) {
    RedirectView redirectView = null;

    try {
      // HttpSession session = SessionContext.getSession(sid, request);
      PtoneUser sessionUser = serviceFactory.getSessionContext().getLoginUser(sid);
      /*
       * if (sso == null || sig == null || sessionUser == null) { redirectView = new
       * RedirectView(Constants.webUIUrl); return redirectView; }
       */
      if (sso == null || sig == null) {
        redirectView = new RedirectView(Constants.webUIUrl + "/signin?community=true");
        return redirectView;
      }
      String email = "";// sessionUser.getUserEmail();
      String externalId = "";// sessionUser.getPtId();
      if (sessionUser == null) {
        if (ptId == null || ptEmail == null || ptId.equals("null") || ptEmail.equals("null")) {
          redirectView = new RedirectView(Constants.webUIUrl + "/signin?community=true");
          return redirectView;
        } else {
          email = ptEmail;
          externalId = ptId;
        }
      } else {
        email = sessionUser.getUserEmail();
        externalId = sessionUser.getPtId();
      }
      // PtoneUser sessionUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);

      String urlDecode = URLDecoder.decode(sso, "UTF-8");
      String nonce = new String(Base64.decodeBase64(urlDecode));
      String name = "";
      String userName = "";


      /*
       * String urlEncode = nonce + "&name=" + URLEncoder.encode(name, "UTF-8") + "&username=" +
       * URLEncoder.encode(userName, "UTF-8") + "&email=" + URLEncoder.encode(email, "UTF-8") +
       * "&external_id=" + URLEncoder.encode(externalId, "UTF-8");
       */

      String urlEncode = nonce
      // + "&email=" + URLEncoder.encode(email, "UTF-8")
          + "&email=" + email + "&external_id=" + URLEncoder.encode(externalId, "UTF-8");

      String urlBase64 = new String(Base64.encodeBase64(urlEncode.getBytes("UTF-8")));

      int length = 0;
      int maxLength = urlBase64.length();
      final int STEP = 60;

      String urlBase64Encode = "";

      while (length < maxLength) {
        urlBase64Encode +=
            urlBase64.substring(length, length + STEP < maxLength ? length + STEP : maxLength)
                + "\n";
        length += STEP;
      }

      String redirectUrl =
          "http://community.ptone.jp/session/sso_login?sso="
              + URLEncoder.encode(urlBase64Encode, "UTF-8") + "&sig="
              + DiscourseUtil.checksum(urlBase64Encode);

      System.out.println(redirectUrl);

      redirectView = new RedirectView(redirectUrl);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }

    return redirectView;
  }
}
