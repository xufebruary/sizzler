import ProductConfig from 'configs/product.config';
import {
    LINK_WIDGET_ADD,
    LINK_SETTINGS_INFO_UPDATE,
    objectIsEmpty,
    uuid,
    getRect,
    getMyDsConfig
} from '../../modules/common/common';

angular
    .module('pt')
    .controller('dashboardCtrl', ['$scope', '$translate', '$location', '$rootScope', '$stateParams', '$state', '$document', '$timeout', '$q', 'dataMutualSrv', 'panelSltSrv', 'siteEventAnalyticsSrv', 'sysRoles', 'datasourceFactory', 'gridstackService', 'linkDataSrv', 'toggleLoadingSrv', 'widgetDownloadUtils', 'PanelServices', 'WidgetServices', 'PanelResources', 'Track', dashboardCtrlFunc]);

function dashboardCtrlFunc($scope, $translate, $location, $rootScope, $stateParams, $state, $document, $timeout, $q, dataMutualSrv, panelSltSrv, siteEventAnalyticsSrv, sysRoles, datasourceFactory, gridstackService, linkDataSrv, toggleLoadingSrv, widgetDownloadUtils, PanelServices, WidgetServices, PanelResources, Track) {
    var body = $document.find('body').eq(0);

    //删除用
    $scope.widgetDeleteInfo = {
        id: null,
        title: null,
        index: null,
        widget: null
    };

    //公用配置
    $scope.modal = {
        //当前panle信息
        'panelIsShow': false, //是否显示左侧列表
        'widgetCopyShow': false, //是否显示widgetCopy层
        'widgetDeleteShow': false, //是否显示widgetDelete层
        'widgetDeleteType': 'chart', //删除的widget的类型，chart或tool，默认chart
        'panelExportShow': false, //是否显示panel导出Report层
        'copyWidgetId': null, //复制widgetId
        'copyWidgetTitle': null, //复制widget title
        'copyWidgetType': 'chart', //复制widget 的类型，分为chart和tool两种


        'demoShow': false,
        //编辑器相关
        'editorShow': false, //控制外部编辑器显示与隐藏
        'editorOpen': false, //外部编辑器当前打开状态
        'editorElemnet': null, //当前编辑器的Dom
        'editorPlace': null, //当前所编辑widget的位置信息
        'editorNow': null, //当前所编辑widget的信息
        'editorNowCopy': null, //当前所编辑widget的信息副本
        'editorNowIndex': null, //当前所编辑widget的下标信息

        'mapPath': null, //地图更改时的路径,

        'addPanelDetailed': false,

        //数据源管理相关
        'dsType': null, //数据源类型跳转

        //提示框
        'tips': {
            show: false,
            options: {}
        },

        //全局时间
        'dashboardTime': {
            isOpen: false,
            dateKey: 'widgetTime'
        },

        //全局刷新
        'dashboardRefresh': {
            isShow: false,
            showTime: null,
            oldTime: null,
            status: null
        },

        //widget list加载状态
        'widgetRepeatFinish': false,

        //new onboarding
        onboarding: {
            showTips: false,
            showVideo: false,
            videoSrc: null
        },

        //标题相关
        title: {
            nameMod: null,
            editing: false,
            layoutPath: '',
            layoutFind: false
        },

        //面板相关
        panel: {
            list: false,    //控制弹出框字段
            add: false,
            delete: false,
            copy: false,
            edit: false,
            share: false,
            deleteFolder: false,
            viewOnPhone: false,
            info: null      //当前操作的面板信息
        },

        download: {
            isBegin: false
        }
    };

    //移动端的切换pannel-list的按钮，默认收起pannel-list
    $scope.ptPhone = {
        menuBtn: false
    };

	$scope.sharePasswordMsg = {
		show: false,
		error: false,
		require: false,
		sharePassword: ''
	};

    $scope.showChartData = null;

    // =================

    //移动端点击panel事件
    $scope.mobileDashboardClick = mobileDashboardClick;

    //移动端判断是否为分享页面
    $scope.mobileIsShare = mobileIsShare;

    //判断是否显示头部
    $scope.isShowHead = isShowHead;

    //打开复制弹框
    $scope.showCopyWidget = showCopyWidget;

    //dashboard标题修改
    $scope.dashboardTitleEdit = dashboardTitleEdit;

    //dashboard标题修改完成
    $scope.dashboardTitleEditDone = dashboardTitleEditDone;

    //dashboard标题修改焦点事件
    $scope.dashboardTitleEditByKeydown = dashboardTitleEditByKeydown;

    //显示面板操作界面(新增、复制、编辑、分享、删除)
    $scope.showPanelOperation = showPanelOperation;

    //隐藏面板操作界面(新增、复制、编辑、分享、删除)
    $scope.hidePanelOperation = hidePanelOperation;

    //面板操作回调(新增、复制、编辑、分享、删除)
    $scope.panelOperationCallBack = panelOperationCallBack;

    //面板操作失败回调(新增、复制、编辑、分享、删除)
    $scope.panelOperationFailureCallBack = panelOperationFailureCallBack;

    //批量授权
    $scope.linkData = linkData;

    //widget创建
    $scope.widgetCreate = widgetCreate;

    //widget位置信息更新
    $scope.widgetLayoutUpdate = widgetLayoutUpdate;

    //widget编辑
    $scope.widgetEdit = widgetEdit;

    //打开widget编辑器
    $scope.showEditor = showEditor;

    //widget demo数据
    $scope.showWidgetDemo = showWidgetDemo;

    //全局时间
    $scope.localDate = localDate;

    //全局时间初始化
    $scope.dashboardTimeInit = dashboardTimeInit;

    //模式切换(编辑||只读)
    $scope.toggleDashboardMode = toggleDashboardMode;

    //全局刷新
    $scope.globalRefresh = globalRefresh;

    //全局刷新初始化
    $scope.dashboardRefreshInit = dashboardRefreshInit;

    //打开/关闭视频弹窗
    $scope.toggleVideo = toggleVideo;

    //更新用户设置信息(视频)
    $scope.updateShowTips = updateShowTips;

    //面板选中
    $scope.dashboardSelect = dashboardSelect;

    //关闭提示框
    $scope.closeTips = closeTips;

	//输入panel密码
	$scope.checkPassword = checkPassword;

    //校验当前空间是否有ptengine授权账户(控制添加热图按钮)
    $scope.hasPtengineAccount = hasPtengineAccount;

    //下载
    $scope.downloadWidget = downloadWidget;


    //入口
    init()

    // =================

    //入口
    function init() {
    	console.log('init dashboard controller')

        body.removeClass('modal-open');

        //移动端切换空间的时候，pannel-list不能收起，一直展示
        if (localStorage && localStorage.getItem('switchSpaceOfPhone')) {
            $scope.ptPhone.menuBtn = true;
            localStorage.removeItem('switchSpaceOfPhone');
        }

        //进入时，先清除widget列表
        $scope.rootWidget.list = null;

        //默认点击dashboard
        if ($scope.rootSpace.current) {
            if (!$scope.rootPanel.list) {
                getDashboardList();
            } else {
                // $scope.modal.panelIsShow = true;
                showPanelOperation('list');

                //当前路由地址在panel下时，默认点击panel
                dashboardDefaultClick();
            }
        } else {
            $scope.rootPanel.noData = true;
            $scope.rootWidget.list = [];
            // $scope.modal.panelIsShow = true;
            showPanelOperation('list');
        }
    };

    //dashboard 标题修改
    function dashboardTitleEdit() {

        //查看模式 不能修改title
        if ($scope.rootPage.dashboardMode == 'EDIT' && !$scope.rootPanel.now.shareSourceId) {
            $scope.modal.title.editing = true;
            $scope.modal.title.nameMod = $scope.rootPanel.now.panelTitle;

            //全站事件统计
            siteEventAnalyticsSrv.createData({
                "uid": $rootScope.userInfo.ptId,
                "where":"panel",
                "what":"edit_panel_editing_title",
                "how":"click"
            });
        }
    };

    //dashboard 标题修改完成
    function dashboardTitleEditDone() {
        if ($scope.modal.title.nameMod == '') {
            $scope.modal.title.nameMod = $scope.rootPanel.now.panelTitle;
            return;
        }
        else if ($scope.modal.title.nameMod == $scope.rootPanel.now.panelTitle) {
            $scope.modal.title.editing = false;
            return;
        }

        //获取当前panel信息
        var panelInfo = {
            type: 'panel',
            panelId: $scope.rootPanel.now.panelId,
            panelTitle: $scope.modal.title.nameMod,
            spaceId: $scope.rootSpace.current.spaceId
        };

        //更新面板信息
        PanelServices.updatePanel(panelInfo)
        .then((data) => {
            for (var i = 0; i < $scope.rootPanel.list.length; i++) {
                if ($scope.rootPanel.list[i].panelId == panelInfo.panelId) {
                    $scope.rootPanel.list[i].panelTitle = $scope.modal.title.nameMod;
                    break;
                }
            }

            $scope.rootPanel.now.panelTitle = $scope.modal.title.nameMod;

            //面板位置更新
            panelOperationCallBack('edit', data.panelLayout, panelInfo)
        })
        .finally(()=>{
            $scope.modal.title.editing = false;
        })
    };

    //dashboard 标题修改焦点事件
    function dashboardTitleEditByKeydown(e) {
        var keycode = window.event ? e.keyCode : e.which;
        if (keycode == 13) {
            dashboardTitleEditDone();
        }
    }

    //移动端点击事件
    function mobileDashboardClick(item) {
        if (item.type == 'container') {
            //文件夹

            if (angular.isUndefined(item.fold)) {
                item['fold'] = true;
            } else {
                item.fold = !item.fold;
            }
        } else {
            //dashboard
            if ($scope.rootPanel.now === null || $scope.rootPanel.now !== null && $scope.rootPanel.now.panelId !== item.panelId) {
                $scope.dashboardSelect(item.panelId, 'select');
                $scope.ptPhone.menuBtn = !$scope.ptPhone.menuBtn;
            }
        }
    };

    //移动端获取dashboard
    function mobileGetDashboard(id) {
        for (var i = 0; i < $scope.rootPanel.list.length; i++) {
            if ($scope.rootPanel.list[i].panelId == id) {
                return $scope.rootPanel.list[i];
            }
        }
    }

    //移动端判断是否为分享页面
    function mobileIsShare(panelId) {
        var myPanel = PanelServices.getMyPanel($scope.rootPanel.list, panelId);
        return myPanel && myPanel.shareSourceId
    }

    //判断是否显示头部
    function isShowHead() {
        if (!$scope.rootPanel || !$scope.rootPanel.now || $scope.rootPanel.noData) return false;
        return (!$scope.rootPanel.now.shareSourceStatus && $scope.rootPanel.now.shareSourceStatus != 0) || [2].indexOf(+$scope.rootPanel.now.shareSourceStatus) >= 0 || $scope.pt.settings.isPhone;
    };

    //打开复制弹框
    function showCopyWidget(widgetId, widgetTitle, widgetType) {
		if ($scope.rootPage.dashboardMode != 'EDIT') toggleDashboardMode('EDIT');
        $scope.modal.widgetCopyShow = true;
        $scope.modal.copyWidgetId = widgetId;
        $scope.modal.copyWidgetTitle = widgetTitle;
        if (widgetType) { //复制widget时的提示语
            $scope.modal.copyWidgetType = 'tool';
        } else {
            $scope.modal.copyWidgetType = 'chart';
        }

		//全站事件统计
		siteEventAnalyticsSrv.createData({
			"uid": $rootScope.userInfo.ptId,
			"where": $scope.rootPage.dashboardMode === 'EDIT' ? 'panel_edit_mode' : 'panel_read_mode',
			"what":"copy_widget",
			"how":"click"
		});
    };

    //显示面板操作界面(新增、删除、编辑、复制、分享、文件夹删除)
    function showPanelOperation(type, panelInfo){
        if (panelInfo) $scope.modal.panel.info = panelInfo;
        $scope.modal.panel[type] = true;
    };

    //隐藏面板操作界面(新增、删除、编辑、复制、分享、文件夹删除)
    function hidePanelOperation(type,option){
		//全站事件统计
		if(!option){
			var what;
			if(type === 'edit'){
				what = 'edit_dashboard_cancel';
			}
			switch(type){
				case 'edit':
					what = 'edit_dashboard_cancel';
					break;
				case 'copy':
					what = 'copy_dashboard_cancel';
					break;
				case 'delete':
					what = 'delete_dashboard_cancel';
					break;
				case 'deleteFolder':
					what = 'folder_delete_cancel';
			}

			Track.log({where: 'panel', what: what});
		}

        $scope.modal.panel[type] = false;
        $scope.modal.panel.info = null;
        body.removeClass('modal-open');
    };

    //面板操作的回调方法(新增、删除、编辑、复制、分享、文件夹新增、文件夹删除、文件夹编辑)
    function panelOperationCallBack(type, layout, data){
        var panelLayout = angular.fromJson(layout.panelLayout);
        body.removeClass('modal-open');

        switch(type){

            //面板删除
            case "delete":

                //更新前端dashboard列表
                for (var i = 0; i < $scope.rootPanel.list.length; i++) {
                    if ($scope.rootPanel.list[i].panelId == data.panelId) {
                        $scope.rootPanel.list.splice(i, 1);
                        break;
                    }
                }
                //如果删除的面板就是当前选中的面板，则判断面板位置信息中是否存在面板，如存在则默认选中第一个
                // if(data.panelId == $scope.rootPanel.now.panelId){
                //     PanelServices.layoutHasPanel(panelLayout, function(flag){
                //         if(flag){
                //             dashboardSelectFirst(panelLayout);
                //         }
                //         else {
                //             clearData();
                //         }
                //     })
                // }
                break;

            //面板新增
            case "add":
                $scope.rootPanel.list.push(data);
                $scope.rootPanel.noData = false;
                $scope.rootWidget.linkData.showTips = false;
                $scope.dashboardSelect(data.panelId, 'add');
                break;

            //面板复制
            case "copy":

                $scope.rootPanel.list.push(data);
                $scope.dashboardSelect(data.panelId, 'copy');
                break;

            //面板编辑
            case "edit":

                for (var i in $scope.rootPanel.list) {
                    if ($scope.rootPanel.list[i].panelId == data.panelId) {
                        $scope.rootPanel.list[i].panelTitle = data.panelTitle;
                        $scope.rootPanel.list[i].description = data.description;
                        break;
                    }
                }
                break;

            //面板分享
            case "share":

                for (var i in $scope.rootPanel.list) {
                    if ($scope.rootPanel.list[i].panelId == data.panelId) {
                        $scope.rootPanel.list[i] = data;
                        break;
                    }
                }
                $scope.rootPanel.now = data;
                break;

            //文件夹新增
            case "addFolder":
                for (var i = panelLayout.length - 1; i >= 0; i--) {
                    if(panelLayout[i].containerId == data.panelId){
                        panelLayout[i].editing = true;
                        break;
                    }
                }
                break;

            //文件夹删除
            case "deleteFolder":

                //文件夹内的面板批量删除
                if(data.length>0){
                    for (var j = 0; j < data.length; j++) {
                        for (var i = 0; i < $scope.rootPanel.list.length; i++) {
                            if (data[j] == $scope.rootPanel.list[i].panelId) {
                                $scope.rootPanel.list.splice(i, 1);
                            }
                        }
                    }
                }

                //判断当前面板位置信息中是否存在面板，有则选中第一个
                // PanelServices.layoutHasPanel(panelLayout, function(flag){
                //     if(flag){
                //         dashboardSelectFirst(panelLayout);
                //     }
                //     else {
                //         clearData();
                //     }
                // })
                break;

            //文件夹编辑
            case "updateFolder":
                break;

            //面板拖拽
            case "dragEnd":
                //当拖拽位置更新成功，则返回版本号
                $scope.rootPanel.layout.dataVersion = data;
                break;
        }

        if(['share', 'dragEnd'].indexOf(type) < 0){
            //更新位置信息
            if(angular.toJson($scope.rootPanel.layout.panelLayout) != angular.toJson(panelLayout)){
                $scope.rootPanel.layout.panelLayout = panelLayout;
                checkLayout();
            }
            $scope.rootPanel.layout.dataVersion = layout.dataVersion;

            //隐藏面板操作界面
            hidePanelOperation(type,'no-pt-log')
        }
    };

    //校验面板位置信息
    function checkLayout(){
        var layout = $scope.rootPanel.layout.panelLayout;

        //判断是否存在面板
        PanelServices.layoutHasPanel(layout, function(flag){
            if(flag){

                //判断当前选中面板是否被删除
                if($scope.rootPanel.now && $scope.rootPanel.now.panelId){
                    PanelServices.layoutHasPanel(layout, function(flag){
                        if(!flag){
                            dashboardSelectFirst(layout);
                        }
                    }, $scope.rootPanel.now.panelId)
                }
            }
            else {
                clearData();
            }
        })
    }

    //面板操作失败的回调方法
    function panelOperationFailureCallBack(type, data){
        hidePanelOperation(type);
        showTips('sendError');

        if(type == 'dragEnd' && data){
            $scope.rootPanel.layout.panelLayout = angular.fromJson(data.panelLayout.panelLayout);
            $scope.rootPanel.layout.dataVersion = data.panelLayout.dataVersion;
            $scope.rootPanel.list = data.panelList;

            checkLayout();
        }
    };

    //当面板为空时，清除公用数据
    function clearData(){
        $scope.rootPanel.now = {};
        $scope.rootPanel.nowId = null;
        $scope.rootPanel.noData = true;
        $scope.rootWidget.list = [];
        $scope.pt.settings.headFolded = false;
        $scope.modal.addPanelDetailed = false;

        //清除本地选中记录
        PanelServices.clearLocalStorage($scope.rootSpace.current.spaceId);
        toggleLoadingSrv.hide('widgetList');
    }

    //批量授权指令
    function linkData(ds, e) {
		//查看模式下，需要进入编辑模式
		if($scope.rootPage.dashboardMode !== 'EDIT'){
			toggleDashboardMode('EDIT');
		}
		//在全屏模式下点击，退出全屏后进入编辑模式
		if($scope.pt.settings.fullScreen){
			screenfull.exit();
			$scope.pt.settings.fullScreen = false;
			$scope.pt.settings.headFolded = true;
			$scope.pt.settings.asideFolded = true;
			$scope.pt.settings.asideFoldAll = true;
			//查看模式下,将widget缩小
			var colWidth = Math.max(parseInt(30 * (window.screen.width / 1366)), 28);//1366为设计时的标准宽度

			if (screenfull.enabled) {
				document.addEventListener(screenfull.raw.fullscreenchange, function () {
					$timeout(
						function () {
							$(window).resize();
						},
						800
					)
				});
			}
			$scope.rootChart.colWidth = colWidth;
			$scope.rootChart.rowHeight = colWidth;
			$scope.rootPage.contentWidth = parseInt($scope.gridstackOptions.width * colWidth);
		}
		var dom = angular.element(e.target);
        if (e.target.nodeName != 'A') {
            dom = angular.element(e.target).parents('a');
        }
        var left = dom.position().left + parseInt(dom.width() / 2) + 6;

        if ($scope.rootWidget.linkData.currentDs == ds) {
            $scope.rootWidget.linkData.showDire = !$scope.rootWidget.linkData.showDire;
        } else {
            $scope.rootWidget.linkData.showDire = true;
        }
        $scope.rootWidget.linkData.currentDs = $scope.rootWidget.linkData.showDire ? ds : null;
        $scope.rootWidget.linkData.currentDsLeft = left;
        $scope.modal.editorShow = $scope.rootTmpData.editorShow = false;
    };

    //创建全新的widget
    function widgetCreate(type, graphName){
        var ptId = $rootScope.userInfo.ptId;
        var spaceId = $scope.rootSpace.current.spaceId;
        var panelId = $scope.rootPanel.nowId;
        var profileSelected = $scope.rootUser.profileSelected;
        var userSelected = $scope.rootUser.userSelected;
        var mapCode = ProductConfig.defaultMapCode;
        var sendWidget = WidgetServices.widgetCreate(type, graphName, panelId, spaceId, ptId, mapCode, profileSelected, userSelected)

        //保存
        WidgetServices.add(sendWidget)
        .then((data) => {
            sendWidget._ext = {}; // 扩展字段，用于前端临时数据存储，不持久化到库中

            var sizeX = 12;
            var minx = 6;
            var sizeY = 8;
            var miny = 8;
            sendWidget.sizeX = sizeX;
            sendWidget.sizeY = sizeY;
            sendWidget.minSizeX = minx;
            sendWidget.minSizeY = miny;
            sendWidget.autoPos = 1;

            if (type == 'tool') {
                if(graphName == 'text'){
                    sendWidget.minSizeX = 3;
                    sendWidget.minSizeY = 2;

                    $scope.$broadcast('createWidgetOfTool', 'new');
                }
                else if(graphName == 'heatmap'){
                    sendWidget.minSizeX = 13;
                    sendWidget.minSizeY = 14;
                    sendWidget.sizeX = 20;
                    sendWidget.sizeY = 18;
                }
            }

            //前台新增widget
            $scope.rootWidget.noData = false;
            $scope.rootWidget.list.push(sendWidget);

            //打开编辑器
            widgetEdit(sendWidget, $scope.rootWidget.list.length - 1, 'create');

            //位置更新
            widgetLayoutUpdate();

            //GTM
            siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget', 'add_' + type);

            //全站事件统计
            siteEventAnalyticsSrv.createData({
                "uid": $rootScope.userInfo.ptId,
                "where":"panel",
                "what":"edit_panel_add_widget",
                "how":"click",
                "value": graphName || type
            });
        })
    }

    //widget位置信息更新
    function widgetLayoutUpdate() {
        if (!$scope.pt.settings.isPhone) {
            var widgetList = $scope.rootWidget.list;
            var currentDashboard = $scope.rootPanel.now;
            var currentSpaceId = $scope.rootSpace.current.spaceId;

            gridstackService.updateLayout(widgetList, currentDashboard.panelId, currentSpaceId, currentDashboard.layout, widgetLayoutUpdateAfter);
        }
    };

    //widget位置更新后的回调
    function widgetLayoutUpdateAfter(dashboardId, layout) {
        for (var i = 0; i < $scope.rootPanel.list.length; i++) {
            if ($scope.rootPanel.list[i].panelId == dashboardId) {
                $scope.rootPanel.list[i].layout = layout;

                console.log('更新坐标成功');
                console.log($scope.rootPanel.list[i]);
                break;
            }
        }
    }

    //widget编辑
    function widgetEdit(widget, index, type) {
		//全站事件统计
		siteEventAnalyticsSrv.createData({
			"uid": $rootScope.userInfo.ptId,
			"where": $scope.rootPage.dashboardMode === 'EDIT' ? 'panel_edit_mode' : 'panel_read_mode',
			"what":"edit_widget",
			"how":"click"
		});
        //判断是否为拖拽事件
        if(angular.element('.li-widget').eq(index).attr('data-drag') == 'dragStart') return;
        if ($scope.rootPage.dashboardMode != 'EDIT') toggleDashboardMode('EDIT');

        $scope.modal.demoShow = false;
        var judgeDataEdit = setInterval(function() {
            if (angular.element('.editMode').length > 0 && angular.element('.li-widget')[index]) {
                clearInterval(judgeDataEdit);

                if(type && ['create', 'copy'].indexOf(type) >= 0){
                    var currentWidget = $('.widget[data-widget-id="'+widget.baseWidget.widgetId+'"]');
                    var y = currentWidget.offset().top;
                    $(document).scrollTop(y - 60 - 50);
                    currentWidget.addClass("panel-border");
                }

                if (widget.baseWidget.graphName == 'heatmap' || widget.baseWidget.widgetType !== 'tool') {
                    showEditor(widget, index);
                }
            }
        }, 20);
    };

    //计算位置信息及存储当前widget信息
    function showEditor(widget, index) {
        var ew = 405; //编辑器展开后的具体宽度（整个editor包括两部分，这里的编辑器是比较宽的那部分）
        var eh = 300; //编辑器展开后的具体高度
        var w = parseInt(angular.element('.li-widget')[index].offsetWidth); //所在widget的宽度
        var x = getRect(angular.element('.li-widget')[index]).left; //所在widget距离页面左侧距离
        var y = getRect(angular.element('.li-widget')[index]).top; //所在widget距离页面顶部距离
        var s = 'editor-r';

        if ($scope.modal.editorShow) {
            var min_y = 60;
            var max_x = document.documentElement.clientWidth - 20 - ew;
            var max_y = document.documentElement.clientHeight - 20 - eh;
            var default_x = 0;
            var default_y = y > max_y ? max_y : y;
            default_y = default_y < min_y ? min_y : default_y;

            if (x + w + parseInt($scope.modal.editorElemnet[0].offsetWidth) > max_x) {
                s = 'editor-l';

                if (x > w) {
                    default_x = x - parseInt($scope.modal.editorElemnet[0].offsetWidth) - 10;
                } else {
                    default_x = document.documentElement.clientWidth - parseInt($scope.modal.editorElemnet[0].offsetWidth) - 10;
                }
            } else {
                default_x = x + w + 10;
            }


            $scope.modal.editorElemnet.attr('class', 'editor ' + s).css({
                'left': default_x + 'px',
                'top': default_y + 'px'
            });

            $scope.modal.editorOpen = true;
        } else {
            $timeout(function() {
                $scope.$apply(function() {
                    $scope.modal.editorShow = $scope.rootTmpData.editorShow = true;
                })
            }, 0);
            $scope.modal.editorPlace = { 'x': x, 'y': y, 'w': w, 's': s, 'ew': ew, 'eh': eh };
        }

        //打开编辑器的时候，创建一个widget的副本，以备自定义widget使用，现在打开widget必须通过点击编辑按钮才可以。之前的逻辑是：面板处于编辑模式下，只要编辑器是打开的，就可以选中任意一个自定义widget，现在修改为：即使处于编辑模式下，即使编辑器打开了，但是也只能编辑当前大widget下的小widget，不能编辑其他大widget下的小widget
        $scope.modal.editorNow = $scope.modal.editorNowCopy = widget;
        $scope.modal.editorNowIndex = index;
    }

    //demo数据显示
    function showWidgetDemo(widget, index) {
        $scope.modal.editorShow = $scope.rootTmpData.editorShow = false;
        var w = parseInt(angular.element('.li-widget')[index].offsetWidth);
        var x = getRect(angular.element('.li-widget')[index]).left;
        var y = getRect(angular.element('.li-widget')[index]).top;

        if ($scope.modal.demoShow) {
            var max_x = document.documentElement.clientWidth - 30;
            var default_x = 0;

            if (x + w + parseInt($scope.modal.editorElemnet[0].offsetWidth) > max_x) {
                default_x = document.documentElement.clientWidth - parseInt($scope.modal.editorElemnet[0].offsetWidth) - 10;
            } else {
                default_x = x + w + 10;
            }


            $scope.modal.editorElemnet.css({
                'left': default_x + 'px',
                'top': y + 'px'
            })
        } else {
            $scope.modal.demoShow = true;
            $scope.modal.editorPlace = { 'w': w, 'x': x, 'y': y };
            $scope.modal.editorNowCopy = widget
        }

        $scope.modal.editorNow = widget;
        $scope.modal.editorNowIndex = index;

        if (!widget.toolData) {
            widget.toolData = {};
        }
        if (widget.baseWidget.ptoneGraphInfoId == 800) {
            $scope.demoChartData = [];
            $.each(widget._ext.demoData.series, function(i, serie) {
                $.each(serie.data, function(j, data) {
                    var row = [];
                    $.each(data, function(k, record) {
                        row.push(record);
                    });
                    $scope.demoChartData.push(row);
                })
            })
        }
        else if (widget.baseWidget.ptoneGraphInfoId == 900) {
            $scope.demoChartData = [];
            $.each($scope.modal.editorNow._ext.demoData.metricsAmountsMap, function(key, value) {
                $scope.metricsName = value.showName;
                return false;
            });
            var o = {
                metrics: $scope.metricsName
            };
            $scope.demoChartData.push(o);
            $.each(widget._ext.demoData.series, function(i, serie) {
                $.each(serie.data, function(j, data) {
                    var map = {
                        code: data.code,
                        value: data.value[0][$scope.metricsName]
                    };
                    if (data.code != "(not set)") {
                        $scope.demoChartData.push(map);
                    }
                    /*$.each(data,function(k,record){

                     })*/

                })
            })
        }
        else {
            $scope.demoChartData = {
                xAxis: widget._ext.demoData.categories,
                series: []
            };
            var flag = false;
            if (!$scope.demoChartData.xAxis || $scope.demoChartData.xAxis.length == 0) {
                flag = true;
            }
            //初始化demo中的数据（从widget中获取）
            $.each(widget._ext.demoData.series, function(i, serie) {
                var obj = {
                    name: serie.name,
                    data: []
                };
                $.each(serie.data, function(j, data) {
                    obj.data.push(data.y);
                    if (flag) {
                        $scope.demoChartData.xAxis.push(data.name);
                    }
                });
                $scope.demoChartData.series.push(obj);
            })
        }
        //界面显示用
        $scope.showChartData = angular.toJson($scope.demoChartData);
    };

    //提示-显示
    function showTips(type) {
        var options = {
            title: null,
            info: null,
            btnLeftText: $translate.instant('COMMON.CANCEL'),
            btnRightText: $translate.instant('COMMON.OK'),
            btnLeftClass: 'pt-btn-default',
            btnRightClass: 'pt-btn-success',
            btnLeftEvent: 'closeTips()',
            btnRightEvent: 'closeTips()',
            closeEvent: 'closeTips()',
            btnLeftHide: 'false',
            btnRightHide: 'false',
            hdHide: 'false'
        };

        switch (type) {
            //发送数据失败提示(纯文字提示)
            case "sendError":
                options.type = 'tips';
                options.btnLeftText = $translate.instant('COMMON.DELETE');
                options.btnRightText = $translate.instant('COMMON.CANCEL');
                options.info = $translate.instant('TIP.ERROR.SEND_DATA_ERROR');
                break;
        }

        $scope.modal.tips.show = true;
        $scope.modal.tips.options = options;
    }

    //提示-关闭
    function closeTips() {
        $scope.modal.tips.show = false;
    }

    //全局时间
    function localDate(dateKey) {
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
    };

    //模式切换(编辑||只读)
    function toggleDashboardMode(type, from) {
        $rootScope.$broadcast('sysRoleChanged');
        $rootScope.$emit('sysRoleChanged');
        $rootScope.$broadcast('permissionsChanged');
        $rootScope.$emit('permissionsChanged');
        $scope.rootPage.dashboardMode = type;

        if (type == 'EDIT') {
            $scope.pt.settings.editModel = true;
            $scope.pt.settings.headFolded = true;
            $scope.pt.settings.asideFolded = true;
            $scope.pt.settings.asideFoldAll = true;
            $scope.modal.dashboardTime.isOpen = false;
            gridstackService.enableLayout();

            //将全局时间退出
            if ($scope.rootPanel.now.components && $scope.rootPanel.now.components['GLOBAL_TIME'] && $scope.rootPanel.now.components['GLOBAL_TIME'].status == '1') {

                var sendData = {
                    panelId: $scope.rootPanel.nowId,
                    status: 1,
                    value: 'widgetTime',
                    itemId: 16,
                    code: 'GLOBAL_TIME',
                    name: 'GLOBAL_TIME'
                };
                PanelResources.applyPancelComponent(sendData)
                .then((data) => {
                    var index;
                    for (var i = 0; i < $scope.rootPanel.list.length; i++) {
                        if ($scope.rootPanel.list[i].panelId == $scope.rootPanel.now.panelId) {
                            index = i;
                            break;
                        }
                    }

                    if (index) {
                        angular.forEach($scope.rootPanel.list[index].components, function(value, key) {
                            $scope.rootPanel.list[index].components[key].status = 0;
                        });
                        $scope.rootPanel.list[index].globalComponentStatus = 0;
                    }

                    angular.forEach($scope.rootPanel.now.components, function(value, key) {
                        $scope.rootPanel.now.components[key].status = 0;
                    });
                    $scope.rootPanel.now.globalComponentStatus = 0;

                    $scope.modal.dashboardTime.dateKey = 'widgetTime';
                })
            }
        } else {
            $scope.pt.settings.editModel = false;
            if (!$scope.pt.settings.fullScreen) {
                $scope.pt.settings.headFolded = false;
                $scope.pt.settings.asideFolded = false;
                $scope.pt.settings.asideFoldAll = false;
            }

            $scope.modal.editorShow = $scope.rootTmpData.editorShow = false;

            gridstackService.disableLayout();

            if(from && from == 'editDone'){
                //全站事件统计
                siteEventAnalyticsSrv.createData({
                    "uid": $rootScope.userInfo.ptId,
                    "where":"panel",
                    "what":"edit_panel_done_editing",
                    "how":"click"
                });
            }
        }

        //批量授权信息重置
        $scope.rootWidget.linkData.showDire = false;
        $scope.rootWidget.linkData.currentDs = null;
    };

    function globalRefresh() {
        $scope.modal.dashboardRefresh.oldTime = null;
        $scope.modal.dashboardRefresh.status = 'ing';
        $scope.modal.dashboardRefresh.statusI18n = $translate.instant('PANEL.REFRESH.ING');

        datasourceFactory.reloadAllDatasource('user-refresh','no-cache');
    };

    //全局刷新初始化
    function dashboardRefreshInit() {
        $scope.modal.dashboardRefresh.isShow = null;
        $scope.modal.dashboardRefresh.showTime = null;
        $scope.modal.dashboardRefresh.oldTime = null;
        $scope.modal.dashboardRefresh.status = null;
    };

    //打开/关闭视频弹窗
    function toggleVideo(state) {
        if (state) {
            var local = 'zh';
            if ($scope.rootUser.settingsInfo.locale == 'en_US') {
                local = 'en'
            } else if ($scope.rootUser.settingsInfo.locale == 'ja_JP') {
                local = 'jp'
            }
            $scope.modal.onboarding.videoSrc = window.location.protocol + '//' + window.location.host + '/assets/video/video_' + local + '.mp4';
        }

        $scope.modal.onboarding.showVideo = state;

        //全站事件统计
        siteEventAnalyticsSrv.createData({
            "uid": $rootScope.userInfo.ptId,
            "where":"panel",
            "what":"edit_panel_video",
            "how":"click"
        });
    };

    //更新用户设置信息(视频)
    function updateShowTips() {
        var userSetting = angular.copy($scope.rootUser.settingsInfo.showTips);
        userSetting.dashboardVideo = 1;

        var sendData = {
            showTips: angular.toJson(userSetting)
        };

        //更新tips字段
        dataMutualSrv.post(LINK_SETTINGS_INFO_UPDATE, sendData).then(function(data) {
            if (data.status == 'success') {
                //更新前端数据
                $scope.rootUser.settingsInfo.showTips.dashboardVideo = 1;
                $scope.modal.onboarding.showVideo = false;
            } else {
                console.log('function tips update error!');
                if (data.status == 'error') {
                    console.log(data.message)
                }
            }
        });

        //全站事件统计
        siteEventAnalyticsSrv.createData({
            "uid": $rootScope.userInfo.ptId,
            "where":"panel",
            "what":"edit_panel_close_video_banner",
            "how":"click"
        });
    };

    //面板选中
    function dashboardSelect(panelId, type) {
        var space = $scope.rootSpace.current;
        var shareSourceId;

        //模版添加
        if ($scope.rootTmpData.addTemplate !== null) {
            type = 'addTmp';
            panelId = $scope.rootTmpData.addTemplate.panelId;
            $scope.rootTmpData.addTemplate = null;
        }

        $scope.rootWidget.list = [];
        $scope.rootPanel.nowId = panelId;


        //面板列表中校验
        var flag = false;
        for (var i = $scope.rootPanel.list.length - 1; i >= 0; i--) {
            if($scope.rootPanel.list[i].panelId == panelId){

                flag = true;
                shareSourceId = $scope.rootPanel.list[i].shareSourceId;
                //更新前端选中
                $scope.rootPanel.now = $scope.rootPanel.list[i];
				//更新location
				$location.search("panelId", panelId);
                break;
            }
        }

        if(flag){
            //面板选择事件
            panelSltSrv.dashboardSelected(panelId, space.spaceId, type, dashboardSelectBack, false, $scope.pt.settings.isPhone, shareSourceId);
        }
        else {
            //请求panel信息
            PanelServices.getPanelInfo(panelId)
            .then((data) => {
                shareSourceId = data.shareSourceId;
                //更新前端选中
                $scope.rootPanel.now = data;
                //更新location
                $location.search("panelId", panelId);

                //面板选择事件
                panelSltSrv.dashboardSelected(panelId, space.spaceId, type, dashboardSelectBack, false, $scope.pt.settings.isPhone, shareSourceId);
            })
        }
    };

    //面板选中后的回调函数
    function dashboardSelectBack(type, data) {
        switch (type) {
            case "init":
                $scope.dashboardRefreshInit();
                $scope.dashboardTimeInit();
                $scope.toggleDashboardMode(data);
                $scope.rootPanel.panelInit = true;
                break;
            case "getWidgetListBefore":
                $scope.rootWidget.list = [];
                $scope.rootWidget.drawChart = false;
                $scope.loadSetting.dashboard = true;
                $scope.modal.widgetRepeatFinish = false;
                $scope.pt.loadFinish.bodyTimeout = false; //超时提示先去掉
				$scope.sharePasswordMsg.sharePassword = '';
				$scope.sharePasswordMsg.show = false;
                toggleLoadingSrv.show('widgetList');
                break;
            case "getWidgetList":
                $scope.loadSetting.dashboard = false;
                if ($scope.rootPanel.now.shareSourceStatus == '3') {
                    toggleLoadingSrv.hide('widgetList');
                }
                break;
            case "getWidgetListTimeOut":
                $scope.pt.loadFinish.bodyTimeout = true;
                break;
            case "getWidgetListAfter":
                $scope.rootWidget.list = data;
                $scope.rootWidget.noData = false;

                if (data.length > 0) {
                    //如果返回有list列表，证明shareSourceStatus状态为2
                    $scope.rootPanel.now.shareSourceStatus = "2";
                }
                break;
            case "getWidgetListAfterIsEmpty":
                $scope.rootWidget.noData = true;
                $scope.rootWidget.list = [];
                $scope.rootWidget.linkData.showTips = false;
                $scope.modal.widgetRepeatFinish = true;
                break;
            case "hideLoading":
                toggleLoadingSrv.hide('widgetList');
                break;
            case "updateDashboardMessage":
                $scope.rootPanel.now.shareSourceStatus = data;
                toggleLoadingSrv.hide('widgetList');
                break;
            case "linkDataUpdate":
                linkDataSrv.update($scope);
                break;
        }
    }

    //选中面板位置中的第一个面板（如没找到第一个，则显示无面板提示）
    function dashboardSelectFirst(panelLayout){
        PanelServices.clearLocalStorage($scope.rootSpace.current.spaceId);
        PanelServices.layoutFindPanel(panelLayout, 'first', function(panel){
            if(panel){
				var panelTemp = PanelServices.getMyPanel($scope.rootPanel.list, panel.panelId);
                $scope.dashboardSelect(panel.panelId, 'del');
            }
            else {
                clearData();
            }
        });
    }

    /**
     * 获取Dashboard List
     */
    function getDashboardList() {
        PanelServices.getPanels($scope.rootSpace.current.spaceId)
        .then((data) => {
            $scope.rootPanel.list = data.panelList;
			$scope.rootPanel.layout = data.panelLayout;

			//onboarding 成功提示
			var currentDashboard = localStorage.getItem('currentOnboardingStatus');
			if (currentDashboard && currentDashboard == "isSuccess") {
				$scope.modal.onboarding.showTips = true;
				localStorage.removeItem("currentOnboardingStatus");

				//3秒后关闭
				$timeout(function() {
					$scope.$apply(function() {
						$scope.modal.onboarding.showTips = false;
					})
				}, 3000);
			}

			return $q.resolve(data.panelLayout);
        }, (error)=> {
            toggleLoadingSrv.hide('widgetList');
        })
        .then((data) => {
            var sendData = angular.copy(data);

            PanelServices.layoutCheck(angular.fromJson(data.panelLayout), function(hasPanel, layout){
                $scope.rootPanel.layout.panelLayout = layout;

                if(hasPanel){
                    //默认点击panel
                    dashboardDefaultClick();
                }
                else {
                   toggleLoadingSrv.hide('widgetList');
                   clearData();
                }

                //位置信息校验后，如果有修改则更新
                if(angular.toJson(layout) != sendData.panelLayout){
                    sendData.panelLayout = angular.toJson(layout);
                    sendData.updateTime = parseInt(new Date().getTime());
                    PanelServices.updatePanelLayout(sendData);
                }
            })
        })
        .finally(() => {
            showPanelOperation('list');
        })
    }

    //默认选中
    function dashboardDefaultClick() {

        //当前路由地址在panel下时，默认点击panel
        if ($state.current.name == 'pt.dashboard' && $scope.rootPanel.list.length > 0 && $scope.rootPanel.layout.panelLayout.length > 0) {
			var panelLayout = $scope.rootPanel.layout.panelLayout;
			var currentPanelId = $location.search().panelId;
			if(currentPanelId){
				PanelServices.layoutHasPanel(panelLayout, function(result){
					if(result == false){
						currentPanelId = undefined;
					}
				}, currentPanelId);
			}
			if(!currentPanelId){
				currentPanelId = getPanelIdOfLocalStorage(panelLayout);
			}
			//在面板位置列表中找到对应的具体面板，且打开对应的文件夹状态
			PanelServices.layoutFindPanel(panelLayout, currentPanelId, function(panel){
				if(panel){
					var parentId = panel ? panel.panelId : $scope.rootPanel.list[0].panelId;
					var panel = PanelServices.getMyPanel($scope.rootPanel.list, parentId);
					$scope.dashboardSelect(parentId, 'select');
					//更新文件夹打开状态
					$scope.rootPanel.layout.panelLayout = panelLayout;
				}
			});
        }
    }

	/**
	 * 查找本地存储
	 * @param panelLayout
	 * @returns {undefined}
     */
    function getPanelIdOfLocalStorage(panelLayout){
		var result = undefined;
		var spaceId = $scope.rootSpace.current.spaceId;
		var ptId = $rootScope.userInfo.ptId;
		var currentDashboard = localStorage.getItem('currentDashboard') ? angular.fromJson(localStorage.getItem('currentDashboard')) : null;

		//获取localStorage中的选中信息
		if (currentDashboard && currentDashboard[spaceId] && currentDashboard[spaceId][ptId]) {
			var localStoragId = currentDashboard[spaceId][ptId];

			//判断是否来自分享
			if (localStoragId.indexOf('fromShare') >= 0) {
				var shareSourceId = localStoragId.slice(9);

				for (var i = 0; i < $scope.rootPanel.list.length; i++) {
					if($scope.rootPanel.list[i].shareSourceId == shareSourceId){
						localStoragId = $scope.rootPanel.list[i].panelId;
						break;
					}
				}
			}
			PanelServices.layoutHasPanel(panelLayout, function(flag){
				if(flag){
					result = localStoragId;
				}
				else {
					//如果不存在，则删除localStorage中存储的
					PanelServices.clearLocalStorage($scope.rootSpace.current.spaceId);
				}
			}, localStoragId);
		}

		return result;
    }

	function checkPassword(password,panel){
		if(!password){
			$scope.sharePasswordMsg.show = true;
			$scope.sharePasswordMsg.error = false;
			$scope.sharePasswordMsg.require = true;
			return false;
		}
		var sendData = {
			dashboardId: panel.shareSourceId,
			password: encodeURIComponent($scope.sharePasswordMsg.sharePassword)
		};

        PanelResources.sharePanelVerifyPassword(sendData)
        .then((data) => {
            localStorage.setItem('sharePassWord-' + panel.shareSourceId, encodeURIComponent($scope.sharePasswordMsg.sharePassword));
            $scope.sharePasswordMsg.show = false;
            $scope.sharePasswordMsg.error = false;
            $scope.sharePasswordMsg.require = false;
            $scope.dashboardSelect(panel.panelId,'select',panel.shareSourceId);
        }, (data) => {
            $scope.sharePasswordMsg.show = true;
            $scope.sharePasswordMsg.error = true;
            $scope.sharePasswordMsg.require = false;
        })
	}

    //校验当前空间是否有ptengine授权账户(控制添加热图按钮)
    function hasPtengineAccount(){
        var flag = false;
        for (var i = $scope.rootCommon.dsAuthList.length - 1; i >= 0; i--) {
            if($scope.rootCommon.dsAuthList[i].dsCode == 'ptengine'){
                flag = true;
                break;
            }
        }

        return flag;
    }

    //下载
    function downloadWidget(widget, index){
        if ($scope.modal.download.isBegin) {
            return;
        }
        var _panel = $scope.rootPanel.now;

        //通过widget来下载当前widget的数据
        widgetDownloadUtils.download(widget, _panel, $scope);

        //全站事件统计
        siteEventAnalyticsSrv.createData({
            "uid": $rootScope.userInfo.ptId,
            "where": 'panel_read_mode',
            "what":"csv_download",
            "how":"click"
        });
    }

    // =====================

    //页面滚动事件监测
    $(window).scroll(function() {
        var scrollTop = $(this).scrollTop();
        if ($state.current.name == 'pt.dashboard' && !$scope.pt.settings.isPhone) {
            if (scrollTop > 0 && $scope.rootPage.dashboardMode == 'EDIT') {
                //编辑模式

                $('.content').addClass('content-fixed');
            } else if (scrollTop > 51 && $scope.rootPage.dashboardMode == 'READ') {
                //只读模式

                $('.content').addClass('content-fixed');
                $('.content-aside').css('top', (100 - 51) + 'px');
                $('.pt-header').css('top', '-51px');
                $('.pt-aside').css('top', '0px')
            } else {
                $('.content').removeAttr('style').removeClass('content-fixed');
                $('.content-aside').css('top', parseInt(100 - scrollTop) + 'px');
                $('.pt-header').css('top', parseInt(0 - scrollTop) + 'px');
                $('.pt-aside').css('top', parseInt(50 - scrollTop) + 'px')
            }
        }
    });

    //监听路由切换时，关闭编辑器
    $scope.$watch('$state.current.name', function(newData, oldData) {
        if (newData !== 'pt.dashboard') {
            $scope.modal.editorShow = $scope.rootTmpData.editorShow = false;

            $('.content').removeClass('content-fixed');
            $('.aside').css('top', '50px');
            $('.header').css('top', '0px');
        }
    });

    /**
     * 接受来自切换自定义widget中的小widget的编辑事件，需要重新初始化编辑器
     * 在source.dire中也被调用
     * 在data.dire中也被调用
     */
    $scope.$on('changeCustomWidget', function(e, d) {
        $scope.$broadcast('reInitializeEditor');
    });

    //接受widget的上次刷新时间
    $scope.$on('widgetLastLoadTime', function(e, d) {

        if ($scope.modal.dashboardRefresh.oldTime && d) {
            try {
                 if ((d.getTime() - $scope.modal.dashboardRefresh.oldTime.getTime()) < (20 * 60 * 1000)) {
                     return;
                 }
             } catch (e) {
                 return
             }
        }
        $scope.modal.dashboardRefresh.isShow = true;
        $scope.modal.dashboardRefresh.showTime = d;
        $scope.modal.dashboardRefresh.oldTime = d;
        $scope.modal.dashboardRefresh.status = 'success';
        $scope.modal.dashboardRefresh.statusI18n = $translate.instant('PANEL.REFRESH.SUCCESS');

        $timeout(function() {
            $scope.modal.dashboardRefresh.status = null;
        }, 3000);
    });

}
