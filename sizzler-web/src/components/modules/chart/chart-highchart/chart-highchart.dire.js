'use strict';

import highchartTpl from 'components/modules/chart/chart-highchart/chart-highchart.tpl.html';

/**
 * highchart
 * highchart指令
 *
 */
angular
    .module('pt')
    .directive('highchart', ['highchartsNGUtils', 'datasourceFactory', highchart]);

function highchart(highchartsNGUtils, datasourceFactory) {
    return {
        restrict: 'EA',
        template: highchartTpl,
        link: link
    };

    function link(scope, element, attrs) {
        //判断当前图标类型是否是highchart图表
        var variables, baseWidget, isCustomWidget;
        if (scope.widget.baseWidget.widgetType == 'custom' && scope.widget.children.length > 0) {
            variables = scope.child.variables;
            baseWidget = scope.child.baseWidget;
            isCustomWidget = true;
        } else {
            variables = scope.widget.variables;
            baseWidget = scope.widget.baseWidget;
            isCustomWidget = false;
        }


        if (baseWidget.graphName == 'hollowpie' || baseWidget.graphName == 'area' || baseWidget.graphName == 'areaspline' || baseWidget.graphName == 'bar' || baseWidget.graphName == 'pie' || baseWidget.graphName == 'column' || baseWidget.graphName == 'line') {

            //取数类型默认为pull
            var t = "pull";
            //获取图表取数类型
            if (variables[0] && variables[0].customApiInfo) {
                t = variables[0].customApiInfo.type;
            }
            var datasource;
            //创建图表取数任务
            if (isCustomWidget) {
                datasource = datasourceFactory.createDatasource(scope.child, scope.child.baseWidget.widgetId, scope.rootPanel.now, t, baseWidget.refreshInterval * 60000, baseWidget.graphName);
            } else {
                datasource = datasourceFactory.createDatasource(scope.widget, scope.widget.baseWidget.widgetId, scope.rootPanel.now, t, baseWidget.refreshInterval * 60000, baseWidget.graphName);
            }


            var highchartDiv = $(element).children(".chart-thumb-div");

            //highthart初始化类
            highchartsNGUtils(scope, highchartDiv, attrs, datasource);

            datasourceFactory.watchWidgetChange(scope, datasource);

            /*
             //监控图标当前时间粒度
             scope.$watch('datePeriod', function(newPeriod, oldPeriod) {
             if (newPeriod == oldPeriod) return;
             datasource.setParameter("datePeriod", newPeriod);
             datasource.runNumber = 0;
             }); */
        }

        //监控图表类型变化，判断是否重新创建Highcharts图表datasource
        scope.$watch('widget.baseWidget.graphName', function (newGraph, oldGraph) {
            if (newGraph == oldGraph) return;
            var a = false,
                b = false;
            //检查当前类型是否highchart图表类型
            var graphs = ['hollowpie', 'area', 'line', 'column', 'pie', 'bar', 'areaspline'];
            for (var i = 0; i < graphs.length; i++) {
                if (graphs[i] == newGraph) {
                    a = true;
                }
                if (graphs[i] == oldGraph) {
                    b = true;
                }
            }
            //判断以前图表非highchart图标，并且将要切换的图表是highchart图表
            if (a && !b) {
                //获取取数类型
                var t = "pull";
                if (variables[0] && variables[0].customApiInfo) {
                    t = variables[0].customApiInfo.type;
                }
                //初始化图表取数任务
                var datasource;
                if (isCustomWidget) {
                    datasource = datasourceFactory.createDatasource(scope.child, scope.widgetid, scope.rootPanel.now, t, baseWidget.refreshInterval * 60000, baseWidget.graphName);
                } else {
                    datasource = datasourceFactory.createDatasource(scope.widget, scope.widgetid, scope.rootPanel.now, t, baseWidget.refreshInterval * 60000, baseWidget.graphName);
                }

                //交给图表处理类处理
                highchartsNGUtils(scope, element, attrs, datasource);
            }
        });

        //接收子级传递的数据
        scope.$on('to-parent', function (event, data) {
            if (data && data.length == 2) {
                scope.data = scope.data || {};
                scope.data[data[0]] = data[1];
            }
        });
    }
}
