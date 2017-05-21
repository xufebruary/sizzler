// config

import ProductConfig from 'configs/product.config';
import {getLocalLang} from 'components/modules/common/common';
import zh_CN from '../../../assets/i18n/zh_CN.json';
import en_US from '../../../assets/i18n/en_US.json';
import ja_JP from '../../../assets/i18n/ja_JP.json';

var pt =
	angular.module('pt')
	//给rootscope设置需要根据域名动态展示值得内容
	.run(['$rootScope', function ($rootScope) {
		$rootScope.productConfigs = ProductConfig;
	}])
	.config(
		['$controllerProvider', '$compileProvider', '$filterProvider', '$provide',
			function ($controllerProvider, $compileProvider, $filterProvider, $provide) {

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
	.config(
		['$translateProvider',
			function ($translateProvider) {
				$translateProvider.translations('zh_CN', zh_CN);
				$translateProvider.translations('en_US', en_US);
				$translateProvider.translations('ja_JP', ja_JP);

				$translateProvider.preferredLanguage(getLocalLang().locale);
			}
		])
	;

