/**
 * varsion 1.0.0
 */
;(function (parent, fun) {
	if (typeof exports !== 'undefined' && typeof module !== 'undefined') {
		module.exports = fun();
	} else if (typeof define === 'function' && typeof define.amd === 'object') {
		define(fun);
	} else if (typeof define === 'function' && typeof define.cmd === 'object') {
		define(fun);
	} else {
		parent.pttable = fun();
	}
})(this, function(){
	// 常量
	var EMPTY = '',
		ROLE_PAGINATION_BTN = 'ROLE_PAGINATION_BTN', // 分页按钮
		ROLE_THEADER_TH = 'ROLE_THEADER_TH', // 表头
		PAGINATION_PLACEHOLDER = '...',
		PAGINATION_LENGTH = 7,
		PTTABLE_SELECTOR = 'pt-table-wrapper',
		PAGINATION_SELECTOR = 'pagination',
		HEADER_TPL = [
			'<thead>',
				'<tr>',
				'<% for(var i = 0; i < this.columns.length; i++ ){ %>',
					'<% var column = this.columns[i]; %>',
					'<% if(!column.isHidden){ %>',
						'<th title="<% column.name %>" style="<%this.getStyle(column)%>" class="<%column.direction%>" data-role="<%role%>" data-index="<%i%>" data-sortable="<%this.sortable%>" data-direction="<%column.direction%>" >',
						'<% column.name %>',
						'</th>',
					'<% }%>',
				'<% } %>',
				'</tr>',
			'</thead>'
		].join(''),
		BODY_TPL = [
			'<tbody>',
				'<% if(this.data == null || this.data.length == 0){ %>',
					'<tr><td class="empty" colspan="<%this.columns.length%>"><% this.messages.empty %></td></tr>',
				'<% }else{ %>',
					'<% for(var i = 0; i < this.data.length; i++ ){ %>',
					'<tr>',
						'<% var row = this.data[i]; %>',
						'<% for(var col = 0; col < row.length; col++){ %>',
							'<% var column = this.columns[col]; %>',
							'<% if(!column.isHidden){%>',
								'<% var value = column.formatter(row[col]); %>',
								'<td title="<% value %>"><% value %></td>',
							'<% } %>',
						'<% } %>',
					'</tr>',
					'<% } %>',
				'<% } %>',
			'</tbody>'
		].join(''),
		FOOTER_TPL = [
			'<div class="'+PAGINATION_SELECTOR+'">',
				'<a data-role="<%role%>" data-num="<%this.currentPage - 1%>" class=\"<%this.currentPage == 1 ? "disable": "" %>\" > < </a>',
				'<span>',
				'<% for(var i = 0; i < this.items.length; i++){ %>',
					'<% if(this.items[i] == this.placeholder){ %>',
						'<span><% this.items[i] %></span>',
					'<%}else{%>',
						'<a data-role="<%role%>" class="<%this.items[i] == this.currentPage ? \"active\" : \"\" %>" data-num="<% this.items[i] %>"><% this.items[i] %></a>',
					'<%} %>',
				'<% } %>',
				'</span>',
				'<a data-role="<%role%>" data-num="<%this.currentPage + 1%>" class=\"<%this.currentPage == this.pageCount ? "disable": "" %>\" > > </a>',
			'</div>'
		].join('');

	var defaults = {
		features: {
			pagination: true,
	        sortable: true,
	        scrollX: false
		},
		columns: null,
		orders: null,
		data: null,
		pagination: {
			currentPage: 1,
			pageLength: 10
		},
		messages: {
			empty: 'No data to show',
		},
		// 如果返回值为true表示会继续进行排序，为false则不执行
        onBeforeSort: function(index, direction){
            return true;
        }
	};

	/**
	 * 浅复制
	 * @param obj 目标对象
	 * @param props 要复制的对象
	 * @return {*}
	 */
	function extend(obj, props) {
		for (var prop in props) {
			if (props.hasOwnProperty(prop)) {
				obj[prop] = props[prop];
			}
		}
		return obj;
	}

	function deepclone(item) {
	    if (!item) { return item; } // null, undefined values check

	    var types = [ Number, String, Boolean ],
	        result;

	    // normalizing primitives if someone did new String('aaa'), or new Number('444');
	    types.forEach(function(type) {
	        if (item instanceof type) {
	            result = type( item );
	        }
	    });

	    if (typeof result == "undefined") {
	        if (Object.prototype.toString.call( item ) === "[object Array]") {
	            result = [];
	            item.forEach(function(child, index, array) {
	                result[index] = deepclone( child );
	            });
	        } else if (typeof item == "object") {
	            // testing that this is DOM
	            if (item.nodeType && typeof item.cloneNode == "function") {
	                var result = item.cloneNode( true );
	            } else if (!item.prototype) { // check that this is a literal
	                if (item instanceof Date) {
	                    result = new Date(item);
	                } else {
	                    // it is an object literal
	                    result = {};
	                    for (var i in item) {
	                        result[i] = deepclone( item[i] );
	                    }
	                }
	            } else {
	                // depending what you would like here,
	                // just keep the reference, or create new object
	                if (false && item.constructor) {
	                    // would not advice to do that, reason? Read below
	                    result = new item.constructor();
	                } else {
	                    result = item;
	                }
	            }
	        } else {
	            result = item;
	        }
	    }

	    return result;
	}

	function range(start, end){
		var result = [];
		for(var i=start; i<=end; i++){
			result.push(i)
		}
		return result;
	}

	function pluck(arr, attr){
		return arr.map(function(obj){
			return obj[attr];
		});
	}

	function isEmptyArray(arr){
		return arr == null || arr.length === 0;
	}

	// http://krasimirtsonev.com/blog/article/Javascript-template-engine-in-just-20-line
	function templateEngine(html, options) {
	    var re = /<%(.+?)%>/g,
	        reExp = /(^( )?(var|if|for|else|switch|case|break|{|}|;))(.*)?/g,
	        code = 'with(obj) { var r=[];\n',
	        cursor = 0,
	        result,
	        match;
	    var add = function(line, js) {
	        js ? (code += line.match(reExp) ? line + '\n' : 'r.push(' + line + ');\n') :
	            (code += line != '' ? 'r.push("' + line.replace(/"/g, '\\"') + '");\n' : '');
	        return add;
	    }
	    while (match = re.exec(html)) {
	        add(html.slice(cursor, match.index))(match[1], true);
	        cursor = match.index + match[0].length;
	    }
	    add(html.substr(cursor, html.length - cursor));
	    code = (code + 'return r.join(""); }').replace(/[\r\t\n]/g, ' ');
	    try { result = new Function('obj', code).apply(options, [options]); } catch (err) { console.error("'" + err.message + "'", " in \n\nCode:\n", code, "\n"); }
	    return result;
	}

	function findParent(current, selector, root) {
		if (current === root) return null;
		if (current.classList.contains(selector)) {
			return current;
		}
		return findParent(current.parentNode, selector, root);
	}

	function renderHeader(columns, sortable){
		return templateEngine(HEADER_TPL, {
			role: ROLE_THEADER_TH,
			columns: columns,
			getStyle: function(column){
				if(column.width){
					return "width:" + column.width+";";
				}
			},
			sortable: sortable !== false && columns.sortable !== false
		});
	}

	function renderBody(options, pagination){
		var data = pagination ? pagination.data : options.data;
		return templateEngine(BODY_TPL, {
			data: data,
			columns: options.columns,
			messages: options.messages
		});
	}

	function renderFooter(pagination){
		if(pagination === null || isEmptyArray(pagination.data)) return EMPTY;
		if(pagination.pageCount === 1) return EMPTY; // 总页数只有一页时不显示分页条（可配置）
		return templateEngine(FOOTER_TPL, extend({
			role: ROLE_PAGINATION_BTN
		}, pagination));
	}

	function renderTable(options, pagination){
		return [
			'<div class="'+PTTABLE_SELECTOR+'">',
				'<table>',
					renderHeader(options.columns, options.features.sortable),
					renderBody(options, pagination),
				'</table>',
				renderFooter(pagination),
			'</div>'
		].join('');
	}

	/**
	 * @param {Number} currentPage 当前页
	 * @param {Number} pageLength 分页单位，每页显示条数
	 * @param {Array} data 要分页的数据
	 */
	function Pagination(currentPage, pageLength, data){
		this.cached = data || [];
		this.currentPage = currentPage;
		this.pageLength = pageLength;
		this.totalCount = this.cached.length;
		this.pageCount = Math.ceil(this.totalCount / this.pageLength); // 总页数
	}
	Pagination.prototype = {
		constructor: Pagination,
		_getPagination: function(){
			if(this.totalCount <= 0) return null;
			if(this.pageCount <= PAGINATION_LENGTH){
				return range(1, this.pageCount);
			}
			var continueLen = PAGINATION_LENGTH - 2; // 2表示有两个...占位符
			if(this.currentPage < continueLen){
				return range(1, continueLen).concat([PAGINATION_PLACEHOLDER, this.pageCount]);
			}
			if(this.currentPage > this.pageCount - continueLen + 1){
				return [1, PAGINATION_PLACEHOLDER].concat(range(this.pageCount - continueLen + 1, this.pageCount));
			}
			return [1, PAGINATION_PLACEHOLDER].concat(range(this.currentPage - 1, this.currentPage + 1)).concat([PAGINATION_PLACEHOLDER, this.pageCount]);
		},
		_getData: function(){
			var start = (this.currentPage-1) * this.pageLength,
				end = start + this.pageLength;
			return this.cached.slice(start, end);
		},
		prev: function(){
			this.go(this.currentPage - 1);
		},
		next: function(){
			this.go(this.currentPage + 1);
		},
		go: function(page){
			if(page < 1 || page > this.pageCount) return;
			if(this.currentPage == page) return;
			this.currentPage = page;
		},
		value: function(){
			return {
				currentPage: this.currentPage,
				pageLength: this.pageLength,
				totalCount: this.totalCount,
				pageCount: this.pageCount,
				items: this._getPagination(),
				data: this._getData(),
				placeholder: PAGINATION_PLACEHOLDER
			};
		}
	};

	/**
	 * 排序
	 * @param {Array} data 要排序的二维数组
	 * @param {Array} orders 默认的排序规则
	 * @param {Array} comparators 比较器数组
	 * @param {Function} 排序后的回调函数，参数为index,direction
	 */
	function Sorter(data, orders, comparators){
		this.data = data;
		this.orders = orders;
		this.comparators = comparators;
		this.init();
	}
	// 取不到就按数字类型比较
	var numberReg = /^(\-|\+)?\d+(\.\d+)?$/;
	Sorter.guessType = function(a, b){
		if(numberReg.test(a) && numberReg.test(b)) return 'number';
        if(typeof(a) != typeof(b) ) return 'string';

		var types = {
			string: 'string',
			number: 'number',
			boolean: 'number',
			object: 'number'
		};

        return types[typeof(a)] || 'string';
	};
	// 默认比较器
	Sorter.comparators = {
		number: function(a, b, direction){
            if(a === b) return 0;
            return direction > 0 ? a - b : b - a;
        },
        string: function(a, b, direction){
            // a = (a+"").toLowerCase();
            // b = (b+"").toLowerCase();
            if(a === b) return 0;
            var comparison = direction > 0 ? a > b : a < b;
            return comparison === false ? -1 : (comparison - 0);
        }
	};
	Sorter.opposites = function(direction){
		var mapper = {'desc': 'asc', 'asc': 'desc'};
		return direction ? mapper[direction] : 'asc';
	};
	Sorter.directionMapper = {'asc': 1, 'desc': -1};
	Sorter.prototype = {
		constructor: Sorter,
		init: function(){
			var self = this;
			this.orders.forEach(function(order){
				self.sort(order[0], order[1]);
			});
		},
		//direction为asc或者desc
		sort: function(index, direction){
			var self = this;
			this.data.sort(function(a, b){
				var comparator = self.comparators[index] || Sorter.comparators[Sorter.guessType(a[index])];
				return comparator.call(null, a[index], b[index], Sorter.directionMapper[direction]);
			});
		}
	};

	/**
	 * 表格
	 * @param  {HtmlElement} element
	 */
	function pttable(element){
		this.element = element;
		this.options = {};
		this.pagination = null; // 分页对象
		this.sorter = null; // 排序对象
		this._init();
	}

	pttable.prototype = {
		_init: function(){
			this._bindEvent();
		},
		_bindEvent: function(){
			this.element.addEventListener('click', this._click.bind(this), false);
		},
		_unbindEvent: function(){
			this.element.removeEventListener('click', this._click.bind(this), false);
		},
		_click: function(event){
			var target = event.target;
			switch(target.dataset.role){
				case ROLE_PAGINATION_BTN:
					this._handlePagination(target);
					break;
				case ROLE_THEADER_TH:
					this._handleSort(target);
					break;
			}
		},
		_handlePagination: function(el){
			if(el.dataset.role == 'prev'){
				this.pagination.prev();
			}else if(el.dataset.role == 'next'){
				this.pagination.next();
			}else{
				this.pagination.go(parseInt(el.dataset.num));
			}
			this.render();
		},
		// 排序后就需要重新进行分页
		_handleSort: function(target){
			var index = parseInt(target.dataset.index);
			var direction = Sorter.opposites(target.dataset.direction);
			var sortable = target.dataset.sortable;
			// 禁止排序
			if(this.options.features.sortable === false || sortable === 'false') return;
			// 执行排序前回调, 如果返回值不为true,代码就此return
			var beforeSortResult = this.options.onBeforeSort(index, direction);
			if(!beforeSortResult) return;

			// 进行排序(先查看是否设置按某列进行排序)
			var columnConfig = this.options.columns[index],
				sortByIndex = columnConfig.sortByColumnIndex !== undefined ? columnConfig.sortByColumnIndex : index;
			if(this.sorter == null){
				this.sorter = new Sorter(this.options.data, [[sortByIndex, direction]], [this.options.columns[index].comparator]);
			}else{
				this.sorter.sort(sortByIndex, direction);
			}

			// 重置columns的direction属性
			this._setSortDirection(index, direction);
			// 跳转到第一页
			if(this.pagination){ this.pagination.go(1);}

			this.render();
		},
		_initColumns: function(){
			// 初始化columns中formatter
			var defaultformatter = function(value){return value;};
			this.options.columns.forEach(function(column){
				column.formatter = column.formatter || defaultformatter;
			});
		},
		_setSortDirection: function(index, direction){
			if(isEmptyArray(this.options.columns)) return;
			this.options.columns.forEach(function(column){
				column.direction = undefined;
			});
			if(this.options.columns[index]){
				this.options.columns[index].direction = direction;
			}
		},
		// 会重新初始化sorter、pagination
		setOptions: function(options){
			this.options = extend(deepclone(defaults),options);
			this._initColumns();
			// 初始化sorter
			if(!isEmptyArray(this.options.orders)){
				var len = this.options.orders.length,
					last = this.options.orders[len - 1],
					index = last[0],
					direction = last[1];
				this._setSortDirection(index, direction);
				this.sorter = new Sorter(this.options.data, this.options.orders, pluck(this.options.columns, 'comparator'));
			}
			// 初始化pagination
			if(this.options.features.pagination){
				this.pagination = new Pagination(
					this.options.pagination.currentPage,
					this.options.pagination.pageLength,
					this.options.data
				);
			}
			return this;
		},
		render: function(){
			console.log("=======render=========")
			this.element.innerHTML = renderTable(this.options, this.pagination === null ? null : this.pagination.value());
			return this;
		},
		destroy: function(){
			this._unbindEvent();
		}
	};

	return pttable;
});


