'use strict';

import widgetInputTpl from 'components/modules/widget/widget-input/widget-input.tpl.html';

import {
	LINK_WIDGET_ADD,
	LINK_WIDGET_EDIT
} from '../../common/common';

/**
 * widgetIpt
 * widget input类型
 *
 */

angular
.module('pt')
.directive('widgetIpt', ['$rootScope', 'dataMutualSrv', windgetIpt]);


function windgetIpt($rootScope, dataMutualSrv) {
	return {
		restrict: 'EA',
		template: widgetInputTpl,
		link: link
	};

	function link(scope, element, attrs) {

		//回车事件绑定
		scope.panelCopyKeyup = function (e, val) {
			var keycode = window.event ? e.keyCode : e.which;
			if (keycode == 13 && val != null && typeof(val) != 'undefined') {
				scope.save(val)
			}
		};

		//关闭
		scope.close = function () {
			scope.modal.widgetIptShow = false;
		};


		var widgetId = uuid();
		var sendWidget = scope.sendWidget = {
			"panelId": scope.rootPanel.nowId,
			"baseWidget": {
				"widgetId": widgetId,
				"spaceId": scope.rootSpace.current.spaceId,
				"creatorId": $rootScope.userInfo.ptId,
				"ownerId": $rootScope.userInfo.ptId,
				"modifierId": $rootScope.userInfo.ptId,
				"createTime": parseInt(new Date().getTime()),
				"modifyTime": parseInt(new Date().getTime()),
				"isTemplate": 0,
				"isExample": 0,

				"widgetType": "tool",
				"ptoneGraphInfoId": 10100,
				"graphName": 'text',
			},
			"toolData": {
				'value': null,
				'extend': {
					'italics': false,   //斜体
					'bold': false,      //粗体
					'bg': 'white',    //背景色
				}
			},
			"_ext": {} // 扩展字段，用于前端临时数据存储，不持久化到库中
		};
		var widget = {
			sendDataUrl: LINK_WIDGET_ADD,
			widgetIndex: null,          //编辑操作时的数据下标

			//编辑操作时-保存位置信息
			layoutCol: null,
			layoutRow: null,
			layoutSizeX: null,
			layoutSizeY: null,
			layoutMinSizeX: null,
			layoutMinSizeY: null,
		};


		//选择背景色
		scope.sltBg = function (style) {
			// if(scope.sendWidget.toolData.extend.bg == style){
			//     scope.sendWidget.toolData.extend.bg = 'default';
			// } else {
			scope.sendWidget.toolData.extend.bg = style;
			// }
		};


		//编辑
		if (scope.modal.modalType == 'text-e') {
			widget.sendDataUrl = LINK_WIDGET_EDIT;

			for (var i = 0; i < scope.rootWidget.list.length; i++) {
				if (scope.rootWidget.list[i].baseWidget.widgetId == scope.modal.modalEditId) {

					//复制对象
					scope.sendWidget = angular.copy(scope.rootWidget.list[i]);
					widget.widgetIndex = i;
					break;
				}
			}

			scope.sendWidget.baseWidget.widgetId = scope.modal.modalEditId;
			if (angular.isString(scope.sendWidget.toolData.extend)) {
				scope.sendWidget.toolData.extend = angular.fromJson(scope.sendWidget.toolData.extend);
			}
			widget.layoutCol = scope.sendWidget.col;
			widget.layoutRow = scope.sendWidget.row;
			widget.layoutSizeX = scope.sendWidget.sizeX;
			widget.layoutSizeY = scope.sendWidget.sizeY;
			widget.layoutMinSizeX = scope.sendWidget.minSizeX;
			widget.layoutMinSizeY = scope.sendWidget.minSizeY;

			delete scope.sendWidget.col;
			delete scope.sendWidget.row;
			delete scope.sendWidget.sizeX;
			delete scope.sendWidget.sizeY;
			delete scope.sendWidget.minSizeX;
			delete scope.sendWidget.minSizeY;
			delete scope.sendWidget.widgetDrawing;
			delete scope.sendWidget._ext;
		}

		//保存
		scope.save = function () {
			var extend = scope.sendWidget.toolData.extend;
			if (!angular.isString(scope.sendWidget.toolData.extend)) {
				scope.sendWidget.toolData.extend = angular.toJson(extend);
			}

			dataMutualSrv.post(widget.sendDataUrl, scope.sendWidget).then(function (data) {
				if (data.status == 'success') {
					var sizeX = 5;
					var minX = 5;
					var sizeY = 1;
					var minY = 1;


					scope.sendWidget.sizeX = widget.layoutSizeX || sizeX;
					scope.sendWidget.sizeY = widget.layoutSizeY || sizeY;
					scope.sendWidget.minSizeX = widget.layoutMinSizeX || minX;
					scope.sendWidget.minSizeY = widget.layoutMinSizeY || minY;

					//widget定位
					scope.rootWidget.locateId = widgetId;

					//更新状态
					scope.sendWidget.toolData.extend = extend;
					scope.rootWidget.noData = false;

					if (scope.modal.modalType == 'text-e') {
						scope.sendWidget.col = widget.layoutCol;
						scope.sendWidget.row = widget.layoutRow;
						scope.rootWidget.list.splice(widget.widgetIndex, 1, scope.sendWidget);
					} else {
						scope.rootWidget.list.push(scope.sendWidget);
					}

					//关闭弹出层
					scope.close();
				} else if (data.status == 'failed') {
					console.log('Post Data Failed!')
				} else if (data.status == 'error') {
					console.log('Post Data Error: ')
					console.log(data.message)
				}
			})
		};


		//widget 名称修改
		scope.editTitle = function () {
			if (scope.widget.title.selected) {
				scope.widget.title.editing = true;
			}
		};//editTitle
		scope.doneEditing = function () {
			scope.widget.title.editing = false;
			scope.sendWidget.baseWidget.widgetTitle = scope.widget.title.name;
		};//doneEditing
		scope.updateWidgetTitleByKeydown = function (e) {
			var keycode = window.event ? e.keyCode : e.which;
			if (keycode == 13) {
				scope.doneEditing();
			}
		}
	}
}
