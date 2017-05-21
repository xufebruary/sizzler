/**
 * Created by jianqing on 16/12/28.
 */

import resourcesModule from './resources/module';
import uiLoadModule from 'common/services/ui-load.module';
import uiJqModule from 'common/directives/ui-jq.module';
import uiValidateModule from 'common/directives/ui-validate.module';

import uiFullscreenDirective from 'common/directives/ui-full-screen.directive'; //全屏显示
import ptFullScreenDirective from 'common/directives/pt-full-screen.directive'; //全屏显示
import uiFocusDirective from 'common/directives/ui-focus.directive';
import compileHtmlDirective from 'common/directives/compile-html.directive';
import onRepeatFinishDirective from 'common/directives/on-repeat-finish.directive';
import onFinishRenderFiltersDirective from 'common/directives/on-finish-render-filters.directive';
import tableTitleTextOverDirective from 'common/directives/table-title-text-over.directive';
import stopScrollToFatherDirective from 'common/directives/stop-scroll-to-father.direttive';

import {dragDirective,dragHandleDirective} from 'common/directives/drag.directive';

import spaceCreateTipsDirective from 'common/directives/space-create-tips.directive'
import uiLoadingDirective from 'common/directives/ui-loading.directive'
import movableDirective from 'common/directives/movable.directive';
import {
	myVarDirective,
	ptLogFunc,
	uiWarningFunc,
	domSvgFunc,
	svgBgFunc,
	loginSvgBg,
	funcTips,
	worldHighFunc,
	chartTipsTrigger,
	dsAutoDateRate,
	uiInput} from 'common/directives/modules.directive';

import siteEventAnalyticsSrvFunc from 'common/services/track.factory';
import {getUserInfoFunc,sessionContextFunc} from 'common/services/user-info.factory';
import chartService from 'common/services/chart.service';
import uiSearchService from 'common/services/ui-search.service';
import {uiLoadingSrv,toggleLoadingSrv} from 'common/services/ui-loading.service';
import webSocketFunc from 'common/services/socket.factory';
import siteEventAnalyticsSrvFactory from 'common/services/site-event-analytics.factory';
import publicDataSrvFunc from 'common/services/public-data.factory';
import panelSltSrv from 'common/services/panel-select.service';
import loadAllWidgetData from 'common/services/load-all-widget-data.service';
import linkDataSrv from 'common/services/link-data.service';
import getWidgetListSrvFunc from 'common/services/get-widget-list.service';
import dataMutualSrvFunc from 'common/services/data-mutual.service';
import changeThemeSrvFunc from 'common/services/change-theme.factory';

export default angular.module('pt.commons', [resourcesModule.name,uiLoadModule.name,uiJqModule.name,uiValidateModule.name])
	.directive({
		'uiFullscreen': uiFullscreenDirective,
		'ptFullScreen': ptFullScreenDirective,
		'uiFocus':uiFocusDirective,
		'compile':compileHtmlDirective,
		'onRepeatFinish':onRepeatFinishDirective,
		'onFinishRenderFilters': onFinishRenderFiltersDirective,
		'tableTitleTextOver':tableTitleTextOverDirective,
		'stopScrollToFather':stopScrollToFatherDirective,
		'drag':dragDirective,
		'dragHandle':dragHandleDirective,
		'uiLoading':uiLoadingDirective,
		'movable': movableDirective,
		'myVar':myVarDirective,
		'ptLog':ptLogFunc,
		'uiWarning':uiWarningFunc,
		'domSvgFunc':domSvgFunc,
		'svgBg':svgBgFunc,
		'loginSvgBg':loginSvgBg,
		'funcTips':funcTips,
		'worldHigh':worldHighFunc,
		'chartTipsTrigger':chartTipsTrigger,
		'dsAutoDateRate':dsAutoDateRate,
		'uiInput':uiInput,
		'spaceAddTips':spaceCreateTipsDirective
	})
	.factory({
		'Track': siteEventAnalyticsSrvFunc,
		'getUserInfo':getUserInfoFunc,
		'sessionContext':sessionContextFunc,
		'websocket':webSocketFunc,
		'siteEventAnalyticsSrv':siteEventAnalyticsSrvFactory,
		'publicDataSrv': publicDataSrvFunc,
		'changeThemeSrv':changeThemeSrvFunc
	})
	.service({
		'chartService':chartService,
		'uiSearch':uiSearchService,
		'uiLoadingSrv':uiLoadingSrv,
		'toggleLoadingSrv':toggleLoadingSrv,
		'panelSltSrv':panelSltSrv,
		'loadAllWidgetData': loadAllWidgetData,
		'linkDataSrv':linkDataSrv,
		'getWidgetListSrv':getWidgetListSrvFunc,
		'dataMutualSrv':dataMutualSrvFunc
	});
