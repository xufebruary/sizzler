package com.sizzler.provider.common.impl;

import java.util.LinkedHashMap;
import java.util.List;

import com.sizzler.provider.common.EditorDataResponse;

/**
 * Created by ptmind on 2015/12/23.
 */
public class DefaultEditorDataResponse implements EditorDataResponse {

  private boolean changed;
  private boolean deleted;
  private long lastModifiedDate;
  private boolean disconnected;

  private LinkedHashMap<String, List<List>> editorData = new LinkedHashMap<>();

  public LinkedHashMap<String, List<List>> getEditorData() {
    return editorData;
  }

  public void setEditorData(LinkedHashMap<String, List<List>> editorData) {
    this.editorData = editorData;
  }
  
  @Override
  public boolean hasDisconnected() {
    return disconnected;
  }

  public void setDisconnected(boolean disconnected) {
    this.disconnected = disconnected;
  }

  @Override
  public boolean hasChanged() {
    return changed;
  }

  @Override
  public boolean hasDeleted() {
    return deleted;
  }

  @Override
  public long getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setChanged(boolean changed) {
    this.changed = changed;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public void setLastModifiedDate(long lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

}
