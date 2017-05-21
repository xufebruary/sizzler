import forgotPasswordController from './forgotPassword.ctrl';

describe('ForgotPasswordController', () => {
	let rootScope, ctrl, forgotPwdServiceMock;

	//mock forgotPasswordService
	beforeEach(angular.mock.inject(($q) => {
		forgotPwdServiceMock = {
			sendEmail: function () {
				var deferred = $q.defer();
				deferred.resolve({result: true});
				return deferred.promise;
			}
		};
	}));

	//构造forgotPwdCtrl,并传入forgotPwdServiceMock
	beforeEach(() => {
		angular.mock.inject(($controller, $rootScope) => {
			rootScope = $rootScope;
			ctrl = $controller(forgotPasswordController, {
				'ForgotPwdService': forgotPwdServiceMock
			});
		});
	});

	describe("send email", ()=> {
		it("sending state should be false", () => {
			expect(ctrl.state.sending).to.equal(false);
		});

		it("send email success", () => {
			//form表单
			ctrl.email = 'test';
			ctrl.form = {email: {}};

			ctrl.sendEmail();
			// When unit testing using ngMock
			// you need to synchronously maintain the flow of the tests
			// and manually trigger the digest cycle for promises to actually return.
			rootScope.$digest();

			expect(ctrl.state.sendResult.result).to.equal(true);
		});
	});

});

