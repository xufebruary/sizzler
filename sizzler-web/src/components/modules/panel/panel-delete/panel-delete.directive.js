'use strict';

/**
 * 面板删除
 *
 */

import tpl from './panel-delete.html';

panelDeleteDirective.$inject = ['$document', 'uiLoadingSrv', 'siteEventAnalyticsSrv', 'PanelServices','Track'];

function panelDeleteDirective($document, uiLoadingSrv, siteEventAnalyticsSrv, PanelServices,Track) {
    return {
        restrict: 'EA',
        scope: {
            spaceId: '<',           //空间ID
            deletePanelId: '<',     //当前删除面板的ID
            deletePanelTitle: '<',  //当前删除面板的标题
            onCancel: '&',          //取消回调
            onSuccess: '&',         //发送成功回调
            onFailure: '&'          //发送失败回调
        },
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {
        var body = $document.find('body').eq(0);
        body.addClass('modal-open');

        var panelInfo = {
            type: 'panel',
            spaceId: scope.paceId,
            panelId: scope.deletePanelId
        }

        //删除保存
        scope.deletePanel = function(){

            PanelServices.showPopupLoading();
            PanelServices.deletePanel(panelInfo)
            .then((data) => {
                scope.onSuccess({layout: data.panelLayout, data: {panelId: scope.deletePanelId}});

                //GTM
                siteEventAnalyticsSrv.setGtmEvent('click_element', 'dashboard', 'del_save');

				Track.log({where: 'panel', what: 'delete_dashboard_confirm'});
            },() => {
                scope.onFailure();
            })
            .finally(() => {
                PanelServices.hidePopupLoading();
            });
        }
    }
}

export default panelDeleteDirective;
