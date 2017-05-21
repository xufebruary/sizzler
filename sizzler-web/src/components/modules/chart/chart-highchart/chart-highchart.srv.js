
// import  Highcharts from 'highcharts';

import {
	getHighchartsLocalization
} from './highchart.l18n';

import {
	FormatNumber,
	getLocalLang
} from 'components/modules/common/common';



//日期加天数方法
function getNewDay(dateTemp, days) {
    var dateTemp = dateTemp.split("-");
    var nDate = new Date(dateTemp[1] + '-' + dateTemp[2] + '-' + dateTemp[0]); //转换为MM-DD-YYYY格式
    var millSeconds = Math.abs(nDate) + (days * 24 * 60 * 60 * 1000);
    var rDate = new Date(millSeconds);
    var year = rDate.getFullYear();
    var month = rDate.getMonth() + 1;
    if (month < 10) month = "0" + month;
    var date = rDate.getDate();
    if (date < 10) date = "0" + date;
    return (month + "-" + date);
}


//存储公用数据
angular.module('pt')
    .factory('highchartsNGUtils', ['$timeout', 'datasourceFactory', function ($timeout, datasourceFactory) {

        var lineWidth = 160;
        var lineHeight = 100;
        var lineMargin = [0, 0, 0, 0];
        var highchartColorList = ['#C5E1A5', '#EEEEEE', '#A2E0AC', '#B2E2F9', '#F0F4C3', '#F18B85', '#9BAAD3', '#F7B2CC', '#FCD592', '#BC8E84'];
        //var areaColorList = ['#C5E1A5','#e8f2dd','#A2E0AC','#B2E2F9','#F0F4C3','#F18B85','#9BAAD3','#F7B2CC','#FCD592','#BC8E84'];
        //var areaHighlightColorList = ['#AED581','#DCEDC8','#6FBD7F','#88C5EC','#DCE775','#EE6E66','#6E7FBC','#F48FB1','#FBC349','#A56B60'];
        var pieColorList = ['#AED581', '#6FBD7F', '#6E7FBC', '#88C5EC', '#E6EE9C', '#EE6E66', '#FBC349', '#F48FB1', '#A56B60', '#DCEDC8'];
        //var pieHighlightColorList = ['#C5E1A5','#A2E0AC','#9BAAD3','#B2E2F9','#F0F4C3','#F18B85','#FCD592', '#F7B2CC','#BC8E84','#e8f2dd'];
        var columnColorList = ['#AED581', '#6FBD7F', '#B39DDB', '#88C5EC', '#F48FB1', '#FBC349', '#A56B60', '#D4E157', '#2196F3', '#EE6E66'];
        //var columnHighlightColorList = ['#C5E1A5','#DCEDC8','#A2E0AC','#B2E2F9','#9BAAD3','#F0F4C3','#F18B85','#FCD592','#F7B2CC','#BC8E84'];
        var lineColorList = ["#AED581", "#2196F3", "#b39ddb", "#6fbd7f", "#F48FB1", "#FBC349", "#A56B60", "#DCE775", "#88c5ec", "#EE6E66"];

        //var lineHighlightColorList = ['#C5E1A5','#DCEDC8','#A2E0AC','#B2E2F9','#F0F4C3','#F18B85','#9BAAD3','#F7B2CC','#FCD592','#BC8E84'];
        var plotLineColor = '#F18B85';
        var pieOthersColor = '#DCEDC8';

        // 国际化配置
        var localeLang;
        var startOfWeek; // 0 = Sunday, 1 = Monday. Defaults to 1
        var localeTooltipDateTimeLabelFormats;
        var localeXAxisDateTimeLabelFormats;


        //highchart类型
        var chartTypeMap = {
            'stock': 'StockChart',
            'map': 'Map',
            'chart': 'Chart'
        };


        //在点击的时候触发
        var onBlurCss = function () {
            var obj = $('.onBlurEvent')
            $.each(obj, function (i, item) {
                item.blur();
            });
        };


        /**
         * 初始化highcharts
         *
         * 修改源码：
         *   1、修该chart中的overflow:hidden 去掉
         *   2、legend： drawLineMarker方法增加symbolHidde属性（L11342）
         */
        var highchart = function (scope, element, config, datasource) {
            /**
             * 判断是否为自定义widget
             */
            var isCustomWidget = scope.widget.baseWidget.widgetType == 'custom';
            var base = {
                widgetX: null,
                widgetY: null,
                widgetW: null,
                widgetH: null,
                chartH: null,
                chartSetting: null,
                showLegend: null,
                showY1: null,
                showX: null,
                showDataLabels: null,
                legendW: null,       //图例显示的宽
                legendMaxH: null    //图例显示的最大高
            };

            var chart = null;   //chart绘制对象
            var optionsMap = {};
            var mergedOptions = {};

            //创建
            create();

            //创建
            function create() {

                //注册数据注入事件
                datasource.setPushData(function () {

                    var data = datasource.getData();

                    if (data) {

                        // 设置数据时间范围(图表上方显示时间区间)
                        scope.data = data;

                        //向父级传递数据
                        scope.$emit('to-parent', ['dateRange', scope.data.dateRange]);

                        //调用图形初始化函数
                        init();

                        //绘制
                        if (chart) {
                            drawChart(chart, datasource);
                        }
                    } else {
                        if (chart) {
                            try {
                                chart.destroy();
                                chart = null;
                            } catch (ex) {
                            }
                        }
                    }
                });


                //监听图表切换：判断是否是重新初始化Highcharts图表还是销毁Highcharts图表
                if (isCustomWidget) {
                    scope.$watch('child.baseWidget.graphName', function (newGraph, oldGraph) {
                        if (newGraph == oldGraph) return;
                        var f = false;
                        //如果是highchart图表就重新初始化，如果不是就销毁
                        for (var i = 0; i < scope.$root.chartMaps.length; i++) {
                            if (scope.$root.chartMaps[i].chartType == newGraph) {
                                if (scope.$root.chartMaps[i].dataType == 1 || scope.$root.chartMaps[i].dataType == 0) {
                                    f = true;
                                }
                            }
                        }
                        if (f) {
                            init();
                        } else {
                            try {
                                chart.destroy();
                                chart = null;
                            } catch (ex) {
                            }
                        }
                    });
                } else {
                    scope.$watch('widget.baseWidget.graphName', function (newGraph, oldGraph) {
                        if (newGraph == oldGraph) return;
                        var f = false;
                        //如果是highchart图表就重新初始化，如果不是就销毁
                        for (var i = 0; i < scope.$root.chartMaps.length; i++) {
                            if (scope.$root.chartMaps[i].chartType == newGraph) {
                                if (scope.$root.chartMaps[i].dataType == 1 || scope.$root.chartMaps[i].dataType == 0) {
                                    f = true;
                                }
                            }
                        }
                        if (f) {
                            init();
                        } else {
                            try {
                                chart.destroy();
                                chart = null;
                            } catch (ex) {
                            }
                        }
                    });
                }

                //监听销毁事件，销毁图表，清除内存中数据
                scope.$on('$destroy', function () {
                    if (chart) {
                        try {
                            chart.destroy();
                            chart = null; // chart.destroy()后，chart还存在，不为 null
                        } catch (ex) {
                        }

                        $timeout(function () {
                            element.remove();
                        }, 0);
                    }
                });

                // 设置公共loading 事件
                datasourceFactory.setWidgetCommonEvent(scope, datasource);

                // 注册widget重绘方法
                datasource.setRedrawWidgetFunc(function (drawType) {
                    if (chart) {
                        var chartType = isCustomWidget ? scope.child.baseWidget.graphName : scope.widget.baseWidget.graphName;
                        if (drawType == 'reflow' && chartType != 'pie' && chartType != 'bar') {
                            configUpData();
                            chart.options.chart.height = base.chartH; // 更新高度
                            chart.options.legend.maxHeight = base.legendMaxH; // 更新图例计算高度
                            chart.reflow();
                        } else {
                            //调用图形初始化函数
                            init();
                            drawChart(chart, datasource);
                        }
                    }
                });
            }

            // 初始化
            function init() {
                // 初始化chart前，更新widget上的配置信息，widgetX 、Y等
                configUpData();

                //如果chart不为空就销毁
                if (chart) {
                    chart.destroy();
                }
                //获取图表类型
                var chartType = isCustomWidget ? scope.child.baseWidget.graphName.toLowerCase() : scope.widget.baseWidget.graphName.toLowerCase();

                // 如果双轴开启,且只使用一个y轴，修正chartType
                if (base.chartSetting && base.chartSetting.showMultiY == 1 && base.chartSetting.metricsToY) {
                    var usedSingleY = true;
                    var yAxisIndex = -1;
                    angular.forEach(base.chartSetting.metricsToY, function (val, key, i) {
                        if (yAxisIndex == -1) {
                            yAxisIndex = val;
                        } else if (usedSingleY && yAxisIndex != val) {
                            usedSingleY = false;
                        }
                    });
                    if (usedSingleY && yAxisIndex > -1) {
                        chartType = base.chartSetting.yAxis[yAxisIndex].chartType;
                    }
                }

                // 设置面积图
                if (isCustomWidget) {
                    if (scope.child.baseWidget.graphName == 'line' && base.chartSetting && base.chartSetting.areaChart == 1) {
                        chartType = 'areaspline';
                    }
                } else {
                    if (scope.widget.baseWidget.graphName == 'line' && base.chartSetting && base.chartSetting.areaChart == 1) {
                        chartType = 'areaspline';
                    }
                }


                //获取全局默认参数
                mergedOptions = getDefaultOptions();

                //根据图形添加图形特有参数
                privateOptionsInit();
                var optionsFunction = optionsMap[chartType];
                optionsFunction(mergedOptions);

                // 根据widet大小判断x、y轴的显示隐藏
                if (base.widgetY > 9) {
                    displaySet('y', 'show', mergedOptions);
                }
                if (base.widgetX > 7) {
                    displaySet('x', 'show', mergedOptions);
                }

                // 根据用户配置设置x坐标轴显示
                if (base.chartSetting && base.chartSetting.xAxis) {
                    angular.forEach(base.chartSetting.xAxis, function (x, index) {
                        // x.enabled 不为 null、undefined
                        if (x && x.enabled != undefined && mergedOptions.xAxis && mergedOptions.xAxis[index]) {
                            if (x.enabled) {
                                displaySet('x', 'show', mergedOptions, index);
                            } else {
                                displaySet('x', 'hide', mergedOptions, index);
                            }
                        }
                    });
                }

                // 根据用户设置堆图(开启双轴时，Y1 和 Y2 中有一个设置堆图即可，Y1设置与stackedChart同步)
                if (isCustomWidget) {
                    if (scope.child && scope.child.variables[0] && scope.child.variables[0].metrics
                        && base.chartSetting && (chartType == "column" || chartType == "bar") &&
                        ( base.chartSetting.stackedChart == 1
                        || (base.chartSetting.showMultiY == 1 && base.chartSetting.yAxis && base.chartSetting.yAxis[1] && base.chartSetting.yAxis[1].stackedChart == 1) )) {
                        mergedOptions.plotOptions.series.stacking = 'normal'; // 堆叠图(''、normal、percent)
                    }
                } else {
                    if (scope.widget && scope.widget.variables[0] && scope.widget.variables[0].metrics
                        && base.chartSetting && (chartType == "column" || chartType == "bar") &&
                        ( base.chartSetting.stackedChart == 1
                        || (base.chartSetting.showMultiY == 1 && base.chartSetting.yAxis && base.chartSetting.yAxis[1] && base.chartSetting.yAxis[1].stackedChart == 1) )) {
                        mergedOptions.plotOptions.series.stacking = 'normal'; // 堆叠图(''、normal、percent)
                    }
                }


                // 根据用户配置设置y坐标轴显示
                if (base.chartSetting && base.chartSetting.yAxis) {
                    angular.forEach(base.chartSetting.yAxis, function (y, index) {
                        // y.enabled 不为 null、undefined
                        if (y && y.enabled != undefined && mergedOptions.yAxis && mergedOptions.yAxis[index]) {
                            if (y.enabled) {
                                displaySet('y', 'show', mergedOptions, index);
                            } else {
                                displaySet('y', 'hide', mergedOptions, index);
                            }
                        }
                    });
                }

                // 判断是否开启双轴，未开启则隐藏Y2轴
                if (!base.chartSetting || base.chartSetting.showMultiY != 1) {
                    displaySet('y', 'hide', mergedOptions, 1);
                }

                //highchart类型处理函数默认为'Chart'，目前我们全用的是'Chart'；
                var getChartType = function (scope) {
                    if (scope.config === undefined) return 'Chart';
                    return chartTypeMap[('' + scope.config.chartType).toLowerCase()] || (scope.config.useHighStocks ? 'StockChart' : 'Chart');
                };

                //设置chart容器
                mergedOptions.chart.renderTo = element[0];
                var chartType = getChartType(scope);

                // 设置国际化信息
                Highcharts.setOptions({
                    lang: localeLang
                });

                //执行chart初始化函数
                chart = new Highcharts[chartType](mergedOptions, undefined);
            }


            // 数据更新
            function configUpData() {
                base.widgetX = scope.widget.sizeX;
                base.widgetY = scope.widget.sizeY;
                base.widgetW = isCustomWidget ? scope.child.layout.width : base.widgetX * scope.rootChart.colWidth - 20; //20为widget边距
                base.widgetH = isCustomWidget ? scope.child.layout.height : base.widgetY * scope.rootChart.rowHeight - 20;
                base.chartSetting = isCustomWidget ? scope.child.chartSetting : scope.widget.chartSetting;

                if (isCustomWidget) {
                    if (scope.child.baseWidget.graphName == 'pie') {
                        base.chartH = base.widgetH - 24;
                    } else {
                        base.chartH = base.widgetH - 2;
                        //base.chartH = scope.child.baseWidget.showMetricAmount == 1 ? base.chartH-56 : base.chartH-32;
                    }
                } else {
                    if (scope.widget.baseWidget.graphName == 'pie') {
                        base.chartH = base.widgetH - 34 - 24 - 32;
                    } else {
                        base.chartH = base.widgetH - 34 - 24 - 17;
                        base.chartH = scope.widget.baseWidget.showMetricAmount == 1 ? base.chartH - 56 : base.chartH - 32;
                    }
                }

                // 判断如果用户没有设置图例不显示，则多指标时默认显示图例
                if (base.chartSetting && base.chartSetting.showLegend == '1') {
                    base.showLegend = true;
                } else if (base.chartSetting && base.chartSetting.showLegend == '0') {
                    base.showLegend = false;
                } else {
                    var data = datasource.getData();
                    var isMultSeries = data && data.series && data.series.length > 1; // 是否多条曲线
                    if (isCustomWidget) {
                        base.showLegend = isMultSeries || scope.child.baseWidget.graphName == 'pie';
                    } else {
                        base.showLegend = isMultSeries || scope.widget.baseWidget.graphName == 'pie';
                    }

                }
                if (isCustomWidget) {
                    if (!scope.child._ext) scope.child._ext = {};
                } else {
                    if (!scope.widget._ext) scope.widget._ext = {};
                }
                isCustomWidget ? scope.child._ext.widgetShowLegend = base.showLegend : scope.widget._ext.widgetShowLegend = base.showLegend; // 更新display中showLegend显示状态

                base.showDataLabels = (base.chartSetting && base.chartSetting.showDataLabels == 1); // 默认不显示

                base.legendW = base.widgetW - 40;
                base.legendMaxH = parseInt((1 + 2 * (base.widgetH - 230) / 230) * (26 / 470 * base.widgetH) / 14) * 14 + 24; //230为widget最小规格高度,26为widget高度470时的最佳显示高度,14为单条图例的高度, +24是图例分页箭头的高度

                if (base.chartSetting && base.chartSetting.yAxis && base.chartSetting.yAxis[0] && base.chartSetting.yAxis[0].enabled) {
                    base.showY1 = true;
                } else {
                    base.showY1 = false;
                }

                if (base.chartSetting && base.chartSetting.xAxis && base.chartSetting.xAxis[0] && base.chartSetting.xAxis[0].enabled) {
                    base.showX = true;
                } else {
                    base.showX = false;
                }

                // 初始化国际化信息
                var lang = getLocalLang().locale;
                var localeConfig = getHighchartsLocalization(lang);
                localeLang = localeConfig['localeLang'];

                // 周起始日
                if (scope.rootUser.settingsInfo.weekStart == "sunday") {
                    startOfWeek = 0;
                } else if (scope.rootUser.settingsInfo.weekStart == "monday") {
                    startOfWeek = 1;
                } else {
                    startOfWeek = localeConfig['startOfWeek'];
                }

                localeTooltipDateTimeLabelFormats = localeConfig['localeTooltipDateTimeLabelFormats'];
                localeXAxisDateTimeLabelFormats = localeConfig['localeXAxisDateTimeLabelFormats'];

            }


            //获取全局默认参数
            function getDefaultOptions() {
                var defaultOpt = {
                    chart: {
                        events: {
                            click: function (event) {
                                onBlurCss();
                            }
                        },
                        //zoomType: 'xy',
                        zoomType: null,
                        //marginTop: 30,
                        marginTop: null,
                        backgroundColor: 'transparent',
                        height: base.chartH
                    },
                    colors: highchartColorList,
                    //colors: ['#C5E1A5', '#EEEEEE', '#A2E0AC', '#B2E2F9', '#F0F4C3', '#F18B85', '#9BAAD3', '#F7B2CC', '#FCD592', '#BC8E84'],
                    // width: 158,
                    credits: {
                        enabled: false
                    },
                    title: {
                        text: null // 不显示图表title
                    },
                    legend: {
                        enabled: base.showLegend,
                        // itemStyle: {
                        //     color: "red",
                        //     "cursor": "pointer",
                        //     "fontSize": "12px",
                        //     "fontWeight": "bold"
                        // },
                        // width: '100%',
                        // width: base.legendW,
                        symbolHide: true, // 修改源码，drawLineMarker方法增加symbolHidde属性（L11342）
                        symbolWidth: 0,
                        // itemWidth: 200,
                        borderWidth: 0,
                        maxHeight: base.legendMaxH,
                        align: "left",
                        x: base.showY1 ? 25 : -12,
                        useHTML: true,
                        labelFormatter: function () {
                            return '<div class="text-over chart-highchart-legend" title="' + this.name + '"><span class="chart-highchart-legend-color" style="background-color: ' + this.color + '"></span><span class="chart-highchart-legend-info">' + this.name + '<span></div>';
                        },
                        itemStyle: {
                            color: '#bdbdbd',
                            textDecoration: null
                        },
                        itemHoverStyle: {
                            color: '#bdbdbd',
                            // textDecoration: null
                        },
                        itemHiddenStyle: {
                            // color: '#ddd',
                            textDecoration: 'line-through'
                        },
                        symbolPadding: 0,
                        navigation: {
                            activeColor: '#bdbdbd',
                            animation: true,
                            arrowSize: 10,
                            inactiveColor: '#bdbdbd',
                            style: {
                                color: '#bdbdbd',
                                fontSize: '12px'
                            }
                        }
                    },
                    xAxis: [{
                        type: 'datetime',
                        startOfWeek: startOfWeek, // 0 = Sunday, 1 = Monday. Defaults to 1
                        dateTimeLabelFormats: localeXAxisDateTimeLabelFormats,
                        labels: {
                            enabled: false, // 不显示x轴刻度标签
                            //autoRotation: [-50],
                            // align: 'right',
                            style: {
                                color: '#bdbdbd',
                                fontSize: '12px'
                            }
                        },
                        title: {
                            text: null // 不显示x轴名称
                        },
                        //  crosshair: {
                        //     width: 1,
                        // },
                        lineColor: '#bdbdbd',
                        lineWidth: 0, // 不显示x轴
                        showFirstLabel: true,
                        showLastLabel: true,
                        // startOnTick: true,
                        // endOnTick: true,
                        gridLineColor: '#bdbdbd',
                        gridLineWidth: 0, // 不显示格线
                        tickColor: '#bdbdbd',
                        tickWidth: 0, // 设置刻度线宽度
                        tickLength: 0, // 设置刻度线长度
                        tickPixelInterval: 60,
                        minPadding: 0,
                        maxPadding: 0,// 线与顶端距离
                        //min: 0,
                        //tickmarkPlacement : 'on',
                    }],
                    yAxis: [{
                        labels: {
                            style: {
                                color: '#bdbdbd',
                                fontSize: '12px'
                            },
                            enabled: true // 不显示y轴刻度标签
                        },
                        title: {
                            text: null // 不显示y轴名称
                        },
                        lineColor: '#bdbdbd',
                        lineWidth: 0, // 不显示y轴的线
                        offset: -8, // y轴label与y轴的距离
                        tickColor: '#bdbdbd',
                        endOnTick: false,
                        minPadding: 0.01,
                        maxPadding: 0.01, // 线与顶端距离
                        gridLineColor: '#bdbdbd',
                        gridLineWidth: 0 // 不显示格线
                    }, {
                        labels: {
                            style: {
                                color: '#bdbdbd',
                                fontSize: '12px'
                            },
                            enabled: false // 不显示y轴刻度标签
                        },
                        title: {
                            text: null // 不显示y轴名称
                        },
                        opposite: true,
                        lineColor: '#bdbdbd',
                        lineWidth: 0, // 不显示y轴的线
                        offset: -8, // y轴label与y轴的距离
                        tickColor: '#bdbdbd',
                        endOnTick: false,
                        minPadding: 0.01,
                        maxPadding: 0.01, // 线与顶端距离
                        gridLineColor: '#bdbdbd',
                        gridLineWidth: 0 // 不显示格线
                    }],
                    tooltip: {
                        crosshairs: null,
                        hideDelay: 0, //默认为500 ms. when mouse out from a point or chart之后,多久去隐藏toolTip,
                        shadow: false,
                        // shared: true,
                        borderWidth: 0,
                        dateTimeLabelFormats: localeTooltipDateTimeLabelFormats,
                        useHTML: true,
                        headerFormat: '<div class="chart-highchart-tooltip">',
                        pointFormatter: function () {
                            return '<div class="chart-highchart-tooltip-data clearfix"><span>' + this.series.name + ':</span> <b>' + datasource.formatDataViewer(this.y, this.unit, this.dataType, this.dataFormat) + '</b></div>';
                        },
                        footerFormat: '<div class="chart-highchart-tooltip-info" style="background-color: {point.color};"><span class="block">{point.key}</span></div></div>',
                        // formatter: function(){ reuurn ''; },
                        style: {
                            padding: 0,
                        }
                    },
                    exporting: {},
                    subtitle: {},
                    series: [],
                    plotOptions: {
                        series: {
                            borderWidth: 0,
                            minPointLength: 2, // The minimal height for a column or width for a bar ：zero base value
                            dataLabels: {
                                enabled: base.showDataLabels,
								formatter: function () {
									var _split = 0;
									var regTextFloat = /^(-)?(\d)*(\.)(\d)*$/;//验证是否是float，必须有“.”才算float
									if (regTextFloat.test(this.y)) {
										_split = 2;
									}
									//格式化dataLabels的显示
									//无效代码，暂时保留：return Highcharts.numberFormat(this.y, _split, '.', ',');
									return datasource.formatDataViewer(this.point.y, this.point.unit, this.point.dataType, this.point.dataFormat);
								}
                            },
                            lineWidth: 2,
                            states: {
                                hover: {
                                    halo: null,
                                    lineWidth: 3
                                }
                            },
                            marker: {
                                symbol: 'circle',
                                lineColor: null, // inherit from series
                                radius: 1,
                                states: {
                                    hover: {
                                        fillColor: '#FFFFFF',
                                        radius: 4,
                                        radiusPlus: 2,
                                        lineWidthPlus: 2,
                                        lineColor: null, // inherit from series
                                        lineWidth: 3
                                    }
                                }
                            },
                        },
                    },
                    navigator: {
                        enabled: false
                    }
                };

                return defaultOpt;
            }


            // chart类型私有变量信息的初始化
            function privateOptionsInit() {
                //默认参数集合添加方法
                //key映射图形名称，Value具体处理函数
                var setOptionsFunctions = function (chartType, action) {
                    optionsMap[chartType] = action;
                };

                var setCommonLineOpt = function (options) {
                    // 特殊处理line、bar、column
                    if (options.chart.type == "line" || options.chart.type == "spline"
                        || options.chart.type == "area" || options.chart.type == "areaspline"
                        || options.chart.type == "bar" || options.chart.type == "column") {
                        //options.chart.margin = lineMargin;
                        options.chart.spacingTop = 0;
                        options.chart.spacingRight = 0;
                        options.chart.spacingBottom = 0;
                        options.chart.spacingLeft = 0;
                    }
                };

                //饼图默认参数处理
                setOptionsFunctions("pie", function (options) {
                    var defaultSize = 130;
                    var defaultInnerSize = 98;
                    var defaultWidth = 170;
                    var defaultHeight = 230;
                    var legendMaxH = base.legendMaxH;
                    // var pieHeight = base.widgetH-34-24-32-14;
                    var pieHeight = base.chartH;
                    if (base.showLegend) {
                        pieHeight = pieHeight - legendMaxH - 50;
                    } else {
                        legendMaxH = 0;
                    }

                    //计算饼图大小
                    var scale = Math.min(base.widgetW / defaultWidth, (base.widgetH - legendMaxH) / defaultHeight);
                    var size = parseInt(defaultSize * scale);
                    var innerSize = parseInt(defaultInnerSize * scale);

                    var center = ['50%', parseInt(pieHeight / 2)];
                    var top = parseInt((pieHeight - innerSize) / 2) + 17;

                    //计算提示数据层
                    var fontScale = size / defaultSize;
                    element.next('.chart-pie-data').css({
                        'font-size': 10 * fontScale,
                        'width': innerSize + 'px',
                        'height': innerSize + 'px',
                        'top': top + 'px'
                    });

                    var topDomSzie = parseInt(innerSize / Math.sqrt(2));
                    var topDomTop = parseInt((pieHeight - topDomSzie) / 2) + 17;
                    element.nextAll('.chart-pie-data-top').css({
                        'font-size': 10 * fontScale,
                        'width': topDomSzie + 'px',
                        'height': topDomSzie + 'px',
                        'top': topDomTop + 'px'
                    });

					if(!scope.pt.settings.isPhone){
						element.next('.chart-pie-data').css({
							'left': parseInt((base.widgetW-innerSize-30)/2)+'px',
							'transform': 'none'
						});
						element.nextAll('.chart-pie-data-top').css({
							'left': parseInt((base.widgetW-topDomSzie-30)/2)+'px',
							'transform': 'none'
						});
					}

                    options.chart.type = "pie";
                    options.colors = pieColorList;
                    setCommonLineOpt(options);

                    options.legend.y = 22;
                    options.legend.align = "center";
                    options.legend.labelFormatter = function () {
                        // var labelText = this.name + '('+parseFloat(this.percentage).toFixed(2)+'%)';//在图例名称后面追加百分比数据
                        var labelText = this.name;
                        return '<div class="text-over chart-highchart-legend" title="' + labelText + '"><span class="chart-highchart-legend-color" style="background-color: ' + this.color + '"></span><span class="chart-highchart-legend-info">' + labelText + '<span></div>';
                    };

                    options.tooltip.pointFormatter = function () {
                        return '<div class="chart-highchart-tooltip-data clearfix"><span>' + this.series.name + ':</span> <b>' + parseFloat(this.percentage).toFixed(2) + '%</b> （' + datasource.formatDataViewer(this.y, this.unit, this.dataType, this.dataFormat) + '）' + '</div>';
                    };

                        options.chart.backgroundColor = 'transparent';

                    options.plotOptions.pie = {
                        depth: 45,
                        allowPointSelect: false,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: false,
							formatter: function () {
								var _split = 0;
								var regTextFloat = /^(-)?(\d)*(\.)(\d)*$/;//验证是否是float，必须有“.”才算float
								if (regTextFloat.test(this.y)) {
									_split = 2;
								}
								//格式化dataLabels的显示
								//无效代码，暂时保留：return Highcharts.numberFormat(this.y, _split, '.', ',');
								return datasource.formatDataViewer(this.point.y, this.point.unit, this.point.dataType, this.point.dataFormat);
							}
                        },
                        showInLegend: true,

                        // allowPointSelect: true,
                        center: center, //饼图位置
                        slicedOffset: 4, //切片展开距离
                        // size: (base.widgetX > base.widgetY ? base.widgetY : base.widgetX) < 10 ? (base.widgetX > base.widgetY ? base.widgetY : base.widgetX) * 15:null, // 无图例时设置size最小130
                        size: size,
                        //minSize: 130,
                        innerSize: innerSize, //控制圆环大小
                        borderColor: '#ffffff',
                        // borderWidth: 1,
                        shadow: false,
                    };

                    //options.plotOptions.series.dataLabels.format = '{y}';

                });
                setOptionsFunctions("column", function (options) {
                    options.chart.type = "column";
                    options.colors = columnColorList;
                    setCommonLineOpt(options);
                    options.plotOptions.column = {
                        pointPadding: 0.2,
                        borderWidth: 0
                    };

                    // options.tooltip = defaultTooltip;
                });
                setOptionsFunctions("bar", function (options) {
                    options.chart.type = "bar";
                    options.colors = columnColorList;
                    setCommonLineOpt(options);
                    options.xAxis[0].labels.useHTML = true;

                    var xMaxWidth = parseInt(base.widgetW * 0.25);
                    options.xAxis[0].labels.formatter = function () {
                        this.value = '<span class="text-over block" style="max-width:' + xMaxWidth + 'px;" title="' + this.value + '">' + this.value + '</span>';
                        return this.value;
                    };

                    options.plotOptions.bar = {
                        dataLabels: {
                            enabled: false
                        }
                    };
                    options.legend.x = base.showX ? 25 : -12;
                });
                setOptionsFunctions("line", function (options) {
                    options.chart.type = "spline";
                    options.colors = lineColorList;
                    setCommonLineOpt(options);
                    options.plotOptions.line = {};
                    // options.tooltip = lineDefaultTooltip;
                });
                setOptionsFunctions("areaspline", function (options) {
                    options.chart.type = "areaspline";
                    options.colors = lineColorList;
                    setCommonLineOpt(options);
                    options.plotOptions.areaspline = {
                        fillOpacity: 0.1, // 透明度 60%
                    };
                });
            }


            /*
             * chart显示选项设置
             * axis: x&y, type: show&hide
             */
            function displaySet(axis, type, options, index) {
                var show = type == 'show' ? true : false;
                var graphName = options.chart.type;
                if (graphName == 'bar' && index != 1) {
                    axis = (axis == 'x' ? 'y' : 'x'); // bar 掉换x、y轴
                }
                if (axis == 'x') {
                    angular.forEach(options.xAxis, function (x, i) {
                        if ((!index && index != 0) || index == i) {
                            options.xAxis[i].labels.enabled = show; // 显示x轴刻度标签
                            options.xAxis[i].lineWidth = show ? 1 : 0;  // 显示x轴
                            options.xAxis[i].gridLineWidth = 0;     // 不显示格线
                            options.xAxis[i].tickWidth = show ? 1 : 0;  // 设置刻度线宽度
                            options.xAxis[i].tickLength = 10;       // 设置刻度线长度
                        }
                    });
                }
                else if (axis == 'y') {
                    angular.forEach(options.yAxis, function (y, i) {
                        if ((!index && index != 0) || index == i) {
                            options.yAxis[i].labels.enabled = show;     // 显示y轴刻度标签
                            options.yAxis[i].lineWidth = 0;             // 不显示y轴的线
                            options.yAxis[i].gridLineWidth = show ? 1 : 0;  // 显示格线
                            //格式化刻度
                            if (show) {
                                // 取出每个轴的第一个指标的单位作为轴单位
                                var _series = datasource.getData().series;
                                if (_series != null && _series.length != 0) {
                                    var _unit = "";
                                    for (var yIndex = 0; yIndex < _series.length; yIndex++) {
                                        if (i == _series[yIndex].yAxis) {
                                            _unit = _series[yIndex].unit;
                                            break;
                                        }
                                    }

                                    if (_unit != null && _unit != "" && _unit.length != 0 && _unit == "%") {
                                        options.yAxis[i].labels.formatter = function () {
                                            return this.value + _unit;
                                        }
                                    } else {
                                        options.yAxis[i].labels.formatter = function () {
                                            return FormatNumber(this.value, _unit, "short");
                                        }
                                    }
                                }
                            }
                        }
                    });
                    // 开启双轴，共用刻线处理
                    if (show && base.chartSetting.showMultiY == '1') {
                        // options.yAxis[1].gridLineWidth = 0;
                        options.yAxis[0].startOnTick = true;
                        options.yAxis[0].endOnTick = true;
                        options.yAxis[1].startOnTick = true;
                        options.yAxis[1].endOnTick = true;

                        // options.yAxis[0].tickAmount = 6;
                        // options.yAxis[1].tickAmount = 6;
                    }
                }
            }


            //绘制
            function drawChart(tmpChart, tmpDataSource) {
                //横轴处理函数
                var categoriesManage = function (chart, data) {
                    if (chart) {
                        if (chart.xAxis && data && data.categories) {
                            chart.xAxis[0].setCategories(data.categories, true);
                        }
                    }
                };

                //series数据处理函数
                var seriesManage = function (chart, data) {
                    //判断chart不为空,也就是 之前是有图表的，在执行 widget的编辑操作
                    if (!chart || !chart.series || !data.series)
                        return;

                    // 时间中按小时显示，且只有一天数据

                    if (data.dateRange && data.dateRange[0] && data.dateRange[0] == data.dateRange[1]
                        && data.datePeriod == 'hour') {
                        chart.options.xAxis[0].dateTimeLabelFormats.day = '%H:%M';
                    }

                    var seriesColorList = []; // 所有已有曲线对应的颜色列

                    // 根据用户双轴配置处理series,设置指标对应y轴、轴图形
                    var seriesDataList = angular.copy(data.series);
                    angular.forEach(seriesDataList, function (s, sIndex) {

                        // // 判断是否开启双轴,设置对应y轴
                        // var yAxisIndex = 0;
                        // if(base.chartSetting && base.chartSetting.showMultiY == 1){
                        //     if (base.chartSetting && base.chartSetting.metricsToY) {
                        //         // 没有设置默认指标显示在Y1轴
                        //         yAxisIndex = base.chartSetting.metricsToY[s.metricsKey] || 0;
                        //     }
                        // }
                        // s.yAxis = yAxisIndex;
                        var yAxisIndex = s.yAxis;

                        // 设置轴图形(开启双轴时起作用)
                        if (base.chartSetting && base.chartSetting.showMultiY == 1
                            && base.chartSetting.yAxis && base.chartSetting.yAxis[yAxisIndex]) {

                            // 面积图设置
                            if (base.chartSetting.yAxis[yAxisIndex].chartType) {
                                if (base.chartSetting.yAxis[yAxisIndex].chartType == 'line'
                                    && base.chartSetting.yAxis[yAxisIndex].areaChart == 1) {
                                    s.type = 'areaspline';
                                } else if (base.chartSetting.yAxis[yAxisIndex].chartType == 'line') {
                                    s.type = 'spline';
                                } else {
                                    s.type = base.chartSetting.yAxis[yAxisIndex].chartType;
                                }
                            }

                            // 堆图设置(针对没有开启堆图的轴进行处理)
                            if (base.chartSetting.yAxis[yAxisIndex].stackedChart != 1) {
                                s.stack = s.id; // 没开启堆图，将堆分组改成唯一
                            }
                        }

                        // 线在柱的上层显示
                        if (!s.type) s.type = chart.options.chart.type;
                        if (s.type == "line" || s.type == 'spline' || s.type == 'area' || s.type == "areaspline") {
                            s.zIndex = 100 + sIndex;

                            // 线上只有一个点，把点增大为 3
                            if (s.data && s.data.length == 1) {
                                s.marker = {radius: 3};
                            }

                        } else if (s.type == "column") {
                            s.zIndex = sIndex;
                        }

                        // if(base.chartSetting &&
                        //     (base.chartSetting.showMultiY != 1
                        //         || (base.chartSetting.yAxis[0] && base.chartSetting.yAxis[1]
                        //                 && base.chartSetting.yAxis[0].chartType==base.chartSetting.yAxis[1].chartType))){

                        // 使用datetime类型的x轴
                        var useDatetimeAxis = data.useDatetimeAxis;
                        if (useDatetimeAxis) {
                            // 对于线图的处理，坐标轴转为datetime格式
                            // if(!s.type) s.type = chart.options.chart.type;
                            // if(s.type=="line" || s.type=='spline' || s.type=='area' || s.type=="areaspline"){
                            chart.xAxis[0].setCategories(null, true); // 清除坐标轴
                            if (s.data && s.data.length > 0) {
                                var newData = [];
                                angular.forEach(s.data, function (d) {
                                    var newD = angular.copy(d);
                                    var date = (newD.name).split('-');
                                    newD.x = Date.UTC(parseInt(date[0]), parseInt(date[1] || 1) - 1, parseInt(date[2] || 1), parseInt(date[3] || 0), parseInt(date[4] || 0), parseInt(date[5] || 0));
                                    newD.name = '';
                                    newData.push(newD);
                                });
                                s.data = newData;
                                s.pointStart = newData[0].x; // 起始时间
                            }
                        }else{
							chart.xAxis[0].options.type = 'linear';
						}
                        // }

                        // 统计所有已有曲线使用的颜色
                        if (s.color) { // 如果有颜色设置，将使颜色添加到 seriesColorList
                            seriesColorList.push(s.color);
                        }

                        // pie图形others颜色的特殊处理
                        if (chart.options.chart.type == "pie") {
                            if (s.data && s.data.length > 0 && s.data[s.data.length - 1].name == 'Others') {
                                s.data[s.data.length - 1].color = pieOthersColor;
                            }
                        }

                    });

                    //如果在现在的chart中有数据series，但是在返回的数据中没有对应的series，则将在chart中的series删除掉
                    //比如 由 市场维度 从ALL 切换到 JP时，EN在之前的chart中是存在的，但是在新返回的数据中不存在，所以需要将其删除掉
                    for (var i = 0; i < chart.series.length; i++) {
                        //根据返回数据删除series
                        var del = true;
                        angular.forEach(seriesDataList, function (seriesdata) {
                            if (chart.series[i].options.id == seriesdata.id) {
                                del = false;
                            }
                        });
                        //如果在返回数据中没有找到就删除
                        if (del) {
                            //删除
                            chart.series[i].remove();

                            // 删除曲线在颜色列表中的颜色值
                            seriesColorList.splice(seriesColorList.indexOf(seriesdata.color), 1);

                            //删除后数组长度-1，当前I也-1
                            i = i - 1;
                        }
                    }

                    //添加数据中多出的series，并且为已有series更新数据
                    angular.forEach(seriesDataList, function (seriesdata, index) {
                        var colorList = chart.options.colors; // 获取highcharts的颜色方案

                        seriesdata.color = chart.options.colors[index]// 每次重绘都重新开始计算颜色（不使用保存的颜色）

                        var add = true;
                        //更新series数据
                        angular.forEach(chart.series, function (series) {
                            if (series.options.id == seriesdata.id) {
                                series.update(seriesdata);
                                add = false;
                            }
                        });
                        //添加series
                        if (add) {
                            if (chart) {
                                // 新增曲线时,如果没有颜色则设置颜色
                                if (!seriesdata.color) {
                                    for (var i = 0; i < colorList.length; i++) {
                                        var color = colorList[i];
                                        if (seriesColorList.indexOf(color) == -1) {
                                            seriesdata.color = color;
                                            seriesColorList.push(color);
                                            break;
                                        }
                                    }
                                }
                                chart.addSeries(seriesdata);
                                // console.log(JSON.stringify(seriesdata));
                            }
                        }
                    });
                };

                //添加目标值横线函数
                var plotLineManage = function (chart, data) {
                    if (!chart || !data)
                        return;

                    //判断数据中是否有目标值
                    // var goals = data.goals;
                    var graphName = chart.options.chart.type;
                    var goals = isCustomWidget ? scope.child.baseWidget.targetValue : scope.widget.baseWidget.targetValue;
                    var maxValue = data.maxValue;
                    var minValue = data.minValue;

                    if (maxValue) {
                        maxValue = parseFloat(maxValue);
                        maxValue = maxValue + maxValue * 0.01;
                    }

                    if (minValue) {
                        minValue = parseFloat(minValue);
                        minValue = minValue - minValue * 0.01;
                    }

                    // 获取用户坐标轴最大、最小值的设置（目前：Y1值根据数据量设置，Y2自适应）
                    if (base.chartSetting && base.chartSetting.yAxis) {
                        angular.forEach(base.chartSetting.yAxis, function (y, index) {
                            if (y && chart.yAxis && chart.yAxis[index]) {

                                // Y1轴最大小值设置
                                if (index == 0) {
                                    // TODO: 开启双轴、堆图的最大小值设为自适应（需优化到后台计算堆图最值）
                                    if (base.chartSetting.showMultiY == '1'
                                        || ( (base.chartSetting.showMultiY == '1' && y.chartType == 'column' && y.stackedChart == '1')
                                        || ((chart.options.chart.type == 'column' || chart.options.chart.type == 'bar' ) && base.chartSetting.stackedChart == '1') )) {

                                        maxValue = null;
                                        minValue = null;
                                    }

                                    maxValue = y.max || maxValue;
                                    minValue = y.min || minValue;
                                }

                                chart.yAxis[index].update({
                                    max: y.max,
                                    min: y.min
                                });
                            }
                        });
                    }

                    if (maxValue) {
                        maxValue = parseFloat(maxValue);
                        //maxValue = maxValue + maxValue * 0.01;
                    }

                    if (minValue) {
                        minValue = parseFloat(minValue);
                        //minValue = minValue - minValue * 0.01;
                    }


                    // 设置目标值,目标值只在Y1轴上
                    // TODO： 暂时去掉图表的目标值
                    if (false && goals) {

                        goals = parseFloat(goals);

                        //删除以前目标值
                        chart.yAxis[0].removePlotLine();
                        //添加新的目标值
                        chart.yAxis[0].addPlotLine({
                            color: plotLineColor, // 线的颜色，定义为红色
                            dashStyle: 'Dash',
                            value: goals, // 定义在那个值上显示标示线，这里是在x轴上刻度为3的值处垂直化一条线
                            width: 1, // 标示线的宽度
                            zIndex: 10,
                            label: {
                                text: goals, // 标签的内容
                                verticalAlign: 'bottom',
                                useHTML: false,
                                style: {
                                    color: plotLineColor,
                                },
                                align: 'left', // 标签的水平位置，水平居左,默认就是为left
                                textAlign: 'left', //默认和 align一样
                                x: graphName == 'bar' ? 2 : 0,
                                y: graphName == 'bar' ? 0 : -3,
                                rotation: 0
                            }
                        });

                        //调整Y轴大小
                        if (goals > maxValue && maxValue) {
                            chart.yAxis[0].update({
                                min: minValue,
                                max: goals + goals * 0.05
                            });
                        } else if (goals < minValue && minValue) {
                            chart.yAxis[0].update({
                                min: goals - goals * 0.05,
                                max: maxValue
                            });
                        } else {
                            chart.yAxis[0].update({
                                min: minValue,
                                max: maxValue
                            });
                        }
                    } else {
                        if (chart.yAxis && chart.yAxis[0]) {
                            //数据中没有目标值，删除目标值
                            chart.yAxis[0].removePlotLine();
                            chart.yAxis[0].update({
                                min: minValue,
                                max: maxValue
                            });
                        }
                    }

                };

                // 记录chart图表中各个series的颜色
                var recordSeriesColorMap = function (tmpChart, tmpDataSource) {
                    if (!tmpChart)
                        return;
                    var seriesColorMap = {}; // {seriesName : seriesColor}
                    angular.forEach(tmpChart.series, function (series) {
                        seriesColorMap[series.name] = series.color;
                    });

                    // TODO: 为chart的series设置颜色
                    var variableColor = angular.toJson(seriesColorMap);
                    tmpDataSource.widget.variables[0].variableColor = variableColor;
                };


                if (tmpChart && tmpDataSource.getData()) {
                    categoriesManage(tmpChart, tmpDataSource.getData());
                    seriesManage(tmpChart, tmpDataSource.getData());
                    plotLineManage(tmpChart, tmpDataSource.getData());
                    tmpChart.redraw();
                    // 记录图表series的颜色
                    recordSeriesColorMap(tmpChart, tmpDataSource);
                    $timeout(function () {
                        if (tmpChart) {
                            try {
                                tmpChart.reflow();
                            } catch (e) {
                            }
                        }
                    }, 5);

                }
            }
        };

        return highchart;
    }
    ]);
