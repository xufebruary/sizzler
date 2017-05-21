## 重构时步骤

### 梳理需求

1. 邮箱系统是否存在校验，并提示
2. 邮件发送成功后，页面会文案提示
3. 邮箱不存在提示后，输入框获取焦点提示消失

### 定义新的模块 （模块名为pt.moduleName）

1. 先将之前controller引入，并修改router引入该模块

   ```javascript
   require.ensure([], function (require) {
     	//default是babel给添加上的属性，我们要export的对象在这里面。
   	var mod = require('components/modules/forgotPassword/forgotPassword.module').default;
   	$ocLazyLoad.load({
   		name: mod.name
   	});
   	deferred.resolve(mod.controller);
   });
   ```

2. 按照代码规范来重构controller

3. 将业务逻辑提取到service中，并将该service添加到该模块里。

4. 配置需要请求的resources。

5. 在router中添加controllerAs:'$ctrl'，并修改html。
