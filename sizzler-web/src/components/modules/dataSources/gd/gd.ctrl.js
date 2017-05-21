/**
 * Created by dawn on 16/1/1.
 */
'use strict';

import {
	LINK_GET_SOURCE_WIDGET_COUNT,
	LINK_DEL_SAVEDFILE,
	LINK_GET_ACCOUNT_WIDGET_COUNT,
	LINK_DEL_AUTH_ACCOUNT,
	LINK_EXCEL_FILE_ADD,
	LINK_DATA_SOURCE_VIEW,
	LINK_GET_SCHEMA,
	LINK_PULL_REMOTE_DATA,
	LINK_DATA_SOURCE_EDIT_VIEW,
	LINK_GET_AUTH_ACCOUNT,
	LINK_AUTHOR_GA_DRIVE,
	LINK_UPDATE_CONNECTION_SOURCE,
	DATA_SOURCE_WEB_SOCKET,
	openWindow,
	uuid
} from '../../common/common';


/**
 * googledrive
 * @dataSources
 *
 */
angular
.module('pt')
.controller('dsGdCtrl', ['$scope', '$document', '$rootScope', '$translate', 'dataMutualSrv', 'websocket', 'siteEventAnalyticsSrv', 'uiLoadingSrv', 'DataSourcesServices', dsGdCtrl]);

