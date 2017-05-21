import tpl from './title.tpl.html';
import './title.css';


editorTitle.$inject = ['$translate'];

function editorTitle($translate) {
    return {
        restrict: 'EA',
        template: tpl,
        link: link
    };

    function link(scope, elem, attrs) {
        var titleSet = scope.titleSet = {
            'old': scope.modal.editorNow.baseWidget.widgetTitle
        };

        //监听气泡显示状态
        // scope.$watch('editor.pop.name', function(value) {
        //     if (value && value == 'time') {
        //         $document.bind('click', documentClickBindTime);
        //     } else {
        //         $document.unbind('click', documentClickBindTime);
        //     }
        // });
        var documentClickBindTime = function (event) {
            if (timeSettings.datePickerHideClick) {
                timeSettings.datePickerHideClick = false;
            } else if (scope.editor.pop.show && typeof(angular.element(event.target).attr('step-time')) == 'undefined' && !elem[0].contains(event.target)) {
                scope.$apply(function () {
                    scope.editor.pop.name = null;
                    scope.editor.pop.show = false;
                    scope.editor.pop.ele = null;
                });
            }
        };


        var titleSettings = scope.titleSettings = {
            title: scope.modal.editorNow.baseWidget.widgetTitle
        };

        scope.save = function () {
            if (!titleSettings.title || titleSettings.title == '') {
                var widgetTitle = scope.modal.editorNow.baseWidget.widgetTitle;
                titleSettings.title = widgetTitle == '' ? $translate.instant("WIDGET.WIDGET_DEFAULT_NAME") : widgetTitle;
            }
            if (titleSet.old != titleSettings.title) {
                scope.modal.editorNow.baseWidget.widgetTitle = titleSettings.title;
                scope.modal.editorNow.baseWidget.isTitleUpdate = 1; // 用户修改title
                scope.saveData('title-changeTitle');
            }
        };

        //回车事件绑定
        scope.enterEvent = function (e) {
            var keycode = window.event ? e.keyCode : e.which;
            if (keycode == 13) {
                scope.save();
            }
        };

    }
}

export default editorTitle;
