'use strict';


/**
 * widgetList
 * 当前panel下widget列表
 *
 */

import widgetListTpl from './widget-list.html';
import {
    getRect,
	getMyDsConfig
} from 'components/modules/common/common';


widgetListDirective.$inject = ['$state', '$timeout', 'dataMutualSrv', 'sysRoles', 'FileUploader', 'gridstackService','$rootScope','siteEventAnalyticsSrv', 'linkDataSrv', 'PanelServices', 'WidgetResources'];

function widgetListDirective($state, $timeout, dataMutualSrv, sysRoles, FileUploader, gridstackService,$rootScope,siteEventAnalyticsSrv,linkDataSrv,PanelServices,WidgetResources) {
    return {
        restrict: 'EA',
        transclude: false,
        replace: false,
        template: widgetListTpl,
        link: link
    };

    function link(scope, element, attrs) {
        var body = $('body');
        scope.myOptions = {

            widget: {
                add: false,
                delete: false,
                copy: false,
                edit: false,
                demo: false
            }
        };

        // =========

        //显示widget demo数据修改界面
        scope.showWidgetDemoData = showWidgetDemoData;

        //显示widget操作界面(复制、编辑、删除)
        scope.showWidgetOperation = showWidgetOperation;

        //隐藏widget操作界面(复制、编辑、删除)
        scope.hideWidgetOperation = hideWidgetOperation;

        //面板操作回调(复制、编辑、分享、删除)
        scope.widgetOperationCallBack = widgetOperationCallBack;

        //面板操作失败回调(复制、编辑、分享、删除)
        scope.widgetOperationFailureCallBack = widgetOperationFailureCallBack;

        //widget发送改变(拖拽)
        scope.onChange = onChange;

        //widget拖拽开始(拖拽)
        scope.onDragStart = onDragStart;

        //widget拖拽结束(拖拽)
        scope.onDragStop = onDragStop;

        //widget缩放开始(拖拽)
        scope.onResizeStart = onResizeStart;

        //widget缩放结束(拖拽)
        scope.onResizeStop = onResizeStop;

        //widget添加之后(拖拽)
        scope.onItemAdded = onItemAdded;

        //widget删除之后(拖拽)
        scope.onItemRemoved = onItemRemoved;

        // =========

        //显示widget操作界面(删除、编辑、复制、文件夹删除)
        function showWidgetOperation(type, widgetInfo){
            if (widgetInfo) scope.myOptions.widget.info = widgetInfo;
            scope.myOptions.widget[type] = true;
            body.addClass('modal-open');
            scope.toggleDashboardMode('EDIT');

            //全站事件统计
            siteEventAnalyticsSrv.createData({
                "uid": $rootScope.userInfo.ptId,
                "where": scope.rootPage.dashboardMode === 'EDIT' ? 'panel_edit_mode' : 'panel_read_mode',
                "what":"copy_widget",
                "how":"click"
            });
        };

        //隐藏widget操作界面(删除、编辑、复制、文件夹删除)
        function hideWidgetOperation(type){
            scope.myOptions.widget[type] = false;
            scope.myOptions.widget.info = null;
            body.removeClass('modal-open');
        };

        //面板操作的回调方法(删除、编辑、复制)
        function widgetOperationCallBack(type, data){

            switch(type){

                //widget删除(@data: {widgetId})
                case "delete":

                    for (var i = scope.rootWidget.list.length - 1; i >= 0; i--) {
                        if(scope.rootWidget.list[i].baseWidget.widgetId == data.widgetId){
                            scope.rootWidget.list.splice(i, 1);
                            break;
                        }
                    }

                    //判断当前删除的widget的编辑器是否已经打开
                    if (scope.modal.editorShow && data.widgetId == scope.modal.editorNow.baseWidget.widgetId) {
                        scope.modal.editorShow = scope.rootTmpData.editorShow = false;
                    }

                    //判断全局刷新是否显示
                    var showGlobalRefreshFlag = false;

                    //判断列表长度
                    if (scope.rootWidget.list.length == 0) {
                        scope.rootWidget.noData = true;
                    } 
                    else {

                        //更新全局刷新按钮
                        for (var i = 0; i < scope.rootWidget.list.length; i++) {
                            var baseWidget = scope.rootWidget.list[i].baseWidget;

                            if (baseWidget.widgetType != 'tool' && baseWidget.graphName != 'text') {
                                var variables = scope.rootWidget.list[i].variables[0];
                                var dsConfig = getMyDsConfig(variables.dsCode);

                                if (dsConfig.editor.data.drewChartNeedMetrics) {
                                    if (variables.metrics.length > 0) {
                                        showGlobalRefreshFlag = true;
                                        break;
                                    }
                                } else {
                                    if (variables.metrics.length > 0 || variables.dimensions.length > 0) {
                                        showGlobalRefreshFlag = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (showGlobalRefreshFlag) {
                        scope.modal.dashboardRefresh.isShow = true;
                    } else {
                        scope.dashboardRefreshInit();
                    }

                    //批量授权检测
                    linkDataSrv.update(scope);

                    //位置更新
                    widgetLayoutUpdate();

                    break;

                //widget复制(@data: {type, info, widgetId})
                case "copy":

                    //定位用
                    scope.rootWidget.locateId = data.widgetId;

                    if(data.type == 'currentPanel'){
                        //原页面复制(@data.info: widget)

                        //前端列表更新
                        scope.rootWidget.list.push(data.info);

                        //位置更新
                        widgetLayoutUpdate();

                        //如果当前widget编辑器已打开
                        if (scope.modal.editorShow){
                            scope.widgetEdit(data.info, scope.rootWidget.list.length-1, 'copy');
                        }
                    }
                    else {
                        //跨面板复制(@data.info: panelId)
                        
                        var panelId = data.info;
                        var panelLayout = scope.rootPanel.layout.panelLayout;
                        PanelServices.layoutFindPanel(panelLayout, panelId, function(panel){
                            if(panel){
                                scope.dashboardSelect(panel.panelId, 'copy');
                                //更新文件夹打开状态
                                scope.rootPanel.layout.panelLayout = panelLayout;
                            }
                        });
                    }

                    break;

                //demo数据(@data: {widget})
                case "demo":

                    for (var i = scope.rootWidget.list.length - 1; i >= 0; i--) {
                        if(scope.rootWidget.list[i].baseWidget.widgetId == data.widget.baseWidget.widgetId){
                            
                            // scope.rootWidget.list[i] = data.widget;

                            setTimeout(function() {
                                scope.$apply(function(){
                                    scope.rootWidget.list[i].baseWidget.isDemo = data.widget.baseWidget.isDemo;
                                    scope.rootWidget.list[i].toolData.extend = angular.toJson(data.widget.toolData.extend);
                                    scope.rootWidget.list[i]._ext.demoData = data.widget.toolData.extend;
                                })
                            }, 0);
                            break;
                        }
                    }
                    break;
            }
           
            //隐藏面板操作界面
            if(type != 'demo') hideWidgetOperation(type)
        };

        //面板操作失败的回调方法(删除、编辑、复制)
        function widgetOperationFailureCallBack(type){
        }

        //demo数据显示
        function showWidgetDemoData(widget, index) {
            scope.modal.editorShow = scope.rootTmpData.editorShow = false;
            if (!widget.toolData) {
                widget.toolData = {};
            }
            scope.modal.editorNow = widget;
            scope.modal.editorNowIndex = index;

            var currentWidgetDom = angular.element('.li-widget')[index];
            var w = parseInt(currentWidgetDom.offsetWidth);
            var x = getRect(currentWidgetDom).left;
            var y = getRect(currentWidgetDom).top;
            if (scope.myOptions.widget.demo) {
                var max_x = document.documentElement.clientWidth - 30;
                var default_x = 0;

                if (x + w + parseInt(scope.modal.editorElemnet[0].offsetWidth) > max_x) {
                    default_x = document.documentElement.clientWidth - parseInt(scope.modal.editorElemnet[0].offsetWidth) - 10;
                } else {
                    default_x = x + w + 10;
                }

                scope.modal.editorElemnet.css({
                    'left': default_x + 'px',
                    'top': y + 'px'
                })
            } else {
                scope.modal.editorPlace = { 'w': w, 'x': x, 'y': y };
                scope.modal.editorNowCopy = widget;

                showWidgetOperation('demo', widget);
            }
        };

        // ========== wiget 拖拽插件事件 ==========

        //widget发送改变(拖拽)
        function onChange (event, items) {}

        //widget拖拽开始(拖拽)
        function onDragStart (event, ui) {
            widgetDrag('dragStart', ui);
        }

        //widget拖拽结束(拖拽)
        function onDragStop (event, ui) {
            scope.widgetLayoutUpdate('drag');
            widgetDrag('dragStop', ui);
        }

        //widget缩放开始(拖拽)
        function onResizeStart (event, ui) {
            widgetResizeUpdate('resizeStart', ui);
        }

        //widget缩放结束(拖拽)
        function onResizeStop (event, ui) {
            widgetResizeUpdate('resizeStop', ui);
            scope.widgetLayoutUpdate();
        }

        //widget添加之后(拖拽)
        function onItemAdded (item) {};

        //widget删除之后(拖拽)
        function onItemRemoved (item) {};

        //判断widget点击或是拖拽(拖拽)
        function widgetDrag(type, item){
            var element = angular.element(item.helper[0]);

            if(type == 'dragStart'){
                element.attr('data-drag', 'dragStart');
            }
            else if(type == 'dragStop'){
                $timeout(function(){
                    element.attr('data-drag', 'dragStop');
                }, 200);
            }
        }

        //widget改变大小时特殊对应(拖拽)
        function widgetResizeUpdate(type, item){
            var widget,
                widgetIndex,
                element = angular.element(item.element[0]),
                widgetId = element.attr('data-gs-id');

            for(var i=0; i<scope.rootWidget.list.length; i++){
                if(scope.rootWidget.list[i].baseWidget.widgetId == widgetId){
                    widget = scope.rootWidget.list[i];
                    widgetIndex = i;
                    break;
                }
            }

            if(type == 'resizeStart'){
                element.find('.grid-stack-item-content').addClass('clear').end().find('.chart-heatmap').addClass('chart-drawing');
                // widget.widgetDrawing = "resizeStart";
            }
            else if(type == 'resizeStop'){
                if(widget){

                    //自定义widget的变化，需要自适应
                    if (widget.baseWidget.widgetType == 'custom' && widget.children && widget.children.length > 0) {
                        //用widgetId来更新变化的那一个widget，当widget编辑器处于关闭状态的时候才需要自适应，编辑打开的时候只需要更新父级widget的大小信息
                        scope.$broadcast('changeWidgetLayout', widget.baseWidget.widgetId + (scope.rootTmpData.editorShow ? ',saveSmall' : ',saveBig'));
                    }

                    //map地图时自适应(放在chart-highmaps.dire.js中重绘)
                    // $(window).resize();

                    //更新widget绘制状态
                    if(widget.baseWidget.widgetType == 'chart') {
                        widget.widgetDrawing = widget.widgetDrawing == "drawing" ? "drawing" : 'waiting';
                    } else {
                        //非chart直接修改绘制状态为success
                        widget.widgetDrawing = 'success';
                        element.find('.grid-stack-item-content').removeClass('clear').end().find('.chart-heatmap').removeClass('chart-drawing');
                    }
                }
            }
        }

        //widget位置信息更新(拖拽)
        function widgetLayoutUpdate(){
            if(!scope.pt.settings.isPhone){
                gridstackService.updateLayout(scope.rootWidget.list, scope.rootPanel.nowId, scope.rootSpace.current.spaceId, scope.rootPanel.now.layout, widgetLayoutUpdateAfter);
            }
        }

        //widget位置信息更新后的回调函数(拖拽)
        function widgetLayoutUpdateAfter(dashboardId, layout){
            for (var i = 0; i < scope.rootPanel.list.length; i++) {
                if (scope.rootPanel.list[i].panelId == dashboardId) {
                    scope.rootPanel.list[i].layout = layout;

                    console.log('更新坐标成功');
                    console.log(scope.rootPanel.list[i]);
                    break;
                }
            }
        }
    }
}

export default widgetListDirective;
