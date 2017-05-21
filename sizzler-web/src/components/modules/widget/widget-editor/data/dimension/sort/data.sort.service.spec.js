import DataSortService from './data.sort.service';
import sorterNameMapping from './sorter-name.config';

describe('widget editor dataSortService', function(){
	var dataSortService = null;

	beforeEach(() => {
		dataSortService = new DataSortService();
	});

	describe('test getSorterByDataType', () => {

		it('string dataType', () => {
			var expected = [{name: sorterNameMapping.string.asc, direction:'asc'}, {name:sorterNameMapping.string.desc, direction:'desc'}];
			var result = dataSortService.getSorterByDataType('STRING');
			expect(result.length).to.be.equal(2);
			expect(result).to.deep.equal(expected);
		});

		it('date dataType', () => {
			var expected = [{name: sorterNameMapping.date.asc, direction:'asc'}, {name:sorterNameMapping.date.desc, direction:'desc'}];
			var result = dataSortService.getSorterByDataType('DATE');
			expect(result.length).to.be.equal(2);
			expect(result).to.deep.equal(expected);
		});

		it('number dataType', () => {
			var expected = [{name: sorterNameMapping.number.asc, direction:'asc'}, {name:sorterNameMapping.number.desc, direction:'desc'}];
			var result = dataSortService.getSorterByDataType('NUMBER');
			expect(result.length).to.be.equal(2);
			expect(result).to.deep.equal(expected);
		});


	});

	describe('test getSortNameByDataType', () => {
		it('string', () => {
			var asc = dataSortService.getSortNameByDataType('STRING', 'asc');
			var desc = dataSortService.getSortNameByDataType('STRING', 'desc');

			expect(asc).to.be.equal(sorterNameMapping.string.asc);
			expect(desc).to.be.equal(sorterNameMapping.string.desc);
		});

		it('number', () => {
			var asc = dataSortService.getSortNameByDataType('NUMBER', 'asc');
			var desc = dataSortService.getSortNameByDataType('NUMBER', 'desc');

			expect(asc).to.be.equal(sorterNameMapping.number.asc);
			expect(desc).to.be.equal(sorterNameMapping.number.desc);
		});

		it('date', () => {
			var asc = dataSortService.getSortNameByDataType('DATE', 'asc');
			var desc = dataSortService.getSortNameByDataType('DATE', 'desc');

			expect(asc).to.be.equal(sorterNameMapping.date.asc);
			expect(desc).to.be.equal(sorterNameMapping.date.desc);
		});
	});
});
