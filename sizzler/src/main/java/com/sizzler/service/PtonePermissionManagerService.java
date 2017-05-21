package com.sizzler.service;

import java.util.List;

import com.sizzler.domain.pmission.PtoneSysOperation;
import com.sizzler.domain.pmission.PtoneSysPermission;
import com.sizzler.domain.pmission.PtoneSysResource;
import com.sizzler.domain.pmission.PtoneSysRole;
import com.sizzler.domain.user.PtoneUser;

/**
 * @ClassName: PtonePermissionManagerService
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2016/3/28
 * @author: zhangli
 */
public interface PtonePermissionManagerService {

  /**
   * @Description: 得到用户所有权限列表.
   * @param uid
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract List<PtoneSysPermission> findUserPermissionByUid(String uid);

  /**
   * @Description: 得到用户所有角色列表.
   * @param uid
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract List<PtoneSysRole> findUserSysRoleByUid(String uid);

  /**
   * @Description: 得到某个角色下的权限列表.
   * @param roleId
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract List<PtoneSysPermission> findPermissionsByRoleId(String roleId);

  /**
   * @Description: 得到用户某个资源的拥有的最大数.
   * @param uid
   * @param resourceId
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract int getUserRoleQuantity(String uid, String resourceId);

  /**
   * @Description: 为新用户指定默认角色.
   * @param user
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract void saveDefaultUserRole(PtoneUser user);

  /**
   * @Description: 给用户设置角色.
   * @param uid
   * @param roleIds
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract void setUserSysRole(String uid, List<String> roleIds);

  /**
   * @Description: 给角色赋值权限
   * @param roleId
   * @param roleId
   * @param permissionId
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract void setRoleSysPermission(String roleId, List<String> permissionId);

  /**
   * @Description: operation add.
   * @param ptoneSysOperation
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract void addPtoneSysOperation(PtoneSysOperation ptoneSysOperation);

  /**
   * @Description: operation update.
   * @param ptoneSysOperation
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract void updatePtoneSysOperation(PtoneSysOperation ptoneSysOperation);

  /**
   * @Description: find all operation.
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract List<PtoneSysOperation> findPtoneSysOperation();

  /**
   * @Description: get operation by operationId.
   * @param operationId
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract PtoneSysOperation getPtoneSysOperation(String operationId);



  /**
   * @Description: PtoneSysResource add.
   * @param ptoneSysResource
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract void addPtoneSysResource(PtoneSysResource ptoneSysResource);

  /**
   * @Description: PtoneSysResource update.
   * @param ptoneSysResource
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract void updatePtoneSysResource(PtoneSysResource ptoneSysResource);

  /**
   * @Description: find all PtoneSysResource.
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract List<PtoneSysResource> findPtoneSysResource();

  /**
   * @Description: get PtoneSysResource by resourceId.
   * @param resourceId
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract PtoneSysResource getPtoneSysResource(String resourceId);



  /**
   * @Description: permission add.
   * @param ptoneSysPermission
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract void addPtoneSysPermission(PtoneSysPermission ptoneSysPermission);

  /**
   * @Description: permission update.
   * @param ptoneSysPermission
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract void updatePtoneSysPermission(PtoneSysPermission ptoneSysPermission);

  /**
   * @Description: find all permission.
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract List<PtoneSysPermission> findPtoneSysPermission();

  /**
   * @Description: get permission by permissionId.
   * @param permissionId
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract PtoneSysPermission getPtoneSysPermission(String permissionId);



  /**
   * @Description: role add.
   * @param ptoneSysRole
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract void addPtoneSysRole(PtoneSysRole ptoneSysRole);

  /**
   * @Description: role update.
   * @param ptoneSysRole
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract void updatePtoneSysRole(PtoneSysRole ptoneSysRole);

  /**
   * @Description: find all role.
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract List<PtoneSysRole> findPtoneSysRole();

  /**
   * @Description: get role by roleId.
   * @param roleId
   * @date: 2016/5/12
   * @author: zhangli
   */
  public abstract PtoneSysRole getPtoneSysRole(String roleId);


}
