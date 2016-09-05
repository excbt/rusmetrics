'use strict';
var app = angular.module('portalNMC');

app.controller('ParamSetsCtrl',['$scope', '$rootScope', '$resource', '$http', 'crudGridDataFactory', 'notificationFactory', 'objectSvc', 'mainSvc', 'reportSvc', '$filter' ,function($scope, $rootScope, $resource, $http, crudGridDataFactory, notificationFactory, objectSvc, mainSvc, reportSvc, $filter){
//console.log("ParamSetsCtrl");
    var CATEGORY_COEF = 1000;
    
    $rootScope.ctxId = "param_sets_page";
    //ctrl settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    $scope.ctrlSettings.selectedAll = false;
    
    $scope.ctrlSettings.ctxId = "paramset_page";
    
    $scope.set_of_objects_flag = false; //флаг: истина - открыта вкладка с объектами
    $scope.showAvailableObjects_flag = false; // флаг, устанавливающий видимость окна с доступными объектами
    $scope.currentSign = 9999;// устанавливаем начальное значение отличное от нулл и других возможных значение; нулл - будем отлавливать
    $scope.columns = [
        {"name": "reportType", "header": "Тип отчета", "class": "col-xs-11 col-md-11"}
    ];
    $scope.paramsetColumns = [
        {"name": "name", "header": "Наименование", "class": "col-xs-5 col-md-5"}
        /*,{"name": "reportTemplateName", "header": "Шаблон", "class": "col-md-3"}*/
        ,{"name": "period", "header": "Период", "class": "col-xs-2 col-md-2"}
        ,{"name": "fileType", "header": "Тип файла", "class": "col-xs-1 col-md-1"}
        ,{"name": "outputFileNameTemplate", "header": "Шаблон имени файла", "class": "col-xs-3 col-md-3"}
    ];
    $scope.extraProps = {"idColumnName": "id", 
                       "defaultOrderBy" : "name", 
                       "deleteConfirmationProp": "name"};    
    
    $scope.createParamset_flag = false;
    $scope.editParamset_flag = false;
    $scope.createByTemplate_flag = false;
    $scope.currentObject = {};
    $scope.createByTemplate_flag = false;
    $scope.archiveParamset = {};
    $scope.activeStartDateFormat = new Date();
    $scope.activeStartDateFormatted = moment().format($scope.ctrlSettings.dateFormat);
    
    $scope.currentParamSpecialList = null;
    
    //file types
    $scope.fileTypes = ["PDF", "HTML", "XLSX"];

    $scope.groupUrl = "../api/subscr/contGroup";
    $scope.crudTableName = "../api/reportParamset"; 
    
        //Headers of modal window
    $scope.headers = {}
    $scope.headers.addObjects = "Доступные объекты";//header of add objects window
    
    $scope.objects = [];
    $scope.availableObjectGroups = [];
    
    $scope.categories = reportSvc.getReportCategories();
    
    $scope.contServiceTypes = reportSvc.getContServiceTypes();
    
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
        
    //report periods
    if (reportSvc.getReportPeriodsIsLoaded()){
        $scope.reportPeriods = angular.copy(reportSvc.getReportPeriods());    
    };

    var successCallback = function (e) {     
        notificationFactory.success();
        
        $('#moveToArchiveModal').modal('hide');
        if (!$scope.createByTemplate_flag){
            $scope.getActive();
//            reportSvc.getParamsetsForTypes($scope.reportTypes, "");
        };
        $('#createParamsetModal').modal('hide');
        $scope.setDefault();
        
    };
    
    var successDeleteCallback = function (e) {     
        notificationFactory.success();
        
        $('#deleteObjectModal').modal('hide');
        $scope.getArchive();
        $scope.setDefault();        
    };

    var errorCallback = function (e) {
        var errorCode = "-1";
        if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)){
            errorCode = "ERR_CONNECTION";
        };
        if (!mainSvc.checkUndefinedNull(e) && (!mainSvc.checkUndefinedNull(e.resultCode) || !mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode))){
            errorCode = e.resultCode || e.data.resultCode;
        };
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
//        notificationFactory.errorInfo(e.statusText,e.data.description);
    };
    
    $scope.deleteObject = function (object) {
        var table = $scope.crudTableName + "/archive" + $scope.currentReportType.suffix;
//console.log(table);        
//console.log(object[$scope.extraProps.idColumnName]);        
        crudGridDataFactory(table).delete({ id: object[$scope.extraProps.idColumnName] }, successDeleteCallback, errorCallback);
    };
       
//    $scope.getParamsets = function(table, type){
//        crudGridDataFactory(table).query(function (data) {
//            type.paramsets = data;
//            type.paramsetsCount = data.length;
//        });
//    };
    
 //get paramsets   
    $scope.getActive = function(){
        $scope.currentMode = "";//active paramsets
        if (reportSvc.getReportTypesIsLoaded()){
            //report types
            $scope.reportTypes = reportSvc.getReportTypes();
            reportSvc.getParamsetsForTypes($scope.reportTypes, $scope.currentMode);
        };
    };
    
    $scope.getActive();
    
    $scope.getArchive = function(){      
        $scope.currentMode = "/archive";//archive paramsets
        if (reportSvc.getReportTypesIsLoaded()){
            //report types
            $scope.reportTypes = reportSvc.getReportTypes();
            reportSvc.getParamsetsForTypes($scope.reportTypes, $scope.currentMode);       
        };
    };
    
    $scope.$on('reportSvc:reportTypesIsLoaded', function(){
        //report types
        $scope.reportTypes = reportSvc.getReportTypes();
        reportSvc.getParamsetsForTypes($scope.reportTypes, $scope.currentMode);
    });
    
    $scope.$on('reportSvc:reportPeriodsIsLoaded', function(){
        //report types
        $scope.reportPeriods = reportSvc.getReportPeriods();        
    });

    $scope.toggleShowGroupDetails = function(curObject, serviceType){//switch option: current goup details
//console.log(curObject); 
//console.log(serviceType);        
        if (mainSvc.checkUndefinedNull(curObject.showGroupDetails)){
            curObject.showGroupDetails = {};
        }
        curObject.showGroupDetails[serviceType.keyname] = !curObject.showGroupDetails[serviceType.keyname];
//        curObject.showGroupDetails = !curObject.showGroupDetails;
    };

    $scope.selectedItem = function(parentItem, item){
        $scope.setCurrentReportType(parentItem);       
        var curObject = angular.copy(item);
		$scope.currentObject = curObject;
        $scope.activeStartDateFormat = (curObject.activeStartDate == null) ? null : new Date(curObject.activeStartDate);
        var activeStartDate = new Date(curObject.activeStartDate);       
        $scope.activeStartDateFormatted = (curObject.activeStartDate == null) ? "" : moment([activeStartDate.getUTCFullYear(), activeStartDate.getUTCMonth(), activeStartDate.getUTCDate()]).format($scope.ctrlSettings.dateFormat);        
        $scope.getTemplates();       
    };
