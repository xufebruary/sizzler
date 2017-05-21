package com.sizzler.provider.domain.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.sizzler.MetaContentNode;
import com.sizzler.provider.common.MetaResponse;
import com.sizzler.provider.common.file.PtoneFile;
import com.sizzler.provider.domain.PtoneDriveFile;

public class GoogleDriveMetaResponse implements MetaResponse {

  private static final long serialVersionUID = -2288206711877432692L;

  private List<PtoneDriveFile> myDriveFileList;
  private List<PtoneDriveFile> sharedWithMeFileList;

  public List<PtoneDriveFile> getMyDriveFileList() {
    return myDriveFileList;
  }

  public void setMyDriveFileList(List<PtoneDriveFile> myDriveFileList) {
    this.myDriveFileList = myDriveFileList;
  }

  public List<PtoneDriveFile> getSharedWithMeFileList() {
    return sharedWithMeFileList;
  }

  public void setSharedWithMeFileList(List<PtoneDriveFile> sharedWithMeFileList) {
    this.sharedWithMeFileList = sharedWithMeFileList;
  }

  @Override
  public String getContent() {
    String contentJson = "";
    List<MetaContentNode> metaContentNodeList = new ArrayList<>();

    /*
     * MetaContentNode myDriveContentNode=new MetaContentNode();
     * myDriveContentNode.setName("myDrive");
     * 
     * if(myDriveFileList==null||myDriveFileList.size()<=0) {
     * myDriveContentNode.setChild(null); myDriveContentNode.setLeaf(true);
     * }else { myDriveContentNode.setLeaf(false);
     * 
     * List<MetaContentNode> childNodeList=new ArrayList<>();
     * myDriveContentNode.setChild(childNodeList);
     * 
     * for(PtoneDriveFile ptoneDriveFile:myDriveFileList) {
     * childNodeList.add(creatMetaContentNodeByPtoneFile(ptoneDriveFile)); } }
     */
    MetaContentNode myDriveContentNode = createMetaContentNode("myDrive", myDriveFileList);
    metaContentNodeList.add(myDriveContentNode);

    /*
     * MetaContentNode sharedWithMeContentNode=new MetaContentNode();
     * sharedWithMeContentNode.setName("sharedWithMe");
     * if(sharedWithMeFileList==null||sharedWithMeFileList.size()<=0) {
     * sharedWithMeContentNode.setChild(null);
     * sharedWithMeContentNode.setLeaf(true); }else {
     * sharedWithMeContentNode.setLeaf(false); List<MetaContentNode>
     * childNodeList=new ArrayList<>();
     * sharedWithMeContentNode.setChild(childNodeList);
     * 
     * for(PtoneDriveFile ptoneDriveFile:sharedWithMeFileList) {
     * childNodeList.add(creatMetaContentNodeByPtoneFile(ptoneDriveFile)); } }
     */

    MetaContentNode sharedWithMeContentNode = createMetaContentNode("sharedWithMe",
        sharedWithMeFileList);
    metaContentNodeList.add(sharedWithMeContentNode);

    contentJson = JSON.toJSONString(metaContentNodeList);

    return contentJson;
  }

  public MetaContentNode createMetaContentNode(String name, List<PtoneDriveFile> ptoneDriveFileList) {
    MetaContentNode metaContentNode = new MetaContentNode();
    metaContentNode.setName(name);

    if (ptoneDriveFileList == null || ptoneDriveFileList.size() <= 0) {
      // metaContentNode.setChild(null);
      metaContentNode.setLeaf(true);
    } else {
      metaContentNode.setLeaf(false);

      List<MetaContentNode> childNodeList = new ArrayList<>();
      metaContentNode.setChild(childNodeList);

      for (PtoneDriveFile ptoneDriveFile : ptoneDriveFileList) {
        childNodeList.add(creatMetaContentNodeByPtoneFile(ptoneDriveFile));
      }
    }

    return metaContentNode;

  }

  public MetaContentNode creatMetaContentNodeByPtoneFile(PtoneDriveFile ptoneDriveFile) {
    MetaContentNode metaContentNode = new MetaContentNode();
    metaContentNode.setId(ptoneDriveFile.getId());
    metaContentNode.setName(ptoneDriveFile.getName());
    Map<String, Object> extra = new HashMap<>();
    StringBuilder owner = new StringBuilder("");
    List<String> ownerNameList = ptoneDriveFile.getOwnerNameList();

    if (ownerNameList != null && ownerNameList.size() > 0) {
      for (int i = 0; i < ownerNameList.size(); i++) {
        owner.append(ownerNameList.get(i));
        if (i < ownerNameList.size() - 1) {
          owner.append(",");
        }
      }
    }

    if (ptoneDriveFile.getShared()) {
      extra.put("sharingUserName", ptoneDriveFile.getSharingUserName());
      extra.put("sharedWithMeDate", ptoneDriveFile.getSharedWithMeDate());
    } else {
      extra.put("owner", owner.toString());
      extra.put("lastModifiedDate", ptoneDriveFile.getLastModifiedDate());
      extra.put("lastModifiedUserName", ptoneDriveFile.getLastModifiedUserName());
      extra.put("fileSize", ptoneDriveFile.getFileSize());
    }

    extra.put("isDirectory", ptoneDriveFile.isDirectory());

    metaContentNode.setExtra(extra);

    if (ptoneDriveFile.hasChild()) {
      metaContentNode.setLeaf(false);
      List<MetaContentNode> childNodeList = new ArrayList<>();
      metaContentNode.setChild(childNodeList);
      for (PtoneFile childPtoneFile : ptoneDriveFile.getChild()) {
        childNodeList.add(creatMetaContentNodeByPtoneFile((PtoneDriveFile) childPtoneFile));
      }
    } else {
      metaContentNode.setLeaf(true);
    }
    return metaContentNode;
  }

}
