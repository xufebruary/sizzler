package com.sizzler.provider.common.file;

import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.Schema;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ptmind on 2015/10/27.
 */
public class PtoneFile implements Serializable, Cloneable {
  protected String id;
  protected String name;
  protected String folderId;
  protected boolean isDirectory;
  protected PtoneFile parent;
  protected List<PtoneFile> child;
  protected String mimeType;
  protected boolean isEmpty;
  protected List<String> ownerList;
  protected List<String> ownerNameList;
  protected Long lastModifiedDate;
  protected String lastModifiedUser;
  protected String lastModifiedUserName;
  protected Long fileSize;
  /*
   * protected Long rows; protected Long cols;
   */

  /*
   * 文件的schema结构信息： (1)比如excel格式的文件，每个sheet当作table来进行对待 (2)比如csv格式的文件，整个文件当作一个table来对待
   */
  protected MutableSchema schema;
  /*
   * key为table名称，value为对应table所包含的数据行列表（注意此处没有返回DataSet，因为DataSet不是Serializable的）
   */
  protected LinkedHashMap<String, List<Row>> fileDataMap = new LinkedHashMap<>();

  protected LinkedHashMap<String, List<List>> fileListDataMap;

  private boolean updateFileListDataMap = false;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  public boolean isDirectory() {
    return isDirectory;
  }

  public void setDirectory(boolean isDirectory) {
    this.isDirectory = isDirectory;
  }

  public List<PtoneFile> getChild() {
    return child;
  }

  public void setChild(List<PtoneFile> child) {
    this.child = child;
  }

  public void addChild(PtoneFile file) {
    if (this.child == null) {
      this.child = new ArrayList<PtoneFile>();
    }
    this.child.add(file);
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public PtoneFile getParent() {
    return parent;
  }

  public void setParent(PtoneFile parent) {
    this.parent = parent;
  }

  public MutableSchema getSchema() {
    return schema;
  }

  public void setSchema(MutableSchema schema) {
    this.schema = schema;
  }

  public LinkedHashMap<String, List<Row>> getFileDataMap() {
    return fileDataMap;
  }

  public void setFileDataMap(LinkedHashMap<String, List<Row>> fileDataMap) {
    this.fileDataMap = fileDataMap;
    updateFileListDataMap = true;
    this.fileListDataMap = getFileListDataMap();
  }

  public boolean isEmpty() {
    return isEmpty;
  }

  public void setEmpty(boolean isEmpty) {
    this.isEmpty = isEmpty;
  }



  public Long getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Long lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public String getLastModifiedUser() {
    return lastModifiedUser;
  }

  public void setLastModifiedUser(String lastModifiedUser) {
    this.lastModifiedUser = lastModifiedUser;
  }

  public Long getFileSize() {
    return fileSize;
  }

  public void setFileSize(Long fileSize) {
    this.fileSize = fileSize;
  }

  public List<String> getOwnerList() {
    return ownerList;
  }

  public void setOwnerList(List<String> ownerList) {
    this.ownerList = ownerList;
  }

  public List<String> getOwnerNameList() {
    return ownerNameList;
  }

  public void setOwnerNameList(List<String> ownerNameList) {
    this.ownerNameList = ownerNameList;
  }

  public String getLastModifiedUserName() {
    return lastModifiedUserName;
  }

  public void setLastModifiedUserName(String lastModifiedUserName) {
    this.lastModifiedUserName = lastModifiedUserName;
  }

  public LinkedHashMap<String, List<List>> getFileListDataMap() {
    if (fileListDataMap != null && !updateFileListDataMap) {
      return fileListDataMap;
    } else {
      // 根据fileDataMap 来创建 fileListDataMap
      fileListDataMap = new LinkedHashMap<String, List<List>>();
      if (fileDataMap != null) {
        for (Map.Entry<String, List<Row>> fileDataEntry : fileDataMap.entrySet()) {
          String key = fileDataEntry.getKey();
          List<Row> rowList = fileDataEntry.getValue();
          List<List> newRowList = new ArrayList<>();

          for (Row row : rowList) {
            newRowList.add(Arrays.asList(row.getValues()));
          }
          fileListDataMap.put(key, newRowList);
        }
      }
    }


    return fileListDataMap;
  }

  public void setFileListDataMap(LinkedHashMap<String, List<List>> fileListDataMap) {
    this.fileListDataMap = fileListDataMap;
    // fileListDataMap 同时更新

  }

  public boolean hasChild() {
    if (isDirectory) {
      if (child != null && child.size() > 0) {
        return true;
      }
    }

    return false;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public String toString() {
    return "PtoneFile{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", isDirectory="
        + isDirectory + ", parent=" + parent + ", mimeType='" + mimeType + '\'' + '}';
  }


}
