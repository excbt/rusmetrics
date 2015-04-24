'use strict';
var app = angular.module('portalNMK');

app.controller('ReportSettingsCtrl',['$scope', '$resource', 'crudGridDataFactory', 'notificationFactory', function($scope, $resource,crudGridDataFactory, notificationFactory){
    
    $scope.active_tab_active_templates = true;
    
    $scope.currentObject = {};
    $scope.createByTemplate_flag = false;
    $scope.archiveTemplate = {};
    $scope.activeStartDateFormat = new Date();//null;
    $scope.currentReportType = {};
    
    $scope.objects = [
//        {
//            "reportType":"COMMERCE_REPORT"
//            ,"reportTypeName":"Коммерческий"
//            ,"templatesCount": 0
//            ,"templates": []
//        }
//        ,        {
//            "reportType":"CONS_REPORT"
//            ,"reportTypeName":"Сводный"
//            ,"templatesCount":0
//            ,"templates": []
//        }
//        ,        {
//            "reportType":"EVENT_REPORT"
//            ,"reportTypeName":"События"
//            ,"templatesCount": 0
//            ,"templates": []
//        }
    ];
    
    
    $scope.columns = [
        {"name":"reportTypeName","header":"Тип отчета", "class":"col-md-11"}
//        ,{"name":"templatesCount", "header":"Кол-во шаблонов", "class":"col-md-1"}
    ];
    
//    $scope.commerce = {};
//    $scope.cons = {};
//    $scope.event = {};
    
    
    $scope.crudTableName = "../api/reportTemplate"; 
    
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
//console.log(newObjects);            
            $scope.objects = newObjects;
            
            $scope.getActive();
//            $scope.objects[0].reportTypeName = $scope.reportTypes[0].caption;
//            $scope.objects[1].reportTypeName = $scope.reportTypes[1].caption;
//            $scope.objects[2].reportTypeName = $scope.reportTypes[2].caption;
        });
    };
    $scope.getReportTypes();
