/**
 * template - mysql
 * 数据源模版文件-关系型
 * @dataSources
 *
 */

import tpl from './tmpMysql.html';
import {
	LINK_GET_ACCOUNT_WIDGET_COUNT,
	LINK_DEL_AUTH_ACCOUNT,
	LINK_DATA_SOURCE_VIEW,
	LINK_AUTHOR,
	LINK_PULL_REMOTE_DATA,
	LINK_DATA_SOURCE_EDIT_VIEW,
	LINK_GET_SOURCE_WIDGET_COUNT,
	LINK_DEL_SAVEDFILE,
	LINK_GET_SCHEMA,
	LINK_GET_AUTH_ACCOUNT,
	LINK_GET_USER_CONNECTION_CONFIG,
	LINK_SAVE_DB_CONNECTION,
	LINK_TEST_DB_CONNECTION,
	LINK_EXCEL_FILE_ADD,
	DATA_SOURCE_WEB_SOCKET,
	openWindow,
	uuid,
	getMyDsConfig
} from '../../common/common';


tmpMysqlDirective.$inject = ['$rootScope', '$translate', '$state', '$document', 'dataMutualSrv', 'websocket', 'uiLoadingSrv', 'permissions', 'siteEventAnalyticsSrv', 'DataSourcesServices'];
function tmpMysqlDirective($rootScope, $translate, $state, $document, dataMutualSrv, websocket, uiLoadingSrv, permissions, siteEventAnalyticsSrv, DataSourcesServices) {
	return {
		restrict: 'EA',
		template: tpl,
		link: link
	};

	function link(scope, elem, attrs) {
		var body = $document.find('body').eq(0);

		scope.myOptions = {
			i18nCode: null,
			currentDs: null,//当前数据源信息:{id:xxx,code:xxx}
			i18n: {},       //当前界面国际化文案
			link: {         //当前界面国际化链接地址
				webSiteHref: {}  //数据源官网URL地址:{zh_CN:'',en_US:'',ja_JP:''}
			},
			pageLoad: false,        //当前页面加载状态

			//账户相关
			account: {
				list: [],        //已授权账户列表
				data: [],        //当前账户账户下的数据
				dataCopy: [],     //当前账户账户下的数据(副本，导航使用的)
				widgtCount: 0,   //当前账户下widget数量
				'currentAccount': null,
				'currentAccountCid': null,
				'currentAddBtnType': null   // 区分当前所选文件与当前账户的关系: owner(创建者), view(查看者); 注: 如果选择的是所有文件列表(All)时,需要判断所有文件列表内,是否包含当前账户所创建的文件, 有则显示创建文件,否则显示创建账号
			},

			//已存文件相关
			file: {
				allList: [],
				list: [],
				queryStatus: null   //列表请求状态(querying, success, failed, error)
			},

			//界面显示相关
			current: {
				mod: null,          //当前显示模块('noData','tableList','fileList','connectionPost')
				directory: null    //当前远端目录
			},

			//数据库连接或者编辑
			connection: {
				show: false,
				from: null,
				operateType: 'save',    //save || edit_save
				connectFailure: null,

				modHostName: null,
				modPort: 3306,
				modUserName: null,
				modPassword: null,
				modDatabase: null,
				modConnectionName: null,

				sshModSwitch: false,
				sshModHostName: null,
				sshModPort: 22,
				sshModUserName: null,
				sshModAuthMethod: 'password', //password || private_key
				sshModPassword: null,
				sshModPrivateKey: null,
				sshModPassphrase: null,

				configPort: 3306,
				configSshPort: 22
			},

			//文件编辑
			fileEdit: {
				index: null,
				file: null,
				errorCode: null
			},

			//文件刷新操作
			fileRefresh: {
				tipsInfo: $translate.instant('DATA_SOURCE.MYSQL.FILE_REFRESH_SUCCESS'),
				file: null,
				btnClass: 'pt-btn-success',
				btnText: $translate.instant('COMMON.OK')
			},

			//目录刷新
			accountDataRefresh: {
				tipsInfo: $translate.instant('DATA_SOURCE.MYSQL.ACCOUNT_REFRESH_SUCCESS'),
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

			//时区
			timezone: {
				show: false,
				fieds: null
			}
		};

		//文件导航操作
		angular.element(elem).find('.ds-file-hd').on('click', '.guide', function () {
			var id = $(this).attr('id');
			scope.getFileList(id);
			$(this).nextAll('svg,a').remove();

			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where": "data_source",
				"what": "select_table_refresh_2",
				"how": "click",
				"value": scope.myOptions.currentDs.code
			});
		});


		//关闭弹窗
		scope.close = function (toMod) {
			body.removeClass('modal-open');
			scope.myOptions.tips.show = false;

			if (toMod) {
				scope.myOptions.current.mod = toMod;
			}
		};

		/**
		 用于检查当前数据源是否拥有查看账户列表的权限，如果不拥有，则前端不显示
		 */
		scope.checkHasPermission = function () {
			var hasPermission = true;
			if (scope.myOptions.currentDs.config.dataSource.ds.isCheckAccountListShowPermission) {
				//如果要检查当前数据源的账号/链接列表，则取出需要验证的code
				hasPermission = permissions.hasPermission(scope.myOptions.currentDs.config.dataSource.ds.checkAccountListShowPermissionCode);
			}
			return hasPermission;
		}

		/**
		 * eventDistribute
		 * 不同数据源之间的在同一按钮上会有不同事件,所以需要重新分配
		 */
		scope.eventDistribute = function (type, param) {
			if (type == 'addAccount') {
				//关系型数据源为: 新增数据库连接操作
				//账号型数据源为: 授权操作

				if (scope.myOptions.currentDs.config.dataSource.ds.isAccountType) {
					//账号型

					scope.authorization();
				} else {
					//关系型
					//param = [type, from]

					scope.connectionShow(param[0], param[1]);
				}
			}
		};


		/**
		 * dsSrv
		 * 数据源数据处理
		 */
		//账户-下拉选择
		scope.selectAccountName = function (account) {
			if (account == 'all') {
				scope.myOptions.account.currentAccount = {
					'name': $translate.instant('DATA_SOURCE.MYSQL.ALL_ACCOUNT')
				};
				scope.myOptions.account.currentAccountCid = 'all';
			} else {
				scope.myOptions.account.currentAccount = account;
				scope.myOptions.account.currentAccountCid = account.connectionId;
			}

			if (scope.myOptions.current.mod == 'fileList') {
				scope.getFileList('ptoneRootFolderID::connection');
			}

			//更新文件列表
			scope.getSingleFileList(account);
		};

		//账户-解绑提示
		scope.removeAccountShow = function () {
			//loading
			uiLoadingSrv.createLoading(angular.element('.data-source-content'));

			//获取当前账户下的widget数量
			dataMutualSrv.get(LINK_GET_ACCOUNT_WIDGET_COUNT + scope.myOptions.account.currentAccount.connectionId + "/" + scope.myOptions.currentDs.id).then(function (data) {
				if (data.status == 'success') {
					scope.myOptions.account.widgtCount = data.content || 0;
					scope.showTips('accountDelete');
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
				"value": scope.myOptions.currentDs.code
			});
		};

		//账户-解绑
		scope.removeAccount = function (cid) {
			//loading
			uiLoadingSrv.createLoading(angular.element('.pt-popup-content'));
			if (cid) {
				dataMutualSrv.post(LINK_DEL_AUTH_ACCOUNT + cid).then(function (data) {
					if (data.status == 'success') {
						for (var i = 0; i < scope.myOptions.account.list.length; i++) {
							if (scope.myOptions.account.list[i].connectionId == cid) {
								scope.myOptions.account.list.splice(i, 1);
							}
						}
						scope.dsAuthUpdata(scope.myOptions.currentDs, 'del', 'account');

						for (var i = 0; i < scope.myOptions.file.allList.length; i++) {
							if (scope.myOptions.file.allList[i].connectionId == cid) {
								scope.myOptions.file.allList.splice(i, 1);
							}
						}

						if (scope.myOptions.account.list.length > 0) {
							scope.myOptions.account.currentAccount = scope.myOptions.account.list[0];
							scope.myOptions.account.currentAccountCid = scope.myOptions.account.list[0].connectionId;
							scope.getSavedFileList();
						} else {
							scope.myOptions.current.mod = 'noData';
							scope.myOptions.file.list = [];
						}
					} else {
						if (data.status == 'failed') {
						} else if (data.status == 'error') {
						}
					}

					scope.close();
					//loading
					uiLoadingSrv.removeLoading(angular.element('.pt-popup-content'));
				})
			}
		};

		//账户-获取已授权账户列表
		scope.getAccountList = function (type) {
			//loading
			uiLoadingSrv.createLoading(angular.element('.ds-content'));
			dataMutualSrv.get(LINK_DATA_SOURCE_VIEW + scope.rootSpace.current.spaceId + '/' + scope.myOptions.currentDs.id).then(function (data) {
				if (data.status == 'success') {
					scope.myOptions.account.list = data.content;
					if (scope.myOptions.account.list.length > 0) {
						if (!scope.myOptions.account.currentAccount || scope.myOptions.account.currentAccountCid !== scope.myOptions.account.currentAccount.connectionId) {
							if (scope.myOptions.account.list.length == 1) {
								scope.myOptions.account.currentAccount = scope.myOptions.account.list[0];
								scope.myOptions.account.currentAccountCid = scope.myOptions.account.list[0].connectionId;
							} else {
								scope.myOptions.account.currentAccount = {'name': $translate.instant('DATA_SOURCE.MYSQL.ALL_ACCOUNT')};
								scope.myOptions.account.currentAccountCid = 'all';
							}
						}


						if (['googleanalysis', 'googleadwords', 'facebookad', 'ptengine', 'salesforce', 'doubleclick', 'doubleclickCompound'].indexOf(scope.myOptions.currentDs.code) >= 0) {
							//非关系型数据库下显示只有账户列表层展示

							scope.myOptions.current.mod = 'tableList';
						} else {
							//如果当前需要直接进入新增文件,则不用显示已存文件列表界面
							if (scope.dsCtrl.goToMode && scope.dsCtrl.goToMode == 'fileAdd') {
								scope.getSavedFileList();
							} else {
								if (type == 'add') {
									//授权完成后，获取文件列表
									scope.getFileList('ptoneRootFolderID::connection');
								} else {
									scope.myOptions.current.mod = 'tableList';
								}
								scope.getSavedFileList();
							}
						}
					} else {
						scope.myOptions.current.mod = 'noData';
					}

					scope.myOptions.account.list = scope.myOptions.account.list;
					scope.dsAuthUpdata(scope.myOptions.currentDs, type, 'account');
					scope.myOptions.pageLoad = true;
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

		//账户-授权
		scope.authorization = function () {
			var sign = uuid();
			var url = LINK_AUTHOR + scope.myOptions.currentDs.code + '?ptOneUserEmail=' + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign + '&spaceId=' + scope.rootSpace.current.spaceId;

			//链接socket;
			var accreditSocket = new websocket;
			accreditSocket.initWebSocket(DATA_SOURCE_WEB_SOCKET + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign);
			//授权验证跳转
			openWindow(url);
			scope.myOptions.authorize = true;
			//监听授权socket返回值
			scope.wsData = accreditSocket.colletion;
			accreditSocket.ws.onmessage = function (event) {
				scope.$apply(function () {
					scope.wsData = event.data;
				});
			};
			var mywatch = scope.$watch('wsData', function (newValue, oldValue, scope) {
				if (!newValue || newValue === oldValue) {
					return;
				}

				//注销当前监听事件
				mywatch();
				newValue = angular.fromJson(newValue);

				if (newValue.status == 'success') {
					//关闭socket
					accreditSocket.disconnect();
					scope.myOptions.authorize = false;

					console.log(newValue);

					//判断是否重复授权
					var flag = true;
					for (var i = 0; i < scope.myOptions.account.list.length; i++) {
						if (scope.myOptions.account.list[i].name == newValue.content.account) {
							console.log('重复授权!');
							flag = false;
							break;
						}
					}
					if (flag) {
						//更新账户列表
						scope.myOptions.account.list.push(angular.copy(newValue.content.connectionInfo));
						scope.myOptions.account.list = scope.myOptions.account.list;
						scope.dsAuthUpdata(scope.myOptions.currentDs, 'add', 'account');
						scope.myOptions.current.mod = 'tableList';

						if (['bigquery', 'googledrive'].indexOf(scope.myOptions.currentDs.code) >= 0) {
							scope.myOptions.account.currentAccountCid = newValue.content.connectionId;
							//scope.myOptions.account.currentAccount = {'name': newValue.content.account, 'connectionId': newValue.content.connectionId};
							scope.myOptions.account.currentAccount = newValue.content.connectionInfo;

							//授权完成后，获取文件列表
							scope.getFileList('ptoneRootFolderID::connection');

							//更新文件列表
							scope.getSingleFileList();
						}
					}

					//ga + gd
					if (['googleanalysis', 'googleadwords', 'doubleclick', 'doubleclickCompound', 'salesforce', 'facebookad'].indexOf(scope.myOptions.currentDs.code) >= 0) {
						scope.myOptions.accountAdd.accout = newValue.content.account;
						scope.myOptions.accountAdd.connectionId = newValue.content.connectionId;

						scope.showTips('accountAdd');
					}

				} else {
					scope.myOptions.authorizeFailure = true;
					scope.myOptions.authorize = false;
				}
			});
		};


		//文件[远端]-请求文件数据(跳转到编辑器)
		scope.getFileData = function (file, cid, type, path) {
			var url = LINK_PULL_REMOTE_DATA + cid + '/' + scope.myOptions.current.directory + '/' + file.id;
			if (type == 'edit') {
				url = LINK_DATA_SOURCE_EDIT_VIEW + cid + '/' + file.sourceId;

				//编辑时，如果此文件状态有错，即直接弹出报错提示。
				if (file.remoteStatus != 1) {
					scope.myOptions.fileEdit.errorCode = file.remoteStatus;
					scope.showTips('editFileDataError');
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
						scope.myOptions.file.list[scope.myOptions.fileEdit.index].remoteStatus = data.content.remoteStatus;
						scope.myOptions.file.list[scope.myOptions.fileEdit.index].updateTime = data.content.updateTime;
					}

					if (data.content.remoteStatus != 1) {
						scope.showTips('dataWarning');
						uiLoadingSrv.removeLoading('.pt-main');

						for (var i = 0; i < scope.myOptions.file.list.length; i++) {
							if (scope.myOptions.file.list[i].fileId == file.fileId) {
								scope.myOptions.file.list[i].remoteStatus = data.content.remoteStatus;
							}
						}
						return;
					}

					scope.dsEditor.hotType = type;
					scope.dsDataFormat(data.content, 0, scope.dsEditor.hotType);

					if (!scope.dsEditor.hotTable) {
						//如果返回数据为0, 则弹出提示
						scope.showTips('getFileDataEmpty');
						uiLoadingSrv.removeLoading('.pt-main');
					} else {
						scope.openEdit();
					}
				} else {
					if (data.status == 'failed') {
						console.log('Get accountsList Failed!');
					} else if (data.status == 'error') {
						console.log('Get accountsList Error: ');
						console.log(data.message)
					}

					scope.showTips('getFileDataError');
					uiLoadingSrv.removeLoading('.pt-main');
				}
			});
		};

		//文件-添加(点击目录中的文件)
		scope.addFile = function (file, isDirectory) {
			if (!isDirectory) {
				//拼合文件路径保存起来，以便编辑保存文件后发送到服务器
				var path = '';
				angular.element('.ds-file-breadcrumb').find('a').each(function (index) {
					path += $(this).html();
					if (angular.element('.ds-file-breadcrumb').find('a').length - 1 != index) {
						path += '@#*';
					}
				});
				scope.getFileData(file, scope.myOptions.account.currentAccountCid, 'add', path);
			} else {
				// scope.myOptions.fileListType.showTitleShare = false;
				scope.getFileList(file.id, 'isDirectory', file.name);
			}

			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"data_source",
				"what":"select_table",
				"how":"click",
				"value": scope.myOptions.currentDs.code
			});
		};

		//文件-编辑
		scope.editFile = function (file, index) {
			scope.myOptions.fileEdit.file = file;
			scope.myOptions.fileEdit.index = index;
			localStorage.removeItem('widgetEditorLinkToEdit');
			scope.getFileData(file, file.connectionId, 'edit');

			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"data_source",
				"what":"data_sources_manage_edit_table",
				"how":"click",
				"value": scope.myOptions.currentDs.code
			});	
		};

		//文件-刷新
		scope.refreshFile = function (file, index) {
			scope.myOptions.fileRefresh.file = file;

			//loading
			uiLoadingSrv.createLoading(angular.element('.pt-ds-table:eq(' + index + ')'));

			var sendData = angular.copy(file);
			sendData['operateType'] = 'update';

			dataMutualSrv.post(LINK_EXCEL_FILE_ADD, sendData).then(function (data) {
				var tipsInfo;
				var dataWarningFlag = false;
				if (data.status == 'success') {
					if (data.content.remoteStatus == 0) {
						dataWarningFlag = true;
					} else {
						dataWarningFlag = false;
					}
					for (var i = 0; i < scope.myOptions.file.list.length; i++) {
						if (scope.myOptions.file.list[i].fileId == file.fileId) {
							scope.myOptions.file.list[i].remoteStatus = data.content.remoteStatus;
						}
					}

					scope.myOptions.fileRefresh.tipsInfo = $translate.instant('DATA_SOURCE.MYSQL.FILE_REFRESH_SUCCESS');
					scope.myOptions.fileRefresh.btnClass = 'pt-btn-success';
					scope.myOptions.fileRefresh.btnText = $translate.instant('COMMON.OK');
				} else {
					//文件刷新失败提示
					scope.myOptions.fileRefresh.tipsInfo = $translate.instant('DATA_SOURCE.MYSQL.FILE_REFRESH_FAILURE');
					scope.myOptions.fileRefresh.btnClass = 'pt-btn-success';
					scope.myOptions.fileRefresh.btnText = $translate.instant('DATA_SOURCE.MYSQL.REFRESH_AGAIN');
				}

				//loading
				uiLoadingSrv.removeLoading(angular.element('.pt-ds-table:eq(' + index + ')'));
				if (dataWarningFlag) {
					scope.showTips('dataWarning');
				} else {
					scope.showTips("fileRefresh");
				}
			})

			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"data_source",
				"what":"data_sources_manage_updatge_table_schema",
				"how":"click",
				"value": scope.myOptions.currentDs.code
			});	
		}

		//文件-解绑提示
		scope.removeFileShow = function (file, index) {

			//loading
			uiLoadingSrv.createLoading(angular.element('.ds-gd-content li:eq(' + index + ')'));

			//获取当前账户下的widget数量
			dataMutualSrv.get(LINK_GET_SOURCE_WIDGET_COUNT + file.sourceId).then(function (data) {
				if (data.status == 'success') {
					scope.myOptions.fileDelete.fileWidgtCount = data.content || 0;
					scope.myOptions.fileDelete.file = file;
					scope.myOptions.fileDelete.index = index;
					scope.showTips('fileDelete');
				} else {
					if (data.status == 'failed') {
						console.log('Get accountsList Failed!');
					} else if (data.status == 'error') {
						console.log('Get accountsList Error: ');
						console.log(data.message)
					}
				}

				//loading
				uiLoadingSrv.removeLoading(angular.element('.ds-gd-content li:eq(' + index + ')'));
			});

			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"data_source",
				"what":"data_sources_manage_remove _connection_table",
				"how":"click",
				"value": scope.myOptions.currentDs.code
			});	
		};

		//文件-解绑
		scope.removeFile = function (fileSourceId, index) {

			//loading
			uiLoadingSrv.createLoading(angular.element('.ds-gd-content'));

			dataMutualSrv.post(LINK_DEL_SAVEDFILE + fileSourceId).then(function (data) {
				if (data.status == 'success') {
					scope.myOptions.file.list.splice(index, 1);

					for (var i = 0; i < scope.myOptions.file.allList.length; i++) {
						if (scope.myOptions.file.allList[i].sourceId == fileSourceId) {
							scope.myOptions.file.allList.splice(i, 1);
						}
					}

					scope.dsAuthUpdata(scope.myOptions.currentDs, 'del', 'file');
				} else {
					if (data.status == 'failed') {
						console.log('Failed!')
					} else if (data.status == 'error') {
						console.log('Error: ');
						console.log(data.message)
					}
				}

				//loading
				uiLoadingSrv.removeLoading(angular.element('.ds-gd-content'));
				scope.close();
			})
		};

		//文件-时区设置
		scope.setTimezone = function(file, index){
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
				scope.myOptions.timezone.show = true;
				scope.myOptions.timezone.fieds = data;
				scope.myOptions.timezone.fieds.name = file.name;
				scope.myOptions.timezone.fieds.dsId = file.dsId;
				scope.myOptions.timezone.fieds.connectionId = file.connectionId;
				scope.myOptions.timezone.fieds.sourceId = file.sourceId;
			})
			.finally(() => {
				uiLoadingSrv.removeLoading(angular.element('.pt-ds-table:eq(' + index + ')'));
			})
		};

		//文件-时区设置隐藏
		scope.hideTimezone = function(){
			scope.myOptions.timezone = {
				show: false,
				fieds: null
			}
		};

		//文件-切换按钮切换
		scope.chooseConnection = function(){
			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"select_table",
				"what":"data_sources_manage_choose_connection",
				"how":"click",
				"value": scope.myOptions.currentDs.code
			});
		};

		//文件-设置按钮切换
		scope.setConnection = function(){
			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"select_table",
				"what":"data_sources_manage_setting",
				"how":"click",
				"value": scope.myOptions.currentDs.code
			});
		}

		//列表[远端]-请求账户下文档列表
		scope.getFileList = function (id, type, name) {
			scope.myOptions.current.mod = 'fileList';

			//从widget编辑器跳转过来
			if (scope.rootTmpData.dataSources) {
				for (var i = 0; i < scope.myOptions.account.list.length; i++) {
					if (scope.myOptions.account.list[i].connectionId == scope.rootTmpData.dataSources.connectionId) {
						scope.myOptions.account.currentAccount = scope.myOptions.account.list[i];
						scope.myOptions.account.currentAccountCid = scope.myOptions.account.list[i].connectionId;
						scope.rootTmpData.dataSources = null;
						break;
					}
				}
			}

			//当查看All列表时,链接新文件选择当前账户下关联的第一个账户
			if (scope.myOptions.account.currentAccountCid == 'all') {

				for (var i = 0; i < scope.myOptions.account.list.length; i++) {
					if ($rootScope.userInfo.ptId == scope.myOptions.account.list[i].uid) {

						scope.myOptions.account.currentAccount = scope.myOptions.account.list[i];
						scope.myOptions.account.currentAccountCid = scope.myOptions.account.list[i].connectionId;
						break;
					}
				}

				//更新文件列表
				scope.getSingleFileList(scope.myOptions.account.list[0]);
			}

			//loading
			uiLoadingSrv.createLoading(angular.element('.ds-file-bd'));

			var url = LINK_GET_SCHEMA + scope.myOptions.account.currentAccountCid + "/" + id + '?refresh=true';
			dataMutualSrv.get(url).then(function (data) {
				if (data.status == 'success') {
					$(window).scrollTop(0);
					//目录刷新成功提示
					scope.myOptions.accountDataRefresh.tipsInfo = $translate.instant('DATA_SOURCE.MYSQL.ACCOUNT_REFRESH_SUCCESS');
					scope.myOptions.accountDataRefresh.btnClass = 'pt-btn-success';
					scope.myOptions.accountDataRefresh.btnText = $translate.instant('COMMON.OK');

					scope.myOptions.account.data = scope.myOptions.account.dataCopy = angular.fromJson(data.content)[0];

					if (id == 'ptoneRootFolderID::connection') {
						$('.ds-file-breadcrumb').html('<a id="' + id + '" class="guide">' + scope.myOptions.account.currentAccount.name + '</a>');
					}

					if (type && type == 'isDirectory') {
						$('.ds-file-breadcrumb').append('<svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-chevron-small-right"></use></svg><a id="' + id + '" class="guide">' + name + '</a>');
					}

					scope.myOptions.current.directory = id;
				} else {
					//目录刷新失败提示
					scope.myOptions.accountDataRefresh.tipsInfo = $translate.instant('DATA_SOURCE.MYSQL.ACCOUNT_REFRESH_FAILURE');
					scope.myOptions.accountDataRefresh.btnClass = 'pt-btn-success';
					scope.myOptions.accountDataRefresh.btnText = $translate.instant('DATA_SOURCE.MYSQL.REFRESH_AGAIN');

					if (data.status == 'failed') {
						console.log('Get accountsList Failed!');
					} else if (data.status == 'error') {
						console.log('Get accountsList Error: ');
						console.log(data.message)
					}

					scope.showTips('getFileList');

					//清空前端页面数据
					$('.ds-file-breadcrumb').html('');
					scope.myOptions.account.dataCopy.child = [];
				}

				if (type && type == 'refresh') {
					scope.showTips('accountRefresh');
				}

				//loading
				uiLoadingSrv.removeLoading(angular.element('.ds-file-bd'));
			});

			//全站事件统计
			if (type && type == 'refresh') {
				siteEventAnalyticsSrv.createData({
				    "uid": $rootScope.userInfo.ptId,
				    "where":"data_source",
					"what": "select_table_refresh_1",
					"how": "click",
					"value": scope.myOptions.currentDs.code
				});	
			}
			else if (type && type == 'selectNewTable') {
				siteEventAnalyticsSrv.createData({
				    "uid": $rootScope.userInfo.ptId,
				    "where":"data_source",
					"what": "data_sources_manage_select_new_table",
					"how": "click",
					"value": scope.myOptions.currentDs.code
				});	
			}
			else if (type && type == 'settingSelectNewTable'){
				siteEventAnalyticsSrv.createData({
				    "uid": $rootScope.userInfo.ptId,
				    "where":"data_source",
					"what": "data_sources_manage_setting_select_new_table",
					"how": "click",
					"value": scope.myOptions.currentDs.code
				});	
			}
		};

		//列表-当前数据源下已存所有文档
		scope.getSavedFileList = function () {
			//文件列表请求状态
			scope.myOptions.file.queryStatus = 'querying';
			//loading
			uiLoadingSrv.createLoading(angular.element('.ds-gd-content'));

			dataMutualSrv.get(LINK_GET_AUTH_ACCOUNT + scope.rootSpace.current.spaceId + '/' + scope.myOptions.account.currentAccountCid + "/" + scope.myOptions.currentDs.id).then(function (data) {
				if (data.status != 'success') {
					if (data.status == 'failed') {
						console.log('Get SavedFileList Failed!');
					} else if (data.status == 'error') {
						console.log('Get SavedFileList Error: ');
						console.log(data.message)
					}
				} else {
					scope.myOptions.file.allList = data.content;
					scope.getSingleFileList(scope.myOptions.account.currentAccount);

					//默认进来
					if (scope.dsCtrl.goToMode && scope.dsCtrl.goToMode == 'fileAdd') {
						scope.getFileList('ptoneRootFolderID::connection');
						scope.dsCtrl.goToMode = null;
					}

					console.log('已存文件列表： ');
					console.log(scope.myOptions.file.allList)
				}
				//文件列表请求状态
				scope.myOptions.file.queryStatus = data.status;
				//loading
				uiLoadingSrv.removeLoading(angular.element('.ds-gd-content'));
			});
		};

		//列表-某个账户下的已存文档
		scope.getSingleFileList = function (account) {
			if (scope.myOptions.account.currentAccountCid == 'all') {
				scope.myOptions.file.list = angular.copy(scope.myOptions.file.allList);

				//区分当前所选文件与当前账户的关系: owner(创建者), view(查看者);
				// 注: 如果查看的是所有文件列表(All)时,需要判断所有账户列表内,是否存在当前账户, 有则显示创建文件,否则显示创建账号
				var flag = false;
				for (var i = 0; i < scope.myOptions.account.list.length; i++) {
					if ($rootScope.userInfo.ptId == scope.myOptions.account.list[i].uid) {
						flag = true;
						break;
					}
				}
				if (flag) {
					scope.myOptions.account.currentAddBtnType = 'owner';
				} else {
					scope.myOptions.account.currentAddBtnType = 'view';
				}
			} else {
				scope.myOptions.file.list = [];
				for (var i = 0; i < scope.myOptions.file.allList.length; i++) {
					if (scope.myOptions.account.currentAccountCid == scope.myOptions.file.allList[i].connectionId) {
						scope.myOptions.file.list.push(scope.myOptions.file.allList[i])
					}
				}

				//区分当前所选文件与当前账户的关系: owner(创建者), view(查看者);
				if (scope.myOptions.account.currentAccount.uid != $rootScope.userInfo.ptId) {
					scope.myOptions.account.currentAddBtnType = 'view';
				} else {
					scope.myOptions.account.currentAddBtnType = 'owner';
				}
			}
		};

		//表单-显示(关系型数据库连接)
		scope.connectionShow = function (type, from) {
			scope.myOptions.current.mod = 'connectionPost';

			scope.myOptions.connection.from = from;
			scope.myOptions.connection.operateType = type;
			scope.myOptions.connection.show = true;

			if (type == 'edit') {
				//loading
				uiLoadingSrv.createLoading(angular.element('.ds-table-list'));

				dataMutualSrv.get(LINK_GET_USER_CONNECTION_CONFIG + scope.myOptions.account.currentAccountCid).then(function (data) {
					if (data.status == 'success') {
						var config = angular.fromJson(data.content);

						scope.myOptions.connection.modHostName = config.host;
						scope.myOptions.connection.modPort = config.port;
						scope.myOptions.connection.modUserName = config.user;
						scope.myOptions.connection.modPassword = config.password;
						scope.myOptions.connection.modDatabase = config.dataBaseName;
						scope.myOptions.connection.modConnectionName = config.connectionName;
						scope.myOptions.connection.sshModSwitch = config.ssh == 1 ? true : false;
						scope.myOptions.connection.sshModHostName = config.sshHost;
						scope.myOptions.connection.sshModPort = config.sshPort ? config.sshPort : scope.myOptions.connection.configSshPort;
						scope.myOptions.connection.sshModUserName = config.sshUser;
						scope.myOptions.connection.sshModAuthMethod = config.sshAuthMethod ? config.sshAuthMethod : 'password';
						scope.myOptions.connection.sshModPassword = config.sshPassword;
						scope.myOptions.connection.sshModPrivateKey = config.sshPrivateKey;
						scope.myOptions.connection.sshModPassphrase = config.sshPassphrase;
						scope.myOptions.connection.modAccessKeyId = config.accessKeyId;
						scope.myOptions.connection.modSecretAccessKey = config.secretAccessKey;
					} else {
						if (data.status == 'failed') {
							console.log('Get accountsList Failed!');
						} else if (data.status == 'error') {
							console.log('Get accountsList Error: ');
							console.log(data.message)
						}
					}

					//loading
					uiLoadingSrv.removeLoading(angular.element('.ds-table-list'));
				})

				//全站事件统计
				siteEventAnalyticsSrv.createData({
				    "uid": $rootScope.userInfo.ptId,
				    "where":"select_table",
					"what":"data_sources_manage_setting_edit_connection",
					"how":"click",
					"value": scope.myOptions.currentDs.code
				});
			} else {
				scope.myOptions.connection.modHostName = null;
				scope.myOptions.connection.modPort = scope.myOptions.connection.configPort;
				scope.myOptions.connection.modUserName = null;
				scope.myOptions.connection.modPassword = null;
				scope.myOptions.connection.modDatabase = null;
				scope.myOptions.connection.modConnectionName = null;
				scope.myOptions.connection.modAccessKeyId = null;
				scope.myOptions.connection.modSecretAccessKey = null;

				scope.myOptions.connection.sshModSwitch = false;
				scope.myOptions.connection.sshModHostName = null;
				scope.myOptions.connection.sshModPort = scope.myOptions.connection.configSshPort;
				scope.myOptions.connection.sshModUserName = null;
				scope.myOptions.connection.sshModAuthMethod = 'password';
				scope.myOptions.connection.sshModPassword = null;
				scope.myOptions.connection.sshModPrivateKey = null;
				scope.myOptions.connection.sshModPassphrase = null;

				//全站事件统计
				if(from == 'noData'){
					siteEventAnalyticsSrv.createData({
					    "uid": $rootScope.userInfo.ptId,
					    "where":"data_source",
						"what":"add_new_connection",
						"how":"click",
						"value": scope.myOptions.currentDs.code
					});
				}
				else {
					siteEventAnalyticsSrv.createData({
					    "uid": $rootScope.userInfo.ptId,
					    "where":"data_source",
						"what":"select_table_add_new_connection",
						"how":"click",
						"value": scope.myOptions.currentDs.code
					});
				}
			}

			jQuery('.js_connection_ipt').removeClass('error').next('.text-danger').addClass('hide');
		};

		//表单-隐藏(关系型数据库连接)
		scope.connectionHide = function () {
			scope.myOptions.connection.show = false;
			scope.myOptions.current.mod = scope.myOptions.connection.from;

			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"data_source",
				"what":"add_new_connection_cancel",
				"how":"click",
				"value": scope.myOptions.currentDs.code
			});
		};

		//表单-失去焦点数据校验(关系型数据库连接)
		scope.blurVerify = function (e) {
			if (angular.element(e.target).hasClass('js_connection_ipt')) {
				if (angular.element(e.target).val() == '') {
					angular.element(e.target).addClass('error').next('.text-danger').removeClass('hide');
				} else {
					angular.element(e.target).removeClass('error').next('.text-danger').addClass('hide');
				}
			}
		};

		//表单-数据校验(关系型数据库连接)
		scope.dataVerify = function () {
			var flag = true;
			jQuery('.js_connection_ipt').each(function () {
				if (jQuery(this).val() == '') {
					jQuery(this).addClass('error').next('.text-danger').removeClass('hide');
					flag = false;
				} else {
					jQuery(this).removeClass('error').next('.text-danger').addClass('hide');
				}
			});

			return flag;
		};

		//表单-数据发送(关系型数据库连接、S3)
		scope.connectionPost = function (type) {
			scope.close();

			if (scope.dataVerify()) {
				if (type == 'edit') {
					//loading
					uiLoadingSrv.createLoading(angular.element('.add-database-bd'));

					//获取当前账户下的widget数量
					dataMutualSrv.get(LINK_GET_ACCOUNT_WIDGET_COUNT + scope.myOptions.account.currentAccount.connectionId + "/" + scope.myOptions.currentDs.id).then(function (data) {
						if (data.status == 'success') {
							scope.myOptions.account.widgtCount = data.content || 0;
							scope.showTips('connectEdit');
						} else {
							if (data.status == 'failed') {
								console.log('Get accountsList Failed!');
							} else if (data.status == 'error') {
								console.log('Get accountsList Error: ');
								console.log(data.message)
							}
						}

						//loading
						uiLoadingSrv.removeLoading(angular.element('.add-database-bd'));
					});

					return false;
				}

				var url = LINK_SAVE_DB_CONNECTION;
				if (type != 'test') {
					scope.myOptions.connection.operateType = type;
				} else {
					url = LINK_TEST_DB_CONNECTION;
				}

				//loading
				uiLoadingSrv.createLoading(angular.element('.add-database-bd'));

				var sendData = {
					spaceId: scope.rootSpace.current.spaceId,
					dataBaseType: scope.myOptions.currentDs.code,
					dsCode: scope.myOptions.currentDs.code,
					dsId: scope.myOptions.currentDs.id,
					host: scope.myOptions.connection.modHostName,
					port: scope.myOptions.connection.modPort,
					user: scope.myOptions.connection.modUserName,
					password: scope.myOptions.connection.modPassword,
					dataBaseName: scope.myOptions.connection.modDatabase,
					connectionName: scope.myOptions.connection.modConnectionName,
					operateType: scope.myOptions.connection.operateType,
					accessKeyId: scope.myOptions.connection.modAccessKeyId,
					secretAccessKey: scope.myOptions.connection.modSecretAccessKey
				};
				if (scope.myOptions.connection.sshModSwitch) {
					sendData['ssh'] = scope.myOptions.connection.sshModSwitch ? 1 : 0;
					sendData['sshHost'] = scope.myOptions.connection.sshModHostName;
					sendData['sshPort'] = scope.myOptions.connection.sshModPort;
					sendData['sshUser'] = scope.myOptions.connection.sshModUserName;
					sendData['sshAuthMethod'] = scope.myOptions.connection.sshModAuthMethod;
					sendData['sshPassword'] = scope.myOptions.connection.sshModPassword;
					sendData['sshPrivateKey'] = scope.myOptions.connection.sshModPrivateKey;
					sendData['sshPassphrase'] = scope.myOptions.connection.sshModPassphrase;
				}
				if (['edit', 'edit_save'].indexOf(scope.myOptions.connection.operateType) >= 0) {
					sendData['connectionId'] = scope.myOptions.account.currentAccountCid;
				}

				dataMutualSrv.post(url, sendData).then(function (data) {
					if (data.status == 'success') {
						/*
						 content: {
						 config: "{"connectionName":"ptone4","dataBaseType":"mysql","host":"192.168.1.2","operateType":"save","password":"ptone","port":"3306","tableName":"","user":"ptone"}"
						 connectionId: "2b7458d9-1677-40e3-a8e9-a1294ecce910"
						 dsCode: "mysql"
						 dsId: null
						 id: null
						 name: "ptone4"
						 status: "1"
						 uid: "1552"
						 userName: "xxxx"
						 updateTime: 1452743584513
						 }
						 */
						console.log(data);

						if (type == 'test') {
							scope.showTips('connectSuccess');
						} else if (type == 'edit_save') {
							scope.showTips('connectEditSuccess');

							//前端数据(Account)更新
							for (var i = 0; i < scope.myOptions.account.list.length; i++) {
								if (data.content.connectionId == scope.myOptions.account.list[i].connectionId) {
									scope.myOptions.account.list[i] = data.content;
									break;
								}
							}
							//
							if (data.content.connectionId == scope.myOptions.account.currentAccountCid) {
								scope.myOptions.account.currentAccount = data.content;
							}
							//路径修改
							for (var i = 0; i < scope.myOptions.file.allList.length; i++) {
								if (data.content.connectionId == scope.myOptions.file.allList[i].connectionId) {
									var remotePath = scope.myOptions.file.allList[i].remotePath;
									remotePath = remotePath.replace(remotePath.split('@#*')[0], data.content.name)
									scope.myOptions.file.allList[i].remotePath = remotePath;
								}
							}
						} else {
							scope.myOptions.account.currentAccountCid = data.content.connectionId;
							scope.myOptions.account.currentAccount = data.content;

							scope.getAccountList('add'); //更新账户列表

							
						}
					} else {
						if (data.status == 'failed') {
							console.log(data.message);

							var tipsCode = '';
							if (data.message.indexOf(' | ') > 0) {
								tipsCode = data.message.split(' | ')[0];
							}

							if (scope.myOptions.currentDs.code == 's3') {
								scope.myOptions.connection.connectFailure = $translate.instant('DATA_SOURCE.S3.' + tipsCode);
							} else {
								if (tipsCode != 'MSG_DB_LINK_FAILURE' && tipsCode != 'MSG_FAILED') {
									scope.myOptions.connection.connectFailure = $translate.instant('DATA_SOURCE.MYSQL.' + tipsCode);
								} else {
									scope.myOptions.connection.connectFailure = $translate.instant('DATA_SOURCE.MYSQL.MSG_FAILED_1') + scope.myOptions.connection.modHostName + $translate.instant('DATA_SOURCE.MYSQL.MSG_FAILED_2');
								}
							}
							scope.showTips('connectFailure');
						} else if (data.status == 'error') {
							console.log('Connect Error: ');
							console.log(data.message)
						}
					}

					//loading
					uiLoadingSrv.removeLoading(angular.element('.add-database-bd'));
				})

				//全站事件统计
				if (type == 'test') {
					siteEventAnalyticsSrv.createData({
					    "uid": $rootScope.userInfo.ptId,
					    "where":"data_source",
						"what":"add_new_connection_test_connection",
						"how":"click",
						"value": scope.myOptions.currentDs.code
					});
				} 
				else if (type == 'edit_save') {

				}
				else {
					siteEventAnalyticsSrv.createData({
					    "uid": $rootScope.userInfo.ptId,
					    "where":"data_source",
						"what":"add_new_connection_connect",
						"how":"click",
						"value": scope.myOptions.currentDs.code
					});
				}
			}
		};

		//表单-回车事件绑定(关系型数据库连接)
		scope.panelCopyKeyup = function (e) {
			var keycode = window.event ? e.keyCode : e.which;
			if (keycode == 13) {
				scope.connectionPost(scope.myOptions.connection.operateType);
			}
		};

		//表单-SSH开关(关系型数据库连接)
		scope.sshSwitch = function () {

		};

		//提示-显示
		scope.showTips = function (type) {
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
				//表单连接测试成功
				case "connectSuccess":
					options.title = scope.myOptions.i18n.tipsConnectSuccessTitle;
					options.info = scope.myOptions.i18n.tipsConnectSuccessInfo;
					options.btnRightClass = 'pt-btn-success';
					options.btnLeftHide = 'true';
					break;
				//表单连接失败
				case "connectFailure":
					options.title = scope.myOptions.i18n.tipsConnectFailureTitle;
					options.info = scope.myOptions.connection.connectFailure;
					options.btnRightClass = 'pt-btn-success';
					options.btnLeftHide = 'true';
					break;
				//表单修改
				case "connectEdit":
					if (scope.myOptions.currentDs.code == 's3') {
						options.info = $translate.instant('DATA_SOURCE.S3.EDIT_TIPS_1') + scope.myOptions.file.list.length + $translate.instant('DATA_SOURCE.S3.EDIT_TIPS_2') + scope.myOptions.account.widgtCount + $translate.instant('DATA_SOURCE.S3.EDIT_TIPS_3');
					} else {
						options.info = $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_0') + scope.myOptions.currentDs.name + $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_1') + scope.myOptions.file.list.length + $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_2') + scope.myOptions.account.widgtCount + $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_3');
					}
					options.title = scope.myOptions.i18n.tipsConnectEditTitle;
					options.btnRightEvent = 'connectionPost(\"edit_save\")';
					options.btnRightClass = 'pt-btn-success';
					break;
				//表单修改成功
				case "connectEditSuccess":
					options.info = scope.myOptions.i18n.tipsConnectEditSuccessInfo;
					options.btnRightText = $translate.instant('COMMON.OK');
					options.btnRightClass = 'pt-btn-success';
					options.btnRightEvent = 'close(\"tableList\")';
					options.btnLeftHide = 'true';
					options.hdHide = 'true';
					break;
				//目录刷新
				case "accountRefresh":
					options.info = scope.myOptions.accountDataRefresh.tipsInfo;
					options.btnRightClass = scope.myOptions.accountDataRefresh.btnClass;
					options.btnRightText = scope.myOptions.accountDataRefresh.btnText;
					options.btnLeftHide = 'true';
					options.hdHide = 'true';
					break;
				//获取文件目录失败
				case "getFileList":
					options.info = $translate.instant('DATA_SOURCE.MYSQL.PULL_DATA_ERROR', $rootScope.productConfigs);
					options.btnRightClass = 'pt-btn-success';
					options.btnLeftHide = 'true';
					options.hdHide = 'true';
					break;
				//文件解绑
				case "fileDelete":
					options.info = scope.myOptions.fileDelete.fileWidgtCount + $translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_FILE_1') + scope.myOptions.fileDelete.file.name + $translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_FILE_2');
					options.btnRightText = $translate.instant('DATA_SOURCE.MANAGEMENT.BTN_REMOVE');
					options.btnRightEvent = 'removeFile(\"' + scope.myOptions.fileDelete.file.sourceId + '\",\"' + scope.myOptions.fileDelete.index + '\")';
					options.hdHide = 'true';
					break;
				//账户添加
				case "accountAdd":
					options.info = $translate.instant('DATA_SOURCE.ADWORDS.SAVE_SUCCESS_TIP_1') + scope.myOptions.accountAdd.accout + $translate.instant('DATA_SOURCE.ADWORDS.SAVE_SUCCESS_TIP_2');
					options.btnLeftText = $translate.instant('DATA_SOURCE.ADWORDS.BTN_ADD_ACCOUNT');
					options.btnRightText = $translate.instant('DATA_SOURCE.ADWORDS.BTN_CREATE_WIDGET');
					options.btnRightClass = 'pt-btn-success';
					options.btnRightEvent = '$state.go("pt.dashboard")';
					options.hdHide = 'true';
					break;
				//账户解绑
				case "accountDelete":
					options.info = scope.myOptions.account.widgtCount + $translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_ACCOUNT_1') + scope.myOptions.account.currentAccount.name + $translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_ACCOUNT_2');
					options.btnRightText = $translate.instant('DATA_SOURCE.MANAGEMENT.BTN_REMOVE');
					options.btnRightEvent = 'removeAccount(\"' + scope.myOptions.account.currentAccountCid + '\")';
					options.hdHide = 'true';
					break;
				//获取文件内容失败
				case "getFileDataError":
					options.info = $translate.instant('DATA_SOURCE.MYSQL.PULL_DATA_ERROR', $rootScope.productConfigs);
					options.btnRightClass = 'pt-btn-success';
					options.hdHide = 'true';
					break;
				//文件编辑时、文件数据有错提示
				case "editFileDataError":
					options.info = $translate.instant('DATA_SOURCE.MYSQL.' + scope.myOptions.fileEdit.errorCode);
					options.btnRightClass = 'pt-btn-default';
					options.btnLeftHide = 'true';
					options.hdHide = 'true';
					break;
				//当前选择文件内容为空
				case "getFileDataEmpty":
					options.info = $translate.instant('DATA_SOURCE.MYSQL.DATA_EMPTY');
					options.btnRightClass = 'pt-btn-default';
					options.hdHide = 'true';
					break;
				//刷新或编辑file时，远端数据发生变化
				case "dataWarning":
					options.info = $translate.instant('DATA_SOURCE.MYSQL.DATA_CHANGE_TIP');
					options.btnRightClass = 'pt-btn-success';
					options.btnLeftHide = 'true';
					options.hdHide = 'true';
					break;
				//文件刷新
				case "fileRefresh":
					options.info = scope.myOptions.fileRefresh.tipsInfo;
					options.btnRightClass = scope.myOptions.fileRefresh.btnClass;
					options.btnRightText = scope.myOptions.fileRefresh.btnText;
					options.btnLeftHide = 'true';
					options.hdHide = 'true';
					break;
			}

			scope.myOptions.tips.show = true;
			scope.myOptions.tips.options = options;
		};


		//Init
		(function () {
			angular.element('#js_dsLoading').remove();

			var dsCode = attrs.dscode;
			var i18nCode = angular.uppercase(attrs.i18ncode);
			var dsConfig = getMyDsConfig(dsCode);
			scope.myOptions.i18nCode = i18nCode;
			console.log(i18nCode);

			//国际化文案
			scope.myOptions.i18n = {
				//头部
				'hdTitleManage': $translate.instant('DATA_SOURCE.' + i18nCode + '.HD_TITLE_MANAGE'),
				'hdIntroduce': $translate.instant('DATA_SOURCE.' + i18nCode + '.HD_INTRODUCE', $rootScope.productConfigs),
				'hdConnectFile': $translate.instant('DATA_SOURCE.MYSQL.CONNECTION_HD_ADD_TIPS'),
				'hdEditFile': $translate.instant('DATA_SOURCE.' + i18nCode + '.CONNECTION_HD_EDIT_TIPS'),
				'hdTitleSub': $translate.instant('DATA_SOURCE.MYSQL.CONNECTION_TITLE_SUB_TIPS', $rootScope.productConfigs),

				//按钮
				'btnConnectNewFile': $translate.instant('DATA_SOURCE.MYSQL.NEW_FILE'),
				'btnConnectNewAccount': $translate.instant('DATA_SOURCE.MANAGEMENT.ADD_CONNECT'),
				'btnTestConnection': $translate.instant('DATA_SOURCE.MYSQL.TSET'),
				'btnConnection': $translate.instant('DATA_SOURCE.MYSQL.CONNECT'),
				'btnCancelConnection': $translate.instant('COMMON.CANCEL'),
				'btnAllAccount': $translate.instant('DATA_SOURCE.MYSQL.ALL_ACCOUNT'),
				'btnAccountEdit': $translate.instant('DATA_SOURCE.MYSQL.ACCOUNT_EDIT'),
				'btnRemoveAccount': $translate.instant('DATA_SOURCE.MYSQL.BTN_REMOVE'),
				'btnEditFile': $translate.instant('DATA_SOURCE.MYSQL.BTN_EDIT'),
				'btnRefreshFile': $translate.instant('DATA_SOURCE.MYSQL.BTN_REFRESH'),

				//页面提示文字
				'tipsAddAccount': $translate.instant('DATA_SOURCE.MYSQL.ADD_ACCOUNT_TIPS'),
				'tipsAddConnection': $translate.instant('DATA_SOURCE.' + i18nCode + '.CONNECTION_TITLE_ADD_INTRODUCE'),
				'tipsAddConnectionSub': $translate.instant('DATA_SOURCE.' + i18nCode + '.CONNECTION_TITLE_SUB_ADD_INTRODUCE'),
				'tipsEditConnection': $translate.instant('DATA_SOURCE.' + i18nCode + '.CONNECTION_TITLE_EDIT_INTRODUCE'),
				'tipsEditConnectionSub': $translate.instant('DATA_SOURCE.' + i18nCode + '.CONNECTION_TITLE_SUB_EDIT_INTRODUCE', $rootScope.productConfigs),
				'tipsConnectionIntroduce': $translate.instant('DATA_SOURCE.' + i18nCode + '.HD_TITLE_MANAGE'),
				'tipsOwner': $translate.instant('DATA_SOURCE.MYSQL.OWNER'),
				'tipsNoFile': $translate.instant('DATA_SOURCE.MYSQL.NO_FILE'),
				'tipsAccountNoFile': $translate.instant('DATA_SOURCE.MYSQL.ACCOUNT_NO_FILE'),
				'tipsConnected': $translate.instant('DATA_SOURCE.MYSQL.CONNECTED'),
				'tipsRefresh': $translate.instant('DATA_SOURCE.MYSQL.REFRESH_TIPS'),
				'tipsEmptyFolder': $translate.instant('DATA_SOURCE.MYSQL.EMPTY_TIPS'),

				//弹出框提示信息
				'tipsConnectSuccessTitle': $translate.instant('DATA_SOURCE.MYSQL.MSG_SUCCESS_TITLE'),
				'tipsConnectSuccessInfo': $translate.instant('DATA_SOURCE.MYSQL.MSG_SUCCESS'),
				'tipsConnectFailureTitle': $translate.instant('DATA_SOURCE.MYSQL.MSG_FAILED_TITLE'),
				'tipsConnectFailureInfo': $translate.instant('DATA_SOURCE.MYSQL.MSG_FAILED_TITLE'),
				'tipsConnectEditTitle': $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_TITLE'),
				'tipsConnectEditSuccessInfo': $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_SUCCESS'),
				'tipsGetFileListFailureInfo': $translate.instant('DATA_SOURCE.MYSQL.PULL_DATA_ERROR', $rootScope.productConfigs),

				//表单信息
				'tableConnectionName': $translate.instant('DATA_SOURCE.MYSQL.CONNECTION_NAME'),
				'tableErrorEmptyCName': $translate.instant('DATA_SOURCE.MYSQL.ERROR_EMPTY_CNAME'),
				'tableHostName': $translate.instant('DATA_SOURCE.MYSQL.HOST_NAME'),
				'tableErrorEmptyHostName': $translate.instant('DATA_SOURCE.MYSQL.ERROR_EMPTY_HOSTNAME'),
				'tablePort': $translate.instant('DATA_SOURCE.MYSQL.PORT'),
				'tableErrorEmptyPort': $translate.instant('DATA_SOURCE.MYSQL.ERROR_EMPTY_PORT'),
				'tableUserName': $translate.instant('DATA_SOURCE.MYSQL.USER_NAME'),
				'tableErrorEmptyUserName': $translate.instant('DATA_SOURCE.MYSQL.ERROR_EMPTY_USERNAME'),
				'tablePassword': $translate.instant('DATA_SOURCE.MYSQL.PASSWORD'),
				'tableErrorEmptyPassword': $translate.instant('DATA_SOURCE.MYSQL.ERROR_EMPTY_PASSWORD'),
				'tableDatabase': $translate.instant('DATA_SOURCE.MYSQL.DATABASE'),
				'tableErrorEmptyDatabase': $translate.instant('DATA_SOURCE.' + i18nCode + '.ERROR_EMPTY_DATABASE'),
				'tableSSH': $translate.instant('DATA_SOURCE.MYSQL.SSH_SWITCH'),
				'tableSSHHostName': $translate.instant('DATA_SOURCE.MYSQL.SSH_HOST_NAME'),
				'tableErrorEmptySSHHostName': $translate.instant('DATA_SOURCE.MYSQL.ERROR_EMPTY_HOSTNAME'),
				'tableSSHPort': $translate.instant('DATA_SOURCE.MYSQL.SSH_PORT'),
				'tableErrorEmptySSHPort': $translate.instant('DATA_SOURCE.MYSQL.ERROR_EMPTY_PORT'),
				'tableSSHUserName': $translate.instant('DATA_SOURCE.MYSQL.SSH_USER_NAME'),
				'tableErrorEmptySSHUserName': $translate.instant('DATA_SOURCE.MYSQL.ERROR_EMPTY_USERNAME'),
				'tableSSHAuthMethod': $translate.instant('DATA_SOURCE.MYSQL.SSH_AUTH_METHOD'),
				'tableSSHAuthPassword': $translate.instant('DATA_SOURCE.MYSQL.SSH_AUTH_PASSWORD'),
				'tableSSHErrorEmptyPassword': $translate.instant('DATA_SOURCE.MYSQL.ERROR_EMPTY_PASSWORD'),
				'tableSSHAuthPrivateKey': $translate.instant('DATA_SOURCE.MYSQL.SSH_AUTH_PRIVATE_KEY'),
				'tableSSHErrorEmptyPrivateKey': $translate.instant('DATA_SOURCE.MYSQL.ERROR_EMPTY_PRIVATE_KEY'),
				'tableSSHAuthPassPhrase': $translate.instant('DATA_SOURCE.MYSQL.SSH_AUTH_PASSPHRASE'),

				//远端目录
				'remoteConnectFile': $translate.instant('DATA_SOURCE.MYSQL.CONNECT_FILE'),
				'remoteAddFileTipsTitle': $translate.instant('DATA_SOURCE.' + i18nCode + '.ADD_FILE_TIPS_TITLE'),
				'remoteAddFileTipsInfo': $translate.instant('DATA_SOURCE.MYSQL.ADD_FILE_TIPS_INFO'),
				
				//时区提示信息
				'tipsTimezoneCode': 'DATA_SOURCE.TIMEZONE.NO_SUPPORT'
			};

			if(['MYSQL', 'MYSQL_AMAZONRDS', 'SQLSERVER'].indexOf(i18nCode)>=0){
				scope.myOptions.i18n.tipsTimezoneCode = 'DATA_SOURCE.TIMEZONE.MYSQL_SUPPORT';
			}

			//数据源官网URL地址
			//scope.myOptions.link.webSiteHref = dsConfig.dataSource.ds.webSiteHref;

			//不同数据源不同配置
			switch (dsCode) {
				case "postgre":
					scope.myOptions.connection.modPort = 5432;
					scope.myOptions.connection.configPort = 5432;
					scope.myOptions.i18n.tipsAddAccount = $translate.instant('DATA_SOURCE.MYSQL_POSTGRE.ADD_ACCOUNT_TIPS');
					break;
				case "redshift":
					scope.myOptions.connection.modPort = 5439;
					scope.myOptions.connection.configPort = 5439;
					scope.myOptions.i18n.tipsAddAccount = $translate.instant('DATA_SOURCE.MYSQL_REDSHIFT.ADD_ACCOUNT_TIPS');
					break;
				case "standardRedshift":
					scope.myOptions.connection.modPort = 5439;
					scope.myOptions.connection.configPort = 5439;
					scope.myOptions.i18n.tipsAddAccount = $translate.instant('DATA_SOURCE.MYSQL_REDSHIFT.ADD_ACCOUNT_TIPS');
					break;
				case "bigquery":
					scope.myOptions.i18n.tipsAddAccount = $translate.instant('DATA_SOURCE.BIGQUERY.ADD_ACCOUNT_TIPS');
					break;
				case "auroraAmazonRds":
					scope.myOptions.i18n.tipsAddAccount = $translate.instant('DATA_SOURCE.MYSQL_AURORAAMAZONRDS.ADD_ACCOUNT_TIPS');
					break;
				case "sqlserver":
					scope.myOptions.connection.modPort = 1433;
					scope.myOptions.connection.configPort = 1433;
					scope.myOptions.i18n.tipsAddAccount = $translate.instant('DATA_SOURCE.SQLSERVER.ADD_ACCOUNT_TIPS');
					break;
			}


			//数据初始化
			var judgeData = setInterval(function () {
				if (scope.rootCommon.dsList.length > 0) {
					clearInterval(judgeData);

					scope.myOptions.currentDs = angular.copy(scope.getDsInfo(dsCode));
					scope.dsCtrl.editDs = angular.copy(scope.getDsInfo(dsCode));

					//从widget编辑器跳转过来
					if (scope.rootTmpData.dataSources) {
						//var ds = scope.getDsInfo(scope.rootTmpData.dataSources.dsCode);
						if (scope.myOptions.currentDs.code == scope.rootTmpData.dataSources.dsCode) {
							scope.dsCtrl.goToMode = scope.rootTmpData.dataSources.type;
						}
					}

					if (scope.dsCtrl.dsDataTep[scope.myOptions.currentDs.code]) {
						scope.$apply(function () {
							scope.myOptions.pageLoad = true;
							scope.myOptions.account.list = angular.copy(scope.myOptions.account.list);
							scope.myOptions.file.list = angular.copy(scope.myOptions.file.list);
							scope.myOptions.file.queryStatus = 'success';
						})
					} else {
						scope.dsCtrl.dsDataTep[scope.myOptions.currentDs.code] = {
							'accountList': [],
							'fileAllList': [],
							'fileList': [],
							'currentAccount': null,
							'currentAccountCid': null,
							'query': {fileAllList: null, accountList: null}
						};
						scope.myOptions.file.queryStatus = null;
					}
					scope.getAccountList('first');
				}
			}, 50);
		})()
	}
}

export default tmpMysqlDirective;
