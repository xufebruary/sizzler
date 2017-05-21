'use strict';

/**
 * 面板头部
 *
 */

import tpl from './panel-header.html';
import './panel-header.scss';

panelHeaderDirective.$inject = ['$rootScope', '$document', 'uiLoadingSrv', 'PanelServices', 'Track'];

function panelHeaderDirective($rootScope, $document, uiLoadingSrv, PanelServices,Track) {
    return {
        restrict: 'EA',
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {
        var body = $document.find('body').eq(0);

        // ==========


        // ==========

    }
}

export default panelHeaderDirective;
