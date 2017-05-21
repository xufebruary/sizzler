package com.sizzler.domain.panel;

import java.io.Serializable;

import com.sizzler.common.utils.StringUtil;
import com.sizzler.dexcoder.annotation.PK;

/**
 * Panel文件夹布局
 */
public class PtonePanelLayout implements Serializable {

  private static final long serialVersionUID = 5071038075121806765L;

  public static final String PANEL_LAYOUT_KEY_PREFIX = "PanelLayoutKey:";

  public static final int PANEL_LAYOUT_KEY_LOCK_TIMEOUT = 5; // panelLayout分布式锁默认超时时间，
                                                             // 单位s

  public static final int PANEL_LAYOUT_KEY_LOCK_WAIT_INTERVAL = 200; // panelLayout分布式锁等待锁时间，
                                                                     // 单位ms

  public static final String PANEL_TYPE_MANAGER = "1"; // 是模板管理员空间的layout信息

  @PK
  private Long id; // 主键ID
  private String panelLayout; // panel的文件夹布局，可以设置多级文件夹
  private String uid; // 用户ID
  private String spaceId; // 空间ID
  private Long updateTime; // 修改时间
  private String panelType;// panelType == 1 是模板管理员
  private Long dataVersion;// 位置信息版本号

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPanelLayout() {
    return panelLayout;
  }

  public void setPanelLayout(String panelLayout) {
    this.panelLayout = panelLayout;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public Long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Long updateTime) {
    this.updateTime = updateTime;
  }

  public String getPanelType() {
    return panelType;
  }

  public void setPanelType(String panelType) {
    this.panelType = panelType;
  }

  public Long getDataVersion() {
    return dataVersion;
  }

  public void setDataVersion(Long dataVersion) {
    this.dataVersion = dataVersion;
  }

  public static String getPanelLayoutKey(String spaceId) {
    String lockKey = null;
    if (StringUtil.isNotBlank(spaceId)) {
      lockKey = PANEL_LAYOUT_KEY_PREFIX + spaceId;
    }
    return lockKey;
  }

  public static String getPanelLayoutKey(PtonePanelLayout panelLayout) {
    String spaceId = null;
    if (panelLayout != null) {
      spaceId = panelLayout.getSpaceId();
    }
    return getPanelLayoutKey(spaceId);
  }
}
