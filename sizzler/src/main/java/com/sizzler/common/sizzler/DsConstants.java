package com.sizzler.common.sizzler;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据源相关常量定义
 */
public class DsConstants {

  // excel编辑器显示样例数据条数
  public static final Long EXCEL_EDITOR_ROW_LIMIT = 200L;
  public static final Long EXCEL_EDITOR_COLUMN_LIMIT = 50L;

  // 数据类型识别样本数据条数
  public static final Long DATA_TYPE_DETERMINE_ROW_LIMIT = 1000L; // bigquery限制10条，
                                                                  // 其他1000条

  // 数据源类型
  public static final String DS_TYPE_MODEL = "Model";
  // 标准化数据源类型
  public static final String DS_TYPE_STANDARDMODEL = "StandardModel";

  // 数据源id、code
  public static final long DS_ID_GA = 1;
  public static final String DS_CODE_GA = "googleanalysis";

  public static final long DS_ID_GOOGLEADWORDS = 3;
  public static final String DS_CODE_GOOGLEADWORDS = "googleadwords";

  public static final long DS_ID_UPLOAD = 4;
  public static final String DS_CODE_UPLOAD = "upload";

  public static final long DS_ID_MYSQL = 5;
  public static final String DS_CODE_MYSQL = "mysql";

  public static final long DS_ID_GOOGLEDRIVE = 6;
  public static final String DS_CODE_GOOGLEDRIVE = "googledrive";

  public static final long DS_ID_MYSQLAMAZONRDS = 7;
  public static final String DS_CODE_MYSQLAMAZONRDS = "mysqlAmazonRds";

  public static final long DS_ID_POSTGRE = 8;
  public static final String DS_CODE_POSTGRE = "postgre";

  public static final long DS_ID_REDSHIFT = 9;
  public static final String DS_CODE_REDSHIFT = "redshift";

  public static final long DS_ID_AURORAAMAZONRDS = 10;
  public static final String DS_CODE_AURORAAMAZONRDS = "auroraAmazonRds";

  public static final long DS_ID_FACEBOOK = 11;
  public static final String DS_CODE_FACEBOOK = "facebook";

  public static final long DS_ID_FACEBOOKAD = 12;
  public static final String DS_CODE_FACEBOOKAD = "facebookad";

  public static final long DS_ID_PTENGINE = 13;
  public static final String DS_CODE_PTENGINE = "ptengine";

  public static final long DS_ID_BIGQUERY = 14;
  public static final String DS_CODE_BIGQUERY = "bigquery";

  public static final long DS_ID_GOOGLELOGIN = 15;
  public static final String DS_CODE_GOOGLELOGIN = "googlelogin";

  public static final long DS_ID_LINKEDINLOGIN = 16;
  public static final String DS_CODE_LINKEDINLOGIN = "linkedinlogin";

  public static final long DS_ID_FACEBOOKLOGIN = 17;
  public static final String DS_CODE_FACEBOOKLOGIN = "facebooklogin";

  public static final long DS_ID_TWITTER = 18;
  public static final String DS_CODE_TWITTER = "twitter";

  public static final long DS_ID_SALESFORCE = 19;
  public static final String DS_CODE_SALESFORCE = "salesforce";

  public static final long DS_ID_S3 = 20;
  public static final String DS_CODE_S3 = "s3";

  public static final long DS_ID_DOUBLECLICK = 21;
  public static final String DS_CODE_DOUBLECLICK = "doubleclick";

  public static final long DS_ID_DOUBLECLICK_COMPOUND = 22;
  public static final String DS_CODE_DOUBLECLICK_COMPOUND = "doubleclickCompound";

  public static final long DS_ID_PAYPAL = 23;
  public static final String DS_CODE_PAYPAL = "paypal";

  public static final long DS_ID_STANDARDREDSHIFT = 26;
  public static final String DS_CODE_STANDARDREDSHIFT = "standardRedshift";

  public static final long DS_ID_STRIPE = 25;
  public static final String DS_CODE_STRIPE = "stripe";

  public static final long DS_ID_GOOGLEADSENSE = 28;
  public static final String DS_CODE_GOOGLEADSENSE = "googleadsense";

  public static final long DS_ID_MAILCHIMP = 27;
  public static final String DS_CODE_MAILCHIMP = "mailchimp";

  public static final long DS_ID_FACEBOOKPAGES = 29;
  public static final String DS_CODE_FACEBOOKPAGES = "facebookPages";

