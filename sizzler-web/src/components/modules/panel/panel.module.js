import panelServices from './panel.services';
import mobilePanelListDirective from '../mobile/panel-list/panel-list.directive';

import panelListDirective from './panel-list/panel-list.directive';
import panelAddDirective from './panel-add/panel-add.directive';
import panelCopyDirective from './panel-copy/panel-copy.directive';
import panelEditDirective from './panel-edit/panel-edit.directive';
import panelDeleteDirective from './panel-delete/panel-delete.directive';
import panelShareDirective from './panel-share/panel-share.directive';
import folderDeleteDirective from './folder-delete/folder-delete.directive';
import viewOnPhoneDirective from './panel-view-on-phone/panel-view-on-phone.directive';
import panelHeaderDirective from './panel-header/panel-header.directive';


export default angular.module('pt.panel', [])
.directive({
	'panelList': panelListDirective,
	'panelAdd': panelAddDirective,
	'panelCopy': panelCopyDirective,
	'panelEdit': panelEditDirective,
	'panelDelete': panelDeleteDirective,
	'panelShare': panelShareDirective,
	'folderDelete': folderDeleteDirective,
	'panelHeader': panelHeaderDirective,
	'mobilePanelList': mobilePanelListDirective,
	'viewOnPhone': viewOnPhoneDirective
})
.service({
	'PanelServices': panelServices
})
