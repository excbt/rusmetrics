
angular.module('portalNMK')
    .controller('IndicatorsCtrl', ['$scope','$rootScope', 'crudGridDataFactory',function($scope, $rootScope, crudGridDataFactory){

    $scope.welcome = "Welcome to indicators controller.";
 
        
          $scope.zpointTable = "../api/subscr/"+$rootScope.contObject.id+"/service/"+$rootScope.timeDetailType+"/"+$rootScope.contZPoint.id+"?beginDate="+$rootScope.reportStart+"&endDate="+$rootScope.reportEnd;
            
 console.log("$scope.zpointTable = "+$scope.zpointTable);
    //Определяем оформление для таблицы показаний прибора
        
        //Определеяю названия колонок
    var heatColumns = [{
						fieldName : "dataDate",
						header : "Дата",
						headerClass : "col-md-1",
						dataClass : "col-md-1"
						
					}, 
                        {
						fieldName : "workTime",
						header : "Время наработки, час",
						headerClass : "col-md-1",
						dataClass : "col-md-1"
					}, {
						fieldName : "h_delta",
						header : "ГКал отопления, ГКал",
						headerClass : "col-md-1",
						dataClass : "col-md-1"
					}, {
						fieldName : "m_in",
						header : "Масса подачи",
						headerClass : "col-md-1",
						dataClass : "col-md-1"
					}, {
						fieldName : "noticeDate",
						header : "Дата",
						headerClass : "col-md-2",
						dataClass : "col-md-2"
					}, {
						fieldName : "noticeObject",
						header : "Объект",
						headerClass : "col-md-2",
						dataClass : "col-md-2"
					} , {
						fieldName : "noticeZpoint",
						header : "Точка учета",
						headerClass : "col-md-1",
						dataClass : "col-md-1"
					} ];
        
    $scope.tableDef = {
					tableClass : "crud-grid table table-lighter table-bordered table-condensed table-hover",
					hideHeader : false,
					headerClassTR : "info",
					columns : []
		};

      //Получаем показания
        $scope.columns = [];
 //       $scope.data = {};
        $scope.getData = function () {
            var table =  $scope.zpointTable;
            crudGridDataFactory(table).query(function (data) {
                    
                    var iCol = 0;
                    var notUserColumns = new Set(["id","toJSON","$get", "$save", "$query", "$remove", "$delete", "$update", "version", "timeDetailType"]);
                    for (var k in data[0]){ 
                        if (notUserColumns.has(k)){continue;};      
                        var column = {};
                        
                        column.header = k;
                        column.headerClass = "col-md-1";
                       // column.dataClass = "col-md-1";
                        column.fieldName = k; 
                        
                        $scope.columns[iCol] = column;
                        iCol=iCol+1;
//console.log("Column["+iCol+"]= "+k);                          
                    };
                    $scope.tableDef.columns = $scope.columns;
                    
                    var tmp = data.map(function(el){
                        var result  = {};
                        if (typeof el.dataDate != 'undefined'){
                            el.dataDate = (new Date(el.dataDate)).toLocaleString();
                        }
                    });
                
                    $scope.data = data;

            });
        };

        $scope.getData();    

}]);