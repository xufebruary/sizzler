import widgetDirective from './widget/widget.directive';
import widgetListDirective from './widget-list/widget-list.directive';
import widgetCopyDirective from './widget-copy/widget-copy.directive';
import widgetDeleteDirective from './widget-delete/widget-delete.directive';
import widgetDemoDirective from './widget-demo/widget-demo.directive';

import widgetServices from './widget.services';


export default angular.module('pt.widget', [])
.directive({
	'widget': widgetDirective,
	'widgetList': widgetListDirective,
	'widgetCopy': widgetCopyDirective,
	'widgetDelete': widgetDeleteDirective,
	'widgetDemo': widgetDemoDirective,
})
.service({
	'WidgetServices': widgetServices
})