  public static final long DS_ID_YAHOOADSYDN = 30;
  public static final String DS_CODE_YAHOOADSYDN = "yahooAdsYDN";

  public static final long DS_ID_YAHOOADSSS = 31;
  public static final String DS_CODE_YAHOOADSSS = "yahooAdsSS";

  public static final long DS_ID_SQLSERVER = 32;
  public static final String DS_CODE_SQLSERVER = "sqlserver";

  public static final long DS_ID_PTAPP = 215;
  public static final String DS_CODE_PTAPP = "ptapp";

  public static final String DS_CODE_GOOGLESHEET = "googlesheet";

  public static final String DS_CODE_GOOGLESPREADSHEET = "googlespreadsheet";

  /**
   * api类型数据源列表
   */
  private static List<String> apiDsList = new ArrayList<String>();
  static {
    apiDsList.add(DS_CODE_GA);
    apiDsList.add(DS_CODE_GOOGLEADWORDS);
    apiDsList.add(DS_CODE_FACEBOOKAD);
    apiDsList.add(DS_CODE_PTENGINE);
    apiDsList.add(DS_CODE_PTAPP);
    apiDsList.add(DS_CODE_SALESFORCE);
    apiDsList.add(DS_CODE_DOUBLECLICK);
    apiDsList.add(DS_CODE_DOUBLECLICK_COMPOUND);
    apiDsList.add(DS_CODE_PAYPAL);
    apiDsList.add(DS_CODE_STRIPE);
    apiDsList.add(DS_CODE_GOOGLEADSENSE);
    apiDsList.add(DS_CODE_MAILCHIMP);
    apiDsList.add(DS_CODE_FACEBOOKPAGES);
    apiDsList.add(DS_CODE_YAHOOADSYDN);
    apiDsList.add(DS_CODE_YAHOOADSSS);
  }

  public static boolean isApiDs(String dsCode) {
    return apiDsList.contains(dsCode);
  }

  /**
   * 支持计算的api类型数据源列表
   */
  private static List<String> supportCalculateApiDsList = new ArrayList<String>();
  static {
    supportCalculateApiDsList.add(DS_CODE_SALESFORCE);
    supportCalculateApiDsList.add(DS_CODE_PAYPAL);
    supportCalculateApiDsList.add(DS_CODE_STRIPE);
    supportCalculateApiDsList.add(DS_CODE_MAILCHIMP);
  }

  public static boolean isSupportCalculateApiDs(String dsCode) {
    return isApiDs(dsCode) && supportCalculateApiDsList.contains(dsCode);
  }

  /**
   * 只包含历史数据的数据源列表（如gd、excel等数据存储在本地库中，不更新不会变化，都是历史数据）
   */
  private static List<String> onlyHistoryDataDsList = new ArrayList<String>();
  static {
    onlyHistoryDataDsList.add(DS_CODE_GOOGLEDRIVE);
    onlyHistoryDataDsList.add(DS_CODE_GOOGLESHEET);
    onlyHistoryDataDsList.add(DS_CODE_GOOGLESPREADSHEET);
    onlyHistoryDataDsList.add(DS_CODE_S3);
    onlyHistoryDataDsList.add(DS_CODE_UPLOAD);
  }

  /**
   * 是否是只包含历史数据的数据源（如gd、excel等数据存储在本地库中，不更新不会变化，都是历史数据）
   */
  public static boolean isOnlyHistoryDataDs(String dsCode) {
    return onlyHistoryDataDsList.contains(dsCode);
  }

  /**
   * 只包含实时数据的数据源列表（如mysql等关系型数据库，数据变化未知，无法区分历史和实时数据，都按实时数据处理）
   */
  private static List<String> onlyRealtimeDataDsList = new ArrayList<String>();
  static {
    onlyRealtimeDataDsList.add(DS_CODE_AURORAAMAZONRDS);
    onlyRealtimeDataDsList.add(DS_CODE_BIGQUERY);
    onlyRealtimeDataDsList.add(DS_CODE_MYSQL);
    onlyRealtimeDataDsList.add(DS_CODE_MYSQLAMAZONRDS);
    onlyRealtimeDataDsList.add(DS_CODE_POSTGRE);
    onlyRealtimeDataDsList.add(DS_CODE_REDSHIFT);
    onlyRealtimeDataDsList.add(DS_CODE_SQLSERVER);
    onlyRealtimeDataDsList.add(DS_CODE_STANDARDREDSHIFT);
  }

