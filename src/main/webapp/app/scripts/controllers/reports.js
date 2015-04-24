//reports controller
var app = angular.module('portalNMK');
app.controller('ReportsCtrl',['$scope', 'crudGridDataFactory', 'notificationFactory', function($scope, crudGridDataFactory, notificationFactory){
                    
    
    $scope.currentObject = {};
   
    $scope.objects = [
//        {
//            "reportType":"COMMERCE_REPORT"
//            ,"reportTypeName":"Коммерческий"
//            ,"paramsetsCount": 0
//            ,"paramsets": []
//        }
//        ,        {
//            "reportType":"CONS_REPORT"
//            ,"reportTypeName":"Сводный"
//            ,"paramsetsCount":0
//            ,"paramsets": []
//        }
//        ,        {
//            "reportType":"EVENT_REPORT"
//            ,"reportTypeName":"События"
//            ,"paramsetsCount": 0
//            ,"paramsets": []
//        }
    ];
    
    
    $scope.columns = [
        {"name":"reportTypeName","header":"Тип отчета", "class":"col-md-11"}
//        ,{"name":"templatesCount", "header":"Кол-во шаблонов", "class":"col-md-1"}
    ];
    
//    $scope.commerce = {};
//    $scope.cons = {};
//    $scope.event = {};
    
    
    $scope.crudTableName = "../api/reportParamset"; 
    
    //report types
    $scope.reportTypes = [];
    $scope.getReportTypes = function(){
        var table = "../api/reportSettings/reportType";
        crudGridDataFactory(table).query(function(data){
            $scope.reportTypes = data;
            var newObjects = [];
            var newObject = {};
            for (var i = 0; i<data.length; i++){
                newObject = {};
                newObject.reportType = data[i].keyname;
                newObject.reportTypeName = data[i].caption;
                newObject.suffix = data[i].suffix;
                
                newObjects.push(newObject);
            };
            
            $scope.objects = newObjects;
            
            $scope.getActive();
        });
    };
    $scope.getReportTypes();
    

    $scope.oldColumns = [
        {"name":"name", "header":"Название варианта", "class":"col-md-5"}
        ,{"name":"activeStartDate", "header":"Действует с", "class":"col-md-2"}
    ];
    
    
    var successCallback = function (e) {
        notificationFactory.success();
    };

    var errorCallback = function (e) {
        notificationFactory.error(e.data.ExceptionMessage);
    };
    
    $scope.getParamsets = function(table, type){
        crudGridDataFactory(table).query(function (data) {
            type.paramsets = data;
            type.paramsetsCount = data.length;
        });
    };
      
//    $scope.getCommerceParamsets = function (table) {
//        crudGridDataFactory(table).query(function (data) {
//            $scope.commerce.paramsets = data;
//
//            $scope.objects[0].paramsetsCount = $scope.commerce.paramsets.length;
//            $scope.objects[0].paramsets = $scope.commerce.paramsets;
//        });
//    };
//    $scope.getConsParamsets = function (table) {
//        crudGridDataFactory(table).query(function (data) {
//            $scope.cons.paramsets = data; 
//
//            $scope.objects[1].paramsetsCount = $scope.cons.paramsets.length;
//            $scope.objects[1].paramsets = $scope.cons.paramsets;
//        });
//    };
//    $scope.getEventParamsets = function (table) {
//        crudGridDataFactory(table).query(function (data) {
//            $scope.event.paramsets = data;
//
//            $scope.objects[2].paramsetsCount = $scope.event.paramsets.length;
//            $scope.objects[2].paramsets = $scope.event.paramsets;
//        });
//    };
 //get templates   

    $scope.getActive = function(){
        
        if (($scope.objects == []) || (typeof $scope.objects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.objects.length; i++){
            $scope.getParamsets($scope.crudTableName+$scope.objects[i].suffix, $scope.objects[i]);
        };
        
//        $scope.getCommerceParamsets($scope.crudTableName+"/commerce");
//        $scope.getConsParamsets($scope.crudTableName+"/cons");
//        $scope.getEventParamsets($scope.crudTableName+"/event");
    };
    
//    $scope.getActive();
    
    
    $scope.toogleShowGroupDetails = function(curObject){//switch option: current goup details
         curObject.showGroupDetails = !curObject.showGroupDetails;
    };
    
   
    $scope.selectedItem = function(item){      
        var curObject = angular.copy(item);
		$scope.currentObject = curObject;
      
    };
    
    $scope.createReport = function(paramset){
        alert("Поехали! Выбран вариант: "+paramset.name);
    };


//                        $scope.getReport = function(queryDates) {
//
//                            var sessDateFrom = moment(queryDates.startDate).format('YYYY-MM-DD');
//                            var sessDateTill = moment(queryDates.endDate).format('YYYY-MM-DD');
//
//                            $scope.loading = true;
//                            
//                            $scope.invokeReport().query({beginDate: sessDateFrom, endDate:sessDateTill}
//                                                        ,function(data){
//                                                            
//                                                          }
//                                                       );

//                            terminalDataFactory("sessDateTerminalIdsPeriod").query({
//                                sessDateFrom : sessDateFrom,
//                                sessDateTill : sessDateTill
//                            }, function(data) {
//
//                                $scope.setTerminals(data);
//
//                                console.log("data:" + data);
//                                $scope.loading = false;                    
//
//                            }, errorCallback);
//                        };
    
//                        $scope.invokeReport = function(type) {
//                            return $resource(type, {beginDate: '@beginDate', endDate: '@endDate' 
//                            }, {
//
//                                query: {method: 'GET', isArray: false}
//
//                            });
//			             };   

                    
                }]);