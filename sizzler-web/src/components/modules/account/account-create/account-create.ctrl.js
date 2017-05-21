'use strict';


import ProductConfig from 'configs/product.config';
import {
    uuid,
    GetRequest,
    isAndroid,
    isIphone
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
    .controller('accountCreateCtrl', ['$scope', '$state', 'sessionContext', 'uiLoadingSrv', 'publicDataSrv', 'UserResources', accountCreateCtrl]);

function accountCreateCtrl($scope, $state, sessionContext, uiLoadingSrv, publicDataSrv, UserResources) {
    
    var vm = this;
    vm.showEmail = null;
    vm.user = {
		userEmail: null,
		userPassword: null,
		weekStart: ProductConfig.weekStart
	}
	vm.tips = {
		authSuccess: false,
		password: {
			isError: null,
			isFocused: false,
			errorCode: null
		},
		send: {
			isError: false,
			errorCode: null
		}
	}

    // =========

    //输入框获取焦点
    vm.iptIsFocused = iptIsFocused;

    //输入框失去焦点
    vm.iptIsBlur = iptIsBlur;

    //激活
    vm.activate = activate;


    //入口
    init();
    
    // =========

    //入口
    function init(){
    	vm.isPhone = isAndroid || isIphone;

    	var request = GetRequest();
    	if (request['e']) {
	        vm.showEmail = Base64.decode(decodeURIComponent(decodeURIComponent(request['e'])));
	        vm.user.userEmail = request['e'];
	    } else {
	        $state.go('signin');
	    }
    }

    //激活账户
    function activate() {
        if(dataIsPass()) {
        	uiLoadingSrv.createLoading('.inner');
        	var sendData = angular.copy(vm.user);
        	sendData.userPassword = md5(sendData.userPassword);

        	//激活账户，且返回空间信息
        	UserResources.activateUsers(sendData)
        	.then((data) => {
        		var sid = data.sid;
                var accountCreateData = {
                    spaceId: data.spaceId,
                    spaceDomain: data.spaceDomain
                };
                sessionContext.saveSession(sid, 'activate');
                publicDataSrv.setPublicData('accountCreate', accountCreateData);
                vm.tips.send.isError = false;
        	},(error) => {
        		vm.tips.send.isError = true;
                vm.tips.send.errorCode = error.errorCode && error.errorCode == "USER_ACTIVE_REDIS_KEY_VALIDATE" ? "DATADECK_SET_PASSWORD.UPDATA_FAILED" : "SYSTEM.SYSTEM_ERROR";
        	})
        	.finally(() => {
        		uiLoadingSrv.removeLoading('.inner');
        	})
        }
    }

    //校验数据
    function dataIsPass(){
    	var password = vm.user.userPassword;
    	var isPass = true;
    	if(!password){
    		vm.tips.password.isError = true;
    		vm.tips.password.errorCode = "LOGIN.ERROR_TIP.PASSWORD_NULL";
    		isPass = false;
    	}
    	else if(password.length<6 || password.length>20){
    		vm.tips.password.isError = true;
    		vm.tips.password.errorCode = "LOGIN.ERROR_TIP.PASSWORD_LENGTH";
    		isPass = false;
    	}
    	
    	if(vm.isPhone && !isPass) {
    		$('.ipt-login').removeAttr('style').eq(0).css('box-shadow', 'inset 0 0 0 1px #ef4f4b');
    	}
    	else {
    		$('.ipt-login').removeAttr('style');
    	}
    	return isPass;
    }

    //输入框获取焦点
    function iptIsFocused(type){
    	$scope.$apply(function(){
            //只有当用户点击按钮进行过校验后再做焦点移除判断
    		if(vm.tips[type].isError !== null) vm.tips[type].isError = false;
    		vm.tips[type].isFocused = true;
    	})
    }

    //输入框失去焦点
    function iptIsBlur(type){
        //只有当用户点击按钮进行过校验后再做焦点移除判断
        if(vm.tips.password.isError !== null) dataIsPass();
    	$scope.$apply(function(){
	    	vm.tips[type].isFocused = false;
	    })
    }
}
