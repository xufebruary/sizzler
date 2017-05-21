package com.sizzler.provider.common.util;

/**
 * Created by ptmind on 2015/10/22.
 */
public enum ColunmType {
  DIMENSION("DIMENSION"), METRIC("METRIC");
  private String name;

  ColunmType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
