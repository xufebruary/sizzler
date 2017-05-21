'use strict';

import {
	LINK_EXCEL_FILE_ADD,
	getScrollbarWidth,
	i2s,
	uuid
} from '../../common/common';

/**
 * Edit
 * @dataSources
 *
 */
angular
.module('pt')
.controller('dsEditorCtrl', ['$scope', '$rootScope', '$translate', '$state', '$document', '$timeout', 'dataMutualSrv', 'uiLoadingSrv', 'siteEventAnalyticsSrv', dsEditorCtrl]);

function dsEditorCtrl($scope, $rootScope, $translate, $state, $document, $timeout, dataMutualSrv, uiLoadingSrv, siteEventAnalyticsSrv) {
	var body = $document.find('body').eq(0);
	//数据类型列表[国际化文案需要再次渲染]
	$scope.dsEditor['dataTypeList'] = [
		{'code': 'NUMBER', 'type': 'metrics', 'name': $translate.instant('COMMON.NUMBER')},
		{
			'code': 'CURRENCY', 'type': 'metrics', 'name': $translate.instant('COMMON.CURRENCY'),
			format: [
				{'code': '¥##', 'type': 'CURRENCY', 'name': $translate.instant('COMMON.CURRENCY_FORMAT.JPY')},
				{'code': '$##', 'type': 'CURRENCY', 'name': $translate.instant('COMMON.CURRENCY_FORMAT.USD')},
				{'code': '¥###', 'type': 'CURRENCY', 'name': $translate.instant('COMMON.CURRENCY_FORMAT.RMB')},
			]
		},
		{'code': 'PERCENT', 'type': 'metrics', 'name': $translate.instant('COMMON.PERCENT')},
		{'code': 'STRING', 'type': 'dimension', 'name': $translate.instant('COMMON.STRING')},
		{
			'code': 'DATE', 'type': 'dimension', 'name': $translate.instant('COMMON.DATE'),
			format: [
				{'code': 'yyyyMMdd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.YYMMDD')},
				{'code': 'yyyy/MM/dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.YY-MM-DD')},
				{'code': 'yyyy-MM-dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.Y-M-D')},
				{'code': 'yyyy.MM.dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.Y_M_D')},
				{'code': 'yyyy年MM月dd日', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.YYYYMMDD')},
				// {'code': 'MM/dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.MM-DD')},
				// {'code': 'MM-dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M-D')},
				// {'code': 'MM.dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M_D')},
				// {'code': 'MM月dd日', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.MMDD')},
				{'code': 'MM/dd/yyyy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.MM-DD-YYYY')},
				{'code': 'MM-dd-yyyy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M-D-YY')},
				{'code': 'MM.dd.yyyy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M_D_YY')},
				{'code': 'MM/dd/yy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.MM-DD-YY')},
				{'code': 'MM-dd-yy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M-D-Y')},
				{'code': 'MM.dd.yy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M_D_Y')},
			]
		},
		{
			'code': 'DATETIME', 'type': 'dimension', 'name': $translate.instant('COMMON.DATETIME'),
			format: [
				{
					'code': 'yyyy/MM/dd HH:mm:ss',
					'type': 'DATETIME',
					'name': $translate.instant('COMMON.DATETIME_FORMAT.YYYY-MM-DD-H-M-S')
				},
				{
					'code': 'yyyy.MM.dd HH:mm:ss',
					'type': 'DATETIME',
					'name': $translate.instant('COMMON.DATETIME_FORMAT.YYYY-M-D-H-M-S')
				},
				{
					'code': 'yyyy-MM-dd HH:mm:ss',
					'type': 'DATETIME',
					'name': $translate.instant('COMMON.DATETIME_FORMAT.YYYY_M_D-H-M-S')
				},
				{
					'code': 'yy/MM/dd HH:mm:ss',
					'type': 'DATETIME',
					'name': $translate.instant('COMMON.DATETIME_FORMAT.YY-MM-DD-H-M-S')
				},
				{
					'code': 'yy.MM.dd HH:mm:ss',
					'type': 'DATETIME',
					'name': $translate.instant('COMMON.DATETIME_FORMAT.YY-M-D-H-M-S')
				},
				{
					'code': 'yy-MM-dd HH:mm:ss',
					'type': 'DATETIME',
					'name': $translate.instant('COMMON.DATETIME_FORMAT.YY_M_D-H-M-S')
				},
				{
					'code': 'MM/dd/yyyy HH:mm:ss',
					'type': 'DATETIME',
					'name': $translate.instant('COMMON.DATETIME_FORMAT.MM-DD-YYYY-H-M-S')
				},
				{
					'code': 'MM.dd.yyyy HH:mm:ss',
					'type': 'DATETIME',
					'name': $translate.instant('COMMON.DATETIME_FORMAT.M-D-YYYY-H-M-S')
				},
				{
					'code': 'MM-dd-yyyy HH:mm:ss',
					'type': 'DATETIME',
					'name': $translate.instant('COMMON.DATETIME_FORMAT.M_D_YYYY-H-M-S')
				},
				{
					'code': 'yyyy年MM月dd日 HH時mm分ss秒',
					'type': 'DATETIME',
					'name': $translate.instant('COMMON.DATETIME_FORMAT.YYYYMMDDHMS')
				},
			]
		},
		{
			'code': 'TIME', 'type': 'dimension', 'name': $translate.instant('COMMON.TIME'),
			format: [
				{'code': 'HH:mm:ss', 'type': 'TIME', 'name': $translate.instant('COMMON.TIME_FORMAT.H-M-S')},
				{'code': 'HH:mm', 'type': 'TIME', 'name': $translate.instant('COMMON.TIME_FORMAT.H-M')},
				{'code': 'HH:mm a', 'type': 'TIME', 'name': $translate.instant('COMMON.TIME_FORMAT.H-M_A')},
				{'code': 'HH:mm:ss a', 'type': 'TIME', 'name': $translate.instant('COMMON.TIME_FORMAT.H-MS_A')},
				{'code': 'HH時mm分ss秒', 'type': 'TIME', 'name': $translate.instant('COMMON.TIME_FORMAT.HH-MM-SS')},
			]
		},
		{
			'code': 'DURATION', 'type': 'dimension', 'name': $translate.instant('COMMON.DURATION'),
			format: [
				{'code': '##s', 'type': 'DURATION', 'name': $translate.instant('COMMON.DURATION_FORMAT.SECONDS')},
				{'code': '##m', 'type': 'DURATION', 'name': $translate.instant('COMMON.DURATION_FORMAT.MINUTES')},
				{'code': '##h', 'type': 'DURATION', 'name': $translate.instant('COMMON.DURATION_FORMAT.HOURS')},
			]
		},
		{'code': 'TIMESTAMP', 'type': 'dimension', 'name': $translate.instant('COMMON.TIMESTAMP')}
		// {'code': 'LOCATION_COUNTRY', 'type': 'dimension', 'name': $translate.instant('COMMON.LOCATION_COUNTRY')},
		// {'code': 'LOCATION_REGION', 'type': 'dimension', 'name': $translate.instant('COMMON.LOCATION_REGION')},
		// {'code': 'LOCATION_CITY', 'type': 'dimension', 'name': $translate.instant('COMMON.LOCATION_CITY')}
	];

	var editSet = $scope.editSet = {
		oldDsContent: angular.copy($scope.dsEditor.dsContnet),//保存原始数据
		pageLoad: false,    //当前页面加载状态
		scrollBarWidth: getScrollbarWidth(),

		//数据校验
		verify: {
			flag: true,
			table: []
		},

		//数据范围
		dataRange: {
			headerIndex: $scope.dsEditor.hotTable.headIndex + 1,
			startIndex: $scope.dsEditor.hotTable.ignoreRowStart + 1,
			endIndex: $scope.dsEditor.hotTable.ignoreRowEnd + 1,
			startMin: 1,
			endMin: 1,
			headerIndexErrorTips: false,
			startIndexErrorTips: false,
			endIndexErrorTips: false
		},

		//表头
		headData: {
			modeHeadValue: []
		},

		//数据类型下拉
		dataTypeDropdown: {
			show: false,
			left: 0,
			schema: null,
			right: false    //是否在左侧显示
		},

		//提示框
		tips: {
			show: false,
			options: {}
		},

		//路由跳转
		routerChange: {
			flag: false,
			to: null
		},

		//重名校验(存储各个sheet的重名情况,例: {列C ID: [1,3]})
		nameVerify: [],

		//表格宽度
		tdWidth: null
	};
	dataInit();

	//切换表头模式(指定||自定义)
	$scope.changeMode = function (mode,option) {
		$scope.dsEditor.hotTable.headMode = mode;

		if (mode == 'custom') {
			//清除表头
			var headIndex = $scope.dsEditor.hotTable.ignoreRow.indexOf($scope.dsEditor.hotTable.headIndex);
			if (headIndex >= 0) {
				$scope.dsEditor.hotTable.ignoreRow.splice(headIndex);
			}
			$scope.dsEditor.hotTable.headIndex = null;
			angular.element('.ds-table-bd-tr').removeClass('ds-table-header');
			editSet.dataRange.startMin = 1;
			// for (var i = $scope.dsEditor.hotTable.schema.length - 1; i >= 0; i--) {
			// $scope.dsEditor.hotTable.schema[i].name = '';
			// };
		} else {
			//当输入值在范围之外则默认为1
			if (!editSet.dataRange.headerIndex) {
				editSet.dataRange.headerIndexErrorTips = true;
			} else {
				editSet.dataRange.headerIndexErrorTips = false;

				//添加表头
				$scope.dsEditor.hotTable.headIndex = editSet.dataRange.headerIndex - 1;
				var headIndex = $scope.dsEditor.hotTable.ignoreRow.indexOf($scope.dsEditor.hotTable.headIndex);
				if (headIndex < 0) {
					$scope.dsEditor.hotTable.ignoreRow.push(headIndex);
				}
				angular.element('.ds-table-bd-tr').removeClass('ds-table-header').eq(editSet.dataRange.headerIndex - 1).addClass('ds-table-header');
				for (var i = $scope.dsEditor.hotTable.schema.length - 1; i >= 0; i--) {
					var name = $scope.dsEditor.hotTable.data[editSet.dataRange.headerIndex - 1][i].substr(0, 30);
					if (name == '') {
						name = $translate.instant('DATA_SOURCE.EDITOR.COL') + i2s(i);
					}
					$scope.dsEditor.hotTable.schema[i].name = $scope.editSet.headData.modeHeadValue[i] = name;

					if ($scope.editSet.headData.modeHeadValue[i] != '' || $scope.dsEditor.hotTable.ignoreCol.indexOf(i) < 0) {
						angular.element('.ds-ipt-header').eq(i).removeClass('bg-danger').addClass('no-bg');
					}
				}

				editSet.dataRange.startIndex = editSet.dataRange.startMin = editSet.dataRange.headerIndex + 1;
				$scope.changeRang();
			}
		}

		//校验表头
		if (editSet.verify.table.length > 0 && !editSet.verify.table[$scope.dsEditor.hotTableIndex].flag) {
			dataVerify()
		}

		//表头数据重名校验
		$scope.unique();

		//全站事件统计
		if(mode && option != 'no-site-event'){
			var value = '';
			if(option){
				value = editSet.dataRange.headerIndex ? $scope.dsEditor.hotTable.id + ',' + editSet.dataRange.headerIndex : $scope.dsEditor.hotTable.id + ',' + 1;
			}else{
				value = $scope.dsEditor.hotTable.id + ',' + mode;
			}
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where:'excel_editor',
				what: option ? 'change_header_index' : 'select_header_model',
				how: option ? 'change' : 'click',
				value: value
			});
		}

	};

	//调整数据有效范围
	$scope.changeRang = function (type) {
		if (type) {
			if (!editSet.dataRange.startIndex) {
				editSet.dataRange.startIndexErrorTips = true;
			} else {
				editSet.dataRange.startIndexErrorTips = false;
			}

			if (!editSet.dataRange.endIndex) {
				editSet.dataRange.endIndexErrorTips = true;
			} else {
				editSet.dataRange.endIndexErrorTips = false;
			}

			if (!editSet.dataRange.startIndex || !editSet.dataRange.endIndex) {
				return
			}
		}

		//行忽略范围
		$scope.dsEditor.hotTable.ignoreRow = [];
		angular.element('.ds-table-bd-tr').removeClass('ds-table-ignore');

		//行头匹配
		var sMax = editSet.dataRange.startIndex - 1;
		for (var i = 0; i < sMax; i++) {
			$scope.dsEditor.hotTable.ignoreRow.push(i);
			angular.element('.ds-table-bd-tr').eq(i).addClass('ds-table-ignore');
		}

		//行尾匹配
		var j = parseInt($scope.dsEditor.hotTable.rowSum) - (editSet.dataRange.endIndex - 1);
		if (j <= 200) {
			for (j; j < parseInt($scope.dsEditor.hotTable.rowSum); j++) {
				if ($scope.dsEditor.hotTable.ignoreRow.indexOf(j) < 0) {
					$scope.dsEditor.hotTable.ignoreRow.push(j);
					angular.element('.ds-table-bd-tr').eq(j).addClass('ds-table-ignore');
				}
			}
		}

		if ($scope.dsEditor.hotTable.headType == 'row') {
			$scope.dsEditor.hotTable.ignoreRowStart = editSet.dataRange.startIndex - 1;
			$scope.dsEditor.hotTable.ignoreRowEnd = editSet.dataRange.endIndex - 1;
		} else {
			// $scope.dsEditor.hotTable.ignoreColStart = 1,
			// $scope.dsEditor.hotTable.ignoreColEnd = 0,
		}

		//列忽略范围
		for (var i = $scope.dsEditor.hotTable.ignoreCol.length - 1; i >= 0; i--) {
			var index = $scope.dsEditor.hotTable.ignoreCol[i];

			//置灰
			for (var j = $scope.dsEditor.hotTable.data.length - 1; j >= 0; j--) {
				angular.element('.ds-table-bd-tr').eq(j).children('td:eq(' + (index + 1) + ')').addClass('ds-table-ignore-td');
			}
			angular.element('.ds-editor-header').children('.ds-editor-hd-box').eq(index).addClass('ds-table-ignore-td')
		}

		//表头忽略
		if ($scope.dsEditor.hotTable.headMode == 'assign') {
			var headIndex = $scope.dsEditor.hotTable.ignoreRow.indexOf($scope.dsEditor.hotTable.headIndex);
			if (headIndex < 0) {
				$scope.dsEditor.hotTable.ignoreRow.push($scope.dsEditor.hotTable.headIndex);
			}
		}

		console.log($scope.dsEditor.hotTable.ignoreCol);
		console.log($scope.dsEditor.hotTable.ignoreRow);

		//全站事件统计
		if(type){
			var indexValue = type === 'start' ? editSet.dataRange.startIndex : editSet.dataRange.endIndex;
			siteEventAnalyticsSrv.createData({
				uid: $rootScope.userInfo.ptId,
				where:'excel_editor',
				what:'change_ignore_row_' + type,
				how:'change',
				value:$scope.dsEditor.hotTable.id + ',' + indexValue
			});
		}

	};

	//切换sheet
	$scope.sltSheet = function (sheet, index) {
		$scope.dsEditor.dsContnet.table[$scope.dsEditor.hotTableIndex] = angular.copy($scope.dsEditor.hotTable);
		$scope.dsDataFormat($scope.dsEditor.dsContnet, index);
		editSet.dataRange.startIndex = $scope.dsEditor.hotTable.ignoreRowStart + 1;
		editSet.dataRange.endIndex = $scope.dsEditor.hotTable.ignoreRowEnd + 1;
		editSet.dataRange.headerIndex = $scope.dsEditor.hotTable.headIndex + 1;

		//重新置换表头数据
		for (var i = $scope.dsEditor.hotTable.schema.length - 1; i >= 0; i--) {
			if ($scope.dsEditor.hotTable.headMode == 'custom') {
				$scope.editSet.headData.modeHeadValue[i] = $scope.dsEditor.hotTable.schema[i].name;
			} else {
				$scope.dsEditor.hotTable.schema[i].name = $scope.editSet.headData.modeHeadValue[i] = $scope.dsEditor.hotTable.data[editSet.dataRange.headerIndex - 1][i].substr(0, 30);
			}

			if ($scope.dsEditor.hotTable.schema[i].name == '') {
				$scope.dsEditor.hotTable.schema[i].name = $scope.editSet.headData.modeHeadValue[i] = $translate.instant('DATA_SOURCE.EDITOR.COL') + i2s(i);
			}
		}

		$scope.$on('ngRepeatFinished', function (ngRepeatFinishedEvent) {
			$scope.changeRang();
			dataVerify();

			if (editSet.verify.table.length > 0 && !editSet.verify.table[$scope.dsEditor.hotTableIndex].flag) {
				var table = editSet.verify.table[$scope.dsEditor.hotTableIndex];

				for (var i = 0; i < table.schema.length; i++) {
					if (table.schema[i]) {
						angular.element('.ds-ipt-header').eq(i).removeClass('bg-danger').addClass('no-bg');
					} else {
						angular.element('.ds-ipt-header').eq(i).removeClass('no-bg').addClass('bg-danger');
					}
				}
			}

			//表头数据重名校验
			$scope.unique();

			//滚动条置顶
			$('.ds-editor-table-bd').scrollTop(0)
		});

		//生成表格宽度 dsEditor.hotWeight
		$scope.editSet.tdWidth = getCurrentWidth();

		//全站事件统计
		siteEventAnalyticsSrv.createData({
			uid: $rootScope.userInfo.ptId,
			where:'excel_editor',
			what:'select_sheet',
			how:'click',
			value:$scope.dsEditor.hotTable.id
		});
	};


	//切换类型(指标||维度||忽略)
	$scope.changeType = function (type, index) {
		if ($scope.dsEditor.hotTable.schema[index].type !== type.code) {
			var colIndex = $scope.dsEditor.hotTable.ignoreCol.indexOf(index);

			if (type.code == 'ignore') {
				if (!$scope.dsEditor.hotTable.ignoreCol) {
					$scope.dsEditor.hotTable.ignoreCol = [];
					$scope.dsEditor.hotTable.ignoreCol.push(index);
				} else {
					if (colIndex < 0) {
						$scope.dsEditor.hotTable.ignoreCol.push(index);
					}
				}

				//置灰
				for (var i = $scope.dsEditor.hotTable.data.length - 1; i >= 0; i--) {
					angular.element('.ds-table-bd-tr').eq(i).children('td:eq(' + (index + 1) + ')').addClass('ds-table-ignore-td');
				}
				;
				angular.element('.ds-editor-header').children('.ds-editor-hd-box').eq(index).addClass('ds-table-ignore-td');
				angular.element('.ds-ipt-header').eq(i).removeClass('bg-danger').addClass('no-bg');
			} else {
				if (colIndex >= 0) {
					$scope.dsEditor.hotTable.ignoreCol.splice(colIndex)
				}

				//取消置灰
				for (var i = $scope.dsEditor.hotTable.data.length - 1; i >= 0; i--) {
					angular.element('.ds-table-bd-tr').eq(i).children('td:eq(' + (index + 1) + ')').removeClass('ds-table-ignore-td');
				}
				;
				angular.element('.ds-editor-header').children('.ds-editor-hd-box').eq(index).removeClass('ds-table-ignore-td')
			}
			$scope.dsEditor.hotTable.schema[index].type = type.code;
		}
	}

	//忽略某列
	$scope.ignore = function (isIgnore, index) {
		console.log(isIgnore + '; index: ' + index)
		var colIndex = $scope.dsEditor.hotTable.ignoreCol.indexOf(index);

		if (isIgnore) {
			if (!$scope.dsEditor.hotTable.ignoreCol) {
				$scope.dsEditor.hotTable.ignoreCol = [];
				$scope.dsEditor.hotTable.ignoreCol.push(index);
			} else {
				if (colIndex < 0) {
					$scope.dsEditor.hotTable.ignoreCol.push(index);
				}
			}

			//置灰
			for (var i = $scope.dsEditor.hotTable.data.length - 1; i >= 0; i--) {
				angular.element('.ds-table-bd-tr').eq(i).children('td:eq(' + (index + 1) + ')').addClass('ds-table-ignore-td');
			}
			;
			angular.element('.ds-editor-header').children('.ds-editor-hd-box').eq(index).addClass('ds-table-ignore-td');
			angular.element('.ds-ipt-header').eq(i).removeClass('bg-danger').addClass('no-bg');

			$scope.dsEditor.hotTable.schema[index].isIgnore = 1;
		} else {
			if (colIndex >= 0) {
				$scope.dsEditor.hotTable.ignoreCol.splice(colIndex)
			}

			//取消置灰
			for (var i = $scope.dsEditor.hotTable.data.length - 1; i >= 0; i--) {
				angular.element('.ds-table-bd-tr').eq(i).children('td:eq(' + (index + 1) + ')').removeClass('ds-table-ignore-td');
			}
			angular.element('.ds-editor-header').children('.ds-editor-hd-box').eq(index).removeClass('ds-table-ignore-td')

			$scope.dsEditor.hotTable.schema[index].isIgnore = 0;
		}

		//GTM
		siteEventAnalyticsSrv.setGtmEvent('click_element', 'ds_gd', 'ignore_' + isIgnore);
	};

	$scope.ignoreCol = function(index){
		//全站事件统计
		var ignoreType = $scope.dsEditor.hotTable.schema[index].isIgnore ? false : true;
		siteEventAnalyticsSrv.createData({
			uid: $rootScope.userInfo.ptId,
			where:'excel_editor',
			what:'select_ignore_col',
			how:'click',
			value: $scope.dsEditor.hotTable.schema[index].id + ',' + ignoreType
		});
	};

	//切换数据类型
	$scope.changeDataType = function (dataType, index) {
		$scope.dsEditor.hotTable.schema[index].type = dataType.type;
		$scope.dsEditor.hotTable.schema[index].dataType = dataType.code;
		$scope.dsEditor.hotTable.schema[index].dataFormat = '';//清空dataFormat字段
		$document.unbind('click', documentClickBindData);
		editSet.dataTypeDropdown.show = false;

		//全站事件统计
		siteEventAnalyticsSrv.createData({
			uid: $rootScope.userInfo.ptId,
			where:'excel_editor',
			what:'select_data_format',
			how:'click',
			value: $scope.dsEditor.hotTable.schema[index].id + ',' + $scope.dsEditor.hotTable.schema[index].dataType + ',' + $scope.dsEditor.hotTable.schema[index].dataFormat
		});

	};

	//切换数据格式
	$scope.changeDataFormat = function (format, index) {
		$scope.dsEditor.hotTable.schema[index].dataType = format.type;
		$scope.dsEditor.hotTable.schema[index].dataFormat = format.code;
		if (format.type == 'CURRENCY') {
			$scope.dsEditor.hotTable.schema[index].type = 'metrics';
		} else if (['DATE', 'TIME', 'DURATION'].indexOf(format.type) >= 0) {
			$scope.dsEditor.hotTable.schema[index].type = 'dimension';
		}
		$document.unbind('click', documentClickBindData);
		editSet.dataTypeDropdown.show = false;

		//全站事件统计
		siteEventAnalyticsSrv.createData({
			uid: $rootScope.userInfo.ptId,
			where:'excel_editor',
			what:'select_data_format',
			how:'click',
			value: $scope.dsEditor.hotTable.schema[index].id + ',' + $scope.dsEditor.hotTable.schema[index].dataType + ',' + $scope.dsEditor.hotTable.schema[index].dataFormat
		});
	};

	//修改表头内容
	$scope.changeHeader = function (head, index, type) {
		console.log($scope.editSet.headData.modeHeadValue);
		$scope.changeMode('custom','no-site-event');

		if (type == 'blur' && head == '') {
			head = $translate.instant('DATA_SOURCE.EDITOR.COL') + i2s(index);
		}

		$scope.dsEditor.hotTable.schema[index].name = $scope.editSet.headData.modeHeadValue[index] = head;
		//重名验证
		$scope.unique();

		if (editSet.verify.table.length > 0) {
			if (head !== '') {
				angular.element('.ds-ipt-header').eq(index).removeClass('bg-danger').addClass('no-bg');
				editSet.verify.table[$scope.dsEditor.hotTableIndex].schema = false;
			} else {
				angular.element('.ds-ipt-header').eq(index).removeClass('no-bg').addClass('bg-danger');
				editSet.verify.table[$scope.dsEditor.hotTableIndex].schema = true;
			}
		}

		//全站事件统计
		siteEventAnalyticsSrv.createData({
			uid: $rootScope.userInfo.ptId,
			where:'excel_editor',
			what:'change_header_title',
			how:'change',
			value: $scope.dsEditor.hotTable.schema[index].id + ',' + $scope.dsEditor.hotTable.schema[index].name
		});
	};

	/**
	 * 列名重名校验
	 *
	 * 返回重名的Index
	 */
	$scope.unique = function () {
		//var list = $scope.editSet.headData.modeHeadValue;
		var list = $scope.dsEditor.hotTable.schema;
		var uniqueList = [];

		//获取所有重名的下标Index
		for (var i = 0; i < list.length; i++) {
			var tmpList = angular.copy(list);
			tmpList[i].name = uuid();

			for (var j = 0; j < tmpList.length; j++) {
				if (tmpList[j].name.toUpperCase() == list[i].name.toUpperCase()) {
					uniqueList.push(j);
				}
			}
		}

		//去掉重复的下标
		var uniqueArr = [];
		$.each(uniqueList, function (i, el) {
			if ($.inArray(el, uniqueArr) === -1) uniqueArr.push(el);
		});


		var flag = false;
		for (var i = 0; i < $scope.editSet.nameVerify.length; i++) {
			if ($scope.editSet.nameVerify[i].index == $scope.dsEditor.hotTableIndex) {
				flag = true;

				if (uniqueArr.length == 0) {
					$scope.editSet.nameVerify.splice(i, 1);
				} else {
					$scope.editSet.nameVerify[i].list = uniqueArr.sort();
				}
				break;
			}

		}

		if (!flag && uniqueArr.length > 0) {
			$scope.editSet.nameVerify.push({
				index: $scope.dsEditor.hotTableIndex,
				list: uniqueArr.sort()
			})
		}

		//列名标红
		angular.element('.ds-ipt-header').removeClass('bg-danger').addClass('no-bg');
		if (uniqueArr.length > 0) {
			for (var i = 0; i < uniqueArr.length; i++) {
				angular.element('.ds-ipt-header').eq(uniqueArr[i]).removeClass('no-bg').addClass('bg-danger');
			}
		}

		//sheet按钮标红
		angular.element('.ds-ft-btn').each(function () {
			angular.element(this).children('span').removeClass('text-danger');
		});
		for (var i = 0; i < $scope.editSet.nameVerify.length; i++) {
			angular.element('.ds-ft-btn').eq($scope.editSet.nameVerify[i].index).children('span').addClass('text-danger');
		}
	};

	//重名校验
	$scope.nameVerify = function (index, list) {
		if (index != 'all') {
			var flag = false;
			var tmp = angular.copy(list);

			tmp[index] = uuid();
			for (var i = 0; i < tmp.length; i++) {
				if (tmp[i] == list[index]) {
					flag = true;
					break;
				}
			}


			$scope.editSet.nameVerify.push({
				tableIndex: $scope.dsEditor.hotTableIndex,
				list: []
			})

			return flag;
		} else {
			var isSome = [];
			for (var i = 0; i < list.length; i++) {
				var tmp = angular.copy(list);
				tmp[i] = uuid();
				var hasIndex = tmp.indexOf(list[i]);

				if (hasIndex)

					if ($scope.editSet.nameVerify[$scope.dsEditor.hotTableIndex]) {

					} else {
						$scope.editSet.nameVerify[$scope.dsEditor.hotTableIndex] = []
					}
			}
		}
	};

	//save
	$scope.save = function () {
		//loading
		uiLoadingSrv.createLoading(angular.element('.ds-editor-box'));

		$scope.dsEditor.dsContnet.table[$scope.dsEditor.hotTableIndex] = angular.copy($scope.dsEditor.hotTable);

		for (var i = 0; i < editSet.oldDsContent.table.length; i++) {
			for (var j = editSet.oldDsContent.table[i].schema.length - 1; j >= 0; j--) {
				var old = editSet.oldDsContent.table[i].schema[j];
				var current = $scope.dsEditor.dsContnet.table[i].schema[j];

				if (old.name != current.name || old.type != current.type || old.dataType != current.dataType || old.dataFormat != current.dataFormat || old.isIgnore != current.isIgnore) {
					$scope.dsEditor.dsContnet.table[i].schema[j].isCustom = 1;
					console.log($scope.dsEditor.dsContnet.table[i].schema[j])
				}
			}
		}
		var dsData = angular.copy($scope.dsEditor.dsContnet);

		//如果之前表格已存储
		// if(editSet.routerChange.flag){
		//     dsData.operateType = 'edit_save';
		// }

		if (!dataVerify().flag) {
			//loading
			uiLoadingSrv.removeLoading(angular.element('.ds-editor-box'));
			showTips('dataVerify');
		} else if ($scope.editSet.nameVerify.length > 0) {
			uiLoadingSrv.removeLoading(angular.element('.ds-editor-box'));
			showTips('nameVerify');
		} else {
			dataMutualSrv.post(LINK_EXCEL_FILE_ADD, angular.copy(dsData)).then(function (data) {
				if (data.status == 'success') {
					console.log(data);
					editSet.routerChange.flag = true;
					showTips('saveSuccess');

					if (dsData.operateType == 'save') {
						$scope.dsAuthUpdata($scope.dsCtrl.editDs, 'add', 'file');
					}

					//GTM
					siteEventAnalyticsSrv.setGtmEvent('click_element', 'ds_gd', 'save_file');

					//保存成功后，修改操作类型
					// $scope.dsEditor.dsContnet.operateType = 'edit_save';
				} else {
					showTips('saveError');

					if (data.status == 'failed') {
						console.log('Post Data Failed!')
					} else if (data.status == 'error') {
						console.log('Post Data Error: ')
						console.log(data.message)
					}
				}

				//loading
				uiLoadingSrv.removeLoading(angular.element('.ds-editor-box'));
			})
		}

		//全站事件统计
		siteEventAnalyticsSrv.createData({
			uid: $rootScope.userInfo.ptId,
			where:'excel_editor',
			what:'save_connection_source',
			how:'click',
			value: $scope.dsEditor.dsContnet.sourceId + ',' + dsData.operateType
		});
	};

	//cancel
	$scope.cancel = function () {
		// $scope.dsCtrl.showEdit = false;
		$scope.goTo('fileList');
		//全站事件统计
		siteEventAnalyticsSrv.createData({
			uid: $rootScope.userInfo.ptId,
			where:'excel_editor',
			what:'cancel',
			how:'click',
			value: $scope.dsEditor.dsContnet.sourceId
		});
	};

	//数字转字母
	$scope.i2s = function (index) {
		return i2s(index);
	};


	//提示框跳转
	$scope.goTo = function (type) {
		$scope.closeTips();

		if (type == 'createWidget') {
			$state.go('pt.dashboard');
		} else {
			if ($scope.dsCtrl.editDs.code != 'upload') {
				$scope.dsCtrl.goToMode = 'fileAdd';
			}
			if ($scope.dsEditor.dsContnet.operateType == 'edit_save') {
				$scope.dsCtrl.goToMode = 'fileList';
			}

			$scope.sltDs($scope.dsCtrl.editDs, 'edit');
		}
	};

	//关闭提示框
	$scope.closeTips = function (type) {
		body.removeClass('modal-open');
		editSet.tips.show = false;

		if (type && type == 'routerChange') {
			$scope.dsEditor.dsContnet.operateType = 'edit_save';
			$scope.editSet.routerChange.flag = false;
		}
	};



	//切换数据类型下拉
	$scope.toggleDataType = function (e, index, schema) {
		if (index == editSet.dataTypeDropdown.index && editSet.dataTypeDropdown.show) {
			$document.unbind('click', documentClickBindData);
			editSet.dataTypeDropdown.show = false;
		} else {
			//var left = angular.element(e.target).parents('.ds-editor-hd-box').position().left;
			var left = angular.element(e.target).parents('.ds-editor-hd-box').offset().left - 80 - 2;//80:外框左侧边距,2:外框边框宽度
			var marginRight = document.documentElement.clientWidth - angular.element(e.target).parents('.ds-editor-hd-box').offset().left;
			editSet.dataTypeDropdown.right = false;
			if (marginRight < 190) {
				editSet.dataTypeDropdown.right = true;
				left = left - 108;
			}

			editSet.dataTypeDropdown.left = left;
			editSet.dataTypeDropdown.show = true;
			editSet.dataTypeDropdown.index = index;
			editSet.dataTypeDropdown.schema = schema;

			//GTM
			siteEventAnalyticsSrv.setGtmEvent('click_element', 'ds_gd', 'col_type');
		}
	};
	var documentClickBindData = function (event) {
		var elem = angular.element('.dropdown-menu-dataType');
		if (editSet.dataTypeDropdown.show && !angular.element(event.target).hasClass('btn-dataType') && !angular.element(event.target).parents('button').hasClass('btn-dataType') && !elem[0].contains(event.target)) {
			$scope.$apply(function () {
				editSet.dataTypeDropdown.show = false;
			});
		}
	};
	$scope.$watch('editSet.dataTypeDropdown.show', function (value) {
		if (value) {
			$document.bind('click', documentClickBindData);
		} else {
			$document.unbind('click', documentClickBindData);
		}
	});
	//绑定滚动事件
	angular.element('.ds-editor-box-bd').on('scroll', function () {
		editSet.dataTypeDropdown.show = false;
	});


	//页面手动跳转
	$scope.changeRouter = function () {
		$scope.closeTips();

		editSet.routerChange.flag = true;
		$state.go(editSet.routerChange.to.name);
	};


	//数据初始化
	function dataInit() {
		//清除全局loading
		uiLoadingSrv.removeLoading('.pt-main');

		if ($scope.dsEditor.dsContnet.length == 0) {
			editSet.routerChange.flag = true;
			$state.go('pt.dataSources');
		} else {
			editSet.pageLoad = true;
			$scope.$on('ngRepeatFinished', function (ngRepeatFinishedEvent) {
				if ($scope.dsEditor.hotTable.headMode == 'assign') {
					//添加表头
					angular.element('.ds-table-bd-tr').removeClass('ds-table-header').eq(editSet.dataRange.headerIndex - 1).addClass('ds-table-header');
					editSet.dataRange.startIndex = editSet.dataRange.startIndex <= editSet.dataRange.headerIndex ? editSet.dataRange.headerIndex + 1 : editSet.dataRange.startIndex;
					editSet.dataRange.startMin = editSet.dataRange.headerIndex + 1;
				}
				$scope.changeRang();
			});

			//生成表格宽度 dsEditor.hotWeight
			$scope.editSet.tdWidth = getCurrentWidth();

			//动态调整列宽
			angular.element(window).resize(function(){
				$timeout(function(){
					$scope.$apply(function(){
						$scope.editSet.tdWidth = getCurrentWidth();
					})
				},50)
			})
		}
	}

	//获取表格宽度
	function getCurrentWidth(){
		var length = $scope.dsEditor.hotTable.schema.length;
		//tableWidth 宽度应该为dom: ds-editor-box-bd的宽度,由于加载问题,改为dom: ds-header-breadcrumb;
		var tableWidth = angular.element(".ds-header-breadcrumb").width()+54;
		var tdWidth = '140px';

		if(+$scope.dsEditor.hotWeight <= tableWidth){
			tdWidth = parseInt((tableWidth-28)/length) + 'px';
		}
		return tdWidth;
	}

	//数据校验
	function dataVerify() {
		$scope.dsEditor.dsContnet.table[$scope.dsEditor.hotTableIndex] = angular.copy($scope.dsEditor.hotTable);
		var data = $scope.dsEditor.dsContnet;

		editSet.verify = {
			flag: true,
			table: []
		};

		var tableFlag = true;
		for (var i = 0; i < data.table.length; i++) {
			editSet.verify.table.push({index: i, flag: true, schema: []});

			for (var j = 0; j < data.table[i].schema.length; j++) {
				if ($scope.dsEditor.dsContnet.table[i].ignoreCol.indexOf(j) < 0 && data.table[i].schema[j].name == '') {
					tableFlag = false;
					editSet.verify.table[i].flag = false;
					editSet.verify.table[i].schema.push(false)
				} else {
					editSet.verify.table[i].schema.push(true)
				}
			}

			editSet.verify.flag = tableFlag;
		}

		if (!editSet.verify.flag) {
			if (!editSet.verify.table[$scope.dsEditor.hotTableIndex].flag) {
				var table = editSet.verify.table[$scope.dsEditor.hotTableIndex];

				for (var i = 0; i < table.schema.length; i++) {
					if (table.schema[i]) {
						angular.element('.ds-ipt-header').eq(i).removeClass('bg-danger').addClass('no-bg');
					} else {
						angular.element('.ds-ipt-header').eq(i).removeClass('no-bg').addClass('bg-danger');
					}
				}
			}

			for (var i = 0; i < editSet.verify.table.length; i++) {
				if (editSet.verify.table[i].flag) {
					angular.element('.ds-ft-btn').eq(i).children('span').removeClass('text-danger');
				} else {
					angular.element('.ds-ft-btn').eq(i).children('span').addClass('text-danger');
				}
			}
		}

		console.log(editSet.verify)
		return editSet.verify;
	}


	//路由监控
	$rootScope.$on('$stateChangeStart', function (event, to, toParams, from, fromParams) {
		if(from.name == 'pt.dataSources.editor' && to.name != 'signin') {
			if (!editSet.routerChange.flag) {
				editSet.routerChange.to = to;
				showTips('jumpPage');
				event.preventDefault();
				return;
			}
			if (to.name =='pt.dataSources.editor' && $scope.dsEditor.dsContnet.length == 0) {
				editSet.routerChange.flag = true;
				$scope.goTo('fileList');
				event.preventDefault();
				return;
			}
		}
	});


	//页面提示
	function showTips(type) {
		var options = {
			title: null,
			info: null,
			btnLeftText: $translate.instant('COMMON.CANCEL'),
			btnRightText: $translate.instant('COMMON.SUBMIT'),
			btnLeftClass: 'pt-btn-default',
			btnRightClass: 'pt-btn-danger',
			btnLeftEvent: 'closeTips()',
			btnRightEvent: 'closeTips()',
			closeEvent: 'closeTips()',
			btnLeftHide: 'false',
			btnRightHide: 'false',
			hdHide: 'false'
		};
		// body.addClass('modal-open');

		switch (type) {
			//数据校验
			case "dataVerify":
				options.info = $translate.instant('DATA_SOURCE.EDITOR.DATA_ERROR_TIPS');
				options.btnRightClass = 'pt-btn-default';
				options.btnRightText = $translate.instant('DATA_SOURCE.EDITOR.BTN_SET_AGAIN');
				options.btnLeftHide = 'true';
				options.hdHide = 'true';
				break;
			//重名校验
			case "nameVerify":
				options.info = $translate.instant('DATA_SOURCE.EDITOR.NAME_ERROR_TIPS');
				options.btnRightClass = 'pt-btn-success';
				options.btnRightText = $translate.instant('COMMON.OK');
				options.btnLeftHide = 'true';
				options.hdHide = 'true';
				break;
			//数据保存失败
			case "saveError":
				options.info = $translate.instant('DATA_SOURCE.EDITOR.SAVE_FILTER');
				options.btnLeftHide = 'true';
				options.btnRightClass = 'pt-btn-success';
				options.btnRightText = $translate.instant('COMMON.CLOSE');
				options.hdHide = 'true';
				break;
			//数据保存成功
			case "saveSuccess":
				if ($scope.dsCtrl.editDs.code == 'upload') {
					options.btnLeftText = $scope.dsCtrl.editDs.name + $translate.instant('DATA_SOURCE.EDITOR.HD_MANAGE');
				} else {
					options.btnLeftText = $scope.dsEditor.dsContnet.operateType == 'save' ? $translate.instant('DATA_SOURCE.EDITOR.BTN_ADD_FILL') : $translate.instant('DATA_SOURCE.EDITOR.BTN_EDIT_FILL');
				}
				options.info = $scope.dsEditor.dsContnet.name + $translate.instant('DATA_SOURCE.EDITOR.SAVE_SUCCESS_TIP_2');
				//options.btnLeftText = $scope.dsEditor.dsContnet.operateType == 'save' ? $translate.instant('DATA_SOURCE.EDITOR.BTN_ADD_FILL') : $translate.instant('DATA_SOURCE.EDITOR.BTN_EDIT_FILL');
				options.btnRightText = $translate.instant('DATA_SOURCE.EDITOR.BTN_CREATE_WIDGET');
				options.btnRightClass = 'pt-btn-success';
				options.btnLeftEvent = 'goTo("fileAdd")';
				options.btnRightEvent = 'goTo("createWidget")';
				options.closeEvent = 'closeTips("routerChange")';
				options.hdHide = 'true';
				break;
			//页面跳转提示
			case "jumpPage":
				options.title = '数据保存';
				options.info = $translate.instant('DATA_SOURCE.EDITOR.JUMP_PAGE');
				options.btnLeftText = $translate.instant('DATA_SOURCE.EDITOR.BTN_STAY');
				options.btnRightText = $translate.instant('DATA_SOURCE.EDITOR.BTN_LEAVE');
				options.btnRightEvent = 'changeRouter()';
				options.hdHide = 'true';
				break;
		}

		editSet.tips.show = true;
		editSet.tips.options = options;
	}


	// window.onbeforeunload = function(){
	//     event.returnValue="确定离开当前页面吗？";
	// }
}
