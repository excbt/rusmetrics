//Service decides common tasks for all portal

angular.module('portalNMC')
.service('mainSvc', function($cookies, $http, $rootScope){
    //set services settings
    var mainSvcSettings = {};
    mainSvcSettings.subscrUrl = "../api/subscr";
    mainSvcSettings.servicePermissionUrl = mainSvcSettings.subscrUrl+"/manage/service/permissions";
    mainSvcSettings.loadingServicePermissionFlag = false;
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
    
    var getDateRangeOptions = function(param){
        var result = null;
        switch (param){
            case "ru": result=dateRangeOptsRu;break;
            case "monitor-ru": result=dateRangeMonitorOptsRu;break;
            case "indicator-ru": 
console.log($cookies.fromDate);                
                result = angular.copy(dateRangeMonitorOptsRu);
                result.startDate = (angular.isDefined($cookies.fromDate)&&($cookies.fromDate!=null))?moment($cookies.fromDate).startOf('day'):moment($rootScope.reportStart).startOf('day');
                result.endDate = (angular.isDefined($cookies.toDate)&&($cookies.toDate!=null))?moment($cookies.toDate).startOf('day'): moment($rootScope.reportEnd).startOf('day');
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
    
    //get user info
    var userInfoUrl = "../api/systemInfo/fullUserInfo";
    $http.get(userInfoUrl)
            .success(function(data, satus, headers, config){
                $rootScope.userInfo = data;
//console.log($rootScope.userInfo);        
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
        if ((angular.isUndefined(numvalue)) || (numvalue === "") || (numvalue==null)){
            result = true;
            return result;
        }
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
        result = parseInt(numvalue)>=0?true:false;
        return result;
    };
    
    ///////////////// end checkers
    
    // *************** generation confirm code *****************
    // **************************
//    var firstNum = Math.round(Math.random()*10);
//    var secondNum = Math.round(Math.random()*100);
    var getConfirmCode = function(){
        var tmpFirst = Math.round(Math.random()*10);
        var tmpSecond = Math.round(Math.random()*100);
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
    
    return {
        checkContext,
        checkNumericValue,
        checkPositiveNumberValue,
        checkStrForDate,
        checkUndefinedEmptyNullValue,
        getConfirmCode,
        getContextIds,
        getLoadingServicePermissionFlag,
        getLoadedServicePermission,
        getMonitorMapSettings,
        getObjectMapSettings,
        getDateRangeOptions,
        getUserServicesPermissions,
        isRma,
        isSystemuser,
        setMonitorMapSettings,
        setObjectMapSettings,
        strDateToUTC
    };
});