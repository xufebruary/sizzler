package com.sizzler.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sizzler.common.Constants.JsonViewConstants;
import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.common.exception.BusinessErrorCode;
import com.sizzler.common.exception.BusinessException;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.lock.DistributedLockConstants;
import com.sizzler.common.lock.annotation.DistributedLock;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.dao.PanelLayoutDao;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.panel.PtonePanelLayout;
import com.sizzler.domain.panel.dto.PanelLayoutNode;
import com.sizzler.domain.panel.vo.PanelExtVo;
import com.sizzler.service.PanelLayoutService;
import com.sizzler.system.ServiceFactory;

@Service("panelLayoutService")
public class PanelLayoutServiceImpl extends ServiceBaseInterfaceImpl<PtonePanelLayout, Long>
    implements PanelLayoutService {
  
  private static Logger log = LoggerFactory.getLogger(PanelLayoutServiceImpl.class);

  @Autowired
  private PanelLayoutDao panelLayoutDao;
  
  @Autowired
  private ServiceFactory serviceFactory;

  @Override
  public PtonePanelLayout getBySpaceId(String spaceId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("spaceId", new Object[] {spaceId});
    return panelLayoutDao.getByWhere(paramMap);
  }

  @Override
  public PtonePanelLayout getByManager() {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("panelType", new Object[] {PtonePanelLayout.PANEL_TYPE_MANAGER});
    return panelLayoutDao.getByWhere(paramMap);
  }

  @Override
  public void updateOrCreatePanelLayout(String uid, String spaceId, PtonePanelInfo info) {
    // 查询panelLayout
    PtonePanelLayout panelLayout = this.getBySpaceId(spaceId);
    if (null == panelLayout) {
      // 创建一个panelLayout
      panelLayout = new PtonePanelLayout();
      panelLayout.setPanelType(null);
      panelLayout.setUid(uid);
      panelLayout.setSpaceId(spaceId);
      panelLayout.setUpdateTime(System.currentTimeMillis());
      panelLayout.setPanelLayout(createLayout(info));
      save(panelLayout);
    } else {
      // 修改panel信息
      String layout = panelLayout.getPanelLayout();
      if (checkLayoutIsNull(layout)) {
        // 创建
        layout = createLayout(info);
      } else {
        // 在最后添加一个
        layout = appendLayout(layout, info);
      }
      long dataVersion = panelLayout.getDataVersion();
      dataVersion = dataVersion + 1;
      panelLayout.setDataVersion(dataVersion);
      panelLayout.setPanelLayout(layout);

      update(panelLayout);
    }
  }

  @Override
  public PtonePanelLayout updateOrCreatePanelLayout(PtonePanelLayout panelLayout, String uid,
      String spaceId, List<PtonePanelInfo> infoList) {
    if (null == panelLayout) {
      panelLayout = new PtonePanelLayout();
      panelLayout.setPanelType(null);
      panelLayout.setUid(uid);
      panelLayout.setSpaceId(spaceId);
      panelLayout.setUpdateTime(System.currentTimeMillis());
      panelLayout.setDataVersion(0L);
      if (null != infoList && !infoList.isEmpty()) {
        panelLayout.setPanelLayout(createLayout(infoList));
      } else {
        panelLayout.setPanelLayout(null);
      }
      save(panelLayout);
      panelLayout = this.getBySpaceId(spaceId);
    } else {
      if (null != infoList && !infoList.isEmpty()) {
        panelLayout.setPanelLayout(createLayout(infoList));
      } else {
        panelLayout.setPanelLayout(null);
      }
      long dataVersion = panelLayout.getDataVersion();
      dataVersion = dataVersion + 1;
      panelLayout.setDataVersion(dataVersion);

      update(panelLayout);
    }
    return panelLayout;
  }

  @Deprecated
  @Override
  public PtonePanelLayout updateDataVersion(PtonePanelLayout panelLayout) {
    if (null == panelLayout) {
      return panelLayout;
    }
    Long dataVersion = panelLayout.getDataVersion();
    dataVersion += 1;
    panelLayout.setDataVersion(dataVersion);
    update(panelLayout);
    return panelLayout;
  }
  
  @Override
  @DistributedLock(name = DistributedLockConstants.DISTRIBUTED_LOCK_PANEL_LAYOUT,
      interval = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_WAIT_INTERVAL,
      timeout = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_TIMEOUT)
  public PanelExtVo updateDataVersion(PtonePanelLayout panelLayout, String spaceId,
      boolean isPanelTemplet) throws BusinessException {
    PanelExtVo panelExtVo = new PanelExtVo();
    panelExtVo.setStatus(JsonViewConstants.JSON_VIEW_STATUS_SUCCESS);
    if (panelLayout == null) {
      String errorMsg = "update panel layout dataVersion warn: layout info is null";
      log.warn(errorMsg);
      throw new BusinessException(BusinessErrorCode.Panel.UPDATE_PANEL_LAYOUT_ERROR, errorMsg);
    }

    try {
      PtonePanelLayout dbPanelLayout = this.getBySpaceId(spaceId);
      if (dbPanelLayout.getDataVersion() - panelLayout.getDataVersion() != 0) {
        panelExtVo =
            serviceFactory.getPanelService().getPanelWithComponentsListBySpaceId(spaceId,
                isPanelTemplet);
        panelExtVo.setStatus(JsonViewConstants.JSON_VIEW_STATUS_FAILED);

        log.info(">>>>> update panel layout failed, casuse by dataVersion is old, spaceId: "
            + spaceId + ", dbVersion:: " + dbPanelLayout.getDataVersion() + ", currentVersion::"
            + panelLayout.getDataVersion());
      } else {
        panelLayout.setId(dbPanelLayout.getId()); // 修正PanelLayout的id，部分接口没有传递id
        long dataVersion = panelLayout.getDataVersion();
        dataVersion = dataVersion + 1;
        panelLayout.setDataVersion(dataVersion);
        update(panelLayout);
        panelExtVo.setPanelLayout(panelLayout);
        panelExtVo.setStatus(JsonViewConstants.JSON_VIEW_STATUS_SUCCESS);
      }
    } catch (Exception e) {
      String errorMsg =
          "update panel layout dataVersion error:" + e.getMessage() + "::"
              + JSON.toJSONString(panelLayout);
      log.error(errorMsg, e);
      panelExtVo.setStatus(JsonViewConstants.JSON_VIEW_STATUS_ERROR);
      throw new BusinessException(BusinessErrorCode.Panel.UPDATE_PANEL_LAYOUT_ERROR, errorMsg);
    }

    return panelExtVo;
  }
  
  @Override
  public PanelExtVo operatePanelLayout(PanelLayoutNode newPanelLayoutNode, String spaceId,
      String operation) {
    PanelExtVo panelExtVo = new PanelExtVo();
    PtonePanelLayout dbPanelLayout = this.getBySpaceId(spaceId);
    if (dbPanelLayout == null) {
      log.warn(operation + " panel layout node warn: layout from db is null");
      throw new ServiceException(operation + " panel layout node warn: layout from db is null");
    } else {
      String panelLayout = dbPanelLayout.getPanelLayout();
      String newPanelLayout =
          PanelLayoutNode.operatePanelLayout(panelLayout, newPanelLayoutNode, operation);
      dbPanelLayout.setPanelLayout(newPanelLayout);

      long dataVersion = dbPanelLayout.getDataVersion();
      dataVersion = dataVersion + 1;
      dbPanelLayout.setDataVersion(dataVersion);
      update(dbPanelLayout);

      panelExtVo.setPanelLayout(dbPanelLayout);
      panelExtVo.setStatus(JsonViewConstants.JSON_VIEW_STATUS_SUCCESS);
    }
    return panelExtVo;
  }

  private String concatLayout(String panelId, String panelTitle) {
    String type = "panel";
    panelTitle = panelTitle.replaceAll("\\\"", "\\\\\"");
    StringBuilder sb = new StringBuilder("");
    sb.append("{").append("\"type\":\"").append(type).append("\", ").append("\"panelId\":\"")
        .append(panelId).append("\", ").append("\"panelTitle\":\"").append(panelTitle)
        .append("\" ").append("}");
    return sb.toString();
  }

  private String createLayout(PtonePanelInfo info) {
    if (null == info) {
      return null;
    }
    String panelId = info.getPanelId();
    String panelTitle = info.getPanelTitle();
    StringBuilder sb = new StringBuilder("");
    sb.append("[").append(concatLayout(panelId, panelTitle)).append("]");
    return sb.toString();
  }

  private String createLayout(List<PtonePanelInfo> infoList) {
    StringBuilder sb = new StringBuilder("");
    sb.append("[");
    int size = infoList.size();
    for (int i = 0; i < size; i++) {
      PtonePanelInfo info = infoList.get(i);
      sb.append(concatLayout(info.getPanelId(), info.getPanelTitle()));
      if ((i + 1) != size) {
        sb.append(",");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  private String appendLayout(String layout, PtonePanelInfo info) {
    if (null == info || StringUtil.isBlank(layout)) {
      return layout;
    }
    int layoutLength = layout.length();
    int lastIndex = StringUtils.lastIndexOf(layout, "]");
    if (lastIndex != -1 && (lastIndex + 1) == layoutLength) {
      layout = layout.substring(0, lastIndex);
      StringBuilder sb = new StringBuilder("");
      sb.append(layout).append(", ").append(concatLayout(info.getPanelId(), info.getPanelTitle()))
          .append("]");
      return sb.toString();
    } else {
      return layout;
    }
  }

  private boolean checkLayoutIsNull(String layout) {
    try {
      if (StringUtil.isBlank(layout)) {
        return true;
      } else {
        JSONArray obj = JSON.parseArray(layout);
        if (obj == null || obj.isEmpty()) {
          return true;
        }
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

}
