
angular.module('portalNMK')
    .controller('IndicatorsCtrl', ['$scope','$rootScope', 'crudGridDataFactory',function($scope, $rootScope, crudGridDataFactory){
    
    var checkInputParams = function(){
        if (($rootScope.contObject==null) || (typeof $rootScope.contObject=='undefined')){
            window.location.replace("#/objects_edit/");
            return false;
        };
        return true;
    };
        
   // checkInputParams();    
    
    
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
                header : "Масса обработки, т",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "t_in":{
                header : "Температура подачи, град C",
                headerClass : "col-md-1",
                dataClass : "col-md-1"
            }, 
            "t_out":{
                header : "Температура обработки, град C",
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
                header : "Объем обработки, м3",
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
                header : "Давление на обработке, Мпа",
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
        
        
    //helper for date formating
//    function padStr(i) {
//        return (i < 10) ? "0" + i : "" + i;
//    }   
        
        var setUrl = function(){
        if (checkInputParams()){
           
        }
    };
    setUrl();        

      //Получаем показания
    $scope.columns = [];
 //       $scope.data = {};
    $scope.getData = function () {
         if (!checkInputParams()){
            return;   
         }
         $scope.zpointTable = "../api/subscr/"+$rootScope.contObject.id+"/service/"+$rootScope.timeDetailType+"/"+$rootScope.contZPoint.id+"?beginDate="+$rootScope.reportStart+"&endDate="+$rootScope.reportEnd;
        var table =  $scope.zpointTable;      
        crudGridDataFactory(table).query(function (data) {
                var iCol = 0;
                var notUserColumns = new Set(["id","toJSON","$get", "$save", "$query", "$remove", "$delete", "$update", "version", "timeDetailType"]);
                for (var k in data[0]){ 
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

                var tmp = data.map(function(el){
                    var result  = {};
                    for(var i in $scope.columns){
                        if ($scope.columns[i].fieldName == "dataDate"){
//                          var datad = new Date(el.dataDate);
                          el.dataDate = moment(el.dataDate).format("DD.MM.YY HH:mm");
//                            el.dataDate = moment(el.dataDate).subtract(1, 'hours').format("DD.MM.YY HH:mm");
//                        moment().
//                          el.dataDate = datad.toLocaleString("ru-RU", {year:"2-digit",  month:"numeric", day: "numeric", hour:"numeric", minute:"numeric"});
//                    var dateStr = padStr(datad.getDate()) +"."+
//                        padStr(1 + datad.getMonth()) +"."+
//                        padStr(datad.getFullYear()) +" "+
//                        padStr(datad.get.getHours()) +":"+
//                        padStr(datad.getMinutes()) 
//                    ;
//                    el.dataDate = dateStr;
                        
//                    el.dataDate = (new Date(el.dataDate)).toLocaleString();  
                            continue;
                        }
//console.log($scope.columns[i].fieldName);                        
//console.log(el[$scope.columns[i].fieldName]); 
//console.log(typeof el[$scope.columns[i].fieldName]);
                        if (el[$scope.columns[i].fieldName]!=null){
                            el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed(2);
                        };
                        
                    };
                    
//                    if (typeof el.dataDate != 'undefined'){
//                          var datad = new Date(el.dataDate);
//                          el.dataDate = moment(el.dataDate).subtract(1, 'hours').format("DD.MM.YY HH:mm");
//                        moment().
//                          el.dataDate = datad.toLocaleString("ru-RU", {year:"2-digit",  month:"numeric", day: "numeric", hour:"numeric", minute:"numeric"});
//                    var dateStr = padStr(datad.getDate()) +"."+
//                        padStr(1 + datad.getMonth()) +"."+
//                        padStr(datad.getFullYear()) +" "+
//                        padStr(datad.get.getHours()) +":"+
//                        padStr(datad.getMinutes()) 
//                    ;
//                    el.dataDate = dateStr;
//                    el.dataDate = (new Date(el.dataDate)).toLocaleString();  
//                        }
                    
                });
                $scope.data = data;
        });
    };

    $scope.getData();    

}]);