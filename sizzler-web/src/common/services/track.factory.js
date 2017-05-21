'use strict';

import {
	LINK_USER_OPERATE_LOG
} from 'components/modules/common/common';

//angular
//	.module('pt')
//	.factory('Track', ['dataMutualSrv', '$rootScope', siteEventAnalyticsSrvFunc]);
siteEventAnalyticsSrvFunc.$inject = ['dataMutualSrv', '$rootScope'];
function siteEventAnalyticsSrvFunc(dataMutualSrv, $rootScope) {
	return {
		log: function (data) {
			console.log('jianqing')
			var defaults = {how: 'click'};
			if($rootScope.userInfo){
				defaults.uid = $rootScope.userInfo.ptId;
			}
			var postData = Object.assign(defaults, data);
			dataMutualSrv.post(LINK_USER_OPERATE_LOG, postData);
		}
	};
}

export default siteEventAnalyticsSrvFunc;
