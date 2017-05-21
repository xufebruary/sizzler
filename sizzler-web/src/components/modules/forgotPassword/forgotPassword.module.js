import forgotPasswordCtrl from './forgotPassword.ctrl';
import 'components/modules/signin/signin.css';
import './forgotPassword.scss';

export default angular.module('pt.forgotPassword', [])
	.controller({
		'ForgotPwdCtrl': forgotPasswordCtrl
	});


