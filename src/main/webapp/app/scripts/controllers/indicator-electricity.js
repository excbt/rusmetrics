angular.module('portalNMC')
.controller('ElectricityCtrl', function($scope, mainSvc, indicatorSvc, $location, $cookies, $rootScope){
console.log("run ElectricityCtrl"); 
                    // Проверка пользователя - системный/ не системный
    $scope.isSystemuser = function(){
        var result = false;
        $scope.userInfo = $rootScope.userInfo;
        if (angular.isDefined($scope.userInfo)){
            result = $scope.userInfo._system;
        };
        return result;
    };
    
     //define init controller
    var initCtrl = function(){
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
        
        if (angular.isUndefined(tmpTimeDetailType)||(tmpTimeDetailType===null)){
            if (angular.isDefined(pathParams.timeDetailType)&&(pathParams.timeDetailType!=="null")){
                $scope.timeDetailType = pathParams.timeDetailType;
            }else{               
                if (angular.isDefined($cookies.timeDetailType)&&($cookies.timeDetailType!="undefined")&&($cookies.timeDetailType!="null")){
                    $scope.timeDetailType = $cookies.timeDetailType;
                }else{                   
                    $scope.timeDetailType = indicatorSvc.getTimeDetailType();
                };
            };
        };
        
        $scope.contZPoint = indicatorSvc.getZpointId();
        $scope.contZPointName = (indicatorSvc.getZpointName()!="undefined")?indicatorSvc.getZpointName() : "Без названия";
        $scope.contObject = indicatorSvc.getContObjectId();
        $scope.contObjectName = (indicatorSvc.getContObjectName()!="undefined")?indicatorSvc.getContObjectName() : "Без названия";
    };
    initCtrl();
});