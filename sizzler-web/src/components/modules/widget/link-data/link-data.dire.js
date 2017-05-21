/**
 * linkData
 * widget模板批量授权
 *
 */
import tpl from 'components/modules/widget/link-data/link-data.tpl.html';

import {
	LINK_DATA_SOURCE_VIEW,
	LINK_GET_DS_PROFILES,
	LINK_PROFILE_API_REMOTE,
	LINE_DS_PROFILES,
	LINK_DATASOURCE_CATEGORYS,
	LINK_SALESFORCE_OBJECTS,
	LINK_WIDGET_LIST_EDIT,
	LINK_AUTHOR,
	DATA_SOURCE_WEB_SOCKET,
	getDsInfo,
	uuid,
	openWindow
} from 'components/modules/common/common';

import 'assets/libs/jquery/slimscroll/jquery.slimscroll.min.js';
import treeUtils from 'utils/tree-adaptor.utils';
import errorCodes from 'configs/errorcode.config';

angular
.module('pt')
.directive('linkData', linkData);

linkData.$inject = ['$rootScope', '$document', '$translate', '$timeout', '$state', 'websocket', 'dataMutualSrv', 'uiLoadingSrv', 'linkDataSrv', 'publicDataSrv', 'siteEventAnalyticsSrv'];

