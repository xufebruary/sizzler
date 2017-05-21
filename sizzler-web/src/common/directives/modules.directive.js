/**
 * ui mosules
 * 小的组件集合
 */

import {
	LINK_SETTINGS_INFO_UPDATE,
	LINK_EXCEL_FILE_UPDATE_SOURCE_DATA,
	loginSvg
} from 'components/modules/common/common';

	//.directive('ptLog', ['$rootScope', 'siteEventAnalyticsSrv', ptLogFunc])//pt日志统计
	//.directive('uiWarning', [uiWarningFunc])//警告提示
	//.directive('domSvgFunc', [domSvgFunc]) //动态svg图标指令
	//.directive('svgBg', ['$timeout', svgBgFunc]) //svg background
	//.directive('loginSvgBg', ['$timeout', loginSvgBg]) //登陆、忘记密码等的绿色背景指令
	//.directive('funcTips', ['$translate', '$state', 'dataMutualSrv', funcTips]) //功能介绍气泡
	//.directive('worldHigh', [worldHighFunc]) //搜索关键字标绿
	//.directive('chartTipsTrigger', ['datasourceFactory', 'dataMutualSrv', '$timeout', chartTipsTrigger]) //widget底部获取错误信息，以及提示信息
	//.directive('dsAutoDateRate', ['$document', '$translate', dsAutoDateRate]) //数据源自定义更新频率设置
	//.directive('uiInput', ['$timeout', uiInput]) //Input输入框



export function myVarDirective(){
	return {
		restrict: 'A',
		link: link
	};
	function link(scope, element, attrs){
		var splits = attrs.myVar.split('=');
		scope.$watch(splits[1], function (val) {
			scope.$eval(attrs.myVar);
		});
	}
}

/**
 * pt日志统计
 */

ptLogFunc.$inject = ['$rootScope', 'siteEventAnalyticsSrv'];
export function ptLogFunc($rootScope, siteEventAnalyticsSrv) {
	'use strict';

	return {
		restrict: 'A',
		link: link
	};

	function link(scope, element, attrs) {
		var options;
		var uid = $rootScope && $rootScope.userInfo && $rootScope.userInfo.ptId;

		// =========

		init();

		// =========

		//入口
		function init() {
			//发送日志
			jQuery(element).ready(function(){
				jQuery(element).on('click', function(){
					options = getOptions();

					if(!options['uid'] && uid){
						options['uid'] = uid;
					}
					if(!options['how']){
						options['how'] = 'click';
					}

					siteEventAnalyticsSrv.createData(options);
				})
			})
		}

		//获取参数设置
		function getOptions() {
			var linkOptions;

			if (attrs.ptLog) {
				try{
					linkOptions = scope.$eval(attrs.ptLog);
				} catch(e){}
				if (angular.isObject(options) && angular.isObject(linkOptions)) {
					linkOptions = angular.extend({}, options, linkOptions);
				}
			} else if (options) {
				linkOptions = options;
			}
			return linkOptions;
		}
	}
}


/**
 * 警告提示
 */
export function uiWarningFunc() {
	'use strict';

	return {
		restrict: 'EA',
		template: '<span class="pt-tooltip">'
		+ '<svg>'
		+ '<use xlink:href="#icon-warning"></use>'
		+ '</svg>'
		+ '<div class="pt-inner pt-inner-left">'
		+ '<div class="pt-arrows pt-arrows-left"> </div>'
		+ '<div class="pt-content">{{myOptions.content}}</div>'
		+ '</div>'
		+ '</span>',
		link: link
	};

	function link(scope, element, attr) {
		$(element).on('mouseenter touchstart', function () {
			var tips_wrap = $(this).find('.pt-inner'),
				height = +tips_wrap.height();
			var newHeight = -(height + 10) + 'px';
			tips_wrap.css('top', newHeight);
		});

		scope.myOptions = {
			content: attr.tipscontent
		};
		console.log(scope.myOptions.content)
	}
}


