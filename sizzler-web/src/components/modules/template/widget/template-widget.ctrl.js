'use strict';

/**
 * widgetTemplatesCtrl
 *
 */
import {
	uuid
} from 'components/modules/common/common';

angular
    .module('pt')
    .controller('widgetTemplatesCtrl', ['$scope', '$rootScope', 'dataMutualSrv', '$translate',widgetTemplatesCtrl]);


function widgetTemplatesCtrl($scope, $rootScope, dataMutualSrv, $translate) {
    //基础数据
    var modal = $scope.modal = {
        'templatesList': [], //模板列表
        'tags': [], //Tag选中列表
        'publishWidgetShow' : false
    };

    $scope.page = 1;
    $scope.pageSize = 5;
    $scope.publishStatus = 'all';
    $scope.publishArea = 'all';

    $scope.getWidgetTemplet = function(){
        var code = $scope.publishStatus;
        if($scope.publishStatus == 'all' && $scope.publishArea != 'all' ){
            code = $scope.publishArea;
        }
        var queryModel = {
            'page':$scope.page-1,
            'pageSize':$scope.pageSize,
            'statusCode':code,
            'keywords':$scope.keyword
        };
        //请求模板列表
        dataMutualSrv.post(LINK_TEMPLATE_LIST_ALL,queryModel,'wgtList').then(function(data) {
            if (data.status == 'success') {
                modal.templatesList = data.content;
                $scope.totalItems = data.total;
            }
        });
    }

    $scope.getWidgetTemplet();

    $scope.pageChanged = function() {
        console.log('Page changed to: ' + $scope.page);
        $scope.getWidgetTemplet();
    };

    $scope.delete = function(id, title, index, widget) {

        if (confirm("确定删除Widget: " + title + "?")) {

            dataMutualSrv.post(LINK_WIDGET_TEMPLET_DELETE + id).then(function(data) {
                if (data.status == 'success') {
                    modal.templatesList.splice(modal.templatesList.indexOf(widget), 1);
                } else if (data.status == 'failed') {
                    console.log('删除Widget数据后台插入失败!')
                } else if (data.status == 'error') {
                    console.log(data.message)
                }
            });

        } else  {
            console.log('取消删除Widget')
        }

    };

    $scope.copy = function(id, title) {
        if (confirm($translate.instant('TEMPLET.CONFIRM_COPY_WIDGET_TEMPLET') + ":" + title + "？")) {
            copy(id);
        } else {
            console.log('取消复制Widget Templet')
        }
    };

    function copy(widgetId) {
        //当前widget数据获取
        var widgetList = modal.templatesList;
        var widget = {};
        var widgetIndex = null;
        for (var i = 0; i < widgetList.length; i++) {
            if (widgetList[i].baseWidget.widgetId == widgetId) {

                //复制对象
                widget = angular.copy(widgetList[i]);
                widgetIndex = i;
                break;
            }
        };
        //var title = widget.baseWidget.widgetTitle + " (copy)";
        /*var t = 1;
        function getName(tl) {
            for (var i = 0; i < $scope.rootWidget.list.length; i++) {
                if ($scope.rootWidget.list[i].baseWidget.widgetTitle == tl) {
                    title = widget.baseWidget.widgetTitle + " (copy " + (t++) + ")"
                    getName(title);
                    break;
                }
            }
        };
        getName(title)*/
        widget.baseWidget.widgetId = uuid();
        widget.baseWidget.spaceId = $scope.rootSpace.current.spaceId;
        widget.baseWidget.createTime = parseInt(new Date().getTime());
        widget.baseWidget.modifyTime = parseInt(new Date().getTime());
        for (var i = 0; i < widget.variables.length; i++) {
            var id = uuid()
            widget.variables[i].variableId = id;
        };

        var sizeX = widget.sizeX;
        var sizeY = widget.sizeY;

        delete widget.col;
        delete widget.row;
        delete widget.sizeX;
        delete widget.sizeY;
        delete widget.minSizeX;
        delete widget.minSizeY;
        delete widget.baseWidget.metricsJson;
        delete widget.baseWidget.dimensionsJson;
        delete widget.widgetDrawing;
        delete widget._ext;

        widget.baseWidget.description = angular.toJson(widget.baseWidget.description);
        widget.baseWidget.widgetTitle = angular.toJson(widget.baseWidget.widgetTitle);

        //获取Profile List
        dataMutualSrv.post(LINK_WIDGET_ADD, widget).then(function(data) {
            if (data.status == 'success') {
                // widget['availableDatePeriod'] = data.content.availableDatePeriod;
                // widget['dateValue'] = dateValue;
                widget['sizeX'] = sizeX;
                widget['sizeY'] = sizeY;
                widget.baseWidget.description = angular.fromJson(widget.baseWidget.description);
                widget.baseWidget.widgetTitle = angular.fromJson(widget.baseWidget.widgetTitle);
                modal.templatesList.push(widget);
                //定位用
                $scope.rootWidget.locateId = widget.baseWidget.widgetId;
            } else if (data.status == 'failed') {
                console.log('Post Data Failed!')
            } else if (data.status == 'error') {
                console.log('Post Data Error: ')
                console.log(data.message)
            }
        });
    };
    $scope.adminAddWidget = false;
    $scope.add = function(){
        $scope.wgtTempletCreate();
    }

    /*************************
     * 添加widget模板     *
     *************************/
    $scope.wgtTempletCreate = function(){
        $scope.adminAddWidget = true;
        var widgetId = uuid();
        var variableId = uuid();
        var sendWidget = {
            "panelId": $scope.rootPanel.nowId,
            "baseWidget": {
                "widgetId": widgetId,
                "spaceId": $scope.rootSpace.current.spaceId,
                "widgetTitle": {'zh_CN':'Widget Title','en_US':'Widget Title','ja_JP':'Widget Title'},
                "isTitleUpdate": '0',
                "creatorId": $rootScope.userInfo.ptId,
                "ownerId": $rootScope.userInfo.ptId,
                "modifierId": $rootScope.userInfo.ptId,
                "dateKey": $scope.rootUser.userSelected === null ? 'last_week' : $scope.rootUser.userSelected.dateKey,
                "datePeriod": 'day',
                "refreshInterval": null,
                "createTime": parseInt(new Date().getTime()),
                "modifyTime": parseInt(new Date().getTime()),
                "targetValue": null,
                "byTemplate": 0, //0-新增。1-使用模板生成
                "isTemplate": 1, //0：非，1：是
                "isExample": 1, // 是否展示demo数据，0：非，1：是
                "status": 1, // 默认状态为有效
                "description":null,
                "widgetType": "chart",
                "ptoneGraphInfoId": 100,
                "graphName": 'line'
            },
            "variables": [
                {
                    "variableId": variableId,
                    "ptoneDsInfoId": 1,
                    "variableGraphId": 100,
                    "variableColor": null,
                    "accountName": null,
                    "profileId": null,
                    "dimensions": [
                        // {
                        //      "id": null,
                        //      "name": null,
                        //      "code": null,
                        //      "sort":'normal',
                        //      "max":'auto',
                        // }
                    ],
                    "metrics": [
                        /*
                        {
                            "id": null,
                            "code": null,
                            "name": null,
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
                    ]
                }
            ],
            "chartSetting": {
                "stackedChart": 0,
                "areaChart": 0,
                "showLegend": 0,
                "showDataLabels": 0,
                "showMapName":'0',
                "showMultiY": 0,
                "metricsToY": {},
                "xAxis": [],
                "yAxis": []
            },
            "_ext": {} // 扩展字段，用于前端临时数据存储，不持久化到库中
        };


        //在新增操作(create&template),先判是否存在数据账户信息
        if($scope.rootUser.profileSelected) {
            var dsId = $scope.rootUser.profileSelected.dsId;
            var accountName = $scope.rootUser.profileSelected.accountName;
            var profileId = $scope.rootUser.profileSelected.prfileId;

            sendWidget.variables[0].accountName = accountName;
            sendWidget.variables[0].profileId = profileId;
        };

        //保存
        dataMutualSrv.post(LINK_WIDGET_ADD, angular.copy(sendWidget),'wgtSave').then(function(data){
            if (data.status == 'success') {
                $scope.adminAddWidget = false;
                var sizeX = 5;
                var minx = 5;
                var sizeY = 6;
                var miny = 6;
                sendWidget.sizeX = sizeX;
                sendWidget.sizeY = sizeY;
                sendWidget.minSizeX = minx;
                sendWidget.minSizeY = miny;

                //widget定位
                $scope.rootWidget.locateId = sendWidget.baseWidget.widgetId;

                //前台新增widget
                modal.templatesList.push(sendWidget);

                //打开编辑器
                $scope.edit(sendWidget, modal.templatesList.length-1, 'create');
            } else if (data.status == 'failed') {
                console.log('Post Data Failed!')
            } else if (data.status == 'error') {
                console.log('Post Data Error: ')
                console.log(data.message)
            }
        });

    }

    //模板编辑
    /*$scope.edit = function(id) {
        modal.modalShow = true;
        modal.modalType = 'tpl-e';
        modal.modalTitle = 'Template 编辑';
        modal.modalEditId = id;
    };*/

    /**********************
     * 模板编辑        *
     **********************/
    $scope.edit = function(widget, index, type){
        if(type == 'create'){
            //等待widget初始化完成再计算位置信息
            $scope.$on('ngRepeatFinished', function (ngRepeatFinishedEvent) {
                if(type){showEditor();}

                //此监听事件会一直在，所以需要做判断。针对模板添加时不需要弹出编辑框
                type = null;
            });
        } else {
            showEditor();
        }
        //计算位置信息及存储当前widget信息
        function showEditor(){
            console.log(widget)
            var w = parseInt(angular.element('.li-widget')[index].offsetWidth);
            var x = getRect(angular.element('.li-widget')[index]).left;
            var y = getRect(angular.element('.li-widget')[index]).top;

            if(modal.editorShow){
                var max_x = document.documentElement.clientWidth - 30;
                var default_x = 0;

                if( x + w + parseInt(modal.editorElemnet[0].offsetWidth) > max_x ){
                    default_x = document.documentElement.clientWidth - parseInt(modal.editorElemnet[0].offsetWidth) - 10;
                } else {
                    default_x = x + w + 10;
                }


                modal.editorElemnet.css({
                    'left': default_x+'px',
                    'top': y+'px'
                })
            } else {
                modal.editorShow = true;
                modal.editorPlace = {'w':w, 'x':x, 'y':y};
            }

            modal.editorNow = widget;
            modal.editorNowIndex = index;


        }
    };//edit

    // 模板发布/撤销模板发布（isPublished： 1：未发布，2：发布）
    $scope.publishWidgetTemplet = function(templetId, title, isPublished) {
        $scope.modal.publishWidgetShow = true;

        if (isPublished == 1) {
            $scope.modal.tips = $translate.instant('COMMON.PUBLISH');
        } else {
            $scope.modal.tips = $translate.instant('COMMON.CANCEL') + " " + $translate.instant('COMMON.PUBLISH');
        }
        $scope.modal.templetId = templetId;
        $scope.modal.title = title;
        $scope.modal.isPublished = isPublished;
    };

    $scope.switchStatus = function(status) {
        $scope.selectStatus = status;
    };

}