function linkData($rootScope, $document, $translate, $timeout, $state, websocket, dataMutualSrv, uiLoadingSrv, linkDataSrv, publicDataSrv, siteEventAnalyticsSrv) {
	return {
		restrict: 'EA',
		template: tpl,
		link: link
	};

	function link(scope, element, attrs) {

		var widgetList = [];
		var sendWidget = scope.sendWidget = {
			"baseWidget": {
				"widgetId": null,
				"creatorId": null,
				"templetId": null
			},
			"variables": [{
				"variableId": null,
				"ptoneDsInfoId": null,
				"accountName": null,
				"profileId": null,
				"dimensions": null,
				"metrics": null,
				'segment': null,
				'filters': null
			}]
		};

		/**
		 * 基础数据定义
		 */
		scope.myOptions = {

			//数据源相关
			ds: {
				current: null,
				dsId: null,
				dsCode: null,
				config: null
			},

			//账户相关
			account: {
				current: null,
				list: null
			},

			//档案相关
			profile: {
				type: null,     //数据源类型(mysql, googleanalysis, other)
				current: null,  //当前选中
				list: null,     //档案列表
				listTmp: null,  //搜索备用
				query: null,    //档案列表请求状态(querying, success, failed, error)
				messageCode: null//错误信息
			},

			//搜索相关
			search: {
				dsSearchKey: null,
				dsSearchIndex: {},      //控制子展开与否
				dsSearchIndexChild: {}  //控制孙展开与否
			},

			//其他
			other: {
				currentStep: 'source',  //当前显示步骤
				showStep: true,         //是否显示第三步
				authorize: false,       //授权中//验证中
				authorizeFailure: false,//验证失败
				btnDisabled: true,      //保存按钮状态
				isHasPermission: true
			},

			//GA专用
			ga: {
				copyGaAccount: '',
				propertieList: []   //GA专用
			}
		};
		scope.gaSearchKey = null;

		//function
		scope.tab = tab;
		scope.selectDs = selectDs;
		scope.selectAccount = selectAccount;
		scope.selectProfile = selectProfile;
		scope.accredit = accredit;
		scope.save = save;
		scope.close = close;
		scope.searchGaAccount = searchGaAccount;
		scope.searchDsAccount = searchDsAccount;
		scope.restDsAccountList = restDsAccountList;
		scope.goToDsManage = goToDsManage;


		//数据初始化
		dataInit();

		$document.bind('click', documentClickBindLinkData);


		//显示已选择的数据源名称
		scope.showSelectedDs = function (dsId) {
			if (!scope.myOptions.other.isHasPermission) {
				return scope.myOptions.ds.dsCode;
			} else {
				var name = null;

				angular.forEach(scope.rootCommon.dsList, function (ds, index) {
					if (ds.id == dsId) {
						name = ds.name;
					}
				});
				if (name) {
					return name;
				}
			}
		};

		//监听是否有更改数据源
		scope.$watch('rootWidget.linkData.currentDs.id', function (newValue, oldValue) {
			if (newValue === oldValue) {
				return;
			}
			dataInit();
		});

		//==================================

		/**
		 * 数据初始化
		 */
		function dataInit() {
			dataReset();

			//当前账户下没有此数据源权限,则提示
			if (scope.rootWidget.linkData.currentDs.type == 'noPermission') {
				element.find('.step-a').addClass('no-border');
				scope.myOptions.other.currentStep = 'source';
				scope.myOptions.other.isHasPermission = false;
				return;
			}

			scope.myOptions.ds.current = getDsInfo(scope.rootWidget.linkData.currentDs.id, scope.rootCommon.dsList);
			scope.myOptions.ds.dsId = scope.myOptions.ds.current.id;
			scope.myOptions.ds.dsCode = scope.myOptions.ds.current.code;
			scope.myOptions.ds.config = scope.myOptions.ds.current.config;

			if (scope.myOptions.ds.dsId && scope.myOptions.account.current && scope.myOptions.profile.current) {
				element.find('.step-a, .step-b, .step-c').addClass('source-box-pass');
				tab('profile');
			} else if (scope.myOptions.ds.dsId && scope.myOptions.account.current) {
				element.find('.step-a, .step-b').addClass('source-box-pass');
				tab('profile');
			} else if (scope.myOptions.ds.dsId) {
				element.find('.step-a').addClass('source-box-pass');
				tab('account');
			}

			//部分数据源不能直接显示第三步
			if (scope.myOptions.ds.config.linkData.hideStepThree) {
				scope.myOptions.other.showStep = false
			}
		}

		/**
		 * 数据重置
		 */
		function dataReset() {
			scope.myOptions = {

				//数据源相关
				ds: {
					current: null,
					dsId: null,
					dsCode: null,
					config: null
				},

				//账户相关
				account: {
					current: null,
					list: null
				},

				//档案相关
				profile: {
					type: null,     //数据源类型(mysql, googleanalysis, other)
					current: null,  //当前选中
					list: null,     //档案列表
					listTmp: null,  //搜索备用
					query: null,    //档案列表请求状态(querying, success, failed, error)
					messageCode: null//错误信息
				},

				//搜索相关
				search: {
					dsSearchKey: null,
					dsSearchIndex: {},      //控制子展开与否
					dsSearchIndexChild: {}  //控制孙展开与否
				},

				//其他
				other: {
					currentStep: 'source',  //当前显示步骤
					showStep: true,         //是否显示第三步
					authorize: false,       //授权中//验证中
					authorizeFailure: false,//验证失败
					btnDisabled: true,      //保存按钮状态
					isHasPermission: true,  //是否具有数据源权限
					disabledClick: false    //是否屏蔽全屏点击事件判断(主要用于授权打开标签页用)
				},

				//GA专用
				ga: {
					copyGaAccount: '',
					propertieList: []   //GA专用
				}
			};

			scope.rootWidget.linkData.dsChange = false;
			element.find('.step-a').removeClass('no-border');
		}

		/**
		 * 步骤切换
		 */
		function tab(type) {
			scope.myOptions.other.currentStep = type;

			if (type == 'account') {
				getAuthAccounts(scope.myOptions.ds.dsId);
			}
			else if (type == 'profile') {
				scope.myOptions.profile.query = 'querying';

				var accountName = scope.myOptions.account.current.name;
				var connectionId = scope.myOptions.account.current.connectionId;
				if (scope.myOptions.ds.dsCode == 'upload') {
					accountName = 'upload';
					connectionId = 'all';
				}

				getProfileList(accountName, connectionId, scope.myOptions.ds.dsId, scope.myOptions.ds.dsCode);
			}
		}

		/**
		 * 数据源选择
		 */
		function selectDs(ds) {
			if (ds.dsCode == 'upload') {
				scope.myOptions.profile.query = 'querying';
				scope.getProfileList('upload', 'all', scope.myOptions.ds.dsId, scope.myOptions.ds.dsCode);
			} else {
				scope.myOptions.other.currentStep = 'account';
				getAuthAccounts(ds.dsId);
			}
		}

		/**
		 * 获取已授权账户列表
		 */
		function getAuthAccounts(dsId) {
			uiLoadingSrv.createLoading(angular.element(element).find('.editor-pop'));

			dataMutualSrv.get(LINK_DATA_SOURCE_VIEW + scope.rootSpace.current.spaceId + '/' + dsId).then(function (data) {
				if (data.status == 'success') {
					//如果已授权账户为空，则显示授权页面
					scope.myOptions.account.list = angular.copy(data.content);

					if (angular.isDefined(scope.myOptions.account.list) && scope.myOptions.account.list.length > 0 && !scope.myOptions.account.current) {
						//scope.myOptions.account.current = scope.myOptions.account.list[0];
						scope.myOptions.profile.current = null;
					}

					if (scope.myOptions.account.current && scope.myOptions.ds.config.linkData.hideStepThree) {
						//针对账号级别授权的数据源只需到账号即可.

						scope.myOptions.other.btnDisabled = false;
					}
				} else {
					console.log('link data get accountsList Failed!');
					if (data.status == 'error') {
						console.log(data.message);
					}
				}

				uiLoadingSrv.removeLoading(angular.element(element).find('.editor-pop'));
			});
		}

		/**
		 * 账户选择
		 */
		function selectAccount(account) {
			scope.myOptions.account.current = account;

			if (scope.myOptions.ds.config.linkData.hideStepThree) {
				//针对账号级别授权的数据源只需到账号即可.

				scope.myOptions.other.btnDisabled = false;
			} else {
				scope.myOptions.profile.query = 'querying';
				scope.myOptions.other.btnDisabled = true;
				scope.myOptions.profile.current = null;
				scope.gaSearchKey = null;
				scope.myOptions.search.dsSearchKey = null;
				scope.myOptions.search.dsSearchIndex = {};
				scope.myOptions.search.dsSearchIndexChild = {};

				//获取档案列表
				getProfileList(account.name, account.connectionId, account.dsId, account.dsCode);
			}
		}

		/**
		 * 获取档案列表
		 */
		function getProfileList(accountName, connectionId, dsId, dsCode) {
			var url, config = scope.myOptions.ds.config.editor.source;
			scope.myOptions.other.currentStep = 'profile';
			scope.myOptions.profile.type = dsCode;

			switch (config.getProfileType) {
				case "api":
					url = LINK_GET_DS_PROFILES + '/' + dsCode + '/' + connectionId + '/' + accountName;
					break;
				case "apiRemote":
					url = LINK_PROFILE_API_REMOTE + connectionId + '/' + dsCode + '/' + accountName;
					break;
				case "table":
					url = LINE_DS_PROFILES + scope.rootSpace.current.spaceId + '/' + connectionId + '/' + dsId;
					scope.myOptions.profile.type = 'mysql';
					break;
				case "category":
					url = LINK_DATASOURCE_CATEGORYS + '/' + dsId;
					break;
				case "salesforce":
					url = LINK_SALESFORCE_OBJECTS;
					break;
			}

			//如果account没改变，并且已存在列表旧数据，则复用旧数据
			var getDataFlag = true;
			var dsAccountProfile = publicDataSrv.getPublicData('dsAccountProfile');
			if (!config.profileOfNormal) {
				// 取数前,先查看是否以存储可复用的数据列表(当存储的列表为空时,也需要重新取数)
				if (dsAccountProfile[dsId] && dsAccountProfile[dsId][connectionId] && dsAccountProfile[dsId][connectionId].length > 0) {
					scope.myOptions.profile.list = angular.copy(dsAccountProfile[dsId][connectionId]);
					scope.myOptions.profile.listTmp = angular.copy(dsAccountProfile[dsId][connectionId]);

					//切换至下一步
					getProfileListAfter('success');

					getDataFlag = false;
				}
			}
			if (getDataFlag) {
				_getList();
			}

			//save data
			function _getList() {
				scope.myOptions.profile.list = [];
				scope.myOptions.profile.listTmp = [];

				dataMutualSrv.get(url).then(function (data) {
					var list = [];
					if (data.status == 'success') {

						if (dsCode == 'facebookad') {

							list = data.content.accountList;
						} else if (dsCode == 'doubleclick' || dsCode == 'doubleclickCompound') {

							list = data.content.userProfiles;

							//由于字段名不统一,在前端将name加上.统一调用
							for (var i = 0; i < list.length; i++) {
								list[i]['name'] = list[i].userName;
							}
						} else if (dsCode == 'googleanalysis') {
							list = treeUtils.formatGAProfileList(data.content);
						} else {
							list = data.content;
						}
					} else {
						console.log('link data get profile list failed!');
						if (data.status == 'error') {
							console.log(data.message)
						}
					}

					scope.myOptions.profile.list = angular.copy(list);
					scope.myOptions.profile.listTmp = angular.copy(list);
					//存储备用
					if (!config.profileOfNormal) {
						publicDataSrv.setPublicData('dsAccountProfile', dsId, connectionId, angular.copy(list));
					}

					var messageCode = null;
					if (dsCode == 'facebookad') {
						messageCode = data.message;
					}
					//切换至下一步
					getProfileListAfter(data.status, messageCode, data.code);
				})
			}
		}

		/**
		 * getProfileListAfter
		 * 取档案列表之后,统一处理
		 */
		function getProfileListAfter(status, message, code) {
			scope.myOptions.other.currentStep = 'profile';
			scope.myOptions.profile.query = status;

			if(code){
				message = errorCodes[code] && errorCodes[code].i18nKey;
			}
			message = message || 'WIDGET.EDITOR.ACCOUNT.GET_PROFILE_ERROR';

			var errorInfo = $translate.instant(message);
			errorInfo = errorInfo.replace("{spaceId}", scope.rootSpace.current.domain);
			scope.myOptions.profile.messageCode = errorInfo;

			$timeout(function(){
				angular.element('.source-box.step-c .search-list .bd').slimscroll({
					height: '160px',
					size:'6px',
					allowPageScroll: false
				});
			},100);
		}

		/**
		 * 选择档案
		 */
		function selectProfile(profile) {
			if (scope.myOptions.ds.dsCode == 'googleanalysis') {
				scope.gaSearchKey = profile.profileName;
			} else {
				scope.myOptions.search.dsSearchKey = profile.name;
			}

			scope.myOptions.profile.current = profile;
			scope.myOptions.other.btnDisabled = false;
		}

		/**
		 * 跳转到数据源Account管理界面
		 */
		function goToDsManage(dsCode) {
			dsCode = dsCode || scope.rootWidget.linkData.currentDs.code;
			$state.go('pt.dataSources.' + dsCode);
		};

		/**
		 * 重置搜索列表
		 */
		function restDsAccountList(data) {
			scope.myOptions.profile.list = data;
		}

		/**
		 * 保存
		 */
		function save() {
			widgetList = [];
			sendWidget.variables[0].ptoneDsInfoId = scope.myOptions.ds.dsId;
			sendWidget.variables[0].accountName = scope.myOptions.account.current.name;
			sendWidget.variables[0].connectionId = scope.myOptions.account.current.connectionId;

			if (!scope.myOptions.ds.config.linkData.hideStepThree) {
				sendWidget.variables[0].profileId = scope.myOptions.profile.current.id;
			}

			//获取需要授权的widget信息,查找模板数据并替换
			for (var i = 0; i < scope.rootWidget.list.length; i++) {
				if (scope.rootWidget.list[i].baseWidget.widgetType != 'tool' && angular.isDefined(scope.rootWidget.list[i].baseWidget.isExample) && scope.rootWidget.list[i].baseWidget.isExample == 1 && scope.rootWidget.list[i].variables[0].ptoneDsInfoId == scope.myOptions.ds.dsId) {
					//更新发送数据
					sendWidget.baseWidget.isExample = 0;
					sendWidget.baseWidget.isDemo = 0;
					sendWidget.baseWidget.widgetId = scope.rootWidget.list[i].baseWidget.widgetId;
					sendWidget.baseWidget.creatorId = scope.rootWidget.list[i].baseWidget.creatorId;
					if (scope.rootWidget.list[i].baseWidget.widgetType == 'chart') {
						sendWidget.variables[0].variableId = scope.rootWidget.list[i].variables[0].variableId;
						sendWidget.variables[0].segment = scope.rootWidget.list[i].variables[0].segment;
						sendWidget.variables[0].filters = scope.rootWidget.list[i].variables[0].filters;

						sendWidget.variables[0].dimensions = angular.copy(scope.rootWidget.list[i].variables[0].dimensions);
						sendWidget.variables[0].metrics = angular.copy(scope.rootWidget.list[i].variables[0].metrics);
					}

					widgetList.push(angular.copy(sendWidget));
				}
			}

			updataWidget(widgetList);
		}

		/**
		 * widget更新
		 */
		function updataWidget(widgetData) {
			dataMutualSrv.post(LINK_WIDGET_LIST_EDIT, widgetData).then(function (data) {
				if (data.status == 'success') {
					//更新前端数据
					for (var i = 0; i < scope.rootWidget.list.length; i++) {
						for (var j = 0; j < widgetList.length; j++) {
							if (scope.rootWidget.list[i].baseWidget.widgetId == widgetList[j].baseWidget.widgetId) {
								scope.rootWidget.list[i].baseWidget.isTemplate = 0;
								scope.rootWidget.list[i].baseWidget.isExample = 0;
								scope.rootWidget.list[i].baseWidget.isDemo = 0;

								if (scope.rootWidget.list[i].baseWidget.widgetType == 'chart') {
									scope.rootWidget.list[i].variables[0].ptoneDsInfoId = sendWidget.variables[0].ptoneDsInfoId;
									scope.rootWidget.list[i].variables[0].accountName = sendWidget.variables[0].accountName;
									scope.rootWidget.list[i].variables[0].connectionId = sendWidget.variables[0].connectionId;
									scope.rootWidget.list[i].variables[0].segment = widgetList[j].variables[0].segment;
									scope.rootWidget.list[i].variables[0].filters = widgetList[j].variables[0].filters;

									if (!scope.myOptions.ds.config.linkData.hideStepThree) {
										scope.rootWidget.list[i].variables[0].profileId = sendWidget.variables[0].profileId;
									}
								}
							}
						}
					}


					//授权层隐藏
					scope.rootWidget.linkData.showDire = false;
					scope.rootWidget.linkData.currentDs = null;
					linkDataSrv.update(scope);
				} else if (data.status == 'failed') {
					console.log('Post Data Failed!')
				} else if (data.status == 'error') {
					console.log('Post Data Error: ');
					console.log(data.message)
				}
			});
		}

		/**
		 * 关闭
		 */
		function close() {
			scope.rootWidget.linkData.showDire = false;
			scope.rootWidget.linkData.currentDs = null;
		}

		/**
		 * 授权
		 */
		function accredit() {
			scope.myOptions.other.disabledClick = true;
			var accreditSocket = new websocket;
			var sign = uuid();
			var url = LINK_AUTHOR + scope.rootWidget.linkData.currentDs.code + '?ptOneUserEmail=' + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign + '&spaceId=' + scope.rootSpace.current.spaceId;
			var socketUrl = DATA_SOURCE_WEB_SOCKET + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign;

			if (scope.myOptions.ds.dsCode == 'ptengine') {
				url = '/signin/ptengine';
				localStorage.setItem('ptengineLoginReferrer', 'dataSources');
				localStorage.setItem('ptengineAuthSign', sign);
				localStorage.setItem('ptengineSpaceId', scope.rootSpace.current.spaceId);

				socketUrl = DATA_SOURCE_WEB_SOCKET + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign + '&spaceId=' + scope.rootSpace.current.spaceId;
			}

			accreditSocket.initWebSocket(socketUrl);

			//授权验证跳转
			openWindow(url);
			scope.myOptions.other.authorize = true;
			scope.myOptions.other.disabledClick = false; //在打开新标签后,即可恢复(针对全屏点击事件的判断)
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
					scope.myOptions.other.authorize = false;


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
						var dsFlag = false;
						var index = 0;
						for (var i = 0; i < scope.rootCommon.dsAuthList.length; i++) {
							if (scope.rootCommon.dsAuthList[i].dsId == scope.myOptions.ds.dsId) {
								dsFlag = true;
								index = i;
								break;
							}
						}

						//已存数据源列表更新
						if (dsFlag) {
							++scope.rootCommon.dsAuthList[index].accountNum;
						} else {
							scope.rootCommon.dsAuthList.push({
								accountNum: 1,
								dsCode: scope.myOptions.ds.current.code,
								dsName: scope.myOptions.ds.current.name,
								dsId: scope.myOptions.ds.current.id,
								dsConfig: scope.myOptions.ds.current.config,
                                dsOrderNumber: scope.myOptions.ds.current.orderNumber + '',
                                isPlus: 2
							})
						}

						scope.myOptions.account.list.push(angular.copy(newValue.content.connectionInfo));

						//默认选中第一个账户
						if (!scope.myOptions.account.current && scope.myOptions.account.list.length > 0) {
							//scope.myOptions.account.current = scope.myOptions.account.list[0];//避免舞蹈用户，不再选中第一个账户，置灰处理，让用户自己去选
							scope.myOptions.profile.current = null;
						}
					}
				} else {
					scope.myOptions.other.authorizeFailure = true;
					scope.myOptions.other.authorize = false;
				}
			});
		}

		/**
		 * 档案搜索(GA专用)
		 */
		function searchGaAccount() {
			if (!scope.myOptions.profile.listTmp) {
				return;
			}
			if (scope.gaSearchKey) {
				var reg = new RegExp('.*?' + scope.gaSearchKey + '.*?', "i");
				var tempCopyGaAccount = angular.copy(scope.myOptions.profile.listTmp);
				$.each(scope.myOptions.profile.listTmp, function (o, one) {
					var oneDelFlag = true;
					$.each(one['webproperties'], function (t, two) {
						var twoDelFlag = true;
						$.each(two['profiles'], function (e, three) {
							//不包含则删除第三层
							if (!reg.test(three['accountName']) && !reg.test(three['webpropertyName']) && !reg.test(three['profileName'])) {
								getObject(tempCopyGaAccount, 'profileId', three['profileId']).Remove();
							}
							if (reg.test(three['profileName']) || reg.test(three['webpropertyName'])) {
								//则第二层 不能删
								twoDelFlag = false;
								scope.myOptions.ga.propertieList[o].p[t] = true;
							}
							if (reg.test(three['profileName']) || reg.test(three['webpropertyName']) || reg.test(three['accountName'])) {
								//则第一层 不能删
								oneDelFlag = false;
								scope.myOptions.ga.propertieList[o].t = true;
							}
						});
						if (twoDelFlag) {
							getObject(tempCopyGaAccount, 'webpropertyId', two['webpropertyId']).Remove();
						}
					});
					if (oneDelFlag) {
						getObject(tempCopyGaAccount, 'accountId', one['accountId']).Remove();
					}
				});
				scope.myOptions.profile.list = tempCopyGaAccount;
			} else {
				scope.myOptions.profile.list = scope.myOptions.profile.listTmp;
			}
		}

		/**
		 * 搜索回调(一级目录)
		 */
		function collapseAllItem(flag) {
			if (scope.myOptions.search.dsSearchIndex) {
				$timeout(function () {
					$.each(scope.myOptions.search.dsSearchIndex, function (i) {
						scope.myOptions.search.dsSearchIndex[i] = flag;
					});
				});
			}
		}

		/**
		 * 搜索回调(二级目录)
		 */
		function collapseAllItem2(flag) {
			if (scope.myOptions.search.dsSearchIndex) {
				$timeout(function () {
					$.each(scope.myOptions.search.dsSearchIndex, function (i) {
						scope.myOptions.search.dsSearchIndex[i] = flag;
					});
					$.each(scope.myOptions.search.dsSearchIndexChild, function (i) {
						$.each(scope.myOptions.search.dsSearchIndexChild[i], function (j) {
							scope.myOptions.search.dsSearchIndexChild[i][j] = flag;
						})
					});
				});
			}
		}

		/**
		 * 档案搜索
		 */
		function searchDsAccount(key, tierData, temp, fun) {
			scope.myOptions.search.dsSearchKey = key;
			if (scope.myOptions.ds.config.editor.source.threeLayer) {
				uiSearch.search(key, 3, tierData, temp, fun, collapseAllItem2);
			} else {
				uiSearch.search(key, 2, tierData, temp, fun, collapseAllItem);
			}
		}

		/**
		 * 获取搜索对象
		 */
		function getObject(Origin, Key, Value) {
			var T, F;
			var secondChildren = 'webproperties';
			var thirdChildren = 'profiles';
			for (F = Origin.length; F--;) {
				if (Value === (T = Origin[F])[Key]) {
					return {
						Obj: T,
						Index: F,
						Parent: Origin,
						Remove: function () {
							T === Origin[F] && Origin.splice(F, 1)
						}
					}
				} else if (secondChildren in T) {
					T = getObject(T[secondChildren], Key, Value, secondChildren)
					if (T) return T
				} else if (thirdChildren in T) {
					T = getObject(T[thirdChildren], Key, Value, thirdChildren)
					if (T) return T
				}
			}
		}

		/**
		 * 弹出框点击事件监听
		 */
		function documentClickBindLinkData(event) {
			if (!scope.myOptions.other.disabledClick && !element[0].contains(event.target) && !angular.element(event.target).hasClass('js-linkData') && !angular.element(event.target).parents('a').hasClass('js-linkData')) {
				$timeout(function () {
					scope.$apply(function () {
						close();
					})
				}, 0);
				$document.unbind('click', documentClickBindLinkData);
			}
		}
	}
}
