'use strict';
import cookieUtils from 'utils/cookie.utils';

/**
 * 数据拦截器
 *
 */
angular
    .module('pt')
    .factory('UserInterceptor', ['$q', '$rootScope', UserInterceptorFunc]);

function UserInterceptorFunc($q, $rootScope) {
    return {
        request: function (config) {
        	var userInfo = $rootScope.userInfo;
            var sid = cookieUtils.get("sid");
            // if(sid) config.headers.Token = sid;
			if(userInfo && userInfo.ptId !== undefined){
                // config.headers.PtId = userInfo.ptId;
			}
            return config;
        },
        response: function (response) {
            return response;
        },
        responseError: function (response) {
            var data = response.data;
            // 判断错误码，如果是未登录
            if (response.config.url.indexOf('/pt/logs/process') == -1
                && response.config.url.indexOf('/pt/space/checkDomain/') == -1
                && response.config.url.indexOf('/pt/users/password/check') == -1
                && response.config.url.indexOf('/pt/users/signup/') == -1) {
                // 以下事件不需要登录
                // 全站事件统计 || 邀请链接域名校验 || 登录密码校验 || 注册

                if (!data) {
                    //angular.element('#sysTips').removeClass('hide');
                    //alert("A network error occurred. Please try again");
                }
                // sid已经失效
                if (data == "noSession") {
                    // 清除本地存储,跳转到登陆页
					$rootScope.$emit('userIntercepted');
                }
                if (data == "noPermission") {
                    alert("no permission");
                }
                if (data == "noQuantityPermission") {
                    //alert("no QuantityPermission");
                }
                if (data == "errorVersion") {
                    alert("error version");
                    $rootScope.$emit("userIntercepted", "errorVersion");
                }
            }
            return $q.reject(response);
        }
    };
}
