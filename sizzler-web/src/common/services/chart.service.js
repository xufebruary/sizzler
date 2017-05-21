'use strict';

/**
 * chartService
 * 图形相关的服务
 *
 */

chartService.$inject = ['$translate'];

function chartService($translate){

	return {

		//获取左下角提示信息
		getTips: function(widget, type, dsConfig){
			var tipsInfo = '';
		    var list = type == 'metrics' ? widget.variables[0].metrics : widget.variables[0].dimensions;
		    var listLength = list.length;

		    if(type == 'metrics'){
		        for (var i = listLength - 1; i >= 0; i--) {
		            var metric = list[i];

		            if(metric.alias){
		                tipsInfo += metric.alias;
		            }
		            else if(dsConfig.editor.data.metrics){
		                if(metric.type == 'compoundMetrics') {
		                    tipsInfo += metric.name;
		                }
		                else {
		                    if(dsConfig.editor.data.metricsHasCount){
		                        tipsInfo += metric.calculateType + '(' + $translate.instant(metric.i18nCode) + ')';
		                    }
		                    else {
		                        tipsInfo += $translate.instant(metric.i18nCode);
		                    }
		                }
		            }
		            else if(dsConfig.editor.data.value){
		                tipsInfo += metric.name;
		            }

		            if(i != 0) tipsInfo += ', ';
		        }
		    }
		    else if(type == 'dimensions'){
		        for (var i = listLength - 1; i >= 0; i--) {
		            var dimension = list[i];

		            if(dimension.alias){
		                tipsInfo += dimension.alias;
		            }
		            else if(dsConfig.editor.data.dimensions){
		                tipsInfo += $translate.instant(dimension.i18nCode);
		            }
		            else if(dsConfig.editor.data.attributes){
		                tipsInfo += dimension.name;
		            }
		            if(i != 0) tipsInfo += ', ';
		        }
		    }

		    return tipsInfo;
		}
	}
};

export default chartService;