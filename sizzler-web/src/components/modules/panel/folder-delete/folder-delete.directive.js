'use strict';

/**
 * 文件夹删除
 *
 */

import tpl from './folder-delete.html';

folderDeleteDirective.$inject = ['$document', 'uiLoadingSrv', 'PanelServices', 'siteEventAnalyticsSrv','Track'];

function folderDeleteDirective($document, uiLoadingSrv, PanelServices, siteEventAnalyticsSrv,Track) {
    return {
        restrict: 'EA',
        scope: {
            spaceId: '<',           //空间ID
            deleteFolderLayout: '<',//当前删除文件夹的位置信息
            onCancel: '&',          //取消回调
            onSuccess: '&',         //发送成功回调
            onFailure: '&'          //发送失败回调
        },
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {
        var deletePanelIdList = [];
        var currentFolderLayout = angular.fromJson(scope.deleteFolderLayout);
        angular.element('body').addClass('modal-open');

        //循环查找文件夹下的面板ID
        if(currentFolderLayout.columns) forList(currentFolderLayout.columns[0]);

        //删除保存
        scope.deleteFolder = function(){

            var sendData = {
                type: "container",
                pids: deletePanelIdList,
                spaceId: scope.spaceId,
                panelId: currentFolderLayout.containerId
            };

            PanelServices.showPopupLoading();
            PanelServices.deletePanel(sendData)
            .then((data) => {
                scope.onSuccess({layout: data.panelLayout, data: deletePanelIdList});

                //GTM
                siteEventAnalyticsSrv.setGtmEvent('click_element', 'dashboard', 'del_folder');

				Track.log({where: 'panel', what: 'folder_delete_confirm'});
            },() => {
                scope.onFailure();
            })
            .finally(() => {
                PanelServices.hidePopupLoading();
            })
        }

        //循环查找dashboard id
        function forList (list) {
            for (var i = 0; i < list.length; i++) {
                if (list[i].type == 'container') {
                    if(list[i].columns) forList(list[i].columns[0]);
                } else {
                    deletePanelIdList.push(list[i].panelId);
                }
            }
        }
    }
}

export default folderDeleteDirective;
