'use strict';

import {
    LINK_USER_FORGOT_URL,
    uuid,
    loginSvg
} from 'components/modules/common/common';
var Base64 = require('js-base64').Base64;


forgotPwdCtrlFunc.$inject = ['$scope', '$http', '$translate', 'siteEventAnalyticsSrv'];

export default function forgotPwdCtrlFunc($scope, $http, $translate, siteEventAnalyticsSrv) {
    $scope.authError = false;
    $scope.authSuccess = false;
    $scope.errorMessage = "";
    $scope.successMessage = "";
    $scope.isSubmit = false;
    $scope.sendEmailByPassword = function () {
        $scope.errorMessage = "";
        $scope.form.email.$dirty = true;
        if ($scope.email) {
            $scope.isSubmit = true;
            $scope.successMessage = "";
            $http({
                method: 'POST',
                url: LINK_USER_FORGOT_URL + $scope.email + '/'+ Base64.encode(window.location.host)
            }).success(function (data, status, headers, config) {
                if (data.status == 'success') {
                    $scope.successMessage = $translate.instant(data.content);
                    $scope.authSuccess = true;
                    $("form")[0].reset();

                    //全站事件统计
                    siteEventAnalyticsSrv.createData({
                        uid: data,
                        time: new Date().getTime(),
                        position: 'panel',
                        operate: 'panel-copy-save-input-name',
                        operateId: uuid,
                        content: JSON.stringify({
                            'email': $scope.email
                        })
                    });
                } else if (data.status == 'failed') {
                    $scope.errorMessage = $translate.instant(data.message);
                    $scope.authError = true;
                    console.log('send failed!')
                } else if (data.status == 'error') {
                    $scope.errorMessage = $translate.instant("SYSTEM.SYSTEM_ERROR");
                    $scope.authError = true;
                    console.log(data.message)
                }
                $scope.isSubmit = false;
            }).error(function (data, status, headers, config) {
                console.log('server error')
                $scope.isSubmit = false;
            });
        }
    };

    var loginData = $scope.loginData = {
        points: loginSvg
    };

    window.onresize = function () {
        loginData.points = loginSvg;
    }
}
