'use strict';

/**
 * 面板列表
 *
 */

import {
  uuid
} from '../../common/common';


import tpl from './panel-list.html';


panelListDirective.$inject = ['$rootScope', '$localStorage', '$document', '$translate', 'uiLoadingSrv',  'siteEventAnalyticsSrv', 'PanelServices','Track'];

function panelListDirective($rootScope, $localStorage, $document, $translate, uiLoadingSrv, siteEventAnalyticsSrv, PanelServices,Track) {
    return {
        restrict: 'EA',
        scope: {
            spaceId: '<',               //空间ID
            settingsInfo: '=',          //用户设置信息
            currentPanelList: '=',      //当前面板列表
            currentPanelLayout: '=',     //当前面板位置信息
            currentPanelSelect: '=',     //当前面板位置信息
            showPanelOperation: '&',    //操作回调
            panelOperationCallBack: '&', //操作回调
            panelLayoutUpdateCallBack: '&', //操作回调
            panelOperationFailureCallBack: '&', //操作回调
            dashboardSelect: '&'       //操作回调
        },
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {

        //指令内部参数调用
        scope.myOptions = {

            //dashboard 选项下来
            optionDropdown: {
                show: false,
                left: 0,
                top: 0,
                item: null,
                idLisst: [],
                subNav: {edit: true, copy: false, delete: true}
            },

            //提示框
            tips: {
                show: false,
                options: {}
            },

            //文件夹标题修改
            folderTitle: {
                item: null,
                old: null
            }
        };


        // ==========

        //添加文件夹
        scope.addFolder = addFolder;

        //添加面板
        scope.addPanel = addPanel;

        //切换显示下拉选项
        scope.toggleDashboardOption = toggleDashboardOption;

        //点击操作(面板/文件夹)
        scope.panelSelect = panelSelect;

        //拖拽结束事件(面板/文件夹)
        scope.dragEnd = dragEnd;

        //判断是否为分享页面
        scope.isShare = isShare;

        //标题编辑完成
        scope.doneEditing = doneEditing;

        //面板操作调用
        scope.panelOperation = panelOperation;

        //功能介绍-OK
        scope.tipsOk = tipsOk;


        init();

        // ==========

        //入口
        function init(){
            //功能提示按钮位置信息计算
            if(scope.settingsInfo.showTips && scope.settingsInfo.showTips.dashboardAddEntry == 0){
                $('#js-dashboardAddNotification').mouseenter(function(){
                    var headHeight = 70;
                    var top = $('#js-dashboardAddDom').position().top + headHeight;

                    var tipsDom = $('#js-dashboard-add-tips');
                    var tipsHeight = 120;
                    var tipsMarginBottom = 50;
                    var bodyHeight = document.body.offsetHeight;

                    tipsDom.css({
                        'top': top+'px',
                        'display': 'block'
                    });

                    if((top + tipsHeight + tipsMarginBottom) >= bodyHeight){
                        tipsDom.addClass('top');
                    } else {
                        tipsDom.removeClass('top');
                    }
                });

                $('#js-dashboard-add-tips').mouseleave(function(){
                    $(this).removeAttr('style');
                });
            }
        }

        //添加文件夹
        function addFolder() {
            var newFolder = {
                type: "container",
                spaceId: scope.spaceId,
                panelTitle: $translate.instant('PANEL.ADD.NEW_FOLDER_NAME')
            };
            PanelServices.addPanel(newFolder)
            .then((data) => {
                /**
                 *  data: {
                 *      panel: {}
                 *      panelLayout: {}
                 *      panelList: null
                 *  }
                 */
                var folderInfo = data.panel;
                scope.panelOperationCallBack({type: 'addFolder', layout: data.panelLayout, data: folderInfo});

                //编辑标题
                scope.myOptions.folderTitle.item = folderInfo;
                scope.myOptions.folderTitle.old = folderInfo.panelTitle;

                //隐藏添加按钮
                $('.dashboard-add-dom').find('.dashboard-add-dropdown').hide().end().mouseenter(function(){
                    $(this).find('.dashboard-add-dropdown').removeAttr('style');
                });

                //GTM
                siteEventAnalyticsSrv.setGtmEvent('click_element', 'dashboard', 'add_folder');

                //全站事件统计
                siteEventAnalyticsSrv.createData({
                    "uid": $rootScope.userInfo.ptId,
                    "where":"panel",
                    "what":"add_folder",
                    "how":"click"
                });
            }, () => {
                scope.panelOperationFailureCallBack({type: 'addFolder'});
            })
        }

        //拖拽结束事件
        var sendMsgTimer;
        function dragEnd() {
            clearTimeout(sendMsgTimer);
            sendMsgTimer = window.setTimeout(function() {
                layoutUpdate(angular.copy(scope.currentPanelLayout));
            }, 500);
        }

        //面板位置更新
        function layoutUpdate(layout){
            layout.panelLayout = angular.toJson(layout.panelLayout);
            layout.updateTime = parseInt(new Date().getTime());

            PanelServices.updatePanelLayout(layout)
            .then((data)=>{
                if(data.status && data.status == "failed"){
                    scope.panelOperationFailureCallBack({type: 'dragEnd', data: data});
                }
                else {
                    scope.panelOperationCallBack({type: 'dragEnd', layout: data.panelLayout, data: data.panelLayout.dataVersion});
                }
            },()=>{
                scope.panelOperationFailureCallBack({type: 'dragEnd'});
            })
        }

        //添加面板
        function addPanel(){
            scope.showPanelOperation({type: 'add'});

            //全站事件统计
            siteEventAnalyticsSrv.createData({
                "uid": $rootScope.userInfo.ptId,
                "where":"panel",
                "what":"add_panel",
                "how":"click"
            });
        }

        //切换显示下拉选项
        function toggleDashboardOption (e, item) {
            if (scope.myOptions.optionDropdown.item === item && scope.myOptions.optionDropdown.show) {
                $document.unbind('click', documentClickBindData);
                scope.myOptions.optionDropdown.show = false;
            }
            else {
                var top = $(e.target).offset().top;
                var marginTop = parseInt($('.pt-aside').css('top')) - 18;
                if (e.target.nodeName == 'svg' || e.target.nodeName == 'use') {
                    top = $(e.target).parents('.dashboard-menu-option').offset().top;
                }

                scope.myOptions.optionDropdown.show = true;
                scope.myOptions.optionDropdown.item = item;
                scope.myOptions.optionDropdown.idList = [];
                scope.myOptions.optionDropdown.top = parseInt(top - parseInt($(window).scrollTop()) - marginTop);

                if (item.type == 'panel') {
                    var panel = PanelServices.getMyPanel(scope.currentPanelList, item.panelId);
                    if (panel && panel.shareSourceId) {
                        scope.myOptions.optionDropdown.subNav.copy = false;
                    } else {
                        scope.myOptions.optionDropdown.subNav.copy = true;
                    }
                }
                else {
                    scope.myOptions.optionDropdown.subNav.copy = false;
                }

                $document.bind('click', documentClickBindData);
            }
        }

        //隐藏下来选项
        function hideDashboardOption() {
            $document.unbind('click', documentClickBindData);
            scope.myOptions.optionDropdown.show = false;
        }

        //点击操作
        function panelSelect (item) {
            if (item.type == 'container') {
                //文件夹

                if (angular.isUndefined(item.fold)) {
                    item['fold'] = true;
                } else {
                    item.fold = !item.fold;
                }
            }
            else if(item.type == 'panel'){
                //dashboard

                var currentSltPanel = $('.dashboard-menu-single.active')
                if(!currentSltPanel || (currentSltPanel && currentSltPanel.attr('attr-dashboard-id')) !== item.panelId){
					var panel = PanelServices.getMyPanel(scope.currentPanelList, item.panelId);

                    if(panel && panel.panelTitle != item.panelTitle){
                        //如果列表中标题和位置信息中的标题不一致，则按列表中标题更新位置信息

                        var panelInfo = {
                            type: 'panel',
                            spaceId: scope.spaceId,
                            panelId: item.panelId,
                            panelTitle: panel.panelTitle,
                            description: panel.description
                        };

                        PanelServices.updatePanel(panelInfo)
                        .then((data) => {
                            scope.panelOperationCallBack({type:'edit', layout:data.panelLayout, data:panelInfo});
                            scope.dashboardSelect({panelId:item.panelId, type:'select', shareSourceId:panel.shareSourceId});
                        })
                    }
                    else {
                        scope.dashboardSelect({panelId:item.panelId, type:'select'});
                    }
                }
            }

            if (scope.myOptions.optionDropdown.show) {
                $document.unbind('click', documentClickBindData);
                scope.myOptions.optionDropdown.show = false;
            }
        }

        //文档绑定事件
        function documentClickBindData (event) {
            var elem = $('.dashboard-option-pop');
            if (scope.myOptions.optionDropdown.show
                && !$(event.target).hasClass('dashboard-menu-option')
                && !event.target.hasAttribute('data-jstarget')
                && !elem[0].contains(event.target)) {
                scope.$apply(function () {
                    scope.myOptions.optionDropdown.show = false;
                });
                $document.unbind('click', documentClickBindData);
            }
        }

        //提示-显示
        function showTips(type) {
            var options = {
                type: 'query',
                title: null,
                info: null,
                btnLeftText: $translate.instant('COMMON.DELETE'),
                btnRightText: $translate.instant('COMMON.CANCEL'),
                btnLeftClass: 'pt-btn-danger',
                btnRightClass: 'pt-btn-default',
                btnLeftEvent: 'closeTips()',
                btnRightEvent: 'closeTips()',
                closeEvent: 'closeTips()',
                btnLeftHide: 'false',
                btnRightHide: 'false',
                hdHide: 'false'
            };

            switch (type) {
                //发送数据失败提示(纯文字提示)
                case "sendError":
                    options.type = 'tips';
                    options.info = $translate.instant('TIP.ERROR.SEND_DATA_ERROR');
                    break;
            }

            scope.myOptions.tips.show = true;
            scope.myOptions.tips.options = options;
            $('body').addClass('modal-open');
        }

        //提示-关闭
        function closeTips() {
            scope.myOptions.tips.show = false;
            $('body').removeClass('modal-open');
			//全站事件统计
			var what;
			if(scope.myOptions.operation.edit){
				what = 'edit_dashboard_cancel';
			}else if(scope.myOptions.operation.copy){
				what = 'copy_dashboard_cancel';
			}else if(scope.myOptions.operation.delete){
				what = 'delete_dashboard_cancel';
			}
			Track.log({where: 'panel', what: what});
        }

        //判断是否为分享页面
        function isShare(dashboardId) {
            var panel = PanelServices.getMyPanel(scope.currentPanelList, dashboardId);
            return panel && panel.shareSourceId;
        }

        //标题编辑
        function editTitle(item) {
            item.editing = true;
            scope.myOptions.folderTitle.item = item;
            scope.myOptions.folderTitle.old = item.containerName;
        }

        //标题编辑完成
        function doneEditing(item) {
            delete item.editing;

            if (item.containerName == '') {
                item.containerName = scope.myOptions.folderTitle.old;
            }

            if (scope.myOptions.folderTitle.old != item.containerName) {

                var folder = {
                    type: 'container',
                    spaceId: scope.spaceId,
                    panelId: item.containerId,
                    panelTitle: item.containerName
                };

                PanelServices.updatePanel(folder)
                .then((data) => {
                    scope.panelOperationCallBack({type: 'updateFolder', layout: data.panelLayout, data: folder});
                })
            }
        }

        //layout信息中查找
        function getMyFolder(layout, containerId){
            for (var i = layout.length - 1; i >= 0; i--) {
                if(layout[i].type == 'container'){

                    if(layout[i].containerId == containerId){
                        return layout[i]
                    }
                    else {
                        if(layout[i].columns && layout[i].columns[0].length >0){
                            getMyFolder(layout[i].columns[0], containerId)
                        }
                    }
                }
            }
        }

        //功能介绍-OK
        function tipsOk (){
            var userSetting = angular.copy(scope.settingsInfo.showTips);
            userSetting.dashboardAddEntry = 1;

            //更新tips字段
            PanelServices.updateUsersSettingsInfo(userSetting)
            .then((data) => {
                //更新前端数据
                scope.settingsInfo.showTips.dashboardAddEntry = 1;

                $('.dashboard-add-dom').find('.dashboard-add-dropdown').css('display', 'block').mouseenter(function(){
                    $(this).removeAttr('style');
                });
            })
            .finally(() => {
                tipsClose();
            })
        };

        //功能介绍-Close
        function tipsClose (){
            $('#js-dashboard-add-tips').removeAttr('style');
        };

        //面板操作调用
        function panelOperation (type){
            var item = scope.myOptions.optionDropdown.item;
            var itemType = scope.myOptions.optionDropdown.item.type;

            if(item.type == 'panel'){
                //面板

                //请求panel信息
                PanelServices.getPanelInfo(item.panelId)
                .then((data) => {
                    scope.showPanelOperation({type: type, panelInfo: data});
                })
            }
            else {
                //文件夹

                switch(type){
                    case"edit":
                        editTitle(item);
						type = 'edit_folder_rename';
                        break;

                    case"delete":
                        scope.showPanelOperation({type: 'deleteFolder', panelInfo: item});
						type = 'edit_folder_delete';
                        break;
                }
            }

			//全站事件统计
			Track.log({where: 'panel', what: type + '_dashboard', value: scope.myOptions.optionDropdown.item.panelId});

            //隐藏下拉
            hideDashboardOption();
        }

        //滚动时将弹出框隐藏
        $(window).scroll(function () {
            $document.unbind('click', documentClickBindData);
            scope.myOptions.optionDropdown.show = false;
        });
    }
}

export default panelListDirective;
