package com.sizzler.system;

public class OpreateConstants {

	public final static String USER_EMAIL = "userEmail";
	public final static String EXECUTE_TIME = "executeTime";

	public static class BusinessDomain{
		public final static String USER = "user";
		public final static String PANEL = "panel";
		public final static String PANEL_TEMPLET = "panelTemplet";
		public final static String WIDGET = "widget";
		public final static String SPACE = "space";
		public final static String DATASOURCE = "dataSource";
		public final static String PUBLIC = "public";
	}

	public static class User{
		public final static String SIGNUP = "signup";
		public final static String SIGNIN = "signin";
		public final static String SIGNOUT = "signout";
	}

	public static class Panel{
		//取panel list 和位置信息及相关
		public static String GET_LIST = "getPanelList";
		public final static String ADD_PANEL = "addPanel";
		public final static String API_ADD_PANEL = "apiAddPanel";
        public final static String ADD_PANEL_FOLDER = "addPanelFolder";
        public final static String UPDATE_PANEL = "updatePanel";
        public final static String API_UPDATE_PANEL = "apiUpdatePanel";
        public final static String UPDATE_PANEL_FOLDER = "updatePanelFolder";
        public final static String COPY_PANEL = "copyPanel";
        public final static String API_COPY_PANEL = "apiCopyPanel";
        public final static String API_ADD_PANEL_BY_TEMPLET = "apiAddPanelByTemplet";
        public static final String API_ADD_SHARE_PANEL = "apiAddSharePanel";
        public final static String DEL_PANEL = "delPanel";
        public final static String DEL_PANEL_FOLDER = "delPanelFolder";
        public final static String SHARE_PANEL = "sharePanel";
        public static final String API_UPDATE_PANEL_LAYOUT = "apiUpdatePanelLayout";
        public static final String API_SHARE_PANEL_VERIFY_PASSWORD = "apiSharePanelVerifyPassword";
        public static final String API_GET_BASE_PANEL_INFO = "apiGetBasePanelInfo";
        public static final String API_GET_PANEL_INFO = "apiGetPanelInfo";
        public static final String API_VALIDATE_SHARE_PANEL_EXISTS = "apiValidateSharePanelExists";
        public static final String API_APPLY_PANEL_COMPONENT = "apiApplyPanelComponent";
        public static final String API_CANCEL_PANEL_COMPONENT = "apiCancelPanelComponent";
	}
	
	public static class PanelTemplet {
	  public final static String UPDATE_PANEL_TEMPLET = "updatePanelTemplet";
	  public final static String GET_PANEL_TEMPLET_TAGS = "getPanelTempletTags";
	  public final static String GET_ALL_PANEL_TEMPLET_LIST = "getAllPanelTempletList";
      public final static String GET_DEFAULT_PANEL_TEMPLET_LIST = "getDefaultPanelTempletList";
      public final static String GET_PUBLISHED_PANEL_TEMPLET_LIST = "getPublishedPanelTempletList";
      public final static String GET_UNPUBLISHED_PANEL_TEMPLET_LIST = "getUnpublishedPanelTempletList";
      public final static String PUBLISH_PANEL_TEMPLET = "publishPanelTemplet";
	}

	public static class Widget{
		//取widget list 及相关
		public static String GET_LIST = "getWidgetList";
		public final static String GET_LIST_WITH_LAYOUT = "widgetWithLayout";
		public final static String API_GET_LIST_WITH_LAYOUT = "apiWidgetWithLayout";
		public final static String GET_MOBILE_LIST_WITH_LAYOUT = "mobileWidgetWithLayout";
		public final static String ADD_WIDGET = "addWidget";
		public final static String API_ADD_WIDGET = "apiAddWidget";
		public final static String COPY_WIDGET = "copyWidget";
		public final static String DEL_WIDGET = "delWidget";
		public final static String EXPORT_WIDGET_CSV = "exportWidgetCsv";
	}

	public static class Space {
		public final static String DEL_SPACE = "delSpace";
		public final static String UPDATE_SPACE = "updateSpace";
		public final static String INVITE_USER = "inviteUser";
		public final static String VALIDATE_SPACE = "validateSpace";
		public final static String GET_SPACE_PANEL_LIST = "getSpacePanelList";
        public static final String API_INIT_DEFAULT_PANEL_FOR_USER_FIRST_SPACE =
            "initDefaultPanelForUserFirstSpace";
	}

	public static class Datasource{

		//编辑excel列表
		public final static String EDIT_TABLE = "editTable";

		//upload
		public final static String UPLOAD_FILE = "uploadFile";
		public final static String UPDATE_UPLOAD_FILE = "updateUploadFile";

		public final static String DEL_SAVED_FILE = "delSavedFile";
		public final static String DEL_USER_DS_CONNECTION = "delUserDsConnection";
		public final static String SAVE_DATA_SOURCE = "saveDataSource";
		public final static String PULL_REMOTE_DATA = "pullRemoteData";
		public final static String REFRESH_FILE_FROM_REMOTE = "refreshFileFromRemote";
		public final static String SAVE_DB_CONNECTION = "saveDBConnection";
		public final static String TEST_DB_CONNECTION = "testDBConnection";
		public final static String UPDATE_CONNECTION_SOURCE = "updateConnectionSource";
		public final static String SAVE_API_CONNECTION = "saveApiConnection";
		public final static String ADD_DB_CONNECTION = "addAccountConnection";

	}
	
  public static class Public {
    public final static String GET_2D_BAR_CODE = "get2DBarCode";
    public final static String REQUEST_SHORT_URL = "requestShortUrl";
    public final static String BUILD_SHORT_URL = "buildShortUrl";
  }

}
