'use strict';

PanelServices.$inject = ['SpaceResources', 'PanelResources', 'UserResources', 'CommonResources', 'uiLoadingSrv'];

function PanelServices(SpaceResources, PanelResources, UserResources, CommonResources, uiLoadingSrv) {
	this.SpaceResources = SpaceResources;
	this.PanelResources = PanelResources;
	this.UserResources = UserResources;
	this.uiLoadingSrv = uiLoadingSrv;
	this.CommonResources = CommonResources;
}


PanelServices.prototype = {
	constructor: PanelServices,

	/**
	 * 新增操作
	 */
	addPanel: function(info){
		return this.PanelResources.addPanel(info)
	},

	/**
	 * 删除操作
	 */
	deletePanel: function(info){
		return this.PanelResources.deletePanel(info, {
			panelId: info.panelId
		})
	},

	/**
	 * 更新操作
	 */
	updatePanel: function(info){
		return this.PanelResources.updatePanel(info, {
			panelId: info.panelId
		})
	},

	/**
	 *	获取面板列表
	 */
	getPanels: function(spaceId){
		return this.SpaceResources.getSpacePanelList(null, {
			spaceId: spaceId
		});
	},

	/**
	 * 面板复制
	 */
	copyPanel: function(panelId, panelInfo){
		return this.PanelResources.copyPanel(panelInfo,{
			panelId: panelId
		})
	},

	/**
	 * 面板位置信息校验
	 */
	updatePanelLayout: function(layout){
		return this.PanelResources.updatePanelLayout(layout)
	},

	/**
	 * 获取面板具体信息
	 */
	getPanelInfo: function(panelId){
		return this.PanelResources.getPanelInfo(null, {
			panelId: panelId
		})
	},

	/**
     * 面板名称命名(序号自增)
     *
     * @type: 			名称类型(新增、复制)
     * @originalTile: 	原始标题
     * @panelLayout: 	列表
     */
    getName: function(type, originalTile, panelLayout) {
    	var maxLength = 50;
    	var title = originalTile;
    	var tail = '';
    	var num = 1;
    	var _find = function(layuot){
    		var layuotLength = layuot.length;
	        if(layuotLength > 0){
	        	for (var i = 0; i < layuotLength; i++) {
	        		var currentLayout = layuot[i];
		            if (currentLayout.type == 'panel' && currentLayout.panelTitle == title) {
		                if(type == 'add'){
		                	tail = " (" + (num++) + ")";
		                }
		                else if(type == 'copy'){
		                	tail = " (copy " + (num++) + ")";
		                }

		                //名称最大长度为50，超出部分将按需从末尾去掉相应字符
		                title = originalTile.slice(0, (maxLength-tail.length))+tail;
		                _find(panelLayout);
		                break;
		            }
		            else {
		            	if(Array.isArray(currentLayout.columns) && currentLayout.columns[0].length >0){
		            		_find(currentLayout.columns[0]);
		            	}
		            }
			    }
	        }
    	}

    	_find(panelLayout);
        return title;
    },

    /**
     * 依据ID查找具体面板信息
     */
    getMyPanel: function(panelList, panelId){
        for(var i=0; i<panelList.length; i++){
            if(panelList[i].panelId == panelId){
                return panelList[i];
            }
        }
    },

	/**
	 * 更新功能介绍信息
	 */
	updateUsersSettingsInfo: function (userSetting) {
		return this.UserResources.updateUsersSettingsInfo({
			showTips: angular.toJson(userSetting)
		});
	},

	/**
	 * 显示弹出框Loading
	 */
	showPopupLoading: function(){
		this.uiLoadingSrv.createLoading(jQuery('.pt-popup-content'));
	},

	/**
	 * 隐藏弹出框Loading
	 */
	hidePopupLoading: function(){
		this.uiLoadingSrv.removeLoading(jQuery('.pt-popup-content'));
	},

	/**
	 * 在面板位置信息中查找是否存在面板(也可以查找指定ID面板)
	 */
	layoutHasPanel: function(panelLayout, callBack, panelId){
		var falg = false;
		var _find = function(layuot){
	        for (var i = 0; i < layuot.length; i++) {
	            if (layuot[i].type == 'panel') {
	            	if(!panelId){
	            		falg = true;
	                	break;
	            	}
	            	else if(panelId && panelId == layuot[i].panelId){
						falg = true;
	                	break;
	            	}
	            }
	            else {
	            	if(Array.isArray(layuot[i].columns) && layuot[i].columns[0].length >0){
	            		_find(layuot[i].columns[0]);
	            	}
	            }
		    }
		};
		_find(panelLayout);
		callBack(falg)
	},

	/**
	 * 在面板位置信息中查找指定面板,且将文件夹折叠状态打开
	 */
	layoutFindPanel: function(panelLayout, panelId, callBack){
		var panel = null, panelId = panelId || 'first';

		var _find = function(layout, id){
			for(var i=0; i<layout.length; i++){
				var currentPanel = layout[i];

				if(currentPanel.type == 'panel'){
					if(id == 'first') id = currentPanel.panelId;

					if(currentPanel.panelId == id){
						panel = currentPanel;
						return true;
					}
				}
				else {
					if(Array.isArray(currentPanel.columns) && currentPanel.columns[0].length >0){
						if(_find(currentPanel.columns[0], id, callBack)){
							currentPanel.fold = false;
							return true;
						}
					}
				}
			}
		}
		_find(panelLayout, panelId);
		callBack(panel);
	},

	/**
	 * 面板位置信息校验
	 */
	layoutCheck: function(panelLayout, callBack){
		panelLayout = panelLayout ? panelLayout : [];
		var hasPanel = false;
		var _check = function(layuot){
	        for (var i = 0; i < layuot.length; i++) {
	            if (layuot[i].type == 'container') {
	            	delete layuot[i]['editing'];

	            	if(Array.isArray(layuot[i].columns) && layuot[i].columns[0].length >0){
	            		_check(layuot[i].columns[0]);
	            	}
	            }
	            else if(layuot[i].type == 'panel' && !hasPanel){
	            	hasPanel = true;
	            }
		    }
		};

		_check(panelLayout);
		callBack(hasPanel, panelLayout);
	},

	/**
	 * 清除本地当前空间的LocalStorage信息
	 */
	clearLocalStorage: function(spaceId){
        if (localStorage.getItem('currentDashboard')) {
            var currentDashboard = angular.fromJson(localStorage.getItem('currentDashboard'));

            if(currentDashboard[spaceId]){
                delete currentDashboard[spaceId];

                localStorage.setItem('currentDashboard', angular.toJson(currentDashboard));
            }
        }
	},

	/**
	 * 面板列表滚动至顶部
	 */
	panelListScroll: function(){
        $('.dashboard-nav-list').scrollTop(0)
	},

	/**
	 * 获取短链url
	 * @param url
     */
	getShortenUrl: function (url) {
		return this.CommonResources.generateShortUrl({
			url: url
		});
	}

};

export default PanelServices;
