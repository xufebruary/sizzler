'use strict';



/**
 * widget demo数据设置
 *
 */

import './widget-demo.scss';
import tpl from './widget-demo.html';

widgetDemoDirective.$inject = ['WidgetServices'];

function widgetDemoDirective(WidgetServices) {
    return {
        restrict: 'EA',
        scope: {
            modal: "<", //dashboard.js中定义的一些参数
            ptSettings: "<", //dashboard.js中定义的一些参数
            currentPanelId: "<", //面板ID
            currentWidget: "<", //当前widget
            onCancel: '&', //取消回调
            onSuccess: '&' //发送成功回调
        },
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {
        var currentWidget = angular.copy(scope.currentWidget);
        var panelWatch = scope.$watch('currentPanelId', function(newValue, oldValue, scope) {
            if (oldValue != newValue) {
                panelWatch();

                scope.onCancel();
            }
        });
        scope.myOptions = {};

        // ==========

        //删除demo数据
        scope.removeDemoData = removeDemoData;

        //保存demo数据
        scope.saveDemoData = saveDemoData;

        //入口
        init();

        // ==========

        //入口
        function init() {
            scope.pt = {
        		settings: scope.ptSettings
        	}

            //生成显示数据
            scope.showChartData = angular.toJson(getDemoData(angular.copy(scope.currentWidget)));
        }

        //生成显示数据
        function getDemoData(widget){
            let demoChartData;

            if (widget.baseWidget.ptoneGraphInfoId == 800) {
                demoChartData = [];
                $.each(widget._ext.demoData.series, function(i, serie) {
                    $.each(serie.data, function(j, data) {
                        var row = [];
                        $.each(data, function(k, record) {
                            row.push(record);
                        });
                        demoChartData.push(row);
                    })
                })
            } 
            else if (widget.baseWidget.ptoneGraphInfoId == 900) {
                demoChartData = [];
                $.each(scope.modal.editorNow._ext.demoData.metricsAmountsMap, function(key, value) {
                    scope.metricsName = value.showName;
                    return false;
                });
                var o = {
                    metrics: scope.metricsName
                };
                demoChartData.push(o);
                $.each(widget._ext.demoData.series, function(i, serie) {
                    $.each(serie.data, function(j, data) {
                        var map = {
                            code: data.code,
                            value: data.value[0][scope.metricsName]
                        };
                        if (data.code != "(not set)") {
                            demoChartData.push(map);
                        }
                    })
                })
            } 
            else {
                demoChartData = {
                    xAxis: widget._ext.demoData.categories,
                    series: []
                };
                var flag = false;
                if (!demoChartData.xAxis || demoChartData.xAxis.length == 0) {
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
                            demoChartData.xAxis.push(data.name);
                        }
                    });
                    demoChartData.series.push(obj);
                })
            }

            return demoChartData;
        }

        //删除demo数据
        function removeDemoData() {
            currentWidget.baseWidget.isDemo = 0;
            currentWidget.toolData.extend = "";
            scope.showChartData = null;

            widgetUpdate(currentWidget);
            scope.onCancel();
        }

        //保存demo数据
        function saveDemoData () {
            var tempData = angular.copy(angular.fromJson(scope.showChartData));
            //table
            if (currentWidget.baseWidget.ptoneGraphInfoId == 800) {
                // 计算第一个指标列（默认按照第一个指标降序排序）
                var firstMetrics = 0;
                var dimensions = currentWidget._ext.demoData.dimensions;
                if (dimensions) {
                    firstMetrics = dimensions.split(',').length;
                }
                var metricsName = currentWidget._ext.demoData.series[0].data[0][firstMetrics];
                var count = 0;
                //取第一列指标的总和
                $.each(tempData, function(i, item) {
                    if (i > 0) {
                        count += parseInt(item[firstMetrics]);
                    }
                });
                //改指标和总和
                $.each(currentWidget._ext.demoData.metricsAmountsMap, function(key, value) {
                    value.showName = metricsName;
                    value.value = count;
                })
                currentWidget._ext.demoData.series[0].data = tempData;
            } 
            else if (currentWidget.baseWidget.ptoneGraphInfoId == 900) { 
                //map
                var metricsName;
                var count = 0;
                var mapData = [];
                $.each(tempData, function(i, item) {
                    if (i == 0) {
                        metricsName = item['metrics'];
                    } else {
                        var o = {};
                        o[metricsName] = item.value;
                        count += parseInt(item.value);
                        var map = {
                            code: item.code,
                            value: [o]
                        };
                        mapData.push(map);
                    }
                });
                currentWidget._ext.demoData.series[0].data = mapData;
                //改指标和总和
                $.each(currentWidget._ext.demoData.metricsAmountsMap, function(key, value) {
                    value.showName = metricsName;
                    value.value = count;
                })
            } 
            else {
                var array = [];
                //用户修改后的数据替换原有的数据
                currentWidget._ext.demoData.categories = tempData.xAxis;
                //第一条数组的和
                var count = 0;
                var numberOrProgressbarName;
                $.each(currentWidget._ext.demoData.series, function(i, serie) {
                    serie.name = tempData.series[i].name;
                    $.each(serie.data, function(j, data) {
                        data.y = tempData.series[i].data[j];
                        data.name = tempData.xAxis[j];
                        numberOrProgressbarName = data.name;
                        array.push(data.y);
                        if (i == 0) {
                            count += data.y;
                        }
                    });
                    //改指标和总和
                    $.each(currentWidget._ext.demoData.metricsAmountsMap, function(key, value) {
                        value.showName = serie.name;
                        value.value = count;
                    })
                });
                currentWidget._ext.demoData.maxValue = Array.max(array);
                currentWidget._ext.demoData.minValue = Array.min(array);

                //number,progressbar
                if (currentWidget.baseWidget.ptoneGraphInfoId == 620 || currentWidget.baseWidget.ptoneGraphInfoId == 720) {
                    currentWidget._ext.demoData.series[0].metricsName = numberOrProgressbarName;
                }
            }
            currentWidget.baseWidget.isDemo = 1;
            currentWidget.toolData.extend = currentWidget._ext.demoData;

            widgetUpdate(currentWidget);
        }

        //更新widget
        function widgetUpdate(widget) {
            var sendData = WidgetServices.sendDataFormat(angular.copy(widget));
            WidgetServices.update(sendData)
            .then((data) => {
                scope.onSuccess({ data: { "widget": widget } })
                scope.myOptions.isSuccess = true;

                setTimeout(function() {
                    scope.$apply(function(){
                        scope.myOptions.isSuccess = false;
                    })
                }, 3000);
            })
        }
    }
}

export default widgetDemoDirective;
