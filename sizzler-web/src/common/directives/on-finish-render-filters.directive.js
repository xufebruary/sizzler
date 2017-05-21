/**
 * ng-repeat循环后传播事件
 */


onFinishRenderFiltersDirective.$inject = ['$timeout'];
function onFinishRenderFiltersDirective($timeout) {
    return {
        restrict: 'A',
        priority: 0,
        link: function (scope, element, attr) {
            if (scope.$last === true) {
                $timeout(function () {
                    scope.$emit('ngRepeatFinished');
                });
            }
        }
    };
}

export default onFinishRenderFiltersDirective;



