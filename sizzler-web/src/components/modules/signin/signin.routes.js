
loadResolve.$inject = ['sessionContext', '$q', '$ocLazyLoad'];

function loadResolve(sessionContext, $q, $ocLazyLoad ){
	var deferred = $q.defer();

	// 需要判断当前是否是已登录状态
	var sid = cookieUtils.get('sid');
	if (sid) {
		sessionContext.saveSession(sid, 'signin');
	} else {
		require.ensure([], function (require) {
			require('components/modules/signin/signin.css');
			var ctrl = require('components/modules/signin/signin.ctrl');

			deferred.resolve(ctrl.controller);
		});
	}

	return deferred.promise;
}

templateProvider.$inject = ['$q', '$translate'];

function templateProvider($q, $translate){
	var deferred = $q.defer();
	require.ensure([], function (require) {
		var template = require('components/modules/signin/signin.tpl.html');

		// 登录页展示前要取一下应该显示的语言
		$translate.use(getLocalLang().locale);

		deferred.resolve(template);
	});
	return deferred.promise;
}

export default {
	name: 'signin',
	url: '/signin?community',
	params: {type: null},
	reloadOnSearch: false,
	templateProvider: templateProvider,
	controller: 'signinCtrl',
	resolve: {
		load: loadResolve
	}
};
