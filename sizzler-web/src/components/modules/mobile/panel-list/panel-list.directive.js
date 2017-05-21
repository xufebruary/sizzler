import tpl from './panel-list.html';

mobilePanelListDirective.$inject = ['PanelServices'];

function mobilePanelListDirective(PanelServices) {
	return {
		restrict: 'E',
		template: tpl,
		link: link
	};

	function link(scope, element, attrs){
		
		// =================
		
		//移动端点击panel事件
    	scope.mobileDashboardClick = mobileDashboardClick;
		
		//移动端判断是否为分享
    	scope.mobileIsShare = mobileIsShare;

		// =================


		//移动端点击事件
	    function mobileDashboardClick(item) {
	        if (item.type == 'container') {
	            //文件夹

	            if (angular.isUndefined(item.fold)) {
	                item['fold'] = true;
	            } else {
	                item.fold = !item.fold;
	            }
	        } else {
	            //dashboard
	            if (scope.rootPanel.now === null || scope.rootPanel.now !== null && scope.rootPanel.now.panelId !== item.panelId) {
	                scope.dashboardSelect(item.panelId, 'select');
	                scope.ptPhone.menuBtn = !scope.ptPhone.menuBtn;
	            }
	        }
	    };

	    //移动端判断是否为分享页面
	    function mobileIsShare(panelId) {
	        var myPanel = PanelServices.getMyPanel(scope.rootPanel.list, panelId);
	        return myPanel && myPanel.shareSourceId
	    }
	}
}

export default mobilePanelListDirective;
