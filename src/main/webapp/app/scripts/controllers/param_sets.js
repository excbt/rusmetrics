'use strict';
var app = angular.module('portalNMK');

app.controller('ParamSetsCtrl',['$scope', '$rootScope', '$resource','crudGridDataFactory','notificationFactory',function($scope, $rootScope, $resource, crudGridDataFactory, notificationFactory){
    
    //$scope.active_tab_active_templates = true;
    $scope.set_of_objects_flag = false;
    $scope.currentSign = 9999;// устанавливаем начальное значение отличное от нулл и других возможных значение; нулл - будем отлавливать
    $scope.columns = [
        {"name":"reportType","header":"Тип отчета", "class":"col-md-11"}
//        ,{"name":"templatesCount", "header":"Кол-во шаблонов", "class":"col-md-1"}
    ];
        
//    $scope.oldColumns = [
//        {"name":"reportSetName", "header":"Название варианта", "class":"col-md-5"}
//        ,{"name":"reportSetPeriod", "header":"Интервал", "class":"col-md-2"}
//        ,{"name":"reportSetFileType", "header":"Тип файла", "class":"col-md-2"}
//        ,{"name":"reportSetCreateDate", "header":"Дата создания", "class":"col-md-2"}
//        ,{"name":"reportSetArchiveDate", "header":"Дата архивирования", "class":"col-md-2"}
//    ];
    
//    $scope.oldObjects = [
//        {"reportSetName":"Набор параметров 1"
//         ,"reportSetPeriod": "Вчера"
//         ,"reportSetFileType": "PDF"
//         ,"reportSetCreateDate":"11.11.1111"
//        }
//        , {"reportSetName":"Набор параметров 2"
//         ,"reportSetPeriod": "Прошлый месяц"
//         ,"reportSetFileType": "XSL"
//        ,"reportSetCreateDate":"21.11.1112"
//        }
//    ];
    
    
    $scope.createParamset_flag = false;
    $scope.editParamset_flag = false;
    $scope.createByTemplate_flag = false;
    $scope.currentObject = {};
    $scope.createByTemplate_flag = false;
    $scope.archiveParamset = {};
    $scope.activeStartDateFormat = new Date();
    
    
//    $scope.commerce = {};
//    $scope.cons = {};
//    $scope.event = {};
    
    
    $scope.crudTableName = "../api/reportParamset"; 
    
    $scope.objects = [
//        {
//            "reportType":"COMMERCE_REPORT"
//            ,"reportTypeName":"Коммерческий"
//            ,"paramsetsCount": 0
//            ,"paramsets": []
//        }
//        ,        {
//            "reportType":"CONS_REPORT"
//            ,"reportTypeName":"Сводный"
//            ,"paramsetsCount":0
//            ,"paramsets": []
//        }
//        ,        {
//            "reportType":"EVENT_REPORT"
//            ,"reportTypeName":"События"
//            ,"paramsetsCount": 0
//            ,"paramsets": []
//        }
    ];
    
    //report types
    $scope.reportTypes = [];
    $scope.getReportTypes = function(){
        var table = "../api/reportSettings/reportType";
        crudGridDataFactory(table).query(function(data){
            $scope.reportTypes = data;
            var newObjects = [];
            var newObject = {};
            for (var i = 0; i<data.length; i++){
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
//console.log("Сохранил вариант.");  
//console.log("$scope.set_of_objects_flag = "+$scope.set_of_objects_flag);        
        notificationFactory.success();
        
        $('#moveToArchiveModal').modal('hide');
        //$scope.currentObject={};
        if (!$scope.createByTemplate_flag){
            $scope.getActive();
        };
//        if ($scope.set_of_objects_flag){
//            $scope.currentObject = e;
//            $scope.getAvailableObjects();
//            $scope.getSelectedObjects();
//        }else{
            $('#createParamsetModal').modal('hide');
            $scope.setDefault();
            
        //}
        
    };


    var errorCallback = function (e) {
        notificationFactory.error(e.data.ExceptionMessage);
//        if ($scope.set_of_objects_flag){
//            $scope.getAvailableObjects();
//            $scope.getSelectedObjects();
//        }else{
//            $scope.setDefault();
//        }
    };
       
    $scope.getParamsets = function(table, type){
        crudGridDataFactory(table).query(function (data) {
            type.paramsets = data;
            type.paramsetsCount = data.length;
        });
    };
    
//    $scope.getCommerceParamsets = function (table) {
//        crudGridDataFactory(table).query(function (data) {
//            $scope.commerce.paramsets = data;
//            
//            $scope.objects[0].paramsetsCount = $scope.commerce.paramsets.length;
//            $scope.objects[0].paramsets = $scope.commerce.paramsets;
//        });
//    };
//    $scope.getConsParamsets = function (table) {
//        crudGridDataFactory(table).query(function (data) {
//            $scope.cons.paramsets = data; 
//           
//            $scope.objects[1].paramsetsCount = $scope.cons.paramsets.length;
//            $scope.objects[1].paramsets = $scope.cons.paramsets;
//        });
//    };
//    $scope.getEventParamsets = function (table) {
//        crudGridDataFactory(table).query(function (data) {
//            $scope.event.paramsets = data;
//            
//            $scope.objects[2].paramsetsCount = $scope.event.paramsets.length;
//            $scope.objects[2].paramsets = $scope.event.paramsets;
//        });
//    };
 //get paramsets   
    $scope.getActive = function(){
        
        if (($scope.objects == []) || (typeof $scope.objects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.objects.length; i++){
            $scope.getParamsets($scope.crudTableName+$scope.objects[i].suffix, $scope.objects[i]);
        };
        
//        $scope.getCommerceParamsets($scope.crudTableName+"/commerce");
//        $scope.getConsParamsets($scope.crudTableName+"/cons");
//        $scope.getEventParamsets($scope.crudTableName+"/event");
    };
    
//    $scope.getActive();
    
    $scope.getArchive = function(){
        if (($scope.objects == []) || (typeof $scope.objects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.objects.length; i++){
            $scope.getParamsets($scope.crudTableName+"/archive"+$scope.objects[i].suffix, $scope.objects[i]);
        };        
//        $scope.getCommerceParamsets($scope.crudTableName+"/archive/commerce");
//        $scope.getConsParamsets($scope.crudTableName+"/archive/cons");
//        $scope.getEventParamsets($scope.crudTableName+"/archive/event");
    };
    
    
    $scope.toogleShowGroupDetails = function(curObject){//switch option: current goup details
        curObject.showGroupDetails = !curObject.showGroupDetails;
    };
    
   
    $scope.selectedItem = function(parentItem, item){
        $scope.setCurrentReportType(parentItem);
//console.log("In selected item.");        
        var curObject = angular.copy(item);
		$scope.currentObject = curObject;
        $scope.activeStartDateFormat = (curObject.activeStartDate == null) ? null : new Date(curObject.activeStartDate);
        
        $scope.getTemplates();
//console.log("Selected item = ");        
//for(var k in $scope.currentObject){
//console.log("$scope.currentObject["+k+"]="+$scope.currentObject[k]);    
//}        
    };
    
//    $scope.updateParamsets = function(object){
//        var table = "";
//        
//                
//        switch (object.reportTypeKey){
//            case "COMMERCE_REPORT":  table=$scope.crudTableName+"/commerce/"; break;   
//            case "CONS_REPORT":  table=$scope.crudTableName+"/cons/"; break;   
//            case "EVENT_REPORT":  table=$scope.crudTableName+"/event/"; break;       
//        };
//        
//    };
    
    $scope.saveParamset = function(object){
//console.log("In saveParamset. $scope.createParamset_flag = "+$scope.createParamset_flag);
//console.log("$scope.editParamset_flag = "+$scope.editParamset_flag);        
//console.log("$scope.createByTemplate_flag = "+$scope.createByTemplate_flag);                
        var table="";
        
        var tmp = $scope.selectedObjects.map(function(elem){
            return elem.id;
        });
        object.activeStartDate = ($scope.activeStartDateFormat==null)?null:$scope.activeStartDateFormat.getTime();    
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){
            object.paramsetStartDate = (new Date($rootScope.reportStart)) || null;
            object.paramsetEndDate = (new Date($rootScope.reportEnd)) || null;
        }

        
        if ($scope.createByTemplate_flag){
            object.activeStartDate = ($scope.activeStartDateFormat==null)?null:$scope.activeStartDateFormat.getTime();
            table = $scope.crudTableName+"/createByTemplate/"+$scope.archiveParamset.id;    
            crudGridDataFactory(table).save({contObjectIds: tmp}, object, successCallback, errorCallback);
            return;
        };
        table=$scope.crudTableName+$scope.currentReportType.suffix;
//        switch ($scope.currentReportType.reportType){
//                case "COMMERCE_REPORT":  table=$scope.crudTableName+"/commerce"; break;   
//                case "CONS_REPORT":  table=$scope.crudTableName+"/cons"; break;   
//                case "EVENT_REPORT":  table=$scope.crudTableName+"/event"; break;       
//            };
        if ($scope.createParamset_flag){            
            object._active = true;
            
            crudGridDataFactory(table).save({reportTemplateId: object.reportTemplate.id, contObjectIds: tmp},object, successCallback, errorCallback);
        };
        if ($scope.editParamset_flag){
//            table = table+"?contObjectIds="+tmp;         
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
    
//    $scope.createTemplate = function(object){
//        var table = $scope.crudTableName+"/archive/move"    
//        crudGridDataFactory(table).save(object, successCallback, errorCallback);
//    };
    
    $scope.createByTemplate =  function(object){
//        Установка объектов при создании варианта по архивному шаблону
        $scope.createByTemplate_flag = true;
        $scope.archiveParamset = {};
        $scope.archiveParamset.id = object.id;
        $scope.archiveParamset.name = object.name;
        $scope.currentObject = angular.copy(object);//{};
        $scope.currentObject._active = true;

        $scope.getAvailableObjects(object.id);
    
        //$scope.currentObject.name = angular.copy(object.name);
        //$scope.currentObject.description = angular.copy(object.description);
        
        
//        var table = $scope.crudTableName+"/createByTemplate/"    
//        crudGridDataFactory(table).save({srcReportTemplateId: id},object, successCallback, errorCallback);
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
        $scope.editInterval = false;
        //Устанавливаем активность вкладок по дефолту: Основные свойства - активная, Выбор объектов - неактивная
        $('#main_properties_tab').addClass("active");
        $('#main_properties').addClass("active");
        $('#set_of_objects_tab').removeClass("active");
        $('#set_of_objects').removeClass("active");
        
    };
    
    $scope.currentReportType = {};
    $scope.setCurrentReportType = function(object){
        $scope.currentReportType.reportType = object.reportType;
        $scope.currentReportType.reportTypeName=object.reportTypeName;
        $scope.currentReportType.suffix=object.suffix;
    };
    $scope.addParamSet = function(object){
        $scope.setCurrentReportType(object);
        $scope.currentObject = {};
        $scope.createParamset_flag = true;
        $scope.getTemplates();
        $scope.getAvailableObjects(0); //Ноль используем для вновь созданных объектов - у нас нет другого способа получить доступные объекты для нового парамсета.
        $scope.selectedObjects = [];
    };
    $scope.editParamSet =function(parentObject,object){
//        $scope.setCurrentReportType(parentObject);
        $scope.selectedItem(parentObject, object);
        $scope.editParamset_flag = true;
        $scope.editInterval = false;
        $scope.getAvailableObjects(object.id);//получаем доступные объекты для заданного парамсета
        $scope.getSelectedObjects();
        
        $scope.currentSign = object.reportPeriod.sign;
        if (($scope.currentSign == null) || (typeof $scope.currentSign == 'undefined')){           
            $scope.paramsetStartDateFormat = moment(object.paramsetStartDate).format("YYYY-MM-DD");
            $scope.paramsetEndDateFormat= moment(object.paramsetEndDate).format("YYYY-MM-DD");  
            $scope.intervalFormat = "c "+$scope.paramsetStartDateFormat+" по "+$scope.paramsetEndDateFormat;
        }
    };
    
     //Account objects
    $scope.availableObjects = [];
    $scope.selectedObjects = [];
    
    $scope.getAvailableObjects = function(paramsetId){      
        var table=$scope.crudTableName+"/"+paramsetId+"/contObject/available";        
        crudGridDataFactory(table).query(function(data){           
            $scope.availableObjects = data;         
//for(var k in $scope.availableObjects){
//console.log("$scope.availableObjects["+k+"]="+$scope.availableObjects[k]);    
//}            
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
//for(var k in e){        
//console.log("e["+k+"]= "+e[k]);  
//}
//console.log("e.id = "+e.id);        
//console.log("e.objectId = "+e.objectId);                
        
        var el = {};
        var arr1 = [];
        var arr2 = [];
        var resultArr = [];
        if ($scope.addObject_flag){
//console.log("Flag true");            
            arr1 = $scope.availableObjects;
            arr2 = $scope.selectedObjects; 
            resultArr = arr2;
        }else{
//console.log("Flag false");              
            arr2 = $scope.availableObjects;
            arr1 = $scope.selectedObjects;
            resultArr = arr1;
        };
       
        for (var i=0; i<arr1.length;i++){
            if (arr1[i].id == $scope.currentObjectId) {
//console.log("Find: arr1["+i+"]= "+arr1[i].fullName);                
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
//console.log("tmp="+tmp);       
        return tmp; //Возвращаем массив Id-шников выбранных объектов
        
//for(var k in el){        
//console.log("el["+k+"]= "+el[k]);  
//}       
        
//        if ($scope.addObject_flag){
//            for (var i=0; i<=$scope.availableObjects.length;i++){
//                if ($scope.availableObjects[i].id == e.id) {
//                    el = $scope.availableObjects[i].id;
//                    $scope.availableObjects.splice(i,1);
//                    break;
//                };
//            }
//            $scope.selectedObjects.push(el);
//        }else{
//            for (var i=0; i<=$scope.selectedObjects.length;i++){
//                if ($scope.selectedObjects[i].id == e.id) {
//                    el = $scope.selectedObjects[i].id;
//                    $scope.selectedObjects.splice(i,1);
//                    break;
//                };
//            }
//            $scope.availableObjects.push(el);
//        };
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
        
//        switch ($scope.currentReportType.reportType){
//            case "COMMERCE_REPORT":  table="../api/reportTemplate/commerce"; break;   
//            case "CONS_REPORT":  table="../api/reportTemplate/cons"; break;   
//            case "EVENT_REPORT":  table="../api/reportTemplate/event"; break;       
//        };
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