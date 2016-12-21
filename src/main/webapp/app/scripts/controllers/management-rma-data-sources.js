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
    var MODEM_DIAL_TEL_LENGTH = 10;
    var DATA_SOURCE_TYPE_KEY_DEFAULT = "DEVICE";
    var RAW_CONNECTION_TYPE_DEFAULT = "SERVER";

    $rootScope.ctxId = "management_rma_data_sources_page";
    //ctrl variables
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.subscrUrl = "../api/rma";
    $scope.ctrlSettings.datasourcesUrl = $scope.ctrlSettings.subscrUrl+"/dataSources";
    $scope.ctrlSettings.datasourceTypesUrl = $scope.ctrlSettings.subscrUrl+"/dataSourceTypes";
    $scope.ctrlSettings.rawModemModelsUrl = $scope.ctrlSettings.subscrUrl + "/dataSources/rawModemModels";
    $scope.ctrlSettings.DEVICE_MODES = [
        {
            caption: "Сервер",
            keyname: "SERVER"
        },
        {
            caption: "Клиент",
            keyname: "CLIENT"
        }
    ];
    
//    $scope.ctrlSettings.inputIpComplete = false;
    //data initial 
    $scope.data = {};
    $scope.data.dataSources = [];
    $scope.data.dataSourcesTypes = [];
    $scope.data.rawModemModels = [];
    $scope.data.currentObject = {};
