/**
 * Created by jianqing on 16/12/9.
 */

import {
	LINK_SHARE_PANEL
	} from 'components/modules/common/common';

panelShareController.$inject = ['$document','PanelServices','siteEventAnalyticsSrv','$timeout', 'Track'];

function panelShareController($document,PanelServices,siteEventAnalyticsSrv,$timeout,Track){
	var vm = this;

	var body = $document.find('body').eq(0);
	var currentPanel = angular.fromJson(vm.currentPanel);
	currentPanel.type = 'panel';

	//指令内部参数调用
	vm.myOptions = {
		shareUrl: +currentPanel.shareUrl ? true : false,
		publicPanelShareUrl: LINK_SHARE_PANEL + currentPanel.panelId,
		sharePasswordStatus: currentPanel.sharePassword ? true : false, //密码开关
		sharePassword: currentPanel.sharePassword ? decodeURIComponent(currentPanel.sharePassword) : '', //密码值
		sharePasswordSuccess: false
	};

	//弹出框隐藏滚动条
	body.addClass('modal-open');

	//分享保存
	vm.sharePanel = function(){
		currentPanel.shareUrl = vm.myOptions.shareUrl ? 1 : 0;
		PanelServices.updatePanel(currentPanel)
			.then((data) => {
				vm.onSuccess({data: currentPanel, layout: data.panelLayout});
				body.addClass('modal-open');//因为onSuccess回调里有露出滚动条，为了防止闪动，这里需要加回去
			},() => {
				vm.onFailure();
			});

		//全站事件统计
		Track.log({'where': 'panel_share','what': 'change_panel_share','how': 'change','value': vm.myOptions.shareUrl ? 'ON' : 'OFF'});
	};

	//select
	vm.iptClick = function (event) {
		// note: 下面这两行可以去掉
		angular.element(event.target).select();

		//GTM
		siteEventAnalyticsSrv.setGtmEvent('click_element', 'dashboard', 'click_url');
		//全站事件统计
		Track.log({'where': 'panel_share','what': 'select_share_url','how': 'click','value': currentPanel.panelId});
	};

	//保存密码
	vm.savePassword = function(value){
		if(!value) return false;
		currentPanel.sharePassword = encodeURIComponent(value);
		PanelServices.updatePanel(currentPanel)
			.then((data) => {
				vm.onSuccess({data: currentPanel, layout: data.panelLayout});
				body.addClass('modal-open');//因为onSuccess回调里有露出滚动条，为了防止闪动，这里需要加回去
				vm.myOptions.sharePasswordSuccess = true;
				$timeout(function(){
					vm.myOptions.sharePasswordSuccess = false;
				},3000);
			},() => {
				vm.onFailure();
			});
		//全站事件统计
		Track.log({'where': 'panel_share','what': 'save_panel_password','how': 'click','value': currentPanel.sharePassword});
	};
	//清除密码
	vm.clearPassword = function(){
		//全站事件统计
		Track.log({'where': 'panel_share','what': 'select_panel_password','how': 'click','value': vm.myOptions.sharePasswordStatus});
		currentPanel.sharePassword = vm.myOptions.sharePassword = '';
		vm.myOptions.sharePasswordSuccess = false;
		PanelServices.updatePanel(currentPanel)
			.then((data) => {
				vm.onSuccess({data: currentPanel, layout: data.panelLayout});
				vm.myOptions.sharePasswordSuccess = false;
			},() => {
				vm.onFailure();
			});
	};

	//更新密码
	vm.changePassword = function(){
		vm.myOptions.sharePasswordSuccess = false;
	}

}

export default panelShareController;
