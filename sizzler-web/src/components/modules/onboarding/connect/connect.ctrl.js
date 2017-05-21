import $ from 'jquery';


/**
 * widget connect
 *
 * Created by ao at 20160910
 */

angular
	.module('pt')
	.controller('connectCtrl', connectCtrl);

connectCtrl.$inject = ['$rootScope', '$scope', '$timeout', '$translate', 'uiLoadingSrv', 'onboardingDataSrc', 'uiSearch', 'siteEventAnalyticsSrv'];

function connectCtrl($rootScope, $scope, $timeout, $translate, uiLoadingSrv, onboardingDataSrc, uiSearch, siteEventAnalyticsSrv) {
	$scope.myOptions = {
		currentPageIsShow: null,

		dsInfo: null,
		spaceInfo: null,
		galleryList: null,
		dashboardId: null,
		dsAccount: null,
		widgetSltList: null,

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

		//GA专用
		ga: {
			copyGaAccount: '',
			propertieList: []   //GA专用
		},

		//其他
		other: {
			btnDisabled: true,      //保存按钮状态
			titleLocal: null,
			errorTips: null
		}
	};

	//===============


	$scope.changeAccount = changeAccount;
	$scope.back = back;
	$scope.confirm = confirm;
	$scope.selectProfile = selectProfile;
	$scope.searchProfile = searchProfile;


	dataInit();


	//===============

	/**
	 * 数据初始化
	 */
	function dataInit() {
		$scope.myOptions.spaceInfo = onboardingDataSrc.getData('spaceInfo');
		$scope.myOptions.galleryList = onboardingDataSrc.getData('galleryList');
		$scope.myOptions.widgetSltList = onboardingDataSrc.getData('widgetSltList');
		$scope.myOptions.dashboardId = onboardingDataSrc.getData('dashboardId');
		$scope.myOptions.dsInfo = onboardingDataSrc.getData('dsInfo');
		$scope.myOptions.dsAccount = onboardingDataSrc.getData('dsAccount');
		$scope.myOptions.profile = onboardingDataSrc.getData('profile');
		$scope.myOptions.currentPageIsShow = onboardingDataSrc.currentPageIsShow();

		$scope.myOptions.profile.current = null;

		if ($scope.myOptions.dsInfo.config.editor.source.profileName) {
			$scope.myOptions.other.titleLocal = $translate.instant("ONBOARDING.CONNECT.TITLE_PROFILE");
		} else if ($scope.myOptions.dsInfo.config.editor.source.accountClientName) {
			$scope.myOptions.other.titleLocal = $translate.instant("ONBOARDING.CONNECT.TITLE_ACCOUNT");
		} else if ($scope.myOptions.dsInfo.config.editor.source.adAccountName) {
			$scope.myOptions.other.titleLocal = $translate.instant("ONBOARDING.CONNECT.TITLE_AD_ACCOUNT");
		}

		if ($scope.myOptions.dsInfo && $scope.myOptions.dsInfo.code == 'googleanalysis') {
			listPackUp();
		}
	}

	/**
	 * 列表初始化收起(GA专用)
	 */
	function listPackUp() {
		var flag = $scope.myOptions.profile.list.length == 1;

		for (var i = $scope.myOptions.profile.list.length - 1; i >= 0; i--) {
			$scope.myOptions.ga.propertieList[i] = {
				t: flag,
				p: []
			};
			var a = $scope.myOptions.profile.list[i].webproperties;
			for (var j = a.length - 1; j >= 0; j--) {
				$scope.myOptions.ga.propertieList[i].p[j] = flag;
			}
		}
	}

	/**
	 * 授权
	 */
	function changeAccount() {
		onboardingDataSrc.accredit($scope, accreditAfter);

		//GTM
		siteEventAnalyticsSrv.setGtmEvent('click_element', 'onboarding', 'select_profile_change_account');
		
		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"onboarding",
			"what":"select_profile_change_account",
			"how":"click"
		});
	}

	/**
	 * 授权之后
	 *
	 * connectionInfo: {
             config:"{"accessToken":"ya29"}"
             connectionId:"6e11295d-c6d3-4b9c-a778-beb9930575cf"
             dsCode:"googleanalysis"
             dsId:1
             name:"sylphlili@ptmind.com"
             spaceId:"a7643a2b-f7f2-4661-8ad4-f74349e0f43a"
             status:"1"
             uid:"149"
             updateTime:1473479119369
             userName:"dawn"
         }
	 */
	function accreditAfter(status, connectionInfo) {
		if (status == 'success' && connectionInfo.connectionId) {
			//loading
			uiLoadingSrv.createLoading(angular.element('.onboarding-connect'));

			$scope.myOptions.dsAccount = connectionInfo;
			$scope.myOptions.profile.query = 'querying';
			onboardingDataSrc.setData('dsAccount', connectionInfo);

			//清除旧数据
			$scope.myOptions.profile.current = null;
			$scope.myOptions.profile.list = null;
			$scope.myOptions.profile.listTmp = null;
			$scope.myOptions.profile.query = 'querying';
			$scope.myOptions.other.btnDisabled = true;

			onboardingDataSrc.getProfileList(connectionInfo.name, connectionInfo.connectionId, connectionInfo.dsId, connectionInfo.dsCode, getProfileListAfter)
		}
	}

	/**
	 * getProfileListAfter
	 * 取档案列表之后,统一处理
	 */
	function getProfileListAfter(state, status) {
		if (state == 'pt.dashboard') {
			//如果是MCC账号等则直接进行授权后,进入面板
			onboardingDataSrc.confirm(onboardingDataSrc.confirmAfter);
		} else {
			$scope.myOptions.profile = onboardingDataSrc.getData('profile');

			//loading
			uiLoadingSrv.removeLoading(angular.element('.onboarding-connect'));

			$scope.myOptions.search.dsSearchKey = null;
		}

		$scope.myOptions.profile.query = status;
	}

	/**
	 * Back
	 */
	function back() {
		//切换路由
		onboardingDataSrc.stateChange('onboarding.preview');

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"onboarding",
			"what":"select_profile_back",
			"how":"click"
		});
	}

	/**
	 * confirm
	 */
	function confirm() {
		if ($scope.myOptions.profile.current) {
			//loading
			uiLoadingSrv.createLoading(angular.element('.onboarding-connect'));

			onboardingDataSrc.confirm();

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'onboarding', 'select_profile_comfirm');

			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"onboarding",
				"what":"select_profile_confirm",
				"how":"click"
			});
		} else {
			$scope.myOptions.other.errorTips = true;

			//显示2秒,自动隐藏
			$timeout(function () {
				$scope.$apply(function () {
					$scope.myOptions.other.errorTips = null;
				})
			}, 2000);
		}
	}

	/**
	 * 选择档案
	 */
	function selectProfile(profile) {
		if ($scope.myOptions.dsInfo.code == 'googleanalysis') {

			//ga下用的是profileId.需转为id统一处理
			profile['id'] = profile.profileId;
		}

		$scope.myOptions.profile.current = profile;
		$scope.myOptions.other.btnDisabled = false;

		//存储widget信息
		onboardingDataSrc.setData('profile', $scope.myOptions.profile);
	}

	/**
	 * 重置搜索列表
	 */
	function restDsAccountList(data) {
		$scope.myOptions.profile.list = data;
	}

	/**
	 * 搜索回调(一级目录)
	 */
	function collapseAllItem(flag) {
		if ($scope.myOptions.search.dsSearchIndex) {
			$timeout(function () {
				$.each($scope.myOptions.search.dsSearchIndex, function (i) {
					$scope.myOptions.search.dsSearchIndex[i] = flag;
				});
			});
		}
	}

	/**
	 * 搜索回调(二级目录)
	 */
	function collapseAllItem2(flag) {
		if ($scope.myOptions.search.dsSearchIndex) {
			$timeout(function () {
				$.each($scope.myOptions.search.dsSearchIndex, function (i) {
					$scope.myOptions.search.dsSearchIndex[i] = flag;
				});
				$.each($scope.myOptions.search.dsSearchIndexChild, function (i) {
					$.each($scope.myOptions.search.dsSearchIndexChild[i], function (j) {
						$scope.myOptions.search.dsSearchIndexChild[i][j] = flag;
					})
				});
			});
		}
	}

	/**
	 * 档案搜索
	 */
	function searchProfile() {
		if ($scope.myOptions.dsInfo.config.editor.source.gaSearch) {
			searchGaAccount();
		} else {
			searchDsAccount();
		}

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"onboarding",
			"what":"select_profile_search",
			"how":"click",
			"value": $scope.myOptions.search.dsSearchKey
		});
	}

	/**
	 * 档案搜索(GA之外的数据源)
	 */
	function searchDsAccount() {
		var config = $scope.myOptions.dsInfo.config.editor.source;
		var key = $scope.myOptions.search.dsSearchKey,
			tierData = ['child'],
			temp = $scope.myOptions.profile.listTmp,
			fun = restDsAccountList,
			tier = 1,
			backFunc = collapseAllItem;

		if (config.twoLayer) {
			tier = 2;
			tierData = ['child', 'child'];
		} else if (config.threeLayer) {
			tier = 3;
			backFunc = collapseAllItem2;
		}
		uiSearch.search(key, tier, tierData, temp, fun, backFunc);
	}

	/**
	 * 档案搜索(GA专用)
	 */
	function searchGaAccount() {
		if (!$scope.myOptions.profile.listTmp) {
			return;
		}
		if ($scope.myOptions.search.dsSearchKey) {
			var reg = new RegExp('.*?' + $scope.myOptions.search.dsSearchKey + '.*?', "i");
			var tempCopyGaAccount = angular.copy($scope.myOptions.profile.listTmp);
			$.each($scope.myOptions.profile.listTmp, function (o, one) {
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
							$scope.myOptions.ga.propertieList[o].p[t] = true;
						}
						if (reg.test(three['profileName']) || reg.test(three['webpropertyName']) || reg.test(three['accountName'])) {
							//则第一层 不能删
							oneDelFlag = false;
							$scope.myOptions.ga.propertieList[o].t = true;
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
			$scope.myOptions.profile.list = tempCopyGaAccount;
		} else {
			$scope.myOptions.profile.list = $scope.myOptions.profile.listTmp;
			listPackUp();
		}
	}

	/**
	 * 获取搜索对象
	 */
	function getObject(Origin, Key, Value) {
		var T, F;
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
			} else {
				if ('webproperties' in T) {
					T = getObject(T['webproperties'], Key, Value);
					if (T) return T
				} else {
					if ('profiles' in T) {
						T = getObject(T['profiles'], Key, Value);
						if (T) return T
					}
				}
			}
		}
	}
}

