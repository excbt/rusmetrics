'use strict';
app = angular.module('portalNMC');
app.service('reportSvc', ['$http', '$cookies', '$interval', '$rootScope', 'crudGridDataFactoryWithCanceler', 'mainSvc', '$q',
function($http, $cookies, $interval, $rootScope, crudGridDataFactoryWithCanceler, mainSvc, $q){
    //url к данным
    var reportTypesUrl = "../api/reportSettings/reportType";
    var reportPeriodsUrl = "../api/reportSettings/reportPeriod";
    var reportsBaseUrl = "../api/reportParamset";
    var reportsContextLaunchUrl = reportsBaseUrl + "/menu/contextLaunch";
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
    var reportCategories = [
        {
            name: "OPERATE",
            caption: "Оперативные",
            prefix: "Э",
            class: "active",
            reportTypes: []
        },
        {
            name: "ANALITIC",
            caption: "Аналитические",
            prefix: "А",
            
            reportTypes: []            
        },
        {
            name: "SERVICE",
            caption: "Служебные",
            prefix: "C",
            reportTypes: []
        }
    ];
    
    var resourceCategories = [
        {
            name: "ALL",
            caption: "По всем энергоресурсам",
            isDefault: true,
            isAll: true
        },
        {
            name: "ELECTRICITY",
            caption: "Электричество"
        },
        {
            name: "WATER",
            caption: "Отопление, ГВС, ХВС"
        }
    ];
    
    var contServiceTypes = [
        {
            keyname: "heat",
            caption: "Теплоснабжение",
            class: "active"
        },{
            keyname: "hw",
            caption: "ГВС"
        },{
            keyname: "cw",
            caption: "ХВС"
        },{
            keyname: "el",
            caption: "Электроснабжение"
        }
    ]
    
    //request canceling params
    var requestCanceler = null;
    var httpOptions = null;

    function isCancelParamsIncorrect(){
        return mainSvc.checkUndefinedNull(requestCanceler) || mainSvc.checkUndefinedNull(httpOptions);
    }
    function getRequestCanceler () {
        return requestCanceler;
    }
    ////////////////////////////// 
    
    var getResourceCategories = function(){
        return resourceCategories;
    };
    
    var getContServiceTypes = function(){
        return contServiceTypes;
    };
    
    var getDefaultResourceCategory = function(){
        var defResCat = null;
        resourceCategories.some(function(resCat){
            if (resCat.isDefault == true){
                defResCat = resCat;
                return true;
            };
        });
        return defResCat;
    };
    
    var getReportCategories = function(){
        return reportCategories;
    };
        //загрузка типов отчетов
    var reportTypes = [];
    var getReportTypes = function(){
        return reportTypes;
    };
    var loadReportTypes = function(){
        setReportTypesIsLoaded(false);
        if (requestCanceler == null)
            return null;
        crudGridDataFactoryWithCanceler(reportTypesUrl, requestCanceler).query(function(data){
            reportTypes = data;
            reportCategories.forEach(function(cat){cat.reportTypes = []});//reset category reportType array
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
                newObject.resourceCategory = data[i].resourceCategory;
                newObject.contServiceTypes = data[i].contServiceTypes;
                newObject.suffix = data[i].suffix;
                newObject.reportMetaParamSpecialList = data[i].reportMetaParamSpecialList;
                newObject.reportMetaParamCommon = data[i].reportMetaParamCommon;
                    //flag: the toggle visible flag for the special params page.
                newObject.reportMetaParamSpecialList_flag = (data[i].reportMetaParamSpecialList.length > 0 ? true : false);                 
                for (var categoryCounter = 0; categoryCounter < reportCategories.length; categoryCounter++){                                          
                    if (newObject.reportCategory.localeCompare(reportCategories[categoryCounter].name) == 0){                        
                        newObject.reportTypeName = newObject.reportTypeName.slice(3, newObject.reportTypeName.length);
                        reportCategories[categoryCounter].reportTypes.push(newObject);                                             
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
        setReportPeriodsIsLoaded(false);
        if (requestCanceler == null)
            return null;
        crudGridDataFactoryWithCanceler(reportPeriodsUrl, requestCanceler).query(function(data){
            reportPeriods = data;
            setReportPeriodsIsLoaded(true);
            $rootScope.$broadcast('reportSvc:reportPeriodsIsLoaded');
        });
    };    
    
        //Загрузка вариантов отчетов
    var getParamsets = function(table, type){
        if (requestCanceler == null)
            return null;
        crudGridDataFactoryWithCanceler(table, requestCanceler).query(function (data) {       
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
    
    // ** Загрузка вариантов отчетов для контекстного меню объектов
    var loadReportsContextLaunch = function(){
        var url = reportsContextLaunchUrl;
        if (isCancelParamsIncorrect() == true)
            return null;
        return $http.get(url, httpOptions);
    };
        
    //Периодическое обновление отчетов
    ///???????
    
    //Проверка отчета при сохранении / перед запуском
    var checkDateInterval = function(left, right){     
        if (!mainSvc.checkStrForDate(left)){
            return false;
        };
        if (!mainSvc.checkStrForDate(right)){
            return false;
        };
        if ((left==null)|| (right==null)||(left=="")||(right=="")){console.log("1");return false;};
        var startDate = mainSvc.strDateToUTC(left, dateFormat);
        var sd = (startDate!=null)?(new Date(startDate)) : null;         
        var endDate = mainSvc.strDateToUTC(right, dateFormat);
        var ed = (endDate!=null)?(new Date(endDate)) : null;                
//        if ((isNaN(startDate.getTime()))|| (isNaN(endDate.getTime()))){return false;};       
        if ((sd==null)|| (ed==null)){return false;};               
        return ed>=sd;
    };
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
        var isUsingSettlementMonth = false;
        if (!mainSvc.checkUndefinedNull(paramset.currentReportPeriod) && !mainSvc.checkUndefinedNull(paramset.currentReportPeriod.isSettlementMonth) && (paramset.currentReportPeriod.isSettlementMonth == true || paramset.currentReportPeriod.isSettlementDay == true)){
            isUsingSettlementMonth = true;
        };
        if (currentSign == null && !isUsingSettlementMonth){
                //check interval
            var startDateMillisec = mainSvc.strDateToUTC(paramset.psStartDateFormatted, dateFormat);
            var startDate = new Date(startDateMillisec);
            var endDateMillisec = mainSvc.strDateToUTC(paramset.psEndDateFormatted, dateFormat);
            var endDate = new Date(endDateMillisec);            
            intervalValidate_flag = (!isNaN(startDate.getTime())) 
                && (!isNaN(endDate.getTime())) 
                && checkDateInterval(paramset.psStartDateFormatted, paramset.psEndDateFormatted);
        };        
        result = !(((paramset.reportPeriodKey == null) 
                    || (paramset.reportTemplate.id == null)))
            && intervalValidate_flag;
        return result;        
    };
    
    var checkPSRequiredFieldsOnSave = function(reportType, reportParamset, currentSign, mode){
        var messageForUser;
        if (mainSvc.checkUndefinedNull(reportType)){
            console.log("Report type = " + reportType);
            messageForUser = "Не удалось проверить тип отчета.\n";
            return false;
        }
//console.log(reportParamset);        
        if (!reportType.hasOwnProperty('reportMetaParamCommon')){
            return true;
        };
        var result = true;
        messageForUser = "Не все параметры варианта отчета заданы:\n";
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
        var isUsingSettlementMonth = false;
        if (!mainSvc.checkUndefinedNull(reportParamset.currentReportPeriod) && !mainSvc.checkUndefinedNull(reportParamset.currentReportPeriod.isSettlementMonth) && (reportParamset.currentReportPeriod.isSettlementMonth == true || reportParamset.currentReportPeriod.isSettlementDay == true)){
            isUsingSettlementMonth = true;
        }
//        if (isUsingSettlementMonth == true && (reportParamset.settlementDay == null || reportParamset.settlementDay == '' || reportParamset.settlementDay == 0)){
//            if (result){messageForUser += "Основные свойства: " + "\n";};
//            messageForUser += "\u2022" + " Некорректно задан расчетный день" + "\n";
//            result = false;
//        }
//console.log(!isUsingSettlementMonth);        
        if (currentSign == null && !isUsingSettlementMonth){
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
        if (!mainSvc.checkUndefinedNull(reportParamset.currentParamSpecialList)){
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
        };
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
//console.log(reportParamset);        
//console.log(result);        
//console.log(messageForUser);                
        return {"flag": result,
                "message": messageForUser};
    };
    
    //init service
    var initService = function(){  
        
        requestCanceler = $q.defer();
        httpOptions = {
            timeout: requestCanceler.promise
        };
//console.log(angular.copy(requestCanceler));        
        
        loadReportPeriods();
        loadReportTypes();
    };
    
    initService();
    
    return {
        checkPSRequiredFieldsOnSave,
        getContServiceTypes,
        getDefaultResourceCategory,
        getParamsetsForTypes,        
        getReportCategories,
        getReportPeriods,
        getReportPeriodsIsLoaded,
        getReportTypes,
        getReportTypesIsLoaded,
        getRequestCanceler,
        getResourceCategories,
        loadReportsContextLaunch
    }
}]);

app.filter('resourceCategoryFilter', function(){
    return function(items, props){
        if (props.isAll == true){
            return items;
        };
        var filteredItems = [];      
        items.forEach(function(item){
            if (item.resourceCategory == props.name){
                filteredItems.push(item);
            };
        });     
        return filteredItems;
    }
});

app.filter('serviceTypesFilter', function(){
    return function(items, props){
//console.log(items);
//console.log(props);
        var filteredItems = [];      
        items.forEach(function(item){
            if (angular.isUndefined(item.contServiceTypes) || item.contServiceTypes.length <= 0){
                filteredItems.push(item);
                return;
            };
            if (angular.isArray(item.contServiceTypes)){
                item.contServiceTypes.some(function(elem){
                    if (elem.keyname == props.keyname){
                        filteredItems.push(item);
                        return true;
                    }
                })
            };
        });     
//console.log(filteredItems);        
        return filteredItems;
    }
});