'use strict';

import ProductConfig from 'configs/product.config';
import highmapsTpl from 'components/modules/chart/chart-highmaps/chart-highmaps.tpl.html';
// import Highcharts from 'highcharts';
// import 'highcharts/highmaps';
// import 'vendor/highcharts/map/custom_world';
// import 'vendor/highcharts/map/jp_all';
// import 'vendor/highcharts/mapdata';
import {countryCode, continentsCode} from 'assets/i18n/country_code';

/**
 * highmaps
 * highmaps指令
 *
 */

angular
.module('pt')
.filter('mapCodeFilter', function () {
	return function (input, param) {
		$.each(countryCode, function (i, item) {
			if (input == item['code']) {
				input = item.name[param];
			}
		});
		return input;
	};
});

angular
.module('pt')
.directive('highmaps', ['$timeout', 'datasourceFactory', '$translate', highmaps]);

function highmaps($timeout, datasourceFactory, $translate) {
	return {
		restrict: 'EA',
		template: highmapsTpl,
		link: link
	};

	function link(scope, element, attrs) {
		//判断当前图标类型是否是highchart图表
		var variables = scope.widget.variables;

		scope.changeMapCode = function () {
			scope.widget.baseWidget.mapCode == "world";
		};

		if (scope.widget.baseWidget.graphName == 'map') {

			//取数类型默认为pull
			var t = "pull";
			//获取图表取数类型
			if (variables[0] && variables[0].customApiInfo) {
				t = variables[0].customApiInfo.type;
			}

			var seriesData;
			var mapPath;
			var chartMap;
			//var baseMapPath = "http://code.highcharts.com/mapdata/";
			var baseMapPath = "/assets/libs/highcharts/map/";
			var datasource = datasourceFactory.createDatasource(scope.widget, scope.widget.baseWidget.widgetId, scope.rootPanel.now, t, scope.widget.baseWidget.refreshInterval * 60000, scope.widget.baseWidget.graphName);

			datasourceFactory.watchWidgetChange(scope, datasource);
			datasourceFactory.setWidgetCommonEvent(scope, datasource);

			datasource.setPushData(function () {
				var data = datasource.getData() || {};
				if (datasource.getData()) {
					seriesData = data.series[0] || [];

                    if(!scope.widget.baseWidget.mapCode){
                        scope.widget.baseWidget.mapCode = ProductConfig.defaultMapCode;
                    }

                    if (scope.widget.variables[0] && [3, 12, 28, 29].indexOf(+scope.widget.variables[0].ptoneDsInfoId) > -1) {
                        scope.widget.baseWidget.mapCode = "world";
                    }

                    if (scope.widget.baseWidget.mapCode != "world") {
                        mapPath = Highcharts.mapDataIndex.Countries[scope.widget.baseWidget.mapCode];
                        if (mapPath && Highcharts.maps[mapPath.replace(".js", "")]) {
                            chartMap = mapReady(mapPath, element, scope, seriesData);
                            redrawMap(scope, datasource, chartMap);
                        } else {
                            var paths = mapPath.split("/");
                            $.getScript(baseMapPath + paths[paths.length - 1], function () {
                                console.log("get map from server.");
                                chartMap = mapReady(mapPath, element, scope, seriesData);
                                redrawMap(scope, datasource, chartMap);
                            });
                        }
                    } else{
                        mapPath = "custom/world.js";
						chartMap = mapReady(mapPath, element, scope, seriesData);
						redrawMap(scope, datasource, chartMap);
					}
				} else {
					try {
						chartMap.destroy();
						chartMap = null;
					} catch (ex) {
					}

				}
				//向父级传递数据
				scope.$emit('to-parent', ['dateRange', data.dateRange]);
			});
		}
	}

	function redrawMap(scope, datasource, chartMap) {
		datasource.setRedrawWidgetFunc(function () {
			var showMapName = scope.widget.chartSetting['showMapName'] == 0 ? false : true;
			chartMap.series[0].update({
				dataLabels: {
					enabled: showMapName
				}
			});

			//延迟300ms重绘
			var mapResize = $timeout(
			   function () {
			       chartMap.reflow();
			   },
			   300
			).then(
			   function () {
			       $timeout.cancel(mapResize);
			   }
			);
		});
	}

	function mapReady(mapPath, element, scope, seriesData) {
		var baseMapJSON = Highcharts.maps[mapPath.replace(".js", "")];
		var highchartDiv = $(element).children(".chart-thumb-div");

		try {
			var valueMapping = Object.keys(seriesData.data[1].value[0]);
		} catch (e) {
			valueMapping = getValueMapping(scope);
		}
		//重组数据
		var mapPushData = [];
		$.each(seriesData.data, function (index, feature) {
			var views = parseFloat(feature.value[0][valueMapping[0]]);
			var showViews = feature.showValue ? feature.showValue[0][valueMapping[0]] : views;
			
			if (views == 0) {
				views = null;
				showViews = null;
			}
			if (feature && feature.code) {
				mapPushData.push({
					//特殊处理
					code: (feature.code).toString().replace(" Prefecture", ""),
					value: views,
					showValue: showViews
				});
			}
		});
		var options = createMapOptions(highchartDiv[0], scope, baseMapJSON, valueMapping, mapPushData);
		var chartMap = new Highcharts.Map(options);
		//chartMap.series[0].setData(mapPushData);
		chartMap.redraw();
		return chartMap;
	}

	function getValueMapping(scope) {
		var metrics = angular.fromJson(scope.widget.variables[0].metrics);
		var valueMapping = [];
		for (var i = 0; i < metrics.length; i++) {
			valueMapping.push($translate.instant(metrics[i].i18nCode));
		}
		return valueMapping;
	}

	function createMapOptions(element, scope, baseMapJSON, valueMapping, mapPushData) {

		var sourceData = getPushData(mapPushData);
		var colorAxis = createMapLegendColorAxis(sourceData);
		var maxValue = Array.max(sourceData);

		var joinBy = ['name', 'code'];
		return ({
			chart: {
				type: 'map',
				renderTo: element,
				events: {
					redraw: function () {
						createMapNavigation(this, scope, element);
					}
				},
				margin: [0, 20, 0, 20] //距离上右下左的距离值
			},
			title: {
				text: scope.widget.baseWidget.title
			},

			mapNavigation: {
				enabled: false,
				enableButtons: false,
				enableDoubleClickZoom: false
			},

			credits: {
				enabled: false
			},

			tooltip: {
				formatter: function () {
					var str = "";
					str += this.point.code + '<br>';
					//str += '(' + this.point.properties && this.point.properties['iso-a2'] + ')' ;
					str += valueMapping[0] + ':' + this.point.showValue;
					/*var values = this.point.value;
					 for(var i=0;i<values.length;i++){
					 str += valueMapping[i] + ': ' + values[i][valueMapping[i]] + '<br>';
					 }*/
					return str;
				}
			},

			legend: {
				enabled: false,
				layout: 'vertical',
				align: 'left',
				y: 5,
				symbolHeight: 150,
				symbolRadius: 2,
				symbolWidth: 8,
				verticalAlign: 'top',
				floating: true,
				navigation: {
					style: {
						color: '#BDBDBD'
					}
				},
				valueDecimals: 0,
				itemStyle: {
					//fontWeight: 'bold',
					//fontStyle: 'italic',
					//fontSize: "12px",
					color: '#BDBDBD',
					textDecoration: 'none'
				},
				itemHoverStyle: {
					textDecoration: 'none'
				}
			},

			colorAxis: {
				//min: 1,
				max: maxValue,
				//endOnTick:false,
				//type: 'linear',//logarithmic or linear
				//minColor: '#E6EE9C',//E6EE9C DCEDC8
				// maxColor: '#78AB43',
				tickPosition: 'inside',
				//showLastLabel:false,
				//showFirstLabel:false,
				//tickInterval:1,
				tickPixelInterval: 40,
				tickLength: 0,
				labels: {
					x: 5,
					y: 0,
					//rotation:45,
					step: 1,
					style: {
						color: '#BDBDBD'
					}
					/*formatter:function() {
					 if(this.isLast){
					 return maxValue;
					 }*//*else if(this.isFirst){
					 return Array.min(sourceData);
					 }*//*else{
					 var result = this.value;
					 if (this.value > 1000000) { result = Math.floor(this.value / 1000000) + "M" }
					 else if (this.value > 1000) { result = Math.floor(this.value / 1000) + "k" }
					 return result;
					 }
					 }*/
				},
				dataClasses: colorAxis //colorAxis
			},

			plotOptions: {
				map: {
					dataLabels: {
						enabled: scope.widget.chartSetting['showMapName'] == 0 ? false : true,
						formatter: function () {
							var s;
							/*if((this.point.value && this.point.value[0][valueMapping[0]] >= 0)){
							 s = this.point.properties && this.point.properties['name'];
							 }*/
							if (this.point.value && this.point.value > 0) {
								s = this.point.properties && this.point.properties['name'];
							}
							return s;
						}
					}
				},
				series: {
					mapData: baseMapJSON,
					joinBy: joinBy,
					name: name,
					point: {
						events: {
							click: function () {
								var code = this.code;
								//当数据源为GA时，才能点击
								if (scope.widget.variables[0] && scope.widget.variables[0].ptoneDsInfoId == 1 && Highcharts.mapDataIndex.Countries[code]) {
									scope.$apply(function () {
										scope.widget.widgetDrawing = true;
										scope.widget.baseWidget.mapCode = code;
									})
								}
							}
						}
					},
					states: {
						hover: {
							color: function () {
								return this.point.color
							},
							borderColor: 'white',
							borderWidth: 2,
							brightness: 2
						}
					},
					nullColor: '#EEEEEE',
					borderWidth: 0
				}
			},
			series: [{
				data: mapPushData
			}]
		});
	}

	function getPushData(data) {
		var o = [];
		$.each(data, function (i, item) {
			o.push(item.value);
		});
		return o;
	}

	function createMapLegendColorAxis(array) {
		//var colorArrays = ['#DCEDC8','#D5E1A5','#AED581','#9CCC65','#8BC34A'];
		var colorArrays = ['#DCEDC8', '#C5E1A5', '#AED581', '#9CCC65', '#8BC34A'];
		var result = [], k = 5, num = parseInt(array.length / k);
		array = array.sort(function (a, b) {
			return a - b;
		});
		for (var i = 1; i <= k; i++) {
			var r = array[i * num];
			if (i < k) {
				result.push(r);
			} else {
				result.push(array[array.length - 1]);
			}
		}
		var obj = [];
		$.each(result, function (i, item) {
			var color = {
				from: i > 0 ? result[i - 1] : 0,
				to: result[i],
				color: colorArrays[i]
			}
			obj.push(color);
		})
		return obj;
	}

	function createMapNavigation(chart, scope, element) {
		$(element).find('.zoomCss').remove();
		var x = chart.chartWidth - 30;
		var y = chart.chartHeight - 40;
		var offset = 4;
		var r = 9;
		var distance = 27;
		//放大
		var groupZoomIn = chart.renderer.g()
		.attr({
			class: 'zoomCss',
			zIndex: 9999
		})
		.on("click", function () {
			chart.mapZoom(0.5);
		}).add();
		chart.renderer.circle(x, y, r)
		.attr({
			'id': scope.widget.baseWidget.widgetId + 'ZoomIn',
			'stroke-width': 2,
			//stroke: '#ffffff',
			fill: '#BDBDBD'
		})
		.on("mouseover", function () {
			$(this).attr("fill", "#757575");
		})
		.on("mouseout", function () {
			$(this).attr("fill", "#BDBDBD");
		})
		.add(groupZoomIn);
		chart.renderer.path(['M', x, y - offset, 'L', x, y + offset,
			'M', x - offset, y, 'L', x + offset, y])
		.attr({
			'stroke-width': 2,
			stroke: '#ffffff',
			zIndex: 2
		})
		.on("mouseover", function () {
			$('#' + scope.widget.baseWidget.widgetId + 'ZoomIn').attr("fill", "#757575");
		})
		.on("mouseout", function () {
			$('#' + scope.widget.baseWidget.widgetId + 'ZoomIn').attr("fill", "#BDBDBD");
		})
		.add(groupZoomIn);

		//缩小
		y = y + distance;
		var groupZoomOut = chart.renderer.g()
		.attr({
			class: 'zoomCss',
			zIndex: 9999
		})
		.on("click", function () {
			chart.mapZoom(2);
		}).add();
		chart.renderer.circle(x, y, r)
		.attr({
			'stroke-width': 1,
			'id': scope.widget.baseWidget.widgetId + 'ZoomOut',
			//stroke: '#ffffff',
			fill: '#BDBDBD'
		})
		.on("mouseover", function () {
			$(this).attr("fill", "#757575");
		})
		.on("mouseout", function () {
			$(this).attr("fill", "#BDBDBD");
		})
		.add(groupZoomOut);
		chart.renderer.path(['M', x - offset, y, 'L', x + offset, y])
		.attr({
			'stroke-width': 2,
			stroke: '#ffffff',
			zIndex: 2
		})
		.on("mouseover", function () {
			$('#' + scope.widget.baseWidget.widgetId + 'ZoomOut').attr("fill", "#757575");
		})
		.on("mouseout", function () {
			$('#' + scope.widget.baseWidget.widgetId + 'ZoomOut').attr("fill", "#BDBDBD");
		})
		.add(groupZoomOut);

		//重置
		y = y - 2 * distance;
		var groupZoomReset = chart.renderer.g()
		.attr({
			class: 'zoomCss',
			zIndex: 9999
		})
		.on("click", function () {
			chart.mapZoom();
		}).add();
		chart.renderer.circle(x, y, r)
		.attr({
			'id': scope.widget.baseWidget.widgetId + 'zoomReset',
			'stroke-width': 1,
			//stroke: '#ffffff',
			fill: '#BDBDBD'
		})
		.on("mouseover", function () {
			$(this).attr("fill", "#757575");
		})
		.on("mouseout", function () {
			$(this).attr("fill", "#BDBDBD");
		})
		.add(groupZoomReset);
		//chart.renderer.image('public/img/chart/map-reset.png',x - 8,y - 8,16,16)
		var m = 4.0;
		var narrowPath = ['M', x - 3, y - 6, 'c', 1.477 / m, -0.331 / m, 3.189 / m, -0.507 / m, 4.955 / m, -0.507 / m, 'c', 3.859 / m, 0 / m, 12.759 / m, 0.924 / m, 18.903 / m, 8.666 / m, 'l', 0.021 / m, -0.021 / m, 'l', 3.984 / m, -9.189
		/ m, 'c', 0.135 / m, -0.308 / m, 0.457 / m, -0.489 / m, 0.789 / m, -0.442 / m, 'c', 0.166 / m, 0.023 / m, 0.313 / m, 0.101 / m, 0.426 / m, 0.212 / m, 'c', 0.113 / m, 0.113 / m, 0.189 / m, 0.264 / m, 0.211 / m, 0.432 / m, 'l', 2.992 / m, 22.889
		/ m, 'c', 0.029 / m, 0.229 / m, -0.047 / m, 0.461 / m, -0.213 / m, 0.625 / m, 'c', -0.163 / m, 0.163 / m, -0.396 / m, 0.242 / m, -0.625 / m, 0.213 / m, 'l', -22.889 / m, -2.993 / m, 'c', -0.334 / m, -0.042 / m, -0.596 / m, -0.304 / m, -0.643 / m, -0.636
		/ m, 'c', -0.047 / m, -0.333 / m, 0.134 / m, -0.654 / m, 0.442 / m, -0.79 / m, 'l', 9.19 / m, -3.984 / m, 'l', 0.82 / m, -0.82 / m, 'c', -3.938 / m, -5.565 / m, -9.898 / m, -6.459 / m, -13.273 / m, -6.459 / m, 'c', -2 / m, 0 / m, -3.37 / m, 0.296 / m, -3.428 / m, 0.308
		/ m, 'c', -0.187 / m, 0.041 / m, -0.374 / m, -0.076 / m, -0.414 / m, -0.265 / m, 'l', -1.516 / m, -6.821 / m, 'z'];
		var narrow1 = chart.renderer.path(narrowPath)
		.attr({
			'stroke-width': 1,
			fill: '#ffffff',
			zIndex: 2
		})
		.on("mouseover", function () {
			$('#' + scope.widget.baseWidget.widgetId + 'zoomReset').attr("fill", "#757575");
		})
		.on("mouseout", function () {
			$('#' + scope.widget.baseWidget.widgetId + 'zoomReset').attr("fill", "#BDBDBD");
		})
		.add(groupZoomReset);

		var narrow2 = chart.renderer.path(narrowPath)
		.attr({
			'stroke-width': 1,
			fill: '#ffffff',
			zIndex: 3,
			'transform': 'rotate(180 ' + x + ' ' + y + ')'
		})
		.on("mouseover", function () {
			$('#' + scope.widget.baseWidget.widgetId + 'zoomReset').attr("fill", "#757575");
		})
		.on("mouseout", function () {
			$('#' + scope.widget.baseWidget.widgetId + 'zoomReset').attr("fill", "#BDBDBD");
		})
		.add(groupZoomReset);

		if (scope.widget.baseWidget.mapCode != "world") {
			//返回世界
			y = y - distance;
			var groupZoomWorld = chart.renderer.g()
			.attr({
				class: 'zoomCss',
				zIndex: 9999
			})
			.on("click", function () {
				scope.$apply(function () {
					scope.widget.baseWidget.mapCode = "world";
				});

			}).add();
			chart.renderer.circle(x, y, r)
			.attr({
				'id': scope.widget.baseWidget.widgetId + 'world',
				'stroke-width': 1,
				//stroke: '#ffffff',
				fill: '#BDBDBD'
			})
			.on("mouseover", function () {
				$(this).attr("fill", "#757575");
			})
			.on("mouseout", function () {
				$(this).attr("fill", "#BDBDBD");
			})
			.add(groupZoomWorld);
			//chart.renderer.image('public/img/chart/map-back.png',x - 8,y - 8,16,16)
			var n = 3.8;
			chart.renderer.path(['M', x + 4, y + 7, 'c', 5.68 / n, -10.3 / n, 6.64 / n, -26.01 / n, -15.7 / n, -25.49 / n, 'v', 12.69 / n, 'l', -19.2 / n, -19.2 / n, 19.2 / n, -19.2 / n, 'v', 12.4 / n,
				'c', 26.7 / n, -0.6 / n, 29.7 / n, 23.6 / n, 15.6 / n, 38.7 / n, 'z'])
			.attr({
				'stroke-width': 1,
				//stroke: '#ffffff',
				fill: '#ffffff',
				zIndex: 2
			})
			.on("mouseover", function () {
				$('#' + scope.widget.baseWidget.widgetId + 'world').attr("fill", "#757575");
			})
			.on("mouseout", function () {
				$('#' + scope.widget.baseWidget.widgetId + 'world').attr("fill", "#BDBDBD");
			})
			.add(groupZoomWorld);
		}
	}
}
