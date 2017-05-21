import ProductConfig from './product.config';
import { getLocalLang } from 'components/modules/common/common';
import zh_CN from '../assets/i18n/zh_CN.json';
import en_US from '../assets/i18n/en_US.json';
import ja_JP from '../assets/i18n/ja_JP.json';


var pt =
    angular.module('pt')
    //给rootscope设置需要根据域名动态展示值得内容
    .run(['$rootScope', function($rootScope) {
        $rootScope.productConfigs = ProductConfig;
    }])
    .config(['$controllerProvider', '$compileProvider', '$filterProvider', '$provide',
        function($controllerProvider, $compileProvider, $filterProvider, $provide) {

            // lazy controller, directive and service
            pt.controller = $controllerProvider.register;
            pt.directive = $compileProvider.directive;
            pt.filter = $filterProvider.register;
            pt.factory = $provide.factory;
            pt.service = $provide.service;
            pt.constant = $provide.constant;
            pt.value = $provide.value;
        }
    ])
    .config(['$translateProvider', function($translateProvider) {
        $translateProvider.translations('zh_CN', zh_CN);
        $translateProvider.translations('en_US', en_US);
        $translateProvider.translations('ja_JP', ja_JP);

        $translateProvider.preferredLanguage(getLocalLang().locale);

    }])
    .config(['$httpProvider', function($httpProvider) {
        $httpProvider.interceptors.push('UserInterceptor');

        // Override $http service's default transformRequest
        $httpProvider.defaults.transformRequest = [function(data) {
            /**
             * The workhorse; converts an object to x-www-form-urlencoded serialization.
             * @param {Object} obj
             * @return {String}
             */
            var param = function(obj) {
                var query = '';
                var name, value, fullSubName, subName, subValue, innerObj, i;

                for (name in obj) {
                    value = obj[name];

                    if (value instanceof Array) {
                        for (i = 0; i < value.length; ++i) {
                            subValue = value[i];
                            fullSubName = name + '[' + i + ']';
                            innerObj = {};
                            innerObj[fullSubName] = subValue;
                            query += param(innerObj) + '&';
                        }
                    } else if (value instanceof Object) {
                        for (subName in value) {
                            subValue = value[subName];
                            fullSubName = name + '[' + subName + ']';
                            innerObj = {};
                            innerObj[fullSubName] = subValue;
                            query += param(innerObj) + '&';
                        }
                    } else if (value !== undefined && value !== null) {
                        query += encodeURIComponent(name) + '=' + encodeURIComponent(value) + '&';
                    }
                }

                return query.length ? query.substr(0, query.length - 1) : query;
            };

            return angular.isObject(data) && String(data) !== '[object File]' ? param(data) : data;
        }];
    }]);
