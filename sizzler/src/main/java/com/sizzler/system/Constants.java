package com.sizzler.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;

public class Constants {

  public static class OperateLog {

    public static final String DATA_QUERY = "data_query";
    // 该字段用于标识是否查询缓存
    public static final String QUERY_CACHE = "query_cache";
    public static final String GA_DATA_QUERY = "ga_data_query";
    public static final String GOOGLEADWORDS_DATA_QUERY = "googleadwords_data_query";
    public static final String MODEL_DATA_QUERY = "model_data_query";

    public static class OperateLogContent {
      public static final String USER_EMAIL = "user_email";
      public static final String PANEL_ID = "panel_id";
      public static final String DS_ID = "ds_id";
      public static final String DS_NAME = "ds_name";
      public static final String WIDGET_ID = "widget_id";
      public static final String DATE_KEY = "date_key";
      public static final String START_DATE = "start_date";
      public static final String END_DATE = "end_date";
      public static final String CLIENT_ID = "client_id";

    }

    public static class GaOperateLogContent extends OperateLogContent {
      public static final String ACCOUNT = "account";
      public static final String PROFILE = "profile";
    }

  }

  @PostConstruct
  public void init() {
    Elastic.ANALYZER_MAPPING.put("cn", "ik_max_word");
    Elastic.ANALYZER_MAPPING.put("en", "english");
    Elastic.ANALYZER_MAPPING.put("jp", "kuromoji");
    Elastic.ANALYZER_MAPPING.put("pinyin_cn", "pinyin");
    Elastic.ANALYZER_MAPPING.put("romaji_jp", "romaji_analyzer");
    Elastic.ANALYZER_MAPPING.put("katakana_jp", "katakana_analyzer");
    Elastic.ANALYZER_MAPPING.put("ngram", "ngram_analyzer");
  }

  public static class Elastic {
    public static Map<String, String> ANALYZER_MAPPING = new ConcurrentHashMap<>();
    public static final String FILED_ORIGINAL = "original"; // 字段原始数据不分词
    public static final String DATADECK_PANEL_TYPE = "ptone_panel_info";
    public static final String PANEL_SEARCH_FIELDS[] = new String[] { "panel_title", "description" };
  }

  /**
   * 判断用户是否来源于日本市场
   * 
   * @param userSource
   * @author zhangli
   * @return
   */
  public static boolean sourceFromJapan(String userSource) {
    if (userSource.contains("-jp-")) {
      return true;
    } else
      return false;
  }

  /**
   * 判断用户是否内部注册
   * 
   * @param userSource
   * @author zhangli
   * @return
   */
  public static boolean sourceIsInternal(String userSource) {
    if (userSource.contains("internal")) {
      return true;
    } else
      return false;
  }

  public static String buildTimeStamp;

  public static String middleVersionStatus;

  public static String buildGaClient;

  public static String providerStatus;

  public static String hdfsClusterPath;

  public static String ptoneAdminEmail;

  public static String middlePropertiesName;

  public static String productDomain;

  public static String gdRefreshJobSwitch;
  public static String gdRefreshJobIp;
  public static int refreshJobThreadCount;

  // 本地部署方式
  public static String LOCAL_DEPLOY_TYPE = "localDeploy";
  // 现有产品部署方式
  public static String PRODUCT_DEPLOY_TYPE = "product";

  /**
   * salesforce远程获取的指标维度列表缓存7天
   */
  public static final int SALESFORCE_REMOTE_METRICS_DIMENSION_LIST_CACHE_7_DAY = 7 * 24 * 60 * 60;
  /**
   * salesforce远程获取的指标维度列表缓存1天
   */
  public static final int SALESFORCE_REMOTE_METRICS_DIMENSION_LIST_CACHE_1_DAY = 1 * 24 * 60 * 60;

  public static final String defaultPublished = "3";// 默认预制的发布模板
  public static final String published = "2"; // 发布的模板
  public static final String validate = "1";
  public static final String inValidate = "0";
  public static final String inReady = "-1"; // 在准备中，目前提供给FileController在创建UserConnection时使用

  public static final int validateInt = 1;
  public static final int inValidateInt = 0;

  public static final String STR_ALL = "all"; // 用于标记获取全部列表

