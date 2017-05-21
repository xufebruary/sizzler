import resourceWrapper from './resourceWrapper';

var resources = [
	// 添加widget
	{
		name: 'add',
		method: 'post',
		url: '/pt/widgets/add'
	},

	// 更新widget
	{
		name: 'update',
		method: 'post',
		url: '/pt/widgets/update'
	},

	// 删除widget
	{
		name: 'delete',
		method: 'post',
		url: '/pt/widgets/del/{widgetId}'
	},

	// 获取单个widget
	{
		name: 'getWidgetInfo',
		method: 'get',
		url: '/pt/widgets/getOne/{widgetId}'
	},

	// 获取widget列表
	{
		name: 'getWidgets',
		method: 'get',
		url: '/pt/widgets/widgetWithLayout/'
	},

	// 获取widget列表（移动端）
	{
		name: 'getWidgetsByMobile',
		method: 'get',
		url: '/pt/widgets/widgetWithLayout/mobile/'
	},

	//更新widget单独字段
	{
		name: 'updateWidgetBase',
		method: 'post',
		url: '/pt/widgets/updateBaseWidget'
	},

	//更新widget别名
	{
		name: 'alias',
		method: 'put',
		url: '/pt/widgets/metrics-dimensions/alias'
	}
];

export default resourceWrapper(resources);
