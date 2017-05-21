'use strict';
import {
	getRect
	}from 'components/modules/common/common';

/**
 * movable
 * 可拖拽
 */




function movableDirective() {
    return {
        restrict: 'EA',
        link: link
    };

    function link(scope, element, attrs) {
        scope.modal.editorElemnet = element;

        var place = scope.modal.editorPlace; //获取当前widget位置信息（dashboard/dashboard.js）
        var disX = 0;
        var disY = 0;
        //var min_y = angular.element('.content-fixed').length > 0 ? 60 : 110;
        var min_y = 60;
        var max_x = document.documentElement.clientWidth - 20;
        var max_y = document.documentElement.clientHeight - 20;
        var default_x = 0;
        var default_y = place.y > max_y ? max_y : place.y;
		default_y = default_y < min_y ? min_y : default_y;

        //根据当前位置计算编辑器显示位置
        if( place.x + place.w + parseInt(element[0].offsetWidth) + place.ew > max_x ){
            place.s = 'editor-l';
            default_x = document.documentElement.clientWidth - parseInt(element[0].offsetWidth) - 10;

            if(place.x > (parseInt(element[0].offsetWidth)+place.ew+10)){
                default_x = place.x - parseInt(element[0].offsetWidth) - 10;
            }
        } else {
            default_x = place.x + place.w + 10;
        }
        element.attr('class', 'editor '+place.s).css({
            'left': default_x+'px',
            'top': default_y+'px'
        });

        // 鼠标按下时
        angular.element(attrs.moveBy).on('mousedown', function(e) {
            var min_x = !scope.pt.settings.asideFolded ? 250 : -(parseInt(element[0].offsetWidth)-30);
            var evnt = e || event;                      // 得到鼠标事件
            disX = evnt.clientX - getRect(element[0]).left;  // 鼠标横坐标 - dom的left
            disY = evnt.clientY - getRect(element[0]).top;   // 鼠标纵坐标 - dom的top

            //min_y = angular.element('.content-fixed').length > 0 ? 60 : 110;
            min_y = 60;
            // max_x = document.documentElement.clientWidth - 20;
            // max_y = document.documentElement.clientHeight - 20;

            // 鼠标移动时
            document.onmousemove = function(e) {
                var evnt = e || event;
                var x = evnt.clientX - disX;
                var y = evnt.clientY - disY;

                x = ( x < min_x ) ? min_x : x;  // 当offset到窗口最左边时
                x = ( x > max_x ) ? max_x : x;  // 当offset到窗口最右边时
                y = ( y < min_y ) ? min_y : y;  // 当offset到窗口最上边时
                y = ( y > max_y ) ? max_y : y;  // 当offset到窗口最下边时

                element.css({
                    'left': x+'px',
                    'top': y+'px'
                })
            };

            // 鼠标抬起时
            document.onmouseup = function() {
                document.onmousemove =null;
                document.onmouup = null;
            };

            return false;
        });
    }
}

export default movableDirective;
