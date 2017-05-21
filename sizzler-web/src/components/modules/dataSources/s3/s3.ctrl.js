'use strict';

import {
    LINK_EXCEL_FILE_ADD
} from '../../common/common';



/**
 * s3
 * @dataSources
 *
 */
angular
    .module('pt')
    .controller('dsS3Ctrl', ['$scope', '$document', '$translate', 'dataMutualSrv', 'uiLoadingSrv', 'DataSourcesServices', dsS3Ctrl]);

function dsS3Ctrl($scope, $document, $translate, dataMutualSrv, uiLoadingSrv, DataSourcesServices) {
    var body = $document.find('body').eq(0);

    var dsBinding = $scope.dsBinding = {
        dsCode: 's3',
        dsName:'Amazon S3',
        pageLoad: false,        //当前页面加载状态

        accountList: [],        //已授权账户列表
        accountData: [],        //当前账户账户下的数据
        accountDataCopy:[],     //当前账户账户下的数据(副本，导航使用的)
        accountWidgtCount: 0,   //当前账户下widget数量

        current: {
            mod: null,          //当前显示模块('noData','tableList','fileList','connectionPost') 
            directory : null    //当前远端目录
        },

        connection: {
            show: false,
            from: null,
            operateType: 'save',    //save || edit_save
            connectFailure: null,

            modConnectionName: null,
            modAccessKeyId: null,
            modSecretAccessKey: null
        }, 

        //文件编辑
        fileEdit: {
            index: null,
            file: null,
            errorCode: null
        },

        //目录刷新
        accountDataRefresh: {
            tipsInfo: $translate.instant('DATA_SOURCE.S3.ACCOUNT_REFRESH_SUCCESS'),
            btnClass: 'btn-success',
            btnText: $translate.instant('COMMON.OK')
        },

        //文件刷新操作
        fileRefresh: {      
            tipsInfo: $translate.instant('DATA_SOURCE.S3.FILE_REFRESH_SUCCESS'),
            file: null,
            btnClass: 'btn-success',
            btnText: $translate.instant('COMMON.OK')
        },

        //提示框
        tips:{
            show: false,
            options: {}
        },

        //文件解绑
        fileDelete: {
            file: null,
            index: null,
            fileWidgtCount: 0
        },

        //时区
        timezone: {
            show: false,
            fieds: null
        }
    };
    //数据初始化
    $scope.dsSrv.dataInit(dsBinding, 's3');

    //文件导航操作
    $('.ds-file-hd').on('click', '.guide',function(){
        var id = $(this).attr('id');
        $scope.dsSrv.getFileList(dsBinding, id);
        // dsBinding.fileListType.id = id;
        $(this).nextAll('svg,a').remove();
    });

    //刷新已存文件
    $scope.refreshFile = function(file, index){
        dsBinding.fileRefresh.file = file;

        //loading
        uiLoadingSrv.createLoading(angular.element('.pt-ds-table:eq('+index+')'));

        var sendData = angular.copy(file);
        sendData['operateType'] = 'update';

        dataMutualSrv.post(LINK_EXCEL_FILE_ADD, sendData).then(function(data) {
            var tipsInfo;
            var dataWarningFlag = false;
            if (data.status == 'success') {
                //文件刷新成功提示
                $scope.dsCtrl.dsDataTep.s3.fileList[index].remoteStatus = data.content.remoteStatus;
                $scope.dsCtrl.dsDataTep.s3.fileList[index].updateTime = data.content.updateTime;

                if(data.content.remoteStatus == 0){
                    dataWarningFlag = true;
                } else {
                    dataWarningFlag = false;
                }
                for (var i = 0; i < $scope.dsCtrl.dsDataTep.s3.fileList.length; i++) {
                    if($scope.dsCtrl.dsDataTep.s3.fileList[i].fileId == file.fileId){
                       $scope.dsCtrl.dsDataTep.s3.fileList[i].remoteStatus = data.content.remoteStatus;
                    }
                };

                dsBinding.fileRefresh.tipsInfo = $translate.instant('DATA_SOURCE.S3.FILE_REFRESH_SUCCESS')
                dsBinding.fileRefresh.btnClass = 'btn-success',
                dsBinding.fileRefresh.btnText = $translate.instant('COMMON.OK');
            } else {
                //文件刷新失败提示
                dsBinding.fileRefresh.tipsInfo = $translate.instant('DATA_SOURCE.S3.FILE_REFRESH_FAILURE');
                dsBinding.fileRefresh.btnClass = 'btn-default';
                dsBinding.fileRefresh.btnText = $translate.instant('DATA_SOURCE.S3.REFRESH_AGAIN');

                if (data.status == 'failed') {
                    console.log('refreshFile Failed!');
                } else if (data.status == 'error') {
                    console.log('refreshFile Error: ');
                    console.log(data.message)
                }
            }

            //loading
            uiLoadingSrv.removeLoading(angular.element('.pt-ds-table:eq('+index+')'));
            if(dataWarningFlag){
                showTips('dataWarning');
            } else {
                showTips("fileRefresh");
            }
        })
    }

    //文件-时区设置
    $scope.setTimezone = function(file, index){
        //loading
        uiLoadingSrv.createLoading(angular.element('.pt-ds-table:eq(' + index + ')'));

        console.log(file);

        let sendData = {
            dsId: file.dsId,
            connectionId: file.connectionId,
            sourceId: file.sourceId
        }
        DataSourcesServices.getTimezone(sendData)
        .then((data) => {
            $scope.dsBinding.timezone.show = true;
            $scope.dsBinding.timezone.fieds = data;
            $scope.dsBinding.timezone.fieds.name = file.name;
            $scope.dsBinding.timezone.fieds.dsId = file.dsId;
            $scope.dsBinding.timezone.fieds.connectionId = file.connectionId;
            $scope.dsBinding.timezone.fieds.sourceId = file.sourceId;
        })
        .finally(() => {
            uiLoadingSrv.removeLoading(angular.element('.pt-ds-table:eq(' + index + ')'));
        })
    };

    //文件-时区设置隐藏
    $scope.hideTimezone = function(){
        $scope.dsBinding.timezone = {
            show: false,
            fieds: null
        }
    };

    //GD页面提示
    function showTips(type){
        var options = {
            title: null,
            info: null,
            btnLeftText: $translate.instant('COMMON.CANCEL'),
            btnRightText: $translate.instant('COMMON.OK'),
            btnLeftClass: 'btn-default',
            btnRightClass: 'btn-danger',
            btnLeftEvent: 'close()',
            btnRightEvent: 'close()',
            closeEvent: 'close()',
            btnLeftHide: 'false',
            btnRightHide: 'false',
            hdHide: 'false'
        }

        // body.addClass('modal-open');
        switch(type){
            //文件刷新
            case "fileRefresh":
                options.title = '文件刷新';
                options.info = $translate.instant('DATA_SOURCE.S3.NAME')+'「'+$scope.dsBinding.fileRefresh.file.name+'」'+$scope.dsBinding.fileRefresh.tipsInfo;
                options.btnRightClass = dsBinding.fileRefresh.btnClass;
                options.btnRightText = dsBinding.fileRefresh.btnText;
                options.btnLeftHide = 'true';
                options.hdHide = 'true';
                break;
            //刷新或编辑file时，远端数据发生变化
            case "dataWarning":
                options.title = '远端数据发生变化';
                options.info = $translate.instant('DATA_SOURCE.S3.DATA_CHANGE_TIP');
                options.btnRightClass = 'btn-success';
                options.btnLeftHide = 'true';
                options.hdHide = 'true';
                break;
        }

        dsBinding.tips.show = true;
        dsBinding.tips.options = options;
    }

    //关闭弹窗
    $scope.close = function(toMod) {
        body.removeClass('modal-open');
        dsBinding.tips.show = false;

        if(toMod) {
            dsBinding.current.mod = toMod;
        }
    };

    /*
        1. 新增账户(addAccount) --> 授权(addAccount) --> 获取当前账户下文件列表(getFileList) --> 添加文件(addFile)
        2. 获取授权账户(getAccountList) --> 获取当前账户下文件列表(getFileList) --> 添加文件(addFile)
    */
}