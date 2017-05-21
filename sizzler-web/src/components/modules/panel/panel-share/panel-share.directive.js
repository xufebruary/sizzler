'use strict';

/**
 * 面板分享
 *
 */

import tpl from './panel-share.html';
import panelShareController from './panel-share.controller';
import './panel-share.scss';

function panelShareDirective() {
    return {
        restrict: 'EA',
		template: tpl,
        scope: {
            currentPanel: '<',  //当前面板信息
            onCancel: '&',      //取消回调
            onSuccess: '&',     //发送成功回调
            onFailure: '&'      //发送失败回调
        },
        controller: panelShareController,
		controllerAs: '$ctrl',
		bindToController: true
    };

}

export default panelShareDirective;
