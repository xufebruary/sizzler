/**
 * Created by zhangli on 2016/4/3.
 */

import {
	OTHER_LOGIN_WEB_SOCKET,
	LINK_SIGNUP_PREREGISTRATION_URL,
	openWindow,
	emailReg,
	isDomain,
	uuid
} from 'components/modules/common/common';

import consts from 'configs/const.config';

angular
    .module('pt')
    .controller("landingPageController", ['$scope', '$http', '$translate', '$state', 'sessionContext', 'siteEventAnalyticsSrv', 'websocket', '$cookies','$timeout',function ($scope, $http, $translate, $state, sessionContext, siteEventAnalyticsSrv, websocket, $cookies,$timeout) {

        //如果是datadeck.jp需要隐藏landingpage，不让显示
        $scope.noLandingPage = false;
        if(isDomain('datadeck.jp') || isDomain('datadeck.com')){
            $scope.noLandingPage = true;
            return false;
        }

    $scope.user = {};
    $scope.authError = false;
    $scope.errorMessage = "";

    $scope.user.weekStart = 'sunday';
    $scope.user.locale = getLocalLang().locale;

    $scope.loadSetting = {};
    $scope.loadSetting.modelWindow = false;
    $scope.loadSetting.topNav = false;
    $scope.open = function(){
        $scope.loadSetting.modelWindow = true;
    }

    $scope.openTopNav = function(){
        var fixedBar = $('.landing-page .fixed-bar'),mobilDiv = fixedBar.find('.mobil');
        if(fixedBar.hasClass('opened') && mobilDiv.hasClass('open')){
            $scope.loadSetting.topNav = false;
        }else{
            $scope.loadSetting.topNav = true;
        }
    };

    $scope.close = function(){
        $scope.loadSetting.modelWindow = false;
    }

    $scope.enterKeyUp = function(e,form){
        var keycode = window.event?e.keyCode:e.which;
        if(keycode==13){
            $scope.signupPreRegistration(form);
        }
    };

    //回车
    $scope.preRegisterKeyup = function(e,form){
        var keycode = window.event?e.keyCode:e.which;
        if(keycode==13){
            $scope.signupPreRegistration(form);
        }
    };
    $scope.signupPreRegistration = function (form) {
        $scope[form].email.$dirty = true;
        $scope[form].userPassword.$dirty = true;
        if (!$scope.user.userEmail || !$scope.userPassword) {
            return;
        }
        if (!emailReg.test($scope.user.userEmail)) {
            $scope.authError = true;
            $scope.errorMessage = $translate.instant('LOGIN.ERROR_TIP.EMAIL_FORMAT');
            return;
        }
        $scope.user.userPassword = $.md5($scope.userPassword);
        $scope.loadSetting.signin = true;
        $http({
            method: 'POST',
            url: LINK_SIGNUP_PREREGISTRATION_URL,
            data: angular.toJson($scope.user)
        }).success(function (data, status, headers, config) {
            if (data.status == 'success') {
                setCookie("ptId", data.content.uid);
                setCookie("ptEmail", data.content.ptEmail);
                //用户预注册成功返回前台的email
                localStorage.setItem("preRegistrationEmail", data.content.ptEmail);
                $state.go('preRegistrationSuccess',{from:'preRegistration'});
            } else if (data.status == 'failed') {
                $scope.authError = true;
                $scope.errorMessage = $translate.instant(data.message);
            } else if (data.status == 'error') {
                $scope.authError = true;
                $scope.errorMessage = $translate.instant('LOGIN.SIGNUP') + $translate.instant('COMMON.ERROR');
                console.log(data);
            }
            $scope.loadSetting.signin = false;
        }).error(function (data, status, headers, config) {
            $scope.authError = false;
            $scope.loadSetting.signin = false;
            console.log('server error')
        });
    };

    $scope.gaSignup = function () {
        $scope.authorization(consts.BACK_UI_URL + '/connect/googlelogin?authType=signup');
    };
    $scope.linkedinSignup = function () {
        $scope.authorization(consts.BACK_UI_URL + '/connect/linkedinlogin?authType=signup');
    };
    $scope.facebookSignup = function () {
        $scope.authorization(consts.BACK_UI_URL + '/connect/facebooklogin?authType=signup');
    };

    $scope.authorization = function (url) {
        var sign = uuid();
        //链接socket;
        var accreditSocket = new websocket;
        accreditSocket.initWebSocket(OTHER_LOGIN_WEB_SOCKET + sign);
        //授权验证跳转
        openWindow(url + '&sign=' + sign + '&localLang=' + getLocalLang().locale);
        //监听授权socket返回值
        $scope.wsData = accreditSocket.colletion;
        accreditSocket.ws.onmessage = function (event) {
            $scope.$apply(function () {
                $scope.wsData = event.data;
            });
        };
        var mywatch = $scope.$watch('wsData', function (newValue, oldValue, scope) {
			if (!newValue || newValue === oldValue) {
				return;
			}

			//注销当前监听事件
			mywatch();
			newValue = angular.fromJson(newValue);

			if (newValue.status == 'success') {

                setCookie("ptId", newValue.content.ptId);
                setCookie("ptEmail", newValue.content.ptEmail);
                localStorage.setItem("preRegistrationEmail", newValue.content.ptEmail);
                if (newValue.content.userStatus == 'success') {
                    //用户预注册成功返回前台的email
                    $state.go('preRegistrationSuccess',{from:'preRegistration'});
                } else if (newValue.content.userStatus == 'exists') {
                    //用户预注册成功返回前台的email
                    $state.go('preRegistrationSuccess');
                }

                //关闭socket
                accreditSocket.disconnect();
                console.log(newValue);
            }
        });
    };


    $scope.facebookShare = function () {
        var url = "https://www.facebook.com/dialog/feed?app_id=" + consts.FACEBOOK_SHARE_APP_ID_JP +
            "&redirect_uri=" + consts.WEB_UI_URL + "/shareing.html" +
            "&name=期間限定のPtOne先行体験ユーザー登録を受付しました！" +
            "&description=毎日見たい重要なKPIなどのデータを簡単に管理できるビジネスダッシュボードサービス。様々なSaaS型サービスやデーターベースと連携したクラウド型ソフトウェアです。" +
            "&link=http://www.ptone.jp" +
            "&picture=https://pbs.twimg.com/media/CfT8slCUIAECIIq.jpg";
        openWindow(url);
    };


    //banner 置顶
    $(window).scroll(function () {
        var scrollTop = $(this).scrollTop();
        if(scrollTop > 0){
            $(document).find('.landing-page .fixed-bar').addClass('fixed');
        }else{
            var fixedBar = $(document).find('.landing-page .fixed-bar');
            fixedBar.removeClass('fixed');
            if(fixedBar.find('.mobil').hasClass('open')){
                fixedBar.addClass('opened');
            }else{
                fixedBar.removeClass('opened');
            }
        }
    });

    if(window.localStorage && localStorage.getItem("preRegistrationEmail")){
        $scope.loadSetting.preRegistrationName = localStorage.getItem("preRegistrationEmail").split('@')[0];
    }


    twttr.ready(function(twttr) {
        twttr.events.bind('tweet', function (event) {
            var ptEmail = window.localStorage && localStorage.getItem("preRegistrationEmail");
            var type = "twitter";
            if(ptEmail){
                $.ajax({
                    url: consts.WEB_MIDDLE_URL + '/pt/users/updateForwardCount/' + ptEmail + '/' + type,
                    type: "POST",
                    contentType: "application/json;charset=UTF-8",
                    success: function (data) {

                    }
                });
            }
        });
    });






    /*******************
     * role
     *******************/
    $scope.myInterval = 5000;
    var slides = $scope.slides = [];
    $scope.addSlide = function() {
        slides.push({
            image: 'img/c' + slides.length + '.jpg',
            text: ['Carousel text #0','Carousel text #1','Carousel text #2','Carousel text #3'][slides.length % 4]
        });
    };
    for (var i=0; i<4; i++) {
        $scope.addSlide();
    }



    /*******************
     * Login svg
     *******************/
    $scope.landingPageSvg = function(w,h) {
        //var w = angular.element('.ft-login').width();
        //var h = angular.element('.ft-login').height();

        var points = "0 "+parseInt(h*0.08)+"," +
            parseInt(w*0.29) + " " + h + "," +
            parseInt(w*0.73) + " " + h + "," +
            w + " " + parseInt(h*0.23) + "," +
            w + " " + parseInt(h*0.19) + "," +
            parseInt(w*0.36) + " " + 0 + "," +
            0 + " " + 0;

        $scope.myOptions.points = points;
    }

    $scope.myOptions = {
        points: null
    };

    //window.onresize = function () {
    //    $scope.myOptions.points = $scope.landingPageSvg();
    //};


    /**
     * banner 星空效果
     */

    $timeout(function(){
        // 初始化 传入dom id
        var victor = new Victor("container", "output");
        victor(["#35ac03", "#3f4303"]).set();
    });


    /**
     * GTM Event
     *
     */
    $scope.gtmEvent = function(type){
        switch (type) {
            case 'community':
                try {
                    dataLayer_TZGC5N.push({
                        'event': 'click_element',
                        'pos': {
                            'area': 'header_nav',
                            'element_id': 'community'
                        }
                    });
                } catch (e) {}
                break;
            case 'signin':
                try {
                    dataLayer_TZGC5N.push({
                        'event': 'click_element',
                        'pos': {
                            'area': 'header_nav',
                            'element_id': 'signin'
                        }
                    });
                } catch (e) {}
                break;
            case 'signup':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'header_nav',
                            'element_id' : "signup"
                        }
                    });
                } catch (e) {}
                break;
            case 'signup_popup_email':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_popup',  'element_id' : 'email'}
                    });
                } catch (e) {}
                break;
            case 'signup_popup_password':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_popup',  'element_id' : 'password'}
                    });
                } catch (e) {}
                break;
            case 'signup_popup_btn':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_popup',  'element_id' : 'signup'}
                    });
                } catch (e) {}
                break;
            case 'signup_popup_google':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_popup',  'element_id' : 'google'}
                    });
                } catch (e) {}
                break;
            case 'signup_popup_facebook':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_popup',  'element_id' : 'facebook'}
                    });
                } catch (e) {}
                break;
            case 'signup_popup_linkedin':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_popup',  'element_id' : 'linkedin'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar1_email':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar1',  'element_id' : 'email'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar1_password':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar1',  'element_id' : 'password'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar1_btn':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar1',  'element_id' : 'signup'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar1_google':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar1',  'element_id' : 'google'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar1_facebook':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar1',  'element_id' : 'facebook'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar1_linkedin':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar1',  'element_id' : ' linkedin'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar_bottom_email':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar_bottom',  'element_id' : 'email'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar_bottom_password':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar_bottom',  'element_id' : 'password'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar_bottom_btn':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar_bottom',  'element_id' : ' signup'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar_bottom_google':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar_bottom',  'element_id' : 'google'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar_bottom_facebook':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar_bottom',  'element_id' : 'facebook'}
                    });
                } catch (e) {}
                break;
            case 'signup_bar_bottom_linkedin':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element', 'pos' : { 'area' : 'signup_bar_bottom',  'element_id' : ' linkedin'}
                    });
                } catch (e) {}
                break;
            case 'community_feedback_bar_1':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'community_feedback_bar_1',
                            'element_id' : 'tweet'
                        }
                    });
                } catch (e) {}

                openWindow("https://twitter.com/intent/tweet?text=@PtOne_jp&nbsp;");
                break;
            case 'community_feedback_bar_2':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'community_feedback_bar_2',
                            'element_id' : 'tweet'
                        }
                    });
                } catch (e) {}

                openWindow("https://twitter.com/intent/tweet?text=@PtOne_jp&nbsp;");
                break;
            case 'bottom_info_company':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'bottom_info',
                            'element_id' : 'company'
                        }
                    });
                } catch (e) {}

                openWindow("http://www.ptmind.co.jp/");
                break;
            case 'bottom_info_treaty':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'bottom_info',
                            'element_id' : 'treaty'
                        }
                    });
                } catch (e) {}

                openWindow("http://support.ptone.jp/treaty");
                break;
            case 'bottom_info_contact':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'bottom_info',
                            'element_id' : 'contact us'
                        }
                    });
                } catch (e) {}

                openWindow("http://www.ptmind.co.jp/contact");
                break;
            case 'bottom_info_community':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'bottom_info',
                            'element_id' : 'community'
                        }
                    });
                } catch (e) {}
                break;
            case 'bottom_info_facebook':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'bottom_info',
                            'element_id' : 'facebook'
                        }
                    });
                } catch (e) {}

                openWindow("https://www.facebook.com/ptone.jp");
                break;
            case 'bottom_info_twitter':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'bottom_info',
                            'element_id' : 'twitter'
                        }
                    });
                } catch (e) {}

                openWindow("https://twitter.com/Ptone_jp");
                break;
            case 'bottom_info_youtube':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'bottom_info',
                            'element_id' : 'youtube'
                        }
                    });
                } catch (e) {}

                openWindow("https://www.youtube.com/channel/UCUuf9BD0aRPdrdOzwVcgJdA");
                break;
            case 'succ_page_more_community':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'succ_page',
                            'element_id' : 'more_community'
                        }
                    });
                } catch (e) {}

                openWindow("http://community.ptone.jp/session/sso");
                break;
            case 'community_refer_facebook':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'community_refer',
                            'element_id' : 'facebook'
                        }
                    });
                } catch (e) {}
                break;
            case 'community_refer_twitter':
                try {
                    dataLayer_TZGC5N.push({
                        'event' : 'click_element',
                        'pos' : {
                            'area' : 'community_refer',
                            'element_id' : 'tweet'
                        }
                    });
                } catch (e) {}

                openWindow("https://twitter.com/intent/tweet?text=期間限定のPtOne先行体験ユーザー登録を受付しました！誰でも簡単に、あらゆる役割の人たちが利用できるデータプラットフォームです。  http://www.ptone.jp   pic.twitter.com/6x9ZPNblsx");
                break;
        }
    }
}]);

