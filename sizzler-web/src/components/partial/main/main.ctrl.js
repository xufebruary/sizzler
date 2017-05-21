'use strict';

/**
 * Main Controller
 * 除登录,登出,找回密码,注册页面外的总控制器
 */
import {
	LINK_LANGUAGE,
	LINK_GRAPH,
	LINK_DS,
	LINK_SYS_DATA_VERSION,
	LINK_TAGS_PUBLISH,
	LINK_SETTINGS_INFO,
	LINK_GET_AUTH_ACCOUNT_DETAIL,
	LINK_BASE_WIDGET_TEMPLET_BASIC_EDIT,
	LINK_SIGNOUT_URL,
	getMyDsConfig,
	getScrollbarWidth,
	getLocalLang,
	delCookie,
	clearCookie
} from '../../modules/common/common';

import '../../modules/widget/widget-list/gridstackSrc';

angular
.module('pt')
.controller('mainCtrl', ['$scope', '$window', '$translate', '$timeout', '$state', 'UserResources', 'sessionContext', 'permissions', 'dataMutualSrv', 'publicDataSrv', 'sysRoles', 'toggleLoadingSrv', mainCtrlFunc]);

function mainCtrlFunc($scope, $window, $translate, $timeout, $state, UserResources, sessionContext, permissions, dataMutualSrv, publicDataSrv, sysRoles, toggleLoadingSrv){
	/**
	 * 退出
	 */
	$scope.logout = function () {
		//先发送请求再清除$rootScope.sid
		dataMutualSrv.post(LINK_SIGNOUT_URL);
		UserResources.clear();
		$scope.changeLogo();
		publicDataSrv.clearPublicData('all');
		$state.go('signin');
	};

	$scope.goToDashboard = function () {
		var domain = $scope.rootSpace.current.domain;

		$state.go('pt.dashboard', {spaceDomain: domain});
	}

	$scope.loadSetting = {
		'dashboard': false,  //是否显示dashboard loading
		'signin': false,     //是否显示signin loading
		'settings': false,   //是否显示settings loading
		'widget': false      //是否显示widget loading
	};

	// add 'ie' classes to html
	function isSmartDevice($window) {
		// Adapted from http://www.detectmobilebrowsers.com
		var ua = $window['navigator']['userAgent'] || $window['navigator']['vendor'] || $window['opera'];
		// Checks for iOs, Android, Blackberry, Opera Mini, and Windows mobile devices
		return (/iPhone|iPod|iPad|Silk|Android|BlackBerry|Opera Mini|IEMobile/).test(ua);
	}

	var isIE = !!navigator.userAgent.match(/MSIE/i);
	isIE && angular.element($window.document.body).addClass('ie');
	isSmartDevice($window) && angular.element($window.document.body).addClass('smart');


	//控制左侧导航显示与隐藏
	$scope.toggleAside = function () {
		$scope.pt.settings.asideFolded = !$scope.pt.settings.asideFolded;

		$scope.rootPage.windowMinWidth = $scope.pt.settings.asideFolded ? ($scope.rootChart.columns * $scope.rootChart.colWidth + 40) : ($scope.rootChart.columns * $scope.rootChart.colWidth + 200 + 40);
	};


	/*****************************
	 *  Dashboard 公用数据存放   *
	 *****************************/
	//Space相关
	$scope.rootSpace = {
		list: [],     //space列表
		current: null,  //当前space信息
		member: null    //当前space下所有member列表
	};
	//panel相关
	$scope.rootPanel = {
		list: null,     //panel列表
		layout: null,   //panel位置信息
		now: null,      //当前选中的panle信息
		nowId: null,    //当前选中的panel的ID信息
		noData: false,  //无panel提示
		templateList: [],//panel模板列表

		panelCtrl: {}   //panel控制器中的定义值(path: js/controllers/panel.js)
	};
	//widget相关
	$scope.rootWidget = {
		list: [],           //widget列表
		locateId: null,     //widget定位时的ID信息
		noData: false,      //无widget提示
		drawChart: false,   //等待widget加载完成再统一绘制

		//批量授权
		linkData: {
			dsList: [],     //数据源列表
			showTips: false,//是否显示提示面板
			showDire: false,//是否开始渲染指令
			currentDs: null,//当前授权数据源信息
			currentDsLeft: null //当前授权数据源偏移位置
		}
	};
	//user相关
	$scope.rootUser = {
		settingsInfo: {},                   //客户设置信息
		userSelected: {},                   //客户预存时间范围信息(属于settingsInfo)
		profileSelected: {},                //客户预存授权信息(属于settingsInfo)
		//lang: $translate.proposedLanguage(),//客户本地语言版本
		sysRoles: { //用户权限相关
			createCustomWidget: sysRoles.hasSysRole("ptone-test") //客户是否有创建自定义widget的权限
		}

	};
	//common相关
	$scope.rootCommon = {
		langList: [],   //语言字典表
		weekStart: [],  //周起始时间字典表
		tagList: [],    //tag列表
		grapList: [],   //图形列表
		timeOwns: [],   //时间粒度
		dsList: [],     //数据源字典表列表
		dsAuthList: [], //数据源已授权列表
		dataVersion: {},//数据版本号
		dashboardList: {}//前端已请求过的widget列表
	};
	//chart相关
	$scope.rootChart = {
		columns: $scope.pt.settings.isPhone ? 1 : 36,
		resizable: false,
		draggable: false,
		colWidth: $scope.pt.settings.isPhone ? window.screen.width : Math.max(parseInt(30 * (window.screen.width / 1366)), 28),    //1366为设计时的标准宽度
		rowHeight: Math.max(parseInt(30 * (window.screen.width / 1366)), 28),
		margins: [Math.max(parseInt(10 * (window.screen.width / 1366)), 9), Math.max(parseInt(10 * (window.screen.width / 1366)), 9)]
	};
	//页面相关
	$scope.rootPage = {
		fullScreen: false,
		dashboardMode: "READ",
		// windowWidth: window.screen.width - getScrollbarWidth(),
		windowMinWidth: $scope.pt.settings.isPhone ? window.screen.width : $scope.rootChart.columns * $scope.rootChart.colWidth + 200 + 40, //页面最小宽度=widget总显示宽度+左导航宽度(200)+左右最小边距(40)
		windowScale: (window.screen.width - getScrollbarWidth()) / 1366,  //1366为设计时的标准宽度
		contentWidth: $scope.pt.settings.isPhone ? window.screen.width : parseInt($scope.rootChart.columns * $scope.rootChart.colWidth)
	};
	//接收子级传递的临时数据
	$scope.rootTmpData = {
		'dataSources': null,
		'addTemplate': null,     //添加模版时,dashboard信息中转点
		'editorShow': null //dashboard中的widget编辑器是否打开，这个字段专门给main.ctrl使用的
	};

	$scope.processData = function (array, fieldName) {
		$.each(array, function (i, item) {
			if (item[fieldName]) {
				try {
					var strTagName = item[fieldName];
					item[fieldName] = angular.fromJson(strTagName);
				} catch (e) {
				}
			}
		})
	};

	//获取基础数据超时
	function getCommonDataTimeout(tips){
		console.log(tips);
		$scope.pt.loadFinish.bodyTimeout = true;
	}
	//获取基础数据
	function getCommonData() {
		//获取语言字典表
		dataMutualSrv.get(LINK_LANGUAGE,null,{'timeout': 20000}).then(function (data) {
			if (data.status == 'success') {
				$scope.rootCommon.langList = data.content;
			} else if (data.status == 'failed') {
				console.log('Get langList Failed!')
			} else if (data.status == 'error') {
				console.log('Get langList Error: ');
				console.log(data.message)
			}else if(data === 'timeout'){
				getCommonDataTimeout('超时啦！---获取语言字典表');
			}
		});

		//获取语言字典表
		// dataMutualSrv.get(LINK_WEEKSTART).then(function(data) {
		//     rootCommon.weekStart = data.content;
		// });


		//获取图表类型
		dataMutualSrv.get(LINK_GRAPH,null,{'timeout': 20000}).then(function (data) {
			if (data.status == 'success') {
				$scope.rootCommon.grapList = data.content;
			} else if (data.status == 'failed') {
				console.log('Get grapList Failed!')
			} else if (data.status == 'error') {
				console.log('Get grapList Error: ');
				console.log(data.message)
			}else if(data === 'timeout'){
				getCommonDataTimeout('超时啦！---获取图表类型');
			}
		});

		//获取各数据版本号
		dataMutualSrv.get(LINK_SYS_DATA_VERSION,null,{'timeout': 20000}).then(function (data) {
			if (data.status == 'success') {
				$scope.rootCommon.dataVersion = data.content;
			} else if (data.status == 'failed') {
				console.log('Post Data Failed!')
			} else if (data.status == 'error') {
				console.log('Post Data Error: ');
				console.log(data.message)
			}else if(data === 'timeout'){
				getCommonDataTimeout('超时啦！---获取各数据版本号');
			}
		});


		//获取发布的panel标签列表
		dataMutualSrv.get(LINK_TAGS_PUBLISH,null,{'timeout': 20000}).then(function (data) {
			if (data.status == 'success') {
				for (var i = 0; i < data.content.length; i++) {
					data.content[i].ptoneTagName = angular.fromJson(data.content[i].ptoneTagName);
				}
				$scope.rootCommon.tagList = data.content;
			} else if (data.status == 'failed') {
				console.log('Get tagList Failed!')
			} else if (data.status == 'error') {
				console.log('Get tagList Error: ');
				console.log(data.message)
			}else if(data === 'timeout'){
				getCommonDataTimeout('超时啦！---获取发布的panel标签列表');
			}
		});


		//判断当前是否有空间
		if ($scope.rootSpace.current != undefined) {

			//获取数据源字典表列表
			dataMutualSrv.get(LINK_DS + '/' + $scope.rootSpace.current.spaceId,null,{'timeout': 20000}).then(function (data) {
				if (data.status == 'success') {
					for (var i = 0; i < data.content.length; i++) {
						data.content[i].config = getMyDsConfig(data.content[i].code)
					}

					$scope.rootCommon.dsList = data.content;
				}else if(data === 'timeout'){
					getCommonDataTimeout('超时啦！---判断当前是否有空间');
				} else {
					if (data.status == 'failed') {
						console.log('Get dsList Failed!')
					} else if (data.status == 'error') {
						console.log('Get dsList Error: ');
						console.log(data.message)
					}
				}
			});

			//获取用户设置信息
			dataMutualSrv.get(LINK_SETTINGS_INFO + '/' + $scope.rootSpace.current.spaceId,null,{'timeout': 20000}).then(function (data) {
				if (data.status == 'success') {
					$scope.rootUser.settingsInfo = data.content;
					$scope.rootUser.settingsInfo.showTips = angular.fromJson($scope.rootUser.settingsInfo.showTips);
					$scope.rootUser.profileSelected = data.content.profileSelected == '' ? null : angular.fromJson(data.content.profileSelected);
					$scope.rootUser.userSelected = data.content.userSelected == '' ? null : angular.fromJson(data.content.userSelected);
				} else if (data.status == 'failed') {
					console.log('Get settingsInfo Failed!')
				} else if (data.status == 'error') {
					console.log('Get settingsInfo Error: ');
					console.log(data.message)
				}else if(data === 'timeout'){
					getCommonDataTimeout('超时啦！---获取用户设置信息');
				}
			});

			//获取已授权数据源列表
			dataMutualSrv.get(LINK_GET_AUTH_ACCOUNT_DETAIL + $scope.rootSpace.current.spaceId,null,{'timeout': 20000}).then(function (data) {
				if (data.status == 'success') {
					for (var i = 0; i < data.content.length; i++) {
						data.content[i].dsConfig = getMyDsConfig(data.content[i].dsCode)
					}

					$scope.rootCommon.dsAuthList = data.content;
				}else if(data === 'timeout'){
					getCommonDataTimeout('超时啦！---获取已授权数据源列表');
				} else {
					if (data.status == 'failed') {
						console.log('Get dsList Failed!')
					} else if (data.status == 'error') {
						console.log('Get dsList Error: ');
						console.log(data.message)
					}
				}
			});
		}
	}


	//针对移动端自定义widget，当手机横竖屏切换时
	if ($scope.pt.settings.isPhone) {
		$(window).on('orientationchange', function (e) {
			$scope.$broadcast('changeWidgetLayout', 'orientationchange');//通知小widget
			$scope.$broadcast('changeWidgetWHOfPhone', 'orientationchange');//通知大widget
		})
	}

	//针对地图自适应大小
	$scope.$on('gridster-draggable-changed', function (gridster) {
		var gridsterResize = $timeout(
			function () {
				$(window).resize();
			},
			300
		).then(
			function () {
				$timeout.cancel(gridsterResize);
			}
		);
	});


	/**
	 * switchSpace
	 * 切换空间时,URL重置
	 *
	 */
	$scope.switchSpace = function (space) {
		if ($scope.pt.settings.isPhone) {//移动端切换空间
			$scope.rootSpace.current = space;
			localStorage.setItem('switchSpaceOfPhone', true);//移动端切换空间的时候，pannel-list不能收起
		}

		// $state.transitionTo($state.current, {spaceDomain: space.domain, panelId: null}, {reload: true, notify: true});

		window.location.href = '/' + space.domain + '/Dashboard';
	};


	/**
	 * clearBaseData
	 * 清除基础数据(当删除空间)
	 *
	 */
	$scope.clearBaseData = function (type) {
		if (type == 'all') {
			//Space相关
			$scope.rootSpace = {
				list: [],
				current: null,
				member: null
			};
			//panel相关
			$scope.rootPanel = {
				list: [],
				layout: null,
				now: null,
				nowId: null,
				noData: true,
				panelCtrl: {}
			};
			//widget相关
			$scope.rootWidget = {
				list: [],
				locateId: null,
				noData: true,
				drawChart: false,

				//批量授权
				linkData: {
					dsList: [],
					showTips: false,
					showDire: false,
					dsChange: false,
					currentDs: null,
					currentDsLeft: null
				}
			};
			//common相关
			$scope.rootCommon.dsAuthList = [];
		} else {
			switch (type) {
				case 'rootSpace':
					$scope.rootSpace = {
						list: [],
						current: null,
						member: null
					};
					break;
				case 'rootPanel':
					$scope.rootPanel = {
						list: null,
						layout: null,
						now: null,
						nowId: null,
						noData: true,
						panelCtrl: {}
					};
					break;
				case 'rootWidget':
					$scope.rootWidget = {
						list: [],
						locateId: null,
						noData: true,
						drawChart: false,

						//批量授权
						linkData: {
							dsList: [],
							showTips: false,
							showDire: false,
							dsChange: false,
							currentDs: null,
							currentDsLeft: null
						}
					};
					break;
			}
		}
	};

	/**
	 * 拖拽插件配置
	 */
	$scope.gridstackOptions = {
		width: $scope.rootChart.columns,
		//cellWidth: $scope.rootChart.colWidth,
		cellHeight: $scope.rootChart.rowHeight,
		//verticalMargin: $scope.rootChart.margins,
		verticalMargin: 0,
		//auto: true,
		float: false,
		staticGrid: true,
		animate: false,
		resizable: {
			handles: 'e, se, s, sw, w',
			// autoHide: false
		},
		draggable: {
			// handle: '.gridster-hand',
			scroll: true,
			scrollSensitivity: 100,
			scope: ".grid-stack"
		},
		disableDrag: true,
		disableResize: true,
		removeTimeout: 2000,
		handler: null
	};


	/**
	 * Ctrl Init
	 *
	 */
	(function () {

		//获取空间列表信息
		$scope.rootSpace = publicDataSrv.getPublicList('rootSpace');
		$scope.rootUser.settingsInfo = publicDataSrv.getPublicData('settingsInfo');
		$scope.rootUser.userSelected = $scope.rootUser.settingsInfo.userSelected == '' ? null : angular.fromJson($scope.rootUser.settingsInfo.userSelected);

		//获取基础数据
		getCommonData();
		$scope.changeLogo();

        toggleLoadingSrv.hide('body');
	})()
}
