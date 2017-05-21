'use strict';

import {
  LINK_USER_OPERATE_LOG
} from 'components/modules/common/common';


/**
 * siteEventAnalyticsSrv
 * 全站事件统计
 */

//angular
//    .module('pt')
//    .factory('siteEventAnalyticsSrv', ['dataMutualSrv', siteEventAnalyticsSrvFunc]);
siteEventAnalyticsSrvFactory.$inject = ['dataMutualSrv'];
function siteEventAnalyticsSrvFactory(dataMutualSrv) {
    return {
        //Ptone日志
        createData: function (data) {
            // dataMutualSrv.post(LINK_USER_OPERATE_LOG, data);
        },

        setGtmEvent: function(envent , elemet, value) {

        }   
    };
}

export default siteEventAnalyticsSrvFactory;
