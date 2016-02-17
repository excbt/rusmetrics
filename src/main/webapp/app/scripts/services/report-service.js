'use strict';
angular.module('portalNMC')
.service('reportSvc', ['$http', '$cookies', '$interval', '$rootScope', 'crudGridDataFactory', 'mainSvc',
function($http, $cookies, $interval, $rootScope, crudGridDataFactory, mainSvc){
    //url к данным
    var reportTypesUrl = "../api/reportSettings/reportType";
    var reportPeriodsUrl = "../api/reportSettings/reportPeriod";
    var reportsBaseUrl = "../api/reportParamset"; 
    //переменные сервиса
    
    var dateFormat = "DD.MM.YYYY"; //user date format
    
    var reportTypesIsLoaded = false;
    var getReportTypesIsLoaded = function(){
        return reportTypesIsLoaded;
    };
    
    var setReportTypesIsLoaded = function(val){
        reportTypesIsLoaded = val;
    };
    
    var reportPeriodsIsLoaded = false;
    var getReportPeriodsIsLoaded = function(){
        return reportPeriodsIsLoaded;
    };
    var setReportPeriodsIsLoaded = function(val){
        reportPeriodsIsLoaded = val;
    };
    //Получение отчетов
        //категории отчетов
    var categories = [
        {
            name: "ANALITIC",
            caption: "Аналитические",
            prefix: "А",
            reportTypes: []
        },
        {
            name: "OPERATE",
            caption: "Оперативные",
            prefix: "Э",
            reportTypes: []
        },
        {
            name: "SERVICE",
            caption: "Служебные",
            prefix: "C",
            reportTypes: []
        }
    ];
    
    var getCategories = function(){
        return categories;
    };
        //загрузка типов отчетов
    var reportTypes = [];
    var getReportTypes = function(){
        return reportTypes;
    };
    var loadReportTypes = function(){
        setReportTypesIsLoaded(false);
        crudGridDataFactory(reportTypesUrl).query(function(data){
            reportTypes = data;
//console.log(data);
            var newObjects = [];
            var newObject = {};
            for (var i = 0; i < data.length; i++){
                if (!data[i]._enabled){
                    continue;
                };
                if ((! mainSvc.isSystemuser() && data[i].isDevMode)){
                    continue;
                };
                newObject = {};
                newObject.reportType = data[i].keyname;
                newObject.reportTypeName = data[i].caption;
                newObject.reportCategory = data[i].reportCategory;
                newObject.suffix = data[i].suffix;
                newObject.reportMetaParamSpecialList = data[i].reportMetaParamSpecialList;
                newObject.reportMetaParamCommon = data[i].reportMetaParamCommon;
                    //flag: the toggle visible flag for the special params page.
                newObject.reportMetaParamSpecialList_flag = (data[i].reportMetaParamSpecialList.length > 0 ? true : false);                 
                for (var categoryCounter = 0; categoryCounter <  categories.length; categoryCounter++){                                          
                    if (newObject.reportCategory.localeCompare(categories[categoryCounter].name) == 0){                        
                        newObject.reportTypeName = newObject.reportTypeName.slice(3, newObject.reportTypeName.length);
                        categories[categoryCounter].reportTypes.push(newObject);                                             
                        continue;
                    };
                };
                newObjects.push(newObject);
            };        
            reportTypes = newObjects;         
            $rootScope.$broadcast('reportSvc:reportTypesIsLoaded');
            setReportTypesIsLoaded(true);
        });
    };
//    loadReportTypes();
    
    //report periods ( ** загрузка периодов отчетов)
    var reportPeriods = [];
    var getReportPeriods = function(){
        return reportPeriods;
    };
    var loadReportPeriods = function(){
        setReportPeriodsIsLoaded(false)
        crudGridDataFactory(reportPeriodsUrl).query(function(data){
            reportPeriods = data;
            setReportPeriodsIsLoaded(true);
            $rootScope.$broadcast('reportSvc:reportPeriodsIsLoaded');
        });
    };    
    
        //Загрузка вариантов отчетов
    var getParamsets = function(table, type){
        crudGridDataFactory(table).query(function (data) {       
            type.paramsets = data;
            type.paramsetsCount = data.length;
        });
    };
    
 //get paramsets   
    var getParamsetsForTypes = function(reportTypes, mode){     
        if ((reportTypes.length == 0)){return;};
        for (var i = 0; i < reportTypes.length; i++){
            if (typeof reportTypes[i].suffix != 'undefined'){             
                getParamsets(reportsBaseUrl + mode + reportTypes[i].suffix, reportTypes[i]);
            };
        };
    };
        
    //Периодическое обновление отчетов
    ///???????
    
    //Проверка отчета при сохранении / перед запуском
        //for the paramset
    var checkPSRequiredFields = function(paramset, currentSign){
        var result;
        if (!(paramset.hasOwnProperty('reportPeriodKey')) || !(paramset.hasOwnProperty('reportTemplate'))){
            return false;
        };  
        //interval validate flag
            //default value    
        var intervalValidate_flag = true; 
            //if the paramset use interval
        if (currentSign == null){
                //check interval
            var startDateMillisec = mainSvc.strDateToUTC(paramset.psStartDateFormatted, dateFormat);
            var startDate = new Date(startDateMillisec);
            var endDateMillisec = mainSvc.strDateToUTC(paramset.psEndDateFormatted, dateFormat);
            var endDate = new Date(endDateMillisec);            
            intervalValidate_flag = (!isNaN(startDate.getTime())) 
                && (!isNaN(endDate.getTime())) 
                && $scope.checkDateInterval(paramset.psStartDateFormatted, paramset.psEndDateFormatted);
        };        
        result = !(((paramset.reportPeriodKey == null) 
                    || (paramset.reportTemplate.id == null)))
            && intervalValidate_flag;
        return result;        
    };
    
    var checkPSRequiredFieldsOnSave = function(reportType, reportParamset, currentSign, mode){        
        if (!reportType.hasOwnProperty('reportMetaParamCommon')){
            return true;
        };
        var result= true;
        var messageForUser = "Не все параметры варианта отчета заданы:\n";
        //Check common params before save
            //file ext
        if (mainSvc.checkUndefinedEmptyNullValue(reportParamset.outputFileType)){
//        if (angular.isUndefined(reportParamset.outputFileType) || (reportParamset.outputFileType === null) || (reportParamset.outputFileType === "")){
            messageForUser += "Основные свойства: "+"\n";
            messageForUser += "\u2022"+" Не задан тип файла"+"\n";
            result= false;
        };
            //start date
            //if the paramset use a date interval
        if (currentSign == null){
            var startDateMillisec = mainSvc.strDateToUTC(reportParamset.psStartDateFormatted, dateFormat);
            var startDate = new Date(startDateMillisec);
            var endDateMillisec = mainSvc.strDateToUTC(reportParamset.psEndDateFormatted, dateFormat);
            var endDate = new Date(endDateMillisec); 
            if (reportType.reportMetaParamCommon.startDateRequired 
                && (isNaN(startDate.getTime()) 
                    || (!mainSvc.checkStrForDate(reportParamset.psStartDateFormatted))))    
            {
                if (result){messageForUser += "Основные свойства: " + "\n";};
                messageForUser += "\u2022" + " Некорректно задано начало периода" + "\n";
                result = false;
            };

            if (reportType.reportMetaParamCommon.startDateRequired 
                && (isNaN(endDate.getTime()) 
                    || (!mainSvc.checkStrForDate(reportParamset.psEndDateFormatted))))    
            {
                if (result){messageForUser += "Основные свойства: " + "\n";};
                messageForUser += "\u2022" + " Некорректно задан конец периода" + "\n";
                result = false;
            };
            
            if (reportType.reportMetaParamCommon.startDateRequired 
                && !isNaN(endDate.getTime()) 
                && !isNaN(startDate.getTime()) 
                && (startDateMillisec > endDateMillisec))    
            {
                if (result){messageForUser += "Основные свойства: " + "\n";};
                messageForUser += "\u2022" + " Некорректно заданы границы периода" + "\n";
                result = false;
            };
        }

                    //Count of objects
        if (reportType.reportMetaParamCommon.oneContObjectRequired 
            && (reportParamset.selectedObjects.length == 0) 
            && reportType.reportMetaParamCommon.manyContObjectRequired)
        {
            messageForUser += "\u2022" + " Должен быть выбран хотя бы один объект" + "\n";
            result = false;
        };
        if (reportType.reportMetaParamCommon.oneContObjectRequired 
            && (reportParamset.selectedObjects.length == 0) 
            && !reportType.reportMetaParamCommon.manyContObjectRequired)
        {
            messageForUser += "\u2022" + " Необходимо выбрать один объект" + "\n";
            result = false;
        };
        if (reportType.reportMetaParamCommon.manyContObjectRequired 
            && (reportParamset.selectedObjects.length <= 0))
        {
            messageForUser += "\u2022" + " Необходимо выбрать несколько объектов" + "\n";
            result = false;
        };
        
        if (!reportType.reportMetaParamCommon.manyContObjectRequired 
            && (reportParamset.selectedObjects.length > 1) 
            &&  reportType.reportMetaParamCommon.oneContObjectRequired)
        {
            messageForUser += "\u2022" + " Нельзя выбрать более одного объекта" + "\n";
            result = false;
        };
        
        if (reportType.reportMetaParamCommon.manyContObjectsZipOnly 
            && (reportParamset.selectedObjects.length > 1))
        {
            reportParamset.outputFileZipped =  true;
        };
        //check special properties
        var specListFlag = true;
        reportParamset.currentParamSpecialList.forEach(function(element, index, array){
            if (element.paramSpecialRequired && !(element.textValue 
                                                 || element.numericValue 
                                                 || element.oneDateValue 
                                                 || element.startDateValue
                                                 || element.endDateValue
                                                 || element.directoryValue)
               )
            {
                if (specListFlag){messageForUser += "Дополнительные свойства: " + "\n";};
                messageForUser += "\u2022"+" Не задан параметр \"" + element.paramSpecialCaption + "\" \n";
                result = false;
                specListFlag = false;
            }
        });
        if(messageForUser != "Не все параметры варианта отчета заданы:\n"){
            if (mode.toUpperCase().localeCompare("CREATE") == 0){
                messageForUser += "\n Этот вариант отчета нельзя запустить без уточнения или использовать в рассылке, не задав обязательных параметров. Продолжить?";
            };
            result = false;
        };
        result = result && checkPSRequiredFields(reportParamset, currentSign);  
        if (!result){          
            reportParamset.showParamsBeforeRunReport = true;
        };
        return {"flag": result,
                "message": messageForUser};
    };
    
    //init service
    var initService = function(){      
        loadReportPeriods();
        loadReportTypes();
    };
    
    initService();
    
    return {
        checkPSRequiredFieldsOnSave,
        getCategories,
        getParamsetsForTypes,
        getReportPeriods,
        getReportPeriodsIsLoaded,
        getReportTypes,
        getReportTypesIsLoaded
        
    }
}]);