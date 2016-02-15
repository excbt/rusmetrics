'use strict';
angular.module('portalNMC')
.service('reportSvc', ['$http', '$cookies', '$interval', '$rootScope', 'crudGridDataFactory', 'mainSvc',
function($http, $cookies, $interval, $rootScope, crudGridDataFactory, mainSvc){
    var reportTypesUrl = "../api/reportSettings/reportType";
    var reportPeriodsUrl = "../api/reportSettings/reportPeriod";
    var reportsBaseUrl = "../api/reportParamset"; 
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
            $rootScope.$broadcast('reportSvc:reportTypesLoaded');
        });
    };
    loadReportTypes();
    
    //report periods ( ** загрузка периодов отчетов)
    var reportPeriods = [];
    var getReportPeriods = function(){
        return reportPeriods;
    };
    var loadReportPeriods = function(){
        crudGridDataFactory(reportPeriodsUrl).query(function(data){
            reportPeriods = data;
            $rootScope.$broadcast('reportSvc:reportPeriodsLoaded');
        });
    };
    loadReportPeriods();
    
        //Загрузка вариантов отчетов
    var getParamsets = function(table, type){
        crudGridDataFactory(table).query(function (data) {
            type.paramsets = data;
            type.paramsetsCount = data.length;
        });
    };
    
 //get paramsets   
    var getActive = function(reportPeriods){        
        if ((reportPeriods.length == 0)){return;};
        for (var i = 0; i < reportPeriods.length; i++){
            if (typeof reportPeriods[i].suffix != 'undefined'){
                getParamsets(reportsBaseUrl + reportPeriods[i].suffix, reportPeriods[i]);
            }
        };
    };

    var getArchive = function(reportPeriods){
        if ((reportPeriods.length == 0)){return;};
        for (var i = 0; i < reportPeriods.length; i++){
            if (typeof reportPeriods[i].suffix != 'undefined'){
                getParamsets(reportsBaseUrl + "/archive" + reportPeriods[i].suffix, reportPeriods[i]);
            };
        };        
    };
    
    var getParamsetsForTypes = function(reportPeriods, mode){
        if ((reportPeriods.length == 0)){return;};
        for (var i = 0; i < reportPeriods.length; i++){
            if (typeof reportPeriods[i].suffix != 'undefined'){
                getParamsets(reportsBaseUrl + mode + reportPeriods[i].suffix, reportPeriods[i]);
            };
        };
    };
        
    //Периодическое обновление отчетов
    ///???????
    
    
    return {
        getCategories,
        getReportTypes,
        getReportPeriods,
        getParamsetsForTypes
    }
}]);