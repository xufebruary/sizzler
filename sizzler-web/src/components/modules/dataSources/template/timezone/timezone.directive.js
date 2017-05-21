'use strict';

import './timezone.scss';
import tpl from './timezone.html';
import moment from 'moment-timezone';
import timezoneNames from './timezone'

/**
 * timezone
 *
 */
timezoneDirective.$inject = ['$filter', '$translate', '$timeout', 'uiLoadingSrv', 'DataSourcesServices'];

function timezoneDirective($filter, $translate, $timeout, uiLoadingSrv, DataSourcesServices) {
    return {
        restrict: 'EA',
        scope: {
            fieds: '<',
            tipsCode: '@',
            hideTimezone: '&'
        },
        template: tpl,
        link: link
    };

    function link(scope, elem, attrs) {
        scope.myOptions = {
            isSupoortTimezone: scope.fieds.supportTimezone == 1 && scope.fieds.hasTimezoneFiled == 1,
            isDefaultTimezone: scope.fieds.isDefaultTimezone == 1,
            currentTimezone: {
                name: $translate.instant('DATA_SOURCE.TIMEZONE.DEFAULT_TIPS'),
                code: null
            },
            defaultTimezoneModel: false,
            timezoneList: null,
            errorTips: false,
            tipsLocalCode: scope.tipsCode || 'DATA_SOURCE.TIMEZONE.TIPS',
            dropdownIsOpen: false,
            dropdownOptions: []
        }

        // ==========

        scope.hide = hide;
        scope.save = save;
        scope.isDisabled = isDisabled;
        scope.selectTimezone = selectTimezone;
        scope.dropdownToggle = dropdownToggle;
        scope.searchChange = searchChange;

        //入口
        init();

        // ==========

        function init() {
            show();
            scope.myOptions.timezoneList = getZones();
            scope.myOptions.defaultTimezoneModel = !scope.myOptions.isSupoortTimezone || scope.myOptions.isDefaultTimezone;
            if (scope.myOptions.isSupoortTimezone && !scope.myOptions.defaultTimezoneModel && scope.fieds.dataTimezone && scope.fieds.dataTimezone.name) {
                scope.myOptions.currentTimezone = scope.fieds.dataTimezone;
            }

            //支持timezone但没有时间戳类型字段，则变更提示
            if(scope.fieds.supportTimezone == 1 && scope.fieds.hasTimezoneFiled == 0){
                scope.myOptions.tipsLocalCode = 'DATA_SOURCE.TIMEZONE.NO_FILED';
            }
            searchChange();
        }


        //保存
        function save() {
            if(isDisabled()){
                scope.myOptions.errorTips = true;

                //显示2秒,自动隐藏
                $timeout(function () {
                    scope.$apply(function () {
                        scope.myOptions.errorTips = null;
                    })
                }, 2000);

                return false
            }

            showPopupLoading();

            let sendData = {
                isDefaultTimezone: +scope.myOptions.defaultTimezoneModel,
                dataTimezone: scope.myOptions.currentTimezone.code ? scope.myOptions.currentTimezone : null
            };

            let linkInfo = {
                dsId: scope.fieds.dsId,
                connectionId: scope.fieds.connectionId,
                sourceId: scope.fieds.sourceId
            };

            if (isChange(sendData)) {
                DataSourcesServices.updateTimezone(sendData, linkInfo)
                    .then(data => hide())
                    .finally(data => hidePopupLoading())
            } else {
                hide();
                hidePopupLoading();
            }
        }

        //获取按钮disabled状态
        function isDisabled(){
            return !scope.myOptions.defaultTimezoneModel && !scope.myOptions.currentTimezone.code;
        }

        //校验是否有所修改
        function isChange(sendData) {
            let flag = false;

            if (scope.myOptions.isSupoortTimezone) {
                flag = scope.fieds.isDefaultTimezone != sendData.isDefaultTimezone;

                if (!flag) {
                    if (scope.fieds.dataTimezone && sendData.dataTimezone) {
                        flag = scope.fieds.dataTimezone.name != sendData.dataTimezone.name;
                    } else {
                        flag = scope.fieds.dataTimezone != sendData.dataTimezone;
                    }
                }
            }

            return flag;
        }

        //时区列表生成
        function getZones() {
            let zones = [];
            let names = moment.tz.names();
            
            timezoneNames.forEach((name) => {
                zones.push({
                    name: name,
                    code: moment.tz(name).format('Z')
                });
            })
            return zones;
        }

        //选择时区
        function selectTimezone(zone) {
            scope.myOptions.currentTimezone = angular.copy(zone);
            scope.myOptions.dropdownIsOpen = false;
        }

        //显示下拉框
        function dropdownToggle() {
            scope.search = '';
            searchChange()
        }

        //搜索框修改
        function searchChange(value) {
            let v = value ? {name: value} : '';
            scope.myOptions.dropdownOptions = $filter('filter')(scope.myOptions.timezoneList, v, false);
        }

        //显示弹出框
        function show() {
            jQuery('body').addClass('modal-open');
        }

        //隐藏弹出框
        function hide() {
            scope.hideTimezone();
            jQuery('body').removeClass('modal-open');
        }

        //显示弹出框Loading
        function showPopupLoading() {
            uiLoadingSrv.createLoading(jQuery('.pt-popup-content'));
        }

        //隐藏弹出框Loading
        function hidePopupLoading() {
            uiLoadingSrv.removeLoading(jQuery('.pt-popup-content'));
        }
    }
}

export default timezoneDirective;