//    $scope.data.currentObject.dataSourceTypeKey = "DEVICE";
    
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
            mainSvc.sortItemsBy($scope.data.dataSources, 'dataSourceName');
        },
              function(e){
            console.log(e);
        });
    };
    
    getDatasources($scope.ctrlSettings.datasourcesUrl);
    
    function findModemIdentity(mmId){
        if ($scope.data.rawModemModels.length == 0)
            return null;
        var result = null;
        $scope.data.rawModemModels.some(function(model){
            if (model.id == mmId){
                result = model;
                return true;
            }
        })
        return result;
    }
    
    $scope.changeModemModel = function(){
        var modemModel = findModemIdentity($scope.data.currentObject.rawModemModelId);
        $scope.data.currentObject.rawModemIdentity = modemModel.rawModemModelIdentity;        
        $scope.data.currentObject.rawModemDialupAvailable = modemModel.isDialup;
    }
    
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
        if (mainSvc.checkUndefinedNull($scope.data.currentObject.rawModemIdentity) &&
            !mainSvc.checkUndefinedNull($scope.data.currentObject.rawModemModelId)){
            var modemModel = findModemIdentity($scope.data.currentObject.rawModemModelId);
            $scope.data.currentObject.rawModemIdentity = modemModel.rawModemModelIdentity;
            $scope.data.currentObject.rawModemDialupAvailable = modemModel.isDialup;
        }
    };
    
    function getRawModemModels(){
        $http.get($scope.ctrlSettings.rawModemModelsUrl).then(function(resp){            
            $scope.data.rawModemModels = resp.data;
        }, errorCallback)
    }
    getRawModemModels();
    
    $scope.addDatasource = function(){
        $scope.data.currentObject = {};
        $scope.data.currentObject.dataSourceTypeKey = DATA_SOURCE_TYPE_KEY_DEFAULT;
        $scope.data.currentObject.rawConnectionType = RAW_CONNECTION_TYPE_DEFAULT;
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
        $scope.data.currentObject.dataSourceTypeKey = DATA_SOURCE_TYPE_KEY_DEFAULT;
        $scope.data.currentObject.rawConnectionType = RAW_CONNECTION_TYPE_DEFAULT;
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
    
    function checkDatasource(dsource){
        var checkDsourceFlag = true;
        if (angular.isUndefined(dsource.dataSourceName) || (dsource.dataSourceName === null) || (dsource.dataSourceName === "")){
            notificationFactory.errorInfo("Ошибка","Не задано имя источника данных.");
            checkDsourceFlag = false;
        };
        
        if (dsource.dataSourceTypeKey === 'MANUAL') {
            return checkDsourceFlag;
        }
        
        if ((dsource.rawConnectionType != 'CLIENT') && (!$scope.checkPositiveNumberValue(dsource.dataSourcePort))){
            notificationFactory.errorInfo("Ошибка","Не корректно задан порт источника данных.");
            checkDsourceFlag = false;
        };        
        
//        if (!$("#inputIP").inputmask("isComplete")){
        if ((dsource.rawConnectionType != 'CLIENT') && (angular.isUndefined(dsource.dataSourceIp) || (dsource.dataSourceIp === null) || (dsource.dataSourceIp === ""))){
            notificationFactory.errorInfo("Ошибка","Не заполнен ip \\ hostname источника данных.");
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
        
        
        if (dsource.dataSourceTypeKey == 'DEVICE' && dsource.rawConnectionType == 'CLIENT'){           
            if (mainSvc.checkUndefinedNull(dsource.rawModemModelId)){
                notificationFactory.errorInfo("Ошибка","Не задана модель модема");
                checkDsourceFlag = false;
            };
        };
        if (dsource.dataSourceTypeKey == 'DEVICE' && dsource.rawConnectionType == 'CLIENT' && dsource.rawModemDialEnable == true){           
            if (mainSvc.checkUndefinedNull(dsource.rawModemDialTel) || dsource.rawModemDialTel.length < MODEM_DIAL_TEL_LENGTH){
                notificationFactory.errorInfo("Ошибка","Не корректно задан телефонный номер модема");
                checkDsourceFlag = false;
            };
        };
        if (dsource.dataSourceTypeKey == 'DEVICE' && dsource.rawConnectionType == 'CLIENT' && dsource.rawModemIdentity == 'SERIAL_NR'){
            if (mainSvc.checkUndefinedNull(dsource.rawModemSerial) || dsource.rawModemSerial.length == 0){
                notificationFactory.errorInfo("Ошибка","Не корректно задан серийный номер модема");
                checkDsourceFlag = false;
            };
        };
        if (dsource.dataSourceTypeKey == 'DEVICE' && dsource.rawConnectionType == 'CLIENT' && dsource.rawModemIdentity == 'MAC_ADDR'){
            if (mainSvc.checkUndefinedNull(dsource.rawModemMacAddr) || dsource.rawModemMacAddr.length == 0){
                notificationFactory.errorInfo("Ошибка","Не корректно задан MAC-адрес модема");
                checkDsourceFlag = false;
            };
        };
        if (dsource.dataSourceTypeKey == 'DEVICE' && dsource.rawConnectionType == 'CLIENT' && dsource.rawModemIdentity == 'IMEI'){
            if (mainSvc.checkUndefinedNull(dsource.rawModemImei) || dsource.rawModemImei.length == 0){
                notificationFactory.errorInfo("Ошибка","Не корректно задан IMEI модема");
                checkDsourceFlag = false;
            };
        };
        return checkDsourceFlag;
    }
    
    $scope.saveDatasource = function(dsource){ 
        var checkDsourceFlag = checkDatasource(dsource);

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
    
    $scope.isTestMode = function () {
        return mainSvc.isTestMode();
    }
    
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
    
    var setMainPropertiesActiveTab = function () {
        var tab = null;
        tab = document.getElementById('con_properties_tab');
        if (!mainSvc.checkUndefinedNull(tab)) {
            tab.classList.remove("active");
        }
        tab = document.getElementById('con_properties');
        if (!mainSvc.checkUndefinedNull(tab)) {
            tab.classList.remove("active");
        }
        tab = document.getElementById('ex_properties_tab');
        if (!mainSvc.checkUndefinedNull(tab)) {
            tab.classList.remove("active");
        }
        tab = document.getElementById('ex_properties');
        if (!mainSvc.checkUndefinedNull(tab)) {
            tab.classList.remove("active");
        }
        
        tab = document.getElementById("main_properties_tab");
        if (!mainSvc.checkUndefinedNull(tab)) {
            tab.classList.add("active");
        }
        tab = document.getElementById("main_properties");
        if (!mainSvc.checkUndefinedNull(tab)) {
            tab.classList.add("in");
            tab.classList.add("active");
        }
    };

    $("#showDatasourceModal").on("hidden.bs.modal", function () {
        setMainPropertiesActiveTab();
    });
    
    //set input mask
    //for IP
//    $(document).ready(function(){
//        $("#inputIP").inputmask({alias: "ip"});
//    });

}]);