//    29863766
    $scope.checkAndSaveParamset = function(paramsets, object){       
        if (!object.name || object.name == ''){
            notificationFactory.errorInfo("Ошибка", "Не задано наименование варианта отчета. Заполните поле 'Наименование'.");
            return "Add / edit paramset: no name";
        };
        if (!mainSvc.checkUndefinedNull(paramsets) && angular.isArray(paramsets)){
            for (var psCounter = 0; psCounter < paramsets.length; psCounter++){
                if ((object.id != paramsets[psCounter].id) && (object.name.localeCompare(paramsets[psCounter].name) == 0)){                   
                    notificationFactory.errorInfo("Ошибка", "Вариант отчета должен иметь уникальное наименование . Измените поле 'Наименование'.");
                    return "Add / edit paramset: name is not unique.";
                };
            };
        };
        var flag = $scope.checkRequiredFieldsOnSave();
        if (flag === false){
            $('#messageForUserModal').modal();
        }else{
            $scope.saveParamset(object);
        };
    };
    
    //Save paramset
    $scope.saveParamset = function(object){  
        //close modal window with the message for user
        //$('#messageForUserModal').modal('hide');
        
        //perform Special paramset props
        if(angular.isArray($scope.currentParamSpecialList)){
            $scope.currentParamSpecialList.forEach(function(element){
                element.oneDateValue = (element.oneDateValueFormatted == null) ? null : element.oneDateValueFormatted.getTime();
                element.startDateValue = (element.startDateValueFormatted == null) ? null : element.startDateValueFormatted.getTime();
                element.endDateValue = (element.endDateValueFormatted == null) ? null : element.endDateValueFormatted.getTime();
            });
        };
        //set the list of the special params
        object.paramSpecialList = $scope.currentParamSpecialList;      
        var table="";       
        //get the id's array of the selected objects - server expect array of object ids      
        var tmp = $scope.selectedObjects.map(function(elem){      
            return elem.id;
        });   
        //
//        object.activeStartDate = ($scope.activeStartDateFormat==null)?null:$scope.activeStartDateFormat.getTime();    
        //var astDate = (new Date(moment($scope.activeStartDateFormatted, $scope.ctrlSettings.dateFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601
    //    var UTCastdt = Date.UTC(astDate.getFullYear(), astDate.getMonth(), astDate.getDate());
    //    object.activeStartDate = ($scope.activeStartDateFormatted=="")?null:UTCastdt; 
        
        var astDate = (new Date(moment($scope.activeStartDateFormatted, $scope.ctrlSettings.dateFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601             
        var UTCastdt = Date.UTC(astDate.getFullYear(), astDate.getMonth(), astDate.getDate()); 
//console.log(UTCastdt);        
//console.log($scope.activeStartDateFormatted);            
//console.log(mainSvc.checkStrForDate($scope.activeStartDateFormatted));            
        object.activeStartDate = (!mainSvc.checkStrForDate($scope.activeStartDateFormatted)) ? null : UTCastdt; 
        
        
        //set the param, which define - available auto/manual start report.
        object.allRequiredParamsPassed = !object.showParamsBeforeRunReport;
//console.log(object.allRequiredParamsPassed);        
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){
            //perform start interval           
            var stDate = (new Date(moment($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601             
            var UTCstdt = Date.UTC(stDate.getFullYear(), stDate.getMonth(), stDate.getDate());              
            object.paramsetStartDate = (!isNaN(UTCstdt)) ? new Date(UTCstdt) : null;//(new Date($scope.paramsetStartDateFormat)) /*(new Date($rootScope.reportStart))*/ || null;
            
            //perform end interval
            var endDate = (new Date(moment($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601                        
            var UTCenddt = Date.UTC(endDate.getFullYear(), endDate.getMonth(), endDate.getDate()); 
            
            object.paramsetEndDate = (!isNaN(UTCenddt)) ? new Date(UTCenddt) : null;//(new Date($scope.paramsetEndDateFormat)) /*(new Date($rootScope.reportEnd))*/ || null;
        }else{
            object.paramsetStartDate = null;
            object.paramsetEndDate = null;
        }
//console.log(object);
//console.log((moment($scope.paramsetStartDateFormatted.startDate).startOf('day')));        
//console.log(typeof (moment($scope.paramsetStartDateFormatted.startDate).startOf('day')));                
        if ($scope.createByTemplate_flag){
            object.id = null;          
//            object.activeStartDate = ($scope.activeStartDateFormat==null)?null:$scope.activeStartDateFormat.getTime();
//            var astDate = (new Date($scope.activeStartDateFormatted)); 

            table = $scope.crudTableName + "/createByTemplate/" + $scope.archiveParamset.id;  
//console.log("$scope.createByTemplate_flag"); 
//console.log(tmp);            
            crudGridDataFactory(table).save({contObjectIds: tmp}, object, successCallback, errorCallback);
            return;
        };
        table = $scope.crudTableName + $scope.currentReportType.suffix;
//console.log($scope.createParamset_flag);        
        if ($scope.createParamset_flag){            
            object._active = true;
            
            crudGridDataFactory(table).save({reportTemplateId: object.reportTemplate.id, contObjectIds: tmp}, object, successCallback, errorCallback);
        };
        if ($scope.editParamset_flag){
            var clearContObjectIds = false; //the clear selected paramset objects flag           
            if (mainSvc.checkUndefinedNull(tmp) || tmp.length == 0){                
                clearContObjectIds = true;
            };
            crudGridDataFactory(table).update({reportParamsetId: object.id, 
                                               contObjectIds: tmp, 
                                               clearContObjectIds: clearContObjectIds}, 
                                              object, 
                                              successCallback, 
                                              errorCallback);
        };
    };
    
    $scope.toArchive = function(url, id) {
        return $resource(url, {
            }, {
                update: {method: 'PUT', params:{reportParamsetId:id}}
            });
    };
    
    $scope.moveToArchive = function(object){
        var table = $scope.crudTableName + "/archive/move";  
        $scope.toArchive(table, object.id).update({}, object, successCallback, errorCallback);
    };
    
    $scope.createByTemplate =  function(parentObject, object){
//        Установка объектов при создании варианта по архивному шаблону        
        $scope.selectedItem(parentObject, object);
        $scope.createByTemplate_flag = true;
        $scope.archiveParamset = {};
        $scope.archiveParamset.id = object.id;
        $scope.archiveParamset.name = object.name;
        $scope.currentParamSpecialList = prepareParamSpecialList(object, $scope.currentReportType);//$scope.archiveParamset.paramSpecialList;
//console.log(object);        
//console.log($scope.archiveParamset);                
//console.log($scope.currentParamSpecialList);        
        $scope.currentObject = angular.copy(object);
        $scope.currentObject.id = null;
        $scope.currentObject._active = true;
        $scope.currentObject.activeEndDate = null;
        $scope.getAvailableObjects(object.id);
//        $scope.selectedObjects = [];
        $scope.getSelectedObjects($scope.archiveParamset.id);
//console.log($scope.currentObject.common || !$scope.currentObject._active); 
        $scope.set_of_objects_flag = false;
        $scope.showAvailableObjects_flag = false;
        activateMainPropertiesTab();
    };
    
    $scope.setDefault = function(){
//        Сброс всех вспомогательных объектов в дефолтное состояние
        $scope.currentObject = {};
        $scope.createByTemplate_flag = false;
        $scope.archiveParamset = {};
        $scope.activeStartDateFormat = null;
        $scope.activeStartDateFormatted = "";
        $scope.currentReportType = {};
        $scope.createParamset_flag = false;
        $scope.editParamset_flag = false;
        $scope.set_of_objects_flag = false;
        $scope.showAvailableObjects_flag = false;
        $scope.currentSign = 9999;
        $scope.selectedObjects = [];
        //Устанавливаем активность вкладок по дефолту: Основные свойства - активная, Выбор объектов - неактивная
//        $('#main_properties_tab').addClass("active");
//        $('#main_properties').addClass("active");
//        $('#set_of_objects_tab').removeClass("active");
//        $('#set_of_objects').removeClass("active");
        
    };
    
    $scope.currentReportType = {};
    $scope.setCurrentReportType = function(object){
        $scope.currentReportType = object;
//        $scope.currentReportType.reportType = object.reportType;
//        $scope.currentReportType.reportTypeName=object.reportTypeName;
//        $scope.currentReportType.suffix=object.suffix;
    };
    
    var activateMainPropertiesTab = function(){
        $('#main_properties_tab').addClass("active");
        $('#set_of_objects_tab').removeClass("active");
        $('#extra_properties_tab').removeClass("active");
        $('#createParamsetModal').modal();
    };
    
    //get custom directory data
    $scope.getDirectory = function(url, obj){
        $http.get(url)
            .success(function(data){
                obj.specialTypeDirectoryValues = data;
                //set default directory value
                if (!mainSvc.checkUndefinedNull(obj.directoryValue)){
                    return;
                };
                obj.directoryValue = null;
                if (angular.isArray(data) && (data.length > 0)){
                    data.some(function(elem){
                        if (elem.isDefault == true){
                            obj.directoryValue = elem[obj.specialTypeDirectoryValue];
                            return true;
                        };
                    });
                };
                
            })
            .error(function(e){
                console.log(e);
            });
    };
    
    function prepareCurrentParamSpecialListForAddParamSet_OLD_VERSION () {
        $scope.currentParamSpecialList = $scope.currentReportType.reportMetaParamSpecialList.map(function(element){
            var result = {};
            result.paramSpecialCaption = element.paramSpecialCaption;
            result.reportMetaParamSpecialId = element.id;
            result.paramSpecialRequired = element.paramSpecialRequired;
            result.paramSpecialTypeKeyname = element.paramSpecialType.keyname;
            if (isParamSpecialTypeDirectory(element))
            {
                result.specialTypeDirectoryUrl =element.paramSpecialType.specialTypeDirectoryUrl;
                result.specialTypeDirectoryKey =element.paramSpecialType.specialTypeDirectoryKey;
                result.specialTypeDirectoryCaption = element.paramSpecialType.specialTypeDirectoryCaption;
                result.specialTypeDirectoryValue = element.paramSpecialType.specialTypeDirectoryValue;
                $scope.getDirectory(".." + result.specialTypeDirectoryUrl, result);
            };
            result.textValue = null;
            result.boolValue = null;
            result.numericValue = null;
            result.oneDateValue = null;
            
            result.startDateValue = null;
            result.endDateValue = null;
            result.oneDateValueFormatted = null;
            result.startDateValueFormatted = null;
            result.endDateValueFormatted = null;            
            return result;
        });
    }
    
    function prepareCurrentParamSpecialListForAddParamSet(){
        var result = null;
        result = $scope.currentReportType.reportMetaParamSpecialList.map(function(element){
            var result = {};
            result.paramSpecialCaption = element.paramSpecialCaption;
            result.reportMetaParamSpecialId = element.id;
            result.paramSpecialRequired = element.paramSpecialRequired;
            result.paramSpecialTypeKeyname = element.paramSpecialType.keyname;
            result.reportMetaParamCategory = element.reportMetaParamCategory;
            result.reportMetaParamCategoryOrder = element.reportMetaParamCategory.categoryOrder;
            result.reportMetaParamCategoryKeyname = element.reportMetaParamCategoryKeyname;
            result.reportMetaParamOrder = element.reportMetaParamOrder;
            result.reportMetaParamFullOrder = element.reportMetaParamCategory.categoryOrder * CATEGORY_COEF + (mainSvc.checkUndefinedNull(element.reportMetaParamOrder) ? 0 : element.reportMetaParamOrder);            
                    
            if (isParamSpecialTypeDirectory(element))
            {
                result.specialTypeDirectoryUrl =element.paramSpecialType.specialTypeDirectoryUrl;
                result.specialTypeDirectoryKey =element.paramSpecialType.specialTypeDirectoryKey;
                result.specialTypeDirectoryCaption = element.paramSpecialType.specialTypeDirectoryCaption;
                result.specialTypeDirectoryValue = element.paramSpecialType.specialTypeDirectoryValue;
                $scope.getDirectory(".." + result.specialTypeDirectoryUrl, result);
            };
            result.textValue = null;
            result.boolValue = null;
            result.numericValue = null;
            result.oneDateValue = null;
            
            result.startDateValue = null;
            result.endDateValue = null;
            result.oneDateValueFormatted = null;
            result.startDateValueFormatted = null;
            result.endDateValueFormatted = null;            
            return result;
        });
        
        addCategoryRows(result);
        //sort by special params by full order                        
        mainSvc.sortNumericItemsBy(result, "reportMetaParamFullOrder");
        
        $scope.currentParamSpecialList = result;
    }
    
    $scope.addParamSet = function(object){
        $scope.setCurrentReportType(object);
        //подготавливаем массив специальных параметров, который будет заполнять пользователь
//console.log($scope.currentReportType.reportMetaParamSpecialList);        
        prepareCurrentParamSpecialListForAddParamSet();
//        $scope.paramsetStartDateFormat = (new Date());        
//        $scope.paramsetEndDateFormat= (new Date()); 
        $scope.psStartDateFormatted = moment().format($scope.ctrlSettings.dateFormat);//(new Date());        
        $scope.psEndDateFormatted= moment().format($scope.ctrlSettings.dateFormat);//(new Date());        
//        $scope.paramsetEndDateFormat.setDate($scope.paramsetStartDateFormat.getDate()+1);
        $scope.set_of_objects_flag = false;
        $scope.showAvailableObjects_flag = false;
       
        $scope.createByTemplate_flag = false;
        $scope.createParamset_flag = true;
        $scope.editParamset_flag = false;
        $scope.currentSign = 9999;
        $scope.getTemplates();
        $scope.getAvailableObjects(0); //Ноль используем для вновь созданных объектов - у нас нет другого способа получить доступные объекты для нового парамсета.
        $scope.selectedObjects = [];

//settings for activate tab "Main options", when create window opened.        
//        $('#main_properties_tab').addClass("active");
//        $('#set_of_objects_tab').removeClass("active");
//        $('#createParamsetModal').modal();
        activateMainPropertiesTab();
        
         //set the fields for a new paramset
        $scope.currentObject = {};
            //set default report period
        $scope.currentObject.reportPeriodKey = "CURRENT_MONTH";
            //set file type for report
        $scope.currentObject.outputFileType = $scope.fileTypes[0];
        
        $scope.currentReportPeriod = {};
        
//console.log($scope.currentObject.reportTemplate);        
        $scope.currentObject.showParamsBeforeRunReport = true;       
        $scope.currentObject._active = true;
        $scope.currentObject.paramSpecialList = []; //массив для специальных параметров
    };
    
    //Проверка параметра - ссылочный параметр или нет
    function isParamSpecialTypeDirectory(element){
        var result = (element.paramSpecialType.specialTypeDirectoryUrl != null)
                && (typeof element.paramSpecialType.specialTypeDirectoryUrl != 'undefined')
        && (element.paramSpecialType.specialTypeDirectoryKey != null)
                && (typeof element.paramSpecialType.specialTypeDirectoryKey != 'undefined')
        && (element.paramSpecialType.specialTypeDirectoryCaption != null)
                && (typeof element.paramSpecialType.specialTypeDirectoryCaption != 'undefined')
        && (element.paramSpecialType.specialTypeDirectoryValue != null)
                && (typeof element.paramSpecialType.specialTypeDirectoryValue != 'undefined');       
        return result;
    };
    
    function addCategoryRows (srcArray) {
        var categoryArr = [];
        srcArray.forEach(function(elem){
            var isHaveCurCategory = false;
            categoryArr.forEach(function(category){
                if (category.keyname === elem.reportMetaParamCategory.keyname){
                    isHaveCurCategory = true;
                    return true;
                }
            });
            if (isHaveCurCategory === false){
                categoryArr.push(angular.copy(elem.reportMetaParamCategory));            
            };
        });
        
        var categoriesForDelete = [];
        categoryArr.forEach(function(cat, catInd){
            srcArray.some(function(sp){
                if (sp.reportMetaParamCategory.keyname === cat.keyname &&  sp.paramSpecialTypeKeyname === "SPECIAL_CATEGORY_SWITCH"){
                    categoriesForDelete.push(cat.keyname);
                    return true;
                }                    
            });
        });
                        
//console.log(categoryArr);
//console.log(categoriesForDelete);
        categoriesForDelete.forEach(function(cfd){
            categoryArr.some(function(cat, ind){
                if (cat.keyname === cfd){
                    categoryArr.splice(ind, 1);
                    return true;
                }
            });
        });
        //add category to special param list
        categoryArr.forEach(function(cat){
            var result = angular.copy(cat);
            result.paramSpecialCaption = cat.caption;
            result.paramSpecialTypeKeyname = "SPECIAL_CATEGORY_CAPTION";
            result.reportMetaParamFullOrder = cat.categoryOrder * CATEGORY_COEF - 1;
            result.paramSpecialRequired = false;
            result.reportMetaParamCategory = angular.copy(cat);
            srcArray.push(result);
        });
//console.log(srcArray);        
        
    }
    
    function removeCategoryRowsBeforeSave (spl /* SpecialParamList*/) {
        spl.forEach(function(sp, ind){
            if (sp.paramSpecialTypeKeyname === "SPECIAL_CATEGORY_CAPTION"){
                spl.splice(ind, 1);
            }
        });        
    }
    
    var prepareParamSpecialList = function(paramsetObj, paramsetType){       
            var result = paramsetType.reportMetaParamSpecialList.map(function(element){
                var result = {};
                result.paramSpecialCaption = element.paramSpecialCaption;
                result.reportMetaParamSpecialId = element.id;
                result.paramSpecialRequired = element.paramSpecialRequired;
                result.paramSpecialTypeKeyname = element.paramSpecialType.keyname;
                
                result.reportMetaParamCategory = element.reportMetaParamCategory;
                result.reportMetaParamCategoryOrder = element.reportMetaParamCategory.categoryOrder;
                result.reportMetaParamCategoryKeyname = element.reportMetaParamCategoryKeyname;
                result.reportMetaParamOrder = element.reportMetaParamOrder;
                result.reportMetaParamFullOrder = element.reportMetaParamCategory.categoryOrder * CATEGORY_COEF + (mainSvc.checkUndefinedNull(element.reportMetaParamOrder) ? 0 : element.reportMetaParamOrder);
                
                if (isParamSpecialTypeDirectory(element))
                {
                    result.specialTypeDirectoryUrl = element.paramSpecialType.specialTypeDirectoryUrl;
                    result.specialTypeDirectoryKey = element.paramSpecialType.specialTypeDirectoryKey;
                    result.specialTypeDirectoryCaption = element.paramSpecialType.specialTypeDirectoryCaption;
                    result.specialTypeDirectoryValue = element.paramSpecialType.specialTypeDirectoryValue;
                    $scope.getDirectory(".." + result.specialTypeDirectoryUrl, result);                
                };
                //Ищем значение этого параметра в массиве параметров варианта отчета
                if (paramsetObj.paramSpecialList.length == 0){
                    result.textValue = null;
                    result.boolValue = null;
                    result.numericValue = null;
                    result.oneDateValue = null;
                    result.startDateValue = null;
                    result.endDateValue = null;
                    result.oneDateValueFormatted = null;
                    result.startDateValueFormatted = null;
                    result.endDateValueFormatted = null;
                    result.directoryValue = null;
                    return result;
                }
                var elementIndex = -1;
                paramsetObj.paramSpecialList.some(function(el, index, array){
                    if (el.reportMetaParamSpecialId === element.id){
                        elementIndex = index;
                        return true;
                    }else{
                        return false;
                    }
                });
                if (elementIndex != -1){
                    result.id = paramsetObj.paramSpecialList[elementIndex].id || null;
                    result.textValue = paramsetObj.paramSpecialList[elementIndex].textValue || null;
                    result.boolValue = paramsetObj.paramSpecialList[elementIndex].boolValue || null;
                    result.numericValue = paramsetObj.paramSpecialList[elementIndex].numericValue || null;
                    result.oneDateValue = paramsetObj.paramSpecialList[elementIndex].oneDateValue || null;
                    result.startDateValue = paramsetObj.paramSpecialList[elementIndex].startDateValue || null;
                    result.endDateValue = paramsetObj.paramSpecialList[elementIndex].endDateValue || null;
                    result.oneDateValueFormatted = (paramsetObj.paramSpecialList[elementIndex].oneDateValue == null) ? null : new Date(paramsetObj.paramSpecialList[elementIndex].oneDateValue);
                    result.startDateValueFormatted = (angular.isUndefined(paramsetObj.paramSpecialList[elementIndex].startDateValue) || (paramsetObj.paramSpecialList[elementIndex].startDateValue == null)) ? null : new Date(paramsetObj.paramSpecialList[elementIndex].startDateValue);
                    result.endDateValueFormatted = (paramsetObj.paramSpecialList[elementIndex].endDateValue == null) ? null : new Date(paramsetObj.paramSpecialList[elementIndex].endDateValue);
                    if (mainSvc.checkUndefinedNull(paramsetObj.paramSpecialList[elementIndex].directoryValue)){
                        result.directoryValue = null;
                    } else if (mainSvc.isNumeric(paramsetObj.paramSpecialList[elementIndex].directoryValue)) {
                        result.directoryValue = Number(paramsetObj.paramSpecialList[elementIndex].directoryValue);
                    }else{
                        result.directoryValue = paramsetObj.paramSpecialList[elementIndex].directoryValue;
                    }
                    result.version = paramsetObj.paramSpecialList[elementIndex].version || null;
                }else{
                    result.id = null;
                    result.textValue = null;
                    result.boolValue = null;
                    result.numericValue = null;
                    result.oneDateValue = null;
                    result.startDateValue = null;
                    result.endDateValue = null;
                    result.oneDateValueFormatted = null;
                    result.startDateValueFormatted = null;
                    result.endDateValueFormatted = null;
                    result.directoryValue = null;
                }              
                return result;

            });
        addCategoryRows(result);
        mainSvc.sortNumericItemsBy(result, "reportMetaParamFullOrder");
//console.log(result);        
        return result;
    };
    
    $scope.editParamSet =function(parentObject, object){
//        $scope.setCurrentReportType(parentObject);     
        $scope.selectedItem(parentObject, object);
        $scope.currentParamSpecialList = prepareParamSpecialList(object, $scope.currentReportType);        
        
        $scope.currentObject.showParamsBeforeRunReport = !$scope.currentObject.allRequiredParamsPassed;
//console.log($scope.currentObject.allRequiredParamsPassed);   
        $scope.createParamset_flag = false;
        $scope.editParamset_flag = true;
        $scope.createByTemplate_flag = false;
        $scope.getAvailableObjects(object.id);//получаем доступные объекты для заданного парамсета
        $scope.getSelectedObjects($scope.currentObject.id);
//console.log(object);        
        $scope.currentSign = object.reportPeriod.sign;
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){           
            $scope.paramsetStartDateFormat = (new Date(object.paramsetStartDate));            
            $scope.psStartDateFormatted = ((object.paramsetStartDate != null)) ? moment([$scope.paramsetStartDateFormat.getUTCFullYear(),  $scope.paramsetStartDateFormat.getUTCMonth(), $scope.paramsetStartDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat) : "";
//console.log($scope.psStartDateFormatted);   
            
//            $scope.paramsetStartDateFormatted ={
//                startDate: moment([$scope.paramsetStartDateFormat.getUTCFullYear(), $scope.paramsetStartDateFormat.getUTCMonth(), $scope.paramsetStartDateFormat.getUTCDate()]),
//                endDate : moment([$scope.paramsetStartDateFormat.getUTCFullYear(), $scope.paramsetStartDateFormat.getUTCMonth(), $scope.paramsetStartDateFormat.getUTCDate()])
//            };
//console.log($scope.paramsetStartDateFormatted.startDate);             
            
            $scope.paramsetEndDateFormat = (new Date(object.paramsetEndDate));
            $scope.psEndDateFormatted = (object.paramsetEndDate != null) ? moment([$scope.paramsetEndDateFormat.getUTCFullYear(), $scope.paramsetEndDateFormat.getUTCMonth(), $scope.paramsetEndDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat) : "";
//console.log($scope.psEndDateFormatted); 
            
//            $scope.paramsetEndDateFormatted ={
//                startDate: moment([$scope.paramsetEndDateFormat.getUTCFullYear(), $scope.paramsetEndDateFormat.getUTCMonth(), $scope.paramsetEndDateFormat.getUTCDate()]),
//                endDate : moment([$scope.paramsetEndDateFormat.getUTCFullYear(), $scope.paramsetEndDateFormat.getUTCMonth(), $scope.paramsetEndDateFormat.getUTCDate()])
//            };            
//console.log($scope.paramsetEndDateFormatted.startDate); 
        }
    //settings for activate tab "Main options", when edit window opened.
        $scope.set_of_objects_flag = false;
        $scope.showAvailableObjects_flag = false;
//        $('#main_properties_tab').addClass("active");
//        $('#set_of_objects_tab').removeClass("active");
//        $('#createParamsetModal').modal();       
        activateMainPropertiesTab();
    };
    
     //Account objects
    $scope.availableObjects = [];
    $scope.selectedObjects = [];
    
    $scope.getAvailableObjects = function(paramsetId){      
        var table = $scope.crudTableName + "/" + paramsetId + "/contObject/available";        
        crudGridDataFactory(table).query(function(data){           
            $scope.availableObjects = angular.copy(data);
//            $scope.availableObjects = objectSvc.sortObjectsByFullNameEx($scope.availableObjects); 
        });        
    };
//    $scope.getAvailableObjects();
    $scope.getSelectedObjects = function(paramsetId){
        var table = $scope.crudTableName + "/" + paramsetId + "/contObject";
        crudGridDataFactory(table).query(function(data){
            $scope.selectedObjects = data;
            objectSvc.sortObjectsByFullName($scope.selectedObjects);
        });
    };
    $scope.prepareObjectsList = function(){
        $scope.availableObjectGroups.forEach(function(el){el.selected = false});
        $scope.ctrlSettings.selectedAll = false;
    };
    
    $scope.getGroupObjects = function(group){
        var url = $scope.groupUrl + "/" + group.id + "/contObject";
        crudGridDataFactory(url).query(function(data){           
            group.objects = data;     
        });        
    };    
    
    $scope.getAvailableObjectGroups = function(){         
        crudGridDataFactory($scope.groupUrl).query(function(data){           
            var tempGroupArr = data;
            tempGroupArr.forEach(function(group){
                $scope.getGroupObjects(group);
            });
            mainSvc.sortItemsBy(tempGroupArr, "contGroupName");
            $scope.availableObjectGroups = tempGroupArr;          
        });        
    };
    
    $scope.getAvailableObjectGroups();
    
    $scope.viewAvailableObjects = function(objectGroupFlag){
        $scope.showAvailableObjects_flag = !$scope.showAvailableObjects_flag;
        $scope.showAvailableObjectGroups_flag = objectGroupFlag;
        if (objectGroupFlag){
            $scope.headers.addObjects = "Доступные группы объектов";
            //prepare the object goups to view in table
//            var tmpArr = $scope.availableObjectGroups.map(function(element){
//                var result = element;
//                result.fullName = element.contGroupName;//set the field, which view entity name in table
//                return result;
//            });
            $scope.availableEntities = $scope.availableObjectGroups;//tmpArr;
        }else{
            $scope.headers.addObjects = "Доступные объекты";
            $scope.availableObjects = objectSvc.sortObjectsByFullNameEx($scope.availableObjects);
            $scope.availableEntities = $scope.availableObjects;
        };
    };
    
    $scope.joinObjectsFromSelectedGroups = function(groups){
        var result = [];
        groups.forEach(function(group){
                if(group.selected){
                    Array.prototype.push.apply(result, group.objects);
//                    totalGroupObjects = group.objects;
                };
        });                 
        return result;
    };
    
    $scope.deleteDoublesObjects = function(targetArray){
        var arrLength = targetArray.length;
        while (arrLength >= 2){
            arrLength--;                                               
            if (targetArray[arrLength].fullName === targetArray[arrLength - 1].fullName){                   
                targetArray.splice(arrLength, 1);
            };
        }; 
    };
    
    $scope.addUniqueObjectsFromGroupsToSelectedObjects = function(arrFrom, arrTo){
        for (var j=0; j < arrFrom.length; j++){
            var uniqueFlag = true;
            for (var i = 0; i < arrTo.length; i++){
                if(arrFrom[j].fullName === arrTo[i].fullName){
                    uniqueFlag = false;
                    break;
                };
            };
            if (uniqueFlag){
                arrTo.push(arrFrom[j]);
            };
        }; 
        
    };
    
    $scope.removeGroupObjectsFromAvailableObjects = function(objectsFromGroup, availableObjects){
        for (var j=0; j < objectsFromGroup.length; j++){
            var elementIndex = -1;
            for (var i = 0; i < availableObjects.length; i++){
                if(objectsFromGroup[j].fullName === availableObjects[i].fullName){
                    elementIndex = i;
                    break;
                };
            };
            if (elementIndex >= 0){
                availableObjects.splice(elementIndex,1);
            };
        }; 
    };
    
    var objectPerform = function(addObject_flag, currentObjectId){
        var el = {};
        var arr1 = [];
        var arr2 = [];
        var resultArr = [];
        if ($scope.addObject_flag){           
            arr1 = $scope.availableObjects;
            arr2 = $scope.selectedObjects; 
            resultArr = arr2;
        }else{             
            arr2 = $scope.availableObjects;
            arr1 = $scope.selectedObjects;
            resultArr = arr1;
        };
       
        for (var i = 0; i < arr1.length; i++){
            if (arr1[i].id == $scope.currentObjectId) {               
                el = angular.copy(arr1[i]);
                el.selected = false;
                arr1.splice(i, 1);
                break;
            };
        }
        arr2.push(el);
        
        var tmp = resultArr.map(function(elem){
            return elem.id;
        });     
        return tmp; //Возвращаем массив Id-шников выбранных объектов
    };
    
    
    $scope.getResource = function(url, id) {
        return $resource(url, {
            }, {
                update: {method: 'PUT', params:{reportParamsetId:id}},
                addObject: {method: 'POST', params:{contObjectId: id}},
                removeObject: {method: 'DELETE'}
            });
    };
    
    $scope.removeSelectedObject = function(object){
        $scope.availableObjects.push(object);
        $scope.selectedObjects.splice($scope.selectedObjects.indexOf(object), 1);
        $scope.availableObjects = objectSvc.sortObjectsByFullNameEx($scope.availableObjects);
//console.log($scope.showAvailableObjectGroups_flag);        
//        if ($scope.showAvailableObjectGroups_flag) {
//            $scope.availableObjects = objectSvc.sortObjectsByFullNameEx($scope.availableObjects);            
//        }else{
//            $scope.availableEntities = angular.copy($scope.availableObjects);
//            $scope.availableEntities = objectSvc.sortObjectsByFullNameEx($scope.availableEntities);
//        };
    };
    
    $scope.selectAllAvailableEntities = function(){      
        for (var index = 0; index < $scope.availableEntities.length; index++){         
            $scope.availableEntities[index].selected = $scope.ctrlSettings.selectedAll;
        };
    };
    
    $scope.addSelectedEntities = function(){
    //console.log($scope.availableObjects);
        if ($scope.showAvailableObjectGroups_flag){
            var totalGroupObjects = $scope.joinObjectsFromSelectedGroups($scope.availableEntities);   
//console.log(totalGroupObjects);            
            objectSvc.sortObjectsByFullName(totalGroupObjects);
            //del doubles
            
            $scope.deleteDoublesObjects(totalGroupObjects);
            //add groupObjects to selected objects
                //add only unique objects
            $scope.addUniqueObjectsFromGroupsToSelectedObjects(totalGroupObjects, $scope.selectedObjects);   
            //remove groupObjects from availableObjects
            $scope.removeGroupObjectsFromAvailableObjects(totalGroupObjects, $scope.availableObjects);   
        };
        var tmpArray = angular.copy($scope.availableObjects);
        for(var i = 0; i < $scope.availableObjects.length; i++){
            var curObject = $scope.availableObjects[i];

            if (curObject.selected){
    //console.log(curObject);                            
    // console.log("curObject is performanced");               
                var elem = angular.copy(curObject);
                elem.selected = null;
    //console.log(tmpArray.indexOf(curObject));  
                var elementIndex = -1;
                tmpArray.some(function(element,index,array){
                    if (element.fullName === curObject.fullName){
                        elementIndex = index;
                        return true;
                    }else{
                        return false;
                    }
                });
                tmpArray.splice(elementIndex, 1);
                $scope.selectedObjects.push(elem);
                curObject.selected = null;
            };
        }
        $scope.availableObjects = tmpArray;
        $scope.showAvailableObjects_flag = false;
        objectSvc.sortObjectsByFullName($scope.selectedObjects);
    };
    
    $scope.removeObject = function(object){
        $scope.addObject_flag = false;
        $scope.currentObjectId = object.id;
        objectPerform(false, object.id);

    };
    
    //templates
    $scope.templatesForCurrentParaset = [];
    $scope.getTemplates = function(){        
       var table = "../api/reportTemplate" + $scope.currentReportType.suffix; 
        crudGridDataFactory(table).query(function(data){
            $scope.templatesForCurrentParaset = data;
            //if create new paraset
            if ($scope.createParamset_flag){
                //set default template
                $scope.currentObject.reportTemplate = $scope.templatesForCurrentParaset[0];
            }
        });
    };
    
    $scope.closeSaveObjectModal = function(){
        $('#saveObjectModal').hide();
    };
    
    $scope.$watch('currentObject.reportPeriodKey', function (newKey) {
        //отслеживаем изменение периода у варианта отчета
        if (mainSvc.checkUndefinedNull($scope.reportPeriods)){
            console.log($scope.reportPeriods);
            return "reportPeriods is undefined or null.";
        };
        for (var i = 0; i < $scope.reportPeriods.length; i++){
            if (newKey == $scope.reportPeriods[i].keyname){
                $scope.currentSign = $scope.reportPeriods[i].sign;
                $scope.currentReportPeriod = $scope.reportPeriods[i];
                if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){           
                    $scope.paramsetStartDateFormat = ($scope.currentObject.paramsetStartDate == null) ? null : (new Date($scope.currentObject.paramsetStartDate));
                    $scope.paramsetEndDateFormat = ($scope.currentObject.paramsetEndDate == null) ? null : (new Date($scope.currentObject.paramsetEndDate));
                }
            };
        };
        if (!mainSvc.checkUndefinedNull($scope.currentReportPeriod) && 
            $scope.currentReportPeriod.isSettlementMonth == true){
            if (mainSvc.checkUndefinedNull($scope.currentObject.settlementMonth)){
                $scope.currentObject.settlementMonth = (new Date()).getMonth() + 1;
            };
            if (mainSvc.checkUndefinedNull($scope.currentObject.settlementYear)){
                $scope.currentObject.settlementYear = (new Date()).getFullYear();
            };
            setPropForSettlementMonth();
        }
    }, false);
    
    $scope.isDisabled = function(){
        return $scope.isROfield() || $scope.currentObject.common || !$scope.currentObject._active;
    };
    
    $scope.isSpecialParamDisabled = function (curSp, spl) {        
        var isSPD = false;
        spl.some(function (sp) {
            if (curSp.reportMetaParamCategory.keyname === sp.reportMetaParamCategory.keyname && 
                sp.paramSpecialTypeKeyname === "SPECIAL_CATEGORY_SWITCH" && 
                sp.boolValue !== true){                
                isSPD = true;
                return true;
            }
        });
        return $scope.isDisabled() || isSPD;
    }
    
    $scope.showAddObjectButton = function(){      
        return !$scope.showAvailableObjects_flag && $scope.set_of_objects_flag;
    };
    
    
    //date picker
    $scope.dateOptsParamsetRu = {
        locale : {
            daysOfWeek : [ 'Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб' ],
            firstDay : 1,
            monthNames : [ 'Январь', 'Февраль', 'Март', 'Апрель',
                    'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь',
                    'Октябрь', 'Ноябрь', 'Декабрь' ]
        },
        singleDatePicker: true,
        format: $scope.ctrlSettings.dateFormat
    };
    $(document).ready(function() {
                  $('#inputSingleDateStart').datepicker({
                      dateFormat: "dd.mm.yy",
                      firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                      dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                      monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
                        beforeShow: function(){
                            setTimeout(function(){
                                $('.ui-datepicker-calendar').css("display", "table");
                            }, 1);
                        },
                      onChangeMonthYear: function(){
                            setTimeout(function(){
                                $('.ui-datepicker-calendar').css("display", "table");
                            }, 1);
                        }
                  });
                  $('#inputSingleDateEnd').datepicker({
                      dateFormat: "dd.mm.yy",
                      firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                      dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                      monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
                        beforeShow: function(){
                            setTimeout(function(){
                                $('.ui-datepicker-calendar').css("display", "table");
                            }, 1);
                        },
                      onChangeMonthYear: function(){
                            setTimeout(function(){
                                $('.ui-datepicker-calendar').css("display", "table");
                            }, 1);
                        }
                  });
                  $('#inputStartDate').datepicker({
                      dateFormat: "dd.mm.yy",
                      firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
                      dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
                      monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
                        beforeShow: function(){
                            setTimeout(function(){
                                $('.ui-datepicker-calendar').css("display", "table");
                            }, 1);
                        },
                      onChangeMonthYear: function(){
                            setTimeout(function(){
                                $('.ui-datepicker-calendar').css("display", "table");
                            }, 1);
                        }
                  });
    });
    
    //control visibles
    var setVisibles = function(ctxId){
        var ctxFlag = false;
        var tmp = mainSvc.getContextIds();
        tmp.forEach(function(element){
            if(element.permissionTagId.localeCompare(ctxId) == 0){
                ctxFlag = true;
            };
            var elDOM = document.getElementById(element.permissionTagId);//.style.display = "block";
            if (angular.isUndefined(elDOM) || (elDOM == null)){
                return;
            };              
            $('#' + element.permissionTagId).removeClass('nmc-hide');
        });
//        if (ctxFlag == false){
//            window.location.assign('#/');
//        };
    };
    setVisibles($scope.ctrlSettings.ctxId);
    //listen change of service list
    $rootScope.$on('servicePermissions:loaded', function(){
        setVisibles($scope.ctrlSettings.ctxId);
    });

    //checkers
        //check date interval
    $scope.checkDateInterval = function(left, right){  
        if (!mainSvc.checkStrForDate(left)){
            return false;
        };
        if (!mainSvc.checkStrForDate(right)){
            return false;
        };        
        if ((left == null) || (right == null) || (left == "") || (right == "")){return false;};
        var startDate = mainSvc.strDateToUTC(left, $scope.ctrlSettings.dateFormat);
        var sd = (startDate != null) ? (new Date(startDate)) : null;         
        var endDate = mainSvc.strDateToUTC(right, $scope.ctrlSettings.dateFormat);
        var ed = (endDate != null) ? (new Date(endDate)) : null;                
//        if ((isNaN(startDate.getTime()))|| (isNaN(endDate.getTime()))){return false;};       
        if ((sd == null) || (ed == null)){return false;};               
        return ed >= sd;
    };
        //check fields
//    $scope.checkRequiredFields = function(){
//        var result;
//        if (!($scope.currentObject.hasOwnProperty('reportPeriodKey')) || !($scope.currentObject.hasOwnProperty('reportTemplate'))){
////            $scope.currentObject.showParamsBeforeRunReport = true;
//            return false;
//        };  
//        //interval validate flag
//            //default value    
//        var intervalValidate_flag = true; 
//            //if the paramset use interval
//        if ($scope.currentSign == null){
//                //check interval
//            var startDateMillisec = mainSvc.strDateToUTC($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat);
//            var startDate = new Date(startDateMillisec);
//            var endDateMillisec = mainSvc.strDateToUTC($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat);
//            var endDate = new Date(endDateMillisec);
//            intervalValidate_flag = (!isNaN(startDate.getTime())) || (!isNaN(endDate.getTime())) || $scope.checkDateInterval($scope.psStartDateFormatted, $scope.psEndDateFormatted);
//        };
//        
//        result = !((($scope.currentObject.reportPeriodKey == null) ||   
//        ($scope.currentObject.reportTemplate.id == null)))
//        &&intervalValidate_flag;
////        $scope.currentObject.allRequiredParamsPassed = !result;      
//        return result;
//        
//    };
    
        //check field "outputFileZipped"
    $scope.checkOutputFileZipped = function(){
        if (!$scope.currentReportType.hasOwnProperty('reportMetaParamCommon')){
            return false;
        };
        var result = false;
//console.log($scope.currentReportType);        
        if ($scope.currentReportType.reportMetaParamCommon.manyContObjectsZipOnly && ($scope.selectedObjects.length > 1))
        {
            $scope.currentObject.outputFileZipped = true;
            result = true;
        };
        return result;
    };
        //check fields before save
    $scope.checkRequiredFieldsOnSave = function(){
        $scope.currentObject.psStartDateFormatted = $scope.psStartDateFormatted;
        $scope.currentObject.psEndDateFormatted = $scope.psEndDateFormatted;
        $scope.currentObject.selectedObjects = $scope.selectedObjects;
        //remove special category rows from specialParamList
        removeCategoryRowsBeforeSave($scope.currentParamSpecialList);
        
        $scope.currentObject.currentParamSpecialList = $scope.currentParamSpecialList;
        $scope.currentObject.currentReportPeriod = $scope.currentReportPeriod;
        var checkRes = reportSvc.checkPSRequiredFieldsOnSave($scope.currentReportType, $scope.currentObject, $scope.currentSign, "create");
        $scope.messageForUser = checkRes.message;
        return checkRes.flag;
        
//        if (!$scope.currentReportType.hasOwnProperty('reportMetaParamCommon')){
//            return true;
//        };
//        var result = true;
//        $scope.messageForUser = "Не все параметры варианта отчета заданы:\n";
//        //Check common params before save
//            //file ext
//        if (angular.isUndefined($scope.currentObject.outputFileType) || ($scope.currentObject.outputFileType === null)||($scope.currentObject.outputFileType === "")){
//            $scope.messageForUser += "Основные свойства: " + "\n";
//            $scope.messageForUser += "\u2022"+" Не задан тип файла" + "\n";
//            result = false;
//        };
//            //one date - for future
////        if ($scope.currentReportType.reportMetaParamCommon.oneDateRequired && something)
//        
//            //start date
//            //if the paramset use a date interval
//        if ($scope.currentSign == null){
////            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && $scope.psStartDateFormatted==null)
////            {
////                $scope.messageForUser += "- Не задано начало периода"+"\n";
////            };
//            var startDateMillisec = mainSvc.strDateToUTC($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat);
//            var startDate = new Date(startDateMillisec);
//            var endDateMillisec = mainSvc.strDateToUTC($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat);
//            var endDate = new Date(endDateMillisec); 
//            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && (isNaN(startDate.getTime()) || (!mainSvc.checkStrForDate($scope.psStartDateFormatted))))    
//            {
//                if (result){ $scope.messageForUser += "Основные свойства: " + "\n";};
//                $scope.messageForUser += "\u2022" + " Некорректно задано начало периода" + "\n";
//                result = false;
//            };
//                        //end date
////            if ($scope.currentReportType.reportMetaParamCommon.endDateRequired && $scope.paramsetEndDateFormat==null)
////            if ($scope.currentReportType.reportMetaParamCommon.endDateRequired && $scope.psEndDateFormatted=="")    
////            {
////                $scope.messageForUser += "- Не задан конец периода"+"\n";
////            };
//            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && (isNaN(endDate.getTime()) ||(!mainSvc.checkStrForDate($scope.psEndDateFormatted))))    
//            {
//                if (result){ $scope.messageForUser += "Основные свойства: " + "\n";};
//                $scope.messageForUser += "\u2022" + " Некорректно задан конец периода" + "\n";
//                result = false;
//            };
//            
//            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && !isNaN(endDate.getTime()) && !isNaN(startDate.getTime()) && (startDateMillisec > endDateMillisec))    
//            {
//                if (result){ $scope.messageForUser += "Основные свойства: " + "\n";};
//                $scope.messageForUser += "\u2022" + " Некорректно заданы границы периода" + "\n";
//                result = false;
//            };
//        }
//
//                    //Count of objects
//        if ($scope.currentReportType.reportMetaParamCommon.oneContObjectRequired && ($scope.selectedObjects.length == 0) && $scope.currentReportType.reportMetaParamCommon.manyContObjectRequired)
//        {
//            $scope.messageForUser += "\u2022" + " Должен быть выбран хотя бы один объект" + "\n";
//            result = false;
//        };
//        if ($scope.currentReportType.reportMetaParamCommon.oneContObjectRequired && ($scope.selectedObjects.length == 0) && !$scope.currentReportType.reportMetaParamCommon.manyContObjectRequired)
//        {
//            $scope.messageForUser += "\u2022" + " Необходимо выбрать один объект" + "\n";
//            result= false;
//        };
//        if ($scope.currentReportType.reportMetaParamCommon.manyContObjectRequired && ($scope.selectedObjects.length <= 0))
//        {
//            $scope.messageForUser += "\u2022" + " Необходимо выбрать несколько объектов" + "\n";
//            result = false;
//        };
//        
//        if (!$scope.currentReportType.reportMetaParamCommon.manyContObjectRequired && ($scope.selectedObjects.length > 1) &&  $scope.currentReportType.reportMetaParamCommon.oneContObjectRequired)
//        {
//            $scope.messageForUser += "\u2022" + " Нельзя выбрать более одного объекта" + "\n";
//            result= false;
//        };
//        
//        if ($scope.currentReportType.reportMetaParamCommon.manyContObjectsZipOnly && ($scope.selectedObjects.length > 1))
//        {
//            $scope.currentObject.outputFileZipped =  true;
//        };
//        if (angular.isArray($scope.currentParamSpecialList)){
//            //check special properties
//                var specListFlag = true;
//            $scope.currentParamSpecialList.forEach(function(element, index, array){                 
//                if (element.paramSpecialRequired && !(element.textValue 
//                                                     || element.numericValue 
//                                                     || element.oneDateValue 
//                                                     || element.startDateValue
//                                                     || element.endDateValue
//                                                     || element.directoryValue)
//                   )
//                {
//                    if (specListFlag){ $scope.messageForUser += "Дополнительные свойства: " + "\n";};
//                    $scope.messageForUser += "\u2022" + " Не задан параметр \"" + element.paramSpecialCaption + "\" \n";
//                    result = false;
//                    specListFlag = false;
//                }
//            });
//        };
//        if($scope.messageForUser != "Не все параметры варианта отчета заданы:\n"){
////            $scope.messageForUser = "Внимание!!! \n"+$scope.messageForUser;
//            $scope.messageForUser += "\n Этот вариант отчета нельзя запустить без уточнения или использовать в рассылке, не задав обязательных параметров. Продолжить?";
////            $scope.messageForUser+=" Этот вариант отчета нельзя запустить без уточнения, а также его нельзя использовать в рассылке. Продолжить?";
////            alert($scope.messageForUser);
////            $('#messageForUserModal').modal();
//            result = false;
//        };
//        result = result && $scope.checkRequiredFields();  
//        if (!result){            
//            $scope.currentObject.showParamsBeforeRunReport = true;
//        };
//        return result;
    };
    
        //check user rights
    $scope.isAdmin = function(){
        return mainSvc.isAdmin();
    };

    $scope.isReadonly = function(){
        return mainSvc.isReadonly();
    };
    
    $scope.isTestMode = function () {
        return mainSvc.isTestMode();
    }
    
    $scope.isSystemViewInfo = function () {
        return mainSvc.getViewSystemInfo();
    }

    $scope.isROfield = function(){
        return ($scope.isReadonly());
    };
    
    //work with tabs
    $scope.setActiveTab = function(tabId){
        var tab = document.getElementById('a_teplo_sys');     
        tab.classList.remove("active");
        var tab = document.getElementById('a_electro_sys');     
        tab.classList.remove("active");
        var tab = document.getElementById('a_gas_sys');     
        tab.classList.remove("active");
        var tab = document.getElementById(tabId);     
        tab.classList.add("active");
        
    };
        
    $("#createParamsetModal").on("shown.bs.modal", function(){
        if (Number($scope.currentObject.settlementDay) > 3 && Number($scope.currentObject.settlementDay) <=9){
            $scope.currentObject.settlementDay = "0" + $scope.currentObject.settlementDay;
            $scope.$apply();
        }
        $("#inputSettlementDay").inputmask("d", {placeholder: ""});
        
        $("#inputFileTemplate").inputmask('Regex', { regex: "[a-zA-Z0-9]+"} );
    });
    
    var setPropForSettlementMonth = function(){         
        $('#inputSettlementMonth').datepicker({
          dateFormat: "MM, yy",
          firstDay: $scope.dateOptsParamsetRu.locale.firstDay,
          dayNamesMin: $scope.dateOptsParamsetRu.locale.daysOfWeek,
          monthNames: $scope.dateOptsParamsetRu.locale.monthNames,
            monthNamesShort: ['Янв','Фев','Мар','Апр','Май','Июн','Июл','Авг','Сен','Окт','Ноя','Дек'],
            changeMonth: true,
            changeYear: true,
            showButtonPanel: true,
            closeText: "Ок",
            currentText: "",
            onClose: function(dateText, inst) { 
                $(this).datepicker('setDate', new Date(inst.selectedYear, inst.selectedMonth, 1));
                $scope.currentObject.settlementMonth = inst.selectedMonth + 1;
                $scope.currentObject.settlementYear = inst.selectedYear;               
                setTimeout(function(){
                    $('.ui-datepicker-calendar').addClass("nmc-hide");
                }, 1);
            },
            beforeShow: function(){
                setTimeout(function(){                    
                    $('.ui-datepicker-calendar').addClass("nmc-hide");
                    $('.ui-datepicker-current').addClass("nmc-hide");
                }, 1);
            },
            onChangeMonthYear: function(){
                setTimeout(function(){
                    $('.ui-datepicker-current').addClass("nmc-hide");
                    $('.ui-datepicker-calendar').addClass("nmc-hide");
                }, 1);
            }
        });
        
        $('#inputSettlementMonth').datepicker('setDate', new Date($scope.currentObject.settlementYear, $scope.currentObject.settlementMonth - 1, 1));
    };
    
    // ***************************************************************
    // Work with cont service types
    
    $scope.setCurrentServiceType = function(servType){
        $scope.currentServiceType = angular.copy(servType);        
    };
    
    $scope.setCurrentCategory = function(category){        
        $scope.currentCategory = category;        
    };
    
    $scope.setCurrentServiceType($scope.contServiceTypes[0]);
    
    var filtredCategories = $filter('withReportTypes')($scope.categories);
    if (angular.isArray(filtredCategories) && filtredCategories.length > 0){
        filtredCategories[0].class = "active";
    }
    $scope.setCurrentCategory(filtredCategories[0]);
    // ****************************************************************
    
    $scope.preview = function(reportType){       
        var url = reportType.previewUrl;
        window.open(url);
    };

}]);