/**
 * 动态svg图标指令
 */
export function domSvgFunc() {

	return {
		restrict: 'EA',
		replace: true,
		scope: true,
		template: '<svg class="{{myOptions.class}}"><use xlink:href="{{myOptions.svgId}}"></use></svg>',
		link: link
	};

	function link(scope, element, attr) {
		scope.myOptions = {
			svgId: attr.svgid,
			class: attr.class
		};
		scope.$watch(function () {
			return element.attr('data-svgid');
		}, function (newValue) {
			element.find('use').attr('xlink:href', newValue);
		});
	}
}


/**
 * svg背景
 */
svgBgFunc.$inject = ['$timeout'];
export function svgBgFunc($timeout) {
	'use strict';

	return {
		restrict: 'EA',
		replace: true,
		scope: true,
		template: '<svg class="login-bg" width="100%" height="100%" version="1.1" xmlns="http://www.w3.org/2000/svg"></svg>',
		link: link
	};

	function link(scope, element, attr) {

		scope.myOptions = {
			points: loginSvg(),
			pointHtml: null
		};

		scope.bgDraw = function () {
			scope.myOptions.pointHtml = '<polygon points="' + scope.myOptions.points[0] + '" style="fill:#8bc34a;"/><polygon points="' + scope.myOptions.points[1] + '" style="fill:#689f38; opacity:0.3"/>';
			angular.element('.login-bg').html(scope.myOptions.pointHtml);
		};

		jQuery(window).on('resize', function () {
			scope.myOptions.points = loginSvg();
			scope.bgDraw();
		});


		$timeout(function () {
			scope.bgDraw();
		}, 50);
	}
}


/**
 * 登陆、忘记密码等的绿色背景指令
 */
loginSvgBg.$inject = ['$timeout'];
export function loginSvgBg($timeout) {
	return {
		restrict: 'EA',
		link: function (scope, ele) {
			scope.svgData = {
				svgPoints: loginSvg()
			};
			function renderSvg() {
				$(ele).children('polygon:first-child').attr('points', scope.svgData.svgPoints[0]).end()
					.children('polygon:last-child').attr('points', scope.svgData.svgPoints[1]);
			}

			$timeout(function () {
				renderSvg();
			});
			$(window).resize(function () {
				scope.svgData.svgPoints = loginSvg();
				renderSvg();
			});
		}
	}
}


/**
 * 功能介绍气泡
 */
