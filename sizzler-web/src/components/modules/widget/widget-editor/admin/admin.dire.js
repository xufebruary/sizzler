import adminTpl from 'components/modules/widget/widget-editor/admin/admin.tpl.html';

angular
    .module('pt')
    .directive('editorAdmin', ['$document', 'dataMutualSrv', '$timeout', editorAdmin]);

function editorAdmin($document, dataMutualSrv, $timeout) {
    return {
        restrict: 'EA',
        replace: true,
        template: adminTpl,
        link: link
    };

    function link(scope, elem, attrs) {

        scope.isSuccess = false;

        //监听气泡显示状态
        // scope.$watch('editor.pop.name', function (value) {
        //     if (value && value == 'admin') {
        //         $document.bind('click', documentClickBindAccount);
        //     } else {
        //         $document.unbind('click', documentClickBindAccount);
        //     }
        // });
        var documentClickBindAccount = function (event) {
            if (scope.editor.pop.show && typeof(angular.element(event.target).attr('step-admin')) == 'undefined' && !elem[0].contains(event.target)) {
                scope.$apply(function () {
                    scope.editor.pop.name = null;
                    scope.editor.pop.show = false;
                });
                $document.unbind('click', documentClickBindAccount);
            }
        };

        var adminSettings = scope.adminSettings = {
            type: 'zh_CN', //zh_CN||en_US||ja_JP
            modelTitle: {'zh_CN': null, 'en_US': null, 'ja_JP': null},
            modelDescribe: {'zh_CN': null, 'en_US': null, 'ja_JP': null}
        };

        scope.changeType = function (type) {
            adminSettings.type = type;
            if (scope.modal.editorNow.baseWidget.widgetTitle && !adminSettings.modelTitle[type]) {
                adminSettings.modelTitle[type] = scope.modal.editorNow.baseWidget.widgetTitle[type];
            }
            if (scope.modal.editorNow.baseWidget.description && !adminSettings.modelDescribe[type]) {
                adminSettings.modelDescribe[type] = scope.modal.editorNow.baseWidget.description[type];
            }
        };

        //init adminSettings
        for(var i = 0;i<scope.rootCommon.langList.length;i++){
            scope.changeType(scope.rootCommon.langList[i].code);
        }

        scope.saveWidgetInfo = function () {
            scope.modal.editorNow.baseWidget.widgetTitle = adminSettings.modelTitle;
            scope.modal.editorNow.baseWidget.description = adminSettings.modelDescribe;
            for(var i = 0;i<scope.rootCommon.langList.length;i++){
                var code = scope.rootCommon.langList[i].code;
                if(!adminSettings.modelTitle[code] || !adminSettings.modelDescribe[code]){
                    alert("Widget title or Widget description not be null.");
                    return;
                }
            }
            if ($('.js_tagsList').length > 0) {
                scope.modal.editorNow.tags = [];
                $('.js_tagsList:checked').each(function (i, item) {
                    scope.modal.editorNow.tags.push(item.value);
                });
            }
            var tempWidget = {
                widgetId:scope.modal.editorNow.baseWidget.widgetId,
                widgetTitle:angular.toJson(adminSettings.modelTitle),
                description:angular.toJson(adminSettings.modelDescribe)
            };
            dataMutualSrv.post(LINK_BASE_WIDGET_EDIT,tempWidget).then(function (data) {
                if (data.status == 'success') {
                    scope.isSuccess = true;
                    $timeout(function () {
                            scope.isSuccess = false;
                        },
                        1000
                    )
                    console.log('Post Data Success!')
                } else if (data.status == 'failed') {
                    console.log('Post Data Failed!')
                } else if (data.status == 'error') {
                    console.log('Post Data Error: ')
                    console.log(data.message)
                }
            })
        };

        scope.$on('ngRepeatFinished', function (ngRepeatFinishedEvent) {
            if ($('.js_tagsList').length > 0) {
                $('.js_tagsList').each(function (i, item) {
                    for (var o in scope.modal.editorNow.tags) {
                        if (item.value == scope.modal.editorNow.tags[o]) {
                            $(this).attr("checked", 'true')
                        }
                    }
                });
            }

        });


    }
}
