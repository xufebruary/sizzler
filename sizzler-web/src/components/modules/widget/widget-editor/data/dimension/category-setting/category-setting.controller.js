import {
	getMyDsConfig
} from 'components/modules/common/common';

function categorySettingController() {

	var vm = this;

	var dataConfig = getMyDsConfig(vm.dsCode).editor.data;

	vm.state = {
		iptValue: 100,
		sliderValue: 5,
		sliderOptions: {
			floor: 1,
			ceil: 100,
			onStart: function () {
			},
			onChange: function () {
			},
			onEnd: function () {
				vm.state.iptValue = vm.state.sliderValue;
			}
		},
		showOthers: false
	};

	vm.setIptValue = setIptValue;

	vm.save = save;

	vm.close = close;

	render();

	function render() {
		var graphName = vm.graphName;
		var autoAddTimeDimension = dataConfig.autoAddTimeDimension;
		var index = parseInt(vm.dimensionIndex);
		var dimension = vm.dimension;

		var max;
		var currentValue;

		// 如果默认增加时间维度并且为线图时,则index要增加1
		if(autoAddTimeDimension && graphName == 'line'){
			vm.dimensionIndex++;
			index = index + 1;
		}

		if (index == 0) {
			max = currentValue = graphName == 'line' ? 1000 : 100;
		} else {
			currentValue = max = 10;
		}

		if (dimension.max != null) {
			currentValue = dimension.max;
		}

		// 维度拖拽排序后,就会出现超出最大值的情况
		currentValue = currentValue > max ? max : currentValue;

		vm.state.iptValue = currentValue;
		vm.state.sliderValue = currentValue;
		vm.state.sliderOptions.ceil = max;

		// 第一个维度饼图默认勾选showOther
		vm.state.showOthers = index == 0 && graphName == 'pie' ? true : false;
		if(dimension.showOthers != null && index == 0){
			vm.state.showOthers = dimension.showOthers == 1 ? true : false;
		}
	}

	function setIptValue(type) {
		var iptValue = $(".max-ipt").val();

		if (iptValue != '') {
			iptValue = +iptValue;
			if (iptValue > vm.state.sliderOptions.ceil) {
				iptValue = vm.state.sliderOptions.ceil;
			} else if (iptValue < vm.state.sliderOptions.floor) {
				iptValue = vm.state.sliderOptions.floor;
			}
			vm.state.iptValue = iptValue;
			vm.state.sliderValue = iptValue;
		} else {
			if (type == 'blur') {
				vm.state.iptValue = vm.state.sliderValue;
			}
		}
	}

	function save() {
		vm.onSubmit({
			max: vm.state.sliderValue,
			showOthers: vm.state.showOthers
		});
	}

	function close() {
		vm.onCancel();
	}
}

export default categorySettingController;
