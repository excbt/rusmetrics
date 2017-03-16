/*jslint node: true, white: true, nomen: true, eqeq: true*/
/*global angular, $, alert, moment*/
'use strict';
angular.module('portalNMC')
.controller('SettingsMeterPeriodSettingCtrl', ['$scope', '$rootScope', '$routeParams', '$resource', '$cookies', '$compile', '$parse', 'crudGridDataFactory', 'notificationFactory', '$http', 'objectSvc', 'mainSvc', '$timeout', '$window' , 'meterPeriodsSvc', function ($scope, $rootScope, $routeParams, $resource, $cookies, $compile, $parse, crudGridDataFactory, notificationFactory, $http, objectSvc, mainSvc, $timeout, $window, meterPeriodsSvc) {
    $rootScope.ctxId = "management_rma_meter_period_setting_page";
    
    //var METER_PERIOD_SETTING_URl = "../api/subscr/meter-period-settings";
    
    $scope.extraProps = {"idColumnName" : "id", "defaultOrderBy" : "name", "nameColumnName" : "name"};
    $scope.orderBy = { field: $scope.extraProps.defaultOrderBy, asc: true};
    
    $scope.ctrlSettings = {};
        //model columns
    $scope.ctrlSettings.periodColumns = [
        {
            "name": "name",
            "caption": "Название",
            "class": "col-xs-3",
            "type": "name"
        },
        {
            "name": "description",
            "caption": "Описание",
            "class": "col-xs-3"
        },
        {
            "name": "fromDay",
            "caption": "Начало периода",
            "class": "col-xs-1"
        },
        {
            "name": "toDay",
            "caption": "Конец периода",
            "class": "col-xs-1"
        },
        {
            "name": "valueCount",
            "caption": "Количество показаний",
            "class": "col-xs-1"
        }
    ];
    
    $scope.data = {};
    $scope.data.meterPeriodSettings = [];
    $scope.data.currentMeterPeriodSetting = {};
    
    var setConfirmCode = function(){
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode();
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;                    
    };
    
    function errorCallback(e) {
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
        console.log(e);
    }
    
    function successGetCallback(resp) {
        console.log(resp);
        var tmp = angular.copy(resp.data);
        mainSvc.sortItemsBy(tmp, $scope.extraProps.defaultOrderBy);
        $scope.data.meterPeriodSettings = tmp;
    }
    
    function successSaveCallback(resp) {
        console.log(resp);
        var tmpArr = angular.copy($scope.data.meterPeriodSettings);
        if (resp.status === 201) {//created response            
            tmpArr.push(angular.copy(resp.data));
            mainSvc.sortItemsBy(tmpArr, $scope.extraProps.defaultOrderBy);
            $scope.data.meterPeriodSettings = tmpArr;
        }
        if (resp.status === 200) {//updated response            
            var changedItem = mainSvc.findItemBy(tmpArr, $scope.extraProps.idColumnName, resp.data.id);
            var index = tmpArr.indexOf(changedItem);
            if (index !== -1) {
                tmpArr[index] = angular.copy(resp.data);
            }
            mainSvc.sortItemsBy(tmpArr, $scope.extraProps.defaultOrderBy);
            $scope.data.meterPeriodSettings = tmpArr;
        }
        $('#showMeterPeriodSettingModal').modal('hide');        
    }    
    
    function loadMeterPeriodSetting() {
//        var url = METER_PERIOD_SETTING_URl;
//        $http.get(url).then(successGetCallback, errorCallback);
        meterPeriodsSvc.loadMeterPeriods().then(successGetCallback, errorCallback);
    }
    
    function successDeleteCallback(resp) {
        console.log(resp);
        loadMeterPeriodSetting();
        $('#deleteWindowModal').modal('hide');        
    }
    
    $scope.checkPeriod = function(obj) {
        return (Number(obj.fromDay) > Number(obj.toDay));
    };
    
    function checkData(obj) {
        if (angular.isUndefined(obj) || (obj === null) ) {
            notificationFactory.errorInfo("Ошибка", "Период не заполнен. Проверьте введенные данные!");
            return false;
        }
        var result = true,
            fromDayIsValid = true,
            toDayIsValid = true;
        
        if (angular.isUndefined(obj.name) || (obj.name === null) || (obj.name === "")) {
            notificationFactory.errorInfo("Ошибка", "Не задано название периода!");            
            result = false;
        }
        
        if (angular.isUndefined(obj.fromDay) || (obj.fromDay === null) || (obj.fromDay === "")) {
            notificationFactory.errorInfo("Ошибка", "Не задано начало периода!");
            fromDayIsValid = false;
            result = false;
        }
        
        if (angular.isUndefined(obj.toDay) || (obj.toDay === null) || (obj.toDay === "")) {
            notificationFactory.errorInfo("Ошибка", "Не задан конец периода!");
            toDayIsValid = false;
            result = false;
        }
        
        if (fromDayIsValid && toDayIsValid && $scope.checkPeriod(obj)) {
            notificationFactory.errorInfo("Ошибка", "Некорректно задан период!");
            result = false;
        }
        
        return result;
    }
        
    $scope.saveMeterPeriodSetting = function() {
        if (checkData($scope.data.currentMeterPeriodSetting) === false){
            return false;
        }        
        var /*url = METER_PERIOD_SETTING_URl,*/
            rmethod = "PUT";
        if (angular.isUndefined($scope.data.currentMeterPeriodSetting.id) || $scope.data.currentMeterPeriodSetting.id === null ) {
            rmethod = "POST";
        }
        meterPeriodsSvc.saveMeterPeriod(rmethod, $scope.data.currentMeterPeriodSetting).then(successSaveCallback, errorCallback);
//        $http(
//            {
//                method: rmethod,
//                url: url, 
//                data: $scope.data.currentMeterPeriodSetting
//            }).then(successSaveCallback, errorCallback);
    };
    
    $scope.addMeterPeriod = function() {
        $scope.data.currentMeterPeriodSetting = {};
        $('#showMeterPeriodSettingModal').modal();
    };
    
    $scope.selectedItem = function (item) {
        setConfirmCode();
        $scope.data.currentMeterPeriodSetting = angular.copy(item);        
    };
    
    $scope.deleteMeterPeriodSetting = function(mps) {
        meterPeriodsSvc.deleteMeterPeriod(mps.id).then(successDeleteCallback, errorCallback);
//        $http.delete(METER_PERIOD_SETTING_URl + "/" + mps.id).then(successDeleteCallback, errorCallback);
    };
    
        // Проверка пользователя - системный/ не системный
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
    
    $scope.isTestMode = function () {
        return mainSvc.isTestMode();
    };
    
    $scope.isSystemViewInfo = function () {
        return mainSvc.getViewSystemInfo();
    };
    
    function initCtrl() {
        loadMeterPeriodSetting();
    }
    
    initCtrl();
    
    $('#showMeterPeriodSettingModal').on('shown.bs.modal', function () {                    
        $('#inputMeterPeriodName').focus();
        $('#inputMeterPeriodFromDay').inputmask('integer', {mask: '9[9]', min: 1, max: 31});
        $('#inputMeterPeriodToDay').inputmask('integer', {mask: '9[9]', min: 1, max: 31});
        $('#inputMeterPeriodValueCount').inputmask('integer', {max: 5});        
    });

}]);
