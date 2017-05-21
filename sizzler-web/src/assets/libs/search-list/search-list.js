;(function (parent, fun) {
	if (typeof exports !== 'undefined' && typeof module !== 'undefined') {
		module.exports = fun();
	} else if (typeof define === 'function' && typeof define.amd === 'object') {
		define(fun);
	} else if (typeof define === 'function' && typeof define.cmd === 'object') {
		define(fun);
	} else {
		parent.searchlist = fun();
	}
})(window.pt || window, function (searchlist) {

	var THRESH_HOLD = 100,  // 节流函数阈值
		PARENT_NAME = 'li', // 父元素nodeName
		CHILD_NAME = 'ul',  // 子元素nodeName
		HAS_CHILDREN_CLASS_NAME = 'cascade', // 表示是否继续有层级
		TOGGLE_CLASS_NAME = 'on', // 控制显示隐藏
		ACTIVE_CLASS_NAME = 'active', // 选中样式
		ATTR_ID = 'data-id',
		ATTR_NAME = 'data-name';

	// 默认配置
	var defaults = {
		selectedId: undefined,  // 默认选中id
		tips: { 				// 提示信息
			empty: '没有数据'     // 搜索结果为空时的提示
		},
		onSelect: null    		// 选中时的回调
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

	/**
	 * 深复制 (http://stackoverflow.com/questions/4459928/how-to-deep-clone-in-javascript)
	 * @param  {[type]} item [description]
	 * @return {[type]}      [description]
	 */
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

	// 空数组判断
	function isEmptyArray(array){
		return array == null || array.length === 0;
	}

	/**
	 * 节流函数
	 * @param fn
	 * @param scope
	 * @return {Function}
	 */
	function throttle(fn, scope) {
		var last = 0, deferTimer;
		return function () {
			var context = scope || this;
			var now = new Date().getTime(),
				args = arguments;

			if (last && now < last + THRESH_HOLD) {
				clearTimeout(deferTimer);
				deferTimer = setTimeout(function () {
					last = now;
					fn.apply(context, args);
				}, THRESH_HOLD);
			} else {
				last = now;
				if (last != 0) { // 第一次不触发
					fn.apply(context, args);
				}
			}
		};
	}

	/**
	 * 查找父元素
	 * @param current Node对象
	 * @param parentNodeName 要查找父元素名称
	 * @param root 用来限定查找的范围
	 * @return {*}
	 */
	function findParent(current, parentNodeName, root) {
		if (current === root) return null;
		if (current.nodeName.toLowerCase() == parentNodeName.toLowerCase()) {
			return current;
		}
		return findParent(current.parentNode, parentNodeName, root);
	}

	/**
	 * 是否有子元素
	 * @param node Node对象
	 * @param childNodeName 子元素nodeName
	 * @return {boolean}
	 */
	function hasChildren(node, childNodeName) {
		var nodeList = node.childNodes;
		if (nodeList && nodeList.length) {
			for (var i = 0; i < nodeList.length; i++) {
				if (nodeList[i].nodeName.toLowerCase() == childNodeName.toLowerCase()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 字符串变量内插
	 * @param str
	 * @param o
	 * @return {*}
	 */
	function supplant(str, o) {
		return str.replace(/{([^{}]*)}/g,
			function (a, b) {
				var r = o[b];
				return typeof r === 'string' || typeof r === 'number' ? r : a;
			}
		)
	}

	function escapeRegExp(str) {
  		return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
	}

	/**
	 * 将tree数据结构转化为ul,li互相嵌套结构
	 * @param treeList
	 * @param options {
	 * 	selectedId: 选中id,
	 * 	expand: boolean 表示是否要全部展开(搜索结果需要全部展开),
	 * 	buildName: function 动态构建展示名称
	 * }
	 */
	function build2Html(treeList, options) {
		if(isEmptyArray(treeList)) return '<div class="tip">'+options.tips.empty+'</div>';

		var htmlContent = htmlContent || '<ul>';

		for (var i = 0; i < treeList.length; i++) {
			var node = treeList[i],
				// 是否有子节点
				hasChildren = node.child && node.child.length > 0,
				hasChildrenClsName = hasChildren ? HAS_CHILDREN_CLASS_NAME : '',
				activeClsName = node.id == options.selectedId ? ACTIVE_CLASS_NAME : '';

			var nodeName = node.name;
			if (options.expand || node.expand) { // 要展开
				hasChildrenClsName += ' ' + TOGGLE_CLASS_NAME;
			}
			if (options.buildName && typeof options.buildName === 'function') { // 使用函数动态生成name
				nodeName = options.buildName(nodeName);
			}

			htmlContent += supplant("<li class='{hasChildrenClsName} {activeClsName}' data-last-li='{lastLi}' data-id='{id}' data-name='{name}'>" +
				"<a><span>{displayName}</span></a>", {
				hasChildrenClsName: hasChildrenClsName,
				id: node.id,
				name: node.name,
				displayName: nodeName,
				activeClsName: activeClsName,
				lastLi: hasChildren ? 'false' : 'true'
			});

			if (hasChildren) {
				// 递归调用
				htmlContent += build2Html(node.child, options);
			}
			htmlContent += "</li>"
		}

		htmlContent += "</ul>";

		return htmlContent;
	}

	/**
	 * 对树状结构数据进行关键字过滤
	 * @param treeList
	 * @param keyword
	 * @return {*}
	 */
	function filter(treeList, searchVal) {
		return treeList.reduce(function (prev, curr) {
			// 如果有子元素,则继续进行walk
			if (curr.child && curr.child.length) {
				var child = filter(curr.child, searchVal);
				if (child.length) {
					var retained = extend({}, curr);
					retained.child = child;
					prev.push(retained);
					return prev;
				}
			}
			// 否则进行匹配
			var reg = new RegExp(searchVal, 'gi');
			var matched = reg.test(curr.name);
			if (matched) {
				prev.push(extend({}, curr));
			}
			return prev;
		}, []);
	}

    // 赋属性
    function setExpand(node, id) {
        var i, c, len, child = node.child;
        if ((node.child == null || node.child.length === 0) && id == node.id)
            return true;
        if (child instanceof Array) {
            for (i = 0, len = child.length; i < len; i++) {
                if (setExpand(child[i], id)) {
                    node.expand = true;
                    return true;
                }
            }
        }
        return false;
    }

	/**
	 * 将id为selected的祖先元素都标记为expand(展开)
	 * @param treeList
	 * @param selectedId
	 * @return {*}
     */
	function mark(dataList, selectedId) {
		if(isEmptyArray(dataList)) return null;
		if(selectedId == undefined) return dataList;

		var tree = {
			id: -1,
			name: null,
			child: dataList
		};
		var node = deepclone(tree);
		setExpand(node, selectedId);
		return node.child;
	}

	function renderSearchList(treeList, options) {
		return [
			'<div class="search-list">',
				'<div class="hd">',
					'<input type="text"/><a></a>',
				'</div>',
				'<div class="bd">',
					build2Html(treeList, options),
				'</div>',
			'</div>'
		].join('');
	}

	/**
	 * 查找组件函数
	 * @param element Element 该组件渲染容器
	 * @param data 要渲染的数据
	 * @param options 配置
	 * @constructor
	 */
	function SearchList(element, options) {
		this.element = element;
		this.options = extend(defaults, options || {});

		this.treeList = null; // 用来渲染
		this.data = null; // 调用者的原始数据
		// 初始化
		this.init();
	}

	SearchList.prototype = {
		init: function () {
			this.bindEvent();
			return this;
		},
		render: function (data) {
			this.data = data;
			this.treeList = mark(data, this.options.selectedId);
			this.element.innerHTML = renderSearchList(this.treeList, this.options);
		},
		bindEvent: function () {
			this.element.addEventListener('click', this.click.bind(this), false);
			this.element.addEventListener('input', throttle(this.keyup, this), false);
		},
		click: function (event) {
			var parentNode = findParent(event.target, PARENT_NAME, this.element);
			if (parentNode == null) return;

			if (hasChildren(parentNode, CHILD_NAME)) {
				this.toggle(parentNode);
			} else {
				this.selectNode(parentNode);
			}
		},
		keyup: function (event) {
			if (event.target.nodeName.toLowerCase() !== 'input') {
				return;
			}
			var searchVal = event.target.value.trim();
			var filterResult = !searchVal ? this.treeList : filter(this.treeList, escapeRegExp(searchVal));
			this.element.querySelector('.bd').innerHTML = build2Html(filterResult, extend({
				expand: !!searchVal, // 如果过滤则全部展开
				buildName: !!searchVal && function (name) {
					return name.replace(new RegExp(escapeRegExp(searchVal), 'gi'), function (val) {
						return '<i>' + val + '</i>';
					});
				}
			}, this.options));
		},
		// 控制显示隐藏
		toggle: function (node) {
			node.classList.toggle(TOGGLE_CLASS_NAME);
		},
		selectNode: function (node) {
			var selectedId = node.getAttribute(ATTR_ID),
				selectedName = node.getAttribute(ATTR_NAME);

			// 设置选中样式
			var prev = this.element.querySelector('.'+ACTIVE_CLASS_NAME);
			prev && prev.classList.remove(ACTIVE_CLASS_NAME);
			node.classList.add(ACTIVE_CLASS_NAME);

			// 重新mark
			this.setOptions({
				selectedId: selectedId
			});
			this.treeList = mark(this.data, selectedId);

			// 回调
			this.options.callback.call(null, {
				id: selectedId,
				name: selectedName
			});
		},
		setOptions: function(options){
			this.options = extend(this.options, options);
		}
	};

	/**
	 * 对外暴露的接口
	 * @type {{render: searchlist.render}}
	 */
	searchlist = function(element, options){
		this.instance = new SearchList(element, options);
	};

	searchlist.prototype = {
		render: function(data){
			this.instance.render(data);
		},
		setOptions: function(options){
			this.instance.setOptions(options);
		}
	};

	return searchlist;
});
