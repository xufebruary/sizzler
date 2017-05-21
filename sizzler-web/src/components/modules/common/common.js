import ProductConfig from 'configs/product.config';
import consts from 'configs/const.config';
import cookieUtils from 'utils/cookie.utils';

var FACEBOOK_SHARE_APP_ID_JP = consts.FACEBOOK_SHARE_APP_ID_JP;
var FACEBOOK_SHARE_APP_ID_COM = consts.FACEBOOK_SHARE_APP_ID_COM;
var WEB_UI_URL = consts.WEB_UI_URL;
var BACK_UI_URL = consts.BACK_UI_URL;
var WEB_SOCKET_URL = consts.WEB_SOCKET_URL;
var WEB_MIDDLE_URL = consts.WEB_MIDDLE_URL;
var SITE_EVENT_ANALYTICS = consts.SITE_EVENT_ANALYTICS;
var APP_WEB_URL = consts.APP_WEB_URL;

//后台数据请求地址版本
export const LINK_API_VERSION = '/api/v1/';

//日志接收地址
export const LINK_USER_OPERATE_LOG = SITE_EVENT_ANALYTICS + '/pt/logs/userOperateLog';

//后台数据请求地址
export const  LINK_SIGNIN_URL = WEB_MIDDLE_URL + '/pt/users/signin';
export const  LINK_GA_SIGNIN_URL = WEB_MIDDLE_URL + '/pt/users/ga/signin';
export const  LINK_SIGNOUT_URL = WEB_MIDDLE_URL + '/pt/users/signout';
export const  LINK_SIGNUP_URL = WEB_MIDDLE_URL + '/pt/users/signup/official';
export const  LINK_SIGNUP_PREREGISTRATION_URL = WEB_MIDDLE_URL + '/pt/users/signup/preregistration';
export const  LINK_USER_FORGOT_URL = WEB_MIDDLE_URL + '/pt/users/send/';
export const  LINK_USER_RESET_PASSWORD_URL = WEB_MIDDLE_URL + '/pt/users/password/reset';
export const  LINK_USER_RESET_EXISTS_URL = WEB_MIDDLE_URL + '/pt/users/exists';
export const  LINK_SYS_PRE_USER_LIST = WEB_MIDDLE_URL + "/pt/users/getPreUserList";
export const  LINK_SYS_PRE_USER_UPDATE = WEB_MIDDLE_URL + "/pt/users/updatePreUserStatus";
export const  LINK_SYS_PRE_USER_UPDATE_PASSWORD = WEB_MIDDLE_URL + "/pt/users/updatePreUserPassword";
export const  LINK_SYS_USER_LIST = WEB_MIDDLE_URL + "/pt/users/getUserList";
export const  LINK_SYS_USER_UPDATE = WEB_MIDDLE_URL + "/pt/users/updateUserStatus";
export const  LINK_SYS_USER_UPDATE_ACCESS = WEB_MIDDLE_URL + "/pt/users/updateAccess";
export const  LINK_SYS_GET_ALL_PERMISSION = WEB_MIDDLE_URL + "/pt/users/getAllPermission";
export const  LINK_SYS_GET_USER_PERMISSION = WEB_MIDDLE_URL + "/pt/users/getUserPermission/";

//请求widget-----pc
export const  LINK_PANEL_WIDGET = WEB_MIDDLE_URL + '/pt/widgets/widget/';
//请求widget-----pc
export const  LINK_PANEL_WIDGET_WITH_LAYOUT = WEB_MIDDLE_URL + '/pt/widgets/widgetWithLayout/';
//请求widget-----mobil
export const  LINK_PANEL_WIDGET_MOBIL = WEB_MIDDLE_URL + '/pt/widgets/widget/mobile/';
//请求widget-----mobil
export const  LINK_PANEL_WIDGET_WITH_LAYOUT_MOBIL = WEB_MIDDLE_URL + '/pt/widgets/widgetWithLayout/mobile/';

//添加widget
export const  LINK_WIDGET_ADD = WEB_MIDDLE_URL + '/pt/widgets/add';
//编辑widget
export const  LINK_WIDGET_EDIT = WEB_MIDDLE_URL + '/pt/widgets/update';
//编辑widgetList
export const  LINK_WIDGET_LIST_EDIT = WEB_MIDDLE_URL + '/pt/widgets/updateWidgets';
//编辑widget基本信息
export const  LINK_BASE_WIDGET_EDIT = WEB_MIDDLE_URL + '/pt/widgets/updateBaseWidget';
//widget templet 基本信息更新
export const  LINK_BASE_WIDGET_TEMPLET_BASIC_EDIT = WEB_MIDDLE_URL + '/pt/widgetTemplet/basic/update';
//编辑widget模板基本信息
export const  LINK_BASE_WIDGET_TEMPLET_EDIT = WEB_MIDDLE_URL + '/pt/widgets/updateBaseWidgetTemplet';
//删除widget
export const  LINK_WIDGET_DELETE = WEB_MIDDLE_URL + '/pt/widgets/del/';
export const  LINK_WIDGET_TEMPLET_DELETE = WEB_MIDDLE_URL + '/pt/widgetTemplet/del/';
//单个widget数据请求
export const  LINK_WIDGET_DATA = WEB_MIDDLE_URL + '/pt/data/widgetData/';
//模板widget列表
export const  LINK_WIDGET_TEMPLET_LIST = WEB_MIDDLE_URL + '/pt/widgetTemplet/list/';
//单个csv下载widget数据请求
export const  LINK_CSV_WIDGET_DATA = WEB_MIDDLE_URL + '/pt/data/csvWidgetData/';

//发布的和有效的标签tag
export const  LINK_TAGS = WEB_MIDDLE_URL + '/pt/tags/owns/';
//获取发布的模版标签
export const  LINK_TAGS_PUBLISH = WEB_MIDDLE_URL + '/pt/tags/published';
//根据type获取发布的和有效的标签
export const  LINK_TAGS_TYPE = WEB_MIDDLE_URL + '/pt/tags/owns/type?type=';
//获取发布的widget标签
export const  LINK_WIDGET_TAGS_PUBLISH = WEB_MIDDLE_URL + '/pt/tags/owns/publish?type=2';
//获取发布的panel标签
export const  LINK_PANEL_TAGS_PUBLISH = WEB_MIDDLE_URL + '/pt/tags/owns/publish?type=0';
//获取发布的系统标签
export const  LINK_SYSTEM_TAGS_PUBLISH = WEB_MIDDLE_URL + '/pt/tags/owns/publish?type=1';
export const  LINK_TAG_ADD = WEB_MIDDLE_URL + '/pt/tags/add';
export const  LINK_TAG_DELETE = WEB_MIDDLE_URL + '/pt/tags/del/';
export const  LINK_TAG_EDIT = WEB_MIDDLE_URL + '/pt/tags/update';
export const  LINK_TAG_SEARCH = WEB_MIDDLE_URL + '/pt/tags/search/';


//图表类型account接口
export const  LINK_GRAPH = WEB_MIDDLE_URL + '/pt/basic/charts/owns/chart';
//数据源接口
export const  LINK_DS = WEB_MIDDLE_URL + '/pt/ds/owns';
export const  LINK_GET_DS_INFO = WEB_MIDDLE_URL + '/pt/ds/info/';
//时间维度
export const  LINK_TIME_OWNS = WEB_MIDDLE_URL + '/rest/time/owns';
//刷新时间
export const  LINK_RFINTERVALS = WEB_MIDDLE_URL + '/rest/intervals/owns';
//语言字典表
export const  LINK_LANGUAGE = WEB_MIDDLE_URL + '/pt/dict/item/language';
//周起始日字典表
export const  LINK_WEEKSTART = WEB_MIDDLE_URL + '/pt/dict/item/week_start';



//GA获取Profile List
export const  LINK_PROFILES_GA = WEB_MIDDLE_URL + '/pt/ga/profiles/';//这个接口太老了，不是真的获取profileId用的
//其他数据源获取Profile list
export const  LINE_DS_PROFILES = WEB_MIDDLE_URL + '/pt/sourceManager/getSpaceWidgetAuthAccount/';
// GA获取Segment List
export const  LINK_SEGMENTS_GA = WEB_MIDDLE_URL + '/pt/ga/segments/';
//GA已授权用户管理
export const  LINK_ACCOUNTS_GA = WEB_MIDDLE_URL + '/pt/ga/email';
//GA账户授权地址
//var LINK_AUTHOR_GA = BACK_UI_URL + '/authorizationCodeServlet?ptOneUserEmail=';
export const  LINK_AUTHOR = BACK_UI_URL + '/connect/';
export const  LINK_AUTHOR_GA = BACK_UI_URL + '/connect/googleanalysis?ptOneUserEmail=';
export const  LINK_AUTHOR_GA_DRIVE = BACK_UI_URL + '/connect/googledrive?ptOneUserEmail=';
export const  LINK_AUTHOR_GA_ADWORDS = BACK_UI_URL + '/connect/googleadwords?ptOneUserEmail=';
//GA指标
export const  LINK_METRICS_GA = WEB_MIDDLE_URL + '/pt/ds/metrics/';
export const  LINK_SEGMENT_METRICS_GA = WEB_MIDDLE_URL + '/pt/ds/segmentMetrics/1';
//GA维度
export const  LINK_DIMENSIONS_GA = WEB_MIDDLE_URL + '/pt/ds/dimension/';
export const  LINK_SEGMENT_DIMENSIONS_GA = WEB_MIDDLE_URL + '/pt/ds/segmentDimension/1';
export const  LINK_SEGMENT_LIST = WEB_MIDDLE_URL + '/pt/ds/segmentList/';
export const  LINK_DIMENSIONSLIST_GA = WEB_MIDDLE_URL + '/pt/ds/dimensionList/1';
export const  LINK_SEGMENT_DIMENSIONSLIST_GA = WEB_MIDDLE_URL + '/pt/ds/segmentDimensionList/1';

//facebook账户授权地址
export const  LINK_AUTHOR_FACEBOOK_AD = BACK_UI_URL + '/connect/facebookad?ptOneUserEmail=';



//salesforce获取所有objects列表
export const  LINK_SALESFORCE_OBJECTS = WEB_MIDDLE_URL + '/pt/salesforce/objects';
//salesforce获取所有report列表   'pt/ds/profileApiRemote/{connectionId}'
export const  LINK_SALESFORCE_REPORT = WEB_MIDDLE_URL + '/pt/ds/profileApiRemote/';
//salesforce获取所有reports及columns列表
export const  LINK_SALESFORCE_REPORTCOLUMNS = WEB_MIDDLE_URL + '/pt/salesforce/reportColumns';
//salesforce获取指标维度
export const  LINK_SALESFORCE_METRICES_AND_DIMENSION = WEB_MIDDLE_URL + '/pt/salesforce/metricsAndDimensions';



//paypal根据category获取到对应的指标维度列表
export const LINK_PAYPAL_METRICES_AND_DIMENSION = WEB_MIDDLE_URL + '/pt/paypal/metricsAndDimensionsByCategory';


//远程获取档案列表/账号列表
export const LINK_PROFILE_API_REMOTE = WEB_MIDDLE_URL + "/pt/ds/profileApiRemote/";

//获取某个数据源下的分类列表
export const LINK_DATASOURCE_CATEGORYS = WEB_MIDDLE_URL + '/pt/ds/categorys';
//根据用户所选分类获取指标维度列表
export const LINK_DATASOURCE_METRICES_AND_DIMENSION_BY_CATEGORY = WEB_MIDDLE_URL + '/pt/ds/metricsAndDimensionsByCategory';

// 获取过滤器列表 pt/ds/filterList/{dsId}
export const LINK_FILTER_LIST = WEB_MIDDLE_URL + '/pt/ds/filterList/';

