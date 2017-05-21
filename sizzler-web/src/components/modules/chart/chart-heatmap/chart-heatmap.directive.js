'use strict';

import tpl from './chart-heatmap.html';
import {
    LINK_HEATMAP_DATA
} from 'components/modules/common/common';
import cookieUtils from 'utils/cookie.utils';


/**
 * heatmap
 *
 */
angular
    .module('pt')
    .directive('heatmap', ['$rootScope', '$filter', '$http', heatmap]);

function heatmap($rootScope, $filter, $http) {
    return {
        restrict: 'EA',
        scope: {
            isFullScreen: '<',  //是否为全屏
            isPhone: '<',       //是否为移动端
            heatmapUrl: '<',    //热图地址
            widgetId: '<',      //widget ID
            widgetTitle: '<',   //widget标题
            chartMinSize: '<',  //chart最小单元格尺寸
            chartDrawing: '<',  //绘制状态
            chartSizeX: '<',    //当前chart的X
            chartSizeY: '<',    //当前chart的Y
            chartIsTitleUpdate: '<', //当前chart标题的修改状态
            locale:'<',         //本地语言版本
            onUpdateTitle: '&' //更新widget标题
        },
        template: tpl,
        link: link
    };

    function link(scope, element, attrs) {
        var loadingDom, errorDom, iframeDom, tipsDom, iframeUrl, errorType, containerSize, isError;

        scope.myOptions = {}

        // ==========

        init();

        // ==========

        //入口
        function init() {
            scope.myOptions.productConfigs = $rootScope.productConfigs;
            
            $(element).ready(function(){
                loadingDom = $(element).find('.pt-loading');
                errorDom = $(element).find('.heatmap-error-info');
                iframeDom = $(element).find('iframe');
                tipsDom = $(element).find('.tips-wrap');

                //校验URL
                checkUrl(scope.heatmapUrl);
            })
        }

        //校验URL（当url为空时表示为空widget）
        function checkUrl(url){
            if(url != ''){
                showLoading();
                
                var reg = new RegExp("^((https|http|ftp|rtsp|mms)?://).*(heatmap_share\.html\#ptengine\=).*");
                if (!reg.test(url)) {
                    errorType = 'url';
                    dataError();
                } else {
                    loadData(url);
                }
            }
        }

        //获取ptengine的数据
        function loadData(url) {
            iframeUrl = url;
            var urlProtocol = getProtocol(url);
 
            //使用https协议请求数据
            if(urlProtocol == 'http' && window.location.protocol != urlProtocol+':'){
                url = url.replace(getProtocol(url), 'https');
            }
            
            var ptDataUrl = url.split('heatmap_share.html#ptengine=')[0] + 'pagescene/getHeatMapJsonp.pt';
            var key = url.split('heatmap_share.html#ptengine=')[1];
            var data = {heatmapUrl: ptDataUrl+"?heatMapKey="+key};

            $http({
                method: 'POST',
                url: LINK_HEATMAP_DATA+"?sid="+cookieUtils.get('sid')+"&uiVersion="+BASE_VERSION,
                data: angular.toJson(data),
                withCredentials: true,
                timeout: 20000
            })
            .success(function (data, status, headers, config) {
                if (data.status == 'success') {
                    errorType = '';
                    dataRight(data.content);
                }
                else {
                    errorType = 'url';
                    dataError();
                }
            })
            .error(function (data, status, header, config) {
                errorType = 'url';
                dataError();
            });
        }

        //数据正确
        function dataRight(data) {
            hideError();

            if (data) {
                containerSize = +data.containerSize.width;

                //使用客户URL的HTTP协议
                var iframeUrlProtocol = getProtocol(iframeUrl);
                var overlayUrlProtocol = getProtocol(data.overlayUrl);
                iframeUrl = iframeUrl.replace(iframeUrlProtocol, overlayUrlProtocol);

                //如果当前浏览器非chrome和firefox，且当前http协议不一致时，就提示http错误
                if(!isChrome() && !isFirefox() && overlayUrlProtocol != 'https' && (overlayUrlProtocol+':') != window.location.protocol){
                    hideLoading();
                    errorType = 'http';
                    showError();
                }
                else {
                    var loadFlag = false;
                    var loadTime = 0;
                    var timeout = 5000;
                    var size = getSize();

                    //firefox浏览器特殊对应,当http协议不支持时，会出现load事件进不去的情况。所以额外添加时间校验。
                    if(isFirefox()){

                        var judgeData = setInterval(function() {
                            loadTime += 50;
                            if(loadFlag || loadTime >= timeout){
                                clearInterval(judgeData);
                                iframeLoadFinish(overlayUrlProtocol);
                            }
                        }, 50)
                    }

                    //iframe加载
                    iframeDom
                        .attr('width', size.width)
                        .attr('height', size.height)
                        .attr('src', iframeUrl)
                        .css({
                            'transform': 'scale('+ size.scale +')'
                        })
                        .load(function(){
                            loadFlag = true;

                            if(loadTime < timeout){
                                if(judgeData) clearInterval(judgeData);
                                iframeLoadFinish(overlayUrlProtocol);
                            }
                        })
                }

                //过滤器信息
                var filter = '';
                if(data.filter.length>0){
                    for (var i = data.filter.length - 1; i >= 0; i--) {
                        filter += i==0 ? data.filter[i] : (data.filter[i]+'&');
                    }
                }

                //时间范围
                var time = $filter('date')((+data.time.startTime)*1000, 'yyyy/MM/dd');
                if(data.time.startTime != data.time.endTime){
                    time += '~'+$filter('date')((+data.time.endTime)*1000, 'yyyy/MM/dd');
                }

                scope.myOptions.data = {
                    title: data.passpage.title,
                    filter: filter,
                    time: time,
                    overlayUrl: data.overlayUrl
                }

                //更新标题
                updateTitle(data.passpage.title);
            }
            else {
                hideLoading();
            }
        }

        //iframe加载完成，判断协议是否支持
        function iframeLoadFinish(overlayUrlProtocol){
            hideLoading();
            errorType = '';
            
            //如果能访问iframe内的内容就说明已经被阻止(除非iframe内的域名和当前域名一致)
            if(iframeDom.contents() && overlayUrlProtocol != 'https' && (overlayUrlProtocol+':') != window.location.protocol){
                errorType = 'http';
                showError();
            }
        }

        //数据错误(先清除旧的iframe，再新建空的iframe)
        function dataError(){
            hideLoading();
            showError();
            clearIframe();
        }

        //获取chart大小
        function getSize(){
            var chartWidth = scope.isPhone ? ($(element).width()+10) : (+scope.chartMinSize * +scope.chartSizeX); //10-chart左右内边距之和
            var chartHeight = +scope.chartMinSize * +scope.chartSizeY;
            var scale = Math.min((chartWidth-50)/320, 1);//50-热图边距及chart内边距(只有当chart小于320时，才整体缩放)
            var width = Math.min((chartWidth-50), containerSize); 
            var height = parseInt((chartHeight-10-36-24)/scale);//10-热图上下边距，36-chart头部标题区域高度，24-chart底部icon高度
            scope.myOptions.tipsDomMaxHeight = parseInt((+chartHeight-10-36-24-10-20)/3)+'px';

            //等待页面绘制完成
            $(element).ready(function(){
                if(scope.isPhone){
                    $(element).find('.widget-bd').height((height*scale+10)+'px');//10为热图底部边距
                    $(element).find('.iframe-wrap').addClass('iframe-wrap-mobile');

                    //横竖屏
                    $(window).on('orientationchange', function (e) {
                        redraw();
                    })

                    height = '90%';
                }
                else {
                    $(element).find('.iframe-wrap').removeClass('iframe-wrap-mobile');
                }
            })

            return {
                width: Math.max(width, 320),  //最新宽度320
                height: height,
                scale: scale
            }
        }

        //重绘
        function redraw(){
            var size = getSize();
            iframeDom
                .attr('width', size.width)
                .attr('height', size.height)
                .css({
                    'transform': 'scale('+ size.scale +')'
                })

            // hideLoading();
        }

        //重新加载
        function reload(){
            showLoading();
            clearIframe();
            hideError();
            checkUrl(scope.heatmapUrl);
            scope.myOptions.data = null;
        }

        //清除iframe
        function clearIframe(){
            iframeDom.remove();
            iframeDom = $('<iframe>').attr('name', 'pt_engine_share');
            iframeDom.appendTo($(element).find('.widget-bd'));
        }


        //获取域名
        function getHostName(url) {
            var e = new RegExp('^(?:(?:https?|ftp):)/*(?:[^@]+@)?([^:/#]+)'),
                matches = e.exec(url);
            return matches ? matches[1] : url;
        };

        //获取协议
        function getProtocol(url){
             var e = new RegExp(/^(?:(?:https?|ftp))/i),
                matches = e.exec(url);
            return matches ? matches[0] : 'http';
        }

        //显示loading
        function showLoading(){
            loadingDom.removeClass('hide');
        }

        //隐藏loading
        function hideLoading(){
            loadingDom.addClass('hide');
        }

        //显示错误提示
        function showError(){
            errorDom.find('.js-error-'+errorType).removeClass('hide').siblings().addClass('hide');
            errorDom.removeClass('hide');
            isError = true;
        }

        //隐藏错误提示
        function hideError(){
            errorDom.addClass('hide');
            isError = false;
        }

        //更新标题
        function updateTitle(title){
            //标题自动命名
            if (scope.chartIsTitleUpdate == '0' && title != scope.widgetTitle) {
                var sendData = {
                    widgetId: scope.widgetId,
                    widgetTitle: title
                };

                scope.onUpdateTitle({'sendData': sendData});
            }
        }

        //校验浏览器是否为Chrome
        function isChrome(){
            var ua = navigator.userAgent.toLowerCase(); 
            return ua.indexOf('safari') != -1 && ua.indexOf('chrome') > -1
        }

        //校验浏览器是否为Firefox
        function isFirefox(){
            var ua = navigator.userAgent.toLowerCase(); 
            return ua.indexOf('firefox') > -1;
        }

        // ==========
        //监测大小
        scope.$watch('chartSizeX + chartSizeY + isFullScreen', function(newSize, oldSize){
            if(newSize !== oldSize){
                redraw();
            }
        })

        //监测URL
        scope.$watch('heatmapUrl', function(newUrl, oldUrl){
            if(newUrl !== oldUrl){
                reload();
            }
        })
    }
}

