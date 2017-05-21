import arrayUtils from 'utils/array.utils';
import cloneUtils from 'utils/clone.utils';

dataSortController.$inject = ['DataSortService', 'Track'];

function dataSortController(DataSortService, Track){
	var vm = this;
	console.log(vm)
	vm.state = {
		currentSorter: null,
		currentMetric: null,
		dimensionSorters: {
			current: null,
			options: []
		},
		metricSorters: {
			current: null,
			options: []
		}
	};

	render();

	// 渲染方法
	vm.render = render;

	// 选择排序类型
	vm.sltSortType = sltSortType;

	// 修改排序
	vm.changeSorter = changeSorter;

	// 选择指标
	vm.sltMetric = sltMetric;

	// 提交
	vm.submit = submit;

	// 取消
	vm.cancel = cancel;

	function render(){
		// 设置默认排序
		setCurrentSorter();
		// 设置指标和维度排序
		setDimensionAndMetricSorters();
		// 设置默认指标
		setCurrentMetric();
	}

	function sltSortType(){
		var type = vm.state.currentSorter.type,
			sorter = {
				type: type
			};

		if(type === 'dimensionValue'){
			var dataType = vm.dimensions[0].dataType;
			sorter.id = vm.dimensions[0].uuid;
			sorter.order = vm.state.dimensionSorters.current.direction;
		}else if(type === 'metricsValue'){
			sorter.id = vm.state.currentMetric.uuid;
			sorter.order = vm.state.metricSorters.current.direction;
		}else{
			// 默认
			sorter.id = undefined;
			sorter.order = undefined;
		}

		vm.state.currentSorter = sorter;

		Track.log({where: 'widget_editor_metrics_sort', what: 'select_' + type});
	}

	function submit(){
		console.log(vm.state.currentSorter)
		vm.onSubmit(vm.state.currentSorter);
	}

	function cancel(){
		vm.onCancel();
	}

	function setCurrentSorter(){
		if(arrayUtils.isEmpty(vm.defaultSorts) || arrayUtils.isEmpty(vm.metrics)){
			vm.state.currentSorter = DataSortService.getDefaultOrders(vm.dimensions, vm.metrics);
		}else{
			vm.state.currentSorter = cloneUtils.deep(vm.defaultSorts)[0];
			if(vm.state.currentSorter.type == 'metricsValue'){
				var foundMetric = vm.metrics.find(function (o) {
					return o.uuid == vm.state.currentSorter.id;
				});
				// 找不到时将排序设置为按第一个指标降序
				if(typeof foundMetric === 'undefined'){
					vm.state.currentSorter.id = vm.metrics[0].uuid;
					vm.state.currentSorter.order = 'desc';
				}
			}
		}
	}

	function setDimensionAndMetricSorters() {
		// 维度
		var dataType = vm.dimensions[0].dataType;
		vm.state.dimensionSorters.options = DataSortService.getSorterByDataType(dataType);
		vm.state.dimensionSorters.current = DataSortService.getCurrentDimensionSorter(vm.state.dimensionSorters.options, vm.state.currentSorter, dataType);

		// 指标
		if(arrayUtils.isNotEmpty(vm.metrics)){
			vm.state.metricSorters.options = DataSortService.getSorterByDataType("NUMBER");
			vm.state.metricSorters.current = DataSortService.getCurrentMetricSorter(vm.state.metricSorters.options, vm.state.currentSorter);
		}
	}

	function setCurrentMetric() {
		if(arrayUtils.isNotEmpty(vm.metrics)){
			var metric = DataSortService.getMetricByUUID(vm.metrics, vm.state.currentSorter);
			sltMetric(metric);
		}
	}

	function changeSorter(sorter) {
		vm.state.currentSorter.order = sorter.direction;
		setDimensionAndMetricSorters();
		Track.log({where: 'widget_editor_metrics_sort', what: 'select_' + vm.state.currentSorter.type, value: sorter.direction});
	}

	function sltMetric(metric) {
		vm.state.currentSorter.id = metric.uuid;
		vm.state.currentMetric = metric;
		Track.log({where: 'widget_editor_metrics_sort', what: 'select_value_sort_by', value: metric.id});
	}

}

export default dataSortController;
