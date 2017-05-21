package com.sizzler.common;

/**
 * Panel来源类型枚举
 * 
 * @date: 2016年12月21日
 * @author peng.xu
 */
public class SourceType {


  public static class Panel {
    /**
     * 手动创建panel
     */
    public final static String USER_CREATED = "USER_CREATED";
    /**
     * 预制Panel模板创建panel
     */
    public final static String DEFAULT_TEMPLET = "DEFAULT_TEMPLET";
    /**
     * Panel模板创建panel
     */
    public final static String PANEL_TEMPLET = "PANEL_TEMPLET";
  }


  public static class Widget {
    /**
     * 手动创建widget
     */
    public final static String USER_CREATED = "USER_CREATED";
    /**
     * 预制Panel模板创建widget
     */
    public final static String DEFAULT_TEMPLET = "DEFAULT_TEMPLET";
    /**
     * Panel模板创建widget
     */
    public final static String PANEL_TEMPLET = "PANEL_TEMPLET";
    /**
     * WidgetGallery板创建widget
     */
    public final static String WIDGET_GALLERY = "WIDGET_GALLERY";
  }



  public static class UserConnection {
    /**
     * 手动创建
     */
    public final static String USER_CREATED = "USER_CREATED";
    /**
     * 系统默认预制
     */
    public final static String SYSTEM_DEFAULT = "SYSTEM_DEFAULT";
  }

}
