import tpl from './panel-view-on-phone.html';
import viewOnPhoneController from './panel-view-on-phone.controller';
import './panel-view-on-phone.scss';

function viewOnPhoneDirective() {
	return {
		restrict: 'E',
		replace: true,
		template: tpl,
		scope: {
			currentPanelId: '<',
			spaceDomain: '<',
			viewOnPhoneStatus:'='
		},
		controller: viewOnPhoneController,
		controllerAs: '$ctrl',
		bindToController: true
	};
}

export default viewOnPhoneDirective;
