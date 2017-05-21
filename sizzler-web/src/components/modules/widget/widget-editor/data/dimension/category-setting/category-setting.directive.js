import categorySettingController from './category-setting.controller';
import tpl from './category-setting.html';

function categorySettingDirective() {
	return {
		restrict: 'E',
		template: tpl,
		scope: {
			dsCode: '@',
			graphName: '@',
			dimensionIndex: '<',
			dimension: '<',

			onCancel: '<',
			onSubmit: '<'
		},
		controller: categorySettingController,
		controllerAs: '$ctrl',
		bindToController: true
	};
}

export default categorySettingDirective;
