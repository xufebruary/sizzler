'use strict';


import ProductConfig from 'configs/product.config';
import {
	uuid,
	GetRequest,
	isAndroid,
	isIphone,
	LINK_USER_RESET_PASSWORD_URL,
	LINK_SPACE_DOMAIN_CHECK
} from 'components/modules/common/common';

import md5 from 'js-md5';

var Base64 = require('js-base64').Base64;

/**
 * reset password
 * 密码重置
 *
 */
angular
    .module('pt')
    .controller('datadeckResetPwdCtrl', ['$scope', '$http', '$translate', '$state', 'sessionContext', 'uiLoadingSrv', 'dataMutualSrv', resetPwdCtrlFunc]);

function resetPwdCtrlFunc($scope, $http, $translate, $state, sessionContext, uiLoadingSrv, dataMutualSrv) {
    $scope.user = {};
    $scope.authSuccess = false;
    $scope.showForm = true;
    $scope.spaceHost = window.location.host + '/';
    var request = GetRequest();
    $scope.spaceId = uuid();
	//是否为移动设备（手机）
	$scope.isPhone = isAndroid || isIphone;
    if (request['e']) {
        $scope.userEmail = Base64.decode(decodeURIComponent(decodeURIComponent(request['e'])));
    } else {
        $state.go('signin');
    }
    $scope.resetPassword = function () {
        $scope.form.newPassword.$dirty = true;
        $scope.form.spaceDomain.$dirty = true;
        $scope.form.spaceName.$dirty = true;
        if ($scope.form.newPassword.$valid && $scope.form.spaceDomain.$valid && $scope.form.spaceName.$valid) {
            uiLoadingSrv.createLoading('body');
            $scope.user.userPassword = md5($scope.newPassword);
            $scope.user.userEmail = request['e'];
            $scope.user.domain = $scope.spaceDomain;
            $scope.user.name = $scope.spaceName;
            $scope.user.spaceId = $scope.spaceId;
            $scope.user.weekStart = ProductConfig.weekStart;
            $http({
                method: 'POST',
                url: LINK_USER_RESET_PASSWORD_URL + "?type=datadeck",
                data: angular.toJson($scope.user)
            }).success(function (data, status, headers, config) {
                if (data.status == 'success') {
                    // $scope.errorMessage = $translate.instant('FORGOT_PASSWORD.RESET_PASSWORD.TIP_3');
                    sessionContext.saveSession(data.content.sid, 'signin');
                } else if (data.status == 'failed') {
                    if (data.message == "space_invalidate") {
                        $scope.form.spaceDomain.$error.unique = true;
                    } else {
                        $scope.authSuccess = true;
                        $scope.errorMessage = $translate.instant(data.message);
                    }
                } else if (data.status == 'error') {
                    $scope.authSuccess = true;
                    $scope.errorMessage = $translate.instant("SYSTEM.SYSTEM_ERROR");
                }
                uiLoadingSrv.removeLoading('body');
            }).error(function (data, status, headers, config) {
                uiLoadingSrv.removeLoading('body');
                console.log('server error')
            });
        }
    };

    /**
     * verifyEvent
     * 校验
     */
    $scope.verifyEvent = function (type, eventType) {
        if (type == 'name') {
            if (!$scope.spaceName) {
                if ($scope.form.spaceDomain.$valid) {
                    $scope.spaceName = $scope.spaceDomain;
                }
            }
        }
    };

}


angular
    .module('pt')
    .directive('domainUnique', ['dataMutualSrv', domainUniqueFunc]);

function domainUniqueFunc(dataMutualSrv) {
    return {
        require: 'ngModel',
        priority: 0,
        link: function (scope, ele, attrs, c) {
            ele.bind('blur', function () {
                c.$setValidity('unique', true);
                if (scope.spaceDomain && !scope.form.spaceDomain.$error.pattern) {
                    dataMutualSrv.get(LINK_SPACE_DOMAIN_CHECK + scope.spaceId + '/' + scope.spaceDomain).then(function (data) {
                        if (data.status == 'success') {
                            if (data.content) {
                                c.$setValidity('unique', true);
                                if (!scope.spaceName) {
                                    scope.spaceName = scope.spaceDomain;
                                }
                            } else {
                                c.$setValidity('unique', false);
                            }
                        } else {
                            c.$setValidity('unique', false);
                            if (data.status == 'failed') {
                                console.log('Post Data Failed!')
                            } else if (data.status == 'error') {
                                console.log('Post Data Error: ');
                                console.log(data.message)
                            }
                        }
                    })
                }
            });
        }
    }
}
