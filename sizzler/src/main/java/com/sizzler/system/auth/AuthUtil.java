package com.sizzler.system.auth;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.pmission.PtoneSysPermission;
import com.sizzler.domain.pmission.PtoneSysRole;
import com.sizzler.domain.session.dto.PtoneSession;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.system.ServiceFactory;

public class AuthUtil {

  private static String openPlus = "2";
  private static String roleType = "reverse";
  private static String dsPre = "datasource-";
  private static String dsSufix = "-view";

  public static boolean hasSysRole(List<PtoneSysRole> sysRoles, String role) {
    boolean flag = false;
    if (null != sysRoles && !sysRoles.isEmpty()) {
      for (PtoneSysRole sysRole : sysRoles) {
        if (sysRole.getCode().equals(role)) {
          flag = true;
        }
      }
    }
    return flag;
  }

  public static boolean modifySysRole(boolean has, String type) {
    if (StringUtil.isNotBlank(type) && type.equals(roleType)) {
      return !has;
    } else {
      return has;
    }
  }

  public static boolean hasSysPermission(List<PtoneSysPermission> sysPermissions, String permission) {
    boolean flag = false;
    if (CollectionUtil.isNotEmpty(sysPermissions)) {
      for (PtoneSysPermission per : sysPermissions) {
        if (per.getCode().equals(permission)) {
          flag = true;
        }
      }
    }
    return flag;
  }

  public static boolean modifySysPermission(boolean has, String type) {
    if (StringUtil.isNotBlank(type) && type.equals(roleType)) {
      return !has;
    } else {
      return has;
    }
  }

  public static <T> List<T> validateDataSourcePermission(List<T> dataList, List<PtoneSysPermission> sysPermissions, String filed,
      String type) {
    try {
      Iterator iterator = dataList.iterator();
      while (iterator.hasNext()) {
				T t = (T) iterator.next();
				String value = getValueByClazz(filed, t);
				String plus = getValueByClazz("isPlus", t);

				//对外开放的不走权限
				if (StringUtil.isNotBlank(plus) && !plus.equals(openPlus)) {
					if(modifySysPermission(AuthUtil.hasSysPermission(sysPermissions,dsPre + value + dsSufix), type)){
						iterator.remove();
					}
				}
			}
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dataList;
  }

  public static String getValueByClazz(String field, Object clazz) {
    String value = null;
    PropertyDescriptor pd = null;
    try {
      pd = new PropertyDescriptor(field, clazz.getClass());
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }
    Method getMethod = pd.getReadMethod();
    try {
      value = (String) getMethod.invoke(clazz);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return value;
  }

  public static void setValueByClazz(String field,String value, Object clazz) {
    PropertyDescriptor pd = null;
    try {
      pd = new PropertyDescriptor(field, clazz.getClass());
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }
    Method setMethod = pd.getWriteMethod();
    try {
      setMethod.invoke(clazz,value);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * 处理isPlus字段
   * @param dataList
   * @author li.zhang
   * @return
   */
  public static <T> void processDsInfoPlus(List<T> dataList) {
    Iterator iterator = dataList.iterator();
    while (iterator.hasNext()) {
      T t = (T) iterator.next();
      setValueByClazz("isPlus",openPlus,t);
    }
  }

  /**
   * 根据权限增加数据源
   * @author li.zhang
   * @return
   */
  public static List<PtoneDsInfo> addDsInfoByRole(List<PtoneSysPermission> sysPermissions,ServiceFactory serviceFactory,String source) {
    //数据源角色code字段必须符合ptone-dscode-datasource-user约定
    List<PtoneDsInfo> dsListByRole = new ArrayList<>();
    if(CollectionUtil.isNotEmpty(sysPermissions)){
      Map<String,Object[]> paramMap = new HashMap<>();
      Object param[] = new Object[sysPermissions.size()];
      for(int i = 0;i!=sysPermissions.size();i++){
        if(sysPermissions.get(i).getCode().startsWith(dsPre) && sysPermissions.get(i).getCode().endsWith(dsSufix) ){
          param[i] = sysPermissions.get(i).getCode().split("-")[1];
        }
      }
      paramMap.put("code",param);
      dsListByRole = serviceFactory.getPtoneDsService().getAllDsInfoList();
      if(CollectionUtil.isNotEmpty(dsListByRole)){
        AuthUtil.processDsInfoPlus(dsListByRole);
      }
    }
    return dsListByRole;
  }

  /**
   * 根据空间获取用户权限
   * @author li.zhang
   * @return
   */
  public static List<PtoneSysPermission> getUserPermissionBySpaceId(ServiceFactory serviceFactory, String spaceId, String sid) {
    List<PtoneSysPermission> sysPermissions = new ArrayList<>();
//    try {
//      PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
//      PtoneUser loginUser = serviceFactory.getPtoneSessionContext().getLoginUser(sid);
//      PtoneSession session = serviceFactory.getPtoneSessionContext().getSession(sid);
//      if(spaceInfo.getOwnerId().equals(loginUser.getPtId())){
//				sysPermissions = session.getSysPermissions();
//			}else {
//				sysPermissions = serviceFactory.getPtonePermissionManagerService().findUserPermissionByUid(spaceInfo.getOwnerId());
//			}
//    } catch (ClassNotFoundException e) {
//      e.printStackTrace();
//    }
    return sysPermissions;
  }

}
