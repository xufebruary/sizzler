'use strict';

import {
  LINK_SIGNOUT_URL,
  LINK_SIGNIN_URL,
  LINK_GA_SIGNIN_URL,
  LINK_USER_RESET_EXISTS_URL,
  OTHER_LOGIN_WEB_SOCKET,
  getLocalLang,
  isAndroid,
  isIphone,
  loginKey,
  setCookie,
  delCookie,
  clearCookie,
  uuid,
  openWindow
} from 'components/modules/common/common';
import md5 from 'js-md5';

import consts from 'configs/const.config';

/**
 * signin
 * 登录
 *
 */
angular
    .module('pt')
    .controller('signinCtrl', ['$scope', '$http', '$translate', '$state', 'sessionContext', 'siteEventAnalyticsSrv', 'websocket', '$stateParams','dataMutualSrv','publicDataSrv','sysRoles','permissions',signinCtrlFunc]);

function signinCtrlFunc($scope, $http, $translate, $state, sessionContext, siteEventAnalyticsSrv, websocket, $stateParams,dataMutualSrv,publicDataSrv,sysRoles,permissions) {
    $scope.emailErrorMessage = "";
    $scope.passwordErrorMessage = "";
	$scope.showErrorTipsPhone = {};
    $scope.showSigninDiv = true;
    $scope.userCookieTime = false;
    $scope.loadSetting = {signin: false};
    $scope.community = $stateParams.community;
    if (typeof($scope.community) == "undefined" || $scope.community == null) {
        $scope.community = "false";
    }


    //是否为移动设备（手机）
    $scope.isPhone = isAndroid || isIphone;

    /**
     * 在登陆页面假如能找到用户名和密码信息，需要自动登陆
     */

    if (window.localStorage && localStorage.getItem("ptnm")) {
        $scope.userEmail = localStorage.getItem("ptnm");
    }
    $scope.login = function () {
        $scope.form.email.$dirty = true;
        $scope.form.userPassword.$dirty = true;

		//移动端提示信息特殊处理
		if($scope.isPhone){
			$scope.isShowPhoneTips();
		}

		var user = {
            userEmail: $scope.userEmail,
            userPassword: typeof($scope.userPassword) != 'undefined' ? md5($scope.userPassword) : null,
            rememberMe: !!$scope.userCookieTime
        };
        if (!$scope.userPassword || !$scope.userEmail) {
            return;
        }
        $scope.loadSetting.signin = true;

        $http({
            method: 'POST',
            url: LINK_SIGNIN_URL + "?community=" + $scope.community,
            data: angular.toJson(user)
        })
		.success(function (data, status, headers, config) {
			if (data.status == 'success') {
				//用户登录框保存的email
				localStorage.setItem("ptnm", user.userEmail);
				setCookie("ptId", data.content.uid);
				setCookie("ptEmail", data.content.ptEmail);

				if (data.content.type == "OFFICIAL_USER") {
					if ($scope.community == "false") {
						//如果记住密码，则保存
						if ($scope.userCookieTime) {
							localStorage.setItem("sid", data.content.sid);
							localStorage.setItem("ptmm", user.userPassword);
						}
						sessionContext.saveSession(data.content.sid, 'signin');
					}
				}


                //全站事件统计
                siteEventAnalyticsSrv.createData({
                    uid: data.content.uid,
                    where: "main_user_login",
                    what: "login",
                    how: "click",
                    value: user.userEmail
                });
			}
			else if (data.status == 'failed') {
				if (data.message == "email_error") {
					$scope.emailErrorMessage = $translate.instant("LOGIN.ERROR_TIP.USER_NOT_RIGHT");
				} else if (data.message == "password_error") {
					$scope.passwordErrorMessage = $translate.instant("LOGIN.ERROR_TIP.PASSWORD_NOT_RIGHT");
				}

				$scope.loadSetting.signin = false;
				console.log('login failed!')
			}
			else if (data.status == 'error') {
				$scope.emailErrorMessage = $translate.instant("SYSTEM.SYSTEM_ERROR");
				console.log(data.message)
				$scope.loadSetting.signin = false;
			}

			//移动端提示信息特殊处理
			if($scope.isPhone){
				$scope.isShowPhoneTips();
			}
		})
		.error(function (data, status, headers, config) {
			$scope.loadSetting.signin = false;

			//移动端提示信息特殊处理
			if($scope.isPhone){
				$scope.isShowPhoneTips();
			}
		});
    };

    $scope.getLoginSource = function(type){
        var source = getLocalLang().source;
        var loginSource;
        return loginSource;
    };

    $scope.otherLogin = function (sid) {
        $scope.loadSetting.signin = true;
        //$scope.showSigninDiv = false;
        $http({
            method: 'POST',
            url: LINK_GA_SIGNIN_URL + "?ptEmail=" + sid
        })
            .success(function (data, status, headers, config) {
                if (data.status == 'success') {
                    localStorage.setItem("gid", sid);
                    sessionContext.saveSession(data.content.sid, 'signin');
                    return;
                } else if (data.status == 'failed') {
                    alert("login failed");
					$scope.loadSetting.signin = false;
					console.log(data.message)
                } else if (data.status == 'error') {
                    $(location).attr('href', '/');
					$scope.loadSetting.signin = false;
					console.log(data.message)
				}
            })
            .error(function (data, status, headers, config) {
                $scope.loadSetting.signin = false;
                console.log('server error')
            });
        return;
    }

   //  $scope.authorization = function (url) {
   //      var sign = uuid();
   //      //链接socket;
   //      var accreditSocket = new websocket;
   //      accreditSocket.initWebSocket(OTHER_LOGIN_WEB_SOCKET + sign);
   //      //授权验证跳转
   //      openWindow(url + '&authType=login&sign=' + sign + '&community=' + $scope.community + '&localLang=' + getLocalLang().locale);
   //      //监听授权socket返回值
   //      $scope.wsData = accreditSocket.colletion;
   //      accreditSocket.ws.onmessage = function (event) {
   //          $scope.$apply(function () {
   //              $scope.wsData = event.data;
   //          });
   //      };
   //      var mywatch = $scope.$watch('wsData', function (newValue, oldValue, scope) {
			// if (!newValue || newValue === oldValue) {
			// 	return;
			// }

			// //注销当前监听事件
			// mywatch();
			// newValue = angular.fromJson(newValue);

   //          if (newValue.status == 'success') {

   //              setCookie("ptId", newValue.content.ptId);
   //              setCookie("ptEmail", newValue.content.ptEmail);
   //              var community = newValue.content.community;

   //              //关闭socket
   //              accreditSocket.disconnect();
   //              if (newValue.content.type == "PRE_REGISTRATION_USER") {
   //                  if (community == "false") {
   //                      // //用户预注册成功返回前台的email
   //                      // localStorage.setItem("preRegistrationEmail", newValue.content.ptEmail);
   //                      // if (newValue.content.userStatus == "success") {
   //                      //     $state.go('preRegistrationSuccess', {from: 'preRegistration'});
   //                      // } else {
   //                      //     $state.go('preRegistrationSuccess');
   //                      // }
   //                      $scope.otherLogin(newValue.content.key);
   //                  } else if (community == "true") {
   //                      $scope.loginBBS();
   //                      window.location.href = "http://community.ptone.jp/session/sso";
   //                  }
   //              } else if (newValue.content.type == "OFFICIAL_USER") {
   //                  if (community == "false") {
   //                      $scope.otherLogin(newValue.content.key);
   //                  } else if (community == "true") {
   //                      $scope.loginBBS();
   //                      window.location.href = "http://community.ptone.jp/session/sso";
   //                  }
   //              }
   //              console.log(newValue);
   //          }
   //      });
   //  };

    // //点击ptengine登陆的话，需要记录来源为login
    // $scope.toPtengineLogin = function () {
    //     localStorage.setItem('ptengineLoginReferrer', 'login');
    // };

    /**
     * 第三方登陆的hover效果，js处理，css不好用
     */
    $scope.lightHeight = function(e){
        var $this = $(e.currentTarget),hoverSvg = $this.find('.hover'),defaultSvg = $this.find('.default');
        hoverSvg.css('display','inline');
        defaultSvg.css('display','none');
    };
    $scope.lightLow = function(e){
        var $this = $(e.currentTarget),hoverSvg = $this.find('.hover'),defaultSvg = $this.find('.default');
        hoverSvg.css('display','none');
        defaultSvg.css('display','inline');
    };


    /**
	 * 是否显示错误提示信息(移动端)
	 * 点击登录校验,提示信息逐列显示
	 */
    $scope.isShowPhoneTips = function(){
		var emailCheck = $scope.form.email.$dirty && ($scope.form.email.$invalid || $scope.emailErrorMessage);
		var passwordCheck = emailCheck ? false : $scope.form.userPassword.$dirty && ($scope.form.userPassword.$invalid || $scope.passwordErrorMessage );
		var emailErrorType = '';
		var emailErrorMessage = '';
		var passwordErrorType = '';
		var passwordErrorMessage = '';

		if(emailCheck){

			if($scope.form.email.$error.required){
				emailErrorType = 'isEmpty';
			}
			else if($scope.form.email.$error.pattern){
				emailErrorType = 'isInvalid';
			}
			else if($scope.form.email.$error.maxlength){
				emailErrorType = 'isMax';
			}
			else if($scope.emailErrorMessage){
				emailErrorMessage = $scope.emailErrorMessage;
				emailErrorType = 'isError';
			}

			$('.ipt-login').removeAttr('style').eq(0).css('box-shadow', 'inset 0 0 0 1px #ef4f4b');
		}
		else if(passwordCheck){

			if($scope.form.userPassword.$error.required){
				passwordErrorType = 'isEmpty';
			}
			else if($scope.form.userPassword.$error.minlength){
				passwordErrorType = 'isMin';
			}
			else if($scope.form.userPassword.$error.maxlength){
				passwordErrorType = 'isMax';
			}
			else if($scope.passwordErrorMessage){
				passwordErrorMessage = $scope.passwordErrorMessage;
				passwordErrorType = 'isError';
			}

			$('.ipt-login').removeAttr('style').eq(1).css('box-shadow', 'inset 0 0 0 1px #ef4f4b');
		}

		$scope.showErrorTipsPhone = {
			wrapDom: emailCheck || passwordCheck,
			emailCheck: emailCheck,
			passwordCheck: passwordCheck,
			emailErrorType: emailErrorType,
			emailErrorMessage: emailErrorMessage,
			passwordErrorType: passwordErrorType,
			passwordErrorMessage: passwordErrorMessage
		};
	};
}


angular
    .module('pt')
    .directive('isAlreadyTaken', function ($http) {
    return {
        require: 'ngModel',
        link: function (scope, ele, attrs, ctrl) {
            ele.bind('blur', function () {
                if (ctrl.$viewValue) {
                    var link = LINK_USER_RESET_EXISTS_URL + ctrl.$viewValue;
                    $http({
                        method: 'POST',
                        url: link
                    }).success(function (data, status, headers, config) {
                        if (data.message == 0) {
                            ctrl.$setValidity('emailAvailable', true);
                        } else {
                            ctrl.$setValidity('emailAvailable', false);
                        }
                    }).error(function (data, status, headers, config) {
                        ctrl.$setValidity('emailAvailable', false);
                    });
                }
            });
        }
    }
});


angular
    .module('pt')
    .directive('isContainsBlank', [function () {
    return {
        require: 'ngModel',
        link: function (scope, ele, attrs, ctrl) {
            ele.bind('keyup', function () {
                if (/.*?\s+.*?/g.test(ctrl.$viewValue)) {
                    ctrl.$setValidity('containsBlankAvailable', false);
                } else {
                    ctrl.$setValidity('containsBlankAvailable', true);
                }
            });
        }
    }
}]);




