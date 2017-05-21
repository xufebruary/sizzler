'use strict';


/**
 * widget删除
 *
 */

import tpl from './widget-delete.html';

widgetDeleteDirective.$inject = ['siteEventAnalyticsSrv', 'WidgetServices'];

function widgetDeleteDirective(siteEventAnalyticsSrv, WidgetServices) {
	return {
		restrict: 'EA',
		scope: {
			currentWidgetId: "<", //当前widget
			onCancel: '&', //取消回调
            onSuccess: '&' //发送成功回调
		},
		template: tpl,
		link: link
	};

	function link(scope, element, attrs) {

		//删除保存
		scope.deleteWidget = function () {
			WidgetServices.showPopupLoading();

			WidgetServices.delete(scope.currentWidgetId)
			.then((data) => {

				scope.onSuccess({data: {widgetId: scope.currentWidgetId}});
			})
			.finally(() => {
				WidgetServices.hidePopupLoading();

				//GTM
				siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget', 'del_save');
			})
		};
	}
}

export default widgetDeleteDirective;
