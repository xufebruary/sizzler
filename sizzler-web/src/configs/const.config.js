/**
 * 这些常量会在编译阶段由webpack插件处理
 * 根据命令行参数中的环境信息,动态读取env.config.js,然后替换
 * @type {string}
 */

var WEB_UI_URL = "${ptone.web.ui.url}";
var BACK_UI_URL = "${ptone.ptengine.url}";
var WEB_SOCKET_URL = "${ptone.websocket.url}";
var WEB_MIDDLE_URL = "${ptone.web.middle.url}";
var SITE_EVENT_ANALYTICS = '${collect.server.url}';
var FACEBOOK_SHARE_APP_ID_JP = "${facebook.share.app.id.jp}";
var FACEBOOK_SHARE_APP_ID_COM = "${facebook.share.app.id.com}";

// 国际化key
var I18N_KEY = "DATADECK_LANG_SETTING";

module.exports = {
	WEB_UI_URL,
	BACK_UI_URL,
	WEB_SOCKET_URL,
	WEB_MIDDLE_URL,
	SITE_EVENT_ANALYTICS,
	FACEBOOK_SHARE_APP_ID_JP,
	FACEBOOK_SHARE_APP_ID_COM,

	I18N_KEY
};
