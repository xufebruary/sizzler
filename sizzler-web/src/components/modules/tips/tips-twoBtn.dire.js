'use strict';

import tipTpl from 'components/modules/tips/tips-twoBtn.tpl.html';

/**
 * tipsTwoBtn
 *
 */
angular
    .module('pt')
    .directive('tipsTwoBtn', tipsTwoBtn);

function tipsTwoBtn() {
    return {
        restrict: 'EA',
        replace: true,
        template: tipTpl,
        link: link
    }

    function link(scope, element, attr) {
        var linkOptions = scope.$eval(attr.tipsOptions);
        var tips = scope.tips = {
            type: linkOptions.type || 'query',
            title: linkOptions.title,
            info: linkOptions.info,
            hdHide: linkOptions.hdHide || false,
            btnLeftText: linkOptions.btnLeftText,
            btnRightText: linkOptions.btnRightText,
            btnLeftClass: linkOptions.btnLeftClass,
            btnRightClass: linkOptions.btnRightClass,
            btnLeftHide: linkOptions.btnLeftHide || false,
            btnRightHide: linkOptions.btnRightHide || false,

            btnLeftEvent: 'scope.'+linkOptions.btnLeftEvent,
            btnRightEvent: 'scope.'+linkOptions.btnRightEvent,
            closeEvent: 'scope.'+linkOptions.closeEvent
        };


        scope.closeEvent = function(){
            eval(tips.closeEvent);
        };

        scope.btnLeftEvent = function(){
            eval(tips.btnLeftEvent);
        };

        scope.btnRightEvent = function(){
            eval(tips.btnRightEvent);
        };
    }
}





/**
 * addQuotes
 *
 */
angular
    .module('pt')
    .filter('addQuotes', addQuotes);

function addQuotes() {
    return function(value) {
        return "\""+value+'\"';
    };
}
