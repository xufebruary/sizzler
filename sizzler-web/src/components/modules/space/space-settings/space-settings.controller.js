
SpaceSettingController.$inject = ['$scope', 'SpaceService', '$state', 'uiLoadingSrv', '$q'];

function SpaceSettingController($scope, SpaceService, $state, uiLoadingSrv, $q){
	var $body = $('body'),vm = this;

	vm.errors = {}; //错误信息
	vm.state = {
		showDialog: false //控制弹出框显示
	};

	// 初始化
	init();

	// 保存
	vm.saveSpace = saveSpace;

	// 删除
	vm.deleteSpace = deleteSpace;

	// 退出
	vm.quitSpace = quitSpace;

	// 选择周起始日
	vm.sltWeekStart = sltWeekStart;

	// 关闭弹出框
	vm.closeDialog = closeDialog;

	// 显示弹出框
	vm.showDialog = showDialog;

	// 表单元素校验
	vm.check = check;

	// 清除错误信息
	vm.clearErrors = clearErrors;

	// 是否有错误
	vm.hasErrors = hasErrors;

	/**
	 * 入口
     */
	function init(){
		vm.space = getSpaceInfoFromParentScope();
	}

	function saveSpace () {
		/**
		 * 校验全部字段,返回promise数组
		 * @type {Array}
         */
		var all = ['name','domain'].map((type) => {
			return check(type);
		});

		/**
		 * 执行promise数组,返回单个promise对象
         */
		$q.all(all).then(() => {
			// 没有错误时提交表单
			if(!hasErrors()){
				// 提交表单
				submitForm();
			}
		});
	}

	function submitForm(){
		showLoading('.space-setting-bd');
		SpaceService.updateSpace(vm.space)
		.then(() => {
			//重新刷新当前页
			window.location.href = '/'+vm.space.domain+'/SpaceSettings';
		})
		.finally(() => {
			hideLoading('.space-setting-bd');
		});
	}

	function isDomainAvailable(){
		return SpaceService.isDomainAvailable(vm.space.spaceId, vm.space.domain)
		.then((result) => {
			if(result){
				return $q.resolve();
			}else{
				return $q.reject();
			}
		});
	}

	function deleteSpace() {
		showLoading('.popup-content');
		SpaceService.deleteSpace(vm.space.spaceId)
		.then(() => {
			goToCreateOrViewOtherSpace();
		})
		.finally(() => {
			hideLoading('.popup-content');
		});
	}

	function quitSpace () {
		showLoading('.popup-content');
		SpaceService.quitSpace(vm.space.spaceId)
		.then(() => {
			goToCreateOrViewOtherSpace();
		})
		.finally(() => {
			hideLoading('.popup-content');
		});
	}

	function sltWeekStart(type) {
		vm.space.weekStart = type;
	}

	function closeDialog() {
		$body.removeClass('modal-open');
		vm.state.showDialog = false;
	}

	function showDialog() {
		$body.addClass('modal-open');
		vm.state.showDialog = true;
	}

	/**
	 * 校验字段
	 * 进行异步请求校验时,如果校验失败则reject。 否则都resolve(后续需要检查errors对象)。
	 * @param type
	 * @returns Promise 对象
     */
	function check(type) {
		switch (type) {
			case 'name':
				// 如果空间名称为空,则赋值为子域名
				if(!vm.space.name){
					vm.space.name = vm.space.domain;
				}

				vm.errors.name = vm.errors.name || {};

				// 为空校验
				vm.errors.name.required = !vm.space.name;

				return $q.resolve();

				break;

			case 'domain':

				vm.errors.domain = vm.errors.domain || {};

				// 为空校验
				vm.errors.domain.required = !vm.space.domain;

				// 正则校验
				var reg = /^[a-z0-9][a-z0-9_\-]{0,28}[a-z0-9]$/;
				vm.errors.domain.reg = !reg.test(vm.space.domain);

				/**
				 * 唯一性校验
				 * 返回promise对象,使得resolve时可以提交表单
				 */
				if(vm.space.domain && !vm.errors.domain.reg) {
					return isDomainAvailable().then(() => {
						vm.errors.domain.unique = false;
						return $q.resolve();
					},() => {
						vm.errors.domain.unique = true;
						return $q.reject();
					});
				}

				return $q.resolve();

				break;
		}
	}

	function clearErrors(type) {
		if(vm.errors[type]){
			vm.errors[type] = null;
		}
	}

	// vm.errors是否有错误
	function hasErrors(type){
		if(type){
			return hasErrorsInObject(vm.errors[type]);
		}else{
			for(var key in vm.errors){
				return hasErrorsInObject(vm.errors[key]);
			}
		}
		return false;
	}

	function hasErrorsInObject(obj){
		for(var attr in obj){
			if(obj[attr]){
				return true;
			}
		}
		return false;
	}

	function goToCreateOrViewOtherSpace(){
		if($scope.rootSpace.list.length <= 1){
			$scope.rootSpace.list = [];
			$body.removeClass('modal-open');

			//清除前端所有基础数据
			$scope.clearBaseData('all');

			$state.go('spaceCreate');
		} else {
			for(var i=0; i<$scope.rootSpace.list.length; i++){
				if($scope.rootSpace.list[i].spaceId != vm.space.spaceId){
					window.location.href = '/'+$scope.rootSpace.list[i].domain+'/Dashboard';
					break;
				}
			}
		}
	}

	function showLoading(name){
		uiLoadingSrv.createLoading(angular.element(name));
	}

	function hideLoading(name) {
		uiLoadingSrv.removeLoading(angular.element(name));
	}

	/**
	 * 从父级scope中获取当前space对象
	 * @returns {{spaceId: *, name: *, host: string, domain: (*|string), weekStart: *, ownerEmail: *, type: *}}
     */
	function getSpaceInfoFromParentScope(){
		var currentSpace = $scope.rootSpace.current;
		return {
			spaceId: currentSpace.spaceId,
			name: currentSpace.name,
			host: window.location.host,
			domain: currentSpace.domain,
			weekStart: currentSpace.weekStart,
			ownerEmail: currentSpace.ownerEmail,
			type: currentSpace.type
		};
	}
}

export default SpaceSettingController;
