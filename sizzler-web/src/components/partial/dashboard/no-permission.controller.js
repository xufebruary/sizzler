import {
	LINK_SIGNOUT_URL
} from 'components/modules/common/common';

noPermissionController.$inject = ['$scope', '$state', '$stateParams', 'toggleLoadingSrv', 'dataMutualSrv', 'publicDataSrv', 'UserResources', 'SpaceResources','$timeout'];

function noPermissionController($scope, $state, $stateParams, toggleLoadingSrv, dataMutualSrv, publicDataSrv, UserResources, SpaceResources,$timeout) {
	var vm = this;

	vm.state = {
		spaceDomain: null
	};

	render();

	vm.switchAccount = function () {
		var redirectUrl = $stateParams.redirectUrl;
		dataMutualSrv.post(LINK_SIGNOUT_URL);
		UserResources.clear();
		$scope.changeLogo();
		publicDataSrv.clearPublicData('all');
		$state.go('signin', {redirectUrl: redirectUrl});
	};

	vm.viewCurrentAccount = function () {
		$state.go('home');
	};

	function render() {
		toggleLoadingSrv.hide('body');

		SpaceResources.getSpaceNameByDomain(null, {spaceDomain: $stateParams.spaceDomain})
			.then(function (result) {
				vm.state.spaceDomain = result.name;
			}, function () {
				vm.state.spaceDomain = $stateParams.spaceDomain;
			});
	}
}

angular.module('pt').controller('DashboardNoPermissionCtrl', noPermissionController);
