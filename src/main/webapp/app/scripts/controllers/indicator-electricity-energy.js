angular.module('portalNMC')
.controller('ElectricityEnergyCtrl', function($scope, $http, indicatorSvc, mainSvc, $location, $cookies, $rootScope, notificationFactory){
console.log("Run ElectricityEnergyCtrl.");
    
        //TEMPPPPPPPPPPPPPPP
    $scope.data = [
        {
            "id": 0,
            "dataDate": "01.01.2016 00:00",
            "profileInterval": "30",
            "p_Ap": "test",
            "p_An": "test"
        },
        {
            "id": 1,
            "dataDate": "01.01.2016 01:00",
            "profileInterval": "100",
            "p_Ap": "test",
            "p_An": "test"
        }
    ];
    /////////end TEMP region ////////////////
    
     //define init indicator params method
    var initIndicatorParams = function(){
        var pathParams = $location.search();   
        var tmpZpId = null;//indicatorSvc.getZpointId();    
        var tmpContObjectId = null;//indicatorSvc.getContObjectId();
        var tmpZpName = null;//indicatorSvc.getZpointName();    
        var tmpContObjectName = null;//indicatorSvc.getContObjectName();
        var tmpTimeDetailType = null;
        if (angular.isUndefined(tmpZpId)||(tmpZpId===null)){
            if (angular.isDefined(pathParams.zpointId)&&(pathParams.zpointId!=="null")){
                indicatorSvc.setZpointId(pathParams.zpointId);
            };
        };
        if (angular.isUndefined(tmpContObjectId)||(tmpContObjectId===null)){
            if (angular.isDefined(pathParams.objectId)&&(pathParams.objectId!=="null")){
                indicatorSvc.setContObjectId(pathParams.objectId);
            };
        };
        
        if (angular.isUndefined(tmpZpName)||(tmpZpName===null)){
            if (angular.isDefined(pathParams.zpointName)&&(pathParams.zpointName!=="null")){
                indicatorSvc.setZpointName(pathParams.zpointName);
            };
        };
        if (angular.isUndefined(tmpContObjectName)||(tmpContObjectName===null)){
            if (angular.isDefined(pathParams.objectName)&&(pathParams.objectName!=="null")){
                indicatorSvc.setContObjectName(pathParams.objectName);
            };
        };
        
        $scope.contZPoint = indicatorSvc.getZpointId();
        $scope.contZPointName = (indicatorSvc.getZpointName()!="undefined")?indicatorSvc.getZpointName() : "Без названия";
        $scope.contObject = indicatorSvc.getContObjectId();
        $scope.contObjectName = (indicatorSvc.getContObjectName()!="undefined")?indicatorSvc.getContObjectName() : "Без названия";     
        
        //if exists url params "fromDate" and "toDate" get date interval from url params, else get interval from indicator service.
        if (angular.isDefined(pathParams.fromDate)&&(pathParams.fromDate!=="null")){
            $rootScope.reportStart = pathParams.fromDate;
        }else if(angular.isDefined($cookies.fromDate)&&($cookies.fromDate!=="null")){
                $rootScope.reportStart = $cookies.fromDate;
            }else{
                $rootScope.reportStart = indicatorSvc.getFromDate();
        };
        if (angular.isDefined(pathParams.toDate)&&(pathParams.toDate!=="null")){
            $rootScope.reportEnd = pathParams.toDate;
        }else if (angular.isDefined($cookies.toDate)&&($cookies.toDate!=="null")){
                $rootScope.reportEnd = $cookies.toDate;
            }else{
                $rootScope.reportEnd = indicatorSvc.getToDate();
        };       
    };
        //run init method
    //initIndicatorParams();
    
    $scope.electroKind = "Energy";
    ///api/subscr/66948436/serviceElProfile/30min/159919982
    //{beginDate=[2015-12-01], endDate=[2015-12-31]}
    $scope.curDate = moment().endOf('day').format("YYYY-MM-DD");
    var apiSubscUrl = "../api/subscr/";
    var timeDetailType = "30min";
    var viewMode = "serviceElProfile";
    $scope.dataUrl = apiSubscUrl + $scope.contObject + "/" + viewMode + "/" + timeDetailType + "/" + $scope.contZPoint;// + "/?beginDate=" + $scope.curDate + "&endDate=" + $scope.curDate;
    $scope.columns = [
        {
            header : "Дата",
            headerClass : "col-xs-2 col-md-2 nmc-text-align-center",
            dataClass : "col-xs-2 col-md-2",
            fieldName: "dataDate"
        }, 
        {
            header : "Интервал, мин",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "profileInterval"
        },
        {
            header : "A+, кВт/ч",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "p_Ap"
        },
        {
            header : "A-, кВт/ч",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "p_An"
        },
        {
            header : "R+, кВАр/ч",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "q_Rp"
        },
        {
            header : "R-, кВАр/ч",
            headerClass : "col-xs-1 col-md-1",
            dataClass : "col-xs-1 col-md-1",
            fieldName: "q_Rn"
        },
    ];
    
//    $scope.successCallback = function(response){
        //$scope.data = response.data;
//        var tmp = response.data.map(function(el){
//            var result  = {};
//            for(var i in $scope.columns){
//                if ($scope.columns[i].fieldName == "dataDate"){
    //console.log("Indicator id = "+el.id);                            
    //console.log("Indicator timestamp in millisec, which get from server = "+el.dataDate);
    //console.log("Indicator timestamp +3 hours in sec = "+(Math.round(el.dataDate/1000.0)+3*3600));                            
    //                          var datad = DateNMC(el.dataDate);
    //console.log(datad.getTimezoneOffset());
    //console.log(datad.toLocaleString());                            
                    //el.dataDate=el.dataDateString;//printDateNMC(datad);
//                    continue;
//                };
//                if ((el[$scope.columns[i].fieldName]!=null)&&($scope.columns[i].fieldName !== "dataDate")){
//                    el[$scope.columns[i].fieldName] = el[$scope.columns[i].fieldName].toFixed(3);
//                };
//
//            };                    
//        });
//        $scope.data = tmp;
//    };
//    $scope.errorCallback = function(error){
//        notificationFactory.errorInfo(error.statusText, error.data.description || error.data);
//        console.log(error);
//    };
//    
//    $scope.getData = function(){
//        $http.get($scope.dataUrl).then($scope.successCallback, $scope.errorCallback);
//    };
//    $scope.getData();
//    
//    $scope.refreshData = function(){
//       $scope.dataUrl = apiSubscUrl + $scope.contObject + "/" + viewMode + "/" + timeDetailType + "/" + $scope.contZPoint + "/?beginDate=" + $scope.curDate + "&endDate=" + $scope.curDate;
//        $scope.getData();
//    };
//    
    
    
});