dataController.$inject = ['$scope'];

function dataController($scope){

	var vm = $scope;

	// 取消类别设置回调
	vm.onCancelCategorySetting = onCancelCategorySetting;

	// 提交类别设置结果回调
	vm.onSubmitCategorySetting = onSubmitCategorySetting;

	// 取消排序回调
	vm.onCancelSort = onCancelSort;

	// 提交排序结果回调
	vm.onSubmitSort = onSubmitSort;

	function onSubmitCategorySetting(result){
		onCancelCategorySetting();

		var currentDimension = vm.modal.editorNow.variables[0].dimensions[vm.dataSettings.currentDimensionIndex];
		currentDimension.max = result.max;
		currentDimension.showOthers = result.showOthers ? 1 : 0;
		vm.saveData('data-max');
	}

	function onCancelCategorySetting() {
		vm.editor.dimensionOperation = false;
	}

	function onCancelSort(){
		vm.editor.dimensionOperation = false;
	}

	function onSubmitSort(sort) {
		// 隐藏
		onCancelSort();

		var sortRs = {
			type: sort.type,
			sortOrder: sort.order,
			sortColumn: sort.id
		};
		// 更新
		vm.modal.editorNow.variables[0].dimensions[vm.dataSettings.currentDimensionIndex].sort = sortRs;
		vm.saveData('data-sort');

		console.log(vm.modal.editorNow.variables[0].dimensions[vm.dataSettings.currentDimensionIndex])
	}

}

export default dataController;
