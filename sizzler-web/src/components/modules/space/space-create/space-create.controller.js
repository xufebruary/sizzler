'use strict';

import {
	uuid,
	getLocalLang
} from '../../common/common';

import ProductConfig from 'configs/product.config';

SpaceCreateController.$inject = ['$scope', '$q', '$timeout', 'toggleLoadingSrv', '$state', 'SpaceService', 'publicDataSrv', 'uiLoadingSrv','Track'];

function SpaceCreateController($scope, $q, $timeout, toggleLoadingSrv, $state, SpaceService, publicDataSrv, uiLoadingSrv,Track) {
	var $body = $('body'), vm = this;

	vm.errors = {}; //错误信息

	// 页面状态
	vm.state = {
		memberList: [], //空间成员列表
		space: null, //空间
		dialogType: null //控制弹框类型
	};

	// ====================

	// 初始化
	init();

	// 校验
	vm.check = check;

	// 保存空间
	vm.saveSpace = saveSpace;

	// 清除报错信息
	vm.clearErrors = clearErrors;

	// 邀请跳过
	vm.closeDialog = closeDialog;

	// 邀请用户成功
	vm.onSuccess = onSuccess;

	// ====================

	/**
	 * 入口
	 */
	function init() {
		// 去掉loading
		toggleLoadingSrv.hide('body');

		$body.addClass('modal-open');

		vm.state.space = getSpaceInfoFromParentScope();
		vm.state.dialogType = 'createSpace';
		vm.isPhone = $scope.pt.settings.isPhone;

		if ($scope.pt.settings.asideFolded)
			$scope.pt.settings.asideFolded = false;
	}

	/**
	 * 校验
	 */
	function check(type) {
		switch (type) {
			case 'name':
				// 如果空间名称为空,则赋值为子域名
				if (!vm.state.space.name) {
					vm.state.space.name = vm.state.space.domain;
				}

				vm.errors.name = vm.errors.name || {};

				// 为空校验
				vm.errors.name.required = !vm.state.space.name;

				return $q.resolve();

				break;

			case "domain":
				vm.errors.domain = vm.errors.domain || {};

				// 为空校验
				vm.errors.domain.required = !vm.state.space.domain;

				// 正则校验
				var reg = /^[a-z0-9][a-z0-9_\-]{0,28}[a-z0-9]$/;
				vm.errors.domain.reg = !reg.test(vm.state.space.domain);

				/**
				 * 唯一性校验
				 * 返回promise对象,使得resolve时可以提交表单
				 */
				if (vm.state.space.domain && !vm.errors.domain.reg) {
					return isDomainAvailable().then(() => {
						vm.errors.domain.unique = false;

						// 如果空间名称为空,则赋值为子域名
						if (!vm.state.space.name) {
							vm.state.space.name = vm.state.space.domain;
							vm.errors.name = {required: false};
						}
						return $q.resolve();
					}, () => {
						vm.errors.domain.unique = true;
						return $q.reject();
					});
				}

				return $q.resolve();

				break;
		}
	}

	/**
	 * 清除错误状态
	 */
	function clearErrors(type) {
		if (vm.errors[type]) {
			vm.errors[type] = null;
		}
	}

	// vm.errors是否有错误
	function hasErrors(type) {
		if (type) {
			return hasErrorsInObject(vm.errors[type]);
		} else {
			for (var key in vm.errors) {
				if(hasErrorsInObject(vm.errors[key])) return true;
			}
		}
		return false;
	}

	function hasErrorsInObject(obj) {
		for (var attr in obj) {
			if (obj[attr]) {
				return true;
			}
		}
		return false;
	}


	function saveSpace() {
		/**
		 * 校验全部字段,返回promise数组
		 * @type {Array}
		 */
		var all = ['name', 'domain'].map((type) => {
			return check(type);
		});

		/**
		 * 执行promise数组,返回单个promise对象
		 */
		$q.all(all).then(() => {
			// 没有错误时提交表单
			if (!hasErrors()) {
				// 提交表单
				submitForm();
				Track.log({where: 'create_space', what: 'create_space',value: vm.state.space.spaceId});
			}
		});
	}

	function submitForm() {
		showLoading('.popup-content-bd');

		if (vm.state.space.type == 'create') {
			SpaceService.createSpace(vm.state.space)
			.then((data) => {
				//新建空间

				var space = data;
				createSpaceAfter(space);
			})
			.finally(() => {
				hideLoading('.popup-content-bd');
			});
		}
		else if (vm.state.space.type == 'update') {
			//老用户-空间信息更新

			SpaceService.updateSpace(vm.state.space)
			.then(() => {
				$scope.rootSpace.current.name = vm.state.space.name;
				$scope.rootSpace.current.domain = vm.state.space.domain;
				for (var i = 0; i < $scope.rootSpace.list.length; i++) {
					if ($scope.rootSpace.list[i].spaceId == $scope.rootSpace.current.spaceId) {
						$scope.rootSpace.list[i].name = vm.state.space.name;
						$scope.rootSpace.list[i].domain = vm.state.space.domain;
						break;
					}
				}
				vm.state.dialogType = 'inviteUser';
			})
			.finally(() => {
				hideLoading('.popup-content-bd');
			})
		}
	}

	/**
	 * 创建空间成功之后
	 */
	function createSpaceAfter(space) {
		//更新用户设置信息(当空间删除时,前端用户信息会被清除)
		SpaceService.getUsersSettingsInfo(vm.state.space.spaceId)
		.then((data) => {
			$scope.rootUser.settingsInfo = data;
			$scope.rootUser.profileSelected = data.profileSelected == '' ? null : angular.fromJson(data.profileSelected);
			$scope.rootUser.userSelected = data.userSelected == '' ? null : angular.fromJson(data.userSelected);
		});

		//更新前端dashboard列表,并根据isCreateSpace字段判断是否添加预置面板
		var isCreateSpace = publicDataSrv.getPublicData('isCreateSpace');
		if(isCreateSpace == 0){
			SpaceService.initDefaultPanel(vm.state.space.spaceId, getLocalLang().locale)
			.then((data) => {
				getDashboardList(space);
			})
		}
		else {
			getDashboardList(space);
		}
	}

	function getDashboardList(space){
		SpaceService.getSpacePanelList(vm.state.space.spaceId)
		.then((data) => {

			$scope.rootWidget.list = [];
			$scope.rootSpace.list = [];
			$scope.rootPanel.list = data.panelList;
			$scope.rootPanel.layout = data.panelLayout;
			$scope.rootPanel.layout.panelLayout = data.panelLayout.panelLayout === null ? [] : angular.fromJson(data.panelLayout.panelLayout);
			$scope.rootPanel.noData = $scope.rootPanel.layout.panelLayout.length == 0 || $scope.rootPanel.list.length == 0;
			$scope.rootSpace.list.push(angular.copy(space));
			$scope.rootSpace.current = angular.copy(space);

			//存储公共数据,已备进入mainCtrl中使用
			var spaceData = {
				list: $scope.rootWidget.list,
				current: angular.copy(space)
			};
			publicDataSrv.setPublicData('rootSpace', spaceData);
			publicDataSrv.setPublicData('isCreateSpace', 1); //更新创建空间状态

			//请求到dashboard list后进入邀请界面
			vm.state.dialogType = 'inviteUser';
		})
		.finally(() => {
			hideLoading('.popup-content-bd');
		})
	}

	function isDomainAvailable() {
		return SpaceService.isDomainAvailable(vm.state.space.spaceId, vm.state.space.domain)
		.then((result) => {
			if (result) {
				return $q.resolve();
			} else {
				return $q.reject();
			}
		});
	}

	function showLoading(name) {
		uiLoadingSrv.createLoading(angular.element(name));
	}

	function hideLoading(name) {
		uiLoadingSrv.removeLoading(angular.element(name));
	}

	function closeDialog() {
		$body.removeClass('modal-open');
		$state.go('pt.dashboard', {spaceDomain: vm.state.space.domain});

		//更新空间维态信息
		SpaceService.updateUsersSettingsInfo(vm.state.space.spaceId);

		Track.log({where: 'create_space', what: 'skip_send_invites',value: vm.state.space.spaceId});
	}

	function onSuccess() {
		$body.removeClass('modal-open');

		$timeout(function () {
			$state.go('pt.dashboard', {spaceDomain: vm.state.space.domain});
		}, 3000);

		//更新空间维态信息
		SpaceService.updateUsersSettingsInfo(vm.state.space.spaceId);
	}

	/**
	 * 从父级scope中获取当前space对象
	 * @returns {{spaceId: *, name: *, host: string, domain: (*|string), weekStart: *, ownerEmail: *, type: *}}
	 */
	function getSpaceInfoFromParentScope() {

		var space = {
			type: 'create',
			host: window.location.host + '/',
			spaceId: uuid(),
			name: '',
			domain: '',
			weekStart: ProductConfig.weekStart
		};

		var currentSpace = $scope.rootSpace && $scope.rootSpace.current;

		//未走创建空间流程的老用户
		if (currentSpace) {
			space.type = 'update';
			space.spaceId = currentSpace.spaceId;
			space.name = currentSpace.name;
			space.domain = currentSpace.domain;
			space.weekStart = currentSpace.weekStart;
		}

		return space;
	}
}

export default SpaceCreateController;
