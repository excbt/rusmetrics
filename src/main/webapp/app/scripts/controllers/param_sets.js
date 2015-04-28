'use strict';
var app = angular.module('portalNMK');

app.controller('ParamSetsCtrl',['$scope', '$rootScope', '$resource','crudGridDataFactory','notificationFactory',function($scope, $rootScope, $resource, crudGridDataFactory, notificationFactory){
    
    $scope.set_of_objects_flag = false;
    $scope.currentSign = 9999;// устанавливаем начальное значение отличное от нулл и других возможных значение; нулл - будем отлавливать
    $scope.columns = [
        {"name":"reportType","header":"Тип отчета", "class":"col-md-11"}
    ];
 
    $scope.createParamset_flag = false;
    $scope.editParamset_flag = false;
    $scope.createByTemplate_flag = false;
    $scope.currentObject = {};
    $scope.createByTemplate_flag = false;
    $scope.archiveParamset = {};
    $scope.activeStartDateFormat = new Date();

    $scope.crudTableName = "../api/reportParamset"; 
    
    $scope.objects = [];
    
    //report types
    $scope.reportTypes = [];
    $scope.getReportTypes = function(){
        var table = "../api/reportSettings/reportType";
        crudGridDataFactory(table).query(function(data){
            $scope.reportTypes = data;
            var newObjects = [];
            var newObject = {};
            for (var i = 0; i<data.length; i++){
                if (!data[i]._enabled){
                    continue;
                };
                newObject = {};
                newObject.reportType = data[i].keyname;
                newObject.reportTypeName = data[i].caption;
                newObject.suffix = data[i].suffix;
                
                newObjects.push(newObject);
            };        
            $scope.objects = newObjects;        
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


    var errorCallback = function (e) {
        notificationFactory.error(e.data.ExceptionMessage);
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
        
        $scope.getTemplates();       
    };
      
    $scope.saveParamset = function(object){    
        var table="";       
        var tmp = $scope.selectedObjects.map(function(elem){
            return elem.id;
        });
        object.activeStartDate = ($scope.activeStartDateFormat==null)?null:$scope.activeStartDateFormat.getTime();    
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){
            object.paramsetStartDate = (new Date($scope.paramsetStartDateFormat)) /*(new Date($rootScope.reportStart))*/ || null;
            object.paramsetEndDate = (new Date($scope.paramsetEndDateFormat)) /*(new Date($rootScope.reportEnd))*/ || null;
        }

        if ($scope.createByTemplate_flag){
            object.id = null;          
            object.activeStartDate = ($scope.activeStartDateFormat==null)?null:$scope.activeStartDateFormat.getTime();
            table = $scope.crudTableName+"/createByTemplate/"+$scope.archiveParamset.id;    
            crudGridDataFactory(table).save({contObjectIds: tmp}, object, successCallback, errorCallback);
            return;
        };
        table=$scope.crudTableName+$scope.currentReportType.suffix;
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
    
    $scope.createByTemplate =  function(object){
//        Установка объектов при создании варианта по архивному шаблону
        $scope.createByTemplate_flag = true;
        $scope.archiveParamset = {};
        $scope.archiveParamset.id = object.id;
        $scope.archiveParamset.name = object.name;
        $scope.currentObject = angular.copy(object);
        $scope.currentObject.id = null;
        $scope.currentObject._active = true;
        $scope.getAvailableObjects(object.id);
        $scope.selectedObjects = [];
    };
    
    $scope.setDefault = function(){
//        Сброс всех вспомогательных объектов в дефолтное состояние
        $scope.currentObject = {};
        $scope.createByTemplate_flag = false;
        $scope.archiveParamset = {};
        $scope.activeStartDateFormat = null;
        $scope.currentReportType = {};
        $scope.createParamset_flag = false;
        $scope.editParamset_flag = false;
        $scope.set_of_objects_flag = false;
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
        $scope.currentReportType.reportType = object.reportType;
        $scope.currentReportType.reportTypeName=object.reportTypeName;
        $scope.currentReportType.suffix=object.suffix;
    };
    
    var activateMainPropertiesTab = function(){
        $('#main_properties_tab').addClass("active");
        $('#set_of_objects_tab').removeClass("active");
        $('#createParamsetModal').modal();
    };
    
    $scope.addParamSet = function(object){
        $scope.set_of_objects_flag=false;
        $scope.setCurrentReportType(object);
        $scope.currentObject = {};
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
    };
    $scope.editParamSet =function(parentObject,object){
//        $scope.setCurrentReportType(parentObject);
        
        $scope.selectedItem(parentObject, object);
        $scope.editParamset_flag = true;
        $scope.createByTemplate_flag = false;
        $scope.getAvailableObjects(object.id);//получаем доступные объекты для заданного парамсета
        $scope.getSelectedObjects();
        
        $scope.currentSign = object.reportPeriod.sign;
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){           
            $scope.paramsetStartDateFormat = (new Date(object.paramsetStartDate));
            $scope.paramsetEndDateFormat= (new Date(object.paramsetEndDate));
        }
    //settings for activate tab "Main options", when edit window opened.
        $scope.set_of_objects_flag=false;
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
        });        
    };
//    $scope.getAvailableObjects();
    $scope.getSelectedObjects = function(){
        var table=$scope.crudTableName+"/"+$scope.currentObject.id+"/contObject";
        crudGridDataFactory(table).query(function(data){
            $scope.selectedObjects = data;
        });
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
 
    $scope.addObject = function(object){
        $scope.addObject_flag = true;
        $scope.currentObjectId = object.id;
        objectPerform(true, object.id);

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
            };
        };
              
    }, false);
    
}]);