package com.sizzler.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sizzler.domain.pmission.PtoneSysOperation;
import com.sizzler.domain.pmission.PtoneSysPermission;
import com.sizzler.domain.pmission.PtoneSysResource;
import com.sizzler.domain.pmission.PtoneSysRole;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.service.PtonePermissionManagerService;

@Service("ptonePermissionManagerService")
public class PtonePermissionManagerServiceImpl implements PtonePermissionManagerService {

  @Override
  public List<PtoneSysPermission> findUserPermissionByUid(String uid) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<PtoneSysRole> findUserSysRoleByUid(String uid) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<PtoneSysPermission> findPermissionsByRoleId(String roleId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getUserRoleQuantity(String uid, String resourceId) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void saveDefaultUserRole(PtoneUser user) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setUserSysRole(String uid, List<String> roleIds) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setRoleSysPermission(String roleId, List<String> permissionId) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addPtoneSysOperation(PtoneSysOperation ptoneSysOperation) {
    // TODO Auto-generated method stub

  }

  @Override
  public void updatePtoneSysOperation(PtoneSysOperation ptoneSysOperation) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<PtoneSysOperation> findPtoneSysOperation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PtoneSysOperation getPtoneSysOperation(String operationId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addPtoneSysResource(PtoneSysResource ptoneSysResource) {
    // TODO Auto-generated method stub

  }

  @Override
  public void updatePtoneSysResource(PtoneSysResource ptoneSysResource) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<PtoneSysResource> findPtoneSysResource() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PtoneSysResource getPtoneSysResource(String resourceId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addPtoneSysPermission(PtoneSysPermission ptoneSysPermission) {
    // TODO Auto-generated method stub

  }

  @Override
  public void updatePtoneSysPermission(PtoneSysPermission ptoneSysPermission) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<PtoneSysPermission> findPtoneSysPermission() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PtoneSysPermission getPtoneSysPermission(String permissionId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addPtoneSysRole(PtoneSysRole ptoneSysRole) {
    // TODO Auto-generated method stub

  }

  @Override
  public void updatePtoneSysRole(PtoneSysRole ptoneSysRole) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<PtoneSysRole> findPtoneSysRole() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PtoneSysRole getPtoneSysRole(String roleId) {
    // TODO Auto-generated method stub
    return null;
  }

}