// 获取过滤器中维度值列表（post请求:{"dsId":"","dsCode":"","connectionId":"","accountName":"","profileId":"","dimensionId":"","dateKey":"","uid":""}）
export const LINK_FILTER_DIMENSION_VALUE_LIST = WEB_MIDDLE_URL + '/pt/ds/dimensionValues';

// 获取用户指标 /pt/ds/userMetrics/{dsId}/{tableId}
export const LINK_USER_METRICS = WEB_MIDDLE_URL + '/pt/ds/userMetrics/';
// 获取用户维度 /pt/ds/userDimension/{dsId}/{tableId}
export const LINK_USER_DIMENSIONS = WEB_MIDDLE_URL + '/pt/ds/userDimension/';
// 获取用户指标和维度 /pt/ds/userMetricsAndDimensions/{dsId}/{tableId}
export const LINK_USER_METRICS_AND_DIMENSIONS = WEB_MIDDLE_URL + '/pt/ds/userMetricsAndDimensions/';

// 新建指标维度分类 /pt/ds/addCategory/{type}
export const LINK_ADD_METRICS_DIMENSIONS_CATEGORY = WEB_MIDDLE_URL + '/pt/ds/addCategory/';

// 获取复合指标维度模板key列表 /pt/ds/compoundTempletKey/{type}
export const LINK_COMPOUND_TEMPLET_KEY_LIST = WEB_MIDDLE_URL + '/pt/ds/compoundTempletKey/';

// 获取复合指标维度模板列表 /pt/ds/compoundTemplet/{type}
export const LINK_COMPOUND_TEMPLET_LIST = WEB_MIDDLE_URL + '/pt/ds/compoundTemplet/';

// 生成复合指标维度 /pt/ds/buildCompound/{type}
export const LINK_BUILD_COMPOUND = WEB_MIDDLE_URL + '/pt/ds/buildCompound/';


//custom api变量管理
export const LINK_ACCOUNTS_CUSTOM = WEB_MIDDLE_URL + '/rest/customs/accounts';

//Ptconsole指标
export const LINK_METRICS_PTCONSOLE = WEB_MIDDLE_URL + '/rest/ptconsole/metrics';
//Ptconsole维度
export const LINK_DIMENSION_PTCONSOLE = WEB_MIDDLE_URL + '/rest/ptconsole/dimension/items';


// //保存为模板
// var LINK_TEMPLATE_ADD = WEB_MIDDLE_URL + '/rest/widgets/template/add';
//模板列表--按数据源
export const LINK_TEMPLATE_LIST = WEB_MIDDLE_URL + '/pt/widgets/templet';
// 从es中按照关键字从索引中搜索widget模板
export const LINK_SEARCH_TEMPLATE_LIST = WEB_MIDDLE_URL + '/pt/widgets/templet/search';
//模板列表--所有
export const LINK_TEMPLATE_LIST_ALL = WEB_MIDDLE_URL + '/pt/widgets/templet/page';
//单个模板请求接口
export const LINK_TEMPLATE_INFO = WEB_MIDDLE_URL + '/pt/widgets/templet/t/'
// //删除模板
// var LINK_TEMPLATE_DELETE = WEB_MIDDLE_URL + '/rest/widgets/template/del/';
// //编辑模板
// var LINK_TEMPLATE_EDIT = WEB_MIDDLE_URL + '/rest/widgets/template/update';
//登出
export const LINK_SIGN_OUT = WEB_MIDDLE_URL + '/pt/users/signout';
//push推送路径
export const CUSTOM_PUSH_WEB_SOCKET = 'customPushWebSocketHandler?ptOneUserEmail=';
//dataSource推送路径
export const DATA_SOURCE_WEB_SOCKET = 'dataSourceWebSocketHandler?ptOneUserEmail=';
//dataSource推送路径
export const OTHER_LOGIN_WEB_SOCKET = 'dataSourceWebSocketHandler?sign=';

//用户基本信息
export const LINK_USER_INFO = WEB_MIDDLE_URL + '/pt/users/u';
//获取用户设置信息
export const LINK_SETTINGS_INFO = WEB_MIDDLE_URL + '/pt/users/settings/info';
//更新用户设置信息
export const LINK_SETTINGS_INFO_UPDATE = WEB_MIDDLE_URL + '/pt/users/settings/update';
//更新用户设置信息
export const LINK_USERS_INFO_UPDATE = WEB_MIDDLE_URL + '/pt/users/update';

//生成loginKey
export const LINK_CREATE_LOGIN_KEY = WEB_MIDDLE_URL + '/pt/users/loginKey/';
//获取用户上次选择profile设置信息
export const LINK_SETTINGS_INFO_PROFILE_SELECTED = WEB_MIDDLE_URL + '/pt/users/settings/profileSelected';
//校验分享链接  ?password=
export const LINK_SHARE_SIGNIN = WEB_MIDDLE_URL + '/pt/users/shareSignin/';
//获取分享用户信息
export const LINK_SHARE_USER_INFO = WEB_MIDDLE_URL + '/pt/users/shareUserInfo/'
//分享panel链接
export const LINK_SHARE_PANEL = location.origin + '/share-panel.html?id=';


//数据源相关
export const LINK_EXCEL_FILE_UPLOAD = WEB_MIDDLE_URL + '/pt/file/excelFileUpload';
export const LINK_EXCEL_FILE_UPLOAD_UPDATE = WEB_MIDDLE_URL + '/pt/file/excelFileUpdate';
export const LINK_EXCEL_FILE_EXIST = WEB_MIDDLE_URL + '/pt/file/existFile';
export const LINK_GLOBALIZATION_JSON_FILE_UPDATE = WEB_MIDDLE_URL + '/pt/file/uploadGlobalizationFile';

export const LINK_EXCEL_FILE_ADD = WEB_MIDDLE_URL + '/pt/sourceManager/saveDataSource';

// 根据tableId更新远端文件数据 /pt/sourceManager/updateRemoteSourceData/{tableId}
export const LINK_EXCEL_FILE_UPDATE_SOURCE_DATA = WEB_MIDDLE_URL + '/pt/sourceManager/updateRemoteSourceData/';

//文件重命名
export const LINK_UPDATE_CONNECTION_SOURCE = WEB_MIDDLE_URL + '/pt/sourceManager/updateConnectionSource';

export const LINK_GET_SCHEMA = WEB_MIDDLE_URL + '/pt/sourceManager/getDataSourceAccountSchema/';
export const LINK_DATA_SOURCE_VIEW = WEB_MIDDLE_URL + '/pt/sourceManager/getSpaceDataSourceView/';
export const LINK_DATA_SOURCE_EDIT_VIEW = WEB_MIDDLE_URL + '/pt/sourceManager/getDataSourceEditView/';
export const LINK_PULL_REMOTE_DATA = WEB_MIDDLE_URL + '/pt/sourceManager/pullRemoteData/';
export const LINK_GET_AUTH_ACCOUNT = WEB_MIDDLE_URL + '/pt/sourceManager/getSpaceAuthAccount/';
export const LINK_GET_AUTH_ACCOUNT_DETAIL = WEB_MIDDLE_URL + '/pt/sourceManager/getSpaceDsContentView/';
export const LINK_DEL_AUTH_ACCOUNT = WEB_MIDDLE_URL + '/pt/sourceManager/delUserDsConnection/';
export const LINK_DEL_SAVEDFILE = WEB_MIDDLE_URL + '/pt/sourceManager/delSavedFile/';
export const LINK_REFRESH_FILE = WEB_MIDDLE_URL + '/pt/sourceManager/refreshFileFromRemote/';
export const LINK_GET_ACCOUNT_WIDGET_COUNT = WEB_MIDDLE_URL + '/pt/sourceManager/getAccountWidgetCount/';
export const LINK_GET_SOURCE_WIDGET_COUNT = WEB_MIDDLE_URL + '/pt/sourceManager/getSourceWidgetCount/';
export const LINK_GET_USER_CONNECTION_CONFIG = WEB_MIDDLE_URL + '/pt/sourceManager/getUserConnectionConfig/';

export const LINK_SAVE_DB_CONNECTION = WEB_MIDDLE_URL + '/pt/sourceManager/saveDBConnection/';
export const LINK_TEST_DB_CONNECTION = WEB_MIDDLE_URL + '/pt/sourceManager/testDBConnection/';

export const LINK_SAVE_API_CONNECTION = WEB_MIDDLE_URL+'/pt/sourceManager/saveApiConnection/';


// 获取数据版本号
export const LINK_SYS_DATA_VERSION = WEB_MIDDLE_URL + '/pt/sys/dataVersion';

//系统管理
export const LINK_SYS_REFRESH_CACHE = WEB_MIDDLE_URL + '/pt/sys/refreshSysCache';

//用户ptone权限
export const LINK_PERMISSION_SYS = WEB_MIDDLE_URL + '/pt/permission/sys';

//权限管理
export const LINK_SYS_OPERATION = WEB_MIDDLE_URL + '/pt/permission/operation';
export const LINK_SYS_OPERATION_UPDATE = WEB_MIDDLE_URL + '/pt/permission/operation/update';
export const LINK_SYS_OPERATION_ADD = WEB_MIDDLE_URL + '/pt/permission/operation/add';

export const LINK_SYS_RESOURCE = WEB_MIDDLE_URL + '/pt/permission/resource';
export const LINK_SYS_RESOURCE_UPDATE = WEB_MIDDLE_URL + '/pt/permission/resource/update';
export const LINK_SYS_RESOURCE_ADD = WEB_MIDDLE_URL + '/pt/permission/resource/add';

export const LINK_SYS_ROLE = WEB_MIDDLE_URL + '/pt/permission/role';
export const LINK_SYS_ROLE_UPDATE = WEB_MIDDLE_URL + '/pt/permission/role/update';
export const LINK_SYS_ROLE_ADD = WEB_MIDDLE_URL + '/pt/permission/role/add';

export const LINK_SYS_PERMISSION_LIST = WEB_MIDDLE_URL + '/pt/permission/list';
export const LINK_SYS_PERMISSION_UPDATE = WEB_MIDDLE_URL + '/pt/permission/update';
export const LINK_SYS_PERMISSION_ADD = WEB_MIDDLE_URL + '/pt/permission/add';
export const LINK_SYS_PERMISSION_SETTING = WEB_MIDDLE_URL + '/pt/permission/sys/setting';

/**
 * ptengine 授权与登陆接口
 */
export const LINK_PTENGINE_LOGIN = WEB_MIDDLE_URL + '/pt/ptengine/auth/login';
export const LINK_PTENGINE_ANALYTICS = WEB_MIDDLE_URL + '/pt/ptengine/auth/analytics';
export const LINK_PTENGINE_LOGIN_AUTHORIZE = WEB_MIDDLE_URL + '/pt/ptengine/authConfirm/';//pt/ptengine/authConfirm/{email}/login/1

/**
 * ptapp 授权与登陆接口
 */
export const LINK_PTAPP_LOGIN = APP_WEB_URL + '/pt/users/signinForDatadeck?community=false';
export const LINK_PTAPP_ANALYTICS = WEB_MIDDLE_URL + '/pt/ptengine/auth/analytics';
export const LINK_PTAPP_LOGIN_AUTHORIZE = WEB_MIDDLE_URL + '/pt/sourceManager/addAccountConnection/';//pt/ptengine/authConfirm/{email}/login/1


/**
 * space
 */
