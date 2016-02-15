'use strict';
angular.module('portalNMC')
.service('reportSvc', ['$http', '$cookies', '$interval', '$rootScope',
function($http, $cookies, $interval, $rootScope){
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
                if ((! isSystemuser() && data[i].isDevMode)){
                    continue;
                };
                newObject = {};
                newObject.reportType = data[i].keyname;
                newObject.reportTypeName = data[i].caption;

                newObject.suffix = data[i].suffix;
                newObject.reportMetaParamSpecialList = data[i].reportMetaParamSpecialList;
                newObject.reportMetaParamCommon = data[i].reportMetaParamCommon;
                    //flag: the toggle visible flag for the special params page.
                newObject.reportMetaParamSpecialList_flag = (data[i].reportMetaParamSpecialList.length > 0 ? true : false);            
                for (var categoryCounter = 0; categoryCounter <  categories.length; categoryCounter++){                         
                    if (newObject.reportTypeName[0] ==  categories[categoryCounter].prefix){
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
    var loadReportTypes();
    
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
    getParamsets = function(table, type){
        crudGridDataFactory(table).query(function (data) {
            type.paramsets = data;
            type.paramsetsCount = data.length;
        });
    };
    
 //get paramsets   
    getActive = function(){        
        if (( objects == []) || (typeof  objects[0].suffix == 'undefined')){return;};
        for (var i = 0; i <  objects.length; i++){
             getParamsets( crudTableName +  objects[i].suffix,  objects[i]);
        };
    };
    
//     getActive();
    
     getArchive = function(){
        if (( objects == []) || (typeof  objects[0].suffix == 'undefined')){return;};
        for (var i = 0; i <  objects.length; i++){
             getParamsets( crudTableName + "/archive" +  objects[i].suffix,  objects[i]);
        };        
    };
        
    var 
    //Периодическое обновление отчетов
    return {
        getCategories
    }
};