package com.sizzler.controller.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.sizzler.domain.basic.PtoneBasicDictItem;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.user.PtoneUserBasicSetting;
import com.sizzler.system.ServiceFactory;

@Controller
@Scope("prototype")
@RequestMapping("/dict")
public class DictController {

  @Autowired
  private ServiceFactory serviceFactory;

  @RequestMapping(value = "item/{dictCode}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getDictItemByCode(HttpServletRequest request,
      @PathVariable("dictCode") String dictCode,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // HttpSession session = SessionContext.getSession(sid,request);
      // PtoneUser user = (PtoneUser)session.getAttribute(Constants.Current_Ptone_User);
//      PtoneUser user = serviceFactory.getSessionContext().getLoginUser(sid);
      String ptId = "";
      PtoneUserBasicSetting userSetting =
          serviceFactory.getUserService().getUserSetting(ptId, null);
      String locale = PtoneBasicDictItem.DICT_NAME_DEFAULT_KEY;
      if (userSetting != null && userSetting.getLocale() != null
          && !"".equals(userSetting.getLocale())) {
        locale = userSetting.getLocale();
      }

      List<Map<String, Object>> dictList =
          serviceFactory.getPtoneDictService().getDictByCode(dictCode, locale);

      jsonView.successPack(dictList);
    } catch (Exception e) {
      jsonView.errorPack(" get dict<<" + dictCode + ">> error.", e);
    }
    return jsonView;
  }
}
