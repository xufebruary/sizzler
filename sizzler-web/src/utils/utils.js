export default {
	getSourceByLocation: function(){
		var href = window.location.href;
		var source;
		if(href.indexOf('datadeck.jp') > -1){
			source = 'dd-jp-invite';
		}else if(href.indexOf('datadeck.com') > -1){
			source = 'dd-en-invite';
		}else if(href.indexOf('datadeck.cn') > -1 || href.indexOf('ptone.cn') > -1 || href.indexOf('ptone.com.cn') > -1){
			source = 'dd-cn-invite';
		}else{
			source = 'dd-jp-invite';
		}
		return source;
	},
	//内部注册
	getSourceByInternalLocation: function(){
		var href = window.location.href;
		var source;
		if(href.indexOf('datadeck.jp') > -1){
			source = 'dd-jp-internal';
		}else if(href.indexOf('datadeck.com') > -1){
			source = 'dd-en-internal';
		}else if(href.indexOf('datadeck.cn') > -1 || href.indexOf('ptone.cn') > -1 || href.indexOf('ptone.com.cn') > -1){
			source = 'dd-cn-internal';
		}else{
			source = 'dd-jp-internal';
		}
		return source;
	}
};
