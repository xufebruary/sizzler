/**
 * list 数据格式为:
 * [{
 * 	 id
 * 	 name
 * 	 child: [{
 * 	 	id
 * 	 	name
 * 	 }]
 * }]
 *
 * @return {{scope: {list: string, onSelect: string}}}
 */
function searchList(){
	return {
		restrict: 'E',
		scope: {
			list: '<',  // 要展示的数据列表
			onSelect: '&'  // 回调
		}
	}
}

export default searchList;
