'use strict';


/**
 * widget复制
 *
 */

import './widget-copy.scss';
import tpl from './widget-copy.html';
import {
	uuid
} from 'components/modules/common/common';


widgetCopyDirective.$inject = ['$q', 'siteEventAnalyticsSrv', 'PanelResources', 'WidgetServices'];

function widgetCopyDirective($q, siteEventAnalyticsSrv, PanelResources, WidgetServices) {
	return {
		restrict: 'EA',
		scope: {
			panels: "<",		//面板列表
			panelLayout: "<",	//面板位置信息
			currentPanelId: "<",//当前面板ID
			widgets: "<",		//widget列表
			currentWidget: "<",	//当前widget
			onCancel: '&',  	//取消回调
            onSuccess: '&' 		//发送成功回调
		},
		template: tpl,
		link: link
	};

	function link(scope, element, attrs) {

		scope.myOptions = {};

		// ==========

		//判断是否为分享页面
		scope.isShare = isShare;

		//下拉选择面板
		scope.selectPanel = selectPanel;

		//复制保存
		scope.widgetCopy = widgetCopy;

		//入口
		init();

		// ==========

		//入口
		function init(){

			//根据面板位置信息生成面板列表(排序分享页面)
			scope.myOptions.panels = WidgetServices.layoutCreatePanels(scope.panelLayout);

			//获取当前面板信息
			scope.myOptions.currentPanel = WidgetServices.getMyPanel(scope.panels, scope.currentPanelId);
		}

		//判断是否为分享页面
        function isShare(panelId) {
            var panel = WidgetServices.getMyPanel(scope.panels, panelId);
            return panel && panel.shareSourceId;
        }

		//下拉选择面板
		function selectPanel (panel) {
			scope.myOptions.currentPanel = angular.copy(panel);
		}

		//复制保存
		function widgetCopy(){

			//loading
			WidgetServices.showPopupLoading();
			var widgetInfo = getCopyInfo(scope.currentWidget, scope.myOptions.currentPanel.panelId);
			var sendWidget = WidgetServices.sendDataFormat(widgetInfo.widget);
			var layout = widgetInfo.layout;

			WidgetServices.add(sendWidget)
			.then((data) => { //(@data: widgetInfo)

				if (scope.myOptions.currentPanel.panelId == scope.currentPanelId) {
					//原页面复制

					data.autoPos = 1;
					data.minSizeX = layout.minx;
					data.minSizeY = layout.miny;
					data.sizeX = layout.x;
					data.sizeY = layout.y;

					// 初始化_ext字段
					if (data.baseWidget.widgetType != 'custom') data._ext = {};

					scope.onSuccess({'data': {"type": "currentPanel", "info": data, "widgetId": data.baseWidget.widgetId}})
				}
				else {
					//跨面板复制

					sendLayout(scope.myOptions.currentPanel.panelId, layout, sendWidget.baseWidget.widgetId);
				}
			})
			.finally(() => {
				WidgetServices.hidePopupLoading();

				//GTM
				siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget', 'copy_save');
			})
		}

		//获取复制widget的具体信息
		function getCopyInfo(widget, panelId){
			widget = angular.copy(widget);

			var widgetId = uuid();
			var currentTime = parseInt(new Date().getTime());
			var layout = {
		    	id: widgetId,
				x: widget.sizeX,
				y: widget.sizeY,
				minx: widget.minSizeX,
				miny: widget.minSizeY
		    };

			widget.panelId = panelId;
		    widget.baseWidget.widgetId = widgetId;
		    widget.baseWidget.isTitleUpdate = 0;
		    widget.baseWidget.createTime = currentTime;
		    widget.baseWidget.modifyTime = currentTime;
		    widget.baseWidget.widgetTitle = WidgetServices.getName('copy', widget.baseWidget.widgetTitle, scope.widgets);

		    if (widget.baseWidget.widgetType == 'chart') {
		        for (var i = 0; i < widget.variables.length; i++) {
		            var variableId = uuid();
		            widget.variables[i].variableId = variableId;
		        }
		    }
		    return {"widget": widget, "layout": layout};
		}

		//跨面板复制时发送位置信息
		function sendLayout(panelId, layout, widgetId) {
			var max = 0;
			var index = 0;

			//先请求面板位置信息
			PanelResources.getPanelInfo(null, {
				panelId: panelId
			})
			.then((data) => {
				var panelInfo = angular.copy(data);
				var panelLayout = decodeURIComponent(panelInfo.layout);
				panelLayout = panelLayout == 'null' ? [] : angular.fromJson(panelLayout);

				//找最大值
				if (panelLayout.length > 0) {
					$.each(panelLayout, function (i, item) {
						var currentRow = item.r + item.y;
						max = currentRow >= max ? currentRow : max;
					});
					layout.r = max;
					layout.c = 0;
				}
				panelLayout.push(layout);

				var layoutJson = encodeURIComponent(angular.toJson(panelLayout));

				panelInfo.type = 'panel';
				panelInfo.layout = layoutJson;
				panelInfo.ptonePanelLayout = {dataVersion: -1};

				return $q.resolve(panelInfo);
			})
			.then((panelInfo) => {
				//插入后更新面板位置信息
				PanelResources.updatePanel(panelInfo, {
					panelId: panelInfo.panelId
				})
				.then((data) => {
					scope.onSuccess({'data': {"type": "otherPanel", "info": panelId, "widgetId": widgetId}})
				})
			})
		}
	}
}

export default widgetCopyDirective
