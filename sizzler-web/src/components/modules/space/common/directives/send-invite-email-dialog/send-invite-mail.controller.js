
SendInviteMailController.$inject = ['SpaceService', 'uiLoadingSrv', '$translate','Track'];

function SendInviteMailController(SpaceService, uiLoadingSrv, $translate,Track) {
	var vm = this;

	vm.btnCancelClass = vm.btnCancelClass || 'pt-btn pt-btn-default';
	vm.btnCancelLocal = vm.btnCancelLocal || $translate.instant('COMMON.CANCEL');
	vm.state = {
		sendStatus: null, //发送状态
		emails: [], //要发送的邮件列表
		errors: {  // 存放表单校验错误的信息
			required: false
		}
	};

	// 发送邀请
	vm.inviteMembers = function () {
		vm.check();

		if(vm.state.errors.required) return;

		//调用接口
		uiLoadingSrv.createLoading(angular.element('.popup-content'));
		vm.state.sendStatus = 'sending';

		var emailList = vm.state.emails.map(({ text }) => text); //tags-input插件得到的数据格式为[{text: '123@123.com'}]

		Track.log({where: 'create_space', what: 'send_invites',value: vm.spaceId});

		SpaceService.inviteMembers(vm.spaceId, emailList)
		.then(() => {
			vm.state.sendStatus = 'success';
			// 执行回调
			vm.onSuccess({emailList: emailList});
		}, () => {
			vm.state.sendStatus = 'error';
		})
		.finally(() => {
			uiLoadingSrv.removeLoading(angular.element('.popup-content'));
		});

	};

	vm.check = function() {
		vm.state.errors.required = vm.state.emails.length === 0;
	};
}

export default SendInviteMailController;
