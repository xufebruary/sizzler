// 'use strict';

import timeTpl from 'components/partial/dashboard/dashboard-time.tpl.html';

import {
  getBeforeDay
} from 'components/modules/common/common';

/**
 * dashboard time
 */
angular
    .module('pt')
    .directive('dashboardTime', ['$rootScope', '$document', 'dataMutualSrv', 'PanelResources', 'siteEventAnalyticsSrv', dashboardTime]);


function dashboardTime($rootScope, $document, dataMutualSrv, PanelResources, siteEventAnalyticsSrv) {

    return {
        restrict: 'EA',
        replace: true,
        template: timeTpl,
        link: link
    };

    function link(scope, element, attrs) {
        var myOptions = scope.myOptions = {
            currentTime: null, //widgetTime || dashboardTime
            modelTime: 'widgetTime',
            nowDate: getBeforeDay(0).format('MM/dd/yyyy'),

            //last days
            modelLastDays: 7,
            modelLastDaysTmp: 7,//暂存上次输入数字,以备校验时用
            modelToday: 'past', //Include(last) || Exclude(past)

            //form today
            modelFrom: getBeforeDay(8).format('MM/dd/yyyy'),
            fromKey: null,
            dpFromShow: false,

            //Fixed Time Range
            modelFixed: [getBeforeDay(8).format('MM/dd/yyyy'), getBeforeDay(1).format('MM/dd/yyyy')],
            fixedKey: null,
            dpFixedShow: false,

            //other
            datePickerHideClick: false,    //排除删除及新增操作时的dom关闭事件

            //apply btn disabled
            applyDisabled: false
        };

        //全局时间初始化
        dashboardTimeInit();

        //全局时间
        var documentClickBindDashboardTime = function(event) {
            if (!element[0].contains(event.target) && !angular.element(event.target).parents().hasClass('dashboard-time')) {
                scope.$apply(function() {
                    scope.modal.dashboardTime.isOpen = false;
                });
                $document.unbind('click', documentClickBindDashboardTime);
            }
        };
        $document.bind('click', documentClickBindDashboardTime);


        //关闭提示层
        scope.closeDom = function(){
            scope.modal.dashboardTime.isOpen = false;
            $document.unbind('click', documentClickBindDashboardTime);
        };

        //Cancel
        scope.cancelDashboardTime = function(){
            scope.closeDom();
            dashboardTimeInit();

            //全站事件统计
            siteEventAnalyticsSrv.createData({
                uid: $rootScope.userInfo.ptId,
                where: "panel_global_time",
                what: "cancel_global_time",
                how: "click",
                value: myOptions.currentTime != 'widgetTime' ? myOptions.modelTime : 'widgetTime'
            });
        };

        //切换时间类型
        scope.toggleTimeType = function(){
            if(myOptions.currentTime == 'widgetTime'){
                myOptions.modelTime = 'widgetTime';
                myOptions.dpFromShow = false;
                myOptions.dpFixedShow = false;
            } else {
                myOptions.modelTime = 'last_days';
                myOptions.modelLastDays = 7;
                myOptions.modelLastDaysTmp = 7;
                myOptions.modelToday = 'past';
            }
        };

        //切换全局时间值
        scope.setModedlTime = function(){
            myOptions.currentTime = 'dashboardTime';
            var modelTime = myOptions.modelTime;

            if(modelTime == 'from_today'){
                myOptions.dpFromShow = true;
                myOptions.dpFixedShow = false;
            } else if(modelTime == 'fixed'){
                myOptions.dpFixedShow = true;
                myOptions.dpFromShow = false;
            } else {
                myOptions.dpFromShow = false;
                myOptions.dpFixedShow = false;
            }
        };

        //设置dashboard time
        scope.setDashboardTime = function(type){

            var sendData = {
                panelId: scope.rootPanel.nowId,
                status: 1,
                value: 'widgetTime',
                itemId: 16,
                code: 'GLOBAL_TIME',
                name: 'GLOBAL_TIME'
            };
            var value = myOptions.currentTime;

            if(myOptions.currentTime != 'widgetTime'){
                value = myOptions.modelTime;
                switch (myOptions.modelTime){
                    case "last_days":
                        value = myOptions.modelToday+myOptions.modelLastDays+'day';
                        break;
                    case "from_today":
                        value = new Date(myOptions.modelFrom).format('yyyy-MM-dd')+'|today';
                        break;
                    case "fixed":
                        value = new Date(myOptions.modelFixed[0]).format('yyyy-MM-dd')+'|'+new Date(myOptions.modelFixed[1]).format('yyyy-MM-dd');
                        break;
                }
                sendData['value'] =  value;
            }

            if(angular.isDefined(scope.sharePanelFlag)){
                //分享页面无需保存

                if(myOptions.currentTime == 'widgetTime'){
                    scope.rootPanel.now.globalComponentStatus = 0;
                    scope.rootPanel.now.components['GLOBAL_TIME']['value'] = value;
                    scope.rootPanel.now.components['GLOBAL_TIME']['status'] = 0;
                } else {
                    scope.rootPanel.now['components'] = {'GLOBAL_TIME': {}};

                    scope.rootPanel.now.globalComponentStatus = 1;
                    scope.rootPanel.now.components['GLOBAL_TIME']['value'] = value;
                    scope.rootPanel.now.components['GLOBAL_TIME']['status'] = 1;
                }

                scope.modal.dashboardTime.dateKey = value;
                scope.closeDom();
            } else {
                var index=0;
                for(var i=0; i<scope.rootPanel.list.length; i++){
                    if(scope.rootPanel.list[i].panelId == scope.rootPanel.now.panelId){
                        index = i;
                        break;
                    }
                }

                PanelResources.applyPancelComponent(sendData)
                .then((data) => {
                    if(myOptions.currentTime != 'widgetTime'){
                        if(!scope.rootPanel.list[index].components[sendData.code]){
                            scope.rootPanel.list[index].components[sendData.code] = {};
                            scope.rootPanel.now.components[sendData.code] = {};
                        }

                        scope.rootPanel.list[index].globalComponentStatus = 1;
                        scope.rootPanel.list[index].components[sendData.code]['value'] = value;
                        scope.rootPanel.list[index].components[sendData.code]['status'] = 1;

                        scope.rootPanel.now.globalComponentStatus = 1;
                        scope.rootPanel.now.components[sendData.code]['value'] = value;
                        scope.rootPanel.now.components[sendData.code]['status'] = 1;
                    }
                    else {
                        angular.forEach(scope.rootPanel.list[index].components, function(value, key){
                            scope.rootPanel.list[index].components[key].status = 0;
                        });
                        scope.rootPanel.list[index].globalComponentStatus = 0;

                        angular.forEach(scope.rootPanel.now.components, function(value, key){
                            scope.rootPanel.now.components[key].status = 0;
                        });
                        scope.rootPanel.now.globalComponentStatus = 0;
                    }

                    scope.modal.dashboardTime.dateKey = value;
                    scope.closeDom();
                })
            }

            //全站事件统计
            siteEventAnalyticsSrv.createData({
                uid: $rootScope.userInfo.ptId,
                where: "panel_global_time",
                what: "apply_global_time",
                how: "click",
                value: myOptions.currentTime != 'widgetTime' ? sendData.value : 'widgetTime'
            });
        };

        //修改日历时间
        scope.dateChange = function(type, date){
            scope.$apply(function(){
                if(type == 'from'){
                    myOptions.modelFrom = date;
                    myOptions.fromKey = new Date(myOptions.modelFrom).format('yyyy-MM-dd')+'|today';
                } else if(type == 'fixed'){
                    myOptions.modelFixed = date.split(',');
                    myOptions.fixedKey = new Date(myOptions.modelFixed[0]).format('yyyy-MM-dd')+'|'+new Date(myOptions.modelFixed[1]).format('yyyy-MM-dd');
                }
            })
        };

        //日历显示与隐藏
        scope.togglePicker = function(type){
            myOptions.modelTime = type;
            myOptions.currentTime = 'dashboardTime';

            if(type == 'fixed'){
                myOptions.dpFixedShow = true;
                myOptions.dpFromShow = false;

            } else if(type == 'from_today') {
                myOptions.dpFromShow = true;
                myOptions.dpFixedShow = false;

            } else {
                myOptions.dpFromShow = false;
                myOptions.dpFixedShow = false;
            }

        };

        //过去期间输入校验
		var MAX_NUMBER = 999;
        scope.lastDaysSet = function(type){
            var reg = new RegExp("^[1-9][0-9]{0,2}$");
            var iptValue = myOptions.modelLastDays;

            if (iptValue && !reg.test(iptValue)) {
                myOptions.modelLastDays = myOptions.modelLastDaysTmp;
            } else {
                if(type == 'change'){
                    if(iptValue != null){
                        iptValue = +iptValue;
                        myOptions.modelLastDays = iptValue;
                        myOptions.modelLastDaysTmp = iptValue;
                    }else{
						if(iptValue === undefined){
							myOptions.modelLastDays = myOptions.modelLastDaysTmp;
						}
					}
                } else if(type == 'blur'){
                    if(!iptValue){ // 赋值为上次一保存过的值
						myOptions.modelLastDays = myOptions.modelLastDaysTmp;
                    }
                }
            }
        };

        //全局时间初始化
        function dashboardTimeInit(){
            var dateKey = scope.modal.dashboardTime.dateKey;
            if(dateKey != 'widgetTime'){
                myOptions.currentTime = 'dashboardTime';

                if(dateKey.indexOf('|')>-1){
                    var sdt = dateKey.split('|')[0];
                    var edt = dateKey.split('|')[1];

                    if(edt == 'today'){
                        myOptions.modelTime = 'from_today';
                        myOptions.modelFrom = new Date(sdt).format('MM/dd/yyyy');
                    } else {
                        myOptions.modelTime = 'fixed';

                        myOptions.modelFixed = [new Date(sdt).format('MM/dd/yyyy'), new Date(edt).format('MM/dd/yyyy')];
                    }
                } else if(dateKey.indexOf('last')==0 && dateKey.indexOf('last_')<0 || dateKey.indexOf('past')==0){
                    myOptions.modelTime = 'last_days';
                    myOptions.modelLastDays = +(dateKey.match(/\d+/g)); //提取具体天数
                    myOptions.modelLastDaysTmp = +(dateKey.match(/\d+/g)); //提取具体天数
                    myOptions.modelToday = dateKey.indexOf('last')==0 ? 'last':'past';
                } else {
                    myOptions.modelTime = dateKey;
                }
            } else {
                myOptions.currentTime = 'widgetTime';
            }
        }
    }
}

