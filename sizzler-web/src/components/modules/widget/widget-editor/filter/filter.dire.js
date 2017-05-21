// 'use strict';

import tpl from './filter.tpl.html';
import './filter.css';

import {
	LINK_FILTER_DIMENSION_VALUE_LIST,
	LINK_SEGMENTS_GA,
	LINK_SALESFORCE_METRICES_AND_DIMENSION,
	LINK_FILTER_LIST,
	LINK_USER_METRICS_AND_DIMENSIONS,
	LINK_APP_GET_USER_VAR_KEY_NAME,
	LINK_SEGMENT_LIST,
	uuid
} from '../../../common/common';


/**
 * widgetFilter
 * widget指标过滤器
 */
widgetFilter.$inject = ['$translate', '$localStorage', '$rootScope', 'dataMutualSrv', 'uiSearch', 'uiLoadingSrv', 'siteEventAnalyticsSrv','Track'];
function widgetFilter($translate, $localStorage, $rootScope, dataMutualSrv, uiSearch, uiLoadingSrv, siteEventAnalyticsSrv,Track) {
	return {
		restrict: 'EA',
		template: tpl,
		link: link
	};

	function link(scope) {
		scope.filterSet = {
			segmentsVersions: 1217001,
			dimensionsVersions: 1217001
		};

		scope.myOptions = {
			segmentsSearchKey:null,
			searchPlaceholder: null,     //搜索框的占位符提示信息
			calculatedValueList: angular.copy(scope.dataSettings.calculatedValueList),
			calculatedValueOriginalList: angular.copy(scope.dataSettings.calculatedValueList) //计算指标原始列表(用做搜索)
		};


		var filter = scope.filter = {
			"type": scope.editor.dsId == 1 ? 'saved' : 'new', //saved || new

			//Saved Filter
			"segmentsList": null,
			"savedData": [],

			"searchKey": [],

			//Create New Filter
			"dimensionsOriginalList": null,
			"newData": [
				{ //box
					"onlyShow": true,
					"type": "session", //session || user
					"condition": [
						// dftFilterConData
					]
				}
			],
			'gaDimensionsList': [],
			'gaContainerList': [],
			'showSearchItem': false,
			'valueList': [], //第三步中的已选中选项或者已保存的值
			'dsDateType': '',//自定义过滤器中，第一步选择的指标维度的类型（ga中不适用）
			'dsDateTypeSelected': [],//是否选择过指标维度的类型（ga不适用）
			'googleAdwordsStringFilter': [], //google adwords 数据源的字符串类型过滤器做特殊处理，有复选框供选择
			'filterItemList': [], //google adwords 复选框
			'googleAdwordsItem': [], //google adwords 过滤器三个选择中的第一个选择，选择的是第几个
			'temporaryArray': [], //临时数组
			'googleAdwordsCheckBox': [], //google adwords 复选框的选中状态
			'getCheckboxState':false, //第三步选择复选框时，请求数据的状态
			'filterBigType': 1 //是会话级别还是用户级别，会话是1，用户是0
		};

		//引入国际化信息
		var container = $translate.instant('WIDGET.EDITOR.FILTER.CONTAINER').split('|&|');
		var show = $translate.instant('WIDGET.EDITOR.FILTER.SHOW').split('|&|');
		var type = $translate.instant('WIDGET.EDITOR.FILTER.TYPE').split('|&|');
		scope.containerList = [
			{
				"name": container[0], //"Equal to or exact match",
				"code": "=="//0
			},
			{
				"name": container[1], //"Not equal to or is not an exact match",
				"code": "!="//1
			},
			{
				"name": container[2], //"Contains substring",
				"code": "=@"//2
			},
			{
				"name": container[3], //"Does not contain substring",
				"code": "!@"//3
			},
			{
				"name": container[4], //"Contains a match for regular expression",
				"code": "=~"//4
			},
			{
				"name": container[5], //"Does not contain a match for regular expression",
				"code": "!~"//5
			},
			{
				"name": '<',
				"code": "<"//6
			},
			{
				"name": '≤',
				"code": "<="//7
			},
			{
				"name": '=',
				"code": "="//8
			},
			{
				"name": '≠',
				"code": "<>"//9
			},
			{
				"name": '≥',
				"code": ">="//10
			},
			{
				"name": '>',
				"code": ">"//11
			},
			{
				"name": container[6],
				"code": "equal"//12
			},
			{
				"name": container[7],
				"code": "not_equal"//13
			},
			{
				"name": container[8],
				"code": "contain"//14
			},
			{
				"name": container[9],
				"code": "not_contain"//15
			},
			{
				"name": container[10],
				"code": "start"//16
			},
			{
				"name": container[11],
				"code": "end"//17
			},
			{
				"name": container[12],
				"code": "not_start"//18
			},
			{
				"name": container[13],
				"code": "not_end"//19
			},
			{
				"name": container[14],
				"code": "in"//20
			},
			{
				"name": container[15],
				"code": "not_in"//21
			},
			{
				"name": '<',
				"code": "LESS_THAN"//22
			},
			{
				"name": '≤',
				"code": "LESS_THAN_OR_EQUAL"//23
			},
			{
				"name": '=',
				"code": "EQUAL"//24
			},
			{
				"name": '≥',
				"code": "GREATER_THAN_OR_EQUAL"//25
			},
			{
				"name": '>',
				"code": "GREATER_THAN"//26
			},
			{
				"name": '≠',
				"code": "!=="//27
			},
			{
				"name": '=',
				"code": "==="//28
			},
			{
				"name":container[16],
				"code":'is_null'//29
			},
			{
				"name":container[17],
				"code":'is_not_null'//30
			}
		];

		scope.containerShowList = [
			{
				"name": show[0], //"Include",
				"code": true
			},
			{
				"name": show[1], //"Exclude",
				"code": false
			}
		];

		scope.containerTypeList = [
			{
				"name": type[0], //"User",
				"code": "user"
			},
			{
				"name": type[1], //"Session",
				"code": "session"
			}
		];

		var dftFilterConDataOp = scope.containerList[2];
		var dftFilterConData = {
			"name": "", // User Type
			"code": "", // ga:userType
			"op": dftFilterConDataOp.code, // =@
			"value": "",
			"rel": null, //or || and
			"type": null //google adwords 需要保存metric or dimension
		};

		//选择segments list
		scope.changeFilterSegment = function (segment) {
			scope.myOptions.segmentsSearchKey = segment.name;
			filter.savedData[0] = segment.segmentId;
			filter.segment = segment;

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'select_saved_filter');

			//全站事件统计
			Track.log({where: 'widget_editor_metrics_' + scope.editor.filtersType,what: 'select_segment', value: segment.segmentId});
		};

		//选择 包含 || 不包含
		scope.selectShowType = function (code, box) {
			box.onlyShow = code;
		};

		//选择 用户数 || 会话数
		scope.selectDataType = function (code, box) {
			box.type = code;
		};


		//创建单个filter
		scope.createFilter = function (type, box) {
			var length = box.condition.length;
			if (length > 0) {
				box.condition[length - 1].rel = type;
			}

			var filterData = angular.copy(dftFilterConData);
			var filterOp = angular.copy(dftFilterConDataOp);

			if (scope.editor.dsId == 13) {
				filterOp = scope.containerList[20]; // in
			}

			filterData.name = '';
			filterData.code = '';
			filterData.op = filterOp.code;
			box.condition.push(filterData);

			filter.searchKey.push(filterData.name);
			filter.gaDimensionsList.push(filterData.name);
			filter.gaContainerList.push(filterOp);

			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where: 'widget_editor_metrics_' + scope.editor.filtersType,
				what:  scope.editor.filtersType + '_' + type,
				how: 'click'
			});
		};

		//删除单个filter
		scope.delFilter = function (index, box) {
			//全站事件统计
			Track.log({where: 'widget_editor_metrics_' + scope.editor.filtersType,what: 'delete_condition', value: box['condition'] && box['condition'][index] && box['condition'][index].id});
			for (var i = 0; i < box.condition.length; i++) {//gd删除过滤条件的时候，需要让剩下的条件的第二步和第三步都显示出来
				if (i != index && box.condition[i].code) {
					filter.dsDateTypeSelected[i] = true;
				}
			}
			if (index > 0) {
				box.condition[index - 1].rel = box.condition[index].rel;
			}

			box.condition.splice(index, 1);

			//动态model需要删除model List
			filter.gaDimensionsList.splice(index, 1);
			filter.gaContainerList.splice(index, 1);//删除过滤条件时，要保证html遍历不会出错最好是将这个条件替换成空数组
			filter.valueList.splice(index, 1);
			filter.searchKey.splice(index, 1);//需要将条件的第一个（名称）也同时更新

			filter.dsDateTypeSelected.splice(index, 1);//ga以外的数据源，需要更新条件的后两项是否显示的限制

			//屏蔽此次点击事件的判断
			scope.dataSettings.excludeClick = true;

		};

		//选择单个filter内的Dimensions值
		scope.changeFilterDimensions = function (dimensions, index, box) {
			box.condition[index].i18nCode = dimensions.i18nCode;
			box.condition[index].code = dimensions.code;
			box.condition[index].type = dimensions.type;
			box.condition[index].name = $translate.instant(dimensions.i18nCode);
			filter.gaDimensionsList[index] = dimensions;
			filter.searchKey[index] = dimensions.name;

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'select_filter_condition');
		};
		scope.changeFilterDimensionsDs = function (dimensions, index, box) {
			if (scope.editor.dsId == 1) {
				box.condition[index].i18nCode = dimensions.i18nCode;
				box.condition[index].name = $translate.instant(dimensions.i18nCode);

				filter.searchKey[index] = $translate.instant(dimensions.i18nCode);
			} else {
				box.condition[index].name = dimensions.name;
			}

			box.condition[index].id = dimensions.id;
			box.condition[index].dataType = dimensions.dataType;
			box.condition[index].type = dimensions.type;
			box.condition[index].code = dimensions.code;
			filter.gaDimensionsList[index] = dimensions;
			filter.searchKey[index] = box.condition[index].name;

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'select_filter_condition');
		};

		//选择单个filter内的Container值
		scope.changeFilterContainer = function (container, index, box) {
			box.condition[index].op = container.code;

			if (!filter.gaContainerList[index]) {
				filter.gaContainerList.push(container);
			} else {
				filter.gaContainerList.splice(index, 1, container);
			}

			if(container.code === 'is_not_null' || container.code === 'is_null'){
				filter.dsDateTypeSelected[index] = false;
			}


			//全站事件统计
			Track.log({where: 'widget_editor_metrics_' + scope.editor.filtersType,what: 'select_condition_operator', value: container.code});
		};

		//
		scope.showIptStep = function(box,userIndex){
			if(userIndex === void 0) return false;
			if( box.condition &&
				box.condition[userIndex] &&
				box.condition[userIndex].op &&
				(box.condition[userIndex].op === 'is_null' || box.condition[userIndex].op === 'is_not_null') &&
				scope.dsConfig.editor.filter.needIsNull){
				return false;
			}
			if(!filter.googleAdwordsStringFilter[userIndex] && filter.dsDateTypeSelected[userIndex]) return true;
			return !!(!filter.googleAdwordsStringFilter[userIndex] && box.condition && box.condition[userIndex] && box.condition[userIndex].dataType);
		};




		//修改单个filter内的输入值
		scope.changeFilterValue = function (data, index, box) {
			box.condition[index].value = data;

			if (!filter.valueList[index]) {
				filter.valueList.push(data);
			} else {
				filter.valueList.splice(index, 1, data);
			}
			//全站事件统计
			Track.log({where: 'widget_editor_metrics_' + scope.editor.filtersType, what: 'input_condition_value', value: data, how: 'input'});
		};
		//修改单个filter的关系操作符
		scope.changeFilterRel = function (data, index, box, dsId) {
			var config = scope.dsConfig.editor.filter;
			if (config.isNeedOr) {
				box.condition[index].rel = data == 'and' ? 'or' : 'and';
			}
		};

		/*
		 目前用于salesforce的指标维度只显示profileId对应的
		 */
		function cleanContent(content) {
			if (scope.dsConfig.editor.data.showWithProfileId && scope.modal.editorNow.variables[0].profileId) {
				var temContent = [];
				for (var i = 0; i < content.length; i++) {
					if (scope.modal.editorNow.variables[0].profileId == content[i].categoryId) {
						temContent.push(content[i]);
					}
				}
				return temContent;
			} else {
				return content;
			}
		}

		function dimensionsInit(list) {
			//转换list中name的国际化信息
			var variables = scope.modal.editorNow.variables[0];
			if (scope.editor.dsId == 1) {
				list = uiSearch.nameI18n(2, ['dimensionList'], list, 'name', 'i18nCode');
			} else if (scope.dsConfig.editor.filter.i18nByFilterList) {
				if(scope.editor.dsId == 13){
					list = uiSearch.nameI18n(2, ['abcd'], list, 'name', 'i18nCode');//ptengine过滤器的第三步中的复选框不需要国际化，但是第一步需要国际化，'abcd'是随便瞎写的一个字符串名字
				}else{
					list = uiSearch.nameI18n(2, ['filterItemList'], list, 'name', 'i18nCode');
				}
			}

			scope.loadSetting.widget1 = false;
			filter.dimensionsOriginalList = list;
			scope.dimensionsList = angular.copy(filter.dimensionsOriginalList);

			//google adwords 数据源保存相关复选框
			angular.forEach(scope.dimensionsList, function (con, index) {
				filter.filterItemList.push(con.filterItemList);
			});

			//新增操作时，数据初始化
			if (scope.dataSettings.filterType == 'add') {

				//加入默认数据
				var filterData = angular.copy(dftFilterConData);
				filterData.name = '';
				filterData.code = '';
				filter.newData[0].condition[0] = filterData;

				filter.searchKey[0] = filterData.name;
				filter.gaDimensionsList[0] = filterData.name;
				if (scope.editor.dsId == 13) {
					filter.gaContainerList[0] = scope.containerList[20]; // in
				} else {
					filter.gaContainerList[0] = dftFilterConDataOp;
				}

			} else if (scope.dataSettings.filterType == 'edit') {
				filter.type = scope.dataSettings.currentFilter.type;
				filter.newData = angular.copy(scope.dataSettings.currentFilter.newData);
				for (var i = 0; i < filter.newData[0].condition.length; i++) {
					filter.searchKey.push(filter.newData[0].condition[i].name);
					filter.gaDimensionsList.push(filter.newData[0].condition[i].name);

					//只有google adwords有复选框
					if ([3,12,13,21,22].indexOf(+scope.editor.dsId)>=0 && filter.newData[0].condition[i].type) {

						if (['metrics', 'compoundMetrics'].indexOf(filter.newData[0].condition[i].type)>=0) {

							filter.dsDateTypeSelected[i] = true;
							filter.googleAdwordsStringFilter[i] = false;
							filter.googleAdwordsItem.push(i);
							filter.googleAdwordsCheckBox[i] = [];
							scope.tempIndex = i;
							filter.temporaryArray[i] = filter.newData[0].condition[i].value;
						} else if (filter.newData[0].condition[i].type == 'dimension') {

							filter.dsDateTypeSelected[i] = false;
							filter.googleAdwordsStringFilter[i] = true;
							var code = filter.newData[0].condition[i].code,
								value = filter.newData[0].condition[i].value;
							scope.googleAdWordsChecked = filter.newData[0].condition[i].checked; //用来存放复选框的选择状态
							scope.tempIndex = null;//记录临时的index用来保存用户自定义的复选框在filterItemList中的位置
							scope.tempConditionIndex = i; //用来记录临时的循环遍历的i值
							angular.forEach(list, function (con, index) {
								if (con.code == code) {//因为name是国际化过的名字，这样比较在切换语言后会出错，所以改成比较code
									filter.googleAdwordsItem.push(index);
									scope.tempIndex = index;
								}
							});
							filter.temporaryArray[i] = '';

							if (!filter.filterItemList[scope.tempIndex]) {
								//用户自定义的复选框

								scope.loadSetting.widget1 = true;
								var filterItemData = {
									"dsId": scope.editor.dsId,
									"dsCode": scope.editor.dsCode,
									"connectionId": variables.connectionId,
									"accountName": variables.accountName,
									"profileId": variables.profileId,
									"dimensionId": filter.newData[0].condition[i].id,
									"dateKey": scope.modal.editorNow.baseWidget.dateKey,
									"uid": $rootScope.userInfo.ptId,
									"checked": scope.googleAdWordsChecked,
									"tempIndex": scope.tempIndex,
									"tempConditionIndex": scope.tempConditionIndex,
									"postValue": value//value这个值需要先保存到服务器，否则异步的value会不正确
								};
								dataMutualSrv.post(LINK_FILTER_DIMENSION_VALUE_LIST, filterItemData).then(function (data) {
									if (data.status == 'success') {
										var googleAdWordsChecked = data.content.checked.split(','),
											tempIndex = +data.content.tempIndex,
											tempConditionIndex = +data.content.tempConditionIndex,
											postValue = data.content.postValue;
										filter.filterItemList[tempIndex] = data.content.filterItemList;
										angular.forEach(filter.filterItemList[filter.googleAdwordsItem[tempConditionIndex]], function (con, index) {
											if (postValue && postValue.indexOf(con.code) > -1) {
												filter.temporaryArray[tempConditionIndex] += (con.name + ',');
											}
										});

										filter.googleAdwordsCheckBox[tempConditionIndex] = [];
										angular.forEach(googleAdWordsChecked, function (con, index) {
											con == 'true' ? filter.googleAdwordsCheckBox[tempConditionIndex][index] = true : filter.googleAdwordsCheckBox[tempConditionIndex][index] = false;
										});
										filter.valueList[tempConditionIndex] = filter.temporaryArray[tempConditionIndex];
									} else if (data.status == 'failed') {
										console.log('Post Data Failed!')
									} else if (data.status == 'error') {
										console.log('Post Data Error: ');
										console.log(data.message);
									}
									scope.loadSetting.widget1 = false;
								});
							} else {
								// google adwords 本身就存在的复选框

								angular.forEach(filter.filterItemList[filter.googleAdwordsItem[i]], function (con) {
									if (value.indexOf(con.code) > -1) {
										filter.temporaryArray[i] += (con.name + ',');
									}
								});
								scope.googleAdWordsChecked = scope.googleAdWordsChecked.split(',');
								filter.googleAdwordsCheckBox[i] = [];
								angular.forEach(scope.googleAdWordsChecked, function (con, index) {
									con == 'true' ? filter.googleAdwordsCheckBox[i][index] = true : filter.googleAdwordsCheckBox[i][index] = false;
								});
							}

						}
					}


					angular.forEach(scope.containerList, function (con) {
						if (con.code == filter.newData[0].condition[i].op) {
							if (+scope.editor.dsId !== 3) {
								filter.gaContainerList.push(con);
							} else {
								filter.gaContainerList[i] = con;
							}
						}
						//} else if (con.code == filter.newData[0].condition[i].op && (con.name == filter.newData[0].condition[i].op || [">=", "<="].indexOf(filter.newData[0].condition[i].op) > -1) && +variables.ptoneDsInfoId == 19) {
						//    filter.gaContainerList[i] = con;
						//}
					});
					if (+scope.editor.dsId !== 3 && +scope.editor.dsId !== 13) {
						filter.valueList.push(filter.newData[0].condition[i].value);
					} else {
						if ((filter.newData[0].condition[i].type && ['metrics', 'compoundMetrics'].indexOf(filter.newData[0].condition[i].type) >= 0) || (filter.filterItemList[scope.tempIndex] && filter.filterItemList[scope.tempIndex].length > 0)) {
							filter.valueList[i] = filter.temporaryArray[i];
						}
					}

				}
			}

			//loading
			uiLoadingSrv.removeLoading(angular.element('.editor-data'));
		}

		//删除整个过滤器
		scope.remove = function () {
			saveFilter('remove');
		};

		scope.validateEmptyFilter = function () {
			var flag = false;

			if(filter.type=='new'){
				for (var i = 0; i < filter.newData.length; i++) {
					for (var j = 0; j < filter.newData[i].condition.length; j++) {
						if (!filter.newData[i].condition[j].name ||
							(!filter.newData[i].condition[j].value && filter.newData[i].condition[j].op !== 'is_null' && filter.newData[i].condition[j].op !== 'is_not_null')) {
							flag = true;
							break;
						}
					}
				}
			} else if(filter.type == 'saved'){
				if(filter.savedData.length<=0){
					flag = true;
				}
			}

			return flag;
		};

		//保存
		scope.save = function () {
			saveFilter(scope.dataSettings.filterType, filter);

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'apply_filter');

			//全站事件统计
			Track.log({where: 'widget_editor_metrics_' + scope.editor.filtersType, what:'apply'});
		};

		//close
		scope.close = function (option) {
			// scope.dataSettings.filterShow = false;
			scope.editor.filterShow = false;

			//屏蔽此次点击事件的判断
			scope.dataSettings.excludeClick = true;

			//全站事件统计
			if(!option){
				Track.log({where: 'widget_editor_metrics_' + scope.editor.filtersType, what:'cancle'});
			}
		};

		//过滤器保存
		function saveFilter(type, data) {
			if (type != 'remove') {
				delete data.dimensionsOriginalList;
				delete data.gaDimensionsList;
				delete data.gaContainerList;
				delete data.valueList;
				delete data.segment;
				delete data.segmentsList;
				delete data.searchKey;
				delete data.showSearchItem;
				delete data.dsDateType;//选择的过滤器（ds）的类型---NUMBER、DATE等
				delete data.dsDateTypeSelected;//是否选择过上面那个下拉菜单
				delete data.googleAdwordsStringFilter; //google adwords 数据源的复选框选项
				delete data.filterItemList;
				delete data.googleAdwordsItem;
				delete data.temporaryArray;
				delete data.googleAdwordsCheckBox;

				for (var i = 0; i < data.newData.length; i++) {
					for (var j = 0; j < data.newData[i].condition.length; j++) {
						delete data.newData[i].condition[j].index;
					}
				}
			}

			if (type == 'add' || type == 'edit') {
				scope.modal.editorNow.variables[0][scope.dataSettings.filterScope] = angular.copy(data);
			} else if (type == 'remove') {
				scope.modal.editorNow.variables[0][scope.dataSettings.filterScope] = null;
			}

			//重置操作状态
			scope.dataSettings.filterType = null;
			scope.dataSettings.filterScope = null;
			scope.dataSettings.currentFilter = null;

			//清除旧的过滤器
			if(scope.modal.editorNow.variables[0].metrics){
				for(var i=0; i<scope.modal.editorNow.variables[0].metrics.length; i++){
					scope.modal.editorNow.variables[0].metrics[i].segment = null;
				}
			}

			//save
			scope.saveData('filter-saveFilter');

			//close
			scope.close('no-pt-log');
		}

		//搜索框
		scope.resetSegmentsList = function (data) {
			scope.segmentsList = data;

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'query_saved_filter');
		};
		scope.resetDimensionsList = function (data) {
			scope.dimensionsList = data;

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'query_saved_filter');
		};
		scope.resetCalculatedValueList = function(data){
			scope.myOptions.calculatedValueList = data;
			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'query_saved_filter');
		};
		scope.myOptions.segmentsSearchKey = null;
		scope.dimensionIndex = {};//控制下拉的打开与否
		scope.dimensionExpansionIndex = {};//控制子展开与否
		scope.collapseAllItem = function (flag) {
			if (scope.dimensionExpansionIndex) {
				$.each(scope.dimensionExpansionIndex, function (i, item) {
					scope.dimensionExpansionIndex[i] = flag;
				});
			}
		};
		scope.searchContent = function (key, field, backData, fun) {
			if (key) {
				try {
					//scope.filter.showSearchItem = true;
					scope.collapseAllItem(true);
					var reg = new RegExp('.*?' + $.regTrim(key) + '.*?', "i");
					var data;
					scope.copyData = angular.copy(backData);
					for (var i = 0; i < scope.copyData.length; i++) {
						var flag = true;
						//第一层名字是否匹配到
						var nameFlag = reg.test(scope.copyData[i].name);
						var copyItem = angular.copy(scope.copyData[i]);
						for (var j = 0; j < scope.copyData[i][field].length; j++) {
							//没匹配到
							if (!reg.test(scope.copyData[i][field][j].name)) {
								scope.copyData[i][field].splice(j, 1);
								j--;
							} else {
								flag = false;
							}
						}
						if (flag && !nameFlag) {
							scope.copyData.splice(i, 1);
							i--;
						}
						//复原删除的数据
						if (flag && nameFlag) {
							scope.copyData[i] = copyItem;
						}
					}
					data = scope.copyData;
				} catch (e) {
					if (!$('#segmentsSearch').hasClass('open')) {
						$('#segmentsSearch').addClass('open');
					}
					var reg = new RegExp('.*?' + $.regTrim(key) + '.*?', "i");
					scope.copyData = angular.copy(backData);
					for (var i = 0; i < scope.copyData.length; i++) {
						//没匹配到
						if (!reg.test(scope.copyData[i][field])) {
							scope.copyData.splice(i, 1);
							i--;
						}
					}
					data = scope.copyData;
				}
			} else {
				//scope.filter.showSearchItem = false;
				scope.collapseAllItem(false);
				data = backData;
			}
			fun(data);
		};

		scope.search = function (key, type) {
			var config = scope.dsConfig.editor.filter;
			// var key;//搜索关键字
			var tier;//原始数据层级数目
			var tierData;//层级列表[]
			var dataList;//原始数据
			var backFun;//回调函数
			var collapseFun;//控制子层是否展开

			if (type == 'segment') {
				tier = 1;
				tierData = [];
				dataList = scope.filter.segmentsList;
				backFun = scope.resetSegmentsList;
			} else if (type == 'dimension') {
				if (config.oneLayer) {
					tier = 1;
				} else if (config.twoLayer) {
					tier = 2;
				}
				tierData = ['dimensionList'];
				dataList = scope.filter.dimensionsOriginalList;
				backFun = scope.resetDimensionsList;
			}
			collapseFun = scope.collapseAllItem;

			// window.searchData(key,tier,tierData,dataList,backFun,collapseFun);
			uiSearch.search(key, tier, tierData, dataList, backFun, collapseFun);

			//搜索计算指标列表
			if(scope.myOptions.calculatedValueOriginalList.length>0){
				uiSearch.search(key, 1, ['name'], scope.myOptions.calculatedValueOriginalList, scope.resetCalculatedValueList, collapseFun);
			}
		};

		/**
		 * 搜索框点击事件
		 */
		scope.searchIptClick = function(index){
			scope.resetDimensionsList(scope.filter.dimensionsOriginalList);
			scope.resetCalculatedValueList( scope.myOptions.calculatedValueOriginalList);
			scope.filter.searchKey[index];
			scope.collapseAllItem(false)
		};

		/**
		 * 过滤器选择自定义过滤器时，第一步选择维度指标的时候，判断第二步应该显示那些规则
		 * @param datatype NUMBER | CURRENCY | PERCENT | DURATION | DATA | TIMESTAMP | TIME | STRING google adwords 没有这个字段
		 * @param box 过滤器的所在这一行
		 * @param $index 一个过滤器的第$index个条件,最多有5个条件，所以最大为4
		 * @param dsId
		 * @param type google adwords 才有这个字段 metric | dimension
		 * @param i 第一个选择项中的index，比如，第一个条件有10个选项，当在第一个选项中选了第三个的时候，i = 2
		 */
		scope.selectGDDataType = function (content, box, $index, dsId, i) {
			filter.dsDateTypeSelected[$index] = true;
			filter.getCheckboxState = true;
			filter.googleAdwordsStringFilter[$index] = false;
			if(content.i18nCode){//保存国际化信息
				box.condition[$index]['i18nCode'] = content.i18nCode;
			}
			if(dsId!=1 && dsId!=3 && dsId!=12 && dsId!=13 && dsId!=21 && dsId!=22 && content.dataType){
				var datatype = content.dataType;
				switch (datatype) {
					case 'NUMBER':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[6];
						box.condition[$index].op = scope.containerList[6].code;
						break;
					case 'INTEGER':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[6];
						box.condition[$index].op = scope.containerList[6].code;
						break;
					case 'DOUBLE':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[6];
						box.condition[$index].op = scope.containerList[6].code;
						break;
					case 'FLOAT':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[6];
						box.condition[$index].op = scope.containerList[6].code;
						break;
					case 'LONG':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[6];
						box.condition[$index].op = scope.containerList[6].code;
						break;
					case 'CURRENCY':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[6];
						box.condition[$index].op = scope.containerList[6].code;
						break;
					case 'PERCENT':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[6];
						box.condition[$index].op = scope.containerList[6].code;
						break;
					case 'DURATION':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[6];
						box.condition[$index].op = scope.containerList[6].code;
						break;
					case 'DATE':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[7];
						box.condition[$index].op = scope.containerList[7].code;
						break;
					case 'TIMESTAMP':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[7];
						box.condition[$index].op = scope.containerList[7].code;
						break;
					case 'DATETIME':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[7];
						box.condition[$index].op = scope.containerList[7].code;
						break;
					case 'TIME':
						filter.dsDateType = 'number';
						filter.gaContainerList[$index] = scope.containerList[7];
						box.condition[$index].op = scope.containerList[7].code;
						break;
					case 'STRING':
						filter.dsDateType = 'text';
						filter.gaContainerList[$index] = scope.containerList[14];
						box.condition[$index].op = scope.containerList[14].code;
						break;
					case 'CUSTOM':
						filter.dsDateType = 'text';
						filter.gaContainerList[$index] = scope.containerList[14];
						box.condition[$index].op = scope.containerList[14].code;
						break;
				}
				if(dsId == 19){//salesforce数据源的object类型，默认都是不等于
					if((''+scope.modal.editorNow.variables[0].profileId).indexOf('|join|') === -1){
						filter.gaContainerList[$index] = scope.containerList[27];
						box.condition[$index].op = scope.containerList[27].code;
						box.condition[$index].dataType = datatype;
					}
				}else if(dsId == 28){
					//googleadsense数据源，当维度的dataType不为空时，使用==作为默认操作符
					filter.gaContainerList[$index] = scope.containerList[2];
					box.condition[$index].op = scope.containerList[2].code;
					box.condition[$index].dataType = datatype;
				}else if(dsId == 31 && (datatype === 'STRING' || datatype === 'DATE')){
					//yahoo ads ss 数据源的String、Date类型的支持EQUALS/NOT_EQUALS/CONTAINS
					filter.gaContainerList[$index] = scope.containerList[12];
					box.condition[$index].op = scope.containerList[12].code;
					box.condition[$index].dataType = datatype;
				}
			}
			if (content.type) {
				var type = content.type;

				if ([1,3,12,13,21,22].indexOf(+dsId)>=0 && ['dimension'].indexOf(type)>=0) {
					filter.valueList[$index] = '';//清空value值，防止重新选择的时候会留下痕迹
					box.condition[$index].value = '';//清空value值，防止重新选择的时候会留下痕迹
					box.condition[$index].type = 'dimension';
					//因为搜索时，会导致dimensionsList变化，连带i也改变，因此需要更新i的值
					filter.dimensionsOriginalList.forEach(function(item,index,arr){
						if(item.code === content.code && item.id === content.id){
							i = index;
						}
					});
					filter.dsDateTypeSelected[$index] = false;
					filter.googleAdwordsStringFilter[$index] = true;
					if (scope.editor.dsId == 1) {
						box.condition[$index].op = scope.containerList[2].code;// 包含
						filter.gaContainerList[$index] = scope.containerList[2];// 包含
						filter.googleAdwordsStringFilter[$index] = false;
					} else if (dsId == 13 || dsId == 12) {
						filter.gaContainerList[$index] = scope.containerList[20];// in
					}
					if (filter.googleAdwordsItem.length >= $index) {
						filter.googleAdwordsItem.splice($index, 1, i);//维护一个复选框的选择列表
					} else if (filter.googleAdwordsItem.length < $index) {
						filter.googleAdwordsItem.push(i);
					}
					filter.googleAdwordsCheckBox[$index] = [];
					angular.forEach(filter.filterItemList[filter.googleAdwordsItem[$index]], function (con, index) {
						filter.googleAdwordsCheckBox[$index].push(false);
					});


					if (!filter.filterItemList[i]) {
						var variables = scope.modal.editorNow.variables[0];
						scope.loadSetting.widget1 = true;
						var filterItemData = {
							"dsId": dsId,
							"dsCode": scope.editor.dsCode,
							"connectionId": variables.connectionId,
							"accountName": variables.accountName,
							"profileId": variables.profileId,
							"dimensionId": content.id,
							"dateKey": scope.modal.editorNow.baseWidget.dateKey,
							"uid": $rootScope.userInfo.ptId
						};
						dataMutualSrv.post(LINK_FILTER_DIMENSION_VALUE_LIST, filterItemData).then(function (data) {
							if (data.status == 'success') {
								filter.getCheckboxState = false;
								filter.filterItemList[i] = data.content.filterItemList;
								angular.forEach(data.content.filterItemList, function () {
									filter.googleAdwordsCheckBox[$index].push(false);
								});
							} else if (data.status == 'failed') {
								console.log('Post Data Failed!')
							} else if (data.status == 'error') {
								console.log('Post Data Error: ');
								console.log(data.message);
							}
							scope.loadSetting.widget1 = false;
						});
					}else{
						filter.getCheckboxState = false;
					}

				} else if ([1,3,12,13,21,22].indexOf(+dsId)>=0 && ['metrics', 'compoundMetrics'].indexOf(type)>=0) {
					filter.valueList[$index] = '';//清空value值，防止重新选择的时候会留下痕迹
					box.condition[$index].value = '';//清空value值，防止重新选择的时候会留下痕迹
					box.condition[$index].type = 'metrics';
					filter.googleAdwordsCheckBox[$index] = [];//这个只用用来占位置的，所以，弄个空数组
					filter.googleAdwordsItem.push($index);//这个只是用来占位置的，没有实际用处
					filter.dsDateTypeSelected[$index] = true;
					filter.googleAdwordsStringFilter[$index] = false;
					filter.dsDateType = 'number';
					//用于设置操作符默认显示
					if (scope.editor.dsId == 1) {
						filter.gaContainerList[$index] = scope.containerList[6];// ==
					} else if (dsId == 3) {
						filter.gaContainerList[$index] = scope.containerList[6];
					} else if (dsId == 13) {
						filter.gaContainerList[$index] = scope.containerList[20];// in
					} else if (dsId == 12) {
						filter.gaContainerList[$index] = scope.containerList[22];// <
					}
					//box.condition[$index].op = scope.containerList[6].code;
					box.condition[$index].op = filter.gaContainerList[$index].code;
				}
			}

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', 'select_filter_condition');

			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where: 'widget_editor_metrics_' + scope.editor.filtersType,
				what: 'select_condition',
				how: 'click',
				value: content.id
			});
		};
		//添加过滤器的第二步，规则的显示
		scope.showRegExp = function ($index, box) {
			if (box) {
				if (scope.editor.dsId == 1) {//Google Analytics
					if(box.type == 'metrics'){
						return [27,28,6,7,10,11].indexOf($index)>=0;
					} else {
						//不需要区分细分和过滤器，GA的维度都是一样的过滤条件
						if(scope.dataSettings.filterScope === 'segment'){
							return [0,1,2,3,4,5,6,7,10,11,27,28].indexOf($index)>=0;
						}else if(scope.dataSettings.filterScope === 'filters'){
							return [0,1,2,3,4,5].indexOf($index)>=0;
						}
					}
				}
				if (scope.editor.dsId == 3) {//Google Adwords
					return $index > 5 && $index < 12;
				}
				if (scope.editor.dsId == 13) {//Ptengine
					return $index == 20 || $index == 21;
				}
				if (scope.editor.dsId == 12) {//Facebook Ads
					return ($index >= 22 && $index <= 26) || $index == 27;
				}
				if (scope.editor.dsId == 19) {//Salesforce
					var profileId = scope.modal.editorNow.variables[0].profileId;
					var isObject = (''+profileId).indexOf('|join|') === -1;
					if(isObject){//salesforce数据源的Object类型特殊处理
						if (box.dataType && ['NUMBER','CURRENCY','PERCENT','DURATION','DATE','TIME','TIMESTAMP','DATETIME'].indexOf(box.dataType) > -1 && [27, 6, 7, 8, 10, 11].indexOf($index) >= 0) {
							return true;
						} else if (box.dataType && (box.dataType == 'STRING' || box.dataType == 'BOOLEAN') && [27, 8].indexOf($index) >= 0) {
							return true;
						}
					}else{
						if (box.dataType && ['NUMBER','CURRENCY','PERCENT','DURATION','DATE','TIME','TIMESTAMP','DATETIME'].indexOf(box.dataType) > -1 && [27, 6, 7, 8, 10, 11].indexOf($index) >= 0) {
							return true;
						} else if (box.dataType && box.dataType == 'STRING' && [12,13,14,15,16,17,18,19].indexOf($index) >= 0) {
							return true;
						} else if(box.dataType && box.dataType == 'BOOLEAN' && [27, 8].indexOf($index) >= 0){
							return true;
						}
					}
					return false;
				}
				if(scope.editor.dsId == 28){//Google Adsense
					//googleadsense数据源使用== 和 =@
					return $index == 0 || $index == 2;
				}
				if(box.dataType && ['NUMBER','CURRENCY','PERCENT','DURATION'].indexOf(box.dataType) > -1){//这里是当编辑过滤器时，已经存在dataType这个字段，就应该根据dataType显示第二步的选项
					if(scope.dsConfig.editor.filter.needIsNull){
						return  [6,7,8,9,10,11,29,30].indexOf($index) > -1;
					}else{
						return $index > 5 && $index < 12;
					}
				}
				if(scope.editor.dsId !== 31 && box.dataType && ['DATE','TIME','TIMESTAMP','DATETIME'].indexOf(box.dataType) > -1 ){ // Yahoo Ads Sponsored Search
					if(scope.dsConfig.editor.filter.needIsNull){
						return  [7,8,9,10,29,30].indexOf($index) > -1;
					}else{
						return $index > 6 && $index < 11;
					}
				}
				if(box.dataType && box.dataType === 'STRING' && scope.editor.dsId === 215){
					return $index < 6 || ($index > 15 && $index < 20);
				}
				if(box.dataType && box.dataType !== 'STRING' && box.dataType !== 'CUSTOM' && scope.editor.dsId === 215){
					return $index > 5 && $index < 12;
				}
				if(box.dataType && box.dataType === 'CUSTOM' && scope.editor.dsId === 215){
					return $index < 12 || ($index > 15 && $index < 20);
				}
				if(scope.editor.dsId == 31 && box.dataType && (box.dataType == 'DATE'||  box.dataType == 'STRING')) {
					return $index > 11 && $index < 15;
				}
				if(scope.dsConfig.editor.filter.needIsNull){
					return  box.dataType && box.dataType == 'STRING' && [12,13,14,15,16,17,18,19,29,30].indexOf($index) > -1;
				}else{
					return box.dataType && box.dataType == 'STRING' && $index > 11 && $index < 20;
				}
			}
		};

		/**
		 * 选择google adwords 过滤器的复选框
		 * @param box
		 * @param $index 此过滤器的第$index个条件，最多5个，也就是最大值是4
		 */
		scope.selectGoogleAdwordsFilter = function (box, $index) {
			var ptoneDsInfoId = scope.modal.editorNow.variables[0].ptoneDsInfoId;
			box.condition[$index].checked = filter.googleAdwordsCheckBox[$index].toString();//将选中状态保存到服务器中
			if (scope.editor.dsId == 3) {
				box.condition[$index].op = 'in';//adwords code为in
			} else {
				box.condition[$index].op = filter.gaContainerList[$index].code;
			}
			filter.valueList[$index] = '';
			box.condition[$index].value = '';
			angular.forEach(filter.googleAdwordsCheckBox[$index], function (con, index) {
				if (con) {
					//给前台显示的是name
					filter.valueList[$index] += (filter.filterItemList[filter.googleAdwordsItem[$index]][index].name + ',');
					//给后台传的值是code
					box.condition[$index].value += (filter.filterItemList[filter.googleAdwordsItem[$index]][index].code + ',');
				}
			});
		};

		/**
		 * 获取自定义变量列表
		 * @param i18nCode 获取自定义列表的名字
		 * @param fold  折叠状态
		 * @param $index
		 * @param type
		 */
		scope.isCustomVariable = function(i18nCode,fold,$index,type){//当处于折叠状态时，点击去获取列表，已展开状态不获取
			if(i18nCode && (i18nCode === 'METRICS_CATEGOTY.PTAPP.CUSTOMVARIABLES' || i18nCode === 'DIMENSION_CATEGOTY.PTAPP.CUSTOMVARIABLES') && fold){
				var url = LINK_APP_GET_USER_VAR_KEY_NAME +  scope.modal.editorNow.variables[0].connectionId + '/'+ scope.modal.editorNow.variables[0].dsCode + '/' + scope.modal.editorNow.variables[0].accountName + '/' +scope.modal.editorNow.variables[0].profileId + '/'+ 'metrics' ;
				//获取自定义变量列表
				dataMutualSrv.get(url).then(function (data) {
					if (data.status == 'success') {
						if(data.content && data.content.length > 0){
							scope.dimensionsList[$index][type] = data.content[0].metricsList;
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

		//获取过滤器第一个列表
		function getFilterList(){
			var localStorageKey = scope.editor.dsCode + '-' + scope.dataSettings.filterScope;
			var url = LINK_SEGMENT_LIST+scope.editor.dsId + '/' + scope.filter.filterBigType;
			dataMutualSrv.get(url).then(function (data) {
				if (data.status == 'success') {
					dimensionsInit(cleanContent(data.content));
					if (scope.dsConfig.editor.filter.isCache) {
						// localStorage 缓存过滤器列表
						$localStorage[localStorageKey] = {
							'v': scope.rootCommon.dataVersion[localStorageKey],
							'd': data.content
						}
					}
				} else {
					if (data.status == 'failed') {
						console.log('Post Data Failed!')
					} else if (data.status == 'error') {
						console.log('Post Data Error: ');
						console.log(data.message);
					}
				}
			});
		}


		//切换用户或者会话
		scope.changeFilterBigType = function(code){
			if(!scope.dsConfig.editor.filter.reGetFilterList) return false;
			if(code === 'user'){
				if(scope.filter.filterBigType  !== 0){
					scope.filter.filterBigType = 0;
					getFilterList();
				}
			}else if(code === 'session'){
				if(scope.filter.filterBigType !== 1){
					scope.filter.filterBigType = 1;
					getFilterList();
				}
			}
		};



		//GTM
		scope.setGtm = function (type) {
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_metric', type);

			//全站事件统计
			Track.log({where: 'widget_editor_metrics_' + scope.editor.filtersType, what: type});
		};

		/**
		 * 处理已保存过滤器，第一个input的国际化
		 * @param key
		 * @param userIndex
		 */
		scope.translateSearchKey = function(key,userIndex){
			var temp = false;
			filter.newData[0].condition.forEach(function(item,index,arr){
				if(key === item.name && item.i18nCode){
					temp = true;
					filter.searchKey[userIndex] = $translate.instant(item.i18nCode);
				}
			});
			if(!temp){
				filter.searchKey[userIndex] = key;
			}
		};


		/**
		 * Filter Init
		 */
		(function () {
			//过滤器下不会有saved的情况存在
			if (scope.dataSettings.filterScope == 'filters') {
				scope.filter.type = 'new';
				scope.myOptions.searchPlaceholder = $translate.instant('WIDGET.EDITOR.FILTER.TIP2');
			} else {
				scope.myOptions.searchPlaceholder = $translate.instant('WIDGET.EDITOR.FILTER.SEGMENT_SELECT');

				if (scope.editor.dsId == 1) {
					//获取GA Segments List(已保存的过滤器列表)

					scope.loadSetting.widget = true;
					dataMutualSrv.get(LINK_SEGMENTS_GA + scope.modal.editorNow.variables[0].accountName).then(function (data) {
						if (data.status == 'success') {
							scope.loadSetting.widget = false;

							filter.segmentsList = data.content;
							scope.segmentsList = angular.copy(filter.segmentsList);

							if (scope.dataSettings.filterType == 'edit') {

								filter.type = scope.dataSettings.currentFilter.type;
								filter.savedData = angular.copy(scope.dataSettings.currentFilter.savedData);
								for (var i = 0; i < scope.segmentsList.length; i++) {
									if (scope.segmentsList[i].segmentId == filter.savedData[0]) {
										filter.segment = scope.segmentsList[i];
										break;
									}
								}

								scope.myOptions.segmentsSearchKey = filter && filter.segment && filter.segment.name;
							}
						} else {
							if (data.status == 'failed') {
								console.log('Post Data Failed!');
							} else if (data.status == 'error') {
								console.log('Post Data Error: ');
								console.log(data.message)
							}
						}
					});
				}
			}

			//获取GA或者GD维度列表-原始数据
			scope.loadSetting.widget1 = true;
			var localStorageKey = scope.editor.dsCode + '-' + scope.dataSettings.filterScope;
			if (scope.dsConfig.editor.filter.isCache && $localStorage[localStorageKey] && $localStorage[localStorageKey].v == scope.rootCommon.dataVersion[localStorageKey]) {

				//dimensionsInit(angular.copy($localStorage[localStorageKey].d));
				dimensionsInit(cleanContent(angular.copy($localStorage[localStorageKey].d)));
			} else {

				var url;
				if(scope.dataSettings.filterScope == 'filters'){
					if(scope.editor.dsId == 19) {
						var salesforceFlag = 'Object';
						var profileId = scope.modal.editorNow.variables[0].profileId;
						var connectionId = scope.modal.editorNow.variables[0].connectionId;
						var accountName = scope.modal.editorNow.variables[0].accountName;
						url = LINK_SALESFORCE_METRICES_AND_DIMENSION + "/" + profileId + "/" + connectionId+ '/' + accountName +"/true";
						if((profileId + '').indexOf('Summary') > -1 || (profileId + '').indexOf('Matrix') > -1 || (profileId + '').indexOf('Tabular') > -1){
							salesforceFlag = 'Report';
						}
					} else if (scope.dsConfig.editor.filter.getForDimensionsAndMetrics) {
						url = LINK_FILTER_LIST + scope.editor.dsId;
					} else {
						url = LINK_USER_METRICS_AND_DIMENSIONS + scope.editor.dsId + '/' + scope.modal.editorNow.variables[0].profileId;
					}
				} else {
					url = LINK_SEGMENT_LIST+scope.editor.dsId + '/' + 1;//默认是session级
				}

				dataMutualSrv.get(url).then(function (data) {
					if (data.status == 'success') {
						if (scope.editor.dsId == 19) {
							//salesforce数据源需要特殊处理
							if(salesforceFlag === 'Object'){
								var _dimensionsContent = [];
								for (var i = 0; i < data.content.length; i++) {
									var dimensionsCategory = data.content[i];
									dimensionsCategory.dimensionList = dimensionsCategory.metricsList;
									dimensionsCategory.metricsList = [];
									_dimensionsContent.push(dimensionsCategory);
								}
								dimensionsInit(_dimensionsContent);
								scope.dsConfig.editor.filter.twoLayer = true;
								scope.dsConfig.editor.filter.oneLayer = false;
							}else if(salesforceFlag === 'Report'){
								if(data.content.length > 0){
									var reportType = data.content[0].name;
									var dimensionList = data.content[0].dimensionList;
									var metricsList = data.content[0].metricsList;
									var spliter = [{//分割线
										type:"spliter"
									}];
									var filterList =[];
									if(reportType === 'Summary' || reportType === 'Matrix'){//Summary 和 Matrix类型的需要区分指标和维度，中间需要有个分割线
										filterList = dimensionList.concat(spliter,metricsList);
									}else{//Object类型，report类型的Tabular都是指标和维度相同，不需要分割线，并且只用指标列表或者维度列表就行了（否则就重复了）
										filterList = dimensionList;
									}
									dimensionsInit(filterList);
									scope.dsConfig.editor.filter.twoLayer = false;
									scope.dsConfig.editor.filter.oneLayer = true;
								}

							}
						} else {
							dimensionsInit(cleanContent(data.content));
						}

						if (scope.dsConfig.editor.filter.isCache) {
							// localStorage 缓存过滤器列表
							$localStorage[localStorageKey] = {
								'v': scope.rootCommon.dataVersion[localStorageKey],
								'd': data.content
							}
						}
					} else {
						if (data.status == 'failed') {
							console.log('Post Data Failed!')
						} else if (data.status == 'error') {
							console.log('Post Data Error: ');
							console.log(data.message);
						}
					}
				});
			}
		})();
	}
}

export default widgetFilter;

