'use strict';

/**
 * uiSearch
 * 搜索下拉
 *
 */
//angular
//    .module('pt')
//    .service('uiSearch', ['$translate', uiSearch]);
uiSearchService.$inject = ['$translate'];
function uiSearchService($translate) {

    /**
     * searchData
     * 搜索列表数据处理(现最多循环查询3级)
     *
     * @param key-搜索关键字
     * @param tier-原始数据层级数目
     * @param tierData-层级列表[]
     * @param dataList-原始数据
     * @param backFun-回调函数
     * @param collapseFun-过滤后子层是否收起显示(在2级以上列表使用)
     *
     */
    this.search = function (key, tier, tierData, dataList, backFun, collapseFun) {
        var reg = new RegExp('.*?' + $.regTrim(key) + '.*?', "i");
        var queryData;
        var copyData = angular.copy(dataList);

        if (!key) {
            queryData = dataList;

            if (tier > 1) {
                collapseFun(false);
            } else if (tier == 1) {
                collapseFun();
            }
        } else {
            if (tier > 1) {
                collapseFun(true);
            } else if (tier == 1) {
                collapseFun();
            }

            for (var i = copyData.length - 1; i >= 0; i--) {
                var dataI = copyData[i];

                if (tier == 1) {
                    //层级为1
                    if (!reg.test(dataI.name)) {
                        copyData.splice(i, 1);
                    }
                } else if (tier == 2) {
                    //层级为2
                    var tierI = tierData[0];
                    var flagI = true;
                    var nameFlagI = reg.test(dataI.name);
                    var copyItemI = angular.copy(dataI);

                    for (var j = dataI[tierI].length - 1; j >= 0; j--) {
                        var dataJ = dataI[tierI];

                        if (!reg.test(dataJ[j].name)) {
                            dataI[tierI].splice(j, 1);
                        } else {
                            flagI = false;
                        }
                    }

                    if (flagI && !nameFlagI) {
                        copyData.splice(i, 1);
                    }
                    //复原删除的数据
                    if (flagI && nameFlagI) {
                        copyData[i] = copyItemI;
                    }
                } else if (tier == 3) {
                    //层级为3
                    var tierI = tierData[0];
                    var flagI = true;//第二层是否需要删除
                    var flagL = true;//第三层是否需要删除
                    var nameFlagI = reg.test(dataI.name);//第一层的名字是否匹配
                    for (var j = dataI[tierI].length - 1; j >= 0; j--) {
                        var dataJ = dataI[tierI][j];
                        var tierJ = tierData[1];
                        var nameFlagJ = reg.test(dataJ.name);//第二层的名字是否匹配
                        var flagK = false;//标记第三层有没有匹配的
                        for (var k = dataJ[tierJ].length - 1; k >= 0; k--) {
                            if (!reg.test(dataJ[tierJ][k].name) && !nameFlagJ && !nameFlagI) {//如果第一层第二层不匹配并且第三层的第k个不匹配，删掉这第k个第三层
                                dataJ[tierJ].splice(k, 1);
                            } else {
                                flagK = true;
                                flagL = false;
                            }
                        }
                        if (!nameFlagJ && !flagK && !nameFlagI) {//第一层第二层不匹配，第三层全不匹配，删掉整个第二层
                            dataI[tierI].splice(j, 1);
                        }
                        if (nameFlagJ || flagK) {//第三层有一个匹配，或者第二层匹配，需要恢复第二层
                            flagI = false;
                        }
                    }
                    if (!nameFlagI && flagI && flagL) {//如果第一层不匹配，第二层不匹配，第三层也不匹配，删除第一层
                        copyData.splice(i, 1);
                    }
                }
            }
            queryData = copyData;
        }
        backFun(queryData);
    };


    /**
     * nameI18n
     * 数据名称国际化(一般用在GA的维度或指标)
     *
     * @param tier-数据层级数目
     * @param tierData-层级列表[]
     * @param i18nData-需要国际化的字段
     * @param i18nCode-国际化配置code字段
     * @param dataList-原始数据
     *
     */
    this.nameI18n = function (tier, tierData, dataList, i18nData, i18nCode) {
        var copyData = angular.copy(dataList);

        for (var i = copyData.length - 1; i >= 0; i--) {
            var dataI = copyData[i];
            var tierI = tierData[0];
            dataI[i18nData] = $translate.instant(dataI[i18nCode]);

            if (dataI[tierI] && dataI[tierI].length) {
                for (var j = dataI[tierI].length - 1; j >= 0; j--) {
                    var dataJ = dataI[tierI][j];
                    var tierJ = tierData[0];

                    dataJ[i18nData] = $translate.instant(dataJ[i18nCode]);
                }
            }
        }

        return copyData;
    }
}
export default uiSearchService;
