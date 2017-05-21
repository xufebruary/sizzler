'use strict';

import 'jquery';
import angular from 'angular';
import 'angular-ui-router';
import 'angular-translate';
import 'ngstorage';
import 'oclazyload';
import 'angular-cookies';

import 'assets/libs/angular/angular-gridstack/gridstack';
import 'assets/libs/angular/angular-bootstrap/ui-bootstrap-tpls-0.13.0';
import 'assets/css/modules/widget/widget-list/gridstack.css';
import 'components/modules/widget/widget-list/gridstack.controller';
import 'components/modules/widget/widget-list/gridstack.directive';
import 'components/modules/widget/widget-list/gridstackitem.directive';

import 'common/common.module';
import 'angular-sanitize';



import {
	LINK_SHARE_SIGNIN,
	LINK_PERMISSION_SYS
} from 'components/modules/common/common';

/**
 * getRequest
 * 获取URL中的尾参
 *
 */
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


//待页面加载完后启动angular
window.bootstrapData;
angular.element(document).ready(function () {
	var dashboadId = getRequest()['id'];
	if (!dashboadId) {
		return;
	}
	var sharePassWord = localStorage.getItem('sharePassWord-' + dashboadId) ? encodeURIComponent(localStorage.getItem('sharePassWord-' + dashboadId)) : '';
	var sharePanelUrl = LINK_SHARE_SIGNIN + 'panel' + '/' + dashboadId + '?password=' + sharePassWord;

	$.get(sharePanelUrl, function (data) {
		window.bootstrapData = data;
		angular.bootstrap(document, ['pt']);
	});
});

angular.module('pt', [
	'ngCookies',
	'ngStorage',
	'ui.router',
	'ui.bootstrap',
	'pt.commons',
	'oc.lazyLoad',
	'pascalprecht.translate',
	'ngSanitize',
	// 'widget.scrollbar',
	// 'pasvaz.bindonce',
	'gridstack-angular'
]);


//服务级权限判断
angular.module('pt')
.factory('permissions', ['$rootScope', function ($rootScope) {
	var permissionList;
	return {
		setPermissions: function (permissions) {
			permissionList = permissions;
			$rootScope.$broadcast('permissionsChanged')
		},
		getPermissions: function (sid) {
			$.get(LINK_PERMISSION_SYS + "?sid=" + sid, function (permissionData) {
				permissionList = permissionData.content;
				$rootScope.$broadcast('permissionsChanged')
			});
		},
		hasPermission: function (permission) {
			var permissionFlag = false;
			permission = permission.trim();

			if (permissionList) {
				$.each(permissionList, function (i, item) {
					if (item.code.trim() === permission) {
						permissionFlag = true;
					}
				});
			}
			return permissionFlag;
		}
	};
}]);

//指令级权限判断
angular.module('pt').directive('hasPermission', ['permissions', function (permissions) {
	return {
		link: function (scope, element, attrs) {

			var value = attrs.hasPermission.trim();
			var notPermissionFlag = value[0] === '!';
			if (notPermissionFlag) {
				value = value.slice(1).trim();
			}

			function toggleVisibilityBasedOnPermission() {
				var hasPermission = permissions.hasPermission(value);

				if (hasPermission && !notPermissionFlag || !hasPermission && notPermissionFlag)
					element.show();
				else
					element.hide();
			}

			toggleVisibilityBasedOnPermission();
			scope.$on('permissionsChanged', toggleVisibilityBasedOnPermission);
		}
	};
}]);

//服务级角色判断
angular.module('pt')
.factory('sysRoles', ['$rootScope', function ($rootScope) {
	var sysRolesList;
	return {
		setSysRoles: function (sysRoles) {
			sysRolesList = sysRoles;
			$rootScope.$broadcast('sysRoleChanged')
		},
		hasSysRole: function (role) {
			var roleFlag = false;
			role = role.trim();
			if (sysRolesList) {
				$.each(sysRolesList, function (i, item) {
					if (item.code.trim() === role) {
						roleFlag = true;
					}
				});
			}
			return roleFlag;
		}
	};
}]);

//指令级角色判断
angular.module('pt').directive('hasSysRole', ['sysRoles', function (sysRoles) {
	return {
		link: function (scope, element, attrs) {

			var value = attrs.hasSysRole.trim();
			var notRoleFlag = value[0] === '!';
			if (notRoleFlag) {
				value = value.slice(1).trim();
			}

			function toggleVisibilityBasedOnRole() {
				var hasSysRole = sysRoles.hasSysRole(value);
				var relation = hasSysRole && !notRoleFlag || !hasSysRole && notRoleFlag;
				if (relation)
					element.show();
				else
					element.hide();
			}

			toggleVisibilityBasedOnRole();
			scope.$on('sysRoleChanged', toggleVisibilityBasedOnRole);
		}
	};
}]);
