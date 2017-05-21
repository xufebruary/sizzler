;(function (parent, fun) {
	if (typeof exports !== 'undefined' && typeof module !== 'undefined') {
		module.exports = fun();
	} else if (typeof define === 'function' && typeof define.amd === 'object') {
		define(fun);
	} else if (typeof define === 'function' && typeof define.cmd === 'object') {
		define(fun);
	} else {
		parent.uaUtil = fun();
	}
})(window, function(){
	var ua = window.navigator.userAgent;

	// 移动端、safari、chrome、edge
	function isSupport() {
		console.log(ua);
	}

	return {
		isSupport: isSupport
	}
});
