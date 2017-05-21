'use strict';



/**
 * dataSources
 * 
 */
angular
	.module('pt')
	.controller('permissionManagerCtrl', ['$scope', '$document', 'dataMutualSrv', 'uiLoadingSrv',permissionManagerCtrl]);

function permissionManagerCtrl($scope, $document, dataMutualSrv, uiLoadingSrv) {
	var body = $document.find('body').eq(0);
    $('.header').removeAttr('style');

	$scope.permission = {
		operationList:{},
		resourceList:{},
		permissionList:{},
		roleList:{},
		rolePermissionList:{},
		select_resource:{},
		select_operation:{},
		currentRole:null,
		showRoleDetail:false,
		operation:{
			name:null,
			code:null,
			status:1
		},
		resource:{
			name:null,
			code:null,
			link:null,
			space_id:'ptone',
			status:1
		},
		role:{
			name:null,
			code:null,
			description:null,
			space_id:'ptone',
			status:1
		},
		permission:{
			name:null,
			code:null,
			url:null,
			description:null,
			status:1,
			resourceId:null,
			operationId:null,
			space_id:'ptone'
		}
	}

	$scope.getOperationList = function(){
		uiLoadingSrv.createLoading(body);
		dataMutualSrv.get(LINK_SYS_OPERATION).then(function(data) {
			if(data.status == "success"){
				$scope.permission.operationList = data.content;
			}
			uiLoadingSrv.removeLoading(body);
		});
	}

	$scope.updateOperation = function(operation) {
		dataMutualSrv.post(LINK_SYS_OPERATION_UPDATE, operation).then(function(data) {
			if(data.status == "success"){
				alert(data.message);
			}
		});
	};

	$scope.addOperation = function(){
		if(!$scope.permission.operation.name || !$scope.permission.operation.code){
			return;
		}
		dataMutualSrv.post(LINK_SYS_OPERATION_ADD, $scope.permission.operation).then(function(data) {
			if(data.status == "success"){
				// $scope.permission.operationList.unshift(angular.copy($scope.permission.operation));
				$scope.permission.operation = null;
				$scope.dataInit();
				// alert(data.message);
			}
		});
	}

	$scope.getResourceList = function(){
		uiLoadingSrv.createLoading(body);
		dataMutualSrv.get(LINK_SYS_RESOURCE).then(function(data) {
			if(data.status == "success"){
				$scope.permission.resourceList = data.content;
			}
			uiLoadingSrv.removeLoading(body);
		});
	}

	$scope.updateResource = function(resource) {
		dataMutualSrv.post(LINK_SYS_RESOURCE_UPDATE, resource).then(function(data) {
			if(data.status == "success"){
				alert(data.message);
			}
		});
	};

	$scope.addResource = function(){
		if(!$scope.permission.resource.name || !$scope.permission.resource.code){
			return;
		}
		dataMutualSrv.post(LINK_SYS_RESOURCE_ADD, $scope.permission.resource).then(function(data) {
			if(data.status == "success"){
				// $scope.permission.resourceList.unshift(angular.copy($scope.permission.resource));
				$scope.permission.resource = null;
				$scope.dataInit();
				// alert(data.message);
			}
		});
	}

	$scope.getPermissionList = function(){
		uiLoadingSrv.createLoading(body);
		dataMutualSrv.get(LINK_SYS_PERMISSION_LIST).then(function(data) {
			if(data.status == "success"){
				$scope.permission.permissionList = data.content;
			}
			uiLoadingSrv.removeLoading(body);
		});
	}

	$scope.updatePermission = function(permission) {
		dataMutualSrv.post(LINK_SYS_PERMISSION_UPDATE, permission).then(function(data) {
			if(data.status == "success"){
				alert(data.message);
			}
		});
	};
	$scope.changePermissionCode = function () {
		$scope.permission.permission.code = $scope.permission.select_resource.code  + "-" + $scope.permission.select_operation.code;
	}

	$scope.addPermission = function(){
		if(!$scope.permission.permission.name || !$scope.permission.permission.code || !$scope.permission.select_resource.code
		|| !$scope.permission.select_operation.code){
			return;
		}
		$scope.permission.permission.resourceId = $scope.permission.select_resource.resourceId;
		$scope.permission.permission.operationId = $scope.permission.select_operation.operationId;
		dataMutualSrv.post(LINK_SYS_PERMISSION_ADD,angular.copy($scope.permission.permission)).then(function(data) {
			if(data.status == "success"){
				// $scope.permission.permissionList.unshift(angular.copy($scope.permission.permission));
				$scope.getPermissionList();
			}
		});
	}

	$scope.getRoleList = function(){
		uiLoadingSrv.createLoading(body);
		dataMutualSrv.get(LINK_SYS_ROLE).then(function(data) {
			if(data.status == "success"){
				$scope.permission.roleList = data.content;
			}
			uiLoadingSrv.removeLoading(body);
		});
	}

	$scope.updateRole = function(role) {
		dataMutualSrv.post(LINK_SYS_ROLE_UPDATE, role).then(function(data) {
			if(data.status == "success"){
				alert(data.message);
			}
		});
	};

	$scope.addRole = function(){
		if(!$scope.permission.role.name || !$scope.permission.role.code || !$scope.permission.role.description){
			return;
		}
		dataMutualSrv.post(LINK_SYS_ROLE_ADD, $scope.permission.role).then(function(data) {
			if(data.status == "success"){
				$scope.permission.role = null;
				$scope.dataInit();
			}
		});
	}

	$scope.editRolePermission = function(role){
		uiLoadingSrv.createLoading(body);
		dataMutualSrv.get(LINK_PERMISSION_SYS + "/" + role.roleId).then(function(data) {
			if(data.status == "success"){
				$scope.permission.currentRole = role;
				$scope.permission.showRoleDetail = true;
				$scope.permission.rolePermissionList = data.content;
			}
			uiLoadingSrv.removeLoading(body);
		});
	}

	$scope.updateRolePermission = function(role){

		if(!role.roleId){
			return;
		}

		var rolePermission = {
			roleId: role.roleId,
			permissionIds: []
		};

		if ($('.permissionList').length > 0) {
			$('.permissionList:checked').each(function(i, item) {
				rolePermission.permissionIds.push(item.value);
			});
		};

		uiLoadingSrv.createLoading(body);
		dataMutualSrv.post(LINK_SYS_PERMISSION_SETTING,rolePermission).then(function(data) {
			if(data.status == "success"){
				alert(data.message);
			}
			uiLoadingSrv.removeLoading(body);
		});
	}

	$scope.$on('ngRepeatFinished', function (ngRepeatFinishedEvent) {
		if ($('.permissionList').length > 0) {
			$('.permissionList').each(function(i, item) {
				for (var o in $scope.permission.rolePermissionList) {
					if (item.value == $scope.permission.rolePermissionList[o].permissionId) {
						$(this).prop("checked", true);
					}
				}
			});
		}

	});

	$scope.dataInit = function(){
		$scope.getOperationList();
		$scope.getResourceList();
		$scope.getPermissionList();
		$scope.getRoleList();
	}
	$scope.dataInit();
};
