'use strict';

import tpl from './share-add.tpl.html';

function getRequest() {
    var url = location.href; //获取url中"?"符后的字串
    var theRequest = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.split("?")[1];
        var strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}

/**
 * Share Add Dashboard
 * 添加分享页面至空间
 *
 */
angular
    .module('pt')
    .directive('shareAdd', shareAddFunc);

shareAddFunc.$inject = ['$rootScope', '$translate', '$localStorage', 'dataMutualSrv', 'PanelResources'];

function shareAddFunc($rootScope, $translate, $localStorage, dataMutualSrv, PanelResources) {
    return {
        restrict: 'EA',
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {
        var dashboadId = getRequest()['id'];
        scope.sltSpace = null; //当前所选的空间
        scope.errorSpace = null; //添加至空间时,错误信息(空间不存在 || 面板分享已取消 || 空间列表为空)
        scope.errorType = null; //错误类型(spaceEmpty-空间列表为空 || spaceAddError-添加报错)

        /**
         * 检测此分享页面是否已存在于当前选中的空间内
         *
         */
        scope.isAlreadyExist = function() {

            PanelResources.existsSharePanel({
                    panelId: dashboadId,
                    spaceId: scope.sltSpace.spaceId
                })
                .then((data) => {
                    scope.myOptions.page.isAlreadyAdd = data;
                })
        };


        /**
         * 选择Space
         *
         */
        scope.selectSpace = function(space) {
            scope.sltSpace = space;
            scope.myOptions.page.isAlreadyAdd = null;

            //检测此分享页面是否已存在于当前选中的空间内
            scope.isAlreadyExist();
        };


        /**
         * 添加至空间后,直接跳转到指定的空间
         *
         */
        scope.goToSpace = function() {
            var spaceId = scope.sltSpace.spaceId;
            var ptId = $rootScope.userInfo.ptId;
            // var currentDashboard = $localStorage.currentDashboard;
            var currentDashboard = localStorage.getItem('currentDashboard') ? angular.fromJson(localStorage.getItem('currentDashboard')) : null;
            if (currentDashboard) {
                if (!currentDashboard[spaceId]) {
                    currentDashboard[spaceId] = {};
                }
            } else {
                currentDashboard = {};
                currentDashboard[spaceId] = {};
            }
            currentDashboard[spaceId][ptId] = 'fromShare' + scope.rootPanel.now.panelId;
            localStorage.setItem('currentDashboard', angular.toJson(currentDashboard));

            window.location.href = '/' + scope.sltSpace.domain + '/Dashboard';
        };


        /**
         * 将分享页添加到Space
         *
         */
        scope.addDashboadToSpace = function() {
            if (scope.myOptions.page.isAlreadyAdd) {
                scope.goToSpace();
            } else {

                var sendData = {
                    panelId: dashboadId,
                    spaceId: scope.sltSpace.spaceId,
                    component: {
                        value: scope.modal.dashboardTime.dateKey,
                        status: scope.modal.dashboardTime.dateKey == 'widgetTime' ? 0 : 1
                    }
                };

                PanelResources.addSharePanel(sendData)
                    .then((data) => {
                        if (data == 'success') {
                            scope.myOptions.page.isAlreadyAdd = true;
                            scope.myOptions.page.status = true;

                            //直接跳转到指定的空间
                            scope.goToSpace();
                        } else {
                            scope.errorType = 'spaceAddError';
                            scope.myOptions.page.status = data;

                            if (data == 'panelDelete') {
                                //分享的dashboard已删除(显示OPPS)

                                scope.errorSpace = $translate.instant("SHARE.ADD_DASHBOARD.TIPS_DASHBOARD_DELETE");
                            } else if (data == 'panelShareOff') {
                                //dashboard已取消分享(显示OPPS)

                                scope.errorSpace = $translate.instant("SHARE.ADD_DASHBOARD.TIPS_SHARE_OFF");
                            } else if (data == 'srcSpaceDelete') {
                                //分享的dashboard所属的space已删除(显示OPPS)

                                scope.errorSpace = $translate.instant("SHARE.ADD_DASHBOARD.TIPS_FORM_SPACE_DELETE");
                            } else if (data == 'targetSpaceDelete') {
                                //当前所选空间已删除

                                scope.errorSpace = $translate.instant("SHARE.ADD_DASHBOARD.TIPS_TO_SPACE_DELETE");
                            } else if (data == 'spaceNotIn') {
                                //当前账号不属于所选空间

                                scope.errorSpace = $translate.instant("SHARE.ADD_DASHBOARD.TIPS_TO_SPACE_DELETE");
                            } else if (data == 'panelExists') {
                                //所选空间中已存在当前分享页面

                                scope.goToSpace();
                            }
                        }
                    })
            }
        };


        /**
         * 跳转至创建空间页
         *
         */
        scope.goToCreateSpace = function() {
            window.location.href = '/create';
        };


        /**
         * Init
         *
         */
        (function() {
            if (scope.myOptions.space.list.length == 0) {
                scope.errorType = 'spaceEmpty';
                scope.errorSpace = $translate.instant("SHARE.ADD_DASHBOARD.TIPS_SPACE_EMPTY");
            } else {
                scope.sltSpace = angular.copy(scope.myOptions.space.list[0]);
                scope.selectSpace(scope.sltSpace);
            }
        })();
    }
}
