'use strict';

/**
 * 0.1.1
 * General-purpose jQuery wrapper. Simply pass the plugin name as the expression.
 *
 * It is possible to specify a default set of parameters for each jQuery plugin.
 * Under the jq key, namespace each plugin by that which will be passed to ui-jq.
 * Unfortunately, at this time you can only pre-define the first parameter.
 * @example { jq : { datepicker : { showOn:'click' } } }
 *
 * @param ui-jq {string} The $elm.[pluginName]() to call.
 * @param [ui-options] {mixed} Expression to be evaluated and passed as options to the function
 *     Multiple parameters can be separated by commas
 * @param [ui-refresh] {expression} Watch expression and refire plugin on changes
 *
 * @example <input ui-jq="datepicker" ui-options="{showOn:'click'},secondParameter,thirdParameter" ui-refresh="iChange">
 */

export default angular
    .module('ui.jq', ['ui.load'])
    .value('uiJqConfig', {})
    .directive('uiJq', ['uiJqConfig', 'JQ_CONFIG', 'uiLoad', '$timeout', uiJqFunc]);


function uiJqFunc(uiJqConfig, JQ_CONFIG, uiLoad, $timeout) {

    return {
        restrict: 'A',
        compile: function uiJqCompilingFunction(tElm, tAttrs) {

            if (!angular.isFunction(tElm[tAttrs.uiJq]) && !JQ_CONFIG[tAttrs.uiJq]) {
                throw new Error('ui-jq: The "' + tAttrs.uiJq + '" function does not exist');
            }
            var options = uiJqConfig && uiJqConfig[tAttrs.uiJq];

            return function uiJqLinkingFunction(scope, elm, attrs) {

                function getOptions() {
                    var linkOptions = [];

                    // If ui-options are passed, merge (or override) them onto global defaults and pass to the jQuery method
                    if (attrs.uiOptions) {
                        linkOptions = scope.$eval('[' + attrs.uiOptions + ']');
                        if (angular.isObject(options) && angular.isObject(linkOptions[0])) {
                            linkOptions[0] = angular.extend({}, options, linkOptions[0]);
                        }
                    } else if (options) {
                        linkOptions = [options];
                    }
                    return linkOptions;
                }

                // If change compatibility is enabled, the form input's "change" event will trigger an "input" event
                if (attrs.ngModel && elm.is('select,input,textarea')) {
                    elm.bind('change', function () {
                        elm.trigger('input');
                    });
                }

                // Call jQuery method and pass relevant options
                function callPlugin() {
                    $timeout(function () {
                        var options = getOptions();
                        //针对自定义滚动条绑定事件
                        if (attrs.slimscroll == 'templateList') {
                            elm.bind('slimscroll', function (e, pos) {
                                if (pos == 'bottom') {
                                    scope.tplListScrool();
                                }
                            })
                        }

                        if (attrs.slimscroll == 'chartTable') {
                            elm.resize(function () {
                                console.log(1)
                            })
                        }

                        //针对编辑器日历绑定事件
                        if (attrs.picker) {
                            // options[0].date = elm.val();
                            options[0].onBeforeShow = function () {
                                // elm.DatePickerSetDate(attrs.date, true);
                                elm.DatePickerSetDate(attrs.date.split(","), true);
                            }

                            options[0].onChange = function (formated, dates) {
                                // elm.attr('data-date', formated);
                                // elm.prev('label').find('input').val(formated);

                                eval('scope.' + attrs.changefun + '(\"' + attrs.detetype + '\",\"' + formated + '\")');

                                //scope.dateChange(attrs.detetype, formated)
                            },
                                options[0].onRender = function (date) {
                                    return {
                                        disabled: date.valueOf() > new Date().valueOf()
                                    }
                                }

                        }

                        //轮播
                        //if (attrs.unslider) {
                        //    elm.unslider({
                        //        speed: 500,
                        //        delay: 3000,
                        //        keys: true,
                        //        dots: true,
                        //        arrows: false,
                        //        fluid: false
                        //    });
                        //}
                        elm[attrs.uiJq].apply(elm, options);
                    }, 0, false);
                }

                function refresh() {
                    // If ui-refresh is used, re-fire the the method upon every change
                    if (attrs.uiRefresh) {
                        scope.$watch(attrs.uiRefresh, function () {
                            callPlugin();
                        });
                    }
                }

                if (JQ_CONFIG[attrs.uiJq]) {
                    uiLoad.load(JQ_CONFIG[attrs.uiJq]).then(function () {
                        callPlugin();
                        refresh();
                    }).catch(function () {

                    });
                } else {
                    callPlugin();
                    refresh();
                }
            };
        }
    };
}
