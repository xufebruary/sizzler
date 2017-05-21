'use strict';

/**
 * dataMutualSrv
 * 数据交互
 *
 */

import {
	getBeforeDay,
	uuid
} from 'components/modules/common/common';

import cookieUtils from 'utils/cookie.utils';


//angular
//	.module('pt')
//	.service('dataMutualSrv', ['$http', '$q', '$rootScope', 'sysRoles', dataMutualSrvFunc]);
dataMutualSrvFunc.$inject = ['$http', '$q', '$rootScope', 'sysRoles'];
function dataMutualSrvFunc($http, $q, $rootScope, sysRoles) {
	this.get = function (url, type, option) {
		var deferred = $q.defer();
		var config = {
			method: 'GET',
			url: url.indexOf('?') > -1 ? url + "&sid=" + cookieUtils.get('sid') + "&uiVersion=" + BASE_VERSION : url + "?sid=" + cookieUtils.get('sid') + "&uiVersion=" + BASE_VERSION,
			cache: false,
			withCredentials: true
		};
		if(option){
			config = $.extend({},config,option);
		}
		$http(config).success(function (data, status, headers, config) {
			if (data.status == 'success') {

				//获取widget列表
				if (type == 'wgtList') {
					getWidgetListFormatData(data);
					if (sysRoles.hasSysRole("ptone-admin-user")) {
						getWidgetTempletListFormatData(data);
					}
				}

				console.log('Get Data Success!');
			} else if (data.status == 'failed') {
				console.log('Get Data Failed!');
			} else if (data.status == 'error') {
				console.log('Get Data Error: ');
				console.log(data.message)
			}

			deferred.resolve(data);
		}).error(function (data, status, headers, config) {
			//请求失败
			console.log(url);
			console.log('Get Data Error!');
			if(data){
				deferred.resolve(data);
			}else{
				deferred.resolve('timeout');
			}

		});

		return deferred.promise;
	};
	this.post = function (url, data, type) {
		var deferred = $q.defer();
		var newUrl = url + "?sid=" + cookieUtils.get('sid') + "&uiVersion=" + BASE_VERSION;
		type = type ? type : "";

		//保存widget
		if (type == 'wgtSave') {
			saveWidgetFormatData(data);
			if (sysRoles.hasSysRole("ptone-admin-user")) {
				if (data.baseWidget.widgetTitle) {
					data.baseWidget.widgetTitle = angular.toJson(data.baseWidget.widgetTitle);
				}
				if (data.baseWidget.description) {
					data.baseWidget.description = angular.toJson(data.baseWidget.description);
				}
			}
		}

		if (type == 'addPanelTemplet') {
			if (sysRoles.hasSysRole("ptone-admin-user")) {
				data.panelTitle = angular.toJson(data.panelTitle);
			}
		}

		if (type == 'onboardingAddWidget') {
			newUrl = newUrl + "&isPreview=1";
		}

		$http({
			method: 'POST',
			//url: url + ';jsessionid=' + $rootScope.sid,
			url: newUrl,
			data: angular.toJson(data),
			withCredentials: true
		}).success(function (data, status, headers, config) {
			if (data.status == 'success') {
				//获取widget列表,只对widget模板特殊化
				if (type == 'wgtList') {
					data.total = data.content.total;
					data.content = data.content.templets;
					getWidgetListFormatData(data);
					if (sysRoles.hasSysRole("ptone-admin-user")) {
						getWidgetTempletListFormatData(data);
					}
				}
				console.log('Post Data Success!')
			} else if (data.status == 'failed') {
				console.log('Post Data Failed!')
			} else if (data.status == 'error') {
				console.log('Post Data Error: ')
				console.log(data.message)
			}
			deferred.resolve(data);
		}).error(function (data, status, headers, config) {
			//请求失败
			console.log('Post Data Error: ')
		});

		return deferred.promise;
	}
}

function getWidgetListFormatData(data) {
	for (var i = data.content.length - 1; i >= 0; i--) {
		//当widget为非工具类chart时
		if (data.content[i].baseWidget.widgetType == 'chart') {

			//处理非正常数据,恢复默认值
			if (!data.content[i].variables[0]) {
				data.content[i].variables[0] = [
					{
						"variableId": uuid(),
						"ptoneDsInfoId": 0,
						"dsCode": null,
						"variableGraphId": 800,
						"variableColor": null,
						"connectionId": null,
						"accountName": null,
						"profileId": null,
						"dimensions": [],
						"ignoreNullDimension": 0,
						"dateDimensionId": "",
						"metrics": [],
						"sort": null
					}
				]
			}
			;

			try {
				if (!data.content[i].variables[0].metrics) {
					data.content[i].variables[0].metrics = [];
					data.content[i].variables[0].metricsCode = [];
				}
				if (data.content[i].variables[0].metrics && angular.isString(data.content[i].variables[0].metricsCode)) {
					data.content[i].variables[0].metricsCode = angular.fromJson(data.content[i].variables[0].metricsCode);
				}

				if (!data.content[i].variables[0].dimensions) {
					data.content[i].variables[0].dimensions = [];
					data.content[i].variables[0].dimensionsCode = [];
				}
				if (data.content[i].variables[0].dimensions && angular.isString(data.content[i].variables[0].dimensionsCode)) {
					data.content[i].variables[0].dimensionsCode = angular.fromJson(data.content[i].variables[0].dimensionsCode);
				}
			} catch (e) {
				console.log('widget data error: ')
				console.log(data.content[i])
			}
		}
	}
	;
}

function getWidgetTempletListFormatData(data) {
	$.each(data.content, function (i, item) {
		try {
			var strTagName = item.baseWidget.description;
			var jsonTagName = angular.fromJson(strTagName);
			item.baseWidget.description = jsonTagName;
		} catch (e) {
			console.log("转化description报错");
		}

		try {
			var strTitleName = item.baseWidget.widgetTitle;
			var jsonTitleName = angular.fromJson(strTitleName);
			item.baseWidget.widgetTitle = jsonTitleName;
		} catch (e) {
			console.log("转化widgetTitle报错");
		}

		item['sizeX'] = item.baseWidget.sizeX;
		item['sizeY'] = item.baseWidget.sizeY;
		item['minSizeX'] = 5;
		item['minSizeY'] = 6;
	})
}

function saveWidgetFormatData(data) {
	if (data.toolData && !angular.isString(data.toolData.extend)) {
		var extend = data.toolData.extend;
		data.toolData.extend = angular.toJson(extend);
	}
	if (data.variables && data.variables[0] && data.variables[0].metricsCode && !angular.isString(data.variables[0].metricsCode)) {
		data.variables[0].metricsCode = angular.toJson(data.variables[0].metricsCode);
	}
	if (data.variables && data.variables[0] && data.variables[0].dimensionsCode && !angular.isString(data.variables[0].dimensionsCode)) {
		data.variables[0].dimensionsCode = angular.toJson(data.variables[0].dimensionsCode);
	}
	delete data.col;
	delete data.row;
	delete data.sizeX;
	delete data.sizeY;
	delete data.minSizeX;
	delete data.minSizeY;
	delete data.autoPos;
	delete data.baseWidget.metricsJson;
	delete data.baseWidget.dimensionsJson;
	delete data.widgetDrawing;
	delete data._ext;
}

export default dataMutualSrvFunc;
