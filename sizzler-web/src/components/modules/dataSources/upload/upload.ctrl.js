'use strict';

import {
	LINK_EXCEL_FILE_UPLOAD,
	LINK_PULL_REMOTE_DATA,
	LINK_DATA_SOURCE_EDIT_VIEW,
	LINK_GET_AUTH_ACCOUNT,
	LINK_GET_SOURCE_WIDGET_COUNT,
	LINK_DEL_SAVEDFILE,
	LINK_UPDATE_CONNECTION_SOURCE,
	LINK_EXCEL_FILE_UPLOAD_UPDATE,
	uuid
} from '../../common/common';
var Base64 = require('js-base64').Base64;

import cookieUtils from 'utils/cookie.utils';

/**
 * mysql
 * @dataSources
 *
 */
angular
.module('pt')
.controller('dsUploadCtrl', ['$scope', '$document', '$rootScope', '$translate', '$timeout', 'FileUploader', 'dataMutualSrv', 'uiLoadingSrv', 'siteEventAnalyticsSrv', 'DataSourcesServices', dsUploadCtrl]);

function dsUploadCtrl($scope, $document, $rootScope, $translate, $timeout, FileUploader, dataMutualSrv, uiLoadingSrv, siteEventAnalyticsSrv, DataSourcesServices) {
	var body = $document.find('body').eq(0);

	$scope.myOptions = {
		currentDs: null,//当前数据源信息:{id:xxx,code:xxx}
		pageLoad: false,        //当前页面加载状态

		current: {
			mod: null           //当前显示模块('noData','tableList')
		},

		//账户相关
		account: {
			list: [],        //已授权账户列表
			data: [],        //当前账户账户下的数据
			dataCopy: [],     //当前账户账户下的数据(副本，导航使用的)
			widgtCount: 0,   //当前账户下widget数量
			queryStatus: null//请求状态(querying, success, failed, error)
		},

		//文件相关
		file: {
			updateType: 'add',	//文件上传类型(add-新增;update-更新)
			list: [],        //已授权账户列表
			widgtCount: 0,   //当前账户下widget数量
			queryStatus: null,//请求状态(querying, success, failed, error)
			isUploading: false,	//是否在上传中
			isUploadFinish: false, //是否上传完成
			isCancel: false,	//是否取消上传
			progress: 0,		//上传进度
			currentFile: null,
			fileNewName: "",
			nowFileName: "",
			uploadingI18n: null
		},

		//文件解绑
		fileDelete: {
			file: null,
			index: null,
			fileWidgtCount: 0
		},

		//文件编辑
		fileEdit: {
			index: null,
			file: null,
			errorCode: null
		},

		//文件重命名
		fileRename: {
			index: null,
			file: null,
			fileNewName: null,
			showTips: false	//显示重命名弹窗
		},

		//文件更新
		fileUpdate: {
			file: null,
			index: null,
			nameIsSame: false,
			tipsInfo: null
		},

		//提示框
		tips: {
			show: false,
			options: {}
		},

		//时区
		timezone: {
			show: false,
			fieds: null
		}
	};

	//Excel文件上传实例化
	var user = {
		id: $rootScope.userInfo.ptId,
		sid: cookieUtils.get('sid')
	};
	$scope.uploader = new FileUploader({
		url: LINK_EXCEL_FILE_UPLOAD + "/" + $scope.rootSpace.current.spaceId + "?sid=" + user.sid
	});

	var encodeStr = function (str) {
		return Base64.encode(encodeURIComponent(str));
	};

	//关闭弹窗
	$scope.close = function (toMod) {
		body.removeClass('modal-open');
		$scope.myOptions.tips.show = false;
		$scope.myOptions.fileRename.showTips = false;
		$scope.clearFileQueue();

		if (toMod) {
			$scope.dsBinding.current.mod = toMod;
		}
	};

	//显示上传文件窗口
	$scope.clickFile = function (type) {
		$scope.myOptions.file.updateType = type;
		document.getElementById("uploadFileInput").click();

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"data_source",
			"what":"data_sources_manage_upload_file",
			"how":"click",
			"value": $scope.myOptions.currentDs.code
		});
	};

	// FILTERS
	$scope.uploader.filters.push({
		name: 'customFilter',
		fn: function (item, options) {
			return this.queue.length <= 1;
		}
	});

	//事件-文件添加失败
	$scope.uploader.onWhenAddingFileFailed = function (item, filter, options) {
		console.info('onWhenAddingFileFailed', item, filter, options);
	};
	//事件-文件选择完成
	$scope.uploader.onAfterAddingFile = function (fileItem) {
		console.info('onAfterAddingFile', fileItem);
		$scope.fileSelectFinsh(fileItem);
	};
	$scope.uploader.onAfterAddingAll = function (addedFileItems) {
		console.info('onAfterAddingAll', addedFileItems);
	};
	//上传之前
	$scope.uploader.onBeforeUploadItem = function (item) {
		$scope.myOptions.file.isCancel = false;
		$scope.myOptions.file.isUploading = true;
		$scope.myOptions.file.isUploadFinish = false;
		$scope.myOptions.tips.show = false;
		body.addClass('modal-open');
		console.log($scope.uploader.queue);
		console.info('onBeforeUploadItem', item);
	};
	$scope.uploader.onProgressItem = function (fileItem, progress) {

		$scope.myOptions.file.progress = progress;
		if (progress == 100) {
			$timeout(function () {
				$scope.myOptions.file.progress = progress;
				$scope.myOptions.file.isUploadFinish = true;
			}, 1000);
		}
		console.info('onProgressItem', fileItem, progress);
	};
	$scope.uploader.onProgressAll = function (progress) {
		console.info('onProgressAll', progress);
	};
	$scope.uploader.onSuccessItem = function (fileItem, response, status, headers) {
		console.info('onSuccessItem', fileItem, response, status, headers);
	};
	$scope.uploader.onErrorItem = function (fileItem, response, status, headers) {
		console.info('onErrorItem', fileItem, response, status, headers);
	};
	$scope.uploader.onCancelItem = function (fileItem, response, status, headers) {
		console.info('onCancelItem', fileItem, response, status, headers);
	};
	//上传完成
	$scope.uploader.onCompleteItem = function (fileItem, response, status, headers) {
		console.info('onCompleteItem', fileItem, response, status, headers);
		console.log(response);

		//如果点了取消或者关闭,则不用提示结果
		if (!$scope.myOptions.file.isCancel) {

			var judgeData = setInterval(function () {
				if ($scope.myOptions.file.isUploadFinish) {
					clearInterval(judgeData);

					if (response.status == 'success') {
						$scope.fileUploadSuccess(response.content);
					} else {
						$scope.fileUploadFailed(response.message);
					}

					$scope.clearFileQueue();
				}
			}, 50);
		}
	};
	$scope.uploader.onCompleteAll = function () {
		//uiLoadingSrv.removeLoading(body);
		console.info('onCompleteAll');
	};


	//获取文件全名(带类型)
	$scope.getFileFullName = function () {
		var queueList = $scope.uploader.queue;
		if (queueList && queueList.length > 0) {
			return queueList[0].file.name;
		} else {
			return "";
		}
	};

	//获取文件名(不带类型)
	$scope.getFileName = function (name) {
		var fileFullName = name ? name : $scope.getFileFullName();
		if (fileFullName == "" || fileFullName.indexOf(".") == -1) {
			return fileFullName;
		}
		var lastIndex = fileFullName.lastIndexOf(".");
		return fileFullName.substring(0, lastIndex);
	};

	//获取文件类型
	$scope.getFileExt = function (name) {
		var fileFullName = name ? name : $scope.getFileFullName();
		if (fileFullName == "" || fileFullName.indexOf(".") == -1) {
			return fileFullName;
		}
		var lastIndex = fileFullName.lastIndexOf(".");
		return fileFullName.substring(lastIndex, fileFullName.length).toLocaleLowerCase();
	};

	//校验文件格式
	$scope.checkFileExt = function () {
		var _ext = $scope.getFileExt();
		if (_ext == "" || (_ext.indexOf("csv") < 0 && _ext.indexOf("xls") < 0 && _ext.indexOf("xlsx") < 0)) {
			return false;
		}
		return true;
	};

	//校验文件大小(MAX: 50M)
	$scope.checkFileSize = function () {
		var size = $scope.uploader.queue[0].file.size;
		//return size < 50 * 1024 * 1024;
		return true;
	};

	//文件上传队列清空操作
	$scope.clearFileQueue = function () {
		$scope.uploader.clearQueue();
		document.uploadFileForm.reset();
		$scope.myOptions.file.progress = 0;
		//$scope.myOptions.file.isUploading = false;
	};

	//取消上传
	$scope.cancelUpload = function () {
		$scope.myOptions.file.isUploading = false;
		$scope.clearFileQueue();
		if ($scope.uploader.queue[0] && $scope.uploader.queue[0].isUploading) {
			$scope.uploader.queue[0].cancel();
		}
	};

	//文件自动重命名(上传时)
	$scope.autoRename = function () {
		var title = $scope.getFileFullName(),
			fileName = $scope.getFileFullName(),
			t = 1;

		var _getName = function (titleTmp) {
			for (var i = 0; i < $scope.myOptions.file.list.length; i++) {
				if ($scope.myOptions.file.list[i].name.toLowerCase() == titleTmp.toLowerCase()) {
					title = $scope.getFileName() + "(" + (t++) + ")" + $scope.getFileExt();

					_getName(title);
					break;
				}
			}
		};
		_getName(fileName);

		return title;
	};

	//文件[远端]-请求文件数据(跳转到编辑器)
	$scope.getFileData = function (file, cid, type, path) {
		var url = LINK_PULL_REMOTE_DATA + cid + '/' + $scope.myOptions.current.directory + '/' + file.id;
		if (type == 'edit') {
			url = LINK_DATA_SOURCE_EDIT_VIEW + cid + '/' + file.sourceId;

			//编辑时，如果此文件状态有错，即直接弹出报错提示。
			if (file.remoteStatus != 1) {
				$scope.myOptions.fileEdit.errorCode = file.remoteStatus;
				$scope.showTips('editFileDataError');
				return false;
			}
		}
		//loading
		uiLoadingSrv.createLoading('.pt-main');
		dataMutualSrv.get(url).then(function (data) {
			if (data.status == 'success') {
				if (type == 'add') {
					data.content.remotePath = path;
				} else if (type == 'edit') {
					$scope.myOptions.file.list[$scope.myOptions.fileEdit.index].remoteStatus = data.content.remoteStatus;
					$scope.myOptions.file.list[$scope.myOptions.fileEdit.index].updateTime = data.content.updateTime;
				}

				if (data.content.remoteStatus != 1) {
					$scope.showTips('dataWarning');
					uiLoadingSrv.removeLoading('.pt-main');

					for (var i = 0; i < $scope.myOptions.file.list.length; i++) {
						if ($scope.myOptions.file.list[i].fileId == file.fileId) {
							$scope.myOptions.file.list[i].remoteStatus = data.content.remoteStatus;
						}
					}
					return;
				}

				$scope.dsEditor.hotType = type;
				$scope.dsDataFormat(data.content, 0, $scope.dsEditor.hotType);

				if (!$scope.dsEditor.hotTable) {
					//如果返回数据为0, 则弹出提示
					$scope.showTips('getFileDataEmpty');
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

				$scope.showTips('getFileDataError');
				uiLoadingSrv.removeLoading('.pt-main');
			}
		});
	};

	//文件-获取已存文件列表
	$scope.getFileList = function (type) {
		//loading
		uiLoadingSrv.createLoading(angular.element('.ds-content'));
		dataMutualSrv.get(LINK_GET_AUTH_ACCOUNT + $scope.rootSpace.current.spaceId + '/all/' + $scope.myOptions.currentDs.id).then(function (data) {
			if (data.status == 'success') {
				$scope.myOptions.file.list = data.content;
				if ($scope.myOptions.file.list.length > 0) {
					$scope.myOptions.current.mod = 'tableList';
				} else {
					$scope.myOptions.current.mod = 'noData';
				}
				$scope.dsAuthUpdata($scope.myOptions.currentDs, type, 'account');
				$scope.myOptions.pageLoad = true;
			} else if (data.status == 'failed') {
				console.log('Get gaAccountList Failed!');
			} else if (data.status == 'error') {
				console.log('Get gaAccountList Error: ');
				console.log(data.message)
			}
			//loading
			uiLoadingSrv.removeLoading(angular.element('.ds-content'));
		})
	};

	//文件-文件选择完成
	$scope.fileSelectFinsh = function (fileItem) {
		$scope.myOptions.file.currentFile = fileItem.file;
		console.log($scope.myOptions.file.currentFile);


		//上传地址(新增或更新)
		if ($scope.myOptions.file.updateType == 'update') {
			//更新操作

			var type = $scope.getFileExt($scope.myOptions.fileUpdate.file.name);
			var newType = $scope.getFileExt();
			if (!$scope.checkFileExt() || type != newType) {
				//文件类型不对或与原文件不同则出提示
				//xls和xlsx视作同一类型

				if (type != newType) {
					if (!(['.xls', '.xlsx'].indexOf(type) >= 0 && ['.xls', '.xlsx'].indexOf(newType) >= 0) && !(['.csv'].indexOf(type) >= 0 && ['.csv'].indexOf(newType) >= 0)) {
						$scope.myOptions.fileUpdate.tipsInfo = $translate.instant('DATA_SOURCE.UPLOAD.TIPS_UPDATE_TYPE_ERROR');
						$scope.showTips('fileUpdateType');
						return;
					}
				} else {
					$scope.myOptions.fileUpdate.tipsInfo = $translate.instant('DATA_SOURCE.UPLOAD.ERROR_FILE_EXIST_INFO');
					$scope.showTips('fileUpdateType');
					return;
				}
			}
		} else {
			//新增操作

			//校验文件格式
			if (!$scope.checkFileExt()) {
				$scope.showTips('fileExtError');
				return;
			}
		}


		//校验文件大小
		if (!$scope.checkFileSize()) {
			$scope.showTips('fileSizeError');

			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				time: new Date().getTime(),
				operate: 'excel-upload-btn',
				operateId: uuid(),
				position: 'datasource-excel-upload'
			});
			return;
		}

		//文件名校验(如有重名则自动命名)
		//$scope.uploader.queue[0].name = $scope.autoRename();
		//console.log($scope.uploader.queue[0].name);

		//文件上传
		var currentFileName = encodeStr($scope.autoRename());

		//上传地址(新增或更新)
		if ($scope.myOptions.file.updateType == 'update') {
			//更新操作

			var name = $scope.getFileName($scope.myOptions.fileUpdate.file.name);
			var fullName = $scope.myOptions.fileUpdate.file.name;
			var newName = $scope.getFileName();
			var newFullName = $scope.getFileFullName();

			if (fullName != newFullName) {
				$scope.myOptions.fileUpdate.tipsInfo = $translate.instant('DATA_SOURCE.UPLOAD.TIPS_FILE_NAME_DIFFERENT_INFO_1')
					+ newFullName
					+ $translate.instant('DATA_SOURCE.UPLOAD.TIPS_FILE_NAME_DIFFERENT_INFO_2')
					+ fullName
					+ $translate.instant('DATA_SOURCE.UPLOAD.TIPS_FILE_NAME_DIFFERENT_INFO_3');
			} else {
				$scope.myOptions.fileUpdate.tipsInfo = $translate.instant('DATA_SOURCE.UPLOAD.TIPS_FILE_NAME_SAME_INFO_1')
					+ fullName
					+ $translate.instant('DATA_SOURCE.UPLOAD.TIPS_FILE_NAME_SAME_INFO_2');
			}
			$scope.showTips('fileUpdate');

			$scope.myOptions.file.uploadingI18n = $translate.instant("DATA_SOURCE.UPLOAD.TIPS_FILE_IS_UPDATE_1") + fullName + $translate.instant("DATA_SOURCE.UPLOAD.TIPS_FILE_IS_UPDATE_2");
		} else {
			//新增操作

			$scope.myOptions.file.uploadingI18n = $translate.instant("DATA_SOURCE.UPLOAD.TIPS_FILE_IS_UPLOADING_1") + $scope.myOptions.file.currentFile.name + $translate.instant("DATA_SOURCE.UPLOAD.TIPS_FILE_IS_UPLOADING_2");
			$scope.uploader.queue[0].url = LINK_EXCEL_FILE_UPLOAD + "/" + $scope.rootSpace.current.spaceId + "?sid=" + $rootScope.sid + "&nowFileName=" + currentFileName;
			$scope.uploader.queue[0].upload();
		}
	};

	//文件-上传成功
	$scope.fileUploadSuccess = function (data) {
		if ($scope.myOptions.file.updateType == 'update') {
			$scope.$apply(function () {
				$scope.myOptions.file.isUploading = false;
				$scope.myOptions.file.list[$scope.myOptions.fileUpdate.index].updateTime = data.updateTime;
				$scope.myOptions.fileUpdate.tipsInfo = $translate.instant('DATA_SOURCE.GD.FILE_REFRESH_SUCCESS');
				$scope.myOptions.fileUpdate.btnClass = 'pt-btn-success';
				$scope.myOptions.fileUpdate.btnText = $translate.instant('COMMON.OK');
				$scope.showTips('fileUpdateTips');
			})
		} else {
			$scope.dsEditor.hotType = 'upload';
			$scope.dsDataFormat(data, 0, $scope.dsEditor.hotType);

			if (!$scope.dsEditor.hotTable) {
				$scope.$apply(function () {
					$scope.myOptions.file.isUploading = false;
					$scope.showTips('getFileDataEmpty');
				})
			} else {
				$scope.openEdit('excel');
			}
		}
	};

	//文件-上传失败
	$scope.fileUploadFailed = function (message) {
		if ($scope.myOptions.file.updateType == 'update') {
			$scope.$apply(function () {
				$scope.myOptions.file.isUploading = false;
				$scope.myOptions.fileUpdate.tipsInfo = $translate.instant('DATA_SOURCE.GD.FILE_REFRESH_FAILURE');
				$scope.myOptions.fileUpdate.btnClass = 'pt-btn-success';
				$scope.myOptions.fileUpdate.btnText = $translate.instant('DATA_SOURCE.GD.REFRESH_AGAIN');
				$scope.showTips('fileUpdateTips');
			})
		} else {
			$scope.$apply(function () {
				$scope.myOptions.file.isUploading = false;
				$scope.showTips('fileUploadFailed');
			})
		}
		console.log('uploader error');
		console.log(message);
	};

	//文件-编辑
	$scope.editFile = function (file, index) {
		$scope.myOptions.fileEdit.file = file;
		$scope.myOptions.fileEdit.index = index;
		localStorage.removeItem('widgetEditorLinkToEdit');
		$scope.getFileData(file, file.connectionId, 'edit');

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"data_source",
			"what":"data_sources_manage_upload_edit",
			"how":"click",
			"value": $scope.myOptions.currentDs.code
		});
	};

	//文件-解绑提示
	$scope.removeFileShow = function (file, index) {

		//获取当前账户下的widget数量
		dataMutualSrv.get(LINK_GET_SOURCE_WIDGET_COUNT + file.sourceId).then(function (data) {
			if (data.status == 'success') {
				$scope.myOptions.fileDelete.fileWidgtCount = data.content || 0;
				$scope.myOptions.fileDelete.file = file;
				$scope.myOptions.fileDelete.index = index;
				$scope.showTips('fileDelete');
			} else {
				if (data.status == 'failed') {
					console.log('Get accountsList Failed!');
				} else if (data.status == 'error') {
					console.log('Get accountsList Error: ');
					console.log(data.message)
				}
			}
		});

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"data_source",
			"what":"data_sources_manage_upload_remove_connection",
			"how":"click",
			"value": $scope.myOptions.currentDs.code
		});
	};

	//文件-解绑
	$scope.removeFile = function (fileSourceId) {

		//loading
		uiLoadingSrv.createLoading(angular.element('.pt-popup-content'));

		dataMutualSrv.post(LINK_DEL_SAVEDFILE + fileSourceId).then(function (data) {
			if (data.status == 'success') {

				for (var i = 0; i < $scope.myOptions.file.list.length; i++) {
					if ($scope.myOptions.file.list[i].sourceId == fileSourceId) {
						$scope.myOptions.file.list.splice(i, 1);
					}
				}
				$scope.dsAuthUpdata($scope.myOptions.currentDs, 'del', 'file');

				if ($scope.myOptions.file.list.length == 0) {
					$scope.myOptions.current.mod = 'noData'
				}
			} else {
				if (data.status == 'failed') {
					console.log('Failed!')
				} else if (data.status == 'error') {
					console.log('Error: ');
					console.log(data.message)
				}
			}

			//loading
			uiLoadingSrv.removeLoading(angular.element('.pt-popup-content'));
			$scope.close();
		})
	};

	//文件-重命名提示
	$scope.renameFileShow = function (file, index) {
		$scope.myOptions.fileRename.fileNewName = $scope.getFileName(file.name).slice(0, 50);
		$scope.myOptions.fileRename.showTips = true;
		$scope.myOptions.fileRename.file = file;
		$scope.myOptions.fileRename.index = index;

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"data_source",
			"what":"data_sources_manage_upload_rename",
			"how":"click",
			"value": $scope.myOptions.currentDs.code
		});
	};

	//文件-重命名
	$scope.renameFile = function (fileNewName) {
		if (fileNewName != "") {
			var sendData = {
				sourceId: $scope.myOptions.fileRename.file.sourceId,
				name: fileNewName + $scope.getFileExt($scope.myOptions.fileRename.file.name)
			};
			dataMutualSrv.post(LINK_UPDATE_CONNECTION_SOURCE, sendData).then(function (data) {
				if (data.status == 'success') {
					for (var i = 0; i < $scope.myOptions.file.list.length; i++) {
						if ($scope.myOptions.file.list[i].sourceId == sendData.sourceId) {
							$scope.myOptions.file.list[i].name = fileNewName + $scope.getFileExt($scope.myOptions.fileRename.file.name);
							break;
						}
					}
				} else {
					if (data.status == 'failed') {
						console.log('Check file exist Failed!');
					} else if (data.status == 'error') {
						console.log('Check file exist Error: ');
						console.log(data.message)
					}
				}

				$scope.close();
			});
		} else {
			$scope.close();
		}
	};

	//文件-重命名-回车事件绑定
	$scope.eventKeydown = function (e) {
		var keycode = window.event ? e.keyCode : e.which;
		if (keycode == 13) {
			$scope.renameFile($scope.myOptions.fileRename.fileNewName);
		}
	};

	//文件-更新提示
	$scope.fileUpdateShow = function (file, index) {
		$scope.myOptions.fileUpdate.file = file;
		$scope.myOptions.fileUpdate.index = index;

		$scope.clickFile('update');

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"data_source",
			"what":"data_sources_manage_upload_update_file",
			"how":"click",
			"value": $scope.myOptions.currentDs.code
		});
	};

	//文件-更新
	$scope.fileUpdate = function () {
		$scope.uploader.queue[0].url = LINK_EXCEL_FILE_UPLOAD_UPDATE + "/" + $scope.myOptions.fileUpdate.file.connectionId + "/" + $scope.myOptions.fileUpdate.file.sourceId + "?sid=" + cookieUtils.get('sid');
		$scope.uploader.queue[0].upload();
	};

	//文件-时区设置
	$scope.setTimezone = function(file, index){
		//loading
		uiLoadingSrv.createLoading(angular.element('.pt-ds-table:eq(' + index + ')'));

		console.log(file);

		let sendData = {
			dsId: $scope.myOptions.currentDs.id,
			connectionId: file.connectionId,
			sourceId: file.sourceId
		}
		DataSourcesServices.getTimezone(sendData)
		.then((data) => {
			$scope.myOptions.timezone.show = true;
			$scope.myOptions.timezone.fieds = data;
			$scope.myOptions.timezone.fieds.name = file.name;
			$scope.myOptions.timezone.fieds.dsId = file.dsId;
			$scope.myOptions.timezone.fieds.connectionId = file.connectionId;
			$scope.myOptions.timezone.fieds.sourceId = file.sourceId;
		})
		.finally(() => {
			uiLoadingSrv.removeLoading(angular.element('.pt-ds-table:eq(' + index + ')'));
		})
	};

	//文件-时区设置隐藏
	$scope.hideTimezone = function(){
		$scope.myOptions.timezone = {
			show: false,
			fieds: null
		}
	};

	//提示-显示
	$scope.showTips = function (type) {
		var options = {
			title: null,
			info: null,
			btnLeftText: $translate.instant('COMMON.CANCEL'),
			btnRightText: $translate.instant('COMMON.CLOSE'),
			btnLeftClass: 'pt-btn-default',
			btnRightClass: 'pt-btn-success',
			btnLeftEvent: 'close()',
			btnRightEvent: 'close()',
			closeEvent: 'close()',
			btnLeftHide: 'false',
			btnRightHide: 'false',
			hdHide: 'true'
		};

		body.addClass('modal-open');
		switch (type) {
			//所选文件格式错误
			case "fileExtError":
				options.title = $translate.instant('DATA_SOURCE.UPLOAD.ERROR_FILE_TITLE');
				options.info = $translate.instant('DATA_SOURCE.UPLOAD.ERROR_FILE_EXIST_INFO');
				options.btnLeftHide = 'true';
				break;
			//所选文件大小错误
			case "fileSizeError":
				options.title = $translate.instant('DATA_SOURCE.UPLOAD.ERROR_FILE_TITLE');
				options.info = $translate.instant('DATA_SOURCE.UPLOAD.ERROR_FILE_SIZE_INFO', $rootScope.productConfigs);
				options.btnLeftHide = 'true';
				break;
			//所选文件上传失败
			case "fileUploadFailed":
				options.info = $translate.instant('DATA_SOURCE.UPLOAD.ERROR_FILE_UPLOAD_FAILED');
				options.btnLeftHide = 'true';
				options.btnRightClass = 'pt-btn-success';
				break;
			//文件更新确认
			case "fileUpdate":
				//options.title = $translate.instant('DATA_SOURCE.UPLOAD.TIPS_FILE_UPDATE_TITLE');
				options.info = $scope.myOptions.fileUpdate.tipsInfo;
				options.btnRightText = $translate.instant('DATA_SOURCE.UPLOAD.TIPS_BTN_FILE_UPDATE');
				options.btnRightClass = 'pt-btn-success';
				options.btnRightEvent = "fileUpdate()";
				options.btnLeftHide = 'true';
				break;
			//文件更新时类型不匹配
			case "fileUpdateType":
				//options.title = $translate.instant('DATA_SOURCE.UPLOAD.TIPS_FILE_UPDATE_TITLE');
				options.info = $scope.myOptions.fileUpdate.tipsInfo;
				options.btnLeftHide = 'true';
				break;
			//文件更新状态
			case "fileUpdateTips":
				options.info = $scope.myOptions.fileUpdate.tipsInfo;
				options.btnRightClass = $scope.myOptions.fileUpdate.btnClass;
				options.btnRightText = $scope.myOptions.fileUpdate.btnText;
				options.btnLeftHide = 'true';
				break;
			//文件解绑
			case "fileDelete":
				options.info = $scope.myOptions.fileDelete.fileWidgtCount + $translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_FILE_1') + $scope.myOptions.fileDelete.file.name + $translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_FILE_2');
				options.btnRightText = $translate.instant('DATA_SOURCE.MANAGEMENT.BTN_REMOVE');
				options.btnRightEvent = 'removeFile(\"' + $scope.myOptions.fileDelete.file.sourceId + '\",\"' + $scope.myOptions.fileDelete.index + '\")';
				options.btnRightClass = 'pt-btn-danger';
				break;
			//获取文件内容失败
			case "getFileDataError":
				options.info = $translate.instant('DATA_SOURCE.MYSQL.PULL_DATA_ERROR', $rootScope.productConfigs);
				options.btnLeftHide = 'true';
				break;
			//获取文件内容为空
			case "getFileDataEmpty":
				options.info = $translate.instant('DATA_SOURCE.MYSQL.DATA_EMPTY');
				options.btnLeftHide = 'true';
				break;
		}

		$scope.myOptions.tips.show = true;
		$scope.myOptions.tips.options = options;
	};


	//Init
	(function () {
		angular.element('#js_dsLoading').remove();

		$scope.clearFileQueue();
		var dsCode = 'upload';

		//数据初始化
		var judgeData = setInterval(function () {
			if ($scope.rootCommon.dsList.length > 0) {
				clearInterval(judgeData);

				$scope.myOptions.currentDs = angular.copy($scope.getDsInfo(dsCode));
				$scope.dsCtrl.editDs = angular.copy($scope.getDsInfo(dsCode));

				if ($scope.dsCtrl.dsDataTep[$scope.myOptions.currentDs.code]) {
					$scope.$apply(function () {
						$scope.myOptions.pageLoad = true;
						$scope.myOptions.file.list = angular.copy($scope.myOptions.file.list);
						$scope.myOptions.file.queryStatus = 'success';
					})
				} else {
					$scope.dsCtrl.dsDataTep[$scope.myOptions.currentDs.code] = {
						'accountList': []
					};
					$scope.myOptions.file.queryStatus = null;
				}
				$scope.getFileList('first');
			}
		}, 50);
	})()


	/**
	 * 1.上传操作: 点击添加按钮--选择文件--校验文件类型(false:return)--校验文件大小(false:return)--获取新文件名--上传
	 */
}
