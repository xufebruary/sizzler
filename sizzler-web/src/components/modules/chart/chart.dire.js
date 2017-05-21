import {
	LINK_WIDGET_EDIT,
	uuid,
	FormatNumber,
	objectIsEmpty
} from 'components/modules/common/common';

import chartTpl from 'components/modules/chart/chart.tpl.html';
import chartCustomTpl from 'components/modules/chart/chart-custom.tpl.html';

//图表总指令
angular
.module('pt')
.directive('ptnonechart', ['datasourceFactory', "$rootScope", "$translate", '$timeout', function (datasourceFactory, $rootScope, $translate, $timeout) {
	return {
		restrict: 'EA',
		//调用各个图表指令
		template: chartTpl,
		link: function (scope, element, attrs) {

			scope.formatNumber = FormatNumber;

			//highchart判断
			scope.isHighchart = function () {
				for (var i = 0; i < $rootScope.chartMaps.length; i++) {
					if (scope.widget.baseWidget.graphName == $rootScope.chartMaps[i].chartType && $rootScope.chartMaps[i].creator == "highcharts") {
						return true;
					}
				}
				return false;
			};

			//时间范围国家化
			scope.localDate = function (widget) {
				var dateKey = widget.baseWidget.dateKey;
				var dsId = widget.variables[0].ptoneDsInfoId;

				//当开启全局时间后,widget时间采用全局时间显示;

				if (scope.rootPanel.now && angular.isDefined(scope.rootPanel.now.components) && !objectIsEmpty(scope.rootPanel.now.components) && scope.rootPanel.now.components['GLOBAL_TIME'].status == '1' && scope.rootPanel.now.components['GLOBAL_TIME'].value != 'widgetTime') {
					if (angular.isDefined(scope.sharePanelFlag)) {
						//分享页面

						dateKey = scope.rootPanel.now.components['GLOBAL_TIME'].value;
					} else {
						//非分享页面(在只读模式下,且在非模版widget应用全局时间)

						if (scope.rootPage.dashboardMode == 'READ' && widget.baseWidget.isExample != 1 && widget.baseWidget.isDemo != 1) {
							dateKey = scope.rootPanel.now.components['GLOBAL_TIME'].value;
						}
					}
				}

				var localInfo;
				if (!dateKey && dsId) {
					if ([1, 3, 5, 7, 8, 9, 10].indexOf(+dsId) >= 0) {
						//除gd外默认选中过去7天不包含今天
						dateKey = 'past7day';
					} else if (dsId == 6) {//gd默认选中all time
						dateKey = 'all_time';
					}
				}

				if (dateKey.indexOf('|') > -1) {
					var sdt = dateKey.split('|')[0];
					var edt = dateKey.split('|')[1];

					if (edt == 'today') {
						localInfo = $translate.instant('WIDGET.EDITOR.TIME.FROM') + new Date(sdt).format('MM/dd') + ' ' + $translate.instant('WIDGET.EDITOR.TIME.TO_TODAY')
					} else {
						localInfo = new Date(sdt).format('MM/dd') + '-' + new Date(edt).format('MM/dd');
					}
				} else if (dateKey.indexOf('last') == 0 && dateKey.indexOf('last_') < 0 || dateKey.indexOf('past') == 0) {
					var modelTodayCode = dateKey.indexOf('last') == 0 ? 'INCLOUD_TODAY' : 'EXCLOUD_TODAY';

					localInfo = $translate.instant('WIDGET.EDITOR.TIME.LAST') + dateKey.match(/\d+/g) + $translate.instant('WIDGET.EDITOR.TIME.DAYS') + '（' + $translate.instant('WIDGET.EDITOR.TIME.' + modelTodayCode) + '）';
				} else {
					localInfo = $translate.instant('WIDGET.EDITOR.TIME.' + angular.uppercase(dateKey));
				}

				return localInfo;
			};

			//chart-bd 的class
			scope.returnChartBDClass = function(widget){
				var bdClass = '';
				if(widget.baseWidget.graphName=='number'){
					bdClass += 'chart-num';
				}
				//专为新意互动做的特殊对应，隐藏掉表格等图表，只显示数字
				if((widget.baseWidget.graphName != 'number' &&
					widget.baseWidget.graphName != 'progressbar' &&
					widget.baseWidget.graphName != 'pie')
					&& widget.chartSetting.hideDetail == 1){
					bdClass += ' invisible';
				}
				if(widget.baseWidget.graphName=='progressbar' ||
					widget.baseWidget.graphName=='number' ||
					widget.baseWidget.showMetricAmount != 1 ||
					widget.baseWidget.graphName=='pie'){
					bdClass += ' top0';
				}else if(widget.baseWidget.graphName=='map'){//map类型在小屏幕下，后退按钮只显示一半，因此将top值缩小10px
					bdClass += ' top32';
				}else{
					bdClass += ' top42';
				}
				return bdClass;
			}

			//接收子级传递的数据
			scope.$on('to-parent', function (event, data) {
				if (data && data.length == 2) {
					scope.data = scope.data || {};
					scope.data[data[0]] = data[1];
				}
			});

		}
	}
}]);


