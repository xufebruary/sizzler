import tpl from './time.tpl.html';
import './time.css';

import {
	LINK_USER_DIMENSIONS,
	LINK_SALESFORCE_METRICES_AND_DIMENSION,
	getBeforeDay,
	LINK_DIMENSIONS_GA,
	uuid
}from '../../../common/common';


editorTime.$inject = ['dataMutualSrv', 'siteEventAnalyticsSrv','Track'];
function editorTime(dataMutualSrv, siteEventAnalyticsSrv,Track) {
	return {
		restrict: 'EA',
		template: tpl,
		link: link
	};

	function link(scope, elem, attrs) {

		//监听气泡显示状态
		// scope.$watch('editor.pop.name', function(value) {
		//     if (value && value == 'time') {
		//         $document.bind('click', documentClickBindTime);
		//     } else {
		//         $document.unbind('click', documentClickBindTime);
		//     }
		// });
		var documentClickBindTime = function (event) {
			if (timeSettings.datePickerHideClick) {
				timeSettings.datePickerHideClick = false;
			} else if (scope.editor.pop.show && typeof(angular.element(event.target).attr('step-time')) == 'undefined' && !elem[0].contains(event.target)) {
				scope.$apply(function () {
					scope.editor.pop.name = null;
					scope.editor.pop.show = false;
					scope.editor.pop.ele = null;
				});
			}
		};


		var timeSettings = scope.timeSettings = {
			modelTime: null,
			nowDate: getBeforeDay(0).format('MM/dd/yyyy'),

			//last days
			modelLastDays: 7,
			modelLastDaysTmp: 7,//暂存上次输入数字,以备校验时用
			modelToday: 'past', //Include(last) || Exclude(past)
			timeError: [false],

			//form today
			modelFrom: getBeforeDay(8).format('MM/dd/yyyy'),
			dpFromShow: false,

			//Fixed Time Range
			modelFixed: [getBeforeDay(8).format('MM/dd/yyyy'), getBeforeDay(1).format('MM/dd/yyyy')],
			dpFixedShow: false,

			//other
			datePickerHideClick: false,    //排除删除及新增操作时的dom关闭事件

			//新增维度time字段
			dateDimensionList: [],
			dataDimensionDefaultName: 'select a dimension time',

			//目前salesforce专用的二级列表
			categoryList: [],
			dimensionIndex: {},//控制下拉的打开与否
			dimensionExpansionIndex: {}//控制子展开与否
		};

		//数据初始化
		dataInit();


		//监听时间类型
		scope.$watch('timeSettings.modelTime', function (newValue, oldValue) {
			if (newValue !== oldValue) {
				scope.modal.editorNow.baseWidget.dateKey = timeSettings.modelTime;
				var needPtLog = true;

				if (timeSettings.modelTime == 'last_days') {
					scope.modal.editorNow.baseWidget.dateKey = timeSettings.modelToday + timeSettings.modelLastDays + 'day';
					//全站事件统计
					Track.log({where: 'widget_editor_time', what: 'select_' + newValue, value: timeSettings.modelLastDays });
					needPtLog = false;
				}

				if (timeSettings.modelTime == 'from_today') {
					timeSettings.dpFromShow = true;

					scope.modal.editorNow.baseWidget.dateKey = new Date(timeSettings.modelFrom).format('yyyy-MM-dd') + '|today';

					//全站事件统计
					Track.log({where: 'widget_editor_time', what: 'select_' + newValue, value: timeSettings.modelFrom });
					needPtLog = false;
				} else {
					timeSettings.dpFromShow = false;
				}

				if (timeSettings.modelTime == 'fixed') {
					timeSettings.dpFixedShow = true;

					scope.modal.editorNow.baseWidget.dateKey = new Date(timeSettings.modelFixed[0]).format('yyyy-MM-dd') + '|' + new Date(timeSettings.modelFixed[1]).format('yyyy-MM-dd');

					//GTM
					siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_time', 'fixed_time_range');

					//全站事件统计
					Track.log({where: 'widget_editor_time', what: 'select_' + newValue, value: timeSettings.modelFixed[0] + ',' + timeSettings.modelFixed[1] });
					needPtLog = false;
				} else {
					timeSettings.dpFixedShow = false;

					//GTM
					siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_time', 'dynamic_time_range');
				}

				if (timeSettings.modelTime == 'all_time') {
					timeSettings.dpFixedShow = false;

					scope.modal.editorNow.baseWidget.dateKey = 'all_time';
				}

				//全站事件统计
				if(needPtLog){
					Track.log({where: 'widget_editor_time', what: 'select_' + newValue});
				}


				//save
				scope.saveData('time-changeTime');
			}
		}, true);

		//监听Incloud || Excloud
		scope.$watch('timeSettings.modelToday', function (newValue, oldValue) {
			if (newValue !== oldValue) {
				scope.modal.editorNow.baseWidget.dateKey = timeSettings.modelToday + timeSettings.modelLastDays + 'day';

				//save
				scope.saveData('time-inExcloud');
			}
		}, true);

		//Last days set
		//scope.lastDaysSet = function (value) {
		//    var reg = new RegExp("^[1-9][0-9]{0,2}$");
		//
		//    if (reg.test(value)) {
		//        timeSettings.timeError[0] = false;
		//        scope.modal.editorNow.baseWidget.dateKey = timeSettings.modelToday + timeSettings.modelLastDays + 'day';
		//
		//        //save
		//        scope.saveData('time-lastDaysSet');
		//    } else {
		//        timeSettings.timeError[0] = true;
		//    }
		//};

		//过去期间输入校验
		var $inputTime = $('#inputTime'),
			MAX_NUMBER = 999;
		scope.lastDaysSet = function (type) {
			// 删除完为空, 有特殊字符如e,则为undefined
			var iptValue = timeSettings.modelLastDays;
			var reg = new RegExp("^[1-9][0-9]{0,2}$");

			if (iptValue && !reg.test(iptValue)) {
				timeSettings.modelLastDays = timeSettings.modelLastDaysTmp;
			} else {
				if (type == 'change') {
					if (iptValue != null) {
						iptValue = +iptValue;
						timeSettings.modelLastDaysTmp = iptValue;
						//全站事件统计
						Track.log({where: 'widget_editor_time', what: 'select_last_days',value: iptValue});
					}else{
						if(iptValue === undefined){ // 数据不合法
							timeSettings.modelLastDays = timeSettings.modelLastDaysTmp;
						}
					}
				} else if (type == 'blur') {
					if (iptValue == null) { //为空或者数据不合法
						timeSettings.modelLastDays = timeSettings.modelLastDaysTmp;
					}
				}
			}

			//save
			if(timeSettings.modelLastDays){
				scope.modal.editorNow.baseWidget.dateKey = timeSettings.modelToday + timeSettings.modelLastDays + 'day';
				scope.saveData('time-lastDaysSet');
			}

		};

		//修改日历时间
		scope.dateChange = function (type, date) {
			if (type == 'from') {
				timeSettings.modelFrom = date;
				scope.modal.editorNow.baseWidget.dateKey = new Date(timeSettings.modelFrom).format('yyyy-MM-dd') + '|today';
				//全站事件统计
				Track.log({where: 'widget_editor_time', what: 'select_from_today',value: date});
			} else if (type == 'fixed') {
				timeSettings.modelFixed = date.split(',');
				scope.modal.editorNow.baseWidget.dateKey = new Date(timeSettings.modelFixed[0]).format('yyyy-MM-dd') + '|' + new Date(timeSettings.modelFixed[1]).format('yyyy-MM-dd');
				//全站事件统计
				Track.log({where: 'widget_editor_time', what: 'select_fixed',value: timeSettings.modelFixed[0] + ',' + timeSettings.modelFixed[1]});
			}

			scope.$apply(function () {
				timeSettings
			});

			//save
			scope.saveData('time-dateChange');
		};


		//数据初始化
		function dataInit() {
			var ptoneDsInfoId = scope.modal.editorNow.variables[0].ptoneDsInfoId;
			var dataKey = scope.modal.editorNow.baseWidget.dateKey;
			var config = scope.dsConfig.editor.time;
			if (dataKey === null || (config.defaultSelectTime == 0 && dataKey == 'all_time')) {
				if (config.defaultSelectTime == 0) {
					//除gd外默认选中过去7天不包含今天
					timeSettings.modelTime = 'last_days';
					timeSettings.modelLastDays = 7;
					timeSettings.modelLastDaysTmp = 7;
					timeSettings.modelToday = 'past';

					scope.modal.editorNow.baseWidget.dateKey = 'past7day';

					//}else if(ptoneDsInfoId == 6){
				} else if (config.time.defaultSelectTime == 1) {
					//gd默认选中all time
					timeSettings.modelTime = 'all_time';
					scope.modal.editorNow.baseWidget.dateKey = 'all_time';
				}
			} else {
				if (dataKey.indexOf('|') > -1) {
					var sdt = dataKey.split('|')[0];
					var edt = dataKey.split('|')[1];

					if (edt == 'today') {
						timeSettings.modelTime = 'from_today';
						timeSettings.modelFrom = new Date(sdt).format('MM/dd/yyyy');
					} else {
						timeSettings.modelTime = 'fixed';

						timeSettings.modelFixed = [new Date(sdt).format('MM/dd/yyyy'), new Date(edt).format('MM/dd/yyyy')];
					}
				} else if (dataKey.indexOf('last') == 0 && dataKey.indexOf('last_') < 0 || dataKey.indexOf('past') == 0) {
					timeSettings.modelTime = 'last_days';
					timeSettings.modelLastDays = +dataKey.match(/\d+/g); //提取具体天数
					timeSettings.modelLastDaysTmp = +dataKey.match(/\d+/g); //提取具体天数
					timeSettings.modelToday = dataKey.indexOf('last') == 0 ? 'last' : 'past';
				} else {
					if (scope.modal.editorNow.variables[0].ptoneDsInfoId == 1) {
						timeSettings.modelTime = dataKey;
					} else {//gd默认选中all time
						if (dataKey) {
							timeSettings.modelTime = dataKey;
						} else {
							timeSettings.modelTime = 'all_time';
						}
					}
				}
			}


			//获取时间维度
			getDataDimension();
		}

		function getDataDimension() {
			var dimensionsUrl = LINK_USER_DIMENSIONS + scope.modal.editorNow.variables[0].ptoneDsInfoId + '/' + scope.modal.editorNow.variables[0].profileId;
			if (scope.modal.editorNow.variables[0].ptoneDsInfoId == 19) {//saleforce接口
				dimensionsUrl = LINK_SALESFORCE_METRICES_AND_DIMENSION + '/' + scope.modal.editorNow.variables[0].profileId + '/' + scope.modal.editorNow.variables[0].connectionId + '/' + scope.modal.editorNow.variables[0].accountName +  '/false';
			}else if(scope.modal.editorNow.variables[0].ptoneDsInfoId == 27){//MailChimp接口
				dimensionsUrl = LINK_DIMENSIONS_GA + scope.modal.editorNow.variables[0].ptoneDsInfoId;
			}
			dataMutualSrv.get(dimensionsUrl).then(function (data) {
				if (data.status == 'success') {
					if (scope.modal.editorNow.variables[0].ptoneDsInfoId == 19) {
						for (var i = 0; i < data.content.length; i++) {
							var dimensionsCategory = data.content[i];
							var hasDateDimension = false;
							var _dateDimensionsList = [];
							for (var j = 0; j < dimensionsCategory.metricsList.length; j++) {
								var dimension = dimensionsCategory.metricsList[j];
								if (dimension.dataType == 'DATETIME' || dimension.dataType == 'DATE') {
									hasDateDimension = true;
									timeSettings.dateDimensionList.push(dimension);
									_dateDimensionsList.push(dimension);
									if (scope.modal.editorNow.variables[0].dateDimensionId === dimension.code + '|join|' + dimension.dataType) {
										timeSettings.dataDimensionDefaultName = dimensionsCategory.name + "-" + dimension.name;
									}
								}
							}
							if (hasDateDimension) {
								//有时间维度的分类，放到categoryList中
								dimensionsCategory.metricsList = [];
								dimensionsCategory.dimensionList = _dateDimensionsList;
								timeSettings.categoryList.push(dimensionsCategory);
							}

						}
						if (timeSettings.dateDimensionList.length > 0 && !scope.modal.editorNow.variables[0].dateDimensionId) {//如果有，就默认选中第一个
							scope.modal.editorNow.variables[0].dateDimensionId = timeSettings.dateDimensionList[0].code + '|join|' + timeSettings.dateDimensionList[0].dataType;
							scope.selectDimensionTime(timeSettings.dateDimensionList[0].code + '|join|' + timeSettings.dateDimensionList[0].dataType, timeSettings.dateDimensionList[0].name);
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
									mailChimpDimention.forEach(function(item,index){
										if(item.isShowOnTimeDropdowns === 1 && (item.dataType == 'DATE' || item.dataType == 'TIMESTAMP' || item.dataType == 'DATETIME')){
											timeSettings.dateDimensionList.push(mailChimpDimention[index]);
											if (scope.modal.editorNow.variables[0].dateDimensionId === item.code) {
												timeSettings.dataDimensionDefaultName = item.name;
											}
										}
									});
								}

							}
							if(timeSettings.dateDimensionList.length > 0 && !scope.modal.editorNow.variables[0].dateDimensionId){
								scope.modal.editorNow.variables[0].dateDimensionId = timeSettings.dateDimensionList[0].code;
								scope.selectDimensionTime(timeSettings.dateDimensionList[0].code, timeSettings.dateDimensionList[0].name,timeSettings.dateDimensionList[0]);
							}
					} else{
						for (var i = 0; i < data.content.length; i++) {
							if (data.content[i].dataType == 'DATE' || data.content[i].dataType == 'TIMESTAMP' || data.content[i].dataType == 'DATETIME') {
								timeSettings.dateDimensionList.push(data.content[i]);
								if (scope.modal.editorNow.variables[0].dateDimensionId === data.content[i].id) {
									timeSettings.dataDimensionDefaultName = data.content[i].name;
								}
							}
						}
						if (timeSettings.dateDimensionList.length > 0 && !scope.modal.editorNow.variables[0].dateDimensionId) {//如果有，就默认选中第一个
							scope.modal.editorNow.variables[0].dateDimensionId = timeSettings.dateDimensionList[0].id;
							scope.selectDimensionTime(timeSettings.dateDimensionList[0].id, timeSettings.dateDimensionList[0].name);
						}
					}

				} else if (data.status == 'failed') {
					console.log('Post Data Failed!');
				} else if (data.status == 'error') {
					console.log('Post Data Error: ');
					console.log(data.message);
				}
			});
		}

		//选择时间维度
		scope.selectDimensionTime = function (id, name,item) {
			if(scope.modal.editorNow.variables[0].ptoneDsInfoId == 27){
				scope.modal.editorNow.variables[0].dateDimensionId = item.code;
				timeSettings.dataDimensionDefaultName = item.name;
				scope.saveData('time-selectDimensionTime');
				//全站事件统计
				Track.log({where: 'widget_editor_time', what: 'select_time',value: item.name});
			}else{
				scope.modal.editorNow.variables[0].dateDimensionId = id;
				timeSettings.dataDimensionDefaultName = name;
				scope.saveData('time-selectDimensionTime');
				//全站事件统计
				Track.log({where: 'widget_editor_time', what: 'select_time',value: name});
			}
			

			
		}

	}
}

export default editorTime;
