package com.sizzler.common.extension;

@SPI
public interface ExtensionFactory {
  /*
   * 取得类型为type，名称为name的对象
   */
  public <T> T getExtension(Class<T> type, String name);
}
