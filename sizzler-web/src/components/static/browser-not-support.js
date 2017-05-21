String.prototype.supplant = function (o) {
	return this.replace(/{([^{}]*)}/g,
		function (a, b) {
			var r = o[b];
			return typeof r === 'string' || typeof r === 'number' ? r : a;
		}
	);
};

var tpl = '<div class="pt-browser-container"> \
    <div class="pt-browser-content"> \
        <div class="pt-browser-logo"> \
            <img src="/assets/images/browser/{alias}-logo.png"> \
        </div> \
        <div> \
            <h1>{title}</h1> \
            <p>{tip01}</p> \
            <p>{tip02}</p> \
            <div class="pt-browser"> \
                <a class="pt-chrome" href="{chrome}" target="_blank"> \
                    <img src="/assets/images/browser/chrome-logo.png"> \
                    <span>Chrome</span> \
                </a> \
                <a class="pt-safari" href="{safari}" target="_blank"> \
                    <img src="/assets/images/browser/safari-logo.png"> \
                    <span>Safari</span> \
                </a> \
                <a class="pt-firefox" href="{firefox}" target="_blank"> \
                    <img src="/assets/images/browser/firefox-logo.png"> \
                    <span>Firefox</span> \
                </a> \
                <a class="pt-edge" href="{edge}" target="_blank"> \
                    <img src="/assets/images/browser/Microsoft_Edge_logo.png"> \
                    <span>Microsoft Edge</span> \
                </a> \
            </div> \
        </div> \
    </div> \
    <a class="continue" onclick="continueView()" href="javascript:void(0);">{continueText}</a> \
    <div class="pt-browser-footer"> \
        <span>Powered by Ptmind</span> \
    </div> \
</div>';

var i18nConfigs = {
	'ja_JP': {
		title: '{name}をご利用いただくための推奨環境',
		tip01: '現在ご利用のブラウザは{name}にアクセスできません。',
		tip02: '動作の保証はいたしかねますので、以下のいずれかのブラウザをご利用ください。',
		chrome: 'https://www.google.co.jp/intl/ja/chrome/browser/desktop/',
		safari: 'https://support.apple.com/ja-jp/HT204416',
		firefox: 'https://www.mozilla.org/ja/firefox/new/',
		edge: 'https://www.microsoft.com/ja-jp/windows/microsoft-edge',
		continueText: 'ご利用中のプラウザでログインします'
	},
	'en_US': {
		title: '{name} Recommends Using The Most Recent Versions of The Browsers Below',
		tip01: 'You’re using an unsupported web browser.',
		tip02: 'For the best DataDeck experience, use one of the browsers below.',
		chrome: 'https://www.google.com/chrome/browser/desktop/index.html',
		safari: 'https://support.apple.com/en-us/HT204416',
		firefox: 'https://www.mozilla.org/en-US/firefox/new/',
		edge: 'https://www.microsoft.com/en-us/download/details.aspx?id=48126',
		continueText: 'Still proceed with the current browser'
	},
	'zh_CN': {
		title: '{name}推荐您使用以下最新版本的浏览器',
		tip01: '您正在使用的浏览器{name}不支持，',
		tip02: '为了保证您拥有好的使用体验，点击以下图标下载最新版本的浏览器',
		chrome: 'http://www.google.cn/chrome/browser/desktop/index.html',
		safari: 'https://support.apple.com/zh-cn/HT204416',
		firefox: 'http://www.firefox.com.cn/download/',
		edge: 'https://www.microsoft.com/zh-cn/windows/microsoft-edge',
		continueText: '继续使用当前浏览器访问'
	}
};

function render(){
	var currentLocal = getLocalLang().locale,
		configs = i18nConfigs[currentLocal];

	// body容器
	var containerEle = document.body;
	var compiledTpl = tpl.supplant(configs).supplant(productConfigs);

	// 设置样式
	containerEle.setAttribute('class', currentLocal);
	containerEle.innerHTML = compiledTpl;

	// 设置favico
	var link = document.createElement('link');
	link.type = "image/x-icon";
	link.rel = "shortcut icon";
	link.href = "/assets/images/favicon/{favicon}.ico".supplant(productConfigs);
	document.head.appendChild(link);

	// 设置title
	document.title = productConfigs.title;
}

function continueView(){
	if (!location.origin) {
	  	location.origin = location.protocol + "//" + location.hostname + (location.port ? ':' + location.port: '');
	}
	var matches = location.search.match("refer=(.*)");
	var refer = location.origin + "/";
	if(matches){
		refer = decodeURIComponent(matches[1]);
	}

	location.href = refer;
	cookieUtils.set('continue_visite', true, 1);
}


/**
 * 先暂时单独拿出来(是因为common文件内容太大了)
 * @returns {{locale: *, source: *}}
 */
function getLocalLang(){
	var href = window.location.href;
	var source,locale;
	if(href.indexOf('datadeck.jp') > -1){
		locale = 'ja_JP';
		source = 'dd-jp-basic';
	}else if(href.indexOf('datadeck.com') > -1){
		locale = 'en_US';
		source = 'dd-en-basic';
	}else if(href.indexOf('ptone.cn') > -1 || href.indexOf('ptone.com.cn') > -1 ){
		locale = 'zh_CN';
		source = 'dd-cn-basic';//来源现和datadeck.cn一致
	}else if(href.indexOf('datadeck.cn') > -1){
		locale = 'zh_CN';
		source = 'dd-cn-basic';
	}else{
		//从productConfig中获取defaultLocal信息
		locale = productConfigs.defaultLocale || 'ja_JP';
		source = productConfigs.source || 'dd-jp-basic';
	}

	var settingLan = localStorage.getItem('DATADECK_LANG_SETTING');
	// 用户登录并且本地已存储i18nkey
	if(cookieUtils.get('sid') && settingLan){
		locale = settingLan;
	}

	return {'locale':locale,'source':source};
}

// 开始执行
render();

