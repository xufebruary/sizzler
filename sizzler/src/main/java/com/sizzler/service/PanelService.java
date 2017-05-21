package com.sizzler.service;


import java.util.List;
import java.util.Map;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.common.exception.BusinessException;
import com.sizzler.domain.panel.PanelGlobalComponent;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.panel.PtonePanelLayout;
import com.sizzler.domain.panel.dto.PanelInfoExt;
import com.sizzler.domain.panel.vo.PanelExtVo;
import com.sizzler.domain.panel.vo.SharePanelVerifyVo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.widget.vo.WidgetListVo;

public interface PanelService extends ServiceBaseInterface<PtonePanelInfo, String> {

  public abstract String validateSharePanel(String panelId, String password);

  public abstract void deletePanel(String panelId, boolean isDelete);

  /**
   * 删除并更新layout信息
   * @param panelId
   * @param panelLayout
   * @modify shaoqiang.guo 新增参数isDelete，用以判断是删除还是恢复
   */
  @Deprecated
  public abstract PtonePanelLayout deletePanelAndUpdatePanelLayout(String panelId,
      PtonePanelLayout panelLayout, boolean isDelete);
  
  /**
   * 删除并更新layout信息
   * @param panelId
   * @param spaceId
   */
  public abstract PanelExtVo deletePanelAndLayout(String panelId, String spaceId, boolean isDelete);

  /**
   * 批量删除panel
   * @param panelIds
   * @modify shaoqiang.guo 新增参数isDelete，用以判断是删除还是恢复
   */
  public abstract void batchDeletePanels(String[] panelIds, boolean isDelete);

  /**
   * 批量删除panel并更新layout信息
   * @param panelIds
   * @param panelLayout
   * @modify shaoqiang.guo 新增参数isDelete，用以判断是删除还是恢复
   */
  @Deprecated
  public abstract PtonePanelLayout deleteBatchPanelsAndUpdatePanelLayout(String[] panelIds,
      PtonePanelLayout panelLayout, boolean isDelete);
  
  /**
   * 批量删除panel并更新layout信息
   * @param panelIds
   * @param panelLayout
   */
  public abstract PanelExtVo batchDeletePanelsAndLayout(String panelFolderId,
      String[] panelIds, String spaceId, boolean isDelete);

  /**
   * 根据panelId去查询该panel下的所有的widget， 并将所有的widgetId置入数组，返回Map
   * @author shaoqiang.guo
   * @date 2016年11月23日 上午9:55:51
   * @param panelId
   * @return
   */
  public abstract Map<String, Object[]> buildWidgetIdArray(String panelId);

  public abstract List<PanelGlobalComponent> findGlobalComponents(List<String> panelIdList);

  public abstract void updateComponents(PanelGlobalComponent component);

  public abstract void saveComponents(PanelGlobalComponent component);

  public abstract PanelGlobalComponent getComponents(Map<String, Object[]> paramMap);

  public abstract void applyPanelComponent(PanelGlobalComponent component) throws BusinessException ;
  
  public abstract void cancelPanelComponent(String panelId);

  public abstract String addSharePanel(String sharePanelId, String uid, String spaceId,
      PanelGlobalComponent component);

  public abstract void updatePanelShareSourceStatus(String sharePanelId, String status);

  public abstract void updatePanelInfo(PtonePanelInfo panel);

  /**
   * 修改并修改layout信息
   * @param panel
   * @param uid
   * @param panelLayout
   */
  @Deprecated
  public abstract PtonePanelLayout updatePanelInfoAndUpdatePanelLayout(PtonePanelInfo panel,
      PtonePanelLayout panelLayout);

  /**
   * 修改并修改layout信息
   * @param panel
   * @param spaceId
   */
  public abstract PanelExtVo updatePanelAndLayout(PtonePanelInfo panel,
      String spaceId);


  public abstract void addNewPanel(PtonePanelInfo panel, String uid);

  /**
   * 添加并修改layout信息
   * @param panel
   * @param uid
   * @param panelLayout
   */
  @Deprecated
  public abstract PtonePanelLayout addNewPanelAndUpdatePanelLayout(PtonePanelInfo panel,
      String uid, PtonePanelLayout panelLayout);

  /**
   * 添加并修改layout信息
   * @param panel
   * @param uid
   * @param spaceId
   */
  public abstract PanelExtVo addPanelAndLayout(PtonePanelInfo panel, String uid,
      String spaceId);

  public abstract PanelExtVo addPanelByTemplet(String templetId, String spaceId,
      String uid) throws BusinessException;
  
  public abstract List<PtonePanelInfo> getPanelListBySpaceId(String spaceId);

