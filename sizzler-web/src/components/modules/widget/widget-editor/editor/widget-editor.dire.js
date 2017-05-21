import {
	LINK_WIDGET_EDIT,
	LINK_USER_DIMENSIONS,
	LINK_DIMENSIONS_GA,
	getMyDsConfig,
	uuid
} from 'components/modules/common/common';

import tpl from './widget-editor.tpl.html';
import './widget-editor.css';

/**
 * widgetEditor
 * widget编辑器
 */

widgeteditor.$inject = ['$rootScope', '$state', '$translate', 'dataMutualSrv', 'siteEventAnalyticsSrv','sysRoles', 'gridstackService', 'WidgetResources','UserResources'];
function widgeteditor($rootScope, $state, $translate, dataMutualSrv, siteEventAnalyticsSrv, sysRoles, gridstackService, WidgetResources,UserResources) {
	// 'use strict';

	return {
		restrict: 'EA',
		replace: true,
		template: tpl,
		link: link
	};

	function link(scope, elem, attrs) {

		//widget的数据源设置,widget editor都可以使用
		scope.dsConfig = getMyDsConfig(scope.modal.editorNow.variables[0].dsCode);

		//数据源配置信息
		scope.dsData = {
			dsInfo: getDsInfo(scope.modal.editorNow.variables[0].dsCode)
		};

		scope.editor = {
			'filtersType':'',//选择的过滤器类别 filter/segment
			'pop': {
				'name': null,   //account, data, time, chart
				'show': false
			},
			'disabled': [false, false, false, false],//数据源、指标、时间、图形、自定义
			'documentClick': [false, false, false, false],


			//公用数据存储,避免重复请求
			//1-Account
			'dsId': scope.modal.editorNow.variables[0].ptoneDsInfoId,   //数据源类型
			'dsCode': scope.modal.editorNow.variables[0].dsCode,   //数据源类型
			'accountList': [],    //widget-已授权账户列表
			'accountName': scope.modal.editorNow.variables[0].accountName,  //widget-账户名
			'gaAccountList': [],  //GA-账户列表
			'fbaAccountList': [],//Facebook ads
			'doubleclickAccountList': [],//double click user profile list
			'doubleclickCompoundAccountList': [],//double click user profile list
			'dsAccountList': [], //
			'ptengineAccountList': [],
			'profileBackups': {}, // dsCode:[]...
			//2-Data
			'filterShow': false,    //控制过滤器层显示与否
			'dimensionOperation': false, //控制维度操作显示与否
			'calculatedValue': false//控制计算指标显示与否
		};


		var mywatch = scope.$watch('rootPanel.nowId', function (newValue, oldValue, scope) {
			if (oldValue !== newValue) {
				//注销当前监听事件
				mywatch();

				scope.modal.editorShow = scope.rootTmpData.editorShow = false;
			}
		});

		/**
		 * 接受来自dashboard的重新初始化编辑器的指令
		 */
		scope.$on('reInitializeEditor', function (e, d) {
			initOrder();
		});

		// ==========


		//标题自动命名
		scope.autoSetWidgetTitle = autoSetWidgetTitle;

		//获取当前数据源信息
		scope.getDsInfo = getDsInfo;

		//关闭
		scope.close = close;

		//选择编辑步骤
		scope.sltStep = sltStep;

		//保存单个数据
		scope.saveData = saveData;

		//
		scope.isEveryCustomWidgetHaveProfileId = isEveryCustomWidgetHaveProfileId;

		////跳转至数据源新增界面
		scope.toAddFile  = toAddFile;

		//判断显示步骤
		scope.showStep = showStep;

		//保存widget数据
		scope.save = save;

		//入口
		init();

		// ==========

		//入口
		function init(){
			//默认显示顺序判断
			initOrder();

			//指标,维度Name国际化
			if (scope.dsConfig.editor.data.translateTitle) {
				if (scope.modal.editorNow.variables[0].metrics.length > 0) {
					for (var i = 0; i < scope.modal.editorNow.variables[0].metrics.length; i++) {
						if (scope.modal.editorNow.variables[0].metrics[i].type == 'metrics') {
							scope.modal.editorNow.variables[0].metrics[i].name = $translate.instant(scope.modal.editorNow.variables[0].metrics[i].i18nCode);
						}
					}
				}
				if (scope.modal.editorNow.variables[0].dimensions.length > 0) {
					for (var i = 0; i < scope.modal.editorNow.variables[0].dimensions.length; i++) {
						scope.modal.editorNow.variables[0].dimensions[i].name = $translate.instant(scope.modal.editorNow.variables[0].dimensions[i].i18nCode);
					}
				}
			}
		}

		//关闭
		function close () {
			scope.modal.editorShow = scope.rootTmpData.editorShow = false;
			gridstackService.enableLayout();
		}

		//标题自动命名
		function autoSetWidgetTitle () {
			if (scope.modal.editorNow.baseWidget.isTitleUpdate == 1) {
				return;
			}
			var title = "", variables = scope.modal.editorNow.variables[0], config = scope.dsConfig.editor.data;
			if (variables.metrics && variables.metrics.length > 0) {
				$.each(variables.metrics, function (i, item) {
					var itemName = item.name;

					if(item.alias){
						title += item.alias;
					}
					else if (config.translateTitle) {
						var translateName = $translate.instant(item.i18nCode);
						title += item.type == "compoundMetrics" ? itemName : ( config.metricsHasCount ? ( item.calculateType + '(' + translateName + ')') : translateName);
					} 
					else {
						title += itemName;
					}
					
					if (variables.metrics.length > 1 && i != variables.metrics.length - 1) {
						title += " " + $translate.instant("COMMON.AND") + " ";
					} 
					else {
						title += " ";
					}
				})
			}
			if (variables.dimensions && variables.dimensions.length > 0) {
				title += $translate.instant("COMMON.BY") + " ";
				$.each(variables.dimensions, function (i, item) {
					if (variables.dimensions.length > 1 && i != variables.dimensions.length - 1) {
						if(item.alias){
							title += item.alias;
						}
						else if (config.translateTitle) {
							title += $translate.instant(item.i18nCode);
						} else {
							title += item.name;
						}

						title += " " + $translate.instant("COMMON.AND") + " ";
					} else {
						if(item.alias){
							title += item.alias;
						}
						else if (config.translateTitle) {
							title += $translate.instant(item.i18nCode);
						} else {
							title += item.name;
						}

						title += " ";
					}
				})
			}
			scope.modal.editorNow.baseWidget.widgetTitle = title == '' ? $translate.instant("WIDGET.WIDGET_DEFAULT_NAME") : title;
		}

		//获取当前数据源信息
		function getDsInfo (dsInfo) {
			if (!dsInfo) {
				return false;
			}
			var ds;
			for (var i = 0; i < scope.rootCommon.dsList.length; i++) {
				if (isNaN(-dsInfo)) {
					//dsCode
					if (scope.rootCommon.dsList[i].code == dsInfo) {
						ds = scope.rootCommon.dsList[i];
						break;
					}
				} else {
					//dsId
					if (scope.rootCommon.dsList[i].id == dsInfo) {
						ds = scope.rootCommon.dsList[i];
						break;
					}
				}
			}

			return ds;
		}


		//选择编辑步骤
		function sltStep (type, index) {
			if (scope.editor.disabled[index] && type !== 'title') {
				return;
			}

			if (type == 'data') {
				scope.editor.filterShow = false;
				scope.editor.dimensionOperation = false;
			}

			if (type == 'demo') {
				scope.modal.demoShow = true;
				scope.showWidgetDemo(scope.modal.editorNow, scope.modal.editorNowIndex);
			}

			if (scope.editor.pop.name != type) {
				scope.editor.pop.name = type;
				scope.editor.pop.show = true;
			} else {
				scope.editor.pop.name = null;
				scope.editor.pop.show = false;
			}
		}

		//widget 模板保存时同时保存指标和维度的国际化名字，用于检索用
		function saveMetricsOrDimensionI18nKey(custom) {
			// var keyMap = $translate.getI18nMap();
			if (sysRoles.hasSysRole("ptone-admin-user")) {
				if(!custom){
					if(scope.modal.editorNow.variables.length > 0){
						var metrics = scope.modal.editorNow.variables[0].metrics;
						var dimensions = scope.modal.editorNow.variables[0].dimensions;
						processMetricsOrDimension(metrics);
						processMetricsOrDimension(dimensions);
					}
				}
			}
		}

		function processMetricsOrDimension(array,keyMap){

			if(array.length > 0){
				for(var i = 0;i<array.length;i++){
					var i18nCode = array[i].i18nCode;
					var i18nName = {
						"zh_CN":null,
						"en_US":null,
						"ja_JP":null
					};
					for(var n in i18nName){
						$translate(i18nCode, null, null, null, n).then((value) => {
							i18nName[n] = value;
						});
					}
					array[i].i18nName = i18nName;
				}
			}
		}

		/**
		 * 保存
		 * @param type
		 * @param custom ---是否为自定义widget
		 * @param widget --- 自定义widget的对象
		 */
		function saveData (type,custom,customWidget) {
			scope.loadSetting.widget = true;
			saveMetricsOrDimensionI18nKey(custom);
			if(!custom){
				dataMutualSrv.post(LINK_WIDGET_EDIT, angular.copy(scope.modal.editorNow), 'wgtSave').then(function (data) {
					if (data.status == 'success') {

						//保存当前账号与时间信息
						if (type) {
							if (type.indexOf('time-') >= 0) {
								if(scope.dsConfig.editor.time.dataKeyOfInherit){//需要时间维态的数据源修改时间时，才需要记住维态时间
									scope.rootUser.userSelected = {"dateKey": scope.modal.editorNow.baseWidget.dateKey};
									//rootUser.userSelected 需要单独接口来维护
									var sendData = {"ptId":$rootScope.userInfo.ptId,"userSelected":JSON.stringify({"dateKey": scope.modal.editorNow.baseWidget.dateKey})};
									UserResources.updateUsersSettingsInfo(sendData);
								}
							} else if (type.indexOf('source-') >= 0) {
								scope.rootUser.profileSelected = {
									"accountName": scope.modal.editorNow.variables[0].accountName,
									"dsId": scope.modal.editorNow.variables[0].ptoneDsInfoId,
									"prfileId": scope.modal.editorNow.variables[0].profileId,
									"connectionId": scope.modal.editorNow.variables[0].connectionId,
									"dsCode": scope.modal.editorNow.variables[0].dsCode
								}
							}
						}

						//更新已存widget列表,减少请求次数
						// scope.rootCommon.dashboardList[scope.rootPanel.nowId] = angular.copy(scope.rootWidget.list);
					} else {
						if (data.status == 'failed') {
							console.log('Post Data Failed!')
						} else if (data.status == 'error') {
							console.log('Post Data Error: ');
							console.log(data.message)
						}
					}
					scope.loadSetting.widget = false;
				})
			}else{
				dataMutualSrv.post(LINK_WIDGET_EDIT, angular.copy(customWidget), 'wgtSave').then(function (data) {
					if (data.status == 'success') {

						//保存当前账号与时间信息
						if (type) {
							if (type.indexOf('time-') >= 0) {
								scope.rootUser.userSelected = {"dateKey": customWidget.baseWidget.dateKey}
								//rootUser.userSelected 需要单独接口来维护
								var sendData = {"ptId":$rootScope.userInfo.ptId,"userSelected":JSON.stringify({"dateKey": customWidget.baseWidget.dateKey})};
								UserResources.updateUsersSettingsInfo(sendData);
							} else if (type.indexOf('source-') >= 0) {
								scope.rootUser.profileSelected = {
									"accountName": customWidget.variables[0].accountName,
									"dsId": customWidget.variables[0].ptoneDsInfoId,
									"prfileId": customWidget.variables[0].profileId,
									"connectionId": customWidget.variables[0].connectionId,
									"dsCode": customWidget.variables[0].dsCode
								}
							}
						}

						//更新已存widget列表,减少请求次数
						// scope.rootCommon.dashboardList[scope.rootPanel.nowId] = angular.copy(scope.rootWidget.list);
					} else {
						if (data.status == 'failed') {
							console.log('Post Data Failed!')
						} else if (data.status == 'error') {
							console.log('Post Data Error: ');
							console.log(data.message)
						}
					}
					scope.loadSetting.widget = false;
				})
			}
		}

		//跳转至数据源新增界面
		function toAddFile () {
			scope.rootTmpData.dataSources = {
				type: 'fileAdd',
				dsCode: scope.modal.editorNow.variables[0].dsCode,
				connectionId: scope.modal.editorNow.variables[0].connectionId
			};
			$state.go('pt.dataSources.' + scope.modal.editorNow.variables[0].dsCode);
		}

		function isEveryCustomWidgetHaveProfileId (editorNow){
			if(editorNow && editorNow.children && editorNow.children.length > 0){
				return editorNow.children.every(function(item,index,arr){
					return item.baseWidget.widgetType === 'tool' || (item.baseWidget.widgetType !== 'tool' && item.variables[0].profileId);
				});
			}
		}

		//默认显示顺序判断
		function initOrder() {
			if(scope.modal.editorNow.baseWidget.graphName != 'heatmap'){
				var variables = scope.modal.editorNow.variables[0];
				if (scope.modal.editorNow.baseWidget.widgetType == 'custom') {
					//自定义widget的编辑器和普通编辑器不同
					//有创建自定义widget的用户，在打开编辑器后，只能修改标题和在图形选项中创建小widget
					//没有创建自定义widget的用户，在widget gallery中打开编辑器后，可以批量修改数据源和时间，可以修改标题，不能批量修改图形

					if (scope.rootUser.sysRoles.createCustomWidget) {
						scope.editor.pop.name = 'chart';
						scope.editor.filterShow = false;
						scope.editor.dimensionOperation = false;
						scope.editor.disabled = [true, true, true, false];
					}
					else {
						//没有创建自定义widget的用户，当前的操作面板要根据数据源是否已经全部设置过为依据，如果所有数据源都已经设置过了，编辑器默认打开的是时间，否则当前打开的是数据源

						if(scope.isEveryCustomWidgetHaveProfileId(scope.modal.editorNow)){
							scope.editor.pop.name = 'time';
							scope.editor.filterShow = false;
							scope.editor.dimensionOperation = false;
							scope.editor.disabled = [false, true, false, true];
						}else{
							scope.editor.pop.name = 'source';
							scope.editor.filterShow = false;
							scope.editor.dimensionOperation = false;
							scope.editor.disabled = [false, true, false, true];
						}

					}
				}
				else {
					if (scope.modal.editorNow.baseWidget.widgetType == 'tool' && scope.modal.editorNow.baseWidget.parentId) {
						//如果是自定义widget的富文本框，只能删除操作

						scope.editor.pop.name = 'custom';
						scope.editor.filterShow = false;
						scope.editor.dimensionOperation = false;
						scope.editor.disabled = [true, true, true, true, false];
					}
					else {
						//当widget数据为空时，默认显示第一步(数据源、account、profileId缺一不可)，google adwords 数据源普通账号只有数据源和account信息

						if (scope.modal.editorNow.baseWidget.isExample == 1 || (scope.dsConfig.editor.source.secondStepIsHide && !(variables.ptoneDsInfoId && variables.profileId)) || (!scope.dsConfig.editor.source.secondStepIsHide && !(variables.ptoneDsInfoId && variables.accountName && variables.profileId))) {
							//当第二步不显示时,只判断dsId和profileId,其他的则需要判断account

							scope.editor.pop.name = 'source';

							//控制选择顺序
							scope.editor.disabled = [false, true, true, true];
							scope.editor.documentClick[0] = true;
						} else if ((variables.metrics && variables.metrics.length > 0) || (variables.dimensions && variables.dimensions.length > 0)) {
							//只要存在一个指标或者维度，就可以选择图表

							scope.editor.pop.name = 'data';
							scope.editor.filterShow = false;
							scope.editor.dimensionOperation = false;

							if((variables.profileId + '').indexOf('|join|') > -1){//salesforce数据源，Report类型，时间不可点
								scope.editor.disabled = [false, false, true, false];
							}else{
								//控制选择顺序
								isTimeDisable();//判断时间是否可点
							}

							scope.editor.documentClick[1] = true;
						} else if ((!variables.metrics || variables.metrics.length <= 0) && (!variables.dimensions || variables.dimensions.length <= 0)) {
							//指标维度都没有

							scope.editor.pop.name = 'data';
							scope.editor.filterShow = false;
							scope.editor.dimensionOperation = false;

							//控制选择顺序
							scope.editor.disabled = [false, false, true, true];
							scope.editor.documentClick[1] = true;
						} else {
							scope.editor.pop.name = 'data';
							scope.editor.filterShow = false;
							scope.editor.dimensionOperation = false;

							isTimeDisable();//判断时间是否可点
						}
					}
				}

				scope.editor.pop.show = true;

				//校验widget类型
				checkWidgetType(scope.modal.editorNow);
			}
			else {
				scope.editor.pop.name = 'heatmap';
				scope.editor.pop.show = true;
			}
		}

		//判断时间是否可点
		function isTimeDisable() {
			/*if(scope.modal.editorNow.variables[0].ptoneDsInfoId == 1
			 || scope.modal.editorNow.variables[0].ptoneDsInfoId == 3
			 || scope.modal.editorNow.variables[0].ptoneDsInfoId == 12
			 || scope.modal.editorNow.variables[0].ptoneDsInfoId == 13){*/
			scope.dsConfig = getMyDsConfig(scope.modal.editorNow.variables[0].dsCode);//为了防止切换自定义widget出错，再次初始化dsConfig
			if (scope.dsConfig.editor.time.isTimeDisable && scope.dsConfig.editor.time.isTimeDisableByProfileIdList.indexOf(+scope.modal.editorNow.variables[0].profileId) < 0) {
				scope.editor.disabled = [false, false, false, false];
			// } else if (scope.dsConfig.editor.time.isTimeDisableByProfileIdList.indexOf(+scope.modal.editorNow.variables[0].profileId) >= 0) {
				//如果是mailchimp中的GrowthHistory分类，则不允许使用time
				// scope.editor.disabled = [false, false, true, false];
			} else {
				var dimensionsUrl;
				if(scope.modal.editorNow.variables[0].ptoneDsInfoId == 27){
					dimensionsUrl = LINK_DIMENSIONS_GA + scope.modal.editorNow.variables[0].ptoneDsInfoId;
				}else{
 					dimensionsUrl = LINK_USER_DIMENSIONS + scope.modal.editorNow.variables[0].ptoneDsInfoId + '/' + scope.modal.editorNow.variables[0].profileId;
				}

				var dimensionData = [];
				dataMutualSrv.get(dimensionsUrl).then(function (data) {
					if (data.status == 'success') {
						if(scope.modal.editorNow.variables[0].ptoneDsInfoId == 27){
							if(data.content && data.content.length){
								var mailChimpDimention;
								data.content.forEach(function(item){
									if(item.id == scope.modal.editorNow.variables[0].profileId){
										mailChimpDimention = item.dimensionList;
									}
								});

								if(mailChimpDimention && mailChimpDimention.length){
									for(var i = 0; i < mailChimpDimention.length; i++){
										if(mailChimpDimention[i].dataType == 'DATE' || mailChimpDimention[i].dataType == 'TIMESTAMP' || mailChimpDimention[i].dataType == 'DATETIME'){
											dimensionData.push(mailChimpDimention[i]);
										}
									}
								}

								if (dimensionData.length > 0) {
									scope.editor.disabled = [false, false, false, false];
								} else {
									scope.editor.disabled = [false, false, true, false];//如果维度里没有时间的，时间选项就不可点击
								}
							}
						}else{
							for (var i = 0; i < data.content.length; i++) {
							if (data.content[i].dataType == 'DATE' || data.content[i].dataType == 'TIMESTAMP' || data.content[i].dataType == 'DATETIME') {
								dimensionData.push(data.content[i]);
							}
						}
							if (dimensionData.length > 0) {
								scope.editor.disabled = [false, false, false, false];
							} else {
								scope.editor.disabled = [false, false, true, false];//如果维度里没有时间的，时间选项就不可点击
							}
						}
						
					}
				});
			}
		}

		//判断是否为自定义widget
		function checkWidgetType(widget){
			var type = widget.baseWidget.widgetType;
			var parentId = widget.baseWidget.parentId;

			if(type == 'custom' || (parentId && parentId != '')){
				//当自定义widget编辑时,禁用拖拽事件(因为会和小widget拖拽冲突)

				gridstackService.disableLayout('disableDrag');
			} else {
				gridstackService.enableLayout();
			}
		}

		//判断显示步骤
		function showStep (step){
			if(scope.modal.editorNow.baseWidget.graphName == 'heatmap'){
				return step == 'heatmap' ? true : false;
			}
			else {
				return step != 'heatmap' ? true : false;
			}
		}


		//保存widget数据(heatmap)
		function save(type, data){
			var currentWidget = scope.modal.editorNow;

			switch(type){
				case "heatmap":
					currentWidget.toolData.value = data;
					break;
			}

			//处理数据
			var sendData = angular.copy(scope.modal.editorNow);
			if (sendData.toolData && !angular.isString(sendData.toolData.extend)) {
				var extend = sendData.toolData.extend;
				sendData.toolData.extend = angular.toJson(extend);
			}
			if (sendData.variables && sendData.variables[0] && sendData.variables[0].metricsCode && !angular.isString(sendData.variables[0].metricsCode)) {
				sendData.variables[0].metricsCode = angular.toJson(sendData.variables[0].metricsCode);
			}
			if (sendData.variables && sendData.variables[0] && sendData.variables[0].dimensionsCode && !angular.isString(sendData.variables[0].dimensionsCode)) {
				sendData.variables[0].dimensionsCode = angular.toJson(sendData.variables[0].dimensionsCode);
			}
			delete sendData.col;
			delete sendData.row;
			delete sendData.sizeX;
			delete sendData.sizeY;
			delete sendData.minSizeX;
			delete sendData.minSizeY;
			delete sendData.autoPos;
			delete sendData.baseWidget.metricsJson;
			delete sendData.baseWidget.dimensionsJson;
			delete sendData.widgetDrawing;
			delete sendData._ext;

			//更新数据
			WidgetResources.update(sendData)
		}

		// ==========

		scope.$watch('modal.editorNow.variables[0].dsCode', function (ne, ol) {
			scope.dsConfig = getMyDsConfig(ne);
		});


		/**
		 * 监听编辑器打开状态(当已打开编辑器,需更新编辑器数据)
		 */
		scope.$watch('modal.editorOpen', function (newData, oldData) {
			if (newData) {

				//重置状态
				scope.editor = {
					'pop': {
						'name': null,
						'show': false
					},
					'disabled': [false, false, false, false],
					'documentClick': [false, false, false, false],

					'dsId': scope.modal.editorNow.variables[0].ptoneDsInfoId,
					'dsCode': scope.modal.editorNow.variables[0].dsCode,
					'accountList': [],
					'accountName': scope.modal.editorNow.variables[0].accountName,
					'gaAccountList': [],
					'fbaAccountList': [],
					'doubleclickAccountList': [],
					'doubleclickCompoundAccountList': [],
					'dsAccountList': [],
					'ptengineAccountList': [],
					'profileBackups': {},
					'filterShow': false,
					'dimensionOperation': false,
					'calculatedValue': false
				};

				initOrder();
				scope.$broadcast('changeWidgetEditor', scope.modal.editorNow);//此时，需要通知子级更新相关信息，比如需要更新data中的指标维度列表
				scope.modal.editorOpen = false;
			}
		});

	}
}

export default widgeteditor;



