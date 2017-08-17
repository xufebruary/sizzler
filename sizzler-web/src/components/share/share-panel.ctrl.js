'use strict';

import {
	OTHER_LOGIN_WEB_SOCKET,
	LINK_SPACE_LIST,
	LINK_SIGNOUT_URL,
	LINK_USER_INFO,
	LINK_SHARE_SIGNIN,
	isAndroid,
	isIphone,
	isDomain,
	GetRequest,
	getScrollbarWidth,
	uuid,
	setCookie,
	getCookie,
	delCookie,
	clearCookie,
	objectIsEmpty,
	openWindow,
	getMyDsConfig,
	getLocalLang
} from 'components/modules/common/common';

import ProductConfig from 'configs/product.config';
import Favico from 'assets/libs/jquery/favico';
import consts from 'configs/const.config';

import cookieUtils from 'utils/cookie.utils';

// jquery scrollbar 插件引入
require('assets/libs/jquery/custom-content-scroller/jquery.mCustomScrollbar')($);

/**
 * Dashboad Share
 * 分享页面
 *
 */
angular
    .module('pt')
    .controller('shareCtrl', ['$scope', '$rootScope', '$translate', '$localStorage', '$http', '$timeout', 'websocket', 'dataMutualSrv', 'getWidgetListSrv', 'sessionContext', 'sysRoles', 'siteEventAnalyticsSrv', 'datasourceFactory', 'chartService', 'PanelResources', shareCtrlFunc]);

