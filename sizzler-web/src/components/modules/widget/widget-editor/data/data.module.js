import dataPanelModule from './data-panel/data-panel.module';
import dimensionModule from './dimension/dimension.module';

export default angular.module('pt.widget-editor.data', [dataPanelModule.name, dimensionModule.name]);
