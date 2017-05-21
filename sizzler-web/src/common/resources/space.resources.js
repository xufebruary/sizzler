import {
	LINK_API_VERSION
} from 'components/modules/common/common';
import resourceWrapper from './resourceWrapper';

var basicUrl = LINK_API_VERSION + 'spaces';
var resources = [
	// 创建space
	{
		name: 'createSpace',
		method: 'post',
		url: '/pt/space/add'
	},
	// 更新space
	{
		name: 'updateSpace',
		method: 'post',
		url: '/pt/space/update'
	},

	// 删除
	{
		name: 'deleteSpace',
		method: 'post',
		url: '/pt/space/delete/{spaceId}/{host}'
	},

	// 退出
	{
		name: 'quitSpace',
		method: 'post',
		url: '/pt/space/exitSpace/{spaceId}/{userEmail}'
	},

	// 校验domain是否可用,true为可用
	{
		name: 'isDomainAvailable',
		method: 'get',
		url: '/pt/space/checkDomain/{spaceId}/{domain}'
	},

	// 接受空间邀请
	{
		name: 'acceptInvitation',
		method: 'post',
		url: '/pt/space/acceptInvite/{inviteCode}'
	},

	// 验证空间邀请码并获取空间邀请信息
	{
		name: 'getSpaceInviteInfo',
		method: 'get',
		url: '/pt/space/checkInviteUrl/{inviteCode}'
	},

	// 获取已邀请的空间成员列表
	{
		name: 'getInvitedSpaceMemberList',
		method: 'get',
		url: '/pt/space/users/{spaceId}'
	},

	// 邀请空间成员
	{
		name: 'inviteSpaceMembers',
		method: 'post',
		url: '/pt/space/inviteUsers/{spaceId}/{host}'
	},

	// 删除空间成员
	{
		name: 'deleteSpaceMember',
		method: 'post',
		url: '/pt/space/deleteSpaceUser/{spaceId}/{email}'
	},

	// 获取面板列表,并按字段isCreateSpace判断是否添加预置面板
	{
		name: 'getDashboardList',
		method: 'get',
		url: '/pt/panels/panel/{spaceId}?isCreateSpace={isCreateSpace}&localLang={localLang}'
	},

	// 校验是否有权限访问某一空间
	{
		name: 'checkHasPermissionOfSpace',
		method: 'post',
		url: '/pt/space/validate'
	},

	// 根据空间域名获取空间name
	{
		name: 'getSpaceNameByDomain',
		method: 'get',
		url: '/pt/space/by-domain/{spaceDomain}'
	},

	// 获取空间下的面板列表
	{
		name: 'getSpacePanelList',
		method: 'get',
		url: basicUrl + '/{spaceId}/panels'
	},

	// 为第一次创建空间的用户根据source预制panel
	{
		name: 'initDefaultPanel',
		method: 'post',
		url: basicUrl + '/{spaceId}/panels/default?localLang={localLang}'
	}
];

export default resourceWrapper(resources);