function shareCtrlFunc($scope, $rootScope, $translate, $localStorage, $http, $timeout, websocket, dataMutualSrv, getWidgetListSrv, sessionContext, sysRoles, siteEventAnalyticsSrv,datasourceFactory, chartService, PanelResources) {

	$rootScope.loadFinish = {
		bodyTimeout: false
	}
    $scope.myOptions = {

        //页面显示相关
        page: {
            load: 'loading',        //页面加载状态: loading || success
            showSpaceList: false,   //控制选择空间弹框显示
            showLogin: false,       //控制登录弹框显示
            isAlreadyAdd: null,     //登录后,判断此分享页是否已经添加到账户的空间中
            status: null,           //当前页面状态(panelDelete-已删除,panelShareOff-已取消分享,spaceDelete-space已删除)
            locale: getLocalLang().locale //当前语言版本
        },

        //空间相关
        space: {
            list: null
        },

        //最后更新时间
        widgetLastLoadTime: null
    };

    // config
    $scope.pt = {
        settings: {
            isPhone: isAndroid || isIphone //是否为移动设备（手机）
        },
        loadFinish: {
            body: false,
            aside: false,
            widgetList: false
        }
    };


    var dashboadId;
    var request = GetRequest();
    if (request['id']) {
        dashboadId = request['id'];
    } else {
        return;
    }

    $scope.modal = {
        //全局时间
        'dashboardTime': {
            isOpen: false,
            dateKey: 'widgetTime'
        }
    };

    $scope.sharePanelFlag = true;//这个变量是区分是否为分享panel的

    $scope.shareUser = {
        ptId: null,
        userName: null,
        locale: null,
        weekStart: null
    };

    $scope.loadSetting = {
        'dashboard': false,  //是否显示dashboard loading
        'signin': false,     //是否显示signin loading
        'settings': false,   //是否显示settings loading
        'widget': false      //是否显示widget loading
    };

    /*******************
     *  公用数据存放   *
     *******************/
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
        queryStatus: null
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
        colWidth: $scope.pt.settings.isPhone ? window.screen.width : Math.max(parseInt((window.screen.width - 17 - 40) / 36), 28),    //1366为设计时的标准宽度
        rowHeight: Math.max(parseInt((window.screen.width - 17 - 40) / 36), 28),
        margins: [parseInt(10 * (window.screen.width / 1366)), parseInt(10 * (window.screen.width / 1366))]
    };

    //页面相关
    $scope.rootPage = {
        windowMinWidth: $scope.rootChart.columns * $scope.rootChart.colWidth + 200 + 40, //页面最小宽度=widget总显示宽度+左导航宽度(200)+左右最小边距(40)
        windowScale: (window.screen.width - getScrollbarWidth()) / 1366,  //1366为设计时的标准宽度
        contentWidth: parseInt($scope.rootChart.columns * $scope.rootChart.colWidth)
    };

    //图形左下角提示信息（指标、维度）
    $scope.chartTips = chartService.getTips;

    //关闭footer的注册提示
    $scope.changeShowRegistrationFooter = function () {
        $scope.showRegistrationFooter = 'false';
        $localStorage.showRegistrationFooter = 'false';
    };

    $scope.sharePanelMsg = {
		validate: false,
		panelShareOff: false, //分享取消
		panelDelete: false, //分享面板删除
		passwordError: false, //密码不匹配
		confirmPasswordMsg: false, //密码输入提示
		passWordOfNull: false, //密码为空
		passWordError: false, //密码错误
		sharePassword: ''
	};

    $scope.showLogin = function () {
        $scope.myOptions.page.showLogin = true;
    };

	$scope.confirmPassword = function(password){
		if(!password) {
			$scope.sharePanelMsg.confirmPasswordMsg = true;
			$scope.sharePanelMsg.passWordOfNull = true;
			$scope.sharePanelMsg.passWordError = false;
			return false;
		}
		var sendData = {
			dashboardId: dashboadId,
			password: encodeURIComponent($scope.sharePanelMsg.sharePassword)
		};
		//全站事件统计
		siteEventAnalyticsSrv.createData({
			"where": "panel_share",
			"what": "validate_panel_password",
			"how": "click",
			"value": encodeURIComponent($scope.sharePanelMsg.sharePassword)
		});

        PanelResources.sharePanelVerifyPassword(sendData)
        .then((data) => {
            /**
             * data: true || false
             */
            if(data){
                localStorage.setItem('sharePassWord-' + dashboadId, encodeURIComponent($scope.sharePanelMsg.sharePassword));
                $scope.sharePanelMsg.validate = true;
                $scope.sharePanelMsg.panelShareOff = false;
                $scope.sharePanelMsg.passwordError = false;
                if(!$scope.rootPanel.now){//第一次进入时，需要初始化
                    var sharePanelUrl = LINK_SHARE_SIGNIN + 'panel' + '/' + dashboadId + '?password=' + encodeURIComponent(encodeURIComponent($scope.sharePanelMsg.sharePassword));
                    $.get(sharePanelUrl, function (data) {
                        window.bootstrapData = data;
                        init();
                    });
                }else{//因为更改全局时间需要重新取数，不需要重新初始化了
                    $scope.rootWidget.list.forEach(function(item){
                        var ds = datasourceFactory.getDatasource(item.baseWidget.widgetId);
                        ds.reload();
                    });
                }
                $scope.sharePanelMsg.sharePassword = '';
            } else {
                $scope.sharePanelMsg.passwordError = true;
                $scope.sharePanelMsg.confirmPasswordMsg = true;
                $scope.sharePanelMsg.passWordOfNull = false;
                $scope.sharePanelMsg.passWordError = true;
            }
        })
	};


    /**
     * 弹框显示(选择空间 || 登录)
     *
     */
    $scope.showPopup = function (type) {
        if (type == 'showSpaceList') {
            //选择空间(再次登录后,需要重新获取列表)

            dataMutualSrv.get(LINK_SPACE_LIST).then(function (data) {
                if (data.status == 'success') {
                    $scope.myOptions.space.list = data.content;

                } else {
                    if (data.status == 'failed') {
                        console.log('Post Data Failed!')
                    } else if (data.status == 'error') {
                        console.log('Post Data Error: ');
                        console.log(data.message)
                    }
                }

                $scope.myOptions.page[type] = true;
            });
        } else {

            $scope.myOptions.page[type] = true;
        }
    };


    /**
     * 关闭弹框(选择空间 || 登录)
     *
     */
    $scope.closePopup = function (type, status) {
        $scope.myOptions.page[type] = false;

        if($scope.myOptions.page.status !== null && $scope.myOptions.page.status != 'spaceNotIn' && $scope.myOptions.page.status != 'targetSpaceDelete'){
            $scope.sharePanelMsg.panelShareOff = true;
        } else {
            $scope.sharePanelMsg.panelShareOff = false;
        }

        if(status && status == 'success'){
			//登录成功后,隐藏底部注册提示信息
			$scope.showRegistrationFooter = 'false';
		}
    };


    /**
     * 分享中的退出功能
     *
     */
    $scope.loginOut = function () {
        dataMutualSrv.post(LINK_SIGNOUT_URL).then(function (data) {
            if (data.status == 'success') {
                delCookie("sid");
                clearCookie();//清除所有cookie
                localStorage.setItem('gid', null);//清空ga登陆的痕迹
                sessionContext.removeSession();//退出登陆的时候需要删除localstorage中的用户名和密码
                localStorage.setItem('sid', 'public-share-panel');//需要重新设置sid，排序取数需要sid
                $rootScope.userInfo = {};//需要清除用户名和密码
                $translate.use(getLocalLang().locale);
                $scope.myOptions.page.locale = getLocalLang().locale;
            } else if (data.status == 'failed') {
                console.log('Post Data Failed!')
            } else if (data.status == 'error') {
                console.log('Post Data Error: ');
                console.log(data.message)
            }
        });
    };



    /**********************
     * 全局时间        *
     **********************/
    $scope.localDate = function (dateKey) {
        var localInfo;
        if (dateKey == 'widgetTime') {
            localInfo = $translate.instant('PANEL.TIME.WIDGET_TIME');
        } else if (dateKey.indexOf('|') > -1) {
            var sdt = dateKey.split('|')[0];
            var edt = dateKey.split('|')[1];

            if (edt == 'today') {
                localInfo = $translate.instant('WIDGET.EDITOR.TIME.FROM') + new Date(sdt).format('MM/dd') + ' ' + $translate.instant('WIDGET.EDITOR.TIME.TO_TODAY')
            } else {
                localInfo = new Date(sdt).format('MM/dd') + '-' + new Date(edt).format('MM/dd');
            }
        } else if (dateKey.indexOf('last') == 0 && dateKey.indexOf('last_') < 0 || dateKey.indexOf('past') == 0) {
            var modelTodayCode = dateKey.indexOf('last') == 0 ? 'INCLOUD_TODAY' : 'EXCLOUD_TODAY';

            localInfo = $translate.instant('WIDGET.EDITOR.TIME.LAST') + dateKey.match(/\d+/g) + $translate.instant('WIDGET.EDITOR.TIME.DAYS') + '（' + $translate.instant('WIDGET.EDITOR.TIME.' + modelTodayCode) + '）';
        } else {
            localInfo = $translate.instant('WIDGET.EDITOR.TIME.' + angular.uppercase(dateKey));
        }
        return localInfo;
    };

    //全局时间初始化
    function dashboardTimeInit() {
        $scope.modal.dashboardTime.isOpen = false;

        if (angular.isDefined($scope.rootPanel.now.components) && !objectIsEmpty($scope.rootPanel.now.components) && $scope.rootPanel.now.components['GLOBAL_TIME'].status == '1') {
            $scope.modal.dashboardTime.dateKey = $scope.rootPanel.now.components['GLOBAL_TIME'].value;
        } else {
            $scope.modal.dashboardTime.dateKey = 'widgetTime';
        }
    }


    /**********************
     * 时间维度判断        *
     **********************/
    $scope.dimensionsCheck = function (widget) {
        var dsId = widget.variables[0].ptoneDsInfoId;

        //在应用了全局时间后,除了GA,AD,PT,FB之外的数据源都需校验是否包含时间维度
        if ([1, 3, 12, 13, 18, 19, 21, 23, 25, 27, 28, 29, 30, 31].indexOf(+dsId) < 0 && !widget.variables[0].dateDimensionId && $scope.modal.dashboardTime.dateKey != 'widgetTime') {
            return true;
        } else {
            return false;
        }
    };



    $scope.isDomain = function(domain){
        return isDomain(domain);
    };

    /**
     * 判断当前用户是否存在登录信息
     * 如果存在,则自动登录
     *
     */
    $scope.userIsSignin = function () {
        if (!$rootScope.sid && window.localStorage && localStorage.getItem("sid")) {
            $rootScope.sid = localStorage.getItem("sid");
            setCookie("sid", $rootScope.sid);
        }
        if (getCookie("sid") && !$rootScope.sid) {
            $rootScope.sid = getCookie("sid");
        }
        if (cookieUtils.get('sid')) {

            $http({
                method: 'GET',
                url: LINK_USER_INFO + "?sid=" + cookieUtils.get('sid') + "&uiVersion=" + BASE_VERSION,
                data: angular.toJson('shareGetUser')
            })
            .success(function (data) {
                if (data.status == 'success') {
                    $scope.myOptions.space.list = data.content.space;

                    $rootScope.userInfo = data.content.userInfo;
                    $rootScope.userInfo.fistLetterSvg = "#icon-" + angular.lowercase($rootScope.userInfo.userEmail.slice(0, 1));
                    $rootScope.userInfo.emailName = $rootScope.userInfo.userEmail.split('@')[0];

                    //本地语言初始化
                    $translate.use(data.content.setting.locale);
                    localStorage.setItem(consts.I18N_KEY, data.content.setting.locale);
                    $scope.myOptions.page.locale = data.content.setting.locale;

                    //全站事件统计
                    siteEventAnalyticsSrv.createData({
                        "uid": $rootScope.userInfo.ptId,
                        "where": "share-page",
                        "what": "share-page",
                        "how": "visit",
                        "value": dashboadId
                    });
                } else {
                    if (data.status == 'failed') {
                        console.log('Post Data Failed!')
                    } else if (data.status == 'error') {
                        console.log('Post Data Error: ');
                        console.log(data.message)
                    }


                    //SID错误,则取消自动登录.清除本地SID信息
                    $rootScope.sid = 'public-share-panel';

                    //全站事件统计
                    siteEventAnalyticsSrv.createData({
                        "where": "share-page",
                        "what": "share-page",
                        "how": "visit",
                        "value": dashboadId
                    });
                }

                $scope.myOptions.page.load = 'success';
                angular.element('#js_shareContent').removeClass('hide');
            })
            .error(function () {

                //SID错误,则取消自动登录.清除本地SID信息
                $rootScope.sid = 'public-share-panel';
                $scope.myOptions.page.load = 'success';
                angular.element('#js_shareContent').removeClass('hide');

                //全站事件统计
                siteEventAnalyticsSrv.createData({
                    "where": "share-page",
                    "what": "share-page",
                    "how": "visit",
                    "value": dashboadId
                });
            });
        }
        else {
            //share panel 的特殊需求，如果无法获取sid，需要将sid设置为一个字符串

            $rootScope.sid = 'public-share-panel';
            $scope.myOptions.page.load = 'success';
            angular.element('#js_shareContent').removeClass('hide');

			if (!$localStorage.showRegistrationFooter) {
			// 	$scope.showRegistrationFooter = $localStorage.showRegistrationFooter;
			// } else {
				$scope.showRegistrationFooter = 'true'; // 进入页面默认显示注册提示
			}

            //全站事件统计
            siteEventAnalyticsSrv.createData({
                "where": "share-page",
                "what": "share-page",
                "how": "visit",
                "value": dashboadId
            });
        }
    };



    /**
     * 初始化widgetData-websocket, 链接socket;
     *
     */
    function initWidgetDataSocket(callBackFunc){

        // 如果socket存在则跳过初始化
        if($rootScope.socketData.id){
            if(callBackFunc) callBackFunc();
        } else {
            var isCloseFlag = false;
            var widgetDataSocketSign = 'WidgetData:' + uuid(); // widget取数push数据用websocket sign
            $rootScope.socketData = {
                id: widgetDataSocketSign,
                func: new websocket
            };
            $rootScope.socketData.func.initWebSocket(OTHER_LOGIN_WEB_SOCKET + widgetDataSocketSign);

            $rootScope.socketData.func.ws.onclose = function (event) {
                //清除数据后重连

                $rootScope.socketData = {
                    id: null,
                    func: null
                };
                initWidgetDataSocket();
                isCloseFlag = true;
            };

            $rootScope.socketData.func.ws.onopen = function(){
                if(callBackFunc) callBackFunc();

                //socket断开重连后,调取全局刷新widget（分享页上有token，暂不对应）
                // if(isCloseFlag) {
                //     datasourceFactory.reloadAllDatasource();
                //     isCloseFlag = false;
                // }
            }
        }
    }

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


    /**
     * 等待widget初始化完成
     */
    $scope.widgetRepeatFinish = function () {
        $timeout(function(){
            $scope.rootWidget.drawChart = true;
			/**********************
			 * 富文本框的滚动条      *
			 **********************/
			if(!$scope.pt.settings.isPhone){//移动端不需要滚动条插件
				$('.js-mCustomScrollbar.widget-bd-tool').mCustomScrollbar({
					axis: "yx",
					theme: "minimal-dark",
					autoHideScrollbar: true
				});
			}
        }, 0);
    };


    /**
     * Ctrl Init
     *
     */
	init();
	function init(){
		//创建socket
		$rootScope.socketData = {
			id: null,
			func: null
		};
		// initWidgetDataSocket();
		/**
		 * 请求Token后启动angular
		 *
		 */
		if(window.bootstrapData){
			var data = window.bootstrapData;
			sysRoles.setSysRoles(data.content.roles);

			var favicon=new Favico();
			var image;
			// var link = document.head.querySelector("link");
			if(sysRoles.hasSysRole('cig-media-smart')){
				// link.href = "cig.ico";
				window.document.title = '新意互动 | CIG';
				image=document.getElementById('cig');
			}else{
				window.document.title = ProductConfig.title;
				image = document.getElementById(ProductConfig.favicon);
			}
			//favicon.image(image);

			//处理Token
			if (data.status == 'success') {
				var accessToken = data.content.accessToken;
				if (accessToken) {
					$rootScope.accessToken = accessToken;
					$rootScope.accessTokenTimeout = new Date().getTime() + 60 * 1000; // 有效期1分钟

					//获取panel列表后，默认展示
                    PanelResources.getPanelInfo(null, {
                        panelId: dashboadId,
                        accessToken: accessToken
                    })
                    .then((panel) => {
                        $scope.rootPanel.now = panel;
                        $scope.rootPanel.nowId = panel.panelId;

                        //全局时间初始化
                        dashboardTimeInit();

                        //初始化socket，获取widget list
                        initWidgetDataSocket(getWidgetListSrv.getList(panel, true, $scope.pt.settings.isPhone, getWidgetListBack))
                    })
				} else {
					$scope.sharePanelMsg.panelShareOff = true;
					$scope.rootWidget.queryStatus = 'finish';
				}
			} else if(data.status == 'error'){
				console.log(data.message);
				$scope.sharePanelMsg.panelShareOff = true;
				$scope.rootWidget.queryStatus = 'finish';
			} else if(data.status == 'failed'){
				$scope.rootWidget.queryStatus = 'finish';
				if(data.message === 'panelShareOff'){
					$scope.sharePanelMsg.panelShareOff = true;
				}else if(data.message === 'passwordError'){
					$scope.sharePanelMsg.passwordError = true;
					$timeout(function(){
						$('.share-password input').focus();
					},300);
				}else if(data.message === 'panelDelete'){
					$scope.sharePanelMsg.panelShareOff = true;
				}else if(data.message === 'spaceDelete'){
					$scope.sharePanelMsg.panelShareOff = true;
				}

			}
			/**
			 * 1. 进入分享页,首先判断当前用户是否存在登录信息
			 * 2. 如果存在登录信息,则获取国际化语言版本; 否则,使用本地语言
			 * 3. 当没有登录且localstorage中没有showRegistrationFooter,就显示footer中的注册提示
			 */
			$scope.userIsSignin();
		}

		//GA 虚拟PV
        /*
		setTimeout(function () {
			if (dataLayer_TZGC5N && dataLayer_TZGC5N instanceof Array) {
				dataLayer_TZGC5N.push({'event': 'page_view'});
			}
		}, 0);
		*/
	};



    /**
	 * 社交Facebook 分享页面
	 */
	$scope.facebookShare = function () {
		var pageInfo = getCurrentPageInfo();
		var url = "https://www.facebook.com/dialog/feed?app_id=" +
			pageInfo.facebookShareId +
			// "&redirect_uri=" + consts.WEB_UI_URL + "/shareing.html" +
			"&name=" + pageInfo.title;

		pageInfo.description && (url +=  "&description=" + pageInfo.description);
		url += "&link=" + pageInfo.host + "&picture=" + pageInfo.pic;

		openWindow(url);
	};

	/**
	 * 社交Twitter 分享页面
	 */
	$scope.twitterShare = function(){
		var pageInfo = getCurrentPageInfo();
		var url = "https://twitter.com/intent/tweet?text=" + pageInfo.title;

		pageInfo.description && (url +=  "  " + pageInfo.description);
		url += "  " + pageInfo.host;// + "  " + "pic.twitter.com/6x9ZPNblsx";

		openWindow(url);
	};

	/**
	 * 获取社交分享信息
     */
	function getCurrentPageInfo(){
		return {
			facebookShareId: isDomain('datadeck.com') ? consts.FACEBOOK_SHARE_APP_ID_COM : consts.FACEBOOK_SHARE_APP_ID_JP,
			title: $scope.rootPanel.now.panelTitle,
			description: $scope.rootPanel.now.description || 'DataDeck',
			host: window.location.href,
			pic: window.location.protocol + "//" + window.location.host + "/assets/images/share/new-datadeck.jpg"
			// pic: "https://dash.datadeck.jp/assets/images/share/datadeck.jpg"
		}
	};

	/**
	 * 获取widget列表后的回调函数
     */
	function getWidgetListBack(list){
		$scope.rootWidget.list = list;
		$scope.rootWidget.noData = !(list.length > 0);
        $scope.rootWidget.queryStatus = 'finish';
	};

    //接受widget的上次刷新时间
    $scope.$on('widgetLastLoadTime', function(e, d) {
        $scope.myOptions.widgetLastLoadTime = d;
    });
}

