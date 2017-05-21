package com.sizzler.domain.panel.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;

public class PanelLayoutNode implements Serializable {

  private static final long serialVersionUID = -4344820177755230049L;

  public static final String TYPE_PANEL = "panel";
  public static final String TYPE_CONTAINER = "container";

  public static final String OPERATION_ADD = "add";
  public static final String OPERATION_UPDATE = "update";
  public static final String OPERATION_DELETE = "delete";
  public static final String OPERATION_BATCH_DELETE = "batchDelete";

  private String type; // panel || container

  private String panelId;
  private String panelTitle;
  private String shareSourceId;

  private String containerId;
  private String containerName;
  private List<List<PanelLayoutNode>> columns;
  private Boolean fold;
  private Boolean editing;

  public PanelLayoutNode() {
  }

  public PanelLayoutNode(String type) {
    this(type, null, null);
  }

  public PanelLayoutNode(String type, String id) {
    this(type, id, null);
  }

  public PanelLayoutNode(String type, String id, String name) {
    this.type = type;
    if (TYPE_PANEL.equals(type)) {
      this.panelId = id;
      this.panelTitle = name;
    } else if (TYPE_CONTAINER.equals(type)) {
      this.containerId = id;
      this.containerName = name;
    }
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPanelId() {
    return panelId;
  }

  public void setPanelId(String panelId) {
    this.panelId = panelId;
  }

  public String getPanelTitle() {
    return panelTitle;
  }

  public void setPanelTitle(String panelTitle) {
    this.panelTitle = panelTitle;
  }

  public String getShareSourceId() {
    return shareSourceId;
  }

  public void setShareSourceId(String shareSourceId) {
    this.shareSourceId = shareSourceId;
  }

  public String getContainerId() {
    return containerId;
  }

  public void setContainerId(String containerId) {
    this.containerId = containerId;
  }

  public String getContainerName() {
    return containerName;
  }

  public void setContainerName(String containerName) {
    this.containerName = containerName;
  }

  public List<List<PanelLayoutNode>> getColumns() {
    return columns;
  }

  public void setColumns(List<List<PanelLayoutNode>> columns) {
    this.columns = columns;
  }

  public Boolean getFold() {
    return fold;
  }

  public void setFold(Boolean fold) {
    this.fold = fold;
  }

  public Boolean getEditing() {
    return editing;
  }

  public void setEditing(Boolean editing) {
    this.editing = editing;
  }

  /**
   * 将panelLayout json转换为PanelLayoutNode对象
   */
  public static List<PanelLayoutNode> parsePanelLayout(String panelLayout) {
    List<PanelLayoutNode> rootNodeList = new ArrayList<PanelLayoutNode>();
    if (StringUtil.isNotBlank(panelLayout)) {
      rootNodeList = JSON.parseArray(panelLayout, PanelLayoutNode.class);
    }
    return rootNodeList;
  }

  /**
   * 返回添加panel后新的panelLayou json
   */
  public static String add(String panelLayout, PanelLayoutNode newPanel) {
    List<PanelLayoutNode> newRootNodeList = new ArrayList<PanelLayoutNode>();
    if (newPanel != null) {
      newRootNodeList.add(newPanel);
    }
    List<PanelLayoutNode> rootNodeList = parsePanelLayout(panelLayout);
    if (rootNodeList != null) {
      newRootNodeList.addAll(rootNodeList);
    }
    return JSON.toJSONString(newRootNodeList);
  }

  /**
   * 返回修改panel后新的panelLayou json
   */
  public static String update(String panelLayout, PanelLayoutNode newPanel) {
    List<PanelLayoutNode> newRootNodeList = new ArrayList<PanelLayoutNode>();
    List<PanelLayoutNode> rootNodeList = parsePanelLayout(panelLayout);
    if (rootNodeList != null) {
      newRootNodeList.addAll(rootNodeList);
      if (newPanel != null) {
        for (PanelLayoutNode panelNode : newRootNodeList) {
          updatePanelNode(panelNode, newPanel);
        }
      }
    }
    return JSON.toJSONString(newRootNodeList);
  }

  /**
   * 递归遍历panelLayou对象, 修改对应panel节点信息
   */
  private static void updatePanelNode(PanelLayoutNode currentNode, PanelLayoutNode newPanel) {
    if (currentNode != null && newPanel != null) {
      if (PanelLayoutNode.TYPE_PANEL.equals(currentNode.getType())) {
        if (currentNode.getPanelId().equals(newPanel.getPanelId())) {
          // 修改panel信息
          currentNode.setPanelTitle(newPanel.getPanelTitle());
          return;
        }
      } else {
        if (currentNode.getContainerId().equals(newPanel.getContainerId())) {
          // 修改panel信息
          currentNode.setContainerName(newPanel.getContainerName());
          return;
        } else {
          if (currentNode.getColumns() != null) {
            for (List<PanelLayoutNode> nodeList : currentNode.getColumns()) {
              for (PanelLayoutNode node : nodeList) {
                updatePanelNode(node, newPanel);
              }
            }
          }
        }
      }
    }
  }

  /**
   * 返回删除panel后新的panelLayou json
   */
  public static String delete(String panelLayout, List<String> nodeIdList) {

    List<PanelLayoutNode> newRootNodeList = new ArrayList<PanelLayoutNode>();
    List<PanelLayoutNode> rootNodeList = parsePanelLayout(panelLayout);
    if (rootNodeList != null) {
      newRootNodeList.addAll(rootNodeList);
      if (CollectionUtil.isNotEmpty(nodeIdList)) {
        deletePanelNode(newRootNodeList, nodeIdList);
      }
    }
    return JSON.toJSONString(newRootNodeList);
  }

  /**
   * 递归遍历panelLayout对象，删除对应panel节点
   */
  private static void deletePanelNode(List<PanelLayoutNode> currentNodeList, List<String> nodeIdList) {
    if (currentNodeList != null && CollectionUtil.isNotEmpty(nodeIdList)) {
      Iterator<PanelLayoutNode> iterator = currentNodeList.iterator();
      while (iterator.hasNext()) {
        PanelLayoutNode currentNode = iterator.next();
        if (PanelLayoutNode.TYPE_PANEL.equals(currentNode.getType())) {
          if (nodeIdList.contains(currentNode.getPanelId())) {
            iterator.remove();
          }
        } else {
          if (nodeIdList.contains(currentNode.getContainerId())) {
            iterator.remove();
          }
          if (currentNode.getColumns() != null) {
            for (List<PanelLayoutNode> nodeList : currentNode.getColumns()) {
              deletePanelNode(nodeList, nodeIdList);
            }
          }
        }
      }
    }
  }

  /**
   * 操作panelLayout的公共方法， 统一了 add、 update、 delete
   */
  public static String operatePanelLayout(String panelLayout, PanelLayoutNode newPanel,
      String operation) {
    if (newPanel == null) {
      return panelLayout;
    } else if (PanelLayoutNode.OPERATION_ADD.equals(operation)) {
      return add(panelLayout, newPanel);
    } else if (PanelLayoutNode.OPERATION_UPDATE.equals(operation)) {
      return update(panelLayout, newPanel);
    } else if (PanelLayoutNode.OPERATION_DELETE.equals(operation)) {
      List<String> nodeIdList = new ArrayList<String>();
      if (PanelLayoutNode.TYPE_PANEL.equals(newPanel.getType())) {
        nodeIdList.add(newPanel.getPanelId());
      } else if (PanelLayoutNode.TYPE_CONTAINER.equals(newPanel.getType())) {
        nodeIdList.add(newPanel.getContainerId());
      }
      return delete(panelLayout, nodeIdList);
    } else {
      return panelLayout;
    }
  }

}
