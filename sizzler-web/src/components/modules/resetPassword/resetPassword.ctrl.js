'use strict';

import {
	GetRequest,
	loginSvg,
	LINK_USER_RESET_PASSWORD_URL,
} from 'components/modules/common/common';

import md5 from 'js-md5';

/**
 * reset password
 * 密码重置
 *
 */
angular
    .module('pt')
    .controller('resetPwdCtrl', ['$scope', '$http', '$translate', '$state',resetPwdCtrlFunc]);

function resetPwdCtrlFunc($scope, $http, $translate, $state) {
    $scope.user = {};
    $scope.authSuccess = false;
    $scope.showForm = true;
    var request = GetRequest();
    if (request['e']) {
        $scope.user.userEmail = request['e'];
    } else {
        $state.go('signin');
    }
    $scope.resetPassword = function () {
        $scope.form.newPassword.$dirty = true;
        if ($scope.form.newPassword.$valid) {
            $scope.form.confirmPassword.$dirty = true;
        }
        if ($scope.form.newPassword.$valid && $scope.form.confirmPassword.$valid) {
            $scope.user.userPassword = $scope.newPassword;
            $scope.user.userPassword = md5($scope.user.userPassword);
            $http({
                method: 'POST',
                url: LINK_USER_RESET_PASSWORD_URL,
                data: angular.toJson($scope.user)
            }).success(function (data, status, headers, config) {
                if (data.status == 'success') {
                    $scope.showForm = false;
                    $scope.errorMessage = $translate.instant('FORGOT_PASSWORD.RESET_PASSWORD.TIP_3');
                    setTimeout(function () {
                        $state.go('signin');
                    }, 5000);
                } else if (data.status == 'failed') {
                    $scope.errorMessage = $translate.instant(data.message);
                } else if (data.status == 'error') {
                    $scope.errorMessage = $translate.instant("SYSTEM.SYSTEM_ERROR");
                }
                $scope.authSuccess = true;
            }).error(function (data, status, headers, config) {
                console.log('server error')
            });
        }
    };

    var loginData = $scope.loginData = {
        points: loginSvg()
    };

    window.onresize = function () {
        loginData.points = loginSvg();
    }
}
