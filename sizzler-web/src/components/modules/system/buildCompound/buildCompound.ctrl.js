'use strict';



/**
 * dataSources
 * 
 */
angular
    .module('pt')
    .controller('buildCompound', ['$scope', '$document', 'dataMutualSrv',buildCompound]);

function buildCompound($scope, $document, dataMutualSrv) {
    var body = $document.find('body').eq(0);
    $('.header').removeAttr('style');
    $scope.pt.settings.asideFolded = false;
    

    $scope.typeList = ["metrics", "dimension"];
    $scope.currentType = "metrics";

    function init(){
        $scope.getCompoundTempletKey($scope.currentType);
        $scope.getCompoundTemplet($scope.currentType);

        $scope.keyList = [];
        $scope.compoundTempletList = [];
        $scope.tableColumnList = [];
        $scope.newCategory = {
            "id": "",
            "name": ""
        };

        $scope.tableId = "";
        $scope.buildParam = {
            "tableId": "",
            "categoryId" : "",
            "templetIdList": [],
            "keyToColumnMap" : {}
        };
    } 

    $scope.changeType = function(type){
        $scope.currentType = type;
        init();
    };

    /**
    * 获取复合指标、维度key的列表
    * type: metrics || dimension
    */
    $scope.getCompoundTempletKey = function(type){
    	dataMutualSrv.get(LINK_COMPOUND_TEMPLET_KEY_LIST + type).then(function(data) {
            if(data.status == "success"){
                $scope.keyList = data.content;
                console.log($scope.keyList);
            }else{
                console.log(data.message);
            }
        });
    };

    /**
    * 获取复合指标、维度的列表
    * type: metrics || dimension
    */
    $scope.getCompoundTemplet = function(type){
        dataMutualSrv.get(LINK_COMPOUND_TEMPLET_LIST + type).then(function(data) {
            if(data.status == "success"){
                $scope.compoundTempletList = data.content;
                console.log($scope.compoundTempletList);
            }else{
                console.log(data.message);
            }
        });
    };

    /**
    * 获取table下的所有column
    */
    $scope.getTableColumnList = function(tableId){
        // 指定获取gd文件的所有列的列表
        dataMutualSrv.get(LINK_USER_METRICS_AND_DIMENSIONS + 6 +"/" + tableId).then(function(data) {
            if(data.status == "success"){
                $scope.tableColumnList = data.content;
                console.log($scope.tableColumnList);
            }else{
                console.log(data.message);
            }
        });
    };

    $scope.addCategory = function(category, type){
        if(confirm("add " + type + " category ?")){
            dataMutualSrv.post(LINK_ADD_METRICS_DIMENSIONS_CATEGORY + type, category).then(function(data) {
                if(data.status == "success"){
                    $scope.buildParam.categoryId = data.content.id;
                    console.log(data.content);
                    alert("add " + type + " success");
                }else{
                    alert("add " + type + " failed: " + data.message);
                    console.log(data.message);
                }
            });
        }
    };

    $scope.changeTable = function(tableId){
        if(tableId && tableId != $scope.buildParam.tableId){
            $scope.buildParam.tableId = tableId;
            $scope.getTableColumnList(tableId);
            $scope.tableColumnList = [];
            $scope.buildParam.templetIdList = [];
            $scope.buildParam.keyToColumnMap = {};
        }
    };

    $scope.changeKeyMap = function(key, col){
        console.log(key);
        console.log(col);
        $scope.buildParam.keyToColumnMap[key] = col;
    };

    $scope.build = function(buildParam, type){

        var templetIdList = $("input[type='checkbox'][name='compoundTempletId']:checked");
        if(templetIdList.length == 0){
            alert("Please select the metrics or dimension templet to be build");
            return;
        }else{
            for(var i=0; i< templetIdList.length; i++){
                var _checkBox = templetIdList.eq(i);
                var templetId = _checkBox.val();
                $scope.buildParam.templetIdList.push(templetId);
            }
        }

        if($scope.checkBuildParam(buildParam)){
            console.log(buildParam);
            if(confirm("build " + type + " ?")){
                dataMutualSrv.post(LINK_BUILD_COMPOUND + type, buildParam).then(function(data) {
                    if(data.status == "success"){
                        console.log(data.content);
                        alert("build " + type + " success");
                    }else{
                        alert("build " + type + " failed: " + data.message);
                        console.log(data.message);
                    }
                });
            }
        }
        
    };

    $scope.checkBuildParam = function(buildParam){
        if(!buildParam.categoryId){
            alert("check categoryId");
            return false;
        }
        if(!buildParam.tableId){
            alert("check tableId");
            return false;
        }
        if(!buildParam.templetIdList || buildParam.templetIdList.length == 0){
            alert("check selected compound templet List");
            return false;
        }

        for(var i = 0; i < $scope.keyList.length; i++){
            var key = $scope.keyList[i];
            if(!buildParam.keyToColumnMap || !buildParam.keyToColumnMap[key]){
                alert("check key to column mapping");
                return false;
            }
        }
        
        return true;
    };

    init();
}
