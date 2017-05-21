'use strict';



/**
 * uiLoading
 *
 */


function uiLoadingDirective() {
    return {
        restrict: 'EA',
        replace: true,
        template: '<div class="pt-loading">'
        +'<span class="pt-center pt-loading-box-sm">'
        +'<svg class="load-s btnloads">'
        +'<use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-loading"></use>'
        +'</svg>'
        +'<svg class="load-m btnloadm">'
        +'<use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-loading"></use>'
        +'</svg>'
        +'</span>'
        +'</div>',
        link: link
    };

    function link(scope, element, attr) {
        // if(attr.type=='body'){
        //    element.addClass="pt-loadding-bd"
        // }
    }
}

export default uiLoadingDirective;
