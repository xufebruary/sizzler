'use strict';

uiFocusDirective.$inject = ['$timeout', '$parse'];
function uiFocusDirective($timeout, $parse) {
    return {
        link: function (scope, element, attr) {
            var model = $parse(attr.uiFocus);
            scope.$watch(model, function (value) {
                if (value === true) {
                    $timeout(function () {
                        var t = element[0].value;
                        element[0].value = "";
                        element[0].focus();
                        element[0].value = t;
                    });
                }
            });
            //element.bind('blur', function() {
            //   scope.$apply(model.assign(scope, false));
            //});
        }
    };
}

export default uiFocusDirective;
