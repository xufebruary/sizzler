'use strict';
import {
  LINK_SETTINGS_INFO_UPDATE
}from '../../modules/common/common';

import onBoardingTpl from 'components/partial/onboarding/onboarding.tpl.html';
// import 'vendor/jquery/bxslider/jquery.bxslider.css';
import 'assets/libs/jquery/bxslider/jquery.bxslider';

angular
    .module('pt')
    .directive('onBoarding', ['$state', 'dataMutualSrv', onBoarding]);

function onBoarding ($state, dataMutualSrv){
    return {
        restrict: 'EA',
        template: onBoardingTpl,
        link: link
    };
    function link(scope, element) {
        scope.onBoarding = {
            hideOnboarding: 0,
            locale:''
        };
        var onBoarding = $('.on-boarding');
        onBoarding.on('click','.skip,.start',function(){
            onBoarding.find('.inner,.on-boarding-model').fadeOut();
            $('body').css('overflow','auto');
            $(document).off('keydown');
            scope.rootUser.settingsInfo.hideOnboarding = 1;

            dataMutualSrv.post(LINK_SETTINGS_INFO_UPDATE, {'hideOnboarding': 1}).then(function(data) {
                if (data.status == 'success') {
                    console.log('Setting Account Success!');
                } else {
                    if (data.status == 'failed') {
                        console.log('Setting Account Failed!');
                    } else if (data.status == 'error') {
                        console.log('Setting Account Error: ');
                        console.log(data.message)
                    }
                }
            });
        });

        /**
         * init
         *
         */
        (function(){
            if(scope.onBoarding.hideOnboarding && scope.onBoarding.locale){
                onBoarding.remove();
                $(document).off('keydown');
            }else {

                //用户信息在用户登录后,就已取回.在userInfoSrc.js;
                if (scope.rootUser.settingsInfo.hideOnboarding && scope.rootUser.settingsInfo.hideOnboarding == 1) {
                    scope.onBoarding.hideOnboarding = 1;
                    $('.on-boarding').remove();
                    $(document).off('keydown');
                } else {
                    //设置用户国际化
                    //scope.onBoarding.locale = scope.rootUser.settingsInfo.locale;

                    //当在创建空间显示时,需要去掉默认遮罩背景层
                    if ($state.current.name == 'spaceCreate') {
                        angular.element(element).find('.on-boarding-model').remove();
                    }

                    $('.on-boarding').find('.inner,.on-boarding-model').fadeIn(function(){
                        onBoarding.find('.bxslider').bxSlider({
                            infiniteLoop: false,
                            hideControlOnEnd: true,
                            onSliderLoad: function() {
                                $('.on-boarding').removeClass('invisible');
                            }
                        });
                        angular.element(window).resize();
                    });
                    $('body').css('overflow', 'hidden');
                    $(document).on('keydown', function (e) {
                        if (e.which == 37) {
                            $('.on-boarding').find('.bx-prev').click();
                        } else if (e.which == 39) {
                            $('.on-boarding').find('.bx-next').click();
                        }
                    });

                }
            }
        })()

    }
}
