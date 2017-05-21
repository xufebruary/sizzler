import {
	LINK_DATA_SOURCE_EDIT_VIEW,
	LINK_DATA_SOURCE_VIEW,
	LINK_GET_DS_PROFILES,
	LINK_PROFILE_API_REMOTE,
	LINE_DS_PROFILES,
	LINK_DATASOURCE_CATEGORYS,
	LINK_SALESFORCE_OBJECTS,
	LINK_AUTHOR,
	LINK_GET_DS_INFO,
	LINK_SALESFORCE_REPORT,
	DATA_SOURCE_WEB_SOCKET,
	getMyDsConfig,
	uuid,
	getDsInfo,
	openWindow
} from 'components/modules/common/common';

import 'assets/libs/jquery/slimscroll/jquery.slimscroll.min.js';
import treeUtils from 'utils/tree-adaptor.utils';
import errorCodes from 'configs/errorcode.config';

import tpl from './source.tpl.html';
import './source.css';


editorSource.$inject = ['$document', '$rootScope', '$timeout', '$state', '$translate', 'dataMutualSrv', 'websocket', 'siteEventAnalyticsSrv', 'uiSearch', 'linkDataSrv', 'uiLoadingSrv', 'publicDataSrv'];
function editorSource($document, $rootScope, $timeout, $state, $translate, dataMutualSrv, websocket, siteEventAnalyticsSrv, uiSearch, linkDataSrv, uiLoadingSrv, publicDataSrv) {
	return {
		restrict: 'EA',
		replace: true,
		template: tpl,
		link: link
	};

	function link(scope, elem, attrs) {
		scope.accountSettings = {
			dsAuthList: null,

			showSteps: false, //是否显示第二及后面的步骤
			showProfileIdStep: true,//是否显示第三步
			now: 'source',
			accountsList: '',   //已授权账户
			accountListNew: null,//已授权账户---简庆新增
			gaAccountList: '',  //GA-账户列表
			fbaAccountList: '', //facebook ads 账户列表
			ptengineAccountList: '', // ptengine 账户列表
			doubleclickAccountList: '',//doubleclick user profile list
			doubleclickCompoundAccountList: '',//doubleclick user profile list
			dsAccountList: '',
			copyDsAccount: '',
			copyGaAccount: '',  //GA-账户列表(搜索备用),
			copyGdAccount: '',  //GD-账户列表(搜索备用)
			copyDoubleclickAccount: '',//doubleclick user profile list
			copyDoubleclickCompoundAccount: '',//doubleclick user profile list
			copyFbaAccount: '',//facebook ads 账户列表(搜索备用)
			copyPtengineAccountList: '',//ptengine 账户列表(搜索备用)
			authorize: false,      //授权中//验证中
			authorizeFailure: false,//验证失败

			propertieList: [],  //GA
			sltAccount: null,   //所选数据源账号信息

			//档案列表请求状态
			profile: {
				type: null, //数据源code
				list: null, //档案列表
				query: null  //档案列表请求状态(querying, success, failed, error)
			},
			profileTabActive: 'Object', //档案列表展示标签页---默认展示Object
			//数据源权限相关
			isHasPermission: true,  //是否有次数据源的权限
			dsName: null           //当没有数据源权限时,单独请求数据源名称
		};

		//自定义widget的数据源选择
		scope.customWidgetOptions = {
			customDataSourceListAll: [], //自定义widget的中的所有数据源汇总数组
			customDataSourceListTemp: [], //自定义widget的数据源，去重后的数组
			customDataSourceList: null, //自定义widget，当前大widget下的所有小widget的数据源汇总列表对象，包含配置信息
			showSteps: false, //是否显示第二及后面的步骤
			showProfileIdStep: false //是否显示第三步
		};

		/**
		 * selectDs
		 * 选择数据源(第一步-选择数据源)
		 */
		scope.selectDs = function (ds) {
			scope.accountSettings.showSteps = true;
			scope.dsData.dsInfo = scope.getDsInfo(ds.dsCode);
			scope.dsConfig = getMyDsConfig(ds.dsCode);

			//选择完数据源以后，Googleadwords不能直接显示第三步
			scope.dsConfig.editor.source.accountOfTwoStep ? scope.accountSettings.showProfileIdStep = false : scope.accountSettings.showProfileIdStep = true;

			var variables = scope.modal.editorNow.variables[0];

			//当数据源(非模板)切换时,需要把图标类型重新切换为table类型，防止不支持地图类型等错误(自定义widget除外，因为自定义widget已经确定是什么图标类型了，不能再换成table)
			if (variables.ptoneDsInfoId != ds.dsId && scope.modal.editorNow.baseWidget.isExample != 1) {

				if (!scope.modal.editorNow.layout) {//不是自定义widget
					scope.modal.editorNow.baseWidget.graphName = 'table';
					scope.modal.editorNow.baseWidget.ptoneGraphInfoId = 800;
					variables.variableGraphId = 800;
				}

				//清除双轴信息
				scope.modal.editorNow.chartSetting.showMultiY = 0;
				scope.modal.editorNow.chartSetting.yAxis = [{"enabled": true}, {"enabled": true}];

				//清空授权账号信息
				variables.ptoneDsInfoId = ds.dsId;
				variables.dsCode = ds.dsCode;
				variables.accountName = '';
				variables.connectionId = '';
				variables.profileId = '';
				variables.metrics = [];
				variables.dimensions = [];
				variables.dateDimensionId = '';
				variables.sort = '';
				variables.segment = null;
				variables.filters = null;

				scope.saveData('source-selectDs');

				//禁用指标维度选项
				scope.editor.dsId = ds.dsId;
				scope.editor.dsCode = ds.dsCode;
				scope.editor.disabled[1] = true;
				scope.editor.disabled[2] = true;
				scope.editor.disabled[3] = true;
			}

			if (scope.dsData.dsInfo.code == 'upload') {
				scope.accountSettings.profile.query = 'querying';
				scope.getProfileList('upload', 'all', scope.dsData.dsInfo.id);
			} else {
				//get account list
				scope.getAuthAccounts(ds.dsId);

				// Tab change
				scope.accountSettings.now = 'account';
			}

			//时间范围初始化
			timeInit();

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_source', 'account');

			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where:'widget_editor',
				what:'select_datasource',
				how:'click',
				value: ds.dsCode
			});
		};


		/**
		 * getAuthAccounts
		 * 请求已授权账户列表(进入第二步)
		 */
		scope.getAuthAccounts = function (ds, type) {
			var url = LINK_DATA_SOURCE_VIEW + scope.rootSpace.current.spaceId + '/' + ds;
			scope.editor.dsId = ds;
			dataMutualSrv.get(url).then(function (data) {
				if (data.status == 'success') {
					//如果已授权账户为空，则显示授权页面
					scope.accountSettings.accountsList = data.content;

					//存储数据以免重复请求
					scope.editor.accountList = data.content;

					//默认选中第一个账户
					if (!scope.modal.editorNow.variables[0].accountName && scope.accountSettings.accountsList.length > 0 && scope.modal.editorNow.variables[0].accountName != scope.accountSettings.accountsList[0].name) {
						scope.modal.editorNow.variables[0].accountName = scope.accountSettings.accountsList[0].name;

						if (scope.modal.editorNow.baseWidget.isExample != 1 && !scope.dsConfig.linkData.hideStepThree) {
							scope.modal.editorNow.variables[0].profileId = '';
						}
					}
				} else {
					if (data.status == 'failed') {
						console.log('Get accountsList Failed!')
					} else if (data.status == 'error') {
						console.log('Get accountsList Error: ')
						console.log(data.message)
					}
				}
			});
			//}
		};


		/**
		 *  选择授权账户(第二步)
		 * @param account
		 * @param custom
		 */
		scope.selectAccount = function (account, custom, dsCode, dsConfig) {
			scope.accountSettings.sltAccount = account;
			var variables, config;
			if (custom && dsCode && dsConfig) {
				var indexOfChildren;
				indexOfChildren = scope.customWidgetOptions.customDataSourceListAll.indexOf(dsCode);
				variables = scope.modal.editorNow.children[indexOfChildren].variables[0];
				config = dsConfig.editor.source;
			} else {
				variables = scope.modal.editorNow.variables[0];
				config = scope.dsConfig.editor.source;
			}

			//save
			variables.accountName = account.name;
			variables.connectionId = account.connectionId;

			//有的模版数据源只用选择账号即可授权
			if (!custom && scope.modal.editorNow.baseWidget.isExample == 1 && scope.dsConfig.linkData.hideStepThree) {
				//批量授权更新
				scope.modal.editorNow.baseWidget.isExample = 0;
				scope.modal.editorNow.baseWidget.isDemo = 0;
				linkDataSrv.update(scope);

				scope.saveData('source-setDsProfileId');

				if (scope.editor.disabled[1] || !scope.modal.editorNow.variables[0].metrics || scope.modal.editorNow.variables[0].metrics.length == 0) {
					scope.editor.documentClick[1] = true;
					scope.editor.disabled[1] = false;
					scope.editor.pop.name = 'data';
				}
				//编辑-授权
				if (scope.modal.editorNow.variables[0].metrics && scope.modal.editorNow.variables[0].metrics.length > 0) {
					scope.editor.disabled[2] = false;
					scope.editor.disabled[3] = false;
				}

				//在切换profile的时候，验证当前数据源是否需要重新加载左侧控件的状态，如果需要则通过广播通知
				if (scope.dsConfig.editor.source.isReinitTimeByProfile) {
					scope.$emit('changeCustomWidget');
				}

				return;
			}

			variables.profileId = '';// 重新选择账户信息时，需要清空profileId信息
			if (config.deleteMetricDimension && scope.modal.editorNow.baseWidget.isExample != 1 && !custom) {
				variables.metrics = [];//重新选择账户信息时，需要清空指标和维度
				variables.dimensions = [];
				variables.segment = null;
				variables.filters = null;
			}

			//切换查询状态
			scope.accountSettings.profile.query = 'querying';
			//除ADS数据源(MCC账号没有第三步)之外,都直接进入第三步界面.
			if (!custom) {
				scope.saveData('source-selectAccount');
				//获取档案列表
				scope.getProfileList(account.name, account.connectionId, account.dsId);
			} else {
				//自定义widget，保存时特殊处理
				scope.editor.accountName = account.name;
				scope.saveData('source-selectAccount', 'custom', scope.modal.editorNow.children[indexOfChildren]);
				//获取档案列表
				scope.getProfileList(account.name, account.connectionId, account.dsId, null, 'custom', dsConfig.editor.source, variables);
			}

			//判断是否有指标维度，否则禁用指标维度
			if (scope.modal.editorNow.variables[0].metrics.length == 0 && !custom) {//自定义widget选择完账号后，不需要禁止掉时间
				scope.editor.disabled[1] = true;
				scope.editor.disabled[2] = true;
				scope.editor.disabled[3] = true;
			}

			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where:'widget_editor',
				what:'select_account',
				how:'click'
			});
		};

		/**
		 * 切换档案列表的标签页---目前只有salesforce数据源才有标签页
		 * @param type  --- 'Object' /  'Report'
		 */
		scope.changeProfileTab = function (type) {
			if (!type || scope.accountSettings.profileTabActive === type) return;
			var variables = scope.modal.editorNow.variables[0];
			scope.accountSettings.profileTabActive = type;
			scope.getProfileList(variables.accountName, variables.connectionId, variables.ptoneDsInfoId, type);
		};

		/**
		 * * getProfileList （最终数据在scope.accountSettings.profile.list中）
		 * 获取相应的档案列表数据(进入第三步)
		 * @param accountName
		 * @param connectionId
		 * @param dsId
		 * @param profileTabActive ---- salesforce数据源的档案列表有tab切换
		 * @param custom  --- 是否为自定义widget
		 * @param customConfig---自定义widget的设置信息
		 * @param variables --- 自定义widget的variables
		 */
		scope.getProfileList = function (accountName, connectionId, dsId, profileTabActive, custom, customConfig, variables) {
			// 需要清空上一次操作的
			scope.accountSettings.profile.list = null;
			scope.accountSettings.now = 'profile';
			scope.accountSettings.profile.type = scope.dsData.dsInfo.code; // 为什么后面会赋值mysql??

			var url, config;
			if (custom) {
				config = customConfig;
			} else {
				config = scope.dsConfig.editor.source;
				variables = scope.modal.editorNow.variables[0];
			}

			switch (config.getProfileType) {
				case "api":
					url = LINK_GET_DS_PROFILES + '/' + variables.dsCode + '/' + connectionId + '/' + accountName;
					break;
				case "apiRemote":
					if(config.getProfileListNotNeedAccount){//yahoo数据源，不能传account，因为account中可能存在不安全字符
						url = LINK_PROFILE_API_REMOTE + connectionId;
					}else{
						url = LINK_PROFILE_API_REMOTE + connectionId + '/' + variables.dsCode + '/' + accountName;
					}
					break;
				case "table":
					url = LINE_DS_PROFILES + scope.rootSpace.current.spaceId + '/' + connectionId + '/' + dsId;
					scope.accountSettings.profile.type = 'mysql';
					break;
				case "category":
					url = LINK_DATASOURCE_CATEGORYS + '/' + dsId;
					break;
				case "salesforce":
					//salesforce 获取档案列表分为object列表（一层结构）和report列表（两层结构）
					if (variables.profileId && !profileTabActive) {
						if ((variables.profileId + '').indexOf('|join|') > -1) {//当编辑档案列表时，Report列表的profileId包含'|join|'
							scope.accountSettings.profileTabActive = 'Report';
						}
					}
					if (scope.accountSettings.profileTabActive === 'Object') {
						url = LINK_SALESFORCE_OBJECTS;
						scope.dsConfig.editor.source.oneLayer = true;
						scope.dsConfig.editor.source.twoLayer = false;
					} else if (scope.accountSettings.profileTabActive === 'Report') {
						url = LINK_SALESFORCE_REPORT + connectionId + '/' + variables.dsCode + '/' + accountName;
						scope.accountSettings.profile.type = 'mysql';
						scope.dsConfig.editor.source.oneLayer = false;
						scope.dsConfig.editor.source.twoLayer = true;
					}
					break;
			}

			//如果account没改变，并且已存在列表旧数据，则复用旧数据
			var getDataFlag = true;
			var dsAccountProfile = publicDataSrv.getPublicData('dsAccountProfileOfNewStructure');

			////是否需要缓冲，profileOfNormal == false,表示需要缓冲
			if (!config.profileOfNormal) {
				// 取数前,先查看是否以存储可复用的数据列表(当存储的列表为空时,也需要重新取数)
				if (config.profileHasTab) {
					profileTabActive = profileTabActive ? profileTabActive : scope.accountSettings.profileTabActive;
					if (dsAccountProfile[dsId] && dsAccountProfile[dsId][connectionId] && dsAccountProfile[dsId][connectionId][profileTabActive] && dsAccountProfile[dsId][connectionId][profileTabActive].length > 0) {
						var list = dsAccountProfile[dsId][connectionId][profileTabActive];

						//yahoo Ysss 数据源需要国际化
						if (scope.dsConfig.editor.source.profileNeedI18n) {
							list.forEach(function (item, index) {
								list[index].name = $translate.instant(list[index].i18nCode);
							});
						}
						scope.accountSettings.profile.list = angular.copy(list);

						//切换至下一步
						scope.getProfileListAfter('success');

						getDataFlag = false;
					}
				} else {
					if (dsAccountProfile[dsId] && dsAccountProfile[dsId][connectionId] && dsAccountProfile[dsId][connectionId].length > 0) {
						var list = dsAccountProfile[dsId][connectionId];

						//yahoo Ysss 数据源需要国际化
						if (scope.dsConfig.editor.source.profileNeedI18n) {
							list.forEach(function (item, index) {
								list[index].name = $translate.instant(list[index].i18nCode);
							});
						}
						scope.accountSettings.profile.list = angular.copy(list);

						//切换至下一步
						scope.getProfileListAfter('success');

						getDataFlag = false;
					}
				}
			}

			if (getDataFlag) {
				getList();
			}

			//save data
			function getList() {
				scope.accountSettings.profile.list = [];
				scope.editor.profileBackups[scope.accountSettings.profile.type] = [];

				var dsCode = scope.accountSettings.profile.type;

				scope.accountSettings.profile.query = 'querying';
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
						if (data.status == 'failed') {
							console.log('Get gaAccountList Failed!');
						} else if (data.status == 'error') {
							console.log('Get gaAccountList Error: ');
							console.log(data.message)
						}
					}

					//yahoo Ysss 数据源需要国际化
					if (scope.dsConfig.editor.source.profileNeedI18n) {
						list.forEach(function (item, index) {
							list[index].name = $translate.instant(list[index].i18nCode);
						});
					}

					scope.accountSettings.profile.list = list;

					//存储备用
					if (!config.profileOfNormal) {
						if (config.profileHasTab) {
							publicDataSrv.setPublicData('dsAccountProfileOfNewStructure', dsId, connectionId, angular.copy(list), scope.accountSettings.profileTabActive);
						} else {
							publicDataSrv.setPublicData('dsAccountProfileOfNewStructure', dsId, connectionId, angular.copy(list));
						}
					}

					var messageCode = null;
					if (scope.accountSettings.profile.type == 'facebookad') {
						messageCode = data.message;
					}
					//切换至下一步
					scope.getProfileListAfter(data.status, messageCode, data.code);
				})
			}
		};

		/**
		 * 搜索列表插件回调
		 * @param data
		 */
		scope.selectSearchList = function (data) {
			var dsCode = scope.dsData.dsInfo.code;
			var dsConfig = scope.dsConfig;
			scope.selectProfile(data, null, null, dsCode, dsConfig);
			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where:'widget_editor',
				what:'select_profile',
				how:'click'
			});
		};

		/**
		 * selectProfile(需要添加详细注释)
		 * 选择档案列表(第三步-选择档案)
		 */
		scope.selectProfile = function (profile, $event, custom, dsCode, dsConfig) {
			// $event.stopPropagation();
			var config;
			scope.editor.filterShow = false;//过滤器隐藏
			scope.editor.dimensionOperation = false;

			if (custom) {
				config = dsConfig;
			} else {
				config = scope.dsConfig.editor.source;
			}

			//yahoo yss数据源需要清空图表信息
			if (config.isNeedGrap) {
				scope.modal.editorNow.baseWidget.graphName = 'table';
				scope.modal.editorNow.baseWidget.ptoneGraphInfoId = 800;
				scope.modal.editorNow.variables[0].variableGraphId = 800;
			}

			//重新选择账户信息时，非模板widget时，需要清空指标和维度
			if (scope.modal.editorNow.baseWidget.isExample != 1 && config.deleteMetricDimension && !custom) {
				scope.modal.editorNow.variables[0].metrics = [];
				scope.modal.editorNow.variables[0].dimensions = [];
				scope.modal.editorNow.variables[0].segment = null;
				scope.modal.editorNow.variables[0].filters = null;
			}

			//重新选择账户信息时，是否需要清空已选择的时间字段
			if (config.deleteDateDimensionsId) {
				scope.modal.editorNow.variables[0].dateDimensionId = null;
			}

			//实时保存数据
			if (!custom) {
				if (!scope.modal.editorNow.variables[0].profileId || scope.modal.editorNow.variables[0].profileId != profile.id) {
					//批量授权更新
					if (scope.modal.editorNow.baseWidget.isExample == 1 || scope.modal.editorNow.baseWidget.isDemo == 1) {
						scope.modal.editorNow.baseWidget.isExample = 0;
						scope.modal.editorNow.baseWidget.isDemo = 0;
						linkDataSrv.update(scope);
					}

					scope.modal.editorNow.variables[0].profileId = profile.id;

					//upload数据源没有第二步,所以在第三步添加connectionId
					if (scope.modal.editorNow.variables[0].dsCode == 'upload') {
						var item = treeUtils.getItem(scope.accountSettings.profile.list, profile.id);
						scope.modal.editorNow.variables[0].connectionId = item.extra.connectionId;
					}
					scope.saveData('source-setDsProfileId');
				}
			} else {
				var indexOfChildren;
				indexOfChildren = scope.customWidgetOptions.customDataSourceListAll.indexOf(dsCode);
				var customWidget = scope.modal.editorNow.children[indexOfChildren];

				scope.editor.profileId = profile.id;//给自定义widget设置的值，用来表示已经选中的profileId

				if (!customWidget.variables[0].profileId || customWidget.variables[0].profileId != profile.id) {
					//批量授权更新
					if (customWidget.baseWidget.isExample == 1 || customWidget.baseWidget.isDemo == 1) {
						customWidget.baseWidget.isExample = 0;
						customWidget.baseWidget.isDemo = 0;
						linkDataSrv.update(scope);
					}
					customWidget.variables[0].profileId = profile.id;

					//upload数据源没有第二步,所以在第三步添加connectionId
					if (customWidget.variables[0].dsCode == 'upload') {
						var item = treeUtils.getItem(scope.accountSettings.profile.list, profile.id);
						scope.modal.editorNow.variables[0].connectionId = item.extra.connectionId;
					}
					scope.saveData('source-setDsProfileId', 'custom', customWidget);
				}
			}

			if (!custom) {
				if (scope.editor.disabled[1] || !scope.modal.editorNow.variables[0].metrics || scope.modal.editorNow.variables[0].metrics.length == 0) {
					scope.editor.documentClick[1] = true;
					scope.editor.disabled[1] = false;
					scope.editor.pop.name = 'data';
				}

				//编辑-授权
				if (scope.modal.editorNow.variables[0].metrics && scope.modal.editorNow.variables[0].metrics.length > 0) {
					scope.editor.disabled[2] = false;
					scope.editor.disabled[3] = false;
				}

				//在切换profile的时候，验证当前数据源是否需要重新加载左侧控件的状态，如果需要则通过广播通知
				if (scope.dsConfig.editor.source.isReinitTimeByProfile) {
					scope.$emit('changeCustomWidget');
				}

				if ((profile.id + '').indexOf('|join|') > -1) {//salesforce数据源，Report类型，时间不可点
					scope.editor.disabled[2] = true;
				}
			}

		}

		/**
		 * getProfileListAfter
		 * 取档案列表之后,统一处理
		 */
		scope.getProfileListAfter = function (status, message, code) {
			if(status == 'failed') status = 'error'; //用来错误提示
			if(code){
				message = errorCodes[code] && errorCodes[code].i18nKey;
			}
			message = message || 'WIDGET.EDITOR.ACCOUNT.GET_PROFILE_ERROR';

			scope.accountSettings.now = 'profile';
			scope.accountSettings.profile.query = status;

			var errorTip = $translate.instant(message);
			errorTip = errorTip.replace("{spaceId}", scope.rootSpace.current.domain);
			scope.accountSettings.profile.messageCode = errorTip;

			$timeout(function(){
				angular.element('.source-box.step-c .search-list .bd').slimscroll({
					height: '160px',
					size:'6px',
					allowPageScroll: false
				});
			},100);

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_source', 'profile');
		};


		//切换选择步骤
		scope.tab = function (type, dsCode, customConfig) {
			scope.accountSettings.now = type;
			var variables;
			if (dsCode) {//传入deCode，说明是自定义widget，需要获取dsCode所对应的children的小widget的数据
				variables = scope.modal.editorNow.children[scope.customWidgetOptions.customDataSourceListAll.indexOf(dsCode)].variables[0];
			} else {
				variables = scope.modal.editorNow.variables[0];
			}

			if (type == 'account' && variables.ptoneDsInfoId) {
				scope.getAuthAccounts(variables.ptoneDsInfoId);
			}
			if (type == 'profile') {
				//切换查询状态
				if (scope.dsData.dsInfo.code == 'upload') {
					//请求文件列表
					scope.getProfileList('upload', 'all', scope.dsData.dsInfo.id);
				} else {
					if (variables.accountName) {
						if (dsCode) {
							scope.getProfileList(variables.accountName, variables.connectionId, variables.ptoneDsInfoId, null, 'custom', customConfig, variables);
						} else {
							scope.getProfileList(variables.accountName, variables.connectionId, variables.ptoneDsInfoId);
						}
					}
				}
			}

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'widget_source', type);

			//全站事件统计
			if(type === 'source') type = 'datasource';
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where:'widget_editor',
				what:'select_' + type + '_step',
				how:'click'
			});
		};

		//显示已选择的数据源名称
		scope.showSelectedDs = function (dsId) {
			if (!scope.accountSettings.isHasPermission) {
				return scope.accountSettings.dsName;
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


		//profile 编辑链接到文件编辑器
		scope.profileEdit = function (id) {
			var sourceId = id, cid = scope.modal.editorNow.variables[0].connectionId;
			var url = LINK_DATA_SOURCE_EDIT_VIEW + cid + '/' + sourceId;
			var loadElement = angular.element('.editor-search-bd');
			uiLoadingSrv.createLoading(loadElement);
			dataMutualSrv.get(url).then(function (data) {
				if (data.status == 'success') {
					var ds = null, dsFlag = false;
					for (var i = 0; i < scope.rootCommon.dsAuthList.length; i++) {
						if (scope.rootCommon.dsAuthList[i].dsCode == scope.modal.editorNow.variables[0].dsCode) {
							ds = scope.rootCommon.dsAuthList[i];
							dsFlag = true;
						}
					}
					if (dsFlag) {
						var widgetEditorLinkToEdit = JSON.stringify({
							type: 'dsEdit',
							editData: data.content,
							ds: ds,
							dsCode: scope.modal.editorNow.variables[0].dsCode,
							connectionId: scope.modal.editorNow.variables[0].connectionId
						});
						window.localStorage.setItem('widgetEditorLinkToEdit', widgetEditorLinkToEdit);
						$('body').append('<a href="/pt/dataSources/editor" id="goto" target="_blank"></a>');
						$('#goto').get(0).click();
						$('body').find('#goto').remove();
						uiLoadingSrv.removeLoading(loadElement);
					}

				} else {
					if (data.status == 'failed') {
						console.log('Get accountsList Failed!');
					} else if (data.status == 'error') {
						console.log('Get accountsList Error: ');
						console.log(data.message)
					}

					alert('getFileDataError');
				}
				uiLoadingSrv.removeLoading(loadElement);
			});
		};

		/**
		 * accredit
		 * 账户授权(第二步-账户授权)
		 * 注:这里为什么会有账户授权的代码,这应该是数据源管理的逻辑
		 */
		scope.accredit = function (id) {
			var sign = uuid();
			var accreditSocket = new websocket;
			var url = LINK_AUTHOR + scope.dsData.dsInfo.code + '?ptOneUserEmail=' + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign + '&spaceId=' + scope.rootSpace.current.spaceId;
			var socketUrl = DATA_SOURCE_WEB_SOCKET + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign;

			if (scope.dsData.dsInfo.code == 'ptengine') {
				url = '/signin/ptengine';
				localStorage.setItem('ptengineLoginReferrer', 'dataSources');
				localStorage.setItem('ptengineAuthSign', sign);
				localStorage.setItem('ptengineSpaceId', scope.rootSpace.current.spaceId);

				socketUrl = DATA_SOURCE_WEB_SOCKET + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign + '&spaceId=' + scope.rootSpace.current.spaceId;
			}

			accreditSocket.initWebSocket(socketUrl);

			//授权验证跳转
			openWindow(url);
			scope.accountSettings.authorize = true;
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
					scope.accountSettings.authorize = false;

					//判断是否重复授权
					var flag = true;
					for (var i = 0; i < scope.accountSettings.accountsList.length; i++) {
						if (scope.accountSettings.accountsList[i].name == newValue.content.account) {
							console.log('重复授权!');
							flag = false;
							break;
						}
					}
					if (flag) {
						var dsFlag = false;
						var index = 0;
						for (var i = 0; i < scope.rootCommon.dsAuthList.length; i++) {
							if (scope.rootCommon.dsAuthList[i].dsId == scope.modal.editorNow.variables[0].ptoneDsInfoId) {
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
								dsCode: scope.dsData.dsInfo.code,
								dsName: scope.dsData.dsInfo.name,
								dsId: scope.dsData.dsInfo.id,
								dsConfig: scope.dsData.dsInfo.config,
								dsOrderNumber: scope.dsData.dsInfo.orderNumber + '',
								isPlus: 2
							})
						}

						scope.accountSettings.accountsList.push(angular.copy(newValue.content.connectionInfo));
						//存储数据以免重复请求
						scope.editor.accountList = angular.copy(scope.accountSettings.accountsList);

						//默认选中第一个账户
						if (!scope.modal.editorNow.variables[0].accountName && scope.accountSettings.accountsList.length > 0 && scope.modal.editorNow.variables[0].accountName != scope.accountSettings.accountsList[0].name) {
							scope.modal.editorNow.variables[0].accountName = scope.accountSettings.accountsList[0].name;
							scope.modal.editorNow.variables[0].profileId = '';
						}
					}
				} else {
					scope.accountSettings.authorizeFailure = true;
					scope.accountSettings.authorize = false;
				}
			});

			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where:'widget_editor',
				what:'select_account_add_a_account',
				how:'click'
			});
		};

		//再次授权
		scope.verify_try = function () {
			$('.failure').fadeOut('fast', function () {
				scope.accredit();
			});
		};

		/**
		 * goToDsManage
		 * 跳转到数据源Account管理界面
		 *
		 */
		scope.goToDsManage = function (dsCode) {
			$state.go('pt.dataSources.' + dsCode);
			//全站事件统计
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where:'widget_editor',
				what:'select_account_add_a_account',
				how:'click'
			});
		};

		/**
		 * 获取自定义widget中的所有数据源
		 * @param editorNow ----大widget的编辑器对象
		 */
		scope.getAllCustomDataSource = function (editorNow) {
			if (editorNow && editorNow.children && editorNow.children.length > 0) {
				return editorNow.children.map(function (item, index, arr) {
					scope.customWidgetOptions.customDataSourceListAll.push(item.variables[0].dsCode);
					if (item.baseWidget.widgetType === 'tool') return;
					return item.variables[0].dsCode;
				}).filter(function (item, index, arr) {
					return item && arr.indexOf(item) === index;
				})
			}
		};

		/**
		 * 批量设置自定义widget数据源的展开折叠事件
		 * @param $index---数据源位置
		 * @param dsCode
		 * @param customConfig
		 */
		scope.selectCustomDataSource = function ($index, dsCode, customConfig, folder) {
			if (folder) {//如果已经是展开的了，就不需要再次请求数据了
				return;
			}
			var indexOfChildren;
			scope.customWidgetOptions.customDataSourceList.forEach(function (item, index, arr) {
				scope.customWidgetOptions.customDataSourceList[index]['folder'] = index === $index;
			});
			indexOfChildren = scope.customWidgetOptions.customDataSourceListAll.indexOf(dsCode);
			sourceStep(dsCode, scope.modal.editorNow.children[indexOfChildren].variables[0], 'custom', customConfig);
		};

		/**
		 * 数据源选择，应该停留在哪个步骤 (这个方法太难看懂了)
		 * @param dsCode ---数据源的dsCode字段
		 * @param variables ---widget的variables字段
		 * @param custom ---是否为自定义widget
		 * @param customConfig ----自定义widget的数据源配置对象
		 */
		function sourceStep(dsCode, variables, custom, customConfig) {
			//编辑器已打开的情况下,dsInfo需要重新获取.
			scope.dsData.dsInfo = scope.getDsInfo(dsCode);
			var stepA, stepB, stepC, config;

			if (dsCode && !custom) {
				stepA = false;
				stepB = false;
				stepC = false;
				config = scope.dsConfig.editor.source;
			} else if (custom) {
				stepA = true;
				stepB = true;
				stepC = false;//自定义widget，第一步不存在，第二步默认为true，第三步看后面的判断结果
				config = customConfig.editor.source;
			}

			scope.editor.dsId = variables.ptoneDsInfoId;
			scope.editor.accountName = variables.accountName;

			if (scope.modal.editorNow.baseWidget.isExample == 1) {
				//Widget Example
				scope.accountSettings.dsAuthList = [getDsInfo(scope.editor.dsId, scope.rootCommon.dsList)];

				if (!scope.accountSettings.dsAuthList[0]) {
					//当前账户下没有此数据源权限,则提示
					elem.find('.step-a').addClass('no-border');
					scope.accountSettings.isHasPermission = false;

					//获取ds name
					dataMutualSrv.get(LINK_GET_DS_INFO + dsCode).then(function (data) {
						if (data.status == 'success') {
							scope.accountSettings.dsName = data.content.name;
						}
					});
					return;
				}

				//有的数据源模版时不显示第三步
				if (scope.dsConfig.linkData.hideStepThree) {
					scope.accountSettings.showProfileIdStep = false;
				}
			} else {
				scope.accountSettings.dsAuthList = angular.copy(scope.rootCommon.dsAuthList);
			}

			if (scope.accountSettings.dsAuthList.length === 0 && !custom) {
				//如果已授权数据源为空，需要用户重新选择数据源，自定义widget不能选择第一步
				scope.accountSettings.now = 'source';
				elem.find('.step-a').addClass('source-box-pass');
			} else {
				stepA = true;
				for (var i = 0; i < scope.accountSettings.dsAuthList.length; i++) {
					//数据源不为空时，需要比较此widget保存的数据源信息与用户数据源列表是否相符，否则重新选择数据源
					if (scope.accountSettings.dsAuthList[i].dsId == variables.ptoneDsInfoId) {
						stepB = true;
						break;
					}
				}
				if (stepA && !stepB && !custom) {//用户的数据源列表不在widget的数据源列表中，需要重新选择数据源，自定义widget不存在这种情况
					scope.accountSettings.showSteps = false;
					scope.accountSettings.now = 'source';
					elem.find('.step-a').addClass('source-box-pass');
				}
				if (stepB && variables.ptoneDsInfoId) {
					//数据源和保存的数据源匹配
					scope.accountSettings.showSteps = true;//显示第二及后面步骤
					if (scope.dsData.dsInfo.code == 'upload') {
						//upload不显示第二步,直接请求文件列表,进入第三步
						//请求请求文件列表
						if (custom) {
							scope.getProfileList('upload', 'all', scope.dsData.dsInfo.id, null, 'custom', config, variables);
						} else {
							scope.getProfileList('upload', 'all', scope.dsData.dsInfo.id);
						}

					} else {
						if (config.accountOfTwoStep) {
							//google adwords不能马上显示第三步,需要先判断是普通账户还是MCC账户
							scope.accountSettings.showProfileIdStep = false;
						}
						//请求已授权账户列表
						dataMutualSrv.get(LINK_DATA_SOURCE_VIEW + scope.rootSpace.current.spaceId + '/' + variables.ptoneDsInfoId).then(function (data) {
							if (data.status == 'success') {
								scope.accountSettings.accountListNew = data.content;

								//只有在有connectionId时，才能进入第三步(区分isExample)
								if(!variables.connectionId){
									elem.find('.step-b').addClass('source-box-pass');
									if (custom) {
										scope.tab('account', dsCode, config);
									} else {
										scope.tab('account');
									}

									return;
								}

								if (config.accountOfTwoStep) {
									//google adwords 需要单独判断，是普通账户只有两步，MCC账户才有可能有三步
									if (variables.accountName) {
										scope.loadSetting.widget = true;
										var googleAdwordsUrl = LINK_GET_DS_PROFILES + '/' + variables.dsCode + '/' + variables.connectionId + '/' + variables.accountName;
										dataMutualSrv.get(googleAdwordsUrl).then(function (data) {
											if (data.status == 'success') {
												var content = data.content, canManageClients = content.canManageClients;
												if (canManageClients) {
													//MCC账户
													scope.accountSettings.profile.list = content.childs;
													// Tab change
													scope.accountSettings.now = 'profile';
													elem.find('.step-c').addClass('source-box-pass');
													scope.accountSettings.showProfileIdStep = true;
													$timeout(function(){
														angular.element('.source-box.step-c .search-list .bd').slimscroll({
															height: '160px',
															size:'6px',
															allowPageScroll: false
														});
													},100);
												} else {
													//普通账户
													elem.find('.step-b').addClass('source-box-pass');
													if (custom) {
														scope.tab('account', dsCode, config);
													} else {
														scope.tab('account');
													}
												}
											} else {
												if (data.status == 'failed') {
													console.log('Get google adwords account Failed!');
												} else if (data.status == 'error') {
													console.log('Get  google adwords account Error: ');
													console.log(data.message)
												}
											}
											//切换到下一步
											scope.accountSettings.profile.query = 'success';
										});
									} else {
										elem.find('.step-b').addClass('source-box-pass');
										if (custom) {
											scope.tab('account', dsCode, config);
										} else {
											scope.tab('account');
										}

									}
								} else {
									//除google adwords 数据源以外，都是有三步的
									for (var i = 0; i < scope.accountSettings.accountListNew.length; i++) {
										if (scope.accountSettings.accountListNew[i].name == variables.accountName) {
											stepC = true;
											break;
										}
									}
									if (stepC && variables.profileId) {
										//数据源账户和保存的数据源账户匹配，跳到第三步

										elem.find('.step-c').addClass('source-box-pass');
										if (custom) {
											scope.editor.profileId = variables.profileId;
											scope.tab('profile', dsCode, config);
											scope.getProfileList(variables.accountName, variables.connectionId, variables.ptoneDsInfoId, null, 'custom', config, variables);
										} else {
											scope.tab('profile');
											//请求档案列表
											scope.getProfileList(variables.accountName, variables.connectionId, variables.ptoneDsInfoId);
										}

									} else {
										//数据源账户和保存的数据源账户不匹配，跳到第二步

										elem.find('.step-b').addClass('source-box-pass');
										if (custom) {
											scope.tab('account', dsCode, config);
										} else {
											scope.tab('account');
										}

									}
								}

								//需要设置‘scope.accountSettings.sltAccount’的值
								for (var i = 0; i < scope.accountSettings.accountListNew.length; i++) {
									if (scope.accountSettings.accountListNew[i].name == variables.accountName) {
										scope.accountSettings.sltAccount = scope.accountSettings.accountListNew[i];
									}
								}

							} else {
								if (data.status == 'failed') {
									console.log('Get gaAccountList Failed!');
								} else if (data.status == 'error') {
									console.log('Get gaAccountList Error: ');
									console.log(data.message)
								}
							}
						})
					}
				}
			}
		}


		/**
		 * dataInit
		 * 指令进入之前的数据初始化
		 */
		dataInit();

		function dataInit() {
			if (scope.modal.editorNow.baseWidget.widgetType === 'custom') {//自定义widget的数据源选择需要特殊处理
				scope.customWidgetOptions.customDataSourceListTemp = scope.getAllCustomDataSource(scope.modal.editorNow);
				//自定义widget，打开数据源时，分为“所有数据源都已经设置过”和“至少还有1个数据源没有设置过”两种情况
				if (scope.isEveryCustomWidgetHaveProfileId(scope.modal.editorNow)) {
					//默认展开的是第一个数据源
					scope.customWidgetOptions.customDataSourceList = scope.customWidgetOptions.customDataSourceListTemp.map(function (item, index, arr) {
						var dsInfo = getDsInfo(item, scope.rootCommon.dsList);
						return {
							'now': 'account',
							'folder': index === 0,
							'dsOrderNumber': dsInfo.orderNumber,
							'dsCode': dsInfo.code,
							'dsName': dsInfo.name,
							'dsId': dsInfo.id,
							'dsConfig': dsInfo.config
						}
					});
					scope.customWidgetOptions.showSteps = true;
					scope.customWidgetOptions.showProfileIdStep = true;
					console.log("========curren scope is=======", scope);
				} else {
					//默认展开的是第一个没有设置过的数据源
					scope.customWidgetOptions.customDataSourceList = scope.customWidgetOptions.customDataSourceListTemp.map(function (item, index, arr) {
						var dsInfo = getDsInfo(item, scope.rootCommon.dsList);
						return {
							'now': 'source',
							'folder': false,
							'dsOrderNumber': dsInfo.orderNumber,
							'dsCode': dsInfo.code,
							'dsName': dsInfo.name,
							'dsId': dsInfo.id,
							'dsConfig': dsInfo.config
						}
					});
				}
			} else {
				sourceStep(scope.modal.editorNow.variables[0].dsCode, scope.modal.editorNow.variables[0]);
			}
		}

		//时间初始化
		function timeInit() {
			var dataKey = scope.modal.editorNow.baseWidget.dateKey;
			var configOfSource = scope.dsConfig.editor.source,
				configOfTime = scope.dsConfig.editor.time;
			if (dataKey === null || !configOfSource.defaultTimeIsAllTime && dataKey == 'all_time') {
				if (configOfSource.defaultTimeIsAllTime) {
					//gd默认选中all time
					scope.modal.editorNow.baseWidget.dateKey = 'all_time';
				} else {
					//除gd外默认选中过去7天不包含今天
					scope.modal.editorNow.baseWidget.dateKey = 'past7day';
				}
			}
			if(!configOfTime.dataKeyOfInherit && dataKey){
				//从别的数据源切换到不需要时间维态的数据源时，gd/excel/s3，默认选中all_time
				scope.modal.editorNow.baseWidget.dateKey = 'all_time';
			}
			if(configOfTime.dataKeyOfInherit && dataKey){
				//从时间不维态的数据源切换到时间维态的数据源，需要重新设置dataKey为维态时间
				scope.modal.editorNow.baseWidget.dateKey = scope.rootUser.userSelected ? scope.rootUser.userSelected.dateKey : dataKey;
			}
		}

		//监听父级是否有切换widget，如果有，需要重新初始化data信息
		scope.$on('changeWidgetEditor', function (e, newLocation) {
			//数据初始化
			scope.accountSettings.now = 'source';
			scope.accountSettings.isHasPermission = true;
			elem.find('.step-a').removeClass('no-border');
			dataInit();
		});

	}
}

export default editorSource;
