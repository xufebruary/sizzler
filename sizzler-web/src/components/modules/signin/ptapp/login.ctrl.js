import {
	LINK_PTENGINE_LOGIN,
	LINK_PTENGINE_LOGIN_AUTHORIZE,
	LINK_PTENGINE_ANALYTICS,
	LINK_PTAPP_LOGIN,
	LINK_PTAPP_LOGIN_AUTHORIZE,
	getScrollbarWidth
} from 'components/modules/common/common';
import md5 from 'js-md5';

import cookieUtils from 'utils/cookie.utils';

var emailReg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;

angular
	.module('pt')
	.controller("signinPtappController", ['$scope', '$http', '$cookies', '$cookieStore', '$translate', '$state', 'sessionContext', '$rootScope', 'dataMutualSrv',function($scope, $http, $cookies, $cookieStore, $translate, $state, sessionContext, $rootScope, dataMutualSrv) {


	$scope.authError = false;
	$scope.errorMessage = "";
	$scope.showBar = false;
	$scope.showSigninDiv = true;
	$scope.userCookieTime = false;
	$scope.loadSetting = {
		loginRes: null //登陆返回内容
	};


	var loginUrl = '',authorizeUrl = '',notAuthorizeUrl = '';

	$scope.login = function() {
		$scope.form.email.$dirty = true;
		$scope.form.userPassword.$dirty = true;
		var user = {
			userEmail: $scope.userEmail,
			userPassword: typeof($scope.userPassword) != 'undefined' ? md5($scope.userPassword) : null
		};
		if (!$scope.userPassword || !$scope.userEmail) {
			return;
		}
		if (!emailReg.test($scope.userEmail)) {
			$scope.errorMessage = $translate.instant('LOGIN.ERROR_TIP.EMAIL_FORMAT');
			return;
		}
		//$scope.showBar = true;
		$scope.loadSetting.signin = true;
		//判断登陆来源,拼出请求接口地址
		if(window.localStorage && localStorage.getItem('ptappLoginReferrer') && localStorage.getItem('ptappLoginReferrer') == 'login'){
			loginUrl = LINK_PTAPP_LOGIN + "?sid=" + cookieUtils.get('sid');
			authorizeUrl =  LINK_PTAPP_LOGIN_AUTHORIZE + $scope.userEmail + '/login/1/'+localStorage.getItem('ptappAuthSign');
			notAuthorizeUrl =  LINK_PTAPP_LOGIN_AUTHORIZE + $scope.userEmail + '/login/0/null';
		}else if(window.localStorage && localStorage.getItem('ptappLoginReferrer') && localStorage.getItem('ptappLoginReferrer') == 'dataSources'){
			var spaceId = $scope.loadSetting.spaceId = localStorage.getItem('ptappSpaceId');
			loginUrl = LINK_PTAPP_LOGIN;
			authorizeUrl =  LINK_PTAPP_LOGIN_AUTHORIZE + localStorage.getItem('ptappAuthSign');
			notAuthorizeUrl =   LINK_PTAPP_LOGIN_AUTHORIZE + $scope.userEmail + '/analytics/0/null'+'/'+spaceId;
		}else{
			return false;
		}

		$http({
			method: 'POST',
			url: loginUrl,
			data: angular.toJson(user)
		})
			.success(function(data, status, headers, config) {
				if (data.status == 'success') {
					$scope.showSigninDiv = false;
					$scope.userEmail = data.content.ptEmail;
					$scope.loadSetting.signin = false;
					$scope.loadSetting.loginRes = data.content;

				} else if (data.status == 'failed') {
					$scope.authError = true;
					$scope.errorMessage = $translate.instant(data.message);
					$scope.loadSetting.signin = false;
					console.log('login failed!')
				} else if (data.status == 'error') {
					$scope.authError = true;
					$scope.errorMessage = $translate.instant("SYSTEM.SYSTEM_ERROR");
					console.log(data.message);
					$scope.loadSetting.signin = false;
				}
			})
			.error(function(data, status, headers, config) {
				$scope.loadSetting.signin = false;
				console.log('server error')
			});
	};

	$scope.authorizeFun = function(){
		$scope.loadSetting.signin = true;
		var postData = {
			name:$scope.userEmail,
			dsId: 215,
			dsCode: 'ptapp',
			configObject:$scope.loadSetting.loginRes,
			spaceId:$scope.loadSetting.spaceId
		};
		dataMutualSrv.post(authorizeUrl,postData).then(function(data){
			if (data.status == 'success') {
				if(data.content.sid){//从登录授权过来的用户，需要保存用户信息
					sessionContext.saveSession(data.content.sid, 'signin');
					// $state.go('pt.dashboard');
				}else if(data.content.email){//从数据源管理过来授权的用户，需要关闭页面
					var opened=window.open('about:blank','_self');
					opened.opener=null;
					opened.close();
				}
				$scope.loadSetting.signin = false;
			} else if (data.status == 'failed') {
				console.log('Post Data Failed!');
			} else if (data.status == 'error') {
				console.log('Post Data Error: ');
				console.log(data.message)
			}
			$scope.loadSetting.widget = false;
			localStorage.removeItem("ptappAuthSign");
		})
	};
	$scope.notAuthorize = function(){
		var opened=window.open('about:blank','_self');
		opened.opener=null;
		opened.close();
		localStorage.removeItem("ptappAuthSign");
	};

	var loginData = $scope.loginData = {
		points: loginSvg()
	};

	window.onresize = function() {
		loginData.points = loginSvg();
	};

	$scope.signup = function(){
		if(window.localStorage && window.localStorage.getItem('NG_TRANSLATE_LANG_KEY') && window.localStorage.getItem('NG_TRANSLATE_LANG_KEY') == 'ja_JP'){
			window.location.href = 'http://www.ptengine.jp';
		}else if(window.localStorage && window.localStorage.getItem('NG_TRANSLATE_LANG_KEY') && window.localStorage.getItem('NG_TRANSLATE_LANG_KEY') == 'en_US'){
			window.location.href = 'http://www.ptengine.com';
		}else{
			window.location.href = 'http://www.ptengine.cn';
		}
	}
}]);


/*******************
 * Login Canvas
 *******************/
function loginSvg() {
	var defaultW = 1366;
	var defaultH = 768;
	var w = Math.max($('body').width(), window.innerWidth) - getScrollbarWidth();
	var h = Math.max($('body').height(), window.innerHeight);
	var points = [
		"0 0," +
		parseInt(557 / defaultW * w) + " " + h + "," +
		w + " " + h + "," +
		w + " 0," +
		parseInt(1287 / defaultW * w) + " " + parseInt(167 / defaultH * h),
		"0 " + h + ", " +
		parseInt(904 / defaultW * w) + " " + parseInt(576 / defaultH * h) + "," +
		w + " " + 0 + "," +
		w + " " + 0 + "," +
		w + " " + h
	]

	return points;
} //loginCanvas


