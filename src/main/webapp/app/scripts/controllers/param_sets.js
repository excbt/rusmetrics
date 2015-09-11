'use strict';
var app = angular.module('portalNMC');

app.controller('ParamSetsCtrl',['$scope', '$rootScope', '$resource', '$http','crudGridDataFactory','notificationFactory','objectSvc', 'mainSvc' ,function($scope, $rootScope, $resource, $http, crudGridDataFactory, notificationFactory, objectSvc, mainSvc){
    //ctrl settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY"; //date format
    
    $scope.set_of_objects_flag = false; //флаг: истина - открыта вкладка с объектами
    $scope.showAvailableObjects_flag = false; // флаг, устанавливающий видимость окна с доступными объектами
    $scope.currentSign = 9999;// устанавливаем начальное значение отличное от нулл и других возможных значение; нулл - будем отлавливать
    $scope.columns = [
        {"name":"reportType","header":"Тип отчета", "class":"col-md-11"}
    ];
    $scope.paramsetColumns = [
        {"name":"name","header":"Наименование", "class":"col-md-1"}
        ,{"name":"reportTemplateName","header":"Шаблон", "class":"col-md-1"}
        ,{"name":"period","header":"Период", "class":"col-md-1"}
        ,{"name":"fileType","header":"Тип файла", "class":"col-md-1"}
    ];
    $scope.extraProps={"idColumnName":"id", "defaultOrderBy" : "name", "deleteConfirmationProp":"name"};    
    
    $scope.createParamset_flag = false;
    $scope.editParamset_flag = false;
    $scope.createByTemplate_flag = false;
    $scope.currentObject = {};
    $scope.createByTemplate_flag = false;
    $scope.archiveParamset = {};
    $scope.activeStartDateFormat = new Date();
    $scope.activeStartDateFormatted = moment().format($scope.ctrlSettings.dateFormat);
    
    
    
    //file types
    $scope.fileTypes = ["PDF", "HTML", "XLSX"];

    $scope.groupUrl = "../api/contGroup";
    $scope.crudTableName = "../api/reportParamset"; 
    
        //Headers of modal window
    $scope.headers = {}
    $scope.headers.addObjects = "Доступные объекты";//header of add objects window
    
    $scope.objects = [];
    $scope.availableObjectGroups = [];
    
    $scope.isSystemuser = function(){
        $scope.userInfo = $rootScope.userInfo;
        return $scope.userInfo._system;
    };
    
    //report types
    $scope.reportTypes = [];
    $scope.getReportTypes = function(){
        var table = "../api/reportSettings/reportType";
        crudGridDataFactory(table).query(function(data){
            $scope.reportTypes = data;
//console.log(data);
            var newObjects = [];
            var newObject = {};
            for (var i = 0; i<data.length; i++){
                if (!data[i]._enabled){
                    continue;
                };
                if ((!$scope.isSystemuser()&&data[i].isDevMode)){
                    continue;
                };
                newObject = {};
                newObject.reportType = data[i].keyname;
                newObject.reportTypeName = data[i].caption;
                newObject.suffix = data[i].suffix;
                newObject.reportMetaParamSpecialList = data[i].reportMetaParamSpecialList;
                newObject.reportMetaParamCommon = data[i].reportMetaParamCommon;
                    //flag: the toggle visible flag for the special params page.
                newObject.reportMetaParamSpecialList_flag = (data[i].reportMetaParamSpecialList.length>0?true:false);
                
                newObjects.push(newObject);
            };        
            $scope.objects = newObjects; 
//console.log(data);            
            $scope.getActive();
        });
    };
    $scope.getReportTypes();
    
    //report periods
    $scope.reportPeriods = [];
    $scope.getReportPeriods = function(){
        var table = "../api/reportSettings/reportPeriod";
        crudGridDataFactory(table).query(function(data){
            $scope.reportPeriods = data;
        });
    };
    $scope.getReportPeriods();

    var successCallback = function (e) {     
        notificationFactory.success();
        
        $('#moveToArchiveModal').modal('hide');
        if (!$scope.createByTemplate_flag){
            $scope.getActive();
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
        notificationFactory.errorInfo(e.statusText,e.data.description);
    };
    
    $scope.deleteObject = function (object) {
        var table = $scope.crudTableName +"/archive"+ $scope.currentReportType.suffix;
//console.log(table);        
//console.log(object[$scope.extraProps.idColumnName]);        
        crudGridDataFactory(table).delete({ id: object[$scope.extraProps.idColumnName] }, successDeleteCallback, errorCallback);
    };
       
    $scope.getParamsets = function(table, type){
        crudGridDataFactory(table).query(function (data) {
            type.paramsets = data;
            type.paramsetsCount = data.length;
        });
    };
    
 //get paramsets   
    $scope.getActive = function(){
        
        if (($scope.objects == []) || (typeof $scope.objects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.objects.length; i++){
            $scope.getParamsets($scope.crudTableName+$scope.objects[i].suffix, $scope.objects[i]);
        };
    };
    
//    $scope.getActive();
    
    $scope.getArchive = function(){
        if (($scope.objects == []) || (typeof $scope.objects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.objects.length; i++){
            $scope.getParamsets($scope.crudTableName+"/archive"+$scope.objects[i].suffix, $scope.objects[i]);
        };        
    };
    
    
    $scope.toogleShowGroupDetails = function(curObject){//switch option: current goup details
        curObject.showGroupDetails = !curObject.showGroupDetails;
    };
    
   
    $scope.selectedItem = function(parentItem, item){
        $scope.setCurrentReportType(parentItem);       
        var curObject = angular.copy(item);
		$scope.currentObject = curObject;
        $scope.activeStartDateFormat = (curObject.activeStartDate == null) ? null : new Date(curObject.activeStartDate);
        var activeStartDate = new Date(curObject.activeStartDate);
console.log(curObject);        
        $scope.activeStartDateFormatted = (curObject.activeStartDate == null) ? "" : moment([activeStartDate.getUTCFullYear(),activeStartDate.getUTCMonth(), activeStartDate.getUTCDate()]).format($scope.ctrlSettings.dateFormat);
        
        $scope.getTemplates();       
    };
    
    $scope.checkAndSaveParamset = function(object){
//console.log($scope.currentParamSpecialList);        
//return;        
        var flag = $scope.checkRequiredFieldsOnSave();
        if (flag===false){
            $('#messageForUserModal').modal();
        }else{
            $scope.saveParamset(object);
        };
    };
    
    //Save paramset
    $scope.saveParamset = function(object){  
        //close modal window with the message for user
        $('#messageForUserModal').modal('hide');
        
        //perform Special paramset props
        $scope.currentParamSpecialList.forEach(function(element){
            element.oneDateValue = (element.oneDateValueFormatted==null)?null:element.oneDateValueFormatted.getTime();
            element.startDateValue = (element.startDateValueFormatted==null)?null:element.startDateValueFormatted.getTime();
            element.endDateValue = (element.endDateValueFormatted==null)?null:element.endDateValueFormatted.getTime();
        });
        
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
console.log($scope.activeStartDateFormatted);            
console.log(mainSvc.checkStrForDate($scope.activeStartDateFormatted));            
        object.activeStartDate = (!mainSvc.checkStrForDate($scope.activeStartDateFormatted))?null:UTCastdt.getTime(); 
        
        
        //set the param, which define - available auto/manual start report.
        object.allRequiredParamsPassed = !object.showParamsBeforeRunReport;
//console.log(object.allRequiredParamsPassed);        
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){
            //perform start interval           
            var stDate = (new Date(moment($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601             
            var UTCstdt = Date.UTC(stDate.getFullYear(), stDate.getMonth(), stDate.getDate());              
            object.paramsetStartDate = (!isNaN(UTCstdt)) ?new Date(UTCstdt) : null;//(new Date($scope.paramsetStartDateFormat)) /*(new Date($rootScope.reportStart))*/ || null;
            
            //perform end interval
            var endDate = (new Date(moment($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat).format("YYYY-MM-DD"))); //reformat date string to ISO 8601                        
            var UTCenddt = Date.UTC(endDate.getFullYear(), endDate.getMonth(), endDate.getDate()); 
            
            object.paramsetEndDate = (!isNaN(UTCenddt)) ?new Date(UTCenddt) : null;//(new Date($scope.paramsetEndDateFormat)) /*(new Date($rootScope.reportEnd))*/ || null;
        }else{
            object.paramsetStartDate = null;
            object.paramsetEndDate = null;
        }
console.log(object);
//console.log((moment($scope.paramsetStartDateFormatted.startDate).startOf('day')));        
//console.log(typeof (moment($scope.paramsetStartDateFormatted.startDate).startOf('day')));                
        if ($scope.createByTemplate_flag){
            object.id = null;          
//            object.activeStartDate = ($scope.activeStartDateFormat==null)?null:$scope.activeStartDateFormat.getTime();
//            var astDate = (new Date($scope.activeStartDateFormatted)); 

            table = $scope.crudTableName+"/createByTemplate/"+$scope.archiveParamset.id;  
//console.log("$scope.createByTemplate_flag"); 
//console.log(tmp);            
            crudGridDataFactory(table).save({contObjectIds: tmp}, object, successCallback, errorCallback);
            return;
        };
        table=$scope.crudTableName+$scope.currentReportType.suffix;
//console.log($scope.createParamset_flag);        
        if ($scope.createParamset_flag){            
            object._active = true;
            
            crudGridDataFactory(table).save({reportTemplateId: object.reportTemplate.id, contObjectIds: tmp},object, successCallback, errorCallback);
        };
        if ($scope.editParamset_flag){       
            crudGridDataFactory(table).update({reportParamsetId: object.id, contObjectIds: tmp}, object, successCallback, errorCallback);
        };
    };
    
    $scope.toArchive = function(url, id) {
        return $resource(url, {
            }, {
                update: {method: 'PUT', params:{reportParamsetId:id}}
            });
    };
    
    $scope.moveToArchive = function(object){
        var table = $scope.crudTableName+"/archive/move"  
        $scope.toArchive(table, object.id).update({}, object, successCallback, errorCallback);
    };
    
    $scope.createByTemplate =  function(parentObject, object){
//        Установка объектов при создании варианта по архивному шаблону
        $scope.selectedItem(parentObject, object);
        $scope.createByTemplate_flag = true;
        $scope.archiveParamset = {};
        $scope.archiveParamset.id = object.id;
        $scope.archiveParamset.name = object.name;
        $scope.currentParamSpecialList = $scope.archiveParamset.paramSpecialList;
        $scope.currentObject = angular.copy(object);
        $scope.currentObject.id = null;
        $scope.currentObject._active = true;
        $scope.getAvailableObjects(object.id);
        $scope.selectedObjects = [];
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
            })
            .error(function(e){
                console.log(e);
            });
    };
    
    
    $scope.addParamSet = function(object){
        $scope.setCurrentReportType(object);
        //подготавливаем массив специальных параметров, который будет заполнять пользователь
//console.log($scope.currentReportType.reportMetaParamSpecialList);        
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
                result.specialTypeDirectoryValue =element.paramSpecialType.specialTypeDirectoryValue;
                $scope.getDirectory(".."+result.specialTypeDirectoryUrl, result);
            };
            result.textValue=null;
            result.numericValue=null;
            result.oneDateValue=null;
            
            result.startDateValue=null;
            result.endDateValue=null;
            result.oneDateValueFormatted=null;
            result.startDateValueFormatted=null;
            result.endDateValueFormatted=null;
            result.directoryValue=null;
            return result;
        });
//        $scope.paramsetStartDateFormat = (new Date());        
//        $scope.paramsetEndDateFormat= (new Date()); 
        $scope.psStartDateFormatted = moment().format($scope.ctrlSettings.dateFormat);//(new Date());        
        $scope.psEndDateFormatted= moment().format($scope.ctrlSettings.dateFormat);//(new Date());        
//        $scope.paramsetEndDateFormat.setDate($scope.paramsetStartDateFormat.getDate()+1);
        $scope.set_of_objects_flag=false;
        $scope.showAvailableObjects_flag = false;
       
        $scope.createByTemplate_flag = false;
        $scope.createParamset_flag = true;
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
        
//console.log($scope.currentObject.reportTemplate);        
        $scope.currentObject.showParamsBeforeRunReport = true;       
        $scope.currentObject._active = true;
        $scope.currentObject.paramSpecialList = []; //массив для специальных параметров
    };
    
    //Проверка параметра - ссылочный параметр или нет
    function isParamSpecialTypeDirectory(element){
        var result=  (element.paramSpecialType.specialTypeDirectoryUrl!=null)
                && (typeof element.paramSpecialType.specialTypeDirectoryUrl!='undefined')
        && (element.paramSpecialType.specialTypeDirectoryKey!=null)
                && (typeof element.paramSpecialType.specialTypeDirectoryKey!='undefined')
        && (element.paramSpecialType.specialTypeDirectoryCaption!=null)
                && (typeof element.paramSpecialType.specialTypeDirectoryCaption!='undefined')
        && (element.paramSpecialType.specialTypeDirectoryValue!=null)
                && (typeof element.paramSpecialType.specialTypeDirectoryValue!='undefined');       
        return result;
    };
    
    $scope.editParamSet =function(parentObject,object){
//        $scope.setCurrentReportType(parentObject);     
        $scope.selectedItem(parentObject, object);
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
                result.specialTypeDirectoryValue =element.paramSpecialType.specialTypeDirectoryValue;
                $scope.getDirectory(".."+result.specialTypeDirectoryUrl, result);                
            };
            //Ищем значение этого параметра в массиве параметров варианта отчета
            if (object.paramSpecialList.length==0){
                result.textValue = null;
                result.numericValue = null;
                result.oneDateValue = null;
                result.startDateValue = null;
                result.endDateValue = null;
                result.oneDateValueFormatted=null;
                result.startDateValueFormatted=null;
                result.endDateValueFormatted=null;
                result.directoryValue = null;
                return result;
            }
            var elementIndex = -1;
            object.paramSpecialList.some(function(el,index,array){
                if (el.reportMetaParamSpecialId === element.id){
                    elementIndex = index;
                    return true;
                }else{
                    return false;
                }
            });
            if (elementIndex!=-1){
                result.id = object.paramSpecialList[elementIndex].id || null;
                result.textValue = object.paramSpecialList[elementIndex].textValue || null;
                result.numericValue = object.paramSpecialList[elementIndex].numericValue || null;
                result.oneDateValue = object.paramSpecialList[elementIndex].oneDateValue || null;
                result.startDateValue = object.paramSpecialList[elementIndex].startDateValue || null;
                result.endDateValue = object.paramSpecialList[elementIndex].endDateValue || null;
                result.oneDateValueFormatted=(object.paramSpecialList[elementIndex].oneDateValue == null) ? null :new Date(object.paramSpecialList[elementIndex].oneDateValue);
                result.startDateValueFormatted=(object.paramSpecialList[elementIndex].startDateValue == null) ? null :new Date(object.paramSpecialList[elementIndex].startDateValue);
                result.endDateValueFormatted=(object.paramSpecialList[elementIndex].endDateValue == null) ? null :new Date(object.paramSpecialList[elementIndex].endDateValue);
                result.directoryValue = Number(object.paramSpecialList[elementIndex].directoryValue) || null;                
                result.version = object.paramSpecialList[elementIndex].version || null;
            }else{
                result.id = null;
                result.textValue = null;
                result.numericValue = null;
                result.oneDateValue = null;
                result.startDateValue = null;
                result.endDateValue = null;
                result.oneDateValueFormatted=null;
                result.startDateValueFormatted=null;
                result.endDateValueFormatted=null;
                result.directoryValue = null;
            }
            return result;
            
        });
        $scope.currentObject.showParamsBeforeRunReport = !$scope.currentObject.allRequiredParamsPassed;
//console.log($scope.currentObject.allRequiredParamsPassed);   
        $scope.createParamset_flag = false;
        $scope.editParamset_flag = true;
        $scope.createByTemplate_flag = false;
        $scope.getAvailableObjects(object.id);//получаем доступные объекты для заданного парамсета
        $scope.getSelectedObjects();
console.log(object);        
        $scope.currentSign = object.reportPeriod.sign;
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){           
            $scope.paramsetStartDateFormat = (new Date(object.paramsetStartDate));            
            $scope.psStartDateFormatted =((object.paramsetStartDate!=null))?moment([$scope.paramsetStartDateFormat.getUTCFullYear(), $scope.paramsetStartDateFormat.getUTCMonth(), $scope.paramsetStartDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat):"";
console.log($scope.psStartDateFormatted);   
            
//            $scope.paramsetStartDateFormatted ={
//                startDate: moment([$scope.paramsetStartDateFormat.getUTCFullYear(), $scope.paramsetStartDateFormat.getUTCMonth(), $scope.paramsetStartDateFormat.getUTCDate()]),
//                endDate : moment([$scope.paramsetStartDateFormat.getUTCFullYear(), $scope.paramsetStartDateFormat.getUTCMonth(), $scope.paramsetStartDateFormat.getUTCDate()])
//            };
//console.log($scope.paramsetStartDateFormatted.startDate);             
            
            $scope.paramsetEndDateFormat= (new Date(object.paramsetEndDate));
            $scope.psEndDateFormatted =(object.paramsetEndDate!=null)?moment([$scope.paramsetEndDateFormat.getUTCFullYear(), $scope.paramsetEndDateFormat.getUTCMonth(), $scope.paramsetEndDateFormat.getUTCDate()]).format($scope.ctrlSettings.dateFormat):"";
console.log($scope.psEndDateFormatted); 
            
//            $scope.paramsetEndDateFormatted ={
//                startDate: moment([$scope.paramsetEndDateFormat.getUTCFullYear(), $scope.paramsetEndDateFormat.getUTCMonth(), $scope.paramsetEndDateFormat.getUTCDate()]),
//                endDate : moment([$scope.paramsetEndDateFormat.getUTCFullYear(), $scope.paramsetEndDateFormat.getUTCMonth(), $scope.paramsetEndDateFormat.getUTCDate()])
//            };            
//console.log($scope.paramsetEndDateFormatted.startDate); 
        }
    //settings for activate tab "Main options", when edit window opened.
        $scope.set_of_objects_flag=false;
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
        var table=$scope.crudTableName+"/"+paramsetId+"/contObject/available";        
        crudGridDataFactory(table).query(function(data){           
            $scope.availableObjects = data;
            objectSvc.sortObjectsByFullName($scope.availableObjects); 
        });        
    };
