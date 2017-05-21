package com.sizzler.service;

import java.util.List;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.common.exception.BusinessException;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.panel.PtonePanelLayout;
import com.sizzler.domain.panel.dto.PanelLayoutNode;
import com.sizzler.domain.panel.vo.PanelExtVo;

public interface PanelLayoutService extends ServiceBaseInterface<PtonePanelLayout, Long> {
  /**
   * 根据用户、空间查询空间下的panel布局
   * @param spaceId 空间的UUID
   * @return
   */
  public abstract PtonePanelLayout getBySpaceId(String spaceId);

  /**
   * 获取template中的layout，只提供给模板管理员使用
   * @return
   */
  public abstract PtonePanelLayout getByManager();

  /**
   * 修改或者创建panelLayout，更多用于分享
   * @param uid
   * @param spaceId
   */
  public void updateOrCreatePanelLayout(String uid, String spaceId, PtonePanelInfo info);

  /**
   * 创建、更新panelLayout，用于在获取layout时，没有Layout信息，然后通过infoList数据组装出一个layout对象
   * @param uid
   * @param spaceId
   * @param infoList
   */
  public PtonePanelLayout updateOrCreatePanelLayout(PtonePanelLayout panelLayout, String uid,
      String spaceId, List<PtonePanelInfo> infoList);

  /**
   * 修改位置信息版本号
   * @param panelLayout
   * @return
   */
  @Deprecated
  public PtonePanelLayout updateDataVersion(PtonePanelLayout panelLayout);

  /**
   * 修改位置信息版本号
   * @param panelLayout
   * @param spaceId 冗余参数，用于生成分布式key
   * @return
   */
  public PanelExtVo updateDataVersion(PtonePanelLayout panelLayout, String spaceId,
      boolean isPanelTemplet) throws BusinessException;

  /**
   * 操作panelLayout， 包括 add、 update、 delete
   * @param panelId
   * @param spaceId
   * @return
   * @date: 2016年11月11日
   * @author peng.xu
   */
  public PanelExtVo operatePanelLayout(PanelLayoutNode newPanelLayoutNode, String spaceId,
      String operation);

}
