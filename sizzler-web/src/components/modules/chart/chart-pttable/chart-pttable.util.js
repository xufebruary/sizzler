import 'assets/libs/jquery/datatables/jquery.dataTables.min';
// import 'vendor/jquery/datatables/jquery.dataTables.css';

import {
	LINK_WIDGET_EDIT,
	html_encode,
	getMyDsConfig,
	objectIsEmpty
} from '../../common/common';


//pttable图表处理service
angular
	.module('pt')
	.factory('pttableNGUtils', ['$timeout', 'datasourceFactory', 'dataMutualSrv', function ($timeout, datasourceFactory, dataMutualSrv) {

		return {
			createDataTableChart: function (scope, element, config, datasource) {

				var tableChart = null;

				var defSort = [];
				var flag = false;
				var initTableChart = function (tmpDatasource) {
					var widgetH = scope.widget.sizeY * scope.rootChart.rowHeight - 12;//12为widget上下边距及边线
					// var tableH = scope.widget.baseWidget.showMetricAmount == 1 ? (widgetH-34-12-60-24)*0.9 : (widgetH-30-12-24)*0.8;
					var tableH = scope.widget.baseWidget.showMetricAmount == 1 ? (widgetH - 34 - 24 - 17 - 56) : (widgetH - 34 - 24 - 17 - 32);
					var theadH = 30;
					var rowH = 30;

					//如果chart不为空就销毁
					if (tableChart) {
						tableChart.fnClearTable();
						tableChart.fnDestroy();
						$(tableChart).empty(); // 清空table
					}

					var tableColumn = [];
					var tableColumnCode = [];
					var tableData = [];
					var tabLength = parseInt((tableH - theadH - 2) / rowH);
					var hasFilter = false;
					var hasSort = true;
					var hasPaging = true;
					var hasInfo = false;
					var hasLengthChange = false;

					/*
					 * 构建table数据
					 * 修改源码：1、给<th>、<td> 增加title （2015年11月21日）
					 *
					 */
					var data = tmpDatasource.getData();


					if (data && data.series[0] && data.series[0].data) {
						angular.forEach(data.series[0].data, function (row, index) {
							if (index == 0) {
								angular.forEach(row, function (col, colIndex) {
									tableColumn.push({'title': col});
								});
							} else {
								angular.forEach(row, function (col, colIndex) {
									row[colIndex] = html_encode(col + '');//将表格中的html数据进行转义(需转成字符串统一正则处理)
								});
								tableData.push(row);
							}
						});

						if (tableData.length <= tabLength) {
							hasPaging = false;
						} else {
							tabLength = parseInt((tableH - theadH - 2 - 30) / rowH);

							if(tabLength == 0){
								tabLength = 1;
								element.css('margin-top', '-10px');
							}
							else {
								element.removeAttr('style');
							}
						}

						var tableColumnCode = data.columnsCode || [];
						// 计算第一个指标列（默认按照第一个指标降序排序）
						var firstMetrics = data.firstMetricsIndex || 0;
						var srcColumnLength = data.srcColumnLength || 0;
						var fixSortColumnIndex = data.fixSortColumnIndex || {};
						var defaultOrder = [];
						var _columnDataType = data.columnDataType;
						if (tableColumn.length == 0) {
							hasSort = false;
						} else {
							var dsCode = tmpDatasource.widget.variables[0].dsCode;
							var dsConfig = getMyDsConfig(dsCode);

							// if (scope.sharePanelFlag) {//分享panel，关系型数据库默认没有排序
							// 	var dsId = tmpDatasource.widget.variables[0].ptoneDsInfoId;
							// 	hasSort = dsConfig.chart.table.useDefaultSort;
							// }
							//关系型数据库以外的数据源使用默认前端排序
							if (dsConfig.chart.table.useDefaultSort) {
								defaultOrder.push([firstMetrics < tableColumn.length ? firstMetrics : 0, "desc"]);
							} else {
								var order = tmpDatasource.widget.variables[0].sort && tmpDatasource.widget.variables[0].sort ? JSON.parse(tmpDatasource.widget.variables[0].sort)[0] : {};
								if (!objectIsEmpty(order)) {
									var key, tempFlag = false;
									for (var j = 0; j < tableColumnCode.length; j++) {
										for (key in order) {
											if (key == tableColumnCode[j]) {
												defaultOrder = [[j, order[key]]];
												tempFlag = true;
											}
										}
									}
									if (!tempFlag) {
										defaultOrder = [];
										tmpDatasource.widget.variables[0].sort = '';
										dataMutualSrv.post(LINK_WIDGET_EDIT, angular.copy(tmpDatasource.widget), 'wgtSave')
									}
								} else {
									defaultOrder = [];
								}
							}
						}

						// 获取tableDiv
						var talbeDiv = $(element).find("table");
						var aoColumns = [];
						var sWidth = parseInt(1 / (firstMetrics + tableColumn.length) * 100);
						for (var i = 0; i < tableColumn.length; i++) {
							var bVisible = (i >= srcColumnLength ? false : true);
							var iDataSort = fixSortColumnIndex[i] || i;
							var _aoColumnsTemp = {
								"sWidth": i < firstMetrics ? sWidth * 2 + '%' : sWidth + '%',
								"sTitle": tableColumn[i].title + '',
								"iDataSort": iDataSort,
								"bVisible": bVisible
							};
							if (_columnDataType) {
								var __columnDataType = _columnDataType[encodeURI(tableColumn[i].title)];
								//console.log(_columnDataType);
								if (typeof(__columnDataType) != "undefined" && __columnDataType != null && __columnDataType.length != 0) {
									var _dataType = __columnDataType;
									//验证是否需要设置mRender
									var _mRender = dataRenderFormat(_dataType);
									if (_mRender != null) {
										_aoColumnsTemp.mRender = _mRender;
									}
								}
							}
							aoColumns.push(_aoColumnsTemp);
						}

						tableChart = talbeDiv.dataTable({
							"displayLength": tabLength,
							"columns": tableColumn,
							"data": tableData,
							"bFilter": hasFilter,
							"bSort": hasSort,
							"bPaginate": hasPaging,
							"bInfo": hasInfo,
							"bLengthChange": hasLengthChange,
							"bDestory": true,
							"order": defaultOrder,
							"aoColumns": aoColumns,
							language: {
								//lengthMenu : '',
								//lengthMenu: '<select class="form-control input-xsmall">' + '<option value="5">5</option>' + '<option value="10">10</option>' + '<option value="20">20</option>' + '<option value="30">30</option>' + '<option value="40">40</option>' + '<option value="50">50</option>' + '</select>条记录',//左上角的分页大小显示。
								processing: "",//处理页面数据的时候的显示
								paginate: {//分页的样式文本内容。
									previous: "<",
									next: ">",
									first: "<<",
									last: ">>"
								},
								zeroRecords: "No data to show",//table tbody内容为空时，tbody的内容。
								//下面三者构成了总体的左下角的内容。
								//info: "总共_PAGES_ 页，显示第_START_ 到第 _END_ ，筛选之后得到 _TOTAL_ 条，初始_MAX_ 条 ",//左下角的信息显示，大写的词为关键字。
								infoEmpty: "No data to show",//筛选为空时左下角的显示。
								infoFiltered: ""//筛选之后的左下角筛选提示(另一个是分页信息显示，在上面的info中已经设置，所以可以不显示)，
							}
						});


						tableChart.fnDraw();

						//mysql等关系型数据库的表格排序需要自定义
						$(element).find("table").on('order.dt', function (e, settings) {
							if (flag) {
								flag = false;
								var obj = {};

								var dsCode = tmpDatasource.widget.variables[0].dsCode;
								var dsConfig = getMyDsConfig(dsCode);

								if (settings.aLastSort[0] && !dsConfig.chart.table.useDefaultSort) {//share 下面不允许排序----20170314放开排序
									var tempData = tmpDatasource.getData();
									if (tempData && tempData.columnsCode) {
										// settings.aLastSort[0].col 取的为实际排序列索引，
										// settings.aLastSort[0].src 取的为点击列排序索引，
										// fix by xupeng 20160811
										obj[tempData.columnsCode[settings.aLastSort[0].src]] = settings.aLastSort[0].dir;
										var sortJson = [];
										sortJson.push(obj);
										scope.$apply(function(){
											tmpDatasource.widget.variables[0].sort = JSON.stringify(sortJson);
										});
										
										if(!scope.sharePanelFlag){//分享页面不需要update
											dataMutualSrv.post(LINK_WIDGET_EDIT, angular.copy(tmpDatasource.widget), 'wgtSave');
										}
									}
								}
							}
						});

						flag = true;
					}
				};

				var drawTableChart = function () {

					//调用图形初始化函数
					initTableChart(datasource);
					// tmpDatasource.afterWidgetDrawEvent();
				}
				//根据数据类型来处理对应列的数据
				var dataRenderFormat = function (dataType, unit, dataFormat) {
					var _mRender = null;
					if (dataType == null || dataType == "" || dataType.length == 0) {
						return _mRender;
					}

					var _checkNumber = function (source) {
						var reg = /^(\-|\+)?\d+(\.\d+)?$/; //判断字符串是否为数字(正数、负数、小数) //var reg = /^[0-9]+.?[0-9]*$/
						return !reg.test(source);
					}

					var _formatFactory = {
						"number": function (source, type, val) {
							if (type == "display") {
								if (source != null && source != "" && source.length != 0) {
									//验证当前数据是否是数字类型
									if (_checkNumber(source)) {
										return source; // 如果不是数值直接将data返回，unit抛弃（table中的维度列）
									}
									source = datasource.splitNumber(source);
									return source;
								} else {
									return source;
								}
								//处理数据
								return source;
							} else {
								return source;
							}
						},
						/**
						 货币类型的稍后处理
						 */
						"money": function (source, type, val) {
							if (type == "display") {
								//验证当前数据是否是数字类型
								if (source != null && source != "" && source.length != 0) {
									//var _moneyType = [{"$##":"$"}, {"€##":"€"}, {"¥##":"¥"}, {"¥###":"¥"}];
									var numberSign = ""; // 数值正负号
									if (source.substring(0, 1) == "-") {
										numberSign = "-";
										source = source.substring(1, source.length);
									}

									//直接截取第一位数留下
									var _unit = source.substring(0, 1);
									var _money = source;
									if (_checkNumber(_unit)) {
										//如果单位不是个数字，则证明当前货币有货币符号
										_money = source.substring(1, source.length);
									} else {
										_unit = "";
									}

									if (_checkNumber(_money)) {
										return source; // 如果不是数值直接将data返回，unit抛弃（table中的维度列）
									}
									source = datasource.splitNumber(_money);
									return numberSign + _unit + source;
								} else {
									return source;
								}
								//处理数据
								return source;
							} else {
								return source;
							}
						}
					}

					var _checkType = datasource.checkDataType(dataType);

					if (_checkType != null) {
						switch (_checkType) {
							case "number":
								_mRender = _formatFactory.number;
								break;
							case "money":
								_mRender = _formatFactory.money;
								break;
						}
					}

					return _mRender;
				}

				//判断取数任务中是否有数据，如果有就直接设置categories、series、plotLine
				datasource.setPushData(function () {
					scope.data = this.getData() || {};
					//向父级传递数据
					scope.$emit('to-parent', ['dateRange', scope.data.dateRange]);

					drawTableChart(datasource);
				});

				datasource.setRedrawWidgetFunc(drawTableChart);

				//for test change width 大小
				scope.resizeWidth = function () {
					drawTableChart(datasource);
				};

				//监听销毁事件，销毁图表，清除内存中数据
				scope.$on('$destroy', function () {
					if (tableChart) {
						try {
							tableChart.fnClearTable();
							tableChart.fnDestroy();
							$(tableChart).empty();
						} catch (ex) {
						}

						$timeout(function () {
							element.remove();
						}, 0);
					}
				});

			}

		}

	}]);
