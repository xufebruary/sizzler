'use strict';

import {
	LINK_DS,
	LINK_ADD_BY_TEMPLATE,
	LINK_WIDGET_TEMPLET_LIST,
	uuid,
	getMyDsConfig,
	isDomain,
} from 'components/modules/common/common';


/**
 * widget Gallery
 * widget模版
 *
 * Created by ao at 20160809
 */

angular
	.module('pt')
	.controller('dataSourceCtrl', dataSourceCtrl);

dataSourceCtrl.$inject = ['$rootScope', '$scope', '$document', '$timeout', '$translate', 'dataMutualSrv', 'uiLoadingSrv', 'onboardingDataSrc', 'publicDataSrv', 'siteEventAnalyticsSrv'];

function dataSourceCtrl($rootScope, $scope, $document, $timeout, $translate, dataMutualSrv, uiLoadingSrv, onboardingDataSrc, publicDataSrv, siteEventAnalyticsSrv) {
	'use strict';
	var body = $document.find('body').eq(0);

	$scope.myOptions = {
		currentStep: null, // ds || gallery
		isSuccess: false,   //是否创建成功

		//用户设置信息
		userInfo: null,

		//空间信息
		spaceInfo: null,

		//Dashboard
		dashboardId: uuid(),

		//i18n
		headTitle: $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.TITLE'),
		footerTips: $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.TIPS_SELECT'),

		//ds相关
		dsList: [],
		currentDs: null,
		errorDs: null,
		imgLoadNum: 0,  //图片加载数量
		serviceDsList: null,    //高级服务数据源列表


		//gallery相关
		galleryList: [],
		galleryTmpList: [], //备用列表,以备搜索
		sltGalleryList: [],
		errorGallery: null,
		queryList: null,    //列表请求状态(failed, success)

		//搜索
		modelSearch: null,

		//按钮
		btnLeftI18n: $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.BTN_LEFT'),
		btnRightI18n: $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.BTN_RIGHT'),
		btnIsDisabled: true,
		helpInfoA: $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.TIPS_HELP_1'),
		helpInfoB: $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.TIPS_HELP_2'),
		helpInfoC: $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.TIPS_HELP_3')
	};


	$scope.clickEvent = clickEvent;
	$scope.chooseEvent = chooseEvent;
	$scope.keyEvent = keyEvent;
	$scope.search = search;
	$scope.imgLoad = imgLoad;
	$scope.callIntercom = callIntercom;

	dataInit();

	//===============

	/**
	 * 数据初始化
	 */
	function dataInit() {
		$scope.myOptions.userInfo = onboardingDataSrc.getData('userInfo');
		$scope.myOptions.spaceInfo = onboardingDataSrc.getData('spaceInfo');
		$scope.myOptions.currentStep = onboardingDataSrc.getData('currentStep');
		$scope.myOptions.dsList = onboardingDataSrc.getData('dsList');
		$scope.myOptions.serviceDsList = publicDataSrv.getPublicData('serviceDsList');

		if(!$scope.myOptions.dsList){
			getDsList();
		} else {
			dataRecover();
		}
	}

	/**
	 * 数据恢复(Back进入)
	 */
	function dataRecover(){
		$scope.myOptions.galleryList = onboardingDataSrc.getData('galleryList');
		$scope.myOptions.galleryTmpList = angular.copy($scope.myOptions.galleryList);
		$scope.myOptions.sltGalleryList = onboardingDataSrc.getData('gallerySltList');
		$scope.myOptions.currentDs = onboardingDataSrc.getData('dsInfo');
		$scope.myOptions.headTitle = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.TITLE');
		$scope.myOptions.btnLeftI18n = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.BTN_LEFT');
		$scope.myOptions.btnRightI18n = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.BTN_RIGHT');
		$scope.myOptions.footerTips = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.TIPS_SELECT');
		$scope.myOptions.helpInfoA = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.TIPS_HELP_1');
		$scope.myOptions.helpInfoB = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.TIPS_HELP_2');
		$scope.myOptions.helpInfoC = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.TIPS_HELP_3');
		$scope.myOptions.btnIsDisabled = false;

		//页面渲染完成
		$scope.$on('$viewContentLoaded', function() {
			$timeout(function() {
				//loading
				uiLoadingSrv.removeLoading(angular.element('.widget-gallery-bd'));
			},0);
		});
	}

	/**
	 * 点击事件(next,back,save,cancel)
	 */
	function clickEvent(position) {
		var flag;

		if (position == 'left') {
			if ($scope.myOptions.currentStep == 'ds') {
				$scope.close();
			} else {
				$scope.myOptions.currentStep = 'ds';
				$scope.myOptions.btnIsDisabled = false;
				$scope.myOptions.sltGalleryList = [];
				$scope.myOptions.headTitle = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.TITLE');
				$scope.myOptions.btnLeftI18n = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.BTN_LEFT');
				$scope.myOptions.btnRightI18n = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.BTN_RIGHT');
				$scope.myOptions.footerTips = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.TIPS_SELECT');
				$scope.myOptions.helpInfoA = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.TIPS_HELP_1');
				$scope.myOptions.helpInfoB = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.TIPS_HELP_2');
				$scope.myOptions.helpInfoC = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.TIPS_HELP_3');

				onboardingDataSrc.setData('currentStep', 'ds');

				//GTM
				siteEventAnalyticsSrv.setGtmEvent('click_element','onboarding','select_metrics_back');
			
				//全站事件统计
				siteEventAnalyticsSrv.createData({
				    "uid": $rootScope.userInfo.ptId,
				    "where":"onboarding",
					"what":"select_metrics_back",
					"how":"click"
				});
			}
		}
		else if (position == 'right') {
			//数据校验
			flag = dataCheck($scope.myOptions.currentStep);

			if (flag) {
				if ($scope.myOptions.currentStep == 'ds') {

					if($scope.myOptions.currentDs.config.dataSource.ds.connect != 'direct' ){
						//如果数据源类型不属于直接连接类型,则直接进入数据源界面

						onboardingDataSrc.updateOnboarding('pt.dataSources.'+$scope.myOptions.currentDs.code);
						return;
					}

					$scope.myOptions.currentStep = 'gallery';
					$scope.myOptions.errorDs = null;
					$scope.myOptions.queryList = null;
					$scope.myOptions.modelSearch = null;
					$scope.myOptions.sltGalleryList = [];
					$scope.myOptions.galleryList = [];
					$scope.myOptions.headTitle = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.TITLE');
					$scope.myOptions.btnLeftI18n = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.BTN_LEFT');
					$scope.myOptions.btnRightI18n = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.BTN_RIGHT');
					$scope.myOptions.footerTips = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.TIPS_SELECT');
					$scope.myOptions.helpInfoA = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.TIPS_HELP_1');
					$scope.myOptions.helpInfoB = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.TIPS_HELP_2');
					$scope.myOptions.helpInfoC = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_GALLERY.TIPS_HELP_3');
					$scope.myOptions.btnIsDisabled = true;

					getGalleryList('gallery');

					onboardingDataSrc.setData('currentStep', 'gallery');

					//从数据源界面进入gallery界面,需要清空数据
					onboardingDataSrc.setData('gallerySltList', null);

					//GTM
					siteEventAnalyticsSrv.setGtmEvent('click_element','onboarding','select_data_source_next', $scope.myOptions.currentDs.name);
					
					//全站事件统计
					siteEventAnalyticsSrv.createData({
					    "uid": $rootScope.userInfo.ptId,
					    "where": "onboarding",
						"what": "select_datasource_next",
						"how": "click",
						"value": $scope.myOptions.currentDs.code
					});
				}
				else {
					$scope.myOptions.errorGallery = false;

					//存储选中的gallery列表
					onboardingDataSrc.setData('gallerySltList', $scope.myOptions.sltGalleryList);

					addWidget();
				}
			} else {
				$scope.myOptions.errorDs = true;

				//显示2秒,自动隐藏
				$timeout(function () {
					$scope.$apply(function () {
						$scope.myOptions.errorDs = null;
					})
				}, 2000);
			}
		}
	}

	/**
	 * 选择事件(数据源, gallery)
	 *
	 * @param type: [ds || gallery]
	 * @param data: [dsObject, galleryObject]
	 * @param flag: 是否选中
	 */
	function chooseEvent(type, data, flag) {
		if (type == 'ds') {
			$scope.myOptions.currentDs = flag ? data : null;

			//存储
			onboardingDataSrc.setData('dsInfo', $scope.myOptions.currentDs);

			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where": "onboarding",
				"what": "select_datasource",
				"how": "click",
				"value": data.code
			});
		} else {
			var galleryIndex = -1;
			for(var i=0; i<$scope.myOptions.sltGalleryList.length; i++){
				if($scope.myOptions.sltGalleryList[i].templetId == data.templetId){
					galleryIndex = i;
					break;
				}
			}

			if (flag && galleryIndex < 0) {
				$scope.myOptions.sltGalleryList.push(data);
			} else if (!flag && galleryIndex >= 0) {
				$scope.myOptions.sltGalleryList.remove(galleryIndex);
			}

			//全站事件统计
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"onboarding",
				"what":"select_metrics",
				"how":"click",
				"value": data.templetId
			});
		}

		$scope.$apply(function () {
			$scope.myOptions.btnIsDisabled = !dataCheck(type);
		})
	}

	/**
	 * 搜索框回车事件
	 */
	function keyEvent(e) {
		var keycode = window.event ? e.keyCode : e.which;
		if (keycode == 13) {
			search();
		}
	}

	/**
	 * 搜索
	 */
	function search() {
		angular.element('.widget-gallery-chart-info').removeAttr('style');
		$scope.myOptions.galleryList = null;

		if ($scope.myOptions.modelSearch && $scope.myOptions.modelSearch != '') {

			//存储选中的gallery列表
			onboardingDataSrc.setData('gallerySltList', $scope.myOptions.sltGalleryList);

			getGalleryList('search');
		} else {
			//当搜索空时,显示所有
			$scope.myOptions.galleryList = angular.copy($scope.myOptions.galleryTmpList);
		}

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"onboarding",
			"what":"select_metrics_search",
			"how":"click",
			"value": $scope.myOptions.modelSearch
		});
	}

	/**
	 * 图片加载
	 */
	function imgLoad() {
		$scope.myOptions.imgLoadNum++;

		if ($scope.myOptions.dsList && $scope.myOptions.imgLoadNum >= $scope.myOptions.dsList.length) {
			//loading
			uiLoadingSrv.removeLoading(angular.element('.widget-gallery-bd'));
		}
	}

	/**
	 * 数据校验
	 */
	function dataCheck(type) {
		if (type == 'ds') {
			return $scope.myOptions.currentDs ? true : false;
		} else {
			return $scope.myOptions.sltGalleryList.length > 0;
		}
	}

	/**
	 * 添加widget
	 * url中带有参数isPreview,是在dataMutualSrv方法中依靠type字段判断添加的
	 */
	function addWidget() {
		//loading
		uiLoadingSrv.createLoading(angular.element('.onboarding-ds'));

		var url = LINK_ADD_BY_TEMPLATE + '/' + $scope.myOptions.spaceInfo.current.spaceId + '/' + $scope.myOptions.dashboardId;
		var sendData = [];
		var gtmSendData = '';
		for (var i = 0; i < $scope.myOptions.sltGalleryList.length; i++) {
			sendData.push($scope.myOptions.sltGalleryList[i].templetId);
			gtmSendData = gtmSendData + '[\"' + $scope.myOptions.sltGalleryList[i].templetId + '\"]';
		}

		dataMutualSrv.post(url, sendData, 'onboardingAddWidget').then(function (data) {
			if (data.status == 'success') {
				saveAfter(data.content);
			} else {
				console.log('新增widget gallery失败!');
				if (data.status == 'error') {
					console.log(data.message)
				}

				//loading
				uiLoadingSrv.removeLoading(angular.element('.onboarding-ds'));
			}

		});

		//GTM
		siteEventAnalyticsSrv.setGtmEvent('click_element','onboarding','select_metrics_next', gtmSendData);

		//全站事件统计
		siteEventAnalyticsSrv.createData({
		    "uid": $rootScope.userInfo.ptId,
		    "where":"onboarding",
			"what":"select_metrics_next",
			"how":"click",
			"value": gtmSendData
		});
	}

	/**
	 * 添加成功后
	 * 为每个widget添加尺寸及位置信息
	 */
	function saveAfter(list) {
		var rowMax = 0;         //最大行值
		var colMax = 0;         //最大列值
		var currentRowMaxY = 0; //当前行中最大Y值

		//widget尺寸及位置信息生成
		for (var i = 0; i < list.length; i++) {
			var sizeX = 12;
			var sizeY = 8;
			var minx = 6;
			var miny = 8;

			if (list[i].baseWidget['sizeX']) {
				sizeX = list[i].baseWidget.sizeX;
				sizeY = list[i].baseWidget.sizeY;
			}

			if (list[i].baseWidget.widgetType == 'tool' && list[i].baseWidget.widgetType == 'text') {
				minx = 3;
				miny = 2;
			}

			list[i]['sizeX'] = sizeX;
			list[i]['sizeY'] = sizeY;
			list[i]['minSizeX'] = minx;
			list[i]['minSizeY'] = miny;

			//如果当前列数超过最大列数,则再起一行.
			//36为当前面版的栅格化最大列数
			if((colMax + sizeX) > 36){
				rowMax = rowMax + currentRowMaxY;
				colMax = 0;
				currentRowMaxY = 0;
			}

			list[i]['row'] = rowMax;
			list[i]['col'] = colMax;

			colMax = colMax+sizeX;
			currentRowMaxY = Math.max(currentRowMaxY, sizeY);
		}

		//存储返回的widget列表
		onboardingDataSrc.setData('widgetSltList', list);

		//设置dashboardId
		onboardingDataSrc.setData('dashboardId', $scope.myOptions.dashboardId);

		//loading
		uiLoadingSrv.removeLoading(angular.element('.onboarding-ds'));

		//切换路由
		onboardingDataSrc.stateChange('onboarding.preview');
	}

	/**
	 * 获取gallery列表
	 */
	function getGalleryList(type) {
		//loading
		uiLoadingSrv.createLoading(angular.element('.widget-gallery-bd'));

		if (type == 'gallery' || type == 'search') {
			var url = LINK_WIDGET_TEMPLET_LIST;
			var sendData = {
				searchKey: $scope.myOptions.modelSearch,
				dsCode: $scope.myOptions.currentDs.code,
				isPublish: [1],
				publishArea: [getMyHostName() + '-' + $scope.myOptions.userInfo.locale]
			}
		}
		dataMutualSrv.post(url, sendData).then(function (data) {
			if (data.status == 'success') {

				$scope.myOptions.galleryList = angular.copy(data.content);

				if (type == 'gallery') {
					//存储所有widget模版以备搜索
					$scope.myOptions.galleryTmpList = angular.copy(data.content);

					//存储以备恢复
					onboardingDataSrc.setData('galleryList', angular.copy(data.content));
				}
			} else {
				console.log('获取widget gallery列表失败!');
				if (data.status == 'failed') {
					//console.log('Post Data Failed!')
				} else if (data.status == 'error') {
					//console.log('Post Data Error: ');
					console.log(data.message)
				}
			}

			$scope.myOptions.queryList = data.status;

			//loading
			uiLoadingSrv.removeLoading(angular.element('.widget-gallery-bd'));
		})
	}

	/**
	 * 获取支持模板的数据源列表
	 */
	function getDsList() {
		//获取数据源字典表列表
		dataMutualSrv.get(LINK_DS + '/' + $scope.myOptions.spaceInfo.current.spaceId).then(function (data) {
			if (data.status == 'success') {
				for (var i = 0; i < data.content.length; i++) {
					data.content[i].config = getMyDsConfig(data.content[i].code)
				}

				$scope.myOptions.dsList = angular.copy(data.content);
				onboardingDataSrc.setData('dsList', angular.copy(data.content));

				console.log($scope.myOptions.dsList)
			} else if (data.status == 'failed') {
				console.log('Get dsList Failed!')
			} else if (data.status == 'error') {
				console.log('Get dsList Error: ');
				console.log(data.message)
			}
		});
	}

	/**
	 * 获取域名
	 */
	function getMyHostName() {
		var hostName = 'datadeck.jp';

		if (isDomain('datadeck.com')) {
			hostName = 'datadeck.com';
		} else if (isDomain('datadeck.cn')) {
			hostName = 'datadeck.cn';
		}

		return hostName;
	}

	/**
	 * 调用对话框插件(Intercom)
	 */
	function callIntercom() {
		

		//全站事件统计
		if($scope.myOptions.currentStep == 'gallery'){
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"onboarding",
				"what":"select_metrics_tell_us",
				"how":"click"
			});
		}
		else {
			siteEventAnalyticsSrv.createData({
			    "uid": $rootScope.userInfo.ptId,
			    "where":"onboarding",
				"what":"select_datasource_tell_us",
				"how":"click"
			});
		}
	}
}


