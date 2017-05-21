import arrayUtils from './array.utils';

describe('array.utils', function () {
	var persons;

	beforeEach(function () {
		persons = [{
			name: 'zhsang',
			math: 2,
			english: 5
		}, {
			name: 'lisi',
			math: 4,
			english: 7
		}, , {
			name: 'lisi',
			math: 3,
			english: 6
		}];

	})

	describe('max', function () {

		it('test custom fn 01', function () {
			var expected = {
				name: 'lisi',
				math: 4,
				english: 7
			};

			var max = arrayUtils.max(persons, function (person) {
				return person.math + person.english;
			});

			expect(max).to.deep.equal(expected);

		});

		it('test custom fn 02', function () {
			var expected = {
				name: 'lisi',
				math: 4,
				english: 7
			};

			var max = arrayUtils.max(persons, function (person) {
				return person.english;
			});

			expect(max).to.deep.equal(expected);

		});

		it('test numbers', function(){
			var numbers = [1,23,4,5];

			expect(arrayUtils.max(numbers)).to.be.equal(23);
		});
	});
});
