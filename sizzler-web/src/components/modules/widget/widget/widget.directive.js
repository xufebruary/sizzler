'use strict';

/**
 * 面板
 *
 */
import widgetListTpl from './widget.html';
import {
	LINK_WIDGET_EDIT,
	LINK_BASE_WIDGET_EDIT,
	LINK_EXCEL_FILE_UPDATE_SOURCE_DATA,
	getMyDsConfig
} from 'components/modules/common/common';

widgetDirective.$inject = ['$state', '$timeout', '$translate', 'dataMutualSrv', 'sysRoles', 'FileUploader', 'WidgetResources', 'chartService'];

function widgetDirective($state, $timeout, $translate, dataMutualSrv, sysRoles, FileUploader ,WidgetResources, chartService) {
    return {
        restrict: 'EA',
        transclude: false,
        replace: false,
        template: widgetListTpl,
        link: link
    };

    function link(scope, element, attrs) {

        var myOptions = scope.myOptions = {
            widgetTitle: [],   //widget title

            widget: {
                add: false,
                delete: false,
                copy: false,
                edit: false,
            }
        };

        // =========

        //更新heatmap widget标题
        scope.updateWidgetTitle = updateWidgetTitle;

        //widget左下角提示信息
        scope.chartTips = chartService.getTips;

        // =========

        //创建uploader
        scope.uploader = new FileUploader();

        /***********************
         *工具类widget-text
         ***********************/
        var text = scope.text = [];
        //widget 名称修改
        scope.editTitle = function (index) {
            if (scope.text[index].selected) {
                scope.text[index].editing = true;
            }
        };//editTitle
        scope.doneEditing = function (index) {
            scope.text[index].editing = false;

            scope.rootWidget.list[index].toolData.value = scope.text[index].name;
            scope.saveText(index);
        };//doneEditing
        scope.updateWidgetTitleByKeydown = function (e, index) {
            var keycode = window.event ? e.keyCode : e.which;
            if (keycode == 13) {
                scope.doneEditing(index);
            }
        };

        scope.goToDs = function () {
            $state.go('pt.dataSources.facebookad');
        }

        //保存
        scope.saveText = function (index, value) {
            var widget = angular.copy(scope.rootWidget.list[index]);
            if (!angular.isString(widget.toolData.extend)) {
                widget.toolData.extend = angular.toJson(widget.toolData.extend);
            }
            widget.toolData.value = scope.rootWidget.list[index].toolData.value = value;

            if (sysRoles.hasSysRole("ptone-admin-user")) {//管理员需要将标题转json
                widget.baseWidget.widgetTitle = angular.toJson(widget.baseWidget.widgetTitle);
            }

            delete widget.col;
            delete widget.row;
            delete widget.sizeX;
            delete widget.sizeY;
            delete widget.minSizeX;
            delete widget.minSizeY;
            delete widget.widgetDrawing;
            delete widget._ext;
            delete widget.baseWidget.widgetEdit;//删除富文本框的编辑状态

            dataMutualSrv.post(LINK_WIDGET_EDIT, widget).then(function (data) {
                console.log('updata success!')
            })
        };


		//时间维度判断
		scope.dimensionsCheck = function (widget) {
			var dsId = widget.variables[0].ptoneDsInfoId;

			//在应用了全局时间后,除了GA,AD,PT,FB之外的数据源都需校验是否包含时间维度
			if ([1, 3, 12, 13, 18, 19, 21, 23, 25, 27, 28, 29, 30, 31].indexOf(+dsId) < 0 && !widget.variables[0].dateDimensionId && scope.modal.dashboardTime.dateKey != 'widgetTime') {
				return true;
			} else {
				return false;
			}
		};


        /**
         * widget title
         */
		scope.widgetTitleFun = {

			//edit widget title
			editTitle: function (index) {

				//管理员 || 查看模式 不能双击修改title
				//if ($rootScope.userInfo.access != 1 && scope.rootPage.dashboardMode != 'READ') {
				if (!sysRoles.hasSysRole("ptone-admin-user") && scope.rootPage.dashboardMode != 'READ') {
					scope.myOptions.widgetTitle[index].editing = true;
					scope.myOptions.widgetTitle[index].name = scope.rootWidget.list[index].baseWidget.widgetTitle;
				}
			},
			//编辑完成
			doneEditing: function (index) {
				if (scope.myOptions.widgetTitle[index].name == '') {
					scope.myOptions.widgetTitle[index].name = scope.rootWidget.list[index].baseWidget.widgetTitle;
					scope.myOptions.widgetTitle[index].editing = false;
				} else if (scope.myOptions.widgetTitle[index].name != scope.rootWidget.list[index].baseWidget.widgetTitle && myOptions.widgetTitle[index].name != 'Widget Title') {
					scope.widgetTitleFun.updateTitle(index);
				} else {
					scope.myOptions.widgetTitle[index].editing = false;
				}
			},
			updateWidgetTitleByKeydown: function (e, index) {
				var keycode = window.event ? e.keyCode : e.which;
				if (keycode == 13) {
					scope.widgetTitleFun.doneEditing(index);
				}
			},
			updateTitle: function (index) {
				var sendData = {
					widgetId: scope.rootWidget.list[index].baseWidget.widgetId,
					widgetTitle: scope.myOptions.widgetTitle[index].name,
					isTitleUpdate: 1
				};

				dataMutualSrv.post(LINK_BASE_WIDGET_EDIT, sendData).then(function (data) {
					if (data.status == 'success') {
						//修改标题标识
						scope.rootWidget.list[index].baseWidget.widgetTitle = myOptions.widgetTitle[index].name;
						scope.rootWidget.list[index].baseWidget.isTitleUpdate = 1;

						scope.myOptions.widgetTitle[index].editing = false;
					} else if (data.status == 'failed') {
						console.log('Post Data Failed!')
					} else if (data.status == 'error') {
						console.log('Post Data Error: ')
						console.log(data.message)
					}
				});
			}
		};


        /**
         * widgetRepeatFinish
         */
        scope.widgetRepeatFinish = function () {
            $timeout(function(){
                //widget添加或复制后定位
                if (scope.rootWidget.locateId !== null) {
                    var widget = $('.widget[data-widget-id="'+scope.rootWidget.locateId+'"]')
                    if (widget.length > 0) {
                        var y = widget.offset().top;
                        $(document).scrollTop(y - 60 - 50);
                        widget.addClass("panel-border");
                    }

                    scope.rootWidget.locateId = null;
                }


                //统一绘图
                scope.rootWidget.drawChart = true;
                scope.modal.widgetRepeatFinish = true;
                scope.hideLoading('widgetList');
            }, 0);
        };

		/**
		 * widget为空的逻辑
		 * @returns {boolean}
		 */
		scope.isWidgetEmpty = function(widget,dsConfig){
			if((widget.baseWidget.widgetType == 'custom' && widget.children.length == 0) || (widget.baseWidget.graphName == 'heatmap' && widget.toolData.value == '' && scope.rootPage.dashboardMode == 'EDIT')){
				return true;
			}
			if(widget.baseWidget.graphName != 'heatmap' && widget.baseWidget.widgetType !== 'custom' && scope.rootWidget.drawChart && scope.rootPage.dashboardMode == 'EDIT'){
				if(dsConfig.editor.data.drewChartComplexMode){//单维度绘图，salesforce数据源是复合模式，其他数据源都是正常模式
					var profileId = widget.variables[0].profileId;
					var isSummaryOrMatrix =(''+profileId).indexOf('Summary') > -1 || (''+profileId).indexOf('Matrix') > -1;
					var isTabular = (''+profileId).indexOf('Tabular') > -1;
					var isObject = (''+profileId).indexOf('|join|') === -1;
					if(isSummaryOrMatrix){//不支持单维度绘图
						return widget.variables[0].metrics.length <= 0;
					}else if(isTabular || isObject){//支持单维度绘图
						if(!dsConfig.editor.data.drewChartNeedMetrics){
							if(widget.variables[0].metrics.length <= 0 && widget.variables[0].dimensions.length <= 0){
								return true;
							}
							return widget.variables[0].metrics.length <= 0 && widget.variables[0].dimensions.length > 0 && widget.baseWidget.graphName != 'table';
						}else{
							return false;
						}
					}
				}else{
					if(widget.variables[0].metrics.length <= 0 && dsConfig.editor.data.drewChartNeedMetrics){
						return true;
					}else{
						if(!dsConfig.editor.data.drewChartNeedMetrics){
							if(widget.variables[0].metrics.length <= 0 && widget.variables[0].dimensions.length <= 0){
								return true;
							}
							return widget.variables[0].metrics.length <= 0 && widget.variables[0].dimensions.length > 0 && widget.baseWidget.graphName != 'table';
						}else{
							return false;
						}
					}
				}

			}else{
				return false;
			}
		};


		/**
		 * widget为空,中间显示‘笔’或者‘文字提示’的逻辑
		 * @returns {boolean}
		 */
		scope.isShowSingleDimensions = function(widget,dsConfig){
			if(dsConfig.editor.data.drewChartComplexMode){//目前只有salesforce数据源是混合模式
				var profileId = widget.variables[0].profileId;
				var isSummaryOrMatrix =(''+ profileId).indexOf('Summary') > -1 || (''+ profileId).indexOf('Matrix') > -1;
				var isTabular = (''+ profileId).indexOf('Tabular') > -1;
				var isObject = (''+ profileId).indexOf('|join|') === -1;
				if(isSummaryOrMatrix){//不支持单维度绘图
					return false;
				}else if(isTabular || isObject){//支持单维度绘图
					return widget.variables[0].metrics.length <= 0 && widget.variables[0].dimensions.length > 0 && widget.baseWidget.graphName != 'table';
				}
			}
			return widget.variables[0].metrics.length <= 0 && widget.variables[0].dimensions.length > 0 && widget.baseWidget.graphName != 'table' && !dsConfig.editor.data.drewChartNeedMetrics;
		};



        // =========

        //更新热图widget标题
        function updateWidgetTitle(sendData, chartType){
            var title = sendData.widgetTitle;
            if(title == ''){
                if(chartType == 'heatmap'){
                    title = $translate.instant("WIDGET.WIDGET_HEATMAP_DEFAULT_NAME");
                }
                else {
                    title = $translate.instant("WIDGET.WIDGET_DEFAULT_NAME");
                }
            }

            WidgetResources.updateWidgetBase(sendData)
            .then((data) => {
                //更新前端数据
                for (var i = scope.rootWidget.list.length - 1; i >= 0; i--) {
                    if(scope.rootWidget.list[i].baseWidget.widgetId == sendData.widgetId){
                        scope.rootWidget.list[i].baseWidget.widgetTitle = title;
                        scope.rootWidget.list[i].baseWidget.isTitleUpdate = 1;
                        break;
                    }
                }
            })
        }
    }
}

