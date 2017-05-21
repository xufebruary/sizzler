'use strict';

/**
 * 面板编辑
 *
 */

import tpl from './panel-edit.html';

panelEditDirective.$inject = ['$rootScope', '$document', 'uiLoadingSrv', 'PanelServices','Track'];

function panelEditDirective($rootScope, $document, uiLoadingSrv, PanelServices,Track) {
    return {
        restrict: 'EA',
        scope: {
            spaceId: '<',           //空间ID
            currentPanelId: '<',    //当前复制对象面板
            currentPanelTitle: '<', //当前复制对象面板
            currentPanelDescription: '<', //当前复制对象面板
            currentPanelShareId: '<', //是否来自分享
            onCancel: '&',          //取消回调
            onSuccess: '&',         //发送成功回调
            onFailure: '&'          //发送失败回调
        },
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {
        var body = $document.find('body').eq(0);
        var currentPanel;
        var titleLocal = angular.copy(scope.currentPanelTitle);
        var panelInfo = {
            type: 'panel',
            spaceId: scope.spaceId,
            panelId: scope.currentPanelId,
            panelTitle: null,
            description: null
        };

        //指令内部参数调用
        scope.myOptions = {
            titleMode: scope.currentPanelTitle,
            descriptionMode: scope.currentPanelDescription,
            sendStatus: false
        }

        // ==========

        //编辑保存
        scope.editPanel = editPanel;

        //失去焦点事件
        scope.eventBlur = eventBlur;


        init();

        // ==========

        //入口
        function init(){
            //弹出框隐藏滚动条
            body.addClass('modal-open');
        }

        //编辑保存
        function editPanel(){
            if(titleCheck()) return;

            panelInfo.panelTitle = scope.myOptions.titleMode;
            panelInfo.description = scope.myOptions.descriptionMode;

            PanelServices.showPopupLoading();
            PanelServices.updatePanel(panelInfo)
            .then((data) => {
                scope.onSuccess({data: panelInfo, layout: data.panelLayout});
            },() => {
                scope.myOptions.sendStatus = true;
                scope.onFailure();
            })
            .finally(() => {
                PanelServices.hidePopupLoading();
            });

			//全站事件统计
			Track.log({where: 'panel', what: 'edit_dashboard_save'});
        }

        //失去焦点事件
        function eventBlur() {
            if (titleCheck()) {
                scope.$apply(function(){
                    scope.myOptions.titleMode = titleLocal;
                })
            }
        }

        //标题校验
        function titleCheck(){
            return !scope.myOptions.titleMode || scope.myOptions.titleMode == '';
        }

        //获取面板名称回调
        function getNameCallBack(title){
            titleLocal = title;
            scope.myOptions.titleMode = title;
        }
    }
}

export default panelEditDirective;
