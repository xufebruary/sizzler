export default {
	max: function(arr, fn){
		if(arr == null) return undefined;

		if(fn == null){
			return Array.max(arr);
		}

		var lastComputed = -Infinity, computed, result;
		arr.forEach(function(o, index){
			computed = fn(o, index, arr);
			if(computed > lastComputed){
				lastComputed = computed;
				result = o;
			}
		});

		return result;
	},

	isEmpty: function(arr){
		if(arr == null) return true;
		return arr.length === 0;
	},

	isNotEmpty: function(arr){
		return !this.isEmpty(arr);
	}
}
