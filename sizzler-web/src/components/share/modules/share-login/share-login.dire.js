/**
 * Created by jianqing on 16/3/1.
 */

'use strict';

import md5 from 'js-md5';

import consts from 'configs/const.config';

var WEB_MIDDLE_URL = consts.WEB_MIDDLE_URL;

/**
 * share login
 *
 */


var emailReg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
var LINK_SIGNIN_URL = WEB_MIDDLE_URL + '/pt/users/signin';
var LINK_GA_SIGNIN_URL = WEB_MIDDLE_URL + '/pt/users/ga/signin';
var LINK_SIGNOUT_URL = WEB_MIDDLE_URL + '/pt/users/signout';
var LINK_SIGNUP_URL = WEB_MIDDLE_URL + '/pt/users/signup';
var LINK_USER_FORGOT_URL = WEB_MIDDLE_URL + '/pt/users/send/';
var LINK_USER_RESET_PASSWORD_URL = WEB_MIDDLE_URL + '/pt/users/password/reset';
var LINK_USER_RESET_EXISTS_URL = WEB_MIDDLE_URL + '/pt/users/exists';


angular
    .module('pt')
    .directive('ngInputFocus', [function () {
    var FOCUS_CLASS = "ng-focused";
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, element, attrs, ctrl) {
            ctrl.$focused = false;
            element.bind('focus', function (evt) {
                element.addClass(FOCUS_CLASS);
                scope.$apply(function () {
                    ctrl.$focused = true;
                });
            }).bind('blur', function (evt) {
                element.removeClass(FOCUS_CLASS);
                scope.$apply(function () {
                    ctrl.$focused = false;
                });
            });
        }
    }
}]);


function GetRequest() {
    var url = location.href; //获取url中"?"符后的字串
    var theRequest = {};
    if (url.indexOf("?") != -1) {
        var str = url.split("?")[1];
        var strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}

import loginTpl from './share-login.tpl.html';

angular
    .module('pt')
    .directive('shareLogin', shareLogin);

shareLogin.$inject = ['$http', '$cookies', '$translate', 'dataMutualSrv', 'sessionContext'];
function shareLogin($http, $cookies, $translate, dataMutualSrv, sessionContext) {
    return {
        restrict: 'EA',
        template: loginTpl,
        link: link
    };

    function link(scope, element, attrs) {
        scope.login = {
            authError: false,
            emailErrorMessage: "",
            passwordErrorMessage: "",
            showBar: false,
            showSigninDiv: true,
            userEmail: '',
            userPassword: '',
            loadSetting: {}
        };
        scope.userCookieTime = true;

        if (window.localStorage && localStorage.getItem("ptnm")) {
            scope.login.userEmail = localStorage.getItem("ptnm");
        }

        //统一回车事件
        scope.enterKeyUp = function (e, fun, model) {
            var keycode = window.event ? e.keyCode : e.which;
            if (keycode == 13 && name != null && typeof(name) != 'undefined') {
                if (model) {
                    //对回车的支持
                    model['$focused'] = false;
                }
                fun();
            } else {
                if (model) {
                    model['$focused'] = true;
                }
            }
        };

        scope.community = false;
        scope.login = function () {
            scope.form.email.$dirty = true;
            scope.form.userPassword.$dirty = true;
            var user = {
                userEmail: scope.login.userEmail,
                userPassword: typeof(scope.login.userPassword) != 'undefined' ? md5(scope.login.userPassword) : null,
                rememberMe: !!scope.userCookieTime
            };
            if (!scope.login.userPassword || !scope.login.userEmail) {
                return;
            }

            $http({
                method: 'POST',
                url: LINK_SIGNIN_URL + "?community=" + scope.community,
                data: angular.toJson(user)
            })
            .success(function (data) {
                if (data.status == 'success') {

                    localStorage.setItem("ptnm", user.userEmail);
                    $cookies.put("ptId", data.content.uid);
                    $cookies.put("ptEmail", data.content.ptEmail);
                    localStorage.setItem("preRegistrationEmail", data.content.ptEmail);

                    if (data.content.type == "PRE_REGISTRATION_USER") {
                        if (scope.community == false) {
                            window.location.href = "/#/preRegistrationSuccess";
                        } else if (scope.community == true) {
                            scope.loginBBS();
                            window.location.href = "http://community.ptone.jp/session/sso";
                        }
                    }
                    else if (data.content.type == "OFFICIAL_USER") {
                        if (scope.community == false) {
                            //如果记住密码，则保存
                            if (scope.userCookieTime) {
                                localStorage.setItem("sid", data.content.sid);
                                localStorage.setItem("ptmm", user.userPassword);
                            }
                            sessionContext.saveSession(data.content.sid, 'share');
                        } else if (scope.community == true) {
                            scope.loginBBS();
                            window.location.href = "http://community.ptone.jp/session/sso";
                        }
                    }

                    scope.login.authError = false;
                    scope.closePopup('showLogin', 'success');
                }
                else if (data.status == 'failed') {
                    scope.login.authError = true;
                    if (data.message == "email_error") {
                        scope.login.emailErrorMessage = $translate.instant("LOGIN.ERROR_TIP.USER_NOT_RIGHT");
                    } else if (data.message == "password_error") {
                        scope.login.passwordErrorMessage = $translate.instant("LOGIN.ERROR_TIP.PASSWORD_NOT_RIGHT");
                    }
                    console.log('login failed!');
                }
                else if (data.status == 'error') {
                    scope.login.authError = true;
                    scope.login.errorMessage = $translate.instant("SYSTEM.SYSTEM_ERROR");
                    console.log(data.message);
                }
            })
            .error(function () {
                console.log('server error')
            });
        };

    }
}
