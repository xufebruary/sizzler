/**
 * 添加空间底部提示
 */

spaceCreateTipsDirective.$inject = ['UserResources'];
export default function spaceCreateTipsDirective(UserResources) {
	'use strict';

	return {
		restrict: 'EA',
		scope: {
			tipsUserInfo: '='   //用户设置信息
		},
		template: '<div class="space-create-tips">'
			   	+	'<div translate="SPACE.TIP.ADD_SPACE"></div>'
			    +	'<a translate="ONBOARDING.TIPS.FUNC_TIPS.BTN_CLOSE" ng-click="close()"></a>'
				+ '</div>',
		link: link
	};

	function link(scope, element, attr) {
	    scope.close = function() {
	        var showTipsTmp = angular.copy(scope.tipsUserInfo);
	        showTipsTmp['spaceCreate'] = 1;
	        var sendData = {
	            showTips: angular.toJson(showTipsTmp)
	        };

	        UserResources.updateUsersSettingsInfo(sendData)
	            .finally(() => {
	                scope.tipsUserInfo['spaceCreate'] = 1;
	            })
	    }
	}
}