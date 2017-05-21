import dataSortDirective from './data.sort.directive';
import dataSortService from './data.sort.service';

export default angular.module('pt.widget-editor.data.dimension.sort', [])
	.service({
		'DataSortService': dataSortService
	})
	.directive({
		'widgetEditorDimensionDataSort': dataSortDirective
	});
