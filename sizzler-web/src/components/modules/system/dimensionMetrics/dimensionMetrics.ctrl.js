'use strict';



/**
 * dataSources
 * 
 */
angular
	.module('pt')
	.controller('dimensionMetricsManagerCtrl', ['$scope', '$document', 'FileUploader',dimensionMetricsManagerCtrl]);

function dimensionMetricsManagerCtrl($scope, $document, FileUploader) {
    var body = $document.find('body').eq(0);
    
    var categoryBegin = "CategoryBegin";
    var valueBegin = "ValueBegin";
    
    var categoryOpt = [
    		{title:"分类中文", code:"zh_name"},
    		{title:"分类英文", code:"en_name"}, 
    		{title:"分类日文", code:"jp_name"}, 
    		{title:"分类Code", code:"code"}, 
    		{title:"分类描述", code:"desc"}
    	];
    var valueOpt = [
    		{title:"指标维度中文", code:"zh_name"},
    		{title:"指标维度英文", code:"en_name"}, 
    		{title:"指标维度日文", code:"jp_name"}, 
    		{title:"指标维度Code", code:"code"}, 
    		{title:"指标维度描述", code:"desc"}
		];    
    $scope.dataModel = {
    	"category":{
    		"id":"",
    		"zh_name":"",
    		"en_name":"",
    		"jp_name":"",
    		"code":"",
    		"desc":"",
    		"isPackup":false,
    		"dimensionMetricsList":[]
    	},
    	"dimensionMetrics":{
    		"zh_name":"",
    		"en_name":"",
    		"jp_name":"",
    		"code":"",
    		"desc":""
    	}
    }
    
    $scope.setting = {
    	"dsId":"",
    	"dsCode":"",
    	"startCategoryId":"",
    	"startValueId":"",
    	"type":0,
    	"categorys":[],
    	"nowFile":null,
    	"categorySqlList":[],
    	"valueSqlList":[],
    	"categoryJsonList":[],
    	"valueJsonList":[]
    }
    
    $scope.addCategory = function(){
    	var category = angular.copy($scope.dataModel.category);
   		$scope.setting.categorys.push(category);
    }
    
    $scope.addDimensionMetrics = function(category){
    	var dimensionMetrics = angular.copy($scope.dataModel.dimensionMetrics);
    	category.dimensionMetricsList.push(dimensionMetrics);
    }
    
    $scope.packup = function(category){
    	if(category.isPackup){
    		category.isPackup = false;
    	}else{
    		category.isPackup = true;
    	}
    }
    
    $scope.delItem = function(key, list){
    	if(confirm("是否确认删除？")){
    		list.splice(list.indexOf(key), 1);	
    	}
    }
    
    $scope.importCsv = function(){
    	document.getElementById("csvFile").click();
    }
    
    $scope.createSql = function(){
    	$scope.setting.categorySqlList = [];
	    $scope.setting.valueSqlList = [];
    
    	var checkSetting = function(){
    		if($scope.setting.dsId == "" || $scope.setting.dsCode == "" || $scope.setting.startCategoryId == "" || $scope.setting.startValueId == ""){
    			return false;
    		}
    		return true;
    	}
    	
    	var getTableName = function(isCategory){
    		var tableName = "";
    		
    		if($scope.setting.type == 0){
    			//指标
    			tableName = "ptone_ds_metrics";
    		}else{
    			//维度
    			tableName = "ptone_ds_dimension";
    		}
    		if(isCategory){
    			tableName += "_category";
    		}
    		return tableName;
    	}
    	
    	var getInsertSql = function(value, category, isCategory){
    		var getValue = function(v, hasLast){
    			if(hasLast){
    				return "'"+v+"', ";
    			}else{
    				return "'"+v+"'";
    			}
    		}
    		
    		var getOrderNumber = function(id, isCategory){
    			var startId = isCategory?$scope.setting.startCategoryId:$scope.setting.startValueId;
    			return (id-startId)*10+10;
    		}
    	
    		var tableName = getTableName(isCategory);
    		var insertSql = "INSERT INTO `"+tableName+"` VALUES (";
    		if((isCategory && $scope.setting.type == 0) || (isCategory && $scope.setting.type == 1)){//指标维度分类
    			insertSql += getValue(value.id, true);//Id
    			insertSql += getValue(value.en_name, true);//name
    			insertSql += getValue(value.code, true);//code
    			insertSql += getValue(value.desc, true);//desc
    			insertSql += getValue($scope.setting.dsId, true);//ds_id
    			insertSql += getValue($scope.setting.dsCode, true);//ds_code
    			insertSql += getValue(getOrderNumber(value.id, isCategory), true);//order_number
    			insertSql += getValue(0, true);//is_delete
    			insertSql += getValue(getI18nCode(value.en_name, isCategory), false);//i18n_code
    		}else if(!isCategory && $scope.setting.type == 0){//指标
    			insertSql += getValue(value.id, true);//Id
    			insertSql += getValue(value.en_name, true);//name
    			insertSql += getValue(value.code, true);//code
    			insertSql += getValue(value.code, true);//queryCode
    			insertSql += getValue("STRING", true);//data_type
    			insertSql += getValue("", true);//data_format
    			insertSql += getValue("", true);//unit
    			insertSql += getValue(value.desc, true);//desc
    			insertSql += getValue("", true);//default_date_period
    			insertSql += getValue("", true);//available_date_period
    			insertSql += getValue($scope.setting.dsId, true);//ds_id
    			insertSql += getValue($scope.setting.dsCode, true);//ds_code
    			insertSql += getValue(category.id, true);//category_id
    			insertSql += getValue(category.code, true);//category_code
    			insertSql += getValue(0, true);//allow_segment
    			insertSql += getValue(getOrderNumber(value.id, isCategory), true);//order_number
    			insertSql += getValue(0, true);//is_delete
    			insertSql += getValue(getI18nCode(value.en_name, isCategory), false);//i18n_code
    		}else if(!isCategory && $scope.setting.type == 1){//维度
    			insertSql += getValue(value.id, true);//Id
    			insertSql += getValue(value.en_name, true);//name
    			insertSql += getValue(value.code, true);//code
    			insertSql += getValue(value.code, true);//queryCode
    			insertSql += getValue(value.desc, true);//desc
    			insertSql += getValue($scope.setting.dsId, true);//ds_id
    			insertSql += getValue($scope.setting.dsCode, true);//ds_code
    			insertSql += getValue(category.id, true);//category_id
    			insertSql += getValue(category.code, true);//category_code
    			insertSql += getValue(0, true);//allow_segment
    			insertSql += getValue(getOrderNumber(value.id, isCategory), true);//order_number
    			insertSql += getValue(0, true);//is_delete
    			insertSql += getValue(getI18nCode(value.en_name, isCategory), true);//i18n_code
    			insertSql += getValue("", true);//data_type
    			insertSql += getValue("", true);//data_format
    			insertSql += getValue(0, false);//has_filter_item
    		}
    		insertSql += ");";
    		return insertSql;
    	}
    	
    	if(!checkSetting()){
    		alert("全局设置不完整！");
    		return;
    	}
		//生成SQL语句 
		if($scope.setting.categorys.length == 0){
			alert("没有要生成的指标维度信息！");
			return;
		} 
		var startCategoryId = $scope.setting.startCategoryId;
		var startValueId = $scope.setting.startValueId;
		var categorys = $scope.setting.categorys; 
		var categorySqlList = [];
		var valueSqlList = [];
		for(var i=0; i<categorys.length; i++){
			var category = categorys[i];
			category.id = startCategoryId;
			categorySqlList.push(getInsertSql(category, category, true));
			for(var j=0; j<category.dimensionMetricsList.length; j++){
				var dimensionMetrics = category.dimensionMetricsList[j];
				dimensionMetrics.id = startValueId;
				valueSqlList.push(getInsertSql(dimensionMetrics, category, false));
				startValueId++;
			}
			startCategoryId++;
		}
		$scope.setting.categorySqlList = categorySqlList;
		$scope.setting.valueSqlList = valueSqlList;
		for(var i=0; i<categorySqlList.length; i++){
			console.log(categorySqlList[i]);
		}
		for(var i=0; i<valueSqlList.length; i++){
			console.log(valueSqlList[i]);
		}
    }
    
    $scope.createJSON = function(){
    
    	$scope.setting.categoryJsonList = [];
		
		$scope.setting.valueJsonList = [];
    
    	//生成SQL语句 
		if($scope.setting.dsCode == ""){
			alert("数据源CODE不能为空！");
			return;
		}
		
		var categoryEn = {};//英文
		var categoryZh = {};//中文
		var categoryJp = {};//日文
		
		var valueEn = {};
		var valueZh = {};
		var valueJp = {};
		
		
		var dsCode = $scope.setting.dsCode.toUpperCase()
		
		categoryEn[dsCode] = {};
		categoryZh[dsCode] = {};
		categoryJp[dsCode] = {};
		
		valueEn[dsCode] = {};
		valueZh[dsCode] = {};
		valueJp[dsCode] = {};
		
		var categorys = $scope.setting.categorys; 
		for(var i=0; i<categorys.length; i++){
			var category = categorys[i];
			
			console.log("create category json log::::::::::::::en:"+category.en_name+"|zh:"+category.zh_name+"|jp:"+category.jp_name);
			categoryEn[dsCode][cleanName(category.en_name)] = category.en_name;
			categoryZh[dsCode][cleanName(category.en_name)] = category.zh_name;
			categoryJp[dsCode][cleanName(category.en_name)] = category.jp_name;
			
			for(var j=0; j<category.dimensionMetricsList.length; j++){
				var dimensionMetrics = category.dimensionMetricsList[j];
				console.log("create value json log::::::::::::::en:"+dimensionMetrics.en_name+"|zh:"+dimensionMetrics.zh_name+"|jp:"+dimensionMetrics.jp_name);
				
				valueEn[dsCode][cleanName(dimensionMetrics.en_name)] = dimensionMetrics.en_name;
				valueZh[dsCode][cleanName(dimensionMetrics.en_name)] = dimensionMetrics.zh_name;
				valueJp[dsCode][cleanName(dimensionMetrics.en_name)] = dimensionMetrics.jp_name;
			}
		}
		
		$scope.setting.categoryJsonList.push(categoryEn);
		$scope.setting.categoryJsonList.push(categoryZh);
		$scope.setting.categoryJsonList.push(categoryJp);
		
		$scope.setting.valueJsonList.push(valueEn);
		$scope.setting.valueJsonList.push(valueZh);
		$scope.setting.valueJsonList.push(valueJp);
		console.log("create json");
    };
    
    //创建uploader
    var uploader = $scope.uploader = new FileUploader();
    
    $scope.uploader.onAfterAddingFile = function(fileItem) {
        $scope.setting.nowFile = fileItem.file;
        
        //读取file数据
        var reader = new FileReader(); 
        reader.readAsText(fileItem._file, "gbk");
        reader.onload=function(f){  
        	var categoryList = [];
	        //取出每行数据  
	        var resultDataArray = this.result.split("\n");
	        //取出每列数据
	        var beginReadCategory = false;
	        var beginCheckCategory = false;
	        var beginReadValue = false;
	        var beginCheckValue = false;
	        var categoryIndex = 0;
	        var isError = false;
	        var msg = "";
	        var nowCategory = null;
	        for(var i=0; i<resultDataArray.length; i++){
				var line = 	resultDataArray[i];
				
				if(isNewLine(line)){
					continue;
				}
				
				//验证是否是分类
	        	if((line.toUpperCase()).indexOf(categoryBegin.toUpperCase()) > -1 && !beginCheckCategory){
	        		//检查分类title格式
	        		beginCheckCategory = true;
	        		beginReadValue = false;
	        		categoryIndex ++;
	        	}else if(beginCheckCategory){
	        		//检查分类title格式
	        		if(checkLine(categoryOpt, line)){
						//开始读取分类数据	   
						beginReadCategory = true;	
	        		}else{
	        			//分类数据格式不正确
						isError = true;
						msg = "第"+categoryIndex+"个分类格式有误，请检查后重试！";
						break;
	        		}
	        		beginCheckCategory = false;
	        	}else if(beginReadCategory){
	        		
	        		nowCategory = readLine(categoryOpt, line, true);
	        		if(categoryIndex != 1){
	        			nowCategory.isPackup = true;
	        		}
	        		categoryList.push(nowCategory);
	        		//读取分类数据
	        		beginReadCategory = false;
	        	}
	        	
	        	//验证是否是数据
	        	if((line.toUpperCase()).indexOf(valueBegin.toUpperCase()) > -1 && !beginCheckValue){
	        		beginCheckValue = true;
	        	}else if(beginCheckValue){
	        		if(checkLine(valueOpt, line)){
	        			beginReadValue = true;
	        		}else{
	        			isError = true;
						msg = nowCategory.zh_name+"分类的数据格式有误，请检查后重试！";
						break;
	        		}
	        		beginCheckValue = false;
	        	}else if(beginReadValue){
	        		var _valueObj =  readLine(valueOpt, line, false);
	        		nowCategory.dimensionMetricsList.push(_valueObj);
	        	}
			}
			if(isError){
				showError(msg);
			}
			console.log(categoryList);
			$scope.setting.categorys = categoryList; 
	    }
        console.log("start reader file data");
    };
    
    $scope.cleanFile = function(){
    	if(confirm("是否确认删除？")){
	    	$scope.setting.categorys = [];
	    	$scope.setting.nowFile = null;
	    	$scope.setting.categorySqlList = [];
	    	$scope.setting.valueSqlList = [];
	    	$scope.setting.categoryJsonList = [];
	    	$scope.setting.valueJsonList = [];
	    	$scope.uploader.clearQueue();
			document.csvFileForm.reset();
		}
    }
    
    var showError = function(msg){
    	//清理相关数据
    	$scope.setting.categorys = [];
    	$scope.setting.nowFile = null;
    	$scope.uploader.clearQueue();
		document.csvFileForm.reset();
		alert(msg);
    }
    
    var cleanName = function(name){
		var _name = name.replace(new RegExp(/,|\.|-|\(|\)|:|\<|\>/, 'gm'), '');
		_name = _name.replace(new RegExp(/ /, 'gm'), '_');
		return _name.toUpperCase();
	}
    /**
    	通过name去获取i18ncode
    */
    var getI18nCode = function(name, isCategory){
    	
    	var _middleName = $scope.setting.dsCode.toUpperCase();
    	var _firstName = "";
    	var _lastName = cleanName(name);
    	if($scope.setting.type == 0){
			//指标
			_firstName = "METRICS";
		}else{
			//维度
			_firstName = "DIMENSION";
		}
		if(isCategory){
			_firstName += "_CATEGOTY";
		}
    	return _firstName+"."+_middleName+"."+_lastName;
    }
   
   var isNewLine = function(value){
   		var valueArray = value.split(",");
   		var isNull = true;
   		for(var i=0; i<valueArray.length; i++){
   			var val = strTrim(valueArray[i]);
   			if(val != "" && val != null && val.length > 0){
   				isNull = false;
   				break;
   			}
   		}
   		return isNull;
   }
   
   var strTrim = function(str){
   		if(str == null){return "";}
   		return str.replace(/(^\s*)|(\s*$)/g,'')
   }
   
   var strClean = function(str){
   		str = strTrim(str);
   		str = str.replace(new RegExp(/\n/, 'gm'), '');
   		str = str.replace(new RegExp(/\"/, 'gm'), '\\"');
   		return str;
   }
   
    var checkLine = function(titleOpt, value){
    	var valueArray = value.split(",");
    	if(titleOpt.length != valueArray.length){
    		return false;
    	}else{
    		var isSuccess = true;
    		for(var i=0; i<titleOpt.length; i++){
    			if(!angular.equals(titleOpt[i].title, strClean(valueArray[i]))){
    				isSuccess = false;
    				break;
    			}
    		}
    		return isSuccess;
    	}
    }
    
    var readLine = function(titleOpt, value, isCategory){
    	var valueArray = value.split(",");
    	var obj = null;
    	if(isCategory){
    		obj = angular.copy($scope.dataModel.category);
    	}else{
    		obj =  angular.copy($scope.dataModel.dimensionMetrics);
    	}
    	for(var i=0; i<titleOpt.length; i++){
    		var _opt = titleOpt[i];
    		var _val = valueArray[i];
    		obj[_opt.code] = strClean(_val);
    	}
    	return obj;
    }
    
};
