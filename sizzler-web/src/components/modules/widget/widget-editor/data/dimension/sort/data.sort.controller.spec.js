import dataSortController from './data.sort.controller';
import DataSortService from './data.sort.service';
import sorterNameMapping from './sorter-name.config';


describe('widget editor dataSortController', function(){
	var $ctrl, dataSortService, track;

	var DIMENSION = 'dimensionValue',
		METRIC = 'metricsValue';

	var dateSorters = [{name: sorterNameMapping.date.asc, direction:'asc'}, {name:sorterNameMapping.date.desc, direction:'desc'}];
	var numberSorters = [{name: sorterNameMapping.number.asc, direction:'asc'}, {name:sorterNameMapping.number.desc, direction:'desc'}];
	var stringSorters = [{name: sorterNameMapping.string.asc, direction:'asc'}, {name:sorterNameMapping.string.desc, direction:'desc'}];

	beforeEach(function(){
		dataSortService = new DataSortService();
		track = {log: function(){}};
		angular.mock.inject(($controller) => {
			$ctrl = $controller;
		});
	});

	describe('render', function(){
		// 指标和默认排序为空
		describe('metrics and defaultSorts are null', function(){
			function createController(dataType){
				var data = {
					dimensions: [{uuid: '1', name:'date', dataType: dataType}]
				};
				return $ctrl(dataSortController, {
					'DataSortService': dataSortService,
					'Track': track
				}, data);
			}

			// 数据类型为日期
			it('data type is date', function(){
				var ctrl = createController('DATE');
				var expectState = {
					currentSorter: {
						id: '1',
						order: 'asc',
						type: DIMENSION
					},
					currentMetric: null,
					dimensionSorters: {
						current: dateSorters[0],
						options: dateSorters
					},
					metricSorters: {
						current: null,
						options: []
					}
				};

				expect(ctrl.state).to.deep.equal(expectState);
			});

			// 数据类型为数字
			it('data type is number', function(){
				var ctrl = createController('NUMBER');
				var expectState = {
					currentSorter: {
						id: '1',
						order: 'desc',
						type: DIMENSION
					},
					currentMetric: null,
					dimensionSorters: {
						current: numberSorters[1],
						options: numberSorters
					},
					metricSorters: {
						current: null,
						options: []
					}
				};
				expect(ctrl.state).to.deep.equal(expectState);
			});

			// 数据类型为字符
			it('data type is string', function(){
				var ctrl = createController('STRING');
				var expectState = {
					currentSorter: {
						id: '1',
						order: 'asc',
						type: DIMENSION
					},
					currentMetric: null,
					dimensionSorters: {
						current: stringSorters[0],
						options: stringSorters
					},
					metricSorters: {
						current: null,
						options: []
					}
				};

				expect(ctrl.state).to.deep.equal(expectState);
			});
		});

		// 指标不为空，默认排序为空
		describe('metrics is not null and defaultSorts is null', function(){
			var metrics = [{uuid:'2', name:'metric'}]
			function createController(dataType){
				var data = {
					metrics: metrics,
					dimensions: [{uuid: '1', name:'dimension', dataType: dataType}]
				};
				return $ctrl(dataSortController, {
					'DataSortService': dataSortService,
					'Track': track
				}, data);
			}

			// 数据类型为日期
			it('data type is date', function(){
				var ctrl = createController('DATE');
				var expectState = {
					currentSorter: {
						id: '1',
						order: 'asc',
						type: DIMENSION
					},
					currentMetric: metrics[0],
					dimensionSorters: {
						current: dateSorters[0],
						options: dateSorters
					},
					metricSorters: {
						current: numberSorters[1],
						options: numberSorters
					}
				};

				expect(ctrl.state).to.deep.equal(expectState);
			});

			// 数据类型为数字
			it('data type is number', function(){
				var ctrl = createController('NUMBER');
				var expectState = {
					currentSorter: {
						id: '2',
						order: 'desc',
						type: METRIC
					},
					currentMetric: metrics[0],
					dimensionSorters: {
						current: numberSorters[1],
						options: numberSorters
					},
					metricSorters: {
						current: numberSorters[1],
						options: numberSorters
					}
				};
				expect(ctrl.state).to.deep.equal(expectState);
			});

			// 数据类型为字符
			it('data type is string', function(){
				var ctrl = createController('STRING');
				var expectState = {
					currentSorter: {
						id: '2',
						order: 'desc',
						type: METRIC
					},
					currentMetric: metrics[0],
					dimensionSorters: {
						current: stringSorters[0],
						options: stringSorters
					},
					metricSorters: {
						current: numberSorters[1],
						options: numberSorters
					}
				};

				expect(ctrl.state).to.deep.equal(expectState);
			});
		});

		// 指标和默认排序都不为空
		describe('defaultSorts is not null', function(){
			var metrics = [{uuid:'2', name:'metric'}, {uuid:'3', name:'metric03'}];
			var dimensions = [{uuid: '1', name:'dimension', dataType: 'NUMBER'}];
			function createController(sort){
				var data = {
					metrics: metrics,
					defaultSorts: [sort],
					dimensions: dimensions
				};
				return $ctrl(dataSortController, {
					'DataSortService': dataSortService,
					'Track': track
				}, data);
			}

			// 按维度降序排序
			it('sort by dimension desc', function(){
				var sorter = {id: '1', type: DIMENSION, order: 'desc'};
				var ctrl = createController(sorter);
				var expectState = {
					currentSorter: sorter,
					currentMetric: metrics[0],
					dimensionSorters: {
						current: numberSorters[1],
						options: numberSorters
					},
					metricSorters: {
						current: numberSorters[1],
						options: numberSorters
					}
				};

				expect(ctrl.state).to.deep.equal(expectState);
			});

			// 按维度升序排序
			it('sort by dimension asc', function(){
				var sorter = {id: '1', type: DIMENSION, order: 'asc'};
				var ctrl = createController(sorter);
				var expectState = {
					currentSorter: sorter,
					currentMetric: metrics[0],
					dimensionSorters: {
						current: numberSorters[0],
						options: numberSorters
					},
					metricSorters: {
						current: numberSorters[1],
						options: numberSorters
					}
				};

				expect(ctrl.state).to.deep.equal(expectState);
			});

			// 按指标降序排序
			it('sort by metric desc', function(){
				var sorter = {id: '3', type: METRIC, order: 'desc'};
				var ctrl = createController(sorter);
				var expectState = {
					currentSorter: sorter,
					currentMetric: metrics[1],
					dimensionSorters: {
						current: numberSorters[1],
						options: numberSorters
					},
					metricSorters: {
						current: numberSorters[1],
						options: numberSorters
					}
				};

				expect(ctrl.state).to.deep.equal(expectState);
			});

			// 按指标升序排序
			it('sort by metric asc', function(){
				var sorter = {id: '2', type: METRIC, order: 'asc'};
				var ctrl = createController(sorter);
				var expectState = {
					currentSorter: sorter,
					currentMetric: metrics[0],
					dimensionSorters: {
						current: numberSorters[1],
						options: numberSorters
					},
					metricSorters: {
						current: numberSorters[0],
						options: numberSorters
					}
				};

				expect(ctrl.state).to.deep.equal(expectState);
			});

		});
	});

	describe('sltSortType', function(){
		var metrics = [{uuid:'2', name:'metric'}, {uuid:'3', name:'metric03'}];
		var dimensions = [{uuid: '1', name:'dimension', dataType: 'NUMBER'}];
		function createController(sort){
			var data = {
				metrics: metrics,
				defaultSorts: [sort],
				dimensions: dimensions
			};
			return $ctrl(dataSortController, {
				'DataSortService': dataSortService,
				'Track': track
			}, data);
		}

		// 由维度变为指标
		it('sort type change from dimension to metric', function(){
			var sorter = {id: '1', type: DIMENSION, order: 'asc'};
			var ctrl = createController(sorter);
			ctrl.state.currentSorter.type = METRIC;
			ctrl.sltSortType();

			var expectState = {
				currentSorter: {id: '2', type: METRIC, order: 'desc'},
				currentMetric: metrics[0],
				dimensionSorters: {
					current: numberSorters[0],
					options: numberSorters
				},
				metricSorters: {
					current: numberSorters[1],
					options: numberSorters
				}
			};

			expect(ctrl.state).to.deep.equal(expectState);
		});

		// 由指标变为维度
		it('sort type change from metric to dimension', function(){
			var sorter = {id: '3', type: METRIC, order: 'asc'};
			var ctrl = createController(sorter);
			ctrl.state.currentSorter.type = DIMENSION;
			ctrl.sltSortType();

			var expectState = {
				currentSorter: {id: '1', type: DIMENSION, order: 'desc'},
				currentMetric: metrics[1],
				dimensionSorters: {
					current: numberSorters[1],
					options: numberSorters
				},
				metricSorters: {
					current: numberSorters[0],
					options: numberSorters
				}
			};

			expect(ctrl.state).to.deep.equal(expectState);
		});
	});

});
