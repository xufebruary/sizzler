import {
	LINK_API_VERSION
} from 'components/modules/common/common';
import resourceWrapper from './resourceWrapper';

var basicUrl = LINK_API_VERSION + 'panel-templets';
var resources = [

	// 发布/取消发布 (panelTemplet) 
	{
		name: 'publishPanelTemplet',
		method: 'post',
		url: basicUrl + '/publish'
	},

	// 更新 (panelTemplet) 
	{
		name: 'updateTempletPanel',
		method: 'put',
		url: basicUrl + '/{templetId}'
	},

	// 获取（panel对应tags列表 ）
	{
		name: 'getTempletTags',
		method: 'get',
		url: basicUrl + '/{templetId}/tags'
	},

	// 获取（所有panelTemplet列表 ）
	{
		name: 'getAllPanelTemplet',
		method: 'get',
		url: basicUrl
	},

	// 获取（默认预制的PanelTemplet列表）
	{
		name: 'getDefaultPublishedPanelTemplet',
		method: 'get',
		url: basicUrl + '/default'
	},
	
	// 获取（已发布panelTemplet列表）
	{
		name: 'getPublishedPanelTemplet',
		method: 'get',
		url: basicUrl + '/published'
	},
	
	// 获取（未发布panelTemplet列表 ）
	{
		name: 'getUnpublishedPanelTemplet',
		method: 'get',
		url: basicUrl + '/unpublished'
	}
];

export default resourceWrapper(resources);