funcTips.$inject = ['$state','dataMutualSrv'];
export function funcTips($state, dataMutualSrv) {
	return {
		restrict: 'EA',
		scope: {
			onOk: '&',          //确认事件
			tipsArea: '@',      //功能区域
			tipsCode: '@',      //提示文字国际化Code
			tipsIsLeft: '@',    //提示框是否要居左显示
			tipsUserInfo: '='   //用户设置信息
		},
		replace: true,
		template: '<div class="notification-icon hide">'
		+ '     <span>'
		+ '         <svg>'
		+ '             <use xlink:href="#icon-ask"></use>'
		+ '         </svg>'
		+ '     </span>'
		+ '     <div class="notification-inner animated fadeInDown" ng-class="{\'left\': tipsIsLeft}">'
		+ '         <span class="arrows" ng-class="{\'left\': tipsIsLeft}"></span>'
		+ '         <div class="notification-info clear">'
		+ '             <p>{{tipsCode | translate}}</p>'
		+ '             <div class="clear">'
		//+ '                 <a ng-click="tipsClose()">{{myOptions.btnCancelLocalCode | translate}}</a>'
		+ '                 <button class="pt-btn notification-info-btn m-l-md" ng-click="tipsOk()">{{myOptions.btnOklLocalCode | translate}}</button>'
		+ '             </div>'
		+ '         </div>'
		+ '     </div>'
		+ '     <div class="notification-ring"></div>'
		+ '  </div>',
		link: function (scope, element, attrs) {
			scope.myOptions = {
				showTipsTmp: null,
				btnCancelLocalCode: "ONBOARDING.TIPS.FUNC_TIPS.BTN_CANCEL",
				btnOklLocalCode: "ONBOARDING.TIPS.FUNC_TIPS.BTN_OK"
			};
			console.log(scope.tipsArea + ': ' + scope.tipsIsLeft);
			// ===========

			scope.tipsClose = tipsClose;
			scope.tipsOk = tipsOk;

			init();

			// ===========

			/**
			 * Init
			 */
			function init() {
				if (scope.tipsArea == 'dashboardAddEntry') {
					//添加面板入口需要动态调整位置

					// 50为顶部导航高度, 6为添加按钮向下偏移位置
					scope.parentTop = angular.element('#js-dashboardAddDom').offset().top;
					var top = (angular.element('#js-dashboardAddDom').offset().top - 50 - 6) + 'px';

					angular.element(element).css('top', top);

					scope.$watch('parentTop', function (oldTop, newTop) {
						if (oldTop !== newTop) {
							console.log(newTop);
						}
					})
				}
			}

			//加载完成后,显示
			angular.element(element).ready(function () {
				element.removeClass('hide');
			});


			angular.element(element).mouseenter(function () {
				angular.element(element).find('.notification-inner').removeAttr('style')
			});


			/**
			 * Close
			 */
			function tipsClose() {
				angular.element(element).find('.notification-inner').css('display', 'none');
				//eval('scope.onCancel');
			}

			/**
			 * Ok
			 */
			function tipsOk() {
				console.log(scope.onOk());
				if (scope.onOk) {
					scope.onOk();
				}

				scope.myOptions.showTipsTmp = angular.copy(scope.tipsUserInfo);
				scope.myOptions.showTipsTmp[scope.tipsArea] = 1;

				var sendData = {
					showTips: angular.toJson(scope.myOptions.showTipsTmp)
				};

				//更新tips字段(添加面板入口的判断在panel-list.dire.js)
				dataMutualSrv.post(LINK_SETTINGS_INFO_UPDATE, sendData).then(function (data) {
					if (data.status == 'success') {
						//更新前端数据
						scope.tipsUserInfo[scope.tipsArea] = 1;

						if (scope.tipsArea == 'spaceSettingsEntry') {
							angular.element('#js-spaceBtnwarp').find('.dropdown').css('display', 'block');

							//空间管理按钮外层需要绑定悬浮事件,去除显示样式.
							angular.element('#js-spaceBtnwarp').mouseenter(function () {
								angular.element(this).find('.dropdown').removeAttr('style');
							})
						}
						else if (scope.tipsArea == 'dataSourceManageEntry') {
							$state.go('pt.dataSources');
						}
					} else {
						console.log('function tips update error!');
						if (data.status == 'error') {
							console.log(data.message)
						}
					}
				});
			}

		}
	}
}


/**
 * 搜索关键字标绿
 */
export function worldHighFunc() {
	return {
		restrict: 'EA',
		link: link
	};

	function link(scope, elem, attrs) {

		function dataInit(key) {
			var c = attrs.name;
			var out = attrs.name;

			if (key) {
				var reg = new RegExp($.regTrim(key), "gi");
				/*if (reg.test(c)) {
				 //正则替换
				 c = c.replace(reg, "♂" + scope.dataSettings.metricsSearchKey + "♀");
				 c = c.replace(/♂/g, "<span style='color:#27ae60'>").replace(/♀/g, "</span>");
				 }*/
				var i = 0;
				var arr;
				while ((arr = reg.exec(c)) != null) {
					var str = arr[0];
					out = out.substr(0, arr.index + i * 2) +
						"♂" + out.substr(arr.index + i * 2, str.length) + "♀" +
						out.substring(arr.index + i * 2 + str.length);
					i++;
				}
				out = out.replace(/♂/g, "<span class='text-green-400'>").replace(/♀/g, "</span>");
			}
			$(elem).html(out);
		}

		var key = attrs.searchkey;
		dataInit(key);

		scope.$on('triggerHighWord', function (e, d) {
			dataInit(d);
		})
	}
}


