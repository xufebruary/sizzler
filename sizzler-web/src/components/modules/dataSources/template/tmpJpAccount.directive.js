'use strict';


/**
 * template - account
 * 数据源模版文件-账号型
 * @dataSources
 *
 */
import tpl from './tmpJpAccount.html';
import {
	getMyDsConfig,
	LINK_SAVE_API_CONNECTION,
	LINK_GET_ACCOUNT_WIDGET_COUNT,
	LINK_DEL_AUTH_ACCOUNT,
	LINK_DATA_SOURCE_VIEW,
	DATA_SOURCE_WEB_SOCKET,
	LINK_GET_USER_CONNECTION_CONFIG,
	LINK_AUTHOR,
	uuid,
	openWindow
} from 'components/modules/common/common';


tmpJpAccountDirective.$inject = ['$rootScope', '$translate', '$state', '$document', 'dataMutualSrv', 'websocket', 'uiLoadingSrv', 'DataSourcesServices']
function tmpJpAccountDirective($rootScope, $translate, $state, $document, dataMutualSrv, websocket, uiLoadingSrv, DataSourcesServices) {
    return {
        restrict: 'EA',
        template: tpl,
        link: link
    };

	function link(scope, elem, attrs) {
		var body = $document.find('body').eq(0);

		scope.myOptions = {
			currentDs: null,//当前数据源信息:{id:xxx,code:xxx}
			i18n: {},       //当前界面国际化文案
			link: {         //当前界面国际化链接地址
				webSiteHref: {}  //数据源官网URL地址:{zh_CN:'',en_US:'',ja_JP:''}
			},
			pageLoad: false,        //当前页面加载状态

			authorize: false,           //授权中
			authorizeFailure: false,    //验证失败
			accountList: [],            //已授权账户列表

			current: {
				mod: null           //当前显示模块('noData','tableList')
			},

			connection: {
				show: false,
				from: null,
				operateType: 'add',    //add || edit
				connectFailure: null,

				model : {
					yahooAdsYDN:[
						{titleI18nCode:"DATA_SOURCE.YAHOOADSYDN.CONNECTIONNAME", name:"connectionName", errorI18nCode:"DATA_SOURCE.YAHOOADSYDN.ERROR_CONNECTION_NAME_EMPTY", value:""},
						{titleI18nCode:"DATA_SOURCE.YAHOOADSYDN.APPACCOUNTID", name:"APP_ACCOUNT_ID", errorI18nCode:"DATA_SOURCE.YAHOOADSYDN.ERROR_APPACCOUNTID_EMPTY", value:""},
						{titleI18nCode:"DATA_SOURCE.YAHOOADSYDN.APPACCOUNTPWD", name:"APP_ACCOUNT_PASSWORD", errorI18nCode:"DATA_SOURCE.YAHOOADSYDN.ERROR_APPACCOUNTPWD_EMPTY", value:""},
						{titleI18nCode:"DATA_SOURCE.YAHOOADSYDN.ACCOUNTID", name:"ACCOUNT_ID", errorI18nCode:"DATA_SOURCE.YAHOOADSYDN.ERROR_ACCOUNTID_EMPTY", value:""}
					],
					yahooAdsSS:[
						{titleI18nCode:"DATA_SOURCE.YAHOOADSSS.CONNECTIONNAME", name:"connectionName", errorI18nCode:"DATA_SOURCE.YAHOOADSSS.ERROR_CONNECTION_NAME_EMPTY", value:""},
						{titleI18nCode:"DATA_SOURCE.YAHOOADSSS.APPACCOUNTID", name:"APP_ACCOUNT_ID", errorI18nCode:"DATA_SOURCE.YAHOOADSSS.ERROR_APPACCOUNTID_EMPTY", value:""},
						{titleI18nCode:"DATA_SOURCE.YAHOOADSSS.APPACCOUNTPWD", name:"APP_ACCOUNT_PASSWORD", errorI18nCode:"DATA_SOURCE.YAHOOADSSS.ERROR_APPACCOUNTPWD_EMPTY", value:""},
						{titleI18nCode:"DATA_SOURCE.YAHOOADSSS.ACCOUNTID", name:"ACCOUNT_ID", errorI18nCode:"DATA_SOURCE.YAHOOADSSS.ERROR_ACCOUNTID_EMPTY", value:""}
					]
				}
			},

			//账户相关
			account: {
				list: [],        //已授权账户列表
				data: [],        //当前账户账户下的数据
				dataCopy:[],     //当前账户账户下的数据(副本，导航使用的)
				widgtCount: 0,   //当前账户下widget数量
				queryStatus: null,//请求状态(querying, success, failed, error)
				currentAccountCid: null
			},

			//提示框
			tips:{
				show: false,
				options: {}
			},

			//解绑账户
			accountDel: {
				account: null,
				index: null,
				widgtCount: 0
			},

			//新增账户
			accountAdd: {
				accout: null,
				connectionId: null,
				tipsInfo: null
			},

			fileEdit:{
				errorCode: null
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
			scope.getFileList(scope.id);
			$(this).nextAll('svg,a').remove();
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
		 开启链接修改/添加页面
		 @param type String edit==修改，add==添加
		 @param from String 跳转前是在哪个mod上
		 */
		scope.connectionShow = function(type, _from,currentAccountCid,name){
			scope.myOptions.current.mod = 'connectionPost';
			scope.myOptions.connection.from = _from;
			scope.myOptions.connection.operateType = type;
			scope.myOptions.connection.show = true;
			if(scope.myOptions.account.currentAccount && currentAccountCid){
				scope.myOptions.account.currentAccount.connectionId = currentAccountCid;
			}

			if(type == 'edit'){
				//loading
				uiLoadingSrv.createLoading(angular.element('.ds-table-list'));

				dataMutualSrv.get(LINK_GET_USER_CONNECTION_CONFIG + currentAccountCid).then(function(data){
					if (data.status == 'success') {
						var config = angular.fromJson(data.content);
						scope.myOptions.connection.model[config.dsCode][0].value = name;
						scope.myOptions.connection.model[config.dsCode][1].value = config.APP_ACCOUNT_ID;
						scope.myOptions.connection.model[config.dsCode][2].value = config.APP_ACCOUNT_PASSWORD;
						scope.myOptions.connection.model[config.dsCode][3].value = config.ACCOUNT_ID;

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
			} else {
				var code = scope.myOptions.currentDs.code;
				scope.myOptions.connection.model[code][0].value = '';
				scope.myOptions.connection.model[code][1].value = '';
				scope.myOptions.connection.model[code][2].value = '';
				scope.myOptions.connection.model[code][3].value = '';
			}
			//隐藏错误信息提示
			jQuery('.js_connection_ipt').removeClass('error').next('.text-danger').addClass('hide');
		};

		/**
		 隐藏表单
		 */
		scope.connectionHide = function(){
			scope.myOptions.connection.show = false;
			scope.myOptions.current.mod = scope.myOptions.connection.from;
		}

		/**
		 表单提交
		 */
		scope.connectionPost = function(type){
			//首先验证数据是否正确
			if(scope.dataVerify()){
				console.log("数据正确");

				if(type == 'edit'){
					//loading
					uiLoadingSrv.createLoading(angular.element('.add-database-bd'));

					//获取当前账户下的widget数量
					dataMutualSrv.get(LINK_GET_ACCOUNT_WIDGET_COUNT + scope.myOptions.account.currentAccount.connectionId + "/" + scope.myOptions.currentDs.id).then(function(data) {
						if (data.status == 'success') {
							scope.myOptions.account.widgtCount = data.content || 0;
							scope.showTips('connectEditSuccess');
						} else {
							if (data.status == 'failed') {
								console.log('获取widget数量失败');
							} else if (data.status == 'error') {
								console.log('获取使用中的widget数量出错 ');
								console.log(data.message)
							}
						}
						//loading
						uiLoadingSrv.removeLoading(angular.element('.add-database-bd'));
					});

					return false;
				}
				scope.close();
				uiLoadingSrv.createLoading(angular.element('.add-database-bd'));
				var _dsCode = scope.myOptions.currentDs.code;
				var _dsId = scope.myOptions.currentDs.id;

				var url = LINK_SAVE_API_CONNECTION;

				var sendData = {
					connectionInfo:{
						spaceId: scope.rootSpace.current.spaceId,
						dsCode: _dsCode,
						dsId: _dsId
					},
					authInfo:{}
				};

				var _modelList = scope.myOptions.connection.model[_dsCode];
				for(var i=0; i<_modelList.length; i++){
					var _model = _modelList[i];
					if(_model.name == "connectionName"){
						//如果是链接名称，则放到connectionInfo中，存放的key改为name，是为了与userConnection对象中的name字段对应上
						sendData.connectionInfo['name'] = _model.value;
					}else{
						sendData.authInfo[_model.name] = _model.value;
					}
				}

				if ( ['edit', 'edit_save'].indexOf(scope.myOptions.connection.operateType)>=0 ) {
					sendData.connectionInfo['connectionId'] = scope.myOptions.account.currentAccount.connectionId;
				}

				dataMutualSrv.post(url, sendData).then(function(data) {
					if (data.status == 'success') {
						if(type === 'edit_save'){
							scope.showTips('accountEdit');
						}else{
							scope.showTips('accountAdd');
						}
						console.log("save api connection success");
						//更新列表数据
						//判断是否是修改了数据
						var flag = false;
						for (var i = 0; i < scope.myOptions.account.list.length; i++) {
							if(scope.myOptions.account.list[i].connectionId == data.content.connectionId){
								scope.myOptions.account.list[i] = data.content;
								flag = true;
								break;
							}
						}
						if(!flag){
							scope.myOptions.account.list.unshift(data.content);
						}
						scope.myOptions.current.mod = 'tableList';
						scope.dsAuthUpdata(scope.myOptions.currentDs, type, 'account');
					}else{
						//错误处理
						if(data.message){
							scope.myOptions.fileEdit.errorCode = data.message;
							scope.showTips('connectEditError');
						}else{
							scope.myOptions.fileEdit.errorCode = null;
							scope.showTips('connectEditError');
						}
					}
					uiLoadingSrv.removeLoading(angular.element('.add-database-bd'));
				});
			}
		}

		/**
		 表单校验，用于验证模型中的数据是否为空
		 */
		scope.dataVerify= function(){
			var flag = true;
			jQuery('.js_connection_ipt').each(function(){
				if(jQuery(this).val() == ''){
					jQuery(this).addClass('error').next('.text-danger').removeClass('hide');
					flag = false;
				} else {
					jQuery(this).removeClass('error').next('.text-danger').addClass('hide');
				}
			})

			return flag;
		}

		//表单-失去焦点数据校验(关系型数据库连接)
		scope.blurVerify = function(e){
			if(angular.element(e.target).val() == ''){
				angular.element(e.target).addClass('error').next('.text-danger').removeClass('hide');
			} else {
				angular.element(e.target).removeClass('error').next('.text-danger').addClass('hide');
			}
		}

		//表单-回车事件绑定(关系型数据库连接)
		scope.panelCopyKeyup = function(e){
			var keycode = window.event?e.keyCode:e.which;
			if(keycode==13){
				scope.connectionPost();
			}
		}

		/**
		 * dsSrv
		 * 数据源数据处理
		 */

		//账户-解绑提示
		scope.removeAccountShow = function(account){
			scope.myOptions.account.currentAccount = account;

			//loading
			uiLoadingSrv.createLoading(angular.element('.data-source-content'));

			//获取当前账户下的widget数量
			dataMutualSrv.get(LINK_GET_ACCOUNT_WIDGET_COUNT + scope.myOptions.account.currentAccount.connectionId + "/" + scope.myOptions.currentDs.id).then(function(data) {
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
		};

		scope.removeAccount = function(cid){
			//loading
			uiLoadingSrv.createLoading(angular.element('.pt-popup-content'));
			if(cid){
				dataMutualSrv.post(LINK_DEL_AUTH_ACCOUNT + cid).then(function(data) {
					if (data.status == 'success') {
						for (var i = 0; i < scope.myOptions.account.list.length; i++) {
							if(scope.myOptions.account.list[i].connectionId == cid){
								scope.myOptions.account.list.splice(i, 1);
							}
						}
						scope.dsAuthUpdata(scope.myOptions.currentDs, 'del', 'account');

						if(scope.myOptions.account.list.length==0){
							scope.myOptions.current.mod = 'noData';
						}
					} else {
						if (data.status == 'failed') {} else if (data.status == 'error') {}
					}

					scope.close();
					//loading
					uiLoadingSrv.removeLoading(angular.element('.pt-popup-content'));
				})
			}
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

		//账户-获取已授权账户列表
		scope.getAccountList = function(type) {
			//loading
			uiLoadingSrv.createLoading(angular.element('.ds-content'));
			dataMutualSrv.get(LINK_DATA_SOURCE_VIEW +scope.rootSpace.current.spaceId +'/'+ scope.myOptions.currentDs.id).then(function(data) {
				if (data.status == 'success') {
					scope.myOptions.account.list = data.content;
					if(scope.myOptions.account.list.length >0){
						if(!scope.myOptions.account.currentAccount || scope.myOptions.account.currentAccountCid !== scope.myOptions.account.currentAccount.connectionId){
							if(scope.myOptions.account.list.length == 1){
								scope.myOptions.account.currentAccount = scope.myOptions.account.list[0];
								scope.myOptions.account.currentAccountCid = scope.myOptions.account.list[0].connectionId;
							} else {
								scope.myOptions.account.currentAccount = {'name': $translate.instant('DATA_SOURCE.MYSQL.ALL_ACCOUNT')};
								scope.myOptions.account.currentAccountCid = 'all';
							}
						}

						if(scope.myOptions.currentDs.code == 'facebookad'){
							//facebook需要对授权的过期时间做判断，如果是过期了需要显示提示
							for(var i=0; i<scope.myOptions.account.list.length; i++){
								var _tempAccount = scope.myOptions.account.list[i];
								var _tempConfig = angular.fromJson(_tempAccount.config);
								var _expireTime = _tempConfig.expireTime;
								var _nowTime = new Date().getTime();
								if(_expireTime < _nowTime){
									//已过期，需要提示
									_tempAccount.showExpireTip = true;
								}
								scope.myOptions.account.list[i] = _tempAccount;
							}
						}

						if (['googleanalysis', 'googleadwords', 'facebookad', 'ptengine', 'salesforce', 'doubleclick', 'doubleclickCompound', 'paypal'].indexOf(scope.myOptions.currentDs.code)>=0) {
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
								//scope.getSavedFileList();
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
		scope.authorization = function(type){
			var accreditSocket = new websocket;
			var sign = uuid();
			if(scope.myOptions.currentDs.code == 'ptengine'){
				var url = '/signin/ptengine';
				localStorage.setItem('ptengineLoginReferrer','dataSources');
				localStorage.setItem('ptengineAuthSign',sign);
				localStorage.setItem('ptengineSpaceId',scope.rootSpace.current.spaceId);
				//链接socket;
				accreditSocket.initWebSocket(DATA_SOURCE_WEB_SOCKET + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail))+ '&sign=' + sign + '&spaceId=' + scope.rootSpace.current.spaceId);
			} else {
				var url = LINK_AUTHOR + scope.myOptions.currentDs.code + '?ptOneUserEmail=' + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign + '&spaceId=' + scope.rootSpace.current.spaceId;

				//链接socket;
				accreditSocket.initWebSocket(DATA_SOURCE_WEB_SOCKET + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign);
			}

			//授权验证跳转
			openWindow(url);
			scope.myOptions.authorize = true;

			//监听授权socket返回值
			scope.wsData = accreditSocket.colletion;
			accreditSocket.ws.onmessage = function(event) {
				scope.$apply(function() {
					scope.wsData = event.data;
				});
			};
			var mywatch = scope.$watch('wsData', function(newValue, oldValue, scope) {
				if (newValue === oldValue) {
					return;
				}

				//注销当前监听事件
				mywatch();
				newValue = eval('(' + newValue + ')');
				if (newValue.status == 'success') {

					//关闭socket
					accreditSocket.disconnect();
					scope.myOptions.authorize = false;

					console.log(newValue);

					//判断是否重复授权
					var flag = true;
					for (var i = 0; i < scope.myOptions.account.list.length; i++) {
						if(scope.myOptions.account.list[i].name == newValue.content.account){
							console.log('重复授权!');
							flag = false;

							//重新授权,需要更新提示状态(例如FaceBook ADS会有有效期,过期后需要重新授权)
							if((type && type == 'again') || scope.myOptions.currentDs.code == 'facebookad'){
								scope.myOptions.account.list[i].showExpireTip = false;
							}
							if(scope.myOptions.currentDs.code != 'facebookad'){
								break;
							}
						}
					}
					if(flag){
						//更新账户列表
						scope.myOptions.account.list.push(angular.copy(newValue.content.connectionInfo));
						scope.dsAuthUpdata(scope.myOptions.currentDs, 'add', 'account');
						scope.myOptions.current.mod = 'tableList';
					}

					scope.myOptions.accountAdd.accout = newValue.content.account;
					scope.myOptions.accountAdd.connectionId = newValue.content.connectionId;
					scope.myOptions.accountAdd.tipsInfo = $translate.instant('DATA_SOURCE.ADWORDS.SAVE_SUCCESS_TIP_1')+scope.myOptions.accountAdd.accout+$translate.instant('DATA_SOURCE.ADWORDS.SAVE_SUCCESS_TIP_2');

					if(type && type == 'again'){
						//重新授权(例如FaceBook ADS会有有效期,过期后需要重新授权)
						scope.myOptions.accountAdd.tipsInfo += $translate.instant("DATA_SOURCE.FBA.SAVE_SUCCESS_TIP_3") + $translate.instant("DATA_SOURCE.FBA.SAVE_SUCCESS_TIP_4");
					}
					scope.showTips('accountAdd');
				} else {
					scope.myOptions.authorizeFailure = true;
					scope.myOptions.authorize = false;
				}
			});
		};

		//提示-显示
		scope.showTips = function(type){
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
			switch(type){
				//获取文件目录失败
				case "getFileList":
					options.info = $translate.instant('DATA_SOURCE.MYSQL.PULL_DATA_ERROR', $rootScope.productConfigs);
					options.btnLeftHide = 'true';
					options.hdHide = 'true';
					break;
				//账户添加
				case "accountAdd":
					options.title = scope.myOptions.i18n.tipsConnectSuccessTitle;
					options.info = $translate.instant('DATA_SOURCE.MYSQL.MSG_SUCCESS_TITLE');
					options.btnLeftText = $translate.instant('DATA_SOURCE.ADWORDS.BTN_ADD_ACCOUNT');
					options.btnRightText = $translate.instant('DATA_SOURCE.ADWORDS.BTN_CREATE_WIDGET');
					options.btnRightClass = 'pt-btn-success';
					options.btnRightEvent = '$state.go("pt.dashboard")';
					options.hdHide = 'true';
					break;
				//账号编辑
				case 'accountEdit':
					options.title = scope.myOptions.i18n.tipsConnectSuccessTitle;
					options.info = scope.myOptions.i18n.tipsConnectSuccessInfo;
					options.btnRightEvent = 'close()';
					options.hdHide = 'true';
					options.btnLeftHide = 'true';
					options.btnRightClass = 'pt-btn-success';
					break;
				//账户解绑
				case "accountDelete":
					options.info = scope.myOptions.account.widgtCount+$translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_ACCOUNT_1')+scope.myOptions.account.currentAccount.name+$translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_ACCOUNT_2');
					options.btnRightText = $translate.instant('DATA_SOURCE.MANAGEMENT.BTN_REMOVE');
					options.btnRightEvent = 'removeAccount(\"'+scope.myOptions.account.currentAccount.connectionId+'\")';
					options.hdHide = 'true';
					break;
				case "connectEditSuccess":
					options.info = $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_0') + scope.myOptions.currentDs.name + $translate.instant('DATA_SOURCE.YAHOOADSYDN.EDIT_TIPS_1')+scope.myOptions.account.widgtCount + $translate.instant('DATA_SOURCE.YAHOOADSYDN.EDIT_TIPS_2');
					options.title = scope.myOptions.i18n.tipsConnectEditTitle;
					options.btnRightEvent = 'connectionPost(\"edit_save\")';
					options.btnRightClass = 'pt-btn-success';
					break;
				case "connectEditError":
					if(scope.myOptions.fileEdit.errorCode){
						options.info = $translate.instant('DATA_SOURCE.YAHOOADSYDN.' + scope.myOptions.fileEdit.errorCode);
					}else{
						options.info = $translate.instant('DATA_SOURCE.YAHOOADSYDN.ERROR_CONNECTION_FAILED_TIPS');
					}

					options.title = $translate.instant('DATA_SOURCE.YAHOOADSYDN.ERROR_CONNECTION_FAILED_TITLE');
					options.btnRightEvent = 'close()';
					options.btnLeftHide = 'true';
					options.btnRightClass = 'pt-btn-success';
					break;
			}

			scope.myOptions.tips.show = true;
			scope.myOptions.tips.options = options;
		};


		//Init
		(function(){
			angular.element('#js_dsLoading').remove();

			var dsCode = attrs.dscode;
			var i18nCode = angular.uppercase(attrs.i18ncode);
			var dsConfig = getMyDsConfig(dsCode);
			console.log(i18nCode);

			//国际化文案
			scope.myOptions.i18n = {
				//头部
				'hdTitleManage': $translate.instant('DATA_SOURCE.'+i18nCode+'.MANAGE'),
				'hdIntroduce': $translate.instant('DATA_SOURCE.'+i18nCode+'.INTRODUCE'),

				//修改链接信息说明
				'connectionHdEditTips':$translate.instant('DATA_SOURCE.'+i18nCode+'.CONNECTION_HD_EDIT_TIPS'),
				//添加链接信息说明
				'connectionHdAddTips':$translate.instant('DATA_SOURCE.'+i18nCode+'.CONNECTION_HD_ADD_TIPS'),
				//添加链接标题描述说明
				'connectionTitleAddIntroduce':$translate.instant('DATA_SOURCE.'+i18nCode+'.CONNECTION_TITLE_ADD_INTRODUCE'),
				//修改链接标题描述说明
				'connectionTitleEditIntroduce':$translate.instant('DATA_SOURCE.'+i18nCode+'.CONNECTION_TITLE_EDIT_INTRODUCE'),
				//添加链接时提交的描述说明
				'connectionTitleSubAddIntroduce':$translate.instant('DATA_SOURCE.'+i18nCode+'.CONNECTION_TITLE_SUB_ADD_INTRODUCE'),
				//修改链接时提交的描述说明
				'connectionTitleSubEditIntroduce':$translate.instant('DATA_SOURCE.'+i18nCode+'.CONNECTION_TITLE_SUB_EDIT_INTRODUCE', $rootScope.productConfigs),
				'tipsConnectSuccessTitle': $translate.instant('DATA_SOURCE.MYSQL.MSG_SUCCESS_TITLE'),
				'tipsConnectSuccessInfo': $translate.instant('DATA_SOURCE.YAHOOADSYDN.MSG_SUCCESS_TITLE'),
				'tipsConnectEditTitle': $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_TITLE'),


				//按钮
				'btnConnectNewAccount': $translate.instant('WIDGET.ADD_AUTHOR_ACCOUNT'),

				//页面提示文字
				'tipsAddAccount': $translate.instant('DATA_SOURCE.'+i18nCode+'.ADD_ACCOUNT_TIPS'),
				'tipsOwner': $translate.instant('DATA_SOURCE.MYSQL.OWNER'),

				//弹出框提示信息
				'tipsGetFileListFailureInfo': $translate.instant('DATA_SOURCE.'+i18nCode+'.PULL_DATA_ERROR', $rootScope.productConfigs),
				
				//时区提示信息
				'tipsTimezoneCode': 'DATA_SOURCE.TIMEZONE.NO_SUPPORT'
			};

			//数据源官网URL地址
			//scope.myOptions.link.webSiteHref = dsConfig.dataSource.ds.webSiteHref;

			//数据初始化
			var judgeData = setInterval(function() {
				if(scope.rootCommon.dsList.length>0){
					clearInterval(judgeData);

					scope.myOptions.currentDs = angular.copy(scope.getDsInfo(dsCode));
					scope.dsCtrl.editDs = angular.copy(scope.getDsInfo(dsCode));

					if(scope.dsCtrl.dsDataTep[scope.myOptions.currentDs.code]){
						scope.$apply(function(){
							scope.myOptions.pageLoad = true;
							scope.myOptions.account.list = angular.copy(scope.myOptions.account.list);
							scope.myOptions.account.queryStatus = 'success';
						})
					} else {
						scope.dsCtrl.dsDataTep[scope.myOptions.currentDs.code] = {
							'accountList': []
						};
						scope.myOptions.account.queryStatus = null;
					}
					scope.getAccountList('first');
				}
			}, 50);
		})()
	}
}

export default tmpJpAccountDirective;