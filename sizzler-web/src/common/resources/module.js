/**
 * rest resources module
 */

import commonResources from './common.resources';
import userResources from './user.resources';
import spaceResources from './space.resources';
import widgetResources from './widget.resources';
import panelResources from './panel.resources';
import panelTempletsResources from './panelTemplets.resources';
import dataSourceResources from './dataSource.resources';

export default angular.module('pt.resources', [])
.service({
	'CommonResources': commonResources,
	'UserResources': userResources,
	'SpaceResources': spaceResources,
	'WidgetResources': widgetResources,
	'PanelResources': panelResources,
	'PanelTempletsResources': panelTempletsResources,
	'DataSourceResources': dataSourceResources
});
