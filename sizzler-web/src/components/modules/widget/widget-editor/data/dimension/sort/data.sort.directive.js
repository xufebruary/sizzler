import tpl from './data.sort.html';
import dataSortController from './data.sort.controller';

function dataSortDirective(){
	return {
		restrict: 'E',
		replace: true,
		scope:{
			dimensions: '<',
			metrics: '<',
			defaultSorts: '<',

			onSubmit: '<',
			onCancel: '<'
		},
		template: tpl,
		controllerAs: '$ctrl',
		controller: dataSortController,
		bindToController: true
	};
}

export default dataSortDirective;
