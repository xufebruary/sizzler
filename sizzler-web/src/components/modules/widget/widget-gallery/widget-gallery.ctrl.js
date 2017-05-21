/**
 * widget Gallery
 * widget模版
 *
 * Created by ao at 20160809
 */

import {
	LINK_ADD_BY_TEMPLATE,
	LINK_WIDGET_TEMPLET_LIST,
	LINK_WIDGET_TEMPLATE_DS_LIST,
	uuid,
	isDomain
} from '../../common/common';


angular
.module('pt')
.controller('widgetGalleryCtrl', widgetGalleryCtrl);

widgetGalleryCtrl.$inject = ['$rootScope', '$scope', '$state', '$document', '$timeout', '$translate', 'dataMutualSrv', 'uiLoadingSrv', 'linkDataSrv', 'siteEventAnalyticsSrv','Track'];

function widgetGalleryCtrl($rootScope, $scope, $state, $document, $timeout, $translate, dataMutualSrv, uiLoadingSrv, linkDataSrv, siteEventAnalyticsSrv,Track) {
    'use strict';
    var body = $document.find('body').eq(0);

	$scope.myOptions = {
		currentStep: 'ds', // ds || gallery
		isSuccess: false,   //是否创建成功

		//i18n
		headTitle: $translate.instant('WIDGET.GALLERY.STEP_1.TITLE'),
		footerTips: $translate.instant('WIDGET.GALLERY.STEP_1.TIPS_SELECT'),

		//ds相关
		dsList: [],
		currentDs: null,
		errorDs: null,
		imgLoadNum: 0,  //图片加载数量


		//gallery相关
		galleryList: [],
		galleryTmpList: [], //备用列表,以备搜索
		sltGalleryList: [],
		errorGallery: null,
		queryList: null,    //列表请求状态(failed, success)

		//搜索
		modelSearch: null,

		//按钮
		btnLeftI18n: $translate.instant('WIDGET.GALLERY.STEP_1.BTN_LEFT'),
		btnRightI18n: $translate.instant('WIDGET.GALLERY.STEP_1.BTN_RIGHT'),
		btnIsDisabled: true
	};


	$scope.close = close;
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
		//收起顶部
		$scope.pt.settings.headFolded = true;

		if ($scope.rootPage.dashboardMode == 'EDIT') {
			body.addClass('modal-open');
			getDsList();
		} else {
			//非编辑模式下进入,则退出
			close();
		}

		//全站事件统计
        siteEventAnalyticsSrv.createData({
            "uid": $rootScope.userInfo.ptId,
            "where":"panel",
            "what":"edit_panel_add_widget",
            "how":"click",
            "value": 'widget_gallery'
        });
	}

    /**
     * 关闭
     */
    function close(type) {
        $state.go('pt.dashboard');
        body.removeClass('modal-open');

        $timeout(function(){
            $(window).scroll();
        }, 50);

        if(type && type == 'add'){

            if ($scope.myOptions.currentStep == 'ds') {
                //GTM
                siteEventAnalyticsSrv.setGtmEvent('click_element','widget_gallery','select_data_source_cancel');
            } else {
                //GTM
                siteEventAnalyticsSrv.setGtmEvent('click_element','widget_gallery','select_metrics_cancel');
            }
        }
    }

	/**
	 * 点击事件(next,back,save,cancel)
	 */
	function clickEvent(position) {
		var flag;

		if (position == 'left') {
			if ($scope.myOptions.currentStep == 'ds') {
				$scope.close();
				Track.log({where: 'widget_gallery', what: 'select_data_source_cancel',value: $scope.myOptions.currentDs && $scope.myOptions.currentDs.code});
			} else {
				$scope.myOptions.currentStep = 'ds';
				$scope.myOptions.btnIsDisabled = false;
				$scope.myOptions.sltGalleryList = [];
				$scope.myOptions.headTitle = $translate.instant('WIDGET.GALLERY.STEP_1.TITLE');
				$scope.myOptions.btnLeftI18n = $translate.instant('WIDGET.GALLERY.STEP_1.BTN_LEFT');
				$scope.myOptions.btnRightI18n = $translate.instant('WIDGET.GALLERY.STEP_1.BTN_RIGHT');
				$scope.myOptions.footerTips = $translate.instant('WIDGET.GALLERY.STEP_1.TIPS_SELECT');
				Track.log({where: 'widget_gallery', what: 'back'});
			}
		}
		else if (position == 'right') {
			//数据校验
			flag = dataCheck($scope.myOptions.currentStep);

			if (flag) {
				if ($scope.myOptions.currentStep == 'ds') {
					$scope.myOptions.currentStep = 'gallery';
					$scope.myOptions.errorDs = null;
					$scope.myOptions.queryList = null;
					$scope.myOptions.modelSearch = null;
					$scope.myOptions.sltGalleryList = [];
                    $scope.myOptions.galleryList = [];
					$scope.myOptions.headTitle = $translate.instant('WIDGET.GALLERY.STEP_2.TITLE');
					$scope.myOptions.btnLeftI18n = $translate.instant('WIDGET.GALLERY.STEP_2.BTN_LEFT');
					$scope.myOptions.btnRightI18n = $translate.instant('WIDGET.GALLERY.STEP_2.BTN_RIGHT');
					$scope.myOptions.footerTips = $translate.instant('WIDGET.GALLERY.STEP_2.TIPS_SELECT');
					$scope.myOptions.btnIsDisabled = true;

					getGalleryList('gallery');
                    //GTM
                    siteEventAnalyticsSrv.setGtmEvent('click_element','widget_gallery','select_data_source_next', $scope.myOptions.currentDs.name);

					Track.log({where: 'widget_gallery', what: 'select_data_source_next',value: $scope.myOptions.currentDs.code});
                } else {
                    $scope.myOptions.errorGallery = false;
                    addWidget();
					//全站数据统计
					var values = '';
					if($scope.myOptions.sltGalleryList && $scope.myOptions.sltGalleryList.length){
						$scope.myOptions.sltGalleryList.forEach(function(item,index){
							if(index === ($scope.myOptions.sltGalleryList.length -1)){
								values += item.templetId + ''
							}else{
								values += item.templetId + ','
							}
						});
					}
					Track.log({where: 'widget_gallery', what: 'add_widget',value: values });
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
			Track.log({where: 'widget_gallery', what: 'select_data_source',value: flag ? data.code : ''});
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
            //GTM
            siteEventAnalyticsSrv.setGtmEvent('click_element','onboarding','select_metrics', data.widgetTitle);

			Track.log({where: 'widget_gallery', what: 'select_widget_gallery',value: flag ? data.templetId : ''});
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
			getGalleryList('search');
		} else {
			//当搜索空时,显示所有
			$scope.myOptions.galleryList = angular.copy($scope.myOptions.galleryTmpList);
		}
        //GTM
        siteEventAnalyticsSrv.setGtmEvent('click_element','widget_gallery','select_metrics_search');

		Track.log({where: 'widget_gallery', what: 'widget_gallery_search',value: $scope.myOptions.modelSearch || ''});
	}

	/**
	 * 图片加载
	 */
	function imgLoad() {
		$scope.myOptions.imgLoadNum++;

		if ($scope.myOptions.imgLoadNum >= $scope.myOptions.dsList.length) {
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
			return $scope.myOptions.sltGalleryList.length <= 0 ? false : true;
		}
	}

	/**
	 * 添加widget
	 */
	function addWidget() {
		//loading
		uiLoadingSrv.createLoading(angular.element('.popup-content'));

		var url = LINK_ADD_BY_TEMPLATE + '/' + $scope.rootSpace.current.spaceId + '/' + $scope.rootPanel.nowId;

		var sendData = [];
		for (var i = 0; i < $scope.myOptions.sltGalleryList.length; i++) {
			sendData.push($scope.myOptions.sltGalleryList[i].templetId);
		}

		dataMutualSrv.post(url, sendData).then(function (data) {
			if (data.status == 'success') {
				saveAfter(data.content);
			} else {
				console.log('新增widget gallery失败!');
				if (data.status == 'error') {
					console.log(data.message)
				}
			}

			//loading
			uiLoadingSrv.removeLoading(angular.element('.popup-content'));
		})
	}

	/**
	 * 添加成功后
	 * 为每个widget添加尺寸及位置信息
	 */
	function saveAfter(list) {
		var rowMax = 0;         //最大行值
		var colMax = 0;         //最大列值
		var currentRowMaxY = 0; //当前行中最大Y值

		//查找当前widget列表中最大行值
		if ($scope.rootWidget.list.length > 0) {
			$.each($scope.rootWidget.list, function (i, item) {
				var currentRow = item.row + item.sizeY;
				rowMax = currentRow >= rowMax ? currentRow : rowMax;
			});
		}

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
			if ((colMax + sizeX) > $scope.rootChart.columns) {
				rowMax = rowMax + currentRowMaxY;
				colMax = 0;
				currentRowMaxY = 0;
			}

			list[i]['row'] = rowMax;
			list[i]['col'] = colMax;

			list[i].autoPos = 1;

			colMax = colMax + sizeX;
			currentRowMaxY = Math.max(currentRowMaxY, sizeY);
		}

		//widget定位最后一个
		$scope.rootWidget.locateId = list[list.length - 1].baseWidget.widgetId;

		//前台新增widget
		$scope.rootWidget.noData = false;
		$scope.rootWidget.list = $scope.rootWidget.list.concat(list);

		//更新添加成功状态
		$scope.myOptions.isSuccess = true;

		//2秒后关闭
		$timeout(function () {
            $scope.close('add');
		}, 1500);

		//批量授权检测
		linkDataSrv.update($scope);

		//位置更新
		$scope.widgetLayoutUpdate();
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
				publishArea: [getMyHostName() + '-' + $scope.rootUser.settingsInfo.locale]
			}
		}
		dataMutualSrv.post(url, sendData).then(function (data) {
			if (data.status == 'success') {

				$scope.myOptions.galleryList = angular.copy(data.content);

				if (type == 'gallery') {
					//存储所有widget模版以备搜索
					$scope.myOptions.galleryTmpList = angular.copy(data.content);
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
		var sendData = {
			searchKey: '',
			dsCode: '',
			isPublish: [1],
			publishArea: [getMyHostName() + '-' + $scope.rootUser.settingsInfo.locale]
		};

		dataMutualSrv.post(LINK_WIDGET_TEMPLATE_DS_LIST + '/' + $scope.rootSpace.current.spaceId, sendData).then(function (data) {
			if (data.status == 'success') {
				$scope.myOptions.dsList = angular.copy(data.content);
			} else {
				console.log('widget gallery请求数据源列表出错!');
				if (data.status == 'error') {
					console.log(data.message)
				}
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
		
		Track.log({where: 'widget_gallery', what: 'tell_us'});
	}

	//页面渲染完成
	//$scope.$on('$viewContentLoaded', function(event) {
	//    $timeout(function() {
	//        //loading
	//        uiLoadingSrv.createLoading(angular.element('.widget-gallery-bd'));
	//    },0);
	//});
}


/**
 * 单个数据源模版
 */
angular
.module('pt')
    .directive('singleGalleryDataSource', [singleGalleryDataSourceFunc]);

function singleGalleryDataSourceFunc() {
	return {
		restrict: 'EA',
		replace: true,
		scope: {
			galleryDs: "=",
			galleryCurrentDs: "=",
			galleryChooseEvent: "&",
			galleryImgLoad: "&",
		},
		template: '<div class="box">'
				+ '  <div class="box-active">'
				+ '      <svg>'
				+ '          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-ok"></use>'
				+ '      </svg>'
				+ '   </div>'
				+ '   <div class="box-img">'
				+ '        <img alt="Google Analytics" ng-src="/assets/images/ds/set-{{galleryDs.code}}.jpg">'
				+ '    </div>'
				+ '</div>',
		link: link
	};

	function link(scope, element, attrs) {
		var dom = angular.element(element);

		//默认选中
		if (scope.galleryCurrentDs && scope.galleryCurrentDs.code == scope.galleryDs.code) {
			dom.addClass('active');
		}

		dom.on('click', function () {
			angular.element(this).toggleClass('active').siblings('.box').removeClass('active');

			scope.galleryChooseEvent({type: 'ds', data: scope.galleryDs, flag: dom.hasClass('active')})
		});

		dom.find('img').on('load', function () {
			scope.galleryImgLoad();
		})
	}
}


/**
 * 单个gallery模版
 */
angular
.module('pt')
    .directive('singleWidgetGallery', [singleWidgetGalleryFunc]);

function singleWidgetGalleryFunc() {
	return {
		restrict: 'EA',
		replace: true,
		scope: {
			galleryLocal: "=",
			galleryCurrent: "=",
			gallerySltList: "=",
			galleryChooseEvent: "&",
		},
		template: '<div class="box widget-gallery-chart" ng-class="{\'active\': isActive}">'
				+ '  <div class="box-active">'
				+ '     <svg>'
				+ '         <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-ok"></use>'
				+ '     </svg>'
				+ '   </div>'
				+ '   <div class="widget-gallery-chart-bd">'
				+ '     <span>{{galleryCurrent.widgetTitle}}</span>'
				+ '     <div class="widget-gallery-chart-img"><img ng-src="/assets/images/chart/{{galleryCurrent.templetGraphName}}.png"></div>'
				+ '   </div>'
				+ '    <div class="widget-gallery-chart-ds">'
				+ '        <span>'
				+ '           <svg>'
				+ '              <use xlink:href="{{\'#icon-ds-\'+galleryCurrent.dsCode.toLowerCase()}}"></use>'
				+ '          </svg>'
				+ '      </span>'
				+ '  </div>'
				+ '</div>',
		link: link
	};

	function link(scope, element, attrs) {
		var dom = angular.element(element);
		var infoDom = angular.element('.widget-gallery-chart-info');
		scope.isActive = false;
		scope.galleryCurrent.widgetTitle = scope.galleryCurrent.widgetTitle ? angular.fromJson(scope.galleryCurrent.widgetTitle)[scope.galleryLocal] : '';
		scope.galleryCurrent.description = scope.galleryCurrent.description ? angular.fromJson(scope.galleryCurrent.description)[scope.galleryLocal] : '';

		if(scope.gallerySltList){
			for(var i=0; i<scope.gallerySltList.length; i++){
				if(scope.gallerySltList[i].templetId == scope.galleryCurrent.templetId){
					scope.isActive = true;
					break;
				}
			}
		}

		dom.mouseenter(function () {
			infoDom.find('.info-title').html(scope.galleryCurrent.widgetTitle);
			infoDom.find('.info-bd').html(scope.galleryCurrent.description);

			var domPosition = dom.position();
			var infoTop = (domPosition.top < 0 ? 0 : domPosition.top) - infoDom.height() - 30;
			var infoLeft = domPosition.left < 0 ? 0 : domPosition.left;

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

			scope.galleryChooseEvent({type: 'gallery', data: scope.galleryCurrent, flag: dom.hasClass('active')})
		})
	}
}
