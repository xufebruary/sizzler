'use strict';

/**
 * dataMutualSrv
 * 数据交互
 *
 */

import {
	uuid
} from 'components/modules/common/common';

import cookieUtils from 'utils/cookie.utils';

angular
    .module('pt')
    .service('dataMutualSrv', ['$http', '$q','$rootScope',
	function($http, $q,$rootScope) {
		this.get = function(url, type) {
			var deferred = $q.defer();
			if(type && type == 'share'){
				url = url.indexOf('?') > -1 ? url + "&sid=" + cookieUtils.get('sid') + "&uiVersion=" + BASE_VERSION : url + "?sid=" + cookieUtils.get('sid') + "&uiVersion=" + BASE_VERSION;
			}else{
				url = url.indexOf('?') > -1 ? url + '&shareUrl=true&accessToken=' + $rootScope.accessToken + "&sid=" + cookieUtils.get('sid') + "&uiVersion=" + BASE_VERSION : url + '?shareUrl=true&accessToken=' + $rootScope.accessToken + "&sid=" + cookieUtils.get('sid') + "&uiVersion=" + BASE_VERSION;
			}

			$http({
				method: 'GET',
				//url: url + ';jsessionid=' + $rootScope.sid + '?shareUrl=true&accessToken=' + $rootScope.accessToken,
				url: url ,
				cache:false,
				withCredentials: true,
				timeout: 20000
			}).
			success(function(data, status, headers, config) {
				if (data.status == 'success') {

                    //获取widget列表
                    if(type == 'wgtList'){
						getWidgetListFormatData(data);
						/*if($rootScope.userInfo && $rootScope.userInfo.access.indexOf('1') != -1){
							getWidgetTempletListFormatData(data);
						}*/
                    }

                    console.log('Get Data Success!')
				} else if (data.status == 'failed') {
					console.log('Get Data Failed!')
				} else if (data.status == 'error') {
					console.log('Get Data Error: ')
					console.log(data.message)
				}

				deferred.resolve(data);
			}).
			error(function(data, status, headers, config) {
				//请求失败
				console.log('Get Data Error!');
					if(data){
						deferred.resolve(data);
					}else{
						$rootScope.loadFinish.bodyTimeout = true;
					}
			});

			return deferred.promise;
		};
		this.post = function(url, data, type) {
			var deferred = $q.defer();

			//保存widget
			if(type == 'wgtSave'){
				saveWidgetFormatData(data);
				/*if($rootScope.userInfo && $rootScope.userInfo.access.indexOf('1') != -1){
					if(data.baseWidget.widgetTitle){
						data.baseWidget.widgetTitle = angular.toJson(data.baseWidget.widgetTitle);
					}
					if(data.baseWidget.description){
						data.baseWidget.description = angular.toJson(data.baseWidget.description);
					}
				}*/
			}

			if(type == 'addPanelTemplet'){
				/*if($rootScope.userInfo && $rootScope.userInfo.access.indexOf('1') != -1){
					data.panelTitle = angular.toJson(data.panelTitle);
				}*/
			}

			$http({
				method: 'POST',
				//url: url + ';jsessionid=' + $rootScope.sid,
				url: url + "?sid=" + $rootScope.sid + "&uiVersion=" + BASE_VERSION,
				data: angular.toJson(data),
				withCredentials: true
			}).
			success(function(data, status, headers, config) {
				if (data.status == 'success') {
					//获取widget列表,只对widget模板特殊化
					if(type == 'wgtList'){
						data.total = data.content.total;
						data.content = data.content.templets;
						getWidgetListFormatData(data);
						/*if($rootScope.userInfo && $rootScope.userInfo.access.indexOf('1') != -1){
							getWidgetTempletListFormatData(data);
						}*/
					}
					console.log('Post Data Success!')
				} else if (data.status == 'failed') {
					console.log('Post Data Failed!')
				} else if (data.status == 'error') {
					console.log('Post Data Error: ')
					console.log(data.message)
				}
				deferred.resolve(data);
			}).
			error(function(data, status, headers, config) {
				//请求失败
				console.log('Post Data Error: ')
			});

			return deferred.promise;
		}
}]); //dataMutualSrv

function getWidgetListFormatData (data){
	for (var i = data.content.length - 1; i >= 0; i--) {
		//当widget为非工具类chart时
		if(data.content[i].baseWidget.widgetType =='chart'){
			//处理非正常数据,恢复默认值
			if(!data.content[i].variables[0]){
				data.content[i].variables[0] = [
					{
						"variableId": uuid(),
						"ptoneDsInfoId": 0,
						"dsCode": null,
						"variableGraphId": 800,
						"variableColor": null,
						"connectionId":null,
						"accountName": null,
						"profileId": null,
						"dimensions": [],
						"ignoreNullDimension":0,
						"dateDimensionId":"",
						"metrics": [],
						"sort":null
					}
				]
			}

			try{
				if(!data.content[i].variables[0].metrics){
					data.content[i].variables[0].metrics = [];
					data.content[i].variables[0].metricsCode = [];
				}
				if( data.content[i].variables[0].metrics && angular.isString(data.content[i].variables[0].metricsCode) ){
					data.content[i].variables[0].metricsCode = angular.fromJson(data.content[i].variables[0].metricsCode);
				}

				if(!data.content[i].variables[0].dimensions){
					data.content[i].variables[0].dimensions = [];
					data.content[i].variables[0].dimensionsCode = [];
				}
				if( data.content[i].variables[0].dimensions && angular.isString(data.content[i].variables[0].dimensionsCode) ){
					data.content[i].variables[0].dimensionsCode = angular.fromJson(data.content[i].variables[0].dimensionsCode);
				}
			} catch (e){
				console.log('widget data error: ')
				console.log(data.content[i])
			}
		}
	};
}

function getWidgetTempletListFormatData(data){
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

function saveWidgetFormatData(data){
	if(data.toolData && !angular.isString(data.toolData.extend)){
		var extend = data.toolData.extend;
		data.toolData.extend = angular.toJson(extend);
	}
	if( data.variables && data.variables[0] && data.variables[0].metricsCode && !angular.isString(data.variables[0].metricsCode) ){
		data.variables[0].metricsCode = angular.toJson(data.variables[0].metricsCode);
	}
	if( data.variables && data.variables[0] && data.variables[0].dimensionsCode && !angular.isString(data.variables[0].dimensionsCode) ){
		data.variables[0].dimensionsCode = angular.toJson(data.variables[0].dimensionsCode);
	}
	delete data.col;
	delete data.row;
	delete data.sizeX;
	delete data.sizeY;
	delete data.minSizeX;
	delete data.minSizeY;
	delete data.baseWidget.metricsJson;
	delete data.baseWidget.dimensionsJson;
	delete data.widgetDrawing;
	delete data._ext;
}
