import treeUtils from './tree-adaptor.utils';

describe('tree adaptor utils', function () {
	var raw,mapList;

	beforeEach(function(){
		raw = [{
			"accountId": "25101074",
			"accountName": "马自达（中国）",
			"webproperties": [
				{
					"webpropertyId": "UA-25101074-1",
					"accountId": "25101074",
					"accountName": "马自达（中国）",
					"webpropertyName": "http://www.mazda.com.cn",
					"profiles": [
						{
							"accountName": "马自达（中国）",
							"profileName": "马自达中国车展专题2012",
							"webpropertyName": "http://www.mazda.com.cn",
							"webpropertyId": "UA-25101074-1",
							"profileId": "58628067",
							"accountId": "25101074"
						}
					]
				}
			]
		}];
		mapList = [{
			"continent":{
				"en_US":"Asia",
				"ja_JP":"アジア",
				"zh_CN":"亚洲"
			},
			"groupCountries":[{
				"code":"Afghanistan",
				"continent":{
					"en_US":"Asia",
					"ja_JP":"アジア",
					"zh_CN":"亚洲"
				},
				"name":{
					"en_US":"Afghanistan",
					"ja_JP":"アフガニスタン",
					"zh_CN":"阿富汗"
				},
				"path":"countries/af/af-all.js"
			}]
		}];
	});

	describe('formatGAProfileList:', function () {
		it('deep equal', function () {
			var copyRaw = JSON.parse(JSON.stringify(raw));

			var result = treeUtils.formatGAProfileList(raw);

			var expectResult = [{
				"id": "25101074",
				"name": "马自达（中国）",
				"child": [
					{
						"id": "UA-25101074-1",
						"name": "http://www.mazda.com.cn",
						"child": [
							{
								"name": "马自达中国车展专题2012",
								"id": "58628067"
							}
						]

					}
				]
			}];

			expect(result).to.deep.equal(expectResult);

			expect(raw).to.deep.equal(copyRaw);

		});
	});

	describe('formatMapList',function(){
		it('map list should deep equal,no change origin list',function(){
			var copyMapList = JSON.parse(JSON.stringify(mapList));

			var result = treeUtils.formatMapList(mapList,"zh_CN");

			var expectResult = [{
				"name": "亚洲",
				"child": [
					{
						"id": "Afghanistan",
						"name": "阿富汗",
						"path":"countries/af/af-all.js"
					}
				]
			}];

			expect(result).to.deep.equal(expectResult);

			expect(mapList).to.deep.equal(copyMapList);
		})
	});

	describe('getItem', function(){
		it('it should can be found', function(){
			var expectItem = {
				id: 5
			};
			var treeData = [{
				id: 0,
				child: [{
					id: 12,
					child: []
				}]
			},{
				id: 1,
				child: [{
					id: 2
				},{
					id: 3,
					child: [{id: 4}, {id:5}, {id:6}]
				}]
			}];
			var foundItem = treeUtils.getItem(treeData, 5);
			expect(foundItem).to.deep.equal(expectItem);
		});
	});
});
