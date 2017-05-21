'use strict';


import {
	LINK_AUTHOR,
	DATA_SOURCE_WEB_SOCKET,
	LINK_GET_DS_PROFILES,
	LINK_PROFILE_API_REMOTE,
	LINE_DS_PROFILES,
	LINK_DATASOURCE_CATEGORYS,
	LINK_SALESFORCE_OBJECTS,
	LINK_WIDGET_LIST_EDIT,
	LINK_SETTINGS_INFO_UPDATE,
	LINK_DATA_SOURCE_VIEW,
	uuid,
	openWindow,
	loginSvg
} from 'components/modules/common/common';


/**
 * onboarding
 */
angular
	.module('pt')
	.controller('onboardingCtrl', onboardingCtrl);

onboardingCtrl.$inject = ['$scope', 'publicDataSrv', 'onboardingDataSrc'];

function onboardingCtrl($scope, publicDataSrv, onboardingDataSrc) {

	init();

	//=================

	function init() {
		var settingsInfo = publicDataSrv.getPublicData('settingsInfo');

		//判断onboarding字段,为1则直接进入dashboard
		if (settingsInfo.viewOnboarding == 1) {
			onboardingDataSrc.stateChange('pt.dashboard');
		} else {
			$scope.hideLoading('body');

			//更新流程状态
			localStorage.setItem('currentOnboardingStatus', 'isBegin');
		}
	}
}


/**
 * onboarding
 */
angular
	.module('pt')
	.service('onboardingDataSrc', ['$rootScope', '$state', '$translate', 'websocket', 'dataMutualSrv', 'publicDataSrv', 'uiLoadingSrv', 'SpaceResources', 'PanelResources', onboardingDataSrc]);

