'use strict';

import progressbarsTpl from 'components/modules/chart/chart-progressbars/chart-progressbars.tpl.html';
import {
	FormatNumber,
	GetPercent
} from '../../common/common';

/**
 * progressbars
 * progressbars指令，因原来框架里有progressbar只能叫progressbars
 *
 */

angular
    .module('pt')
    .directive('progressbars', ['datasourceFactory', progressbars]);

function progressbars(datasourceFactory) {
    return {
        restrict: 'EA',
        template: progressbarsTpl,
        link: link
    };

    function link(scope, element, attrs) {
        var variables = scope.widget.variables;
        var baseWidget = scope.widget.baseWidget;

        scope.formatNumber = FormatNumber;
        scope.getPercent = function (value, targetValue) {
            if (scope.widget.chartSetting.reverseTarget == 1) {
                return GetPercent(targetValue, value); // 反向目标
            } else {
                return GetPercent(value, targetValue);
            }
        };

        //定义数据返回成功后回掉函数
        var pushData = function () {
            var seriesData = datasource.getData();
            scope.data = {};
            if (seriesData) {
                scope.data = seriesData.series[0].data[0] || {};
                scope.data.dateRange = seriesData.dateRange;
                scope.data.name = seriesData.series[0].metricsName || '';
                scope.data.unit = seriesData.series[0].unit;
                scope.data.formatValue = scope.formatNumber(scope.data.y, scope.data.unit, 'shortArray');

                init();
            }

            //向父级传递数据
            scope.$emit('to-parent', ['dateRange', scope.data.dateRange]);

        };

        //获取数据获取类型
        var t = "pull";
        if (variables[0] && variables[0].customApiInfo) {
            t = variables[0].customApiInfo.type;
        }
        //初始化数据请求任务
        var datasource = datasourceFactory.createDatasource(scope.widget, scope.widget.baseWidget.widgetId, scope.rootPanel.now, t, baseWidget.refreshInterval * 60000, 'progressbars');
        datasource.setPushData(pushData);
        if (datasource.runNumber != 0) {
            datasource.runNumber = 0;
        }

        //清空数据
        datasource.setData(null);
        datasourceFactory.watchWidgetChange(scope, datasource);
        datasourceFactory.setWidgetCommonEvent(scope, datasource);


        //number init
        var init = function () {

            //设置总体字体大小
            var defalutW = 170;
            var defalutH = 230;
            var w = scope.widget.sizeX * scope.rootChart.colWidth;
            var h = scope.widget.sizeY * scope.rootChart.rowHeight;
            var scale = Math.min(w / defalutW, h / defalutH);
            var domH = parseInt(130 * scale); //130为默认高度值

            element.css('font-size', parseInt(scale * 10) + 'px').find('.dom-table-cell').height(domH + 'px');
            element.find('.chart-progressbars-bg').css({
                'height': parseInt(6 * scale) + 'px',
                'border-radius': parseInt(parseInt(6 * scale) / 2) + 'px'
            }).end().find('.chart-progressbars-bar').css('border-radius', parseInt(parseInt(6 * scale) / 2) + 'px');

            //设置百分比字体大小
            var data = scope.getPercent(scope.data.y, scope.widget.baseWidget.targetValue) + '';
            var dataScale = 5 / (data.length > 5 ? data.length : 5);
            element.find('.chart-progressbars-data span').css('font-size', dataScale * 4.6 + 'em');
            element.find('.chart-progressbars-data small').css('font-size', dataScale * 3.4 + 'em');

            datasource.afterWidgetDrawEvent();
        };

        // 注册widget重绘方法
        datasource.setRedrawWidgetFunc(function () {
            init();
        });
    }
}
