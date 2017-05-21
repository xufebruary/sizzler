'use strict';

/**
 * 面板复制
 *
 */

import tpl from './panel-copy.html';
import {
  uuid
} from 'components/modules/common/common';

panelAddDirective.$inject = ['$rootScope', '$document', '$translate', 'uiLoadingSrv', 'PanelServices','Track'];

function panelAddDirective($rootScope, $document, $translate, uiLoadingSrv, PanelServices,Track) {
    return {
        restrict: 'EA',
        scope: {
            spaceId: '<',           //空间ID
            currentPanelId: '<',    //当前复制对象面板
            currentPanelLayout: '<',//面板位置信息
            currentPanelList: '<',  //面板列表
            onCancel: '&',          //取消回调
            onSuccess: '&',         //发送成功回调
            onFailure: '&'          //发送失败回调
        },
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {
        var body = $document.find('body').eq(0);
        var titleLocal, panelInfo;

        //指令内部参数调用
        scope.myOptions = {
            titleMode: null
        }

        // ==========

        //复制保存
        scope.copyPanel = copyPanel;

        //失去焦点事件
        scope.eventBlur = eventBlur;


        init();

        // ==========

        //入口
        function init(){
            //弹出框隐藏滚动条
            body.addClass('modal-open');

            getMyPanel(angular.fromJson(scope.currentPanelList), scope.currentPanelId, function(panel){
                titleLocal = panel.panelTitle;
                panelInfo = {
                    "type": "panel",
                    "spaceId": scope.spaceId,
                    "panelTitle": null,
                    "description": panel.description
                };

                //获取面板名称
                titleLocal = PanelServices.getName('copy', titleLocal, scope.currentPanelLayout);
                scope.myOptions.titleMode = titleLocal;
            });
        }

        //复制保存
        function copyPanel(){
            if(titleCheck()) return;

            panelInfo.panelTitle = scope.myOptions.titleMode;
            PanelServices.showPopupLoading();
            PanelServices.copyPanel(scope.currentPanelId, panelInfo)
            .then((data) => {
                scope.onSuccess({data: data.panel, layout: data.panelLayout});
            },() => {
                scope.onFailure();
            })
            .finally(() => {
                PanelServices.panelListScroll();
                PanelServices.hidePopupLoading();
            });
			//全站事件统计
			Track.log({where: 'panel', what: 'copy_dashboard_save'});
        }

        //失去焦点事件
        function eventBlur() {
            if (titleCheck()) {
                scope.$apply(function(){
                    scope.myOptions.titleMode = titleLocal;
                })
            }
        }

        //依据ID查找具体面板信息
        function getMyPanel(list, id, callBack){
            var panel = null;
            for(var i=0; i<list.length; i++){
                if(list[i].panelId == id){
                    panel = list[i];
                    break;
                }
            }

            if(panel){
                callBack(panel)
            }
            else {
                PanelServices.getPanelInfo(id)
                .then((data) => {
                    callBack(data)
                })
            }
        }

        //标题校验
        function titleCheck(){
            return !scope.myOptions.titleMode || scope.myOptions.titleMode == '';
        }
    }
}

export default panelAddDirective;
