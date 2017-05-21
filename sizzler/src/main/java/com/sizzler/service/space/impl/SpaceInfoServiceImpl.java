package com.sizzler.service.space.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.dao.WidgetDao;
import com.sizzler.dao.space.SpaceInfoDao;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.panel.PtonePanelLayout;
import com.sizzler.domain.space.PtoneRetainDomain;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.space.PtoneSpaceUser;
import com.sizzler.domain.space.dto.SpaceInfoDto;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.service.PanelService;
import com.sizzler.service.UserService;
import com.sizzler.service.WidgetService;
import com.sizzler.service.space.SpaceService;
import com.sizzler.service.space.SpaceUserService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.util.CascadeDeleteUtil;

@Service("spaceService")
public class SpaceInfoServiceImpl extends ServiceBaseInterfaceImpl<PtoneSpaceInfo, String>
    implements SpaceService {

  @Autowired
  private SpaceInfoDao spaceInfoDao;

  @Autowired
  private SpaceUserService spaceUserService;

  @Autowired
  private UserService userService;

  @Autowired
  private ServiceFactory serviceFactory;

  @Autowired
  private WidgetDao widgetDao;

  @Autowired
  private PanelService panelService;

  @Autowired
  private WidgetService widgetService;

  private Logger logger = LoggerFactory.getLogger(SpaceInfoServiceImpl.class);

  private static boolean retainDomainRefresh = true; // 默认刷新，刷新一次后改为false

  private static List<String> retainDomainStrList = new ArrayList<String>();

  private static List<String> retainDomainRegexpList = new ArrayList<String>();

  public static boolean isRetainDomainRefresh() {
    return retainDomainRefresh;
  }

  public static void setRetainDomainRefresh(boolean retainDomainRefresh) {
    SpaceInfoServiceImpl.retainDomainRefresh = retainDomainRefresh;
  }

  public List<String> getRetainDomainStrList() {
    return retainDomainStrList;
  }

  public void setRetainDomainStrList(List<String> retainDomainStrList) {
    SpaceInfoServiceImpl.retainDomainStrList = retainDomainStrList;
  }

  public List<String> getRetainDomainRegexpList() {
    return retainDomainRegexpList;
  }

  public void setRetainDomainRegexpList(List<String> retainDomainRegexpList) {
    SpaceInfoServiceImpl.retainDomainRegexpList = retainDomainRegexpList;
  }

  @Override
  @Transactional
  public SpaceInfoDto addSpace(SpaceInfoDto space, PtoneUser creator) {
    long createTime = System.currentTimeMillis();
    String creatorId = creator.getPtId();
    space.setOwnerId(creatorId);
    space.setOwnerEmail(creator.getUserEmail());
    space.setCreateTime(createTime);
    space.setCreatorId(creatorId);
    space.setModifierId(creatorId);
    space.setModifyTime(createTime);
    space.setIsDelete(Constants.inValidateInt);
    PtoneSpaceInfo spaceInfo = new PtoneSpaceInfo();
    BeanUtils.copyProperties(space, spaceInfo);

    // // 检查子域名是否存在
    // if (spaceInfo != null && StringUtil.isNotBlank(spaceInfo.getDomain())) {
    // if (checkDomainExists(spaceInfo.getDomain(), spaceInfo.getSpaceId())) {
    // ServiceException se = new ServiceException("This subdomain has exists !");
    // throw se;
    // }
    // }

    this.save(spaceInfo);
    PtoneSpaceUser spaceUser = new PtoneSpaceUser();
    spaceUser.setSpaceId(space.getSpaceId());
    spaceUser.setStatus(PtoneSpaceUser.STATUS_ACCEPTED);
    spaceUser.setType(PtoneSpaceUser.TYPE_OWNDER);
    spaceUser.setUid(creatorId);
    spaceUser.setUserEmail(creator.getUserEmail());
    spaceUser.setCreatorId(creatorId);
    spaceUser.setCreateTime(createTime);
    spaceUser.setIsDelete(Constants.inValidateInt);
    spaceUserService.save(spaceUser);

    space.setUid(creatorId);
    space.setType(PtoneSpaceUser.TYPE_OWNDER);
    space.setOwnerName(creator.getUserName());
    space.setUserEmail(creator.getUserEmail());

    // 创建空间完成后创建一条空间的panelLayout信息
    PtonePanelLayout panelLayout = new PtonePanelLayout();
    panelLayout.setPanelType(null);
    panelLayout.setUid(creatorId);
    panelLayout.setSpaceId(space.getSpaceId());
    panelLayout.setUpdateTime(System.currentTimeMillis());
    panelLayout.setDataVersion(0L);
    panelLayout.setPanelLayout(null);
    serviceFactory.getPanelLayoutService().save(panelLayout);

    return space;
  }

  @Override
  public void updateSpace(SpaceInfoDto space) {
    PtoneSpaceInfo spaceInfo = new PtoneSpaceInfo();
    BeanUtils.copyProperties(space, spaceInfo);

    PtoneSpaceInfo tmpSpaceInfo = this.get(spaceInfo.getSpaceId());
    if (tmpSpaceInfo != null && StringUtil.isBlank(tmpSpaceInfo.getCreatorId())) {
      spaceInfo.setCreatorId(tmpSpaceInfo.getOwnerId());
      spaceInfo.setCreateTime(System.currentTimeMillis());
    }

    // // 检查子域名是否存在
    // if (spaceInfo != null && StringUtil.isNotBlank(spaceInfo.getDomain())) {
    // if (checkDomainExists(spaceInfo.getDomain(), spaceInfo.getSpaceId())) {
    // ServiceException se = new ServiceException("This subdomain has exists !");
    // throw se;
    // }
    // }

    this.update(spaceInfo);
  }

  @Override
  public SpaceInfoDto getSpaceInfo(String spaceId, String uid) {
    PtoneSpaceInfo spaceInfo = this.get(spaceId);

    if (spaceInfo == null) {
      return null;
    }

    SpaceInfoDto space = new SpaceInfoDto();
    BeanUtils.copyProperties(spaceInfo, space);

    String ownerId = spaceInfo.getOwnerId();
    PtoneUser owner = userService.getPtoneUser(ownerId);
    if (owner != null) {
      space.setOwnerName(owner.getUserName());
      space.setOwnerEmail(owner.getUserEmail());
    }

    if (StringUtil.isNotBlank(uid)) {
      PtoneSpaceUser spaceUser = spaceUserService.getSpaceUserByUid(spaceId, uid);
      if (spaceUser != null) {
        space.setType(spaceUser.getType());
        space.setUid(uid);
        space.setUserEmail(spaceUser.getUserEmail());
      }
    }
    return space;
  }

  @Override
  @Transactional
  public void deleteById(String spaceId, String uid, boolean isDelete) {
    /*
     * TODO 以后如果需要权限判断时使用 PtoneSpaceInfo space = this.get(spaceId); if (space != null &&
     * (StringUtil.isBlank(uid) || uid.equals(space.getOwnerId()))) {
     * space.setIsDelete(Constants.validateInt); this.update(space); }
     */
    PtoneSpaceInfo space = new PtoneSpaceInfo();
    space.setSpaceId(spaceId);
    space.setIsDelete(Constants.validateInt);
    // ptone_space_info
    this.update(space);
    cascadeDelectBySpaceId(spaceId, isDelete);
  }

  @Override
  @Transactional
  public void cascadeDelectBySpaceId(String spaceId, boolean isDelete) {
    if (StringUtil.isBlank(spaceId)) {
      return;
    }
    Map<String, Object[]> panelIdMap = new HashMap<String, Object[]>(1);
    Map<String, Object[]> widgetIdMap = new HashMap<String, Object[]>(1);
    buildPanelIdAndWidgetIdArray(spaceId, panelIdMap, widgetIdMap);

    Map<String, Object[]> spaceIdMap = new HashMap<>(1);
    spaceIdMap.put("spaceId", new Object[] {spaceId});

    Map<String, Map<String, String>> updateMap = CascadeDeleteUtil.buildParamMap(isDelete);

    Map<String, String> statusMap = updateMap.get(CascadeDeleteUtil.STATUS);

    Map<String, String> deleteMap = updateMap.get(CascadeDeleteUtil.IS_DELETE);

    // ptone_panel_info
    serviceFactory.getPanelService().update(spaceIdMap, statusMap);

    // ptone_space_user
    serviceFactory.getSpaceUserService().update(spaceIdMap, deleteMap);
    // ptone_panel_layout
    serviceFactory.getPanelLayoutService().update(spaceIdMap, deleteMap);
    // panel_global_component
    serviceFactory.getPanelGlobalComponentService().update(panelIdMap, deleteMap);
    // user_compound_metrics_dimension
    serviceFactory.getUserCompoundMetricsDimensionService().update(spaceIdMap, deleteMap);
    // ptone_widget_info 更新
    serviceFactory.getWidgetService().update(panelIdMap, statusMap);

    widgetService.deleteWidgetCorrelation(widgetIdMap, updateMap);

    serviceFactory.getPtoneUserConnectionService().update(spaceIdMap, statusMap);
    serviceFactory.getUserConnectionSourceService().update(spaceIdMap, statusMap);
    serviceFactory.getUserConnectionSourceTableService().update(spaceIdMap, statusMap);
    serviceFactory.getUserConnectionSourceTableColumnService().update(spaceIdMap, statusMap);
  }

  /**
   * 根据spaceId查询panelId，根据panelId查询widgetId
   * @author shaoqiang.guo
   * @date 2016年11月23日 下午7:19:23
   * @param spaceId
   * @param panelIdMap
   * @param widgetIdMap
   * @return
   */
  public void buildPanelIdAndWidgetIdArray(String spaceId, Map<String, Object[]> panelIdMap,
      Map<String, Object[]> widgetIdMap) {
    List<String> panelIdList = new ArrayList<String>();
    panelIdList.add(spaceId);

    List<String> widgetIdList = new ArrayList<String>();

    List<PtonePanelInfo> panelList = spaceInfoDao.getPanelBySpaceId(spaceId);
    if (CollectionUtil.isNotEmpty(panelList)) {
      for (PtonePanelInfo ptoneSpaceInfo : panelList) {
        if (ptoneSpaceInfo == null) {
          continue;
        }
        String panelId = ptoneSpaceInfo.getPanelId();
        if (StringUtil.isBlank(panelId)) {
          continue;
        }
        panelIdList.add(panelId);
        addWidgetIdToList(widgetIdList, panelId);
      }
    }
    if (CollectionUtil.isNotEmpty(widgetIdList)) {
      widgetIdMap.put("widgetId", widgetIdList.toArray());
    }
    if (CollectionUtil.isNotEmpty(panelIdList)) {
      panelIdMap.put("panelId", panelIdList.toArray());
    }
  }

  /**
   * 根据panelId查询widget，并将widgetId放到List中
   * @author shaoqiang.guo
   * @date 2016年11月23日 下午8:03:32
   * @param widgetIdList
   * @param panelId
   */
  public void addWidgetIdToList(List<String> widgetIdList, String panelId) {
    List<PtoneWidgetInfo> widgetList = widgetDao.findWidgetByPanelId(panelId);
    for (PtoneWidgetInfo ptoneWidgetInfo : widgetList) {
      if (ptoneWidgetInfo == null) {
        continue;
      }
      String widgetId = ptoneWidgetInfo.getWidgetId();
      if (StringUtil.isBlank(widgetId)) {
        continue;
      }
      widgetIdList.add(widgetId);
    }
  }

  @Override
  public List<SpaceInfoDto> getUserSpaceList(String uid) {
    List<SpaceInfoDto> spaceList = new ArrayList<SpaceInfoDto>();
    List<PtoneSpaceUser> userList = spaceUserService.getUserSpaceUserList(uid);
    if (userList != null && userList.size() > 0) {
      Map<String, PtoneSpaceUser> spaceToUserMap = new HashMap<String, PtoneSpaceUser>();
      for (PtoneSpaceUser user : userList) {
        spaceToUserMap.put(user.getSpaceId(), user);
      }
      Object[] spaceIdArray = spaceToUserMap.keySet().toArray();
      Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
      paramMap.put("spaceId", spaceIdArray);
      paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
      Map<String, String> orderMap = new HashMap<>();
      orderMap.put("name", "asc");
      List<PtoneSpaceInfo> spaceInfoList = spaceInfoDao.findByWhere(paramMap, orderMap);
      if (spaceInfoList != null && spaceInfoList.size() > 0) {
        Set<String> ownerIdList = new HashSet<String>();
        for (PtoneSpaceInfo spaceInfo : spaceInfoList) {
          SpaceInfoDto space = new SpaceInfoDto();
          BeanUtils.copyProperties(spaceInfo, space);
          PtoneSpaceUser user = spaceToUserMap.get(space.getSpaceId());
          space.setType(user.getType());
          space.setUid(user.getUid());
          space.setUserEmail(user.getUserEmail());
          spaceList.add(space);
          ownerIdList.add(spaceInfo.getOwnerId());
        }

        paramMap = new HashMap<String, Object[]>();
        paramMap.put("ptId", ownerIdList.toArray());
        paramMap.put("status", new Object[] {Constants.validate});
        List<PtoneUser> ownerList = userService.findByWhere(paramMap);
        Map<String, PtoneUser> ownerMap = new HashMap<String, PtoneUser>();
        if (ownerList != null && ownerList.size() > 0) {
          for (PtoneUser user : ownerList) {
            ownerMap.put(user.getPtId(), user);
          }
        }
        for (SpaceInfoDto space : spaceList) {
          PtoneUser owner = ownerMap.get(space.getOwnerId());
          if (owner != null) {
            space.setOwnerEmail(owner.getUserEmail());
            space.setOwnerName(owner.getUserName());
          }
        }

      }
    }
    return spaceList;
  }

  @Override
  public List<PtoneSpaceUser> getSpaceUserList(String spaceId) {
    return spaceUserService.getSpaceFollowerSpaceUserList(spaceId);
  }

  @Override
  @Transactional
  public void inviteUsers(String spaceId, List<String> emails, PtoneUser user) {
    if (StringUtil.isNotBlank(spaceId) && emails != null && emails.size() > 0) {
      for (String email : emails) {
        // 判断SpaceUser是否存在
        PtoneSpaceUser spaceUser = spaceUserService.getSpaceUserByEmail(spaceId, email);
        if (spaceUser == null) {
          String uid = null;
          // 判断PtoneUser是否存在
          PtoneUser ptoneUser = userService.getPtoneUserByEmail(email);
          if (ptoneUser != null) {
            uid = ptoneUser.getPtId();
            // 修改预注册用户为有效用户
            if (Constants.inValidate.equals(ptoneUser.getIsPreRegistration())) {
              ptoneUser.setIsPreRegistration(Constants.validate);
              userService.update(ptoneUser);
            }
          }
          spaceUser = new PtoneSpaceUser();
          spaceUser.setSpaceId(spaceId);
          spaceUser.setStatus(PtoneSpaceUser.STATUS_INVITING);
          spaceUser.setType(PtoneSpaceUser.TYPE_FOLLOWER);
          spaceUser.setUid(uid);
          spaceUser.setUserEmail(email);
          spaceUser.setCreatorId(user.getPtId());
          spaceUser.setCreateTime(System.currentTimeMillis());
          spaceUser.setIsDelete(Constants.inValidateInt);
          spaceUserService.save(spaceUser);
        }
      }
    }
  }

  @Override
  public boolean checkDomainExists(String domain, String currentSpaceId) {
    // 更新预留域名配置
    if (retainDomainRefresh) {
      List<PtoneRetainDomain> list = spaceInfoDao.getRetainDomainList();
      if (list != null && list.size() > 0) {
        for (PtoneRetainDomain retainDomain : list) {
          if (PtoneRetainDomain.TYPE_REGEXP.equalsIgnoreCase(retainDomain.getType())) {
            retainDomainRegexpList.add(retainDomain.getCode().toLowerCase());
          } else {
            retainDomainStrList.add(retainDomain.getCode().toLowerCase());
          }
        }
      }
      retainDomainRefresh = false;
    }

    if (StringUtil.isBlank(domain)) {
      return true;
    }

    domain = domain.toLowerCase();

    // 预留域名处理
    if (retainDomainStrList != null && retainDomainStrList.contains(domain)) {
      return true;
    }
    // 预留域名正则处理
    if (retainDomainRegexpList != null && retainDomainRegexpList.size() > 0) {
      for (String regexp : retainDomainRegexpList) {
        if (domain.matches(regexp)) {
          return true;
        }
      }
    }

    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("domain", new Object[] {domain});
    paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
    PtoneSpaceInfo spaceInfo = spaceInfoDao.getByWhere(paramMap);
    return spaceInfo != null && !spaceInfo.getSpaceId().equals(currentSpaceId);
  }

  @Override
  public String checkInviteUrl(PtoneSpaceInfo spaceInfo, String userEmail) {
    String result = PtoneSpaceUser.INVITE_URL_STATUS_INVALIDATE;

    if (spaceInfo != null) {
      PtoneSpaceUser spaceUser =
          spaceUserService.getSpaceUserByEmail(spaceInfo.getSpaceId(), userEmail);
      if (spaceUser != null) {
        String uid = spaceUser.getUid();
        PtoneUser receiverUser = serviceFactory.getUserService().getUser(userEmail);
        //邀请未激活用户跳转到邀请注册页面 add by zhangli 2017/02/28
        if(null != receiverUser && receiverUser.getIsActivited().equals(Constants.inValidate)){
          result = PtoneSpaceUser.INVITE_URL_STATUS_NOT_ACTIVE;
          return result;
        }
        if (StringUtil.isNotBlank(uid) || receiverUser != null) {
          String status = spaceUser.getStatus();
          if (PtoneSpaceUser.STATUS_INVITING.equals(status)) {
            result = PtoneSpaceUser.INVITE_URL_STATUS_SIGNIN;
          } else if (PtoneSpaceUser.STATUS_ACCEPTED.equals(status)) {
            result = PtoneSpaceUser.INVITE_URL_STATUS_ACCEPTED;
          }
        } else {
          result = PtoneSpaceUser.INVITE_URL_STATUS_SIGNUP;
        }
      } else {
        result = PtoneSpaceUser.INVITE_URL_STATUS_INVITE_REMOVED;
      }
    } else {
      result = PtoneSpaceUser.INVITE_URL_STATUS_SPACE_REMOVED;
    }

    return result;
  }

  @Override
  public boolean acceptInvite(String spaceId, String userEmail) {
    boolean result = false;
    PtoneSpaceInfo spaceInfo = this.get(spaceId);
    if (spaceInfo != null) {
      PtoneSpaceUser spaceUser = spaceUserService.getSpaceUserByEmail(spaceId, userEmail);
      if (spaceUser != null) {
        spaceUser.setStatus(PtoneSpaceUser.STATUS_ACCEPTED);
        if (StringUtil.isBlank(spaceUser.getUid())) {
          PtoneUser ptoneUser = userService.getPtoneUserByEmail(userEmail);
          if (ptoneUser != null) {
            spaceUser.setUid(ptoneUser.getPtId());
            spaceUserService.update(spaceUser);
            result = true;
          }
        } else {
          spaceUserService.update(spaceUser);
          result = true;
        }
      }
    }
    return result;
  }

  @Override
  @Transactional
  public void changeSpaceOwner(String spaceId, String uid, String newOwnerId) {
    PtoneSpaceInfo spaceInfo = this.get(spaceId);
    if (spaceInfo != null) {
      if (StringUtil.isNotBlank(uid) && uid.equals(spaceInfo.getOwnerId())) {
        PtoneUser ptoneUser = userService.getPtoneUser(newOwnerId);
        if (ptoneUser != null) {
          spaceInfo.setOwnerId(newOwnerId);
          spaceInfo.setOwnerEmail(ptoneUser.getUserEmail());

          PtoneSpaceUser oldOwner = spaceUserService.getSpaceUserByUid(spaceId, uid);
          oldOwner.setType(PtoneSpaceUser.TYPE_FOLLOWER);

          PtoneSpaceUser newOwner = spaceUserService.getSpaceUserByUid(spaceId, newOwnerId);
          newOwner.setType(PtoneSpaceUser.TYPE_OWNDER);

          this.update(spaceInfo);
          spaceUserService.update(oldOwner);
          spaceUserService.update(newOwner);

        } else {
          ServiceException se = new ServiceException("new owner not exists");
          throw se;
        }

      } else {
        ServiceException se = new ServiceException("not your own sapce");
        throw se;
      }
    } else {
      ServiceException se = new ServiceException("not find this sapce");
      throw se;
    }
  }

  @Override
  public void deleteSpaceUser(String spaceId, String userEmail) {
    PtoneSpaceUser spaceUser = spaceUserService.getSpaceUserByEmail(spaceId, userEmail);
    if (spaceUser != null) {
      spaceUser.setIsDelete(Constants.validateInt);
      spaceUserService.update(spaceUser);
    }
  }

  @Override
  public PtoneSpaceUser getSpaceUserByUid(String spaceId, String uid) {
    return spaceUserService.getSpaceUserByUid(spaceId, uid);
  }

  @Override
  public int countSpacePanel(String spaceId) {
    return spaceInfoDao.countSpacePanel(spaceId);
  }

  @Override
  public String validateSpacePanel(PtoneSpaceInfo space, String panelId, String uid) {
    String result = null;
    if (space != null) {
      String spaceDomain = space.getDomain();
      Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
      paramMap.put("domain", new Object[] {spaceDomain});
      paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
      PtoneSpaceInfo spaceInfo = spaceInfoDao.getByWhere(paramMap);
      if (spaceInfo != null) {
        if (StringUtil.isNotBlank(uid)) {
          paramMap = new HashMap<String, Object[]>();
          paramMap.put("spaceId", new Object[] {spaceInfo.getSpaceId()});
          paramMap.put("uid", new Object[] {uid});
          paramMap.put("status", new Object[] {PtoneSpaceUser.STATUS_ACCEPTED});
          paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
          PtoneSpaceUser spaceUser = spaceUserService.getByWhere(paramMap);
          if (spaceUser != null) {
            result = PtoneSpaceInfo.SPACE_STATUS_AVAILABLE;
            // 如果panelId存在则校验panel
            if (StringUtil.isNotBlank(panelId)) {
              paramMap = new HashMap<String, Object[]>();
              paramMap.put("spaceId", new Object[] {spaceInfo.getSpaceId()});
              paramMap.put("panelId", new Object[] {panelId});
              paramMap.put("status", new Object[] {Constants.validate});
              PtonePanelInfo panel = panelService.getByWhere(paramMap);
              if (panel != null) {
                result = PtonePanelInfo.PANEL_STATUS_AVAILABLE;
              } else {
                result = PtonePanelInfo.PANEL_STATUS_NOT_EXIST;
              }
            }
          } else {
            result = PtoneSpaceInfo.SPACE_STATUS_NO_AUTH;
          }
        } else {
          result = PtoneSpaceInfo.SPACE_STATUS_NO_AUTH;
        }
      } else {
        result = PtoneSpaceInfo.SPACE_STATUS_NOT_EXIST;
      }
    } else {
      result = PtoneSpaceInfo.SPACE_STATUS_NOT_EXIST;
    }
    return result;
  }

}
