package com.sizzler.provider.common.impl;

import com.sizzler.provider.common.UpdateDataResponse;

/**
 * Created by ptmind on 2015/12/26.
 */
public class DefaultUpdateDataResponse implements UpdateDataResponse {
  private boolean updateStatus;
  private boolean changed;
  private boolean deleted;
  private long lastModifiedDate;
  private boolean disconnected;


  @Override
  public boolean getUpdateStatus() {
    return false;
  }

  public void setUpdateStatus(boolean updateStatus) {
    this.updateStatus = updateStatus;
  }

  public void setChanged(boolean changed) {
    this.changed = changed;
  }

  @Override
  public boolean hasChanged() {
    return changed;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  @Override
  public boolean hasDeleted() {
    return deleted;
  }

  @Override
  public boolean hasDisconnected() {
    return disconnected;
  }

  public void setDisconnected(boolean disconnected) {
    this.disconnected = disconnected;
  }

  @Override
  public long getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(long lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }
}