export const LINK_SPACE_LIST =  WEB_MIDDLE_URL+'/pt/space/list';
export const LINK_SPACE_UPDATE =  WEB_MIDDLE_URL+'/pt/space/update';
export const LINK_SPACE_ADD =  WEB_MIDDLE_URL+'/pt/space/add';
export const LINK_SPACE_DELETE =  WEB_MIDDLE_URL+'/pt/space/delete/';
export const LINK_SPACE_QUIT =  WEB_MIDDLE_URL+'/pt/space/exitSpace/';
export const LINK_SPACE_DOMAIN_CHECK =  WEB_MIDDLE_URL+'/pt/space/checkDomain/';
export const LINK_SPACE_INVITE_USERS =  WEB_MIDDLE_URL+'/pt/space/inviteUsers/';
export const LINK_USER_PASSWORD_CHECK =  WEB_MIDDLE_URL+'/pt/users/password/check';
export const LINK_SPACE_USERS=  WEB_MIDDLE_URL+'/pt/space/users/';
export const LINK_SPACE_DELETE_USERS=  WEB_MIDDLE_URL+'/pt/space/deleteSpaceUser/';
export const LINK_INVITE_URL_CHECK=  WEB_MIDDLE_URL+'/pt/space/checkInviteUrl/';
export const LINK_INVITE_ACCEPT=  WEB_MIDDLE_URL+'/pt/space/acceptInvite/';


/*CalculatedValue*/
export const LINK_ADD_CALCULATE_VALUE =  WEB_MIDDLE_URL+'/pt/ds/addCalculatedValue';
export const LINK_UPDATE_CALCULATE_VALUE =  WEB_MIDDLE_URL+'/pt/ds/updateCalculatedValue';
export const LINK_DEL_CALCULATE_VALUE =  WEB_MIDDLE_URL+'/pt/ds/deleteCalculatedValue';
export const LINK_LIST_CALCULATE_VALUE =  WEB_MIDDLE_URL+'/pt/ds/calculatedValueList';
export const LINK_GET_USE_CALCULATE_VALUE =  WEB_MIDDLE_URL+'/pt/ds/getUseCalculatedValueCount';
export const LINK_VALIDATE_CALCULATE_VALUE =  WEB_MIDDLE_URL+'/pt/ds/validateCalculatedValue';
export const LINK_SELECTED_METRICS =  WEB_MIDDLE_URL+'/pt/widgets/selectedMetrics/';

/*widget gallery*/
export const LINK_ADD_BY_TEMPLATE = WEB_MIDDLE_URL+'/pt/widgets/addByTemplet';
export const LINK_WIDGET_TEMPLATE_DS_LIST = WEB_MIDDLE_URL+'/pt/widgetTemplet/dsList';

/*用户权限更新*/
export const LINK_UPDATE_USER_SPACE_PERMISSION = WEB_MIDDLE_URL+'/pt/users/permission/update';

/*ptapp 获取自定义变量列表*/
export const LINK_APP_GET_USER_VAR_KEY_NAME = WEB_MIDDLE_URL + '/pt/ds/metricsDimensionApiRemote/';

/*batch Widget Data*/
export const LINK_BATCH_WIDGET_DATA = WEB_MIDDLE_URL+'/pt/data/batchWidgetData';

/*获取heatmap数据*/
export const LINK_HEATMAP_DATA = WEB_MIDDLE_URL+'/pt/ptengine/heatmap/data'

/**
 * profiles
 *
 * /pt/ds/accounts/{dsCode}/{connectionId}/{email:.+}
 */
export const LINK_GET_DS_PROFILES = WEB_MIDDLE_URL + '/pt/ds/profiles';



export const isAndroid = navigator.userAgent.toLowerCase().indexOf('android') >= 0;
export const  isIphone = navigator.userAgent.toLowerCase().indexOf('iphone') >= 0;


/**
 * 富文本框上传图片接口
 */

export const  LINK_UPLOAD_IMAGE = WEB_MIDDLE_URL + '/pt/file/imgUpload';
export const  emailReg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;


/**
 * getRect
 * 获取dom相对于窗口的位置
 */
export function getRect(element) {
    var rect = element.getBoundingClientRect();
    var top = document.documentElement.clientTop;
    var left = document.documentElement.clientLeft;
    return {
        top: rect.top - top,
        bottom: rect.bottom - top,
        left: rect.left - left,
        right: rect.right - left
    }
}

//计算百分比方法
export function GetPercent(num, total) {
	var value = '';
	num = parseFloat(num);
	total = parseFloat(total);
	if (isNaN(num) || isNaN(total) || total == 0) {
		value =  "---";
	}else{
		value = (total <= 0 ? "0" : (Math.round(num / total * 10000) / 100.00));
	}

	return value;
}

// 格式化 number 显示
/**
 * dataUnit 单位
 * type = null : shortValue + shortUnit + dataUnit
 * type = array : [shortValue , shortUnit + dataUnit]
 * type = short : 缩略,  30.23M, shortValue + shortUnit
 * type = shortArray : [shortValue, shortUnit]
 * type = thousands 千分位 12,233.21
 * type = thousandsArray
 * NumberMinUnitValue 数字最小单位值(默认为百万: 1,000,000)
 */
export function FormatNumber (num, dataUnit, type, NumberMinUnitValue){

	var pre = '';
	var val = '';
	var post = '';
	dataUnit = dataUnit || '';
	var shortUnit = '';

	if(num || num == 0){
		var reg = /^(\-|\+)?\d+(\.\d+)?$/; //判断字符串是否为数字(正数、负数、小数) //var reg = /^[0-9]+.?[0-9]*$/;
		if (!reg.test(num)){
			val = num;
		} else {
			if('h' == dataUnit){
				//将小时转换成毫秒
				num = num*60*60*1000;
				dataUnit = 'ms';
			}else if('m' == dataUnit){
				//将分钟转换成毫秒
				num = num*60*1000;
				dataUnit = 'ms';
			}else if('s' == dataUnit){
				num = num*1000;
				dataUnit = 'ms';
			}

			// 如果单位是秒(s),对时间进行格式化显示为: 1h2m3s4ms
			if('ms' == dataUnit){
				var ms = 1;
				var s = 1000;
				var m = 1000 * 60;
				var h = 1000 * 60 * 60;
				var d = 1000 * 60 * 60 * 24;

				// 修正数值位数
				if(num != 0){
					num = parseFloat(num).toFixed(0); // 不保留小数
					num = parseFloat(num); // 去除末尾的0
				}

				// 如果是缩略的时间格式，则修正数值
				if(!type || type == "array" || type == 'short' || type == 'shortArray'){
					if(num > d){
						num = (num - num%h);
					}else if(num > h){
						num = (num - num%m);
					}else if(num > m){
						num = (num - num%s);
					}
				}

				var result = '';
				if(num >= d){
					result += (num - num%d)/d + 'd'; // 取整
					num = num%d; // 求余
				}
				if(num >= h){
					result += (num - num%h)/h + 'h'; // 取整
					num = num%h; // 求余
				}
				if(num >= m){
					result += (num-num%m)/m + 'm';
					num = num%m;
				}
				if(num >= s){
					result += (num-num%s)/s + 's';
					num = num%s;
				}

				// 修正数值位数
				if(num != 0){
					num = parseFloat(num).toFixed(2); // 保留两位小数
					num = parseFloat(num); // 去除末尾的0
				}

				if(!result || (result && num != 0) ){
					result += num + dataUnit;
				}

				// 时间格式不需要拼接dataUnit和shortUnit, 设为空串
				dataUnit = '';
				shortUnit = '';
				val = result;

			} else {
				if(!type || type == "array" || type == 'short' || type == 'shortArray'){
					var _negative = false;
					var k = 1000;
					var m = 1000 * 1000;
					var b = 1000 * 1000 * 1000;
					var t = 1000 * 1000 * 1000 * 1000;
					var e = 1000 * 1000 * 1000 * 1000 * 1000;//大于999t的显示为科学计数法
					NumberMinUnitValue = NumberMinUnitValue || m;

					if(num < 0){
						num = Math.abs(num);//转换为正数
						_negative = true;
					}
					if(num >= NumberMinUnitValue){
						if(num >= e){//大于999t的显示为科学计数法（转换为字符串显示）
							var valString = num.toString();
							var valLen = valString.length;
							if(valString.slice(1,2) == 0){
								val = valString.slice(0,1) + 'e+' + valLen;
							}else{
								val = valString.slice(0,1) + '.' +  valString.slice(1,2) + 'e+' + valLen;
							}
							
							shortUnit = '';
						}else if(num > t){
							val = num / t;
							shortUnit = 'T';
						}else if(num > b){
							val = num / b;
							shortUnit = 'B';
						}else if(num >= m){
							val = num / m;
							shortUnit = 'M';
						}else if(num >= k){
							if(num < 10*k){
								val = num;
								shortUnit = '';
							}else{//最新规则，大于10000才显示k
								val = num / k;
								shortUnit = 'K';
							}
						}else{
							val = num;
						}
					} else {
						val = num;
					}
					
					if(_negative){
						val = "-" + val;
					}

					//reg = /^[0-9]+[0-9]*]*$/; //判断正整数
					//reg = /^-?\d+$/; // 判断整数
					if(!/e|E/.test(val.toString())){//科学计数法不需要转换保留两位小数等规则
						val = parseFloat(val).toFixed(2); // 保留两位小数
					    val = parseFloat(val); // 2.90 >>> 2.9， 去除末尾的0
					}
					//四舍五入需要进一位的临近值处理 例如999995，应显示为1M，不是1000k
					if(val === 1000 && shortUnit === 'K'){
						val = 1;
						shortUnit = 'M';
					}else if(val === 1000 && shortUnit === 'M'){
						val = 1;
						shortUnit = 'B';
					}else if(val === 1000 && shortUnit === 'B'){
						val = 1;
						shortUnit = 'T';
					}
					if(num >= k && dataUnit !== '%'){//如果百万以下数字，三位需要逗号分隔，如果类型是百分比的值，不需要加逗号分隔
						val = (val + '').replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,'); // 三位分隔
					}

				}else if(type =='thousands' || type == 'thousandsArray'){
					val = parseFloat(num).toFixed(2); // 保留两位小数
					val = parseFloat(val); // 2.90 >>> 2.9， 去除末尾的0
					//四舍五入需要进一位的临近值处理 例如999995，应显示为1M，不是1000k
					if(val === 1000 && shortUnit === 'K'){
						val = 1;
						shortUnit = 'M';
					}else if(val === 1000 && shortUnit === 'M'){
						val = 1;
						shortUnit = 'B';
					}else if(val === 1000 && shortUnit === 'B'){
						val = 1;
						shortUnit = 'T';
					}
					if(num >= k && dataUnit !== '%'){//如果百万以下数字，三位需要逗号分隔，如果类型是百分比的值，不需要加逗号分隔
						val = (val + '').replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,'); // 三位分隔
					}
					val = (val + '').replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,'); // 三位分隔
				}
			}
		}
	}

	if(dataUnit =='$' || dataUnit == '€' || dataUnit == '¥'){
		pre = dataUnit;
		val = val + shortUnit;
		post = '';
	}else{
		pre = '';
		val = val + shortUnit;
		post = dataUnit;
	}

	if(type == "array" || type == 'shortArray' || type == 'thousandsArray'){
		return [pre, val, post];
	}else{
		return pre + val + post;
	}
}

/**
 * uuid
 * 生成各种ID
 */
export function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";

    var uuid = s.join("");
    return uuid;
}

/**
 * getLocalLang
 * 获取本地国际化CODE
 * 域名匹配不上,则走产品配置
 * 如果还没有返回ja_JP
 */
