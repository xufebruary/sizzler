'use strict';

import cookieUtils from './utils/cookie.utils';
import {
	getLocalLang
} from 'components/modules/common/common';

/**
 * Config for the router
 */
angular.module('pt')
	.run(
		['$rootScope', '$state', '$stateParams',
			function ($rootScope, $state, $stateParams) {
				$rootScope.$state = $state;
				$rootScope.$stateParams = $stateParams;
			}
		]
	)
	.config(
		['$stateProvider', '$urlRouterProvider', '$locationProvider',
			function ($stateProvider, $urlRouterProvider, $locationProvider) {
				$urlRouterProvider.otherwise('/404');

				$stateProvider
					// home
					.state('home', {
						url: '/',
						controller: ['sessionContext', function (sessionContext) {
							sessionContext.getSession('home');
						}]
					})
					.state('default', {
						url: '/default',
						controller: ['sessionContext', function (sessionContext) {
							sessionContext.getSession('home');
						}]
					})
					//404
					.state('404', {
						url: '/404',
						controller: '404Ctrl',
						controllerAs: '$ctrl',
						bindToController: true,
						template: require('components/modules/404/404.html')
						// templateProvider: ['$q', function ($q) {
						// 	var deferred = $q.defer();
						// 	require.ensure([], function (require) {
						// 		var template = require('components/modules/404/404.html');
						// 		deferred.resolve(template);
						// 	});
						// 	return deferred.promise;
						// }],
						// resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
						// 	var deferred = $q.defer();
                        //
						// 	require.ensure([], function (require) {
						// 		var mainCtrl = require('components/modules/404/404.controller');
						// 		deferred.resolve(mainCtrl.controller);
						// 	});
                        //
						// 	return deferred.promise;
						// }]
					})
					//onboarding
					.state('onboarding', {
						url: '^/{spaceDomain}/Onboarding',
						reloadOnSearch: false,
						controller: 'onboardingCtrl',
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/onboarding/onboarding.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {


								var ctrl = require('components/modules/onboarding/onboarding.ctrl');


								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}],
						data: {
							title: 'Dashboard名称 | 空间名称'
						}
					})
					//onboarding-dataSource
					.state('onboarding.dataSource', {
						url: '^/{spaceDomain}/Onboarding/dataSource',
						reloadOnSearch: false,
						controller: 'dataSourceCtrl',
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/onboarding/dataSource/dataSource.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/onboarding/dataSource/dataSource.scss');
								var ctrl = require('components/modules/onboarding/dataSource/dataSource.ctrl');

								deferred.resolve(ctrl.controller);
							});

							console.log('onboarding.dataSource resolve...')

							return deferred.promise;
						}],
						data: {
							title: 'Dashboard名称 | 空间名称'
						}
					})
					//onboarding-preview
					.state('onboarding.preview', {
						url: '^/{spaceDomain}/Onboarding/Preview',
						reloadOnSearch: false,
						controller: 'previewCtrl',
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/onboarding/preview/preview.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('assets/css/modules/onboarding/preview/preview.css');
								require('components/modules/chart/chart-data.serv');

								//第三方插件
								require('assets/libs/angular/angular-gridstack/gridstack');
								require('assets/css/modules/widget/widget-list/gridstack.css');
								require('components/modules/widget/widget-list/gridstack.controller');
								require('components/modules/widget/widget-list/gridstack.directive');
								require('components/modules/widget/widget-list/gridstackitem.directive');
								require('components/modules/widget/widget-list/gridstackSrc');

								//chart
								require('components/modules/chart/chart.dire');
								require('components/modules/chart/chart-data.serv');
								// require('components/modules/chart/chart.ctrl');
								require('components/modules/chart/chart-highchart/highchart.l18n');
								require('components/modules/chart/chart-highchart/chart-highchart.dire');
								require('components/modules/chart/chart-highchart/chart-highchart.srv');
								require('components/modules/chart/chart-highmaps/chart-highmaps.dire');
								require('components/modules/chart/chart-number/chart-number.dire');
								require('components/modules/chart/chart-progressbars/chart-progressbars.dire');
								require('components/modules/chart/chart-pttable/chart-pttable.dire');
								require('components/modules/chart/chart-pttable/chart-pttable.util');
								require('components/modules/chart/chart-simplenumber/chart-simplenumber.dire');


								var ctrl = require('components/modules/onboarding/preview/preview.ctrl');

								$ocLazyLoad.load([{
									name: 'gridstack-angular'
								}]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}],
						data: {
							title: 'Dashboard名称 | 空间名称'
						}
					})
					//onboarding-preview
					.state('onboarding.connect', {
						url: '^/{spaceDomain}/Onboarding/Connect',
						reloadOnSearch: false,
						controller: 'connectCtrl',
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/onboarding/connect/connect.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/onboarding/connect/connect.scss');

								//第三方插件
								require('assets/libs/angular/angular-ngscrollbar/ngscrollbar');

								var ctrl = require('components/modules/onboarding/connect/connect.ctrl');

								$ocLazyLoad.load([{
									name: 'widget.scrollbar'
								}]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}],
						data: {
							title: 'Dashboard名称 | 空间名称'
						}
					})
					//main
					.state('pt', {
						//abstract: true,
						url: '/{spaceDomain}/pt',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/partial/main/main.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'mainCtrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							console.log('resolve pt')
							var deferred = $q.defer();

							require.ensure([], function (require) {
								var ctrl = require('components/partial/main/main.ctrl.js');
								require('components/modules/chart/chart-data.serv');
								require('components/modules/tips/tips-twoBtn.dire');

								//第三方插件
								require('angular-bindonce');
								require('ngstorage');
								require('angular-file-upload');
								require('assets/libs/angular/angular-ngscrollbar/ngscrollbar');
								require('assets/libs/angular/angular-bootstrap/ui-bootstrap-tpls-0.13.0');
								require('jquery-ui');
								require('lodash');

								$ocLazyLoad.load([{
									name: 'ngStorage'
								}, {
									name: 'angularFileUpload'
								}, {
									name: 'widget.scrollbar'
								}, {
									name: 'ui.bootstrap'
								}, {
									name: 'pasvaz.bindonce'
								}]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]
					})
					//dashboard list
					.state('pt.dashboard', {
						url: '^/{spaceDomain}/Dashboard?UID&panelId',
						reloadOnSearch: false,
						controller: 'dashboardCtrl',
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/partial/dashboard/dashboard.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						resolve: {
							init: ['$q', '$ocLazyLoad', 'SpaceResources', '$stateParams', '$location', '$state', function ($q, $ocLazyLoad, SpaceResources, $stateParams, $location, $state) {
								console.log('pt.dashboard resolve')

								var deferred = $q.defer();
								// 校验空间和面板是否合法
								var domain = $stateParams.spaceDomain,
									panelId = $location.search().panelId;
								return SpaceResources.checkHasPermissionOfSpace({
									domain: domain,
									panelId: panelId
								}).then(function (status) {
									if (status === 'available') {
										return $q.resolve();
									} else if (status === 'spaceNoAuth') { // 没权限
										$state.go('dashboardNoPermission', {
											redirectUrl: encodeURIComponent(location.href),
											spaceDomain: domain
										});
										return $q.reject();
									} else { // 空间不存在或者panelId非法,跳转到错误页面
										$state.go('404');
										return $q.reject();
									}
								}).then(function () {
									require.ensure([], function (require) {
										//dashboard controller
										var ctrl = require('components/partial/dashboard/dashboard.ctrl');

										//第三方插件
										require('angular-drag-and-drop-lists');
										require('assets/libs/angular/angular-gridstack/gridstack');
										require('components/modules/widget/widget-list/gridstack.scss');
										require('components/modules/widget/widget-list/gridstack.controller');
										require('components/modules/widget/widget-list/gridstack.directive');
										require('components/modules/widget/widget-list/gridstackitem.directive');
										require('assets/libs/angular/angular-rzslider/rzslider.css');
										require('assets/libs/angular/angular-rzslider/rzslider');

										//widget
										require('components/modules/widget/widget-list/gridstackSrc');
										require('components/modules/widget/widget-download/widget-download.util');
										require('components/modules/widget/widget-input/widget-input.dire');

										//chart
										require('components/modules/chart/chart.dire');
										require('components/modules/chart/chart-data.serv');
										require('components/modules/chart/chart-highchart/highchart.l18n');
										require('components/modules/chart/chart-highchart/chart-highchart.dire');
										require('components/modules/chart/chart-highchart/chart-highchart.srv');
										require('components/modules/chart/chart-highmaps/chart-highmaps.dire');
										require('components/modules/chart/chart-number/chart-number.dire');
										require('components/modules/chart/chart-progressbars/chart-progressbars.dire');
										require('components/modules/chart/chart-pttable/chart-pttable.dire');
										require('components/modules/chart/chart-pttable/chart-pttable.util');
										require('components/modules/chart/chart-simplenumber/chart-simplenumber.dire');
										require('components/modules/chart/chart-heatmap/chart-heatmap.directive');
										require('components/modules/chart/chart-heatmap/chart-heatmap.scss');
										require('components/modules/chart/chart-richtext/chart-richtext.directive');
										require('components/modules/chart/chart-richtext/chart-richtext.scss');
										require('components/modules/widget/link-data/link-data.dire');

										//css
										require('components/partial/dashboard/dashboard.css');

										//other directives
										require('components/partial/dashboard/dashboard-time.dire');

										//pt module
										var widgetEditorModule = require('components/modules/widget/widget-editor/widget-editor.module').default;
										var widgetModule = require('components/modules/widget/widget.module').default;
										var panelModule = require('components/modules/panel/panel.module').default;

										$ocLazyLoad.load([{
											name: 'dndLists'
										}, {
											name: 'gridstack-angular'
										}, {
											name: 'rzModule'
										}, {
											name: widgetEditorModule.name
										}, {
											name: widgetModule.name
										}, {
											name: panelModule.name
										}]);

										deferred.resolve(ctrl.controller);
									});
									return deferred.promise;
								});
							}]
						},
						data: {
							title: 'Dashboard名称 | 空间名称'
						}
					})
					// 没权限访问提示页面
					.state('dashboardNoPermission', {
						url: '/no-permission?redirectUrl&spaceDomain',
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/partial/dashboard/no-permission.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'DashboardNoPermissionCtrl',
						controllerAs: '$ctrl',
						bindToController: true,
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var ctrl = require('components/partial/dashboard/no-permission.controller');
								deferred.resolve(ctrl.controller);
							});
							return deferred.promise;
						}]
					})
					//create widget gallery
					.state('pt.dashboard.widgetGallery', {
						url: '/widgetGallery',
						reloadOnSearch: false,
						controller: 'widgetGalleryCtrl',
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/widget/widget-gallery/widget-gallery.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('assets/css/modules/widget/widget-gallery/widget-gallery.css');
								var ctrl = require('components/modules/widget/widget-gallery/widget-gallery.ctrl');

								$ocLazyLoad.load([
									ctrl
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}],
						data: {
							title: 'create widget gallery'
						}
					})
					//space create
					.state('spaceCreate', {
						parent: 'pt',
						url: '^/create',
						reloadOnSearch: false,
						controller: 'SpaceCreateCtrl',
						controllerAs: '$ctrl',
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/space/space-create/space-create.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var mod = require('components/modules/space/space.module').default;
								$ocLazyLoad.load({name: mod.name});
								deferred.resolve(mod.controller);
							});

							return deferred.promise;
						}],
						data: {
							title: 'create space'
						}
					})
					//dashboard template
					.state('pt.dashboardTemplate', {
						url: '^/{spaceDomain}/Template?UID',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/panel/panel-template/panel-template.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'PanelTemplateController',
						controllerAs: '$ctrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/panel/panel-template/panel-template.css');
								var ctrl = require('components/modules/panel/panel-template/panel-template.controller');

								$ocLazyLoad.load([
									ctrl
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]
					})
					// widget template manager
					.state('pt.templates', {
						url: '/templates/',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/template/widget/template-widget.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'widgetTemplatesCtrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/template/widget/widget_publish.dire');
								var ctrl = require('components/modules/template/widget/template-widget.ctrl');

								$ocLazyLoad.load([
									ctrl
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]
					})
					// space settings
					.state('pt.spaceSettings', {
						url: '^/{spaceDomain}/SpaceSettings?UID',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/space/space-settings/space-settings.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'SpaceSettingsCtrl',
						controllerAs: '$ctrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								var mod = require('components/modules/space/space.module').default;
								$ocLazyLoad.load({name: mod.name});
								deferred.resolve(mod.controller);
							});

							return deferred.promise;
						}]
					})
					// space member
					.state('pt.spaceMember', {
						url: '^/{spaceDomain}/SpaceMember?UID',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/space/space-member/space-member.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'SpaceMemberCtrl',
						controllerAs: '$ctrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								var mod = require('components/modules/space/space.module').default;
								$ocLazyLoad.load({name: mod.name});
								deferred.resolve(mod.controller);
							});

							return deferred.promise;
						}]
					})
					// user settings
					.state('pt.settings', {
						url: '/settings?UID',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/settings/settings.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'settingsCtrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/dataSources/template/template.css');
								var ctrl = require('components/modules/settings/settings.ctrl.js');
								require('components/modules/settings/settings.css');

								$ocLazyLoad.load([
									ctrl
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]
					})
					// data sources
					.state('pt.dataSources', {
						url: '^/{spaceDomain}/DataSources?UID',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/dataSources.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'dataSourcesCtrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								var ctrl = require('components/modules/dataSources/dataSources.ctrl');
								var dataSourcesModule = require('components/modules/dataSources/dataSources.module').default;
								require('components/modules/dataSources/dataSources.css');

								$ocLazyLoad.load([
									{
										name: dataSourcesModule.name
									}
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]
					})
					// data sources -- edit
					.state('pt.dataSources.editor', {
						url: '/editor',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/edit/edit.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'dsEditorCtrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								var ctrl = require('components/modules/dataSources/edit/edit.ctrl');
								require('components/modules/dataSources/edit/edit.css');

								$ocLazyLoad.load([
									ctrl
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]

					})
					// data sources -- googleanalysis
					.state('pt.dataSources.googleanalysis', {
						url: '/googleanalysis',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/ga/ga.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}]
					})
					// data sources -- googledrive
					.state('pt.dataSources.googledrive', {
						url: '/googledrive',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/gd/gd.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'dsGdCtrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								var ctrl = require('components/modules/dataSources/gd/gd.ctrl');
								require('components/modules/dataSources/gd/gd.css');

								$ocLazyLoad.load([
									ctrl
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]
					})
					.state('pt.dataSources.s3', {
						url: '/s3',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/s3/s3.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'dsS3Ctrl',
						permission: 'datasource-s3-view',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								var ctrl = require('components/modules/dataSources/s3/s3.ctrl');
								require('components/modules/dataSources/s3/s3.css');

								$ocLazyLoad.load([
									ctrl
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]
					})
					// data sources -- mysql
					.state('pt.dataSources.mysql', {
						url: '/mysql',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/mysql/mysql.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}]
					})
					// data sources -- MYSQL RDS
					.state('pt.dataSources.mysqlAmazonRds', {
						url: '/mysqlAmazonRds',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/mysqlAmazonRds/mysqlAmazonRds.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}]
					})
					// data sources -- MYSQL Remote
					.state('pt.dataSources.auroraAmazonRds', {
						url: '/auroraAmazonRds',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/auroraAmazonRds/auroraAmazonRds.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}]
					})
					// data sources -- postgre
					.state('pt.dataSources.postgre', {
						url: '/postgre',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/mysqlPostgre/postgre.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}]
					})
					// data sources -- standardRedshift
					.state('pt.dataSources.standardRedshift', {
						url: '/standardRedshift',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/standardRedshift/standardRedshift.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-standardRedshift-view'
					})
					// data sources -- redshift
					.state('pt.dataSources.redshift', {
						url: '/redshift',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/mysqlRedshift/redshift.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}]
					})
					// data sources -- googleadwords
					.state('pt.dataSources.googleadwords', {
						url: '/googleadwords',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/googleadwords/googleadwords.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}]
					})
					// data sources -- facebook
					.state('pt.dataSources.facebook', {
						url: '/facebook',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/facebook/facebook.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}]
					})
					// data sources -- facebookads
					.state('pt.dataSources.facebookad', {
						url: '/facebookad',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/facebookad/facebookad.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}]
					})
					// data sources -- ptapp
					.state('pt.dataSources.ptapp', {
						url: '/ptapp',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/ptapp/ptapp.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-ptapp-view'
					})
					// data sources -- ptengine
					.state('pt.dataSources.ptengine', {
						url: '/ptengine',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/ptengine/ptengine.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-ptengine-view'
					})
					// data sources -- bigquery
					.state('pt.dataSources.bigquery', {
						url: '/bigquery',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/bigquery/bigquery.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}]
					})
					// data sources -- upload
					.state('pt.dataSources.upload', {
						url: '/upload',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/upload/upload.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'dsUploadCtrl',
						permission: 'datasource-upload-view',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								var ctrl = require('components/modules/dataSources/upload/upload.ctrl');
								require('components/modules/dataSources/upload/upload.css');
								$ocLazyLoad.load([
									ctrl
								]);
								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]

					})
					// data sources -- doubleclick
					.state('pt.dataSources.doubleclick', {
						url: '/doubleclick',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/doubleclick/doubleclick.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-doubleclick-view'
					})
					// data sources -- doubleclick
					.state('pt.dataSources.doubleclickCompound', {
						url: '/doubleclickCompound',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/doubleclickCompound/doubleclickCompound.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-doubleclickCompound-view'
					})
					// data sources -- salesforce
					.state('pt.dataSources.salesforce', {
						url: '/salesforce',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/salesforce/salesforce.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-salesforce-view'
					})
					// data sources -- paypal
					.state('pt.dataSources.paypal', {
						url: '/paypal',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/paypal/paypal.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-paypal-view'
					})
					// data sources -- stripe
					.state('pt.dataSources.stripe', {
						url: '/stripe',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/stripe/stripe.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-stripe-view'
					})
					// data sources -- googleadsense
					.state('pt.dataSources.googleadsense', {
						url: '/googleadsense',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/googleadsense/googleadsense.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-googleadsense-view'
					})
					// data sources -- mailchimp
					.state('pt.dataSources.mailchimp', {
						url: '/mailchimp',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/mailchimp/mailchimp.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-mailchimp-view'
					})
					// data sources -- facebookPages
					.state('pt.dataSources.facebookPages', {
						url: '/facebookPages',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/facebookPages/facebookPages.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-facebookPages-view'
					})
					// data sources -- yahoo ads ydn
					.state('pt.dataSources.yahooAdsYDN', {
						url: '/yahooAdsYDN',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/yahooAdsYDN/yahooAdsYDN.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-yahooAdsYDN-view'
					})
					// data sources -- yahoo ads yahooAdsSS
					.state('pt.dataSources.yahooAdsSS', {
						url: '/yahooAdsSS',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/yahooAdsSS/yahooAdsSS.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-yahooAdsSS-view'
					})
					// data sources -- sqlserver
					.state('pt.dataSources.sqlserver', {
						url: '/sqlserver',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/dataSources/sqlserver/sqlserver.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						permission: 'datasource-sqlserver-view'
					})
					.state('signin', {
						url: '/signin?community&redirectUrl',
						params: {type: null},
						reloadOnSearch: false,
						templateProvider: ['$q', '$translate', function ($q, $translate) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/signin/signin.tpl.html');

								// 登录页展示前要取一下应该显示的语言
								$translate.use(getLocalLang().locale);

								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'signinCtrl',
						resolve: {
							load: ['sessionContext', '$q', function (sessionContext, $q) {
								var deferred = $q.defer();

								// 需要判断当前是否是已登录状态
								var sid = cookieUtils.get('sid');
								if (sid) {
									sessionContext.saveSession(sid, 'signin');
								} else {
									require.ensure([], function (require) {
										require('components/modules/signin/signin.css');
										var ctrl = require('components/modules/signin/signin.ctrl');

										deferred.resolve(ctrl.controller);
									});
								}

								return deferred.promise;
							}]
						}
					})
					.state('ptengineSignin', {
						url: '/signin/ptengine?spaceid',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/signin/ptengine/signin.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'signinPtengineController',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/signin/signin.css');
								var signinCtrl = require('components/modules/signin/ptengine/login.ctrl');

								$ocLazyLoad.load([
									signinCtrl
								]);

								deferred.resolve(signinCtrl.controller);
							});

							return deferred.promise;
						}]
					})
					.state('ptappSignin', {
						url: '/signin/ptapp?spaceid',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/signin/ptapp/signin.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'signinPtappController',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/signin/signin.css');
								var signinCtrl = require('components/modules/signin/ptapp/login.ctrl');

								$ocLazyLoad.load([
									signinCtrl
								]);

								deferred.resolve(signinCtrl.controller);
							});

							return deferred.promise;
						}]
					})
					.state('signupOfficial', {
						url: '/signupOfficial',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/signup/signup.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'signupCtrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/signin/signin.css');
								var signupCtrl = require('components/modules/signup/signup.ctrl');

								$ocLazyLoad.load([
									signupCtrl
								]);

								deferred.resolve(signupCtrl.controller);
							});

							return deferred.promise;
						}]
					})
					.state('forgot', {
						url: '/forgotPassword',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/forgotPassword/forgotPassword.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'ForgotPwdCtrl',
						controllerAs: '$ctrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								var mod = require('components/modules/forgotPassword/forgotPassword.module').default;
								$ocLazyLoad.load({
									name: mod.name
								});
								deferred.resolve(mod.controller);
							});

							return deferred.promise;
						}]
					})
					.state('resetPassword', {
						url: '/resetPassword',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/resetPassword/resetPassword.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'resetPwdCtrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/signin/signin.css');
								var ctrl = require('components/modules/resetPassword/resetPassword.ctrl');

								$ocLazyLoad.load([
									ctrl
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]
					})
					//创建账户
					.state('accountCreate', {
						url: '/create-password',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/account/account-create/account-create.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'accountCreateCtrl',
						controllerAs: '$ctrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/signin/signin.css');
								require('components/modules/account/account-create/account-create.scss');

								var ctrl = require('components/modules/account/account-create/account-create.ctrl');

								$ocLazyLoad.load([
									ctrl
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]
					})
					//创建账户成功提示(移动端)
					.state('accountCreateSuccess', {
						url: '^/{spaceDomain}/create-password-success',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/account/account-create-success/account-create-success.tpl.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'accountCreateSuccessCtrl',
						controllerAs: '$ctrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/signin/signin.css');
								require('components/modules/account/account-create-success/account-create-success.scss');

								var ctrl = require('components/modules/account/account-create-success/account-create-success.ctrl');

								$ocLazyLoad.load([
									ctrl
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}]
					})
					.state('landingPage', {
						url: '/landingPage',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/landingPage/landing-page.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'landingPageController',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								require('components/modules/signin/signin.css');
								require('components/modules/landingPage/twitter_widgets');
								require('components/modules/landingPage/vector');
								require('components/modules/landingPage/landing-page.css');
								require('components/modules/landingPage/landing-page-success.css');

								var ctrl = require('components/modules/landingPage/landing-page.ctrl');
								$ocLazyLoad.load([
									ctrl
								]);

								deferred.resolve(ctrl.controller);
							});

							return deferred.promise;
						}],
						data: {
							title: 'BIより使いやすい、仕事をする人のダッシュボード'
						}
					})
					.state('preRegistrationSuccess', {
						url: '/preRegistrationSuccess?from',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/landingPage/pre-registration-success.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'landingPageController',
						resolve: {
							deps: ['$ocLazyLoad',
								function ($ocLazyLoad) {
									return $ocLazyLoad.load([
										'/components/modules/signin/signin.css?v=' + BASE_VERSION,
										'/components/modules/landingPage/landing-page.ctrl.js?v=' + BASE_VERSION,
										'/components/modules/landingPage/twitter_widgets.js',
										'/components/modules/landingPage/vector.js'
									]);
								}
							]
						},
						data: {
							title: 'BIより使いやすい、仕事をする人のダッシュボード'
						}
					})
					//invites
					.state('invites', {
						url: '/Invites/{invitesCode}',
						reloadOnSearch: false,
						templateProvider: ['$q', function ($q) {
							var deferred = $q.defer();
							require.ensure([], function (require) {
								var template = require('components/modules/space/space-invites/space-invites.html');
								deferred.resolve(template);
							});
							return deferred.promise;
						}],
						controller: 'SpaceInviteCtrl',
						controllerAs: '$ctrl',
						resolve: ['$q', '$ocLazyLoad', function ($q, $ocLazyLoad) {
							var deferred = $q.defer();

							require.ensure([], function (require) {
								var mod = require('components/modules/space/space.module').default;

								$ocLazyLoad.load({name: mod.name});

								deferred.resolve(mod.controller);
							});

							return deferred.promise;
						}]
					});
				// use the HTML5 History API
				//$locationProvider.html5Mode(true);
				$locationProvider.html5Mode({
					enabled: true,
					requireBase: false,
					rewriteLinks: false
				});
			}
		]
	);
