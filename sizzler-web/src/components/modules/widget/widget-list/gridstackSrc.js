(function(){
    'use strict';

    angular
        .module('pt')
        .service('gridstackService', ['PanelResources', gridstackService]);

    function gridstackService(PanelResources){
        var grid = null;

        return {

            /**
             * 拖拽插件初始化
             */
            initLayout: function(gridstackHandler){
                grid = gridstackHandler;
            },

            /**
             * 更新widget位置信息
             *
             * @param widgetList {array}: widget list
             * @param dashboardId {string}: dashboard id
             * @param spaceId {string}: space id
             * @param oldLayout {json}: 当前dashboard下现在的位置信息
             * @param backFunc {function}: 位置更新后的回调函数
             */
            updateLayout: function(widgetList, dashboardId, spaceId, oldLayout, backFunc){

                //定时器,500MS内发送一次
                var sendMsgTimer = null;
                (function(){
                    clearTimeout(sendMsgTimer);
                    sendMsgTimer = window.setTimeout(function() {
                        getMsg();
                    }, 500);
                }());


                //获取参数并发送
                function getMsg() {
                    if (widgetList.length == 0) return;

                    var layout = [];
                    for (var i = 0; i < widgetList.length; i++) {
                        var pos = {
                            id: widgetList[i].baseWidget.widgetId,
                            c: widgetList[i].col,
                            r: widgetList[i].row,
                            x: widgetList[i].sizeX,
                            y: widgetList[i].sizeY,
                            minx: widgetList[i].minWidth,
                            miny: widgetList[i].minHeight
                        };
                        layout.push(pos);
                    }
                    layout = encodeURIComponent(angular.toJson(layout, true));

                    if(layout !== oldLayout){
                        //发送坐标信息
                        sendLayout(layout);
                    }
                }

                //发送坐标信息
                function sendLayout(layoutJson) {
                    var panelInfo = {
                        type: 'panel',
                        panelId: dashboardId,
                        layout: layoutJson,
                        spaceId: spaceId,
                        notUpdatePanelLayout: true
                    };

                    PanelResources.updatePanel(panelInfo, {
                        panelId: panelInfo.panelId
                    })
                    .then((data) => {
                        backFunc(dashboardId, layoutJson);
                    })
                }
            },

            /**
             * 禁用拖拽及缩放
             */
            disableLayout: function(type){
                if(grid){
                    if(type){
                        grid.opts[type] = true;
                        type == 'disableDrag' ? grid.movable('.grid-stack-item', false) : grid.resizable('.grid-stack-item', false);
                    } else {
                        grid.opts.disableDrag = true;
                        grid.opts.disableResize = true;
                        grid.movable('.grid-stack-item', false);
                        grid.resizable('.grid-stack-item', false);
                    }
                }
            },

            /**
             * 启用拖拽及缩放
             */
            enableLayout: function(type){
                if(grid) {
                    if(type){
                        grid.opts[type] = false;
                        type == 'disableDrag' ? grid.movable('.grid-stack-item', true) : grid.resizable('.grid-stack-item', true);
                    } else {
                        grid.opts.disableDrag = false;
                        grid.opts.disableResize = false;
                        grid.movable('.grid-stack-item', true);
                        grid.resizable('.grid-stack-item', true);
                    }
                }
            },

            /**
             * 设置widget高度(全屏情况下,尺寸变化)
             */
            setWidgetHeight: function(cellHeight){
                if(grid) {
                    grid.cellHeight(cellHeight);
                }
            }


        }
    }
})();