/**
 * widget底部获取错误信息，以及提示信息
 */
chartTipsTrigger.$inject = ['datasourceFactory', 'dataMutualSrv', '$timeout'];
export function chartTipsTrigger(datasourceFactory, dataMutualSrv, $timeout) {
    return {
        restrict: 'EA',
        link: function (scope, element, attrs) {
            //$(element).on('mouseenter touchstart', function () {
            //    var tips_wrap = $(this).find('.tips-wrap'),
            //        height = +tips_wrap.height();
            //    var newHeight = -(height + 10) + 'px';
            //    tips_wrap.css('top', newHeight);
            //});


            /**
             * 刷新widget数据
             */
            $(element).on('click', '.refresh-widget', function () {
                var id = $(this).attr('data-widget-id');
                var ds = datasourceFactory.getDatasource(id);
                var tableId = ds.widget.variables[0].profileId;
                var isUpdateSourceData = $(this).attr('data-update-source-data'); // 是否更新数据源的数据（如gd、s3文件更新）

                if (!ds.widget._ext) {
                    ds.widget._ext = {};
                }

                // 判断是否更新数据源的数据（如gd、s3文件更新）
                if(isUpdateSourceData){
                    if (!ds.widget._ext.updateSorceDataStatus || ds.widget._ext.updateSorceDataStatus != 'LOADING') {
                        ds.widget._ext.updateSorceDataStatus = 'LOADING';
                        dataMutualSrv.post(LINK_EXCEL_FILE_UPDATE_SOURCE_DATA + tableId).then(function (data) {
                            var updateStatus = 'FAILED';
                            if (data.status == 'success') {
                                updateStatus = "SUCCESS";
                                console.log('Post Data Success !');
                                console.log(data.content);
                            } else if (data.status == 'failed') {
                                console.log('Post Data Failed: ');
                                console.log(data.message);
                            } else if (data.status == 'error') {
                                console.log('Post Data Error: ');
                                console.log(data.message);
                            }

                            if (!ds.widget._ext) {
                                ds.widget._ext = {};
                            }
                            ds.widget._ext.updateSorceDataStatus = updateStatus;

                            if (updateStatus == 'SUCCESS') {
                                $timeout(function () {
                                    if (!ds.widget._ext) {
                                        ds.widget._ext = {};
                                    }
                                    ds.widget._ext.updateSorceDataStatus = '';
                                    ds.reload();
                                }, 5000);
                            }
                        });
                    }
                }else{
                    ds.reload();
                }
            });
        }
    }
};


/**
 * 数据源自定义更新频率设置
 */
