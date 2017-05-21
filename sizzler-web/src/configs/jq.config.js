// lazyload config

angular.module('pt')
/**
 * jQuery plugin config use ui-jq directive , config the js and css files that required
 * key: function name of the jQuery plugin
 * value: array of the css js file located
 */
	.constant('JQ_CONFIG', {
			// used
			slimScroll: ['/assets/libs/jquery/slimscroll/jquery.slimscroll.min.js'],
			// used
			DatePicker: ['/assets/libs/jquery/datepicker/js/datepicker.js',
				'/assets/libs/jquery/datepicker/js/eye.js',
				'/assets/libs/jquery/datepicker/js/utils.js',
				'/assets/libs/jquery/datepicker/css/datepicker.css'],
			//used
			unslider: ['/assets/libs/jquery/unslider/unslider.js',
				'/assets/libs/jquery/unslider/unslider.css']
		}
	);

