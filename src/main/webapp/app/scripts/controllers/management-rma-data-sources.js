'use strict';
angular.module('portalNMC')
.controller('MngmtDatasourcesCtrl', ['$rootScope', '$scope','$http', 'mainSvc', 'notificationFactory', function($rootScope, $scope, $http, mainSvc, notificationFactory){
//console.log('Run data sources management controller.');
        //default raw data source params
    var TIMEOUT = 25;
    var SLEEPTIME = 250;
    var RESENDS = 3;
    var RECONNECTS = 1;
    var RECONNECT_TIMEOUT = 60;

    $rootScope.ctxId = "management_rma_data_sources_page";
    //ctrl variables
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.subscrUrl = "../api/rma";
    $scope.ctrlSettings.datasourcesUrl = $scope.ctrlSettings.subscrUrl+"/dataSources";
    $scope.ctrlSettings.datasourceTypesUrl = $scope.ctrlSettings.subscrUrl+"/dataSourceTypes";
//    $scope.ctrlSettings.inputIpComplete = false;
    //data initial 
    $scope.data = {};
    $scope.data.dataSources = [];
    $scope.data.dataSourcesTypes = [];
    $scope.data.currentObject = {};
    $scope.data.currentObject.dataSourceTypeKey = "DEVICE";
    
    //ctrl methods
        //get data source types
    var getDatasourceTypes = function(url){
        var targetUrl = url;
        $http.get(targetUrl)
        .then(function(response){
            var tmp = response.data;           
            $scope.data.dataSourcesTypes = tmp;
        },
              function(e){
            console.log(e);
        });
    };
    getDatasourceTypes($scope.ctrlSettings.datasourceTypesUrl);
    
        //get data sources
    var getDatasources = function(url){
        var targetUrl = url;
        $http.get(targetUrl)
        .then(function(response){
            var tmp = response.data;      
//console.log(tmp);
            $scope.data.dataSources = tmp;
        },
              function(e){
            console.log(e);
        });
    };
    
    getDatasources($scope.ctrlSettings.datasourcesUrl);
    
    $scope.selectedItem = function(item){
        $scope.data.currentObject = angular.copy(item);
        if (mainSvc.checkUndefinedNull($scope.data.currentObject.rawTimeout)){
            $scope.data.currentObject.rawTimeout = TIMEOUT;
        }
        if (mainSvc.checkUndefinedNull($scope.data.currentObject.rawSleepTime)){
            $scope.data.currentObject.rawSleepTime = SLEEPTIME;
        }
        if (mainSvc.checkUndefinedNull($scope.data.currentObject.rawResendAttempts)){
            $scope.data.currentObject.rawResendAttempts = RESENDS;
        }
        if (mainSvc.checkUndefinedNull($scope.data.currentObject.rawReconnectAttempts)){
            $scope.data.currentObject.rawReconnectAttempts = RECONNECTS;
        }
        if (mainSvc.checkUndefinedNull($scope.data.currentObject.rawReconnectTimeout)){
            $scope.data.currentObject.rawReconnectTimeout = RECONNECT_TIMEOUT;
        }
    };
    
    $scope.addDatasource = function(){
        $scope.data.currentObject = {};
        $scope.data.currentObject.dataSourceTypeKey = "DEVICE";
        $scope.data.currentObject.rawTimeout = TIMEOUT;
        $scope.data.currentObject.rawSleepTime = SLEEPTIME;
        $scope.data.currentObject.rawResendAttempts = RESENDS;
        $scope.data.currentObject.rawReconnectAttempts = RECONNECTS;
        $scope.data.currentObject.rawReconnectTimeout = RECONNECT_TIMEOUT;
        
        $('#showDatasourceModal').modal();
    };
    $scope.editDatasource = function(dsource){
        $scope.selectedItem(dsource);
//        $scope.data.currentObject = angular.copy(dsource);
        $('#showDatasourceModal').modal();
    };
    
    var successCallback = function(response){
        notificationFactory.success();
        getDatasources($scope.ctrlSettings.datasourcesUrl);
        $('#showDatasourceModal').modal('hide');
        $('#deleteObjectModal').modal('hide');
        $scope.data.currentObject = {};
        $scope.data.currentObject.dataSourceTypeKey = "DEVICE";
    };
    
    var errorCallback = function(e){
//        notificationFactory.errorInfo(e.statusText,e.data.description);       
        console.log(e);
        var errorCode = "-1";
        if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)){
            errorCode = "ERR_CONNECTION";
        };
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
            errorCode = e.resultCode || e.data.resultCode;
        };
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    $scope.saveDatasource = function(dsource){ 
        var checkDsourceFlag = true;
        if (!$scope.checkPositiveNumberValue(dsource.dataSourcePort)){
            notificationFactory.errorInfo("Ошибка","Не корректно задан порт источника данных.");
            checkDsourceFlag = false;
        };
//        if (!$("#inputIP").inputmask("isComplete")){
        if (angular.isUndefined(dsource.dataSourceIp) || (dsource.dataSourceIp === null) || (dsource.dataSourceIp === "")){
            notificationFactory.errorInfo("Ошибка","Не заполнен ip \\ hostname источника данных.");
            checkDsourceFlag = false;
        };
        if (angular.isUndefined(dsource.dataSourceName) || (dsource.dataSourceName === null) || (dsource.dataSourceName === "")){
            notificationFactory.errorInfo("Ошибка","Не задано имя источника данных.");
            checkDsourceFlag = false;
        };
        
        if (dsource.dataSourceTypeKey == 'DEVICE'){
            if (dsource.rawTimeout == ""){
                notificationFactory.errorInfo("Ошибка","Не корректно задано время ожидания ответа.");
                checkDsourceFlag = false;
            }else{
                dsource.rawTimeout = Number(dsource.rawTimeout);
                if (!$scope.checkPositiveNumberValue(dsource.rawTimeout)){
                    notificationFactory.errorInfo("Ошибка","Не корректно задано время ожидания ответа.");
                    checkDsourceFlag = false;
                }
            };
            if (dsource.rawSleepTime == ""){
                notificationFactory.errorInfo("Ошибка","Не корректно задан интервал проверки получения ответа.");
                checkDsourceFlag = false;
            }else{
                dsource.rawSleepTime = Number(dsource.rawSleepTime);
                if (!$scope.checkPositiveNumberValue(dsource.rawSleepTime)){
                    notificationFactory.errorInfo("Ошибка","Не корректно задан интервал проверки получения ответа.");
                    checkDsourceFlag = false;
                }
            };
            if (dsource.rawResendAttempts == ""){
                notificationFactory.errorInfo("Ошибка","Не корректно задано количество попыток переотправки пакета.");
                checkDsourceFlag = false;
            }else{
                dsource.rawResendAttempts = Number(dsource.rawResendAttempts);
                if (!$scope.checkPositiveNumberValue(dsource.rawResendAttempts)){
                    notificationFactory.errorInfo("Ошибка","Не корректно задано количество попыток переотправки пакета.");
                    checkDsourceFlag = false;
                }
            };
            if (dsource.rawReconnectAttempts == ""){
                notificationFactory.errorInfo("Ошибка","Не корректно задано количество переподключений в случае ошибки.");
                checkDsourceFlag = false;
            }else{
                dsource.rawReconnectAttempts = Number(dsource.rawReconnectAttempts);
                if (!$scope.checkPositiveNumberValue(dsource.rawReconnectAttempts)){
                    notificationFactory.errorInfo("Ошибка","Не корректно задано количество переподключений в случае ошибки.");
                    checkDsourceFlag = false;
                }
            };
            if (dsource.rawReconnectTimeout == ""){
                notificationFactory.errorInfo("Ошибка","Не корректно задана пауза между переподключениями.");
                checkDsourceFlag = false;
            }else{
                dsource.rawReconnectTimeout = Number(dsource.rawReconnectTimeout);
                if (!$scope.checkPositiveNumberValue(dsource.rawReconnectTimeout)){
                    notificationFactory.errorInfo("Ошибка","Не корректно задана пауза между переподключениями.");
                    checkDsourceFlag = false;
                }
            };
        };

        if (checkDsourceFlag === false){
            return;
        };
        var targetUrl = $scope.ctrlSettings.datasourcesUrl;
        if (angular.isDefined(dsource.id)&&(dsource.id !=null)){
            targetUrl = targetUrl+"/"+dsource.id;
            $http.put(targetUrl, dsource).then(successCallback,errorCallback);
        }else{
            $http.post(targetUrl, dsource).then(successCallback,errorCallback);
        };
    };
    
    var setConfirmCode = function(){
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode();
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;                    
    };
    
    $scope.deleteObjectInit = function(dsourse){
        $scope.selectedItem(dsourse);
        setConfirmCode();
    };
    
    $scope.deleteObject = function(dsource){
        var targetUrl = $scope.ctrlSettings.datasourcesUrl+"/"+dsource.id;
        $http.delete(targetUrl).then(successCallback,errorCallback);
    };
    
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
    
    //checkers
    $scope.checkNumericValue = function(num){
        return mainSvc.checkNumericValue(num);
    };
    $scope.checkPositiveNumberValue = function(num){
        return mainSvc.checkPositiveNumberValue(num);
    };
    
    $("#showDatasourceModal").on("shown.bs.modal", function(){
        $("#inputDataSourceMode").focus();
//        inputDSTimeout inputDSCheckInterval inputDSRepeatCount inputDSReconnectionCount inputDSReconnectionInterval
        $("#inputDSTimeout").inputmask();
        $("#inputDSCheckInterval").inputmask();
        $("#inputDSRepeatCount").inputmask();
        $("#inputDSReconnectionCount").inputmask();
        $("#inputDSReconnectionInterval").inputmask();
    });
    
    var setMainPropertiesActiveTab = function(){
        var tab = document.getElementById('con_properties_tab');     
        tab.classList.remove("active");
        var tab = document.getElementById('con_properties');     
        tab.classList.remove("active");
        
        var tab = document.getElementById("main_properties_tab");        
        tab.classList.add("active");
        var tab = document.getElementById("main_properties");
        tab.classList.add("in");
        tab.classList.add("active"); 
    };

    $("#showDatasourceModal").on("hidden.bs.modal", function(){
        setMainPropertiesActiveTab();
    });
    
    //set input mask
    //for IP
//    $(document).ready(function(){
//        $("#inputIP").inputmask({alias: "ip"});
//    });

}]);