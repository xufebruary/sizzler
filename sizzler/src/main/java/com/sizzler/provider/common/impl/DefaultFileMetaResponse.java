package com.sizzler.provider.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.sizzler.MetaContentNode;
import com.sizzler.provider.common.MetaResponse;
import com.sizzler.provider.common.file.PtoneFile;

public class DefaultFileMetaResponse implements MetaResponse {

  private List<PtoneFile> fileList;

  public List<PtoneFile> getFileList() {
    return fileList;
  }

  public void setFileList(List<PtoneFile> fileList) {
    this.fileList = fileList;
  }

  public String getRootNodeName() {
    return "";
  }

  @Override
  public String getContent() {
    String contentJson = "";
    List<MetaContentNode> metaContentNodeList = new ArrayList<>();

    MetaContentNode rootContentNode = new MetaContentNode();
    rootContentNode.setName(getRootNodeName());

    if (fileList == null || fileList.size() <= 0) {
      rootContentNode.setLeaf(true);
    } else {
      rootContentNode.setLeaf(false);
      List<MetaContentNode> childNodeList = new ArrayList<MetaContentNode>();
      for (PtoneFile ptoneFile : fileList) {
        childNodeList.add(creatMetaContentNodeByPtoneFile(ptoneFile));
      }
      rootContentNode.setChild(childNodeList);
    }

    metaContentNodeList.add(rootContentNode);
    contentJson = JSON.toJSONString(metaContentNodeList);

    return contentJson;
  }

  public MetaContentNode creatMetaContentNodeByPtoneFile(PtoneFile ptoneFile) {
    MetaContentNode metaContentNode = new MetaContentNode();
    metaContentNode.setId(ptoneFile.getId());
    metaContentNode.setName(ptoneFile.getName());
    Map<String, Object> extra = new HashMap<>();
    StringBuilder owner = new StringBuilder("");
    List<String> ownerNameList = ptoneFile.getOwnerNameList();

    if (ownerNameList != null && ownerNameList.size() > 0) {
      for (int i = 0; i < ownerNameList.size(); i++) {
        owner.append(ownerNameList.get(i));
        if (i < ownerNameList.size() - 1) {
          owner.append(",");
        }
      }
    }

    extra.put("owner", owner.toString());
    extra.put("lastModifiedDate", ptoneFile.getLastModifiedDate());
    extra.put("lastModifiedUserName", ptoneFile.getLastModifiedUserName());
    extra.put("fileSize", ptoneFile.getFileSize());
    extra.put("isDirectory", ptoneFile.isDirectory());
    metaContentNode.setExtra(extra);

    if (ptoneFile.hasChild()) {
      metaContentNode.setLeaf(false);
      List<MetaContentNode> childNodeList = new ArrayList<>();
      metaContentNode.setChild(childNodeList);
      for (PtoneFile childPtoneFile : ptoneFile.getChild()) {
        childNodeList.add(creatMetaContentNodeByPtoneFile(childPtoneFile));
      }
    } else {
      metaContentNode.setLeaf(true);
    }
    return metaContentNode;
  }

}
