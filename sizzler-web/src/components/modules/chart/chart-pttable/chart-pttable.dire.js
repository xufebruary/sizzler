'use strict';

import pttableTpl from 'components/modules/chart/chart-pttable/chart-pttable.tpl.html';

/**
 * pttable
 * pttable指令
 *
 */

angular
    .module('pt')
    .directive('pttable', ['pttableNGUtils', 'datasourceFactory', pttableDirective]);

function pttableDirective(pttableNGUtils, datasourceFactory) {
    return {
        restrict: 'EA',
        template: pttableTpl,
        link: link
    }

    function link(scope, element, attrs) {
        var variables = scope.widget.variables;
        var baseWidget = scope.widget.baseWidget;

        //获取图表取数类型
        var t = "pull";
        if (variables[0] && variables[0].customApiInfo) {
            t = variables[0].customApiInfo.type;
        }
        //初始化图表取数任务
        var datasource = datasourceFactory.createDatasource(scope.widget, scope.widget.baseWidget.widgetId, scope.rootPanel.now, t, baseWidget.refreshInterval * 60000, 'number');

        // 创建dataTable图表
        pttableNGUtils.createDataTableChart(scope, element, attrs, datasource);
        datasourceFactory.watchWidgetChange(scope, datasource);
        datasourceFactory.setWidgetCommonEvent(scope, datasource);

    }
}
