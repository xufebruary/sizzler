'use strict';

import {
	LINK_GET_USER_CONNECTION_CONFIG,
	LINK_SAVE_DB_CONNECTION,
	LINK_TEST_DB_CONNECTION,
	LINK_GET_AUTH_ACCOUNT_DETAIL,
	LINK_GET_ACCOUNT_WIDGET_COUNT,
	LINK_DEL_AUTH_ACCOUNT,
	LINK_DATA_SOURCE_VIEW,
	LINK_AUTHOR,
	LINK_PULL_REMOTE_DATA,
	LINK_DATA_SOURCE_EDIT_VIEW,
	LINK_DEL_SAVEDFILE,
	LINK_GET_SCHEMA,
	LINK_GET_AUTH_ACCOUNT,
	LINK_GET_SOURCE_WIDGET_COUNT,
	MSG_DB_LINK_FAILURE,
	DATA_SOURCE_WEB_SOCKET,
	i2s,
	uuid,
	getMyDsConfig,
	openWindow
} from 'components/modules/common/common';


/**
 * dataSources
 *
 */
angular
    .module('pt')
    .controller('dataSourcesCtrl', ['$scope', '$rootScope', '$translate', '$state', '$document', 'dataMutualSrv', 'websocket', 'uiLoadingSrv', 'siteEventAnalyticsSrv', 'publicDataSrv', dataSourcesCtrl]);

