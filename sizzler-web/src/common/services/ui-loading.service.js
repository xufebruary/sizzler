'use strict';

/**
 * uiLoadingSrv
 * loading
 *
 */
//angular
//    .module('pt')
//    .service('uiLoadingSrv', uiLoadingSrv)
//    .service('toggleLoadingSrv', toggleLoadingSrv);

/**
 * 动态创建loadiing
 */
export function uiLoadingSrv() {

	//createLoading
	this.createLoading = function(dom){
		var loading = angular.element('<div>').attr('class', 'pt-loading').html('<span class="pt-center pt-loading-box-sm">'
	                        +'<svg class="load-s btnloads">'
	                            +'<use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-loading"></use>'
	                        +'</svg>'
	                        +'<svg class="load-m btnloadm">'
	                            +'<use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-loading"></use>'
	                        +'</svg>'
	                    +'</span>');

		console.log(dom);

		//全局loading
		if(dom == '.pt-main'){
        	angular.element('body').addClass('modal-open');
			loading.addClass('pt-main-loading');
		}

		loading.appendTo(angular.element(dom));
	};

	//removeLoading
	this.removeLoading = function(dom){
		angular.element(dom).find('.pt-loading').remove();

		//全局loading
		if(dom == '.pt-main'){
			angular.element('body').removeClass('modal-open');
		}
	}
}


/**
 * 切换loading
 */
export function toggleLoadingSrv(){
	var $this = this;

	/**
	 * 显示loading
	 */
	this.show = function(area){
		angular.element('[data-loading-area=\"'+area+'\"]').removeClass('hide').fadeIn();
	};

	/**
	 * 隐藏loading
	 */
	this.hide = function(area){
		angular.element('[data-loading-area=\"'+area+'\"]').fadeOut(function(){

            // if(area == 'body'){
            // 	$this.show('aside');
            // 	$this.show('widgetList');
            // };
        });
	}
}
