'use strict';

import tpl from 'components/modules/widget/widget-demo/widget-demo.tpl.html';
import {
	LINK_WIDGET_EDIT
} from 'components/modules/common/common';

/**
 * panelShare
 * panel分享
 *
 */
angular
.module('pt')
.directive('widgetDemo', ['$document', 'dataMutualSrv', widgetDemo]);

function widgetDemo($document, dataMutualSrv) {
	return {
		restrict: 'EA',
		replace: true,
		template: tpl,
		link: link
	};

	function link(scope, element, attrs) {
		var body = $document.find('body').eq(0);

		//取消
		scope.close = function () {
			scope.modal.demoShow = false;
		};
		var panelWatch = scope.$watch('rootPanel.nowId', function (newValue, oldValue, scope) {
			if (oldValue != newValue) {
				//注销当前监听事件
				panelWatch();

				scope.modal.demoShow = false;
			}
		});

		scope.removeDemoData = function () {
			scope.showChartData = null;
			scope.modal.editorNow.baseWidget.isDemo = 0;
			dataMutualSrv.post(LINK_WIDGET_EDIT, angular.copy(scope.modal.editorNow), 'wgtSave').then(function (data) {
				if (data.status == 'success') {
					scope.modal.editorNow.toolData.extend = "";
					scope.close();
					console.log('Post Data Success!')
				} else if (data.status == 'failed') {
					console.log('Post Data Failed!')
				} else if (data.status == 'error') {
					console.log('Post Data Error: ')
					console.log(data.message)
				}
			})

		};

		scope.saveDemoData = function () {
			var tempData = angular.copy(angular.fromJson(scope.showChartData));
			//table
			if (scope.modal.editorNow.baseWidget.ptoneGraphInfoId == 800) {
				// 计算第一个指标列（默认按照第一个指标降序排序）
				var firstMetrics = 0;
				var dimensions = scope.modal.editorNow._ext.demoData.dimensions;
				if (dimensions) {
					firstMetrics = dimensions.split(',').length;
				}
				var metricsName = scope.modal.editorNow._ext.demoData.series[0].data[0][firstMetrics];
				var count = 0;
				//取第一列指标的总和
				$.each(tempData, function (i, item) {
					if (i > 0) {
						count += parseInt(item[firstMetrics]);
					}
				});
				//改指标和总和
				$.each(scope.modal.editorNow._ext.demoData.metricsAmountsMap, function (key, value) {
					value.showName = metricsName;
					value.value = count;
				})
				scope.modal.editorNow._ext.demoData.series[0].data = tempData;
			} else if (scope.modal.editorNow.baseWidget.ptoneGraphInfoId == 900) {//map
				var metricsName;
				var count = 0;
				var mapData = [];
				$.each(tempData, function (i, item) {
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
				scope.modal.editorNow._ext.demoData.series[0].data = mapData;
				//改指标和总和
				$.each(scope.modal.editorNow._ext.demoData.metricsAmountsMap, function (key, value) {
					value.showName = metricsName;
					value.value = count;
				})
			} else {
				var array = [];
				//用户修改后的数据替换原有的数据
				scope.modal.editorNow._ext.demoData.categories = tempData.xAxis;
				//第一条数组的和
				var count = 0;
				var numberOrProgressbarName;
				$.each(scope.modal.editorNow._ext.demoData.series, function (i, serie) {
					serie.name = tempData.series[i].name;
					$.each(serie.data, function (j, data) {
						data.y = tempData.series[i].data[j];
						data.name = tempData.xAxis[j];
						numberOrProgressbarName = data.name;
						array.push(data.y);
						if (i == 0) {
							count += data.y;
						}
					});
					//改指标和总和
					$.each(scope.modal.editorNow._ext.demoData.metricsAmountsMap, function (key, value) {
						value.showName = serie.name;
						value.value = count;
					})
				});
				scope.modal.editorNow._ext.demoData.maxValue = Array.max(array);
				scope.modal.editorNow._ext.demoData.minValue = Array.min(array);

				//number,progressbar
				if (scope.modal.editorNow.baseWidget.ptoneGraphInfoId == 620 || scope.modal.editorNow.baseWidget.ptoneGraphInfoId == 720) {
					scope.modal.editorNow._ext.demoData.series[0].metricsName = numberOrProgressbarName;
				}
			}
			scope.modal.editorNow.baseWidget.isDemo = 1;
			scope.modal.editorNow.toolData.extend = scope.modal.editorNow._ext.demoData;
			dataMutualSrv.post(LINK_WIDGET_EDIT, angular.copy(scope.modal.editorNow), 'wgtSave').then(function (data) {
				if (data.status == 'success') {
					console.log('Post Data Success!')
				} else if (data.status == 'failed') {
					console.log('Post Data Failed!')
				} else if (data.status == 'error') {
					console.log('Post Data Error: ')
					console.log(data.message)
				}
			})
		};

		/*var chartElement = $("#" + widget.baseWidget.widgetId).find(".chart-thumb-div");

		 var chart = $(chartElement[0]).highcharts();
		 var x = [];
		 $.each(chart.series,function(i,serie){
		 var obj = {
		 name:serie.name,
		 data:[]
		 };
		 $.each(serie.data,function(j,data){
		 obj.data.push(data.y);
		 if(i == 0){
		 x.push(data.category);
		 }
		 })
		 chartData.push(obj);
		 })
		 chartData.push(x);
		 scope.showChartData = angular.toJson(chartData);*/

		scope.apply = function () {
			/*scope.saveChartData = angular.fromJson(scope.showChartData);
			 $.each(chart.series,function(i,serie){
			 $.each(serie.data,function(j,data){
			 data.update(scope.saveChartData[i].data[j]);
			 })
			 })*/
		}

	}
}
