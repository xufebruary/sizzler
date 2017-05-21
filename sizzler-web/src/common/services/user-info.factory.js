'use strict';

import {
	LINK_USER_INFO,
	LINK_SETTINGS_INFO_UPDATE,
	LINK_UPDATE_USER_SPACE_PERMISSION,
	getCookie,
	isUrlContain,
	isAndroid,
	isIphone,
	setCookie,
	includeStyle,
	getMyDsConfig,
	getLocalLang,
	isDomain
} from 'components/modules/common/common';

import consts from 'configs/const.config';

//angular.module('pt')
//	.factory('getUserInfo', ['$location', '$rootScope', '$translate', '$state', 'sysRoles', 'permissions', 'publicDataSrv', 'dataMutualSrv', getUserInfoFunc]);
getUserInfoFunc.$inject = ['$location', '$rootScope', '$translate', '$state', 'sysRoles', 'permissions', 'publicDataSrv', 'dataMutualSrv'];
function getUserInfoFunc($location, $rootScope, $translate, $state, sysRoles, permissions, publicDataSrv, dataMutualSrv) {
	return {
		setUserInfo: function (data, type) {
			/*
			 data = {
			 permissions: [],
			 roles: [],
			 serviceDsList: [],
			 setting: {
			 demoSwitch: "0",
			 viewOnboarding: "0",
			 hideOnboarding: "0",
			 id: "1474",
			 locale: "zh_CN",
			 profileSelected: null,
			 ptId: "2273",
			 showTips: null,
			 userSelected: null,
			 weekStart: "sunday"
			 },
			 space: [],
			 userInfo: {}
			 };
			 */

			$rootScope.userInfo = data.userInfo;
			$rootScope.userInfo.fistLetterSvg = "#icon-" + angular.lowercase($rootScope.userInfo.userEmail.slice(0, 1));
			$rootScope.userInfo.emailName = $rootScope.userInfo.userEmail.split('@')[0];

			permissions.setPermissions(data.permissions);
			sysRoles.setSysRoles(data.roles);

			includeStyle('/assets/css/l18n-' + data.setting.locale + '.css', 'l18n'); //注入style

			var settingsInfo = data.setting;
			settingsInfo.showTips = angular.fromJson(settingsInfo.showTips);


			var spaceList = data.space;
			var serviceDsList = data.serviceDsList;
			for (var i = 0; i < serviceDsList.length; i++) {
				serviceDsList[i].config = getMyDsConfig(serviceDsList[i].code)
			}
			var enterData = {
				type: type,
				path: null,
				spaceId: null,
				spaceDomain: null
			};
			var spaceData = {
				list: spaceList,
				current: null
			};

			// 将当前用户语言信息存储到本地
			var currLanguage = data.setting.locale;
			localStorage.setItem(consts.I18N_KEY, currLanguage);

			//用来判断是否创建过空间 0 :未建过，1：建过,用户关闭on boarding以后，值即会改会变
			publicDataSrv.setPublicData('isCreateSpace', angular.copy(data.setting.viewOnboarding == null ? "0" : data.setting.viewOnboarding));

			var userSelectedSpace = hasSpace(settingsInfo.spaceSelected, spaceList);
			var defaultSpace = userSelectedSpace || spaceList[0];

			console.log('当前进入方式为: ' + type);

			//获取userInfo后,统一管理进入方式
			switch (type) {
				case "onboarding":
					//onboarding 流程

					// 如果onboarding字段为0,且没有空间(这种情况,就不走onboarding流程)
					// 先更新onboarding字段为1,再进入创建空间流程
					if (spaceList.length == 0) {
						dataMutualSrv.post(LINK_SETTINGS_INFO_UPDATE, {'viewOnboarding': 1}).then(function (data) {
							if (data.status == 'success') {
								settingsInfo.viewOnboarding = 1;
								stateGoThenChangeLanguage('spaceCreate');
							} else {
								console.log('onboarding update error!');
								if (data.status == 'error') {
									console.log(data.message)
								}
							}
						});
					}
					else {
						//获取激活账户刚创建的空间信息(应对未激活的账户先接受邀请的情况)
						var accountCreateData = publicDataSrv.getPublicData('accountCreate');
						if(accountCreateData['spaceId']){
							defaultSpace = hasSpace(accountCreateData.spaceId, spaceList) || spaceList[0];

							//获取后,删除publicData
							accountCreateData = {};
							publicDataSrv.setPublicData('accountCreate', accountCreateData);
						}

						enterData.spaceDomain = defaultSpace.domain;
						enterData.spaceId = defaultSpace.spaceId;
						spaceData.current = angular.copy(defaultSpace);

						//存储公共数据
						publicDataSrv.setPublicData('rootSpace', spaceData);

						if (settingsInfo.viewOnboarding == 1) {
							//用户权限更新
							sysUpdate("pt.dashboard", enterData.spaceId, enterData.spaceDomain);
						} else {
							if(isAndroid || isIphone){
								//移动端不走onboarding流程
								stateGoThenChangeLanguage('accountCreateSuccess', {'spaceDomain': enterData.spaceDomain});
							} else {
								stateGoThenChangeLanguage('onboarding.dataSource', {'spaceDomain': enterData.spaceDomain});
							}
						}
					}

					break;
				case "signin":
				case "activate":
					//用户正常登录或激活进入

					if (spaceList.length == 0) {
						stateGoThenChangeLanguage('spaceCreate');
					} else {
						enterData.spaceDomain = defaultSpace.domain;
						enterData.spaceId = defaultSpace.spaceId;
						spaceData.current = angular.copy(defaultSpace);

						//如果是接受邀请进入的登录,则进入到指定的空间
						var inviteData = publicDataSrv.getPublicData('invite');
						if (inviteData.type && inviteData.type == 'signin') {
							for (var i = 0; i < spaceList.length; i++) {
								if (spaceList[i].spaceId == inviteData.spaceId) {
									enterData.spaceDomain = spaceList[i].domain;
									enterData.spaceId = spaceList[i].spaceId;
									spaceData.current = angular.copy(spaceList[i]);

									//获取后,删除publicData
									inviteData = {
										type: null,
										spaceId: null,
										invitesCode: null
									};
									publicDataSrv.setPublicData('invite', inviteData);
									break;
								}
							}
						}

						//存储公共数据
						publicDataSrv.setPublicData('rootSpace', spaceData);

						//老用户第一次登陆,需要创建控件
						if (!spaceData.current.creatorId) {
							stateGoThenChangeLanguage('spaceCreate');
						} else {
							//用户权限更新
							sysUpdate("pt.dashboard", enterData.spaceId, enterData.spaceDomain);
						}
					}
					break;
				case "signup":
					//用户注册进入
					if (spaceList.length == 0) {
						stateGoThenChangeLanguage('spaceCreate');
					} else {
						enterData.spaceDomain = defaultSpace.domain;
						enterData.spaceId = defaultSpace.spaceId;
						spaceData.current = angular.copy(defaultSpace);

						//存储公共数据
						publicDataSrv.setPublicData('rootSpace', spaceData);

						//老用户第一次登陆,需要创建空间
						if (!spaceData.current.creatorId) {
							stateGoThenChangeLanguage('spaceCreate');
						} else {
							//用户权限更新
							sysUpdate("pt.dashboard", enterData.spaceId, enterData.spaceDomain);
						}
					}
					break;
				case "reload":
					//Session失效
					window.location.reload();
					break;
				case "refresh": //用户刷新URL
					enterData = publicDataSrv.getPublicData('enter');
					// 刷新的是dashboard页面
					if(enterData.path == 'pt.dashboard'){
						if(spaceList){
							spaceData.current = spaceList.find(function (o) {
								return o.domain == enterData.spaceDomain;
							});
							if(spaceData.current){
								enterData.spaceId = spaceData.current.spaceId;
							}
						}
						publicDataSrv.setPublicData('rootSpace', spaceData);
						// spaceId可能为空,此时仍然要跳转到pt.dashboard
						sysUpdate('pt.dashboard', enterData.spaceId, enterData.spaceDomain, enterData.panelId);
					}else{
						if (spaceList.length == 0) {
							stateGoThenChangeLanguage('spaceCreate');
						} else {

							var flag = false;
							for (var i = 0; i < spaceList.length; i++) {
								if (spaceList[i].domain == enterData.spaceDomain) {
									spaceData.current = angular.copy(spaceList[i]);
									enterData.spaceId = spaceList[i].spaceId;
									flag = true;
									break;
								}
							}

							if (enterData.spaceDomain == '' || !flag) {
								enterData.spaceDomain = defaultSpace.domain;
								enterData.spaceId = defaultSpace.spaceId;
								spaceData.current = angular.copy(defaultSpace);
							}

							//存储公共数据,已备进入mainCtrl中使用
							publicDataSrv.setPublicData('rootSpace', spaceData);

							//老用户第一次登陆,需要创建空间
							if (!spaceData.current.creatorId) {
								stateGoThenChangeLanguage('spaceCreate');
							} else if (enterData.permission && !permissions.hasPermission(enterData.permission)) {
								//用户权限更新
								sysUpdate("pt.dashboard", enterData.spaceId, enterData.spaceDomain);
							} else {
								//当URL中直接输入创建地址时,需判断是否已有了空间,如果已存在空间,则进入dashboard.

								if (enterData.path.indexOf('onboarding') >= 0 || enterData.path == 'spaceCreate' || enterData.path == 'accountCreateSuccess') {
									enterData.path = 'pt.dashboard';
								}

								//用户权限更新
								sysUpdate(enterData.path, enterData.spaceId, enterData.spaceDomain);
							}
						}
					}

					break;
				case "home":
					if (spaceList.length == 0) {
						stateGoThenChangeLanguage('spaceCreate');
					} else {
						enterData.spaceDomain = defaultSpace.domain;
						enterData.spaceId = defaultSpace.spaceId;
						spaceData.current = angular.copy(defaultSpace);

						//存储公共数据
						publicDataSrv.setPublicData('rootSpace', spaceData);

						//老用户第一次登陆,需要创建控件
						if (!spaceData.current.creatorId) {
							stateGoThenChangeLanguage('spaceCreate');
						} else {
							//用户权限更新
							sysUpdate('pt.dashboard', enterData.spaceId, enterData.spaceDomain);
						}
					}
					break;
				case "invite":
					//接受邀请进入

					var inviteData = publicDataSrv.getPublicData('invite');
					if (spaceList.length == 0) {
						//如果接受邀请失败,但注册成功.则直接进入创建空间界面
						stateGoThenChangeLanguage('spaceCreate');
					} else {
						//进入接受邀请的空间

						enterData.spaceDomain = defaultSpace.domain;
						enterData.spaceId = defaultSpace.spaceId;
						spaceData.current = angular.copy(defaultSpace);

						for (var i = 0; i < spaceList.length; i++) {
							if (spaceList[i].spaceId == inviteData.spaceId) {
								enterData.spaceDomain = spaceList[i].domain;
								enterData.spaceId = spaceList[i].spaceId;
								spaceData.current = angular.copy(spaceList[i]);
								break;
							}
						}

						//存储公共数据
						publicDataSrv.setPublicData('rootSpace', spaceData);

						//用户权限更新
						sysUpdate('pt.dashboard', enterData.spaceId, enterData.spaceDomain);
					}
					break;
				case "share":
					$translate.use(currLanguage);
					break;
			}

			//存储公共数据
			publicDataSrv.setPublicData('serviceDsList', serviceDsList);
			publicDataSrv.setPublicData('settingsInfo', settingsInfo);
			publicDataSrv.setPublicData('enter', enterData);

			/**
			 * 权限判断,用户权限按空间创建人权限更新
             */
			function sysUpdate(path, spaceId, spaceDomain, panelId){
				if(spaceId){
					dataMutualSrv.get(LINK_UPDATE_USER_SPACE_PERMISSION+'/'+spaceId,null,{timeout:20000}).then(function (data) {
						if (data.status == 'success') {
							serviceDsList = data.content.serviceDsList;
							for (var i = 0; i < serviceDsList.length; i++) {
								serviceDsList[i].config = getMyDsConfig(serviceDsList[i].code)
							}
							publicDataSrv.setPublicData('serviceDsList', serviceDsList);

							permissions.setPermissions(data.content.permissions);
							sysRoles.setSysRoles(data.content.roles);

							var params = {'spaceDomain': spaceDomain, 'panelId': panelId};
							if ($rootScope.userInfo && $rootScope.userInfo.ptId) {
								params.UID = $rootScope.userInfo.ptId;
							}
							stateGoThenChangeLanguage(path, params);

						}else if(data === 'timeout'){
							$('body > .pt-loading > div').removeClass('none');//此处无法获取pt.loadFinish，因此直接操作dom
							console.log('超时了！---权限判断,用户权限按空间创建人权限更新')
						} else {
							console.log('permissions update error!');
							if (data.status == 'error') {
								console.log(data.message)
							}
						}
					});
				}else{
					stateGoThenChangeLanguage(path, {spaceDomain: spaceDomain});
				}
			}

			// 切换状态后再切换语言
			function stateGoThenChangeLanguage(stateName, params) {
				// 往dashboard跳转时检查是否有redirectUrl, 如果有则跳转到该地址
				if(stateName == 'pt.dashboard'){
					var redirectUrl = $location.search().redirectUrl;
					if(redirectUrl){
						location.href = decodeURIComponent(redirectUrl);
						return;
					}
				}

				$state.go(stateName, params).then(() => {
					$translate.use(currLanguage);
				});

				//更新空间选中状态
				if(params){
					var spId;
					for (var i = spaceList.length - 1; i >= 0; i--) {
						if(spaceList[i].domain == params.spaceDomain){
							spId = spaceList[i].spaceId;
							break;
						}
					}

					if(spId && spId != userSelectedSpace.spaceId){
						var sendData = {
							spaceSelected: spId
						}
						dataMutualSrv.post(LINK_SETTINGS_INFO_UPDATE, sendData);
					}
				}

			}

			//排查空间是否存在
			function hasSpace(spaceId, spaceList){
				if(!spaceId) return false;

				for (var i = spaceList.length - 1; i >= 0; i--) {
					if(spaceList[i].spaceId == spaceId){
						return spaceList[i];
					}
				}
				return false;
			}


			//GTM-统计登录信息
			try {
				dataLayer_TZGC5N.push({
					'event': 'login',
					'user': {
						'uid': $rootScope.userInfo.ptId,
						'email': $rootScope.userInfo.userEmail
					}
				});
			} catch (e) {}
		}
	};
}


