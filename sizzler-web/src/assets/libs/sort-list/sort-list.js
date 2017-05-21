/**
 * Created by jianqing on 16/11/16.
 */

;(function(parent, fun){
	if(typeof exports !== 'undefined' && typeof module !== 'undefined'){
		module.exports = fun();
	}
	else if(typeof define === 'function' && typeof define.amd === 'object'){
		define(fun);
	}
	else if(typeof define === 'function' && typeof define.cmd === 'object'){
		define(fun);
	}
	else{
		parent.SortList = fun();
	}
})(window.pt || window,function(SortList){
	"use strict";

	var dragEl,
		parentEl,
		ghostEl,
		cloneEl,
		rootEl,
		nextEl,

		oldIndex,
		newIndex,

		tapEvt,

		moved,

		expando = 'SortList' + (new Date).getTime(),

		win = window,
		document = win.document,
		parseInt = win.parseInt,

		supportDraggable = !!('draggable' in document.creatElement('div')),
		supportCssPointerEvents = (function(el){
			el = document.createElement('x');
			el.style.cssText = 'pointer-events:auto';
			return el.style.pointerEvents = 'auto';
		})(),

		abs = Math.abs,
		min = Math.min,
		slice = [].slice,

		scrollEl,
		scrollParentEl,
		lastEl,
		lastCSS;

	function SortList(el, options){
		if(!(el && el.nodeType && el.nodeType === 1)){
			throw 'el must be HTMLElement.'
		}

		this.el = el;
		this.options = options = _extend({}, options);

		var defaults = {
			sort: true,
			disabled: false,
			draggable: 'li',
			ghostClass: 'sortable-ghost',
			chosenClass: 'sortable-chosen',
			dragClass: 'sortable-drag',
			forceFallback: false,
			dropBubble: false //是否冒泡
		};

		//绑定私有函数
		for(var fn in this){
			if(fn.charAt(0) === '_' && typeof this[fn] === 'function'){
				this[fn] = this[fn].bind(this);
			}
		}

		this.nativeDraggable = options.forceFallback ? false : supportDraggable;

		_on(el, 'mousedown', this._onTapStart);

	}

	SortList.prototype = {
		constructor: SortList,

		_onTapstart: function(evt){
			var _this = this,
				el = this.el,
				options = this.options,
				type = evt.type,
				target = evt.target,
				originalTarget = evt.target.shadowRoot && evt.path[0] || target,
				startIndex;

			if(dragEl){//当已经有被拖拽的元素时，不能再次生成一个
				return;
			}

			if(options.disabled){//当元素被禁用时
				return;
			}

			if(!target){
				return;
			}

			startIndex = _index(target);

			// 准备 `dragstart`

			this._prepareDragStart(evt,target,startIndex);

		},

		_prepareDragStart: function(evt, target, startIndex){
			var _this = this,
				el = _this.el,
				options = _this.options,
				ownerDocument = el.ownerDocument,
				dragStartFn;
			if(target && !dragEl && (target.parentNode === el)){
				tapEvt = evt;

				rootEl = el;
				dragEl = target;
				parentEl = dragEl.parentNode;
				nextEl = dragEl.nextSibling;
				oldIndex = startIndex;

				this._lastX = evt.clientX;
				this._lastY = evt.clientY;

				dragEl.style['will-change'] = 'transform';

				dragStartFn = function(){
					dragEl.draggable = _this.nativeDraggable;

					dragEl.classList.toggle(options.chosenClass);

					_on(dragEl, 'dragend',this);
					_on(rootEl, 'dragstart', this._onDragStart);

					_dispatchEvent(_this, rootEl, 'choose', dragEl, rootEl, oldIndex);
				};

				_on(ownerDocument,'mouseup', _this._onDrop);

				dragStartFn();
			}
		},
		_onDragStart: function(evt){
			var dataTransfer = evt.dataTransfer,
				options = this.options;
			var ownerDocument = this.el.ownerDocument;

			_off(ownerDocument, 'mouseup', this._onDrop);

			dragEl.classList.toggle(options.dragClass);
			if(dataTransfer){
				dataTransfer.effectAllowed = 'move';
				options.setData && options.setData.call(this,dataTransfer,dragEl);
			}

			_on(document, 'drop',this);
			setTimeout(this._dragStarted,0);

		},
		_dragStarted: function(){
			if(rootEl && dragEl){
				var options = this.options;

				dragEl.classList.toggle(options.ghostClass);
				dragEl.classList.toggle(options.dragClass);

				SortList.active = this;

				_dispatchEvent(this,rootEl, 'start',dragEl,rootEl,oldIndex);
			}
		},
		_onDrop: function(evt){
			var el = this.el,
				options = this.options;

			if(this.nativeDraggable){
				_off(document, 'drop', this);
				_off(el, 'dragstart', this._onDragStart);
			}

			this._offUpEvents();

			if(evt){
				if(moved){
					evt.preventDefault();
					!options.dropBubble && evt.stopPropagation();
				}

				ghostEl && ghostEl.parentNode.removeChild(ghostEl);

				if(dragEl){
					if(this.nativeDraggable){
						_off(dragEl, 'dragend', this);
					}

					dragEl.draggable = false;
					dragEl.style['will-change'] = '';

					dragEl.classList.toggle(this.options.ghostClass);
					dragEl.classList.toggle(this.options.chosenClass);

					if(rootEl === parentEl){
						cloneEl && cloneEl.parentNode.removeChild(cloneEl);

						if(dragEl.nextSibling !== nextEl){
							newIndex = _index(dragEl);

							if(newIndex >= 0){
								_dispatchEvent(this, rootEl, 'update', dragEl, rootEl, oldIndex, newIndex);
								_dispatchEvent(this, rootEl, 'sort', dragEl, rootEl, oldIndex, newIndex);
							}
						}
					}
				}
			}
			this._nulling();
		},
		_offUpEvents: function(){
			var ownerDocument = this.el.ownerDocument;

			_off(ownerDocument, 'mouseup', this._onDrop);
		},
		_nulling: function(){
			rootEl =
			dragEl =
			parentEl =
			ghostEl =
			nextEl =
			cloneEl =

			scrollEl =
			scrollParentEl =

			tapEvt =

			moved =
			lastEl =
			lastCSS =

			SortList.active = null;

		}
	};

	function _extend(dst, src){
		if(dst && src){
			for(var key in src){
				if(src.hasOwnProperty(key)){
					dst[key] = src[key];
				}
			}
		}
		return dst;
	}

	function _on(el, event, fun){
		el.addEventListener(event,fun,false);
	}

	function _off(el,event,fun){
		el.removeEventListener(event, fn, false);
	}

	/**
	 *  获取或者设置元素的css样式
	 * @param el  元素
	 * @param prop  属性名
	 * @param val  属性值
	 * @returns {*}
	 * @private
	 */
	function _css(el, prop, val){
		var style = el && el.style;
		if(style){
			if(val === void 0){
				if(win.getComputedStyle){
					val = win.getComputedStyle(el, '');
				}
				else if(el.currentStyle){
					val = el.currentStyle;
				}

				return prop === void 0 ? val : val[prop];
			}
			else{
				if(!(prop in style)){
					prop = '-webkit-' + prop;
				}
				style[prop] = val + (typeof val === 'string' ? '' : 'px');
			}
		}
	}

	function _index(el){
		var index = 0;
		if(!el || !el.parentNode){
			return -1;
		}

		while(el && (el = el.previousElementSibling)){
			index++;
		}
		return index;
	}

	function _dispatchEvent(sortable, rootEl, name, targetEl, fromEl, startIndex, newIndex){
		sortable = (sortable || rootEl[expando]);

		var evt = document.createEvent('Event'),
			options = sortable.options,
			onName = 'on' + name.charAt(0).toUpperCase() + name.substr(1);

		evt.initEvent(name, true,true);

		evt.to = rootEl;
		evt.form = fromEl || rootEl;
		evt.item = targetEl || rootEl;
		evt.clone = cloneEl;

		evt.oldIndex = startIndex;
		evt.newIndex = newIndex;

		rootEl.dispatchEvent(evt);

		if(options[onName]){
			options[onName].call(sortable, evt);
		}
	}

	return SortList;
});
