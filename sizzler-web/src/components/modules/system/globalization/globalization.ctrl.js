'use strict';



/**
 * dataSources
 * 
 */
angular
	.module('pt')
	.controller('globalizationManagerCtrl', ['$scope', '$location', '$document', 'dataMutualSrv', 'uiLoadingSrv',globalizationManagerCtrl]);

function globalizationManagerCtrl($scope, $location, $document, dataMutualSrv, uiLoadingSrv) {
    var body = $document.find('body').eq(0);
    
    var _filePath = "/l18n/";
    
    $scope.jsonList = [
    	{'fileName':'en_US.json', 'title':'英文'},
    	{'fileName':'zh_CN.json', 'title':'中文'},
    	{'fileName':'ja_JP.json', 'title':'日文'}
    	];
    $scope.editModel = false;
    $scope.nowJson = {};
    $scope.nowJsonObject = {};
    $scope.showUpdate = false;
    
    $scope.showJson = function(json){
    	//读取Json文件
    	uiLoadingSrv.createLoading(body);
    	
    	dataMutualSrv.get(_filePath + json.fileName).then(function(data) {
    		//加载编辑器内容
    		$('#editor').jsonEditor(data, 
		    { 
			 change: function(data) {  
			 	$scope.nowJsonObject = data;
			     //$('#json').html(JSON.stringify(data,null,"\t"));
				 //console.log(JSON.stringify(data,null,"\t"));
		      } ,
		      showAdd : false
			 });
			$scope.nowJsonObject = data; 
    		$scope.editModel = true;
    		$scope.nowJson = json;
    		uiLoadingSrv.removeLoading(body);
        });
    }
    
    $scope.backList = function(){
    	$scope.editModel = false;
    	$scope.nowJson = {};
    }
    
    $scope.exportFile = function(){
    	/**
			支持两个参数
			response.reportData		csv的数据
			response.reportName		csv的文件名称
		*/
		function downloadJson(response) {
			var data = response.data;
			var filename =  response.fileName;
		    if (data == null) return;
		    var link = document.createElement('a');
		    var ua 			= window.navigator.userAgent;
		    var isChrome 		= ua.match(/Chrome/i);
		    var isSafari 		= ua.match(/Safari/i);
		    var isFirefox_IE	= ua.match(/Firefox/i) || ua.match(/MSIE/i) 
		    if(typeof link.download != "undefined")
		    {
		            //csv = 'data:text/csv;charset=utf-8,\ufeff' + csv;
		        data = 'data:application/json;charset=utf-8,\ufeff' + data;
		        data = encodeURI(data);
		        //download attribute is supported
		    	//create a temp link and trigger click function on it
		        //this is not working on safari
		        var _body = $document.find('body');
		        var _a = $("<a href='#' download='"+filename+"'></a>").appendTo(_body);
		        _a[0].href = data;
		        _a[0].click();
		        _a.remove();
		        
		    }else{
		    	
		    }
		}
		var response = {
			"fileName":$scope.nowJson.fileName,
			"data":JSON.stringify($scope.nowJsonObject,null,"\t")
		}
		downloadJson(response);
    }
    
    $scope.signUpdate = function(){
    	uiLoadingSrv.createLoading(body);
    	var _data = JSON.stringify($scope.nowJsonObject);
    	dataMutualSrv.post(LINK_GLOBALIZATION_JSON_FILE_UPDATE, {"fileName":$scope.nowJson.fileName, "data":_data}).then(function(data) {
    		if(data.status=="success"){
    			alert("同步更新完成");	
    		}else{
    			alert("同步更新失败");
    		}
    		console.log(data);
    		uiLoadingSrv.removeLoading(body);
    		
        });
    }
    
    function init(){
    	var href = $location.absUrl();
    	if(href.indexOf(":8888") != -1 || href.indexOf("uitest.com") != -1){
    		$scope.showUpdate = true;
    	}
    }
    
    init();
    
    
};
