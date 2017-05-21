'use strict';

import {
	OTHER_LOGIN_WEB_SOCKET,
	getScrollbarWidth,
	uuid,
	getMyDsConfig
} from 'components/modules/common/common';



/**
 * widget preview
 *
 * Created by ao at 20160909
 */

angular
	.module('pt')
	.controller('previewCtrl', previewCtrl);

previewCtrl.$inject = ['$rootScope', '$scope', '$document', '$timeout', 'websocket', 'siteEventAnalyticsSrv', 'uiLoadingSrv', 'onboardingDataSrc', 'publicDataSrv', 'datasourceFactory', 'chartService'];

function previewCtrl($rootScope, $scope, $document, $timeout, websocket, siteEventAnalyticsSrv, uiLoadingSrv, onboardingDataSrc, publicDataSrv, datasourceFactory, chartService) {
	'use strict';
	var body = $document.find('body').eq(0);

	$scope.myOptions = {
		currentPageIsShow: null,
		dsInfo: onboardingDataSrc.getData('dsInfo'),
		spaceInfo: onboardingDataSrc.getData('spaceInfo'),
		userInfo: null,
		widgetSltList: onboardingDataSrc.getData('widgetSltList'),
		dashboardId: onboardingDataSrc.getData('dashboardId'),

		other: {
			authorize: false,       //授权中//验证中
			authorizeFailure: false //验证失败
		},

		//档案相关
		profile: {
			type: null,     //数据源类型(mysql, googleanalysis, other)
			current: null,  //当前选中
			list: null,     //档案列表
			listTmp: null,  //搜索备用
			query: null,    //档案列表请求状态(querying, success, failed, error)
			messageCode: null//错误信息
		}
	};
	
	$scope.dsConfig = getMyDsConfig($scope.myOptions.dsInfo.code);

	//panel相关
	$scope.rootPanel = {
		now: null,      //当前选中的panle信息
		nowId: null     //当前选中的panel的ID信息
	};
	//widget相关
	$scope.rootWidget = {
		list: [],           //widget列表
		locateId: null,     //widget定位时的ID信息
		noData: false,      //无widget提示
		drawChart: false,   //等待widget加载完成再统一绘制
		linkData: {
			showTips: false
		}
	};

	//user相关
	$scope.rootUser = {
		settingsInfo: {}    //客户设置信息
	};

	//chart相关
	$scope.rootChart = {
		columns: $scope.pt.settings.isPhone ? 1 : 36,
		resizable: false,
		draggable: false,
		colWidth: $scope.pt.settings.isPhone ? window.screen.width : Math.max(parseInt((window.screen.width - 100) / 36), 28),    //1366为设计时的标准宽度
		rowHeight: Math.max(parseInt((window.screen.width - 100) / 36), 28),
		margins: [parseInt(10 * (window.screen.width / 1366)), parseInt(10 * (window.screen.width / 1366))]
	};

	//页面相关
	$scope.rootPage = {
		windowMinWidth: $scope.rootChart.columns * $scope.rootChart.colWidth, //页面最小宽度=widget总显示宽度
		windowScale: (window.screen.width - getScrollbarWidth()) / 1366,  //1366为设计时的标准宽度
		contentWidth: parseInt($scope.rootChart.columns * $scope.rootChart.colWidth)
	};

	/**
	 * 拖拽插件配置
	 */
	$scope.gridstackOptions = {
		width: $scope.rootChart.columns,
		cellHeight: $scope.rootChart.rowHeight,
		verticalMargin: 0,
		float: false,
		disableDrag: true,
		disableResize: true
	};

	

	//===============


	$scope.widgetRepeatFinish = widgetRepeatFinish;
	$scope.back = back;
	$scope.accredit = accredit;
	$scope.skip = skip;
    $scope.chartTips = chartService.getTips; //图形左下角提示信息


	dataInit();

	//===============

	/**
	 * 数据初始化
	 */
	function dataInit() {
		$scope.myOptions.currentPageIsShow = onboardingDataSrc.currentPageIsShow();

		initWidgetDataSocket();
	}

	/**
	 * widgetRepeatFinish
	 */
	function widgetRepeatFinish() {
		$timeout(function () {
			//统一绘图
			$scope.rootWidget.drawChart = true;
			$scope.hideLoading('widgetList');
		}, 0);
	}

	/**
	 * 初始化widgetData-websocket, 链接socket;
	 *
	 */
	function initWidgetDataSocket() {
		var socketData = publicDataSrv.getPublicData('socket');

		// 如果socket存在则跳过初始化
		if (socketData.id) {
			return;
		} else {
			var widgetDataSocketSign = 'WidgetData:' + uuid(); // widget取数push数据用websocket sign
			socketData = {
				id: widgetDataSocketSign,
				func: new websocket
			};
			socketData.func.initWebSocket(OTHER_LOGIN_WEB_SOCKET + widgetDataSocketSign);
			publicDataSrv.setPublicData('socket', socketData);


			socketData.func.ws.onclose = function (event) {
				//清除数据后重连

				console.log('Info: widgetData WebSocket connection closed.');
				console.log(event);

				var _socketData = {
					id: null,
					func: null
				};
				publicDataSrv.setPublicData('socket', _socketData);
				initWidgetDataSocket();

				//socket断开重连后,调取全局刷新widget
				datasourceFactory.reloadAllDatasource();
			};
		}
	}

	/**
	 * 授权
	 */
	function accredit() {
		if($scope.myOptions.dsInfo.code == 'ptengine'){
			//ptengine数据源特殊对应，先判断是否有已授权账户，有则默认选中，直接进入下一步

			//loading
			uiLoadingSrv.createLoading(angular.element('body'));

			//获取已授权账户列表
			onboardingDataSrc.getAuthAccounts($scope.myOptions.spaceInfo.current.spaceId, $scope.myOptions.dsInfo.id, getAuthAccountsAfter)
		} 
		else {
			onboardingDataSrc.accredit($scope, accreditAfter);
		}

		//GTM
		siteEventAnalyticsSrv.setGtmEvent('click_element', 'onboarding', 'preview_connect');

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"onboarding",
			"what":"preview_connect_datasource",
			"how":"click"
		});
	}

	/**
	 * getAuthAccounts
	 * 请求已授权账户列表之后的回调
	 */
	function getAuthAccountsAfter(account){
		if(account){
			onboardingDataSrc.setData('dsAccount', account);
			onboardingDataSrc.getProfileList(account.name, account.connectionId, account.dsId, account.dsCode, getProfileListAfter)
		} 
		else {
			//loading
			uiLoadingSrv.removeLoading(angular.element('body'));
			
			onboardingDataSrc.accredit($scope, accreditAfter);
		}
	}

	/**
	 * 授权之后
	 *
	 * connectionInfo: {
             config:"{"accessToken":"ya29.Ci9aA8KiMCzI5IIDZDmEODzOkeYOz5gU3TRLtbK6uKe6OKsIMSGEy1PuyjboWpkwUw"}"
             connectionId:"6e11295d-c6d3-4b9c-a778-beb9930575cf"
             dsCode:"googleanalysis"
             dsId:1
             name:"sylphlili@ptmind.com"
             spaceId:"a7643a2b-f7f2-4661-8ad4-f74349e0f43a"
             status:"1"
             uid:"149"
             updateTime:1473479119369
             userName:"dawn"
         }
	 */
	function accreditAfter(type, connectionInfo) {
		if (type == 'success') {
			//loading
			uiLoadingSrv.createLoading(angular.element('body'));

			if (connectionInfo.connectionId) {
				onboardingDataSrc.setData('dsAccount', connectionInfo);
				onboardingDataSrc.getProfileList(connectionInfo.name, connectionInfo.connectionId, connectionInfo.dsId, connectionInfo.dsCode, getProfileListAfter)
			}
		}
	}

	/**
	 * getProfileListAfter
	 * 取档案列表之后,统一处理
	 */
	function getProfileListAfter(state) {
		if (state == 'pt.dashboard') {
			//如果是MCC账号等则直接进行授权后,进入面板
			onboardingDataSrc.confirm()
		} else {
			//loading
			uiLoadingSrv.removeLoading(angular.element('body'));

			onboardingDataSrc.stateChange(state);
		}
	}

	/**
	 * Back
	 */
	function back() {
		//存储步骤
		//onboardingDataSrc.setData('currentStep', 'gallery');

		//切换路由
		onboardingDataSrc.stateChange('onboarding.dataSource');

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"onboarding",
			"what":"preview_back",
			"how":"click"
		});
	}

	/**
	 * Skip
	 * 跳过,则直接取dashboard list后创建dashboard
	 */
	function skip() {
		onboardingDataSrc.getDashboardList();

		//GTM
		siteEventAnalyticsSrv.setGtmEvent('click_element', 'onboarding', 'preview_skip');

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"onboarding",
			"what":"preview_skip",
			"how":"click"
		});
	}

	// ==========

	//接收子级传递的数据
    $scope.$on('to-parent', function (event, data) {
        if (data && data.length == 2) {
            $scope.data = $scope.data || {};
            $scope.data[data[0]] = data[1];
        }
    });
}

