import spaceSettingController from './space-settings.controller';

describe('space-settings controller', () => {
	var ctrl, $scope, spaceServiceMock, uiLoadingSrvMock;

	// mock controller 依赖的服务
	beforeEach(angular.mock.inject(($q) => {

		spaceServiceMock = {
			/**
			 * mock domain是否可用服务
			 * 如果domain值为existed则不可用,其它都可用
			 * @param spaceId
			 * @param domain
			 * @returns {*}
			 */
			isDomainAvailable: function (spaceId, domain) {

				let result = true;
				if ("existed" == domain) {
					result = false;
				}

				return $q.resolve(result);
			},
			updateSpace: function () {
				return $q.resolve();
			}
		};

		uiLoadingSrvMock = {
			createLoading: function () {
			},
			removeLoading: function () {
			}
		};
	}));

	// 使用$controller构造controller, 并注入mock services
	beforeEach(() => {
		angular.mock.inject(($controller, $rootScope, $q) => {
			$scope = $rootScope.$new();

			// mock rootSpace对象
			$scope.rootSpace = {
				current: {
					name: 'dong',
					domain: 'dong'
				}
			};

			ctrl = $controller(spaceSettingController, {
				'$scope': $scope,
				'SpaceService': spaceServiceMock,
				'$state': {},
				'uiLoadingSrv': uiLoadingSrvMock,
				'$q': $q
			});

		});
	});

	describe('init', () => {
		it('showDialog state should be false', () => {
			expect(ctrl.state.showDialog).to.equal(false);
		});
	});

	describe('check name', () => {
		it('name and domain are all blank, errors should have name required', () => {
			// name和domain表单都为空
			ctrl.space.name = ctrl.space.domain = undefined;
			ctrl.check('name');
			expect(ctrl.errors.name.required).to.equal(true);
		});

		it('name is blank, name value should be equal to domain value, and no errors ', () => {
			// name为空
			ctrl.space.name = undefined;
			ctrl.check('name');
			expect(ctrl.space.name).to.equal(ctrl.space.domain);
			expect(ctrl.errors.name.required).to.equal(false);
		});

		it('name is not blank, name value should not be equal to domain value, and no errors ', () => {
			// name不为空
			ctrl.space.name = "custom value";
			ctrl.check('name');
			expect(ctrl.space.name).to.not.equal(ctrl.space.domain);
			expect(ctrl.errors.name.required).to.equal(false);
		});
	});

	describe('check domain', () => {
		it('domain is blank, errors should have domain required', () => {
			// name为空
			ctrl.space.domain = undefined;
			ctrl.check('domain');
			expect(ctrl.errors.domain.required).to.equal(true);
		});

		it('domain is not blank, but has exists, should have unique errors', () => {
			// name为空
			ctrl.space.domain = "existed";
			ctrl.check('domain');
			$scope.$digest();
			expect(ctrl.errors.domain.required).to.equal(false);
			expect(ctrl.errors.domain.unique).to.equal(true);
		});

		it('domain is not blank, and not exists, should not have errors', () => {
			// name为空
			ctrl.check('domain');
			$scope.$digest();
			expect(ctrl.errors.domain.required).to.equal(false);
			expect(ctrl.errors.domain.unique).to.equal(false);
		});

	});

	describe('saveSpace', () => {
		it('domain and name are all blank', () => {
			// name为空
			ctrl.space.domain = ctrl.space.name = undefined;
			ctrl.saveSpace();
			expect(ctrl.errors.name.required).to.equal(true);
			expect(ctrl.errors.domain.required).to.equal(true);
		});

		it('domain is exists', () => {
			// name为空
			ctrl.space.domain = 'existed';
			ctrl.saveSpace();
			$scope.$digest();
			expect(ctrl.errors.domain.unique).to.equal(true);
		});
	});

});
