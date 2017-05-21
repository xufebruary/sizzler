'use strict';

angular
    .module('pt')
    .controller('PanelTemplateController', ['$scope', '$rootScope', '$document', '$state', 'uiLoadingSrv', 'siteEventAnalyticsSrv', 'PanelResources', 'PanelTempletsResources', PanelTemplateController]);

function PanelTemplateController($scope, $rootScope, $document, $state, uiLoadingSrv, siteEventAnalyticsSrv, PanelResources, PanelTempletsResources) {
    var $body = $('body'),
        vm = this;
    vm.myOptions = {
        bigImg: {
            show: false,
            src: null,
            title: null,
            width: null
        },

        tagsFilter: {
            list: [], //当前选择的tag列表
            tmpList: null //当前选择的tag下隐藏的template
        }
    };

    // ===========

    //选择模板新增panel
    vm.addTemplet = addTemplet;

    //图片放大-显示
    vm.showBigImg = showBigImg;

    //图片放大-关闭
    vm.closeBigImg = closeBigImg;

    //Select Tag
    vm.sltTag = sltTag;

    //Template tag filter(判断是否隐藏 true-隐藏)
    vm.tagFilter = tagFilter;

    //Template tag filter(判断是否隐藏 true-隐藏)
    vm.getDescriptionHtml = getDescriptionHtml;

    // 入口
    init();

    // ===========

    //入口
    function init() {
        uiLoadingSrv.removeLoading('.pt-main');

        //获取panel模板列表
        getPanelTemplets()

        //GTM
        siteEventAnalyticsSrv.setGtmEvent('click_element', 'resources', 'dash_templates');
    }

    //获取模版列表
    function getPanelTemplets() {
        PanelTempletsResources.getPublishedPanelTemplet()
        .then((data) => {
            if (data.length > 0) {
                for (var i = 0; i < data.length; i++) {

                    //try防止管理员添加的信息有误。
                    try {
                        data[i].panelTitle = angular.fromJson(data[i].panelTitle);
                    } catch (e) {
                        data[i].panelTitle = null;
                    }
                    try {
                        data[i].description = angular.fromJson(data[i].description);
                    } catch (e) {
                        data[i].description = null;
                    }

                    if (!data[i].tagList) {
                        data[i].tagList = [];
                    }
                    for (var j = 0; j < data[i].tagList.length; j++) {
                        data[i].tagList[j].ptoneTagName = angular.fromJson(data[i].tagList[j].ptoneTagName);
                    }

                    vm.myOptions.tagsFilter.tmpList = [];
                }
            }
            console.log(data)
            $scope.rootPanel.templateList = data;
            $scope.rootPanel.templetSort = '-orderNumber';
        })
    }

    //选择模板新增panel
    function addTemplet(templet) {
        //loading
        uiLoadingSrv.createLoading('.pt-main');

        var templetInfo = {
            type: "panel",
            templetId: templet.panelId,
            spaceId: $scope.rootSpace.current.spaceId
        }
        //发送模板ID至后台，返回生成后的panelID信息
        PanelResources.createByTemplet(templetInfo)
        .then((data) => {
            var panelInfo = data.panel;

            //如果已请求过面板列表，则更新
            if ($scope.rootPanel.list) {
                $scope.rootPanel.list.unshift(panelInfo);
                $scope.rootPanel.noData = false;
                $scope.rootPanel.layout.panelLayout = angular.fromJson(data.panelLayout.panelLayout);
                $scope.rootPanel.layout.dataVersion = data.panelLayout.dataVersion;
            }

            //存储中转数据至main.ctrl.js,再跳转
            $scope.rootTmpData.addTemplate = panelInfo;
            $scope.rootWidget.linkData.showTips = false;
            $state.go('pt.dashboard');
        }, () => {
            uiLoadingSrv.removeLoading('.pt-main');
        })

        //全站事件统计
        siteEventAnalyticsSrv.createData({
            uid: $rootScope.userInfo.ptId,
            where: "dashboard_templates",
            what: "use_this_template",
            how: "click",
            value: templet.panelId
        });
    };

    //图片放大-显示
    function showBigImg(e, src, title) {
        var ele = angular.element(e.target);
        vm.myOptions.bigImg.width = ele.attr('data-width');
        vm.myOptions.bigImg.src = ele.attr('src');
        vm.myOptions.bigImg.title = ele.attr('alt');
        vm.myOptions.bigImg.show = true;
        $body.addClass('modal-open');
    };

    //图片放大-关闭
    function closeBigImg() {
        vm.myOptions.bigImg.show = false;
        vm.myOptions.bigImg.src = null;
        vm.myOptions.bigImg.title = null;
        vm.myOptions.bigImg.width = null;
        $body.removeClass('modal-open');
    };

    //Select Tag
    function sltTag(tag) {
        var index;
        for (var i = 0; i < vm.myOptions.tagsFilter.list.length; i++) {
            if (vm.myOptions.tagsFilter.list[i].ptoneTagId == tag.ptoneTagId) {
                index = i;
                break;
            }
        }
        if (angular.isDefined(index)) {
            vm.myOptions.tagsFilter.list.remove(index);
        } else {
            vm.myOptions.tagsFilter.list.push(tag)
        }

        //GTM
        siteEventAnalyticsSrv.setGtmEvent('click_element', 'templates', tag.ptoneTagName.en_US);

        //全站事件统计
        siteEventAnalyticsSrv.createData({
            uid: $rootScope.userInfo.ptId,
            where: "dashboard_templates",
            what: "select_template_tag",
            how: "click",
            value: !angular.isDefined(index) + ',' + tag.ptoneTagId
        });
    };

    //Template tag filter(判断是否隐藏 true-隐藏)
    function tagFilter(tagList, index) {
        var flag = false;

        if (vm.myOptions.tagsFilter.list.length != 0) {
            if (tagList !== null && tagList.length >= vm.myOptions.tagsFilter.list.length) {

                var filterFlag = [];
                for (var i = 0; i < vm.myOptions.tagsFilter.list.length; i++) {
                    filterFlag[i] = false;
                    for (var j = 0; j < tagList.length; j++) {
                        if (vm.myOptions.tagsFilter.list[i].ptoneTagId == tagList[j].ptoneTagId) {
                            filterFlag[i] = true;
                            break;
                        }
                    }
                }
                flag = filterFlag.indexOf(false) < 0 ? false : true;
            } else {
                flag = true;
            }


            var tmpIndex = vm.myOptions.tagsFilter.tmpList.indexOf($scope.rootPanel.templateList[index].panelId);
            if (tmpIndex < 0) {
                if (flag) {
                    vm.myOptions.tagsFilter.tmpList.push($scope.rootPanel.templateList[index].panelId);
                }
            } else {
                if (!flag) {
                    vm.myOptions.tagsFilter.tmpList.remove(tmpIndex);
                }
            }

            return flag;
        } else {
            vm.myOptions.tagsFilter.tmpList = [];
        }
    };

    //返回当前语言本地化HTML, 替换换行符
    function getDescriptionHtml(dom) {
        return dom ? dom.replace(/\n/g, "<br/>") : "";
    }
}



//图片load
angular
    .module('pt')
    .directive('imageloaded', [
        function() {
            return {
                restrict: 'A',
                link: function(scope, element, attrs) {
                    element.bind('load', function(e) {
                        jQuery(element).attr('data-width', jQuery(element).width).addClass('img');
                    });
                }
            }
        }
    ]);
