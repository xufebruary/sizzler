import {
	LINK_API_VERSION
} from 'components/modules/common/common';
import resourceWrapper from './resourceWrapper';

var basicUrl = LINK_API_VERSION;

var resources = [

	{
		name: 'getTimezone',
		method: 'get',
		url: basicUrl + 'connections/{dsId}/{connectionId}/{sourceId}/timezone'
	},

	{
		name: 'updateTimezone',
		method: 'put',
		url: basicUrl + 'connections/{dsId}/{connectionId}/{sourceId}/timezone'
	}
];

export default resourceWrapper(resources);
