package com.sizzler.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ptmind.common.utils.StringUtil;
import com.sizzler.common.MediaType;
import com.sizzler.domain.panel.PanelGlobalComponent;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.panel.PtonePanelLayout;
import com.sizzler.domain.panel.dto.PanelInfoExt;
import com.sizzler.domain.panel.vo.PanelExtVo;
import com.sizzler.domain.panel.vo.PanelVo;
import com.sizzler.domain.panel.vo.PanelWidthComponentVo;
import com.sizzler.domain.panel.vo.SharePanelVerifyVo;
import com.sizzler.domain.pmission.PtoneSysRole;
import com.sizzler.domain.session.dto.PtoneSession;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.widget.vo.WidgetListVo;
import com.sizzler.system.Constants;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.annotation.MethodRemark;
import com.sizzler.system.api.annotation.ApiVersion;
import com.sizzler.system.api.common.ResponseResult;
import com.sizzler.system.api.common.RestResultGenerator;

/**
 * panel资源controller接口
 * 
 * @date: 2017年1月3日
 * @author peng.xu
 */
@RestController("panelApiController")
@RequestMapping("/{version}/panels")
@Scope("prototype")
@ApiVersion(Constants.API_VERSION_1)
public class PanelController extends BaseController {

  /**
   * 新增panel、panelTemplet、panelFolder
   * @date: 2016年12月5日
   * @author peng.xu
   */
  @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.API_ADD_PANEL,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseResult<PanelExtVo> addPanel(@RequestBody PanelInfoExt panel,
      HttpServletRequest request, @RequestHeader(value = "token", required = false) String token) {

    boolean isPanelTemplet = false;
    String uid = null;
    PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getSessionUser(token);
    PtoneSession session = serviceFactory.getSessionContext().getSession(token);
//    if (session != null) {
//      List<PtoneSysRole> sysRoles = session.getSysRoles();
//      isPanelTemplet = PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER);
//    }
    if (loginPtoneUser != null) {
      uid = loginPtoneUser.getPtId();
    }
    PanelExtVo panelExtVo = serviceFactory.getPanelService().addPanel(panel, uid, isPanelTemplet);
    return RestResultGenerator.genResult(panelExtVo);
  }

