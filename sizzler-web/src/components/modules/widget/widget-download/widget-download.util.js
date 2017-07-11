
import{
	getMyDsConfig,
	LINK_CSV_WIDGET_DATA
} from 'components/modules/common/common';

import cookieUtils from 'utils/cookie.utils';

//在widget中下载数据
angular
	.module('pt')
	.factory('widgetDownloadUtils', ['datasourceFactory', 'dataMutualSrv', '$document', "$http", "$rootScope", "$translate", function (datasourceFactory, dataMutualSrv, $document, $http, $rootScope, $translate) {
		var COLUMNDELIMITER = COLUMNDELIMITER || ',';
		var LINEDEMLIMITER = LINEDEMLIMITER || '\n';

		function convertArrayOfObjectsToCSV(data) {
			if (data == null || !data.length) {
				return null;
			}
			var result = '';
			$.each(data, function (i, row) {
				//需要处理特殊格式的数据
				result += row.join(COLUMNDELIMITER);
				result += LINEDEMLIMITER;
			});
			return result;
		}

		/**
		 支持两个参数
		 response.reportData        csv的数据
		 response.reportName        csv的文件名称
		 */
		function downloadCSV(response) {
			var data;
			var csv = convertArrayOfObjectsToCSV(response.reportData);
			if (csv == null) return;
			var filename = response.reportName;
			var link = document.createElement('a');
			var ua = window.navigator.userAgent;
			var isChrome = ua.match(/Chrome/i);
			var isSafari = ua.match(/Safari/i);
			var isFirefox_IE = ua.match(/Firefox/i) || ua.match(/MSIE/i)
			if (typeof link.download != "undefined") {
				if (!csv.match(/^data:text\/csv/i)) {
					csv = 'data:text/csv;charset=utf-8,\ufeff' + csv;
				}
				data = encodeURI(csv);
				console.log(data);
				//download attribute is supported
				//create a temp link and trigger click function on it
				//this is not working on safari
				var _body = $document.find('body');
				var _a = $("<a href='#' download='" + filename + "'></a>").appendTo(_body);
				_a[0].href = data;
				_a[0].click();
				_a.remove();

			} else {

			}
		}

		/**
		 处理双引号
		 */
		function cleanData(data) {
			if (typeof(data) == 'string') {
				var reg = new RegExp("\"", "g");
				return data.replace(reg, "\"\"");
			} else {
				return data;
			}
		}

		function createCSV(_chartData, _widgetTitle, _panleTitle) {
			if (_chartData && _chartData.series[0] && _chartData.series[0].data) {
				var _data = [];
				var tableColumnCode = _chartData.columnsCode || []
				angular.forEach(_chartData.series[0].data, function (row, index) {
					if (index == 0) {
						var _header = [];
						angular.forEach(row, function (col, colIndex) {
							if (colIndex < tableColumnCode.length) {
								//处理特殊字符
								_header.push('"' + cleanData(col) + '"');
							}
						});
						_data.push(_header);
					} else {
						var _colData = [];
						angular.forEach(row, function (col, colIndex) {
							if (colIndex < tableColumnCode.length) {
								//处理特殊字符
								_colData.push('"' + cleanData(col) + '"');
							}
						});
						_data.push(_colData);
					}
				});
				var _response = {
					reportData: _data,
					reportName: _widgetTitle + '(' + _panleTitle + ')' + '.csv'
				};
				downloadCSV(_response);
			}
		}

		function isShowExampleData(widget) {
			return widget.baseWidget.isExample == 1;
		}

		return {
			download: function (widget, panel, $scope) {
				$scope.modal.download.isBegin = true;
				if (widget == null) {
					//弹出提示
					console.log("widget not defind");
				} else {
					var _baseWidget = widget.baseWidget;
					var _widgetId = _baseWidget.widgetId;
					var _widgetTitle = _baseWidget.widgetTitle;
					var _panleTitle = panel.panelTitle;
					var _sourceData = datasourceFactory.getDatasource(_widgetId);
					var _graphName = _baseWidget.graphName;
					//如果是table类型的，就直接使用_sourceData.chartData的数据，如果不是table类型的，就走接口获取table的数据
					//if (_graphName == "table") {
					if (_graphName == "other_graph_name") {
						//执行下载
						createCSV(_sourceData.chartData, _widgetTitle, _panleTitle);
						$scope.modal.download.isBegin = false;
					} else {
						var param = "&isTemplet=" + widget.baseWidget.isTemplate == 1;
						param += "&isExample=" + isShowExampleData(widget);
						param += "&templetId=" + widget.baseWidget.templetId;

						var widgetInfoStr = '{}';
						var widgetInfo = angular.copy(widget);

						// 删除多余的属性值
						delete widgetInfo._ext;
						delete widgetInfo.widgetDrawing;
						delete widgetInfo.dateValue;
						delete widgetInfo.sizeX;
						delete widgetInfo.sizeY;
						delete widgetInfo.minSizeX;
						delete widgetInfo.minSizeY;
						delete widgetInfo.row;
						delete widgetInfo.col;
						delete widgetInfo.baseWidget.dimensionsJson;
						delete widgetInfo.baseWidget.metricsJson;
						delete widgetInfo.baseWidget.description;
						delete widgetInfo.baseWidget.ptoneWidgetInfoId;
						delete widgetInfo.baseWidget.widgetTitle;
						delete widgetInfo.baseWidget.isPublished; // widget模板中标记模板是否发布

						var dsConfig = getMyDsConfig(widgetInfo.variables[0].dsCode);

						// 修正widgetInfo,指标、维度国际化
						angular.forEach(widgetInfo.variables, function (variable) {

							//修正指标显示名称(别名)
		                    angular.forEach(variable.metrics, function(m){
		                        if(m.alias) {
		                            m.name = m.alias;
		                        }
		                        else if(dsConfig.editor.data.translateTitle && m.i18nCode){
		                            //修正指标国际化

		                            var name = $translate.instant(m.i18nCode);
		                            m.name = dsConfig.editor.data.metricsHasCount ? (m.calculateType+'('+name+')') : name;
		                        }
		                    });

		                    //修正维度显示名称(别名)
		                    angular.forEach(variable.dimensions, function(d){
		                        if(d.alias){
		                            d.name = d.alias;
		                        }
		                        else if(dsConfig.editor.data.translateTitle && d.i18nCode){
		                            //修正维度国际化

		                            d.name = $translate.instant(d.i18nCode);
		                        }
		                    });
						});

						// 增加panel全局设置
						var panelInfo = panel;
						if(panelInfo && panelInfo.panelId == widgetInfo.panelId
							&& panelInfo.components){

							// 全局时间设置
							var globalTimeValue = null;
							var globalTimeObj = panelInfo.components.GLOBAL_TIME;
							if(globalTimeObj && globalTimeObj.status == 1){
								globalTimeValue = globalTimeObj.value;
							}

							// 全局设置替换widget设置
							if(globalTimeValue && globalTimeValue != 'widgetTime'){
								widgetInfo.baseWidget.dateKey = globalTimeValue;
							}
						}

						widgetInfoStr = JSON.stringify(widgetInfo);

						$http({
							method: 'POST',
							url: LINK_CSV_WIDGET_DATA + _widgetId + "?a=1" + param + "&sid=" + cookieUtils.get('sid'),
							data: widgetInfoStr,
							withCredentials: true
						}).success(function (data) {
							console.log(JSON.stringify(data));
							if (data.status != 'success') {
								console.log("取数失败");
							} else {
								var widgetData = data.content;
								if (widgetData.status == 'noData') {
									console.log("没有数据");
								} else if (widgetData.status == 'failed') {
									console.log("取数失败");
								} else { // success
									var returnData = _sourceData.getTodoData(widgetData);
									//下载数据之前，还需要先处理一遍数据，对数据进行格式化
									createCSV(returnData, _widgetTitle, _panleTitle);
								}
							}
							$scope.modal.download.isBegin = false;
						}).error(function (data, status, headers, config) {
							console.log("取数失败");
							$scope.modal.download.isBegin = false;
						});
					}
				}
			}
		}

	}]);
