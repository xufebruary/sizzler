/**
 * calculatedValue
 * 计算指标指令
 *
 * created by ao at 20160712
 */

import calculatedValueTpl from './calculatedValue.tpl.html';
import './calculatedValue.scss';

import {
	LINK_VALIDATE_CALCULATE_VALUE,
	LINK_UPDATE_CALCULATE_VALUE,
	LINK_ADD_CALCULATE_VALUE,
	uuid
} from '../../../common/common';

import 'assets/libs/angular/angular-ui-codemirror/codemirror.css';
import CodeMirror from 'assets/libs/angular/angular-ui-codemirror/codemirror';


calculatedValue.$inject = ['$document', '$timeout', '$translate', 'dataMutualSrv', 'uiLoadingSrv', 'uiSearch', 'siteEventAnalyticsSrv','$rootScope'];

function calculatedValue($document, $timeout, $translate, dataMutualSrv, uiLoadingSrv, uiSearch, siteEventAnalyticsSrv,$rootScope) {
	"use strict";

	return {
		restrict: 'EA',
		template: calculatedValueTpl,
		link: link
	};

	function link(scope, element, attrs) {
		var body = $document.find('body').eq(0);

		scope.myOptions = {
			name: '',
			codeMirror: null,
			newFormula: null,
			fields: null,
			sltMetricsList: [],
			errorName: false,
			errorFormula: false,
			placeholder: $translate.instant("WIDGET.EDITOR.CALCULATED_VALUE.SET_PLACEHOLDER_" + (scope.dsConfig.editor.data.isSupportFunc ? "2" : "1")),
			funcKeywordsList: [
				{'name': 'SUM', 'code': 'SUM'},
				{'name': 'AVERAGE', 'code': 'AVERAGE'},
				{'name': 'MAX', 'code': 'MAX'},
				{'name': 'MIN', 'code': 'MIN'},
				{'name': 'COUNTA', 'code': 'COUNTA'},
				{'name': 'COUNTUNIQUE', 'code': 'COUNTUNIQUE'}
				//{'name': 'STDEV','code': 'STDEV'},
				//{'name': 'VAR','code': 'VAR'}
			],
			funcKeywords: '',//表达式插件中的支持的关键字类型(需根据当前数据源是否为表格型)

			//校验
			metricsReg: new RegExp(/\[.*?]/g),

			//指标列表
			metricsList: null,  //页面搜索,展示用.
			tmpMetricsList: null,  //页面搜索,备份用.
			metricsListIndex: [] //二级展示开关

			//已存计算指标列表
			//calculatedValueList: null, //编辑时,需剔除编辑的指标
			//tmpCalculatedValueList: null //编辑时,需剔除编辑的指标
		};

		/**
		 * 关闭
		 */
		scope.closeTips = function (type) {

			//GTM
			if (!type && scope.dataSettings.calculatedValueType == 'add') {
				siteEventAnalyticsSrv.setGtmEvent('click_element', 'calc_metric', 'calc_metric.add.cancel');
			}
			body.removeClass('modal-open');
			scope.dataSettings.calculatedValueType = null;
			scope.dataSettings.calculatedValueCurrent = null;
			scope.editor.calculatedValue = false;

			//destroy
			scope.myOptions.codeMirror.setValue("");
			scope.myOptions.codeMirror.clearHistory();
			scope.myOptions = {};

			//显示指标列表
			scope.dataSettings.metricsSearch = true;
		};

		/**
		 * 点击指标添加至表达式框
		 */
		scope.metricsToFormula = function (metrics) {
			var flag = false;
			for (var i = 0; i < scope.myOptions.sltMetricsList.length; i++) {
				if (scope.myOptions.sltMetricsList[i].metricsId == metrics.id) {
					flag = true;
					break;
				}
			}

			if (!flag) {
				var formulaId = uuid();
				var newMetrics = angular.copy(metrics);
				newMetrics['metricsId'] = metrics.id;
				newMetrics['formulaId'] = formulaId;
				scope.myOptions.sltMetricsList.push(newMetrics);
			}
			scope.myOptions.codeMirror.replaceSelection('[' + metrics.name + ']');
			scope.myOptions.codeMirror.focus();
		};

		/**
		 * 点击函数添加至表达式框
		 *
		 * func: {
         *  description:"返回base64解码值"
         *  example:"BASE64_DECODE([字段])，返回base64解码值"
         *  type:"BASE64_DECODE"
         *  usage:"BASE64_DECODE(字段)"
         * }
		 */
		scope.addFuncToFormula = function (func) {
			var formulaCursor = scope.myOptions.codeMirror.getCursor(),
				t = "COUNT_DISTINCT",
				i = func.type === t ? "COUNT(DISTINCT())" : func.type + "()",
				r = formulaCursor.ch + i.length - (t == func.type ? 2 : 1);

			scope.myOptions.codeMirror.replaceSelection(i);
			scope.myOptions.codeMirror.setCursor(formulaCursor.line, r);
			scope.myOptions.codeMirror.focus();
		};

		/**
		 * 获取当前数据源下指标名称列表(全部转为大写存储)
		 */
		scope.getShowNameList = function () {
			var list = angular.copy(scope.myOptions.metricsList);
			var fields = [];

			//数据源指标列表
			for (var i = 0; i < list.length; i++) {
				var current = list[i];

				if (scope.dsConfig.editor.data.metricsTwoLayer) {
					//如果是2级结构,则直接循环子级获取名称列表

					for (var j = 0; j < current.metricsList.length; j++) {
						fields.push(current.metricsList[j].name.toUpperCase());
					}
				} else {
					fields.push(current.name.toUpperCase());
				}
			}

			//已存计算指标列表(排除当前所编辑指标)
			//for (var i = 0; i < scope.myOptions.calculatedValueList.length; i++) {
			//    fields.push(scope.myOptions.calculatedValueList[i].name);
			//}
			return fields;
		};

		/**
		 * 提取表达式转换成对应的CODE
		 */
		scope.getFormulaCode = function () {
			var variableList = [];
			var aggregator;
			var formula = scope.myOptions.newFormula.aggregator.toUpperCase();

			//获取表达式中的指标列表
			for (var g = 0; g < angular.element('.cm-variable-2').length; g++) {
				var variableText = angular.element('.cm-variable-2').eq(g).text();
				variableList = variableList.concat(variableText.match(scope.myOptions.metricsReg));
			}

			for (var i = 0; i < variableList.length; i++) {
				var variableName = variableList[i].toUpperCase();
				var flag = false;

				//先在已选列表中循环查找.
				for (var m = 0; m < scope.myOptions.sltMetricsList.length; m++) {
					if ('[' + scope.myOptions.sltMetricsList[m].name.toUpperCase() + ']' == variableName) {
						formula = formula.replace('[' + scope.myOptions.sltMetricsList[m].name.toUpperCase() + ']', '[' + scope.myOptions.sltMetricsList[m].formulaId + ']');
						flag = true;
						break;
					}
				}

				//未在已选列表中查找到,则在数据源指标列表中查找.(主要针对,用户手动输入指标名称现象)
				if (!flag) {

					var sltMetricsList;
					for (var j = 0; j < scope.myOptions.metricsList.length; j++) {
						if (scope.dsConfig.editor.data.metricsTwoLayer) {
							for (var k = 0; k < scope.myOptions.tmpMetricsList[j].metricsList.length; k++) {
								if (variableName == '[' + scope.myOptions.tmpMetricsList[j].metricsList[k].name.toUpperCase() + ']') {
									sltMetricsList = scope.myOptions.tmpMetricsList[j].metricsList[k];
									flag = true;
									break;
								}
							}
						} else {
							if (variableName == '[' + scope.myOptions.tmpMetricsList[j].name.toUpperCase() + ']') {
								sltMetricsList = scope.myOptions.tmpMetricsList[j];
								flag = true;
								break;
							}
						}
					}

					//如果依旧没找到,则从已存的计算指标列表中查找.
					//if (!flag) {
					//    for (var j = 0; i < scope.myOptions.calculatedValueList.length; j++) {
					//        if (variableName == scope.myOptions.calculatedValueList[j].name) {
					//            sltMetricsList = scope.myOptions.calculatedValueList[j];
					//            flag = true;
					//            break;
					//        }
					//    }
					//}

					if (flag) {
						var newMetrics = sltMetricsList;
						newMetrics['metricsId'] = sltMetricsList.id;
						newMetrics['formulaId'] = uuid();

						formula = formula.replace('[' + newMetrics.name.toUpperCase() + ']', '[' + newMetrics.formulaId + ']');
						scope.myOptions.sltMetricsList.push(newMetrics);
					} else {
						//如都未找到,则提示错误.
						scope.myOptions.errorFormula = true;
						uiLoadingSrv.removeLoading(angular.element(element).find('.pt-popup-content'));
					}

				}

			}

			//aggregator中的函数不用替换
			aggregator = angular.copy(formula);

			//替换表达式中函数的CODE
			if (angular.element('.cm-keyword').length > 0) {
				for (var l = 0; k < scope.myOptions.funcKeywordsList.length; l++) {
					var formulaReg = new RegExp(scope.myOptions.funcKeywordsList[l].name + "\\(", "gi");
					formula = formula.replace(formulaReg, scope.myOptions.funcKeywordsList[l].code);
				}
			}
			console.log("formula: " + formula);
			console.log("aggregator: " + aggregator);
			return {formula: formula.replace(/\n/g, ''), aggregator: aggregator};
		};

		/**
		 * 数据校验
		 */
		scope.verifyData = function () {
			var flag = false;

			//名称校验
			if (!scope.myOptions.name || scope.myOptions.name == '') {
				flag = true;
			} else {
				//校验是否重名

				//已存计算指标列表排查
				for (var k = 0; k < scope.myOptions.calculatedValueList.length; k++) {
					if (scope.myOptions.name.toUpperCase() == scope.myOptions.calculatedValueList[k].name.toUpperCase()) {
						flag = true;
						break;
					}
				}

				if (!flag) {
					//当前数据源指标列表排查
					for (var i = 0; i < scope.myOptions.tmpMetricsList.length; i++) {

						//如果是二级列表,则直接查找子级
						if (scope.dsConfig.editor.data.metricsTwoLayer) {
							for (var j = 0; j < scope.myOptions.tmpMetricsList[i].metricsList.length; j++) {
								if (scope.myOptions.name.toUpperCase() == scope.myOptions.tmpMetricsList[i].metricsList[j].name.toUpperCase()) {
									flag = true;
									break;
								}
							}
						} else {
							if (scope.myOptions.name.toUpperCase() == scope.myOptions.tmpMetricsList[i].name.toUpperCase()) {
								flag = true;
								break;
							}
						}
					}
				}
			}
			scope.myOptions.errorName = flag;

			if (!flag) {
				//表达式校验

				if (angular.element(element).find('.cm-error').length > 0) {
					scope.myOptions.errorFormula = true;
					uiLoadingSrv.removeLoading(angular.element(element).find('.pt-popup-content'));
					return;
				}

				uiLoadingSrv.createLoading($(element).find('.pt-popup-content'));
				var tableId = scope.dsConfig.editor.source.calculatedValueScopeIsTable ? scope.modal.editorNow.variables[0].profileId : "";
				var sendCode = scope.getFormulaCode();
				var sendData = {
					id: uuid(),
					name: scope.myOptions.name + '',
					originalAggregator: scope.myOptions.newFormula.aggregator.replace(/\n/g, ''),
					aggregator: sendCode.aggregator,
					formula: sendCode.formula,
					code: uuid(),
					spaceId: scope.rootSpace.current.spaceId,
					dsId: scope.editor.dsId,
					dsCode: scope.editor.dsCode,
					objects: scope.myOptions.sltMetricsList,
					tableId: tableId,
					type: "compoundMetrics"
				};
				if (scope.dataSettings.calculatedValueType == 'edit') {
					sendData.id = scope.dataSettings.calculatedValueCurrent.id;
					sendData.tableId = scope.dataSettings.calculatedValueCurrent.tableId || "";
				}

				//表达式前端校验
				//scope.myOptions.errorFormula = !formulaCheck(scope.myOptions.newFormula.aggregator, scope.myOptions.metricsReg);
				scope.myOptions.errorFormula = !formulaCheck(sendCode.aggregator, scope.myOptions.metricsReg, angular.element('.cm-keyword').length, scope.myOptions.funcKeywordsList);
				if (scope.myOptions.errorFormula) {
					uiLoadingSrv.removeLoading($(element).find('.pt-popup-content'));

				} else {
					//表达式后端校验

					dataMutualSrv.post(LINK_VALIDATE_CALCULATE_VALUE, sendData).then(function (data) {
						if (data.status == 'success') {
							scope.myOptions.errorFormula = !data.content;

							if (data.content) {
								scope.saveData(sendData);
							} else {
								uiLoadingSrv.removeLoading($(element).find('.pt-popup-content'));
							}
						} else {
							console.log('校验表达式失败,需要重试!!!');

							if (data.status == 'failed') {
								//console.log('Post Data Failed!')
							} else if (data.status == 'error') {
								console.log('Post Data Error: ');
								console.log(data.message)
							}

							uiLoadingSrv.removeLoading($(element).find('.pt-popup-content'));
						}
					});
				}
			}
		};

		/**
		 * 保存
		 */
		scope.saveData = function (sendData) {
			var url = scope.dataSettings.calculatedValueType == 'edit' ? LINK_UPDATE_CALCULATE_VALUE : LINK_ADD_CALCULATE_VALUE;

			dataMutualSrv.post(url, sendData).then(function (data) {
				if (data.status == 'success') {
					if (scope.dataSettings.calculatedValueType == 'edit') {
						for (var i = 0; i < scope.dataSettings.tempCalculatedValueList.length; i++) {
							if (scope.dataSettings.tempCalculatedValueList[i].id == sendData.id) {
								scope.dataSettings.tempCalculatedValueList[i] = angular.copy(data.content);
								break;
							}
						}

						//重新校验指标列表
						scope.checkMetricsList();

						//GTM
						//siteEventAnalyticsSrv.setGtmEvent('click_element','calc_metric','calc_metric.edit.save.'+sendData.name);
					} else {
						scope.dataSettings.tempCalculatedValueList.push(angular.copy(data.content));

						//将滚动条滚动至底部
						scope.dataSettings.scrollBarToBottom = true;

						//直接添加指标
						//scope.addData(angular.copy(data.content), 'compoundMetrics');

						//GTM
						siteEventAnalyticsSrv.setGtmEvent('click_element', 'calc_metric', 'calc_metric.add.save.' + sendData.name);
					}

					//重置搜索
					scope.dataSettings.metricsSearchKey = '';
					scope.restMetricsList(scope.dataSettings.tempMetricsList);
					scope.restCalculatedValueList(scope.dataSettings.tempCalculatedValueList);

					scope.closeTips('add');
				} else {
					console.log('保存计算指标信息失败.');

					if (data.status == 'failed') {
						//console.log('Post Data Failed!')
					} else if (data.status == 'error') {
						console.log('Post Data Error: ');
						console.log(data.message)
					}
				}

				//loading
				uiLoadingSrv.removeLoading($(element).find('.pt-popup-content'));
			});

			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where: 'widget_editor_metrics',
				what: 'save_calculated_metrics',
				how: 'click',
				value: sendData.originalAggregator
			});
		};

		/**
		 * 搜索
		 */
		scope.search = function () {
			var config = scope.dsConfig.editor.data;
			var key = scope.metricsKey;//搜索关键字
			var tier = config.metricsTwoLayer ? 2 : 1;//原始数据层级数目
			var tierData = ["metricsList"]; //层级列表[]
			var dataList = scope.myOptions.tmpMetricsList; //原始数据


			uiSearch.search(key, tier, tierData, dataList, scope.searchBackFunc, scope.collapseBackFun);

			//if (scope.myOptions.tmpCalculatedValueList.length > 0) {
			//    uiSearch.search(key, 1, ['name'], scope.myOptions.tmpCalculatedValueList, scope.searchCalculatedValueListBackFunc, scope.collapseCalculatedValueListBackFun);
			//}
		};

		/**
		 * 搜索回调
		 * 返回搜索后列表
		 */
		scope.searchBackFunc = function (list) {
			scope.$broadcast('triggerHighWord', scope.metricsKey);
			scope.myOptions.metricsList = list;
		};

		/**
		 * 搜索计算指标回调
		 * 返回搜索后列表
		 */
		scope.searchCalculatedValueListBackFunc = function (list) {
			scope.$broadcast('triggerHighWord', scope.metricsKey);
			scope.myOptions.calculatedValueList = list;
		};

		/**
		 * 搜索回调
		 * 返回子级展开状态
		 */
		scope.collapseBackFun = function (flag) {
			if (scope.myOptions.metricsListIndex) {
				$.each(scope.myOptions.metricsListIndex, function (i, item) {
					scope.myOptions.metricsListIndex[i] = flag;
				});
			}
		};

		/**
		 * 搜索计算指标回调
		 * 返回子级展开状态
		 */
		scope.collapseCalculatedValueListBackFun = function () {

		};

		/**
		 * 编辑回显时,替换表达式中指标的国际化文案
		 */
		scope.getAggregatorLocal = function (aggregator) {
			var variableList = aggregator.match(scope.myOptions.metricsReg);

			if (variableList) {
				for (var i = 0; i < scope.myOptions.sltMetricsList.length; i++) {
					var currentMetrics = scope.myOptions.sltMetricsList[i];

					for (var j = 0; j < variableList.length; j++) {
						if (variableList[j] == '[' + currentMetrics.formulaId + ']') {
							aggregator = aggregator.replace('[' + currentMetrics.formulaId + ']', '[' + currentMetrics.name + ']')
						}
					}
				}
			}
			return aggregator;
		};

		/**
		 * 编辑回显时,替换指标列表中的国际化文案
		 */
		scope.getObjectsLocal = function (objects) {
			if (scope.dsConfig.editor.data.translateTitle) {
				for (var l = 0; l < objects.length; l++) {
					objects[l].name = objects[l].i18nCode ? $translate.instant(objects[l].i18nCode) : objects[l].name;
				}
			} else {
				for (var i = 0, currentObject; i < objects.length; i++) {
					currentObject = objects[i];

					for (var j = 0, currentMetrics; j < scope.myOptions.metricsList.length; j++) {
						currentMetrics = scope.myOptions.metricsList[j];

						if (scope.dsConfig.editor.data.metricsTwoLayer) {
							//二级列表

							for (var k = 0; k < currentMetrics.metricsList.length; k++) {
								if (scope.editor.dsId == 19) {
									//salesforce 没有ID.只能依据code做判断
									if (currentObject.code == currentMetrics.metricsList[k].code) {
										currentObject.name = currentMetrics.metricsList[k].name;
										break;
									}
								} else {
									if (currentObject.metricsId == currentMetrics.metricsList[k].id) {
										currentObject.name = currentMetrics.metricsList[k].name;
										break;
									}
								}
							}
						} else {
							if (currentObject.metricsId == currentMetrics.id) {
								currentObject.name = currentMetrics.name;
								break;
							}
						}

					}
				}
			}
			return objects;
		};

		/**
		 * 表达式中手动输入的指标名称校验(不区分大小写)
		 */
		scope.formulaIndexOf = function (list, target) {
			var t = Array.prototype.indexOf;
			if (t)
				return t.call(list, target.toUpperCase());
			for (var a = 0, n = list.length; n > a; a++)
				if (list[a].toUpperCase() === target.toUpperCase())
					return a;
			return -1
		};

		/**
		 * 根据数据源类型生成表达式所支持的函数列表
		 * 账号型(第三方数据源)不支持函数
		 * 表格线(table)支持函数
		 */
		scope.funcKeyWordsInit = function () {
			if (scope.dsConfig.editor.data.isSupportFunc) {
				scope.myOptions.funcKeywords = scope.myOptions.funcKeywordsList[0].name.toLowerCase();
				for (var i = 1; i < scope.myOptions.funcKeywordsList.length; i++) {
					scope.myOptions.funcKeywords = scope.myOptions.funcKeywords + ' ' + scope.myOptions.funcKeywordsList[i].name.toLowerCase();
				}

				console.log(scope.myOptions.funcKeywords);
			}
		};

		/**
		 * 数据初始化
		 */
		scope.dataInit = function () {
			scope.myOptions.metricsList = angular.copy(scope.dataSettings.tempMetricsList);
			scope.myOptions.tmpMetricsList = angular.copy(scope.dataSettings.tempMetricsList);
			scope.myOptions.calculatedValueList = angular.copy(scope.dataSettings.calculatedValueList);
			//scope.myOptions.tmpCalculatedValueList = angular.copy(scope.dataSettings.calculatedValueList);

			//剔除不符合的数据类型,现支持['NUMBER','CURRENCY','DURATION']
			if (!scope.dsConfig.editor.data.calculatedValueScopeShowAll) {
				var tmpList = [];
				for (var i = 0; i < scope.myOptions.metricsList.length; i++) {
					if (['NUMBER', 'PERCENT', 'CURRENCY', 'DURATION'].indexOf(scope.myOptions.metricsList[i].dataType) >= 0) {
						tmpList.push(scope.myOptions.metricsList[i]);
						//scope.myOptions.metricsList.splice(i,1);
					}
				}

				scope.myOptions.metricsList = angular.copy(tmpList);
				scope.myOptions.tmpMetricsList = angular.copy(tmpList);
			}

			//剔除有错误的已存计算指标
			//for (var i = 0; i < scope.myOptions.calculatedValueList.length; i++) {
			//    if (scope.myOptions.calculatedValueList[i].isValidate == 0) {
			//        scope.myOptions.calculatedValueList.splice(i, 1);
			//        break;
			//    }
			//}

			//初始化指标列表收起状态
			if (scope.dsConfig.editor.data.metricsTwoLayer) {
				for (var k = 0; k < scope.myOptions.metricsList.length; k++) {
					scope.myOptions.metricsListIndex.push(false);
				}
			}

			//区分添加和编辑
			if (scope.dataSettings.calculatedValueType == 'edit') {
				scope.myOptions.name = scope.dataSettings.calculatedValueCurrent.name;
				scope.myOptions.sltMetricsList = scope.getObjectsLocal(scope.dataSettings.calculatedValueCurrent.objects);
				scope.myOptions.newFormula = {
					aggregator: scope.getAggregatorLocal(scope.dataSettings.calculatedValueCurrent.aggregator),
					formula: scope.dataSettings.calculatedValueCurrent.code
				};

				//剔除当前编辑的计算指标
				for (var j = 0; j < scope.myOptions.calculatedValueList.length; j++) {
					if (scope.myOptions.calculatedValueList[j].id == scope.dataSettings.calculatedValueCurrent.id) {
						scope.myOptions.calculatedValueList.splice(j, 1);
						break;
					}
				}
			} else {
				scope.myOptions.newFormula = {
					aggregator: "",
					formula: ""
				}
			}

			//获取指标名称列表
			scope.myOptions.fields = scope.getShowNameList();

			//函数列表初始化
			scope.funcKeyWordsInit();
		};

		/**
		 * 表达式插件初始化
		 */
		scope.codeMirrorInit = function () {
			//格式框初始化,timeout是预留出插件初始化时间
			var textareaDom = document.getElementById('calculatedValue-formula');
			$timeout(function () {
				scope.myOptions.codeMirror = CodeMirror.fromTextArea(textareaDom, {
					//mode: "text/x-pt-formula",
					//mode: scope.dsConfig.editor.data.isSupportFunc ? "text/x-pt-formula-func" : "text/x-pt-formula",
					content: textareaDom.value,
					indentWithTabs: true,
					smartIndent: true,
					lineWrapping: false,
					//lineNumbers: true,
					matchBrackets: true,
					theme: "paraiso-light",
					fields: scope.myOptions.fields,
					placeholder: textareaDom.getAttribute('p') ? textareaDom.getAttribute('p') : scope.myOptions.placeholder,
					//onLoad: codeMirrorLoaded()
				});

				function codeMirrorLoaded() {
					angular.element(window).resize();
					uiLoadingSrv.removeLoading(angular.element(element).find('.pt-popup-content'));

				}

				scope.myOptions.codeMirror.on("change", function () {
					//此处的timeout是防止下面的$apply有可能在进行中状态所加
					$timeout(function () {
						scope.$apply(function () {
							scope.myOptions.errorFormula = false;
							scope.myOptions.newFormula.aggregator = scope.myOptions.codeMirror.getValue().replace(/0xa0/, "");
						})
					});
				});

				$(document).ready(function () {
					scope.myOptions.codeMirror.setOption("mode", "text/x-pt-formula");
					scope.myOptions.codeMirror.refresh();
					scope.myOptions.codeMirror.focus();
				});
			}, 200);
		};


		//格式框插件-自定义mode类型
		function codeMirrorModeInit() {
			//此段可以参照插件官网API(http://codemirror.net/doc/manual.html#config)
			!function (e) {

				!function (e) {
					function t(e) {
						e.state.placeholder && (e.state.placeholder.parentNode.removeChild(e.state.placeholder), e.state.placeholder = null )
					}

					function r(e) {
						t(e);
						var r = e.state.placeholder = document.createElement("pre");
						r.style.cssText = "height: 0; overflow: visible",
							r.className = "CodeMirror-placeholder",
							r.appendChild(document.createTextNode(e.getOption("placeholder"))),
							e.display.lineSpace.insertBefore(r, e.display.lineSpace.firstChild)
					}

					function a(e) {
						o(e) && r(e)
					}

					function n(e) {
						var a = e.getWrapperElement()
							, n = o(e);
						a.className = a.className.replace(" CodeMirror-empty", "") + (n ? " CodeMirror-empty" : ""),
							n ? r(e) : t(e)
					}

					function o(e) {
						return 1 === e.lineCount() && "" === e.getLine(0)
					}

					e.defineOption("placeholder", "", function (r, o, i) {
						var s = i && i != e.Init;
						if (o && !s)
							r.on("blur", a),
								r.on("change", n),
								n(r);
						else if (!o && s) {
							r.off("blur", a),
								r.off("change", n),
								t(r);
							var l = r.getWrapperElement();
							l.className = l.className.replace(" CodeMirror-empty", "")
						}
						o && !r.hasFocus() && a(r)
					})
				}(e),
					e.defineMode("ptFormula", function (e, t) {
						function r(t, r) {
							var o = t.next();
							if (p[o]) {
								var i = p[o](t, r, e.fields);
								if (i !== !1)
									return i
							}
							if (1 == m.hexNumber && ("0" == o && t.match(/^[xX][0-9a-fA-F]+/) || ("x" == o || "X" == o) && t.match(/^'[0-9a-fA-F]+'/)))
								return "number";
							if (1 == m.binaryNumber && (("b" == o || "B" == o) && t.match(/^'[01]+'/) || "0" == o && t.match(/^b[01]+/)))
								return "number";
							if (o.charCodeAt(0) > 47 && o.charCodeAt(0) < 58)
								return t.match(/^[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?/),
								1 == m.decimallessFloat && t.eat("."),
									"number";
							if ("?" == o && (t.eatSpace() || t.eol() || t.eat(";")))
								return "variable-3";
							if ("'" == o || '"' == o && m.doubleQuote)
								return r.tokenize = a(o),
									r.tokenize(t, r);
							if ((1 == m.nCharCast && ("n" == o || "N" == o) || 1 == m.charsetCast && "_" == o && t.match(/[a-z][a-z0-9]*/i)) && ("'" == t.peek() || '"' == t.peek()))
								return "keyword";
							if (/^[\(\),\;\[\]]/.test(o))
								return null;
							if (m.commentSlashSlash && "/" == o && t.eat("/"))
								return t.skipToEnd(),
									"comment";
							if (m.commentHash && "#" == o || "-" == o && t.eat("-") && (!m.commentSpaceRequired || t.eat(" ")))
								return t.skipToEnd(),
									"comment";
							if ("/" == o && t.eat("*"))
								return r.tokenize = n,
									r.tokenize(t, r);
							if ("." != o) {
								if (d.test(o))
									return t.eatWhile(d),
										"operator";
								if ("{" == o && (t.match(/^( )*(d|D|t|T|ts|TS)( )*'[^']*'( )*}/) || t.match(/^( )*(d|D|t|T|ts|TS)( )*"[^"]*"( )*}/)))
									return "number";
								t.eatWhile(/^[_\w\d]/);
								var h = t.current().toLowerCase();
								return f.hasOwnProperty(h) && (t.match(/^( )+'[^']*'/) || t.match(/^( )+"[^"]*"/)) ? "number" : l.hasOwnProperty(h) ? "atom" : u.hasOwnProperty(h) ? "keyword" : c.hasOwnProperty(h) ? "builtin" : s.hasOwnProperty(h) ? "string-2" : null
							}
							return 1 == m.zerolessFloat && t.match(/^(?:\d+(?:e[+-]?\d+)?)/i) ? "number" : 1 == m.ODBCdotTable && t.match(/^[a-zA-Z_\d\u4E00-\u9FA5]+/) ? "variable-2" : void 0
						}

						function a(e) {
							return function (t, a) {
								for (var n, o = !1; null != (n = t.next());) {
									if (n == e && !o) {
										a.tokenize = r;
										break
									}
									o = !o && "\\" == n
								}
								return "string"
							}
						}

						function n(e, t) {
							for (; ;) {
								if (!e.skipTo("*")) {
									e.skipToEnd();
									break
								}
								if (e.next(),
										e.eat("/")) {
									t.tokenize = r;
									break
								}
							}
							return "comment"
						}

						function o(e, t, r) {
							t.context = {
								prev: t.context,
								indent: e.indentation(),
								col: e.column(),
								type: r
							}
						}

						function i(e) {
							e.indent = e.context.indent,
								e.context = e.context.prev
						}

						var s = t.client || {},
							l = t.atoms || {
									"false": !0,
									"true": !0,
									"null": !0
								},
							c = t.builtin || {},
							u = t.keywords || {},
							d = t.operatorChars || /^[\/\*\+\-%<>!=&|~^]/,
							m = t.support || {},
							p = t.hooks || {},
							f = t.dateSQL || {
									date: !0,
									time: !0,
									timestamp: !0
								};
						return {
							startState: function () {
								return {
									tokenize: r,
									context: null
								}
							},
							token: function (e, t) {
								if (e.sol() && t.context && null == t.context.align && (t.context.align = !1),
										e.eatSpace())
									return null;
								var r = t.tokenize(e, t);
								if ("comment" == r)
									return r;
								t.context && null == t.context.align && (t.context.align = !0);
								var a = e.current();
								return "(" == a ? o(e, t, ")") : "[" == a ? o(e, t, "]") : t.context && t.context.type == a && i(t),
									r
							},
							indent: function (t, r) {
								var a = t.context;
								if (!a)
									return 0;
								var n = r.charAt(0) == a.type;
								return a.align ? a.col + (n ? 0 : 1) : a.indent + (n ? 0 : e.indentUnit)
							},
							blockCommentStart: "/*",
							blockCommentEnd: "*/",
							lineComment: m.commentSlashSlash ? "//" : m.commentHash ? "#" : null
						}
					}),
					function () {
						function t(e) {
							for (var t; null != (t = e.next());)
								if ("`" == t && !e.eat("`"))
									return "variable-2";
							return e.backUp(e.current().length - 1),
								e.eatWhile(/\w/) ? "variable-2" : null
						}

						function r(e) {
							return e.eat("@") && (e.match(/^session\./),
								e.match(/^local\./),
								e.match(/^global\./)),
								e.eat("'") ? (e.match(/^.*'/),
									"variable-2") : e.eat('"') ? (e.match(/^.*"/),
									"variable-2") : e.eat("`") ? (e.match(/^.*`/),
									"variable-2") : e.match(/^[0-9a-zA-Z$\.\_]+/) ? "variable-2" : null
						}

						function a(e, t, r) {
							for (var a, n = ""; null != (a = e.next());) {
								if ("]" == a && !e.eat("]"))
									return r && scope.formulaIndexOf(r, n) < 0 ? "error" : "variable-2";
								//return r && r.indexOf(n) < 0 ? "error" : "variable-2";
								n += a
							}
							return null
						}

						function n(e) {
							for (var t, r = ""; null != (t = e.next());) {
								if ("]" == t && !e.eat("]"))
									return "variable-2";
								r += t
							}
							return null
						}

						function o(e) {
							return e.eat("N") ? "atom" : e.match(/^[a-zA-Z.#!?]/) ? "variable-2" : null
						}

						function i(e) {
							for (var t = {}, r = e.split(" "), a = 0; a < r.length; ++a)
								t[r[a]] = !0;
							return t
						}

						var s = "alter and as asc between by count create delete desc distinct drop from having in insert into is join like not on or order select set table union update values where "
							, keywords = "sum avg max min count distinct row_max row_min max_date min_date hour_diff minute_diff second_diff day_diff month_diff year_diff week quarter now first_day_of_month last_day_of_month work_day_of_month if year month day hour to_date date_add date_sub concat instring length repeat reverse substr day_of_week regexp_extract regexp_replace time_convert percent ip_location coalesce base64_decode base64_encode";
						e.defineMIME("text/x-pt-formula", {
							name: "ptFormula",
							client: i("charset clear connect edit ego exit go help nopager notee nowarning pager print prompt quit rehash source status system tee"),
							keywords: i(scope.myOptions.funcKeywords),
							atoms: i("false true null unknown"),
							operatorChars: /^[\/*+\-%<>!=&|^]/,
							dateSQL: i("date time timestamp"),
							support: i("ODBCdotTable decimallessFloat zerolessFloat binaryNumber hexNumber doubleQuote nCharCast charsetCast commentHash commentSpaceRequired"),
							hooks: {
								"@": r,
								"`": t,
								"\\": o,
								"[": a
							}
						})
					}()
			}(CodeMirror);
		}


		/**
		 * 指令初始化
		 */
		(function () {
			uiLoadingSrv.createLoading(angular.element(element).find('.calculatedValue-formula'));

			scope.dataInit();

			codeMirrorModeInit();

			scope.codeMirrorInit();
		})();
	}
}


/**
 * 表达式校验
 *
 * @param string: 表达式完整字符串
 * @param reg: 提取指标的正则表达式
 * @param keyWordsLength: 表达式中包含的函数个数
 * @param keyWordsList: 函数关键字列表
 *
 * @returns {boolean}
 */
function formulaCheck(string, reg, keyWordsLength, keyWordsList) {

	var randomKey = new Date().getTime(); //生成一个替换字符串
	var variableList = string.match(reg); //获取表达式中的所有指标列表

	//将表达式中的指标名称剔除
	if (variableList) {
		for (var i = 0; i < variableList.length; i++) {
			string = string.replace(variableList[i], '[]');
		}
	}

	// 剔除空白符
	string = string.replace(/\s/g, '');

	//将表达式中的函数替换为指定字符串
	if (keyWordsLength > 0) {
		var keyText = keyWordsList[0].name + '\\(';
		for (var i = 1; i < keyWordsList.length; i++) {
			keyText = keyText + '|' + keyWordsList[i].name + '\\(';
		}
		console.log(keyText);
		var stringReg = new RegExp(keyText, "gi");
		string = string.replace(stringReg, randomKey + "(");
	}

	console.log(string);


	// 错误情况，空字符串
	if ("" === string) {
		console.log('表达式错误: 空字符串');
		return false;
	}

	// 错误情况，运算符连续(由于负值的存在,先不判断减号(-)和负号(-))
	//if( /[\+\-\*\/]{2,}/.test(string) ){
	if (/[\+\*\/]{2,}/.test(string)) {
		console.log('表达式错误: 运算符连续');
		return false;
	}

	// 空括号
	if (/\(\)/.test(string)) {
		console.log('表达式错误: 空括号');
		return false;
	}

	// 错误情况，括号不配对
	var stack = [];
	for (var i = 0, item; i < string.length; i++) {
		item = string.charAt(i);
		if ('(' === item) {
			stack.push('(');
		} else if (')' === item) {
			if (stack.length > 0) {
				stack.pop();
			} else {
				console.log('表达式错误: 括号不配对');
				return false;
			}
		}
	}
	if (0 !== stack.length) {
		console.log('表达式错误: 括号不配对');
		return false;
	}

	// 错误情况，(后面是运算符(由于负值的存在,先不判断减号(-)和负号(-))
	//if(/\([\+\-\*\/]/.test(string)){
	if (/\([\+\*\/]/.test(string)) {
		console.log('表达式错误: (后面是运算符');
		return false;
	}

	// 错误情况，)前面是运算符
	if (/[\+\-\*\/]\)/.test(string)) {
		console.log('表达式错误: )前面是运算符');
		return false;
	}

	// 错误情况，指标间隔符[前面不是运算符
	if (/[^\+\-\*\/\(]\[/.test(string)) {
		console.log('表达式错误: 指标间隔符[前面不是运算符');
		return false;
	}

	//错误情况, 指标间隔符]后面不是运算符
	if (/][^\+\-\*\/\)]/.test(string)) {
		console.log('表达式错误: 指标间隔符]后面不是运算符');
		return false;
	}

	// 错误情况，(前面不是运算符
	//if(/[^\+\-\*\/(randomKey)]\(/i.test(string)){
	var re = new RegExp("[^\\+\\-\\*\\/\\((" + randomKey + ")]\\(", "i");
	if (re.test(string)) {
		console.log('表达式错误: (前面不是运算符');
		return false;
	}

	// 错误情况，)后面不是运算符
	if (/\)[^\+\-\*\/\)]/.test(string)) {
		console.log('表达式错误: )后面不是运算符');
		return false;
	}

	//错误情况, 指标之间没有运算符
	if (/]\[/.test(string)) {
		console.log('表达式错误: 指标之间没有运算符');
		return false;
	}

	//错误情况, 包含单,双引号
	if (/["']/.test(string)) {
		console.log('表达式错误: 包含引号');
		return false;
	}


	//错误情况，表达式中包含字符串
	var tmpStr = string.replace(/[\(\)\+\-\*\/]{1,}/g, '`');
	var array = tmpStr.split('`');
	for (var i = 0, item; i < array.length; i++) {
		item = array[i];

		if (!/\[.*?]/.test(item) && isNaN(item) && item !== randomKey) {
			console.log('表达式错误: 表达式中包含字符串');
			return false;
		}
	}

	//函数表达式不能嵌套
	var tmpReg = new RegExp(randomKey + "\\(.*?\\)", "gi"); //sum(sum(a+b)) => ['sum(sum(a+b)']
	var tmpFuncList = string.match(tmpReg);
	if (tmpFuncList) {
		for (var t = 0; t < tmpFuncList.length; t++) {
			var tReg = new RegExp(randomKey + "\\(", "gi"); //sum(sum(a+b) => ['sum(', 'sum(']
			if (tmpFuncList[0].match(tReg).length > 1) {
				console.log('表达式错误: 函数不能嵌套');
				return false;
			}
		}
	}
	return true;
}

export default calculatedValue;


