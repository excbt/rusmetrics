'use strict';
var app = angular.module('portalNMK');

app.controller('ReportSettingsCtrl',['$scope', 'crudGridDataFactory', 'notificationFactory', function($scope,crudGridDataFactory, notificationFactory){
    
    $scope.active_tab_active_templates = true;
    
    $scope.currentObject = {};
    $scope.createByTemplate_flag = false;
    $scope.archiveTemplate = {};
    $scope.activeStartDateFormat = null;
    
    $scope.columns = [
        {"name":"reportTypeName","header":"Тип отчета", "class":"col-md-11"}
//        ,{"name":"templatesCount", "header":"Кол-во шаблонов", "class":"col-md-1"}
    ];
    
    $scope.commerce = {};
    $scope.cons = {};
    $scope.event = {};
    
    
    $scope.crudTableName = "../api/reportTemplate"; 
    
//    $scope.reportTypes = [];
//    $scope.getReportTypes = function(){
//        var table = "../api/reportSettings/reportType";
//        crudGridDataFactory(table).query(function(data){
//            $scope.reportTypes = data;
//        });
//    };
//    $scope.getReportTypes();
//    
//    $scope.reportPeriods = [];
//    $scope.getReportPeriods = function(){
//        var table = "../api/reportSettings/reportPeriod";
//        grudGridDataFactory(table).query(function(data){
//            $scope.reportPeriods = data;
//        });
//    };
    
    $scope.objects = [
        {
            "reportTypeName":"Коммерческий"
            ,"templatesCount": 0
            ,"templates": []
        }
        ,        {
            "reportTypeName":"Сводный"
            ,"templatesCount":0
            ,"templates": []
        }
        ,        {
            "reportTypeName":"События"
            ,"templatesCount": 0
            ,"templates": []
        }
    ];
    
    $scope.oldColumns = [
        {"name":"name", "header":"Название шаблона", "class":"col-md-5"}
        ,{"name":"activeStartDate", "header":"Действует с", "class":"col-md-2"}
    ];
    
    
    var successCallback = function (e) {
        notificationFactory.success();
        $('#deleteObjectModal').modal('hide');
        $scope.currentObject={};
    };


    var errorCallback = function (e) {
        notificationFactory.error(e.data.ExceptionMessage);
    };
    
    
      
    $scope.getCommerceTemplates = function (table) {
        crudGridDataFactory(table).query(function (data) {
            $scope.commerce.templates = data;
            $scope.objects[0].templatesCount = $scope.commerce.templates.length;
            $scope.objects[0].templates = $scope.commerce.templates;
        });
    };
    $scope.getConsTemplates = function (table) {
        crudGridDataFactory(table).query(function (data) {
            $scope.cons.templates = data; 
            $scope.objects[1].templatesCount = $scope.cons.templates.length;
            $scope.objects[1].templates = $scope.cons.templates;
        });
    };
    $scope.getEventTemplates = function (table) {
        crudGridDataFactory(table).query(function (data) {
            $scope.event.templates = data;
            $scope.objects[2].templatesCount = $scope.event.templates.length;
            $scope.objects[2].templates = $scope.event.templates;
        });
    };
 //get templates   
    $scope.getActive = function(){
        $scope.getCommerceTemplates($scope.crudTableName+"/commerce");
        $scope.getConsTemplates($scope.crudTableName+"/cons");
        $scope.getEventTemplates($scope.crudTableName+"/event");
    };
    
    $scope.getActive();
    
    $scope.getArchive = function(){
        $scope.getCommerceTemplates($scope.crudTableName+"/archive/commerce");
        $scope.getConsTemplates($scope.crudTableName+"/archive/cons");
        $scope.getEventTemplates($scope.crudTableName+"/archive/event");
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
            object.activeStartDate = activeStartDateFormat==null?null:activeStartDateFormat.getTime();
            table = $scope.crudTableName+"/createByTemplate/"    
            crudGridDataFactory(table).save({srcReportTemplateId: $scope.archiveTemplate.id}, object, successCallback, errorCallback);
            return;
        };
        var reportType = object.reportTypeKey;
        
        switch (reportType){
            case "COMMERCE_REPORT":  table=$scope.crudTableName+"/commerce/"; break;   
            case "CONS_REPORT":  table=$scope.crudTableName+"/cons/"; break;   
            case "EVENT_REPORT":  table=$scope.crudTableName+"/events/"; break;       
        };
        crudGridDataFactory(table).update({reportTemplateId: object.id}, object, successCallback, errorCallback);
    };
    
    $scope.moveToArchive = function(object){
        var table = $scope.crudTableName+"/archive/move"    
        crudGridDataFactory(table).update({reportTemplateId: object.id}, object, successCallback, errorCallback);
    };
    
//    $scope.createTemplate = function(object){
//        var table = $scope.crudTableName+"/archive/move"    
//        crudGridDataFactory(table).save(object, successCallback, errorCallback);
//    };
    
    $scope.createByTemplate =  function(object){
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
    $scope.system1.name="Система 1";
    $scope.system1.defaultColumns = [
        {"name":"Колонка1"
        }
        ,{"name":"Колонка2"
        }
        ,{"name":"Колонка3"
        }
        ,{"name":"Колонка4"
        }
        ,{"name":"Колонка5"
        }
        ,{"name":"Колонка6"
        }
        ,{"name":"Колонка7"
        }
        ,{"name":"Колонка8"
        }
        ,{"name":"Колонка9"
        }
        ,{"name":"Колонка12"
        }
        ,{"name":"Колонка13"
        }
        ,{"name":"Колонка22"
        }
        ,{"name":"Колонка23"
        }
        ,{"name":"Колонка32"
        }
        ,{"name":"Колонка33"
        }
        ,{"name":"Колонка42"
        }
        ,{"name":"Колонка43"
        }
        ,{"name":"Колонка52"
        }
        ,{"name":"Колонка53"
        }
        ,{"name":"Колонка62"
        }
        ,{"name":"Колонка63"
        }
    ];    
    $scope.system1.defineColumns = [
    ];
    
    $scope.system2.name="Система 2";
    $scope.system2.defaultColumns = [
        {"name":"Температура"
        }
        ,{"name":"Давление"
        }
        ,{"name":"Объем"
        }
    ];    
    $scope.system2.defineColumns = [
    ];

    $scope.addColumns= function(defaultColumns, defineColumns){
        var result = defineColumns;
        var colSelected = 0;
      
        for (var i =0; i<defaultColumns.length; i++)
        {
            if (defaultColumns[i].selected){  
                defaultColumns[i].class="";
                defaultColumns[i].selected = false;
                if (typeof defineColumns != 'undefined'){
                    var flagElementAlreadyAdded = false;
                    for (var j=0; j<=defineColumns.length; j++){
                        if (typeof defineColumns[j] != 'undefined' && (defineColumns[j].name == defaultColumns[i].name)){
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
    
    $scope.setToDefault = function(){
        $scope.currentObject = {};
        $scope.createByTemplate_flag = false;
        $scope.archiveTemplate = {};
        $scope.activeStartDateFormat = null;
    };

    
}]);