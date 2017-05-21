'use strict';



/**
 * widget 发布
 *
 */

angular.
    module('pt')
    .directive('widgetPublish', ['$document', 'dataMutualSrv',widgetPublish]);

function widgetPublish($document, dataMutualSrv){
    return {
        restrict: 'EA',
        templateUrl: '/components/modules/template/widget/widget_publish.html?v='+BASE_VERSION,
        link: link
    }

    function link(scope, element, attrs){
        var body = $document.find('body').eq(0);
        // body.addClass('modal-open');

        //取消
        scope.close = function () {
            body.removeClass('modal-open');
            scope.modal.publishWidgetShow = false;
        }

        //publish widget
        scope.publish = function() {

            var newBaseWidget = {};
            scope.status = [];
            if($('.js_langList').length > 0){
                $('.js_langList:checked').each(function (i,item) {
                    scope.status.push(item.value);
                });
            };

            if(scope.status.length <= 0){
                scope.showAreaTip = true;
                return;
            }

            //当前widget数据获取
            var widgetList = scope.modal.templatesList;
            var widget = {};
            var widgetIndex = null;
            for (var i = 0; i < widgetList.length; i++) {
                if (widgetList[i].baseWidget.widgetId == scope.modal.templetId) {
                    //复制对象
                    widget = widgetList[i];
                    widgetList[i].baseWidget.isExample = 1;
                    widgetIndex = i;
                    break;
                }
            }

            newBaseWidget.widgetId = widget.baseWidget.widgetId;
            newBaseWidget.modifierId = scope.rootUser.settingsInfo.ptId;
            newBaseWidget.modifyTime = parseInt(new Date().getTime());
            newBaseWidget.isExample = 1;

            if(scope.modal.isPublished != 1){
                newBaseWidget.status = scope.status.join(",");
            }else{
                newBaseWidget.status = scope.modal.isPublished;
            }

            // 修改widget模板发布状态
            dataMutualSrv.post(LINK_BASE_WIDGET_TEMPLET_EDIT, newBaseWidget).then(function(data) {
                if (data.status == 'success') {
                    widget.baseWidget.status = scope.modal.isPublished; // 更新当前widget信息
                    scope.modal.publishWidgetShow = false;
                } else if (data.status == 'failed') {
                    scope.modal.publishWidgetShow = false;
                } else if (data.status == 'error') {
                    scope.modal.publishWidgetShow = false;
                }
            });
        };

    }
}