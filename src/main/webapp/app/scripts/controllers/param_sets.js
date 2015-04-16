'use strict';
var app = angular.module('portalNMK');

app.controller('ParamSetsCtrl',['$scope','crudGridDataFactory','notificationFactory',function($scope, crudGridDataFactory, notificationFactory){
    
    $scope.active_tab_active_templates = true;
    
    $scope.columns = [
        {"name":"reportType","header":"Тип отчета", "class":"col-md-11"}
//        ,{"name":"templatesCount", "header":"Кол-во шаблонов", "class":"col-md-1"}
    ];
        
    $scope.oldColumns = [
        {"name":"reportSetName", "header":"Название варианта", "class":"col-md-5"}
        ,{"name":"reportSetPeriod", "header":"Интервал", "class":"col-md-2"}
        ,{"name":"reportSetFileType", "header":"Тип файла", "class":"col-md-2"}
        ,{"name":"reportSetCreateDate", "header":"Дата создания", "class":"col-md-2"}
        ,{"name":"reportSetArchiveDate", "header":"Дата архивирования", "class":"col-md-2"}
    ];
    
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
      
    $scope.currentObject = {};
    $scope.createByTemplate_flag = false;
    $scope.archiveParamset = {};
    $scope.activeStartDateFormat = null;
    
    $scope.columns = [
        {"name":"reportTypeName","header":"Тип отчета", "class":"col-md-11"}
//        ,{"name":"templatesCount", "header":"Кол-во шаблонов", "class":"col-md-1"}
    ];
    
    $scope.commerce = {};
    $scope.cons = {};
    $scope.event = {};
    
    
    $scope.crudTableName = "../api/reportParamset"; 
    

    $scope.objects = [
        {
            "reportType":"COMMERCIAL_REPORT"
            ,"reportTypeName":"Коммерческий"
            ,"paramsetsCount": 0
            ,"paramsets": []
        }
        ,        {
            "reportType":"CONS_REPORT"
            ,"reportTypeName":"Сводный"
            ,"paramsetsCount":0
            ,"paramsets": []
        }
        ,        {
            "reportType":"EVENT_REPORT"
            ,"reportTypeName":"События"
            ,"paramsetsCount": 0
            ,"paramsets": []
        }
    ];
    
    var successCallback = function (e) {
        notificationFactory.success();
        $('#editParamsetModal').modal('hide');
        $('#moveToArchiveModal').modal('hide');
        //$scope.currentObject={};
        if (!$scope.createByTemplate_flag){
            $scope.getActive();
        };
        $scope.setDefault();
    };


    var errorCallback = function (e) {
        notificationFactory.error(e.data.ExceptionMessage);
    };
       
    $scope.getCommerceParamsets = function (table) {
        crudGridDataFactory(table).query(function (data) {
            $scope.commerce.paramsets = data;
            if ($scope.commerce.paramsets!=[]){
                $scope.objects[0].reportTypeName = $scope.commerce.paramsets[0].reportTemplate.reportType.caption;
            }
            $scope.objects[0].paramsetsCount = $scope.commerce.paramsets.length;
            $scope.objects[0].paramsets = $scope.commerce.paramsets;
        });
    };
    $scope.getConsParamsets = function (table) {
        crudGridDataFactory(table).query(function (data) {
            $scope.cons.templates = data; 
            if ($scope.cons.paramsets!=[]){
                $scope.objects[1].reportTypeName = $scope.cons.paramsets[0].reportTemplate.reportType.caption;
            }
            $scope.objects[1].paramsetsCount = $scope.cons.paramsets.length;
            $scope.objects[1].paramsets = $scope.cons.paramsets;
        });
    };
    $scope.getEventParamsets = function (table) {
        crudGridDataFactory(table).query(function (data) {
            $scope.event.paramsets = data;
            if ($scope.event.paramsets!=[]){
                $scope.objects[2].reportTypeName = $scope.event.paramsets[0].reportTemplate.reportType.caption;
            }
            $scope.objects[2].paramsetsCount = $scope.event.paramsets.length;
            $scope.objects[2].paramsets = $scope.event.paramsets;
        });
    };
 //get paramsets   
    $scope.getActive = function(){
        $scope.getCommerceParamsets($scope.crudTableName+"/commerce");
        $scope.getConsParamsets($scope.crudTableName+"/cons");
        $scope.getEventParamsets($scope.crudTableName+"/event");
    };
    
    $scope.getActive();
    
    $scope.getArchive = function(){
        $scope.getCommerceParamsets($scope.crudTableName+"/archive/commerce");
        $scope.getConsParamsets($scope.crudTableName+"/archive/cons");
        $scope.getEventParamsets($scope.crudTableName+"/archive/event");
    };
    
    
    $scope.toogleShowGroupDetails = function(curObject){//switch option: current goup details
        curObject.showGroupDetails = !curObject.showGroupDetails;
    };
    
   
    $scope.selectedItem = function(item){
//console.log("In selected item.");        
        var curObject = angular.copy(item);
		$scope.currentObject = curObject;
//console.log("Selected item = ");        
//for(var k in $scope.currentObject){
//console.log("$scope.currentObject["+k+"]="+$scope.currentObject[k]);    
//}        
    };
    
    $scope.updateParamsets = function(object){
        var table = "";
        if ($scope.createByTemplate_flag){
            object.activeStartDate = $scope.activeStartDateFormat==null?null:$scope.activeStartDateFormat.getTime();
            table = $scope.crudTableName+"/createByTemplate/"+$scope.archiveParamset.id;    
            crudGridDataFactory(table).save({}, object, successCallback, errorCallback);
            return;
        };
        var reportType = object.reportTypeKey;
        
        switch (reportType){
            case "COMMERCE_REPORT":  table=$scope.crudTableName+"/commerce/"; break;   
            case "CONS_REPORT":  table=$scope.crudTableName+"/cons/"; break;   
            case "EVENT_REPORT":  table=$scope.crudTableName+"/events/"; break;       
        };
        crudGridDataFactory(table).update({reportParamsetId: object.id}, object, successCallback, errorCallback);
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
        $scope.createByTemplate_flag = true;
        $scope.archiveParamset = {};
        $scope.archiveParamset.id = object.id;
        $scope.archiveParamset.name = object.name;
        $scope.currentObject = {};
        $scope.currentObject.name = angular.copy(object.name);
        $scope.currentObject.description = angular.copy(object.description);
        
        
//        var table = $scope.crudTableName+"/createByTemplate/"    
//        crudGridDataFactory(table).save({srcReportTemplateId: id},object, successCallback, errorCallback);
    };
    
    $scope.setDefault = function(){
        $scope.currentObject = {};
        $scope.createByTemplate_flag = false;
        $scope.archiveParamset = {};
        $scope.activeStartDateFormat = null;
    };
    
     //Account objects
    $scope.availableObjects = [];
    $scope.selectedObjects = [];
    
    $scope.getAvailableObjects = function(){
    };
    $scope.getSelectedObjects = function(){
    };
    
    //templates
    $scope.templatesForCurrentParaset = [];
    $scope.getTemplates = function(){
        
    };
    
}]);