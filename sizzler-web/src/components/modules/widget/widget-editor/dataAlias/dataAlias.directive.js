"use strict";


/**
 * dataAlias
 * 自定义指标、维度名称
 *
 * created by ao at 20170105
 */

import tpl from './dataAlias.html';
import './dataAlias.scss';

dataAliasDirective.$inject = ['uiLoadingSrv', 'WidgetServices', 'Track'];

function dataAliasDirective(uiLoadingSrv, WidgetServices, Track) {
    return {
        restrict: 'EA',
        replace: true,
        scope: {
            currentWidgetId: "<",
            currentDataName: "<",
            currentDataAlias: "<",
            currentDataType: "<",
            currentDataUuid: "<",
            onApply: "&",
            onCancel: "&"
        },
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {
        scope.myOptions = {
            aliasModel: scope.currentDataAlias || ''
        }

        scope.apply = function() {
            var alias = (scope.myOptions.aliasModel).trim().slice(0, 50);
            var sendData = {
                widgetId: scope.currentWidgetId,
                type: scope.currentDataType,
                uuid: scope.currentDataUuid,
                alias: alias
            };

            uiLoadingSrv.createLoading('.data-alias');
            WidgetServices.alias(sendData)
                .then((data) => {
                    scope.onApply({ "alias": alias });
                })
                .finally(() => {
                    uiLoadingSrv.removeLoading('.data-alias');

                    //全站事件统计
                    Track.log({where: 'widget_editor_data_alias', what: 'update_alias', value: scope.myOptions.aliasModel});
                })
        }
    }
}

export default dataAliasDirective;


/**********************************
 
    影响范围：
 
    ## 指标、维度名称修改 ##
    ## chart 左下角提示修改 ##
    ## chart 指标、维度显示名称添加悬浮提示 ##
    ## widget标题修改 ##
    ## 所有图形上的显示修改（tooltips，legend）##
    ## 隐藏计算方式功能，页面上的判断去除 ##
    ## 下载数据中的名称同步(除表格之外的下载请求) ##
    ## admin同步
    ## onboarding 预览时的判断 ##
    ## demo数据名称同步 ##
    ## 分享 ##

**********************************/