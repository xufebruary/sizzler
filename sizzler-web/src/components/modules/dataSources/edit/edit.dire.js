'use strict';



/**
 * Edit
 * @dataSources
 *
 */
angular
    .module('pt')
    .directive('dsEdit', ['$state', 'dataMutualSrv',dsEdit]);

function dsEdit($state, dataMutualSrv) {
    return {
        restrict: 'EA',
        // replace: true,
        // require : '^?hotTable',
        templateUrl: '/components/modules/dataSources/edit/edit.tpl.html?v='+BASE_VERSION,
        link: link
    }

    function link(scope, elem, attrs) { 
        var editSet = scope.editSet = {
            startIndex: scope.dsEditor.hotTable.ignoreRowStart + 1,
            endIndex: scope.dsEditor.hotTable.ignoreRowEnd + 1,
            headerIndex: scope.dsEditor.hotTable.headIndex + 1,
            saveSuccessTips: false,
            saveErrorTips: false,
            verify: {
                flag: true,
                table: []
            },
            dataVerifyTips: false,
            dataTypeDropdown: []
        }
        dataInit();

        //切换表头模式(指定||自定义)
        scope.changeMode = function(mode){
            scope.dsEditor.hotTable.headMode = mode;

            if(mode == 'custom'){
                //清除表头
                var headIndex = scope.dsEditor.hotTable.ignoreRow.indexOf(scope.dsEditor.hotTable.headIndex);
                if(headIndex>=0){
                   scope.dsEditor.hotTable.ignoreRow.splice(headIndex); 
                }
                scope.dsEditor.hotTable.headIndex = null;
                angular.element('.ds-table-bd-tr').removeClass('ds-table-header');
                for (var i = scope.dsEditor.hotTable.schema.length - 1; i >= 0; i--) {
                    scope.dsEditor.hotTable.schema[i].name = '';
                };
            } else {
                //添加表头
                scope.dsEditor.hotTable.headIndex = editSet.headerIndex;
                var headIndex = scope.dsEditor.hotTable.ignoreRow.indexOf(scope.dsEditor.hotTable.headIndex);
                if(headIndex<0){
                   scope.dsEditor.hotTable.ignoreRow.push(headIndex); 
                }
                angular.element('.ds-table-bd-tr').removeClass('ds-table-header').eq(editSet.headerIndex-1).addClass('ds-table-header');
                for (var i = scope.dsEditor.hotTable.schema.length - 1; i >= 0; i--) {
                    scope.dsEditor.hotTable.schema[i].name = scope.dsEditor.hotTable.data[editSet.headerIndex-1][i];
                };
            }

            //校验表头
            if( editSet.verify.table.length>0 && !editSet.verify.table[scope.dsEditor.hotTableIndex].flag ){
                dataVerify()
            }
        }
        
        //调整数据有效范围
        scope.changeRang = function(type){

            //行忽略范围
            scope.dsEditor.hotTable.ignoreRow = [];
            angular.element('.ds-table-bd-tr').removeClass('ds-table-ignore');

            //行头匹配
            var sMax = editSet.startIndex-1;
            for (var i = 0; i < sMax; i++) {
                scope.dsEditor.hotTable.ignoreRow.push(i);
                angular.element('.ds-table-bd-tr').eq(i).addClass('ds-table-ignore');
            };

            //行尾匹配
            var j = parseInt(scope.dsEditor.hotTable.rowSum)-(editSet.endIndex-1);
            if(j<=200){
                for (j; j < parseInt(scope.dsEditor.hotTable.rowSum); j++) {
                    scope.dsEditor.hotTable.ignoreRow.push(j);
                    angular.element('.ds-table-bd-tr').eq(j).addClass('ds-table-ignore');
                };
            }
            
            if(scope.dsEditor.hotTable.headType == 'row'){
                scope.dsEditor.hotTable.ignoreRowStart = editSet.startIndex-1;
                scope.dsEditor.hotTable.ignoreRowEnd = editSet.endIndex-1;
            } else {
                // scope.dsEditor.hotTable.ignoreColStart = 1,
                // scope.dsEditor.hotTable.ignoreColEnd = 0,
            }

            //列忽略范围
            for (var i = scope.dsEditor.hotTable.ignoreCol.length - 1; i >= 0; i--) {
                var index = scope.dsEditor.hotTable.ignoreCol[i];

                //置灰
                for (var j = scope.dsEditor.hotTable.data.length - 1; j >= 0; j--) {
                    angular.element('.ds-table-bd-tr').eq(j).children('td:eq('+(index+1)+')').addClass('ds-table-ignore-td');
                };
                angular.element('.ds-editor-header').children('.ds-editor-hd-box').eq(index).addClass('ds-table-ignore-td')
            };

            //表头忽略
            if(scope.dsEditor.hotTable.headMode=='assign'){
                var headIndex = scope.dsEditor.hotTable.ignoreRow.indexOf(scope.dsEditor.hotTable.headIndex);
                if(headIndex<0){
                   scope.dsEditor.hotTable.ignoreRow.push(scope.dsEditor.hotTable.headIndex); 
                }
            }

            console.log(scope.dsEditor.hotTable.ignoreCol)
            console.log(scope.dsEditor.hotTable.ignoreRow)
        }

        //切换sheet
        scope.sltSheet = function(sheet, index) {
            scope.dsEditor.dsContnet.table[scope.dsEditor.hotTableIndex] = angular.copy(scope.dsEditor.hotTable);
            // scope.dsEditor.dsContnet[index] = angular.copy(scope.dsEditor.hotTable);

            scope.dsEditor.hotType = 'upload';
            scope.dsDataFormat(scope.dsEditor.dsContnet, index, scope.dsEditor.hotType);
            editSet.startIndex = scope.dsEditor.hotTable.ignoreRowStart + 1;
            editSet.endIndex = scope.dsEditor.hotTable.ignoreRowEnd + 1;
            editSet.headerIndex = scope.dsEditor.hotTable.headIndex + 1;

            scope.$on('ngRepeatFinished', function (ngRepeatFinishedEvent) {
                scope.changeRang();
                dataVerify();

                if( editSet.verify.table.length>0 && !editSet.verify.table[scope.dsEditor.hotTableIndex].flag ){
                    var table = editSet.verify.table[scope.dsEditor.hotTableIndex];

                    for (var i = 0; i < table.schema.length; i++) {
                        if(table.schema[i]){
                            angular.element('.ds-ipt-header').eq(i).removeClass('bg-danger').addClass('no-bg');
                        } else {
                            angular.element('.ds-ipt-header').eq(i).removeClass('no-bg').addClass('bg-danger');
                        }
                    };
                }
            });
        }


        //切换类型(指标||维度||忽略)
        scope.changeType = function(type, index) {
            console.log(type)
            if(scope.dsEditor.hotTable.schema[index].type !== type.code){
                var colIndex = scope.dsEditor.hotTable.ignoreCol.indexOf(index);

                if(type.code == 'ignore'){
                    if(!scope.dsEditor.hotTable.ignoreCol){
                        scope.dsEditor.hotTable.ignoreCol = [];
                        scope.dsEditor.hotTable.ignoreCol.push(index);
                    } else {
                        if(colIndex < 0){
                            scope.dsEditor.hotTable.ignoreCol.push(index);
                        }
                    }

                    //置灰
                    for (var i = scope.dsEditor.hotTable.data.length - 1; i >= 0; i--) {
                        angular.element('.ds-table-bd-tr').eq(i).children('td:eq('+(index+1)+')').addClass('ds-table-ignore-td');
                    };
                    angular.element('.ds-editor-header').children('.ds-editor-hd-box').eq(index).addClass('ds-table-ignore-td')
                } else {
                    if(colIndex >= 0){
                        scope.dsEditor.hotTable.ignoreCol.splice(colIndex)
                    }

                    //取消置灰
                    for (var i = scope.dsEditor.hotTable.data.length - 1; i >= 0; i--) {
                        angular.element('.ds-table-bd-tr').eq(i).children('td:eq('+(index+1)+')').removeClass('ds-table-ignore-td');
                    };
                    angular.element('.ds-editor-header').children('.ds-editor-hd-box').eq(index).removeClass('ds-table-ignore-td')
                }
                scope.dsEditor.hotTable.schema[index].type = type.code;
            }
        }

        //切换数据类型
        scope.changeDataType = function(dataType, index) {
            scope.dsEditor.hotTable.schema[index].dataType = dataType.code;
        }

        //切换数据格式
        scope.changeDataFormat = function(format, index){
            scope.dsEditor.hotTable.schema[index].dataType = format.type;
            scope.dsEditor.hotTable.schema[index].dataFormat = format.code;
        }

        //修改表头内容
        scope.changeHeader = function(head, index){
            scope.dsEditor.hotTable.schema[index].name = head;

            if( editSet.verify.table.length>0 ){
                if(head !== ''){
                    angular.element('.ds-ipt-header').eq(index).removeClass('bg-danger').addClass('no-bg');
                    editSet.verify.table[scope.dsEditor.hotTableIndex].schema = false;
                } else {
                    angular.element('.ds-ipt-header').eq(index).removeClass('no-bg').addClass('bg-danger');
                    editSet.verify.table[scope.dsEditor.hotTableIndex].schema = true;
                }
            }
        }

        //save
        scope.save = function() {
            scope.dsCtrl.showLoading = true;

            scope.dsEditor.dsContnet.table[scope.dsEditor.hotTableIndex] = angular.copy(scope.dsEditor.hotTable);
            var dsData = scope.dsEditor.dsContnet;

            if( !dataVerify().flag ){
                scope.editSet.dataVerifyTips = true;
                scope.dsCtrl.showLoading = false;
            } else {
                dataMutualSrv.post(LINK_EXCEL_FILE_ADD, angular.copy(dsData)).then(function(data) {
                    if (data.status == 'success') {
                        console.log(data)
                        scope.editSet.saveSuccessTips = true;
                    } else if (data.status == 'failed') {
                        console.log('Post Data Failed!')
                        scope.editSet.saveErrorTips = true;
                    } else if (data.status == 'error') {
                        console.log('Post Data Error: ')
                        console.log(data.message)
                        scope.editSet.saveErrorTips = true;
                    }

                    scope.dsCtrl.showLoading = false;
                })
            }
        }

        //cancel
        scope.cancel = function() {
            // scope.dsCtrl.showEdit = false;
            scope.goTo('fileList')
        }



        //提示框跳转
        scope.goTo = function(type){
            if(type=='createWidget'){
                $state.go('pt.dashboard');
            } else {
                scope.dsCtrl.goToMode = type;
                scope.sltDs(scope.dsCtrl.editDs)
            }
        }
        //关闭提示框
        scope.closeTips = function(){
            scope.editSet.saveSuccessTips = false;
            scope.editSet.dataVerifyTips = false;
            scope.editSet.saveErrorTips = false;
        }


        //数据初始化
        function dataInit(){
            scope.$on('ngRepeatFinished', function (ngRepeatFinishedEvent) {
                scope.changeRang();

                if(scope.dsEditor.hotTable.headMode=='assign'){
                    //添加表头
                    angular.element('.ds-table-bd-tr').removeClass('ds-table-header').eq(editSet.headerIndex-1).addClass('ds-table-header');
                }
                // scope.changeMode(scope.dsEditor.hotTable.headMode)

                // angular.element('#js-dsEditor').slimscroll({
                //     height: Math.min(scope.dsEditor.hotHeight, 580),
                //     size:'4px',
                //     allowPageScroll: false
                // })
            });
        }


        //数据校验
        function dataVerify() {
            scope.dsEditor.dsContnet.table[scope.dsEditor.hotTableIndex] = angular.copy(scope.dsEditor.hotTable);
            var data = scope.dsEditor.dsContnet;

            editSet.verify = {
                flag: true,
                table: []
            };

            var tableFlag = true;
            for (var i = 0; i < data.table.length; i++) {
                editSet.verify.table.push({index: i, flag: true,schema: []});

                for (var j = 0; j < data.table[i].schema.length; j++) {
                    if(scope.dsEditor.hotTable.ignoreCol.indexOf(j)<0 && data.table[i].schema[j].name == ''){
                        tableFlag = false;
                        editSet.verify.table[i].flag = false;
                        editSet.verify.table[i].schema.push(false)
                    } else {
                        editSet.verify.table[i].schema.push(true)
                    }
                };

                editSet.verify.flag = tableFlag;
            };

            // if(!editSet.verify.flag){
                // if(!editSet.verify.table[scope.dsEditor.hotTableIndex].flag){
                    var table = editSet.verify.table[scope.dsEditor.hotTableIndex];

                    for (var i = 0; i < table.schema.length; i++) {
                        if(table.schema[i]){
                            angular.element('.ds-ipt-header').eq(i).removeClass('bg-danger').addClass('no-bg');
                        } else {
                            angular.element('.ds-ipt-header').eq(i).removeClass('no-bg').addClass('bg-danger');
                        }
                    };
                // }
                
                for (var i = 0; i < editSet.verify.table.length; i++) {
                    if(editSet.verify.table[i].flag){
                        angular.element('.ds-ft-btn').eq(i).removeClass('bg-danger');
                    } else {
                        angular.element('.ds-ft-btn').eq(i).addClass('bg-danger');
                    }
                };
            // }

            console.log(editSet.verify)
            return editSet.verify;
        }



        //
        document.body.onbeforeunload = function(){
            event.returnValue="当前表单数据未保存，确定离开当前页面吗？"; 
        }



        // $rootScope.$on('$stateChangeStart', function (event, to, toParams, from, fromParams) {
        //     if(to.name != 'signin'){

        //     }
        // });



    }
}