function dsGdCtrl($scope, $document, $rootScope, $translate, dataMutualSrv, websocket, siteEventAnalyticsSrv, uiLoadingSrv, DataSourcesServices) {
	var body = $document.find('body').eq(0);

	$scope.dsBinding = {
		dsCode: 'googledrive',
		dsName: 'Google Drive',
		pageLoad: false,        //当前页面加载状态
		dsInfo: null,           //数据源字典表信息

		authorize: false,        //授权中
		authorizeFailure: false, //验证失败

		accountList: [],        //已授权账户列表
		account: null,          //当前账户
		accountCid: null,       //当前账户信息
		accountData: [],        //当前账户账户下的数据
		accountDataCopy: [],     //当前账户账户下的数据(副本，导航使用的)
		accountWidgtCount: 0,   //当前账户下widget数量

		fileAdd: false,         //是否新增文件
		// currentAccountNoFile: false, //当前账户下没有绑定任何文件

		//切换mydrive || sharewithme
		fileListType: {
			type: 'mydrive',
			id: 'mydrive',
			isMyDrive: true,
			showTitleShare: false,  //(true-共享者, false-所有者)
		},

		//文件编辑
		fileEdit: {
			index: null,
			file: null
		},

		//文件刷新操作
		fileRefresh: {
			tipsInfo: $translate.instant('DATA_SOURCE.GD.FILE_REFRESH_SUCCESS'),
			file: null,
			btnClass: 'pt-btn-success',
			btnText: $translate.instant('COMMON.OK')
		},

		//GD远端目录刷新
		accountDataRefresh: {
			tipsInfo: $translate.instant('DATA_SOURCE.GD.ACCOUNT_REFRESH_SUCCESS'),
			btnClass: 'pt-btn-success',
			btnText: $translate.instant('COMMON.OK')
		},

		//提示框
		tips: {
			show: false,
			options: {}
		},

		//文件解绑
		fileDelete: {
			file: null,
			index: null,
			fileWidgtCount: 0
		},

		//文件自定义更新频率设置
		autoUpdateRate: {
			showTips: false,
			file: null
		},

		//已存文件列表请求状态
		fileQueryStatus: null,

		//时区
		timezone: {
			show: false,
			fieds: null
		}
	};
	//数据初始化
	dataInit();

	//面包屑中回到google drive
	$scope.showGoogleDrive = function () {
		$scope.dsBinding.fileAdd = false;
		$scope.dsSrv.getSingleFileList($scope.dsCtrl.dsDataTep.googledrive.currentAccount);
		$scope.dsBinding.fileQueryStatus = 'success';
	};


	//切换mydrive || sharewithme
	$scope.changeFileType = function (type) {
		//在all account下添加file时，默认在第一个account下添加file
		if ($scope.dsCtrl.dsDataTep.googledrive.currentAccountCid == 'all') {
			$scope.dsCtrl.dsDataTep.googledrive.currentAccount = $scope.dsBinding.accountList[0];
			$scope.dsCtrl.dsDataTep.googledrive.currentAccountCid = $scope.dsBinding.accountList[0].connectionId;
		}

		if (type == 'sharewithme') {
			$scope.dsBinding.fileListType.showTitleShare = true;
			$scope.dsBinding.fileListType.isMyDrive = false;
		} else {
			$scope.dsBinding.fileListType.showTitleShare = false;
			$scope.dsBinding.fileListType.isMyDrive = true;
		}

		getFileList(type);
		$scope.dsBinding.fileListType.type = type;
		$scope.dsBinding.fileListType.id = type;
	};


	//添加文件（点击GD的目录中的文件）
	$scope.addFile = function (file, isDirectory) {
		if (!isDirectory) {
			//拼合文件路径保存起来，以便编辑保存文件后发送到服务器
			var path = '';
			angular.element('.ds-gd-file-breadcrumb').find('a').each(function (index) {
				path += $(this).html();
				if (angular.element('.ds-gd-file-breadcrumb').find('a').length - 1 != index) {
					path += '@#*';
				}
			});
			getFileData(file, $scope.dsCtrl.dsDataTep.googledrive.currentAccountCid, 'add', path);

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'ds_gd', 'connect_file');
		} else {
			// $scope.dsBinding.fileListType.showTitleShare = false;
			getFileList(file.id, 'isDirectory', file.name);
		}

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"data_source",
			"what":"select_table",
			"how":"click",
			"value": $scope.dsCtrl.editDs.code
		});
	};

	//文件导航操作
	$('.ds-gd-file').on('click', '.guide', function () {
		var id = $(this).attr('id');
		getFileList(id);
		$scope.dsBinding.fileListType.id = id;
		$(this).nextAll('svg,a').remove();

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where": "data_source",
			"what": "select_table_refresh_2",
			"how": "click",
			"value": $scope.dsCtrl.editDs.code
		});
	});

	//文件编辑
	$scope.editFile = function (file, index) {
		$scope.dsBinding.fileEdit.file = file;
		$scope.dsBinding.fileEdit.index = index;
		getFileData(file, file.connectionId, 'edit');

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"data_source",
			"what":"data_sources_manage_edit_table",
			"how":"click",
			"value": $scope.dsCtrl.editDs.code
		});
	};

	//文件自定义更新频率设置-打开弹出框
	$scope.autoUpdateRateShow = function(file){
		$scope.dsBinding.autoUpdateRate.file = file;
		$scope.dsBinding.autoUpdateRate.showTips = true;
	}

	//文件自定义更新频率设置-保存
	$scope.autoUpdateRate = function(updateFrequency, updateHour){
		//loading
		uiLoadingSrv.createLoading(angular.element('.pt-popup-content'));

		var sendData = {
			"sourceId": $scope.dsBinding.autoUpdateRate.file.sourceId,
			"updateFrequency": updateFrequency
			// "updateHour": updateHour,
			// "timezone": "+09:00"
		};

		dataMutualSrv.post(LINK_UPDATE_CONNECTION_SOURCE, sendData).then(function (data) {
			if (data.status == 'success') {
				var fileListLength = $scope.dsCtrl.dsDataTep.googledrive.fileList.length;

				for (var i = fileListLength - 1; i >= 0; i--) {
					if($scope.dsCtrl.dsDataTep.googledrive.fileList[i].sourceId == sendData.sourceId){
						$scope.dsCtrl.dsDataTep.googledrive.fileList[i].updateFrequency = updateFrequency;
						// $scope.dsCtrl.dsDataTep.googledrive.fileList[i].updateHour = updateHour;
						break;
					}
				}
			}
			else {
				console.log('refreshFile Failed!');
				if (data.status == 'error') {
					console.log(data.message)
				}
			}

			$scope.close();

			//loading
			uiLoadingSrv.removeLoading(angular.element('.pt-popup-content'));
		})
	}

	//解绑文件提示
	$scope.removeFileShow = function (file, index) {
		//loading
		uiLoadingSrv.createLoading(angular.element('.pt-ds-table:eq(' + index + ')'));

		//获取当前账户下的widget数量
		dataMutualSrv.get(LINK_GET_SOURCE_WIDGET_COUNT + file.sourceId).then(function (data) {
			if (data.status == 'success') {
				$scope.dsBinding.fileDelete.fileWidgtCount = data.content || 0;
				$scope.dsBinding.fileDelete.file = file;
				$scope.dsBinding.fileDelete.index = index;
				showTips('fileDelete');
			} else {
				if (data.status == 'failed') {
					console.log('Get accountsList Failed!');
				} else if (data.status == 'error') {
					console.log('Get accountsList Error: ');
					console.log(data.message)
				}
			}

			//loading
			uiLoadingSrv.removeLoading(angular.element('.pt-ds-table:eq(' + index + ')'));
		});

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"data_source",
			"what":"data_sources_manage_remove _connection_table",
			"how":"click",
			"value": $scope.dsCtrl.editDs.code
		});
	};

	//解绑文件
	$scope.removeFile = function (fileSourceId, index) {
		//loading
		uiLoadingSrv.createLoading(angular.element('.ds-table-list'));

		dataMutualSrv.post(LINK_DEL_SAVEDFILE + fileSourceId).then(function (data) {
			if (data.status == 'success') {
				$scope.dsCtrl.dsDataTep.googledrive.fileList.splice(index, 1);

				for (var i = 0; i < $scope.dsCtrl.dsDataTep.googledrive.fileAllList.length; i++) {
					if ($scope.dsCtrl.dsDataTep.googledrive.fileAllList[i].sourceId == fileSourceId) {
						$scope.dsCtrl.dsDataTep.googledrive.fileAllList.splice(i, 1);
					}
				}
				isFileBinding();
				$scope.dsAuthUpdata($scope.dsCtrl.editDs, 'del', 'file');
			} else {
				if (data.status == 'failed') {
					console.log('Failed!')
				} else if (data.status == 'error') {
					console.log('Error: ');
					console.log(data.message)
				}
			}

			//loading
			uiLoadingSrv.removeLoading(angular.element('.ds-table-list'));
			$scope.close();
		})
	};

	//文件-切换按钮切换
	$scope.chooseConnection = function(){
		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"select_table",
			"what":"data_sources_manage_choose_connection",
			"how":"click",
			"value": $scope.dsCtrl.editDs.code
		});
	};

	//文件-设置按钮切换
	$scope.setConnection = function(){
		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"select_table",
			"what":"data_sources_manage_setting",
			"how":"click",
			"value": $scope.dsCtrl.editDs.code
		});
	}

	//账户选择（下拉菜单）
	$scope.selectAccountName = function (account) {
		if (account == 'all') {
			$scope.dsCtrl.dsDataTep.googledrive.currentAccount = {'name': $translate.instant('DATA_SOURCE.GD.ALL_ACCOUNT')};
			$scope.dsCtrl.dsDataTep.googledrive.currentAccountCid = 'all';
		} else {
			$scope.dsCtrl.dsDataTep.googledrive.currentAccount = account;
			$scope.dsCtrl.dsDataTep.googledrive.currentAccountCid = account.connectionId;
		}

		if (!$scope.dsBinding.fileAdd) {
			$scope.dsSrv.getSingleFileList(account)
			//getSavedFileList();
		} else {
			// getFileList(dsBinding.fileListType.type);
			getFileList($scope.dsBinding.fileListType.isMyDrive ? 'mydrive' : 'sharewithme');
		}
	};

	//解除绑定账户提示
	$scope.removeAccountShow = function () {
		//loading
		uiLoadingSrv.createLoading(angular.element('.data-source-content'));

		//获取当前账户下的widget数量
		dataMutualSrv.get(LINK_GET_ACCOUNT_WIDGET_COUNT + $scope.dsCtrl.dsDataTep.googledrive.currentAccount.connectionId + "/" + $scope.dsCtrl.editDs.id).then(function (data) {
			if (data.status == 'success') {
				$scope.dsBinding.accountWidgtCount = data.content || 0;
				showTips('accountDelete');
			} else {
				if (data.status == 'failed') {
					console.log('Get accountsList Failed!');
				} else if (data.status == 'error') {
					console.log('Get accountsList Error: ');
					console.log(data.message)
				}
			}

			//loading
			uiLoadingSrv.removeLoading(angular.element('.data-source-content'));
		});

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"data_source",
			"what":"data_sources_manage_setting_remove _connection",
			"how":"click",
			"value": $scope.dsCtrl.editDs.code
		});
	};
	//解除绑定账户
	$scope.removeAccount = function (cid) {
		//loading
		uiLoadingSrv.createLoading(angular.element('.pt-popup-content'));
		if (cid) {
			dataMutualSrv.post(LINK_DEL_AUTH_ACCOUNT + cid).then(function (data) {
				if (data.status == 'success') {
					for (var i = 0; i < $scope.dsCtrl.dsDataTep.googledrive.accountList.length; i++) {
						if ($scope.dsCtrl.dsDataTep.googledrive.accountList[i].connectionId == cid) {
							$scope.dsCtrl.dsDataTep.googledrive.accountList.splice(i, 1);
						}
					}
					$scope.dsAuthUpdata($scope.dsCtrl.editDs, 'del', 'account');

					for (var i = 0; i < $scope.dsCtrl.dsDataTep.googledrive.fileAllList.length; i++) {
						if ($scope.dsCtrl.dsDataTep.googledrive.fileAllList[i].connectionId == cid) {
							$scope.dsCtrl.dsDataTep.googledrive.fileAllList.splice(i, 1);
						}
					}

					if ($scope.dsCtrl.dsDataTep.googledrive.accountList.length > 0) {
						$scope.dsCtrl.dsDataTep.googledrive.currentAccount = $scope.dsBinding.accountList[0];
						$scope.dsCtrl.dsDataTep.googledrive.currentAccountCid = $scope.dsBinding.accountList[0].connectionId;
						getSavedFileList();
					}
				} else {
					if (data.status == 'failed') {
					} else if (data.status == 'error') {
					}
				}

				$scope.close();
				//loading
				uiLoadingSrv.removeLoading(angular.element('.pt-popup-content'));
			})
		}
	};

	//文件-时区设置
	$scope.setTimezone = function(file, index){
		//loading
		uiLoadingSrv.createLoading(angular.element('.pt-ds-table:eq(' + index + ')'));

		console.log(file);

		let sendData = {
			dsId: file.dsId,
			connectionId: file.connectionId,
			sourceId: file.sourceId
		}
		DataSourcesServices.getTimezone(sendData)
		.then((data) => {
			$scope.dsBinding.timezone.show = true;
			$scope.dsBinding.timezone.fieds = data;
			$scope.dsBinding.timezone.fieds.name = file.name;
			$scope.dsBinding.timezone.fieds.dsId = file.dsId;
			$scope.dsBinding.timezone.fieds.connectionId = file.connectionId;
			$scope.dsBinding.timezone.fieds.sourceId = file.sourceId;
		})
		.finally(() => {
			uiLoadingSrv.removeLoading(angular.element('.pt-ds-table:eq(' + index + ')'));
		})
	};

	//文件-时区设置隐藏
	$scope.hideTimezone = function(){
		$scope.dsBinding.timezone = {
			show: false,
			fieds: null
		}
	};

	//刷新已存文件
	$scope.refreshFile = function (file, index) {
		$scope.dsBinding.fileRefresh.file = file;

		//loading
		uiLoadingSrv.createLoading(angular.element('.pt-ds-table:eq(' + index + ')'));

		var sendData = angular.copy(file);
		sendData['operateType'] = 'update';

		dataMutualSrv.post(LINK_EXCEL_FILE_ADD, sendData).then(function (data) {
			var tipsInfo;
			var dataWarningFlag = false;
			if (data.status == 'success') {
				//文件刷新成功提示
				$scope.dsCtrl.dsDataTep.googledrive.fileList[index].remoteStatus = data.content.remoteStatus;
				$scope.dsCtrl.dsDataTep.googledrive.fileList[index].updateTime = data.content.updateTime;

				if (data.content.remoteStatus == 0) {
					dataWarningFlag = true;
				} else {
					dataWarningFlag = false;
				}
				for (var i = 0; i < $scope.dsCtrl.dsDataTep.googledrive.fileList.length; i++) {
					if ($scope.dsCtrl.dsDataTep.googledrive.fileList[i].fileId == file.fileId) {
						$scope.dsCtrl.dsDataTep.googledrive.fileList[i].remoteStatus = data.content.remoteStatus;
					}
				}
				;

				$scope.dsBinding.fileRefresh.tipsInfo = $translate.instant('DATA_SOURCE.GD.FILE_REFRESH_SUCCESS');
				$scope.dsBinding.fileRefresh.btnClass = 'pt-btn-success';
				$scope.dsBinding.fileRefresh.btnText = $translate.instant('COMMON.OK');
			} else {
				//文件刷新失败提示
				$scope.dsBinding.fileRefresh.tipsInfo = $translate.instant('DATA_SOURCE.GD.FILE_REFRESH_FAILURE');
				$scope.dsBinding.fileRefresh.btnClass = 'pt-btn-success';
				$scope.dsBinding.fileRefresh.btnText = $translate.instant('DATA_SOURCE.GD.REFRESH_AGAIN');

				if (data.status == 'failed') {
					console.log('refreshFile Failed!');
				} else if (data.status == 'error') {
					console.log('refreshFile Error: ');
					console.log(data.message)
				}
			}

			//loading
			uiLoadingSrv.removeLoading(angular.element('.pt-ds-table:eq(' + index + ')'));
			if (dataWarningFlag) {
				showTips('dataWarning');
			} else {
				showTips("fileRefresh");
			}
		})

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"data_source",
			"what":"data_sources_manage_updatge_table_schema",
			"how":"click",
			"value": $scope.dsCtrl.editDs.code
		});
	}

	//刷新远端文件列表
	$scope.refreshFileList = function () {
		getFileList($scope.dsBinding.fileListType.id, 'refresh');
	}

	//关闭弹窗
	$scope.close = function () {
		body.removeClass('modal-open');
		$scope.dsBinding.tips.show = false;
		$scope.dsBinding.autoUpdateRate.showTips = false;
	};

	//获取已授权账户列表
	function getAccountList(type) {
		//loading
		uiLoadingSrv.createLoading(angular.element('.ds-content'));
		dataMutualSrv.get(LINK_DATA_SOURCE_VIEW + $scope.rootSpace.current.spaceId + '/' + $scope.dsCtrl.editDs.id).then(function (data) {
			if (data.status == 'success') {
				$scope.dsBinding.accountList = data.content;
				if ($scope.dsBinding.accountList.length > 0) {
					if (!$scope.dsCtrl.dsDataTep.googledrive.currentAccount || $scope.dsCtrl.dsDataTep.googledrive.currentAccountCid !== $scope.dsCtrl.dsDataTep.googledrive.currentAccount.connectionId) {
						if ($scope.dsBinding.accountList.length == 1) {
							$scope.dsCtrl.dsDataTep.googledrive.currentAccount = $scope.dsBinding.accountList[0];
							$scope.dsCtrl.dsDataTep.googledrive.currentAccountCid = $scope.dsBinding.accountList[0].connectionId;
						} else {
							$scope.dsCtrl.dsDataTep.googledrive.currentAccount = {'name': $translate.instant('DATA_SOURCE.GD.ALL_ACCOUNT')};
							$scope.dsCtrl.dsDataTep.googledrive.currentAccountCid = 'all';
						}
					}

					if (type == 'add') {
						//授权完成后，获取文件列表
						getFileList('mydrive');
					}

					getSavedFileList();
				}

				$scope.dsCtrl.dsDataTep['googledrive'].accountList = $scope.dsBinding.accountList;
				$scope.dsAuthUpdata($scope.dsCtrl.editDs, type, 'account');
				$scope.dsBinding.pageLoad = true;
			} else if (data.status == 'failed') {
				console.log('Get gaAccountList Failed!');
			} else if (data.status == 'error') {
				console.log('Get gaAccountList Error: ');
				console.log(data.message)
			}
			//loading
			uiLoadingSrv.removeLoading(angular.element('.ds-content'));
		})
	}

	//请求账户下文档列表
	function getFileList(id, type, name) {
		$scope.dsBinding.fileAdd = true;

		//从widget编辑器跳转过来
		if ($scope.rootTmpData.dataSources) {
			for (var i = 0; i < $scope.dsBinding.accountList.length; i++) {
				if ($scope.dsBinding.accountList[i].connectionId == $scope.rootTmpData.dataSources.connectionId) {
					$scope.dsCtrl.dsDataTep.googledrive.currentAccount = $scope.dsBinding.accountList[i];
					$scope.dsCtrl.dsDataTep.googledrive.currentAccountCid = $scope.dsBinding.accountList[i].connectionId;
					$scope.rootTmpData.dataSources = null;
					break;
				}
			}
			;
		}

		//loading
		uiLoadingSrv.createLoading(angular.element('.ds-gd-file-bd'));

		var url = '';
		url = LINK_GET_SCHEMA + $scope.dsCtrl.dsDataTep.googledrive.currentAccountCid + "/" + id + '?refresh=true';
		dataMutualSrv.get(url).then(function (data) {
			if (data.status == 'success') {
				$(window).scrollTop(0);
				//目录刷新成功提示
				$scope.dsBinding.accountDataRefresh.tipsInfo = $translate.instant('DATA_SOURCE.GD.ACCOUNT_REFRESH_SUCCESS');
				$scope.dsBinding.accountDataRefresh.btnClass = 'pt-btn-success';
				$scope.dsBinding.accountDataRefresh.btnText = $translate.instant('COMMON.OK');

				$scope.dsBinding.accountData = $scope.dsBinding.accountDataCopy = angular.fromJson(data.content)[0];
				if (id == 'mydrive' || id == 'sharewithme') {
					var typeName = $translate.instant('DATA_SOURCE.GD.MY_DRIVE');

					if (id == 'sharewithme') {
						$scope.dsBinding.fileListType.showTitleShare = true;
						typeName = $translate.instant('DATA_SOURCE.GD.SHARE_WITH_ME')
					}
					$('.ds-gd-file-breadcrumb').html('<a id="' + id + '" class="guide">' + typeName + '</a>');
				}

				if (type && type == 'isDirectory') {
					$scope.dsBinding.fileListType.showTitleShare = false;
					$scope.dsBinding.fileListType.id = id;
					$('.ds-gd-file-breadcrumb').append('<svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-chevron-small-right"></use></svg><a id="' + id + '" class="guide">' + name + '</a>');
				}else if(type && type == 'refresh'){//这部分后端接口还需要优化
					//var content = data.content && JSON.parse(data.content);
					//if(content && content[0] && content[0]['child'] && content[0]['child'][0] && content[0]['child'][0].name){
					//	$('#' + id).html(content[0]['child'][0].name);
					//}
				}
			} else {
				//目录刷新成功提示
				$scope.dsBinding.accountDataRefresh.tipsInfo = $translate.instant('DATA_SOURCE.GD.ACCOUNT_REFRESH_FAILURE');
				$scope.dsBinding.accountDataRefresh.btnClass = 'pt-btn-default';
				$scope.dsBinding.accountDataRefresh.btnText = $translate.instant('DATA_SOURCE.GD.REFRESH_AGAIN');

				if (data.status == 'failed') {
					console.log('Get accountsList Failed!');
				} else if (data.status == 'error') {
					console.log('Get accountsList Error: ');
					console.log(data.message)
				}

				showTips('getFileList')
			}

			if (type && type == 'refresh') {
				showTips('accountRefresh');
			}

			//loading
			uiLoadingSrv.removeLoading(angular.element('.ds-gd-file-bd'));
		});

		//全站事件统计
		if (type && type == 'refresh') {
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"data_source",
				"what": "select_table_refresh_1",
				"how": "click",
				"value": $scope.dsCtrl.editDs.code
			});
		}
		else if (type && type == 'settingSelectNewTable'){
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"data_source",
				"what": "data_sources_manage_setting_select_new_table",
				"how": "click",
				"value": $scope.dsCtrl.editDs.code
			});
		}
		else {
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"data_source",
				"what": "data_sources_manage_select_new_table",
				"how": "click",
				"value": $scope.dsCtrl.editDs.code
			});
		}
	}

	//请求文档数据
	function getFileData(file, cid, type, path) {
		var url = LINK_PULL_REMOTE_DATA + cid + '/' + $scope.dsBinding.fileListType.id + '/' + file.id;
		if (type == 'edit') {
			url = LINK_DATA_SOURCE_EDIT_VIEW + cid + '/' + file.sourceId;
		}
		//loading
		uiLoadingSrv.createLoading('.pt-main');
		dataMutualSrv.get(url).then(function (data) {
			if (data.status == 'success') {
				if (type == 'add') {
					data.content.remotePath = path;
				} else if (type == 'edit') {
					$scope.dsCtrl.dsDataTep.googledrive.fileList[$scope.dsBinding.fileEdit.index].remoteStatus = data.content.remoteStatus;
					$scope.dsCtrl.dsDataTep.googledrive.fileList[$scope.dsBinding.fileEdit.index].updateTime = data.content.updateTime;
				}

				if (data.content.remoteStatus == 0) {
					showTips('dataWarning');
					uiLoadingSrv.removeLoading('.pt-main');

					for (var i = 0; i < $scope.dsCtrl.dsDataTep.googledrive.fileList.length; i++) {
						if ($scope.dsCtrl.dsDataTep.googledrive.fileList[i].fileId == file.fileId) {
							$scope.dsCtrl.dsDataTep.googledrive.fileList[i].remoteStatus = data.content.remoteStatus;
						}
					}
					return;
				}

				$scope.dsEditor.hotType = type;
				$scope.dsDataFormat(data.content, 0, $scope.dsEditor.hotType);

				if (!$scope.dsEditor.hotTable) {
					//如果返回数据为0, 则弹出提示
					showTips('getFileDataEmpty');
					uiLoadingSrv.removeLoading('.pt-main');
				} else {
					$scope.openEdit();
				}
			} else {
				if (data.status == 'failed') {
					console.log('Get accountsList Failed!');
				} else if (data.status == 'error') {
					console.log('Get accountsList Error: ');
					console.log(data.message)
				}

				showTips('getFileDataError');
				uiLoadingSrv.removeLoading('.pt-main');
			}
		});
	}

	//请求当前数据源下的所有已存文档列表
	function getSavedFileList() {
		//loading
		uiLoadingSrv.createLoading(angular.element('.ds-gd-content'));

		dataMutualSrv.get(LINK_GET_AUTH_ACCOUNT + $scope.rootSpace.current.spaceId + '/' + $scope.dsCtrl.dsDataTep.googledrive.currentAccountCid + "/" + $scope.dsCtrl.editDs.id).then(function (data) {

			//更新文件更新状态
			$scope.dsBinding.fileQueryStatus = data.status;

			if (data.status != 'success') {
				if (data.status == 'failed') {
					console.log('Get accountsList Failed!');
				} else if (data.status == 'error') {
					console.log('Get accountsList Error: ');
					console.log(data.message)
				}
			} else {
				$scope.dsCtrl.dsDataTep.googledrive.fileAllList = data.content;
				$scope.dsSrv.getSingleFileList($scope.dsCtrl.dsDataTep.googledrive.currentAccount);
				isFileBinding();

				//默认进来
				if ($scope.dsCtrl.goToMode && $scope.dsCtrl.goToMode == 'fileAdd') {
					$scope.changeFileType('mydrive')
					$scope.dsCtrl.goToMode = null;
				}

				console.log('已存文件列表： ')
				console.log($scope.dsCtrl.dsDataTep.googledrive.fileAllList)
			}
			//loading
			uiLoadingSrv.removeLoading(angular.element('.ds-gd-content'));
		});
	}

	//判断某个账户下是否有已绑定文件
	function isFileBinding() {
		var flag = false;
		for (var i = 0; i < $scope.dsCtrl.dsDataTep.googledrive.fileList.length; i++) {
			if ($scope.dsCtrl.dsDataTep.googledrive.fileList[i].connectionId === $scope.dsCtrl.dsDataTep.googledrive.currentAccountCid) {
				flag = true;
				break;
			}
		}
	}

	//授权
	$scope.addAccount = function () {
		var sign = uuid();
		var url = LINK_AUTHOR_GA_DRIVE + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign + '&spaceId=' + $scope.rootSpace.current.spaceId;
		//链接socket;
		var accreditSocket = new websocket;
		accreditSocket.initWebSocket(DATA_SOURCE_WEB_SOCKET + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign);
		//授权验证跳转
		openWindow(url);
		$scope.dsBinding.authorize = true;
		//监听授权socket返回值
		$scope.wsData = accreditSocket.colletion;
		accreditSocket.ws.onmessage = function (event) {
			$scope.$apply(function () {
				$scope.wsData = event.data;
			});
		};
		var mywatch = $scope.$watch('wsData', function (newValue, oldValue, scope) {
			if (!newValue || newValue === oldValue) {
				return;
			}

			//注销当前监听事件
			mywatch();
			newValue = angular.fromJson(newValue);

			if (newValue.status == 'success') {
				//关闭socket
				accreditSocket.disconnect();
				$scope.dsBinding.authorize = false;

				//判断是否重复授权
				var flag = true;
				for (var i = 0; i < $scope.dsCtrl.dsDataTep['googledrive'].accountList.length; i++) {
					if ($scope.dsCtrl.dsDataTep['googledrive'].accountList[i].name == newValue.content.account) {
						console.log('重复授权!')
						flag = false;
						break;
					}
				}
				if (flag) {
					//更新账户列表
					$scope.dsBinding.accountList.push(angular.copy(newValue.content.connectionInfo));
					$scope.dsAuthUpdata($scope.dsCtrl.editDs, 'add', 'account');

					$scope.dsCtrl.dsDataTep.googledrive.currentAccountCid = newValue.content.connectionId;
					$scope.dsCtrl.dsDataTep.googledrive.currentAccount = newValue.content.connectionInfo;

					getFileList('mydrive');
					//getAccountList('add');//更新账户列表
				} else {
					// getFileList($scope.dsBinding.fileListType.isMyDrive ? 'mydrive' : 'sharewithme');
				}
			} else {
				$scope.dsBinding.authorizeFailure = true;
				$scope.dsBinding.authorize = false;
			}
		});

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"data_source",
			"what":"select_table_add_new_connection",
			"how":"click",
			"value": $scope.dsCtrl.editDs.code
		});
	};//accredit

	//再次授权
	$scope.verify_try = function () {
		$('.failure').fadeOut('fast', function () {
			$scope.addAccount();
		});
	};

	//获取当前数据源信息
	$scope.getDsInfo = function (dsCode) {
		var dsInfo;
		for (var i = 0; i < $scope.rootCommon.dsList.length; i++) {
			if ($scope.rootCommon.dsList[i].code == dsCode) {
				dsInfo = $scope.rootCommon.dsList[i];
				break;
			}
		}

		return dsInfo;
	};

	//指令数据初始化
	function dataInit() {
		var judgeData = setInterval(function () {
			if ($scope.rootCommon.dsList.length > 0) {
				clearInterval(judgeData);
				$scope.dsCtrl.editDs = $scope.getDsInfo('googledrive');

				//从widget编辑器跳转过来
				if ($scope.rootTmpData.dataSources) {
					var ds = $scope.getDsInfo($scope.rootTmpData.dataSources.dsCode);
					if ($scope.dsCtrl.editDs.code == ds.code) {
						$scope.dsCtrl.goToMode = $scope.rootTmpData.dataSources.type;
					}
				}

				if ($scope.dsCtrl.dsDataTep['googledrive']) {
					$scope.$apply(function () {
						$scope.dsBinding.pageLoad = true;
						$scope.dsBinding.accountList = angular.copy($scope.dsCtrl.dsDataTep['googledrive'].accountList);
						$scope.dsCtrl.dsDataTep.googledrive.fileList = angular.copy($scope.dsCtrl.dsDataTep['googledrive'].fileList);
					})
				} else {
					$scope.dsCtrl.dsDataTep['googledrive'] = {
						'accountList': [],
						'fileAllList': [],
						'fileList': [],
						'currentAccount': null,
						'currentAccountCid': null
					};
					// getAccountList('first');
				}
				getAccountList('first');
			}
		}, 200)
	}


	//页面提示
	function showTips(type) {
		var options = {
			title: null,
			info: null,
			btnLeftText: $translate.instant('COMMON.CANCEL'),
			btnRightText: $translate.instant('COMMON.OK'),
			btnLeftClass: 'pt-btn-default',
			btnRightClass: 'pt-btn-danger',
			btnLeftEvent: 'close()',
			btnRightEvent: 'close()',
			closeEvent: 'close()',
			btnLeftHide: 'false',
			btnRightHide: 'false',
			hdHide: 'false'
		};

		// body.addClass('modal-open');
		switch (type) {
			//目录刷新
			case "accountRefresh":
				options.title = '目录刷新';
				options.info = $scope.dsBinding.accountDataRefresh.tipsInfo;
				options.btnRightClass = $scope.dsBinding.accountDataRefresh.btnClass;
				options.btnRightText = $scope.dsBinding.accountDataRefresh.btnText;
				options.btnLeftHide = 'true';
				options.hdHide = 'true';
				break;
			//获取文件目录失败
			case "getFileList":
				options.title = '远端目录列表拉取';
				options.info = $translate.instant('DATA_SOURCE.GD.PULL_DATA_ERROR', $rootScope.productConfigs);
				options.btnLeftHide = 'true';
				options.hdHide = 'true';
				break;
			//文件刷新
			case "fileRefresh":
				options.title = '文件刷新';
				options.info = $translate.instant('DATA_SOURCE.GD.NAME') + "「" + $scope.dsBinding.fileRefresh.file.name + "」" + $scope.dsBinding.fileRefresh.tipsInfo;
				options.btnRightClass = $scope.dsBinding.fileRefresh.btnClass;
				options.btnRightText = $scope.dsBinding.fileRefresh.btnText;
				options.btnLeftHide = 'true';
				options.hdHide = 'true';
				break;
			//文件解绑
			case "fileDelete":
				options.title = '确认解绑';
				options.info = $scope.dsBinding.fileDelete.fileWidgtCount + $translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_FILE_1') + $scope.dsBinding.fileDelete.file.name + $translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_FILE_2');
				options.btnRightText = $translate.instant('DATA_SOURCE.MANAGEMENT.BTN_REMOVE');
				options.btnRightEvent = 'removeFile(\"' + $scope.dsBinding.fileDelete.file.sourceId + '\",\"' + $scope.dsBinding.fileDelete.index + '\")';
				options.hdHide = 'true';
				break;
			//账户解绑
			case "accountDelete":
				options.title = '确认解绑';
				options.info = $scope.dsBinding.accountWidgtCount + $translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_ACCOUNT_1') + $scope.dsCtrl.dsDataTep.googledrive.currentAccount.name + $translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_ACCOUNT_2');
				options.btnRightText = $translate.instant('DATA_SOURCE.MANAGEMENT.BTN_REMOVE');
				options.btnRightEvent = 'removeAccount(\"' + $scope.dsCtrl.dsDataTep.googledrive.currentAccountCid + '\")';
				options.hdHide = 'true';
				break;
			//获取文件内容失败
			case "getFileDataError":
				options.title = '远端拉取数据';
				options.info = $translate.instant('DATA_SOURCE.GD.PULL_DATA_ERROR', $rootScope.productConfigs);
				options.btnRightClass = 'pt-btn-success';
				options.hdHide = 'true';
				break;
			//当前选择文件内容为空
			case "getFileDataEmpty":
				options.title = '远端拉取数据';
				options.info = $translate.instant('DATA_SOURCE.GD.DATA_EMPTY');
				options.btnRightClass = 'pt-btn-success';
				options.hdHide = 'true';
				break;
			//刷新或编辑file时，远端数据发生变化
			case "dataWarning":
				options.title = '远端数据发生变化';
				options.info = $translate.instant('DATA_SOURCE.GD.DATA_CHANGE_TIP');
				options.btnRightClass = 'pt-btn-success';
				options.btnLeftHide = 'true';
				options.hdHide = 'true';
				break;
		}

		$scope.dsBinding.tips.show = true;
		$scope.dsBinding.tips.options = options;
	}

	/*
	 1. 新增账户(addAccount) --> 授权(addAccount) --> 获取当前账户下文件列表(getFileList) --> 添加文件(addFile)
	 2. 获取授权账户(getAccountList) --> 获取当前账户下文件列表(getFileList) --> 添加文件(addFile)
	 */
}