function onboardingDataSrc($rootScope, $state, $translate, websocket, dataMutualSrv, publicDataSrv, uiLoadingSrv, SpaceResources, PanelResources) {
	//缓存数据
	var _this = this;
	var baseData = {
		dsInfo: null,       //数据源
		dsAccount: null,    //授权账号
		dsList: null,       //ds列表
		galleryList: null,  //Gallery列表
		gallerySltList: null,  //Gallery选中列表
		widgetSltList: null,    //选中Gallery后,生成的具体widget列表
		dashboardId: null,    //dashboard Id
		//profile: null,          //档案信息
		profile: {
			type: null,     //数据源类型(mysql, googleanalysis, other)
			current: null,  //当前选中
			list: null,     //档案列表
			listTmp: null,  //搜索备用
			query: null,    //档案列表请求状态(querying, success, failed, error)
			messageCode: null//错误信息
		},
		profileList: null,      //档案列表信息
		spaceInfo: publicDataSrv.getPublicList('rootSpace'),    //Space
		userInfo: publicDataSrv.getPublicData('settingsInfo'),  //User

		currentStep: 'ds'   //当前步骤[1-ds, 2-gallery, 3-preview]
	};

	this.setData = function (type, data) {
		baseData[type] = data;
	};

	this.getData = function (type) {
		return baseData[type]
	};

	this.stateChange = function (state) {
		if ($state.current.name != state) {
			$state.go(state, {'spaceDomain': baseData.spaceInfo.current.domain});
		}
	};

	//判断当前路由是否满足显示条件
	this.currentPageIsShow = function () {
		return baseData.dsInfo ? true : false;
	};

	/**
	 * 授权
	 */
	this.accredit = function (scope, backFunc) {
		var accreditSocket = new websocket;
		var spaceId = baseData.spaceInfo.current.spaceId;
		var userEmail = $rootScope.userInfo.userEmail;
		var sign = uuid();
		var url = LINK_AUTHOR + baseData.dsInfo.code + '?ptOneUserEmail=' + encodeURIComponent(encodeURIComponent(userEmail)) + '&sign=' + sign + '&spaceId=' + spaceId;
		var socketUrl = DATA_SOURCE_WEB_SOCKET + encodeURIComponent(encodeURIComponent(userEmail)) + '&sign=' + sign;

		if (baseData.dsInfo.code == 'ptengine') {
			url = '/signin/ptengine';
			localStorage.setItem('ptengineLoginReferrer', 'dataSources');
			localStorage.setItem('ptengineAuthSign', sign);
			localStorage.setItem('ptengineSpaceId', spaceId);

			socketUrl = DATA_SOURCE_WEB_SOCKET + encodeURIComponent(encodeURIComponent(userEmail)) + '&sign=' + sign + '&spaceId=' + spaceId;
		}

		accreditSocket.initWebSocket(socketUrl);

		//授权验证跳转
		openWindow(url);

		//监听授权socket返回值
		scope.wsData = accreditSocket.colletion;
		accreditSocket.ws.onmessage = function (event) {
			scope.$apply(function () {
				scope.wsData = event.data;
			});
		};
		var mywatch = scope.$watch('wsData', function (newValue, oldValue) {
			if (!newValue || newValue === oldValue) {
				return;
			}

			//注销当前监听事件
			mywatch();
			newValue = angular.fromJson(newValue);

			if (newValue.status == 'success') {
				//关闭socket
				accreditSocket.disconnect();

				backFunc('success', angular.copy(newValue.content.connectionInfo));
			} else {
				backFunc('faiure');
			}
		});
	};

	/**
	 * 获取档案列表
	 */
	this.getProfileList = function (accountName, connectionId, dsId, dsCode, backFunc) {
		var url,
			profileList = [],
			config = baseData.dsInfo.config.editor.source,
			spaceId = baseData.spaceInfo.current.spaceId;
		baseData.profile.type = dsCode;
		baseData.profile.query = 'querying';

		switch (config.getProfileType) {
			case "api":
				url = LINK_GET_DS_PROFILES + '/' + dsCode + '/' + connectionId + '/' + accountName;
				break;
			case "apiRemote":
				url = LINK_PROFILE_API_REMOTE + connectionId;
				break;
			case "table":
				url = LINE_DS_PROFILES + spaceId + '/' + connectionId + '/' + dsId;
				baseData.profile.type = 'mysql';
				break;
			case "category":
				url = LINK_DATASOURCE_CATEGORYS + '/' + dsId;
				break;
			case "salesforce":
				url = LINK_SALESFORCE_OBJECTS;
				break;
		}

		//如果account没改变，并且已存在列表旧数据，则复用旧数据
		var getDataFlag = true;
		var dsAccountProfile = publicDataSrv.getPublicData('dsAccountProfile');
		if (!config.profileOfNormal) {
			// 取数前,先查看是否以存储可复用的数据列表(当存储的列表为空时,也需要重新取数)
			if (dsAccountProfile[dsId] && dsAccountProfile[dsId][connectionId] && dsAccountProfile[dsId][connectionId].length > 0) {
				profileList = angular.copy(dsAccountProfile[dsId][connectionId]);
				getDataFlag = false;
				baseData.profile.list = profileList;
				baseData.profile.listTmp = profileList;

				//列表请求状态
				baseData.profile.query = 'success';

				//回调
				backFunc('onboarding.connect', 'success');
			}
		}
		if (getDataFlag) {
			_getList();
		}

		//save data
		function _getList() {
			profileList = [];

			dataMutualSrv.get(url).then(function (data) {
				var list = [];
				if (data.status == 'success') {

					if (dsCode == 'googleadwords') {

						if (data.content.canManageClients) {
							//MCC账户
							list = data.content.childs;
						} else {
							//普通账户
							baseData.profile.current = data.content;

							//列表请求状态
							baseData.profile.query = data.status;

							//回调
							backFunc('pt.dashboard', data.status);
							return;
						}
					} else if (dsCode == 'facebookad') {

						list = data.content.accountList;
					} else if (dsCode == 'doubleclick' || dsCode == 'doubleclickCompound') {

						list = data.content.userProfiles;

						//由于字段名不统一,在前端将name加上.统一调用
						for (var i = 0; i < list.length; i++) {
							list[i]['name'] = list[i].userName;
						}
					} else {
						list = data.content;
					}
				} else {
					console.log('link data get profile list failed!');
					if (data.status == 'error') {
						console.log(data.message)
					}
				}

				profileList = angular.copy(list);
				//存储备用
				if (!config.profileOfNormal) {
					publicDataSrv.setPublicData('dsAccountProfile', dsId, connectionId, angular.copy(list));
				}

				baseData.profile.list = profileList;
				baseData.profile.listTmp = profileList;

				var message = null;
				if (dsCode == 'facebookad') {
					message = $translate.instant(data.message);
					message = message.replace("{spaceId}", baseData.spaceInfo.current.domain);
					baseData.profile.messageCode = message;
				} else {
					baseData.profile.messageCode = $translate.instant("WIDGET.EDITOR.ACCOUNT.GET_PROFILE_ERROR");
				}

				//列表请求状态
				baseData.profile.query = data.status;

				//回调
				backFunc('onboarding.connect', data.status);
			})
		}
	};

	/**
	 * getAuthAccounts
	 * 请求已授权账户列表
	 */
	this.getAuthAccounts = function (spaceId, dsId, backFunc) {
		dataMutualSrv.get(LINK_DATA_SOURCE_VIEW + spaceId + '/' + dsId).then(function (data) {
			if (data.status == 'success') {
				if (data.content.length > 0) {
					backFunc(data.content[0]);
				} else {
					backFunc();
				}
			} else {
				console.log('Get accountsList Failed!')
				if (data.status == 'error') {
					console.log(data.message)
				}

				backFunc();
			}
		});
	};

	/**
	 * 授权保存
	 * 账号授权信息保存后:
	 * 1. 需要获取dashboard list(当没有dashboard时,需要在后台生成对应的字段信息. function: getDashboardList)
	 * 2. 获取dashboard list之后,需要添加dashboard.(function: addDashboard)
	 */
	this.confirm = function () {

		var widgetList = [];
		var sendWidget = {
			"baseWidget": {
				"widgetId": null,
				"creatorId": null,
				"templetId": null
			},
			"variables": [{
				"variableId": null,
				"ptoneDsInfoId": null,
				"accountName": null,
				"profileId": null,
				"dimensions": null,
				"metrics": null,
				'segment': null,
				'filters': null
			}]
		};

		sendWidget.variables[0].ptoneDsInfoId = baseData.dsInfo.id;
		sendWidget.variables[0].accountName = baseData.dsAccount.name;
		sendWidget.variables[0].connectionId = baseData.dsAccount.connectionId;

		if (!baseData.dsInfo.config.linkData.hideStepThree) {
			sendWidget.variables[0].profileId = baseData.profile.current.id;
		}

		//获取需要授权的widget信息,查找模板数据并替换
		for (var i = 0; i < baseData.widgetSltList.length; i++) {
			if (baseData.widgetSltList[i].baseWidget.widgetType != 'tool' && angular.isDefined(baseData.widgetSltList[i].baseWidget.isExample) && baseData.widgetSltList[i].baseWidget.isExample == 1 && baseData.widgetSltList[i].variables[0].ptoneDsInfoId == baseData.dsInfo.id) {
				//更新发送数据
				sendWidget.baseWidget.isExample = 0;
				sendWidget.baseWidget.isDemo = 0;
				sendWidget.baseWidget.widgetId = baseData.widgetSltList[i].baseWidget.widgetId;
				sendWidget.baseWidget.creatorId = baseData.widgetSltList[i].baseWidget.creatorId;
				if (baseData.widgetSltList[i].baseWidget.widgetType == 'chart') {
					sendWidget.variables[0].variableId = baseData.widgetSltList[i].variables[0].variableId;
					sendWidget.variables[0].segment = baseData.widgetSltList[i].variables[0].segment;
					sendWidget.variables[0].filters = baseData.widgetSltList[i].variables[0].filters;

					sendWidget.variables[0].dimensions = angular.copy(baseData.widgetSltList[i].variables[0].dimensions);
					sendWidget.variables[0].metrics = angular.copy(baseData.widgetSltList[i].variables[0].metrics);
				}

				widgetList.push(angular.copy(sendWidget));
			}
		}

		dataMutualSrv.post(LINK_WIDGET_LIST_EDIT, widgetList).then(function (data) {
			if (data.status == 'success') {
				console.log('Onboarding 档案授权成功!')

				_this.getDashboardList()
			} else {
				console.log('Onboarding 档案授权失败!');
				if (data.status == 'error') {
					console.log(data.message)
				}
			}
		});
	};

	/**
	 * 请求dashboard list
	 */
	this.getDashboardList = function () {
		SpaceResources.getSpacePanelList(null, {
			spaceId: baseData.spaceInfo.current.spaceId
		})
		.then((data) => {
			_this.addDashboard(data.panelLayout);
		})
	};

	/**
	 * 新增Dashboard
	 */
	this.addDashboard = function (panelLayout) {
		var panelId = baseData.dashboardId;
		var panelTitle = baseData.dsInfo.name + " " + $translate.instant('ONBOARDING.TIPS.DASHBOARD_NAME');
		var newLayout = {
			type: "panel",
			panelId: panelId,
			panelTitle: panelTitle,
			shareSourceId: null
		};

		panelLayout.panelLayout = panelLayout.panelLayout ? angular.fromJson(panelLayout.panelLayout) : [];
		panelLayout.panelLayout.unshift(newLayout);
		panelLayout.panelLayout = angular.toJson(panelLayout.panelLayout);
		panelLayout.updateTime = parseInt(new Date().getTime());


		var widgetLayout = [];
		for (var i = 0; i < baseData.widgetSltList.length; i++) {
			var pos = {
				id: baseData.widgetSltList[i].baseWidget.widgetId,
				c: baseData.widgetSltList[i].col,
				r: baseData.widgetSltList[i].row,
				x: baseData.widgetSltList[i].sizeX,
				y: baseData.widgetSltList[i].sizeY,
				minx: baseData.widgetSltList[i].minSizeX,
				miny: baseData.widgetSltList[i].minSizeY
			};
			widgetLayout.push(pos);
		}

		//新增panle信息
		var panelInfo = {
			"type": "panel",
			"panelId": panelId,
			"panelTitle": panelTitle,
			"spaceId": baseData.spaceInfo.current.spaceId,
			"layout": encodeURIComponent(angular.toJson(widgetLayout, true))
		};

		PanelResources.addPanel(panelInfo)
		.then((data) => {
			//更新流程状态
			localStorage.setItem('currentOnboardingStatus', 'isSuccess');

			//更新选中状态
			_this.setLocalStorage(baseData.spaceInfo.current.spaceId, $rootScope.userInfo.ptId, panelId);

			//更新字段后跳转(再跳转)
			_this.updateOnboarding('pt.dashboard');
		})
	};

	/**
	 * 更新localStorage中的space所选dashboard信息
	 */
	this.setLocalStorage = function (spaceId, ptId, panelId) {
		var currentDashboard = {};

		if (localStorage.getItem('currentDashboard')) {
			currentDashboard = angular.fromJson(localStorage.getItem('currentDashboard'));

			if (!currentDashboard[spaceId]) {
				currentDashboard[spaceId] = {};
			}
		} else {
			currentDashboard[spaceId] = {};
		}
		currentDashboard[spaceId][ptId] = panelId;
		localStorage.setItem('currentDashboard', angular.toJson(currentDashboard));
	};

	/**
	 * 更新onboarding字段为1
	 * 更新成功后,跳转进入dashboard
	 */
	this.updateOnboarding = function (state) {
		dataMutualSrv.post(LINK_SETTINGS_INFO_UPDATE, {'viewOnboarding': 1}).then(function (data) {
			if (data.status == 'success') {
				var settingsInfo = publicDataSrv.getPublicData('settingsInfo');
				settingsInfo.viewOnboarding = 1;
				publicDataSrv.setPublicData('settingsInfo', settingsInfo);
				publicDataSrv.setPublicData('isCreateSpace', 1); //更新创建空间状态

				$state.go(state, {'spaceDomain': baseData.spaceInfo.current.domain});

				//loading
				uiLoadingSrv.removeLoading(angular.element('body'));
			} else {
				console.log('onboarding update error!');
				if (data.status == 'error') {
					console.log(data.message)
				}
			}
		});
	}
}