/**
 * 单个数据源模版
 */
angular
	.module('pt')
	.directive('singleDataSource', ['$translate', singleDataSourceFunc]);

function singleDataSourceFunc($translate) {
	return {
		restrict: 'EA',
		replace: true,
		template: '<div class="box" ng-class="{\'box-hover-wrap\': ds.isPlus != 2}">'
				+ '  <div class="box-active">'
				+ '      <svg>'
				+ '          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-ok"></use>'
				+ '      </svg>'
				+ '   </div>'
				+ '   <div class="box-img">'
				+ '        <img alt="Google Analytics" ng-src="/assets/images/ds/set-{{ds.code}}.jpg">'
				+ '    </div>'
				+ '    <div class="box-hover-dom" ng-if="ds.isPlus != 2">'
				+ '         <div class="box-hover">{{hoverInfo}}</div>'
				+ '    </div>'
				+ '</div>',
		link: link
	};

	function link(scope, element, attrs) {
		var dom = angular.element(element);
		var ds = angular.fromJson(attrs.ds);

		/**
		 * isPlus:
		 * 0 - 内测
		 * 1 - 高级
		 * 2 - 开放
		 */
		if(ds.isPlus == '0'){
			//内测服务
			scope.hoverInfo = $translate.instant('ONBOARDING.DATA_SOURCE.CHOOSE_DS.TIPS_HOVER_TEST');
		} else if(ds.isPlus == '1'){
			//高级服务
			scope.hoverInfo = $translate.instant("ONBOARDING.DATA_SOURCE.CHOOSE_DS.TIPS_HOVER_ADVANCED");
		}

		//默认选中
		if (scope.myOptions.currentDs && scope.myOptions.currentDs.code == ds.code) {
			dom.addClass('active');
		}

		dom.on('click', function () {
			if(ds.isPlus == '2') {
				angular.element(this).toggleClass('active').siblings('.box').removeClass('active');

				scope.chooseEvent('ds', ds, dom.hasClass('active'))
			} else {
				
			}
		});

		dom.find('img').on('load', function () {
			scope.imgLoad();
		})
	}
}


