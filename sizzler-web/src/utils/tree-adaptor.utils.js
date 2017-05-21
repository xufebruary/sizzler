export default {
    //googleanalysis 数据源 profile list数据结构化
    formatGAProfileList: function(obj) {
        if (obj && obj.length === 0) return obj;
        var newItem = [];
        obj.map(function(item, index) {
            newItem.push({
                'id': item['accountId'],
                'name': item['accountName'],
                'child': []
            });
            if (item['webproperties'] && item['webproperties'].length > 0) {
                item['webproperties'].map(function(item2, index2) {
                    newItem[index]['child'].push({
                        'id': item2['webpropertyId'],
                        'name': item2['webpropertyName'],
                        'child': []
                    });
                    if (item2['profiles'] && item2['profiles'].length > 0) {
                        item2['profiles'].map(function(item3) {
                            newItem[index]['child'][index2]['child'].push({
                                'id': item3['profileId'],
                                'name': item3['profileName']
                            });
                        });
                    }
                });
            }
        });
        return newItem;
    },
	//世界地图数据格式化
	/**
	 *
	 * @param obj
	 * @param locale  国际化key，zh_CN/en_US/ja_JP
	 * @returns {*}
	 */
	formatMapList: function(obj,locale){
		"use strict";
		if (obj && obj.length === 0) return obj;
		var newItem = [];
		obj.map(function(item, index) {
			newItem.push({
				'name': item['continent'][locale],
				'child': []
			});
			if(item['groupCountries'] && item['groupCountries'].length > 0){
				item['groupCountries'].map(function(item2){
					newItem[index]['child'].push({
						'id': item2['code'],
						'name': item2['name'][locale],
						'path': item2['path']
					})
				});
			}
		});
		return newItem;
	},

    getItem: function searchTree(treeList, id) {
        if (treeList == null) return undefined;

        var result = null;

        for(var i=0; i<treeList.length; i++){
        	var tree = treeList[i];
        	if(tree.child && tree.child.length){
        		var found = searchTree(tree.child, id);
 				if(found != null){
 					result = found;
 					break;
 				}
        	}else{
        		if(tree.id == id){
        			result = tree;
        		}
        	}
        }

        return result;

    }
}
