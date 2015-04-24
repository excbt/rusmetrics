//reports controller
var app = angular.module('portalNMK');
app.controller('ReportsCtrl',['$scope', 'crudGridDataFactory', 'notificationFactory', function($scope, crudGridDataFactory, notificationFactory){
                     
    $scope.currentObject = {};
    $scope.objects = [];
    $scope.columns = [
        {"name":"reportTypeName","header":"Тип отчета", "class":"col-md-11"}
    ];
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
      

 //get templates   

    $scope.getActive = function(){
        
        if (($scope.objects == []) || (typeof $scope.objects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.objects.length; i++){
            $scope.getParamsets($scope.crudTableName+$scope.objects[i].suffix, $scope.objects[i]);
        };

    };

    $scope.toogleShowGroupDetails = function(curObject){//switch option: current goup details
         curObject.showGroupDetails = !curObject.showGroupDetails;
    };
    
   
    $scope.selectedItem = function(item){      
        var curObject = angular.copy(item);
		$scope.currentObject = curObject;
      
    };
    
    $scope.createReport = function(paramset){
//        alert("Поехали! Выбран вариант: "+paramset.name);
        var url ="../api/reportService/commerce/"+paramset.id+"/download";
        window.open(url);
        
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