export default widgetDirective;




/**
 * widget单独指令，包括底部获取错误信息，以及提示信息
 */
angular
    .module('pt')
    .directive('widgetDirection', ['$timeout','$sce', function ($timeout,$sce) {
        return {
            restrict: 'EA',
            link: function (scope, element, attrs) {

                scope.widgetDirectionSettings = {
                    isSelfEditorOpen: false //属于自己的widget编辑器是否打开
                };

                /**
                 * widget的数据源设置
                 */
                scope.dsConfig = getMyDsConfig(attrs.dsCode);
                scope.$watch('widget.variables[0].dsCode', function (ne, ol) {
                    scope.dsConfig = getMyDsConfig(ne);
                });


                //接收子级传递的数据
                scope.$on('to-parent', function (event, data) {
                    if (data && data.length == 2) {
                        scope.data = scope.data || {};
                        scope.data[data[0]] = data[1];
                    }
                });
                scope.elementHeight = {
                    widgetHd: null,
                    chartDateRange: null,
                    chartValue: null
                };

                //专为移动端适配设置的字段，用来计算widget大小
                scope.widgetPhoneSetting = {
                    Height: null,
                    sizeX: null,
                    sizeY: null
                };

                //移动端自定义widget的高度需要固定值，根据保存的sizeX和sizeY的比率计算
                scope.setWidgetHeightOfPhone = function(change){
                    if(scope.pt.settings.isPhone){
                        if(scope.widget.baseWidget.widgetType === 'custom'){
                            var ratioOfWidgetWH = scope.widgetPhoneSetting.sizeX ? scope.widgetPhoneSetting.sizeX/scope.widgetPhoneSetting.sizeY : scope.widget.sizeX/scope.widget.sizeY;
                            if(change){
                                scope.widgetPhoneSetting.Height = parseInt((document.body.offsetWidth - 18) / ratioOfWidgetWH);
                            }else{
                                scope.widgetPhoneSetting.Height = parseInt(scope.rootChart.colWidth / ratioOfWidgetWH);
                            }
                            scope.widgetPhoneSetting.sizeX = scope.widgetPhoneSetting.sizeX ? scope.widgetPhoneSetting.sizeX :scope.widget.sizeX;
                            scope.widgetPhoneSetting.sizeY = scope.widgetPhoneSetting.sizeY ? scope.widgetPhoneSetting.sizeY :scope.widget.sizeY;
                            $timeout(function(){
                                element.css('height',scope.widgetPhoneSetting.Height);
                            });
                        }
                    }
                };
                scope.setWidgetHeightOfPhone();

                scope.$on('changeWidgetWHOfPhone',function(e,value){
                    //移动端横竖屏切换
                    if(value === 'orientationchange'){
                        scope.setWidgetHeightOfPhone('change');
                    }
                });

                scope.watchWidgetClass = function(){
                    var widgetClass = '';
                    if(scope.widget.baseWidget.widgetType === 'custom'){
                        widgetClass += ' widget-custom';
                    }
                    //根据权限判断用户是否可以编辑自定义widget
                    if(scope.rootUser.sysRoles.createCustomWidget && scope.widget.baseWidget.widgetType === 'custom'){
                        widgetClass += ' can-edit-custom';
                    }
                    //根据widget编辑器状态和当前编辑器所代表的widget来判断是否可编辑自定义widget
                    if(scope.modal && scope.modal.editorShow){
                        if((scope.modal.editorNow && scope.modal.editorNow.baseWidget.widgetId && scope.modal.editorNow.baseWidget.widgetId === scope.widget.baseWidget.widgetId) || (scope.widget.children && scope.modal.editorNow && scope.modal.editorNow.baseWidget.parentId === scope.widget.baseWidget.widgetId)){
                            widgetClass += ' is-self-editor-open';
                        }
                    }
                    return widgetClass;

                };
                scope.bindHtml = function(text){
                    return $sce.trustAsHtml(text);
                }

            }
        }
    }]);
