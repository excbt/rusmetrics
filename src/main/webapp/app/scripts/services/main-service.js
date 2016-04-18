//Service decides common tasks for all portal

angular.module('portalNMC')
.service('mainSvc', function($cookies, $http, $rootScope, $log, objectSvc, monitorSvc){
    var EMPTY_OBJECT = {};
//    $log.debug("Run main service. main service: row: 5");
    //set services settings
    var mainSvcSettings = {};
    mainSvcSettings.subscrUrl = "../api/subscr";
    mainSvcSettings.servicePermissionUrl = mainSvcSettings.subscrUrl+"/manage/service/permissions";
    mainSvcSettings.loadingServicePermissionFlag = false;
    mainSvcSettings.serverTimeZone = 3;//server time zone at Hours
//    mainSvcSettings.loadedServicePermission = null;
    
    var contextIds = [];
    
    var getContextIds = function(){
        return contextIds;
    };
    
    var getLoadingServicePermissionFlag = function(){
        return mainSvcSettings.loadingServicePermissionFlag;
    };
    
    var getLoadedServicePermission = function(){
        return mainSvcSettings.loadedServicePermission;
    };
    
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
    var getObjectMapSettings = function(){
        return objectMapSettings;
    };
    
    var getMonitorMapSettings = function(){
        return monitorMapSettings;
    };
    
    var setObjectMapSettings = function(mapSettings){
        objectMapSettings = mapSettings;
    };
    var setMonitorMapSettings = function(mapSettings){
        monitorMapSettings = mapSettings;
    };
    
    //methods for the work with the dates
    var dateFormating = function(millisec, dateFormat){
        var result ="";
        var serverTimeZoneDifferent = Math.round(mainSvcSettings.serverTimeZone*3600.0*1000.0);
        var tmpDate = (new Date(millisec + serverTimeZoneDifferent));
        result = (tmpDate == null) ? "" : moment([tmpDate.getUTCFullYear(), tmpDate.getUTCMonth(), tmpDate.getUTCDate(), tmpDate.getUTCHours(), tmpDate.getUTCMinutes()]).format(dateFormat);
        return result;//
    };
        //get UTC time from the string with date
    var strDateToUTC = function(strWithDate, strFormat){       
        var stDate = (new Date(moment(strWithDate, strFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601      
        var result = (!isNaN(stDate.getTime()))?Date.UTC(stDate.getFullYear(), stDate.getMonth(), stDate.getDate()):null;
        return result;
    };
        
    var checkStrForDate = function(strWithDate){
        //check date for format: DD/MM/YYYY
        if (/(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\d\d/.test(strWithDate)){
            return true;
        };  
        return false;
    };
    
    //date range settings
    var dateRangeOptsRu = {
        locale : {
            applyClass : 'btn-green',
            applyLabel : "Применить",
            fromLabel : "с",
            toLabel : "по",
            cancelLabel : 'Отмена',
            customRangeLabel : 'Период',
            daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
            firstDay : 1,
            monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                    'Октябрь', 'Ноябрь', 'Декабрь' ]
        },
        ranges : {
            'Текущий день' : [ moment().startOf('day'),
                    moment().endOf('day') ],
            'Посл 7 дней' : [
                    moment().subtract(6, 'days').startOf('day'),
                    moment().endOf('day') ],
            'Посл 30 дней' : [
                    moment().subtract(29, 'days').startOf('day'),
                    moment().endOf('day') ]
        },
        startDate : moment($cookies.fromDate) || moment().subtract(6, 'days').startOf('day'),
        endDate : moment($cookies.toDate) || moment().endOf('day'),

        format : 'DD.MM.YYYY'
        ,separator: " по "
    };
    
    var dateRangeMonitorOptsRu = dateRangeOptsRu;
    dateRangeMonitorOptsRu.startDate = moment().subtract(6, 'days').startOf('day');
    dateRangeMonitorOptsRu.endDate = moment().endOf('day');
    dateRangeMonitorOptsRu.maxDate = moment().endOf('day');
    
    var getDateRangeOptions = function(param, settings){
        var result = null;
        switch (param){
            case "ru": result = dateRangeOptsRu; break;
            case "monitor-ru": result = dateRangeMonitorOptsRu; break;
            case "indicator-ru":                 
                result = angular.copy(dateRangeMonitorOptsRu);
                result.startDate = (angular.isDefined($cookies.fromDate) && ($cookies.fromDate != null)) ? moment($cookies.fromDate).startOf('day') : moment($rootScope.reportStart).startOf('day');
                result.endDate = (angular.isDefined($cookies.toDate) && ($cookies.toDate != null)) ? moment($cookies.toDate).startOf('day'): moment($rootScope.reportEnd).startOf('day');
                if (!checkUndefinedNull(settings)){
                    if (!checkUndefinedNull(settings.minDate)){
                        result.minDate = settings.minDate;
                    };
                };
                break;
        };
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
    
    var hasMapSettings = function(userInfo){
        var result = false;
        result = !checkUndefinedNull(userInfo.subscriber);
        result=result&&!checkUndefinedNull(userInfo.subscriber.mapCenterLat)
    };
    
    //get user info
    var userInfoUrl = "../api/systemInfo/fullUserInfo";
    $http.get(userInfoUrl)
            .success(function(data, satus, headers, config){
                $rootScope.userInfo = angular.copy(data);
//console.log($rootScope.userInfo);
                if (!checkUndefinedNull($rootScope.userInfo.subscriber)){
//console.log($rootScope.userInfo.subscriber);                    
                    var mapSettings = {};
                    if (!checkUndefinedNull($rootScope.userInfo.subscriber.mapCenterLat)){
                        mapSettings.mapCenterLat = $rootScope.userInfo.subscriber.mapCenterLat;
                    };
                    if (!checkUndefinedNull($rootScope.userInfo.subscriber.mapCenterLng)){
                        mapSettings.mapCenterLng = $rootScope.userInfo.subscriber.mapCenterLng;
                    };
                    if (!checkUndefinedNull($rootScope.userInfo.subscriber.mapZoom)){
                        mapSettings.mapZoom = $rootScope.userInfo.subscriber.mapZoom;
                    };
                    if (!checkUndefinedNull($rootScope.userInfo.subscriber.mapZoomDetail)){
                        mapSettings.mapZoomDetail = $rootScope.userInfo.subscriber.mapZoomDetail;
                    };
//console.log(mapSettings);                     
                    if (mapSettings!=EMPTY_OBJECT){
//console.log(mapSettings);                        
                        objectSvc.setObjectSettings(mapSettings);
                        monitorSvc.setMonitorSettings(mapSettings);
                    };
                    
                };
            })
            .error(function(e){
                console.log(e);
    });
        //check user: system? - true/false
    var isSystemuser = function(){
        var result = false;
        var userInfo = $rootScope.userInfo;
        if (angular.isDefined(userInfo)){
            result = userInfo._system;
        };
        return result;
    };
    
            //check user: rma? - true/false
    var isRma = function(){
        var result = false;
        var userInfo = $rootScope.userInfo;
        if (angular.isDefined(userInfo)){
            result = userInfo.isRma;
        };
        return result;
    };
    
                //check user: admin? - true/false
    var isAdmin = function(){
        var result = false;
        var userInfo = $rootScope.userInfo;
        if (angular.isDefined(userInfo)){
            result = userInfo.isAdmin;
        };
        return result;
    };
    
                    //check user rights: read only?:  - true/false
    var isReadonly = function(){
        var result = false;
        var userInfo = $rootScope.userInfo;
        if (angular.isDefined(userInfo)){
            result = userInfo.isReadonly;
        };
        return result;
    };
    
    //get user permission
    var getUserServicesPermissions = function(){
        mainSvcSettings.loadingServicePermissionFlag = true;
        var targetUrl = mainSvcSettings.servicePermissionUrl;    
        mainSvcSettings.loadedServicePermission = $http.get(targetUrl)
        .then(function(response){
            var tmp = response.data;
            contextIds = tmp;
//console.log(tmp);            
            mainSvcSettings.loadingServicePermissionFlag = false;
            $rootScope.$broadcast('servicePermissions:loaded');
        },
              function(e){
            console.log(e);
            mainSvcSettings.loadingServicePermissionFlag = false;
        });
    };
    
    var checkContext = function(ctxId){       
        var result = false;       
        contextIds.some(function(curCtx){           
            if (curCtx.permissionTagId.localeCompare(ctxId) == 0){               
                result = true;
                return true;
            };
        });
        return result;
    };
    ////////////////////////////////////////////////////////
    
    //checkers
    var checkEmptyNullValue = function(numvalue){                    
        var result = false;
        if ((numvalue === "") || (numvalue==null)){
            result = true;
            return result;
        }
        return result;
    };
    
    var checkUndefinedEmptyNullValue = function(numvalue){                    
        var result = false;
        if ((angular.isUndefined(numvalue)) || (numvalue == null) || (numvalue === "")){
            result = true;
        };
        return result;
    };

    function isNumeric(n) {
      return !isNaN(parseFloat(n)) && isFinite(n);
    };
    
    var checkNumericValue = function(numvalue){ 
        var result = true;
        if (checkEmptyNullValue(numvalue)){
            return result;
        }
        if (!isNumeric(numvalue)){
            result = false;
            return result;
        };
        return result;
    };
    
    var checkPositiveNumberValue = function(numvalue){        
        var result = true;
        result = checkNumericValue(numvalue)
        if (!result){          
            //if numvalue is not number -> return false
            return result;
        };
        result = parseInt(numvalue) >= 0 ? true : false;
        return result;
    };
    
    var checkUndefinedNull = function(numvalue){
        var result = false;
        if ((angular.isUndefined(numvalue)) || (numvalue==null)){
            result = true;
        }
        return result;
    };
    var checkHHmm = function(hhmmValue){
        if (/(0[0-9]|1[0-9]|2[0-3]){1,2}:([0-5][0-9]){1}/.test(hhmmValue)){
            return true;
        };
        return false;
    };
    
    ///////////////// end checkers
    
    // Sort object array by some string field
    var sortItemsBy = function(itemArray, sortField){
        if (!angular.isArray(itemArray)){
            return "Input value is no array.";
        };
        if (checkUndefinedNull(sortField)){
            return "Field for sort is undefined or null.";
        };
        itemArray.sort(function(firstItem, secondItem){
            if (firstItem[sortField].toUpperCase() > secondItem[sortField].toUpperCase()){
                return 1;
            };
            if (firstItem[sortField].toUpperCase() < secondItem[sortField].toUpperCase()){
                return -1;
            };
            return 0;
        });
    };
    
    // Sort organizations by organizationName
    var sortOrganizationsByName = function(orgArr){
        if (!angular.isArray(orgArr)){
            return "Param is no array.";
        };
        orgArr.sort(function(a, b){
            if (a.organizationName.toUpperCase() > b.organizationName.toUpperCase()){
                return 1;
            };
            if (a.organizationName.toUpperCase() < b.organizationName.toUpperCase()){
                return -1;
            };
            return 0;
        });
    };
    
    // *************** generation confirm code *****************
    // **************************
//    var firstNum = Math.round(Math.random()*10);
//    var secondNum = Math.round(Math.random()*100);
    var getConfirmCode = function(useImprovedMethodFlag){
//console.log(useImprovedMethodFlag);        
        var leftCoef = Math.random()*10;
        var rightCoef = Math.random()*100;
        if (!checkUndefinedNull(useImprovedMethodFlag) && useImprovedMethodFlag == true){
            leftCoef = Math.random()*1000 + 100;
            rightCoef = Math.random()*1000 + 100;
        };
        var tmpFirst = Math.round(leftCoef);
        var tmpSecond = Math.round(rightCoef);
        var tmpLabel = tmpFirst+" + "+tmpSecond+" = ";
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
    var DEFAULT_ERROR_MESSAGE = {
        "resultCode": "ERR_DEFAULT",
        "caption": "Непредвиденная ситуация",
        "description": "Обратитесь к администратору системы."
    };
    var serverErrors = [
        {
            "resultCode": "ERR_UNCKNOWN",
            "caption": "Неизвестная ошибка",
            "description": "Обратитесь к администратору системы."
        },
        {
            "resultCode": "ERR_ACCESS_DENIED",
            "caption": "Отказано в доступе",
            "description": "У вас нет прав доступа. Обратитесь к администратору системы."
        },
        {
            "resultCode": "ERR_UNPROCESSABLE_TRANSACTION",
            "caption": "Невозможно выполнить транзакцию",
            "description": "Обратитесь к администратору системы."
        },
        {
            "resultCode": "ERR_DATABASE_ERROR",
            "caption": "Ошибка базы данных",
            "description": "Обратитесь к администратору системы."
        },
        {
            "resultCode": "ERR_BRM_VALIDATION",
            "caption": "Ошибка валидации бизнес-правил",
            "description": "Обратитесь к администратору системы."
        },
        {
            "resultCode": "ERR_VALIDATION",
            "caption": "Ошибка валидации",
            "description": "Обратитесь к администратору системы."
        },
        {
            "resultCode": "ERR_INTERNAL",
            "caption": "Внутренняя ошибка сервера",
            "description": "Обратитесь к администратору системы."
        },
        {
            "resultCode": "ERR_INVALID_STATE",
            "caption": "Ошибка состояния",
            "description": "Обратитесь к администратору системы."
        },
        {
            "resultCode": "ERR_USER_ALREADY_EXISTS",
            "caption": "Пользователь уже существует",
            "description": "Пользователь с таким логином уже существует. Проверьте правильность набора."
        },
        {
            "resultCode": "ERR_CONNECTION",
            "caption": "Ошибка подключения",
            "description": "Не удалось получить данные от сервера. Проверьте соединение с сервером."
        }
    ];
    
    var getServerErrorByResultCode = function(resultCode){
//console.log(resultCode);
        var result = DEFAULT_ERROR_MESSAGE;
        if (checkUndefinedEmptyNullValue(resultCode)){return result};
        serverErrors.some(function(serror){
            if (serror.resultCode == resultCode) {
                result = serror; 
                return true;
            };
        });
        return result;
    };
    
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
    var getHtmlLoading = function(){
        return htmlLoading;
    };
    //*******************************************************************************
    //
    //*******************************************************************************
    
    return {
        checkContext,
        checkHHmm,
        checkNumericValue,
        checkPositiveNumberValue,
        checkStrForDate,
        checkUndefinedEmptyNullValue,
        checkUndefinedNull,
        dateFormating,
        getConfirmCode,
        getContextIds,
        getHtmlLoading,
        getLoadingServicePermissionFlag,
        getLoadedServicePermission,
        getMonitorMapSettings,
        getObjectMapSettings,
        getDateRangeOptions,
        getServerErrorByResultCode,
        getUserServicesPermissions,
        isAdmin,
        isNumeric,
        isRma,
        isReadonly,
        isSystemuser,
        setMonitorMapSettings,
        setObjectMapSettings,
        sortItemsBy,
        sortOrganizationsByName,        
        strDateToUTC
    };
});