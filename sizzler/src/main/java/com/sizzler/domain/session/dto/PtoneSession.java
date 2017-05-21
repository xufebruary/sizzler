package com.sizzler.domain.session.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sizzler.domain.pmission.PtoneSysPermission;
import com.sizzler.domain.pmission.PtoneSysRole;

public class PtoneSession implements Serializable {

  private static final long serialVersionUID = 9021083054112211482L;

  private String sessionId;
  private long createTime = System.currentTimeMillis();
  private long updateSessionTime;
  private long sessionValidateTime;
  private Map<String, Object> attributeMap = new HashMap<>();
  private Map<String, String> clazzMap = new HashMap<>();
  public List<PtoneSysPermission> sysPermissions;
  public List<PtoneSysRole> sysRoles;

  public PtoneSession() {

  }

  public long getSessionValidateTime() {
    return sessionValidateTime;
  }

  public void setSessionValidateTime(long sessionValidateTime) {
    this.sessionValidateTime = sessionValidateTime;
  }

  public PtoneSession(String sessionId) {
    this.sessionId = sessionId;
  }

  public List<PtoneSysPermission> getSysPermissions() {
    return sysPermissions;
  }

  public void setSysPermissions(List<PtoneSysPermission> sysPermissions) {
    this.sysPermissions = sysPermissions;
  }

  public List<PtoneSysRole> getSysRoles() {
    return sysRoles;
  }

  public void setSysRoles(List<PtoneSysRole> sysRoles) {
    this.sysRoles = sysRoles;
  }

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public <T> T getAttribute(String key) throws ClassNotFoundException {
    if (clazzMap.containsKey(key) && attributeMap.containsKey(key)) {
      return (T) JSON.parseObject(JSON.toJSONString(attributeMap.get(key)),
          bulidClass(clazzMap.get(key)));
    } else {
      return null;
    }
  }

  public Class bulidClass(String className) throws ClassNotFoundException {
    return Class.forName(className);
  }

  public void setAttribute(String key, Object object) {
    attributeMap.put(key, object);
    clazzMap.put(key, object.getClass().getName());
    saveSessionToRedis();
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public Map<String, Object> getAttributeMap() {
    return attributeMap;
  }

  public void setAttributeMap(Map<String, Object> attributeMap) {
    this.attributeMap = attributeMap;
  }

  public Map<String, String> getClazzMap() {
    return clazzMap;
  }

  public void setClazzMap(Map<String, String> clazzMap) {
    this.clazzMap = clazzMap;
  }

  public long getUpdateSessionTime() {
    return updateSessionTime;
  }

  public void setUpdateSessionTime(long updateSessionTime) {
    this.updateSessionTime = updateSessionTime;
  }

  public void saveSessionToRedis() {
//    RedisService redisService = SpringContextUtil.getBean("redisService");
//    Long redisTime = redisService.getKeyTTLTime(sessionId);
//    redisService.setKey(sessionId, Integer.parseInt(String.valueOf(redisTime)),
//        JSON.toJSONString(this));
  }
}