  /**
   * 复制panel
   * @date: 2016年12月5日
   * @author peng.xu
   */
  @RequestMapping(value = "{srcPanelId}/copy", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.API_COPY_PANEL,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseResult<PanelExtVo> copyPanel(@RequestBody PanelInfoExt panelExt,
      HttpServletRequest request, @PathVariable("srcPanelId") String srcPanelId, @RequestHeader(
          value = "token", required = false) String token) {

    boolean isPanelTemplet = false;
    String uid = null;
    PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getSessionUser(token);
    PtoneSession session = serviceFactory.getSessionContext().getSession(token);
//    if (session != null) {
//      List<PtoneSysRole> sysRoles = session.getSysRoles();
//      isPanelTemplet = PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER);
//    }
    if (loginPtoneUser != null) {
      uid = loginPtoneUser.getPtId();
    }
    PanelExtVo panelExtVo =
        serviceFactory.getPanelService().copyPanel(panelExt, srcPanelId, uid, isPanelTemplet);
    return RestResultGenerator.genResult(panelExtVo);
  }

  /**
   * 根据模板创建panel
   * @date: 2016年12月5日
   * @author peng.xu
   */
  @RequestMapping(value = "templet", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.API_ADD_PANEL_BY_TEMPLET,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseResult<PanelExtVo> createByTemplet(@RequestBody PanelInfoExt panelExt,
      HttpServletRequest request, @RequestHeader(value = "token", required = false) String token) {

    String uid = null;
    PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getSessionUser(token);
    if (loginPtoneUser != null) {
      uid = loginPtoneUser.getPtId();
    }
    PanelExtVo panelExtVo =
        serviceFactory.getPanelService().addPanelByTemplet(panelExt.getTempletId(),
            panelExt.getSpaceId(), uid);
    return RestResultGenerator.genResult(panelExtVo);
  }


  /**
   * 将分享panel添加到用户空间下 原 share/add/{pid}/{spaceId}
   * @date: 2017年1月13日
   * @author peng.xu
   */
  @RequestMapping(value = "share", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.API_ADD_SHARE_PANEL,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseResult<String> addSharePanel(
      @RequestHeader(value = "token", required = false) String token,
      @RequestBody PanelWidthComponentVo panelWidthComponentVo) {

    PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getSessionUser(token);
    String result =
        serviceFactory.getPanelService().addSharePanel(panelWidthComponentVo.getPanelId(),
            loginPtoneUser.getPtId(), panelWidthComponentVo.getSpaceId(),
            panelWidthComponentVo.getComponent());
    return RestResultGenerator.genResult(result);
  }


  /**
   * 修改panel、panelTemplet、panelFolder
   * @date: 2016年12月5日
   * @author peng.xu
   */
  @RequestMapping(value = "{panelId}", method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.API_UPDATE_PANEL,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseResult<PanelExtVo> updatePanel(@RequestBody PanelInfoExt panelExt,
      HttpServletRequest request, @PathVariable("panelId") String panelId, @RequestHeader(
          value = "token", required = false) String token) {

    boolean isPanelTemplet = false;
    String uid = null;
    PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getSessionUser(token);
    PtoneSession session = serviceFactory.getSessionContext().getSession(token);
//    if (session != null) {
//      List<PtoneSysRole> sysRoles = session.getSysRoles();
//      isPanelTemplet = PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER);
//    }
    if (loginPtoneUser != null) {
      uid = loginPtoneUser.getPtId();
    }
    PanelExtVo panelExtVo =
        serviceFactory.getPanelService().updatePanel(panelExt, uid, isPanelTemplet);
    return RestResultGenerator.genResult(panelExtVo);
  }

  /**
   * 删除panel、panelTemplet、panelFolder
   * @return
   * @date: 2016年11月15日
   * @author peng.xu
   */
  @RequestMapping(value = "{panelId}", method = RequestMethod.DELETE,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.DEL_PANEL,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.CREATED)
  // 此处需要返回值
  public ResponseResult<PanelExtVo> deletePanel(@RequestBody PanelInfoExt panelExt,
      @PathVariable("panelId") String panelId, HttpServletRequest request, @RequestHeader(
          value = "token", required = true) String token) {

    String uid = null;
    PtoneSession session = serviceFactory.getSessionContext().getSession(token);
    List<PtoneSysRole> sysRoles = session.getSysRoles();
    PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getSessionUser(token);
//    boolean isAdmin = PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER);
//    if (loginPtoneUser != null) {
//      uid = loginPtoneUser.getPtId();
//    }
    boolean isAdmin = false;
    PanelExtVo panelExtVo =
        serviceFactory.getPanelService().deletePanel(panelId, panelExt, uid, isAdmin);
    return RestResultGenerator.genResult(panelExtVo);
  }

  /**
   * 修改panelLayout信息
   * @date: 2017年1月11日
   * @author peng.xu
   */
  @RequestMapping(value = "layout", method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.API_UPDATE_PANEL_LAYOUT,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseResult<PanelExtVo> updatePanelLayout(@RequestBody PtonePanelLayout panelLayout,
      HttpServletRequest request, @RequestHeader(value = "token", required = false) String token) {

    PtoneSession session = serviceFactory.getSessionContext().getSession(token);
    List<PtoneSysRole> sysRoles = session.getSysRoles();
    boolean isPanelTemplet = false ; //PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER);
    PanelExtVo panelExtVo =
        serviceFactory.getPanelLayoutService().updateDataVersion(panelLayout,
            panelLayout.getSpaceId(), isPanelTemplet);
    return RestResultGenerator.genResult(panelExtVo);
  }

  /**
   * 获取panel基本信息 原 single/{panelId}
   * @date: 2017年1月13日
   * @author peng.xu
   */
  @RequestMapping(value = "base/{panelId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.API_GET_BASE_PANEL_INFO,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.OK)
  public ResponseResult<PtonePanelInfo> getSinglePanel(@RequestHeader(value = "token",
      required = false) String token, @PathVariable("panelId") String panelId) {

    PtonePanelInfo panelInfo = serviceFactory.getPanelService().get(panelId);
    return RestResultGenerator.genResult(panelInfo);
  }

  /**
   * 获取panel信息 原 getPanel/{panelId}
   * @date: 2017年1月13日
   * @author peng.xu
   */
  @RequestMapping(value = "{panelId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.API_GET_PANEL_INFO,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.OK)
  public ResponseResult<Object> getPanelById(@PathVariable("panelId") String panelId,
      @RequestHeader(value = "token", required = false) String token, @RequestParam(
          value = "accessToken", required = false) String accessToken) {

    boolean isPanelTemplet = false;
    PtoneSession session = serviceFactory.getSessionContext().getSession(token);

    // 分享页面会传递accessToken， 如果是分享页面则不需要校验是否admin账号（目前admin不支持分享）
//    if (session != null && StringUtil.isBlank(accessToken)) {
//      List<PtoneSysRole> sysRoles = session.getSysRoles();
//      isPanelTemplet = PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER);
//    }

    Object panelInfo =
        serviceFactory.getPanelService().getPanelWithComponentsById(panelId, isPanelTemplet);
    return RestResultGenerator.genResult(panelInfo);
  }


  /**
   * 查询widget列表(附带widgetLayout信息) 原： /widgets/widgetWithLayout/mobile/{pid}
   * 和/widgets/widgetWithLayout/{pid}
   * @author li.zhang
   * @date 2017/1/3
   * @param panelId
   * @param token
   * @return
   */
  @RequestMapping(value = "{panelId}/widgets", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Widget.API_GET_LIST_WITH_LAYOUT,
      domain = OpreateConstants.BusinessDomain.WIDGET)
  @ResponseStatus(HttpStatus.OK)
  public ResponseResult<WidgetListVo> findWidget(@PathVariable("panelId") String panelId,
      @RequestParam(value = "device", required = false) String device, @RequestParam(
          value = "password", required = false) String password, @RequestHeader(value = "token",
          required = false) String token) {

    boolean isMobile = "mobile".equalsIgnoreCase(device);
    PtoneSession session = serviceFactory.getSessionContext().getSession(token);
    boolean isAdmin = false;
//    if (session != null) {
//      List<PtoneSysRole> sysRoles = session.getSysRoles();
//      isAdmin = PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER);
//    }

    String uid = null;
    PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getSessionUser(token);
    if (loginPtoneUser != null) {
      uid = loginPtoneUser.getPtId();
    }
    WidgetListVo widgetListVo =
        serviceFactory.getPanelService().findPanelWidgetListWithLayout(panelId, uid, password,
            isAdmin, isMobile);

    return RestResultGenerator.genResult(widgetListVo);
  }

  /**
   * 验证panel分享的密码是否正确
   * @date: 2016年12月5日
   * @author li.zhang
   */
  @RequestMapping(value = "share/verification", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.API_SHARE_PANEL_VERIFY_PASSWORD,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseResult<Boolean> sharePanelVerifyPassword(
      @RequestBody SharePanelVerifyVo sharePanelVerifyVo, @RequestHeader(value = "token",
          required = false) String token) {
    boolean validate =
        serviceFactory.getPanelService().sharePanelVerifyPassword(sharePanelVerifyVo);
    return RestResultGenerator.genResult(validate);
  }

  /**
   * 校验分享panel在指定空间下是否存在是否存在 原 share/exists/{pid}/{spaceId}
   * @date: 2017年1月13日
   * @author peng.xu
   */
  @RequestMapping(value = "share/exists", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.API_VALIDATE_SHARE_PANEL_EXISTS,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseResult<Boolean> existsSharePanel(@RequestBody PanelVo panelVo,
      HttpServletRequest request, @RequestHeader(value = "token", required = false) String token) {

    boolean isExists =
        serviceFactory.getPanelService().isExistsPanelInSpace(panelVo.getPanelId(),
            panelVo.getSpaceId());
    return RestResultGenerator.genResult(isExists);
  }

  /**
   * 应用panel全局控件
   * @date: 2017年1月13日
   * @author peng.xu
   */
  @RequestMapping(value = "components/apply", method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Panel.API_APPLY_PANEL_COMPONENT,
      domain = OpreateConstants.BusinessDomain.PANEL)
  @ResponseStatus(HttpStatus.CREATED)
  public void applyPancelComponent(@RequestHeader(value = "token", required = false) String token,
      @RequestBody PanelGlobalComponent component) {

    PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getSessionUser(token);
    component.setUid(loginPtoneUser.getPtId()); // 设置uid
    serviceFactory.getPanelService().applyPanelComponent(component);
  }

}
