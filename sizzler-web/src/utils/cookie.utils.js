
;(function (parent, fun) {
	if (typeof exports !== 'undefined' && typeof module !== 'undefined') {
		module.exports = fun();
	} else if (typeof define === 'function' && typeof define.amd === 'object') {
		define(fun);
	} else if (typeof define === 'function' && typeof define.cmd === 'object') {
		define(fun);
	} else {
		parent.cookieUtils = fun();
	}
})(window, function(){
	return {
		set: function (name, value, days) {
			if (days) {
				var date = new Date();
				date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
				var expires = "; expires=" + date.toGMTString();
			}
			else var expires = "";
			document.cookie = name + "=" + value + expires + "; path=/";
		},

		get: function (name) {
			var nameEQ = name + "=";
			var ca = document.cookie.split(';');
			for (var i = 0; i < ca.length; i++) {
				var c = ca[i];
				while (c.charAt(0) == ' ') c = c.substring(1, c.length);
				if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
			}
			return null;
		},

		remove: function (name) {
			this.set(name, "", -1);
		}
	};
});