  public static final String managerAccess = "1"; // 管理员权限

  public static final String WIDGET_TEMPLET_OF_PANEL_ID = "PTONE_WIDGET_TEMPLET_OF_PANEL_ID";

  public static final String CURRENT_UID = "UID";
  public static final String Current_Ptone_User = "ptOneUser";
  public static final String Current_Ptone_Anonymous = "ptOneAnonymous";
  public static final String PT_USERNAME = "ptOneUserEmail";
  public static final String PT_PASSWORD = "ptOnePassword";
  public static final String PT_ACCESS_TOKEN = "accessToken";
  public static final String PT_LOGIN_USER_ID = "loginUserId";
  public static final String PT_LOGIN_USER_EMAIL = "loginUserEmail";

  public static final String OFFICIAL_USER = "OFFICIAL_USER";
  public static final String PRE_REGISTRATION_USER = "PRE_REGISTRATION_USER";

  public final static String API_VERSION_PERFIX = "v";
  public final static int API_VERSION_1 = 1;

  public static final String LOGIN_URL = "/pt/users/goSignin";
  public static final String LOGIN_URL_GA = "/pt/users/ga/signin";
  public static final String LOGOUT_VERIFY = "/pt/users/signin";
  public static final String PTENGINE_LOGIN_VERIFY = "/pt/ptengine/auth";
  public static final String PTENGINE_AUTH = "/pt/ptengine/authConfirm";
  public static final String LOGOUT_URL = "/pt/users/signout";
  public static final String REGISTER = "/pt/users/signup";
  public static final String CHECK_EMAIL = "/pt/users/exists";
  public static final String DATADECK_VERIFY_RESET_PASSWORD_REQUEST = "/pt/users/verifyResetPasswordRequest";
  public static final String SEND_EMAIL_PASSWORD = "/pt/users/send";
  public static final String GET_PASSWORD = "/pt/users/getPassword";
  public static final String UPDATE_PASSWORD = "/pt/users/password/reset";
  public static final String UPDATE_FORWARD_COUNT = "/pt/users/updateForwardCount";
  public static final String EXCEL_FILE_UPLOAD = "/pt/file/excelFileUpload";
  public static final String EXCEL_FILE_UPDATE = "/pt/file/excelFileUpdate";
  public static final String IMG_UPLOAD = "/pt/file/imgUpload";
  public static final String GET_ACCESS_TOKEN_VERIFY = "/pt/users/getAccessToken";
  public static final String SHARE_SIGNIN_VERIFY = "/pt/users/shareSignin";
  public static final String SHARE_USER_INFO = "/pt/users/shareUserInfo/";
  public static final String SHARE_GET_PANEL = "/pt/panels/getPanel/";
  public static final String SHARE_GET_WIDGETS = "/pt/widgets/widget/";
  public static final String SHARE_GET_WIDGETS_WITH_LAYOUT = "/pt/widgets/widgetWithLayout/";
  public static final String SHARE_GET_WIDGET_DATA = "/pt/data/widgetData/";
  public static final String SHARE_GET_BATCH_WIDGET_DATA = "/pt/data/batchWidgetData/";
  public static final String SHARE_GET_WIDGET_BY_ID = "/pt/widgets/getOne/";
  public static final String PTENGINE_HEATMAP_DATA = "/pt/ptengine/heatmap/data";
  public static final String SPACE_INVITE = "/pt/space/invite/";
  public static final String SPACE_CHECK_INVITE_URL = "/pt/space/checkInviteUrl/";
  public static final String SPACE_ACCEPT_INVITE = "/pt/space/acceptInvite/";
  public static final String SPACE_CHECK_DOMAIN = "/pt/space/checkDomain/";
  public static final String SIGNUP_USER_BY_PTENGINE = "/pt/users/pte/";
  public static final String PANELS_SHARE_VERIFICATION = "/pt/panels/share/verification";

