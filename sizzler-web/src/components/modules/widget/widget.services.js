'use strict';

import {
    uuid,
    objectIsEmpty,
    getMyDsConfig
} from '../../modules/common/common';

WidgetServices.$inject = ['$translate', 'uiLoadingSrv', 'WidgetResources'];

function WidgetServices($translate, uiLoadingSrv, WidgetResources) {
    this.translate = $translate;
    this.uiLoadingSrv = uiLoadingSrv;
	this.WidgetResources = WidgetResources;
}


WidgetServices.prototype = {
	constructor: WidgetServices,

    /**
     * 新增操作
     */
    add: function(info){
        return this.WidgetResources.add(info)
    },

    /**
     * 删除操作
     */
    delete: function(widgetId){
        return this.WidgetResources.delete(null, {
            widgetId: widgetId
        })
    },

    /**
     * 更新操作
     */
    update: function(widget){
        return this.WidgetResources.update(widget)
    },

    /**
     * 更新别名
     */
    alias: function(widget){
        return this.WidgetResources.alias(widget)
    },


    /**
     * 获取单个widget信息
     */
    getWidgetInfo: function(widgetId){
        return this.WidgetResources.getWidgetInfo({
            widgetId: widgetId
        })
    },

    /**
     * widget创建
     * @param {string=} type: chart, tool, custom
     * @param {string=} graphName: table, text, heatmap
     */
    widgetCreate: function(type, graphName, panelId, spaceId, ptId, mapCode, profileSelected, userSelected){
        let widgetId = uuid();
        let variableId = uuid();
        let ptoneGraphInfoId = 800;
        let currentTime = new Date().getTime();
        let children = type == 'custom' ? [] : null;
        let widgetTitle = this.translate.instant("WIDGET.WIDGET_DEFAULT_NAME");
        let dateKey = userSelected ? userSelected.dateKey : null;
        let ptoneDsInfoId = 0;
        let dsCode = null;
        let accountName = null;
        let profileId = null;
        let connectionId = null;
        let toolData = null;


        if(type == 'custom'){
            ptoneGraphInfoId = 10300;
        }
        else if (type == 'tool'){
            toolData = {
                widgetId: widgetId,
                value: '',
                extend: ''
            };

            if(graphName == 'text'){
                ptoneGraphInfoId = 10100;
            }
            else if(graphName == 'heatmap'){
                ptoneGraphInfoId = 10400;
                widgetTitle = this.translate.instant("WIDGET.WIDGET_HEATMAP_DEFAULT_NAME");
            }
        }

        //在新增操作(create&template),先判是否存在数据账户与时间信息
        if (profileSelected && !objectIsEmpty(profileSelected)) {
            ptoneDsInfoId = profileSelected.dsId;
            dsCode = profileSelected.dsCode;
            accountName = profileSelected.accountName;
            profileId = profileSelected.prfileId;//后端名字写错了，前端也需要写错，不然历史数据不好处理
            connectionId = profileSelected.connectionId;
        }

		//excel/gd/s3数据源不参与时间维态
		if(dsCode){
			let config = getMyDsConfig(dsCode).editor.time;
			if(!config.dataKeyOfInherit){
				dateKey = null;
			}
		}
        //当已存数据源但没有时间时,走默认时间值
        if (dsCode && dateKey == null) {
            let configTime = getMyDsConfig(dsCode);
            if (configTime.editor.time.defaultSelectTime == 0) {
                //除upload,gd,s3外默认选中过去7天不包含今天
                dateKey = 'past7day';
            }
            else if (configTime.editor.time.defaultSelectTime == 1) {
                //upload,gd,s3,salesforce默认选中all time
                dateKey = 'all_time';
            }
        }

        return {
            "panelId": panelId,
            "baseWidget": {
                "widgetId": widgetId,
                "parentId": null,
                "spaceId": spaceId,
                "widgetTitle": widgetTitle,
                "isTitleUpdate": '0',
                "creatorId": ptId,
                "ownerId": ptId,
                "modifierId": ptId,
                "dateKey": dateKey,
                "datePeriod": 'day',
                "refreshInterval": null,
                "createTime": currentTime,
                "modifyTime": currentTime,
                "targetValue": null,
                "byTemplate": 0,    //是否使用模板生成，0-非, 1-是
                "isTemplate": 0,    //是否为模板，0-非, 1-是
                "isExample": 0,     //是否展示demo数据，0：非，1：是
                "status": 1,        // 默认状态为有效，0：非，1：是
                "widgetType": type,
                "ptoneGraphInfoId": ptoneGraphInfoId,
                "graphName": graphName,
                "mapCode": mapCode,
                "showTimePeriod": '0',
                "showMetricAmount": '1' //指标总量的显示（大标题下面的数值）
            },
            "variables": [{
                "variableId": variableId,
                "ptoneDsInfoId": ptoneDsInfoId,
                "dsCode": dsCode,
                "variableGraphId": ptoneGraphInfoId,
                "variableColor": null,
                "connectionId": connectionId,
                "accountName": accountName,
                "profileId": profileId,
                "dimensions": [
                    /*{
                     "id": null,
                     "name": null,
                     "code": null,
                     "i18nCode": null,
                     "sort": null,
                     "max": null,
                     "showOthers": null
                     }*/
                ],
                "ignoreNullDimension": 0, //是否展示没有数据的项，默认不展示，1表示展示，0表示不展示
                "dateDimensionId": "", //
                "metrics": [
                    /*
                     {
                     "id": null,
                     "code": null,
                     "name": null,
                     "i18nCode": null,
                     "showMetricAmount": false,
                     'sort':'normal',
                     'max':'auto',
                     'segment':{
                     "type": "saved", // Saved || new
                     "savedData": [], // saved data
                     "newData": [{
                     "onlyShow": true,
                     "type": "user", //session || user
                     "condition": [
                     {
                     "name":"vt",
                     "op":"include",
                     "value":"1",
                     "rel":"or", //or || and
                     }
                     ]
                     }]
                     }
                     }
                     */
                ],
                "sort": null,
                'segment': null,
                'filters': null
            }],
            "chartSetting": {
                "stackedChart": '0',
                "areaChart": '0',
                "showLegend": '',
                "showDataLabels": '0',
                "showMapName": '0',
                "showMultiY": '0',
                "hideDetail": '0',
                "reverseTarget": '0',
                "metricsToY": {},
                "xAxis": [{ "enabled": true }],
                "yAxis": [{ "enabled": true }, { "enabled": true }]
            },
            "children": children,
            "toolData": toolData,
            "layout": null
        };
    },

    //widget编辑
    widgetEdit: function(widget, index, type) {
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
    },

    /**
     * widget名称命名(序号自增)
     *
     * @type:           名称类型(复制)
     * @originalTile:   原始标题
     * @widgets:        列表
     */
    getName: function(type, originalTile, widgets) {
        var title = originalTile;
        var num = 1;
        var _find = function(list){
            var widgetLength = list.length;
            for (var i = 0; i < widgetLength; i++) {
                if (list[i].baseWidget.widgetTitle == title) {
                    if(type == 'add'){
                        title = originalTile + "(" + (num++) + ")";
                    }
                    else if(type == 'copy'){
                        title = originalTile + " (copy " + (num++) + ")";
                    }
                    _find(widgets);
                    break;
                }
            }
        }

        _find(widgets);
        return title;
    },

    /**
     * 依据ID查找具体面板信息
     */
    getMyPanel: function(panelList, panelId){
        for(var i=0; i<panelList.length; i++){
            if(panelList[i].panelId == panelId){
                return panelList[i];
            }
        }
    },

    /**
     * 根据面板位置信息生成面板列表
     */
    layoutCreatePanels: function(panelLayout){
        var panels = [];
        var _create = function(layout){
            for(var i=0; i<layout.length; i++){
                var currentPanel = layout[i];

                if(currentPanel.type == 'panel' && !currentPanel.shareSourceId){
                    panels.push(currentPanel)
                }
                else {
                    if(Array.isArray(currentPanel.columns) && currentPanel.columns[0].length >0){
                        _create(currentPanel.columns[0])
                    }
                }
            }
        }
        _create(panelLayout);
        return panels;
    },

    /**
     * widget发送数据格式化
     */
    sendDataFormat: function(widget) {
        if (widget.toolData && !angular.isString(widget.toolData.extend)) {
            var extend = widget.toolData.extend;
            widget.toolData.extend = angular.toJson(extend);
        }
        if (widget.variables && widget.variables[0] && widget.variables[0].metricsCode && !angular.isString(widget.variables[0].metricsCode)) {
            widget.variables[0].metricsCode = angular.toJson(widget.variables[0].metricsCode);
        }
        if (widget.variables && widget.variables[0] && widget.variables[0].dimensionsCode && !angular.isString(widget.variables[0].dimensionsCode)) {
            widget.variables[0].dimensionsCode = angular.toJson(widget.variables[0].dimensionsCode);
        }
        delete widget.col;
        delete widget.row;
        delete widget.sizeX;
        delete widget.sizeY;
        delete widget.minSizeX;
        delete widget.minSizeY;
        delete widget.autoPos;
        delete widget.baseWidget.metricsJson;
        delete widget.baseWidget.dimensionsJson;
        delete widget.widgetDrawing;
        delete widget._ext;

        return widget;
    },

    /**
     * 显示弹出框Loading
     */
    showPopupLoading: function(){
        this.uiLoadingSrv.createLoading('.pt-popup-content');
    },

    /**
     * 隐藏弹出框Loading
     */
    hidePopupLoading: function(){
        this.uiLoadingSrv.removeLoading('.pt-popup-content');
    }

};

export default WidgetServices;
