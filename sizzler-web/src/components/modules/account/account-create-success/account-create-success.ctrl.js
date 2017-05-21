'use strict';


import ProductConfig from 'configs/product.config';
import {
	uuid,
	GetRequest,
	isAndroid,
	isIphone,
	LINK_SETTINGS_INFO,
	LINK_SPACE_DOMAIN_CHECK
} from 'components/modules/common/common';

import md5 from 'js-md5';

var Base64 = require('js-base64').Base64;

/**
 * 账户设置成功后的提示(移动端)
 *
 */
angular
    .module('pt')
    .controller('accountCreateSuccessCtrl', ['$scope', '$rootScope', '$translate', '$state', 'publicDataSrv', 'uiLoadingSrv', 'dataMutualSrv', accountCreateSuccessCtrl]);

function accountCreateSuccessCtrl($scope, $rootScope, $translate, $state, publicDataSrv, uiLoadingSrv, dataMutualSrv) {
	var vm = this;

	// ==========

	// 入口
	init();

	// 校验onboarding流程是否走完
	vm.checkOnboarding = checkOnboarding;

	// ==========

	function init () {
		//隐藏loading
		$scope.hideLoading('body');

		vm.host = window.location.host;
		vm.screen = window.screen;
		vm.userEmail = $rootScope.userInfo.userEmail;
		vm.space = publicDataSrv.getPublicList('rootSpace').current;
		vm.settingsInfo = publicDataSrv.getPublicData('settingsInfo');
	}

	function checkOnboarding() {
		uiLoadingSrv.createLoading(angular.element('.account-create-success'));

		//获取用户设置信息
		dataMutualSrv.get(LINK_SETTINGS_INFO + '/' + vm.space.spaceId).then(function (data) {
			if (data.status == 'success') {
				console.log(data.content)

				if(data.content.viewOnboarding == 1){
					$state.go('pt.dashboard', {'spaceDomain': vm.space.domain});
				} else {
					// vm.checkFailed = true;

					//弹出提示框
					alert($translate.instant('ACCOUNT_CREATE_SUCCESS.TIPS_4'));
					//2秒后关闭
					// $timeout(function () {
					// 	$scope.$apply(function () {
					// 		vm.checkFailed = false;
					// 	})
					// }, 3000);
				}
			} else {
				console.log('Get settingsInfo Failed!');
				if (data.status == 'error') {
					console.log(data.message)
				}
			}

			uiLoadingSrv.removeLoading(angular.element('.account-create-success'));
		});
	}
}
