'use strict';

/**
 * 全屏缩放/发大
 */

ptFullScreenDirective.$inject = ['uiLoad', '$document', '$timeout', 'gridstackService'];

//全屏指令，将全屏时间分开
function ptFullScreenDirective(uiLoad, $document, $timeout, gridstackService){
    return {
        restrict: 'A',
        replace: true,
        link: function (scope, element, attrs) {
            // element.addClass('hide');
            uiLoad.load('/assets/libs/screenfull.min.js').then(function () {
                // disable on ie11
                if (screenfull.enabled && !navigator.userAgent.match(/Trident.*rv:11\./)) {
                    // element.removeClass('hide');
                }
                element.on('click', function () {
                    var target;
                    attrs.target && ( target = $(attrs.target)[0] );
                    screenfull.toggle(target);
                });
                $document.on(screenfull.raw.fullscreenchange, function () {
                    if (screenfull.isFullscreen) {
                        // element.addClass('active');
                        if (angular.isUndefined(scope.sharePanelFlag)) {
                            //非分享页面

                            scope.pt.settings.fullScreen = true;
                            scope.pt.settings.headFolded = true;
                            scope.pt.settings.asideFolded = true;
                            scope.pt.settings.asideFoldAll = true;

                            //编辑模式下,将widget放大
                            var colWidth = Math.max(parseInt((window.screen.width - 17 - 40) / scope.rootChart.columns), 28);
                            scope.$apply(function () {
                                if (screenfull.enabled) {
                                    document.addEventListener(screenfull.raw.fullscreenchange, function () {
                                        $timeout(
                                            function () {
                                                $(window).resize();
                                            },
                                            800
                                        )
                                    });
                                }
                                scope.rootChart.colWidth = colWidth;
                                scope.rootChart.rowHeight = colWidth;
                                scope.rootPage.contentWidth = parseInt(scope.rootChart.columns * colWidth);

                                $timeout(
                                    function () {
                                        $(window).resize();
                                    },
                                    1500
                                )
                            })
                        } else {
                            //分享页面
                            scope.$apply(function () {
                                scope.rootPage.shareFullScreen = true;
                            });
                        }
                    } else {
                        // element.removeClass('active');
                        if (angular.isUndefined(scope.sharePanelFlag)) {
                            if (scope.rootPage.dashboardMode == 'READ') {
                                scope.pt.settings.fullScreen = false;
                                scope.pt.settings.headFolded = false;
                                scope.pt.settings.asideFolded = false;
                                scope.pt.settings.asideFoldAll = false;

                                //查看模式下,将widget缩小
                                var colWidth = Math.max(parseInt(30 * (window.screen.width / 1366)), 28);

                                scope.$apply(function () {
                                    if (screenfull.enabled) {
                                        document.addEventListener(screenfull.raw.fullscreenchange, function () {
                                            $timeout(
                                                function () {
                                                    $(window).resize();
                                                },
                                                800
                                            )
                                        });
                                    }
                                    scope.rootChart.colWidth = colWidth;
                                    scope.rootChart.rowHeight = colWidth;
                                    scope.rootPage.contentWidth = parseInt(scope.gridstackOptions.width * colWidth);
                                })
                            }
                            scope.pt.settings.fullScreen = false;
                        } else {
                            //分享页面
                            scope.$apply(function () {
                                scope.rootPage.shareFullScreen = false;
                            });
                        }
                    }

                    //widget 大小自适应
                    gridstackService.setWidgetHeight(scope.rootChart.rowHeight);
                });
            });
        }
    }
}

export default ptFullScreenDirective;
