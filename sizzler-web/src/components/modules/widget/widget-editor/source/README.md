## 需求
1. 大前提是接入一个新的数据源不修改旧代码。
    1. 新数据源完全可以套用之前模板
    2. 新数据源需要新的模板
    3. 新数据源流程变化，例如需要四步

## 重构思路
1. 由里向外的方式
2. 按angular guide来编写组件，然后通过回调通知父元素

## 想法
1. 拉上服务器端讨论数据源最终理想的接入方式
2. 按照确定的接入方式来定义新的接口协议
3. 前端重构时按照新的接口协议来做

## 方案

1. 每个数据源接入步骤可配置
2. 每个步骤对应的模板可配置
3. 对数据源组件（整体）的输入和输出要抽象为统一数据格式


## 问题
1. on-finish-render-filters 的作用

## 其它

### 档案搜索情况
1. 非mysql和ga账户
2. mysql账户
3.

## 从widget-editor-dir输入

1. dsConfig
2. editor对象中的，包括：dsId／dsCode／accountName／disabled／pop／documentClick等等
3. saveData方法
4. isEveryCustomWidgetHaveProfileId(自定义widget使用)

## 从dashboard.ctrl输入
1. modal.editorNow

## 输出
1. modal.editorNow.variables[0].accountName/connectionId/ptoneDsInfoId/dsCode/profileId/variableGraphId
2. modal.editorNow.baseWidget.isExample/isDemo/graphName/ptoneGraphInfoId/dateKey
3. editor.dsId/dsCode/disabled



