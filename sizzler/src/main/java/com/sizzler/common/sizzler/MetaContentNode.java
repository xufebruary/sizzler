package com.sizzler.common.sizzler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetaContentNode implements Serializable {

  private static final long serialVersionUID = 7964676898813537708L;

  private String id;
  private String name;
  private String code;
  private String type;
  private String description;
  private boolean isLeaf;
  private MetaContentNode parent = null;
  private List<MetaContentNode> child = new ArrayList<>();
  private Map<String, Object> extra;

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

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isLeaf() {
    return isLeaf;
  }

  public void setLeaf(boolean isLeaf) {
    this.isLeaf = isLeaf;
  }

  public MetaContentNode getParent() {
    return parent;
  }

  public void setParent(MetaContentNode parent) {
    this.parent = parent;
  }

  public List<MetaContentNode> getChild() {
    return child;
  }

  public void setChild(List<MetaContentNode> child) {
    this.child = child;
  }

  public Map<String, Object> getExtra() {
    return extra;
  }

  public void setExtra(Map<String, Object> extra) {
    this.extra = extra;
  }

}
