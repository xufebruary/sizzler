'use strict';

/**
 * panelSltSrv
 * Panel点击事件
 *
 */

import {
    OTHER_LOGIN_WEB_SOCKET,
    LINK_PANEL_WIDGET_WITH_LAYOUT,
    LINK_PANEL_WIDGET_WITH_LAYOUT_MOBIL,
    getMyDsConfig,
    uuid
} from '../../components/modules/common/common';

panelSltSrv.$inject = ['$rootScope','$location', '$stateParams', '$state', 'dataMutualSrv', 'websocket', 'publicDataSrv', 'datasourceFactory', 'loadAllWidgetData', 'PanelResources'];
function panelSltSrv($rootScope, $location, $stateParams, $state, dataMutualSrv, websocket, publicDataSrv, datasourceFactory, loadAllWidgetData, PanelResources) {
    var current;

    return {
        /**
         * 面板选中事件
         * @param panelId: 当前选中面板ID
         * @param spaceId: 当前空间ID
         * @param type: 选中面板操作类型(add,del,copy,addTmp,select)
         * @param backFunc: 操作回调函数(dashboard.ctrl.js中定义)
         * @param isShare: 区分是否为分享面板
         * @param isPhone: 区分是否为移动端
		 * @param shareSourceId: 分享panel的shareSourceId
         */
        dashboardSelected: function(panelId, spaceId, type, backFunc, isShare, isPhone, shareSourceId) {

            //当前状态存储
            current = {
                backFunc: backFunc,
                type: type,
                ptId: $rootScope.userInfo.ptId,
                isShare: isShare || false,
                isPhone: isPhone || false,
                spaceId: spaceId,
                panelId: panelId,
				shareSourceId: shareSourceId,
                matrixArray: []
            };

			//面板功能(全局刷新、全局时间、全局模式)状态
            var mode = ['add', 'copy', 'addTmp'].indexOf(current.type) >= 0 ? 'EDIT' : "READ";

            current.backFunc('init', mode);

            //初始化
            init();
        }
    };


    /**
     * 选中初始化
     */
    function init() {
        //路由跳转
        if ($state.current.name != 'pt.dashboard') {
            $state.go('pt.dashboard');
        }

        //清除widget请求链接
        $rootScope.datasources = [];

        //更新localStorage中的space所选dashboard信息
        setLocalStorage();

        //socket初始化，链接成功后请求widget列表
        //initWidgetDataSocket(getWidgetList);
        getWidgetList();
    }


    /**
     * 更新localStorage中的space所选dashboard信息
     *
     * localStorage中存储的数据格式为:
     * {
     *      spaceId: {
     *          ptId: dashboardId,
     *          ....
     *      }
     *      ....
     * }
     */
    function setLocalStorage() {
        var currentDashboard = {};

        if (localStorage.getItem('currentDashboard')) {
            currentDashboard = angular.fromJson(localStorage.getItem('currentDashboard'));

            if (!currentDashboard[current.spaceId]) {
                currentDashboard[current.spaceId] = {};
            }
        } else {
            currentDashboard[current.spaceId] = {};
        }
        currentDashboard[current.spaceId][current.ptId] = current.panelId;
        localStorage.setItem('currentDashboard', angular.toJson(currentDashboard));
    }


    /**
     * 初始化widgetData-websocket, 链接socket;
     *
     */
    function initWidgetDataSocket(callBackFunc) {
        var socketData = publicDataSrv.getPublicData('socket');

        // 如果socket存在则跳过初始化
        if (!socketData.id) {
            var widgetDataSocketSign = 'WidgetData:' + uuid(); // widget取数push数据用websocket sign
            var isCloseFlag = false;

            socketData = {
                id: widgetDataSocketSign,
                func: new websocket,
                loadAllWidgetData: false
            };
            socketData.func.initWebSocket(OTHER_LOGIN_WEB_SOCKET + widgetDataSocketSign);
            publicDataSrv.setPublicData('socket', socketData);

            socketData.func.ws.onclose = function(event) {
                //清除数据后重连

                var _socketData = {
                    id: null,
                    func: null
                };
                publicDataSrv.setPublicData('socket', _socketData);
                initWidgetDataSocket();

                isCloseFlag = true;
            };

            socketData.func.ws.onopen = function(){
                if(callBackFunc) callBackFunc();

                //socket断开重连后,调取全局刷新widget
                if(isCloseFlag) {
                    datasourceFactory.reloadAllDatasource();
                    isCloseFlag = false;
                }
            }
        } else {
            //每次切换dashboard都需要更新批量请求字段
            socketData.loadAllWidgetData = false;
            publicDataSrv.setPublicData('socket', socketData);

            if(callBackFunc) callBackFunc();
        }
    }


    /**
     * 获取widgetList
     *
     */
    function getWidgetList() {
        //获取列表前将列表清空
        current.backFunc('getWidgetListBefore');

		//分享面板需要验证是否需要密码
		var password = '';
		if(current.shareSourceId){
			password = localStorage.getItem('sharePassWord-' + current.shareSourceId) ? encodeURIComponent(localStorage.getItem('sharePassWord-' + current.shareSourceId)) : '';
		}
        
        PanelResources.findWidget(null, {
            panelId: current.panelId,
            device: current.isPhone ? 'mobile' : 'pc',
            password: password
        }, {
           'timeout': 20000 
        })
        .then((data) => {
            var list = data.widgetList;
            var layout = data.layout ? angular.fromJson(decodeURIComponent(data.layout)) : null;

            if (list.length == 0) {
                current.backFunc('getWidgetListAfterIsEmpty');

                if (!current.isShare) {
                    current.backFunc('hideLoading');
                }
                return
            }

            getWidgetListBack(list, layout);
        }, (data) => {
            if(data.errorCode){
                if(data.errorCode == 'timeout'){
                    current.backFunc('getWidgetListTimeOut');
                }
                else if (['panel_share_deleted', 'panel_share_closed', 'panel_share_space_deleted','panel_share_password_error'].indexOf(data.errorCode) >= 0) {
                    //需要根据data.message字段更新分享的panel的状态
                    
                    var shareSourceStatus = '0';
                    if (data.errorCode == 'panel_share_closed') {
                        shareSourceStatus = '1';
                    } else if (data.errorCode == 'panel_share_space_deleted') {
                        shareSourceStatus = '3';
                    }
                    if(data.errorCode === 'panel_share_password_error'){
                        shareSourceStatus = '4';//这时候需要输入密码
                    }
                    current.backFunc('updateDashboardMessage', shareSourceStatus);
                }
            }
        })
        .finally((data) => {
            current.backFunc('getWidgetList');
        })
    }


    /**
     * 获取widgetList后的回调
     */
    function getWidgetListBack(list, layout) {
        var newList = [];
        var autoPosList = [];
        for (var i = 0; i < list.length; i++) {
            var widget = list[i];
            var widgetType = widget.baseWidget.widgetType;
            var widgetId = widget.baseWidget.widgetId;
            var graphName = widget.baseWidget.graphName;
            var sizeX = 6;
            var sizeY = 8;
            var minSizeX = 6;
            var minSizeY = 8;
            var row = null;
            var col = null;
            var autoPosFlag = false; //自动排序标志

            if (widgetType == 'tool') {
                if(graphName == 'text'){
                    sizeX = 3;
                    sizeY = 2;
                    minSizeX = 3;
                    minSizeY = 2;
                    widget.baseWidget.widgetEdit = false; //此富文本框处于编辑状态：默认false
                }
                else if(graphName == 'heatmap'){
                    sizeX = 3;
                    sizeY = 2;
                    minSizeX = 13;
                    minSizeY = 14;
                }
            }

            if (layout) {
                var isOverlapFlag = true;
                for (var j = 0; j < layout.length; j++) {
                    if (layout[j].id == widgetId) {
                        sizeX = +layout[j].x;
                        sizeY = +layout[j].y;

                        if (angular.isDefined(layout[j].r)) {
                            row = +layout[j].r;
                            col = +layout[j].c;

                            //校验是否重叠
                            isOverlapFlag = layoutIsOverlap(col, row, sizeX, sizeY);
                        }
                        break;
                    }
                }

                if(isOverlapFlag) autoPosFlag = true;
            }
            else {
                autoPosFlag = true;
            }

            //根据类chart数据格式转换
            if (widgetType == 'tool' && graphName == 'text') {
                try {
                    widget.toolData.value = decodeURIComponent(angular.copy(widget.toolData.value));
                } catch (err) {
                    widget.toolData.value = angular.copy(widget.toolData.value);
                }
            }

            //对自定义widget中的富文本框数据进行处理
            if (widgetType == 'custom') {
                if (widget.children.length > 0) {
                    for (var j = 0; j < widget.children.length; j++) {
                        if (widget.children[j].baseWidget.widgetType == 'tool' && widget.children[j].baseWidget.graphName == 'text') {
                            try {
                                widget.children[j].toolData.value = decodeURIComponent(angular.copy(widget.children[j].toolData.value));
                            } catch (err) {
                                widget.children[j].toolData.value = angular.copy(widget.children[j].toolData.value);
                            }
                        }
                    }
                }
            }

            widget.sizeX = sizeX;
            widget.sizeY = sizeY;
            widget.row = row;
            widget.col = col;
            widget.minSizeX = minSizeX;
            widget.minSizeY = minSizeY;

            if (!current.isShare) {
                widget.dsConfig = getMyDsConfig(widget.variables[0].dsCode);
            }

            //后台一并请求,为widget添加标示
            if (!widget.ext) widget.ext = {};
            // widget.ext['loadAll'] = true;

            if(autoPosFlag){
                //需要自动排序的widget需要放在widget列表后面渲染

                widget.autoPos = 1;
                autoPosList.push(widget);
            }
            else {
                newList.push(widget);
            }
        }

        current.backFunc('getWidgetListAfter', newList.concat(autoPosList));

        //批量授权提示(分享页面不需要提示)
        if (!current.isShare) {
            current.backFunc('linkDataUpdate');
        }
    }


    /**
     * 校验widget位置信息是否存在重叠
     * 按行和列生成2级数组，排查是否重叠
     * [r][c]
     */
    function layoutIsOverlap(c,r,x,y){
        var flag = false;
        var currentMatrix = angular.copy(current.matrixArray);

        for (var i = 0; i < y; i++) {
            if(!currentMatrix[r+i]) currentMatrix[r+i] = [];

            for (var j = 0; j<x; j++) {
                if(currentMatrix[r+i][c+j]){
                    flag = true;
                    break;
                }
                else {
                    currentMatrix[r+i][c+j] = true;
                }
            }

            if(flag) break;
        }

        if(!flag) current.matrixArray = angular.copy(currentMatrix);
        return flag;
    }
}

export default panelSltSrv;

