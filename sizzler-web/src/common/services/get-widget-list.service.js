'use strict';

/**
 * getWidgetListSrv
 * 获取当前Panel下的Widget列表
 *
 */
import {
    LINK_PANEL_WIDGET_WITH_LAYOUT,
    LINK_PANEL_WIDGET_WITH_LAYOUT_MOBIL,
    getMyDsConfig
} from 'components/modules/common/common';

getWidgetListSrvFunc.$inject = ['$rootScope', 'PanelResources'];

function getWidgetListSrvFunc($rootScope, PanelResources) {

    return {

        getList: function(dashboard, isShare, isPhone, backFunc) {
            var newList = [];
 
            //获取widget列表
            PanelResources.findWidget(null, {
                    panelId: dashboard.panelId,
                    device: isPhone ? 'mobile' : 'pc',
                    accessToken: $rootScope.accessToken || null
                })
                .then((data) => {
                    var list = data.widgetList;
                    var layout = data.layout ? angular.fromJson(decodeURIComponent(data.layout)) : null;

                    if (list.length > 0) {
                        newList = widgetFormat(list, layout, isShare);
                    }

                    backFunc(newList);
                })
        }
    };

    //widget位置信息更新
    function widgetFormat(list, layout, isShare) {
        var newList = [];

        for (var i = 0; i < list.length; i++) {
            var widget = list[i];

            var sizeX = 6;
            var sizeY = 8;
            var minSizeX = 6;
            var minSizeY = 8;
            var row = null;
            var col = null;

            if (list[i].baseWidget.widgetType == 'tool' && list[i].baseWidget.graphName == 'text') {
                sizeX = 3;
                sizeY = 2;
                minSizeX = 3;
                minSizeY = 2;
                list[i].baseWidget.widgetEdit = false; //此富文本框处于编辑状态：默认false
            }

            var wId = list[i].baseWidget.widgetId;
            if (layout !== null) {
                for (var k = 0; k < layout.length; k++) {
                    if (layout[k].id == wId) {
                        sizeX = +layout[k].x;
                        sizeY = +layout[k].y;

                        if (angular.isDefined(layout[k].r)) {
                            row = +layout[k].r;
                            col = +layout[k].c;
                        }
                    }
                }
            }

            //根据类chart数据格式转换
            if (list[i].baseWidget.widgetType == 'tool' && list[i].baseWidget.graphName == 'text') {
                try {
                    list[i].toolData.value = decodeURIComponent(angular.copy(list[i].toolData.value));
                } catch (err) {
                    list[i].toolData.value = angular.copy(list[i].toolData.value);
                }
            }

            //对自定义widget中的富文本框数据进行处理
            if (list[i].baseWidget.widgetType == 'custom' && list[i].children.length > 0) {
                for (var j = 0; j < list[i].children.length; j++) {
                    if (list[i].children[j].baseWidget.widgetType == 'tool' && list[i].children[j].baseWidget.graphName == 'text') {
                        try {
                            list[i].children[j].toolData.value = decodeURIComponent(angular.copy(list[i].children[j].toolData.value));
                        } catch (err) {
                            list[i].children[j].toolData.value = angular.copy(list[i].children[j].toolData.value);
                        }
                    }
                }
            }

            widget.sizeX = sizeX;
            widget.sizeY = sizeY;
            widget.row = row;
            widget.col = col;
            widget.minSizeX = minSizeX;
            widget.minSizeY = minSizeY;
            widget.dsConfig = getMyDsConfig(widget.variables[0].dsCode);

            //后台一并请求
            if (!widget.ext) widget.ext = {};
            widget.ext['allLoad'] = true;

            newList.push(widget);
        }
        return newList;
    }
}

export default getWidgetListSrvFunc;
