/**
 * Created by jianqing on 16/12/28.
 */
/**
 * ng-repeat循环后执行指定方法
 */

onRepeatFinishDirective.$inject = ['$timeout'];
function onRepeatFinishDirective($timeout) {
	return {
		restrict: 'A',
		priority: 0,
		link: function (scope, element, attrs) {
			//计算表达式的值
			var fun = attrs.onRepeatFinish;

			if (scope.$last === true) {
				$timeout(function () {
					console.log(fun);
					eval('scope.' + fun);
				});
			}
		}
	};
}

export default onRepeatFinishDirective;
