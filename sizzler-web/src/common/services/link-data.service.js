'use strict';

import {
  LINK_GET_DS_INFO
} from 'components/modules/common/common';

/**
 * linkDataSrv
 * 批量授权
 *
 */
//angular
//    .module('pt')
//    .service('linkDataSrv', ['sysRoles', 'dataMutualSrv', linkDataSrv]);
linkDataSrv.$inject = ['sysRoles', 'dataMutualSrv'];
function linkDataSrv(sysRoles, dataMutualSrv) {

    this.update = function (scope) {
        var flag = false;
        scope.rootWidget.linkData.dsList = [];
        for (var i = 0; i < scope.rootWidget.list.length; i++) {
            if (['custom', 'tool'].indexOf(scope.rootWidget.list[i].baseWidget.widgetType) < 0 && scope.rootWidget.list[i].baseWidget.graphName != 'text') {
                if (scope.rootWidget.list[i].baseWidget.isExample == 1) {
                    flag = true;

                    var dsId = scope.rootWidget.list[i].variables[0].ptoneDsInfoId;
                    var ds = null;

                    for (var j = 0; j < scope.rootCommon.dsList.length; j++) {
                        if (scope.rootCommon.dsList[j].id == dsId) {
                            ds = scope.rootCommon.dsList[j];
                            break;
                        }
                    }

                    if(ds === null){
                        //没有数据源权限时,需请求单个数据源具体信息,以便在批量授权黄条处展现

                        dataMutualSrv.get(LINK_GET_DS_INFO + scope.rootWidget.list[i].variables[0].dsCode).then(function (data) {
                            if (data.status == 'success') {
                                _addDs(angular.copy(data.content));
                            } else {
                                console.log('link data get ds info Failed');

                                ds = {
                                    code: scope.rootWidget.list[i].variables[0].dsCode,
                                    id: scope.rootWidget.list[i].variables[0].ptoneDsInfoId,
                                    name: scope.rootWidget.list[i].variables[0].dsCode,
                                    type: 'noPermission'
                                };
                                scope.rootWidget.linkData.dsList.push(ds);
                            }
                        })
                    } else if (scope.rootWidget.linkData.dsList.indexOf(ds) < 0) {
                        scope.rootWidget.linkData.dsList.push(ds)
                    }
                }
            }
        }
        scope.rootWidget.linkData.showTips = sysRoles.hasSysRole("ptone-admin-user") ? false : flag;

        //当批量授权完成，且当前滚动条有向下滚动时.
        if (!flag && angular.element('.content-fixed').length > 0 && parseInt(angular.element('.content-hd').css('top')) >= 0) {
            angular.element('.content-hd').css('top', '0px');
        }

        //获取单个数据源信息
        function _addDs(dsInfo){
            dsInfo.type = 'noPermission';

            var flag = true;
            for(var i=0; i<scope.rootWidget.linkData.dsList.length; i++){
                if(scope.rootWidget.linkData.dsList[i].code == dsInfo.code){
                    flag = false;
                    break;
                }
            }
            if (flag) {
                scope.rootWidget.linkData.dsList.push(dsInfo)
            }
        }
    }
}

export default linkDataSrv;