/**
 * 自定义widget
 */

angular
.module('pt')
.directive('customWidget', ['$rootScope', 'dataMutualSrv', 'siteEventAnalyticsSrv', customWidget]);

function customWidget($rootScope, dataMutualSrv, siteEventAnalyticsSrv) {
	return {
		restrict: 'EA',
		replace: true,
		template: chartCustomTpl,
		link: link
	};
	function link(scope, elem, attrs) {
		var widgetNow = $(elem).parents('.widget');
		var widgetNowWidth = widgetNow.width(), widgetNowHeight = widgetNow.height();

		scope.customSetting = {
			index: attrs.index,
			toolDefaultText: true,
			customWidgetFocus: false //小widget是否获得焦点（是否选中，默认没有选中）
		};


		/**
		 * 小widget渲染
		 */
		function renderCustomWidget(saveType, change) {
			var widgetNowWidth, widgetNowHeight;
			var elem0 = elem[0];
			if (scope.pt.settings.isPhone) {//移动端需要减去18（9*2）
				var ratioOfWidgetWH = (scope.widgetPhoneSetting.sizeX / scope.widgetPhoneSetting.sizeY).toFixed(2);
				widgetNowWidth = change ? document.body.offsetWidth - 18 : scope.rootChart.colWidth - 18;
				widgetNowHeight = Math.floor(change ? (document.body.offsetWidth - 18) / ratioOfWidgetWH : scope.rootChart.colWidth / ratioOfWidgetWH);
			} else {
				widgetNowWidth = scope.rootChart.colWidth * scope.widget.sizeX - scope.rootChart.margins[0];
				widgetNowHeight = scope.rootChart.rowHeight * scope.widget.sizeY - scope.rootChart.margins[0];
			}
			elem0.style.zIndex = scope.child.layout['z-index'] ? scope.child.layout['z-index'] : 'auto';
			//先判断widget的大小是否有变化，有可能大小屏幕切换过导致宽高变化

			if (widgetNowWidth === scope.child.layout.widgetNowWidth) {
				elem0.style.width = scope.child.layout.width + 'px';
				elem0.style.left = scope.child.layout.left + 'px';
			} else {
				var zoomInWidth = ((widgetNowWidth - scope.child.layout.widgetNowWidth) / scope.child.layout.widgetNowWidth).toFixed(2),
					newWidth = scope.child.layout.width + Math.floor(scope.child.layout.width * zoomInWidth),
					zoomInLeft = scope.child.layout.left / scope.child.layout.widgetNowWidth,
					newLeft = Math.floor(widgetNowWidth * zoomInLeft);
				if (saveType == 'saveSmall') {//widget编辑器打开的状态下，只需要更新父级widget的大小信息
					scope.child.layout.widgetNowWidth = widgetNowWidth;
				} else {
					elem0.style.width = newWidth + 'px';
					elem0.style.left = newLeft + 'px';
					//因为highchart中的图是根据layout里面的width和height来计算的，所以需要更新layout字段
					scope.child.layout.width = newWidth;
					scope.child.layout.left = newLeft;
					scope.child.layout.widgetNowWidth = widgetNowWidth;
				}
			}
			if (widgetNowHeight === scope.child.layout.widgetNowHeight) {
				elem0.style.height = scope.child.layout.height + 'px';
				elem0.style.top = scope.child.layout.top + 'px';
			} else {
				var zoomInHeight = ((widgetNowHeight - scope.child.layout.widgetNowHeight) / scope.child.layout.widgetNowHeight).toFixed(2),
					newHeight = scope.child.layout.height + Math.floor(scope.child.layout.height * zoomInHeight),
					zoomInTop = scope.child.layout.top / scope.child.layout.widgetNowHeight,
					newTop = Math.floor(widgetNowHeight * zoomInTop);
				if (saveType == 'saveSmall') {
					scope.child.layout.widgetNowHeight = widgetNowHeight;
				} else {
					elem0.style.height = newHeight + 'px';
					elem0.style.top = newTop + 'px';
					//因为highchart中的图是根据layout里面的width和height来计算的，所以需要更新layout字段
					scope.child.layout.height = newHeight;
					scope.child.layout.top = newTop;
					scope.child.layout.widgetNowHeight = widgetNowHeight;
				}
			}
		}

		renderCustomWidget();

		/**
		 * 当父级widget大小变化时，会导致内部的小widget大小也变化
		 */
		scope.$on('changeWidgetLayout', function (e, value) {
			var widgetId = value.split(',')[0], saveType = value.split(',')[1];
			if (saveType === 'saveSmall') {
				/**
				 * 当widget编辑器打开的状态下，需要判断，变换大小的widget和编辑器是否匹配
				 */
				if (scope.modal.editorNow.children && scope.modal.editorNow.children.length > 0 && scope.modal.editorNow.children[0].baseWidget.parentId == widgetId) {
					renderCustomWidget('saveSmall');
				} else if ((!scope.modal.editorNow.children || scope.modal.editorNow.children.length <= 0) && scope.modal.editorNow.baseWidget.parentId == widgetId) {
					renderCustomWidget('saveSmall');
				} else {
					renderCustomWidget('saveBig');
				}
			} else if (saveType === 'saveBig') {
				renderCustomWidget('saveBig');
			}
			//移动端横竖屏切换
			if (value === 'orientationchange') {
				renderCustomWidget(null, 'change');
			}
		});

		//大widget获得焦点时
		widgetNow.on('mousedown', function (e) {
			if (!scope.rootUser.sysRoles.createCustomWidget || !widgetNow.hasClass('is-self-editor-open')) {
				return;
			}
			if (e.target == widgetNow[0] && scope.modal.editorShow) {
				widgetNow.find('.custom-widget').removeClass('edit-active');
				scope.modal.editorNow = scope.modal.editorNowCopy;//当所有小widget都失去焦点时，编辑器需要重新初始化
				scope.$emit('changeCustomWidget');//向dashboard发送编辑小widget的事件
			}
		});
		//小widget获得焦点时
		scope.getEdit = function () {
			if (!scope.rootUser.sysRoles.createCustomWidget || !widgetNow.hasClass('is-self-editor-open')) {
				return;
			}
			scope.customSetting.customWidgetFocus = true;
			if (!scope.modal.editorShow) {//如果编辑器没有打开，不能编辑自定义widget
				return;
			}
			widgetNow.find('.custom-widget').removeClass('edit-active');
			$(elem).addClass('edit-active').css('z-index', 999);
			if (scope.modal.editorNow.baseWidget.widgetId !== scope.child.baseWidget.widgetId) {
				scope.modal.editorNow = scope.child;
				scope.modal.editorOpen = true;//需要更新指标维度信息
				scope.$emit('changeCustomWidget');//向dashboard发送编辑小widget的事件
			}
		};


		//小widget失去焦点时
		scope.removeEdit = function () {
			if (!scope.rootUser.sysRoles.createCustomWidget || !widgetNow.hasClass('is-self-editor-open')) {
				return;
			}
			if (!scope.customSetting.customWidgetFocus || !scope.modal.editorShow) {//如果没有选中的自定义widget，后面是不保存的，如果编辑器没有打开，不能编辑自定义widget
				return;
			}
			scope.customSetting.customWidgetFocus = false;
			var $this = $(elem);
			var widgetNowWidth = widgetNow.width(), widgetNowHeight = widgetNow.height();
			scope.child.layout = {
				'width': $this.width(),
				'height': $this.height(),
				'left': $this.position().left,
				'top': $this.position().top,
				'widgetNowWidth': widgetNowWidth,
				'widgetNowHeight': widgetNowHeight,
				'z-index': scope.child.layout['z-index'] ? scope.child.layout['z-index'] : 'auto'
			};
			$this[0].style.zIndex = scope.child.layout['z-index'] ? scope.child.layout['z-index'] : 'auto';
			//$this.css('z-index', scope.customSetting['z-index'] ? scope.customSetting['z-index'] : 'auto');
			scope.saveData();
		};

		//保存单个数据
		scope.saveData = function (type) {
			scope.loadSetting.widget = true;
			dataMutualSrv.post(LINK_WIDGET_EDIT, angular.copy(scope.child), 'wgtSave').then(function (data) {
				if (data.status == 'success') {

					//全站事件统计
					siteEventAnalyticsSrv.createData({
						uid: $rootScope.userInfo.ptId,
						time: new Date().getTime(),
						position: 'widget',
						operate: 'widget-edit-all',
						operateId: uuid(),
						content: JSON.stringify(scope.modal.editorNow)
					});
				} else if (data.status == 'failed') {
					console.log('Post Data Failed!')
				} else if (data.status == 'error') {
					console.log('Post Data Error: ')
					console.log(data.message)
				}
				scope.loadSetting.widget = false;
			})
		};

		//富文本框
		scope.initialize = function (index) {
			scope.customSetting.toolDefaultText = false;
			tinymce.remove();
			tinymce.init({
				selector: '.custom-tinymce-' + index,
				menubar: false,
				inline: true,
				toolbar1: 'formatselect bold italic underline strikethrough alignleft aligncenter alignright alignjustify',
				toolbar2: 'forecolor backcolor | bullist numlist | link image media code',
				plugins: 'code textcolor link image media',
				default_link_target: "_blank",
				forced_root_block: '',
				extended_valid_elements: 'script[type|src],iframe[src|style|width|height|scrolling|marginwidth|marginheight|frameborder]',
				init_instance_callback: function () {
					$(elem).find('.custom-tinymce-' + index).focus();
				},
				setup: function (editor) {
					editor.on('blur', function () {
						var value = editor.getContent();
						if (value) {
							scope.child.toolData.value = value;
							scope.saveText(encodeURIComponent(value));
						} else {
							scope.customSetting.toolDefaultText = true;
						}
						tinymce.remove();
					});
				}
			});
		};

		//保存
		scope.saveText = function (value) {
			var child = angular.copy(scope.child);
			if (!angular.isString(child.toolData.extend)) {
				child.toolData.extend = angular.toJson(child.toolData.extend);
			}
			child.toolData.value = value;
			delete child.col;
			delete child.row;
			delete child.sizeX;
			delete child.sizeY;
			delete child.minSizeX;
			delete child.minSizeY;
			delete child.widgetDrawing;
			delete child._ext;
			delete child.baseWidget.widgetEdit;//删除富文本框的编辑状态

			dataMutualSrv.post(LINK_WIDGET_EDIT, child).then(function (data) {
				console.log('updata success!')
			});
		};

	}
}