  public static final String API_PANELS_SHARE_VERIFICATION = "POST|/api/" + API_VERSION_PERFIX
      + API_VERSION_1 + "/panels/share/verification";
  public static final String API_GET_PANEL = "GET|/api/" + API_VERSION_PERFIX + API_VERSION_1
      + "/panels";
  public static final String API_SEND_PASSWORD_EMAIL = "POST|/api/" + API_VERSION_PERFIX
      + API_VERSION_1 + "/users/forgot";
  public static final String API_VALIDATE_FORGOT_PASSWORD = "GET|/api/" + API_VERSION_PERFIX
      + API_VERSION_1 + "/users/forgot/validate";
  public static final String API_REPEAT_SEND_ACTIVE_USER_EMAIL = "POST|/api/" + API_VERSION_PERFIX
      + API_VERSION_1 + "/users/active/repeat";
  public static final String API_ACTIVE_NEW_USER = "PUT|/api/" + API_VERSION_PERFIX + API_VERSION_1
      + "/users/active";
  public static final String API_VALIDATE_RESET_PASSWORD = "GET|/api/" + API_VERSION_PERFIX
      + API_VERSION_1 + "/users/password/reset/validate";

  public static final String UI_OPERATE_SAVE = "save";
  public static final String UI_OPERATE_EDIT_SAVE = "edit_save";
  public static final String UI_OPERATE_UPDATE = "update";
  public static final String UI_OPERATE_AUTO_UPDATE = "auto_update";

  /** beforeSaveOrUpdateEditorDataToFile acceptTable */
  public static final String ACCEPT_TABLE_KEY = "acceptTable";
  /** beforeSaveOrUpdateEditorDataToFile userConnection */
  public static final String USER_CONNECTION_KEY = "userConnection";
  /** beforeSaveOrUpdateEditorDataToFile userConnectionSource */
  public static final String USER_CONNECTION_SOURCE_KEY = "userConnectionSource";
  /** beforeSaveOrUpdateEditorDataToFile schemaMap */
  public static final String SCHEMA_MAP_KEY = "schemaMap";

  // test使用的controller
  public static final String TEST = "/pt/test";

  // discourse使用的controller
  public static final String DISCOURSE = "/pt/discourse";

  public static final String INDEX = "/pt/index";

  public static final Integer REDIS_SESSION_TIME = 7200;

  public static final String PT_USER_ID = "userID";

  // request body

  public static final String REQUEST_BODY = "requestBody";

  // request中用于标识用户操作日志的id
  public static final String OPERATE_LOG = "operateLog";

  // 状态标识
  public static final String SUCCESS_TAG = "1";
  public static final String ERROR_TAG = "0";
  // 日志字段分隔符
  public final static String FIELD_SPLIT = "\001";
  // 字段内的一级分隔符
  public final static String ITEM_SPLIT = "\002";
  // 字段内的二级分隔符
  public final static String KEY_VALUE_SPLIT = "\003";

  // 用户操作code
  // 1、登录&登出相关的以0开头
  public static final String SIGN_IN_CODE = "0001";
  public static final String SIGN_OUT_CODE = "0001";

  // 2、Panel相关的以1开头
  public static final String PANEL_ADD = "1001";
  public static final String PANEL_UPDATE = "1002";
  public static final String PANEL_DELETE = "1003";
  public static final String PANEL_COPY = "1004";

  // 前台widget编辑器向后台传递参数名称
  // public static final String PARAM_IS_IN_EDITOR = "isInEditor"; // 是否在编辑器环境
  // public static final String PARAM_IS_TEMPLET = "isTemplet"; // 是否为widget模板
  public static final String PARAM_IS_EXAMPLE = "isExample"; // 是否为展示demo数据
  public static final String PARAM_TEMPLET_ID = "templetId"; // 是否为widget模板
  public static final String PARAM_SHOW_MULTI_Y = "showMultiY"; // 是否开启双轴
  public static final String PARAM_Y2_GRAPH = "y2Graph"; // y2轴图形
  public static final String PARAM_Y_USED_LIST = "yUsedList"; // Y1、Y2使用的列表（都使用了哪个轴，判断只使用一个轴情况）
  public static final String PARAM_JUDGE_MULTI_Y = "judgeMultiY"; // 是否根据双轴判断（开启双轴、且轴图形不同）
  public static final String PARAM_WIDGET_INFO = "widget"; // 接收前台的AcceptWidget的json串
  public static final String PARAM_WIDGET_ID = "widgetId";
  public static final String PARAM_DATE_KEY = "dateKey"; // 时间范围
  public static final String PARAM_DATE_PERIOD = "datePeriod"; // 时间颗粒度
  public static final String PARAM_VARIABLE_ID = "variableId";
  public static final String PARAM_DS_ID = "dsId"; // 数据源ID
  public static final String PARAM_GRAPH_ID = "graphId"; // 图形类型ID
  public static final String PARAM_TARGET_VALUE = "targetValue"; // 目标值
  public static final String PARAM_ACCOUNT_NAME = "accountName"; // 授权账号
  public static final String PARAM_PROFILE = "profile";
  public static final String PARAM_METRICS_ID = "metricsId"; // 值为逗号分隔的指标列表
  public static final String PARAM_DIMENSIONS_ID = "dimensionsId"; // 值为逗号分隔的维度列表
  public static final String PARAM_FILETERS = "fileters";
  public static final String PARAM_SORT = "sort";
  public static final String PARAM_NO_CACHE = "no-cache"; // 不走缓存