dsAutoDateRate.$inject = ['$document','$translate'];
export function dsAutoDateRate($document, $translate) {
	return {
		restrict: 'EA',
		replace: true,
		scope: {
			timezone: "@",
			updateFrequency: "@",
			updateHour: "@",
			onCancel: "&",
			onSave: "&"
		},
		template: '<div class="fade in">'
				+'    <div class="pt-popup">'
				+'        <div class="pt-popup-content">'
				+'			  <header class="pt-popup-hd">'
				+'	            <span class="pt-popup-title" translate="DATA_SOURCE.MANAGEMENT.AUTO_UPDATE_RATE.TITLE"></span>'
				+'	            <p class="pt-introduce-text" translate="DATA_SOURCE.MANAGEMENT.AUTO_UPDATE_RATE.INTRODUCE"></p>'
				+'	            <a class="popup-btnClose" ng-click="onCancel()">'
				+'	                <svg><use xlink:href="#icon-close"></use></svg>'
				+'	            </a>'
				+'	        </header>'
				+'			<div class="pt-popup-bd">'
				+'              <ul>'
				+'              	<li><label class="check-box check-box-default"><input ng-model="updateFrequency" type="radio" name="rate" value="never"><i></i><span translate="DATA_SOURCE.MANAGEMENT.AUTO_UPDATE_RATE.OPTION_A"></span></label></li>'
				+'              	<li><label class="check-box check-box-default"><input ng-model="updateFrequency" type="radio" name="rate" value="hour"><i></i><span translate="DATA_SOURCE.MANAGEMENT.AUTO_UPDATE_RATE.OPTION_B"></span></label></li>'
				+'              	<li><label class="check-box check-box-default"><input ng-model="updateFrequency" type="radio" name="rate" value="day"><i></i><span translate="DATA_SOURCE.MANAGEMENT.AUTO_UPDATE_RATE.OPTION_C" translate-values="{updateHour: updateHour, timezone: timezone}"></span></label></li>'
				+'              	<li><label class="check-box check-box-default"><input ng-model="updateFrequency" type="radio" name="rate" value="monday"><i></i><span translate="DATA_SOURCE.MANAGEMENT.AUTO_UPDATE_RATE.OPTION_D" translate-values="{updateHour: updateHour, timezone: timezone}"></span></label></li>'
				+'				</ul>'
				+'          </div>'
				+'			<footer class="pt-popup-footer">'
				+'                <button class="pt-btn pt-btn-default m-r-md" ng-click="onCancel()">'
				+'                    <span translate="COMMON.CANCEL">Cancel</span>'
				+'                </button>'
				+'                <button class="pt-btn pt-btn-success" ng-click="onSave({updateFrequency: updateFrequency, updateHour: updateHour})">'
				+'                    <span translate="COMMON.SAVE">Save</span>'
				+'                </button>'
				+'            </footer>'
				+'        </div>'
				+'    </div>'
				+'    <div class="modal-backdrop fade in"></div>'
				+'</div>',
		link: link
	};

	function link(scope, element, attr) {
		var body = $document.find('body').eq(0);
		body.addClass('modal-open');
	}
}


/**
 * Input输入框
 */
uiInput.$inject = ['$timeout'];
export function uiInput($timeout) {
	return {
		restrict: 'A',
		replace: true,
		link: function (scope, element, attrs) {
			var options;

			// =========

			init()

			// =========


			//入口
			function init(){
				$timeout(function(){
					options = getOptions();
					eventBind(options);
				}, 0, false)
			}

			//获取参数设置
			function getOptions() {
                var linkOptions;

                if (attrs.uiInputOptions) {
                    linkOptions = scope.$eval(attrs.uiInputOptions);
                    if (angular.isObject(options) && angular.isObject(linkOptions)) {
                        linkOptions = angular.extend({}, options, linkOptions);
                    }
                } else if (options) {
                    linkOptions = options;
                }
                return linkOptions;
            }

            //事件绑定
            function eventBind(options){

            	$(element).ready(function(){

            		//最大长度
            		if(options['maxLength']){
            			var maxlength = options.maxLength;

            			$(element).attr('maxlength', maxlength).on('input', function(){
        					var v = $(element).val();
            				if(v.length > maxlength){
            					$(element).val(v.splice(0, (maxlength-1)));
            				}
            			})
            		}

            		//自动获取焦点
	            	if(options['autoFocus']){
	            		$(element).focus();
	            	}

	            	//自动选中内容
	            	if(options['autoSelect']){
	            		$(element).select();
	            	}

	            	//回车事件绑定
	            	if(options['onEnter']){
	            		$(element).on('keydown', function(e) {
						    var keycode = window.event ? e.keyCode : e.which;
						    if (keycode == 13) {
						        scope.$eval(options.onEnter);
						    }
						})
	            	}

	            	//获取焦点事件
	            	if(options['onFocused']){
	            		$(element).on('focus', function(e) {
						    scope.$eval(options.onFocused);
						})
	            	}

	            	//失去焦点事件
	            	if(options['onBlur']){
	            		$(element).on('blur', function(e) {
						    scope.$eval(options.onBlur);
						})
	            	}
            	})
            }
		}
	}
}