angular
    .module('pt')
    .directive('ldpCompare',[function(){
    return {
        restrict: 'EA',
        link: link
    };

    function link(scope,ele,attrs){
        var flag = false,
            scrollFlag = true,
            startX,
            dividerX,
            dividerLeft,
            beforeWidth;
        $(ele).on('mousedown touchstart','.slider',function(e){
            flag = true;
            scrollFlag = false;
            startX = e.clientX;
            if(e.type == 'touchstart'){
                startX = e.originalEvent.touches[0].clientX;
            }
            dividerLeft = +$(ele).find('.divider').css('left').split('px')[0];
            beforeWidth = +$(ele).find('.before').css('width').split('px')[0];
            var rect = getRect(e.currentTarget);
            dividerX = rect.left;
            console.log(e);
        });
        $(ele).on('mousemove touchmove',function(e){
            var windowWidth = window.innerWidth;
            var maxWidth = 620;
            if(windowWidth < 620){
                maxWidth = 390;
                if(windowWidth < 500){
                    maxWidth = 300;
                }
                if(windowWidth < 390){
                    maxWidth = 280;
                }
            }
            if(flag && !scrollFlag){
                var divider = $(ele).find('.divider'),
                    before = $(ele).find('.before');
                if(e.type == 'touchmove'){
                    var diff = e.originalEvent.touches[0].clientX - dividerX;
                }else{
                    var diff = e.clientX - dividerX;
                }

                if(43 < dividerLeft + diff){
                    if(e.type == 'touchmove'){
                        if(maxWidth > dividerLeft + diff){
                            divider.css('left',dividerLeft + diff);
                            before.css('width',beforeWidth + diff);
                        }
                    }else{
                        if(690 > dividerLeft + diff){
                            divider.css('left',dividerLeft + diff);
                            before.css('width',beforeWidth + diff);
                        }
                    }
                }
                if(e.type == 'touchmove' && (10 < dividerLeft + diff)){
                    if(maxWidth > dividerLeft + diff){
                        divider.css('left',dividerLeft + diff);
                        before.css('width',beforeWidth + diff);
                    }
                }
            }
        });

        $(document).on('mouseup touchend',function(e){
            flag = false;
        });

        dividerLeft = +$(ele).find('.divider').css('left').split('px')[0];
        beforeWidth = +$(ele).find('.before').css('width').split('px')[0];
        $(window).scroll(function () {
            var windowWidth = window.innerWidth,
                maxWidth = 370;
            if(windowWidth < 500){
                maxWidth = 200;
            }
            var scrollTop = $(this).scrollTop();
            if(scrollFlag){
                var divider = $(ele).find('.divider'),
                    before = $(ele).find('.before');
                if(dividerLeft + scrollTop < maxWidth){
                    divider.css('left',dividerLeft + scrollTop);
                    before.css('width',beforeWidth + scrollTop);
                }
            }
        });
    }
}]);


/**
 * 按宽度求高度
 * w2h
 *
 */
angular.module('pt')
    .directive('w2h', function() {
        return {
            link: function(scope, element, attr) {
                var width = angular.element(element).width();
                var height = width;
                if(attr.ratio){
                    height = parseInt(width * attr.ratio);
                }
                angular.element(element).height(height+'px');

                if(attr.type){
                    scope.landingPageSvg(width, height)
                }

                //console.log(height)
            }
        };
    });