  /**
   * 判断分享panel在指定空间下是否存在
   * @param pid
   * @param spaceId
   * @return
   * @throws BusinessException
   * @date: 2017年1月13日
   * @author peng.xu
   */
  public abstract boolean isExistsPanelInSpace(String pid, String spaceId) throws BusinessException;

  /**
   * 获取空间下panel列表
   * @param spaceId
   * @param isPanelTemplet 是否为PanelTemplet
   * @return
   * @throws BusinessException
   * @date: 2017年1月13日
   * @author peng.xu
   */
  public abstract PanelExtVo getPanelWithComponentsListBySpaceId(String spaceId,
      boolean isPanelTemplet) throws BusinessException;
  
  
  /**
   * 为第一次新创建空间的用户预制panel
   * @param spaceId
   * @param ptoneUser 当前用户
   * @param localLang 用户语言
   * @return
   * @throws BusinessException
   * @date: 2017年1月20日
   * @author peng.xu
   */
  public abstract void initDefaultPanelForUserFirstSpace(String spaceId, PtoneUser ptoneUser, String localLang)
      throws BusinessException;

  /**
   * 复制panel
   * @param panelInfo 新panel信息
   * @param srcPanelId 原始panelId
   * @param spaceId
   * @return
   * @date: 2016年11月8日
   * @author peng.xu
   */
  public abstract PanelExtVo copyPanel(PtonePanelInfo panelInfo, String srcPanelId,
      String spaceId) throws Exception;

  /**
   * 增加panel文件夹
   * @param panelInfo
   * @param spaceId
   * @return
   * @date: 2016年11月15日
   * @author peng.xu
   */
  public abstract PanelExtVo addPanelFolder(PtonePanelInfo panelInfo, String spaceId);

  /**
   * 修改panel文件夹
   * @param panelInfo
   * @param spaceId
   * @return
   * @date: 2016年11月15日
   * @author peng.xu
   */
  public abstract PanelExtVo updatePanelFolder(PtonePanelInfo panelInfo, String spaceId);
  
  /**
   * 新增panel、panelTemplet、panelFolder
   * @param panel
   * @param uid
   * @param isPanelTemplet
   * @return
   * @date: 2017年1月3日
   * @author peng.xu
   */
  public abstract PanelExtVo addPanel(PanelInfoExt panelExt, String uid, boolean isPanelTemplet)
      throws BusinessException;

  /**
   * 复制panel （TODO： 目前不支持panel模板复制）
   * @param panelExt
   * @param srcPanelId
   * @param uid
   * @param isPanelTemplet
   * @return
   * @date: 2017年1月10日
   * @author peng.xu
   */
  public abstract PanelExtVo copyPanel(PanelInfoExt panelExt, String srcPanelId, String uid, boolean isPanelTemplet)
      throws BusinessException;
  
  
  /**
   * 修改panel、panelTemplet、panelFolder
   * @param panel
   * @param uid
   * @param isPanelTemplet
   * @return
   * @date: 2017年1月3日
   * @author peng.xu
   */
  public abstract PanelExtVo updatePanel(PanelInfoExt panelExt, String uid, boolean isPanelTemplet)
      throws BusinessException;
  
  
  /**
   * 删除panel、panelTemplet、panelFolder
   * @param panel
   * @param uid
   * @param isAdmin
   * @return
   * @date: 2017年1月3日
   * @author peng.xu
   */
  public abstract PanelExtVo deletePanel(String panelId, PanelInfoExt panelExt, String uid, boolean isPanelTemplet)
      throws BusinessException;

  /**
   * 校验分享panel的密码
   * @param sharePanelVerifyVo
   * @return
   * @throws BusinessException
   * @date: 2017年1月12日
   * @author peng.xu
   */
  public abstract boolean sharePanelVerifyPassword(SharePanelVerifyVo sharePanelVerifyVo)
      throws BusinessException;
  
  /**
   * 获取panel信息和全局设置信息
   * @return
   * @throws BusinessException
   * @date: 2017年1月13日
   * @author peng.xu
   */
  public abstract Object getPanelWithComponentsById(String panelId, boolean isPanelTemplet)
      throws BusinessException;
  
  /**
   * 获取panel下的widget列表及widgetLayout信息
   * @param panelId
   * @param uid 
   * @param password 分享panel的密码
   * @param isPanelTemplet 是否panelTemplet
   * @param isMobile 是否移动端
   * @return
   * @date: 2017年1月19日
   * @author peng.xu
   */
  public abstract WidgetListVo findPanelWidgetListWithLayout(String panelId, String uid,
      String password, boolean isPanelTemplet, boolean isMobile) throws BusinessException;

}
