package com.sizzler.provider.common.file;

/**
 * Created by ptmind on 2015/10/27.
 */
public enum PtoneFileMimeType {
  EXCEL("EXCEL"), CSV("CVS");
  private String name;

  PtoneFileMimeType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
