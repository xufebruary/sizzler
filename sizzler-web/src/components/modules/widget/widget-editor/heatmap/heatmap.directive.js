import tpl from './heatmap.html';
import './heatmap.scss';


editorHeatmapDirective.$inject = ['$translate', '$rootScope', 'Track'];

function editorHeatmapDirective($translate, $rootScope, Track) {
    return {
        restrict: 'EA',
        scope: {
            locale: '<',    //语言
        	heatmapUrl: '<', //热图地址
            widgetId: '<',
        	onSuccess: '&'
        },
        template: tpl,
        link: link
    };

    function link(scope, elem, attrs) {
        
    	scope.myOptions = {
            productConfigs: null,
    		modUrl: null,
    		showCancel: false    	
        }
        // =========

        init(scope.heatmapUrl);
        
        // =========

        //入口
        function init(url){
            scope.myOptions.productConfigs = $rootScope.productConfigs;
        	scope.myOptions.modUrl = url;
        }

        //取消
        scope.cancel = function(){
        	scope.myOptions.modUrl = scope.heatmapUrl;
        	scope.myOptions.showCancel = false;
        }

        //提交
        scope.apply = function(){
        	if(scope.myOptions.modUrl != scope.heatmapUrl){
        		scope.onSuccess({data: scope.myOptions.modUrl})
        	}

            //全站事件统计
            Track.log({"where":"widget_editor", "what":"heatmap_link_apply", "value": scope.widgetId});
        }

        //判断是否显示取消按钮
        scope.showCancel = function(){
        	scope.myOptions.showCancel = (scope.myOptions.modUrl != scope.heatmapUrl)
        }

        // ==========
        //监听父级是否有切换widget
        scope.$on('changeWidgetEditor', function (e, newWidget) {
            if(newWidget.baseWidget.graphName == 'heatmap') init(newWidget.toolData.value);
        });
    }
}

export default editorHeatmapDirective;
