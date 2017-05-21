'use strict';

import numberTpl from 'components/modules/chart/chart-number/chart-number.tpl.html';
import {
	FormatNumber,
	GetPercent,
	getMyDsConfig
} from '../../common/common';

/**
 * number
 * number指令
 *
 */

angular
.module('pt')
.directive('number', ['datasourceFactory', number]);

function number(datasourceFactory) {
	return {
		restrict: 'EA',
		template: numberTpl,
		link: link
	};

	function link(scope, element, attrs) {
		scope.numberSetting = {
			isCustomWidget: scope.widget.children && scope.widget.children.length > 0 ? true : false
		};

		var variables, baseWidget;
		if (scope.numberSetting.isCustomWidget) {
			variables = scope.child.variables;
			baseWidget = scope.child.baseWidget;
		} else {
			variables = scope.widget.variables;
			baseWidget = scope.widget.baseWidget;
		}

		scope.formatNumber = FormatNumber; // chart.ctrl.js 中定义
		scope.getPercent = GetPercent; //计算百分比方法

		//获取图表取数类型
		var t = "pull";
		if (variables[0] && variables[0].customApiInfo) {
			t = variables[0].customApiInfo.type;
		}

		//定义数据返回成功后回掉函数
		var pushData = function () {
			var seriesData = this.getData();
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

		//初始化图表取数任务
		var datasource;
		if (scope.numberSetting.isCustomWidget) {
			datasource = datasourceFactory.createDatasource(scope.child, scope.child.baseWidget.widgetId, scope.rootPanel.now, t, baseWidget.refreshInterval * 60000, 'number');
		} else {
			datasource = datasourceFactory.createDatasource(scope.widget, scope.widget.baseWidget.widgetId, scope.rootPanel.now, t, baseWidget.refreshInterval * 60000, 'number');
		}

		datasource.setPushData(pushData);


		//计算增长率方法
		scope.getChain = function (num, total) {
			if (num != total) {
				var c = Math.abs(num - total);
				return scope.getPercent(c, total);
			} else {
				return 0;
			}
		};

		//清空数据
		datasource.setData(null);

		datasourceFactory.watchWidgetChange(scope, datasource);
		datasourceFactory.setWidgetCommonEvent(scope, datasource);


		//number init
		scope.showNumberPercent = true; // 默认显示环比值
		var init = function () {
			var defalutW = 170;
			var defalutH = 230;
			var w, h, scale;
			if (scope.numberSetting.isCustomWidget) {
				var layout = scope.child.layout;
				w = parseInt(layout.width);
				h = parseInt(layout.height);
			} else {
				w = scope.widget.sizeX * scope.rootChart.colWidth;
				h = scope.widget.sizeY * scope.rootChart.rowHeight;
			}
			if (scope.numberSetting.isCustomWidget) {
				scale = Math.min(w / defalutW, h / defalutH);
			} else {
				scale = Math.min(w / defalutW, h / defalutH);
			}
			var domH = parseInt(130 * scale); //130为默认高度值


			if (scope.numberSetting.isCustomWidget) {
				element.css('font-size', parseInt(scale * 40) + 'px').find('.dom-table-cell').height(domH + 'px');
			} else {
				element.css('font-size', parseInt(scale * 10) + 'px').find('.dom-table-cell').height(domH + 'px');
			}

			// 判断是否显示环比
			var dsConfig = getMyDsConfig(scope.widget.variables[0].dsCode);
			if (!dsConfig.editor.time.isTimeDisable && !scope.widget.variables[0].dateDimensionId) {
				scope.showNumberPercent = false; // 如果时间根据维度判断，且没有时间维度则不显示环比
			}

			//console.log('h: '+h+'; scale: '+scale);
			datasource.afterWidgetDrawEvent();

			//控制箭头显示与隐藏
			if (scope.showNumberPercent && !scope.numberSetting.isCustomWidget) {
				angular.element(element).find('.chart-number-percent').removeClass('hide');
			} else {
				angular.element(element).find('.chart-number-percent').addClass('hide');
			}
		};

		// 注册widget重绘方法
		datasource.setRedrawWidgetFunc(function () {
			init();
		});

	}
}
