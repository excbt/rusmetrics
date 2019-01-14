/*jslint node: true, eqeq: true, nomen: true*/
/*global angular, $, moment, alert*/
'use strict';
var app = angular.module('portalNMC');
app.controller('SettingsProgramCtrl', ['$rootScope', '$scope', '$http', 'notificationFactory', 'mainSvc', '$timeout', function ($rootScope, $scope, $http, notificationFactory, mainSvc, $timeout) {
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
    var errorCallback = function (e) {
        $scope.ctrlSettings.isSaving = false;
        
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    function setQtip(btnId, text) {
        $('#prefHelpButton' + btnId).qtip({
            suppress: false,
            content: {
                text: text,
                button : true
            },
            show: {
                event: 'click'
            },
            hide: {
                event: 'unfocus'
            },
            style: {
                classes: 'qtip-nmc-indicator-tooltip',
                width: 1000
            },
            position: {
                my: 'bottom left',
                at: 'top right',
                target: $('#prefHelpButton' + btnId)
            }
        });
    }
    
    function prepareSettingsView() {
        $timeout(function () {
            $scope.data.modifySettings.forEach(function (setting) {
                if (!mainSvc.checkUndefinedNull(setting.subscrPref.prefDescription)) {
                    setQtip(setting.id, setting.subscrPref.prefDescription);
                }
            });
            $("#inputHourCountValue").inputmask();
            $("#inputDayCountValue").inputmask();
            $("#inputMapValueSUBSCR_ZOOM_MAP_PREF").inputmask();
            $("#inputMapValueSUBSCR_LNG_MAP_PREF").inputmask();
            $("#inputMapValueSUBSCR_LAT_MAP_PREF").inputmask();
        });
    }
    
    $scope.setActiveFlagForSmsSetting = function (value) {
        $scope.data.modifySettings.forEach(function (elem) {
            if (elem.subscrPrefKeyname == 'SUBSCR_DAY_COUNT_SMS_PREF' || elem.subscrPrefKeyname == 'SUBSCR_HOUR_COUNT_SMS_PREF') {
                elem.isActive = value;
            }
        });
    };
    
    $scope.setActiveFlagForMapSetting = function (value) {
        $scope.data.modifySettings.forEach(function (elem) {
            if (elem.subscrPrefKeyname == 'SUBSCR_LNG_MAP_PREF' || elem.subscrPrefKeyname == 'SUBSCR_LAT_MAP_PREF' || elem.subscrPrefKeyname == 'SUBSCR_ZOOM_MAP_PREF') {
                elem.isActive = value;
            }
        });
    };
    
    var performSettingsData = function (data) {
        if (angular.isArray(data)) {
            data.forEach(function (setting) {
                if (mainSvc.isNumeric(setting.value)) {
                    setting.value = Number(setting.value);
                }
                
//                //if "SUBSCR_SMS_PREF_TYPE"
//                if (setting.subscrPrefCategory = "SUBSCR_SMS_PREF_TYPE"){
//                    
//                }
            });
            
//            data.push($scope.testData.smsSetting);
        }
        $scope.data.originalSettings = angular.copy(data);
        $scope.data.modifySettings = angular.copy(data);
        
        prepareSettingsView();
        
    };
    
    //get all settings
    var getProgramSettings = function () {
        $http.get($scope.ctrlSettings.settingsUrl)
            .then(function (resp) {
                var data = resp.data;
                if (angular.isArray(data)) {
                    data.forEach(function (setting) {
                        if (setting.subscrPrefCategory == "SUBSCR_OBJECT_TREE") {
                            getTreesForSetting(setting.subscrPrefKeyname);
                        }
                    });
                }
                performSettingsData(data);
            }, errorCallback);
    };
    
    var getTreesForSetting = function (prefKey) {
        if (mainSvc.checkUndefinedNull(prefKey)) {
            console.log("subscrPrefKey is undefined or null");
            return false;
        }
        var url = $scope.ctrlSettings.settingsUrl + "/objectTreeTypes?subscrPrefKeyname=" + prefKey;
        $http.get(url)
            .then(function (resp) {                
                $scope.data.items[prefKey] = angular.copy(resp.data);
            }, errorCallback);
    };
    
    function checkSMSUrlAndViewMessage(urlStr) {
        var result = true; //(urlStr.indexOf("##PHONE##") > -1) && (urlStr.indexOf("##MSG##") > -1);
        if (mainSvc.checkUndefinedNull(urlStr) || urlStr == "") {
            notificationFactory.errorInfo("Внимание", "Поле \"URL сервиса для отправки СМС\" незаполнено!");
            return false;
        }
        if (urlStr.indexOf("##PHONE##") <= -1) {
            notificationFactory.errorInfo("Внимание", "В url адресе сервиса отсутствует '##PHONE##' - указатель на место подстановки телефонного номера.");
            result = false;
        }
        if (urlStr.indexOf("##MSG##") <= -1) {
            notificationFactory.errorInfo("Внимание", "В url адресе сервиса отсутствует '##MSG##' - указатель на место подстановки текста сообщения.");
            result = false;
        }
        return result;
    }
    
    $scope.checkSMSUrl = function (urlStr) {
        var result = true; //(urlStr.indexOf("##PHONE##") > -1) && (urlStr.indexOf("##MSG##") > -1);
        if (mainSvc.checkUndefinedNull(urlStr) || urlStr == "") {
            return false;
        }
        if (urlStr.indexOf("##PHONE##") <= -1) {
            result = false;
        }
        if (urlStr.indexOf("##MSG##") <= -1) {
            result = false;
        }
        return result;
    };
    
    $scope.checkAttemptionCount = function (value) {
        var result = true;
        if (!mainSvc.isNumeric(value) || value <= 0 || value > SMS_COUNT_LIMIT) {
            result = false;
        }
        return result;
    };
    
    function checkAttemptionCountAndViewMessage(smsSetting) {
        var result = true;
        if (!mainSvc.isNumeric(smsSetting.value)) {
            notificationFactory.errorInfo("Ошибка", "Для \"" + smsSetting.subscrPref.caption + "\" введено не числовое значение.");
            result = false;
        }
        if (smsSetting.value <= 0) {
            notificationFactory.errorInfo("Ошибка. Некорректно задано \"" + smsSetting.subscrPref.caption + "\"", "Введенное значение меньше или равно нулю, а должно быть в интервале от 1 до " + SMS_COUNT_LIMIT);
            result = false;
        }
        if (smsSetting.value > SMS_COUNT_LIMIT) {
            notificationFactory.errorInfo("Ошибка. Некорректно задано \"" + smsSetting.subscrPref.caption + "\"", "Введенное значение больше " + SMS_COUNT_LIMIT + ", а должно быть в интервале от 1 до " + SMS_COUNT_LIMIT);
            result = false;
        }
        return result;
    }
    
    function checkAttemptionCountsAndViewMessage(smsSetting) {
        return checkAttemptionCountAndViewMessage(smsSetting["SUBSCR_DAY_COUNT_SMS_PREF"]) & checkAttemptionCountAndViewMessage(smsSetting["SUBSCR_HOUR_COUNT_SMS_PREF"]);
    }
    
    function findSmsSettings() {
        var result = {};
        $scope.data.modifySettings.forEach(function (setting) {
            if (setting.subscrPrefCategory == "SUBSCR_SMS_PREF_TYPE") {
                result[setting.subscrPrefKeyname] = setting;
            }
        });
        return result;
    }
    
    function findMapSettings() {
        var result = {};
        $scope.data.modifySettings.forEach(function (setting) {
            if (setting.subscrPrefCategory == "SUBSCR_MAP_PREF_TYPE") {
                result[setting.subscrPrefKeyname] = setting;
            }
        });
        return result;
    }
    
    $scope.checkPositiveNumberValue = function (value) {
        return mainSvc.checkPositiveNumberValue(value);
    };
    
    $scope.isNumeric = function (value) {
        return mainSvc.isNumeric(value);
    };
    
    function checkNumericValueAndViewMessage(setting) {
        var result = true;
        if (!mainSvc.isNumeric(setting.value)) {
            notificationFactory.errorInfo("Ошибка", "Для \"" + setting.subscrPref.caption + "\" введено не числовое значение.");
            result = false;
        }
        return result;
    }
    
    function checkPositiveNumberValueAndViewMessage(setting) {
        var result = true;
        if (!mainSvc.isNumeric(setting.value)) {
            notificationFactory.errorInfo("Ошибка", "Для \"" + setting.subscrPref.caption + "\" введено не числовое значение.");
            result = false;
        }
        if (setting.value < 0) {
            notificationFactory.errorInfo("Ошибка. Некорректно задано \"" + setting.subscrPref.caption + "\"", "Введенное значение меньше нуля");
            result = false;
        }
        return result;
    }
    
    function checkMapSettingsAndViewMessage(mapSetting) {
        return checkPositiveNumberValueAndViewMessage(mapSetting["SUBSCR_ZOOM_MAP_PREF"]) & checkNumericValueAndViewMessage(mapSetting["SUBSCR_LNG_MAP_PREF"]) & checkNumericValueAndViewMessage(mapSetting["SUBSCR_LAT_MAP_PREF"]);
    }
    
    function smsSettingIsOn(smsSetting) {
        return !mainSvc.checkUndefinedNull(smsSetting) && smsSetting["SUBSCR_SMS_PREF"].isActive == true;
    }
    
    function mapSettingIsOn(mapSetting) {
        return !mainSvc.checkUndefinedNull(mapSetting) && mapSetting["SUBSCR_MAP_PREF"].isActive == true;
    }
    
    function checkSettings() {
        var result = true;
        var smsSetting = findSmsSettings();
        var mapSetting = findMapSettings();
        //TODO: divide check sms and map settings
        if (!smsSettingIsOn(smsSetting) && !mapSettingIsOn(mapSetting)) {
            return result;
        }
        var checkSmsSettings = true;
        if (smsSettingIsOn(smsSetting)) {
            checkSmsSettings = checkSMSUrlAndViewMessage(smsSetting["SUBSCR_SMS_PREF"].value) & checkAttemptionCountsAndViewMessage(smsSetting);
        }
        var checkMapSetting = true;
        if (mapSettingIsOn(mapSetting)) {
            checkMapSetting = checkMapSettingsAndViewMessage(mapSetting);
        }
        return checkSmsSettings && checkMapSetting;
    }
    
    $scope.saveSettings = function () {
        //check settings
        var checkFlag = checkSettings();
        if (checkFlag == false) {
            return false;
        }
//        $scope.data.modifySettings.pop();//remove test SMS setting
        
        $scope.ctrlSettings.isSaving = true;
        $http.put($scope.ctrlSettings.settingsUrl, $scope.data.modifySettings)
            .then(function (resp) {
                $scope.ctrlSettings.isSaving = false;
                notificationFactory.success();
                performSettingsData(resp.data);
            }, errorCallback);
    };
    
    $scope.cancelSettings = function () {
        $scope.data.modifySettings = angular.copy($scope.data.originalSettings);
        prepareSettingsView();
    };
    
    $scope.setOrderBy = function (column) {
        $scope.ctrlSettings.orderBy.field = column;
        $scope.ctrlSettings.orderBy.asc = !$scope.ctrlSettings.orderBy.asc;
    };
    
    $scope.selectItem = function (item) {
        $scope.currentNotice = angular.copy(item);
    };
    
    $scope.isAdmin = function () {
        return mainSvc.isAdmin();
    };
    
    $scope.isDisabled = function () {
        return !$scope.isAdmin() || $scope.ctrlSettings.isSaving == true;
    };
    
    $scope.isSystemViewInfo = function () {
        return mainSvc.getViewSystemInfo();
    };
    
    var initCtrl = function () {
        getProgramSettings();
    };
    initCtrl();

}]);