/*
    {
        "hostType":"A",
        "uid":"1462445360978564",
        "sid":"112474d7",
        "siteid":"1469694820054291",
        "rangetype":"page",
        "passpage":{
            "url":"http://datatest19.ptmind.com/jp_zhr/index.html",
            "title":"Home",
            "pid":"StKiCSv5Ifk8Fo4BqbmhoQ"
        },
        "pageGroupInfo":{
            "rangetype":"page",
            "pageGroupName":"",
            "heatmapUrl":"",
            "pid":"StKiCSv5Ifk8Fo4BqbmhoQ"
        },
        "overlayUrl":"http://datatest19.ptmind.com/jp_zhr/index.html?optimizely_disable=true#ptengine=Af777c90c1b3bcc3d8979137d58b28cfc",
        "mapType":"click",
        "clickCount":0,
        "time":{"inTime":1481183629302,"startTime":1475251200,"endTime":1477843200,"timezone":"+08:00"},
        "filter":[],
        "setting":{"ua":"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)",
        "resolution":{"resolutionY":768,"resolutionX":1024}},
        "parameter":{"merge":{"url":"","paramlist":[]}},
        "mapData":{"fun":"ps_click_map","timestamp":1481183568288,"allcount":0,"samplingcount":0,"click":[]},
        "readline":{"fun":"ps_readline","timestamp":1481183568296,"readline":[]},
        "containerSize":{
            "width":1024,
            "height":768
        },
        "scale":1,
        "maxPvTerminal":"PC",
        "terminalName":"All",
        "pageOrientation":"A",
        "optimizely":null
    }
    var ptDataUrl = location.protocol + '//' + location.host + '/pagescene/getHeatMapJsonp.pt';
    var PT_KEY = window.location.href.split('ptengine=')[1];

    function loadData() {
        return $.ajax({
            url: ptDataUrl,
            data: {
                heatMapKey: PT_KEY
            },
            dataType: "jsonp",
            jsonp: "ptmindHeatMapDataJsonP"
        });
    }
    
    <iframe name="pt_engine_share"
        src="http://192.168.3.123:3100/heatmap_share.html#ptengine=f777c90c1b3bcc3d8979137d58b28cfc" frameborder="0">
    </iframe>
    
    css:
        transform: scale(0.81);
        transform-origin: 0 0;
*/