export function getLocalLang(){
    var href = window.location.href;
    var source,locale;
    // var locale = (window.navigator.language || window.navigator.browserLanguage || window.navigator.systemLanguage || window.navigator.userLanguage || '').split('-').join('_');
    if(href.indexOf('datadeck.jp') > -1){
        locale = 'ja_JP';
        source = 'dd-jp-basic';
    }else if(href.indexOf('datadeck.com') > -1){
        locale = 'en_US';
        source = 'dd-en-basic';
    }else if(href.indexOf('ptone.cn') > -1 || href.indexOf('ptone.com.cn') > -1 ){
        locale = 'zh_CN';
        source = 'dd-cn-basic';//来源现和datadeck.cn一致
    }else if(href.indexOf('datadeck.cn') > -1){
        locale = 'zh_CN';
        source = 'dd-cn-basic';
    }else{
        //从productConfig中获取defaultLocal信息
        locale = ProductConfig.defaultLocale || 'ja_JP';
        source = ProductConfig.source || 'dd-jp-basic';
    }

    var settingLan = localStorage.getItem(consts.I18N_KEY);
    // 用户登录并且本地已存储i18nkey
	if(cookieUtils.get('sid') && settingLan){
		locale = settingLan;
	}

    return {'locale':locale,'source':source};
}

export const loginKey = {
    zh_CN: {
        "LOGIN.EMAIL": "邮箱",
        "LOGIN.PASSWORD": "密码",
        "LOGIN.SIGNUP_INFO.EMAIL": "邮箱",
        "LOGIN.SIGNUP_INFO.PASSWORD": "密码"
    },
    ja_JP: {
        "LOGIN.EMAIL": "登録メールアドレス",
        "LOGIN.PASSWORD": "パスワード",
        "LOGIN.SIGNUP_INFO.EMAIL": "メールアドレス",
        "LOGIN.SIGNUP_INFO.PASSWORD": "パスワード"
    },
    en_US: {
        "LOGIN.EMAIL": "Email",
        "LOGIN.PASSWORD": "Password",
        "LOGIN.SIGNUP_INFO.EMAIL": "Email",
        "LOGIN.SIGNUP_INFO.PASSWORD": "Password"
    }
};


/**
 * 区分具体域名，domain是'www.ptone.jp'或者'ptone.jp'这种形式
 * @param domain
 * @returns {boolean}
 */
export function isDomain(domain){
    var nowDomain = window.location.hostname;
    return nowDomain.indexOf(domain) != -1;
}

/**
 *
 */
export function isUrlContain(string){
    var href = window.location.href;
    return href.indexOf(string) > -1;
}

/**
 * hideObjByCss
 * 隐藏指定对象
 */
export function hideObjByCss(css) {
    var $o = $('.' + css);
    if ($o.length > 0) {
        $.each($o, function () {
            $(this).hide();
        });
    }
}


/**
 * cloneAll
 * 克隆对象
 */
export function cloneAll(fromObj, toObj) {
    for (var i in fromObj) {
        if (typeof fromObj[i] == "object") {
            toObj[i] = {};
            cloneAll(fromObj[i], toObj[i]);
            continue;
        }
        toObj[i] = fromObj[i];
    }
}


/**
 * 获取当前时间
 **/
Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(), //day
        "h+": this.getHours(), //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
        "S": this.getMilliseconds() //millisecond
    };

    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }

    return format;
}

/**
 * 获取当前时间的前N天时间点
 **/
export function getBeforeDay(data) {
    var today = new Date();
    var beforeDay = new Date(Date.parse(new Date().toString()) - 86400000 * data);
    // var lastDay = beforeDay.format('MM/dd/yyyy');
    return beforeDay;
}

/**
 *  方法:Array.remove(dx) 通过遍历,重构数组
 *  功能:删除数组元素.
 *  参数:dx删除元素的下标.
 */
Array.prototype.remove = function (dx) {
    if (isNaN(dx) || dx > this.length) {
        return false;
    }
    for (var i = 0, n = 0; i < this.length; i++) {
        if (this[i] != this[dx]) {
            this[n++] = this[i]
        }
    }
    this.length -= 1
};


export function getStartDate(type) {
    var startDate;
    var now = new Date(); //当前日期
    var nowDayOfWeek = now.getDay(); //今天本周的第几天
    var nowDay = now.getDate(); //当前日
    var nowMonth = now.getMonth(); //当前月
    var nowYear = now.getFullYear(); //当前年

    if (type == 'week') {
        //获得本周的开端日期
        var weekStartDate = new Date(nowYear, nowMonth, nowDay - nowDayOfWeek);
        return weekStartDate.format('yyyy-MM-dd');
    } else if (type == 'moon') {
        //获得本月的开端日期
        var monthStartDate = new Date(nowYear, nowMonth, 1);
        return monthStartDate.format('yyyy-MM-dd');
    }
}


/**
 * compareObject
 * 对象维度比较
 var a = [
 {col: 1,id: "a", row: 1},
 {col: 1,id: "b", row: 2},
 {col: 2,id: "c", row: 1},
 {col: 1,id: "d", row: 3},
 {col: 2,id: "e", row: 2},
 {col: 2,id: "f", row: 3},
 ];
 a.sort(compareObject("row", "col"));
 */
