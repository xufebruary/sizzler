'use strict';



/**
 * dataSources
 * 
 */
angular
	.module('pt')
	.controller('userManagerCtrl', ['$scope', '$document', 'dataMutualSrv', 'uiLoadingSrv',userManagerCtrl]);

function userManagerCtrl($scope, $document, dataMutualSrv, uiLoadingSrv) {
    var body = $document.find('body').eq(0);
    $('.header').removeAttr('style');
    $scope.pt.settings.asideFolded = false;
    $scope.userEmail = "";
    $scope.userList = [];
    $scope.showList = true;
    $scope.showSettingUser = false;
    $scope.settingUser = {};
    $scope.settingUserAccessList = [];
    $scope.allAccessList = [];
    
    /*[
    	{"code":"0", "desc":"普通用户"},
    	{"code":"1", "desc":"模板管理员"},
    	{"code":"3", "desc":"Demo账户"},
    	{"code":"8", "desc":"超级管理员"},
    	{"code":"U", "desc":"Upload数据源"},
    	{"code":"A", "desc":"Ptengine数据源"},
    	{"code":"B", "desc":"Facebookad数据源"},
    	{"code":"J", "desc":"国际化文案修改管理员"},
    	{"code":"P", "desc":"预注册管理、用户管理"},
    	{"code":"D", "desc":"DoubleClick数据源"},
    	{"code":"F", "desc":"Salesforce数据源"},
    	{"code":"S", "desc":"S3数据源"},
    	{"code":"T", "desc":"Twitter数据源（暂未开放）"}
    ];*/
    
    /**
    	获取预注册用户列表
    */
    $scope.getUserList = function(userEmail){
    	if(userEmail == ""){
    		alert("请输入用户邮箱查询");
    		return;
    	}
    	uiLoadingSrv.createLoading(body);
    	dataMutualSrv.get(LINK_SYS_USER_LIST+"/"+userEmail).then(function(data) {
    		if(data.status == "success"){
    			$scope.userList = data.content;
    		}else{
    			
    		}
    		uiLoadingSrv.removeLoading(body);
        });
    }
    
    $scope.userUpdate = function(userId, status, success){
    	dataMutualSrv.get(LINK_SYS_USER_UPDATE+"?userId="+userId+"&status="+status).then(function(data) {
    		if(data.status == "success"){
    			success();
    		}else{
    			alert(data.message);
    			console.log("userUpadate error.");
    		}
        });
    }
    
    $scope.updateUser = function(user, status){
    	var success = function(){
    		user.status = status;
    	}
    	var tipMessage = status == 1 ? "Sure to restore this user?" : "Sure to delete this user?";
    	if(confirm(tipMessage)){
    		$scope.userUpdate(user.ptId, status, success);	
    	}
    }
    
    $scope.showPanel = function(user, type){
    	if(type == 1){
    		$scope.showList = false;
		    $scope.showSettingUser = true;
		    $scope.settingUser = user;
		    // get user access list 
		    dataMutualSrv.get(LINK_SYS_GET_USER_PERMISSION+user.ptId).then(function(data) {
	    		if(data.status == "success"){
	    			console.log(data.content);
	    			$scope.settingUserAccessList = data.content;
	    		}else{
	    			alert("get user permission list faild");
	    			console.log(data.message);
	    		}
	    		uiLoadingSrv.removeLoading(body);
	        });
    	}else{
    		$scope.showList = true;
		    $scope.showSettingUser = false;
		    $scope.settingUser = {};
		    $scope.settingUserAccessList = [];
    	}
    }

	$scope.userHasAccess = function(access){
		var settingUserAccessList = $scope.settingUserAccessList;
		var hasAccess = false;
		for(var i=0; i<settingUserAccessList.length; i++){
			if(access.roleId == settingUserAccessList[i].roleId){
				hasAccess = true;
				break;
			}	
		}
		return hasAccess;
	}
	
	$scope.saveAccess = function(userEmail){
		var userAccesses = $("input[type='checkbox'][name='accessCode']:checked");
		var userAccessList = [];
		for(var i=0; i<userAccesses.length; i++){
			userAccessList.push(userAccesses.eq(i).val());
		}
		var userAccess = userAccessList.join(",");
		uiLoadingSrv.createLoading(body);
		dataMutualSrv.get(LINK_SYS_USER_UPDATE_ACCESS+"/"+$scope.settingUser.ptId+"/"+userAccess).then(function(data) {
    		if(data.status == "success"){
    			alert("权限设置成功");
    			$scope.showPanel(null, 0);
    			$scope.getUserList(userEmail);
    		}else{
    			alert("权限设置失败");
    			console.log(data.message);
    		}
    		uiLoadingSrv.removeLoading(body);
        });
	}
	
	function dataInit(){
		//get all permission list		
		dataMutualSrv.get(LINK_SYS_GET_ALL_PERMISSION).then(function(data) {
    		if(data.status == "success"){
    			console.log(data.content);
    			 $scope.allAccessList = data.content;
    		}else{
    			alert("get all permission list faild");
    			console.log(data.message);
    		}
    		uiLoadingSrv.removeLoading(body);
        });
	}
	
	dataInit();
	
};