//    $scope.getAvailableObjects();
    $scope.getSelectedObjects = function(){
        var table=$scope.crudTableName+"/"+$scope.currentObject.id+"/contObject";
        crudGridDataFactory(table).query(function(data){
            $scope.selectedObjects = data;
            objectSvc.sortObjectsByFullName($scope.selectedObjects);
        });
    };
$scope.prepareObjectsList = function(){
        $scope.availableObjectGroups.forEach(function(el){el.selected = false});
    };
    
    $scope.getGroupObjects = function(group){
        var url = $scope.groupUrl+"/"+group.id+"/contObject";
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
            $scope.availableObjectGroups = tempGroupArr;          
        });        
    };
    
    $scope.getAvailableObjectGroups();
    
    $scope.viewAvailableObjects = function(objectGroupFlag){
        $scope.showAvailableObjects_flag=!$scope.showAvailableObjects_flag;
        $scope.showAvailableObjectGroups_flag=objectGroupFlag;
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
        while (arrLength>=2){
            arrLength--;                                               
            if (targetArray[arrLength].fullName===targetArray[arrLength-1].fullName){                   
                targetArray.splice(arrLength, 1);
            };
        }; 
    };
    
    $scope.addUniqueObjectsFromGroupsToSelectedObjects = function(arrFrom, arrTo){
        for (var j=0; j < arrFrom.length; j++){
            var uniqueFlag = true;
            for (var i = 0; i<arrTo.length; i++){
                if(arrFrom[j].fullName===arrTo[i].fullName){
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
            for (var i = 0; i<availableObjects.length; i++){
                if(objectsFromGroup[j].fullName===availableObjects[i].fullName){
                    elementIndex = i;
                    break;
                };
            };
            if (elementIndex>=0){
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
       
        for (var i=0; i<arr1.length;i++){
            if (arr1[i].id == $scope.currentObjectId) {               
                el = angular.copy(arr1[i]);
                el.selected = false;
                arr1.splice(i,1);
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
        objectSvc.sortObjectsByFullName($scope.availableObjects);
    }
    
    $scope.addSelectedEntities = function(){
    //console.log($scope.availableObjects);
        if ($scope.showAvailableObjectGroups_flag){
            var totalGroupObjects = $scope.joinObjectsFromSelectedGroups($scope.availableEntities);   
console.log(totalGroupObjects);            
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
        for(var i =0; i<$scope.availableObjects.length; i++){
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
        $scope.showAvailableObjects_flag=false;
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
       var table = "../api/reportTemplate"+$scope.currentReportType.suffix; 
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
        for (var i = 0; i<$scope.reportPeriods.length;i++){
            if (newKey == $scope.reportPeriods[i].keyname){
                $scope.currentSign = $scope.reportPeriods[i].sign;
                if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){           
                    $scope.paramsetStartDateFormat = ($scope.currentObject.paramsetStartDate == null) ? null : (new Date($scope.currentObject.paramsetStartDate));
                    $scope.paramsetEndDateFormat= ($scope.currentObject.paramsetEndDate == null) ? null : (new Date($scope.currentObject.paramsetEndDate));
                }
            };
        };
              
    }, false);
    
    $scope.isDisabled = function(){
        return $scope.currentObject.common || !$scope.currentObject._active;
    };
    
    $scope.showAddObjectButton = function(){      
        return !$scope.showAvailableObjects_flag && $scope.set_of_objects_flag;
    };
    
    
    //date picker
    $scope.dateOptsParamsetRu ={
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
                  $('#inputSingleDateStart').daterangepicker(
                      { 
                        locale : $scope.dateOptsParamsetRu.locale,
                        singleDatePicker: $scope.dateOptsParamsetRu.singleDatePicker,
                        format: $scope.dateOptsParamsetRu.format
                      }, 
                      function(start, end, label) {
//                        console.log(start.toISOString(), end.toISOString(), label);
                        }
                  );
        
                  $('#inputSingleDateEnd').daterangepicker(
                      { 
                        locale : $scope.dateOptsParamsetRu.locale,
                        singleDatePicker: $scope.dateOptsParamsetRu.singleDatePicker,
                        format: $scope.dateOptsParamsetRu.format
                      }, 
                      function(start, end, label) {
//                        console.log(start.toISOString(), end.toISOString(), label);
                        }
                  );
        
                  $('#inputStartDate').daterangepicker(
                      { 
                        locale : $scope.dateOptsParamsetRu.locale,
                        singleDatePicker: $scope.dateOptsParamsetRu.singleDatePicker,
                        format: $scope.dateOptsParamsetRu.format
                      }, 
                      function(start, end, label) {
//                        console.log(start.toISOString(), end.toISOString(), label);
                        }
                  );
    });
    
            //key down listener
    window.onkeydown = function(e){ 
//        console.log(e.keyCode);
        if ((e.keyCode == 27)||(e.keyCode==9)){//ESC or TAB pressed
//            $('#inputSingleDateStart').daterangepicker('hide');
            var datePikeckerDiv = document.getElementsByClassName('daterangepicker dropdown-menu single opensright show-calendar');
//console.log(datePikeckerDiv);            
            if (angular.isDefined(datePikeckerDiv)){
                for(var i = 0; i<datePikeckerDiv.length;i++){
                    datePikeckerDiv[i].style.display = 'none';
                };
            };
        };
    };
    
    //checkers
        //check date interval
    $scope.checkDateInterval = function(left, right){  
        if (!mainSvc.checkStrForDate(left)){
            return false;
        };
        if (!mainSvc.checkStrForDate(right)){
            return false;
        };        
        if ((left==null)|| (right==null)||(left=="")||(right=="")){return false;};
        var startDate = mainSvc.strDateToUTC(left, $scope.ctrlSettings.dateFormat);
        var sd = (startDate!=null)?(new Date(startDate)) : null;         
        var endDate = mainSvc.strDateToUTC(right, $scope.ctrlSettings.dateFormat);
        var ed = (endDate!=null)?(new Date(endDate)) : null;                
//        if ((isNaN(startDate.getTime()))|| (isNaN(endDate.getTime()))){return false;};       
        if ((sd==null)|| (ed==null)){return false;};               
        return ed>=sd;
    };
        //check fields
    $scope.checkRequiredFields = function(){
        var result;
        if (!($scope.currentObject.hasOwnProperty('reportPeriodKey'))||!($scope.currentObject.hasOwnProperty('reportTemplate'))){
//            $scope.currentObject.showParamsBeforeRunReport = true;
            return false;
        };  
        //interval validate flag
            //default value    
        var intervalValidate_flag = true; 
            //if the paramset use interval
        if ($scope.currentSign==null){
                //check interval
            var startDateMillisec = mainSvc.strDateToUTC($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat);
            var startDate = new Date(startDateMillisec);
            var endDateMillisec = mainSvc.strDateToUTC($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat);
            var endDate = new Date(endDateMillisec);
            intervalValidate_flag = (!isNaN(startDate.getTime()))||(!isNaN(endDate.getTime()))||$scope.checkDateInterval($scope.psStartDateFormatted, $scope.psEndDateFormatted);
        };
        
        result = !((($scope.currentObject.reportPeriodKey==null) ||   
        ($scope.currentObject.reportTemplate.id==null)))
        &&intervalValidate_flag;
//        $scope.currentObject.allRequiredParamsPassed = !result;      
        return result;
        
    };
    
        //check field "outputFileZipped"
    $scope.checkOutputFileZipped = function(){
        if (!$scope.currentReportType.hasOwnProperty('reportMetaParamCommon')){
            return false;
        };
        var result = false;
//console.log($scope.currentReportType);        
        if ($scope.currentReportType.reportMetaParamCommon.manyContObjectsZipOnly && ($scope.selectedObjects.length>1))
        {
            $scope.currentObject.outputFileZipped =  true;
            result= true;
        };
        return result;
    };
        //check fields before save
    $scope.checkRequiredFieldsOnSave = function(){
        if (!$scope.currentReportType.hasOwnProperty('reportMetaParamCommon')){
            return true;
        };
        var result= true;
        $scope.messageForUser = "Не все параметры варианта отчета заданы:\n";
        //Check common params before save
            //one date - for future
//        if ($scope.currentReportType.reportMetaParamCommon.oneDateRequired && something)
        
            //start date
            //if the paramset use a date interval
        if ($scope.currentSign==null){
//            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && $scope.psStartDateFormatted==null)
//            {
//                $scope.messageForUser += "- Не задано начало периода"+"\n";
//            };
            var startDateMillisec = mainSvc.strDateToUTC($scope.psStartDateFormatted, $scope.ctrlSettings.dateFormat);
            var startDate = new Date(startDateMillisec);
            var endDateMillisec = mainSvc.strDateToUTC($scope.psEndDateFormatted, $scope.ctrlSettings.dateFormat);
            var endDate = new Date(endDateMillisec); 
            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && (isNaN(startDate.getTime())||(!mainSvc.checkStrForDate($scope.psStartDateFormatted))))    
            {
                $scope.messageForUser += "- Некорректно задано начало периода"+"\n";
            };
                        //end date
//            if ($scope.currentReportType.reportMetaParamCommon.endDateRequired && $scope.paramsetEndDateFormat==null)
//            if ($scope.currentReportType.reportMetaParamCommon.endDateRequired && $scope.psEndDateFormatted=="")    
//            {
//                $scope.messageForUser += "- Не задан конец периода"+"\n";
//            };
            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && (isNaN(endDate.getTime())||(!mainSvc.checkStrForDate($scope.psEndDateFormatted))))    
            {
                $scope.messageForUser += "- Некорректно задан конец периода"+"\n";
            };
            
            if ($scope.currentReportType.reportMetaParamCommon.startDateRequired && !isNaN(endDate.getTime())&& !isNaN(startDate.getTime())&&(startDateMillisec>endDateMillisec))    
            {
                $scope.messageForUser += "- Некорректно заданы границы периода"+"\n";
            };
        }

                    //Count of objects
        if ($scope.currentReportType.reportMetaParamCommon.oneContObjectRequired && ($scope.selectedObjects.length==0))
        {
            $scope.messageForUser += "- Должен быть выбран хотя бы один объект"+"\n";
        };
        if ($scope.currentReportType.reportMetaParamCommon.manyContObjectRequired && ($scope.selectedObjects.length<=0))
        {
            $scope.messageForUser += "- Необходимо выбрать несколько объектов"+"\n";
        };
        
        if ($scope.currentReportType.reportMetaParamCommon.manyContObjectsZipOnly && ($scope.selectedObjects.length>1))
        {
            $scope.currentObject.outputFileZipped =  true;
        };
        
        $scope.currentParamSpecialList.forEach(function(element, index, array){
            if (element.paramSpecialRequired && !(element.textValue 
                                                 || element.numericValue 
                                                 || element.oneDateValue 
                                                 || element.startDateValue
                                                 || element.endDateValue
                                                 || element.directoryValue)
               )
            {
                $scope.messageForUser += "- Не задан параметр \""+element.paramSpecialCaption+"\" \n";
            }
        });
        if($scope.messageForUser!="Не все параметры варианта отчета заданы:\n"){
//            $scope.messageForUser = "Внимание!!! \n"+$scope.messageForUser;
            $scope.messageForUser += "\n Этот вариант отчета нельзя запустить без уточнения или использовать в рассылке, не задав обязательных параметров. Продолжить?";
//            $scope.messageForUser+=" Этот вариант отчета нельзя запустить без уточнения, а также его нельзя использовать в рассылке. Продолжить?";
//            alert($scope.messageForUser);
//            $('#messageForUserModal').modal();
            result= false;
        };
        result =result && $scope.checkRequiredFields();  
        if (!result){            
            $scope.currentObject.showParamsBeforeRunReport = true;
        };
        return result;
    };

}]);