function dataSourcesCtrl($scope, $rootScope, $translate, $state, $document, dataMutualSrv, websocket, uiLoadingSrv, siteEventAnalyticsSrv, publicDataSrv) {
    var body = $document.find('body').eq(0);

    //数据源参数配置
    var dsCtrl = $scope.dsCtrl = {
        showDs: null,       //控制数据源各指令间显示与隐藏
        editDs: null,       //存储数据源编辑信息
        showEdit: false,
        serviceDsList: publicDataSrv.getPublicData('serviceDsList'),    //高级服务数据源列表

        //模块跳转
        goToMode: null,     //指定进入数据源模块(fileList, fileAdd)

        //事件处理
        isShowToolTip: null,  //是否显示成功提示框
        isShowList: false,     //是否显示列表页

        //各数据源数据存储
        dsDataTep: {}       //'googleanalysis': {'accountList': []}
                            //'googledrive': {'accountList': [], fileList:[], currentAccount:null, currentAccountCid:null, currentAddBtnType: null, query: {}}
                            // @param currentAddBtnType: 区分当前所选文件与当前账户的关系: owner(创建者), view(查看者);
                            // @param currentAddBtnType: 注: 如果选择的是所有文件列表(All)时,需要判断所有文件列表内,是否包含当前账户所创建的文件, 有则显示创建文件,否则显示创建账号
                            // @param query:{fileAllList: null, accountList: null}; 各个列表的请求状态(querying, success, failed, error),默认为null.
    };

	//修复facebook ad重新授权时，弹窗中的链接无法展开的bug
	$('body').on('click','.js_addAccountTipsForFBA_toggle',function(){$('.js_addAccountTipsForFBA').toggleClass('hide');});

    //选择数据源
    $scope.sltDs = function(ds, type){
        if(type == 'linkData'){
            ds['code'] = ds.dsCode;
            ds['id'] = ds.dsId;
        }

		/**
		 * isPlus:
		 * 0 - 内测
		 * 1 - 高级
		 * 2 - 开放
		 */
        if(ds.isPlus == '2') {
            dsCtrl.editDs = ds;
            dsCtrl.showEdit = false;
            $state.go('pt.dataSources.'+ds.code);
        } 

             //全站事件统计
        if(type == 'linkData'){
            siteEventAnalyticsSrv.createData({
                uid: $rootScope.userInfo.ptId,
                where: "data_source",
                what: "select_connected_data_sources",
                how: "click",
                value: ds.code
            });
        }
        else if(type == 'chooseDs'){
            siteEventAnalyticsSrv.createData({
                uid: $rootScope.userInfo.ptId,
                where: "data_source",
                what: "select_data_sources",
                how: "click",
                value: ds.code
            });
        }
    };

    //返回到数据源管理页
    $scope.backDS = function(){
        dsCtrl.showDs = null;
        dsCtrl.showEdit = false;
    };

    //打开编辑器
    $scope.openEdit = function(type){
        dsCtrl.showEdit = true;
        dsCtrl.showDs = null;

        $state.go('pt.dataSources.editor');
    };

    //判断是否包含指定数据源
    $scope.isHasDs = function(dsCode){
        var flag = false;
        for (var i=0; i<$scope.rootCommon.dsList.length; i++){
            if($scope.rootCommon.dsList[i].code == dsCode){
                flag = true;
                break;
            }
        }

        for(var j=0; j<$scope.dsCtrl.serviceDsList.length; j++){
            if($scope.dsCtrl.serviceDsList[j].code == dsCode){
                flag = true;
                break;
            }
        }

        return flag;
    };



    //数据源编辑器参数配置
    var dsEditor = $scope.dsEditor = {
        ds: null,           //编辑器数据来源类型
        dsType: 'add',      //编辑器类型 add || upload || edit
        dsContnet: [],      //编辑器总数据

        hot: null,          //当前编辑器实例化对象
        hotTable: {},       //当前编辑器总数据
        hotTableIndex: 0,   //当前编辑器表格索引
        hotWeight: 600,     //当前编辑器宽度
        hotHeight: 137,     //当前编辑器高度
        hotReadOnly: false, //是否允许编辑
        hotExtend: {        //当前编辑器扩展信息
            header: [],         //表头列表
            dataType: [],       //数据类型列表
            type: {
                metrics: [],    //指标
                dimension: [],  //维度
                ignore: []      //忽略
            }
        },
        //类型列表
        typeList: [
            {'code': 'metrics', 'name': $translate.instant('DATA_SOURCE.EDITOR.METRICS')},
            {'code': 'dimension', 'name': $translate.instant('DATA_SOURCE.EDITOR.DIMENSION')},
            {'code': 'ignore', 'name': $translate.instant('DATA_SOURCE.EDITOR.IGNORE')}
        ],
        //数据类型列表
        dataTypeList: [
            {'code': 'NUMBER', 'type': 'metrics', 'name': $translate.instant('COMMON.NUMBER')},
            {'code': 'CURRENCY', 'type': 'metrics', 'name': $translate.instant('COMMON.CURRENCY'),
                format: [
                    {'code': '¥##', 'type': 'CURRENCY', 'name': $translate.instant('COMMON.CURRENCY_FORMAT.JPY')},
                    {'code': '$##', 'type': 'CURRENCY', 'name': $translate.instant('COMMON.CURRENCY_FORMAT.USD')},
                    {'code': '¥###', 'type': 'CURRENCY', 'name': $translate.instant('COMMON.CURRENCY_FORMAT.RMB')},
                ]
            },
            {'code': 'PERCENT', 'type': 'metrics', 'name': $translate.instant('COMMON.PERCENT')},
            {'code': 'STRING', 'type': 'dimension', 'name': $translate.instant('COMMON.STRING')},
            {'code': 'DATE', 'type': 'dimension', 'name': $translate.instant('COMMON.DATE'),
                format: [
                    {'code': 'yyyyMMdd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.YYMMDD')},
                    {'code': 'yyyy/MM/dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.YY-MM-DD')},
                    {'code': 'yyyy-MM-dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.Y-M-D')},
                    {'code': 'yyyy.MM.dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.Y_M_D')},
                    {'code': 'yyyy年MM月dd日', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.YYYYMMDD')},
                    // {'code': 'MM/dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.MM-DD')},
                    // {'code': 'MM-dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M-D')},
                    // {'code': 'MM.dd', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M_D')},
                    // {'code': 'MM月dd日', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.MMDD')},
                    {'code': 'MM/dd/yyyy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.MM-DD-YYYY')},
                    {'code': 'MM-dd-yyyy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M-D-YY')},
                    {'code': 'MM.dd.yyyy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M_D_YY')},
                    {'code': 'MM/dd/yy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.MM-DD-YY')},
                    {'code': 'MM-dd-yy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M-D-Y')},
                    {'code': 'MM.dd.yy', 'type': 'DATE', 'name': $translate.instant('COMMON.DATE_FORMAT.M_D_Y')},
                ]
            },
            {'code': 'DATETIME', 'type': 'dimension', 'name': $translate.instant('COMMON.DATETIME'),
                format: [
                    {'code': 'yyyy/MM/dd HH:mm:ss', 'type': 'DATETIME', 'name': $translate.instant('COMMON.DATETIME_FORMAT.YYYY-MM-DD-H-M-S')},
                    {'code': 'yyyy.MM.dd HH:mm:ss', 'type': 'DATETIME', 'name': $translate.instant('COMMON.DATETIME_FORMAT.YYYY-M-D-H-M-S')},
                    {'code': 'yyyy-MM-dd HH:mm:ss', 'type': 'DATETIME', 'name': $translate.instant('COMMON.DATETIME_FORMAT.YYYY_M_D-H-M-S')},
                    {'code': 'yy/MM/dd HH:mm:ss', 'type': 'DATETIME', 'name': $translate.instant('COMMON.DATETIME_FORMAT.YY-MM-DD-H-M-S')},
                    {'code': 'yy.MM.dd HH:mm:ss', 'type': 'DATETIME', 'name': $translate.instant('COMMON.DATETIME_FORMAT.YY-M-D-H-M-S')},
                    {'code': 'yy-MM-dd HH:mm:ss', 'type': 'DATETIME', 'name': $translate.instant('COMMON.DATETIME_FORMAT.YY_M_D-H-M-S')},
                    {'code': 'MM/dd/yyyy HH:mm:ss', 'type': 'DATETIME', 'name': $translate.instant('COMMON.DATETIME_FORMAT.MM-DD-YYYY-H-M-S')},
                    {'code': 'MM.dd.yyyy HH:mm:ss', 'type': 'DATETIME', 'name': $translate.instant('COMMON.DATETIME_FORMAT.M-D-YYYY-H-M-S')},
                    {'code': 'MM-dd-yyyy HH:mm:ss', 'type': 'DATETIME', 'name': $translate.instant('COMMON.TIME_FORMAT.M_D_YYYY-H-M-S')},
                    {'code': 'yyyy年MM月dd日 HH時mm分ss秒', 'type': 'DATETIME', 'name': $translate.instant('COMMON.DATETIME_FORMAT.YYYYMMDDHMS')},
                ]
            },
            {'code': 'TIME', 'type': 'dimension', 'name': $translate.instant('COMMON.TIME'),
                format: [
                    {'code': 'HH:mm:ss', 'type': 'TIME', 'name': $translate.instant('COMMON.TIME_FORMAT.H-M-S')},
                    {'code': 'HH:mm', 'type': 'TIME', 'name': $translate.instant('COMMON.TIME_FORMAT.H-M')},
                    {'code': 'HH:mm a', 'type': 'TIME', 'name': $translate.instant('COMMON.TIME_FORMAT.H-M_A')},
                    {'code': 'HH:mm:ss a', 'type': 'TIME', 'name': $translate.instant('COMMON.TIME_FORMAT.H-MS_A')},
                    {'code': 'HH時mm分ss秒', 'type': 'TIME', 'name': $translate.instant('COMMON.TIME_FORMAT.HH-MM-SS')}
                ]
            },
            {'code': 'DURATION', 'type': 'dimension', 'name': $translate.instant('COMMON.DURATION'),
                format: [
                    {'code': '##s', 'type': 'DURATION', 'name': $translate.instant('COMMON.DURATION_FORMAT.SECONDS')},
                    {'code': '##m', 'type': 'DURATION', 'name': $translate.instant('COMMON.DURATION_FORMAT.MINUTES')},
                    {'code': '##h', 'type': 'DURATION', 'name': $translate.instant('COMMON.DURATION_FORMAT.HOURS')}
                ]
            },
            {'code': 'TIMESTAMP', 'type': 'dimension', 'name': $translate.instant('COMMON.TIMESTAMP')}
            // {'code': 'LOCATION_COUNTRY', 'type': 'dimension', 'name': $translate.instant('COMMON.LOCATION_COUNTRY')},
            // {'code': 'LOCATION_REGION', 'type': 'dimension', 'name': $translate.instant('COMMON.LOCATION_REGION')},
            // {'code': 'LOCATION_CITY', 'type': 'dimension', 'name': $translate.instant('COMMON.LOCATION_CITY')}
        ],
        //当前编辑器配置信息
        hotSet: {
            maxCols: 25,
            maxRows: 200,
            colWidths: 278,
            rowHeights: 35,
            minSpareRows: 0,
            readOnly: true,
            // columnSorting: {
            //     column: 0,
            //     sortOrder: true
            // },
            colHeaders: true,
            currentRowClassName: 'currentRow',
            currentColClassName: 'currentCol',
            // columns: [
            //     {renderer: customRenderer},
            //     {renderer: customRenderer},
            //     {renderer: customRenderer},
            //     {renderer: customRenderer},
            //     {renderer: customRenderer},
            //     {renderer: customRenderer}
            // ],
            // autoColumnSize: true,
            // manualColumnMove: true,
            // manualRowMove: true,
            rowHeaders: true,
            /*rowHeaders: function(index) {
                var textbox = '<input type="text" value="'+ dsEditor.hotExtend.header[index] +'" data-index="'+index+'" class="js_colHeadings"/>';
                return textbox;
            },*/
            // manualColumnResize: true,
            // manualRowResize: true,
            // contextMenu: ["row_above", "row_below", "col_left", "col_right", "remove_row", "remove_col", "---------", "undo", "redo"],
            contextMenu: {
                callback: function(key, options) {
                    if (key === 'skipRow') {
                        console.log('row: '+options.start.row);
                        // setTimeout(function() {
                        //     // timeout is used to make sure the menu collapsed before alert is shown
                        //     alert("This is a context menu with default and custom options mixed");
                        // }, 100);
                    } else if (key === 'skipCol') {
                        console.log('col: '+options.start.col);
                        // setTimeout(function() {
                        //     // timeout is used to make sure the menu collapsed before alert is shown
                        //     alert("This is a context menu with default and custom options mixed");
                        // }, 100);
                    }
                },
                items: {
                    "row_above": {
                        name: '在上方添加一行'
                    },
                    "row_below": {
                        name: '在下方添加一行'
                    },
                    "hsep": "---------",
                    "col_left": {
                        name: '在左侧添加一列'
                    },
                    "col_right": {
                        name: '在右侧添加一列'
                    },
                    // "make_read_only": {
                    //     name: '只读'
                    // },
                    // "alignment": {},
                    // "commentsAddEdit ": {},
                    // "commentsRemove  ": {},
                    // "borders ": {},
                    "hsep1": "---------",
                    "remove_col": {
                        name: '删除此列?'
                    },
                    "remove_row": {
                        name: '删除此行?'
                        // disabled: function() {
                        //     // if first row, disable this option
                        //     // return hot3.getSelected()[0] === 0
                        // }
                    },
                    "hsep2": "---------",
                    "undo": {
                        name: '撤销'
                    },
                    "redo": {
                        name: '重绘'
                    }
                    // "skipRow": {
                    //     name: '忽略此行？？'
                    // },
                    // "skipCol": {
                    //     name: '忽略此列？？'
                    // }
                }
            },
            init: function(){
                dsEditor.hot = this;
            },
            onAfterInit: function() {
                console.log('onAfterInit call');
            },
            afterGetColHeader: function(col, TH) {
                if(col<0)return;
                var instance = this,
                    input = bulidInput(dsEditor.hotExtend.header[col], col, instance),
                    menu = buildMenu(dsEditor.hotExtend.dataType[col], dsEditor.dataTypeList),
                    button = buildButton();

                addButtonMenuEvent(button, menu);
                Handsontable.Dom.addEvent(menu, 'click', function(event) {
                    if (event.target.nodeName == 'LI') {
                        setColumnType($scope, col, event.target.data['colType'], instance);
                    }
                });

                Handsontable.Dom.addEvent(input, 'blur', function(event) {
                    upHeadData($scope, col, instance, this.value);
                });
                if (TH.firstChild.lastChild.nodeName === 'BUTTON') {
                    TH.firstChild.removeChild(TH.firstChild.lastChild);
                }

                // TH.firstChild.appendChild(button);
                $(TH).find('.colHeader').empty().append(button, input);
                TH.style['white-space'] = 'normal';
            },
            afterRemoveCol: function(index, amount){
                console.log('index: '+index+'; amount: '+amount);

                dsEditor.hotExtend.header.splice(index,1);
                dsEditor.hotExtend.dataType.splice(index,1);
            },
            afterRemoveRow: function(index, amount){
                console.log('index: '+index+'; amount: '+amount)
            },
            afterCreateCol: function(index, amount){
                console.log('index: '+index+'; amount: '+amount);

                dsEditor.hotExtend.header.splice(index, 0, 'Column');
                dsEditor.hotExtend.dataType.splice(index, 0, 'numeric')
            },
            afterCreateRow: function(index, amount){
                console.log('index: '+index+'; amount: '+amount)
            },
            afterColumnMove: function(startColumn, endColumn){
                console.log('startColumn: '+startColumn+'; endColumn: '+endColumn);

                var tmpS = angular.copy(dsEditor.hotExtend.header[startColumn]);
                var tmpE = angular.copy(dsEditor.hotExtend.header[endColumn]);
                dsEditor.hotExtend.header.splice(startColumn, 1, tmpE);
                dsEditor.hotExtend.header.splice(endColumn, 1, tmpS);

                var tmp = angular.copy(dsEditor.hotExtend.dataType[startColumn]);
                dsEditor.hotExtend.dataType[startColumn] = angular.copy(dsEditor.hotExtend.dataType[endColumn]);
                dsEditor.hotExtend.dataType[endColumn] = tmp;
            },
            cells: function (row, col, prop) {
                var cellProperties = {};
                // if (row === 0) {
                //     cellProperties.renderer = firstRowRenderer;
                // }
                if($scope.dsCtrl.hotDataSource != 'excel'){
                    cellProperties.readOnly = true;
                } else {
                    cellProperties.readOnly = false;
                }
                return cellProperties;
            }
        },
        myCustomRenderer: function(hotInstance, td, row, col, prop, value, cellProperties) {
            var MAX_LENGTH = 2;

            if ((value + '').length > MAX_LENGTH) {
              value = value.substr(0, MAX_LENGTH) + '...';
              td.style.backgroundColor = 'yellow';
            } else {
              td.style.backgroundColor = 'red';
            }
            td.innerHTML = value;
        }
    };


    /**
     * dsDataFormat
     * 数据源数据格式化
     *
     * @param dsData: 后台返回的数据源总体数据
     * @param index: 当前编辑的Table索引
     * @param type: 操作类型(add || upload || edit || update)
     */
    $scope.dsDataFormat = function(dsData, index, type){
        /*
        data数据格式:
            {
                connectionId: null, //本地账户ID
                fileId: null,       //远端拉取文件ID
                folderId: null,     //远端拉取文件夹ID
                sourceId: uuid(),   //本地账户下文件ID
                operateType: null,  //操作类型 (save:第一次打开某个文件，进入到在线excel中，然后点击 save 按钮)
                                    //         (update:直接更新某个文件)
                                    //         (edit_save:点击某个已经保存的文件，然后进入到在线excel中，然后点击save按钮)
                name: null,         //数据文件name
                dsId: null,         //数据文件id
                remotePath: null,   //文件路径,
                remoteStatus: 1,    //远端文件是否存在0(异常)||1(存在)
                table: [
                    {
                        id: uuid(),         //table ID
                        name: null,         //table名称
                        code: id||name,     //GD-table ID || MYSQL-table名称
                        colSum: 0,          //table总列数
                        rowSum: 0,          //table总行数
                        ignoreRow: [0],     //忽略行
                        ignoreRowStart: 2,  //头N行忽略
                        ignoreRowEnd: 1,    //尾N行忽略
                        ignoreCol: [],      //忽略列
                        ignoreColStart: 0,  //头N列忽略
                        ignoreColEnd: 0,    //尾N列忽略
                        headType: 'row',    //表头为列(行)
                        headIndex: 0,       //选择某行为表头
                        headMode: 'assign', //表头默认方式为指定行(自定义-custom)
                        schema: [
                            {
                                name: null,     //列(行)名
                                id: uuid(),     //列(行)ID
                                code: id||name, //GD-列(行)ID || MYSQL-列(行)名
                                type: null,     //指标、维度
                                dataType: null, //数据类型
                                dataFormat: null,//数据格式
                                index: 0,       //列(行)顺序索引
                                isIgnore: 0,    //忽略 0-false : 1-true
                                isCustom: 0,    //列(行)名是否修改過。1:用户自定义 0：默认
                            }
                        ]
                        data: [] //包括所有数据(删除的除外)
                    }
                ],
                extend: {}
            }
        */

        //当为上传或者新增操作时，需要动态生成id
        if(type == 'add' || type == 'upload'){
            if(!dsData.sourceId){
                dsData.operateType = 'save';
                dsData.sourceId = uuid();
            }
            for (var i = dsData.table.length - 1; i >= 0; i--) {
                //如果有ID信息则跳过
                //if(dsData.table[i].id) break;
                //dsData.table[i].headMode = 'assign';
                //dsData.table[i].id = uuid();
                dsData.table[i].ignoreRow = [0];
                dsData.table[i].ignoreRowStart = 1;
                dsData.table[i].ignoreRowEnd = 0;

                //if( ['mysql','mysqlAmazonRds','postgre','redshift','auroraAmazonRds', 'bigquery'].indexOf(dsCtrl.editDs.code)>=0 ){
                if( $scope.dsCtrl.editDs.config.dataSource.dsJs.headMode_Custom ){
                    dsData.table[i].headMode = 'custom';
                    dsData.table[i].ignoreRowStart = 0;
                }

                //GD
                /*if(!dsData.table[i].code || dsData.table[i].code==''){
                    dsData.table[i].code = dsData.table[i].id;
                }*/
                for (var j = dsData.table[i].schema.length - 1; j >= 0; j--) {
                    //dsData.table[i].schema[j].id = uuid();
                    // dsData.table[i].schema[j].name =  dsData.table[i].schema[j].name ? dsData.table[i].schema[j].name.substr(0,30) : $translate.instant('DATA_SOURCE.EDITOR.COL')+i2s(j);//截取30
                    dsData.table[i].schema[j].name =  dsData.table[i].schema[j].name ? dsData.table[i].schema[j].name : $translate.instant('DATA_SOURCE.EDITOR.COL')+i2s(j);//截取30

                    //GD
                    /*if(!dsData.table[i].schema[j].code || dsData.table[i].schema[j].code==''){
                        dsData.table[i].schema[j].code = dsData.table[i].schema[j].id;
                    }*/

                    //指定数据类型(维度||指标)
                    for (var k = dsEditor.dataTypeList.length - 1; k >= 0; k--) {
                        if(dsData.table[i].schema[j].dataType == dsEditor.dataTypeList[k].code){
                            dsData.table[i].schema[j].type = dsEditor.dataTypeList[k].type;
                            break;
                        }
                    }
                }

                //当数据为null时，设置为空数组
                if( !dsData.table[i].data ){
                    dsData.table[i].data = [];
                }
            }
        } else if(type == 'edit' || type == 'update'){
            dsData.operateType = 'edit_save';
        }

        var header = [];    //表头
        var dataType = [];  //数据类型
        var type = {        //指标、维度、忽略列表
                metrics: [],
                dimension: [],
                ignore: []
            };
        var extend = [];


        $scope.dsEditor.dsContnet = dsData;
        $scope.dsEditor.hotTable = dsData.table[index];
        $scope.dsEditor.hotTableIndex = index;
        if(dsData.table.length > 0){
            for (var i = dsData.table[index].schema.length - 1; i >= 0; i--) {
                var id = dsData.table[index].schema[i].id;
                header.push(dsData.table[index].schema[i].name);
                dataType.push(dsData.table[index].schema[i].dataType);
                type[dsData.table[index].schema[i].type].push({
                    'id': id,
                    'name': dsData.table[index].schema[i].name
                });
            }

            $scope.dsEditor.hotHeight = dsData.table[index].data.length*22+144; //22为td高度，144为表头高度加边距
            $scope.dsEditor.hotWeight = parseInt(dsData.table[index].colSum)*140+28+6;    //140为td宽度，28为首列宽，6为边距
        } else {
            $scope.dsEditor.hotHeight = 0;
        }

        $scope.dsEditor.hotExtend = {
            header: header,
            dataType: dataType,
            type: type
        };


        console.log($scope.dsEditor);

    };




    /**
     * dsAuthUpdata
     * 数据源授权账户数据更新
     *
     * @param ds: 数据源信息
     * @param type: 操作类型(add || del || first)
     * @param target: 操作目标(account || file)
     */
    $scope.dsAuthUpdata = function(ds, type, target){
        var flag = false;
        var index = null;
        for (var i = 0; i < $scope.rootCommon.dsAuthList.length; i++) {
            if($scope.rootCommon.dsAuthList[i].dsCode == ds.code){
                flag = true;
                index = i;
                break;
            }
        }

        if(type == 'add'){
            if(flag){
                if(target == 'account'){
                    ++$scope.rootCommon.dsAuthList[index].accountNum;
                } else if(target == 'file'){
                    $scope.rootCommon.dsAuthList[index].nameNum = $scope.rootCommon.dsAuthList[index].nameNum ? ++$scope.rootCommon.dsAuthList[index].nameNum : 1;
                }
            } else {
                if( ds.code == 'upload'){
                    $scope.rootCommon.dsAuthList.push({
                        accountNum: 0,
                        nameNum: 1,
                        dsCode: ds.code,
                        dsName: ds.name,
                        dsId: ds.id,
                        dsConfig: ds.config,
                        dsOrderNumber: ds.orderNumber+'',
                        isPlus: 2
                    })
                } else {
                    $scope.rootCommon.dsAuthList.push({
                        accountNum: 1,
                        dsCode: ds.code,
                        dsName: ds.name,
                        dsId: ds.id,
                        dsConfig: ds.config,
                        dsOrderNumber: ds.orderNumber+'',
                        isPlus: 2
                    })
                }
            }
        } else if(type == 'del'){
            if(index !== null){
                if(target == 'account'){
                    --$scope.rootCommon.dsAuthList[index].accountNum;
                    if($scope.rootCommon.dsAuthList[index].accountNum == 0){
                        $scope.rootCommon.dsAuthList.splice(index, 1);
                    }

                    //删除账号时,需要去掉此账号下的文件数(直接重新请求已授权账号)
                    if(ds.config.dataSource.manageTpl.showFile || ds.config.dataSource.manageTpl.showTable){
                        //获取已授权数据源列表
                        dataMutualSrv.get(LINK_GET_AUTH_ACCOUNT_DETAIL+$scope.rootSpace.current.spaceId).then(function (data) {
                            if (data.status == 'success') {
                                for (var i = 0; i < data.content.length; i++) {
                                    data.content[i].dsConfig = getMyDsConfig(data.content[i].dsCode)
                                }

                                $scope.rootCommon.dsAuthList = data.content;
                            } else {
                                if (data.status == 'failed') {
                                    console.log('Get dsList Failed!')
                                } else if (data.status == 'error') {
                                    console.log('Get dsList Error: ');
                                    console.log(data.message)
                                }
                            }
                        });
                    }
                } else if(target == 'file'){
                    --$scope.rootCommon.dsAuthList[index].nameNum;

                    //upload数据源没有account
                    if($scope.rootCommon.dsAuthList[index].nameNum == 0 && ds.code == 'upload'){
                        $scope.rootCommon.dsAuthList.splice(index, 1);
                    }
                }

                //在删除操作时,先判是否和已存在数据账户与时间信息一样
                // if($scope.rootUser.profileSelected && !objectIsEmpty($scope.rootUser.profileSelected)) {
                //     var dsId = $scope.rootUser.profileSelected.dsId,
                //         accountName = $scope.rootUser.profileSelected.accountName,
                //         profileId = $scope.rootUser.profileSelected.prfileId,
                //         connectionId = $scope.rootUser.profileSelected.connectionId;

                //     var flag = false;
                //     for (var i = 0; i < $scope.rootCommon.dsAuthList.length; i++) {
                //         $scope.rootCommon.dsAuthList[i]
                //     };
                //     if(dsId == ds.dsId) {
                //         if(target == 'account' || target == 'file' && profileId){

                //         } else if(target == 'file'){

                //         }
                //     }
                // }
            }
        }
    };


    /**
     * getDsInfo
     * 获取当前数据源信息
     */
    $scope.getDsInfo = function(dsInfo){
        var ds;
        for (var i = 0; i < $scope.rootCommon.dsList.length; i++) {
            if( isNaN(-dsInfo) ){
                //dsCode
                if($scope.rootCommon.dsList[i].code == dsInfo){
                    ds = $scope.rootCommon.dsList[i];
                    break;
                }
            } else {
                //dsId
                if($scope.rootCommon.dsList[i].id == dsInfo){
                    ds = $scope.rootCommon.dsList[i];
                    break;
                }
            }
        }

        return ds;
    };



    /**
     * dsSrv
     * 数据源数据处理
     */
    $scope.dsSrv = {
        currentBinding: null,

        //账户-下拉选择
        selectAccountName: function(dsBinding, account) {
            if (account == 'all') {
                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = {
                    'name': $translate.instant('DATA_SOURCE.MYSQL.ALL_ACCOUNT')
                };
                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid = 'all';
            } else {
                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = account;
                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid = account.connectionId;
            }

            if (dsBinding.current.mod == 'fileList') {
                $scope.dsSrv.getFileList(dsBinding, 'ptoneRootFolderID::connection');
            }

            //更新文件列表
            $scope.dsSrv.getSingleFileList(account);
        },

        //账户-解绑提示
        removeAccountShow: function(dsBinding){
            $scope.dsSrv.currentBinding = dsBinding;
            //loading
            uiLoadingSrv.createLoading(angular.element('.data-source-content'));

            //获取当前账户下的widget数量
            dataMutualSrv.get(LINK_GET_ACCOUNT_WIDGET_COUNT + $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount.connectionId + "/" + $scope.dsCtrl.editDs.id).then(function(data) {
                if (data.status == 'success') {
                    dsBinding.accountWidgtCount = data.content || 0;
                    $scope.dsSrv.showTips(dsBinding, 'accountDelete');
                } else {
                    if (data.status == 'failed') {
                        console.log('Get accountsList Failed!');
                    } else if (data.status == 'error') {
                        console.log('Get accountsList Error: ');
                        console.log(data.message)
                    }
                }

                //loading
                uiLoadingSrv.removeLoading(angular.element('.data-source-content'));
            });
        },

        //账户-解绑
        removeAccount: function(dsBinding, cid){
            if(dsBinding == 'noBinding'){
                dsBinding = $scope.dsSrv.currentBinding;
            }
            //loading
            uiLoadingSrv.createLoading(angular.element('.ds-content'));
            if(cid){
                dataMutualSrv.post(LINK_DEL_AUTH_ACCOUNT + cid).then(function(data) {
                    if (data.status == 'success') {
                        for (var i = 0; i < $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList.length; i++) {
                            if($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList[i].connectionId == cid){
                                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList.splice(i, 1);
                            }
                        }
                        $scope.dsAuthUpdata($scope.dsCtrl.editDs, 'del', 'account');

                        for (var i = 0; i < $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList.length; i++) {
                            if($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList[i].connectionId == cid) {
                                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList.splice(i,1);
                            }
                        }

                        if($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList.length>0){
                            $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = dsBinding.accountList[0];
                            $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid = dsBinding.accountList[0].connectionId;
                            $scope.dsSrv.getSavedFileList(dsBinding);
                        } else {
                            dsBinding.current.mod = 'noData';
                            $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList = [];
                        }
                    } else {
                        if (data.status == 'failed') {} else if (data.status == 'error') {}
                    }

                    $scope.dsSrv.close(dsBinding);
                    //loading
                    uiLoadingSrv.removeLoading(angular.element('.ds-content'));
                })
            }
        },

        //账户-获取已授权账户列表
        getAccountList: function(dsBinding, type) {
            //loading
            uiLoadingSrv.createLoading(angular.element('.ds-content'));
            dataMutualSrv.get(LINK_DATA_SOURCE_VIEW +$scope.rootSpace.current.spaceId +'/'+ $scope.dsCtrl.editDs.id).then(function(data) {
                if (data.status == 'success') {
                    dsBinding.accountList = data.content;
                    if(dsBinding.accountList.length >0){
                        if(!$scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount || $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid !== $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount.connectionId){
                            if(dsBinding.accountList.length == 1){
                                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = dsBinding.accountList[0];
                                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid = dsBinding.accountList[0].connectionId;
                            } else {
                                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = {'name': $translate.instant('DATA_SOURCE.MYSQL.ALL_ACCOUNT')};
                                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid = 'all';
                            }
                        }
                        console.log("........."+$scope.dsCtrl.editDs.code);
                        if($scope.dsCtrl.editDs.code == 'facebookad'){
                        	//facebook需要对授权的过期时间做判断，如果是过期了需要显示提示
                        	for(var i=0; i<dsBinding.accountList.length; i++){
                        		var _tempAccount = dsBinding.accountList[i];
                        		var _tempConfig = angular.toJson(_tempAccount.config);
                        		var _expireTime = _tempConfig.expireTime;
                        		var _nowTime = new Date().getTime();
                        		if(_expireTime < _nowTime){
                        			//已过期，需要提示
                        			_tempAccount.showExpireTip = true;
                        			console.log("过期啦............."+_tempAccount.uid);
                        		}
                        		dsBinding.accountList[i] = _tempAccount;
                        	}
                        }

                        if (['googleanalysis', 'googleadwords', 'facebookad', 'ptengine', 'salesforce', 'doubleclick', 'doubleclickCompound', 'paypal'].indexOf($scope.dsCtrl.editDs.code)>=0) {
                            //非关系型数据库下显示只有账户列表层展示

                            dsBinding.current.mod = 'tableList';
                        } else {
                            if (type == 'add') {
                                //授权完成后，获取文件列表
                                $scope.dsSrv.getFileList(dsBinding, 'ptoneRootFolderID::connection');
                            } else {
                                dsBinding.current.mod = 'tableList';
                            }

                            $scope.dsSrv.getSavedFileList(dsBinding);
                        }
                    } else {
                        dsBinding.current.mod = 'noData';
                    }

                    $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList = dsBinding.accountList;
                    $scope.dsAuthUpdata($scope.dsCtrl.editDs, type, 'account');
                    dsBinding.pageLoad = true;
                } else if (data.status == 'failed') {
                    console.log('Get gaAccountList Failed!');
                } else if (data.status == 'error') {
                    console.log('Get gaAccountList Error: ');
                    console.log(data.message)
                }
                //loading
                uiLoadingSrv.removeLoading(angular.element('.ds-content'));
            })
        },

        //账户-授权
        authorization: function(dsBinding){
            var sign = uuid();
            var url = LINK_AUTHOR + $scope.dsCtrl.editDs.code + '?ptOneUserEmail=' + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign + '&spaceId=' + $scope.rootSpace.current.spaceId;

            //链接socket;
            var accreditSocket = new websocket;
            accreditSocket.initWebSocket(DATA_SOURCE_WEB_SOCKET + encodeURIComponent(encodeURIComponent($rootScope.userInfo.userEmail)) + '&sign=' + sign);
            //授权验证跳转
            openWindow(url);
            dsBinding.authorize = true;
            //监听授权socket返回值
            $scope.wsData = accreditSocket.colletion;
            accreditSocket.ws.onmessage = function(event) {
                $scope.$apply(function() {
                    $scope.wsData = event.data;
                });
            };
            var mywatch = $scope.$watch('wsData', function(newValue, oldValue, scope) {
				if (!newValue || newValue === oldValue) {
					return;
				}

				//注销当前监听事件
				mywatch();
				newValue = angular.fromJson(newValue);

                if (newValue.status == 'success') {
                    //关闭socket
                    accreditSocket.disconnect();
                    dsBinding.authorize = false;

                    console.log(newValue);

                    //判断是否重复授权
                    var flag = true;
                    for (var i = 0; i < $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList.length; i++) {
                        if($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList[i].name == newValue.content.account){
                            console.log('重复授权!');
                            flag = false;
                            break;
                        }
                    }
                    if(flag){
                        //更新账户列表
                        dsBinding.accountList.push(angular.copy(newValue.content.connectionInfo));
                        $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList = dsBinding.accountList;
                        $scope.dsAuthUpdata($scope.dsCtrl.editDs, 'add', 'account');
                        dsBinding.current.mod = 'tableList';

                        if(['bigquery','googledrive'].indexOf($scope.dsCtrl.editDs.code)>=0){
                            $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid = newValue.content.connectionId;
                            //$scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = {'name': newValue.content.account, 'connectionId': newValue.content.connectionId};
                            $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = newValue.content.connectionInfo;

                            //授权完成后，获取文件列表
                            $scope.dsSrv.getFileList(dsBinding, 'ptoneRootFolderID::connection');
                        }
                    }

                    //ga + gd
                    if(['googleanalysis', 'googleadwords', 'doubleclick', 'doubleclickCompound', 'salesforce', 'facebookad', 'paypal'].indexOf($scope.dsCtrl.editDs.code)>=0) {
                        dsBinding.accountAdd.accout = newValue.content.account;
                        dsBinding.accountAdd.connectionId = newValue.content.connectionId;

                        $scope.dsSrv.showTips(dsBinding, 'accountAdd');
                    }

                    //GTM
                    siteEventAnalyticsSrv.setGtmEvent('click_element','ds_ga','add_account');
                } else {
                    dsBinding.authorizeFailure = true;
                    dsBinding.authorize = false;
                }
            });
        },



        //文件[远端]-请求文件数据(跳转到编辑器)
        getFileData: function (dsBinding, file, cid, type, path) {
            var url = LINK_PULL_REMOTE_DATA+cid+'/'+dsBinding.current.directory+'/'+file.id;
            if(type == 'edit'){
                url = LINK_DATA_SOURCE_EDIT_VIEW+cid+'/'+file.sourceId;

                //编辑时，如果此文件状态有错，即直接弹出报错提示。
                if(file.remoteStatus != 1){
                    dsBinding.fileEdit.errorCode = file.remoteStatus;
                    $scope.dsSrv.showTips(dsBinding, 'editFileDataError');
                    return false;
                }
            }
            //loading
            uiLoadingSrv.createLoading('.pt-main');
            dataMutualSrv.get(url).then(function(data) {
                if (data.status == 'success') {
                    if(type == 'add'){
                        data.content.remotePath = path;
                    } else if(type == 'edit'){
                        $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList[dsBinding.fileEdit.index].remoteStatus = data.content.remoteStatus;
                        $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList[dsBinding.fileEdit.index].updateTime = data.content.updateTime;
                    }

                    if(data.content.remoteStatus != 1){
                        $scope.dsSrv.showTips(dsBinding, 'dataWarning');
                        uiLoadingSrv.removeLoading('.pt-main');

                        for (var i = 0; i < $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList.length; i++) {
                            if($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList[i].fileId == file.fileId){
                               $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList[i].remoteStatus = data.content.remoteStatus;
                            }
                        }
                        return;
                    }

                    $scope.dsEditor.hotType = type;
                    $scope.dsDataFormat(data.content, 0, $scope.dsEditor.hotType);

                    if(!$scope.dsEditor.hotTable){
                        //如果返回数据为0, 则弹出提示
                        $scope.dsSrv.showTips(dsBinding, 'getFileDataEmpty');
                        uiLoadingSrv.removeLoading('.pt-main');
                    } else {
                        $scope.openEdit();
                    }
                } else {
                    if (data.status == 'failed') {
                        console.log('Get accountsList Failed!');
                    } else if (data.status == 'error') {
                        console.log('Get accountsList Error: ');
                        console.log(data.message)
                    }

                    $scope.dsSrv.showTips(dsBinding, 'getFileDataError');
                    uiLoadingSrv.removeLoading('.pt-main');
                }
            });
        },

        //文件-添加(点击目录中的文件)
        addFile: function(dsBinding, file, isDirectory){
            if(!isDirectory){
                //拼合文件路径保存起来，以便编辑保存文件后发送到服务器
                var path = '';
                angular.element('.ds-file-breadcrumb').find('a').each(function(index){
                    path += $(this).html();
                    if(angular.element('.ds-file-breadcrumb').find('a').length-1 != index){
                        path += '@#*';
                    }
                });
                $scope.dsSrv.getFileData(dsBinding, file, $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid, 'add', path);
            }else{
                // dsBinding.fileListType.showTitleShare = false;
                $scope.dsSrv.getFileList(dsBinding, file.id, 'isDirectory', file.name);
            }
        },

        //文件-编辑
        editFile: function(dsBinding, file, index){
            dsBinding.fileEdit.file = file;
            dsBinding.fileEdit.index = index;
            localStorage.removeItem('widgetEditorLinkToEdit');
            $scope.dsSrv.getFileData(dsBinding, file, file.connectionId, 'edit');
        },

        //文件-解绑提示
        removeFileShow: function(dsBinding, file, index){
            $scope.dsSrv.currentBinding = dsBinding;

            //loading
            uiLoadingSrv.createLoading(angular.element('.ds-gd-content li:eq('+index+')'));

            //获取当前账户下的widget数量
            dataMutualSrv.get(LINK_GET_SOURCE_WIDGET_COUNT+file.sourceId).then(function(data) {
                if (data.status == 'success') {
                    dsBinding.fileDelete.fileWidgtCount = data.content || 0;
                    dsBinding.fileDelete.file = file;
                    dsBinding.fileDelete.index = index;
                    $scope.dsSrv.showTips(dsBinding, 'fileDelete', dsBinding);
                } else {
                    if (data.status == 'failed') {
                        console.log('Get accountsList Failed!');
                    } else if (data.status == 'error') {
                        console.log('Get accountsList Error: ');
                        console.log(data.message)
                    }
                }

                //loading
                uiLoadingSrv.removeLoading(angular.element('.ds-gd-content li:eq('+index+')'));
            });
        },

        //文件-解绑
        removeFile: function(fileSourceId, index){

            //loading
            uiLoadingSrv.createLoading(angular.element('.ds-gd-content'));

            dataMutualSrv.post(LINK_DEL_SAVEDFILE + fileSourceId).then(function(data) {
                if (data.status == 'success') {
                    $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList.splice(index, 1);

                    for (var i = 0; i < $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList.length; i++) {
                        if($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList[i].sourceId == fileSourceId){
                            $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList.splice(i, 1);
                        }
                    }

                    $scope.dsAuthUpdata($scope.dsCtrl.editDs, 'del', 'file');
                } else {
                    if (data.status == 'failed') {
                        console.log('Failed!')
                    } else if (data.status == 'error') {
                        console.log('Error: ');
                        console.log(data.message)
                    }
                }

                //loading
                uiLoadingSrv.removeLoading(angular.element('.ds-gd-content'));
                $scope.dsSrv.close($scope.dsSrv.currentBinding);
            })
        },




        //列表[远端]-请求账户下文档列表
        getFileList: function(dsBinding, id, type, name) {
            dsBinding.current.mod = 'fileList';

            //从widget编辑器跳转过来
            if($scope.rootTmpData.dataSources){
                for (var i = 0; i < dsBinding.accountList.length; i++) {
                    if(dsBinding.accountList[i].connectionId == $scope.rootTmpData.dataSources.connectionId){
                        $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = dsBinding.accountList[i];
                        $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid = dsBinding.accountList[i].connectionId;
                        $scope.rootTmpData.dataSources = null;
                        break;
                    }
                }
            }

            //当查看All列表时,链接新文件选择当前账户下关联的第一个账户
            if($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid == 'all'){

                for (var i = 0; i < dsBinding.accountList.length; i++) {
                    if ($rootScope.userInfo.ptId == dsBinding.accountList[i].uid) {

                        $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = dsBinding.accountList[i];
                        $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid = dsBinding.accountList[i].connectionId;
                        break;
                    }
                }

                //$scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = dsBinding.accountList[0];
                //$scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid = dsBinding.accountList[0].connectionId;

                //更新文件列表
                $scope.dsSrv.getSingleFileList(dsBinding.accountList[0]);
            }

            //loading
            uiLoadingSrv.createLoading(angular.element('.ds-file-bd'));

            var url =  LINK_GET_SCHEMA + $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid + "/" + id + '?refresh=true';
            dataMutualSrv.get(url).then(function(data) {
                if (data.status == 'success') {
                    $(window).scrollTop(0);
                    //目录刷新成功提示
                    dsBinding.accountDataRefresh.tipsInfo = $translate.instant('DATA_SOURCE.MYSQL.ACCOUNT_REFRESH_SUCCESS');
                    dsBinding.accountDataRefresh.btnClass = 'pt-btn-success';
                    dsBinding.accountDataRefresh.btnText = $translate.instant('COMMON.OK');

                    dsBinding.accountData = dsBinding.accountDataCopy = angular.fromJson(data.content)[0];

                    if(id == 'ptoneRootFolderID::connection'){
                        $('.ds-file-breadcrumb').html('<a id="'+id+'" class="guide">'+$scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount.name+'</a>');
                    }

                    if(type && type == 'isDirectory'){
                        $('.ds-file-breadcrumb').append('<svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-chevron-small-right"></use></svg><a id="'+id+'" class="guide">'+name+'</a>');
                    }

                    dsBinding.current.directory = id;
                } else {
                    //目录刷新失败提示
                    dsBinding.accountDataRefresh.tipsInfo = $translate.instant('DATA_SOURCE.MYSQL.ACCOUNT_REFRESH_FAILURE');
                    dsBinding.accountDataRefresh.btnClass = 'pt-btn-default';
                    dsBinding.accountDataRefresh.btnText = $translate.instant('DATA_SOURCE.MYSQL.REFRESH_AGAIN');

                    if (data.status == 'failed') {
                        console.log('Get accountsList Failed!');
                    } else if (data.status == 'error') {
                        console.log('Get accountsList Error: ');
                        console.log(data.message)
                    }

                    $scope.dsSrv.showTips(dsBinding, 'getFileList');

                    //清空前端页面数据
                    $('.ds-file-breadcrumb').html('');
                    dsBinding.accountDataCopy.child = [];
                }

                if(type && type ==  'refresh'){
                    $scope.dsSrv.showTips(dsBinding, 'accountRefresh');
                }

                //loading
                uiLoadingSrv.removeLoading(angular.element('.ds-file-bd'));
            });
        },

        //列表-当前数据源下已存所有文档
        getSavedFileList: function(dsBinding) {
            //loading
            uiLoadingSrv.createLoading(angular.element('.ds-gd-content'));
            //列表请求状态
            $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].query.fileAllList = 'querying';

            dataMutualSrv.get(LINK_GET_AUTH_ACCOUNT + $scope.rootSpace.current.spaceId + '/' + $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid + "/" + $scope.dsCtrl.editDs.id).then(function(data) {
                if (data.status != 'success') {
                    if (data.status == 'failed') {
                        console.log('Get SavedFileList Failed!');
                    } else if (data.status == 'error') {
                        console.log('Get SavedFileList Error: ');
                        console.log(data.message)
                    }
                } else {
                    $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList = data.content;
                    $scope.dsSrv.getSingleFileList($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount);

                    //默认进来
                    if ($scope.dsCtrl.goToMode && $scope.dsCtrl.goToMode == 'fileAdd') {
                        $scope.dsSrv.getFileList(dsBinding, 'ptoneRootFolderID::connection');
                        $scope.dsCtrl.goToMode = null;
                    }

                    console.log('已存文件列表： ')
                    console.log($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList)
                }
                //列表请求状态
                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].query.fileAllList = data.status;
                //loading
                uiLoadingSrv.removeLoading(angular.element('.ds-gd-content'));
            });
        },

        //列表-某个账户下的已存文档
        getSingleFileList: function(account) {
            if ($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid == 'all') {
                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList = angular.copy($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList);

                //区分当前所选文件与当前账户的关系: owner(创建者), view(查看者);
                // 注: 如果查看的是所有文件列表(All)时,需要判断所有账户列表内,是否存在当前账户, 有则显示创建文件,否则显示创建账号
                var flag = false;
                for (var i = 0; i < $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList.length; i++) {
                    if ($rootScope.userInfo.ptId == $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList[i].uid) {
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAddBtnType = 'owner';
                } else {
                    $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAddBtnType = 'view';
                }
            } else {
                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList = [];
                for (var i = 0; i < $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList.length; i++) {
                    if ($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid == $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList[i].connectionId) {
                        $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList.push($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList[i])
                    }
                }

                //区分当前所选文件与当前账户的关系: owner(创建者), view(查看者);
                if($scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount.uid != $rootScope.userInfo.ptId){
                    $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAddBtnType = 'view';
                } else {
                    $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAddBtnType = 'owner';
                }
            }
        },



        //表单-显示(关系型数据库连接)
        connectionShow: function(dsBinding, type, from){
            dsBinding.current.mod = 'connectionPost';

            dsBinding.connection.from = from;
            dsBinding.connection.operateType = type;
            dsBinding.connection.show = true;

            if(type == 'edit'){
                //loading
                uiLoadingSrv.createLoading('.pt-main');

                dataMutualSrv.get(LINK_GET_USER_CONNECTION_CONFIG+$scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid).then(function(data){
                    if (data.status == 'success') {
                        var config = angular.fromJson(data.content);

                        dsBinding.connection.modHostName = config.host;
                        dsBinding.connection.modPort = config.port;
                        dsBinding.connection.modUserName = config.user;
                        dsBinding.connection.modPassword = config.password;
                        dsBinding.connection.modDatabase = config.dataBaseName;
                        dsBinding.connection.modConnectionName = config.connectionName;
                        dsBinding.connection.sshModSwitch = config.ssh == 1 ? true : false;
                        dsBinding.connection.sshModHostName = config.sshHost;
                        dsBinding.connection.sshModPort = config.sshPort ? config.sshPort : dsBinding.connection.configSshPort;
                        dsBinding.connection.sshModUserName = config.sshUser;
                        dsBinding.connection.sshModAuthMethod = config.sshAuthMethod ? config.sshAuthMethod : 'password';
                        dsBinding.connection.sshModPassword = config.sshPassword;
                        dsBinding.connection.sshModPrivateKey = config.sshPrivateKey;
                        dsBinding.connection.sshModPassphrase = config.sshPassphrase;
                        dsBinding.connection.modAccessKeyId = config.accessKeyId;
                        dsBinding.connection.modSecretAccessKey = config.secretAccessKey;
                    } else {
                        if (data.status == 'failed') {
                            console.log('Get accountsList Failed!');
                        } else if (data.status == 'error') {
                            console.log('Get accountsList Error: ');
                            console.log(data.message)
                        }
                    }

                    //loading
                    uiLoadingSrv.removeLoading('.pt-main');
                })
            } else {
                dsBinding.connection.modHostName = null;
                dsBinding.connection.modPort = dsBinding.connection.configPort;
                dsBinding.connection.modUserName = null;
                dsBinding.connection.modPassword = null;
                dsBinding.connection.modDatabase = null;
                dsBinding.connection.modConnectionName = null;
                dsBinding.connection.modAccessKeyId = null;
                dsBinding.connection.modSecretAccessKey = null;

                dsBinding.connection.sshModSwitch = false;
                dsBinding.connection.sshModHostName = null;
                dsBinding.connection.sshModPort = dsBinding.connection.configSshPort;
                dsBinding.connection.sshModUserName = null;
                dsBinding.connection.sshModAuthMethod = 'password';
                dsBinding.connection.sshModPassword = null;
                dsBinding.connection.sshModPrivateKey = null;
                dsBinding.connection.sshModPassphrase = null;
            }

            jQuery('.js_connection_ipt').removeClass('error').next('.text-danger').addClass('hide');
        },

        //表单-隐藏(关系型数据库连接)
        connectionHide: function(dsBinding){
            dsBinding.connection.show = false;
            dsBinding.current.mod = dsBinding.connection.from;
        },

        //表单-失去焦点数据校验(关系型数据库连接)
        blurVerify: function(e){
            if(angular.element(e.target).val() == ''){
                angular.element(e.target).addClass('error').next('.text-danger').removeClass('hide');
            } else {
                angular.element(e.target).removeClass('error').next('.text-danger').addClass('hide');
            }
        },

        //表单-数据校验(关系型数据库连接)
        dataVerify: function(){
            var flag = true;
            jQuery('.js_connection_ipt').each(function(){
                if(jQuery(this).val() == ''){
                    jQuery(this).addClass('error').next('.text-danger').removeClass('hide');
                    flag = false;
                } else {
                    jQuery(this).removeClass('error').next('.text-danger').addClass('hide');
                }
            })

            return flag;
        },

        //表单-数据发送(关系型数据库连接、S3)
        connectionPost: function(dsBinding, type) {
            if(dsBinding == 'noBinding'){
                dsBinding = $scope.dsSrv.currentBinding;
                $scope.dsSrv.close(dsBinding);
            } else {
                $scope.dsSrv.currentBinding = dsBinding;
            }

            if ($scope.dsSrv.dataVerify()) {
                if(type == 'edit'){
                    //loading
                    uiLoadingSrv.createLoading(angular.element('.add-database-bd'));

                    //获取当前账户下的widget数量
                    dataMutualSrv.get(LINK_GET_ACCOUNT_WIDGET_COUNT + $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount.connectionId + "/" + $scope.dsCtrl.editDs.id).then(function(data) {
                        if (data.status == 'success') {
                            dsBinding.accountWidgtCount = data.content || 0;
                            $scope.dsSrv.showTips(dsBinding, 'connectEdit');
                        } else {
                            if (data.status == 'failed') {
                                console.log('Get accountsList Failed!');
                            } else if (data.status == 'error') {
                                console.log('Get accountsList Error: ');
                                console.log(data.message)
                            }
                        }

                        //loading
                        uiLoadingSrv.removeLoading(angular.element('.add-database-bd'));
                    });

                    return false;
                }

                var url = LINK_SAVE_DB_CONNECTION;
                if (type != 'test') {
                    dsBinding.connection.operateType = type;
                } else {
                    url = LINK_TEST_DB_CONNECTION;
                }

                //loading
                uiLoadingSrv.createLoading(angular.element('.add-database-bd'));

                var sendData = {
                    spaceId: $scope.rootSpace.current.spaceId,
                    dataBaseType: $scope.dsCtrl.editDs.code,
                    dsCode: $scope.dsCtrl.editDs.code,
                    dsId: $scope.dsCtrl.editDs.id,
                    host: dsBinding.connection.modHostName,
                    port: dsBinding.connection.modPort,
                    user: dsBinding.connection.modUserName,
                    password: dsBinding.connection.modPassword,
                    dataBaseName: dsBinding.connection.modDatabase,
                    connectionName: dsBinding.connection.modConnectionName,
                    operateType: dsBinding.connection.operateType,
                    accessKeyId: dsBinding.connection.modAccessKeyId,
                    secretAccessKey: dsBinding.connection.modSecretAccessKey
                }
                if(dsBinding.connection.sshModSwitch){
                    sendData['ssh'] = dsBinding.connection.sshModSwitch ? 1 : 0;
                    sendData['sshHost'] = dsBinding.connection.sshModHostName;
                    sendData['sshPort'] = dsBinding.connection.sshModPort;
                    sendData['sshUser'] = dsBinding.connection.sshModUserName;
                    sendData['sshAuthMethod'] = dsBinding.connection.sshModAuthMethod;
                    sendData['sshPassword'] = dsBinding.connection.sshModPassword;
                    sendData['sshPrivateKey'] = dsBinding.connection.sshModPrivateKey;
                    sendData['sshPassphrase'] = dsBinding.connection.sshModPassphrase;
                }
                if ( ['edit', 'edit_save'].indexOf(dsBinding.connection.operateType)>=0 ) {
                    sendData['connectionId'] = $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid;
                }

                dataMutualSrv.post(url, sendData).then(function(data) {
                    if (data.status == 'success') {
                        /*
                        content: {
                            config: "{"connectionName":"ptone4","dataBaseType":"mysql","host":"192.168.1.2","operateType":"save","password":"ptone","port":"3306","tableName":"","user":"ptone"}"
                            connectionId: "2b7458d9-1677-40e3-a8e9-a1294ecce910"
                            dsCode: "mysql"
                            dsId: null
                            id: null
                            name: "ptone4"
                            status: "1"
                            uid: "1552"
                            userName: "xxxx"
                            updateTime: 1452743584513
                        }
                        */
                        console.log(data)

                        if (type == 'test') {
                            $scope.dsSrv.showTips(dsBinding, 'connectSuccess')
                        } else if (type == 'edit_save') {
                            $scope.dsSrv.showTips(dsBinding, 'connectEditSuccess')

                            //前端数据(Account)更新
                            for (var i = 0; i < dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList.length; i++) {
                                if(data.content.connectionId == dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList[i].connectionId){
                                    dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].accountList[i] = data.content;
                                    break;
                                }
                            }
                            //
                            if(data.content.connectionId == $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid){
                                $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = data.content;
                            }
                            //路径修改
                            for (var i = 0; i < dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList.length; i++) {
                                if(data.content.connectionId == dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList[i].connectionId){
                                    var remotePath = dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList[i].remotePath;
                                    remotePath = remotePath.replace(remotePath.split('@#*')[0], data.content.name)
                                    dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileAllList[i].remotePath = remotePath;
                                }
                            }
                        } else {
                            $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid = data.content.connectionId;
                            $scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount = data.content;

                            $scope.dsSrv.getAccountList(dsBinding, 'add'); //更新账户列表
                        }
                    } else {
                        if (data.status == 'failed') {
                            console.log(data.message);

                            var tipsCode = '';
                            if (data.message.indexOf(' | ') > 0) {
                                tipsCode = data.message.split(' | ')[0];
                            }

                            // if (tipsCode == 'CONNECTION_NAME_EXISTS') {
                            //     var ipt = angular.element('.add-database-bd').find('input[name="connection name"]');
                            //     ipt.addClass('error');
                            // } else {
                                if(dsBinding.dsCode == 's3'){
                                    dsBinding.connection.connectFailure = $translate.instant('DATA_SOURCE.S3.' + tipsCode);
                                }else{
                                    if (tipsCode != 'MSG_DB_LINK_FAILURE' && tipsCode != 'MSG_FAILED') {
                                        dsBinding.connection.connectFailure = $translate.instant('DATA_SOURCE.MYSQL.' + tipsCode);
                                    } else {
                                        dsBinding.connection.connectFailure = $translate.instant('DATA_SOURCE.MYSQL.MSG_FAILED_1') + dsBinding.connection.modHostName + $translate.instant('DATA_SOURCE.MYSQL.MSG_FAILED_2');
                                    }
                                }
                                $scope.dsSrv.showTips(dsBinding, 'connectFailure')
                            // }
                        } else if (data.status == 'error') {
                            console.log('Connect Error: ');
                            console.log(data.message)
                        }
                    }

                    //loading
                    uiLoadingSrv.removeLoading(angular.element('.add-database-bd'));
                })
            }
        },

        //表单-回车事件绑定(关系型数据库连接)
        panelCopyKeyup: function(dsBinding,e){
            var keycode = window.event?e.keyCode:e.which;
            if(keycode==13){
                $scope.dsSrv.connectionPost(dsBinding, dsBinding.connection.operateType);
            }
        },

        //表单-SSH开关(关系型数据库连接)
        sshSwitch: function(dsBinding){

        },

        //提示-显示
        showTips: function(dsBinding, type){
            var options = {
                title: null,
                info: null,
                btnLeftText: $translate.instant('COMMON.CANCEL'),
                btnRightText: $translate.instant('COMMON.OK'),
                btnLeftClass: 'pt-btn-default',
                btnRightClass: 'pt-btn-danger',
                btnLeftEvent: 'close()',
                btnRightEvent: 'close()',
                closeEvent: 'close()',
                btnLeftHide: 'false',
                btnRightHide: 'false',
                hdHide: 'false'
            };

            // body.addClass('modal-open');
            switch(type){
                //表单连接测试成功
                case "connectSuccess":
                    if(dsBinding.dsCode == 's3'){
                        options.title = $translate.instant('DATA_SOURCE.S3.MSG_SUCCESS_TITLE');
                        options.info = $translate.instant('DATA_SOURCE.S3.MSG_SUCCESS');
                    }else{
                        options.title = $translate.instant('DATA_SOURCE.MYSQL.MSG_SUCCESS_TITLE');
                        options.info = $translate.instant('DATA_SOURCE.MYSQL.MSG_SUCCESS');
                    }
                    options.btnRightClass = 'pt-btn-success';
                    options.btnLeftHide = 'true';
                    break;
                //表单连接失败
                case "connectFailure":
                    if(dsBinding.dsCode == 's3'){
                        options.title = $translate.instant('DATA_SOURCE.S3.MSG_FAILED_TITLE');
                    }else{
                        options.title = $translate.instant('DATA_SOURCE.MYSQL.MSG_FAILED_TITLE');
                    }
                    options.info = dsBinding.connection.connectFailure;
                    options.btnRightClass = 'pt-btn-success';
                    options.btnLeftHide = 'true';
                    break;
                //表单修改
                case "connectEdit":
                    if(dsBinding.dsCode == 's3'){
                        options.title = $translate.instant('DATA_SOURCE.S3.EDIT_TIPS_TITLE');
                        options.info = $translate.instant('DATA_SOURCE.S3.EDIT_TIPS_1')+dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList.length+$translate.instant('DATA_SOURCE.S3.EDIT_TIPS_2')+dsBinding.accountWidgtCount+$translate.instant('DATA_SOURCE.S3.EDIT_TIPS_3');
                    }else{
                        options.title = $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_TITLE');
                        options.info = $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_0') + dsBinding.dsName + $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_1')+dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].fileList.length+$translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_2')+dsBinding.accountWidgtCount+$translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_3');
                    }
                    options.btnRightEvent = 'dsSrv.connectionPost(\"noBinding\", \"edit_save\")';
                    options.btnRightClass = 'pt-btn-success';
                    break;
                //表单修改成功
                case "connectEditSuccess":
                    if(dsBinding.dsCode == 's3'){
                        options.info = $translate.instant('DATA_SOURCE.S3.EDIT_TIPS_SUCCESS');
                    }else{
                        options.info = $translate.instant('DATA_SOURCE.MYSQL.EDIT_TIPS_SUCCESS');
                    }
                    options.btnRightText = $translate.instant('COMMON.OK');
                    options.btnRightClass = 'pt-btn-success';
                    options.btnRightEvent = 'close(\"tableList\")';
                    options.btnLeftHide = 'true';
                    options.hdHide = 'true';
                    break;
                //目录刷新
                case "accountRefresh":
                    options.title = '目录刷新';
                    options.info = dsBinding.accountDataRefresh.tipsInfo;
                    options.btnRightClass = dsBinding.accountDataRefresh.btnClass;
                    options.btnRightText = dsBinding.accountDataRefresh.btnText;
                    options.btnLeftHide = 'true';
                    options.hdHide = 'true';
                    break;
                //获取文件目录失败
                case "getFileList":
                    options.title = '远端目录列表拉取';
                    options.info = $translate.instant('DATA_SOURCE.MYSQL.PULL_DATA_ERROR', $rootScope.productConfigs);
                    options.btnLeftHide = 'true';
                    options.hdHide = 'true';
                    break;
                //文件解绑
                case "fileDelete":
                    options.title = '确认解绑';
                    options.info = dsBinding.fileDelete.fileWidgtCount+$translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_FILE_1')+dsBinding.fileDelete.file.name+$translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_FILE_2');
                    options.btnRightText = $translate.instant('DATA_SOURCE.MANAGEMENT.BTN_REMOVE');
                    options.btnRightEvent = 'dsSrv.removeFile(\"'+dsBinding.fileDelete.file.sourceId+'\",\"'+dsBinding.fileDelete.index+'\")';
                    options.hdHide = 'true';
                    break;
                //账户添加
                case "accountAdd":
                    options.info = $translate.instant('DATA_SOURCE.ADWORDS.SAVE_SUCCESS_TIP_1')+dsBinding.accountAdd.accout+$translate.instant('DATA_SOURCE.ADWORDS.SAVE_SUCCESS_TIP_2');
                    options.btnLeftText = $translate.instant('DATA_SOURCE.ADWORDS.BTN_ADD_ACCOUNT');
                    options.btnRightText = $translate.instant('DATA_SOURCE.ADWORDS.BTN_CREATE_WIDGET');
                    options.btnRightClass = 'pt-btn-success';
                    options.btnRightEvent = '$state.go("pt.dashboard")';
                    options.hdHide = 'true';
                    break;
                //账户解绑
                case "accountDelete":
                    options.info = dsBinding.accountWidgtCount+$translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_ACCOUNT_1')+$scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccount.name+$translate.instant('DATA_SOURCE.MANAGEMENT.REMOVE_ACCOUNT_2');
                    options.btnRightText = $translate.instant('DATA_SOURCE.MANAGEMENT.BTN_REMOVE');
                    options.btnRightEvent = 'dsSrv.removeAccount(\"noBinding\", \"'+$scope.dsCtrl.dsDataTep[$scope.dsCtrl.editDs.code].currentAccountCid+'\")';
                    options.hdHide = 'true';
                    break;
                //获取文件内容失败
                case "getFileDataError":
                    options.title = '远端拉取数据';
                    options.info = $translate.instant('DATA_SOURCE.MYSQL.PULL_DATA_ERROR', $rootScope.productConfigs);
                    options.btnRightClass = 'pt-btn-default';
                    options.hdHide = 'true';
                    break;
                //文件编辑时、文件数据有错提示
                case "editFileDataError":
                    options.info = $translate.instant('DATA_SOURCE.MYSQL.'+dsBinding.fileEdit.errorCode);
                    options.btnRightClass = 'pt-btn-default';
                    options.btnLeftHide = 'true';
                    options.hdHide = 'true';
                    break;
                //当前选择文件内容为空
                case "getFileDataEmpty":
                    options.title = '远端拉取数据';
                    options.info = $translate.instant('DATA_SOURCE.MYSQL.DATA_EMPTY');
                    options.btnRightClass = 'pt-btn-default';
                    options.hdHide = 'true';
                    break;
                //刷新或编辑file时，远端数据发生变化
                case "dataWarning":
                    options.title = '远端数据发生变化';
                    options.info = $translate.instant('DATA_SOURCE.MYSQL.DATA_CHANGE_TIP');
                    options.btnRightClass = 'pt-btn-success';
                    options.btnLeftHide = 'true';
                    options.hdHide = 'true';
                    break;
            }

            dsBinding.tips.show = true;
            dsBinding.tips.options = options;
        },
        //提示-关闭
        close: function(dsBinding) {
            body.removeClass('modal-open');
            dsBinding.tips.show = false;
        },


        //数据源-数据初始化
        dataInit: function(dsBinding, type){
            var judgeData = setInterval(function() {
                if($scope.rootCommon.dsList.length>0){
                    clearInterval(judgeData);
                    $scope.dsCtrl.editDs = $scope.getDsInfo(type);

                    //从widget编辑器跳转过来
                    if($scope.rootTmpData.dataSources){
                        var ds = $scope.getDsInfo($scope.rootTmpData.dataSources.dsCode);
                        if($scope.dsCtrl.editDs.code == ds.code){
                            $scope.dsCtrl.goToMode = $scope.rootTmpData.dataSources.type;
                        }
                    }

                    if($scope.dsCtrl.dsDataTep[type]){
                        $scope.$apply(function(){
                            dsBinding.pageLoad = true;
                            dsBinding.accountList = angular.copy($scope.dsCtrl.dsDataTep[type].accountList);
                            $scope.dsCtrl.dsDataTep[type].fileList = angular.copy($scope.dsCtrl.dsDataTep[type].fileList);
                        })
                    } else {
                        $scope.dsCtrl.dsDataTep[type] = {
                            'accountList': [],
                            'fileAllList': [],
                            'fileList': [],
                            'currentAccount': null,
                            'currentAccountCid': null,
                            'query': {fileAllList: null, accountList: null}
                        };
                    }
                    $scope.dsSrv.getAccountList(dsBinding, 'first');
                }
            }, 50)
        }
    }
}








/**
 * translateFilter
 *
 * param: {code: '...', path: '...'}
 */
angular
    .module('pt')
    .filter('translateFilter', ['$translate',translateFilter]);

function translateFilter($translate) {
    return function(value, param) {
        return $translate.instant(param.path+angular.uppercase(param.code));
    };
}



/**
 * replaceFilter
 *
 */
angular
    .module('pt')
    .filter('replaceFilter', [replaceFilter]);

function replaceFilter() {
    return function(value) {
        return value.replace(/\@\#\*/g, ' > ');
    };
}



/**
 * numToLetterFilter
 *
 */
angular
    .module('pt')
    .filter('numToLetterFilter', [numToLetterFilter]);

function numToLetterFilter() {
    return function(value) {
        var a="";
        for(var i=65;i<91;i++)a+=String.fromCharCode(i);
        return a;
    };
}