export function compareObject(firstKey, secondKey) {
    return function (a, b) {
        if (a[firstKey] < b[firstKey]) {
            return -1;
        } else if (a[firstKey] > b[firstKey]) {
            return 1;
        } else {
            if (a[secondKey] < b[secondKey]) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}


/**
 * objectIsEmpty
 * 判断对象是否为空
 */
export function objectIsEmpty(data) {
    if (typeof data === "object" && !(data instanceof Array)) {
        var hasProp = false;
        for (var prop in data) {
            hasProp = true;
            break;
        }

        if (hasProp) {
            return false;
        } else {
            return true;
        }
    }
}


/**
 * 获取滚动条bar的宽度
 */
export function getScrollbarWidth() {
    var oP = document.createElement('p'),
      styles = {
          width: '100px',
          height: '100px',
          overflowY: 'scroll'
      },
      i, scrollbarWidth;
    for (i in styles) oP.style[i] = styles[i];
    document.body.appendChild(oP);
    scrollbarWidth = oP.offsetWidth - oP.clientWidth;
    if(oP.remove){
        oP.remove();
    }else if(oP.removeNode){
        oP.removeNode();
    }
    //oP.remove();
    return scrollbarWidth;
}

/**
 * 时间戳转日期
 */
export function timestamp2Date(timestamp){
    //timestamp = 1398250549490
    var date = new Date(timestamp);
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    var D = date.getDate() + ' ';
    var h = date.getHours() + ':';
    var m = date.getMinutes() + ':';
    var s = date.getSeconds();
    return Y+M+D+h+m+s;
}


/**
 * 数字转字母
 */
export function i2s(num) {
    var temp = "";
	var i = Math.floor(Math.log(25.0 * (num) / 26.0 + 1) / Math.log(26)) + 1;
	if (i > 1) {
		var sub = num - 26 * (Math.pow(26, i - 1) - 1) / 25;
		for (var j = i; j > 0; j--) {
			temp = temp + String.fromCharCode(sub / Math.pow(26, j - 1) + 65);
			sub = sub % Math.pow(26, j - 1);
		}
	} else {
		temp = temp + String.fromCharCode(num + 65);
	}
	return temp;
}

export function GetRequest(paramUrl) {
    var url;
    if(paramUrl){
        url = paramUrl;
    }else{
        url = location.href; //获取url中"?"符后的字串
    }
    var theRequest = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.split("?")[1];
        var strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}

/**
 * 在新窗口中打开该链接
 * openWindow
 */
export function openWindow(url) {
	var a = document.createElement('a');
	a.href = url;
	a.target = '_blank';
	a.style.display = 'none';
	document.body.appendChild(a);
	a.click();
	a.parentNode.removeChild(a);
}


Array.max = function (array) {
    return Math.max.apply(Math, array);
};

Array.min = function (array) {
    return Math.min.apply(Math, array);
};

//字符串正则表达式关键字转化
$.regTrim = function (s) {
    var imp = /[\^\.\\\|\(\)\*\+\-\$\[\]\?]/g;
    var imp_c = {};
    imp_c["^"] = "\\^";
    imp_c["."] = "\\.";
    imp_c["\\"] = "\\\\";
    imp_c["|"] = "\\|";
    imp_c["("] = "\\(";
    imp_c[")"] = "\\)";
    imp_c["*"] = "\\*";
    imp_c["+"] = "\\+";
    imp_c["-"] = "\\-";
    imp_c["$"] = "\\$";
    imp_c["["] = "\\[";
    imp_c["]"] = "\\]";
    imp_c["?"] = "\\?";
    s = s.replace(imp, function (o) {
        return imp_c[o];
    });
    return s;
};

/*
 * MAP对象，实现MAP功能
 *
 * 接口：
 * size()     获取MAP元素个数
 * isEmpty()    判断MAP是否为空
 * clear()     删除MAP所有元素
 * put(key, value)   向MAP中增加元素（key, value)
 * remove(key)    删除指定KEY的元素，成功返回True，失败返回False
 * get(key)    获取指定KEY的元素值VALUE，失败返回NULL
 * element(index)   获取指定索引的元素（使用element.key，element.value获取KEY和VALUE），失败返回NULL
 * containsKey(key)  判断MAP中是否含有指定KEY的元素
 * containsValue(value) 判断MAP中是否含有指定VALUE的元素
 * values()    获取MAP中所有VALUE的数组（ARRAY）
 * keys()     获取MAP中所有KEY的数组（ARRAY）
 *
 * 例子：
 * var map = new ptMap();
 *
 * map.put("key", "value");
 * var val = map.get("key")
 * ……
 *
 */
export function ptMap() {
    this.elements = [];

    //获取MAP元素个数
    this.size = function () {
        return this.elements.length;
    };

    //判断MAP是否为空
    this.isEmpty = function () {
        return (this.elements.length < 1);
    };

    //删除MAP所有元素
    this.clear = function () {
        this.elements = [];
    };

    //向MAP中增加元素（key, value)
    this.put = function (_key, _value) {
        this.removeByKey(_key);
        this.elements.push({
            key: _key,
            value: _value
        });
    };

    //删除指定KEY的元素，成功返回True，失败返回False
    this.removeByKey = function (_key) {
        var bln = false;
        try {
            for (var i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    this.elements.splice(i, 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //删除指定VALUE的元素，成功返回True，失败返回False
    this.removeByValue = function (_value) {//removeByValueAndKey
        var bln = false;
        try {
            for (var i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value) {
                    this.elements.splice(i, 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //删除指定VALUE的元素，成功返回True，失败返回False
    this.removeByValueAndKey = function (_key, _value) {
        var bln = false;
        try {
            for (var i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value && this.elements[i].key == _key) {
                    this.elements.splice(i, 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //获取指定KEY的元素值VALUE，失败返回NULL
    this.get = function (_key) {
        try {
            for (var i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    return this.elements[i].value;
                }
            }
        } catch (e) {
            return false;
        }
        return false;
    };

    //获取指定索引的元素（使用element.key，element.value获取KEY和VALUE），失败返回NULL
    this.element = function (_index) {
        if (_index < 0 || _index >= this.elements.length) {
            return null;
        }
        return this.elements[_index];
    };

    //判断MAP中是否含有指定KEY的元素
    this.containsKey = function (_key) {
        var bln = false;
        try {
            for (var i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //判断MAP中是否含有指定VALUE的元素
    this.containsValue = function (_value) {
        var bln = false;
        try {
            for (var i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //判断MAP中是否含有指定VALUE的元素
    this.containsObj = function (_key, _value) {
        var bln = false;
        try {
            for (var i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value && this.elements[i].key == _key) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    //获取MAP中所有VALUE的数组（ARRAY）
    this.values = function () {
        var arr = [];
        for (var i = 0; i < this.elements.length; i++) {
            arr.push(this.elements[i].value);
        }
        return arr;
    };

    //获取MAP中所有VALUE的数组（ARRAY）
    this.valuesByKey = function (_key) {
        var arr = [];
        for (var i = 0; i < this.elements.length; i++) {
            if (this.elements[i].key == _key) {
                arr.push(this.elements[i].value);
            }
        }
        return arr;
    };

    //获取MAP中所有KEY的数组（ARRAY）
    this.keys = function () {
        var arr = [];
        for (var i = 0; i < this.elements.length; i++) {
            arr.push(this.elements[i].key);
        }
        return arr;
    };

    //获取key通过value
    this.keysByValue = function (_value) {
        var arr = [];
        for (var i = 0; i < this.elements.length; i++) {
            if (_value == this.elements[i].value) {
                arr.push(this.elements[i].key);
            }
        }
        return arr;
    };

    //获取MAP中所有KEY的数组（ARRAY）
    this.keysRemoveDuplicate = function () {
        var arr = [];
        for (var i = 0; i < this.elements.length; i++) {
            var flag = true;
            for (var j = 0; j < arr.length; j++) {
                if (arr[j] == this.elements[i].key) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                arr.push(this.elements[i].key);
            }
        }
        return arr;
    };
}

/**
 * nameI18n
 * 数据名称国际化(一般用在GA的维度或指标)
 *
 * @param tier: 数据层级数目
 * @param tierData: 需要国际化的字段
 * @param dataList: 原始数据
 *
 */
export function nameI18n(tier, tierData, dataList) {
    for (var i = dataList.length - 1; i >= 0; i--) {
        //dataList[i]
    }
}


// setCookie
export function setCookie(c_name, value) {
    document.cookie = c_name + "=" + value + ";path=/";
}

//读取cookies
export function getCookie(name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg))
        return encodeURIComponent(arr[2]);
    else
        return null;
}

// setCookie
export function delCookie(name) {
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval = getCookie(name);
    if (cval != null)
        document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
}

export function clearCookie(){
    var rs = document.cookie.match(new RegExp("([^ ;][^;]*)(?=(=[^;]*)(;|$))", "gi"));
    // 删除所有cookie
    for (var i in rs){
        document.cookie = rs[i] + "=;expires=Mon, 26 Jul 1997 05:00:00 GMT; path=/; " ;
    }
}

/**
 * html字符转义函数
 * @param str
 * @returns {string}
 */
export function html_encode(str) {
	var s = "";
	if (!str || str.length == 0) return "";
	//s = str.replace(/&/g, "&gt;");
	s = str.replace(/</g, "&lt;");
	s = s.replace(/>/g, "&gt;");
	return s;
}


/*******************
 * Login Canvas bg
 *******************/
export function loginSvg() {
    var defaultW = 1366;
    var defaultH = 768;
    var w = Math.max($('body').width(), window.innerWidth) - getScrollbarWidth();
    var h = Math.max($('body').height(), window.innerHeight);
    var points = [
        "0 0," +
        parseInt(557 / defaultW * w) + " " + h + "," +
        w + " " + h + "," +
        w + " 0," +
        parseInt(1287 / defaultW * w) + " " + parseInt(167 / defaultH * h),
        "0 " + h + ", " +
        parseInt(904 / defaultW * w) + " " + parseInt(576 / defaultH * h) + "," +
        w + " " + 0 + "," +
        w + " " + 0 + "," +
        w + " " + h
    ];

    return points;
}


/**
 * getDsInfo
 * 获取当前数据源信息
 */
export function getDsInfo(dsInfo, dsList) {
    var ds;
    for (var i = 0; i < dsList.length; i++) {
        if (isNaN(-dsInfo)) {
            //dsCode
            if (dsList[i].code == dsInfo) {
                ds = dsList[i];
                break;
            }
        } else {
            //dsId
            if (dsList[i].id == dsInfo) {
                ds = dsList[i];
                break;
            }
        }
    }

    if(ds){
        ds['dsId'] = ds.id;
        ds['dsCode'] = ds.code;
        ds['dsName'] = ds.name;
    }
    return ds;
}


/**
 * numAc
 * 数字累加动画
 *
 * @param dom 对象
 * @param num 最终数字  （可选项）  如果为空，会获取对象的INNerhtml
 *
 * numAc('.kk','200','56','50','')
 */
export function numAc(dom, num){
    if(num){
        var obj_text = num ;
    }else{
        var obj_text = parseInt($(dom).text());
    }

    var seep,       //递增间隔
      base,       //递增基数
      speed=100;  //递增速度

    if(obj_text<=0){
        seep = 0;
    } else if(obj_text<=1){
        seep = 0.2;
    } else if(obj_text<=10){
        seep = 2;
    } else if(obj_text <= 50){
        seep = 5;
    } else if(obj_text <= 100){
        seep = 10;
    } else {
        seep = 15;
    }
    base = seep;

    var obj = $(dom);
    var changed = parseInt(base);
    var change = setInterval(function(){
        changed = changed+=parseInt(seep);
        if(changed >= obj_text){
            clearInterval(change);
            obj.html(changed);
            obj.removeClass('numAcAnmintate');
            return false;
        }
        obj.html(changed);
    },speed);
}



/**
 * includeStyle
 * 添加外部样式
 */
export function includeStyle(url, id) {
    if(document.getElementById(id)){
        document.getElementById(id).setAttribute("href", url);
    } else {
        var style = document.createElement("link");
        style.type = "text/css";
        style.setAttribute("rel", "stylesheet");
        style.setAttribute("href", url);
        style.setAttribute("id", id);
        document.getElementsByTagName("head")[0].appendChild(style);
    }
}



/**
 * includeJs
 * 添加外部JS文件
 */
export function includeJs(url, callback) {
    var script = document.createElement("script");
    script.type = "text/javascript";
    script.async = !0;
    script.src = url;
    document.body.appendChild(script);

    //加载完毕回调
    if (script.readyState) { //for IE
        script.onreadystatechange = function() {
            if (script.readyState == "loaded" || script.readyState == "complete") {
                script.onreadystatechange = null;
                if (callback) {
                    callback();
                }
            }
        };
    } else { //for Others
        script.onload = function() {
            if (callback) {
                callback();
            }
        };
    }
}


/**
 * 列表重名排查
 * @param arr 排查对象列表
 *
 * var aa = ['a', 'b', 'a', 'b', 'a(1)'];
 * uniqueList(aa): ['a','b','a(2)','b(1)','a(1)']
 */
export function uniqueList(arr) {
    var tmp = [];
    var n = 0,
      newName = "";

    var _reName = function (originalName) {
        var flag = false;
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] == newName) {
                flag = true;
                break;
            }
        }

        if (flag) {
            n++;
            newName = originalName + '(' + n + ')';
            _reName(originalName)
        }
    };

    for (var i = 0; i < arr.length; i++) {
        if (tmp.indexOf(arr[i]) < 0) {
            //第一个不做重名处理

            tmp.push(arr[i]);
        } else {
            //重名

            n++;
            newName = arr[i] + '(' + n + ')';
            _reName(arr[i]);

            //替换新名称;
            arr[i] = newName;

            //重置状态
            n = 0;
            newName = "";
        }
    }
}


/**
 * getMyDsConfig
 * 返回DS config
 *
 */
export function getMyDsConfig(dsCode) {
	var dsConfig = {
		"dataSource": {
			//数据源管理

			"manageTpl": {  // 已授权数据源管理(针对mysql数据类型) src: /modules/dataSources/dataSources.tpl.html
				"showFile": false,   //file类型
				"showTable": false   //table类型
			},
			"editTpl": {    // 数据源编辑器(针对mysql数据类型) src: /modules/dataSources/edit/edit.tpl.html
				"tableClass": false, //表格样式区分
				"tableHdHide": false,//表格头部
				"sheetHide": false,  //sheet选项
				"helpMessage":false  //显示哪种帮助信息，默认false，显示mysql类型的
			},
			"dsJs": {   // 数据源管理控制器(针对mysql数据类型) src: /modules/dataSources/dataSources.ctrl.js
				"headMode_Custom": false //自定义表头数据
			},
			"ds": {
				"isAccountType": true,  //数据源连接类型: 账号型(默认) || 关系型
				"isShareFile": false,   //数据源文件是否具有分享特性(例如:GD)
				"isFolderFile": false,   //数据源文件是否具有文件夹特性(例如:GD,S3)
				"isCheckAccountListShowPermission":false,//数据源账号/链接列表是否验证拥有权限
				"checkAccountListShowPermissionCode":"",//验证权限时需要提供当前数据源要验证的权限code，例如：	datasource-standardRedshift-showAccountList-view
                "isDataBaseNameRequired":false,//连接数据库时，数据库名称是否必填，默认false,不必填
                "connect": "direct" //数据源连接类型: [直接连接-direct, 普通数据库-database, 特殊数据库-specialDb, googleDrive, upload](详情分类: http://confluence.ptmind.com/pages/viewpage.action?pageId=42304463)
			}
		},
		'editor': {//widget编辑器
			'source': {//选择数据源
				'account': true,//第一步标题为account
				'connection': false,//第一步标题为connection
				'isGoogleAuthorize': false,//是否是google相关授权（需要新打开窗口去授权）
				'addAuthorAccount': true,//添加新账户
				'addNewDB': false,//添加新数据库
				'open_blank': false,//添加授权需要新窗口打开过去的
				'profileName': false, //第三步标题为profile
				'fileName': false,//第三步标题是file
				'tableName': false,//第三步标题是table
				'accountClientName': false,//第三步标题是account client
				'adAccountName': false,//第三步标题是ad account
				'dataTypeName': false,//第三步标题是dataType
				'reportType': false,//第三步标题是reportType
				'pagesName':false,//Facebook Pages第三步是页面
				'ObjectReportName':false,//salesforce 第三步名称是Object/Report
				'profileHasTab':false,//档案列表是否需要标签页展示---目前只有salesforce数据源需要标签页展示
				'gaSearch': false,//专门给ga做的搜索profileId
				'dsSearch': false,//mysql等等数据源的搜索profileId
				'oneLayer': false,//数据结构为一层
				'twoLayer': false,//数据结构为两层
				'threeLayer': false,//数据源结构为三层
				'profileOfGa': false,//需要缓存的数据源类型--ga
				'accountOfTwoStep': false,//此数据源分为普通账号和mcc账号两层账户（google adwords）
				'profileOfFacebookAd': false,//需要缓存的数据源类型--facebook ad
				'profileOfPtengine': false,//需要缓存的数据源类型--ptengine
				'profileOfDoubleclick': false,//需要缓存的数据源类型--Doubleclick
				'profileOfDoubleclickCompound': false,//需要缓存的数据源类型--DoubleclickCompound
				'profileOfNormal': true,//不需要缓存的数据域类
				'profileLinkToEdit':false,//文件可以编辑链接，ga、google-adwords没有
				'profileLinkToEditInTable':true,//可链接的文件为最深层的表
				'profileOfSalesforce':false,//需要缓存的数据源类型--salesforce
				'profileOfPaypal':false,//需要缓存的数据源类型--paypal
				'profileByCategory':false,//根据分类来获取profileId的
				'profileOfApiRemote':false,//要走远程取档案列表/账户列表时，设置为true
				'addFile': true,//第三步底部，添加更多文件或者表
				'addNewFile': false,//添加更多文件的文案为file
				'addNewTable': false,//添加更多文件的文案为table
				'deleteMetricDimension': true,//切换账户的时候，是否需要清空已经选择好的指标维度
				'deleteDateDimensionsId':false,//切换账户的时候，是否哟啊清空已选择的时间查询字段
				'defaultTimeIsAllTime': false,//默认时间为all time
				'secondStepIsHide': false,//第二步选择账户是否隐藏(upload数据源不显示次步骤)
				'isReinitTimeByProfile':false,//切换profile时是否需要根据profile重新加载time控件
				'calculatedValueScopeIsTable':true, //计算指标是否针对table级别(或者针对数据源)
				'getProfileType': "table", //api:第三方数据源查询(旧), apiRemote:第三方数据源查询(新), table:库表结构查询, category:数据库中配置的分类列表, salesforce: 数据源salesforce特殊对应
				'profileNeedI18n': false,//是否需要国际化档案列表
				'isNeedGrap':false,//是否需要清空图表类型
				'getProfileListNotNeedAccount': false//yahoo 获取profile list时，接口不需要传account参数
			},
			'data': {//选择指标维度
				'metrics': false,//指标标题名称为metrics
				'value': true,//指标标题名称为value
				'metricsTips': false,//指标相关说明
				'valueTips': true,//value相关说明
				'metricsTwoLayer': false,//指标有两层结构的
				'metricsOneLayer': true,//指标有一层结构的
				'metricsOneLayerIsShowDataType':true,//一层指标结构时是否显示指标类型图标
				'metricsTwoLayerIsShowDataType':false,//二层指标结构时是否显示指标类型图标
				'metricsHasCount': true,//指标是否有count功能
				'metricsNeedI18n': true,//指标是否需要国际化
				'dimensions': false,//维度名称为dimensions
				'dimensionsTips': false,//维度相关说明
				'attributes': true,//维度名称为attributes
				'attributesTips': true,//attributes 相关说明
				'dimensionsOneLayer': true,//维度有一层结构
				'dimensionsOneLayerIsShowDataType': true,//一层维度结构时是否显示指标类型图标
				'dimensionsTwoLayerIsShowDataType': false,//一层维度结构时是否显示指标类型图标
				'dimensionsTwoLayer': false,//维度有两层结构
				'drewChartNeedMetrics':true, //绘制图表，必须至少有一个指标，光有一个维度不能取数绘图
				'drewChartComplexMode':false,//和上drewChartNeedMetrics配置相对，一个数据源在只有一个维度时是否能绘图是分情况的，为复合模式，目前只有salesforce是这样
				'dimensionsHasGroupBy': true,//维度是否有group by功能
				'translateTitle': false,//自动命名，是否需要国际化
				'dataNeedSave': false,//指标维度是否需要缓存
				'autoAddTimeDimension': false,//自动添加时间维度(ga和ptengine数据源在line下，会自动添加一个时间维度)
				'showWithProfileId':false,//显示指标维度时，匹配profileId与指标维度id，如果相同则显示
				'isSupportQuarterOfGroupBy':true,//在使用日期粒度时，当前数据源是否支持季度粒度，默认支持
				'isSupportWeekOfGroupBy':true,//在使用日期粒度时，当前数据源是否支持周粒度，默认支持
				'calculatedValueScopeShowAll': true,//计算指标是否显示全部(或者需要针对数据类型过滤)
				'calculatedValueBeta': false, //计算指标是否为beta版
				'supportAggregateFunctionArrayOfString':["COUNTA", "COUNTUNIQUE"],//在聚合函数中，字符串类型指标能支持的聚合函数数组
				'supportAggregateFunctionArrayOfNumber':["SUM", "AVERAGE", "MAX", "MIN", "MEDIAN", "COUNTA", "COUNTUNIQUE", "STDEV", "VAR"],//在聚合函数中，数值类型指标能支持的聚合函数数组
				'isSupportFunc': true, //计算指标是否支持聚合函数
				'needDefaultDimension': false //是否需要自动选择维度，目前只有yahoo ADYss数据源需要
			},
			'filter': {//设置过滤器
				'translateMetricName': false,//是否显示国际化后的指标名称
				'savedFilters': false,//是否有已经保存的过滤器
				'createNewFilterRadio': false,//是否显示‘新建过滤器选项’
				'createNewFilter': false,//创建一个新的过滤器（GA文案）
				'setFilter': true,//设置过滤器条件(GA外的文案）
				'containerListOfShow': false,//Ga才有的列表，包含和不包含
				'containerListOfType': false,//Ga才有的列表,用户和会话，app也有这个列表
				'search': false,//第一个条件是否支持搜索
				'twoLayer': false,//第一步条件是否为两层结构
				'oneLayer': true,//第一步条件是否为一层结构
				'isShowOfCondition':false,//第二步是否默认显示
				'neverShowCondition':false,//第二步（有大于小于等于包含下拉菜单的那一步）永远不显示
				'conditionOfCheckbox': false,//第二步的选项为复选框
				'smallCheckbox': false,//第二步的复选框为比较窄的条件
				'getForDimensionsAndMetrics':false,//过滤器是否通过指标维度获取LINK_FILTER_LIST
				'isCache':false,//过滤器列表是否缓存
				'i18nByFilterList':false,//国际化文案是否使用filter列表来
				'isSegment': false,//是否有segment列表
				'isFilters': true,//是否有filter列表
				'isRemoteGetValues':false,//是否远程过滤器选项数据，暂时加上，没有被使用（为了减少当前测试压力），之后需要使用上
				'isNeedOr': true, //添加过滤器的条件（and or中的or）是否需要，默认需要
				'reGetFilterList': false, //点击用户或者会话时，需要重新获取过滤器列表，目前只有ptapp数据源有这个
				'needIsNull': false//某些数据源，第二步的条件可以选择is_null 和 is_not_null
			},
			'time':{//设置时间
				'isTimeDisable':false,//时间是否默认可点击，如果不是，则会走根据指标维度判断的方式
				'isCheckDMHasTime':true,//是否检查指标、维度中是否存在时间类型，如果不检查则直接启用时间设置
				'defaultSelectTime':0,//默认选中的时间，0=7days，1=allTime
				'allTimeIsShow': false,//是否显示All Time选项()
				'isTimeDisableByProfileIdList':[],//时间默认是否可点击的判断依据，通过profileId来判断时间是否可点
				'dataKeyOfInherit': true//默认时间是否参加维态，execl/gd/s3/salesforce不参加维态
			},
			'chart':{
				'showMap':false,//地图是否可用
				'useNumberChart':true,//是否可以用Number类型的图表,目前只有doubleclick不能用
				'needSelectProfileId': false //是否需要根据profileId不同而做特殊处理，yahoo ysss数据源需要根据profileId，显示不同的图表
			}
		},
		'chart' : {// 图表
			'table': {
				'useDefaultSort': true // table排序使用前端默认排序，数据库类型数据源使用数据库排序需要重新取数
			},
			'cacheWidgetHistoryData': false // 是否缓存历史数据（刷新时不发送数据请求）如GA等Api类型为true， mysql等为false
		},
		'linkData': {
			//批量授权

			'hideStepThree': false  //是否隐藏第三步(getProfile)
		},
		'test':false //对暂不公开的功能,可以配置此字段,在需要隐藏的地方调用即可.
	};

	switch (dsCode) {
		case "googleanalysis":
			dsConfig.editor.source.isGoogleAuthorize = true;
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.profileName = true;
			dsConfig.editor.source.gaSearch = true;
			dsConfig.editor.source.threeLayer = true;
			dsConfig.editor.source.profileOfGa = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.deleteMetricDimension = false;
			dsConfig.editor.source.calculatedValueScopeIsTable = false;
			dsConfig.editor.source.getProfileType = 'api';
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.metricsTwoLayer = true;
			dsConfig.editor.data.metricsOneLayer = false;
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.dimensionsOneLayer = false;
			dsConfig.editor.data.dimensionsTwoLayer = true;
			dsConfig.editor.data.dimensionsHasGroupBy = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.autoAddTimeDimension = true;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.savedFilters = true;
			dsConfig.editor.filter.createNewFilterRadio = true;
			dsConfig.editor.filter.createNewFilter = true;
			dsConfig.editor.filter.setFilter = false;
			dsConfig.editor.filter.containerListOfShow = true;
			dsConfig.editor.filter.containerListOfType = true;
			dsConfig.editor.filter.search = true;
			dsConfig.editor.filter.twoLayer = true;
			dsConfig.editor.filter.oneLayer = false;
			//dsConfig.editor.filter.isShowOfCondition = true;
			dsConfig.editor.filter.isSegment = true;
			dsConfig.editor.filter.isCache = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.chart.cacheWidgetHistoryData = true;
			dsConfig.editor.chart.showMap = true;
			break;
		case "ptconsole":
			dsConfig.editor.source.account = false;
			dsConfig.editor.source.connection = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.addFile = false;
			break;
		case "googleadwords":
			dsConfig.editor.source.isGoogleAuthorize = true;
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.accountClientName = true;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.accountOfTwoStep = false;
			dsConfig.editor.source.deleteMetricDimension = false;
			dsConfig.editor.source.calculatedValueScopeIsTable = false;
			dsConfig.editor.source.getProfileType = 'api';
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.metricsTwoLayer = true;
			dsConfig.editor.data.metricsOneLayer = false;
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.dimensionsOneLayer = false;
			dsConfig.editor.data.dimensionsTwoLayer = true;
			dsConfig.editor.data.dimensionsHasGroupBy = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.filter.neverShowCondition = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.isCache = true;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.chart.cacheWidgetHistoryData = true;
			dsConfig.editor.filter.isNeedOr = false;
			dsConfig.editor.chart.showMap = true;
			break;
		case "upload":
			/*dsConfig.editor.source.account = false;
			 dsConfig.editor.source.connection = true;
			 dsConfig.editor.source.dsSearch = true;
			 dsConfig.editor.source.oneLayer = true;
			 dsConfig.editor.source.addFile = false;*/
			dsConfig.dataSource.ds.isAccountType = false;
            dsConfig.dataSource.ds.connect = 'upload';
			dsConfig.dataSource.manageTpl.showFile = true;
			dsConfig.editor.source.fileName = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.defaultTimeIsAllTime = true;
			dsConfig.editor.source.profileLinkToEdit = true;
			dsConfig.editor.source.profileLinkToEditInTable = false;
			dsConfig.editor.source.secondStepIsHide = true;

			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.metricsNeedI18n = false;

			dsConfig.editor.time.defaultSelectTime = 1;
			dsConfig.editor.time.allTimeIsShow = true;
			dsConfig.editor.filter.needIsNull = true;
			dsConfig.editor.time.dataKeyOfInherit = false;
			break;
		case "mysql":
			dsConfig.dataSource.manageTpl.showTable = true;
			dsConfig.dataSource.editTpl.tableClass = true;
			dsConfig.dataSource.editTpl.tableHdHide = true;
			dsConfig.dataSource.editTpl.sheetHide = true;
			dsConfig.dataSource.dsJs.headMode_Custom = true;
			dsConfig.dataSource.ds.isAccountType = false;
            dsConfig.dataSource.ds.connect = 'database';

			dsConfig.editor.source.account = false;
			dsConfig.editor.source.connection = true;
			dsConfig.editor.source.addAuthorAccount = false;
			dsConfig.editor.source.addNewDB = true;
			dsConfig.editor.source.tableName = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.addNewTable = true;
			dsConfig.editor.source.profileLinkToEdit = true;

			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.metricsNeedI18n = false;

			dsConfig.chart.table.useDefaultSort = false;
			dsConfig.editor.filter.needIsNull = true;
			break;
		case "sqlserver":
			dsConfig.dataSource.manageTpl.showTable = true;
			dsConfig.dataSource.editTpl.tableClass = true;
			dsConfig.dataSource.editTpl.tableHdHide = true;
			dsConfig.dataSource.editTpl.sheetHide = true;
			dsConfig.dataSource.dsJs.headMode_Custom = true;
			dsConfig.dataSource.ds.isAccountType = false;
            dsConfig.dataSource.ds.connect = 'database';

			dsConfig.editor.source.account = false;
			dsConfig.editor.source.connection = true;
			dsConfig.editor.source.addAuthorAccount = false;
			dsConfig.editor.source.addNewDB = true;
			dsConfig.editor.source.tableName = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.addNewTable = true;
			dsConfig.editor.source.profileLinkToEdit = true;

			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.metricsNeedI18n = false;

			dsConfig.chart.table.useDefaultSort = false;
			dsConfig.dataSource.ds.isDataBaseNameRequired = true;
			dsConfig.editor.filter.needIsNull = true;
			break;
		case "googledrive":
			dsConfig.dataSource.manageTpl.showFile = true;
			dsConfig.dataSource.ds.isShareFile = true;
			dsConfig.dataSource.ds.isFolderFile = true;
            dsConfig.dataSource.ds.connect = 'googleDrive';
			dsConfig.editor.source.fileName = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.addNewFile = true;
			dsConfig.editor.source.defaultTimeIsAllTime = true;
			dsConfig.editor.source.profileLinkToEdit = true;
			dsConfig.editor.source.profileLinkToEditInTable = false;

			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.metricsNeedI18n = false;

			dsConfig.editor.time.defaultSelectTime = 1;
			dsConfig.editor.time.allTimeIsShow = true;
			dsConfig.editor.filter.needIsNull = true;
			dsConfig.editor.time.dataKeyOfInherit = false;
			break;
		case "s3":
			dsConfig.dataSource.manageTpl.showFile = true;
			dsConfig.dataSource.ds.isAccountType = false;
			dsConfig.dataSource.ds.isFolderFile = true;
            dsConfig.dataSource.ds.connect = 'database';
			dsConfig.editor.source.fileName = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.addNewFile = true;
			dsConfig.editor.source.defaultTimeIsAllTime = true;
			dsConfig.editor.source.profileLinkToEdit = true;
			dsConfig.editor.source.profileLinkToEditInTable = false;

			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.metricsNeedI18n = false;

			dsConfig.editor.time.defaultSelectTime = 1;
			dsConfig.editor.time.allTimeIsShow = true;
			dsConfig.editor.filter.needIsNull = true;
			dsConfig.editor.time.dataKeyOfInherit = false;
			break;
		case "mysqlAmazonRds":
			dsConfig.dataSource.manageTpl.showTable = true;
			dsConfig.dataSource.editTpl.tableClass = true;
			dsConfig.dataSource.editTpl.tableHdHide = true;
			dsConfig.dataSource.editTpl.sheetHide = true;
			dsConfig.dataSource.dsJs.headMode_Custom = true;
			dsConfig.dataSource.ds.isAccountType = false;
            dsConfig.dataSource.ds.connect = 'database';

			dsConfig.editor.source.account = false;
			dsConfig.editor.source.connection = true;
			dsConfig.editor.source.addAuthorAccount = false;
			dsConfig.editor.source.addNewDB = true;
			dsConfig.editor.source.tableName = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.addNewTable = true;
			dsConfig.editor.source.profileLinkToEdit = true;

			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.metricsNeedI18n = false;

			dsConfig.chart.table.useDefaultSort = false;
			break;
		case "postgre":
			dsConfig.dataSource.manageTpl.showTable = true;
			dsConfig.dataSource.editTpl.tableClass = true;
			dsConfig.dataSource.editTpl.tableHdHide = true;
			dsConfig.dataSource.editTpl.sheetHide = true;
			dsConfig.dataSource.dsJs.headMode_Custom = true;
			dsConfig.dataSource.ds.isAccountType = false;
            dsConfig.dataSource.ds.connect = 'database';

			dsConfig.editor.source.account = false;
			dsConfig.editor.source.connection = true;
			dsConfig.editor.source.addAuthorAccount = false;
			dsConfig.editor.source.addNewDB = true;
			dsConfig.editor.source.tableName = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.addNewTable = true;
			dsConfig.editor.source.profileLinkToEdit = true;

			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.metricsNeedI18n = false;

			dsConfig.chart.table.useDefaultSort = false;
			dsConfig.dataSource.ds.isDataBaseNameRequired = true;
			dsConfig.editor.filter.needIsNull = true;
			break;
		case "redshift":
			dsConfig.dataSource.manageTpl.showTable = true;
			dsConfig.dataSource.editTpl.tableClass = true;
			dsConfig.dataSource.editTpl.tableHdHide = true;
			dsConfig.dataSource.editTpl.sheetHide = true;
			dsConfig.dataSource.dsJs.headMode_Custom = true;
			dsConfig.dataSource.ds.isAccountType = false;
            dsConfig.dataSource.ds.connect = 'database';

			dsConfig.editor.source.account = false;
			dsConfig.editor.source.connection = true;
			dsConfig.editor.source.addAuthorAccount = false;
			dsConfig.editor.source.addNewDB = true;
			dsConfig.editor.source.tableName = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.addNewTable = true;
			dsConfig.editor.source.profileLinkToEdit = true;

			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.metricsNeedI18n = false;

			dsConfig.chart.table.useDefaultSort = false;
			dsConfig.dataSource.ds.isDataBaseNameRequired = true;
			dsConfig.editor.filter.needIsNull = true;
			break;
		case "standardRedshift":
			dsConfig.dataSource.manageTpl.showTable = true;
			dsConfig.dataSource.editTpl.tableClass = true;
			dsConfig.dataSource.editTpl.tableHdHide = true;
			dsConfig.dataSource.editTpl.sheetHide = true;
			dsConfig.dataSource.dsJs.headMode_Custom = true;
			dsConfig.dataSource.ds.isAccountType = false;
            dsConfig.dataSource.ds.connect = 'database';
			dsConfig.dataSource.ds.isCheckAccountListShowPermission = true;
			dsConfig.dataSource.ds.checkAccountListShowPermissionCode = "datasource-standardRedshift-showAccountList-view";

			dsConfig.editor.source.account = false;
			dsConfig.editor.source.connection = true;
			dsConfig.editor.source.addAuthorAccount = false;
			dsConfig.editor.source.addNewDB = true;
			dsConfig.editor.source.tableName = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.addNewTable = true;
			dsConfig.editor.source.profileLinkToEdit = true;

			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.metricsNeedI18n = false;

			dsConfig.chart.table.useDefaultSort = false;
			dsConfig.dataSource.ds.isDataBaseNameRequired = true;
			break;
		case "auroraAmazonRds":
			dsConfig.dataSource.manageTpl.showTable = true;
			dsConfig.dataSource.editTpl.tableClass = true;
			dsConfig.dataSource.editTpl.tableHdHide = true;
			dsConfig.dataSource.editTpl.sheetHide = true;
			dsConfig.dataSource.dsJs.headMode_Custom = true;
			dsConfig.dataSource.ds.isAccountType = false;
            dsConfig.dataSource.ds.connect = 'database';

			dsConfig.editor.source.account = false;
			dsConfig.editor.source.connection = true;
			dsConfig.editor.source.addAuthorAccount = false;
			dsConfig.editor.source.addNewDB = true;
			dsConfig.editor.source.tableName = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.addNewTable = true;
			dsConfig.editor.source.profileLinkToEdit = true;

			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.metricsNeedI18n = false;

			dsConfig.chart.table.useDefaultSort = false;
			dsConfig.editor.filter.needIsNull = true;
			break;
		case "facebook":
			dsConfig.editor.source.account = false;
			dsConfig.editor.source.connection = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.twoLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.calculatedValueScopeIsTable = false;
			dsConfig.editor.data.metricsNeedI18n = false;
			dsConfig.editor.chart.showMap = true;
			break;
		case "facebookad":
			dsConfig.editor.source.isGoogleAuthorize = true;
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.adAccountName = true;
			dsConfig.editor.source.profileOfFacebookAd = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.deleteMetricDimension = false;
			dsConfig.editor.source.calculatedValueScopeIsTable = false;
			dsConfig.editor.source.getProfileType = 'api';
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.metricsTwoLayer = true;
			dsConfig.editor.data.metricsOneLayer = false;
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.metricsNeedI18n = true;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.dimensionsOneLayer = false;
			dsConfig.editor.data.dimensionsTwoLayer = true;
			dsConfig.editor.data.dimensionsHasGroupBy = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.filter.neverShowCondition = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.isCache = true;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.chart.cacheWidgetHistoryData = true;
			dsConfig.editor.filter.isNeedOr = false;
			dsConfig.editor.chart.showMap = true;
			break;
		case "ptengine":
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.profileName = true;
			dsConfig.editor.source.profileOfPtengine = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.deleteMetricDimension = false;
			dsConfig.editor.source.calculatedValueScopeIsTable = false;
			dsConfig.editor.source.getProfileType = 'api';
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.metricsTwoLayer = true;
			dsConfig.editor.data.metricsOneLayer = false;
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.dimensionsOneLayer = false;
			dsConfig.editor.data.dimensionsTwoLayer = true;
			dsConfig.editor.data.dimensionsHasGroupBy = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.autoAddTimeDimension = true;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.isShowOfCondition = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.isCache = true;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.editor.filter.isSegment = true;
			dsConfig.editor.filter.isFilters = false;
			dsConfig.chart.cacheWidgetHistoryData = true;
			dsConfig.editor.filter.isNeedOr = false;
			dsConfig.editor.chart.showMap = true;
			break;
		case "ptapp":
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.profileName = true;
			dsConfig.editor.source.profileOfPtengine = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.deleteMetricDimension = false;
			dsConfig.editor.source.calculatedValueScopeIsTable = false;
			dsConfig.editor.source.getProfileType = 'apiRemote';
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.metricsTwoLayer = true;
			dsConfig.editor.data.metricsOneLayer = false;
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.dimensionsOneLayer = false;
			dsConfig.editor.data.dimensionsTwoLayer = true;
			dsConfig.editor.data.dimensionsHasGroupBy = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.autoAddTimeDimension = true;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.containerListOfType = true;
			dsConfig.editor.filter.reGetFilterList = true;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.editor.filter.isSegment = true;
			dsConfig.editor.filter.isFilters = false;
			dsConfig.editor.filter.twoLayer = true;
			dsConfig.editor.filter.oneLayer = false;
			break;
		case "bigquery":
			dsConfig.dataSource.manageTpl.showTable = true;
			dsConfig.dataSource.editTpl.tableClass = true;
			dsConfig.dataSource.editTpl.tableHdHide = true;
			dsConfig.dataSource.editTpl.sheetHide = true;
			dsConfig.dataSource.dsJs.headMode_Custom = true;
            dsConfig.dataSource.ds.connect = 'specialDb';

			dsConfig.editor.source.tableName = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.threeLayer = true;
			dsConfig.editor.source.addNewTable = true;
			dsConfig.editor.source.profileLinkToEdit = true;

			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.metricsNeedI18n = false;

			dsConfig.chart.table.useDefaultSort = false;
			dsConfig.editor.filter.needIsNull = true;
			break;
		case "doubleclick":
            dsConfig.dataSource.ds.connect = 'database'; //暂不开通onboarding流程
			dsConfig.editor.source.isGoogleAuthorize = true;
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.accountClientName = true;
			dsConfig.editor.source.profileOfDoubleclick = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.deleteMetricDimension = false;
			dsConfig.editor.source.calculatedValueScopeIsTable = false;
			dsConfig.editor.source.getProfileType = 'api';
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.metricsTwoLayer = true;
			dsConfig.editor.data.metricsOneLayer = false;
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.dimensionsOneLayer = false;
			dsConfig.editor.data.dimensionsTwoLayer = true;
			dsConfig.editor.data.dimensionsHasGroupBy = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.calculatedValueBeta = true;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.filter.neverShowCondition = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.isCache = true;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.editor.chart.useNumberChart = false;
			break;
		case "salesforce":
            dsConfig.dataSource.ds.connect = 'database'; //暂不开通onboarding流程
			dsConfig.editor.source.isGoogleAuthorize = true;
			dsConfig.editor.source.profileName = false;
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.ObjectReportName = true;
			dsConfig.editor.source.accountOfTwoStep = false;
			dsConfig.editor.source.deleteMetricDimension = true;
			dsConfig.editor.source.deleteDateDimensionsId = true;
			dsConfig.editor.source.profileOfSalesforce = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.defaultTimeIsAllTime = true;
			dsConfig.editor.source.dsSearch = true;
			dsConfig.editor.source.profileHasTab = true;
			dsConfig.editor.source.getProfileType = 'salesforce';
			dsConfig.editor.data.metricsHasCount = true;
			dsConfig.editor.data.metricsNeedI18n = false;
			dsConfig.editor.data.metricsSearch = true;
			dsConfig.editor.data.dimensionsSearch = true;
			dsConfig.editor.data.showWithProfileId = false;
			dsConfig.editor.data.metricsTwoLayerIsShowDataType = true;
			dsConfig.editor.data.metricsOneLayer = false;
			dsConfig.editor.data.metricsTwoLayer = true;
			dsConfig.editor.data.dimensionsOneLayer = false;
			dsConfig.editor.data.dimensionsTwoLayer = true;
			dsConfig.editor.data.dimensionsTwoLayerIsShowDataType = true;
			dsConfig.editor.data.isSupportQuarterOfGroupBy = false;
			dsConfig.editor.data.isSupportWeekOfGroupBy = false;
			dsConfig.editor.data.supportAggregateFunctionArrayOfNumber=["SUM", "AVERAGE", "MAX", "MIN","COUNTA", "COUNTUNIQUE"];//在聚合函数中，数值类型指标能支持的聚合函数数组
			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.data.drewChartComplexMode = true;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.filter.isCache = false;
			dsConfig.editor.filter.twoLayer = true;
			dsConfig.editor.filter.oneLayer = false;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.time.defaultSelectTime = 1;
			dsConfig.editor.time.allTimeIsShow = true;
			dsConfig.editor.time.dataKeyOfInherit = false;
			dsConfig.linkData.hideStepThree = true;
			dsConfig.test = true;//此数据源下计算指标暂不公开
			dsConfig.chart.cacheWidgetHistoryData = true;// TODO：目前程序中会将非当天的数据缓存7天，但实际上历史数据会变化，如果后台实现逻辑修改此处需调整
			break;
		case "doubleclickCompound":
            dsConfig.dataSource.ds.connect = 'database'; //暂不开通onboarding流程
			dsConfig.editor.source.isGoogleAuthorize = true;
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.accountClientName = true;
			dsConfig.editor.source.profileOfDoubleclickCompound = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.deleteMetricDimension = false;
			dsConfig.editor.source.calculatedValueScopeIsTable = false;
			dsConfig.editor.source.getProfileType = 'api';
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.metricsTwoLayer = true;
			dsConfig.editor.data.metricsOneLayer = false;
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.dimensionsOneLayer = false;
			dsConfig.editor.data.dimensionsTwoLayer = true;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.calculatedValueBeta = true;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.filter.neverShowCondition = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.isCache = true;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.test = true;//此数据源下计算指标暂不公开
			break;
		case "paypal":
            dsConfig.dataSource.ds.connect = 'database'; //暂不开通onboarding流程
			dsConfig.editor.source.isGoogleAuthorize = true;
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.profileName = true;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.accountOfTwoStep = false;
			dsConfig.editor.source.deleteMetricDimension = true;
			dsConfig.editor.source.profileOfPaypal = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.getProfileType = 'category';
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.metricsSearch = true;
			dsConfig.editor.data.dimensionsSearch = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.showWithProfileId = true;
			dsConfig.editor.data.metricsOneLayerIsShowDataType = false;
			dsConfig.editor.data.dimensionsOneLayerIsShowDataType = false;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.filter.isCache = false;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.twoLayer = false;
			dsConfig.editor.filter.oneLayer = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.time.isCheckDMHasTime = false;
			dsConfig.linkData.hideStepThree = true;
			dsConfig.chart.cacheWidgetHistoryData = true;// TODO：目前程序中会将非当天的数据缓存7天，但实际上历史数据会变化，如果后台实现逻辑修改此处需调整
			break;
		case "stripe":
            dsConfig.dataSource.ds.connect = 'database'; //暂不开通onboarding流程
			dsConfig.editor.source.isGoogleAuthorize = true;
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.profileName = true;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.accountOfTwoStep = false;
			dsConfig.editor.source.deleteMetricDimension = true;
			dsConfig.editor.source.profileByCategory = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.getProfileType = 'category';
			dsConfig.editor.data.metricsSearch = true;
			dsConfig.editor.data.dimensionsSearch = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.metricsHasCount = true;
			dsConfig.editor.data.showWithProfileId = true;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.isSupportQuarterOfGroupBy = false;
			dsConfig.editor.data.isSupportWeekOfGroupBy = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.filter.isCache = false;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.twoLayer = false;
			dsConfig.editor.filter.oneLayer = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.time.isCheckDMHasTime = false;
			dsConfig.editor.data.supportAggregateFunctionArrayOfNumber=["SUM", "AVERAGE", "MAX", "MIN","COUNTA","COUNTUNIQUE"];//在聚合函数中，数值类型指标能支持的聚合函数数组
			dsConfig.linkData.hideStepThree = true;
			dsConfig.chart.cacheWidgetHistoryData = true;// TODO：目前程序中会将非当天的数据缓存7天，但实际上历史数据会变化，如果后台实现逻辑修改此处需调整
			break;
		case "mailchimp":
            dsConfig.dataSource.ds.connect = 'database'; //暂不开通onboarding流程
			dsConfig.editor.source.isGoogleAuthorize = true;
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.profileName = true;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.accountOfTwoStep = false;
			dsConfig.editor.source.deleteMetricDimension = true;
			dsConfig.editor.source.profileByCategory = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.getProfileType = 'category';
			dsConfig.editor.source.isReinitTimeByProfile = true;
			dsConfig.editor.source.deleteDateDimensionsId = true;
			dsConfig.editor.data.metricsSearch = true;
			dsConfig.editor.data.dimensionsSearch = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.metricsHasCount = true;
			dsConfig.editor.data.showWithProfileId = true;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.isSupportQuarterOfGroupBy = false;
			dsConfig.editor.data.isSupportWeekOfGroupBy = false;
			dsConfig.editor.data.calculatedValueScopeShowAll = false;
			dsConfig.editor.data.drewChartNeedMetrics = false;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.filter.isCache = false;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.twoLayer = false;
			dsConfig.editor.filter.oneLayer = true;
			dsConfig.editor.time.isTimeDisable = true;
			// dsConfig.editor.time.isCheckDMHasTime = false;
			dsConfig.editor.time.isTimeDisableByProfileIdList = [262];
			dsConfig.editor.data.supportAggregateFunctionArrayOfNumber=["SUM", "AVERAGE", "MAX", "MIN","COUNTA","COUNTUNIQUE"];//在聚合函数中，数值类型指标能支持的聚合函数数组
			dsConfig.linkData.hideStepThree = true
			dsConfig.chart.cacheWidgetHistoryData = true;
			break;
		case "googleadsense" :
            dsConfig.dataSource.ds.connect = 'database'; //暂不开通onboarding流程
			dsConfig.editor.source.isGoogleAuthorize = true;
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.accountClientName = true;
			dsConfig.editor.source.profileOfApiRemote = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.deleteMetricDimension = false;
			dsConfig.editor.source.calculatedValueScopeIsTable = false;
			dsConfig.editor.source.getProfileType = 'apiRemote';
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.metricsTwoLayer = true;
			dsConfig.editor.data.metricsOneLayer = false;
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.dimensionsOneLayer = false;
			dsConfig.editor.data.dimensionsTwoLayer = true;
			dsConfig.editor.data.dimensionsHasGroupBy = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.filter.neverShowCondition = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.isCache = true;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.chart.cacheWidgetHistoryData = true;
			dsConfig.editor.chart.showMap = true;
			break;
		case "facebookPages" :
            dsConfig.dataSource.ds.connect = 'database'; //暂不开通onboarding流程
			dsConfig.editor.source.isGoogleAuthorize = true;
			dsConfig.editor.source.open_blank = true;
			dsConfig.editor.source.pagesName = true;
			dsConfig.editor.source.profileOfApiRemote = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.deleteMetricDimension = false;
			dsConfig.editor.source.getProfileType = 'apiRemote';
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.metricsTwoLayer = true;
			dsConfig.editor.data.metricsOneLayer = false;
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.dimensionsOneLayer = false;
			dsConfig.editor.data.dimensionsTwoLayer = true;
			dsConfig.editor.data.dimensionsHasGroupBy = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.filter.neverShowCondition = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.isCache = true;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.chart.cacheWidgetHistoryData = true;
			break;
		case "yahooAdsYDN" :
            dsConfig.dataSource.ds.connect = 'database'; //暂不开通onboarding流程
			dsConfig.editor.source.accountClientName = true;
			dsConfig.editor.source.profileOfApiRemote = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.deleteMetricDimension = false;
			dsConfig.editor.source.getProfileType = 'apiRemote';
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.metricsTwoLayer = true;
			dsConfig.editor.data.metricsOneLayer = false;
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.dimensionsOneLayer = false;
			dsConfig.editor.data.dimensionsTwoLayer = true;
			dsConfig.editor.data.dimensionsHasGroupBy = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.filter.neverShowCondition = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.isCache = true;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.linkData.hideStepThree = true;
			dsConfig.chart.cacheWidgetHistoryData = true;
			dsConfig.editor.source.getProfileListNotNeedAccount = true;
			break;
		case "yahooAdsSS" : //31
			dsConfig.dataSource.ds.connect = 'database'; //暂不开通onboarding流程
			dsConfig.editor.source.reportType = true;
			dsConfig.editor.source.profileOfApiRemote = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.oneLayer = true;
			dsConfig.editor.source.addFile = false;
			dsConfig.editor.source.deleteMetricDimension = true;
			dsConfig.editor.source.profileByCategory = true;
			dsConfig.editor.source.profileOfNormal = false;
			dsConfig.editor.source.getProfileType = 'category';
			dsConfig.editor.source.profileNeedI18n = true;
			dsConfig.editor.source.isNeedGrap = true;
			dsConfig.editor.data.metrics = true;
			dsConfig.editor.data.metricsTips = true;
			dsConfig.editor.data.value = false;
			dsConfig.editor.data.valueTips = false;
			dsConfig.editor.data.metricsOneLayer = true;
			dsConfig.editor.data.metricsHasCount = false;
			dsConfig.editor.data.showWithProfileId = true;
			dsConfig.editor.data.dimensions = true;
			dsConfig.editor.data.attributes = false;
			dsConfig.editor.data.dimensionsTips = true;
			dsConfig.editor.data.attributesTips = false;
			dsConfig.editor.data.dimensionsOneLayer = true;
			dsConfig.editor.data.dimensionsTwoLayer = false;
			dsConfig.editor.data.dimensionsHasGroupBy = false;
			dsConfig.editor.data.translateTitle = true;
			dsConfig.editor.data.dataNeedSave = true;
			dsConfig.editor.data.isSupportFunc = false;
			dsConfig.editor.data.needDefaultDimension = true;
			dsConfig.editor.filter.translateMetricName = true;
			dsConfig.editor.filter.conditionOfCheckbox = true;
			dsConfig.editor.filter.neverShowCondition = true;
			dsConfig.editor.time.isTimeDisable = true;
			dsConfig.editor.filter.getForDimensionsAndMetrics = true;
			dsConfig.editor.filter.isCache = true;
			dsConfig.editor.filter.i18nByFilterList = true;
			dsConfig.chart.cacheWidgetHistoryData = true;
			dsConfig.editor.filter.isNeedOr = false;
			dsConfig.linkData.hideStepThree = true;
			dsConfig.editor.chart.needSelectProfileId = true;
			dsConfig.editor.source.getProfileListNotNeedAccount = true;
			break;
	}
	return dsConfig;
}
