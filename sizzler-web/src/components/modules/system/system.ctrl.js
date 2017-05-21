'use strict';



/**
 * dataSources
 * 
 */
angular
    .module('pt')
    .controller('systemCtrl', ['$scope', '$document', 'dataMutualSrv',systemCtrl]);

function systemCtrl($scope, $document, dataMutualSrv) {
    var body = $document.find('body').eq(0);
    $('.header').removeAttr('style');
    $scope.pt.settings.asideFolded = false;

    

    //数据源参数配置
    var myOptions = $scope.myOptions = {
        status: false,
        tipsShow: false
    };


    //Refresh Cache
    $scope.refreshCache = function() {
        dataMutualSrv.post(LINK_SYS_REFRESH_CACHE, '').then(function(data) {
            $scope.myOptions.status = data.status;
            $scope.myOptions.tipsShow = true;
            console.log(data.message);
            //两个条件都满足显示提示语
            $('#refresh_cache').show();
            setTimeout(function(){
                $scope.$apply(function(){
                    $scope.myOptions.tipsShow = false;
                });
            },2000);
        });
    };

};
