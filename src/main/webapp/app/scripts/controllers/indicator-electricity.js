angular.module('portalNMC')
.controller('ElectricityCtrl', function($scope, mainSvc, indicatorSvc, $location, $cookies, $rootScope, $timeout){
//console.log("run ElectricityCtrl"); 
    $rootScope.ctxId = "indicators_page";
    //params for current page width
    $scope.oldMinWidth = null;//save default width 
    $scope.curMinWidth = "1572px";//set width for data table
                    // Проверка пользователя - системный/ не системный
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
    
     //define init controller
    var initCtrl = function(){
        var pathParams = $location.search();       
        var tmpZpId = null;//indicatorSvc.getZpointId();    
        var tmpContObjectId = null;//indicatorSvc.getContObjectId();
        var tmpZpName = null;//indicatorSvc.getZpointName();    
        var tmpContObjectName = null;//indicatorSvc.getContObjectName();
        var tmpTimeDetailType = null;

        if (angular.isUndefined(tmpZpId) || (tmpZpId === null)){
            if (angular.isDefined(pathParams.zpointId) && (pathParams.zpointId !== "null")){
                indicatorSvc.setZpointId(pathParams.zpointId);
            };
        };
        if (angular.isUndefined(tmpContObjectId) || (tmpContObjectId === null)){
            if (angular.isDefined(pathParams.objectId)&&(pathParams.objectId !== "null")){
                indicatorSvc.setContObjectId(pathParams.objectId);
            };
        };
        
        if (angular.isUndefined(tmpZpName) || (tmpZpName === null)){
            if (angular.isDefined(pathParams.zpointName) && (pathParams.zpointName !== "null")){
                indicatorSvc.setZpointName(pathParams.zpointName);
            };
        };
        if (angular.isUndefined(tmpContObjectName) || (tmpContObjectName === null)){
            if (angular.isDefined(pathParams.objectName) && (pathParams.objectName !== "null")){
                indicatorSvc.setContObjectName(pathParams.objectName);
            };
        };
        
        if (angular.isUndefined(tmpTimeDetailType) || (tmpTimeDetailType === null)){
            if (angular.isDefined(pathParams.timeDetailType) && (pathParams.timeDetailType !== "null")){
                $scope.timeDetailType = pathParams.timeDetailType;
            }else{               
                if (angular.isDefined($cookies.timeDetailType) && ($cookies.timeDetailType != "undefined") && ($cookies.timeDetailType != "null")){
                    $scope.timeDetailType = $cookies.timeDetailType;
                }else{                   
                    $scope.timeDetailType = indicatorSvc.getTimeDetailType();
                };
            };
        };
        
        $scope.contZPoint = indicatorSvc.getZpointId();
        $scope.contZPointName = (indicatorSvc.getZpointName() != "undefined") ? indicatorSvc.getZpointName() : "Без названия";
        $scope.contObject = indicatorSvc.getContObjectId();
        $scope.contObjectName = (indicatorSvc.getContObjectName() != "undefined") ? indicatorSvc.getContObjectName() : "Без названия";
        
        //if exists url params "fromDate" and "toDate" get date interval from url params, else get interval from indicator service.
        if (angular.isDefined(pathParams.fromDate) && (pathParams.fromDate !== "null")){
            $rootScope.reportStart = pathParams.fromDate;
        }else if(angular.isDefined($cookies.fromDate) && ($cookies.fromDate !== "null")){
                $rootScope.reportStart = $cookies.fromDate;
            }else{
                $rootScope.reportStart = indicatorSvc.getFromDate();
        };
        if (angular.isDefined(pathParams.toDate) && (pathParams.toDate !== "null")){
            $rootScope.reportEnd = pathParams.toDate;
        }else if (angular.isDefined($cookies.toDate) && ($cookies.toDate !== "null")){
                $rootScope.reportEnd = $cookies.toDate;
            }else{
                $rootScope.reportEnd = indicatorSvc.getToDate();
        };
        //get date range settings
        $scope.dateRangeOptsRu = mainSvc.getDateRangeOptions("indicator-ru");        
    };
    initCtrl();
    
    $(document).ready(function() {
        $scope.oldMinWidth = $('.wrap > .container-fluid >.row').css("min-width");
        $('.wrap > .container-fluid >.row').css("min-width", $scope.curMinWidth);
    });
    
    $scope.$on('$destroy', function(){
        $('.wrap > .container-fluid >.row').css("min-width", $scope.oldMinWidth);
    });
    
    $scope.initCons = function(){
        $('.wrap > .container-fluid >.row').css("min-width", $scope.curMinWidth);
    };
    
    $scope.initProfile = function(){
        $('.wrap > .container-fluid >.row').css("min-width", $scope.oldMinWidth);
    };
    
    $scope.initTech = function(){
        $('.wrap > .container-fluid >.row').css("min-width", $scope.oldMinWidth);
    };
});