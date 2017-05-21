import tpl from './send-invite-email-dialog.html';
import sendInviteEmailController from './send-invite-mail.controller';


function sendInviteEmailDialogDirective() {
	return {
		restrict: 'E',
		replace: true,
		scope: {
			subtitle: '@', //副标题
			spaceId: '@', //空间id

			btnCancelClass: '@', //取消按钮样式
			btnCancelLocal: '@', //左侧按钮的文案
			onCancel: '&', //取消回调
			onSuccess: '&' //发送成功回调, 参数为emailList
		},
		template: tpl,
		controller: sendInviteEmailController,
		controllerAs: '$ctrl',
		bindToController: true
	};
}

export default sendInviteEmailDialogDirective;
