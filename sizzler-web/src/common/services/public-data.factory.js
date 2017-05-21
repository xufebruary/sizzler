'use strict';


/**
 * publicDataSrv
 * 存储一些公用数据
 */
//angular.module('pt')
//    .factory('publicDataSrv', ['dataMutualSrv', publicDataSrvFunc]);
publicDataSrvFunc.$inject = ['dataMutualSrv'];
function publicDataSrvFunc(dataMutualSrv) {
    var publicData = {

        //用户进入ptone相关
        enter: {
            type: null,         //用户进入ptone的方式: signin, signup, url(带domain参数的正确格式URL), 404;
            path: null,         //用户进入ptone的Url Path
            spaceDomain: null   //当用户进入方式为url时,domain参数值
        },

        //接受邀请相关
        invite: {
            type: null,     //进入方式,自动登录进去(autoSignin),手动登录进去(signin)
            spaceId: null,   //接受邀请进入的spaceID
            invitesCode: null //邀请URL中的加密参数
        },

        //用户创建空间及密码相关(进入onboarding)
        accountCreate: {},

        //用户设置信息
        settingsInfo: {},

        //socket
        socket: {
            id: null,
            func: null
        },

        //已授权账户下存储的档案列表信息(ga,ptengine)
        dsAccountProfile: {
            /*
             dsId: {
             connectionId: profileList,
             ...
             }
             ...
             */
        },

		dsAccountProfileOfNewStructure: {}
    };


    var publicList = {

        //space相关
        rootSpace: {
            list: [],       //space列表
            current: null   //当前space信息
        },

        //common相关
        rootCommon: {
            langList: [],   //语言字典表
            weekStart: [],  //周起始时间字典表
            tagList: [],    //tag列表
            grapList: [],   //图形列表
            timeOwns: [],   //时间粒度
            dsList: [],     //数据源字典表列表
            dsAuthList: [], //数据源已授权列表
            dataVersion: {},//数据版本号
            dashboardList: {}//前端已请求过的widget列表
        }
    };


    return {
        /**
         * 设置公共数据
         *
         */
        setPublicData: function (type, data) {
            if (arguments.length > 2) {
                switch (type) {
                    case 'dsAccountProfile':
                        //当前传入4个参数(type,dsId,connectionId,profileList)

                        var dsId = arguments[1];
                        var connectionId = arguments[2];
                        var profileList = arguments[3];

                        if (!publicData[type][dsId]) {
                            publicData[type][dsId] = {};
                        }

                        if (!publicData[type][dsId][connectionId]) {
                            publicData[type][dsId][connectionId] = {};
                        }
                        publicData[type][dsId][connectionId] = profileList;

                        console.log(publicData[type]);
                        break;

                }
                console.log(arguments[1])
            } else {
                switch (type) {
                    case 'rootSpace':
                        publicList.rootSpace = data;
                        break;
                    default:
                        publicData[type] = data;

                }
            }
        },


        /**
         * 获取公共数据
         *
         */
        getPublicData: function (type) {
            return publicData[type];
        },


        /**
         * 设置公共列表数据
         *
         */
        setPublicList: function (type) {
            var url;
            switch (type) {
                case 'enterPath':
                    url = LINK_LANGUAGE;
                    break;
            }

            dataMutualSrv.get(url).then(function (data) {
                if (data.status == 'success') {

                    switch (langList) {
                        case 'enterPath':
                            publicList.rootCommon.langList = data.content;
                            break;
                    }
                } else if (data.status == 'failed') {
                    console.log('Get langList Failed!')
                } else if (data.status == 'error') {
                    console.log('Get langList Error: ');
                    console.log(data.message)
                }
            });
        },


        /**
         * 获取公共列表数据
         *
         */
        getPublicList: function (type) {
            return publicList[type];
        },


        /**
         * 清除公共数据
         *
         */
        clearPublicData: function (type) {
            switch (type) {
                case 'rootSpace':
                    publicList.rootSpace = {
                        list: null,
                        current: null
                    };
                    break;
                case 'all':
                    publicList.rootSpace = {
                        list: [],
                        current: null
                    }
                    publicList.rootCommon = {
                        langList: [],
                        weekStart: [],
                        tagList: [],
                        grapList: [],
                        timeOwns: [],
                        dsList: [],
                        dsAuthList: [],
                        dataVersion: {},
                        dashboardList: {}
                    }
                    break;
            }


        }
    };
}

export default publicDataSrvFunc;