//angular.module('pt')
//	.factory('sessionContext', ['UserResources', '$rootScope', '$stateParams', 'dataMutualSrv', 'getUserInfo', '$state', 'publicDataSrv', 'siteEventAnalyticsSrv', sessionContextFunc]);
sessionContextFunc.$inject = ['UserResources', '$rootScope', '$stateParams', 'dataMutualSrv', 'getUserInfo', '$state', 'publicDataSrv', 'siteEventAnalyticsSrv'];
function sessionContextFunc(UserResources, $rootScope, $stateParams, dataMutualSrv, getUserInfo, $state, publicDataSrv, siteEventAnalyticsSrv) {
	/**
	 * 校验数据,判断onboarding流程是否走完
	 */
	function checkData(content, type) {
		var flag = false;
		if (type == 'refresh') {
			var enterData = publicDataSrv.getPublicData('enter');

			if (enterData.path == 'ptengineSignin') {
				flag = true;
			}
		}
		else if(type == 'activate'){
			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": content.userInfo.ptId,
			    "where":"user_activate",
				"what":"activate",
				"how":"click"
			});
		}

		if (content && content.setting) {
			return (content.setting.viewOnboarding == '1' || flag) ? type : 'onboarding';
		}
	}

	return {
		saveSession: function (sid, type) {
			$rootScope.sid = sid;
			setCookie("sid", sid);

			dataMutualSrv.get(LINK_USER_INFO).then(function (data) {
				if (data.status == 'success') {
					getUserInfo.setUserInfo(data.content, checkData(data.content, type));
				} else {
					// 用户信息找不到时，清除本地存储信息，并跳转到登录页
					UserResources.clear();
					$state.go('signin');
				}
			});
		},
		getSession: function (type) {
			//type在share时, 值为'shareGetUser'
			//type在非share时, 值为spaceID

			// cookie中sid不为空
			if (getCookie("sid") && !$rootScope.sid) {
				$rootScope.sid = getCookie("sid");
			}
			if (!$rootScope.sid && window.localStorage && localStorage.getItem("sid")) {
				$rootScope.sid = localStorage.getItem("sid");
				setCookie("sid", $rootScope.sid);
			}
			if ($rootScope.sid) {
				if (type == 'shareGetUser') {
					dataMutualSrv.get(LINK_USER_INFO, type).then(function (data) {
						if (data.status == 'success') {
							getUserInfo.setUserInfo(data.content, 'share');
						} else if (data.status == 'failed') {
							console.log('Post Data Failed!')
						} else if (data.status == 'error') {
							console.log('Post Data Error: ');
							console.log(data.message)
						}
					});
				} else {
					dataMutualSrv.get(LINK_USER_INFO,null,{timeout: 20000}).then(function (data) {
						if (data.status == 'success') {
							getUserInfo.setUserInfo(data.content, checkData(data.content, type));
						}else if(data === 'timeout'){
							$('body > .pt-loading > div').removeClass('none');//此处无法获取pt.loadFinish，因此直接操作dom
							console.log('获取sid超时');
						} else {
							//获取用户信息失败,则走退出流程
							$state.go('signin', {type: 'out'});

							if (data.status == 'failed') {
								console.log('Post Data Failed!')
							} else if (data.status == 'error') {
								console.log('Post Data Error: ');
								console.log(data.message)
							}
						}
					});
				}
			} else {
				if (type == 'shareGetUser') {
					//share panel 的特殊需求，如果无法获取sid，需要将sid设置为一个字符串
					$rootScope.sid = 'public-share-panel';
				} else {
					if (isUrlContain('ptone.jp')) {
						$state.go('landingPage');
					} else {
						var redirectUrl = type === 'home' ? '' : encodeURIComponent(location.href);
						$state.go('signin', {redirectUrl: redirectUrl});
					}
				}
			}
		},
		removeSession: function () {
			if (window.localStorage) {
				localStorage.removeItem("ptmm");
				localStorage.removeItem("sid");
				localStorage.removeItem("gid");
				localStorage.removeItem(consts.I18N_KEY);
				$rootScope.sid = null;
				$rootScope.userInfo = null;
			}
		}
	};
}

export {getUserInfoFunc,sessionContextFunc}
