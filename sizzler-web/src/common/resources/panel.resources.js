import {
	LINK_API_VERSION
} from 'components/modules/common/common';
import resourceWrapper from './resourceWrapper';

var basicUrl = LINK_API_VERSION + 'panels';
var resources = [

	// 新增（面板/文件夹）
	{
		name: 'addPanel',
		method: 'post',
		url: basicUrl
	},

	// 复制（面板）
	{
		name: 'copyPanel',
		method: 'post',
		url: basicUrl + '/{panelId}/copy'
	},

	// 新增（面板模版）
	{
		name: 'createByTemplet',
		method: 'post',
		url: basicUrl + '/templet'
	},

	// 新增（分享面板添加至指定空间）
	{
		name: 'addSharePanel',
		method: 'post',
		url: basicUrl + '/share'
	},

	// 验证 (分享面板密码)
	{
		name: 'sharePanelVerifyPassword',
		method: 'post',
		url: basicUrl + '/share/verification'
	},

	// 验证 (分享面板在指定空间下是否存在)
	{
		name: 'existsSharePanel',
		method: 'post',
		url: basicUrl + '/share/exists'
	},

	// 应用 (面板全局控件)
	{
		name: 'applyPancelComponent',
		method: 'put',
		url: basicUrl + '/components/apply'
	},

	// 更新（面板/文件夹）
	{
		name: 'updatePanel',
		method: 'put',
		url: basicUrl + '/{panelId}'
	},
	
	// 更新（面板位置）
	{
		name: 'updatePanelLayout',
		method: 'put',
		url: basicUrl + '/layout'
	},

	// 删除（面板/文件夹）
	{
		name: 'deletePanel',
		method: 'delete',
		url: basicUrl + '/{panelId}'
	},

	// 查找（面板下的widget列表）
	{
		name: 'findWidget',
		method: 'get',
		url: basicUrl + '/{panelId}/widgets?device={device}&password={password}&accessToken={accessToken}'
	},

	// 获取（面板具体信息）
	{
		name: 'getPanelInfo',
		method: 'get',
		url: basicUrl + '/{panelId}/?accessToken={accessToken}'
	}
];

export default resourceWrapper(resources);
