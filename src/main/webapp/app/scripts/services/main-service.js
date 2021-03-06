/*jslint node: true, nomen: true, eqeq: true*/
/*global angular, $, moment*/
'use strict';
//Service decides common tasks for all portal

var app = angular.module('portalNMC');
app.service('mainSvc', ['$cookies', '$http', '$rootScope', '$log', 'objectSvc', 'monitorSvc', '$q', '$timeout', function ($cookies, $http, $rootScope, $log, objectSvc, monitorSvc, $q, $timeout) {
    
    var RADIX = 10;//for parseInt
    
    //Test special settings
    var isUseTest = true,
        isUseColorHighlightIndicatorData = true,
        isViewSystemInfo = true;
    
    function getUseTest() {
        return isUseTest;
    }
    
    function getUseColorHighlightIndicatorData() {
        return isUseColorHighlightIndicatorData;
    }
    
    function getViewSystemInfo() {
        return isViewSystemInfo;
    }
    
    var MAP_PREF = "SUBSCR_MAP_PREF",
        LNG_MAP_PREF = "SUBSCR_LNG_MAP_PREF",
        LAT_MAP_PREF = "SUBSCR_LAT_MAP_PREF",
        ZOOM_MAP_PREF = "SUBSCR_ZOOM_MAP_PREF";
    
//    $log.debug("Run main service. main service: row: 5");
    //set services settings
    var mainSvcSettings = {};
    mainSvcSettings.subscrUrl = "../api/subscr";
    mainSvcSettings.servicePermissionUrl = mainSvcSettings.subscrUrl + "/manage/service/permissions";
    mainSvcSettings.loadingServicePermissionFlag = false;
    mainSvcSettings.serverTimeZone = 3;//server time zone at Hours
//    mainSvcSettings.loadedServicePermission = null;
    mainSvcSettings.mapSettings = {};
    
    var prefUrl = mainSvcSettings.subscrUrl + '/subscrPrefValue';
    
    var contextIds = [];
    
    function checkUndefinedNull(numvalue) {
        var result = false;
        if ((angular.isUndefined(numvalue)) || (numvalue == null)) {
            result = true;
        }
        return result;
    }
    
    //main service request canceler 
    var requestCanceler = null;
    var httpOptions = null;
    
    function isCancelParamsIncorrect() {
        return checkUndefinedNull(requestCanceler) || checkUndefinedNull(httpOptions);
    }
    function getRequestCanceler() {
        return requestCanceler;
    }
    ////////////////////////////
    
    function checkEmptyObject(obj) {
        return Object.keys(obj).length === 0 && obj.constructor === Object;
    }
    
    var getContextIds = function () {
        return contextIds;
    };
    
    var getLoadingServicePermissionFlag = function () {
        return mainSvcSettings.loadingServicePermissionFlag;
    };
    
    var getLoadedServicePermission = function () {
        return mainSvcSettings.loadedServicePermission;
    };
    
    function getServerTimeZone() {
        return mainSvcSettings.serverTimeZone;
    }
    
    //object map
    var objectMapSettings = {};
    objectMapSettings.zoom = null;
    objectMapSettings.lat = null;
    objectMapSettings.lng = null;
    //monitor map
    var monitorMapSettings = {};
    monitorMapSettings.zoom = null;
    monitorMapSettings.lat = null;
    monitorMapSettings.lng = null;
    
    //main menu settings
    
    //setters and getters
    var getObjectMapSettings = function () {
        return objectMapSettings;
    };
    
    var getMonitorMapSettings = function () {
        return monitorMapSettings;
    };
    
    var setObjectMapSettings = function (mapSettings) {
        objectMapSettings = mapSettings;
    };
    var setMonitorMapSettings = function (mapSettings) {
        monitorMapSettings = mapSettings;
    };
    
    // callback for error-respond from server
    function errorCallbackConsole(e) {
        console.log(e);
    }
    
    //methods for the work with the dates
    var dateFormating = function (millisec, dateFormat) {
        var result = "";
        var serverTimeZoneDifferent = Math.round(mainSvcSettings.serverTimeZone * 3600.0 * 1000.0);
        var tmpDate = (new Date(millisec + serverTimeZoneDifferent));
        result = (tmpDate === null) ? "" : moment([tmpDate.getUTCFullYear(), tmpDate.getUTCMonth(), tmpDate.getUTCDate(), tmpDate.getUTCHours(), tmpDate.getUTCMinutes()]).format(dateFormat);
        return result;//
    };
        //get UTC time from the string with date
    var strDateToUTC = function (strWithDate, strFormat) {
        var stDate = (new Date(moment(strWithDate, strFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601      
        var result = (!isNaN(stDate.getTime())) ? Date.UTC(stDate.getFullYear(), stDate.getMonth(), stDate.getDate()) : null;
        return result;
    };
        
    var checkStrForDate = function (strWithDate) {
        //check date for format: DD/MM/YYYY
        if (/(0[1-9]|[12][0-9]|3[01])[\- \/.](0[1-9]|1[012])[\- \/.](19|20)\d\d/.test(strWithDate)) {
            return true;
        }
        return false;
    };
    
    function addTimeOffset(val, label) {
        var result = "";
        if (val > 0) {
            if (val < 10) {
                result += "0";
            }
            result += val + label;
        }
        return result;
    }

    function prepareTimeOffset(rawTimeOffset) {
        var result = null;
        if (!checkUndefinedNull(rawTimeOffset)) {
//console.log(rawTimeOffset);
            if (rawTimeOffset.timeDeltaSign === 1) {
                result = "+";
            } else {
                result = "-";
            }

            result += addTimeOffset(rawTimeOffset.years, "г ");
            result += addTimeOffset(rawTimeOffset.mons, "М ");
            result += addTimeOffset(rawTimeOffset.days, "д ");
            result += addTimeOffset(rawTimeOffset.hh, "ч ");
            result += addTimeOffset(rawTimeOffset.mm, "м ");
            result += addTimeOffset(rawTimeOffset.ss, "с ");

        }
        return result;
    }
    
    //date range settings
    var dateRangeOptsRu = {
        locale: {
            applyClass: 'btn-green',
            applyLabel: "Применить",
            fromLabel: "с",
            toLabel: "по",
            cancelLabel: 'Отмена',
            customRangeLabel: 'Период',
            daysOfWeek: [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
            firstDay: 1,
            monthNames: [ 'Январь', 'Февраль', 'Март', 'Апрель',
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                    'Октябрь', 'Ноябрь', 'Декабрь' ]
        },
        ranges: {
            'Текущий день' : [ moment().startOf('day'),
                    moment().endOf('day') ],
            'Посл 7 дней' : [
                moment().subtract(6, 'days').startOf('day'),
                moment().endOf('day') ],
            'Посл 30 дней' : [
                moment().subtract(29, 'days').startOf('day'),
                moment().endOf('day') ]
        },
        startDate : moment($cookies.get('fromDate')) || moment().subtract(6, 'days').startOf('day'),
        endDate : moment($cookies.get('toDate')) || moment().endOf('day'),

        format : 'DD.MM.YYYY',
        separator: " по "
    };
    
    var dateRangeMonitorOptsRu = dateRangeOptsRu;
    dateRangeMonitorOptsRu.startDate = moment().subtract(6, 'days').startOf('day');
    dateRangeMonitorOptsRu.endDate = moment().endOf('day');
    dateRangeMonitorOptsRu.maxDate = moment().endOf('day');
    
    var getDateRangeOptions = function (param, settings) {
        var result = null;
        switch (param) {
        case "ru":
            result = dateRangeOptsRu;
            break;
        case "monitor-ru":
            result = dateRangeMonitorOptsRu;
            break;
        case "indicator-ru":
            result = angular.copy(dateRangeMonitorOptsRu);
            result.startDate = (angular.isDefined($cookies.get('fromDate')) && ($cookies.get('fromDate') != null)) ? moment($cookies.get('fromDate')).startOf('day') : moment($rootScope.reportStart).startOf('day');
            result.endDate = (angular.isDefined($cookies.get('toDate')) && ($cookies.get('toDate') != null)) ? moment($cookies.get('toDate')).startOf('day') : moment($rootScope.reportEnd).startOf('day');
            if (!checkUndefinedNull(settings)) {
                if (!checkUndefinedNull(settings.minDate)) {
                    result.minDate = settings.minDate;
                }
            }
            break;
        }
        return result;
    };
    
//    var getDateRangeOptions = function(param, start, end){
//        var result = null;
//        switch (param){
//            case "ru": result=dateRangeOptsRu;break;
//            case "monitor-ru": result=dateRangeMonitorOptsRu;break;
//            case "indicator-ru": 
//                result = angular.copy(dateRangeMonitorOptsRu);
//                result.startDate = moment(start).startOf('day');
//                result.endDate = moment(end).endOf('day');
//                break;
//        };
//        return result;
//    };
    
    //work with date picker
    
    var datePickerSettingsFullView = {
        dateFormat: "dd.mm.yy",
        firstDay: dateRangeOptsRu.locale.firstDay,
        dayNamesMin: dateRangeOptsRu.locale.daysOfWeek,
        monthNames: dateRangeOptsRu.locale.monthNames,
        beforeShow: function () {
            setTimeout(function () {
                $('.ui-datepicker-calendar').css("display", "table");
            }, 1);
        },
        onChangeMonthYear: function () {
            setTimeout(function () {
                $('.ui-datepicker-calendar').css("display", "table");
            }, 1);
        }
    };
    
//    var datePickerSettingsMMyyView = {
//        dateFormat: "MM, yy",
//        firstDay: dateRangeOptsRu.locale.firstDay,
//        dayNamesMin: dateRangeOptsRu.locale.daysOfWeek,
//        monthNames: dateRangeOptsRu.locale.monthNames,
//        monthNamesShort: ['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек'],
//        changeMonth: true,
//        changeYear: true,
//        showButtonPanel: true,
//        closeText: "Ок",
//        currentText: "",
//        onClose: function (dateText, inst) {
//            $(this).datepicker('setDate', new Date(inst.selectedYear, inst.selectedMonth, 1));
//            $scope.data.currentSSTDate = moment(new Date(inst.selectedYear, inst.selectedMonth, 1)).format($scope.ctrlSettings.systemDateFormat);
//            $scope.getSST($scope.data.currentLocalPlace.id, $scope.data.currentSSTDate);
//            setTimeout(function () {
//                $('.ui-datepicker-calendar').addClass("nmc-hide");
//            }, 1);
//        },
//        beforeShow: function () {
//            setTimeout(function () {
//                $('.ui-datepicker-calendar').addClass("nmc-hide");
//                $('.ui-datepicker-current').addClass("nmc-hide");
//            }, 1);
//        },
//        onChangeMonthYear: function () {
//            setTimeout(function () {
//                $('.ui-datepicker-current').addClass("nmc-hide");
//                $('.ui-datepicker-calendar').addClass("nmc-hide");
//            }, 1);
//        }
//    };
    
    function getDetepickerSettingsFullView() {
        return angular.copy(datePickerSettingsFullView);
    }
    
//    var hasMapSettings = function (userInfo) {
//        var result = false;
//        result = !checkUndefinedNull(userInfo.subscriber);
//        result = result && !checkUndefinedNull(userInfo.subscriber.mapCenterLat);
//    };
    
    //get user info
    var userInfoUrl = "../api/systemInfo/fullUserInfo";
    $http.get(userInfoUrl, httpOptions)
        .then(function (resp) {
            var data = resp.data;
            if (checkUndefinedNull(data) || !data.hasOwnProperty("userName") || checkUndefinedNull(data.subscriber)) {
                console.warn("FullUserInfo is incorrect!", data);
                $rootScope.userInfo = null;
                return false;
            }
            var tmp = angular.copy(data);
//                tmp.userName = "Администратор Всея Руси";
//                tmp.subscriber.subscriberName = "Большая и Малая, Белая и остальная Русь"
            if (tmp.userName.length > 10) {
                tmp.userNameCaption = tmp.userName.substring(0, 10) + "...";
            } else {
                tmp.userNameCaption = tmp.userName;
            }
            if (tmp.subscriber.subscriberName.length > 15) {
                tmp.userSubscrCaption = tmp.subscriber.subscriberName.substring(0, 15) + "...";
            } else {
                tmp.userSubscrCaption = tmp.subscriber.subscriberName;
            }
            $rootScope.userInfo = tmp;
//console.log($rootScope.userInfo);
//                if (!checkUndefinedNull($rootScope.userInfo.subscriber)){                    
//                    var mapSettings = {};
//                    if (!checkUndefinedNull($rootScope.userInfo.subscriber.mapCenterLat)){
//                        mapSettings.mapCenterLat = $rootScope.userInfo.subscriber.mapCenterLat;
//                    };
//                    if (!checkUndefinedNull($rootScope.userInfo.subscriber.mapCenterLng)){
//                        mapSettings.mapCenterLng = $rootScope.userInfo.subscriber.mapCenterLng;
//                    };
//                    if (!checkUndefinedNull($rootScope.userInfo.subscriber.mapZoom)){
//                        mapSettings.mapZoom = $rootScope.userInfo.subscriber.mapZoom;
//                    };
//                    if (!checkUndefinedNull($rootScope.userInfo.subscriber.mapZoomDetail)){
//                        mapSettings.mapZoomDetail = $rootScope.userInfo.subscriber.mapZoomDetail;
//                    };                  
//                    if (mapSettings!=EMPTY_OBJECT){                       
//                        objectSvc.setObjectSettings(mapSettings);
//                        monitorSvc.setMonitorSettings(mapSettings);
//                    };
//                    
//                };
        }, function (e) {
            console.log(e);
        });
    
    // *************** load map settings *********************
    // **********************************************************
    function loadMapSetting(setting, target, broadcastMsg) {
        var url = prefUrl +  "?subscrPrefKeyname=" + setting;
        $http.get(url, httpOptions).then(function (resp) {
            mainSvcSettings.mapSettings[target] = resp.data.value;
            if (!checkUndefinedNull(broadcastMsg)) {
                $rootScope.$broadcast(broadcastMsg);
            }
        }, errorCallbackConsole);
    }
    
    function loadMapCenterLat() {
        loadMapSetting(LAT_MAP_PREF, "mapCenterLat", 'mainSvc:loadMapCenterLng');
    }
    
    function loadMapCenterLng() {
        loadMapSetting(LNG_MAP_PREF, "mapCenterLng", 'mainSvc:loadMapZoom');
    }
    
    function loadMapZoom() {
        loadMapSetting(ZOOM_MAP_PREF, "mapZoom", 'mainSvc:setMapSettingsInServices');
    }
    //load main map setting. If it is active == true, then load other map prefs
    function loadMapSettings() {
        var url = prefUrl + "?subscrPrefKeyname=" + MAP_PREF;
        $http.get(url, httpOptions).then(function (resp) {
            if (checkUndefinedNull(resp) || checkUndefinedNull(resp.data)) {
                console.warn("Subscriber map preference is empty!", resp);
                return false;
            }
            var mapPref = resp.data;
            if (mapPref.isActive === true) {
                $rootScope.$broadcast('mainSvc:loadMapCenterLat');
            }
        }, errorCallbackConsole);
    }
    
    function setMapSettingsInServices() {
        if (!checkEmptyObject(mainSvcSettings.mapSettings)) {
            objectSvc.setObjectSettings(mainSvcSettings.mapSettings);
            monitorSvc.setMonitorSettings(mainSvcSettings.mapSettings);
        }
    }
    
    $rootScope.$on('mainSvc:loadMapCenterLat', loadMapCenterLat);
    $rootScope.$on('mainSvc:loadMapCenterLng', loadMapCenterLng);
    $rootScope.$on('mainSvc:loadMapZoom', loadMapZoom);
    $rootScope.$on('mainSvc:setMapSettingsInServices', setMapSettingsInServices);
    // end of load map settings ****************
    
    var getProp = function (obj, propName) {
        var result = false;
        if (angular.isDefined(obj) && obj !== null && obj.hasOwnProperty(propName)) {
            result = obj[propName];
        }
        return result;
    };
        //check user: system? - true/false
    var isSystemuser = function () {
        return getProp($rootScope.userInfo, "_system");
    };
    
            //check user: rma? - true/false
    var isRma = function () {
        return getProp($rootScope.userInfo, "isRma");
    };
    
                //check user: admin? - true/false
    var isAdmin = function () {
        return getProp($rootScope.userInfo, "isAdmin");
    };
    
    var isCabinet = function () {
        return getProp($rootScope.userInfo, "isCabinet");
    };
    
                    //check user rights: read only?:  - true/false
    var isReadonly = function () {
        return getProp($rootScope.userInfo, "isReadonly");
    };
    
            //check test user
    function isTestUser() {
        return (getProp($rootScope.userInfo, "subscrType") === "TEST_CERTIFICATE");
    }
    
    var externalAllow = [
        {
            keyname: "WEB_ALLOW_SETTING_MAIN_MENU_ITEM",
            permissionTagId: "setting_main_menu_item",
            priority: 100
        },
        {
            keyname: "WEB_ALLOW_REPORT_MAIN_MENU_ITEM",
            permissionTagId: "report_main_menu_item",
            priority: 100
        }
    ];
    
    //get user permission
    var getUserServicesPermissions = function () {
        mainSvcSettings.loadingServicePermissionFlag = true;
        var targetUrl = mainSvcSettings.servicePermissionUrl;
        mainSvcSettings.loadedServicePermission = $http.get(targetUrl, httpOptions)
            .then(function (response) {
                if (checkUndefinedNull(response) || checkUndefinedNull(response.data)) {
                    console.warn("Loaded service permissions is empty or null!", response);
                    return false;
                }
                var tmp = response.data;
                contextIds = tmp;
                if (!isCabinet()) {
                    contextIds.push(externalAllow[0], externalAllow[1]);
                }
//    console.log(tmp);            
                mainSvcSettings.loadingServicePermissionFlag = false;            
                $rootScope.$broadcast('servicePermissions:loaded');
            },
                  function (e) {
                    console.log(e);
                    mainSvcSettings.loadingServicePermissionFlag = false;
                });
    };
    
    var checkContext = function (ctxId) {
        var result = false;
        contextIds.some(function (curCtx) {
            if (curCtx.permissionTagId.localeCompare(ctxId) === 0) {
                result = true;
                return true;
            }
        });
        return result;
    };
    ////////////////////////////////////////////////////////
    
    //checkers
    
    var checkEmptyNullValue = function (numvalue) {
        var result = false;
        if ((numvalue === "") || (numvalue == null)) {
            result = true;
            return result;
        }
        return result;
    };
    
    var checkUndefinedEmptyNullValue = function (numvalue) {
        var result = false;
        if ((angular.isUndefined(numvalue)) || (numvalue == null) || (numvalue === "")) {
            result = true;
        }
        return result;
    };

    function isNumeric(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    }
    
    var checkNumericValue = function (numvalue) {
        var result = true;
        if (checkEmptyNullValue(numvalue)) {
            return result;
        }
        if (!isNumeric(numvalue)) {
            result = false;
            return result;
        }
        return result;
    };
    
    var checkPositiveNumberValue = function (numvalue) {
        var result = true;
        result = checkNumericValue(numvalue);
        if (!result) {
            //if numvalue is not number -> return false
            return result;
        }
        result = parseInt(numvalue, RADIX) >= 0 ? true : false;
        return result;
    };
    
//    function isPositiveNumberValue(val) {
//        if (checkUndefinedNull(val)) {
//            return false;
//        } else {
//            return checkPositiveNumberValue(val);
//        }
//    }
    
    var checkHHmm = function (hhmmValue) {
        if (/(0[0-9]|1[0-9]|2[0-3]){1,2}:([0-5][0-9]){1}/.test(hhmmValue)) {
//        if (/(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]){1}/.test(hhmmValue)) {
            return true;
        }
        return false;
    };
    
    function isTestMode() {
        return getUseTest() && isTestUser();
    }
    
    ///////////////// end checkers
    
    function findItemBy(itemArray, fieldName, value) {
        var result = null;
        if (!angular.isArray(itemArray)) {
            console.log("Input value is no array.");
            return result;
        }
        if (checkUndefinedNull(value)) {
            console.log("value for search is undefined or null.");
            return result;
        }
        if (checkUndefinedNull(fieldName)) {
            itemArray.some(function (item) {
                if (item == value) {
                    result = item;
                    return true;
                }
            });
        } else {
            itemArray.some(function (item) {
                if (item[fieldName] == value) {
                    result = item;
                    return true;
                }
            });
        }
        return result;
    }
    
    // Sort object array by some string field
    function sortItemsBy(itemArray, sortField) {
        if (!angular.isArray(itemArray)) {
            return "Input value is no array.";
        }
        if (checkUndefinedNull(sortField)) {
            return "Field for sort is undefined or null.";
        }
        itemArray.sort(function (firstItem, secondItem) {
            if (checkUndefinedNull(firstItem[sortField]) && checkUndefinedNull(secondItem[sortField])) {
                return 0;
            }
            if (checkUndefinedNull(firstItem[sortField])) {
                return -1;
            }
            if (checkUndefinedNull(secondItem[sortField])) {
                return 1;
            }
            if (firstItem[sortField].toUpperCase() > secondItem[sortField].toUpperCase()) {
                return 1;
            }
            if (firstItem[sortField].toUpperCase() < secondItem[sortField].toUpperCase()) {
                return -1;
            }
            return 0;
        });
    }
    
    function sortNumericItemsBy(itemArray, sortField) {
        if (!angular.isArray(itemArray)) {
            return "Input value is no array.";
        }
        if (checkUndefinedNull(sortField)) {
            return "Field for sort is undefined or null.";
        }
        itemArray.sort(function (firstItem, secondItem) {
            if (checkUndefinedNull(firstItem[sortField]) && checkUndefinedNull(secondItem[sortField])) {
                return 0;
            }
            if (checkUndefinedNull(firstItem[sortField])) {
                return -1;
            }
            if (checkUndefinedNull(secondItem[sortField])) {
                return 1;
            }
            if (firstItem[sortField] > secondItem[sortField]) {
                return 1;
            }
            if (firstItem[sortField] < secondItem[sortField]) {
                return -1;
            }
            return 0;
        });
    }
    
    // Sort organizations by organizationName
    var sortOrganizationsByName = function (orgArr) {
        if (!angular.isArray(orgArr)) {
            return "Param is no array.";
        }
        sortItemsBy(orgArr, "organizationName");
//        orgArr.sort(function(a, b){
//            if (a.organizationName.toUpperCase() > b.organizationName.toUpperCase()){
//                return 1;
//            };
//            if (a.organizationName.toUpperCase() < b.organizationName.toUpperCase()){
//                return -1;
//            };
//            return 0;
//        });
    };
    
    // *************** generation confirm code *****************
    // **************************
//    var firstNum = Math.round(Math.random()*10);
//    var secondNum = Math.round(Math.random()*100);
    var getConfirmCode = function (useImprovedMethodFlag) {
//console.log(useImprovedMethodFlag);        
        var leftCoef = Math.random() * 10;
        var rightCoef = Math.random() * 100;
        if (!checkUndefinedNull(useImprovedMethodFlag) && useImprovedMethodFlag === true) {
            leftCoef = Math.random() * 1000 + 100;
            rightCoef = Math.random() * 1000 + 100;
        }
        var tmpFirst = Math.round(leftCoef);
        var tmpSecond = Math.round(rightCoef);
        var tmpLabel = tmpFirst + " + " + tmpSecond + " = ";
        var result = {
//            firstNum: tmpFirst,
//            secondNum: tmpSecond,
            label: tmpLabel,
            result: tmpFirst + tmpSecond
        };
//console.log(result);        
        return result;
    };
    //************************** end generation
    
    
    //***************** Server errors *************
//  ERR_UNCKNOWN(false, "Unknown Error"),
//	ERR_ACCESS_DENIED(false, "Access Denied"),
//	ERR_UNPROCESSABLE_TRANSACTION(false, "Unprocessable Transaction"),
//	ERR_DATABASE_ERROR(false, "Database Error"),
//	ERR_BRM_VALIDATION(false, "Buisiness Rule Validation Error"),
//	ERR_VALIDATION(false, "Data Validation Error"),
//	ERR_INTERNAL(false, "Internal server error"),
//	ERR_INVALID_STATE(false, "Invalid State Error"),
//	ERR_USER_ALREADY_EXISTS(false, "User Already Exists");
    
    var SUPPORT_STRING = "Обратитесь к поставщику системы.",
        SUPPORT_STRING_EXT = " В случае повторения ситуации обратитесь к поставщику системы.";
    var DEFAULT_ERROR_MESSAGE = {
        "resultCode": "ERR_DEFAULT",
        "caption": "Непредвиденная ситуация",
        "description": SUPPORT_STRING
    };
    var serverErrors = [
        {
            "resultCode": "ERR_UNCKNOWN",
            "caption": "Неизвестная ошибка",
            "description": SUPPORT_STRING
        },
        {
            "resultCode": "ERR_ACCESS_DENIED",
            "caption": "Отказано в доступе",
            "description": "У вас нет прав доступа. " + SUPPORT_STRING
        },
        {
            "resultCode": "ERR_UNPROCESSABLE_TRANSACTION",
            "caption": "Невозможно выполнить транзакцию",
            "description": SUPPORT_STRING
        },
        {
            "resultCode": "ERR_DATABASE_ERROR",
            "caption": "Ошибка базы данных",
            "description": SUPPORT_STRING
        },
        {
            "resultCode": "ERR_BRM_VALIDATION",
            "caption": "Ошибка валидации бизнес-правил",
            "description": SUPPORT_STRING
        },
        {
            "resultCode": "ERR_VALIDATION",
            "caption": "Ошибка валидации",
            "description": SUPPORT_STRING
        },
        {
            "resultCode": "ERR_INTERNAL",
            "caption": "Внутренняя ошибка сервера",
            "description": SUPPORT_STRING
        },
        {
            "resultCode": "ERR_INVALID_STATE",
            "caption": "Ошибка состояния",
            "description": SUPPORT_STRING
        },
        {
            "resultCode": "ERR_USER_ALREADY_EXISTS",
            "caption": "Пользователь уже существует",
            "description": "Пользователь с таким логином уже существует. Проверьте правильность набора."
        },
        {
            "resultCode": "ERR_CONNECTION",
            "caption": "Ошибка подключения",
            "description": "Не удалось подключиться к серверу. Проверьте соединение с сервером."
        },
        {
            "resultCode": "ERR_BAD_REQUEST",
            "caption": "Некорректный запрос",
            "description": "Некорректный запрос к серверу. Проверьте введенные данные и повторите попытку." + SUPPORT_STRING_EXT
        }
    ];
    
    function getServerErrorByResultCode(resultCode) {
//console.log(resultCode);
        var result = DEFAULT_ERROR_MESSAGE;
        if (checkUndefinedEmptyNullValue(resultCode)) { return result; }
        serverErrors.some(function (serror) {
            if (serror.resultCode == resultCode) {
                result = serror;
                return true;
            }
        });
        return result;
    }
    
    function errorCallbackHandler(e) {
        console.log(e);
        var errorCode = "-1";
        if (checkUndefinedNull(e) || checkUndefinedNull(e.data)) {
            errorCode = "ERR_CONNECTION";
            return getServerErrorByResultCode(errorCode);
        }
        if (!checkUndefinedNull(e.status) && e.status === 400) {
            errorCode = "ERR_BAD_REQUEST";
        }
        if (!checkUndefinedNull(e) && (!checkUndefinedNull(e.resultCode) || (!checkUndefinedNull(e.data) && !checkUndefinedNull(e.data.resultCode)))) {
            errorCode = e.resultCode || e.data.resultCode;
        }
        return getServerErrorByResultCode(errorCode);
        
    }
    
//    var getServerErrorByResult = function(result){
////console.log(resultCode);
//        var result = DEFAULT_ERROR_MESSAGE;
//        if (checkUndefinedEmptyNullValue(resultCode)){return result};
//        serverErrors.some(function(serror){
//            if (serror.resultCode == result.resultCode) {
////                if (result.description.indexOf("Access is denied") >= 0){
////                    
////                };
//                result = serror; 
//                return true;
//            };
//        });
//        return result;
//    };
    
    //****************** end region "Server errors"
    
    //*******************************************************************************
    //  Индиктор загрузки для отчетов
    //*******************************************************************************
    var htmlLoading = '<head>'
                    + '<meta charset="utf-8">'
                    + '<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css"/>'
                    + '</head>'
                    + '<body>'
                    + '<div  ng-show="loading" class="nmc-loading">'
                    + '<i class="fa fa-spinner fa-spin"></i> Загрузка ... '
                    + '</div>'
                    + '</body>';
    var getHtmlLoading = function () {
        return htmlLoading;
    };
    //*******************************************************************************
    //
    //*******************************************************************************
    
    // *********************************************************************************************
    //                      Work with trees
    // *********************************************************************************************
            //find node at tree by childObjectList
    var findNodeInTree = function (node, tree) {
        var result = null;
        if (!angular.isArray(tree.childObjectList)) {
            return result;
        }
        tree.childObjectList.some(function (curNode) {
            if (node.id == curNode.id && node.objectName == curNode.objectName) {
                result = curNode;
                return true;
            } else {
                result = findNodeInTree(node, curNode);
                return result != null;
            }
        });
        return result;
    };
    
    var sortTreeNodesBy = function (tree, fieldName) {
        if (checkUndefinedNull(tree.childObjectList) || !angular.isArray(tree.childObjectList)) {
            return;
        }
        sortItemsBy(tree.childObjectList, fieldName);
        tree.childObjectList.forEach(function (node) {
            sortTreeNodesBy(node, fieldName);
        });
    };
    
    // *********************************************************************************************
    //                     end Work with trees
    // *********************************************************************************************
    
    var setToolTip_old = function (title, text, elDom, targetDom, delay, width) {
        var tDelay = 1;
        if (!checkUndefinedNull(delay)) {
            tDelay = delay;
        }
        var tWidth = 1000;
        if (!checkUndefinedNull(width)) {
            tWidth = width;
        }
//console.log(elDom);                
//console.log(targetDom);    
//console.log($(elDom));        
//console.log($(targetDom));        
        $timeout(function () {
//console.log($(elDom));            
            $(elDom).qtip({
                suppress: false,
                content: {
                    text: text,
                    title: title,
                    button : true
                },
                show: {
                    event: 'click'
                },
                style: {
                    classes: 'qtip-nmc-indicator-tooltip',
                    width: tWidth
                },
                hide: {
                    event: 'unfocus'
                },
                position: {
                    my: 'top right',
                    at: 'bottom right',
                    target: $(targetDom)
                }
            });
        }, tDelay);
    };
    
    var setToolTip = function (title, text, elDom, targetDom, delay, width, my, at, qtipclass) {
        var tDelay = 1;
        if (!checkUndefinedNull(delay)) {
            tDelay = delay;
        }
        var tWidth = 1000;
        if (!checkUndefinedNull(width)) {
            tWidth = width;
        }
        var tMy = 'top right';
        if (!checkUndefinedNull(my)) {
            tMy = my;
        }
        var tAt = 'bottom right';
        if (!checkUndefinedNull(at)) {
            tAt = at;
        }
        var tQtipClass = 'qtip-nmc-indicator-tooltip';
        if (!checkUndefinedNull(qtipclass)) {
            tQtipClass = qtipclass;
        }
//console.log(elDom);                
//console.log(targetDom);    
//console.log($(elDom));        
//console.log($(targetDom));        
        $timeout(function () {
//console.log($(elDom));            
            $(elDom).qtip({
                suppress: false,
                content: {
                    text: text,
                    title: title,
                    button : true
                },
                show: {
                    event: 'click'
                },
                style: {
                    classes: tQtipClass,
                    width: tWidth
                },
                hide: {
                    event: 'unfocus'
                },
                position: {
                    my: tMy,
                    at: tAt,
                    target: $(targetDom)
                }
            });
        }, tDelay);
    };
    
/**
    Work with URL 
*/
    function addParamToURL(url, paramName, paramValue) {
        if (checkUndefinedNull(url) || checkUndefinedNull(paramName) || checkUndefinedNull(paramValue)) {
            return url;
        }
        url += ((url.indexOf('?') === -1) ? '?' : '&') + encodeURIComponent(paramName) + "=" + encodeURIComponent(paramValue);
        return url;
        
    }
// end work with URL
    
    function initSvc() {
        requestCanceler = $q.defer();
        httpOptions = {
            timeout: requestCanceler.promise
        };
        
        loadMapSettings();
    }
    initSvc();
    
    return {
        addParamToURL: addParamToURL,
        checkContext: checkContext,
        checkEmptyObject: checkEmptyObject,
        checkHHmm: checkHHmm,
        checkNumericValue: checkNumericValue,
        checkPositiveNumberValue: checkPositiveNumberValue,
        checkStrForDate: checkStrForDate,
        checkUndefinedEmptyNullValue: checkUndefinedEmptyNullValue,
        checkUndefinedNull: checkUndefinedNull,
        dateFormating: dateFormating,
        errorCallbackHandler: errorCallbackHandler,
        findItemBy: findItemBy,
        findNodeInTree: findNodeInTree,        
        getConfirmCode: getConfirmCode,
        getContextIds: getContextIds,
        getDetepickerSettingsFullView: getDetepickerSettingsFullView,
        getHtmlLoading: getHtmlLoading,
        getLoadingServicePermissionFlag: getLoadingServicePermissionFlag,
        getLoadedServicePermission: getLoadedServicePermission,
        getMonitorMapSettings: getMonitorMapSettings,
        getObjectMapSettings: getObjectMapSettings,
        getDateRangeOptions: getDateRangeOptions,
        getRequestCanceler: getRequestCanceler,
        getServerTimeZone: getServerTimeZone,
        getServerErrorByResultCode: getServerErrorByResultCode,
        getUseColorHighlightIndicatorData: getUseColorHighlightIndicatorData,
        getUserServicesPermissions: getUserServicesPermissions,
        getUseTest: getUseTest,
        getViewSystemInfo: getViewSystemInfo,
        isAdmin: isAdmin,
        isCabinet: isCabinet,
        isNumeric: isNumeric,
        /*isPositiveNumberValue: isPositiveNumberValue,*/
        isRma: isRma,
        isReadonly: isReadonly,
        isSystemuser: isSystemuser,
        isTestMode: isTestMode,
        prepareTimeOffset: prepareTimeOffset,
        setMonitorMapSettings: setMonitorMapSettings,
        setObjectMapSettings: setObjectMapSettings,
        setToolTip: setToolTip,
        sortItemsBy: sortItemsBy,
        sortNumericItemsBy: sortNumericItemsBy,
        sortOrganizationsByName: sortOrganizationsByName,
        sortTreeNodesBy: sortTreeNodesBy,
        strDateToUTC: strDateToUTC
    };
}]);