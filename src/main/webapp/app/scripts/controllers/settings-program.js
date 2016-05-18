'use strict';
angular.module('portalNMC')
.controller('SettingsProgramCtrl', function($rootScope, $scope, $http, notificationFactory, mainSvc){
//console.log("Run SettingsNoticeCtrl");
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
    
    var performSettingsData = function(data){
        if (angular.isArray(data)){
            data.forEach(function(setting){               
                if (mainSvc.isNumeric(setting.value)){
                    setting.value = Number(setting.value);
                };
            });
        };            
        $scope.data.originalSettings = angular.copy(data);
        $scope.data.modifySettings = angular.copy(data);                        
    };
    
    //get all settings
    var getProgramSettings = function(){
        $http.get($scope.ctrlSettings.settingsUrl)
        .success(function(data){
            if (angular.isArray(data)){
                data.forEach(function(setting){
                    getTreesForSetting(setting.subscrPrefKeyname);
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
    
    $scope.saveSettings = function(){
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