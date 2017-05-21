var Base64 = require('js-base64').Base64;
import cookieUtils from 'utils/cookie.utils';
import md5 from 'js-md5';
import ProductConfig from 'configs/product.config';
import {
	getLocalLang
} from 'components/modules/common/common';
import utils from 'utils/utils';

SpaceService.$inject = ['SpaceResources', 'UserResources', 'publicDataSrv', '$q'];

// 私有方法
var privateMethods = {
	/**
	 * 移除localStorage中存储的空间信息
	 * @param $localStorage
	 * @param spaceId
	 */
	removeLocalStorageInfo: function (spaceId) {
		if (localStorage.getItem('currentDashboard')) {
			var currentDashboard = JSON.parse(localStorage.getItem('currentDashboard'));
			if (currentDashboard[spaceId]) {
				delete currentDashboard[spaceId];
				localStorage.setItem('currentDashboard', JSON.stringify(currentDashboard));
			}
		}
	}
};

function SpaceService(SpaceResources, UserResources, publicDataSrv, promise) {
	this.SpaceResources = SpaceResources;
	this.UserResources = UserResources;
	this.publicDataSrv = publicDataSrv;
	this.promise = promise;
}

SpaceService.prototype = {
	constructor: SpaceService,

	/**
	 * 空间创建 Duplicate
	 * @param space Object
	 */
	createSpace: function (space) {
		return this.SpaceResources.createSpace({
			spaceId: space.spaceId,
			name: space.name,
			domain: space.domain,
			weekStart: space.weekStart
		});
	},

	/**
	 * 空间更新
	 * @param space Object
	 */
	updateSpace: function (space) {
		return this.SpaceResources.updateSpace({
			spaceId: space.spaceId,
			name: space.name,
			domain: space.domain,
			weekStart: space.weekStart
		});
	},

	/**
	 * 删除空间
	 * @param spaceId String
	 */
	deleteSpace: function (spaceId) {
		// 清除localStorage中的dashboard选中信息
		return this.SpaceResources.deleteSpace(null, {
			spaceId: spaceId,
			host: Base64.encode(window.location.host)
		}).then((data) => {
			//移除localStorage中数据
			privateMethods.removeLocalStorageInfo(spaceId);
			return this.promise.resolve(data);
		});
	},

	/**
	 * 退出空间
	 * @param spaceId String
	 */
	quitSpace: function (spaceId) {
		return this.SpaceResources.quitSpace(null, {
			spaceId: spaceId,
			userEmail: cookieUtils.get('ptEmail')
		}).then((data) => {
			//移除localStorage中数据
			privateMethods.removeLocalStorageInfo(spaceId);
			return this.promise.resolve(data);
		});
	},

	/**
	 * 校验空间子域名是否可用
	 * @param spaceId
	 * @param domain
	 * @returns {*}
	 */
	isDomainAvailable: function (spaceId, domain) {
		return this.SpaceResources.isDomainAvailable(null, {
			spaceId: spaceId,
			domain: domain
		});
	},

	/**
	 * 获取空间下面板列表
	 */
	getSpacePanelList: function (spaceId) {
		return this.SpaceResources.getSpacePanelList(null, {
			spaceId: spaceId
		});
	},

	/**
	 * 添加预置面板
	 */
	initDefaultPanel: function (spaceId, locale) {
		return this.SpaceResources.initDefaultPanel(null, {
			spaceId: spaceId,
			localLang: locale
		})
	},

	/**
	 * 根据空间邀请码获取空间邀请信息
	 * @param inviteCode 邀请码
	 * @return Promise object
	 * @return spaceInviteInfo object
	 */
	getSpaceInviteInfo: function (inviteCode) {
		return this.SpaceResources.getSpaceInviteInfo(null, {
			inviteCode: inviteCode
		}).then((res) => {
			var info = {
				type: res.result,
				spaceId: res.spaceId,
				spaceName: res.spaceName,
				userEmail: res.userEmail,
				salesManager: res.salesManager
			};

			// 登录
			if (res.result == 'signin') {
				var currentUserEmail = cookieUtils.get('ptEmail'),
					sid = cookieUtils.get('sid');

				// 如果sid为空或者邀请邮箱和当前登录用户邮箱不一致,则需要清空本地存储的用户信息,返回需要登录提示
				if (!sid || info.userEmail != currentUserEmail) {
					info.type = 'signin';
					this.UserResources.clear(); //清除本地存储
					// 跳转到登录页输入框要显示该email地址
					localStorage.setItem("ptnm", res.userEmail);
				} else {
					// 跳转到dashboard
					info.type = 'dashboard';
					info.sid = sid;
				}

				// 内存中存储邀请信息数据
				this.publicDataSrv.setPublicData('invite', {
					type: 'signin',
					spaceId: res.spaceId,
					invitesCode: inviteCode
				});

				// 调用接受邀请接口 (TODO:可以优化为放到服务器端做)
				return this.acceptSpaceInvitation(inviteCode).then(() => {
					return this.promise.resolve(info);
				});
			} else {
				return this.promise.resolve(info);
			}

		});
	},

	/**
	 * 注册并接受空间邀请
	 * @param userData{email, password, salesManager} 用户信息
	 * @param inviteCode 邀请码
	 */
	signupAndAcceptSpaceInvitation: function (userData, inviteCode, isRememberme) {
		// 进行注册
		return this.UserResources.signup({
			weekStart: ProductConfig.weekStart,
			locale: getLocalLang().locale,
			source: utils.getSourceByLocation(),
			userEmail: userData.email,
			userPassword: md5(userData.password),
			salesManager: userData.salesManager
		}).then((result) => {
			// 本地存储用户信息
			this.UserResources.addLocal({
				userEmail: userData.email,
				ptEmail: userData.email,
				uid: result.uid,
				sid: result.sid,
				userPassword: md5(userData.password)
			}, isRememberme);

			// 内存中存储邀请信息数据
			this.publicDataSrv.setPublicData('invite', {
				type: 'invite',
				spaceId: userData.spaceId,
				invitesCode: inviteCode
			});

			return this.promise.resolve({inviteCode: inviteCode, sid: result.sid});
		}).then((data) => {
			// 进行接受空间邀请
			return this.acceptSpaceInvitation(data.inviteCode).then(() => {
				return this.promise.resolve(data.sid);
			});
		});
	},

	/**
	 * 激活并接受空间邀请
	 * @param userData{email, password} 用户信息
	 * @param inviteCode 邀请码
	 */
	activeAndAcceptSpaceInvitation: function(userData, inviteCode, isRememberme){
		// 进行激活
		return this.UserResources.activateUsers({
			weekStart: ProductConfig.weekStart,
			userEmail: Base64.encode(userData.email),
			userPassword: md5(userData.password)
		}).then((result) => {
			// 本地存储用户信息
			this.UserResources.addLocal({
				userEmail: userData.email,
				ptEmail: userData.email,
				uid: result.uid,
				sid: result.sid,
				userPassword: md5(userData.password)
			}, isRememberme);
			
			// 内存中存储邀请信息数据
			this.publicDataSrv.setPublicData('invite', {
				type: 'invite',
				spaceId: userData.spaceId,
				invitesCode: inviteCode
			});
			
			return this.promise.resolve({inviteCode: inviteCode, sid: result.sid});
		}).then((data) => {
			// 进行接受空间邀请
			return this.acceptSpaceInvitation(data.inviteCode).then(() => {
				return this.promise.resolve(data.sid);
			});
		});
	},

	/**
	 * 接受空间邀请
	 * @param inviteCode
	 * @return {*}
	 */
	acceptSpaceInvitation: function (inviteCode) {
		return this.SpaceResources.acceptInvitation(null, {
			inviteCode: inviteCode
		});
	},

	/**
	 * 获取已邀请的空间成员列表
	 * @param spaceId
	 * @return {*}
	 */
	getInvitedMemberList: function (spaceId) {
		return this.SpaceResources.getInvitedSpaceMemberList(null, {
			spaceId: spaceId
		});
	},

	/**
	 * 发送邀请空间成员邮件
	 * @param spaceId
	 * @param emailList
	 * @return {*}
	 */
	inviteMembers: function (spaceId, emailList) {
		return this.SpaceResources.inviteSpaceMembers(emailList, {
			spaceId: spaceId,
			host: Base64.encode(window.location.host)
		});
	},

	/**
	 * 删除空间成员
	 * @param spaceId
	 * @param email
	 * @return {*}
     */
	deleteMember: function (spaceId, email) {
		return this.SpaceResources.deleteSpaceMember(null, {
			spaceId: spaceId,
			email: email
		});
	},


	/**
	 * 获取用户设置新
	 * @param spaceId
	 */
	getUsersSettingsInfo: function (spaceId) {
		return this.UserResources.getUsersSettingsInfo(null, {
			spaceId: spaceId
		});
	},


	/**
	 * 更新空间维态信息
	 */
	updateUsersSettingsInfo: function (spaceId) {
		return this.UserResources.updateUsersSettingsInfo({
			spaceSelected: spaceId
		});
	}
};

export default SpaceService;
