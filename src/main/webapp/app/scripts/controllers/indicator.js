
angular.module('portalNMK')
    .controller('IndicatorsCtrl', ['$scope','$rootScope', '$cookies', 'crudGridDataFactory',function($scope, $rootScope, $cookies, crudGridDataFactory){

        //Определяем оформление для таблицы показаний прибора
        
        //Определеяю названия колонок
        
    var listColumns = {
            "dataDate":{
                header : "Дата",
                headerClass : "col-md-2",
                dataClass : "col-md-2"
            }, 
            "workTime":{
                header : "Время наработки, час",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "h_delta":{
                header : "ГКал отопления, ГКал",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
          "m_in":{
                header : "Масса подачи, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "m_out":{
                header : "Масса обратки, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "t_in":{
                header : "Температура подачи, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "t_out":{
                header : "Температура обратки, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            } , 
            "t_cold":{
                header : "Температура холодной воды, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            } ,
            "t_outdoor":{
                header : "Температура окружающей среды, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "m_delta":{
                header : "Разница масс воды, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "v_in":{
                header : "Объем подачи, м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "v_out":{
                header : "Объем обратки, м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "v_delta":{
                header : "Разница объемов (потребление), м3",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "h_in":{
                header : "Входящие ГКал, ГКал",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "h_out":{
                header : "ГКал на выходе, ГКал",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "p_in":{
                header : "Давление на подаче, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "p_out":{
                header : "Давление на обратке, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            },
            "p_delta":{
                header : "Разность давлений, Мпа",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }
        }
        ;    
    
    $scope.tableDef = {
        tableClass : "crud-grid table table-lighter table-bordered table-condensed table-hover excbt_tableIndicators",
        hideHeader : false,
        headerClassTR : "info",
        columns : []
    };
        
    $rootScope.reportStart = moment().format('YYYY-MM-DD');
    $rootScope.reportEnd = moment().format('YYYY-MM-DD');
        
    $scope.totalIndicators = 0;
    $scope.indicatorsPerPage = 25; // this should match however many results your API puts on one page    
    $scope.timeDetailType = "1h";    
    $scope.data = [];    
    $scope.pagination = {
        current: 1
    };         

      //Получаем показания
    $scope.columns = [];
    $scope.getData = function (pageNumber) {
        $scope.pagination.current = pageNumber;   
         var contZPoint = $cookies.contZPoint;
         $scope.contZPointName = $cookies.contZPointName;
         var contObject = $cookies.contObject;
         $scope.contObjectName = $cookies.contObjectName;

         var timeDetailType = $scope.timeDetailType || $cookies.timeDetailType;
         
         $scope.zpointTable = "../api/subscr/"+contObject+"/service/"+timeDetailType+"/"+contZPoint+"/paged?beginDate="+$rootScope.reportStart+"&endDate="+$rootScope.reportEnd+"&page="+(pageNumber-1)+"&size="+$scope.indicatorsPerPage;
        var table =  $scope.zpointTable;
        crudGridDataFactory(table).get(function (data) {           
                $scope.totalIndicators = data.totalElements;
                var iCol = 0;
                var notUserColumns = new Set(["id","toJSON","$get", "$save", "$query", "$remove", "$delete", "$update", "version", "timeDetailType"]);
                for (var k in data.objects[0]){ 
                    if (notUserColumns.has(k)){continue;};      
                    var column = {};
                    column.header = listColumns[k].header || k; 
                    column.headerClass = listColumns[k].headerClass || "col-md-1";
                    column.dataClass = listColumns[k].dataClass || "col-md-1";
                    column.fieldName = k; 
                    $scope.columns[iCol] = column;
                    iCol=iCol+1;                          
                };
                $scope.tableDef.columns = $scope.columns;

                var tmp = data.objects.map(function(el){
                    var result  = {};
                    for(var i in $scope.columns){
                        if ($scope.columns[i].fieldName == "dataDate"){
//                          var datad = new Date(el.dataDate);
                          el.dataDate = moment(el.dataDate).format("DD.MM.YY HH:mm");
  
                            continue;
                        }
                        if (el[$scope.columns[i].fieldName]!=null){
                            el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed(2);
                        };
                        
                    };                    
                });
                $scope.data = data.objects;
        });
    };

    $scope.pageChanged = function(newPage) {       
        $scope.getData(newPage);
    };  
        
    $scope.$watch('reportStart', function (newDates) {  
        if( (typeof $rootScope.reportStart == 'undefined') || ($rootScope.reportStart==null) ){
            return;
        }
        $scope.getData(1);                              
    }, false);    
}]);