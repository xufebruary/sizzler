'use strict';

/**
 * richtext
 * 富文本框类型
 *
 */

import tpl from './chart-richtext.html';
import {
    LINK_UPLOAD_IMAGE
} from 'components/modules/common/common';

import cookieUtils from 'utils/cookie.utils';

angular
    .module('pt')
    .directive('richText', ['$rootScope', '$document', '$translate', 'sysRoles', 'uiLoadingSrv', 'gridstackService', '$sce', richText]);


function richText($rootScope, $document, $translate, sysRoles, uiLoadingSrv, gridstackService, $sce) {
    return {
        restrict: 'EA',
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {
        var body = $document.find('body').eq(0);

        scope.toolData = {
            i18n: 'ja',
            name: '.ptone-tinymce-' + attrs.index
        };


        //等待widget初始化完成
        scope.$watch('$viewContentLoaded', function() {
            if (!scope.pt.settings.isPhone) { //移动端不需要滚动条插件
                $('.js-mCustomScrollbar.widget-bd-tool').mCustomScrollbar({
                    axis: "yx",
                    theme: "minimal-dark",
                    autoHideScrollbar: true,
                    advanced: {
                        autoScrollOnFocus: "input,textarea,select,div,button,datalist,keygen,a[tabindex],area,object,[contenteditable='true']",
                        updateOnContentResize: true,
                        updateOnImageLoad: true
                    }
                });
            }
        });

        if (scope.rootUser && scope.rootUser.settingsInfo && scope.rootUser.settingsInfo.locale) {
            var local = scope.rootUser.settingsInfo.locale;
            if (local == 'zh_CN') {
                scope.toolData.i18n = 'zh_CN'
            } else if (local == 'ja_JP') {
                scope.toolData.i18n = 'ja'
            } else if (local == 'en_US') {
                scope.toolData.i18n = 'en_GB'
            }
        }
        scope.$on('createWidgetOfTool', function(e, data) {
            //if(data == 'new'){
            //    scope.toolData.name = '.ptone-tinymce-' + (scope.rootWidget.list.length);
            //    $timeout(function(){
            //        scope.initialize('placeholder','new');
            //    },2000);
            //}
        });


        // CALLBACKS
        scope.uploader.onWhenAddingFileFailed = function(item, filter, options) {
            console.info('onWhenAddingFileFailed', item, filter, options);
        };

        scope.uploader.onAfterAddingFile = function(fileItem) {
            console.info('onAfterAddingFile', fileItem);
        };
        scope.uploader.onAfterAddingAll = function(addedFileItems) {
            console.info('onAfterAddingAll', addedFileItems);
            addedFileItems = addedFileItems[0];
            if (addedFileItems && addedFileItems.file && addedFileItems.file.size) {
                var inputFather = $('[aria-label = "Insert/edit image"]'),
                    lastFooter = inputFather.find(' > .mce-reset > div:last-child > div');
                if (addedFileItems.file.size / 1024 > 300) {
                    if (lastFooter.find('.temTipsForUploadImg').length > 0) {
                        lastFooter.find('.temTipsForUploadImg').html($translate.instant('WIDGET.RICH_TEXT.TIPS.UPLOAD_IMAGES_TIPS1'));
                    } else {
                        lastFooter.prepend('<p class="temTipsForUploadImg">' + $translate.instant('WIDGET.RICH_TEXT.TIPS.UPLOAD_IMAGES_TIPS1') + '</p>');
                        lastFooter.find('.temTipsForUploadImg').css({
                            'padding-left': '5px',
                            'width': '270px',
                            'height': '50px',
                            'display': 'table-cell',
                            'vertical-align': 'middle',
                            'white-space': 'pre-wrap',
                            'font-size': '12px',
                            'color': 'red'
                        });
                    }
                    for (var i = 0; i < scope.uploader.queue.length; i++) {
                        if (scope.uploader.queue[i].file.name == addedFileItems.file.name) {
                            scope.uploader.queue.splice(i, 1);
                        }
                    }
                } else if (addedFileItems.file.type.indexOf('image') !== 0) {
                    if (lastFooter.find('.temTipsForUploadImg').length > 0) {
                        lastFooter.find('.temTipsForUploadImg').html($translate.instant('WIDGET.RICH_TEXT.TIPS.UPLOAD_IMAGES_TIPS2'));
                    } else {
                        lastFooter.prepend('<p class="temTipsForUploadImg">' + $translate.instant('WIDGET.RICH_TEXT.TIPS.UPLOAD_IMAGES_TIPS2') + '</p>');
                        lastFooter.find('.temTipsForUploadImg').css({
                            'padding-left': '5px',
                            'width': '270px',
                            'height': '50px',
                            'display': 'table-cell',
                            'vertical-align': 'middle',
                            'white-space': 'pre-wrap',
                            'font-size': '12px',
                            'color': 'red'
                        });
                    }
                    for (var i = 0; i < scope.uploader.queue.length; i++) {
                        if (scope.uploader.queue[i].file.name == addedFileItems.file.name) {
                            scope.uploader.queue.splice(i, 1);
                        }
                    }
                } else {
                    if (lastFooter.find('.temTipsForUploadImg').length > 0) {
                        lastFooter.find('.temTipsForUploadImg').remove();
                    }
                    for (var i = 0; i < scope.uploader.queue.length; i++) {
                        if (scope.uploader.queue[i].file.name == addedFileItems.file.name) {
                            scope.uploader.queue[i].url = LINK_UPLOAD_IMAGE + '?sid=' + cookieUtils.get('sid');
                            scope.uploader.queue[i].upload();
                        }
                    }
                }
            }

        };
        scope.uploader.onBeforeUploadItem = function(item) {
            uiLoadingSrv.createLoading(body);
        };
        scope.uploader.onProgressItem = function(fileItem, progress) {
            console.info('onProgressItem', fileItem, progress);
        };
        scope.uploader.onProgressAll = function(progress) {
            console.info('onProgressAll', progress);
        };
        scope.uploader.onSuccessItem = function(fileItem, response, status, headers) {
            console.info('onSuccessItem', fileItem, response, status, headers);
            if (response.status == 'success') {
                var content = response.content,
                    originalFileName = content.originalFileName,
                    inputFather = $('[aria-label = "Insert/edit image"]');
                inputFather.find('input').eq(0).val(content.imgUrl);
                inputFather.find('input').eq(1).val(originalFileName.substring(0, originalFileName.lastIndexOf('.')));
                inputFather.find('input').eq(2).val(200);
                inputFather.find('input').eq(3).val(200);
            } else {
                alert(response.content);
            }
            uiLoadingSrv.removeLoading(body);
        };

        scope.uploader.onErrorItem = function(fileItem, response, status, headers) {
            console.info('onErrorItem', fileItem, response, status, headers);
        };
        scope.uploader.onCancelItem = function(fileItem, response, status, headers) {
            console.info('onCancelItem', fileItem, response, status, headers);
        };
        scope.uploader.onCompleteItem = function(fileItem, response, status, headers) {
            console.info('onCompleteItem', fileItem, response, status, headers);
            if (response.status == 'success') {

            } else {
                alert(response.message);
                console.log('uploader error');
                console.log(response.message);
            }
            uiLoadingSrv.removeLoading(body);

        };
        scope.uploader.onCompleteAll = function() {
            console.info('onCompleteAll');
            uiLoadingSrv.removeLoading(body);
        };


        scope.initialize = function(placeholder, newWidget) {
            //分享直接return
            if (scope.rootPanel.now.shareSourceId) {
                return;
            }

            if (!sysRoles.hasSysRole("ptone-admin-user") && scope.rootPage.dashboardMode != 'EDIT') {
                scope.toggleDashboardMode('EDIT'); //切换pannel到编辑模式
            }

            scope.modal.editorShow = scope.rootTmpData.editorShow = false; //关闭其他编辑器
            gridstackService.disableLayout('disableDrag');
            if (newWidget) {
                scope.rootWidget.list[scope.rootWidget.list.length - 1].baseWidget.widgetEdit = true;
            } else {
                scope.rootWidget.list[attrs.index].baseWidget.widgetEdit = true;
            }

            tinymce.init({
                selector: '.ptone-tinymce-' + attrs.index,
                menubar: false,
                inline: true,
                toolbar1: 'formatselect bold italic underline strikethrough alignleft aligncenter alignright alignjustify',
                toolbar2: 'forecolor backcolor | bullist numlist | link image media code',
                plugins: 'code textcolor link image media',
                //extended_valid_elements: 'script[type|src],iframe[src|style|width|height|scrolling|marginwidth|marginheight|frameborder]',
                language: scope.toolData.i18n,
                default_link_target: "_blank",
                forced_root_block: '',
                init_instance_callback: function(editor) {
                    $(element).find('.ptone-tinymce-' + attrs.index).focus();
                },
                file_picker_callback: function(callback, value, meta) {

                    // Provide image and alt text for the image dialog
                    if (meta.filetype == 'image') {
                        $('.upload_form_' + attrs.index + ' input').click();
                    }
                },
                file_picker_types: 'image',
                setup: function(editor) {
                    editor.on('blur', function() {
                        scope.rootWidget.list[attrs.index].baseWidget.widgetEdit = false;
                        var value = editor.getContent();
                        if (value) {
                            scope.saveText(attrs.index, encodeURIComponent(value));
                            if (newWidget) {
                                scope.rootWidget.list[scope.rootWidget.list.length - 1].toolData.value = value;
                            } else {
                                scope.rootWidget.list[attrs.index].toolData.value = value;
                            }

                        } else {
                            scope.saveText(attrs.index, encodeURIComponent(value));
                            $('.ptone-tinymce-' + attrs.index).html($translate.instant('WIDGET.EDITOR.TOOL.PLACEHOLDER')).css("color", "#bdbdbd");
                        }
                        editor.remove();
                        //editor.destroy();
                        // scope.gridsterOptions.draggable.enabled = true;
                        $(element).parents('.li-widget').find('.ui-resizable-handle').css('display', 'none'); //富文本框失去焦点时，有时，拖拽不会失效
                        gridstackService.enableLayout();
                    });
                }
            });

            if (placeholder) {
                var nowEdit = $('.ptone-tinymce-' + attrs.index);
                var html = nowEdit.html(),
                    placeH = $translate.instant('WIDGET.EDITOR.TOOL.PLACEHOLDER');
                if (html == placeH || html == '<p>' + placeH + '</p>') {
                    nowEdit.html('').css("color", "#616161");
                }
            }
        };

        scope.bindHtml = function(text) {
            return $sce.trustAsHtml(text);
        }

    }
}
