'use strict';

import simplenumberTpl from 'components/modules/chart/chart-simplenumber/chart-simplenumber.tpl.html';
import {
	FormatNumber,
	GetPercent
} from '../../common/common';

/**
 * simplenumber
 * simplenumber指令
 *
 */

angular
    .module('pt')
    .directive('simplenumber', ['datasourceFactory', simplenumber]);

function simplenumber(datasourceFactory) {
    return {
        restrict: 'EA',
        template: simplenumberTpl,
        link: link
    };

    function link(scope, element, attrs) {
        var variables = scope.widget.variables;
        var baseWidget = scope.widget.baseWidget;

        scope.formatNumber = FormatNumber;

        //定义取数任务推送数据函数
        var pushData = function () {
            scope.data = this.getData() || {};
            this.afterWidgetDrawEvent();
            //向父级传递数据
            scope.$emit('to-parent', ['dateRange', scope.data.dateRange]);
        };
        //获取图表取数类型
        var t = "pull";
        if (variables[0] && variables[0].customApiInfo) {
            t = variables[0].customApiInfo.type;
        }
        //初始化图表取数任务
        var datasource = datasourceFactory.createDatasource(scope.widget, scope.widgetid, scope.rootPanel.now, t, baseWidget.refreshInterval * 60000, 'simplenumber');
        //设置runNumber为0立即取数
        datasource.runNumber = 0;
        //清空取数任务内的数据
        datasource.setData(null);
        //添加取数成功后回掉函数
        datasource.setPushData(pushData);
        if (datasource.getData()) {
            datasource.pushData();
        }

        datasourceFactory.watchWidgetChange(scope, datasource);

        datasourceFactory.setWidgetCommonEvent(scope, datasource);

    }
}
