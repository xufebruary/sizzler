import resourceWrapper from './resourceWrapper';
import cookieUtils from 'utils/cookie.utils';
import consts from 'configs/const.config';
import {
	LINK_API_VERSION,
	getLocalLang
} from 'components/modules/common/common';

var basicUrl = LINK_API_VERSION + 'users';
var DefaultUserConstructor = resourceWrapper([
	{
		name: 'signup',
		method: 'post',
		url: '/pt/users/signup/official'
	},
	//获取用户设置信息
	{
		name: 'getUsersSettingsInfo',
		method: 'get',
		url: '/pt/users/settings/info/{spaceId}'
	},
	//更新用户设置信息
	{
		name: 'updateUsersSettingsInfo',
		method: 'post',
		url: '/pt/users/settings/update'
	},
	// 找回密码
	{
		name: 'sendEmailToFindPassword',
		method: 'post',
		url: basicUrl+'/forgot/{email}?domain={host}'
	},
	//发送激活邮件
	{
		name: 'sendActivateEmail',
		method: 'post',
		url: basicUrl+'/active/repeat/{email}'
	},
	//激活账户
	{
		name: 'activateUsers',
		method: 'put',
		url: basicUrl+'/active'
	}
]);

UserResource.$inject = ['$rootScope', '$http', '$q'];

function UserResource($rootScope, $http, $q) {
	DefaultUserConstructor.call(this, $http, $q);
	this.$rootScope = $rootScope;
}

UserResource.prototype = Object.create(DefaultUserConstructor.prototype);

UserResource.prototype.constructor = UserResource;

/**
 * 清除当前用户本地存储信息
 */
UserResource.prototype.clear = function () {
	// 清除cookie中用户信息
	cookieUtils.remove('sid');
	cookieUtils.remove('ptEmail');
	cookieUtils.remove('ptId');

	// 清除localStorage中信息
	if (window.localStorage) {
		localStorage.removeItem("ptmm");
		localStorage.removeItem("sid");
		localStorage.removeItem("gid");
		localStorage.removeItem(consts.I18N_KEY);

		this.$rootScope.sid = null;
		this.$rootScope.userInfo = null;
	}
};

/**
 * 添加用户信息到本地
 * @param info
 * @param isRememberme
 */
UserResource.prototype.addLocal = function (info, isRememberme) {
	localStorage.setItem("ptnm", info.userEmail);
	cookieUtils.set("ptId", info.uid);
	cookieUtils.set("ptEmail", info.ptEmail);

	//如果记住密码，则保存
	if (isRememberme) {
		localStorage.setItem("sid", info.sid);
		localStorage.setItem("ptmm", info.userPassword);
	}
};

export default UserResource;
