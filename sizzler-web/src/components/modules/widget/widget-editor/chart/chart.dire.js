import {
	LINK_WIDGET_ADD,
	uuid,
	objectIsEmpty
} from 'components/modules/common/common';

import ProductConfig from 'configs/product.config';

import {
	continentsCode,
	countryCode
	} from 'assets/i18n/country_code';

import treeUtils from 'utils/tree-adaptor.utils';

import editorChartTpl from './chart.tpl.html';
import './chart.css';
// import Highcharts from 'vendor/highcharts/highcharts.min';

editorChart.$inject = ['$translate', '$rootScope', 'dataMutualSrv', 'siteEventAnalyticsSrv', 'sysRoles', '$timeout','Track'];
function editorChart($translate, $rootScope, dataMutualSrv, siteEventAnalyticsSrv, sysRoles, $timeout,Track) {
	return {
		restrict: 'EA',
		replace: true,
		template: editorChartTpl,
		link: link
	};

	function link(scope, elem, attrs) {

		//监听气泡显示状态
		// scope.$watch('editor.pop.name', function (value) {
		//     if (value && value == 'chart') {
		//         $document.bind('click', documentClickBindChart);
		//     } else {
		//         $document.unbind('click', documentClickBindChart);
		//     }
		// });
		var documentClickBindChart = function (event) {
			if (chartSettings.excludeClick) {
				chartSettings.excludeClick = false;
			} else if (scope.editor.pop.show && typeof(angular.element(event.target).attr('step-chart')) == 'undefined' && !elem[0].contains(event.target)) {
				scope.$apply(function () {
					scope.editor.pop.name = null;
					scope.editor.pop.show = false;
					scope.editor.pop.ele = null;
				});
			}
		};

		var chartSettings = scope.chartSettings = {
			//2-Axis
			dbAxisToggle: false,
			dbAxis: 0,
			dbMod: null,

			//图形
			dbGraph: [],            //双轴时的图形
			// datePeriodList: ['hour','day','week','month'],
			datePeriodList: [
				{
					'name': $translate.instant('COMMON.DATE_PERIOD.HOUR'),
					'code': 'hour'
				},
				{
					'name': $translate.instant('COMMON.DATE_PERIOD.DAY'),
					'code': 'day'
				},
				{
					'name': $translate.instant('COMMON.DATE_PERIOD.WEEK'),
					'code': 'week'
				},
				{
					'name': $translate.instant('COMMON.DATE_PERIOD.MONTH'),
					'code': 'month'
				}
			],

			//map list
			mapList:null,

			//目标值
			modelTarget: null,
			targetError: false,

			//堆叠图
			dbStacked: [],          //双轴时的堆叠图
			stackedChart: false,
			areaChart: false,

			//metrics
			metricsList: [],
			modelMetrics: {},

			//Axis Settings
			axisSet: false,
			axisType: 'auto',
			modelAxis: 'auto',      //axis-radio-btn('auto')
			modelMin: 'auto',
			modelMax: 'auto',
			axisError: [false, false],

			//dispaly settings
			displaySet: true,
			modelDisplay: 'auto',   //display-radio-btn('auto')
			xAxis: false,           //X Axis Labels
			yAxis: false,           //Y Axis Labels
			showLegend: false,      //Legend
			showDataLabels: false, //ShowDataLabels
			showMapName: false,      //map name
			showTimePeriod: false,  //Time Period
			showMetricAmount: false,
			excludeClick: false,
			hideDetail: false,
			reverseTarget: false
		};

		//数据初始化
		dataInit();

		scope.mapCountrys = [];
		scope.continents = continentsCode;
		scope.chartSettings.mapCountrySearch = false;

		if (scope.modal.editorNow.baseWidget.mapCode != "world") {
			scope.countryRadio = "countrySelected";
		} else {
			scope.countryRadio = "worldSelected";
		}

		scope.worldChecked = function () {
			scope.modal.editorNow.baseWidget.mapCode = "world";
			scope.saveData('chart-worldChecked');
		};

		// 拼接数据
		$.each(Highcharts.mapDataIndex.Countries, function (name, path) {
			$.each(countryCode, function (i, item) {
				if (name == item['code']) {
					scope.mapCountrys.push({
						name: angular.fromJson(item['name']),
						path: path,
						continent: item['continent'],
						code: item['code']
					})
				}
			});
		});

		//按洲分组
		scope.mapGroupCountries = [];
		$.each(scope.continents, function (i, item) {
			var groupObj = {
				"continent": item,
				"groupCountries": []
			};
			$.each(scope.mapCountrys, function (j, obj) {
				if (angular.equals(groupObj['continent'], obj['continent'])) {
					groupObj['groupCountries'].push(obj);
				}
			});
			scope.mapGroupCountries.push(angular.copy(groupObj));
		});

		scope.chartSettings.mapList = treeUtils.formatMapList(scope.mapGroupCountries,scope.rootUser.settingsInfo.locale);
		//地图的国家选择
		scope.toggleMapList = function(){
			// scope.chartSettings.mapCountrySearch = !scope.chartSettings.mapCountrySearch;
			$timeout(function(){
				angular.element('.map-list-search .search-list .bd').slimscroll({
					height: '160px',
					size:'6px',
					allowPageScroll: false
				});
			},100);
		};

		scope.changeMapCode = function (map) {
			var item = treeUtils.getItem(scope.chartSettings.mapList, map.id);
			scope.chartSettings.mapCountrySearch = !scope.chartSettings.mapCountrySearch;
			chartSettings.excludeClick = true;
			var baseMapPath = "/assets/libs/highcharts/map/";
			var paths = item.path.split("/");
			var javascriptPath = baseMapPath + paths[paths.length - 1];
			if (Highcharts.maps[item.path.replace(".js", "")]) {
				scope.modal.editorNow.baseWidget.mapCode = item.id;
				scope.saveData('chart-changeMapCode');
			} else {
				$.getScript(javascriptPath, function () {
					scope.$apply(function () {
						scope.modal.editorNow.baseWidget.mapCode = item.id;
						scope.saveData('chart-changeMapCode');
					});
				});
			}
		};


		//双轴开关
		scope.toggleDbAxis = function () {
			chartSettings.dbAxisToggle = !chartSettings.dbAxisToggle;
			scope.modal.editorNow.chartSetting.showMultiY = chartSettings.dbAxisToggle ? 1 : 0;
			scope.chartSettings.dbMod = chartSettings.dbAxisToggle ? 'db' : '';

			// 同步y1轴的graphType
			if (scope.modal.editorNow.chartSetting.yAxis && scope.modal.editorNow.chartSetting.yAxis[0]) {
				scope.modal.editorNow.chartSetting.yAxis[0].chartType = scope.modal.editorNow.baseWidget.graphName;
				scope.modal.editorNow.chartSetting.yAxis[0].areaChart = scope.modal.editorNow.chartSetting.areaChart;
				scope.modal.editorNow.chartSetting.yAxis[0].stackedChart = scope.modal.editorNow.chartSetting.stackedChart;

				chartSettings.dbGraph[0] = scope.modal.editorNow.baseWidget.graphName;
			}

			// 关闭双轴后，如果原来在Y2轴，则切为Y1
			if (!chartSettings.dbAxisToggle && chartSettings.dbAxis == 1) {
				scope.changeDbAxis();
			}

			//save
			scope.saveData('chart-toggleDbAxis');

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_chart', '2-axis-' + chartSettings.dbAxisToggle);

			//全站事件统计
			Track.log({where: 'widget_editor_chart', what: 'select_axis',value: scope.modal.editorNow.chartSetting.showMultiY});
		};

		//切换双轴
		scope.changeDbAxis = function () {
			chartSettings.dbAxis = chartSettings.dbAxis == 0 ? 1 : 0;

			//堆叠图
			chartSettings.stackedChart = chartSettings.dbStacked[chartSettings.dbAxis].stackedChart;
			chartSettings.areaChart = chartSettings.dbStacked[chartSettings.dbAxis].areaChart;

			//指标项互斥
			for (var i in scope.chartSettings.modelMetrics) {
				scope.chartSettings.modelMetrics[i] = !scope.chartSettings.modelMetrics[i];
			}

			//最大值&最小值
			if (scope.modal.editorNow.chartSetting.yAxis.length > 0) {
				if (!scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis]) {
					scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis] = {};
				}
				chartSettings.modelMin = scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis].min ? scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis].min : 'auto';
				chartSettings.modelMax = scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis].max ? scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis].max : 'auto';
			}

			if (chartSettings.modelMin != 'auto' || chartSettings.modelMax != 'auto') {
				chartSettings.modelAxis = 'false';
			} else {
				chartSettings.modelAxis = 'auto';
			}

			//全站事件统计
			var what;
			if(chartSettings.dbAxis === 1){
				what = 'y2';
			}else if(chartSettings.dbAxis === 0){
				what = 'y1';
			}
			Track.log({where: 'widget_editor_chart', what: 'select_' + what});

		};

		//选择图形
		scope.selectGraph = function (grap) {
			if (scope.modal.editorNow.baseWidget.widgetType == 'custom') {
				scope.wgtCreate(grap.code, scope.modal.editorNow.baseWidget.widgetId);
			}

			chartSettings.dbGraph[chartSettings.dbAxis] = grap.code;
			if (scope.modal.editorNow.baseWidget.mapCode != "world") {
				scope.countryRadio = "countrySelected";
			} else {
				scope.countryRadio = "worldSelected";
			}
			//双轴开启时
			if (chartSettings.dbAxisToggle) {
				if (scope.modal.editorNow.chartSetting.yAxis.length == 0) {
					// scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis] = {};
					scope.modal.editorNow.chartSetting.yAxis = [{}, {}];
				} else if (scope.modal.editorNow.chartSetting.yAxis.length == 1) {
					scope.modal.editorNow.chartSetting.yAxis.push({});
				}

				scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis]['chartType'] = chartSettings.dbGraph[chartSettings.dbAxis];
			}

			if (!chartSettings.dbAxisToggle || (chartSettings.dbAxisToggle && chartSettings.dbAxis == 0)) {
				scope.modal.editorNow.baseWidget.graphName = grap.code;
				scope.modal.editorNow.baseWidget.ptoneGraphInfoId = grap.id;
				scope.modal.editorNow.variables[0].variableGraphId = grap.id;
			}

			//save
			scope.saveData();

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_chart', grap.name);

			//全站事件统计
			Track.log({where: 'widget_editor_chart', what: 'select_chart_type',value: grap.name});
		};
		//自定义widget选择富文本框类型
		scope.selectToolGraph = function () {
			scope.wgtCreate('tool', scope.modal.editorNow.baseWidget.widgetId);
			//save
			scope.saveData();
		};

		// 时间粒度
		scope.changeDatePeriod = function (value) {
			// chartSettings.selectedDatePeriod = $translate.instant('COMMON.DATE_PERIOD.'+angular.uppercase(value));
			chartSettings.selectedDatePeriod = value.name;
			scope.modal.editorNow.baseWidget.datePeriod = value.code;
			scope.saveData('chart-changeDatePeriod');
		};

		//目标值
		scope.targetSet = function (value) {
			var reg = new RegExp("^[-+]?[0-9]+(\.[0-9]+)?$");

			if (reg.test(value) || value == '') {
				changeTarget(value);
				chartSettings.targetError = false;
			} else {
				chartSettings.targetError = true;
			}
		};
		//间隔350毫秒
		var changeTargetTimer = null;

		function changeTarget(value) {
			clearTimeout(changeTargetTimer);
			changeTargetTimer = window.setTimeout(function () {
				scope.modal.editorNow.baseWidget.targetValue = value;

				//save
				scope.saveData('chart-changeTarget')
			}, 350);
		}


		//堆叠图
		scope.setStack = function (type) {
			var t = chartSettings[type] == true ? 1 : 0;

			//双轴开启时
			if (chartSettings.dbAxisToggle) {
				if (scope.modal.editorNow.chartSetting.yAxis.length == 0) {
					// scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis] = {};
					scope.modal.editorNow.chartSetting.yAxis = [{}, {}];
				}
				scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis][type] = t;
				chartSettings.dbStacked[chartSettings.dbAxis][type] = chartSettings[type];
			}

			if (!chartSettings.dbAxisToggle || (chartSettings.dbAxisToggle && chartSettings.dbAxis == 0)) {
				scope.modal.editorNow.chartSetting[type] = t;
			}


			//save
			scope.saveData('chart-setStack');

			//全站事件统计
			Track.log({where: 'widget_editor_chart', what: 'select_' + type,value: chartSettings[type]});
		};

		//双轴时指标选择
		scope.sltMetrics = function (type, index) {

			if ((chartSettings.dbAxis == 0 && scope.chartSettings.modelMetrics[type]) || (chartSettings.dbAxis == 1 && !scope.chartSettings.modelMetrics[type])) {
				scope.modal.editorNow.chartSetting.metricsToY[type] = 0;
			} else {
				scope.modal.editorNow.chartSetting.metricsToY[type] = 1;
			}

			scope.saveData('chart-sltMetrics');
		};

		//切换设置(Axis || Display)
		scope.set = function (type) {
			chartSettings[type] = !chartSettings[type];

			if (type == 'axisSet') {
				chartSettings.displaySet = false;
			} else {
				chartSettings.axisSet = false;
			}
		};


		//Axis Settings
		scope.axisSet = function (value, type, index) {
			value = value == '' ? 'auto' : value;
			var reg = new RegExp("^[-+]?[0-9]+(\.[0-9]+)?$");

			if (value == 'auto') {
				if (scope.modal.editorNow.chartSetting.yAxis.length != 0) {

					if (!type) {
						scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis].min = null;
						scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis].max = null;
						chartSettings.modelMin = 'auto';
						chartSettings.modelMax = 'auto';
					} else if (type == 'max') {
						scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis].max = null;
						//chartSettings.modelMax = 'auto';

					} else if (type == 'min') {
						scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis].min = null;
						//chartSettings.modelMin = 'auto';
					}

					// auto时最大值、最小值设置为空
					//if (chartSettings.axisType != 'auto') {
					//save
					scope.saveData('chart-axisSet')
					//}
					//全站事件统计
					Track.log({where: 'widget_editor_chart', what: 'axis_auto',value: 'true'});
				}

			} else {
				chartSettings.modelAxis = 'false';

				if (reg.test(value)) {
					//比较最大值和最小值
					if (angular.isNumber(+chartSettings.modelMin) && angular.isNumber(+chartSettings.modelMax)) {
						if (((+chartSettings.modelMin) > (+chartSettings.modelMax))) {
							chartSettings.axisError[index] = true;
							return;
						} else {
							chartSettings.axisError = [false, false];
						}
					}

					chartSettings.axisError[index] = false;
					changeAxis(value, type);
				} else {
					chartSettings.axisError[index] = true;
				}

				//全站事件统计
				Track.log({where: 'widget_editor_chart', what:  type + '_axis_value', how: 'input',value: value});
			}
		};
		//间隔50毫秒
		var changeAxisTimer = null;

		function changeAxis(value, type) {
			clearTimeout(changeAxisTimer);
			changeAxisTimer = window.setTimeout(function () {
				if (!scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis]) {
					scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis] = {};
				}
				scope.modal.editorNow.chartSetting.yAxis[chartSettings.dbAxis][type] = value;

				//save
				scope.saveData('chart-changeAxis')
			}, 50);
		}


		//Display Settings
		scope.displaySet = function (type) {
			if (type == 'auto') {
				scope.modal.editorNow.baseWidget.showTimePeriod = '0';
				scope.modal.editorNow.baseWidget.showMetricAmount = '1';
				scope.modal.editorNow.chartSetting.showLegend = '',
					scope.modal.editorNow.chartSetting.showDataLabels = '',
					scope.modal.editorNow.chartSetting.xAxis = [{
						"enabled": null
					}];

				if (scope.modal.editorNow.chartSetting.yAxis.length == 1) {
					scope.modal.editorNow.chartSetting.yAxis[0].enabled = null;
				} else if (scope.modal.editorNow.chartSetting.yAxis.length == 2) {
					scope.modal.editorNow.chartSetting.yAxis[0].enabled = null;
					scope.modal.editorNow.chartSetting.yAxis[1].enabled = null;
				}

				chartSettings.xAxis = false;
				chartSettings.yAxis = false;
				// chartSettings.showLegend = false;
				scope.modal.editorNow._ext.widgetShowLegend = false;
				chartSettings.showTimePeriod = false;
				chartSettings.showMetricAmount = false;

				chartSettings.modelDisplay = 'auto';
			} else {
				chartSettings[type] = !chartSettings[type];

				var s = chartSettings[type] ? '1' : '0';
				if (type == 'showTimePeriod') {
					if (scope.modal.editorNow.baseWidget.showTimePeriod != s) {
						scope.modal.editorNow.baseWidget.showTimePeriod = s;
					}
				} else if (type == 'showMetricAmount') {
					if (scope.modal.editorNow.baseWidget.showMetricAmount != s) {
						scope.modal.editorNow.baseWidget.showMetricAmount = s;
					}
				} else if (type == 'showMapName') {
					scope.modal.editorNow.chartSetting[type] = s;
				} else if (type == 'hideDetail') {
					scope.modal.editorNow.chartSetting[type] = s;
				} else if (type == 'reverseTarget') {
					scope.modal.editorNow.chartSetting[type] = s;
				} else {
					if (type == 'xAxis') {
						scope.modal.editorNow.chartSetting[type] = [{
							"enabled": chartSettings[type]
						}];
					} else if (type == 'yAxis') {
						if (scope.modal.editorNow.chartSetting.yAxis.length == 1) {
							scope.modal.editorNow.chartSetting.yAxis[0].enabled = chartSettings[type];
						} else if (scope.modal.editorNow.chartSetting.yAxis.length == 2) {
							scope.modal.editorNow.chartSetting.yAxis[0].enabled = chartSettings[type];
							scope.modal.editorNow.chartSetting.yAxis[1].enabled = chartSettings[type];
						} else {
							scope.modal.editorNow.chartSetting.yAxis = [{
								"enabled": chartSettings[type]
							}, {
								"enabled": chartSettings[type]
							}];
						}
					} else {
						scope.modal.editorNow.chartSetting[type] = s
					}
				}

				if (!chartSettings.xAxis && !chartSettings.yAxis && !scope.modal.editorNow._ext.widgetShowLegend && !chartSettings.showTimePeriod) {
					//chartSettings.modelDisplay = 'auto';
				} else {
					chartSettings.modelDisplay = 'false';
				}
				//全站事件统计
				Track.log({where: 'widget_editor_chart', what: 'select_' + type,value: chartSettings[type]});
			}

			//save
			scope.saveData('chart-displaySet')
		};

		scope.changeMetricAmoutSelect = function (item) {
			if (item) {
				angular.forEach(scope.modal.editorNow.variables[0].metrics, function (metricItem, index) {
					if (item.uuid == metricItem.uuid) {
						metricItem.showMetricAmount = true;
					} else {
						metricItem.showMetricAmount = false;
					}
				});

				if (item.type == 'compoundMetrics' || scope.modal.editorNow.variables[0].ptoneDsInfoId != 1) {
					chartSettings.selectedMetricAmount = item.name;
				} else {
					chartSettings.selectedMetricAmount = $translate.instant(item.i18nCode);
				}

				scope.saveData('chart-changeMetricAmoutSelect');

				//全站事件统计
				Track.log({where: 'widget_editor_chart', what: 'select_amount_value',value: item.name});
			}
		};


		//图表类型选择
		scope.showChart = function(grap){
			"use strict";
			//yahoo Ys SS数据源需要根据不同的profileId设置不同的图表
			if(scope.dsConfig.editor.chart.needSelectProfileId){
				var variables = scope.modal.editorNow.variables[0],flag = null;
				if(variables.dimensions && variables.dimensions.length){
					flag = variables.dimensions.some(function(item){
						return item.isDefaultSelect === 1;
					});
					if(flag){
						return ( (scope.chartSettings.dbAxisToggle && grap.id == 100) || (scope.chartSettings.dbAxisToggle && grap.id == 300) || !scope.chartSettings.dbAxisToggle ) &&
							(scope.dsConfig.editor.chart.showMap || grap.id !== 900) &&  (grap.id == 800 || grap.id == 620 || grap.id == 720);
					}
				}
			}
			return ( (scope.chartSettings.dbAxisToggle && grap.id == 100) || (scope.chartSettings.dbAxisToggle && grap.id == 300) || !scope.chartSettings.dbAxisToggle ) &&
				(scope.dsConfig.editor.chart.showMap || grap.id !== 900) &&
				(scope.dsConfig.editor.chart.useNumberChart || (grap.id!=620 && grap.id!=720))
		};


		//数据初始化
		function dataInit() {
			// 时间粒度
			// chartSettings.selectedDatePeriod = scope.modal.editorNow.baseWidget.datePeriod || 'day';
			chartSettings.selectedDatePeriod = $translate.instant('COMMON.DATE_PERIOD.' + angular.uppercase(scope.modal.editorNow.baseWidget.datePeriod)) || $translate.instant('COMMON.DATE_PERIOD.DAY');

			//目标值
			chartSettings.modelTarget = scope.modal.editorNow.baseWidget.targetValue;
			chartSettings.showTimePeriod = scope.modal.editorNow.baseWidget.showTimePeriod == 1 ? true : false;
			chartSettings.showDataLabels = scope.modal.editorNow.chartSetting.showDataLabels == 1 ? true : false;

			// Display-Settings 初始化指标总量选项
			chartSettings.showMetricAmount = scope.modal.editorNow.baseWidget.showMetricAmount == '1' ? true : false;

			//chartSettings.showMetricAmount = scope.modal.editorNow.variables[0].metrics.length > 0 ? scope.modal.editorNow.baseWidget.showMetricAmount == '1' ? true : false : false;
			chartSettings.metricAmountItem = scope.modal.editorNow.variables[0].metrics || [];
			angular.forEach(chartSettings.metricAmountItem, function (metrics, i) {
				if (i == 0 || metrics.showMetricAmount) { // 默认设置第一个为默认值,如过有历史值设置历史值

					if (metrics.type == 'compoundMetrics' || scope.modal.editorNow.variables[0].ptoneDsInfoId != 1) {
						chartSettings.selectedMetricAmount = metrics.name;
					} else {
						chartSettings.selectedMetricAmount = $translate.instant(metrics.i18nCode);
					}

				}
			});


			if (scope.modal.editorNow.chartSetting === null) {
				scope.modal.editorNow.chartSetting = {
					"stackedChart": '0',
					"areaChart": '0',
					"showLegend": '',
					"showDataLabels": '0',
					"showMultiY": 0,
					"metricsToY": {},
					"xAxis": [{}],
					"yAxis": [{}, {}]
					// "xAxis": [{"enabled": false}],
					// "yAxis": [{"enabled": true, "chartType": "line", "areaChart": 0, "stackedChart": 0, "max": "", "min": ""},{"enabled": true, "max": "", "min": ""}],
				};

				//图形
				var defaultGraph = (scope.modal.editorNow.baseWidget.graphName == 'column' ? 'column' : 'line');
				chartSettings.dbGraph = [defaultGraph, defaultGraph];
				//堆叠图
				chartSettings.dbStacked = [{"stackedChart": false, "areaChart": false}, {
					"stackedChart": false,
					"areaChart": false
				}];
			} else {
				//双轴开关
				chartSettings.dbAxisToggle = scope.modal.editorNow.chartSetting.showMultiY == 1 ? true : false;
				chartSettings.dbMod = chartSettings.dbAxisToggle ? 'db' : '';

				//图形
				var defaultGraph = (scope.modal.editorNow.baseWidget.graphName == 'column' ? 'column' : 'line');
				if (scope.modal.editorNow.chartSetting.yAxis.length == 1) {
					scope.modal.editorNow.chartSetting.yAxis[0].chartType = scope.modal.editorNow.chartSetting.yAxis[0].chartType ? scope.modal.editorNow.chartSetting.yAxis[0].chartType : defaultGraph;

					chartSettings.dbGraph = [scope.modal.editorNow.chartSetting.yAxis[0].chartType, defaultGraph];
				} else if (scope.modal.editorNow.chartSetting.yAxis.length == 2) {
					scope.modal.editorNow.chartSetting.yAxis[0].chartType = scope.modal.editorNow.chartSetting.yAxis[0].chartType ? scope.modal.editorNow.chartSetting.yAxis[0].chartType : defaultGraph;
					scope.modal.editorNow.chartSetting.yAxis[1].chartType = scope.modal.editorNow.chartSetting.yAxis[1].chartType ? scope.modal.editorNow.chartSetting.yAxis[1].chartType : defaultGraph;

					chartSettings.dbGraph = [scope.modal.editorNow.chartSetting.yAxis[0].chartType, scope.modal.editorNow.chartSetting.yAxis[1].chartType];
				} else {
					chartSettings.dbGraph = [defaultGraph, defaultGraph];
				}

				//堆叠图
				chartSettings.stackedChart = scope.modal.editorNow.chartSetting.stackedChart == '0' ? false : true;
				chartSettings.areaChart = scope.modal.editorNow.chartSetting.areaChart == '0' ? false : true;
				if (scope.modal.editorNow.chartSetting.yAxis.length == 1) {
					scope.modal.editorNow.chartSetting.yAxis[0].stackedChart = scope.modal.editorNow.chartSetting.yAxis[0].stackedChart ? scope.modal.editorNow.chartSetting.yAxis[0].stackedChart : 0;
					scope.modal.editorNow.chartSetting.yAxis[0].areaChart = scope.modal.editorNow.chartSetting.yAxis[0].areaChart ? scope.modal.editorNow.chartSetting.yAxis[0].areaChart : 0;

					chartSettings.dbStacked = [{
						"stackedChart": scope.modal.editorNow.chartSetting.yAxis[0].stackedChart == '0' ? false : true,
						"areaChart": scope.modal.editorNow.chartSetting.yAxis[0].areaChart == '0' ? false : true
					}, {
						"stackedChart": chartSettings.stackedChart,
						"areaChart": chartSettings.areaChart
					}];
				} else if (scope.modal.editorNow.chartSetting.yAxis.length == 2) {
					scope.modal.editorNow.chartSetting.yAxis[0].stackedChart = scope.modal.editorNow.chartSetting.yAxis[0].stackedChart ? scope.modal.editorNow.chartSetting.yAxis[0].stackedChart : 0;
					scope.modal.editorNow.chartSetting.yAxis[0].areaChart = scope.modal.editorNow.chartSetting.yAxis[0].areaChart ? scope.modal.editorNow.chartSetting.yAxis[0].areaChart : 0;
					scope.modal.editorNow.chartSetting.yAxis[1].stackedChart = scope.modal.editorNow.chartSetting.yAxis[1].stackedChart ? scope.modal.editorNow.chartSetting.yAxis[1].stackedChart : 0;
					scope.modal.editorNow.chartSetting.yAxis[1].areaChart = scope.modal.editorNow.chartSetting.yAxis[1].areaChart ? scope.modal.editorNow.chartSetting.yAxis[1].areaChart : 0;

					chartSettings.dbStacked = [{
						"stackedChart": scope.modal.editorNow.chartSetting.yAxis[0].stackedChart == '0' ? false : true,
						"areaChart": scope.modal.editorNow.chartSetting.yAxis[0].areaChart == '0' ? false : true
					}, {
						"stackedChart": scope.modal.editorNow.chartSetting.yAxis[1].stackedChart == '0' ? false : true,
						"areaChart": scope.modal.editorNow.chartSetting.yAxis[1].areaChart == '0' ? false : true
					}];
				} else {
					chartSettings.dbStacked = [{
						"stackedChart": chartSettings.stackedChart,
						"areaChart": chartSettings.areaChart
					}, {
						"stackedChart": chartSettings.stackedChart,
						"areaChart": chartSettings.areaChart
					}];
				}

				//地图名称
				chartSettings.showMapName = scope.modal.editorNow.chartSetting.showMapName == '0' ? false : true;


				//判断是否为空
				if (scope.modal.editorNow.chartSetting.metricsToY.length == 0) {
					scope.modal.editorNow.chartSetting.metricsToY = {};
					//save
					scope.saveData('chart-metricsToY')
				}

				if (objectIsEmpty(scope.modal.editorNow.chartSetting.metricsToY)) {
					if (chartSettings.metricsList.length > 0) {
						scope.modal.editorNow.chartSetting.metricsToY = {};
						for (var i = chartSettings.metricsList.length - 1; i >= 0; i--) {
							var metricsKey = chartSettings.metricsList[i].code + "-" + chartSettings.metricsList[i].uuid;
							scope.modal.editorNow.chartSetting.metricsToY[metricsKey] = 0;
							scope.chartSettings.modelMetric[metricsKey] = true;
						}
					}
				} else {
					var j = 0;
					scope.chartSettings.modelMetrics = angular.copy(scope.modal.editorNow.chartSetting.metricsToY);
					for (var i in scope.chartSettings.modelMetrics) {
						scope.chartSettings.modelMetrics[i] = scope.chartSettings.modelMetrics[i] == 0 ? true : false;
					}
				}

				//指标
				chartSettings.metricsList = angular.fromJson(scope.modal.editorNow.variables[0].metrics);


				//Axis-Settings
				if (scope.modal.editorNow.chartSetting.yAxis.length > 0) {
					chartSettings.modelMin = scope.modal.editorNow.chartSetting.yAxis[0].min ? scope.modal.editorNow.chartSetting.yAxis[0].min : 'auto';
					chartSettings.modelMax = scope.modal.editorNow.chartSetting.yAxis[0].max ? scope.modal.editorNow.chartSetting.yAxis[0].max : 'auto';

					if (chartSettings.modelMin != 'auto' || chartSettings.modelMax != 'auto') {
						chartSettings.modelAxis = 'false';
					}
				}

				//Display-Settings
				chartSettings.xAxis = scope.modal.editorNow.chartSetting.xAxis.length > 0 ? scope.modal.editorNow.chartSetting.xAxis[0].enabled : false;
				chartSettings.yAxis = scope.modal.editorNow.chartSetting.yAxis.length > 0 ? scope.modal.editorNow.chartSetting.yAxis[0].enabled : false;
				// chartSettings.showLegend = scope.modal.editorNow.chartSetting.showLegend ? scope.modal.editorNow.chartSetting.showLegend : 0;
				if (!scope.modal.editorNow._ext) scope.modal.editorNow._ext = {};
				chartSettings.showLegend = scope.modal.editorNow._ext.widgetShowLegend || false;

				if (chartSettings.showTimePeriod || chartSettings.xAxis || chartSettings.yAxis || chartSettings.showLegend) {
					chartSettings.modelDisplay = 'false';
				}

				//
				chartSettings.hideDetail = scope.modal.editorNow.chartSetting.hideDetail == 1 ? true : false;
				chartSettings.reverseTarget = scope.modal.editorNow.chartSetting.reverseTarget == 1 ? true : false;
			}
		}


		/*************************
		 * wgtCreate             *
		 * 创建全新的小widget      *
		 *************************/
		scope.wgtCreate = function (type, parentId) {
			var widgetId = uuid();
			var variableId = uuid();
			//line类型对应的是100，column对应300，bar对应400，pie对应500，number对应620，progressbar对应720，table对应800，map对应900
			var ptoneGraphInfoId;
			switch (type) {
				case 'line':
					ptoneGraphInfoId = 100;
					break;
				case 'column':
					ptoneGraphInfoId = 300;
					break;
				case 'bar':
					ptoneGraphInfoId = 400;
					break;
				case 'pie':
					ptoneGraphInfoId = 500;
					break;
				case 'number':
					ptoneGraphInfoId = 620;
					break;
				case 'progressbar':
					ptoneGraphInfoId = 720;
					break;
				case 'table':
					ptoneGraphInfoId = 800;
					break;
				case 'map':
					ptoneGraphInfoId = 900;
					break;
				default:
					ptoneGraphInfoId = 800;
					break;
			}
			var tempDateKey = scope.rootUser.userSelected === null ? 'last_week' : scope.rootUser.userSelected.dateKey;

			var sendWidget = {
				"panelId": scope.rootPanel.nowId,
				"baseWidget": {
					"widgetId": widgetId,
					"parentId": parentId,
					"spaceId": scope.rootSpace.current.spaceId,
					"widgetTitle": null,
					"isTitleUpdate": '0',
					"creatorId": $rootScope.userInfo.ptId,
					"ownerId": $rootScope.userInfo.ptId,
					"modifierId": $rootScope.userInfo.ptId,
					"dateKey": null,
					"datePeriod": 'day',
					"refreshInterval": null,
					"createTime": parseInt(new Date().getTime()),
					"modifyTime": parseInt(new Date().getTime()),
					"targetValue": null,
					"byTemplate": 0, //0-新增。1-使用模板生成
					"isTemplate": 0, //0：非，1：是
					"isExample": 0, // 是否展示demo数据，0：非，1：是
					"status": 1, // 默认状态为有效
					"widgetType": type == 'tool' ? type : 'chart',
					"ptoneGraphInfoId": ptoneGraphInfoId,
					"graphName": type == 'tool' ? 'text' : type,
                    "mapCode": ProductConfig.defaultMapCode,
					"showTimePeriod": '0',
					"showMetricAmount": '1'
				},
				"variables": [
					{
						"variableId": variableId,
						"ptoneDsInfoId": 0,
						"dsCode": null,
						"variableGraphId": ptoneGraphInfoId,
						"variableColor": null,
						"connectionId": null,
						"accountName": null,
						"profileId": null,
						"dimensions": [],
						"ignoreNullDimension": 0,//是否展示没有数据的项，默认不展示，1表示展示，0表示不展示
						"dateDimensionId": "",//
						"metrics": [],
						"sort": null
					}
				],
				"chartSetting": {
					"stackedChart": '0',
					"areaChart": '0',
					"showLegend": '',
					"showDataLabels": '0',
					"showMapName": '0',
					"showMultiY": '0',
					"hideDetail": '0',
					"metricsToY": {},
					"xAxis": [{"enabled": true}],
					"yAxis": [{"enabled": true}, {"enabled": true}]
				},
				"_ext": {}, // 扩展字段，用于前端临时数据存储，不持久化到库中
				"children": [],
				"layout": null
			};

			if (type && type == 'tool') {
				sendWidget.toolData = {
					widgetId: widgetId,
					value: '',
					extend: ''
				};
				scope.$broadcast('createWidgetOfTool');
			}

			//管理员添加时，标题会自动国际化
			//if ($rootScope.userInfo.access.indexOf('1') != -1) {
			if (sysRoles.hasSysRole("ptone-admin-user")) {
				sendWidget.baseWidget.widgetTitle =
				{
					"zh_CN": 'Widget 标题',
					"en_US": 'Widget Title',
					"ja_JP": 'ウィジェット名'
				};
			} else {
				sendWidget.baseWidget.widgetTitle = $translate.instant("WIDGET.WIDGET_DEFAULT_NAME");
			}


			//在新增操作(create&template),先判是否存在数据账户与时间信息
			if (scope.rootUser.profileSelected && !objectIsEmpty(scope.rootUser.profileSelected)) {
				var dsId = scope.rootUser.profileSelected.dsId,
					dsCode = scope.rootUser.profileSelected.dsCode,
					accountName = scope.rootUser.profileSelected.accountName,
					profileId = scope.rootUser.profileSelected.prfileId,
					connectionId = scope.rootUser.profileSelected.connectionId;

				sendWidget.variables[0].ptoneDsInfoId = dsId;
				sendWidget.variables[0].dsCode = dsCode;
				sendWidget.variables[0].accountName = accountName;
				sendWidget.variables[0].profileId = profileId;
				sendWidget.variables[0].connectionId = connectionId;
			}
			if (scope.rootUser.userSelected) {
				sendWidget.baseWidget.dateKey = scope.rootUser.userSelected.dateKey;
			}

			//当已存数据源但没有时间时,走默认时间值
			if (sendWidget.variables[0].ptoneDsInfoId && sendWidget.baseWidget.dateKey == null) {
				if ([1, 3, 5, 7, 8, 9, 10].indexOf(+sendWidget.variables[0].ptoneDsInfoId) >= 0) {
					//除gd外默认选中过去7天不包含今天
					sendWidget.baseWidget.dateKey = 'past7day';
				} else if (dsId == 6) {//gd默认选中all time
					sendWidget.baseWidget.dateKey = 'all_time';
				}
			}
			//自定义widget需要先保存到父级
			scope.modal.editorNow.children.push(sendWidget);
			var childrenLength = scope.modal.editorNow.children.length;
			/*
			 初始化自定义widget的位置信息，在widget的中间
			 */
			var widgetNow = $('[data-widget-id=' + scope.modal.editorNow.baseWidget.widgetId + ']'),
				widgetNowWidth = widgetNow.width(),
				widgetNowHeight = widgetNow.height(),
				left = (widgetNowWidth / 2 - 50),
				top = (widgetNowHeight / 2 - 50);
			sendWidget.layout = {
				'width': 100,
				'height': 100,
				'left': left,
				'top': top,
				'z-index': childrenLength,
				'widgetNowWidth': widgetNowWidth,
				'widgetNowHeight': widgetNowHeight
			};

			//保存小widget
			dataMutualSrv.post(LINK_WIDGET_ADD, angular.copy(sendWidget), 'wgtSave').then(function (data) {
				if (data.status == 'success') {
					var sizeX = 12;
					var minx = 6;
					var sizeY = 8;
					var miny = 8;
					sendWidget.sizeX = sizeX;
					sendWidget.sizeY = sizeY;
					sendWidget.minSizeX = minx;
					sendWidget.minSizeY = miny;

					if (sendWidget.baseWidget.widgetType == 'tool' && sendWidget.baseWidget.graphName == 'text') {
						sendWidget.minSizeX = 3;
						sendWidget.minSizeY = 2;
						//sendWidget.baseWidget.widgetEdit = true;
					}

					//更新已存widget列表,减少请求次数
					// scope.rootCommon.dashboardList[scope.rootPanel.nowId] = angular.copy(scope.rootWidget.list);

					//全站事件统计
					siteEventAnalyticsSrv.createData({
						uid: $rootScope.userInfo.ptId,
						time: new Date().getTime(),
						position: 'widget',
						operate: 'add-widget-btn',
						operateId: uuid(),
						content: JSON.stringify(sendWidget)
					});

				} else if (data.status == 'failed') {
					console.log('Post Data Failed!')
				} else if (data.status == 'error') {
					console.log('Post Data Error: ');
					console.log(data.message)
				}
			});
		};

	}
}

export default editorChart;
