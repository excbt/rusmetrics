'use strict';
angular.module('portalNMC')
.controller('SettingsProgramCtrl', function($rootScope, $scope, $http, notificationFactory, mainSvc, $timeout){
//console.log("Run SettingsNoticeCtrl");
//    Test data *************************************
    $scope.testData = {};
    $scope.testData.smsSetting = {
        isActive: true,
        subscrPrefCategory: "SUBSCR_SMS_PREF",
        subscrPref: {
            caption: "URL сервиса для отправки СМС",
            isActiveCaption: "Использовать рассылку СМС",
            placeholder: "https://service/send.php?login=<Логин>&psw=<Пароль>&phones=##PHONE##&mes=##MSG##",
            keyname: "SMS_PREF",
            prefName: "SMS_PREF"            
        },
        dayCountValue: 20,
        hourCountValue: 10
    };
//    end Test data ***************************************
    
    var SMS_COUNT_LIMIT = 100; //client requirement
    var DAY_SMS_COUNT_DEFAULT = 20;
    var HOUR_SMS_COUNT_DEFAULT = 10;
    
    $rootScope.ctxId = "program_tree_settings_page";
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.settingsUrl = "../api/subscr/subscrPrefValues";
    $scope.ctrlSettings.isSaving = false;
    
    $scope.data = {};
    $scope.data.originalSettings = [];
    $scope.data.modifySettings = [];
    
    $scope.data.items = [];
    $scope.data.items.trees = [
        {
            name: "Дер1"
        },
        {
            name: "Дер2"
        }
    ];
    
    //callbacks
    var errorCallback = function(e){
        $scope.ctrlSettings.isSaving = false;
        
        console.log(e);              
//        notificationFactory.errorInfo(e.statusText, e.data.description || e.data || e);
//        console.log(e);
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
    
    function prepareSettingsView(){
        $timeout(function(){
            $("#inputHourCountValue").inputmask();
            $("#inputDayCountValue").inputmask();            
            $("#inputMapValueSUBSCR_ZOOM_MAP_PREF").inputmask();
            $("#inputMapValueSUBSCR_LNG_MAP_PREF").inputmask();
            $("#inputMapValueSUBSCR_LAT_MAP_PREF").inputmask();
        });
    }
    
    $scope.setActiveFlagForSmsSetting = function(value){
        $scope.data.modifySettings.forEach(function(elem){
            if (elem.subscrPrefKeyname == 'SUBSCR_DAY_COUNT_SMS_PREF' || elem.subscrPrefKeyname == 'SUBSCR_HOUR_COUNT_SMS_PREF'){
                elem.isActive = value;                
            }
        })
    };
    
    $scope.setActiveFlagForMapSetting = function(value){
        $scope.data.modifySettings.forEach(function(elem){
            if (elem.subscrPrefKeyname == 'SUBSCR_LNG_MAP_PREF' || elem.subscrPrefKeyname == 'SUBSCR_LAT_MAP_PREF' || elem.subscrPrefKeyname == 'SUBSCR_ZOOM_MAP_PREF'){
                elem.isActive = value;                
            }
        })
    };
    
    var performSettingsData = function(data){
        if (angular.isArray(data)){
            data.forEach(function(setting){               
                if (mainSvc.isNumeric(setting.value)){
                    setting.value = Number(setting.value);
                };
//                //if "SUBSCR_SMS_PREF_TYPE"
//                if (setting.subscrPrefCategory = "SUBSCR_SMS_PREF_TYPE"){
//                    
//                }
            });
//            data.push($scope.testData.smsSetting);
        };        
        $scope.data.originalSettings = angular.copy(data);
        $scope.data.modifySettings = angular.copy(data);
        
        prepareSettingsView();
        
    };
    
    //get all settings
    var getProgramSettings = function(){
        $http.get($scope.ctrlSettings.settingsUrl)
        .success(function(data){            
            if (angular.isArray(data)){
                data.forEach(function(setting){
                    if (setting.subscrPrefCategory == "SUBSCR_OBJECT_TREE"){
                        getTreesForSetting(setting.subscrPrefKeyname);
                    }
                });
            };
            performSettingsData(data);
        })
        .error(errorCallback);
    };    
    
    var getTreesForSetting = function(prefKey){
        if (mainSvc.checkUndefinedNull(prefKey)){
            console.log("subscrPrefKey is undefined or null");
            return false;
        };
        var url = $scope.ctrlSettings.settingsUrl + "/objectTreeTypes?subscrPrefKeyname=" + prefKey;
        $http.get(url)
        .success(function(data){
            $scope.data.items[prefKey] = angular.copy(data);
        })
        .error(errorCallback);
    };
    
    function checkSMSUrlAndViewMessage(urlStr){        
        var result = true; //(urlStr.indexOf("##PHONE##") > -1) && (urlStr.indexOf("##MSG##") > -1);
        if (mainSvc.checkUndefinedNull(urlStr) || urlStr == ""){
            notificationFactory.errorInfo("Внимание", "Поле \"URL сервиса для отправки СМС\" незаполнено!");
            return false;
        };
        if (urlStr.indexOf("##PHONE##") <= -1){
            notificationFactory.errorInfo("Внимание", "В url адресе сервиса отсутствует '##PHONE##' - указатель на место подстановки телефонного номера.");
            result = false;
        };
        if (urlStr.indexOf("##MSG##") <= -1){            
            notificationFactory.errorInfo("Внимание", "В url адресе сервиса отсутствует '##MSG##' - указатель на место подстановки текста сообщения.");            
            result = false;
        };        
        return result;
    }
    
    $scope.checkSMSUrl = function(urlStr){        
        var result = true; //(urlStr.indexOf("##PHONE##") > -1) && (urlStr.indexOf("##MSG##") > -1);
        if (mainSvc.checkUndefinedNull(urlStr) || urlStr == ""){
            return false;
        };
        if (urlStr.indexOf("##PHONE##") <= -1){            
            result = false;
        };
        if (urlStr.indexOf("##MSG##") <= -1){                      
            result = false;
        };        
        return result;
    }
    
    $scope.checkAttemptionCount = function(value){
        var result = true;
        if (!mainSvc.isNumeric(value) || value <= 0 || value > SMS_COUNT_LIMIT){
            result = false;
        }
        return result;
    }
    
    function checkAttemptionCountAndViewMessage(smsSetting){
        var result = true;
        if (!mainSvc.isNumeric(smsSetting.value)){
            notificationFactory.errorInfo("Ошибка", "Для \"" + smsSetting.subscrPref.caption + "\" введено не числовое значение.");
            result = false;
        };
        if (smsSetting.value <= 0){
            notificationFactory.errorInfo("Ошибка. Некорректно задано \"" + smsSetting.subscrPref.caption + "\"", "Введенное значение меньше или равно нулю, а должно быть в интервале от 1 до " + SMS_COUNT_LIMIT);
            result = false;
        };
        if (smsSetting.value > SMS_COUNT_LIMIT){
            notificationFactory.errorInfo("Ошибка. Некорректно задано \"" + smsSetting.subscrPref.caption + "\"", "Введенное значение больше " + SMS_COUNT_LIMIT + ", а должно быть в интервале от 1 до " + SMS_COUNT_LIMIT);
            result = false;
        }
        return result;
    }
    
    function checkAttemptionCountsAndViewMessage(smsSetting){
        return checkAttemptionCountAndViewMessage(smsSetting["SUBSCR_DAY_COUNT_SMS_PREF"]) & checkAttemptionCountAndViewMessage(smsSetting["SUBSCR_HOUR_COUNT_SMS_PREF"]);
    }
    
    function findSmsSettings(){
        var result = {};
        $scope.data.modifySettings.forEach(function(setting){
            if (setting.subscrPrefCategory == "SUBSCR_SMS_PREF_TYPE"){
                result[setting.subscrPrefKeyname] = setting;                
            }                
        });
        return result;
    }
    
    $scope.checkPositiveNumberValue = function(value){        
        return mainSvc.checkPositiveNumberValue(value);
    }
    
    function checkSettings(){
        var result = true;
        var smsSetting = findSmsSettings();
        if (mainSvc.checkUndefinedNull(smsSetting) || !smsSetting["SUBSCR_SMS_PREF"].isActive){
            return result;
        };
        return checkSMSUrlAndViewMessage(smsSetting["SUBSCR_SMS_PREF"].value) & checkAttemptionCountsAndViewMessage(smsSetting);
    }
    
    $scope.saveSettings = function(){
        //check settings
        var checkFlag = checkSettings();
        if (checkFlag == false){           
            return false;
        };
//        $scope.data.modifySettings.pop();//remove test SMS setting
        
        $scope.ctrlSettings.isSaving = true;
        $http.put($scope.ctrlSettings.settingsUrl, $scope.data.modifySettings)
        .success(function(data){
            $scope.ctrlSettings.isSaving = false;
            notificationFactory.success();
            performSettingsData(data);
        })
        .error(errorCallback);
    };
    
    $scope.cancelSettings = function(){
        $scope.data.modifySettings = angular.copy($scope.data.originalSettings);
        prepareSettingsView();
    };
    
    $scope.setOrderBy = function(column){
        $scope.ctrlSettings.orderBy.field = column;
        $scope.ctrlSettings.orderBy.asc = !$scope.ctrlSettings.orderBy.asc;
    };
    
    $scope.selectItem = function(item){
        $scope.currentNotice = angular.copy(item);
    };
    
    $scope.isAdmin = function(){
        return mainSvc.isAdmin();
    };
    
    $scope.isDisabled = function(){
        return !$scope.isAdmin() || $scope.ctrlSettings.isSaving == true;
    };
    
    var initCtrl = function(){
        getProgramSettings();
    };    
    initCtrl();

});