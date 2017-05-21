'use strict';

import {
  isUrlContain,
  isAndroid,
  isIphone,
  isDomain,
  LINK_PERMISSION_SYS,
  LINK_SIGNIN_URL
} from 'components/modules/common/common';
import Favico from 'assets/libs/jquery/favico';
import ProductConfig from 'configs/product.config';

import cookieUtils from 'utils/cookie.utils';

/**
 * body Controllers
 * 总控制器(最顶级)
 *
 */

angular
    .module('pt')
    .controller('bodyCtrl', ['$scope', '$http', '$rootScope', '$state', '$location', 'UserResources', 'sessionContext', 'publicDataSrv', 'sysRoles','$translate','$timeout', bodyCtrlFunc]);

function bodyCtrlFunc($scope, $http, $rootScope, $state, $location, UserResources, sessionContext, publicDataSrv, sysRoles,$translate,$timeout) {
    console.log('body controller execute...')

	// config
    $scope.pt = {
        name: 'Data deck',
        version: '1.0.0',
        settings: {
            asideFixed: false,
            asideFolded: false,
            headFolded: false,  //头部收起
            asideFoldAll: false,//侧边栏完全收起
            editModel: false,   //是否为编辑模式
            fullScreen: false,  //是否全屏模式
            isPhone: isAndroid || isIphone //是否为移动设备（手机）
        },
        loadFinish: {
            body: false,
            aside: false,
            widgetList: false,
			bodyTimeout: false //登陆进来以后，加载各种公用资源时超时提示
        }
    };

    $scope.isDomain = function(domain){
        return isDomain(domain);
    };

    $scope.changeLogo = function () {
        var favicon = new Favico();
        var image;
        // if (sysRoles.hasSysRole('cig-media-smart')) {
        //     window.document.title = '新意互动 | CIG';
        //     image = document.getElementById('cig');
        // } else {
        //     window.document.title = ProductConfig.title;
        //     image = document.getElementById(ProductConfig.favicon);
        // }
        // window.document.title = ProductConfig.title;
        // favicon.image(image);
        window.document.title = "";
    };

    $scope.changeLogo();

  //   //如果session过期则跳转到登陆页
  //   $rootScope.$on('userIntercepted', function (event, data) {
  //   	console.log('go to signin page ...')
		// UserResources.clear(); // 清除本地存储
		// var redirectUrl = encodeURIComponent(location.href);
		// $state.go('signin', {redirectUrl: redirectUrl}); // 跳转到登录页面
  //   });

    //统一回车事件
    $scope.enterKeyUp = function (e, fun, model) {
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

    //路由监控
    $rootScope.$on('$stateChangeStart', function (event, to, toParams, from, fromParams) {

        if (to.name != 'signin'
            && to.name != 'signup'
            && to.name != 'signupOfficial'
            && to.name != '404'
			&& to.name != 'home'
            && to.name != 'forgot'
            && to.name != 'resetPassword'
            && to.name != 'accountCreate'
			&& to.name != 'dashboardNoPermission'
            && to.name != 'preRegistrationSuccess'
            && to.name != 'invites'
            && to.name != 'landingPage') {

            // 如果用户不存在(用户刷新情况下)
            if (!$rootScope.sid) {
                //获取session, 并阻止默认跳转


                //存储公共数据
                var enterData = {
                    type: 'refresh',
                    path: to.name,
                    permission: to.permission,
                    spaceDomain: toParams.spaceDomain,
					panelId: toParams.panelId,
					redirectUrl: toParams.redirectUrl
                };
                publicDataSrv.setPublicData('enter', enterData);

                sessionContext.getSession('refresh');
                event.preventDefault();
            }
        }
    });
    $rootScope.$on('$stateChangeSuccess', function (event, to, toParams, from, fromParams) {

        //首次进入ptone的时候需要在等待页面渲染之前展示一个loading
        //$scope.bodyFirstLoaded = false;
        if (['signin', 'signup', 'signupOfficial', 'forgot', 'resetPassword', 'accountCreate', 'preRegistrationSuccess', 'invites', 'landingPage', 'ptengineSignin'].indexOf(to.name) >= 0) {
            $scope.hideLoading('body');
        }

        if (to.name != 'pt.dashboard') {
            angular.element('body').removeClass('modal-open');
            angular.element(window).scrollTop(0);
            angular.element('.pt-header').css('top', '0');

            //退出(全屏||编辑)模式
            $scope.pt.settings.headFolded = false;
            $scope.pt.settings.fullScreen = false;
        }
        console.log('state ' + to.name + ' Change Success');

        // 改变 页面的标题
        //$window.document.title = 'PtOne | '+(to.data.title||'');


        //URL Add UID
        if ($rootScope.userInfo && $rootScope.userInfo.ptId && to.name != 'signin') {
            $location.search('UID', $rootScope.userInfo.ptId);
        }

    });
    $rootScope.$on('$stateChangeError', function (event, to, toParams, from, fromParams) {
        console.log('from '+from.name+' state ' + to.name + ' Change Error');
    });
    $rootScope.$on('$viewContentLoading', function (event, viewConfig) {

    });

    // andy add 2016-01-14
    $scope.loginBBS = function () {
        setCookie("ptSid", cookieUtils.get('sid'));
    };

    //方法级角色判断(顶级scope)
    $scope.hasSysRolesFun = function (role) {
        return sysRoles.hasSysRole(role);
    };

    /**
     * hideLoading
     */
    $scope.hideLoading = function (type) {
        angular.element('[data-loading-area=\"'+type+'\"]').fadeOut(function(){
            // angular.element(this).remove();

            if(type == 'body'){
                angular.element('[data-loading-area="aside"], [data-loading-area="widgetList"]').removeClass('hide');
            };
			});
    };
}

//服务级权限判断
angular.module('pt')
    .factory('permissions', ['$rootScope', function ($rootScope) {
        var permissionList;
        return {
            setPermissions: function (permissions) {
                permissionList = permissions;
                $rootScope.$broadcast('permissionsChanged')
            },
            getPermissions: function (sid) {
                $.get(LINK_PERMISSION_SYS + "?sid=" + sid, function (permissionData) {
                    permissionList = permissionData.content;
                    $rootScope.$broadcast('permissionsChanged')
                });
            },
            hasPermission: function (permission) {
                var permissionFlag = false;
                permission = permission.trim();

                if (permissionList) {
                    $.each(permissionList, function (i, item) {
                        if (item.code.trim() === permission) {
                            permissionFlag = true;
                        }
                    });
                }
                return permissionFlag;
            }
        };
    }
    ]);

//指令级权限判断
angular.module('pt').directive('hasPermission', ['permissions', function (permissions) {
    return {
        link: function (scope, element, attrs) {

            var value = attrs.hasPermission.trim();
            var notPermissionFlag = value[0] === '!';
            if (notPermissionFlag) {
                value = value.slice(1).trim();
            }

            function toggleVisibilityBasedOnPermission() {
                var hasPermission = permissions.hasPermission(value);

                if (hasPermission && !notPermissionFlag || !hasPermission && notPermissionFlag)
                    element.show();
                else
                    element.hide();
            }

            toggleVisibilityBasedOnPermission();
            scope.$on('permissionsChanged', toggleVisibilityBasedOnPermission);
        }
    };
}]);

//服务级角色判断 (分享页面同步)
angular.module('pt')
    .factory('sysRoles', ['$rootScope', function ($rootScope) {
        var sysRolesList;
        return {
            setSysRoles: function (sysRoles) {
                sysRolesList = sysRoles;
                $rootScope.$broadcast('sysRoleChanged')
            },
            hasSysRole: function (role) {
                var roleFlag = false;
                role = role.trim();
                if (sysRolesList) {
                    $.each(sysRolesList, function (i, item) {
                        if (item.code.trim() === role) {
                            roleFlag = true;
                        }
                    });
                }
                return roleFlag;
            }
        };
    }]);


//指令级角色判断 (分享页面同步)
angular.module('pt').directive('hasSysRole', ['sysRoles', function (sysRoles) {
    return {
        link: function (scope, element, attrs) {

            var value = attrs.hasSysRole.trim();
            var notRoleFlag = value[0] === '!';
            if (notRoleFlag) {
                value = value.slice(1).trim();
            }

            function toggleVisibilityBasedOnRole() {
                var hasSysRole = sysRoles.hasSysRole(value);
                var relation = hasSysRole && !notRoleFlag || !hasSysRole && notRoleFlag;
                if (relation)
                    element.show();
                else
                    element.hide();
            }

            toggleVisibilityBasedOnRole();
            scope.$on('sysRoleChanged', toggleVisibilityBasedOnRole);
        }
    };
}]);


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
