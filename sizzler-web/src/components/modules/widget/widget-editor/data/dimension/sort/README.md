> 对数据进行排序组件, 其中数据可能为维度也可能为指标。

## 组件接口

```html
<data-sort
    title            // 面板名称
    metrics          // 要排序的维度列表
    dimensions       // 要排序的指标列表
    default-orders   // 默认排序设置
    render-api       // 对外暴露渲染接口
    on-submit        // 提交结果回调
    on-cancel        // 取消回调
></data-sort>
```

1. defaultOrders属性说明

	数据格式为

	```json
	[{
	    type: 'dimensionValue|metricsValue',
	    id: 'dimensionId|metricId',
	    order: 'desc|asc'
	}]
	```

	设计为数组格式,方便日后扩展支持多维度指标排序。

2. onSubmit回调函数说明

	回调函数在点击保存时执行,参数格式为orders(Array),数据格式同上。

3. onCancel回调函数说明

	没有参数, 此时可以将该面板隐藏

## 需求

1. 排序面板显示所操作维度的名称
2. 默认排序规则
    1. 指标、维度默认选中规则
        1. 如果维度类型为date或者指标为空,则默认按维度排序
        2. 如果指标不为空，则按指标进行排序
    2. 默认排序规则
        1. 如果按指标排序,则为降序
        2. 如果按维度排序,数值类型为降序,其它都是升序
3. 排序文案规则
    1. 数值型 asc | desc
    2. 日期型 chronological | reverse chronological
    3. 其它   az | za