/**
 * 自定义widget的大小缩放
 */
angular
.module('pt')
.directive('ptResize', ['$compile', ptResize]);

function ptResize($compile) {
	return {
		restrict: 'AE',
		link: link
	};

	function link(scope, element, attrs) {
		var template = '<div class="resizeHandle" data-direction="up"></div>'
			+ '<div class="resizeHandle" data-direction="down"></div>'
			+ '<div class="resizeHandle" data-direction="left"></div>'
			+ '<div class="resizeHandle" data-direction="right"></div>'
			+ '<div class="resizeHandle" data-direction="up-left"></div>'
			+ '<div class="resizeHandle" data-direction="up-right"></div>'
			+ '<div class="resizeHandle" data-direction="down-left"></div>'
			+ '<div class="resizeHandle" data-direction="down-right"></div>';

		var elem = $(element);
		$(element).append($compile(template)(scope));
		var startX, startY, startWidth, startHeight, direction, maxWidth, maxHeight;


		elem.on('mousedown', '[data-direction]', resizeInit);

		function resizeInit(e) {
			direction = $(this).attr('data-direction');
			startX = e.clientX;
			startY = e.clientY;
			startWidth = parseInt(getComputedStyle(elem[0]).width, 10);
			startHeight = parseInt(getComputedStyle(elem[0]).height, 10);
			var rect = elem.parents('.li-widget')[0].getBoundingClientRect();
			maxWidth = rect.right;
			maxHeight = rect.bottom;
			document.documentElement.addEventListener('mousemove', doDrag, false);
			document.documentElement.addEventListener('mouseup', stopDrag, false);
		}

		function doDrag(e) {
			if (e.clientX > maxWidth || e.clientY > maxHeight) {
				return false;
			}
			if (direction == 'right') {
				elem[0].style.width = (startWidth + e.clientX - startX) + 'px';
			} else {
				elem[0].style.width = (startWidth + e.clientX - startX) + 'px';
				elem[0].style.height = (startHeight + e.clientY - startY) + 'px';
			}
		}

		function stopDrag(e) {
			direction = undefined;
			document.documentElement.removeEventListener('mousemove', doDrag, false);
			document.documentElement.removeEventListener('mouseup', stopDrag, false);
		}

	}
}
