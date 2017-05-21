'use strict';

import ProductConfig from 'configs/product.config';
import {
  LINK_SIGNUP_URL,
  loginKey,
  loginSvg,
  getLocalLang,
  isDomain,
  uuid
} from '../common/common';

import md5 from 'js-md5';
import utils from 'utils/utils';

/**
 * signup
 * 注册
 *
 */
angular
    .module('pt')
    .controller('signupCtrl', ['$scope', '$http', '$translate', 'sessionContext', 'siteEventAnalyticsSrv',signupCtrlFunc]);

function signupCtrlFunc($scope, $http, $translate, sessionContext, siteEventAnalyticsSrv) {

    $scope.myOptions = {
        bgPoints: loginSvg(),
        user: {
            weekStart: ProductConfig.weekStart,
            source: utils.getSourceByInternalLocation(),
            locale: getLocalLang().locale
        },
        authError: false,
        errorMessage:  "",
        loadSetting: {},
        treatyHref: null
    };


    if(isDomain('ptone.jp') || isDomain('ptone.com.cn') || isDomain('ptone.cn')){
        $scope.myOptions.treatyHref = 'http://support.ptone.jp/treaty';
    } else if(isDomain('datadeck.com')) {
        $scope.myOptions.treatyHref = 'https://www.datadeck.com/term-of-use';
    } else if(isDomain('datadeck.jp')){
        $scope.myOptions.treatyHref = 'https://www.datadeck.jp/term-of-use';
    } else if(isDomain('datadeck.cn')){
        $scope.myOptions.treatyHref = 'https://www.datadeck.cn/term-of-use';
    }

    sessionContext.removeSession();

    $scope.signup = function () {
        $scope.form.email.$dirty = true;
        $scope.form.userPassword.$dirty = true;
        if (!$scope.myOptions.user.userEmail || !$scope.userPassword) {
            return;
        }
        $scope.myOptions.user.userPassword = md5($scope.userPassword);
        $scope.myOptions.loadSetting.signin = true;
        $http({
            method: 'POST',
            url: LINK_SIGNUP_URL,
            data: angular.toJson($scope.myOptions.user)
        }).success(function (data, status, headers, config) {
            if (data.status == 'success') {
                sessionContext.saveSession(data.content.sid, 'signup');

                if (window.localStorage) {
                    localStorage.setItem("ptnm", $scope.myOptions.user.userEmail);
                }

                //全站事件统计
                // siteEventAnalyticsSrv.createData({
                //     uid: data.content.uid,
                //     time: new Date().getTime(),
                //     operate: 'register-btn',
                //     operateId: uuid(),
                //     position: 'register',
                //     content: JSON.stringify({
                //         'email': $scope.myOptions.user.userEmail,
                //         'uid': data.content.uid,
                //         'locale': $scope.myOptions.user.locale,
                //         'week-start': $scope.myOptions.user.weekStart
                //     })
                // });
            } else if (data.status == 'failed') {
                $scope.myOptions.authError = true;
                $scope.myOptions.errorMessage = $translate.instant(data.message);
            } else if (data.status == 'error') {
                $scope.myOptions.authError = true;
                $scope.myOptions.errorMessage = $translate.instant('LOGIN.SIGNUP') + $translate.instant('COMMON.ERROR');
                console.log(data);
            }
            $scope.myOptions.loadSetting.signin = false;
        }).error(function (data, status, headers, config) {
            $scope.myOptions.authError = false;
            $scope.myOptions.loadSetting.signin = false;
            console.log('server error')
        });
    };

    window.onresize = function () {
        $scope.myOptions.bgPoints = loginSvg();
    }
}