//    
//    $scope.reportPeriods = [];
//    $scope.getReportPeriods = function(){
//        var table = "../api/reportSettings/reportPeriod";
//        grudGridDataFactory(table).query(function(data){
//            $scope.reportPeriods = data;
//        });
//    };
    

    $scope.oldColumns = [
        {"name":"name", "header":"Название шаблона", "class":"col-md-5"}
        ,{"name":"activeStartDate", "header":"Действует с", "class":"col-md-2"}
    ];
    
    
    var successCallback = function (e) {
        notificationFactory.success();
        $('#editTemplateModal').modal('hide');
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
    
    
    $scope.getTemplates = function(table, type){
        crudGridDataFactory(table).query(function (data) {
            type.templates = data;
            type.templatesCount = data.length;
        });
    };  
    
//    $scope.getCommerceTemplates = function (table) {
//        crudGridDataFactory(table).query(function (data) {
//            $scope.commerce.templates = data;
//
//            $scope.objects[0].templatesCount = $scope.commerce.templates.length;
//            $scope.objects[0].templates = $scope.commerce.templates;
//        });
//    };
//    $scope.getConsTemplates = function (table) {
//        crudGridDataFactory(table).query(function (data) {
//            $scope.cons.templates = data; 
//
//            $scope.objects[1].templatesCount = $scope.cons.templates.length;
//            $scope.objects[1].templates = $scope.cons.templates;
//        });
//    };
//    $scope.getEventTemplates = function (table) {
//        crudGridDataFactory(table).query(function (data) {
//            $scope.event.templates = data;
//
//            $scope.objects[2].templatesCount = $scope.event.templates.length;
//            $scope.objects[2].templates = $scope.event.templates;
//        });
//    };
 //get templates   
    $scope.getActive = function(){
        if (($scope.objects == []) || (typeof $scope.objects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.objects.length; i++){
            $scope.getTemplates($scope.crudTableName+$scope.objects[i].suffix, $scope.objects[i]);
        };
        
//        $scope.getCommerceTemplates($scope.crudTableName+"/commerce");
//        $scope.getConsTemplates($scope.crudTableName+"/cons");
//        $scope.getEventTemplates($scope.crudTableName+"/event");
    };
    
//    $scope.getActive();
    
    $scope.getArchive = function(){
        if (($scope.objects == []) || (typeof $scope.objects[0].suffix == 'undefined')){return;};
        for (var i=0; i<$scope.objects.length; i++){
            $scope.getTemplates($scope.crudTableName+"/archive"+$scope.objects[i].suffix, $scope.objects[i]);
        };
//        $scope.getCommerceTemplates($scope.crudTableName+"/archive/commerce");
//        $scope.getConsTemplates($scope.crudTableName+"/archive/cons");
//        $scope.getEventTemplates($scope.crudTableName+"/archive/event");
    };
    
//    $scope.oldObjects = [
//        {"reportTemplateName":"Дефолт"
//         ,"reportTemplateDate": "01.04.2015"
//        }
//        ,        {"reportTemplateName":"Template 1"
//         ,"reportTemplateDate": "02.04.2015"
//        }
//    ];
    
    $scope.toogleShowGroupDetails = function(curObject){//switch option: current goup details
//                   for(var i=0;i<$scope.objects.length;i++){
//                       if ($scope.objects[i]!=curObject && $scope.objects[i].showGroupDetails==true){
//                           $scope.objects[i].showGroupDetails=false;
//                       }
//                   }
                    curObject.showGroupDetails = !curObject.showGroupDetails;
                    
                  //  $scope.selectedItem(curObject);
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
    
    $scope.updateTemplate = function(object){
        var table = "";
        if ($scope.createByTemplate_flag){
            object.activeStartDate = $scope.activeStartDateFormat==null?null:$scope.activeStartDateFormat.getTime();
            table = $scope.crudTableName+"/createByTemplate/"+$scope.archiveTemplate.id;    
            crudGridDataFactory(table).save({}, object, successCallback, errorCallback);
            return;
        };
        
        table =$scope.crudTableName+$scope.currentReportType.suffix;
//        switch ($scope.currentReportType.reportType){
//            case "COMMERCE_REPORT":  table=$scope.crudTableName+"/commerce/"; break;   
//            case "CONS_REPORT":  table=$scope.crudTableName+"/cons/"; break;   
//            case "EVENT_REPORT":  table=$scope.crudTableName+"/event/"; break;       
//        };
        crudGridDataFactory(table).update({reportTemplateId: object.id}, object, successCallback, errorCallback);
    };
    
    $scope.toArchive = function(url, id) {
        return $resource(url, {
            }, {
                update: {method: 'PUT', params:{reportTemplateId:id}}
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
    
    $scope.createByTemplate =  function(parentObject,object){
        $scope.setCurrentReportType(parentObject);
        $scope.createByTemplate_flag = true;
        $scope.archiveTemplate = {};
        $scope.archiveTemplate.id = object.id;
        $scope.archiveTemplate.name = object.name;
        $scope.currentObject = {};
        $scope.currentObject.name = angular.copy(object.name);
        $scope.currentObject.description = angular.copy(object.description);
        
        
//        var table = $scope.crudTableName+"/createByTemplate/"    
//        crudGridDataFactory(table).save({srcReportTemplateId: id},object, successCallback, errorCallback);
    };
    
    
    //for template designer
    $scope.systems = [];
    $scope.system1 ={} ;
    $scope.system2 ={} ;
    $scope.systems = [$scope.system1, $scope.system2];
    
    $scope.getWizard = function(){
        var table = "../api/reportWizard/columnSettings/commerce";
        crudGridDataFactory(table).get(function(data){
console.log(data);            
            $scope.obtainedSystems = data;
            //$scope.systems = $scope.obtainedSystems.allTsList;
            $scope.systems = data.allTsList;
            //$scope.system2 = data.allTsList[1];
            
            $scope.system1 = data.allTsList[0];
            $scope.system2 = data.allTsList[1];
            $scope.system1.defaultColumns = data.ts1List;
            $scope.system2.defaultColumns = data.ts2List;
            $scope.system1.defineColumns = [];
            $scope.system2.defineColumns = [];

        });
    };
    
    $scope.getWizard();
    

//    $scope.system1.name="Система 1";
//    $scope.system1.defaultColumns = [
//        {"name":"Колонка1"
//        }
//        ,{"name":"Колонка2"
//        }
//        ,{"name":"Колонка3"
//        }
//        ,{"name":"Колонка4"
//        }
//        ,{"name":"Колонка5"
//        }
//        ,{"name":"Колонка6"
//        }
//        ,{"name":"Колонка7"
//        }
//        ,{"name":"Колонка8"
//        }
//        ,{"name":"Колонка9"
//        }
//        ,{"name":"Колонка12"
//        }
//        ,{"name":"Колонка13"
//        }
//        ,{"name":"Колонка22"
//        }
//        ,{"name":"Колонка23"
//        }
//        ,{"name":"Колонка32"
//        }
//        ,{"name":"Колонка33"
//        }
//        ,{"name":"Колонка42"
//        }
//        ,{"name":"Колонка43"
//        }
//        ,{"name":"Колонка52"
//        }
//        ,{"name":"Колонка53"
//        }
//        ,{"name":"Колонка62"
//        }
//        ,{"name":"Колонка63"
//        }
//    ];    
//    
//    $scope.system2.name="Система 2";
//    $scope.system2.defaultColumns = [
//        {"name":"Температура"
//        }
//        ,{"name":"Давление"
//        }
//        ,{"name":"Объем"
//        }
//    ]; 
    
//    $scope.system1.defineColumns = [
//    ];
//    
//   
//    $scope.system2.defineColumns = [
//    ];

    $scope.addColumns= function(defaultColumns, defineColumns){
        var result = defineColumns;
        var colSelected = 0;
console.log(defaultColumns);        
console.log(defineColumns);      
        for (var i =0; i<defaultColumns.length; i++)
        {
            if (defaultColumns[i].selected){  
                defaultColumns[i].class="";
                defaultColumns[i].selected = false;
                if (typeof defineColumns != 'undefined'){
                    var flagElementAlreadyAdded = false;
                    for (var j=0; j<=defineColumns.length; j++){
                        if (typeof defineColumns[j] != 'undefined' && (defineColumns[j].columnHeader == defaultColumns[i].columnHeader)){
                            flagElementAlreadyAdded = true;
                        };
                    }
                
                    if (flagElementAlreadyAdded){continue;};
                }
                     
                var elem = angular.copy(defaultColumns[i]);
                elem.selected = false;
                elem.class = "";
                result.push(elem);
                colSelected=colSelected+1;                
            };
        }
             
        return result;
    };
    
    $scope.removeColumns= function(defineColumns){
        var result= [];
//        var colSelected = 0;

        for (var i =0; i<defineColumns.length; i++)
        {
            if (!defineColumns[i].selected){
                defineColumns[i].selected = false;
                result.push(defineColumns[i]);
//                result[colSelected].selected = false;
//                colSelected+=1;
            };

        }
        return result;
    };

    $scope.moveColumnsUp= function(defineColumns){
        var tmp={};
  //      var colSelected = 0;

        for (var i =1; i<defineColumns.length; i++)
        {
            if (defineColumns[i].selected){
                tmp = defineColumns[i-1];
                defineColumns[i-1] = defineColumns[i];
                defineColumns[i] = tmp;
//                tmp[colSelected] = defineColumns[i];
//                colSelected+=1;
            };

        }
        return defineColumns;
    };

    $scope.moveColumnsDown= function(defineColumns){
                var tmp={};
  //      var colSelected = 0;

        for (var i =defineColumns.length-2; i>=0; i--)
        {
            if (defineColumns[i].selected){
                tmp = defineColumns[i+1];
                defineColumns[i+1] = defineColumns[i];
                defineColumns[i] = tmp;
//                tmp[colSelected] = defineColumns[i];
//                colSelected+=1;
            };

        }
        return defineColumns;
    };
    
    $scope.changeSystemPosition = function(){
       var tmp = $scope.systems[0]; 
        $scope.systems[0] = $scope.systems[1]; 
        $scope.systems[1] = tmp; 
    }; 
    
    $scope.selectColumn = function(item){
        item.selected = !item.selected;
        item.class= item.selected?"active":"";
    };
    
    $scope.setDefault = function(){
        $scope.currentObject = {};
        $scope.createByTemplate_flag = false;
        $scope.archiveTemplate = {};
        $scope.activeStartDateFormat = new Date();
    };
    
    $scope.setCurrentReportType = function(object){
        $scope.currentReportType.reportType = object.reportType;
        $scope.currentReportType.reportTypeName=object.reportTypeName;
        $scope.currentReportType.suffix=object.suffix;
    };
    $scope.editTemplate = function(object, oldObject){
        $scope.selectedItem(oldObject);
        $scope.setCurrentReportType(object);
    };
    
    $scope.addTemplate = function(object){
        $scope.setCurrentReportType(object);
        $scope.currentObject = {};
    };

    
}]);