## pttable

> 轻量级的表格控件

## 支持的功能

### 分页

1. 是否启用分页特性；

### 排序

1. 支持多列排序，表头上只显示最后一次排序提示；
2. 每列是否排序配置；
3. 输出内容格式化；
4. 自定义排序方式；

## 使用方式

1. 创建pttable实例对象。

   ```javascript
   var instance = new pttable(HtmlElement);
   ```

2. 设置options对象。

   ```javascript
   instance.setOptions(options);
   ```

3. 进行render。

   ```javascript
   instance.render();
   ```

## options介绍

``` javascript
var defaults = {
    features: {
        pagination: true, // 是否启用分页特性
        sortable: true, // 是否启用排序特性
        scrollX: false, // 目前不支持
    },
    // 每列配置信息
    columns: [{
        // 列名
        name: '',
      	// 设置列宽,值为css支持的有效值
        width: '',
        // 自定义输出
        formatter: function(value){

        },
        // 该列是否可见
        isHidden: false,
        // 该列按某列来排序
        sortByColumnIndex: 1,
        // 是否可排序
        sortable: false,
        // 自定义排序方式，direction大于0表示升序，反之降序
        comparator: function(a, b, direction){

        }
    }],
    // 排序，可以设置多个，第一个与列对应，第二个表示排序方式
    orders: [[0, 'asc']],
    // tbody数据
    data: [],
    // 分页配置
    pagination: {
        currentPage: 1,
        pageLength: 10
    },
    // 如果返回值为true表示会继续进行排序，为false则不执行
    onBeforeSort: function(index, direction){
        return true;
    }
};
```

## 示例

```javascript
var options = {
    features: {
        pagination: true,
        sortable: true
    },
    columns: [{
        name: 'skill',
        width: '50px'
    }, {
        name: 'year',
        width: '30%',
        formattor: function(value) {
            return value + "年";
        }
    }, {
        name: 'method',
        width: '50px',
        sortable: false
    }, {
        name: 'popularation',
        width: '40%'
    }, {
        name: 'difficulty',
        width: '30%',
        // 对难易程度来排序
        comparator: function(a, b, direction) {
            var order = ['easy', 'normal', 'hard'];
            if (a === b) return 0;
            return direction > 0 ? order.indexOf(a) - order.indexOf(b) : order.indexOf(b) - order.indexOf(a);
        }
    }],
    orders: [
        [4, 'desc'] // 默认对difficulty列进行降序排序
    ],
    pagination: {
        currentPage: 1,
        pageLength: 5
    },
    callbacks: {
      	// 该例子模拟服务器端排序
        beforeSort: function(index, direction) {
            container.querySelector('tbody').innerHTML = 'loading...';
            setTimeout(function() {
                var data = [
                    ['python', '1992', 'a', '4', 'normal'],
                    ['html', '1993', 'b', '3', 'easy']
                ];
                instance.setOptions({
                    data: data,
                    orders: [
                        [index, direction]
                    ]
                }).render();
            }, 2000);
            return false;
        }
    }
};
```

## 计划支持的特性

1. 表头可折行, 设置字体大小(样式)
2. 指标数值表现形式
3. 色值热图
4. 序列号
5. 搜索
6. scrollX 和 scrollY
7. 高度自适应容器

## Bug

1. 分页算法




