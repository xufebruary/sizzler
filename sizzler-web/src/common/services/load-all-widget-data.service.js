'use strict';

/**
 * panelSltSrv
 * Panel点击事件
 *
 */

import {
    LINK_BATCH_WIDGET_DATA
} from '../../components/modules/common/common';

//angular
//    .module('pt')
//    .service('panelSltSrv', ['$rootScope','$location', '$stateParams', '$state', 'dataMutualSrv', 'websocket', 'publicDataSrv', 'datasourceFactory', 'loadAllWidgetData', panelSltSrv])
//    .service('loadAllWidgetData', ['dataMutualSrv', 'publicDataSrv', loadAllWidgetData]);



/**
 * 通知后台加载全部widget数据
 *
 */
loadAllWidgetData.$inject = ['dataMutualSrv'];
function loadAllWidgetData(dataMutualSrv) {

    return {
        loadAll: function(dashboardId, socketId, backFunc) {
            dataMutualSrv.get(LINK_BATCH_WIDGET_DATA + '/' + dashboardId + '/' + socketId + '/loadAll').then(function(data) {
                backFunc();
            });
        }
    }
}

export default loadAllWidgetData;
