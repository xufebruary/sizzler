'use strict';

/**
 * 禁止滚动条滚动到顶部或者底部时引起父级的滚动
 *
 */


function stopScrollToFatherDirective() {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var eventType = 'mousewheel';
            if (document.mozHidden !== undefined) {//firefox 浏览器不认识mousewheel
                eventType = 'DOMMouseScroll';
            }
            $(element).on(eventType, function (e) {
                var scrollTop = this.scrollTop,
                    scrollHeight = this.scrollHeight,
                    height = this.clientHeight;

                var delta = (e.originalEvent.wheelDelta) ? e.originalEvent.wheelDelta : -(e.originalEvent.detail || 0);//Firefox浏览器不认识wheelDelta，只认识detail

                if ((delta > 0 && scrollTop <= delta) || (delta < 0 && scrollHeight - height - scrollTop <= -1 * delta)) {
                    this.scrollTop = delta > 0 ? 0 : scrollHeight;
                    e.preventDefault();
                }
            })
        }
    };
}

export default stopScrollToFatherDirective;
