import formatterUtil from 'utils/formatter.utils';

/**
 * 将json格式字符串转化为json对象
 * @param str
 * @returns {null}
 */
function parse2JSON(str) {
	if (!str) return null;
	return JSON.parse(str);
}

function split2Array(str) {
	if (!str) return [];
	return str.split(',');
}

export default {
	from: function(chartData, widget){
		var data = chartData.series[0].data;
		if(!data || data.length === 0) return [];

		var sortMap = parse2JSON(widget.variables[0].sort); // 字符串类型的json数据

		var formatRs = {
			columns: null,
			data: null,
			sort: null
		};

		// 隐藏列
		var hiddenColumns = chartData.fixSortColumnIndex && Object.values(chartData.fixSortColumnIndex);

		formatRs.columns = data[0].map(function (name, index) {
			var isMetric = index >= chartData.firstMetricsIndex;

			// 该列设置按其它列来排序
			var sortByColumnIndex = undefined;
			if(chartData.fixSortColumnIndex){
				sortByColumnIndex = chartData.fixSortColumnIndex[index];
			}

			return {
				name: name,
				isMetric: isMetric,
				dataType: isMetric ? 'NUMBER' : 'STRING',
				isHidden: hiddenColumns && hiddenColumns.indexOf(index) != -1,
				sortByColumnIndex: sortByColumnIndex
			};
		});
		formatRs.data = data.slice(1, data.length);
		formatRs.boundary = chartData.firstMetricsIndex;
		formatRs.orders = (function () {
			// 维度显示在指标前面,该处表示默认设置第一个指标降序
			var result = [];
			// widget中排序信息优先级最高
			if (sortMap && sortMap.length) {
				result = sortMap.reduce(function (prev, curr) {
					var key = Object.keys(curr)[0],
						value = curr[key];
					var index = chartData.columnsCode.indexOf(key);
					if (index != -1) {
						prev.push([index, value]);
					}
					return prev;
				}, []);
			}
			if (result.length === 0) {
				result = chartData.firstMetricsIndex < chartData.srcColumnLength ? [[chartData.firstMetricsIndex, 'desc']] : [[0, 'desc']];
			}
			return result;
		})();

		return formatRs;
	},
	/**
	 * 统一表格widget的数据格式
	 * @param widget widget对象
	 * @param wsData websocket取数任务对象
	 * @returns {
	 * 	  columns: [{
	 * 	  	name: // 名称,
	 * 	    isMetric: boolean // 是否是指标,
	 * 	  	dataType: // 类型,
	 * 		unit: // 单位,
	 * 		format: // 目前没什么用处
	 * 	  }],
	 * 	  data: [], // body信息
	 * 	  orders: [], // 排序信息
	 * 	  boundary: number // 维度和指标界限
	 * }
	 */
	format: function (widget, wsData) {
		if (wsData == null || wsData.length === 0) return null;
		if (widget == null) return null;

		/**
		 * 从wdData中获取的数据
		 */
		var rows = wsData.rows,  // 包括表头和body的数据
			dataTypeMap = wsData.dataTypeMap, // 列的数据类型
			unitMap = wsData.unitMap, // 列的单位
			formatMap = wsData.dataFormatMap, // 格式化
			metricsNames = split2Array(wsData.metricsName), // 指标名称(逗号分隔)
			metricsKeys = split2Array(wsData.metricsKey), // 与指标名称对应的key
			dimensions = split2Array(wsData.dimensions), // 维度
			allKeys = [].concat(dimensions).concat(metricsKeys); // 维度+指标

		/**
		 * 从widget中获取的数据
		 */
		var sortMap = parse2JSON(widget.variables[0].sort); // 字符串类型的json数据

		/**
		 * 开始构造数据
		 */
		var formatRs = {
			columns: null,
			data: null,
			sort: null
		};
		formatRs.columns = !rows[0] ? [] : rows[0].map(function (name) {
			var index = metricsNames.indexOf(name),
				metricKey = metricsKeys[index];
			return {
				name: name,
				isMetric: index != -1,
				dataType: dataTypeMap[metricKey],
				unit: unitMap[metricKey],
				format: formatMap[metricKey]
			};
		});
		formatRs.data = rows.slice(1, rows.length);
		formatRs.orders = (function () {
			// 维度显示在指标前面,该处表示默认设置第一个指标降序
			var result = [];
			// widget中排序信息优先级最高
			if (sortMap && sortMap.length) {
				result = sortMap.reduce(function (prev, curr) {
					var key = Object.keys(curr)[0],
						value = curr[key];
					var index = allKeys.indexOf(key);
					if (index != -1) {
						prev.push([index, value]);
					}
					return prev;
				}, []);
			}
			if (result.length === 0) {
				result = metricsKeys.length ? [[dimensions.length, 'desc']] : [[0, 'desc']]
			}
			return result;
		})();
		formatRs.boundary = (function () {
			var index = 0;
			if (metricsKeys.length > 0) {
				index = dimensions.length;
			}
			return index;
		})();
		formatRs.getKeyByIndex = function (index) {
			return allKeys[index];
		};
		return formatRs;
	},

	toTable: function (widget, wsData) {
		var tableData = [], headData = [];

		var formattedData = this.format(widget, wsData);
		headData.push(formattedData.columns.reduce(function (prev, curr) {
			prev.push(curr.name);
			return prev;
		}, []));
		tableData.push(headData);

		if (formattedData.data) {
			formattedData.data.forEach(function (rows) {
				var rowData = [];
				rows.forEach(function (value, index) {
					var column = formattedData.columns[index];
					var formatter = formatterUtil.getFormatter(column.dataType, column.unit, column.format);
					rowData.push(formatter(value));
				});
				tableData.push(rowData);
			});
		}

		return tableData;
	},

	formatDemoData: function(demoData){
		var data = demoData.series[0].data;
		if(!data || data.length === 0) return [];

		var formatRs = {
			columns: null,
			data: null,
			sort: null
		};

		var hiddenColumns = demoData.fixSortColumnIndex && Object.values(demoData.fixSortColumnIndex);

		formatRs.columns = data[0].map(function (name, index) {
			var isMetric = index >= demoData.firstMetricsIndex;

			// 该列设置按其它列来排序
			var sortByColumnIndex = undefined;
			if(demoData.fixSortColumnIndex){
				sortByColumnIndex = demoData.fixSortColumnIndex[index];
			}

			return {
				name: name,
				isMetric: isMetric,
				dataType: isMetric ? 'NUMBER' : 'STRING',
				isHidden: hiddenColumns && hiddenColumns.indexOf(index) != -1,
				sortByColumnIndex: sortByColumnIndex
			};
		});
		formatRs.data = data.slice(1, data.length);
		formatRs.boundary = demoData.firstMetricsIndex;
		formatRs.orders = [[formatRs.boundary, 'desc']];

		return formatRs;
	}
};
