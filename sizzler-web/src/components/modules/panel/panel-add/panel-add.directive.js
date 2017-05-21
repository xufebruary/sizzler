'use strict';

/**
 * 面板增加
 *
 */

import tpl from './panel-add.html';
import {
  uuid
} from 'components/modules/common/common';

panelAddDirective.$inject = ['$rootScope', '$document', '$translate', 'uiLoadingSrv', 'siteEventAnalyticsSrv', 'PanelServices'];

function panelAddDirective($rootScope, $document, $translate, uiLoadingSrv, siteEventAnalyticsSrv, PanelServices) {
    return {
        restrict: 'EA',
        scope: {
            spaceId: '<',   //空间ID
            currentPanelLayout: '<',//面板位置信息
            onCancel: '&',  //取消回调
            onSuccess: '&', //发送成功回调
            onFailure: '&'  //发送失败回调
        },
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {
        var titleLocal = $translate.instant('PANEL.ADD.PANEL_DEFAULT_NAME');
        var body = $document.find('body').eq(0);
        var panelInfo = {
            "type": "panel",
            "spaceId": scope.spaceId,
            "panelTitle": null,
            "description": null
        };

        //指令内部参数调用
        scope.myOptions = {
            titleMode: null,
            descriptionMode: null
        }

        //弹出框隐藏滚动条
        body.addClass('modal-open');

        //获取面板名称
        titleLocal = PanelServices.getName('add', titleLocal, scope.currentPanelLayout);
        scope.myOptions.titleMode = titleLocal;
        
        //新增保存
        scope.addPanel = function(){
            if(titleCheck()) return;

            panelInfo.panelTitle = scope.myOptions.titleMode;
            panelInfo.description = scope.myOptions.descriptionMode;

            PanelServices.showPopupLoading();
            PanelServices.addPanel(panelInfo)
            .then((data) => {
                /**
                 *  data: {
                 *      panel: {}
                 *      panelLayout: {}
                 *      panelList: null
                 *  }
                 */
                scope.onSuccess({data: data.panel, layout: data.panelLayout});
            },() => {
                scope.onFailure();
            })
            .finally(() => {
                PanelServices.panelListScroll();
                PanelServices.hidePopupLoading();

                //全站事件统计
                siteEventAnalyticsSrv.createData({
                    "uid": $rootScope.userInfo.ptId,
                    "where":"panel",
                    "what":"add_panel_save",
                    "how":"click"
                });
            });
        }

        //失去焦点事件
        scope.eventBlur = function() {
            if (titleCheck()) {
                scope.$apply(function(){
                    scope.myOptions.titleMode = titleLocal;
                }) 
            }
        };

        //标题校验
        function titleCheck(){
            return !scope.myOptions.titleMode || scope.myOptions.titleMode == '';
        }
    }
}

export default panelAddDirective;