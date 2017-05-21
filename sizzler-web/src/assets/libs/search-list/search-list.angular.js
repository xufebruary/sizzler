(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(['angular', './search-list'], factory);
	}else if (typeof exports !== 'undefined') {
		var angular = require('angular');
		var searchlist = require('./search-list');
		factory(angular, searchlist);
	}else {
		factory(window.angular, window.searchlist);
	}
})(function(angular, searchlist){

	function searchListDirective(){
		return {
			restrict: 'E',
			scope: {
				treeList: '<',
				selectedId: '<',
				onSelect: '&',
				emptyTip: '@'
			},
			link: function(scope, element, attrs){
				// 初始化
				var instance = new searchlist(element[0], {
					callback: function(data){
						scope.$apply(function(){
							scope.onSelect({data: data});
						});
					}
				});

				scope.$watch('treeList', function(curr, prev){
					instance.setOptions({
						selectedId: scope.selectedId,
						tips: {
							empty: scope.emptyTip
						}
					});
					instance.render(curr);
				});
			}
		};
	}

	angular.module('pt.search-list', [])
	.directive({
		'searchList': searchListDirective
	});
});


