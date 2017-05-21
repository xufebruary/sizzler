import resourceWrapper from './resourceWrapper';

var resources = [
	// 生成短链
	{
		name: 'generateShortUrl',
		method: 'post',
		url: '/pt/public/short-url'
	}
];

export default resourceWrapper(resources);