  /**
   * 是否是只包含实时数据的数据源（如mysql等关系型数据库，数据变化未知，无法区分历史和实时数据，都按实时数据处理）
   */
  public static boolean isOnlyRealtimeDataDs(String dsCode) {
    return onlyRealtimeDataDsList.contains(dsCode);
  }

  /**
   * 有widget默认选择维度的数据源列表
   */
  private static List<Long> hasDefaultWidgetDimensionsDsList = new ArrayList<Long>();
  static {
    hasDefaultWidgetDimensionsDsList.add(DS_ID_YAHOOADSSS);
  }

  /**
   * 是否有widget默认选择维度的数据源
   */
  public static boolean isHasDefaultWidgetDimensionsDs(long dsId) {
    return hasDefaultWidgetDimensionsDsList.contains(dsId);
  }

  /**
   * 从远程获取指标、维度列表的数据源列表
   */
  private static List<Long> metricsDimensionFormRemoteDsList = new ArrayList<Long>();
  static {
    metricsDimensionFormRemoteDsList.add(DS_ID_SALESFORCE);
  }

  /**
   * 是否从远程获取指标、维度列表的数据源
   */
  public static boolean isGetMetricsDimensionFormRemoteDs(long dsId) {
    return metricsDimensionFormRemoteDsList.contains(dsId);
  }

  /**
   * 从指标、维度字典表获取指标、维度列表的数据源列表
   */
  private static List<Long> metricsDimensionFormDictDsList = new ArrayList<Long>();
  static {
    metricsDimensionFormDictDsList.add(DS_ID_GA);
    metricsDimensionFormDictDsList.add(DS_ID_GOOGLEADWORDS);
    metricsDimensionFormDictDsList.add(DS_ID_FACEBOOK);
    metricsDimensionFormDictDsList.add(DS_ID_FACEBOOKAD);
    metricsDimensionFormDictDsList.add(DS_ID_PTENGINE);
    metricsDimensionFormDictDsList.add(DS_ID_TWITTER);
    metricsDimensionFormDictDsList.add(DS_ID_DOUBLECLICK);
    metricsDimensionFormDictDsList.add(DS_ID_DOUBLECLICK_COMPOUND);
    metricsDimensionFormDictDsList.add(DS_ID_PAYPAL);
    metricsDimensionFormDictDsList.add(DS_ID_STRIPE);
    metricsDimensionFormDictDsList.add(DS_ID_GOOGLEADSENSE);
    metricsDimensionFormDictDsList.add(DS_ID_MAILCHIMP);
    metricsDimensionFormDictDsList.add(DS_ID_FACEBOOKPAGES);
    metricsDimensionFormDictDsList.add(DS_ID_YAHOOADSYDN);
    metricsDimensionFormDictDsList.add(DS_ID_YAHOOADSSS);
    metricsDimensionFormDictDsList.add(DS_ID_PTAPP);
  }

  /**
   * 是否从指标、维度字典表获取指标、维度列表的数据源
   */
  public static boolean isGetMetricsDimensionFormDictDs(long dsId) {
    return metricsDimensionFormDictDsList.contains(dsId);
  }

  /**
   * 从指标、维度用户设置的column表获取指标、维度列表的数据源列表
   */
  private static List<Long> metricsDimensionFormUserColumnDsList = new ArrayList<Long>();
  static {
    metricsDimensionFormUserColumnDsList.add(DS_ID_UPLOAD);
    metricsDimensionFormUserColumnDsList.add(DS_ID_MYSQL);
    metricsDimensionFormUserColumnDsList.add(DS_ID_GOOGLEDRIVE);
    metricsDimensionFormUserColumnDsList.add(DS_ID_MYSQLAMAZONRDS);
    metricsDimensionFormUserColumnDsList.add(DS_ID_POSTGRE);
    metricsDimensionFormUserColumnDsList.add(DS_ID_REDSHIFT);
    metricsDimensionFormUserColumnDsList.add(DS_ID_AURORAAMAZONRDS);
    metricsDimensionFormUserColumnDsList.add(DS_ID_BIGQUERY);
    metricsDimensionFormUserColumnDsList.add(DS_ID_S3);
    metricsDimensionFormUserColumnDsList.add(DS_ID_STANDARDREDSHIFT);
    metricsDimensionFormUserColumnDsList.add(DS_ID_SQLSERVER);
  }

  /**
   * 是否从用户设置的column表获取指标、维度列表的数据源
   */
  public static boolean isGetMetricsDimensionFormUserColumnDs(long dsId) {
    return metricsDimensionFormUserColumnDsList.contains(dsId);
  }

}
