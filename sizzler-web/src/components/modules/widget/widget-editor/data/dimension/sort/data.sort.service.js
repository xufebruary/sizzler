import arrayUtils from 'utils/array.utils';
import formatterUtils from 'utils/formatter.utils';
import sorterNameMapping from './sorter-name.config';

function dataSortService(){

}

var DIMENSION = 'dimensionValue',
	METRIC = 'metricsValue',
	DESC = 'desc',
	ASC = 'asc';

dataSortService.prototype = {
	constructor: dataSortService,

	/**
	 * 获取默认排序对象,规则见README中"默认排序规则"
	 * @param dimensions
	 * @param metrics
	 * @returns {*}
     */
	getDefaultOrders: function(dimensions, metrics) {
		if(arrayUtils.isEmpty(dimensions)){
			throw new Error('dimensions array can not be empty!');
		}

		var dimension = dimensions[0],
			dataType = dimension.dataType;

		// 日期类型则默认按维度排序
		if(formatterUtils.isDate(dataType)){
			return {
				type: DIMENSION,
				order: ASC,
				id: dimension.uuid
			};
		}

		if(arrayUtils.isNotEmpty(metrics)){
			return {
				type: METRIC,
				order: DESC,
				id: metrics[0].uuid
			};
		}

		return {
			type: DIMENSION,
			order: formatterUtils.isNumber(dataType) ? DESC : ASC,
			id: dimension.uuid
		};
	},

	/**
	 * 根据数据类型获取排序要展示的名称,规则参见README中"排序文案规则"
	 * @param dataType
	 * @param direction
	 * @returns {*}
     */
	getSortNameByDataType: function(dataType, direction){
		if(formatterUtils.isNumber(dataType)) return sorterNameMapping['number'][direction];
		if(formatterUtils.isDate(dataType)) return sorterNameMapping['date'][direction];
		return sorterNameMapping['string'][direction];
	},

	/**
	 * 根据数据类型获取排序不同文案列表
	 * @param dataType
	 * @returns {*}
     */
	getSorterByDataType: function(dataType){
		return [ASC, DESC].reduce((prev, curr) => {
			var name = this.getSortNameByDataType(dataType, curr);
			prev.push({
				name: name,
				direction: curr
			});
			return prev;
		}, []);
	},

	getCurrentSorter: function(sorters, currentOrder, type, dataType){
		if(currentOrder.type === type) {
			return sorters.find((o) => {
				return o.direction === currentOrder.order;
			});
		}
		if(formatterUtils.isNumber(dataType)){
			return sorters[sorters.length - 1];
		}
		return sorters[0];
	},

	/**
	 * 获取维度默认展示的排序对象
	 * @param sorters
	 * @param currentOrder
	 * @returns {*}
     */
	getCurrentDimensionSorter: function(sorters, currentOrder, dataType){
		return this.getCurrentSorter(sorters, currentOrder, DIMENSION, dataType);
	},

	/**
	 * 获取指标魔刃战士的排序对象
	 * @param sorters
	 * @param currentOrder
	 * @returns {*}
     */
	getCurrentMetricSorter: function (sorters, currentOrder) {
		return this.getCurrentSorter(sorters, currentOrder, METRIC, 'NUMBER');
	},

	/**
	 * 或许默认展示指标对象
	 * @param metrics
	 * @param currentOrder
	 * @returns {*}
     */
	getMetricByUUID: function(metrics, currentOrder){
		if(arrayUtils.isEmpty(metrics)) return null;
		if(currentOrder.type !== METRIC) return metrics[0];
		var result = metrics.find((o) => {
			return o.uuid == currentOrder.id;
		});
		return result !== undefined ? result : metrics[0];
	}

};

export default dataSortService;
