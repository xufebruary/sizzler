
errorController.$inject = ['toggleLoadingSrv', '$state'];
import './404.scss';

function errorController(toggleLoadingSrv, $state){

	var vm = this;

	render();

	function render() {
		toggleLoadingSrv.hide('body');
	}

	vm.goToDefaultPage = function () {
		$state.go('home'); // 跳转到根路径
	};

	vm.callIntercom = callIntercom;
	/**
	 * 调用对话框插件(Intercom)
	 */
	function callIntercom() {
		
	}
}

angular.module('pt').controller('404Ctrl', errorController);
