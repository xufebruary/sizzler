package com.sizzler.provider.domain.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.sizzler.MetaContentNode;
import com.sizzler.provider.common.MetaResponse;
import com.sizzler.provider.common.file.PtoneFile;

public class DataBaseMetaFolderResponse implements MetaResponse {

  private static final long serialVersionUID = 2010425365313045718L;

  private String folderId;
  private List<PtoneFile> ptoneFileList = new ArrayList<PtoneFile>();

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  public List<PtoneFile> getPtoneFileList() {
    return ptoneFileList;
  }

  public void setPtoneFileList(List<PtoneFile> ptoneFileList) {
    this.ptoneFileList = ptoneFileList;
  }

  @Override
  public String getContent() {
    String contentJson = "";
    List<MetaContentNode> metaContentNodeList = new ArrayList<>();
    MetaContentNode myDriveContentNode = createMetaContentNode(folderId, ptoneFileList);
    metaContentNodeList.add(myDriveContentNode);
    contentJson = JSON.toJSONString(metaContentNodeList);
    return contentJson;
  }

  public MetaContentNode createMetaContentNode(String name, List<PtoneFile> ptoneFileList) {
    MetaContentNode metaContentNode = new MetaContentNode();
    metaContentNode.setName(name);

    if (ptoneFileList == null || ptoneFileList.size() <= 0) {
      metaContentNode.setLeaf(true);
    } else {
      metaContentNode.setLeaf(false);

      List<MetaContentNode> childNodeList = new ArrayList<MetaContentNode>();
      metaContentNode.setChild(childNodeList);

      for (PtoneFile folder : ptoneFileList) {
        childNodeList.add(creatMetaContentNodeByPtoneFile(folder));
      }
    }

    return metaContentNode;

  }

  public MetaContentNode creatMetaContentNodeByPtoneFile(PtoneFile file) {
    MetaContentNode metaContentNode = new MetaContentNode();
    metaContentNode.setId(file.getName());
    metaContentNode.setName(file.getName());
    Map<String, Object> extra = new HashMap<String, Object>();

    extra.put("isDirectory", file.isDirectory());
    metaContentNode.setExtra(extra);

    if (file.hasChild()) {
      metaContentNode.setLeaf(false);
      List<MetaContentNode> childNodeList = new ArrayList<MetaContentNode>();
      metaContentNode.setChild(childNodeList);
      for (PtoneFile childFile : file.getChild()) {
        childNodeList.add(creatMetaContentNodeByPtoneFile(childFile));
      }
    } else {
      metaContentNode.setLeaf(true);
    }
    return metaContentNode;
  }
}
