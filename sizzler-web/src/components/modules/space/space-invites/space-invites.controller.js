import {
	getLocalLang
} from 'components/modules/common/common';

SpaceInvitesController.$inject = ['SpaceService', '$stateParams', '$state', 'sessionContext', '$timeout', 'uiLoadingSrv', '$translate'];

function SpaceInvitesController(SpaceService, $stateParams, $state, sessionContext, $timeout, uiLoadingSrv, $translate){

	var vm = this, inviteCode = $stateParams.invitesCode;

	// 错误信息
	vm.errors = {};

	// 初始化
	init();

	// 注册并接受邀请
	vm.signupAndAcceptInvite = signupAndAcceptInvite;

	vm.check = check;

	function init() {
		// 获取空间邀请码邀请信息
		SpaceService.getSpaceInviteInfo(inviteCode)
		.then((res) => {
			vm.info = res;
			if(res.type === 'signin'){
				$state.go("signin").then(() => {
					$translate.use(getLocalLang().locale);
				});
			}else if(res.type === 'dashboard'){
				sessionContext.saveSession(res.sid, 'invite');
			}else if(res.type === 'invalidate'){
				go2DefaultPage();
			}
		}, () => {
			go2DefaultPage();
		});
	}

	function go2DefaultPage() {
		//3s后跳转到默认页
		$timeout(function () {
			$state.go('home'); //默认页
		}, 3000);
	}

	function signupAndAcceptInvite() {

		// 表单校验
		this.check('userPassword');

		if(vm.errors.userPassword) return;

		// loading
		uiLoadingSrv.createLoading(angular.element('.forms'));

		if(vm.info.type == 'not_active'){
			SpaceService.activeAndAcceptSpaceInvitation({
				email: vm.info.userEmail,
				password: vm.userPassword,
				spaceId: vm.info.spaceId
			}, inviteCode).then((sid) => {
				// 跳转到dashboard
				sessionContext.saveSession(sid, 'invite');
			}).finally(() => {
				// remove loading
				uiLoadingSrv.removeLoading(angular.element('.forms'));
			});
		} else {
			SpaceService.signupAndAcceptSpaceInvitation({
				email: vm.info.userEmail,
				password: vm.userPassword,
				salesManager: vm.info.salesManager,
				spaceId: vm.info.spaceId
			}, inviteCode).then((sid) => {
				// 跳转到dashboard
				sessionContext.saveSession(sid, 'invite');
			}).finally(() => {
				// remove loading
				uiLoadingSrv.removeLoading(angular.element('.forms'));
			});
		}
	}

	function check(type) {
		if(!vm[type] || vm[type].length < 6 || vm[type].length > 20){
			vm.errors[type] = true;
		}else {
			vm.errors[type] = false;
		}
	}
}

export default SpaceInvitesController;
