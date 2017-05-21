'use strict';



/**
 * dataSources
 * 
 */
angular
    .module('pt')
    .controller('preregisterCtrl', ['$scope', '$document', 'dataMutualSrv',preregisterCtrl]);

function preregisterCtrl($scope, $document, dataMutualSrv) {
    var body = $document.find('body').eq(0);
    $('.header').removeAttr('style');
    $scope.pt.settings.asideFolded = false;
    
    $scope.userEmail = "";
    $scope.preUserList = [];

	$scope.update = {
		newPassword:null
	};


    
    /**
    	获取预注册用户列表
    */
    $scope.getPreUserList = function(){
    	dataMutualSrv.get(LINK_SYS_PRE_USER_LIST).then(function(data) {
    		if(data.status == "success"){
    			$scope.preUserList = data.content;
    		}else{
    			
    		}
        });
    }
    
    $scope.preUserUpdate = function(userIds, success){
    	dataMutualSrv.get(LINK_SYS_PRE_USER_UPDATE+"?userIds="+userIds).then(function(data) {
    		if(data.status == "success"){
    			//success();
    		}else{

    		}
        });
    }

	$scope.preUserUpdatePassword = function(userId){
		dataMutualSrv.get(LINK_SYS_PRE_USER_UPDATE_PASSWORD + "?userId=" + userId + "&password=" + $.md5($scope.update.newPassword)).then(function(data) {
			if(data.status == "success"){
				alert("reset password success.");
			}else{
				alert("reset password error.");
			}
		});
	}
    
    $scope.activityList = function(){
    	var preUserList = $("input[type='checkbox'][name='preUserIds']:checked");
    	if(preUserList.length == 0){
    		alert("Please select the user needs to be activated");
    	}else{
    		if(confirm("You've selected "+preUserList.length+" users. Are you sure to activate these users?")){
    			var _preUserIds = "";
    			for(var i=0; i<preUserList.length; i++){
    				var _checkBox = preUserList.eq(i);
    				var _userId = _checkBox.val();
    				_preUserIds += _userId;
    				if(i < preUserList.length-1){
    					_preUserIds += ",";
    				}
    			}
    			var success = function(){
    				$scope.getPreUserList();
    			}
    			$scope.preUserUpdate(_preUserIds, success);
    			console.log(_preUserIds);
    		}
    	}
    	console.log(preUserList);
    }
    
    $scope.activity = function(user){
    	var success = function(){
    		$scope.getPreUserList();
    	}
    	if(confirm("Sure to activate this user?")){
    		$scope.preUserUpdate(user.ptId, success);	
    	}
    }


	function init(){
		$scope.getPreUserList();
	}
	
	init();
    

    //数据源参数配置
    var myOptions = $scope.myOptions = {
        status: false,
        tipsShow: false
    };


    //Refresh Cache
    $scope.refreshCache = function() {
        dataMutualSrv.post(LINK_SYS_REFRESH_CACHE, '').then(function(data) {
            $scope.myOptions.status = data.status;
            $scope.myOptions.tipsShow = true;

            //两个条件都满足显示提示语
            $('#refresh_cache').show();
            setTimeout(function(){
                $scope.$apply(function(){
                    $scope.myOptions.tipsShow = false;
                });
            },2000);
        });
    };

};
