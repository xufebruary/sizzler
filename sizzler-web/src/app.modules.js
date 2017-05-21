'use strict';

import angular from 'angular';
import 'angular-ui-router';
import 'oclazyload';
import 'angular-sanitize';
import 'angular-cookies';
import 'angular-translate';
import 'common/common.module';
import 'angular-messages';
import 'search-list';
import 'assets/libs/search-list/search-list.scss';

export default angular.module('pt', [
	'ui.router',
	'pascalprecht.translate',
	'ngSanitize',
	'oc.lazyLoad',
	'ngCookies',
	'ui.validate',
	'ngMessages',
	'pt.search-list',
	'pt.commons'
]);
