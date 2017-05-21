package com.sizzler.controller.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sizzler.common.MediaType;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.domain.pmission.PtoneSysOperation;
import com.sizzler.domain.pmission.PtoneSysPermission;
import com.sizzler.domain.pmission.PtoneSysResource;
import com.sizzler.domain.pmission.PtoneSysRole;
import com.sizzler.domain.pmission.dto.PtoneSysPermissionSetting;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

/**
 * @ClassName: Permission
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2016/3/29
 * @author: zhangli
 */

@Controller
@Scope("prototype")
@RequestMapping("/permission")
public class PermissionController {

  @Autowired
  private ServiceFactory serviceFactory;

  @RequestMapping(value = "operation", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getSysOperation(@RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneSysOperation> sysOperation =
          serviceFactory.getPtonePermissionManagerService().findPtoneSysOperation();
      jsonView.successPack(sysOperation);
    } catch (Exception e) {
      jsonView.errorPack("getSysOperation error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "operation/update", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView updateSysOperation(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody PtoneSysOperation operation) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      serviceFactory.getPtonePermissionManagerService().updatePtoneSysOperation(operation);
      jsonView.messagePack("updateSysOperation success");
    } catch (Exception e) {
      jsonView.errorPack("updateSysOperation error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "operation/add", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView addSysOperation(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody PtoneSysOperation operation) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      operation.setOperationId(UUID.randomUUID().toString());
      operation.setStatus(Constants.validate);
      serviceFactory.getPtonePermissionManagerService().addPtoneSysOperation(operation);
      jsonView.messagePack("addSysOperation success");
    } catch (Exception e) {
      jsonView.errorPack("addSysOperation error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "resource", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView getSysResource(@RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneSysResource> sysResources =
          serviceFactory.getPtonePermissionManagerService().findPtoneSysResource();
      jsonView.successPack(sysResources);
    } catch (Exception e) {
      jsonView.errorPack("getSysResource error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "resource/update", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView updateSysResource(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody PtoneSysResource resource) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      serviceFactory.getPtonePermissionManagerService().updatePtoneSysResource(resource);
      jsonView.messagePack("updateSysResource success");
    } catch (Exception e) {
      jsonView.errorPack("updateSysResource error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "resource/add", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView addSysResource(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody PtoneSysResource resource) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      resource.setResourceId(UUID.randomUUID().toString());
      resource.setStatus(Constants.validate);
      serviceFactory.getPtonePermissionManagerService().addPtoneSysResource(resource);
      jsonView.messagePack("addSysResource success");
    } catch (Exception e) {
      jsonView.errorPack("addSysResource error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView getSysPermission(@RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneSysPermission> sysPermissions =
          serviceFactory.getPtonePermissionManagerService().findPtoneSysPermission();
      jsonView.successPack(sysPermissions);
    } catch (Exception e) {
      jsonView.errorPack("getSysPermission error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "update", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView updateSysPermission(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody PtoneSysPermission permission) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      serviceFactory.getPtonePermissionManagerService().updatePtoneSysPermission(permission);
      jsonView.messagePack("updateSysPermission success");
    } catch (Exception e) {
      jsonView.errorPack("updateSysPermission error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView addSysPermission(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody PtoneSysPermission permission) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      permission.setPermissionId(UUID.randomUUID().toString());
      permission.setStatus(Constants.validate);
      serviceFactory.getPtonePermissionManagerService().addPtoneSysPermission(permission);
      jsonView.messagePack("addSysPermission success");
    } catch (Exception e) {
      jsonView.errorPack("addSysPermission error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "role", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView getSysRole(@RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneSysRole> sysRoles =
          serviceFactory.getPtonePermissionManagerService().findPtoneSysRole();
      jsonView.successPack(sysRoles);
    } catch (Exception e) {
      jsonView.errorPack("getSysRole error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "role/update", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView updateSysRole(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody PtoneSysRole role) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      serviceFactory.getPtonePermissionManagerService().updatePtoneSysRole(role);
      jsonView.messagePack("updateSysRole success");
    } catch (Exception e) {
      jsonView.errorPack("updateSysRole error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "role/add", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView addSysRole(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody PtoneSysRole role) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      role.setRoleId(UUID.randomUUID().toString());
      role.setStatus(Constants.validate);
      serviceFactory.getPtonePermissionManagerService().addPtoneSysRole(role);
      jsonView.messagePack("addSysRole success");
    } catch (Exception e) {
      jsonView.errorPack("addSysRole error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "sys/{roleId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView getPermissionByRoleId(@RequestParam(value = "sid", required = false) String sid,
      @PathVariable("roleId") String roleId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneSysPermission> sysPermissions =
          serviceFactory.getPtonePermissionManagerService().findPermissionsByRoleId(roleId);
      jsonView.successPack(sysPermissions);
    } catch (Exception e) {
      jsonView.errorPack("getPermissionByRoleId error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "sys/setting", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  
  public JsonView setRoleSysPermission(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody PtoneSysPermissionSetting sysPermissionSetting) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      serviceFactory.getPtonePermissionManagerService().setRoleSysPermission(
          sysPermissionSetting.getRoleId(), sysPermissionSetting.getPermissionIds());
      jsonView.messagePack("setRoleSysPermission success.");
    } catch (Exception e) {
      jsonView.errorPack("setRoleSysPermission error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "sys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getUserPermission(@RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      List<PtoneSysPermission> sysPermissions =
          serviceFactory.getPtonePermissionManagerService().findUserPermissionByUid(
              loginPtoneUser.getPtId());
      jsonView.successPack(sysPermissions);
    } catch (Exception e) {
      jsonView.errorPack("getUserPermission error.", e);
    }
    return jsonView;
  }
}
