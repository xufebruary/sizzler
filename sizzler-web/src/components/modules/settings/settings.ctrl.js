'use strict';

import {
	LINK_SIGNOUT_URL,
	LINK_CREATE_LOGIN_KEY,
	LINK_SETTINGS_INFO_UPDATE,
	uuid,
	objectIsEmpty,
	includeStyle
} from 'components/modules/common/common';


import md5 from 'js-md5';
import consts from 'configs/const.config';

import cookieUtils from 'utils/cookie.utils';

var Base64 = require('js-base64').Base64;

/**
 * settings
 *
 */
angular
.module('pt')
.controller('settingsCtrl', ['$state', '$scope', '$rootScope', '$translate', '$http', 'dataMutualSrv', 'UserResources', 'publicDataSrv', settingsCtrl]);


function settingsCtrl($state, $scope, $rootScope, $translate, $http, dataMutualSrv, UserResources, publicDataSrv) {

	var settings = $scope.settings = {
		'userEmail': $rootScope.userInfo.userEmail,
		'userName': $rootScope.userInfo.userName,
		'locale': $scope.rootUser.settingsInfo.locale,
		//'weekStart': $scope.rootUser.settingsInfo.weekStart,
		'showLoding': false,
		'modWeekStart': $scope.rootUser.settingsInfo.weekStart == 'sunday' ? $translate.instant('HEADER.PROFILE.SETTINGS.SUNDAY') : $translate.instant('HEADER.PROFILE.SETTINGS.MONDAY'),
		'demoSwitch': $scope.rootUser.settingsInfo.demoSwitch,
		'pageDemoSwitch': $scope.rootUser.settingsInfo.demoSwitch == '0' ? false : true,


		'newPassword': null,
		'accountSaveSuccess': false,
		'dateSaveSuccess': false,

		'passwordIsChange': false

		//'saveSuccess':false
	};


	//监听用户设置信息
	var mywatch = $scope.$watch('rootUser.settingsInfo', function (newValue, oldValue, scope) {
		if (objectIsEmpty(newValue)) {
			return;
		}

		//注销当前监听事件
		mywatch();

		if ($scope.rootUser.settingsInfo.locale == "zh_CN") {
            settings.modLocale = '中文';
		} else if ($scope.rootUser.settingsInfo.locale == "en_US") {
            settings.modLocale = 'English';
		} else if ($scope.rootUser.settingsInfo.locale == "ja_JP") {
			settings.modLocale = '日本語';
		}
	});


	$scope.user = {};
	$scope.modifyPassword = function () {
		if ($scope.settings.newPassword) {
			console.log('change password');
			$scope.user.userPassword = $scope.settings.newPassword;
			$scope.user.userPassword = md5($scope.user.userPassword);
			$http({
				method: 'POST',
				url: consts.WEB_MIDDLE_URL + '/pt/users/update' + "?sid=" + cookieUtils.get('sid'),
				data: angular.toJson($scope.user)
			}).success(function (data, status, headers, config) {
				if (data.status == 'success') {
					//alertify.okBtn($translate.instant('COMMON.OK')).alert($translate.instant('HEADER.PROFILE.MODIFY_PASSWORD.MODIFY_PASSWORD_SUCCESS'));
					//$('form')[0].reset();
					//$state.go('pt.dashboard',{type:'true'});
				} else if (data.status == 'failed') {
					alertify.okBtn($translate.instant('COMMON.OK')).alert($translate.instant('HEADER.PROFILE.MODIFY_PASSWORD.TITLE') + $translate.instant('COMMON.FAILED'));
				} else if (data.status == 'error') {
					alertify.okBtn($translate.instant('COMMON.OK')).alert($translate.instant('HEADER.PROFILE.MODIFY_PASSWORD.TITLE') + $translate.instant('COMMON.ERROR'));
				}
			}).error(function (data, status, headers, config) {
				console.log('server error')
			});
		}
	};


	//切换语言
	$scope.sltLocale = function (name, code) {
		settings.locale = code;
		settings.modLocale = name;
	};

	//切换周起始日期
	$scope.sltWeekStart = function (name, code) {
		settings.weekStart = code;
		settings.modWeekStart = $translate.instant(name);
	};


	//保存数据
	$scope.saveData = function () {
		var flag = false;

		$scope.loadSetting.settings = true;


		//语言
		includeStyle('/assets/css/l18n-' + settings.locale + '.css', 'l18n'); //注入style
		$translate.use(settings.locale);
		$scope.rootUser.settingsInfo.locale = settings.locale;
		//自己维护语言版本
		localStorage.setItem(consts.I18N_KEY, settings.locale);

		//周起始时间
		//$scope.rootUser.settingsInfo.weekStart = settings.weekStart;

		//保存语言及周起始时间
		var settingData = {
			"ptId": $rootScope.userInfo.ptId,
			"locale": settings.locale,
			"demoSwitch": settings.pageDemoSwitch == true ? '1' : '0'
		};

		dataMutualSrv.post(LINK_SETTINGS_INFO_UPDATE, settingData).then(function (data) {
			if (data.status == 'success') {
				console.log('Setting Account Success!')
				$scope.rootUser.settingsInfo.demoSwitch = settingData.demoSwitch;
				settings.showLoding = false;

				$scope.settings.accountSaveSuccess = true;
				$scope.saveSuccess();
				//$scope.close();

				//切换css
				// changeThemeSrv.changeTheme(settings.locale);
			} else if (data.status == 'failed') {
				console.log('Setting Account Failed!')
			} else if (data.status == 'error') {
				console.log('Setting Account Error: ')
				console.log(data.message)
			}
		});

		//保存用户信息
		$rootScope.userInfo.userEmail = settings.userEmail;
		$rootScope.userInfo.userName = settings.userName;
		$rootScope.userInfo.fistLetterSvg = "#icon-" + angular.lowercase($rootScope.userInfo.userEmail.slice(0, 1));

		var user = {
			"ptId": $rootScope.userInfo.ptId,
			"userName": settings.userName
		}

		if ($scope.settings.newPassword && $scope.settings.oldPassword) {
			user.userOldPassword = md5($scope.settings.oldPassword);
			user.userPassword = md5($scope.settings.newPassword);
		}
		dataMutualSrv.post(consts.WEB_MIDDLE_URL + '/pt/users/update', user).then(function (data) {
			if (data.status == 'success') {
				$scope.settings.dateSaveSuccess = true;
				$scope.saveSuccess();
				$scope.settings.passwordIsChange = data.content.updatePassword;

				if($scope.settings.passwordIsChange) angular.element('body').addClass('modal-open');
			} else {
				if (data.status == 'failed') {
					console.log('Setting User Failed!')
				} else if (data.status == 'error') {
					console.log('Setting User Error: ')
					console.log(data.message)
				}
			}
			$scope.loadSetting.settings = false;
		});

		$scope.saveSuccess = function () {
			$('.mod-setting-ft .font-save-success').hide();
			if ($scope.settings.dateSaveSuccess && $scope.settings.accountSaveSuccess) {
				$scope.settings.dateSaveSuccess = false;
				$scope.settings.accountSaveSuccess = false;

				//两个条件都满足显示提示语
				$('.mod-setting-ft .font-save-success').show();
				//两秒后提示消失
				setTimeout(function () {
					$('.mod-setting-ft .font-save-success').fadeOut();
				}, 2000);
			} else {
				//$scope.settings.dateSaveSuccess =false;
				//$scope.settings.accountSaveSuccess = false;
			}
		}
	}

	//退出
	$scope.logout = function(){
		//先发送请求再清除$rootScope.sid
		dataMutualSrv.post(LINK_SIGNOUT_URL);
		UserResources.clear();
		publicDataSrv.clearPublicData('all');
		angular.element('body').removeClass('modal-open')
		$state.go('signin');
	}

}

angular
.module('pt')
.directive('validatePassword', ['$http', '$rootScope', function ($http, $rootScope) {
	return {
		require: 'ngModel',
		link: function (scope, ele, attrs, ctrl) {
			ele.bind('blur', function () {
				if (ctrl.$viewValue) {
					scope.user = {};
					scope.user.userPassword = md5(ctrl.$viewValue);
					$http({
						method: 'POST',
						//url: WEB_MIDDLE_URL + '/pt/users/password/check' + ";jsessionid=" + $rootScope.sid,
						url: consts.WEB_MIDDLE_URL + '/pt/users/password/check' + "?sid=" + $rootScope.sid,
						data: angular.toJson(scope.user)
					}).success(function (data, status, headers, config) {
						if (data.message == 1) {
							ctrl.$setValidity('passwordAvailable', true);
						} else {
							ctrl.$setValidity('passwordAvailable', false);
						}
					}).error(function (data, status, headers, config) {
						ctrl.$setValidity('passwordAvailable', false);
					});
				} else {
					ctrl.$setValidity('passwordAvailable', true);
				}
			});
		}
	}
}])
