import {
	LINK_USER_FORGOT_URL,
	uuid,
	loginSvg
} from 'components/modules/common/common';

var Base64 = require('js-base64').Base64;

ForgotPasswordService.$inject = ['UserResources', '$q', 'siteEventAnalyticsSrv'];

function ForgotPasswordService(UserResources, promise, siteEventAnalyticsSrv){
	this.UserResources = UserResources;
	this.promise = promise;
	this.siteEventAnalyticsSrv = siteEventAnalyticsSrv;
}

ForgotPasswordService.prototype = {
	constructor: ForgotPasswordService,
	/**
	 * 忘记密码发送邮件
	 * @param email
	 * @returns {*}
   	*/
	sendEmail: function(email){
		return this.UserResources.sendEmailToFindPassword(null, {
			email: email,
			host: Base64.encode(window.location.host)
		})
		.then((data) => {

			//全站事件统计
            this.siteEventAnalyticsSrv.createData({
                where: "main_forgot_password",
                what: "reset_password",
                how: "click",
                value: email
            });
			return this.promise.resolve(data);
		}, (error) => {
			return this.promise.reject(error);
		});
	}
}

export default ForgotPasswordService;
