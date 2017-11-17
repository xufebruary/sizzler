
import dataController from './data.controller';
import editorDataTpl from './data.tpl.html';
import './data.css';

import {
	LINK_DEL_CALCULATE_VALUE,
	LINK_GET_USE_CALCULATE_VALUE,
	LINK_LIST_CALCULATE_VALUE,
	LINK_SELECTED_METRICS,
	LINK_METRICS_GA,
	LINK_DIMENSIONS_GA,
	LINK_SALESFORCE_METRICES_AND_DIMENSION,
	LINK_APP_GET_USER_VAR_KEY_NAME,
	LINK_USER_DIMENSIONS,
	LINK_USER_METRICS,
	uuid
}from 'components/modules/common/common';

// jquery scrollbar 插件引入
// require("jquery-mousewheel")($);
require('assets/libs/jquery/custom-content-scroller/jquery.mCustomScrollbar')($);
require('assets/libs/jquery/custom-content-scroller/jquery.mCustomScrollbar.css');

import Sortable from 'assets/libs/sortable/Sortable';

editorData.$inject = ['$localStorage', '$document', '$translate', '$timeout', 'dataMutualSrv', 'uiSearch', 'sysRoles', 'uiLoadingSrv', 'siteEventAnalyticsSrv','$rootScope', 'Track'];
function editorData($localStorage, $document, $translate, $timeout, dataMutualSrv, uiSearch, sysRoles, uiLoadingSrv, siteEventAnalyticsSrv,$rootScope, Track) {
	return {
		restrict: 'EA',
		replace: true,
		template: editorDataTpl,
		controller: dataController,
		bindToController: true,
		link: link
	};

	function link(scope, elem, attrs) {
		var body = $document.find('body').eq(0);

		//监听气泡显示状态
		// scope.$watch('editor.pop.name', function(value) {
		//     if (value && value == 'data') {
		//         $document.bind('click', documentClickBindData);
		//     } else {
		//         $document.unbind('click', documentClickBindData);
		//     }
		// });
		var documentClickBindData = function (event) {
			if (scope.editor.documentClick[1]) {
				scope.editor.documentClick[1] = false;
			} else if (scope.dataSettings.excludeClick) {
				scope.dataSettings.excludeClick = false;
			} else if (scope.editor.pop.show && typeof(angular.element(event.target).attr('step-data')) == 'undefined' && !elem[0].contains(event.target)) {
				scope.$apply(function () {
					scope.editor.pop.name = null;
					scope.editor.pop.show = false;
				});
			}
		};


		scope.dataSettings = {
			//指标
			metricsCode: null,
			metricsSearch: false,
			metricsSearchKey: null,
			metricsList: null,
			tempMetricsList: null,
			metricsListIndex: [], //二级展示开关
			scrollBarToBottom: false, //控制滚动条是否需要滚动至底部

			//计算指标
			showCalculatedValue: false, //是否显示计算指标添加按钮
			calculatedValueType: null, //add || edit
			calculatedValueCurrent: null, //当前编辑具体信息
			calculatedValueList: [],
			tempCalculatedValueList: null,
			calculatedValueNum: null,   //与此指标有关的widget个数

			//维度
			dimensionsCode: null,
			dimensionsSearch: false,
			dimensionsSearchKey: null,
			dimensionsList: null,
			tempDimensionsList: null,
			dimensionsListIndex: [], //二级展示开关

			//自定义指标名称
			dataAlias: {},

			//过滤器
			filterType: null,
			currentFilter: null, //当前编辑或现在的指标信息
			filterScope: null,   //修改范围: filters || segment

			//sort & max
			currentDimensionType: null, //sort || max
			currentDimension: null,
			currentDimensionIndex: null,

			//other
			excludeClick: false,    //排除删除及新增操作时的dom关闭事件

			metricsSearchFocus: false,
			dimensionsSearchFocus: false,

			dimensionsVersions: 1217001,
			metricsVersions: 1217001,

			countSelectBox: [],//计算方法下拉菜单，默认不可见
			attributeSelectBox: [],//attribute 下拉菜单，默认不可见
			countType: [ //计算方式类型及其解释
				{
					name: 'SUM',
					description: $translate.instant('WIDGET.EDITOR.DATA.COUNT.SUM'),//$translate.instant('WIDGET.EDITOR.FILTER.SHOW')
					link: '?data-elevio-article=42228'
				},
				{
					name: 'AVERAGE',
					description: $translate.instant('WIDGET.EDITOR.DATA.COUNT.AVERAGE'),
					link: '?data-elevio-article=42229'
				},
				{
					name: 'MAX',
					description: $translate.instant('WIDGET.EDITOR.DATA.COUNT.MAX'),
					link: '?data-elevio-article=42230'
				},
				{
					name: 'MIN',
					description: $translate.instant('WIDGET.EDITOR.DATA.COUNT.MIN'),
					link: '?data-elevio-article=42231'
				},
				//{
				//    name:'MEDIAN',
				//    description:'返回数值数据集中的中值',
				//    link:'42232'
				//},
				{
					name: 'COUNTA',
					description: $translate.instant('WIDGET.EDITOR.DATA.COUNT.COUNT'),
					link: '?data-elevio-article=42233'
				},
				{
					name: 'COUNTUNIQUE',
					description: $translate.instant('WIDGET.EDITOR.DATA.COUNT.COUNTUNIQUE'),
					link: '?data-elevio-article=42234'
				},
				{
					name: 'STDEV',
					description: $translate.instant('WIDGET.EDITOR.DATA.COUNT.STDEV'),
					link: '?data-elevio-article=42235'
				},
				{
					name: 'VAR',
					description: $translate.instant('WIDGET.EDITOR.DATA.COUNT.VAR'),
					link: '?data-elevio-article=42236'
				}
			],

			//提示框
			tips: {
				show: false,
				options: {}
			},

			//数据加载状态
			loadFinish: {
				metricsList: false,
				calculatedValueList: false,
				dimensionsList: false,
				failed: false //请求数据失败
			},
			metricsOfSalesforceName: null //salesforce 数据源的 report类型中的name： Summary | Matrix | Tabular

		};

		//监听父级是否有切换widget，如果有，需要重新初始化data信息
		scope.$on('changeWidgetEditor', function (e, newLocation) {
			//数据初始化
			dataInit();
			//同时关闭指标或者维度的下拉列表
			scope.dataSettings.metricsSearch = false;
			scope.dataSettings.dimensionsSearch = false;
		});

		/**
		 * 选择计算方式
		 * @param metrics 所选指标
		 * @param name 所选计算方式
		 * @param $index 所选指标$index
		 */
		scope.changeCalculateType = function (metrics, name, $index) {
			metrics.calculateType = name;
			metrics.name = name + '(' + metrics.realName + ')';
			scope.dataSettings.countSelectBox[$index] = false;//选完计算方式后要关闭下拉菜单

			//标题自动命名（isTitleUpdate意思是用户是否自己修改过名字，access意思是是否为管理员）
			//if(scope.modal.editorNow.baseWidget.isTitleUpdate == '0' && $rootScope.userInfo.access != 1){
			if (scope.modal.editorNow.baseWidget.isTitleUpdate == '0' && !sysRoles.hasSysRole("ptone-admin-user")) {
				scope.autoSetWidgetTitle();
			}
			//save
			scope.saveData('data-changeCalculateType');
		};

		/**
		 * 显示计算方式下拉菜单
		 * @param $index
		 * @param type  value 或者 attribute
		 */
		scope.closeOtherCountBox = function ($index, type) {
			if (type === 'value') {
				$.each(scope.dataSettings.countSelectBox, function (i) {
					if (i !== $index) {
						scope.dataSettings.countSelectBox[i] = false;
					}
				});
				$.each(scope.dataSettings.attributeSelectBox, function (i) {
					scope.dataSettings.attributeSelectBox[i] = false;
				});
			} else if (type === 'attribute') {
				$.each(scope.dataSettings.attributeSelectBox, function (i) {
					if (i !== $index) {
						scope.dataSettings.attributeSelectBox[i] = false;
					}
				});
				$.each(scope.dataSettings.countSelectBox, function (i) {
					scope.dataSettings.countSelectBox[i] = false;
				});
			} else {
				$.each(scope.dataSettings.countSelectBox, function (i) {
					scope.dataSettings.countSelectBox[i] = false;
				});
				$.each(scope.dataSettings.attributeSelectBox, function (i) {
					scope.dataSettings.attributeSelectBox[i] = false;
				})
			}
		};

		scope.goToCountHelp = function (e) {
			//e.preventDefault();
			e.stopPropagation();
		};


		// 每次选择、删除指标后，生成 metrcisToY
		scope.buildMetricsToY = function () {
			var selectedMetrics = scope.modal.editorNow.variables[0].metrics || [];
			var oldMetricsToY = scope.modal.editorNow.chartSetting.metricsToY || {};
			var newMetricsToY = {};
			angular.forEach(selectedMetrics, function (m, index) {
				var metricsKey = m.code + "-" + m.uuid;
				newMetricsToY[metricsKey] = oldMetricsToY[metricsKey] == 1 ? 1 : 0;
			});
			scope.modal.editorNow.chartSetting.metricsToY = newMetricsToY;
		};

		//删除操作
		scope.deletedData = function (index, type) {
			var deleteId;
			if (type == 'metrics') {
				deleteId = scope.modal.editorNow.variables[0].metrics[index].id;
				scope.modal.editorNow.variables[0].metrics.splice(index, 1);

				scope.buildMetricsToY();

			} else if (type == 'dimensions') {
				deleteId = scope.modal.editorNow.variables[0].dimensions[index].id;
				scope.modal.editorNow.variables[0].dimensions.splice(index, 1);
			}

			if (scope.modal.editorNow.variables[0].metrics.length <= 0 && scope.modal.editorNow.variables[0].dimensions.length <= 0) {
				scope.editor.disabled[2] = true;
				scope.editor.disabled[3] = true;
			}

			//屏蔽此次点击事件的判断
			scope.dataSettings.excludeClick = true;

			//标题自动命名
			if (scope.modal.editorNow.baseWidget.isTitleUpdate == '0' && !sysRoles.hasSysRole("ptone-admin-user")) {
				scope.autoSetWidgetTitle();
			}

			//save
			scope.saveData('data-deletedData');

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'del_' + type);

			//全站事件统计
			Track.log({where: 'widget_editor_metrics', what: 'delete_' + type, value: deleteId});

		};


		//新增操作
		scope.addData = function (data, type) {
			var config = scope.dsConfig.editor.data;
			var fun = '';//计算方式,根据选择的数据类型不同，默认不同的计算方式
			switch (data.dataType) {
				case 'STRING':
					fun = 'COUNTA';
					break;
				case 'BOOLEAN':
					fun = 'COUNTA';
					break;
				case 'TIME':
					fun = 'COUNTA';
					break;
				case 'DATE':
					fun = 'COUNTA';
					break;
				case 'DATETIME':
					fun = 'COUNTA';
					break;
				case 'TIMESTAMP':
					fun = 'COUNTA';
					break;
				case 'NUMBER':
					fun = 'SUM';
					break;
				case 'CURRENCY':
					fun = 'SUM';
					break;
				case 'PERCENT':
					fun = 'AVERAGE';
					break;
				case 'DURATION':
					fun = 'AVERAGE';
					break;
				default:
					fun = 'SUM';
					break;
			}
			var variables = scope.modal.editorNow.variables[0];
			if (type == 'metrics' || type == 'compoundMetrics') {

				var formatName;
				if (config.metricsHasCount) {//首先判断此指标维度是否可以使用计算函数
					formatName = data.isContainsFunc == 1 ? data.name : fun + '(' + data.name + ')';
				} else {
					formatName = type == 'compoundMetrics' ? data.name : config.metricsNeedI18n ? $translate.instant(data.i18nCode) : data.name;
				}

				scope.dataSettings.metricsSearch = false;
				var metricsData = {
					"id": data.id,
					"code": data.code,
					"formula": data.formula,
					"categoryCode": data.categoryCode,
					"name": formatName,
					"i18nCode": data.i18nCode,
					"uuid": uuid(),
					"showMetricAmount": false,
					'segment': null,
					'sort': null,
					'max': null,
					'showOthers': null,
					'type': data.type,
					'dataType': data.dataType,
					'calculateType': config.metricsHasCount ? fun : '',
					'dataFormat': data.dataFormat,
					'realName': !config.metricsHasCount && type != 'compoundMetrics' ? config.metricsNeedI18n ? $translate.instant(data.i18nCode) : data.name : data.name,
					'unit': data.unit,
					'isValidate': data.isValidate,
					'isContainsFunc': data.isContainsFunc
				};
				if('description' in data){//salseforce 数据源有一个description字段
					metricsData.description = data.description;
				}
				variables.metrics.push(metricsData);

				if (!config.metricsHasCount) {
					scope.editor.disabled[2] = false;
					scope.editor.disabled[3] = false;
				} else {
					scope.editor.disabled[3] = false;
				}


				scope.buildMetricsToY();
			} else if (type == 'dimensions') {

				var dimensionsData = {
					"id": data.id,
					"code": data.code,
					"categoryCode": data.categoryCode,
					// "name": data.name,
					"name": !config.dimensionsHasGroupBy ? config.metricsNeedI18n ? $translate.instant(data.i18nCode) : data.name : data.name,
					"i18nCode": data.i18nCode,
					"uuid": uuid(),
					'sort': null,
					'max': null,
					'showOthers': null,
					'dataType': data.dataType,
					'calculateType': fun,
					'dataFormat': data.dataFormat,
					'realName': !config.dimensionsHasGroupBy ?config.metricsNeedI18n ? $translate.instant(data.i18nCode) : data.name : data.name,
					'datePeriod': data.dataType == 'TIMESTAMP' || data.dataType == 'DATETIME' ? 'day' : scope.groupBy(data.dataFormat),
					'isDefaultSelect': data.isDefaultSelect//是否默认选中
				};
				if('description' in data){//salseforce 数据源有一个description字段
					dimensionsData.description = data.description;
				}
				variables.dimensions.push(dimensionsData);
				scope.dataSettings.dimensionsSearch = false;
			}

			scope.editor.disabled[3] = false;//只要存在一个维度或者一个指标就可以选择图表类型

			//屏蔽此次点击事件的判断
			scope.dataSettings.excludeClick = true;
			scope.dataSettings.dimensionsSearchFocus = false;
			scope.dataSettings.metricsSearchFocus = false;

			//标题自动命名
			if (scope.modal.editorNow.baseWidget.isTitleUpdate == '0') {
				scope.autoSetWidgetTitle();
			}

			//在添加指标、维度时，验证当前数据源是否需要重新加载左侧控件的状态，如果需要则通过广播通知
			if (scope.dsConfig.editor.source.isReinitTimeByProfile) {
				scope.$emit('changeCustomWidget');
			}

			//选择完指标以后，首先判断，数据源是否有日期类型，如果没有，时间选择不可点击
			if(scope.dsConfig.editor.time.isCheckDMHasTime){
				var dimensionsUrl;
				if(variables.ptoneDsInfoId == 19){
					dimensionsUrl = LINK_SALESFORCE_METRICES_AND_DIMENSION + '/' + variables.profileId + '/' + variables.connectionId+ '/' + variables.accountName + '/false';
					scope.editor.disabled[2] = (variables.profileId + '').indexOf('|join|') > -1;//salesforce数据源，Report类型，时间不可点
				}else if(scope.modal.editorNow.variables[0].ptoneDsInfoId == 27){//MailChimp接口
					dimensionsUrl = LINK_DIMENSIONS_GA + scope.modal.editorNow.variables[0].ptoneDsInfoId;
				}else{
					dimensionsUrl = LINK_USER_DIMENSIONS + variables.ptoneDsInfoId + '/' + variables.profileId;
				}
				dataMutualSrv.get(dimensionsUrl).then(function (data) {
					if (data.status == 'success') {
						if(variables.ptoneDsInfoId == 19){
							if((variables.profileId + '').indexOf('|join|') === -1){//salesforce数据源，Report类型，时间不可点
								if(data.content && data.content.length > 0){
									variables.dateDimensionId =  data.content[0].metricsList[0].code + '|join|' + data.content[0].metricsList[0].dataType;
								}
							}
						} else if(scope.modal.editorNow.variables[0].ptoneDsInfoId == 27){
							if(data.content && data.content.length){
								var mailChimpDimention;
								data.content.forEach(function(item){
									if(item.id == scope.modal.editorNow.variables[0].profileId){
										mailChimpDimention = item.dimensionList;
									}
								});

								if(mailChimpDimention && mailChimpDimention.length){
									var flag = false;
									for(var i = 0; i < mailChimpDimention.length; i++){
										if(mailChimpDimention[i].isShowOnTimeDropdowns === 1 && (mailChimpDimention[i].dataType == 'DATE' || mailChimpDimention[i].dataType == 'TIMESTAMP' || mailChimpDimention[i].dataType == 'DATETIME')){
											scope.editor.disabled[2] = false;
											if(!variables.dateDimensionId){
												flag = true;
												variables.dateDimensionId = mailChimpDimention[i].code;
												break;
											} else if (variables.dateDimensionId == mailChimpDimention[i].code) {
												flag = true;
												break;
											}
										}
									}
									if(!flag){
										variables.dateDimensionId = '';
									}
								}
							}
					} else{
							var flag = false;
							for (var i = 0; i < data.content.length; i++) {
								if (data.content[i].dataType == 'DATE' || data.content[i].dataType == 'DATETIME' || data.content[i].dataType == 'TIMESTAMP') {
									scope.editor.disabled[2] = false;
									if (!variables.dateDimensionId) {
										flag = true;
										variables.dateDimensionId = data.content[i].id;
										break;
									} else if (variables.dateDimensionId == data.content[i].id) {
										flag = true;
										break;
									}
								}
							}

							if (!flag) {
								variables.dateDimensionId = '';
							}
						}

						//save
						scope.saveData('data-addData');
					}
				});
			}else{
				scope.editor.disabled[2] = false;

				//save
				scope.saveData('data-addData');
			}


			if(type == 'compoundMetrics'){
				//GTM
				siteEventAnalyticsSrv.setGtmEvent('click_element','widget_metric','widget_metric.value.select.'+formatName);
			}

			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where: 'widget_editor_metrics',
				what: 'select_' + type + '_value',
				how: 'click',
				value: data.id
			});
		};

		//指标维度下拉开关
		scope.switchDownOrUp = function (type) {
			if (type == 'metrics') {
				scope.dataSettings.dimensionsSearch = false;
				scope.dataSettings.dimensionsSearchFocus = false;
				// scope.dataSettings.metricsSearch = !scope.dataSettings.metricsSearch;
				scope.dataSettings.metricsSearchFocus = !scope.dataSettings.metricsSearchFocus;
				scope.dataSettings.metricsSearchKey = '';
				scope.restMetricsList(scope.dataSettings.tempMetricsList);
				scope.restCalculatedValueList(scope.dataSettings.tempCalculatedValueList);
				scope.collapseMetricsBackFunc(false);
			} else {
				scope.dataSettings.metricsSearch = false;
				scope.dataSettings.metricsSearchFocus = false;
				// scope.dataSettings.dimensionsSearch = !scope.dataSettings.dimensionsSearch;
				scope.dataSettings.dimensionsSearchFocus = !scope.dataSettings.dimensionsSearchFocus;
				scope.dataSettings.dimensionsSearchKey = '';
				scope.restDimensionsList(scope.dataSettings.tempDimensionsList);
				scope.collapseDimensionsBackFunc(false);
			}
			scope.closeOtherCountBox(0, 'all');

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'add_' + type);
		};

		/**
		 * 根据维度（也就是dimensions的dataFormat）来返回默认的group by
		 * @param dataFormat
		 * @returns {string}
		 */
		scope.groupBy = function (dataFormat) {
			if (dataFormat) {
				if (dataFormat.indexOf('s') > -1 || dataFormat.indexOf('S') > -1) {
					return 'seconds';
				} else if (dataFormat.indexOf('m') > -1) {
					return 'minute';
				} else if (dataFormat.indexOf('h') > -1 || dataFormat.indexOf('H') > -1) {
					return 'hour';
				} else if (dataFormat.indexOf('d') > -1 || dataFormat.indexOf('D') > -1) {
					return 'day';
				} else if (dataFormat.indexOf('M') > -1) {
					return 'month';
				} else if (dataFormat.indexOf('y') > -1 || dataFormat.indexOf('Y') > -1) {
					return 'year';
				}
			}
			return '';
		};

		/**
		 * 改版维度的group by
		 * @param dimension
		 * @param type
		 */
		scope.changeDimensionsGroupBy = function (dimension, type) {
			var dimensions = scope.modal.editorNow.variables[0].dimensions;
			if (dimensions.length > 0) {
				for (var i = 0; i < dimensions.length; i++) {
					if (dimensions[i].uuid == dimension.uuid) {
						dimensions[i].datePeriod = type
					}
				}
			}
			//save
			scope.saveData('data-changeDimensionsGroupBy');
		};

		//过滤器
		scope.toggleFilter = function (type) {
			//loading
			uiLoadingSrv.createLoading(angular.element('.editor-data'));

			scope.editor.filtersType = type;//设置过滤器类别 filter/segment

			$timeout(function () {
				if (scope.modal.editorNow.variables[0][type]) {
					scope.dataSettings.filterType = 'edit';
				} else {
					scope.dataSettings.filterType = 'add';
				}

				scope.editor.filterShow = true;
				scope.dataSettings.filterScope = type;
				scope.dataSettings.currentFilter = angular.copy(scope.modal.editorNow.variables[0][type]);

				//GTM
				siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'click_filter');
			}, 200);

			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where: 'widget_editor_metrics',
				what: 'filter_btn_' + type,
				how: 'click'
			});
		};

		//过滤器操作
		scope.filter = function (metrics, index) {
			if (metrics.segment !== null) {
				scope.dataSettings.filterType = 'edit';
			} else {
				scope.dataSettings.filterType = 'add';
			}

			scope.editor.filterShow = true;
			scope.dataSettings.metricsNow = metrics;
			scope.dataSettings.metricsNowIndex = index;

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'click_filter');
		};

		//排序操作
		scope.dimensionOperation = function (dimension, index, type) {
			scope.editor.dimensionOperation = true;
			scope.dataSettings.currentDimensionType = type;
			scope.dataSettings.currentDimension = dimension;
			scope.dataSettings.currentDimensionIndex = index;
			scope.dataSettings.defaultSorts = null;
			// 设置sort
			if(type == 'sort'){
				var sort = dimension.sort;
				if(sort != null){
					scope.dataSettings.defaultSorts = [{
						type: sort.type,
						order: sort.sortOrder,
						id: sort.sortColumn || dimension.uuid
					}];
				}
			}

			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where: 'widget_editor_metrics',
				what: type + '_dimension',
				how: 'click',
				value: dimension.id
			});
		};

		//返回该按钮状态(max & sort)
		scope.getBtnDisabled = function (index, type) {
			var graphName = scope.modal.editorNow.baseWidget.graphName;
			var flag = false;
			var config = scope.dsConfig.editor.data;

			if (graphName == 'pie' && index != 0) {
				//饼图时,只有一个维度

				flag = true;
			} else if (['column', 'bar'].indexOf(graphName) >= 0 && scope.modal.editorNow.variables[0].metrics.length >= 2 && index != 0) {
				//柱线图时,当指标超过1个时,只有一个维度

				flag = true;
			} else if (config.autoAddTimeDimension && graphName == 'line') {
				// 当数据源为GA且图形为线图时, 排序不可用,max只在第一个维度生效

				if (type == 'sort') {
					flag = true;
				} else if (type == 'max' && index != 0) {
					flag = true;
				}
			} else {
				if (['pie', 'line', 'column', 'bar'].indexOf(graphName) >= 0) {
					if (type == 'sort' && index != 0) {
						flag = true;
					} else if (type == 'max' && [0, 1].indexOf(index) < 0) {
						flag = true;
					}
				} else {
					flag = true;
				}
			}

			return flag;
		};

		/**
		 * 目前用于salesforce的指标维度只显示profileId对应的
		 */
		function cleanContent(content) {
			if (scope.dsConfig.editor.data.showWithProfileId && scope.modal.editorNow.variables[0].profileId) {
				var temContent = [];
				for (var i = 0; i < content.length; i++) {
					if (scope.modal.editorNow.variables[0].profileId == content[i].id) {
						//如果是一层的指标维度，则需要单独取出指标维度作为列表
						if (scope.dsConfig.editor.data.metricsOneLayer && content[i].type == "metrics") {
							//指标metricsList
							var _metricsList = content[i].metricsList;
							for (var i = 0; i < _metricsList.length; i++) {
								temContent.push(_metricsList[i]);
							}
						} else if (scope.dsConfig.editor.data.dimensionsOneLayer && content[i].type == "dimension") {
							//维度dimensionList
							var _dimensionList = content[i].dimensionList;
							for (var i = 0; i < _dimensionList.length; i++) {
								temContent.push(_dimensionList[i]);
							}
						} else {
							temContent.push(content[i]);
						}
						break;
					}
				}
				return temContent;
			} else {
				return content;
			}
		}


		//搜索框
		scope.restMetricsList = function (data) {
			scope.dataSettings.metricsList = data;
		};
		scope.restDimensionsList = function (data) {
			scope.dataSettings.dimensionsList = data;
		};
		scope.restCalculatedValueList = function (data) {
			scope.dataSettings.calculatedValueList = data;
		};
		scope.condisitionExpansionIndex = {};//控制子展开与否
		//维度搜索回调
		scope.collapseDimensionsBackFunc = function (flag) {
			if (scope.dataSettings.dimensionsListIndex) {
				$.each(scope.dataSettings.dimensionsListIndex, function (i, item) {
					scope.dataSettings.dimensionsListIndex[i] = flag;
				});
			}
		};
		//指标搜索回调
		scope.collapseMetricsBackFunc = function (flag) {
			if (scope.dataSettings.metricsListIndex) {
				$.each(scope.dataSettings.metricsListIndex, function (i, item) {
					scope.dataSettings.metricsListIndex[i] = flag;
				});
			}
		};
		var broadcastKey;
		scope.broadcast = function () {
			scope.$broadcast('triggerHighWord', broadcastKey);
		};

		scope.searchContent = function (key, tierData, temp, fun, field) {
			// 当图形为line时，隐藏time维度
			if (field == 'dimensionList' && scope.modal.editorNow.baseWidget.graphName == 'line') {
				for (var i = scope.dataSettings.dimensionsList.length - 1; i >= 0; i--) {
					if (scope.dataSettings.dimensionsList[i].id == 22) {
						scope.dataSettings.dimensionsList.splice(i, 1);
						break;
					}
				}
			}
			var config = scope.dsConfig.editor.data;
			if (config.metricsTwoLayer || config.dimensionsTwoLayer) {
				uiSearch.search(key, 2, tierData, temp, fun, (field == 'dimensionList' ? scope.collapseDimensionsBackFunc : scope.collapseMetricsBackFunc));
			} else {
				broadcastKey = key;
				uiSearch.search(key, 1, tierData, temp, fun, scope.broadcast);
			}

			//搜索计算指标
			if (scope.dataSettings.tempCalculatedValueList.length > 0) {
				broadcastKey = key;
				uiSearch.search(key, 1, tierData, scope.dataSettings.tempCalculatedValueList, scope.restCalculatedValueList, scope.broadcast);
			}
		};

		var getActiveIndex = function (id) {
			var i = -1;
			$.each($('#' + id).find('a.block'), function (index, item) {
				if ($(this).hasClass('active')) {
					i = index;
				}
			});
			return i;
		};

		var scrollBar = function (id, type) {
			var top = $('#' + id).find('a.active').position().top;
			if (top > 170 && type == 'down') {
				$('#' + id).parent().slimScroll({scrollBy: '174px'});
			}
			if (top < 10 && type == 'up') {
				$('#' + id).parent().slimScroll({scrollBy: '-174px'});
			}
		};

		var movePrev = function (id) {
			var index = getActiveIndex(id);
			if (index < 1) {
				return false;
			}
			index = index == 0 ? index : index - 1;
			$('#' + id).find('a.block').removeClass('active').eq(index).addClass('active');
		};
		var moveNext = function (id) {
			var index = getActiveIndex(id) + 1;
			if (index == $('#' + id).find('a.block').length) {
				return false;
			}
			else {
				$('#' + id).find('a.block').removeClass('active').eq(index).addClass('active');
			}
		};

		/**
		 * 列表循环完毕调取滚动条初始化事件
		 */
		scope.listRepeatFinish = function(domId){
			if(domId == 'calculatedValueList'){
				if(scope.dataSettings.scrollBarToBottom){
					angular.element('#metricsListWrap').mCustomScrollbar("scrollTo","bottom");
					scope.dataSettings.scrollBarToBottom = false;
				}
			} else {
				angular.element('#'+domId).mCustomScrollbar({
					setHeight:'156px',
					theme: "minimal-dark",
					scrollInertia: 0,
					autoDraggerLength: false,
					mouseWheel:{
						preventDefault: true
					}
				});
			}
		};

		/**
		 * 校验所选Metrics列表中是否含有不生效的数据
		 * 主要针对 计算指标
		 * 应用场景: 1.进入widget编辑器. 2.编辑或删除计算指标
		 *
		 * 需根据前端指标列表更新,以免数据保存速度不一致,以至于数据错误(比如,在第一步切换档案,需清空指标列表的清空.)
		 */

		scope.checkMetricsList = function () {
			if(scope.modal.editorNow.variables[0].metrics.length>0){
				dataMutualSrv.get(LINK_SELECTED_METRICS + scope.modal.editorNow.baseWidget.widgetId).then(function (data) {
					if (data.status == 'success') {
						for(var i = 0; i<scope.modal.editorNow.variables[0].metrics.length; i++){
							for(var j=0; j<data.content.length; j++){
								if(scope.modal.editorNow.variables[0].metrics[i].uuid == data.content[j].uuid){
									scope.modal.editorNow.variables[0].metrics[i] = angular.copy(data.content[j]);
									if(scope.dsConfig.editor.data.translateTitle && scope.modal.editorNow.variables[0].metrics[i].type != 'compoundMetrics'){
										var name=  $translate.instant(scope.modal.editorNow.variables[0].metrics[i].i18nCode);
										if(scope.dsConfig.editor.data.metricsHasCount){
											scope.modal.editorNow.variables[0].metrics[i].name = scope.modal.editorNow.variables[0].metrics[i].calculateType + '(' + name + ')';
										}else{
											scope.modal.editorNow.variables[0].metrics[i].name =name;
										}
									}
									break;
								}
							}
						}
						scope.autoSetWidgetTitle();
					} else {
						console.log('校验指标列表失败, 接口地址: ' + LINK_SELECTED_METRICS);
						if (data.status == 'failed') {
							//console.log('Post Data Failed!')
						} else if (data.status == 'error') {
							//console.log('Post Data Error: ');
							console.log(data.message)
						}
					}
				});
			}
		};


		/**
		 * 因为已存维度的dataType字段可能在excel编辑器中被修改，因此datePeriod字段需要重新设置一下
		 */
		scope.checkDimensionsList = function(){
			if(scope.modal.editorNow.variables[0].dimensions.length>0){
				scope.modal.editorNow.variables[0].dimensions.forEach(function(item,index){
					if(!scope.modal.editorNow.variables[0].dimensions[index].datePeriod){
						scope.modal.editorNow.variables[0].dimensions[index].datePeriod = item.dataType == 'TIMESTAMP' || item.dataType == 'DATETIME' ? 'day' : scope.groupBy(item.dataFormat);
					}
				})
			}
		};

		/**
		 * 初始化指标列表收起状态
		 */
		scope.metricsListIndexInit = function(){
			if(scope.dsConfig.editor.data.metricsTwoLayer){
				for (var i = 0; i < scope.dataSettings.metricsList.length; i++) {
					scope.dataSettings.metricsListIndex.push(false);
				}
			}
		};

		/**
		 * 初始化维度列表收起状态
		 */
		scope.dimensionsListIndexInit = function(){
			if(scope.dsConfig.editor.data.metricsTwoLayer){
				for (var i = 0; i < scope.dataSettings.dimensionsList.length; i++) {
					scope.dataSettings.dimensionsListIndex.push(false);
				}
			}
		};

		/**
		 * 列表Name国际化
		 */
		function nameLocal(list) {
			if (scope.dsConfig.editor.data.metricsTwoLayer) {
				for (var i = 0; i < list.length; i++) {
					var current = list[i];
					current.name = $translate.instant(current.i18nCode);

					if (current['metricsList']) {
						for (var j = 0; j < current['metricsList'].length; j++) {
							current['metricsList'][j].name = $translate.instant(current['metricsList'][j].i18nCode);
						}
					}
				}
			}

			return list;
		}

		/**
		 * 添加计算指标
		 */
		scope.addCalculatedValue = function () {
			scope.dataSettings.calculatedValueType = 'add';
			scope.editor.calculatedValue = true;
			body.addClass('modal-open');
			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where: 'widget_editor_metrics',
				what: 'add_calculated_metrics',
				how: 'click'
			});
		};

		/**
		 * 获取计算指标列表
		 */
		scope.getCalculatedValueList = function () {
			var tableId = scope.dsConfig.editor.source.calculatedValueScopeIsTable ? scope.modal.editorNow.variables[0].profileId : "all";
			dataMutualSrv.get(LINK_LIST_CALCULATE_VALUE + '/' + scope.rootSpace.current.spaceId + '/' + scope.editor.dsId + '/' + tableId).then(function (data) {
				if (data.status == 'success') {

					scope.dataSettings.calculatedValueList = data.content;
					scope.dataSettings.tempCalculatedValueList = data.content;
					scope.dataSettings.loadFinish.calculatedValueList = true;
				} else {
					console.log('获取计算指标列表失败!');
					if (data.status == 'failed') {
					} else if (data.status == 'error') {
						console.log(data.message)
					}
				}
			});
		};

		/**
		 * 删除计算指标提示框,请求与当前指标有关联的widget数量
		 */
		scope.calculatedValueDelShowTips = function (calculatedValue) {
			scope.dataSettings.calculatedValueType = 'del';
			scope.dataSettings.calculatedValueCurrent = calculatedValue;

			dataMutualSrv.get(LINK_GET_USE_CALCULATE_VALUE + '/' + calculatedValue.id).then(function (data) {
				if (data.status == 'success') {

					scope.dataSettings.calculatedValueNum = data.content;
					scope.showTips('calculatedValueDel');
				} else {
					console.log('请求与计算指标有关联的widget数量时失败!');
					if (data.status == 'failed') {
					} else if (data.status == 'error') {
						console.log(data.message)
					}

				}
			});
		};

		/**
		 * 删除计算指标
		 */
		scope.calculatedValueDel = function () {
			uiLoadingSrv.createLoading($('body').find('.pt-popup-content'));

			dataMutualSrv.post(LINK_DEL_CALCULATE_VALUE + '/' + scope.dataSettings.calculatedValueCurrent.id, '').then(function (data) {
				if (data.status == 'success') {
					//搜索列表更新
					for (var i = 0; i < scope.dataSettings.calculatedValueList.length; i++) {
						if (scope.dataSettings.calculatedValueList[i].id == scope.dataSettings.calculatedValueCurrent.id) {
							scope.dataSettings.calculatedValueList.splice(i, 1);
							break;
						}
					}
					//原列表更新
					for (var i = 0; i < scope.dataSettings.tempCalculatedValueList.length; i++) {
						if (scope.dataSettings.tempCalculatedValueList[i].id == scope.dataSettings.calculatedValueCurrent.id) {
							scope.dataSettings.tempCalculatedValueList.splice(i, 1);
							break;
						}
					}

					//重新校验指标列表
					scope.checkMetricsList();
					scope.closeTips();
				} else {
					console.log('删除计算指标失败');
					if (data.status == 'failed') {
					} else if (data.status == 'error') {
						console.log(data.message)
					}
				}
				uiLoadingSrv.removeLoading($('body').find('.pt-popup-content'));
			});
		};

		/**
		 * 编辑计算指标
		 */
		scope.calculatedValueEdit = function (calculatedValue) {
			scope.dataSettings.calculatedValueType = 'edit';
			scope.dataSettings.calculatedValueCurrent = calculatedValue;
			scope.editor.calculatedValue = true;
			body.addClass('modal-open');
		};

		/**
		 * 点击计算指标
		 */
		scope.calculatedValueClick = function (index, isContainsFunc) {
			if (scope.dsConfig.editor.data.metricsHasCount && isContainsFunc != 1) {
				scope.dataSettings.countSelectBox[index] = !scope.dataSettings.countSelectBox[index];
				scope.closeOtherCountBox(index, 'value');
			}
		};

		/**
		 * 关闭提示
		 */
		scope.closeTips = function () {
			body.removeClass('modal-open');
			scope.dataSettings.calculatedValueNum = null;
			scope.dataSettings.calculatedValueType = null;
			scope.dataSettings.calculatedValueCurrent = null;
			scope.dataSettings.tips.show = false;
		};

		/**
		 * 提示
		 */
		scope.showTips = function (type) {
			var options = {
				title: null,
				info: null,
				btnLeftText: $translate.instant('COMMON.CANCEL'),
				btnRightText: $translate.instant('COMMON.OK'),
				btnLeftClass: 'pt-btn-default',
				btnRightClass: 'pt-btn-danger',
				btnLeftEvent: 'closeTips()',
				btnRightEvent: 'closeTips()',
				closeEvent: 'closeTips()',
				btnLeftHide: 'false',
				btnRightHide: 'false',
				hdHide: 'false'
			};

			body.addClass('modal-open');
			switch (type) {
				//删除计算指标
				case "calculatedValueDel":
					options.info = $translate.instant('WIDGET.EDITOR.DATA.DEL_CALCULATE_VALUE_0') + scope.dataSettings.calculatedValueNum + $translate.instant('WIDGET.EDITOR.DATA.DEL_CALCULATE_VALUE_1') + scope.dataSettings.calculatedValueCurrent.name + $translate.instant('WIDGET.EDITOR.DATA.DEL_CALCULATE_VALUE_2');
					options.btnRightEvent = "calculatedValueDel()";
					options.btnRightText = $translate.instant('COMMON.REMOVE');
					break;
			}

			scope.dataSettings.tips.show = true;
			scope.dataSettings.tips.options = options;
		};

		/**
		 * 根据当前数据源返回的指标列表判断是否显示 添加计算指标 的按钮
		 * table型数据源需要判断指标列表中是否包含数值型指标,有则显示.
		 *
		 * salesforce(19) & Paypal(23) & Stripe(25) & MailChimp(27)  是账号类型,但需要判断table
		 */
		scope.showCalculatedValueBtn = function () {
			if(!scope.dsConfig.test){
				//如果此数据源对外开放计算指标功能

				if (scope.dsConfig.editor.data.calculatedValueScopeShowAll) {
					scope.dataSettings.showCalculatedValue = true;
				} else {
                    scope.dataSettings.showCalculatedValue = true;
					/*
					for (var i = 0; i < scope.dataSettings.metricsList.length; i++) {
						if (['NUMBER', 'PERCENT', 'CURRENCY', 'DURATION'].indexOf(scope.dataSettings.metricsList[i].dataType) >= 0) {
							scope.dataSettings.showCalculatedValue = true;
							break;
						}
					}
					*/
				}
			}
		};

		scope.dsIconFun = function(dataType,dataFormat){
			var iconId;
			switch(dataType){
				case 'BOOLEAN':
					iconId = '#icon-ds-boolean';
					break;
				case 'TIMESTAMP':
					iconId = '#icon-ds-timestamp';
					break;
				case 'STRING':
					iconId = '#icon-ds-string';
					break;
				case 'NUMBER':
					iconId = '#icon-ds-number';
					break;
				case 'PERCENT':
					iconId = '#icon-ds-percent';
					break;
				case 'DATE':
					iconId = '#icon-ds-date';
					break;
				case 'TIME':
					iconId = '#icon-ds-time';
					break;
				case 'DATETIME':
					iconId = '#icon-ds-datetime';
					break;
				case 'DURATION':
					iconId = '#icon-ds-duration';
					break;
				case 'CURRENCY':
					if(dataFormat && dataFormat  === '¥##'){
						iconId = '#icon-ds-currency-jpy';
					}else if(dataFormat && dataFormat  === '¥###'){
						iconId = '#icon-ds-currency-rmb';
					}else if(dataFormat && dataFormat  === '¥###'){
						iconId = '#icon-ds-currency-rmb';
					}else if(dataFormat === '$##'||!dataFormat){
						iconId = '#icon-ds-currency-usd';
					}
					break;
				default:
					iconId = '';
					break;
			}
			return iconId;
		};

		/**
		 * 获取自定义变量列表
		 * @param i18nCode   作为自定义变量的标志
		 * @param fold   折叠状态
		 * @param $index 排名
		 * @param type 指标或者维度列表名称
		 */
		scope.isCustomVariable = function(i18nCode,fold,$index,type){//当处于折叠状态时，点击去获取列表，已展开状态不获取
			if(i18nCode && (i18nCode === 'METRICS_CATEGOTY.PTAPP.CUSTOMVARIABLES' || i18nCode === 'DIMENSION_CATEGOTY.PTAPP.CUSTOMVARIABLES') && fold){
				var url = LINK_APP_GET_USER_VAR_KEY_NAME +  scope.modal.editorNow.variables[0].connectionId + '/'+ scope.modal.editorNow.variables[0].dsCode + '/' + scope.modal.editorNow.variables[0].accountName + '/' +scope.modal.editorNow.variables[0].profileId + '/'+ 'metrics' ;
				//获取自定义变量列表
				dataMutualSrv.get(url).then(function (data) {
					if (data.status == 'success') {
						if(data.content && data.content.length > 0){
							if(type === 'metricsList'){
								scope.dataSettings[type][$index][type] = data.content[0].metricsList;
							}else if(type === 'dimensionsList'){
								scope.dataSettings[type][$index]['dimensionList'] = data.content[0].metricsList;
							}
						}
					} else if (data.status == 'failed') {
						console.log('获取自定义变量列表Post Data Failed!');
					} else if (data.status == 'error') {
						console.log('获取自定义变量列表Post Data Error: ');
						console.log(data.message);
					}
				});
			}
		};

		scope.needDefaultDimension = function(variables,config){
			"use strict";
			if(!config.needDefaultDimension) return false;

			if(variables.profileId){
				var profileId = variables.profileId;
				if(profileId){
					if(scope.modal.editorNow.variables[0].dimensions.length){//避免重复添加
						scope.dataSettings.dimensionsList.forEach(function(item,index){
							if(item.isDefaultSelect === 1){
								var flag = false;
								scope.modal.editorNow.variables[0].dimensions.forEach(function(innerItem,innerIndex){
									if(item.id === innerItem.id){
										flag = true;
									}
								});
								if(!flag){
									scope.addData(item,'dimensions');
								}
							}
						})
					}else{
						scope.dataSettings.dimensionsList.forEach(function(item,index){
							if(item.isDefaultSelect === 1){
								scope.addData(item,'dimensions');
							}
						})
					}


				}
			}

		};

		/**
		 * Init
		 */
		function dataInit() {

			var metricsUrl = '',//指标URL
				dimensionsUrl = ''; //维度URL
			var variables = scope.modal.editorNow.variables[0], config = scope.dsConfig.editor.data;//dsConfig来自于上级widget-editor.dire.js
			if (config.dataNeedSave) {
				scope.editor.dsId = variables.ptoneDsInfoId;
				metricsUrl = LINK_METRICS_GA + variables.ptoneDsInfoId;
				dimensionsUrl = LINK_DIMENSIONS_GA + variables.ptoneDsInfoId;
				//获取指标列表
				if ($localStorage[variables.dsCode + '-metrics'] && $localStorage[variables.dsCode + '-metrics'].v == scope.rootCommon.dataVersion[variables.dsCode + '-metrics']) {
					//转换list中name的国际化信息
					scope.dataSettings.metricsList = uiSearch.nameI18n(2, ['metricsList'], $localStorage[variables.dsCode + '-metrics'].d, 'name', 'i18nCode');
					scope.dataSettings.metricsList = cleanContent(angular.copy(scope.dataSettings.metricsList));
					// scope.dataSettings.metricsList = angular.copy($localStorage.metrics.d);
					scope.dataSettings.tempMetricsList = angular.copy(scope.dataSettings.metricsList);
					scope.dataSettings.loadFinish.metricsList = true;

					scope.showCalculatedValueBtn(); //判断是否显示计算指标按钮
					scope.metricsListIndexInit();//指标列表二级目录收起状态初始化
				} else {
					dataMutualSrv.get(metricsUrl).then(function (data) {
						if (data.status == 'success') {
							//转换list中name的国际化信息
							data.content = uiSearch.nameI18n(2, ['metricsList'], data.content, 'name', 'i18nCode');
							scope.dataSettings.loadFinish.metricsList = true;

							var _content = cleanContent(data.content);
							scope.dataSettings.metricsList = _content;
							scope.dataSettings.tempMetricsList = angular.copy(_content);
							scope.showCalculatedValueBtn(); //判断是否显示计算指标按钮
							scope.metricsListIndexInit();//指标列表二级目录收起状态初始化

							$localStorage[variables.dsCode + '-metrics'] = {
								'v': scope.rootCommon.dataVersion[variables.dsCode + '-metrics'],
								'd': data.content
							}
						} else if (data.status == 'failed') {
							console.log('Post Data Failed!');
						} else if (data.status == 'error') {
							console.log('Post Data Error: ');
							console.log(data.message)
						}
					});
				}
				//获取维度列表
				// if($localStorage.dimensions && $localStorage.dimensions.v == scope.dataSettings.dimensionsVersions){
				if ($localStorage[variables.dsCode + '-dimensions'] && $localStorage[variables.dsCode + '-dimensions'].v == scope.rootCommon.dataVersion[variables.dsCode + '-dimensions']) {
					//转换list中name的国际化信息
					scope.dataSettings.dimensionsList = uiSearch.nameI18n(2, ['dimensionList'], $localStorage[variables.dsCode + '-dimensions'].d, 'name', 'i18nCode');
					scope.dataSettings.dimensionsList = cleanContent(angular.copy(scope.dataSettings.dimensionsList));
					// scope.dataSettings.dimensionsList = angular.copy($localStorage.dimensions.d);
					scope.dataSettings.tempDimensionsList = angular.copy(scope.dataSettings.dimensionsList);
					scope.dataSettings.loadFinish.dimensionsList = true;

					scope.dimensionsListIndexInit();//维度列表二级目录收起状态初始化
				} else {
					dataMutualSrv.get(dimensionsUrl).then(function (data) {
						if (data.status == 'success') {
							//转换list中name的国际化信息
							data.content = uiSearch.nameI18n(2, ['dimensionList'], data.content, 'name', 'i18nCode');

							scope.dataSettings.loadFinish.dimensionsList = true;
							var _content = cleanContent(data.content);
							scope.dataSettings.dimensionsList = _content;
							scope.dataSettings.tempDimensionsList = angular.copy(_content);
							scope.dimensionsListIndexInit();//维度列表二级目录收起状态初始化

							$localStorage[variables.dsCode + '-dimensions'] = {
								'v': scope.rootCommon.dataVersion[variables.dsCode + '-dimensions'],
								'd': data.content
							}
						} else {
							if (data.status == 'failed') {
								console.log('Post Data Failed!')
							} else if (data.status == 'error') {
								console.log('Post Data Error: ')
								console.log(data.message)
							}
						}
					});
				}
			} else if (variables.ptoneDsInfoId == 19) {
				//salesforce数据源特殊处理
				scope.editor.dsId = variables.ptoneDsInfoId;
				var url = LINK_SALESFORCE_METRICES_AND_DIMENSION + '/' + variables.profileId + '/' + variables.connectionId+ '/' + variables.accountName +'/true';
				var salseforceType = 'object';
				if((variables.profileId + '').indexOf('|join|') > -1){
					var reportType = variables.profileId.split('|join|')[1];
					salseforceType = 'report';
					if(reportType === 'Summary' || reportType === 'Matrix'){//Summary 和 Matrix 类型不支持计算，不支持groupby
						$timeout(function() {
							scope.dsConfig.editor.data.metricsHasCount = false;
							scope.dsConfig.editor.data.dimensionsHasGroupBy = false;
							scope.dsConfig.editor.data.drewChartNeedMetrics = true;//至少需要一个指标才能出数
							scope.dsConfig.editor.data.isSupportFunc = false; //不支持聚合函数
							scope.dsConfig.editor.filter.isFilters = false;//不支持过滤器
							scope.dsConfig.editor.data.metricsOneLayerIsShowDataType = false;
							scope.dsConfig.editor.data.dimensionsOneLayerIsShowDataType = false;
							scope.dsConfig.editor.data.metricsTwoLayerIsShowDataType = false;
							scope.dsConfig.editor.data.dimensionsTwoLayerIsShowDataType = false;
						})
					}else if(reportType === 'Tabular' ){
						$timeout(function() {
							scope.dsConfig.editor.data.metricsHasCount = true;
							scope.dsConfig.editor.data.dimensionsHasGroupBy = true;
							scope.dsConfig.editor.data.drewChartNeedMetrics = false;//一个指标或者一个维度都能出数
							scope.dsConfig.editor.data.isSupportFunc = true;//tabular 类型的数据，计算指标支持聚合函数
							scope.dsConfig.editor.filter.isFilters = true;//支持过滤器
							scope.dsConfig.editor.data.metricsOneLayerIsShowDataType = true;
							scope.dsConfig.editor.data.dimensionsOneLayerIsShowDataType = true;
							scope.dsConfig.editor.data.metricsTwoLayerIsShowDataType = false;
							scope.dsConfig.editor.data.dimensionsTwoLayerIsShowDataType = false;

						})
					}
					$timeout(function(){
						scope.dsConfig.editor.data.metricsOneLayer = true;
						scope.dsConfig.editor.data.metricsTwoLayer = false;
						scope.dsConfig.editor.data.dimensionsOneLayer = true;
						scope.dsConfig.editor.data.dimensionsTwoLayer = false;
					});
				}else{
					$timeout(function(){
						scope.dsConfig.editor.data.metricsOneLayer = false;
						scope.dsConfig.editor.data.metricsTwoLayer = true;
						scope.dsConfig.editor.data.dimensionsOneLayer = false;
						scope.dsConfig.editor.data.dimensionsTwoLayer = true;
						scope.dsConfig.editor.filter.isFilters = true;//支持过滤器
						scope.dsConfig.editor.data.dimensionsHasGroupBy = true;
						scope.dsConfig.editor.data.metricsHasCount = true;
						scope.dsConfig.editor.data.metricsOneLayerIsShowDataType = false;
						scope.dsConfig.editor.data.dimensionsOneLayerIsShowDataType = false;
						scope.dsConfig.editor.data.metricsTwoLayerIsShowDataType = true;
						scope.dsConfig.editor.data.dimensionsTwoLayerIsShowDataType = true;
					});
				}
				dataMutualSrv.get(url).then(function (data) {
					if (data.status == 'success') {
						scope.dataSettings.loadFinish.metricsList = true;
						scope.dataSettings.loadFinish.dimensionsList = true;
						scope.dataSettings.loadFinish.failed = false;
						if(salseforceType === 'object'){
							scope.dataSettings.metricsList = angular.copy(data.content);
							scope.dataSettings.tempMetricsList = angular.copy(data.content);
							scope.showCalculatedValueBtn(); //判断是否显示计算指标按钮
							scope.metricsListIndexInit();//指标列表二级目录收起状态初始化
							var _dimensionsContent = [];
							for (var i = 0; i < data.content.length; i++) {
								var dimensionsCategory = data.content[i];
								dimensionsCategory.dimensionList = dimensionsCategory.metricsList;
								dimensionsCategory.metricsList = [];
								_dimensionsContent.push(dimensionsCategory);
							}
							scope.dataSettings.dimensionsList = _dimensionsContent;
							scope.dataSettings.tempDimensionsList = angular.copy(_dimensionsContent);
							scope.dimensionsListIndexInit();//维度列表二级目录收起状态初始化
							scope.dsConfig.editor.data.isSupportFunc = false; //不支持聚合函数
						}else if(salseforceType === 'report'){
							scope.dataSettings.metricsOfSalesforceName = data.content[0].name;//区分report的三种类型
							scope.dataSettings.metricsList = scope.dataSettings.tempMetricsList = angular.copy(data.content[0].metricsList);
							scope.dataSettings.dimensionsList = scope.dataSettings.tempDimensionsList = angular.copy(data.content[0].dimensionList);
							scope.showCalculatedValueBtn(); //判断是否显示计算指标按钮
						}
					} else if (data.status == 'failed') {
						console.log('获取salesforce指标维度失败');
						scope.dataSettings.loadFinish.metricsList = true;
						scope.dataSettings.loadFinish.dimensionsList = true;
						scope.dataSettings.loadFinish.failed = true;
					} else if (data.status == 'error') {
						console.log('获取salesforce指标维度出错');
						console.log(data.message)
					}
				});
			} else {
				//数据源为其他数据源

				scope.editor.dsId = variables.ptoneDsInfoId;
				metricsUrl = LINK_USER_METRICS + scope.modal.editorNow.variables[0].ptoneDsInfoId + '/' + scope.modal.editorNow.variables[0].profileId;
				dimensionsUrl = LINK_USER_DIMENSIONS + scope.modal.editorNow.variables[0].ptoneDsInfoId + '/' + scope.modal.editorNow.variables[0].profileId;
				dataMutualSrv.get(metricsUrl).then(function (data) {
					if (data.status == 'success') {
						scope.dataSettings.loadFinish.metricsList = true;
						scope.dataSettings.loadFinish.failed = false;
						scope.dataSettings.metricsList = data.content;
						scope.dataSettings.tempMetricsList = angular.copy(data.content);
						scope.showCalculatedValueBtn(); //判断是否显示计算指标按钮
						scope.metricsListIndexInit();//指标列表二级目录收起状态初始化
					} else if (data.status == 'failed') {
						console.log('Post Data Failed!');
						scope.dataSettings.loadFinish.metricsList = false;
						scope.dataSettings.loadFinish.failed = true;
					} else if (data.status == 'error') {
						console.log('Post Data Error: ')
						console.log(data.message)
					}
				});

				dataMutualSrv.get(dimensionsUrl).then(function (data) {
					if (data.status == 'success') {
						scope.dataSettings.loadFinish.dimensionsList = true;
						scope.dataSettings.loadFinish.failed = false;
						scope.dataSettings.dimensionsList = data.content;
						scope.dataSettings.tempDimensionsList = angular.copy(data.content);
						scope.dimensionsListIndexInit();//维度列表二级目录收起状态初始化
					} else if (data.status == 'failed') {
						console.log('Post Data Failed!');
						scope.dataSettings.loadFinish.dimensionsList = false;
						scope.dataSettings.loadFinish.failed = true;
					} else if (data.status == 'error') {
						console.log('Post Data Error: ')
						console.log(data.message)
					}
				});
			}

			//自动选择默认指标维度
			scope.needDefaultDimension(variables,config);

			//请求计算指标列表
			scope.getCalculatedValueList();

			//更新已存指标列表
			scope.checkMetricsList();

			//更新已存维度列表
			scope.checkDimensionsList();
		}

		dataInit();

		//指标维度可拖拽排序
		var metrics_list = document.querySelector('#metrics-list'),
			dimensions_list = document.querySelector('#dimensions-list');
		var Safari = navigator.userAgent.indexOf("Safari") > -1 && navigator.userAgent.indexOf("Chrome") === -1;
		Sortable.create(metrics_list, {
			animation: 150,
			filter: ".select-count",
			forceFallback: true,
			scroll: false,
			fallbackTolerance:Safari ? 0 : 1,//解决safari浏览器下拖拽鼠标手型的问题
			onStart: sortableFn,
			onEnd: sortableFn
		});
		Sortable.create(dimensions_list, {
			animation: 150,
			filter: ".select-count",
			forceFallback: true,
			scroll: false,
			fallbackTolerance:Safari ? 0 : 1,
			onStart: sortableFn,
			onEnd: sortableFn
		});

		function sortableFn(evt){
			var listName,oldIndex,newIndex;
			if(evt.from.id === 'metrics-list'){
				listName = 'metrics-list';
			}else if(evt.from.id === 'dimensions-list'){
				listName = 'dimensions-list';
			}
			var safariAndChrome = navigator.userAgent.indexOf("Safari");
			if(evt.type === 'end' && listName){
				if(evt.oldIndex === evt.newIndex || evt.oldIndex === 'undefined' || evt.newIndex === 'undefined'){
					return;
				}
				oldIndex = evt.oldIndex;
				newIndex = evt.newIndex;

				switch (listName){
					case 'metrics-list':
						scope.modal.editorNow.variables[0].metrics.splice(newIndex,0,scope.modal.editorNow.variables[0].metrics.splice(oldIndex,1)[0]);
						break;
					case 'dimensions-list':
						scope.modal.editorNow.variables[0].dimensions.splice(newIndex,0,scope.modal.editorNow.variables[0].dimensions.splice(oldIndex,1)[0]);
						break;
				}
				scope.autoSetWidgetTitle();
				scope.saveData('data-addData');
				if(safariAndChrome > -1){
					$('body').removeClass('no-select');//解决safari和chrome下选中其他区域的问题
				}
				scope.$apply(function(){//解决windows下，hover状态在拖拽后不能及时更新的问题
					scope.modal.editorNow.variables[0].metrics = scope.modal.editorNow.variables[0].metrics;
					scope.modal.editorNow.variables[0].dimensions = scope.modal.editorNow.variables[0].dimensions;
				});
			}
			if(evt.type === 'start'){
				if(safariAndChrome > -1){
					$('body').addClass('no-select');
				}
				$(evt.from).find('.dropdown-menus').removeClass('open');//关闭所有的删除按钮

				$.each(scope.dataSettings.countSelectBox, function (i) {
					scope.dataSettings.countSelectBox[i] = false;
				});
				$.each(scope.dataSettings.attributeSelectBox, function (i) {
					scope.dataSettings.attributeSelectBox[i] = false;
				});
			}
		}


		// 自定义指标名称
		scope.showDataAlias = function(value, index, type){
			scope.dataSettings.dataAlias = {
				index: index,
				value: value,
				type: type,
				showFlag: true
			}
		}
		scope.dataAliasOnApply = function(alias){
			var type = scope.dataSettings.dataAlias.type;
			var index = scope.dataSettings.dataAlias.index;

			scope.modal.editorNow.variables[0][type][index]['alias'] = alias;
			scope.dataAliasOnCancel();
			scope.autoSetWidgetTitle();
			scope.saveData('data-alias');
		}
		scope.dataAliasOnCancel = function(){
			scope.dataSettings.dataAlias = {}
		}
	}
}

export default editorData;