  // public static final String PARAM_SEGMENT = "segment";
  // public static final String PARAM_METRICS_DATA = "metricsData";
  // public static final String PARAM_DS_WIDGET_INFO = "dsWidgetInfo"; //
  // 对应数据源的独有widget信息，如：gaWidgetInfo

  public static final String JSON_VIEW_STATUS_SUCCESS = "success";
  public static final String JSON_VIEW_STATUS_FAILED = "failed";
  public static final String JSON_VIEW_STATUS_ERROR = "error";

  public static final String CUSTOM_PUSH_KEY_PREFIX = "push_key:";
  public static final String WEB_SOCKET_KEY_PREFIX = "web_socket_key:";
  public static final String WEB_SOCKET_IP_POOL = "web_socket_ip_pool:";

  public static String clientId;
  public static String clientSecret;
  public static String webMiddleUrl;
  public static String webUIUrl;
  public static String webUICookie;
  public static String webSocketVersion;
  public static String elasticDatadeckIndex;
  public static List GA_CLIENT_AUTH_LIST = new ArrayList();

  public static Map<String, List> PROVIDER_INFO_MAP = new HashMap<>();
  public static Map<String, Long> PROVIDER_COUNT_MAP = new ConcurrentHashMap<>();
  public static boolean SOCIAL_CONFIG_START = false;

  public static final String COMMON_DATE_FORMAT = "yyyy-MM-dd";
  public static final String X_AXIS_DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";

  public static final String COMMON_START_DATETIME_FORMAT = "yyyy-MM-dd 00:00:00";
  public static final String COMMON_END_DATETIME_FORMAT = "yyyy-MM-dd 23:59:59";
  public static final String COMMON_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String COMMON_TIME_FORMAT = "HH:mm:ss";

  public static final String GA_DIMENSIONS_DATE = "ga:date";
  public static final String GA_DIMENSIONS_YEAR = "ga:year";
  public static final String GA_DIMENSIONS_ISOYEAR = "ga:isoYear";
  public static final String GA_DIMENSIONS_MONTH = "ga:month";
  public static final String GA_DIMENSIONS_WEEK = "ga:week"; // 周日为周起始日
  public static final String GA_DIMENSIONS_ISOWEEK = "ga:isoWeek"; // 周一为周起始日
  public static final String GA_DIMENSIONS_DAY = "ga:day";
  public static final String GA_DIMENSIONS_HOUR = "ga:hour";
  public static final String GA_DIMENSIONS_MINUTE = "ga:minute";
  public static final String[] GA_TIME_DIMENSIONS_ARRAY = { Constants.GA_DIMENSIONS_DATE,
      Constants.GA_DIMENSIONS_YEAR, Constants.GA_DIMENSIONS_ISOYEAR, Constants.GA_DIMENSIONS_MONTH,
      Constants.GA_DIMENSIONS_WEEK, Constants.GA_DIMENSIONS_ISOWEEK, Constants.GA_DIMENSIONS_DAY,
      Constants.GA_DIMENSIONS_HOUR, Constants.GA_DIMENSIONS_MINUTE };

