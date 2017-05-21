import dataSourcesServices 		from './dataSources.services';

import tmpAccountDirective 		from './template/tmpAccount.directive';
import tmpJpAccountDirective 	from './template/tmpJpAccount.directive';
import tmpMysqlDirective 		from './template/tmpMysql.directive';
import timezoneDirective 		from './template/timezone/timezone.directive';


export default angular.module('pt.dataSources', [])
.directive({
	'tmpAccount': tmpAccountDirective,
	'tmpJpAccount': tmpJpAccountDirective,
	'tmpMysql': tmpMysqlDirective,
	'timezone': timezoneDirective,
})
.service({
	'DataSourcesServices': dataSourcesServices
})
