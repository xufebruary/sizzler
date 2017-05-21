import {
	LINK_WIDGET_DELETE,
	LINK_WIDGET_ADD,
	uuid
} from 'components/modules/common/common';

import editorCustomTpl from './custom.tpl.html';
import './custom.css';

editorCustom.$inject = ['$document', '$translate', '$rootScope', 'dataMutualSrv', 'siteEventAnalyticsSrv'];
function editorCustom($document, $translate, $rootScope, dataMutualSrv, siteEventAnalyticsSrv) {
	return {
		restrict: 'EA',
		template: editorCustomTpl,
		link: link
	};

	function link(scope, elem, attrs) {
		/**
		 * 删除小widget
		 * @param child
		 */
		scope.deleteCustomWidget = function (child) {
			scope.widgetDeleteInfo.id = child.baseWidget.widgetId;
			scope.widgetDeleteInfo.title = child.baseWidget.widgetTitle;
			scope.widgetDeleteInfo.widget = child;

			dataMutualSrv.post(LINK_WIDGET_DELETE + scope.widgetDeleteInfo.id, '').then(function (data) {
				if (data.status == 'success') {

					//更新children字段
					scope.modal.editorNowCopy.children.splice(scope.modal.editorNowCopy.children.indexOf(scope.widgetDeleteInfo.widget), 1);
					scope.modal.editorNow = scope.modal.editorNowCopy;
					scope.$emit('changeCustomWidget');//向dashboard发送编辑小widget的事件

					//全站事件统计
					siteEventAnalyticsSrv.createData({
						uid: $rootScope.userInfo.ptId,
						time: new Date().getTime(),
						position: 'widget',
						operate: 'widget-delete-save-btn',
						operateId: uuid(),
						content: JSON.stringify({
							'widget-id': scope.widgetDeleteInfo.id,
							'widget-title': scope.widgetDeleteInfo.title,
							'panel-id': scope.widgetDeleteInfo.widget.panelId
						})
					});
				} else if (data.status == 'failed') {
					console.log('Post Data Failed!')
				} else if (data.status == 'error') {
					console.log('Post Data Error: ');
					console.log(data.message)
				}
			})

		};

		/**
		 * 复制小的widget
		 * @param child
		 */
		scope.copyCustomWidget = function (child) {
			var saveWidget = angular.copy(child);
			saveWidget.baseWidget.widgetId = uuid();
			saveWidget.baseWidget.createTime = parseInt(new Date().getTime());
			saveWidget.baseWidget.modifyTime = parseInt(new Date().getTime());
			for (var i = 0; i < saveWidget.variables.length; i++) {
				saveWidget.variables[i].variableId = uuid();
			}
			dataMutualSrv.post(LINK_WIDGET_ADD, saveWidget, 'wgtSave').then(function (data) {
				if (data.status == 'success') {

					//此时编辑的是小widget，需要将复制的信息更新到父级去，父级的副本是保存在editorNowCopy上面的
					scope.modal.editorNowCopy.children.push(saveWidget);

					//全站事件统计
					siteEventAnalyticsSrv.createData({
						uid: $rootScope.userInfo.ptId,
						time: new Date().getTime(),
						position: 'widget',
						operate: 'widget-copy-save-btn',
						operateId: uuid(),
						content: angular.toJson(saveWidget)
					});
				} else if (data.status == 'failed') {
					console.log('Post Data Failed!')
				} else if (data.status == 'error') {
					console.log('Post Data Error: ');
					console.log(data.message)
				}


			});
		};

		scope.reWriteLayout = function (direction) {
			var childrenLength = scope.modal.editorNowCopy.children.length;
			scope.layout = scope.modal.editorNow.layout;
			if (direction == 'up') {
				//提高小widget的层级，但是不能高于所有widget的数量值
				if (scope.layout['z-index'] == 'auto') {
					scope.layout['z-index'] = 1;
				} else {
					if (scope.layout['z-index'] >= childrenLength) {
						scope.layout['z-index'] = childrenLength;
					} else {
						scope.layout['z-index'] = scope.layout['z-index'] + 1;
					}
				}
			} else if (direction == 'down') {
				//降低小widget的层级，但是不能低于0
				if (scope.layout['z-index'] == 'auto') {
					scope.layout['z-index'] = 0;
				} else {
					if (scope.layout['z-index'] == 0) {
						scope.layout['z-index'] = 0;
					} else {
						scope.layout['z-index'] = scope.layout['z-index'] - 1;
					}
				}
			}
			$('#' + scope.modal.editorNow.baseWidget.widgetId).css('z-index', scope.layout['z-index']);
			scope.modal.editorNow.layout = scope.layout;
			scope.saveData();
		}
	}
}

export default editorCustom;
