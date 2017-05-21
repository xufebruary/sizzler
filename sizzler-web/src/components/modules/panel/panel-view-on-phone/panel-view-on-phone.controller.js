
import consts from 'configs/const.config';
import cookieUtils from 'utils/cookie.utils';

viewOnPhoneController.$inject = ['$scope','$location', '$document','PanelServices'];

function viewOnPhoneController($scope, $location, $document,PanelServices) {
	var vm = this;

	var sid = cookieUtils.get('sid');

	vm.state = {
		qrCodeUrl: undefined,
		shortenUrl: undefined
	};

	render();

	function render() {
		// 监听currentPanelId,发生变化时就请求
		$scope.$watch(function(){
			return vm.currentPanelId;
		}, function(value){
			if(value){
				// 获取shortUrl和qrCode url
				var url = location.protocol + "//" + location.host  +
					"/" + vm.spaceDomain + "/Dashboard?panelId=" + value;

				vm.state.qrCodeUrl = consts.WEB_MIDDLE_URL + '/pt/public/2d-bar-code?sid=' + sid + '&url=' + url;
				vm.state.shortenUrl = undefined;
				PanelServices.getShortenUrl(url).then(function(result){
					vm.state.shortenUrl = result;
				});
			}
		});
	}

}

export default viewOnPhoneController;
