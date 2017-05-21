/**
 * Created by jianqing on 16/12/28.
 */




function tableTitleTextOverDirective() {
	return {
		restrict: 'A',
		priority: 0,
		link: function link(scope, elem, attrs) {
			var w = elem.parent().width();
			console.log(w);
			elem.width(w + 'px').addClass('block text-over');
		}
	}
}

export default tableTitleTextOverDirective;
