var Base64 = require('js-base64').Base64;

ForgotPasswordCtrl.$inject = ['siteEventAnalyticsSrv', 'UserResources'];

function ForgotPasswordCtrl(siteEventAnalyticsSrv, UserResources) {
	var vm = this;

	vm.state = {
		sendResult: null, //发送成功结果
		errorInfo: null, //发送失败信息
		sending: false //发送中状态
	};

	//发送邮件
	vm.sendEmail = function () {
		//重置错误提示
		vm.state.errorInfo = null;
		vm.form.email.$dirty = true;

		//check
		if (!vm.email) return;

		//发送
		vm.state.sending = true;
		UserResources.sendEmailToFindPassword(null, {
			email: vm.email,
			host: Base64.encode(window.location.host)
		})
		.then((data) => {
			vm.state.sendResult = true;
			vm.state.sendCode = "FORGOT_PASSWORD.TIP_2";

			//全站事件统计
            siteEventAnalyticsSrv.createData({
                where: "main_forgot_password",
                what: "reset_password",
                how: "click",
                value: vm.email
            });
		}, (error) => {
			vm.state.errorInfo = error.errorCode;
		})
		.finally(() => {
			vm.state.sending = false;
		});
	}

	//发送激活邮件
	vm.sendActivatedEmail = function(){
		UserResources.sendActivateEmail(null, {
			email: vm.email
		})
		.then((data) => {
			vm.state.sendResult = true;
			vm.state.sendCode = "FORGOT_PASSWORD.TIP_5";
		}, (error) => {
			vm.state.errorInfo = error.errorCode;
		})
		.finally(() => {
			vm.state.sending = false;
		});
	}
}

export default ForgotPasswordCtrl;