  public static final String PTCONSOLE_DIMENSIONS_DAY = "day";
  public static final String PTCONSOLE_DIMENSIONS_WEEK = "week";
  public static final String PTCONSOLE_DIMENSIONS_MONTH = "month";

  public static String collectServerUrl;

  public static String GOOGLEADWORDS_DEVELOPER_TOKEN = "PUpM0DXGPRr13E0gR1n9fA";

  private static String webappPath;
  private static WebApplicationContext applicationContext;
  private static ServletContext servletContext;

  public static ServletContext getServletContext() {
    return servletContext;
  }

  public static void setServletContext(ServletContext servletContext) {
    Constants.servletContext = servletContext;
  }

  public static void setApplicationContext(WebApplicationContext context) {
    applicationContext = context;
  }

  public static WebApplicationContext getApplicationContext() {
    return applicationContext;
  }

  /**
   * @return 应用程序根目录
   */
  public static String getWebappPath() {
    return webappPath;
  }

  /**
   * 初始化应用程序根目录
   * 
   * @param pWebappPath
   *          the webappPath to set
   */
  public static void setWebappPath(String pWebappPath) {
    webappPath = pWebappPath;
  }

  public void setBuildTimeStamp(String buildTimeStamp) {
    Constants.buildTimeStamp = buildTimeStamp;
  }

  public void setClientId(String clientId) {
    Constants.clientId = clientId;
  }

  public void setClientSecret(String clientSecret) {
    Constants.clientSecret = clientSecret;
  }

  public void setBuildGaClient(String buildGaClient) {
    Constants.buildGaClient = buildGaClient;
  }

  public void setWebMiddleUrl(String webMiddleUrl) {
    Constants.webMiddleUrl = webMiddleUrl;
  }

  public void setWebUIUrl(String webUIUrl) {
    Constants.webUIUrl = webUIUrl;
  }

  public void setWebUICookie(String webUICookie) {
    Constants.webUICookie = webUICookie;
  }

  public void setProviderStatus(String providerStatus) {
    Constants.providerStatus = providerStatus;
  }

  public void setHdfsClusterPath(String hdfsClusterPath) {
    Constants.hdfsClusterPath = hdfsClusterPath;
  }

  public static String getCollectServerUrl() {
    return collectServerUrl;
  }

  public void setCollectServerUrl(String collectServerUrl) {
    Constants.collectServerUrl = collectServerUrl;
  }

  public static String buildHdfsDataPath(String uid, String dsCode, String fileId) {
    return Constants.hdfsClusterPath + "/ptone/" + uid + "/" + dsCode + "/" + fileId + "_data.xls";
  }

  public void setPtoneAdminEmail(String ptoneAdminEmail) {
    Constants.ptoneAdminEmail = ptoneAdminEmail;
  }

  public void setMiddlePropertiesName(String middlePropertiesName) {
    Constants.middlePropertiesName = middlePropertiesName;
  }

  public void setMiddleVersionStatus(String middleVersionStatus) {
    Constants.middleVersionStatus = middleVersionStatus;
  }

  public void setWebSocketVersion(String webSocketVersion) {
    Constants.webSocketVersion = webSocketVersion;
  }

  public String getProductDomain() {
    return productDomain;
  }

  public void setProductDomain(String productDomain) {
    Constants.productDomain = productDomain;
  }

  public void setGdRefreshJobIp(String gdRefreshJobIp) {
    Constants.gdRefreshJobIp = gdRefreshJobIp;
  }

  public void setGdRefreshJobSwitch(String gdRefreshJobSwitch) {
    Constants.gdRefreshJobSwitch = gdRefreshJobSwitch;
  }

  public void setRefreshJobThreadCount(int refreshJobThreadCount) {
    Constants.refreshJobThreadCount = refreshJobThreadCount;
  }

  public void setElasticDatadeckIndex(String elasticDatadeckIndex) {
    Constants.elasticDatadeckIndex = elasticDatadeckIndex;
  }

  /**
   * ftp服务相关配置
   */
  public static final String FTP_IMAGES_PATH = "images";
  public static String ftpServerDomain;

  public String getFtpServerDomain() {
    return ftpServerDomain;
  }

  public void setFtpServerDomain(String ftpServerDomain) {
    Constants.ftpServerDomain = ftpServerDomain;
  }

}