/**
 * widget底部获取错误信息，以及提示信息
 */
angular
    .module('pt')
    .directive('chartTipsTrigger', ['datasourceFactory', function (datasourceFactory) {
    return {
        restrict: 'EA',
        link: function (scope, element, attrs) {
            /**
             * 刷新widget数据
             */
            $(element).on('click', '.refresh-widget', function () {
                var id = $(this).attr('data-widget-id');
                var ds = datasourceFactory.getDatasource(id);
                ds.reload();
            });

        }
    }
}]);

/**
 * widget单独指令，包括底部获取错误信息，以及提示信息
 */
angular
    .module('pt')
    .directive('widgetDirection', ['$timeout','$sce', function ($timeout,$sce) {
    return {
        restrict: 'EA',
        link: function (scope, element, attrs) {
            //接收子级传递的数据
            scope.$on('to-parent', function (event, data) {
                if (data && data.length == 2) {
                    scope.data = scope.data || {};
                    scope.data[data[0]] = data[1];
                }
            });

            scope.elementHeight = {
                widgetHd: null,
                chartDateRange: null,
                chartValue: null
            };
            scope.$watch('widget.widgetDrawing', function (newV, oldV) {
                if (newV == 'success') {
                    scope.elementHeight.widgetHd = $(element).find('.widget-hd').height();
                    scope.elementHeight.chartDateRange = $(element).find('.chart-dateRange').height();
                    scope.elementHeight.chartValue = $(element).find('.chart-value').height();
                    //scope.$broadcast('widgetLoadFinished');
                    $timeout(function () {
                        $(window).resize();
                    }, 200);
                }
            });

            scope.widgetPhoneSetting = {
                width: null,
                sizeX: null,
                sizeY: null
            };

            scope.setWidgetHeightOfPhone = function(){
                if(scope.pt.settings.isPhone){//移动端需要减去18（9*2）
                    if(scope.widget.baseWidget.widgetType === 'custom'){
                        var ratioOfWidgetWH = scope.widget.sizeX/scope.widget.sizeY;
                        scope.widgetPhoneSetting.width = parseInt(scope.rootChart.colWidth / ratioOfWidgetWH);
                        scope.widgetPhoneSetting.sizeX = scope.widget.sizeX;
                        scope.widgetPhoneSetting.sizeY = scope.widget.sizeY;
                        $timeout(function(){
                            element.css('height',scope.widgetPhoneSetting.width);
                        });
                    }
                }
            };
            scope.setWidgetHeightOfPhone();

			scope.bindHtml = function(text){
				return $sce.trustAsHtml(text);
			}
        }
    }
}]);
