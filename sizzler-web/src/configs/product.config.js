/**
 * Created by liudong on 3/9/16.
 * 产品配置信息
 * 可以对品牌，邮箱，logo及默认语言进行配置
 */
;(function (parent, fun) {
	if (typeof exports !== 'undefined' && typeof module !== 'undefined') {
		module.exports = fun();
	} else if (typeof define === 'function' && typeof define.amd === 'object') {
		define(fun);
	} else if (typeof define === 'function' && typeof define.cmd === 'object') {
		define(fun);
	} else {
		parent.productConfigs = fun();
	}
})(window, function(){
    var location = window.location.hostname.toLowerCase();
    //产品的默认配置
    var product = {
        name: '悠哉数据平台', //品牌名称
        alias: 'uzai', //品牌名称别名
        smallLogo: '#icon-logo-uzai-small', //小图标手机上显示
        logo: '#icon-logo-uzai', //大图标pc上显示
        logoClass: {  //logo样式
            header: "headerDefalut" //dashboard头部logo
        },
        email: 'chenxi@uzai.com', //联系邮箱
        defaultLocale: 'zh_CN', //默认显示的语言
        title: '悠哉数据平台', //网站的title
        favicon: 'favicon_uzai', //title上的图标
        source: 'uzai',
        datasource: {
            ip: 'IP Address' //数据源需要授权的ip地址
        },
        defaultMapCode:'China',//默认国家地图
        weekStart:"monday",//周起始日
        link: {
            help: null, //帮助中心
            signup: null, //注册入口
            signupTreaty: null, //注册协议
            heatmapHelp: null,//热图帮助中心
            heatmapHttpsHelp: null//热图HTTPS帮助中心
        }
    };
    //根据location设置datadeck配置
    if(location.indexOf("datadeck") != -1){
        var datadeck = { //欧美
            name: 'DataDeck',
            alias: 'datadeck',
            // smallLogo: '#icon-datadeck-logo-small',
            // logo: '#icon-datadeck-logo',
            smallLogo: '#icon-datadeck-logo-small-new',
            logo: '#icon-datadeck-logo-new', //大图标pc上显示
            logoClass: {  //logo样式
                // header: "headerNewYear" //dashboard头部logo
                header: "headerDefalut"
            },
            title: 'Datadeck | Use data. Make business decisions. As a team.',
            favicon: 'favicon_datadeck',
            email: 'support@datadeck.com',
            datasource: {
                ip: '118.67.102.128/27' //数据源需要授权的ip地址
            },
            defaultMapCode:'world',//默认国家地图
            weekStart:"sunday",//周起始日
            link: {
                help: 'http://help.datadeck.com/', //帮助中心
                signup: 'https://www.datadeck.com/', //注册入口跳转到datadeck.com
                signupTreaty: 'https://www.datadeck.com/term-of-use', //注册协议
                heatmapHelp: 'http://help.datadeck.com/data-visualization/create-a-ptengine-heatmap-widget',//热图帮助中心
                heatmapHttpsHelp: 'http://help.datadeck.com/data-visualization/create-a-ptengine-heatmap-widget'//热图HTTPS帮助中心
            }
        }
        if(location.indexOf(".jp") != -1) { //日本
            datadeck.email = 'support@datadeck.jp';
            datadeck.link.help = 'http://help.datadeck.jp/';
            datadeck.link.signupTreaty = 'https://www.datadeck.jp/term-of-use';
            datadeck.defaultMapCode = 'Japan',//默认国家地图
            datadeck.weekStart = "sunday",//周起始日
            datadeck.link = {
                help: 'http://help.datadeck.jp/',
                signup: 'https://www.datadeck.jp/',
                signupTreaty: 'https://www.datadeck.jp/term-of-use',
                heatmapHelp: 'http://help.datadeck.jp/Knowledgebase/ptengine%E3%81%AE%E3%83%92%E3%83%BC%E3%83%88%E3%83%9E%E3%83%83%E3%83%97%E3%82%92%E4%BD%9C%E6%88%90%E3%81%99%E3%82%8B#pte_heatmap_share_link',
                heatmapHttpsHelp: 'http://help.datadeck.jp/Knowledgebase/ptengine%E3%81%AE%E3%83%92%E3%83%BC%E3%83%88%E3%83%9E%E3%83%83%E3%83%97%E3%82%92%E4%BD%9C%E6%88%90%E3%81%99%E3%82%8B#mix_content_method'
            }
        }else if(location.indexOf(".cn") != -1){ //中国区
            datadeck.title = 'DataDeck - 数据整合可视化平台';
            datadeck.email = 'support@datadeck.cn';
            datadeck.defaultMapCode = 'China',//默认国家地图
            datadeck.weekStart = "monday",//周起始日
            datadeck.link = {
                help: 'http://help.datadeck.cn/',
                signup: 'https://www.datadeck.cn/',
                signupTreaty: 'https://www.datadeck.cn/term-of-use',
                heatmapHelp: ' http://help.datadeck.cn/index.php/knowledgebase/create-a-ptenigne-heatmap#pte_heatmap_share_link',
                heatmapHttpsHelp: 'http://help.datadeck.cn/index.php/knowledgebase/create-a-ptenigne-heatmap#mix_content_method'
            },
            datadeck.datasource.ip = '139.220.242.32/27'
        }
        product = datadeck;
    //ptone的配置
    }else if(location.indexOf("ptone") != -1){
        var ptone = {
            name: 'Pt One', //品牌名称
            alias: 'ptone', //品牌名称别名
            smallLogo: '#icon-pt', //小图标
            logo: '#icon-ptone-login-logo', //logo的svg引用id
            logoClass: {  //logo样式
                header: "headerDefalut" //dashboard头部logo
            },
            email: 'ptone@ptmind.co.jp', //联系邮箱
            title: 'PtOne | BIより使いやすい、仕事をする人のダッシュボード', //网站的title
            favicon: 'favicon_ptone', //title上的图标
            datasource: {
                ip: '118.67.102.128/27' //数据源需要授权的ip地址
            },
            defaultMapCode:'Japan',//默认国家地图
            weekStart:"sunday",//周起始日
            link:{
                help: null, //ptone没有帮助中心
                signup: '/landingPage',
                signupTreaty: 'http://support.ptone.jp/treaty',
                heatmapHelp: 'http://help.datadeck.jp/Knowledgebase/ptengine%E3%81%AE%E3%83%92%E3%83%BC%E3%83%88%E3%83%9E%E3%83%83%E3%83%97%E3%82%92%E4%BD%9C%E6%88%90%E3%81%99%E3%82%8B#pte_heatmap_share_link',
                heatmapHttpsHelp: 'http://help.datadeck.jp/Knowledgebase/ptengine%E3%81%AE%E3%83%92%E3%83%BC%E3%83%88%E3%83%9E%E3%83%83%E3%83%97%E3%82%92%E4%BD%9C%E6%88%90%E3%81%99%E3%82%8B#mix_content_method'
            }
        };
        product = ptone;
    }
    return product;
});
