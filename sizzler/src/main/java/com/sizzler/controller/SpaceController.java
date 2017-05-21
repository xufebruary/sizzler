package com.sizzler.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sizzler.common.MediaType;
import com.sizzler.common.sizzler.MetaContentNode;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.ds.dto.DsContentView;
import com.sizzler.domain.ds.dto.UserAccountSource;
import com.sizzler.domain.panel.vo.PanelExtVo;
import com.sizzler.domain.pmission.PtoneSysPermission;
import com.sizzler.domain.pmission.PtoneSysRole;
import com.sizzler.domain.session.dto.PtoneSession;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.system.Constants;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.annotation.MethodRemark;
import com.sizzler.system.api.annotation.ApiVersion;
import com.sizzler.system.api.common.ResponseResult;
import com.sizzler.system.api.common.RestResultGenerator;

/**
 * panel资源controller接口
 */
@RestController("spaceApiController")
@Scope("prototype")
@RequestMapping("/{version}/spaces")
@ApiVersion(Constants.API_VERSION_1)
public class SpaceController extends BaseController {


  /**
   * 获取空间下panel列表 原panels/panel/{spaceId}
   */
  @RequestMapping(value = "{spaceId}/panels", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Space.GET_SPACE_PANEL_LIST,
      domain = OpreateConstants.BusinessDomain.SPACE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseResult<PanelExtVo> getSpacePanelList(@PathVariable("spaceId") String spaceId,
      @RequestHeader(value = "token", required = false) String token) {

    boolean isPanelTemplet = false;
    PtoneSession session = serviceFactory.getSessionContext().getSession(token);
//    if (session != null) {
//      List<PtoneSysRole> sysRoles = session.getSysRoles();
//      isPanelTemplet = PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER);
//    }

    PanelExtVo panelExtVo =
        serviceFactory.getPanelService().getPanelWithComponentsListBySpaceId(spaceId,
            isPanelTemplet);
    return RestResultGenerator.genResult(panelExtVo);
  }

  /**
   * 为第一次创建空间的用户根据source预制panel
   */
  @RequestMapping(value = "{spaceId}/panels/default", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Space.API_INIT_DEFAULT_PANEL_FOR_USER_FIRST_SPACE,
      domain = OpreateConstants.BusinessDomain.SPACE)
  @ResponseStatus(HttpStatus.CREATED)
  public void initDefaultPanelForUserFirstSpace(@PathVariable("spaceId") String spaceId,
      @RequestParam(value = "localLang", required = false) String localLang, @RequestHeader(
          value = "token", required = false) String token) {

    PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getSessionUser(token);
    serviceFactory.getPanelService().initDefaultPanelForUserFirstSpace(spaceId, loginPtoneUser,
        localLang);
  }

  /**
   * @Description: 获取当前用户下当前dsId下的所有有效数据源连接.
   */
  @RequestMapping(value = "{spaceId}/datasources/{dsId}/connections", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  public ResponseResult getDataSourceView(@PathVariable("dsId") Long dsId, @PathVariable("spaceId") String spaceId,
      HttpServletRequest request, @RequestHeader(value = "token", required = false) String token) {
    List<UserConnection> connectionsResults =
        serviceFactory.getPtoneUserConnectionService().findSpaceUserConnectionList(spaceId, dsId);
    return RestResultGenerator.genResult(connectionsResults);
  }

  /**
   * @Description: 获取当前空间下当前dsId下的所有保存到datadeck的文件列表.
   */
  @RequestMapping(value = "/{spaceId}/datasources/{dsId}/connections/{connectionId}/sources",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  public ResponseResult getAuthAccount(@PathVariable("spaceId") String spaceId, @PathVariable("dsId") String dsId,
      @PathVariable("connectionId") String connectionId, HttpServletRequest request, @RequestHeader(value = "token",
          required = false) String token) {
    List<UserAccountSource> userConnectionResults =
        serviceFactory.getDataSourceManagerService().getSpaceAuthAccount(spaceId, connectionId, dsId);
    return RestResultGenerator.genResult(userConnectionResults);
  }

  /**
   * @Description: 获取widget的档案列表(当前space下).
   */
  @RequestMapping(value = "/{spaceId}/datasources/{dsId}/connections/{connectionId}/tables",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  public ResponseResult getSpaceWidgetAuthAccount(@PathVariable("spaceId") String spaceId,
      @PathVariable("dsId") String dsId, @PathVariable("connectionId") String connectionId, HttpServletRequest request,
      @RequestHeader(value = "token", required = false) String token) {
    List<MetaContentNode> metaContentNodeList =
        serviceFactory.getDataSourceManagerService().getSpaceWidgetAuthAccount(spaceId, connectionId, dsId);
    return RestResultGenerator.genResult(metaContentNodeList);
  }

  /**
   * @Description: 获取当前空间下所有授过权的账户个数和文件个数.
   */
  @RequestMapping(value = "/{spaceId}/datasources/connections/counts", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  public ResponseResult getSpaceDsContentView(@RequestHeader(value = "token", required = false) String token,
      @PathVariable("spaceId") String spaceId, HttpServletRequest request) {
    PtoneUser loginUser = serviceFactory.getSessionContext().getSessionUser(token);
    List<DsContentView> dsContentViews =
        serviceFactory.getDataSourceManagerService().getSpaceDsContentView(spaceId, loginUser.getSource());
//    List<PtoneSysPermission> sysPermissions = PermissionUtil.getUserPermissionBySpaceId(serviceFactory, spaceId, token);
//    dsContentViews = PermissionUtil.validateDataSourcePermission(dsContentViews, sysPermissions, "dsCode", "reverse");
//    PermissionUtil.processDsInfoPlus(dsContentViews);
    return RestResultGenerator.genResult(dsContentViews);
  }

}