/**
 * 单个gallery模版
 */
angular
	.module('pt')
	.directive('singleGallery', ['onboardingDataSrc', singleGalleryFunc]);

function singleGalleryFunc(onboardingDataSrc) {
        return {
            restrict: 'EA',
            replace: true,
            template: '<div class="box widget-gallery-chart" ng-class="{\'active\': isActive}">'
                    + '  <div class="box-active">'
                    + '     <svg>'
                    + '         <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-ok"></use>'
                    + '     </svg>'
                    + '   </div>'
                    + '   <div class="widget-gallery-chart-bd">'
                    + '     <span>{{gallery.widgetTitle}}</span>'
                    + '     <div class="widget-gallery-chart-img"><img ng-src="/assets/images/chart/{{gallery.templetGraphName}}.png"></div>'
                    + '   </div>'
                    + '    <div class="widget-gallery-chart-ds">'
                    + '        <span>'
                    + '           <svg>'
                    + '              <use xlink:href="{{\'#icon-ds-\'+gallery.dsCode.toLowerCase()}}"></use>'
                    + '          </svg>'
                    + '      </span>'
                    + '  </div>'
                    + '</div>',
            link: link
        };

        function link(scope, element, attrs) {
            var dom = angular.element(element);
            var infoDom = angular.element('.widget-gallery-chart-info');
            var gallerySltList = onboardingDataSrc.getData('gallerySltList');
            scope.isActive = false;
            scope.gallery = angular.fromJson(attrs.gallery);
            scope.gallery.widgetTitle = scope.gallery.widgetTitle ? angular.fromJson(scope.gallery.widgetTitle)[scope.myOptions.userInfo.locale] : '';
            scope.gallery.description = scope.gallery.description ? angular.fromJson(scope.gallery.description)[scope.myOptions.userInfo.locale] : '';

            if(gallerySltList){
                for(var i=0; i<gallerySltList.length; i++){
                    if(gallerySltList[i].templetId == scope.gallery.templetId){
                        scope.isActive = true;
                        break;
                    }
                }
            }

            dom.mouseenter(function () {
                infoDom.find('.info-title').html(scope.gallery.widgetTitle);
                infoDom.find('.info-bd').html(scope.gallery.description);

                var domPosition = dom.position();
                var infoTop = (domPosition.top < 0 ? 0 : domPosition.top) - infoDom.height() - 30;
                var infoLeft = domPosition.left < 0 ? 0 : domPosition.left;
                console.log(domPosition);

                //infoDom.css({
                //    top: infoTop,
                //    left: infoLeft,
                //    display: 'block',
                //    opacity: 0
                //}).stop().animate({
                //    top: infoTop - 35,
                //    opacity: 1
                //}, 'normal')

                infoDom.css({
                    top: infoTop,
                    left: infoLeft,
                    display: 'block',
                    opacity: 0
                }).stop().addClass('fadeInUp');
            });

            dom.mouseleave(function () {
                infoDom.removeAttr('style');
            });

            dom.on('click', function () {
                angular.element(this).toggleClass('active');

                scope.chooseEvent('gallery', scope.gallery, dom.hasClass('active'))
            })
        }
    }
