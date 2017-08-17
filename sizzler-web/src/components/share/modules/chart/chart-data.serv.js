
import{
    uuid,
    LINK_WIDGET_DATA,
    LINK_SIGN_OUT,
    LINK_SHARE_SIGNIN,
    getMyDsConfig,
    GetRequest
} from 'components/modules/common/common';

import cookieUtils from 'utils/cookie.utils';

//数据请求工厂服务
angular.module('pt').factory("datasourceFactory", ["$http", "$rootScope", "$translate", "websocket", "dataMutualSrv", "$timeout", function($http, $rootScope, $translate, websocket, dataMutualSrv, $timeout) {

    // 常量
    var WIDGET_DRAWING_STATUS_WAITING = "waiting";
    var WIDGET_DRAWING_STATUS_DRAWING = "drawing";
    var WIDGET_DRAWING_STATUS_SUCCESS = "success";

    var WIDGET_DEFAULT_REFRESH_INTERVAL = 1*20*60*1000; //默认刷新时间(毫秒)
    var WIDGET_DATA_CACHE_TIME = 1*60*60*1000; //默认缓存时间(毫秒)

    var widgetDataSocket = null; // widget取数push数据用websocket
    var widgetDataSocketSign = 'WidgetData:' + uuid(); // widget取数push数据用websocket sign

    //图表对应数据结构类型
    var highcharts="highcharts";
    var selfDefineChart="ptone";

    $rootScope.chartMaps = [{
        chartType: 'hollowpie',
        dataType: 1,
        creator:highcharts
    }, {
        chartType: 'area',
        dataType: 0,
        creator:highcharts
    }, {
        chartType: 'line',
        dataType: 0,
        creator:highcharts
    }, {
        chartType: 'column',
        dataType: 0,
        creator:highcharts
    }, {
        chartType: 'pie',
        dataType: 1,
        creator:highcharts
    }, {
        chartType: 'bar',
        dataType: 0,
        creator:highcharts
    }, {
        chartType: 'areaspline',
        dataType: 0,
        creator:highcharts
    }, {
        chartType: 'simplenumber',
        dataType: 2,
        creator:selfDefineChart
    }, {
        chartType: 'number',
        dataType: 3,
        creator:selfDefineChart
    }, // number为环比图形，其他图形切换为number都需要重新取数
        {
            chartType: 'progressbar',
            dataType: 2,
            creator:selfDefineChart
        }, {
            chartType: 'circlepercent',
            dataType: 2,
            creator:selfDefineChart
        }, {
            chartType: 'table',
            dataType: 4,
            creator:selfDefineChart
        }];

    var getWidgetDatasource = function(widgetId){
        var datasource;
        angular.forEach($rootScope.datasources, function(ds, index) {
            if (ds.widgetId == widgetId) {
                datasource = ds;
            }
        });
        return datasource;
    };

    var widgetDataHandler = function(data){
        var datasource = getWidgetDatasource(data.content.widgetId);
        if(!datasource || datasource && datasource.dataVersion != data.dataVersion){
            return;
        }

        // 获取配置信息
        var dsConfig = getMyDsConfig(datasource.widget.variables[0].dsCode);

        if (data.status != 'success') {
            // datasource.setFlag(-1); // 标记不再取数据更新
            // datasource.tryNumber = 0;
            datasource.setIsCacheData(false); // 设置数据不缓存
            datasource.setData(null);
            if (datasource.getErrorLoadingEvent()) {
                datasource.getErrorLoadingEvent()();
            }

            console.log('>>> tryNumber::'+ datasource.tryNumber);
            console.log('############## GetGaDataFailed::##################');
            console.log('widgetId:: ' +  datasource.widgetId + ' , title:: ' + datasource.widget.baseWidget.widgetTitle);
            console.log('Exception::' + data.message);
            console.log('##################################################');
        } else {
            var widgetData = data.content;
            if(widgetData.status == 'noData' ){
                // datasource.setFlag(0);
                // datasource.tryNumber = 0;
                datasource.setIsCacheData(false); // 设置数据不缓存
                datasource.setData(null);
                //datasource.errorMsg = = '无可显示的数据';
                datasource.errorMsg = $translate.instant('CHART.MSG.' + widgetData.errorCode);
                if (datasource.getErrorLoadingEvent()) {
                    datasource.getErrorLoadingEvent()();
                }
            }else if(widgetData.status == 'failed'){
                // datasource.setFlag(-1); // 标记不再取数据更新（不会再自动刷新）
                // datasource.tryNumber = 0;
                datasource.setIsCacheData(false); // 设置数据不缓存
                datasource.setData(null);
                if(widgetData.errorCode && widgetData.errorCode != 'MSG_FAILED'){
                    datasource.errorMsg = $translate.instant('CHART.MSG.' + widgetData.errorCode);
                }else{
                    datasource.errorMsg = $translate.instant('CHART.MSG.MSG_FAILED1')
                        + '<a href="javascript:" class="text-warning refresh-widget" data-widget-id="' + datasource.widgetId +'">'
                        + $translate.instant('CHART.MSG.MSG_FAILED2') + '</a>'
                        + $translate.instant('CHART.MSG.MSG_FAILED3'); // 默认失败提示
                }
                console.log('############## GetGaDataFailed::##################');
                console.log('widgetId:: ' +  datasource.widgetId + ' , title:: ' + datasource.widget.baseWidget.widgetTitle);
                console.log('Exception::' + widgetData.errorMsg);
                console.log(widgetData.errorLogs);
                console.log('##################################################');
                if (datasource.getErrorLoadingEvent()) {
                    datasource.getErrorLoadingEvent()();
                }
            }else{ // success
                var returnData = datasource.getTodoData(data.content);

                // 判断数据是否需要缓存、并设置缓存时间
                // 如果不包含今天的数据数据不自动刷新（MM/dd）
                var today = new Date().format('MM/dd')
                if(dsConfig.chart.cacheWidgetHistoryData && returnData.dateRange && returnData.dateRange[1] != today){
                    datasource.interval = -1;
                    datasource.setIsCacheData(true); // 设置数据缓存
                    //datasource.setCacheExpiredTime(new Date(new Date().getTime() + WIDGET_DATA_CACHE_TIME)); // 设置数据缓存过期时间
                }

                datasource.setData(returnData);
                //没有demo数据时，第一次初始化
                datasource.widget._ext.demoData = returnData;
                // datasource.setFlag(0);
                // datasource.tryNumber = 0;
                if (datasource.getAfrerLoadingEvent()) {
                    datasource.getAfrerLoadingEvent()();
                }
            }
        }
    }

    // 初始化widgetData-websocket, 链接socket;
    /*
    var initWidgetDataSocket = function(){

        //监听widget数据的socket返回值
        $rootScope.socketData.func.ws.onmessage = function(event) {
            var data = angular.fromJson(event.data);
            $rootScope.$apply(function(){
                widgetDataHandler(data);
            });
        };
    };
    */
    //如果数据请求队列没有初始化就初始化
    if (!$rootScope.datasources) {
        $rootScope.datasources = new Array();
        //数据请求处理
        var datasourceTimer = setInterval(function() {
            var rootScopeSid = $rootScope.sid ? $rootScope.sid : localStorage.getItem('sid');
            //循环数据请求任务
            var autoRefreshData = false // share不自动更新数据
            for (var i = 0; rootScopeSid && i < $rootScope.datasources.length; i++) {
                var interval = $rootScope.datasources[i].interval;
                //判断是否pull类型，并且widgetId都不为空
                if ($rootScope.datasources[i] && $rootScope.datasources[i].type == "pull" && $rootScope.datasources[i].widgetId) {

                    //判断是否是第一次请求，并且不是错误任务
                    if ($rootScope.datasources[i].runNumber == 0 && $rootScope.datasources[i].getFlag() === 0 && $rootScope.datasources[i].isReadyForLoad()) {
                        //调用请求函数
                        $rootScope.datasources[i].load();
                        //记录请求时间
                        $rootScope.datasources[i].loadTime = new Date();
                        //发起请求后请求计数+1；
                        $rootScope.datasources[i].runNumber++;
                        //判断是否是二次请求，并且请求事件间隔有设置，并且不是错误任务或者正在请求任务
                    } else if (autoRefreshData && interval> 0 && $rootScope.datasources[i].runNumber > 0 && $rootScope.datasources[i].getFlag() === 0) {
                        var rundate = new Date()
                        //判断设置的自动请求时间是否到了
                        if ($rootScope.datasources[i].loadTime.getTime() + interval < rundate.getTime()) {
                            // 如果数据需要缓存并且缓存期未过期则不请求数据
                            if(!$rootScope.datasources[i].isCacheData || ($rootScope.datasources[i].cacheExpiredTime && $rootScope.datasources[i].cacheExpiredTime.getTime() < rundate.getTime())){
                                //调用请求函数
                                $rootScope.datasources[i].load();
                                //记录请求时间
                                $rootScope.datasources[i].loadTime = rundate;
                                //发起请求后请求计数+1；
                                $rootScope.datasources[i].runNumber++;
                            }
                        }
                    }
                    //push 不做处理
                } else if ($rootScope.datasources[i] && $rootScope.datasources[i].type == "push") {

                } else {
                    //数据请求任务不符合要去，直接删除
                    $rootScope.datasources.splice(i, 1);
                }
            }
        }, 200);
    }

    //数据请求任务类
    function Datasources(widget, widgetId, panelInfo, type, interval, chartTypes, pushData, beforeLoadingEvent, afrerLoadingEvent, errorLoadingEvent) {
        this.baseUrl = LINK_WIDGET_DATA;
        this.dataVersion = null; // 请求数据的版本号（每次发送请求时更新）

        //请求间隔默认为5分钟
        this.interval = interval || WIDGET_DEFAULT_REFRESH_INTERVAL;
        //请求类型默认为pull
        this.type = type || 'pull';
        //请求计数
        this.runNumber = 0;
        // 失败尝试次数
        this.tryNumber = 0;
        //数据错误
        this.flags = 0;
        this.chartType = chartTypes;
        this.parameters = new Array();
        this.chartData = null;
        this.pushData = pushData || null;
        this.widgetId = widgetId || null;
        this.widget = widget || null;
        this.panelInfo = panelInfo || null;
        this.afrerLoadingEvent = afrerLoadingEvent || null;
        this.beforeLoadingEvent = beforeLoadingEvent || null;
        this.errorLoadingEvent = errorLoadingEvent || null;
        this.redrawWidgetFunc = null;
        this.loadTime = null;
        this.message = null;
        this.errorMsg = $translate.instant('CHART.MSG.MSG_FAILED1')+
            '<a href="javascript:" class="text-warning refresh-widget" data-widget-id="'+this.widgetId+'">'+$translate.instant('CHART.MSG.MSG_FAILED2')+ '</a>'+
            $translate.instant('CHART.MSG.MSG_FAILED3');
        this.detailMsg = null;
        this.isCacheData = false;
        this.cacheExpiredTime = null;
    }
    Datasources.prototype = {
        load: function() {
            this.setFlag(1);
            var datasource = this;
            datasource.setData(null);
            datasource.buildDataVersion();
            datasource.setParameter('dataVersion', datasource.dataVersion);
            datasource.setParameter('requestSource', 'datadeck-share');
            //datasource.setParameter('sign',widgetDataSocketSign); // websocket sign

            //等待socket建立成功
            /*
            var judgeData = setInterval(function() {
                if($rootScope.socketData.func !== null) {
                    clearInterval(judgeData);

                    datasource.setParameter('sign', $rootScope.socketData.id);
                    initWidgetDataSocket();
                    querData();
                }
            }, 100);
            */
            var $this = this;
            queryData();
            function queryData(){
                if ($this.getBeforeLoadingEvent()) {
                    $this.getBeforeLoadingEvent()();
                }

                datasource.widget.widgetDrawing = WIDGET_DRAWING_STATUS_DRAWING;

                console.log(">>>>> " + new Date().toLocaleString() + " :::  load data >>> widgetId:: " +  $this.widgetId + ' , title:: ' + $this.widget.baseWidget.widgetTitle );

                // 如果在编辑器中初始化图表配置参数，不是图表编辑器时，从数据库中读取图表配置
                var widgetInfoStr = '{}';

                datasource.setParameter('isTemplet', $this.widget.baseWidget.isTemplate == 1);// 是否为widget模板
                datasource.setParameter('isExample', $this.isShowExampleData()); // 是否展示demo数据
                datasource.setParameter('templetId', $this.widget.baseWidget.templetId);

                var widgetInfo = angular.copy($this.widget);

                // 删除多余的属性值
                delete widgetInfo._ext;
                delete widgetInfo.widgetDrawing;
                delete widgetInfo.dateValue;
                delete widgetInfo.sizeX;
                delete widgetInfo.sizeY;
                delete widgetInfo.minSizeX;
                delete widgetInfo.minSizeY;
                delete widgetInfo.row;
                delete widgetInfo.col;
                delete widgetInfo.baseWidget.dimensionsJson;
                delete widgetInfo.baseWidget.metricsJson;
                delete widgetInfo.baseWidget.description;
                delete widgetInfo.baseWidget.ptoneWidgetInfoId;
                delete widgetInfo.baseWidget.widgetTitle;
                delete widgetInfo.baseWidget.isPublished; // widget模板中标记模板是否发布

                var dsConfig = getMyDsConfig(widgetInfo.variables[0].dsCode);

                angular.forEach(widgetInfo.variables, function(variable){

                    //修正指标显示名称(别名)
                    angular.forEach(variable.metrics, function(m){
                        if(m.alias) {
                            m.name = m.alias;
                        }
                        else if(dsConfig.editor.data.translateTitle && m.i18nCode){
                            //修正指标国际化

                            var name = $translate.instant(m.i18nCode);
                            if(dsConfig.editor.data.metricsHasCount){
                                m.name  = m.calculateType + '(' + name + ')';
                            }else{
                                m.name = name;
                            }
                        }
                    });

                    //修正维度显示名称(别名)
                    angular.forEach(variable.dimensions, function(d){
                        if(d.alias){
                            d.name = d.alias;
                        }
                        else if(dsConfig.editor.data.translateTitle && d.i18nCode){
                            //修正维度国际化

                            d.name = $translate.instant(d.i18nCode);
                        }
                    });
                });

                // 增加panel全局设置
                var panelInfo = datasource.panelInfo;
                if(panelInfo && panelInfo.panelId == widgetInfo.panelId
                    && panelInfo.components){

                    // 全局时间设置
                    var globalTimeValue = null;
                    var globalTimeObj = panelInfo.components.GLOBAL_TIME;
                    if(globalTimeObj && globalTimeObj.status == 1){
                        globalTimeValue = globalTimeObj.value;
                    }

                    // 全局设置替换widget设置
                    if(globalTimeValue && globalTimeValue != 'widgetTime'){
                        widgetInfo.baseWidget.dateKey = globalTimeValue;
                    }
                }

                widgetInfoStr = JSON.stringify(widgetInfo);
                //console.log('>>> widgetInfo:: ');console.log(widgetInfo);
                //console.log(widgetInfoStr);
                //widgetInfoStr ='{"panelId":"831138e8-6a2f-42a7-902f-3f53a34228df","baseWidget":{"ptoneGraphInfoId":100,"graphName":"line","widgetId":"c9c8a871-bb25-4dbb-9f54-b438db61f360","widgetTitle":"testGa","dateKey":"last7day","datePeriod":"day","creatorId":"4","ownerId":"4","modifierId":"4","status":"1","createTime":1437037405327,"modifyTime":1437037405327,"refreshInterval":10,"targetValue":"120","byTemplate":"0"},"dateValue":null,"availableDatePeriod":null,"variables":[{"variableId":"9366eb3d-5ed6-4720-89a4-eaf981b117db","ptoneDsInfoId":1,"variableGraphId":100,"variableColor":null,"accountName":"peterangel536@gmail.com","profileId":"69398218","metrics":[1,2],"dimensions":[]}]}';
                if(!datasource.widget._ext){
                    datasource.widget._ext = {};
                }
                //如果是demo 数据，则从toolData中取数据
                if(datasource.widget.baseWidget.isDemo == 1){
                    console.log("get data from demo.")
                    var returnData = angular.fromJson(datasource.widget.toolData.extend);
                    datasource.setData(returnData);
                    //没有demo数据时，第一次初始化
                    datasource.widget._ext.demoData = returnData;
                    datasource.setFlag(0);
                    datasource.tryNumber = 0;
                    if (datasource.getAfrerLoadingEvent()) {
                        datasource.getAfrerLoadingEvent()();
                    }
                }else{
                    $http({
                        method: 'POST',
                        url: $this.getBaseUrl() + $this.widgetId + '?a=1&shareUrl=true&accessToken=' + $rootScope.accessToken +$this.buildParametersStr() + "&sid=" + cookieUtils.get('sid') + "&uiVersion=" + BASE_VERSION,
                        data: widgetInfoStr,
                        withCredentials: true
                    }).success(function(data) {
                        queryDataBack();
                        widgetDataHandler(data);
                    }).error(function(data, status, headers, config) {
                        if (data && data.toString() == "goLogout") {
                            $(location).attr('href', LINK_SIGN_OUT);
                            console.log('session timeout ... ');
                            return;
                        }

                        // 尝试五次失败不再重试
                        if(datasource.tryNumber < 5){
                            datasource.setFlag(0);
                            datasource.tryNumber++;
                            setTimeout(function(){
                                datasource.reload();
                            }, datasource.tryNumber * 2 * 1000);
                            console.log('tryNumber::'+ datasource.tryNumber);
                        } else {
                            queryDataBack();
                            datasource.setData(null);
                            if (datasource.getErrorLoadingEvent()) {
                                datasource.getErrorLoadingEvent()();
                            }
                        }
                    });
                }
            }

            function queryDataBack(){
                datasource.setFlag(0); // 标记取数请求完成, 0：取数完成，1：取数中，-1：不取数
                datasource.tryNumber = 0;
            }

        },

        getIsCacheData : function() {
            return this.isCacheData
        },
        setIsCacheData : function(isCacheData) {
            this.isCacheData = isCacheData;
        },

        getCacheExpiredTime : function() {
            return this.cacheExpiredTime
        },
        setCacheExpiredTime : function(cacheExpiredTime) {
            this.cacheExpiredTime = cacheExpiredTime;
        },

        buildDataVersion: function() {
            this.dataVersion = uuid();
        },

        getCurrent: function(widgetId) {
            var currentDatasource = null;
            angular.forEach($rootScope.datasources, function(datasource, index, datasources) {
                if (datasource.widgetId == widgetId) {
                    currentDatasource = datasource;
                }
            });
            return currentDatasource;
        },

        getTodoData: function(data) {
            var thisDatasource = this;
            var result = {
                widgetId: data.widgetId,
                minValue: data.minValue,
                maxValue: data.maxValue,
                datePeriod: data.datePeriod,
                dateRange: '',
                series: [],
                categories: [],
                yAxis:[],
                goals: data.goals,
                dimensions: '',
                metrics: '',
                metricsAmountsMap: data.metricsAmountsMap,
                useDatetimeAxis: (data.data && data.data[0] && data.data[0].useDatetimeAxis),
                widgetExtInfo: data.extInfo
            };

            // 目前只支持单维度的table
            if(data.data && data.data[0] && data.data[0].graphType.toLowerCase() == 'table') {
                var series = { data: [] };
                var rows = data.data[0].rows || [];
                var columns = data.data[0].rows[0] || [];
                var columnsCode = [];
                var columnsDataType = [];//用于给tableColumnsCode一一对应上类型
                var srcColumnLength = columns.length;
                var dimensions = data.data[0].dimensions || '';
                var dimensionsKey = data.data[0].dimensionsKey || '';
                var dimensionsKeyList = dimensionsKey.split(',');

                var metricsCode = data.data[0].metricsCode || '';
                var firstMetricsIndex = 0;
                if(dimensions){
                    firstMetricsIndex = dimensions.split(',').length;
                }
                var metricsKey = data.data[0].metricsKey || '';
                var metricsKeyList = metricsKey.split(',');

                var fixSortColumnIndex = {};
                var fixSortColumnCount = 0;

                angular.forEach(rows, function(row, rowIndex) {
                    angular.forEach(row , function(cell, cellIndex) {
                        var mKey = metricsKeyList[cellIndex - firstMetricsIndex];
                        var unit = data.data[0].unitMap[mKey];
                        var dateType = data.data[0].dataTypeMap[mKey];
                        var dataFormat = data.data[0].dataFormatMap[mKey];
                        if(typeof(dateType) != "undefined" && dateType != null && dateType != "" && dateType.length != 0 && rowIndex == 0){
                            if(typeof(cell) != "undefined" && cell != null && cell != ""){
                                //将cell进行转码
                                columnsDataType[encodeURI(cell)] = dateType;
                            }
                        }
                        if(rowIndex > 0 && cellIndex >= firstMetricsIndex){
                            // 格式化数据显示
                            rows[rowIndex][cellIndex] = thisDatasource.formatDataViewer(cell, unit, dateType, dataFormat, true); // 数据不三位分隔处理

                            // 修正时间格式排序列,只需判断一次即可
                            if(unit == 's'){
                                rows[rowIndex][fixSortColumnIndex[cellIndex]] = cell;
                            }
                        }else{
                            rows[rowIndex][cellIndex] = cell;
                            if(rowIndex == 0 && unit == 's'){
                                var fixIndex = srcColumnLength + fixSortColumnCount;
                                fixSortColumnIndex[cellIndex] = fixIndex;
                                rows[0][fixIndex] = rows[0][cellIndex] + "-fix";
                                fixSortColumnCount++;
                            }
                        }
                    });
                });

                /*
                 columnsCode控制后续的tableColumnsCode，由原来的code拼接改为key(code-uuid)拼接
                 */
                if(dimensionsKey){
                    angular.forEach(dimensionsKey.split(','), function(dCode, index) {
                        columnsCode.push(dCode);
                    });
                }

                if(metricsKey){
                    angular.forEach(metricsKey.split(','), function(mCode, index) {
                        columnsCode.push(mCode);
                    });
                }

                series.data = rows;
                result.series.push(series);
                result.dimensions = dimensions;
                result.metrics = data.data[0].metricsName || '';
                result.firstMetricsIndex = firstMetricsIndex;
                result.columns = columns;
                result.columnsCode = columnsCode;
                result.fixSortColumnIndex = fixSortColumnIndex;
                result.srcColumnLength = srcColumnLength;
                //传入数据类型到utile add by you.zou 2016.3.2
                result.columnDataType = columnsDataType;

            } else if(data.data && data.data[0] && data.data[0].graphType.toLowerCase() == 'map') {
                var series = { data: [] };
                var columns = [];
                var metricsKey = data.data[0].metricsKey || '';
                angular.forEach(data.data[0].rows, function(row, rowIndex) {
                    var code = null;
                    var value = [];
                    var showValue = [];

                    if (rowIndex == 0) {
                        columns = row;
                    } else {
                        angular.forEach(row , function(cell, cellIndex) {
                            if(cellIndex == 0){
                                code = cell;
                            }else{
                                var obj = {};
                                var showObj = {};
                                var name = columns[cellIndex];
                                obj[name] = cell; // 未格式化数据

                                var unit = data.data[0].unitMap[metricsKey];
                                var dateType = data.data[0].dataTypeMap[metricsKey];
                                var dataFormat = data.data[0].dataFormatMap[metricsKey];
                                showObj[name] = thisDatasource.formatDataViewer(cell, unit, dateType, dataFormat); // 格式化数据显示
                                value.push(obj);
                                showValue.push(showObj);
                            }
                        });
                        series.data.push({'code': code, 'value': value, 'showValue': showValue});
                    }
                });
                result.series.push(series);
            } else {

                result.categories = data.categories; // 后台处理categories
                var categoriesMap = {};
                angular.forEach(result.categories, function(category, index){
                    categoriesMap[category] = index;
                });

                angular.forEach(data.data, function(d, index) {

                    // // 获取所有var的数据类型列表,构建yAxis数据类型列表 （not use）
                    // var currVarDataType = d.dataTypeMap[d.metricsKey];
                    // var constainsDataType = false;
                    // angular.forEach(result.yAxis, function(tmpYAxis, index){
                    //     if(tmpYAxis.dataTye == currVarDataType){
                    //         constainsDataType = true;
                    //     }
                    // });
                    // if(!constainsDataType){
                    //     var defaultYAxis = {
                    //         dataType : currVarDataType, // 数据类型
                    //         unit : d.unitMap[d.metricsKey] // 单位
                    //     };
                    //     result.yAxis.push(defaultYAxis);
                    // }


                    // 判断是否开启双轴,设置对应y轴
                    var yAxisIndex = 0;
                    if(thisDatasource.widget.chartSetting && thisDatasource.widget.chartSetting.showMultiY == 1){
                        if (thisDatasource.widget.chartSetting && thisDatasource.widget.chartSetting.metricsToY) {
                            // 没有设置默认指标显示在Y1轴
                            yAxisIndex = thisDatasource.widget.chartSetting.metricsToY[d.metricsKey] || 0;
                        }
                    }

                    // 构建series
                    var series = {
                        id: d.variableId,
                        name: d.variableName,
                        color: d.color,
                        chartType: d.graphType,
                        data: [],
                        stack:  "stack", // d.stack, // 堆图分类(多个指标时只有一个维度，一个指标两个维度，所以只需设置一个分类即可，(如果多指标支持2个维度情况，需要设为metricsName))
                        yAxis: yAxisIndex, // 默认都在Y1轴上
                        metricsName: d.metricsName, // 指标名
                        metricsKey: d.metricsKey,
                        dataType: d.dataTypeMap[d.metricsKey], // 数据类型
                        dataFormat: d.dataFormatMap[d.metricsKey], // 数据格式
                        unit: d.unitMap[d.metricsKey] // 单位
                    };
                    var dataLabels = d.unitMap[d.metricsKey] || null;
                    //var highchartsPointLimit = 200; // highchart默认绘制点数
                    var highchartsPointLimit = 1000; // highchart默认绘制点数
                    angular.forEach(d.rows, function(row, i) {
                        // 前台获取categories（column多指标无维度或有过滤器时，两个series的x不同时会有问题）
                        // if (index === 0) {
                        //     result.categories.push(row[0]);
                        // }
                        if(i<=highchartsPointLimit){ // highcharts默认只显示一千个点，所以需要改一下点数的配置(turboThreshold:5000//set it to a larger threshold, it is by default to 1000)，不过又有一个新问题，highcharts加载的数据超过一千之后会出现效率问题，非常卡。
                            series.data.push({
                                name: row[0],
                                y: row[1],
                                x: categoriesMap[row[0]],
                                contrast: row[2] ? row[2] : null, //对比的目标值
                                dataLabels: row[3] ? row[3] : dataLabels, //单位标签
                                unit: series.unit,
                                dataType: series.dataType,
                                dataFormat: series.dataFormat
                            });
                        }else if(i==highchartsPointLimit+1){
                            try{
                                console.log(">>> widget<"+thisDatasource.widget.baseWidget.widgetTitle+"> only draw "+highchartsPointLimit+" point of " + d.rows.length + " points !");
                            }catch(e){}
                        }
                    });
                    result.series.push(series);
                });

            }

            // 设置时间范围
            if(data.data[0]){
                result.dateRange = data.data[0].dateRange;
            }

            return result;
        },

        /* 判断GA widget是否展示demo数据
         */
        isShowExampleData :  function() {
            return this.widget.baseWidget.isExample == 1;
        },

        /** 判断 widget取数相关所有必须参数是否已经齐全
         *  必须参数：profileId等（有些数据源必须有至少一个指标，有些数据源没有这个限制，drewChartNeedMetrics这个字段是判断是否需要指标的）
         *   (当不需要指标的数据源绘图时，只有table类型才会取数-----前提是没有指标只有维度)
         **/
        isReadyForLoad :  function() {
            var dsConfig = getMyDsConfig(this.widget.variables[0].dsCode);
            if(this.isShowExampleData()){
                return true;
            }else{
                if(dsConfig.editor.data.drewChartComplexMode){//是否为复合模式取数（salesforce数据源，有时单维度可以取数，有时不可以）
                    if(this.widget.variables && this.widget.variables[0] && this.widget.variables[0].accountName && this.widget.variables[0].profileId ){
                        if(('' + this.widget.variables[0].profileId).indexOf('Tabular') > -1 ){
                            return ((this.widget.variables[0].metrics && this.widget.variables[0].metrics.length > 0) || (this.widget.variables[0].dimensions && this.widget.variables[0].dimensions.length > 0)&& this.widget.baseWidget.graphName == 'table');
                        }else if((''+this.widget.variables[0].profileId).indexOf('Matrix') > -1 || (''+this.widget.variables[0].profileId).indexOf('Summary') > -1){
                            return this.widget.variables[0].metrics && this.widget.variables[0].metrics.length > 0;
                        }else{ //object 支持单维度查询
                            return (this.widget.variables[0].metrics && this.widget.variables[0].metrics.length > 0) || ((!this.widget.variables[0].metrics || this.widget.variables[0].metrics.length <= 0) && (this.widget.variables[0].dimensions && this.widget.variables[0].dimensions.length > 0)&& this.widget.baseWidget.graphName == 'table')
                        }
                    }else{
                        return false;
                    }
                }else{
                    return this.widget.variables && this.widget.variables[0]
                        && this.widget.variables[0].accountName
                        && this.widget.variables[0].profileId  // 存在profileId
                        && (dsConfig.editor.data.drewChartNeedMetrics && this.widget.variables[0].metrics && this.widget.variables[0].metrics.length > 0) ||
                        (!dsConfig.editor.data.drewChartNeedMetrics && ((this.widget.variables[0].metrics && this.widget.variables[0].metrics.length > 0) || ((!this.widget.variables[0].metrics || this.widget.variables[0].metrics.length <= 0) && (this.widget.variables[0].dimensions && this.widget.variables[0].dimensions.length > 0)&& this.widget.baseWidget.graphName == 'table')))
                }
            }
        },

        /* 判断两个对象是否相同(TODO：只能判断对象的一级属性值，对于对象中的对象属性此处转为json进行比较，可能存在问题)
         *  keyAttrList：关注属性
         */
        compareObjEqual :  function(newObj, oldObj, keyAttrList) {
            if(newObj === oldObj){
                return true;
            }

            var result = true;
            newObj = newObj || {};
            oldObj = oldObj || {};

            if(keyAttrList && (keyAttrList instanceof Array)){
                angular.forEach(keyAttrList, function(attr){
                    if(result){ // fasle 后就不需要判断了
                        var newVal = newObj[attr];
                        var oldVal = oldObj[attr];
                        if(typeof(newVal) == 'object' && typeof(oldVal) == 'object') {
                            result = (angular.toJson(newVal) == angular.toJson(oldVal));
                        }else{
                            result = (newVal == oldVal);
                        }
                    }
                });

            } else {
                result = (newObj == oldObj);
            }
            return result;
        },

        getChartType : function() {
            return this.chartType;
        },
        setChartType : function(type) {
            if (!this.chartType) {
                this.chartType = type;
            } else {
                var newDataType, oldDataType;
                for (var i = 0; i < $rootScope.chartMaps.length; i++) {
                    if ($rootScope.chartMaps[i].chartType == type) {
                        newDataType = $rootScope.chartMaps[i].dataType;
                    }
                }
                for (var i = 0; i < $rootScope.chartMaps.length; i++) {
                    if ($rootScope.chartMaps[i].chartType == this.chartType) {
                        oldDataType = $rootScope.chartMaps[i].dataType;
                    }
                }
                if (newDataType != oldDataType) {
                    this.chartData = null;
                    this.runNumber = 0;
                    if (this.flags == -1) {
                        this.flags = 0;
                    }
                }
                this.chartType = type;
            }
        },
        reload : function(isUseCache) {
            var runDate = new Date();
            if(!isUseCache || !this.isCacheData || (this.cacheExpiredTime && this.cacheExpiredTime.getTime() < runDate.getTime())) {
                if (this.flags == -1) {
                    this.flags = 0;
                }
                this.runNumber = 0;
                //this.parameters = new Array();
                this.chartData = null;
                this.isCacheData = false;
            }else{
                this.widget.widgetDrawing = WIDGET_DRAWING_STATUS_DRAWING;
                if (this.getBeforeLoadingEvent()) {
                    this.getBeforeLoadingEvent()();
                }

                var thisDatasource = this;
                $timeout(function () {
                    thisDatasource.loadTime = new Date();
                    if (thisDatasource.getAfrerLoadingEvent()) {
                        thisDatasource.getAfrerLoadingEvent()();
                    }
                }, 500);
            }
        },
        setParameter : function(name, value) {
            var flag = true;
            for (var i = 0; i < this.parameters.length; i++) {
                if (this.parameters[i].name == name) {
                    this.parameters[i].value = value;
                    flag = false;
                }
            }
            if (flag) {
                this.parameters.push({
                    name: name,
                    value: value
                });
            }

        },
        removeParameter : function(name) {
            for (i = 0; i < this.parameters.length; i++) {
                if (this.parameters[i].name == name) {
                    this.parameters.splice(i, 1);
                }
            }
        },
        getParameter : function(name) {
            for (i = 0; i < this.parameters.length; i++) {
                if (this.parameters[i].name == name) {
                    return this.parameters[i].value;
                }
            }
            return null;
        },
        getAllParameters : function(){
            return this.parameters;
        },
        buildParametersStr : function(){
            var str = '';
            for (var i = 0; i < this.parameters.length; i++) {
                str += '&'+ this.parameters[i].name + '=' + this.parameters[i].value;
            }
            return str;
        },
        setType : function(type) {
            this.type = type;
        },
        getType : function() {
            return this.type;
        },
        setFlag : function(flag) {
            this.flags = flag;
        },
        getFlag : function() {
            return this.flags
        },
        setRedrawWidgetFunc : function(redrawWidgetFunc) {
            this.redrawWidgetFunc = redrawWidgetFunc;
        },
        getRedrawWidgetFunc : function() {
            return this.redrawWidgetFunc;
        },
        redrawWidget : function(drawType){ // drawType: reflow, ...
            //if(this.beforeLoadingEvent) this.beforeLoadingEvent();
            this.widget.widgetDrawing = WIDGET_DRAWING_STATUS_DRAWING; // 标记widget绘制中
            if(this.getRedrawWidgetFunc()) this.getRedrawWidgetFunc()(drawType);
            if(this.afrerLoadingEvent) this.afrerLoadingEvent();
            this.afterWidgetDrawEvent();
        },
        afterWidgetDrawEvent : function() {
            this.widget.widgetDrawing = WIDGET_DRAWING_STATUS_SUCCESS; // 标记widget绘制完成
        },
        setErrorLoadingEvent : function(eventFunction) {
            this.errorLoadingEvent = eventFunction;
        },
        getErrorLoadingEvent : function() {
            return this.errorLoadingEvent;
        },
        setBeforeLoadingEvent : function(beforeFunction) {
            this.beforeLoadingEvent = beforeFunction;
        },
        getBeforeLoadingEvent : function() {
            return this.beforeLoadingEvent;
        },
        setAfrerLoadingEvent : function(afrerFunction) {
            this.afrerLoadingEvent = afrerFunction;
        },
        getAfrerLoadingEvent : function() {
            return this.afrerLoadingEvent;
        },
        setPushData : function(pushFunction) {
            this.pushData = pushFunction;
        },
        setData : function(data) {
            this.chartData = data;
            if (this.pushData) {
                this.pushData();
            }else{
                this.afterWidgetDrawEvent();
            }
        },
        getData : function() {
            return this.chartData;
        },
        getDta : function() {
            return this.data;
        },
        loadTime : function() {
            this.loadTime = new Date();
        },
        getBaseUrl : function() {
            return this.baseUrl;
        },
        /**
         检查当前类型是否在支持范围内
         */
        checkDataType : function(dataType, dataFormat){
            //验证是否是数值类型：CURRENCY、DOUBLE、FLOAT、INTEGER、LONG、NUMBER
            if(dataType == "DOUBLE" ||
                dataType == "FLOAT" || dataType == "INTEGER" ||
                dataType == "LONG" || dataType == "NUMBER"){
                return "number";
            }else if(dataType == "CURRENCY"){
                return "money";
            }
            return null;
        },
        /**
         数字三位分割处理
         @param source		要分割的数字
         */
        splitNumber : function(source){
            //处理成三位数分割
            source = parseFloat(source).toFixed(2); // 保留两位小数
            source = parseFloat(source); // 2.90 >>> 2.9， 去除末尾的0
            source = (source + '').replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,');
            return source;
        },
        // 格式化数据显示
        /**
         数据格式化需要分为两种
         1：table类型，该类型不需要在此方法内处理数据格式
         2：highcharts类型
         */
        formatDataViewer: function(data, unit, dataType, dataFormat, notSplitNumber) {

            var reg = /^(\-|\+)?\d+(\.\d+)?$/; //判断字符串是否为数字(正数、负数、小数) //var reg = /^[0-9]+.?[0-9]*$/;
            if (!reg.test(data)){
                return data; // 如果不是数值直接将data返回，unit抛弃（table中的维度列）
            }
            data = parseFloat(data);
            unit = unit || '';
            var result = ''; // 默认将数值和单位拼接显示
            var numberSign = ""; // 数值正负号
            if(data < 0){
                numberSign = "-";
                data = Math.abs(data); // 转为正数
            }

            if('h' == unit){
                //将小时转换成毫秒
                data = data*60*60*1000;
                unit = 'ms';
            }else if('m' == unit){
                //将分钟转换成毫秒
                data = data*60*1000;
                unit = 'ms';
            }else if('s' == unit){
                data = data*1000;
                unit = 'ms';
            }

            // 如果单位是秒(s),对时间进行格式化显示为: 1h2m3s4ms
            if('ms' == unit){
                var ms = 1;
                var s = 1000;
                var m = 1000 * 60;
                var h = 1000 * 60 * 60;
                var d = 1000 * 60 * 60 * 24;

                // 修正数值位数
                if(data != 0){
                    data = parseFloat(data).toFixed(2); // 保留两位小数
                    data = parseFloat(data); // 去除末尾的0
                }

                if(data >= d){
                    result += (data - data%d)/d + 'd'; // 取整
                    data = data%d; // 求余
                }
                if(data >= h){
                    result += (data - data%h)/h + 'h'; // 取整
                    data = data%h; // 求余
                }
                if(data >= m){
                    result += (data-data%m)/m + 'm';
                    data = data%m;
                }
                if(data >= s){
                    result += (data-data%s)/s + 's';
                    data = data%s;
                }
            }

            // 修正数值位数
            if(data != 0){
                data = parseFloat(data).toFixed(2); // 保留两位小数
                data = parseFloat(data); // 去除末尾的0
            }

            if(!notSplitNumber && unit != "%"){
                data = this.splitNumber(data);
            }

            if(!result || (result && data != 0) ){
                if( (dataType == 'CURRENCY' && (dataFormat == '¥##' || dataFormat == '$##'|| dataFormat == '¥###' || dataFormat == '$')) ){
                    result += unit + data;
                }else{
                    if('$' == unit || '¥' == unit){
                        result += unit + data;
                    }else{
                        result += data + unit;
                    }
                }
            }

            return numberSign + result;
        }
    }

    return {
        createDatasource: function(widget, widgetId, panelInfo, type, interval, chartTypes, pushData, beforeLoadingEvent, afrerLoadingEvent, errorLoadingEvent) {
            if (!widgetId) return null;
            var datasource = null;
            var currentDatasource = this.getDatasource(widgetId);
            interval = interval || WIDGET_DEFAULT_REFRESH_INTERVAL;
            if (currentDatasource) {
                datasource = currentDatasource;
                datasource.widget = widget;
                datasource.panelInfo = panelInfo;
                datasource.setType(type);
                datasource.interval = interval > 0 ?  interval: 0;
                datasource.setChartType(chartTypes);
                datasource.setPushData(pushData);
                datasource.setBeforeLoadingEvent(beforeLoadingEvent);
                datasource.setAfrerLoadingEvent(afrerLoadingEvent);
                datasource.setErrorLoadingEvent(errorLoadingEvent);
                datasource.setRedrawWidgetFunc(null);
                datasource.setFlag(0);
                datasource.reload();
            } else {
                interval = interval > 0 ?  interval: 0;
                datasource = new Datasources(widget, widgetId, panelInfo, type, interval, chartTypes, pushData, beforeLoadingEvent, afrerLoadingEvent, errorLoadingEvent);
                this.addTask(datasource);
            }
            return datasource;
        },
        deleteDatasource: function(widgetId) {
            for (var i = 0; i < $rootScope.datasources.length; i++) {
                if ($rootScope.datasources[i].widgetId == widgetId) {
                    $rootScope.datasources[i].splice(i, 1);
                }
            }
        },
        addTask: function(datasource) {
            $rootScope.datasources.push(datasource);
        },
        pushData: function(widgetId, data) {
            var datasource = this.getDatasource(widgetId);
            if (datasource) {
                datasource.setType('push');
                datasource.setData(data);
            } else {
                var datasource = this.createDatasource(null, widgetId, null, 'push', 0);
                datasource.setData(data);
                this.addTask(datasource)
            }
        },
        getDatasource: function(widgetId) {
            var currentDatasource = null;
            angular.forEach($rootScope.datasources, function(datasource, index, datasources) {

                if (datasource.widgetId == widgetId) {
                    currentDatasource = datasource;
                }
            });
            return currentDatasource;
        },
        reloadAllDatasource: function() {
            angular.forEach($rootScope.datasources, function(datasource, index, datasources) {
                datasource.reload(true);
            });
        },
        watchWidgetChange: function(currentScope, datasource){

            //监控panel全局设置
            currentScope.$watch('modal.dashboardTime.dateKey',function(newDateKey, oldDateKey){
                if(newDateKey !== oldDateKey && currentScope.rootPanel.now.panelId == datasource.widget.panelId){
                    if($rootScope.accessTokenTimeout > new Date().getTime()){
                        datasource.reload();
                    }else{
                        // 分享链接校验,获取accessToken
                        var request = GetRequest(),dashboadId;
                        if (request['id']) {
                            dashboadId = request['id'];
                        } else {
                            return;
                        }
                        var sharePassWord = localStorage.getItem('sharePassWord-' + dashboadId) ? encodeURIComponent(localStorage.getItem('sharePassWord-' + dashboadId)) : '';
                        var sharePanelUrl = LINK_SHARE_SIGNIN + 'panel' + '/' + dashboadId + '?password=' + sharePassWord;
                        dataMutualSrv.get(sharePanelUrl).then(function(data) {
                            if (data.status == 'success') {
                                var accessToken = data.content.accessToken;
                                if(accessToken){
                                    $rootScope.accessToken = accessToken;
                                    $rootScope.accessTokenTimeout = new Date().getTime() + 60*1000; // 有效期1分钟
                                    datasource.reload();
                                }
                            }else if(data.status == 'failed'){
                                currentScope.rootWidget.queryStatus = 'finish';
                                if(data.message === 'panelShareOff'){
                                    currentScope.sharePanelMsg.panelShareOff = true;
                                }else if(data.message === 'passwordError'){
                                    currentScope.sharePanelMsg.passwordError = true;
                                }else if(data.message === 'panelDelete'){
                                    currentScope.sharePanelMsg.panelShareOff = true;
                                }else if(data.message === 'spaceDelete'){
                                    currentScope.sharePanelMsg.panelShareOff = true;
                                }
                            }
                        });
                    }
                }
            });


            // 监控variables变化，variables为数组
            currentScope.$watch('widget.variables', function(newVariables, oldVariables) {

                var needLoadAttrList = ['sort'];
                var needRedrawAttrList = ['metrics'];

                var isNeedReload = false;
                var isNeedRedraw = false;
                if(newVariables === oldVariables){
                    isNeedReload = isNeedRedraw = false;
                }else{
                    isNeedReload = isNeedRedraw = !(newVariables.length == oldVariables.length);
                    if(!isNeedReload){ // true 后就不需要继续判断
                        for(var i = 0; i < newVariables.length; i++){
                            isNeedReload = !datasource.compareObjEqual(newVariables[i], oldVariables[i], needLoadAttrList);
                            isNeedRedraw = !datasource.compareObjEqual(newVariables[i], oldVariables[i], needRedrawAttrList);
                            if(isNeedReload){ // true 后就不需要继续判断
                                break;
                            }
                        }
                    }
                }

                // 判断variables是否变化、数据必须参数是否完整
                if (isNeedReload && datasource.isReadyForLoad()){
                    //分享panel的排序，也需要校验token，还需要加入密码保护的相关流程
                    if($rootScope.accessTokenTimeout > new Date().getTime()){
                        datasource.reload(null,'widget-refresh');
                    }else{
                        // 分享链接校验,获取accessToken
                        var request = GetRequest(),dashboadId;
                        if (request['id']) {
                            dashboadId = request['id'];
                        } else {
                            return;
                        }
                        var sharePassWord = localStorage.getItem('sharePassWord-' + dashboadId) ? encodeURIComponent(localStorage.getItem('sharePassWord-' + dashboadId)) : '';
                        var sharePanelUrl = LINK_SHARE_SIGNIN + 'panel' + '/' + dashboadId + '?password=' + sharePassWord;
                        dataMutualSrv.get(sharePanelUrl).then(function(data) {
                            if (data.status == 'success') {
                                var accessToken = data.content.accessToken;
                                if(accessToken){
                                    $rootScope.accessToken = accessToken;
                                    $rootScope.accessTokenTimeout = new Date().getTime() + 60*1000; // 有效期1分钟
                                    datasource.reload(null,'widget-refresh');
                                }
                            }else if(data.status == 'failed'){
                                currentScope.rootWidget.queryStatus = 'finish';
                                if(data.message === 'panelShareOff'){
                                    currentScope.sharePanelMsg.panelShareOff = true;
                                }else if(data.message === 'passwordError'){
                                    currentScope.sharePanelMsg.passwordError = true;
                                }else if(data.message === 'panelDelete'){
                                    currentScope.sharePanelMsg.panelShareOff = true;
                                }else if(data.message === 'spaceDelete'){
                                    currentScope.sharePanelMsg.panelShareOff = true;
                                }
                            }
                        });
                    }




                }else if(isNeedRedraw) {
                    datasource.redrawWidget();
                }

            },true);

        },
        setWidgetCommonEvent : function(currentScope, datasource){
            currentScope.showError = false;
            currentScope.showMessage = false;

            //向父级传递数据
            currentScope.$emit('to-parent', ['showError', currentScope.showError]);
            currentScope.$emit('to-parent', ['showMessage', currentScope.showMessage]);

            //注册数据请求前事件
            datasource.setBeforeLoadingEvent(function() {
                currentScope.showError = false;
                currentScope.showMessage = false;
                //if(currentScope.widget._ext){
                //	currentScope.widget._ext.showDownloadCsv = false;
                //}else{
                //	currentScope.widget._ext = {};
                //	currentScope.widget._ext.showDownloadCsv = false;
                //}
                //向父级传递数据
                currentScope.$emit('to-parent', ['showError', currentScope.showError]);
                currentScope.$emit('to-parent', ['showMessage', currentScope.showMessage]);
            });

            //注册请求后事件
            datasource.setAfrerLoadingEvent(function() {

                currentScope.widgetExtInfo = {}; // widget扩展信息
                currentScope.metricAmount = {}; // 指标总量
                if(datasource.getData()){
                    currentScope.widgetExtInfo = datasource.getData().widgetExtInfo || {};
                    var metricAmountMap = datasource.getData().metricsAmountsMap || {};
                    var selectedMetric = {};
                    var graphName = datasource.widget.baseWidget.graphName;
                    var showFirst = (graphName == 'number' || graphName == 'progressbar' || graphName == 'pie' || graphName == 'map');
                    angular.forEach(datasource.widget.variables, function(variable, i){
                        var metrics = variable.metrics || [];
                        angular.forEach(metrics, function(metrics, j){
                            if((i==0 && j==0) || (!showFirst && metrics.showMetricAmount)){ // 默认设置第一个为默认值
                                selectedMetric.code = metrics.code;
                                selectedMetric.name = metrics.name;
                                selectedMetric.alias = metrics.alias;
                                selectedMetric.uuid = metrics.uuid;
                                selectedMetric.key = metrics.code + '-' + metrics.uuid;
                            }
                        });
                    });
                    var mAmount = metricAmountMap[selectedMetric.key] || {};
                    currentScope.metricAmount.name = mAmount.name;
                    currentScope.metricAmount.showName = mAmount.showName || currentScope.metricAmount.name;
                    currentScope.metricAmount.showTitle = selectedMetric.alias ?  selectedMetric.alias+'\n'+selectedMetric.name : currentScope.metricAmount.showName;
                    currentScope.metricAmount.value = mAmount.value;
                    currentScope.metricAmount.unit = mAmount.unit;
                }
                datasource.afterWidgetDrawEvent();
                //}
                //向父级传递数据
                currentScope.$emit('to-parent', ['metricAmount', currentScope.metricAmount]);
                currentScope.$emit('to-parent', ['widgetExtInfo', currentScope.widgetExtInfo]);
                if(datasource.loadTime) currentScope.$emit('widgetLastLoadTime', datasource.loadTime);
            });

            //请求错误事件
            datasource.setErrorLoadingEvent(function() {
                currentScope.showError = true;
                currentScope.errorMsg = datasource.errorMsg;
                currentScope.showMessage = false;
                currentScope.message  = "";

                //向父级传递数据
                currentScope.$emit('to-parent', ['showError', currentScope.showError]);
                currentScope.$emit('to-parent', ['errorMsg', currentScope.errorMsg]);
                currentScope.$emit('to-parent', ['showMessage', currentScope.showMessage]);
                currentScope.$emit('to-parent', ['message', currentScope.message]);
                currentScope.$emit('to-parent-scope',currentScope);
                if(datasource.loadTime) currentScope.$emit('widgetLastLoadTime', datasource.loadTime);

                datasource.afterWidgetDrawEvent();
            });
        }

    };

}]);
