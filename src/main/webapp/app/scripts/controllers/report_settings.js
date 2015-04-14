'use strict';
var app = angular.module('portalNMK');

app.controller('ReportSettingsCtrl',['$scope',function($scope){
    
    $scope.active_tab_active_templates = true;
    
    $scope.columns = [
        {"name":"reportType","header":"Тип отчета", "class":"col-md-11"}
//        ,{"name":"templatesCount", "header":"Кол-во шаблонов", "class":"col-md-1"}
    ];
    
    $scope.objects = [
        {
            "reportType":"Коммерческий"
            ,"templatesCount":" 2"
        }
        ,        {
            "reportType":"Сводный"
            ,"templatesCount":" 0"
        }
        ,        {
            "reportType":"События"
            ,"templatesCount":" 1"
        }
    ];
    
    $scope.oldColumns = [
        {"name":"reportTemplateName", "header":"Название шаблона", "class":"col-md-5"}
        ,{"name":"reportTemplateDate", "header":"Дата создания", "class":"col-md-2"}
    ];
    
    $scope.oldObjects = [
        {"reportTemplateName":"Дефолт"
         ,"reportTemplateDate": "01.04.2015"
        }
        ,        {"reportTemplateName":"Template 1"
         ,"reportTemplateDate": "02.04.2015"
        }
    ];
    
    $scope.toogleShowGroupDetails = function(curObject){//switch option: current goup details
//                   for(var i=0;i<$scope.objects.length;i++){
//                       if ($scope.objects[i]!=curObject && $scope.objects[i].showGroupDetails==true){
//                           $scope.objects[i].showGroupDetails=false;
//                       }
//                   }
                    curObject.showGroupDetails = !curObject.showGroupDetails;
                    
                  //  $scope.selectedItem(curObject);
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
        ,{"name":"Колонка2"
        }
        ,{"name":"Колонка3"
        }
        ,{"name":"Колонка2"
        }
        ,{"name":"Колонка3"
        }
        ,{"name":"Колонка2"
        }
        ,{"name":"Колонка3"
        }
        ,{"name":"Колонка2"
        }
        ,{"name":"Колонка3"
        }
        ,{"name":"Колонка2"
        }
        ,{"name":"Колонка3"
        }
        ,{"name":"Колонка2"
        }
        ,{"name":"Колонка3"
        }
        ,{"name":"Колонка2"
        }
        ,{"name":"Колонка3"
        }
        ,{"name":"Колонка2"
        }
        ,{"name":"Колонка3"
        }
        ,{"name":"Колонка2"
        }
        ,{"name":"Колонка3"
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

    $scope.addColumns= function(defaultColumns){
        var result=[];
        var colSelected = 0;
       // var tmp = $scope.defaultColumns.map(function(el){if (el.selected) {el.selectedforremove=false;return el}});
        for (var i =0; i<defaultColumns.length; i++)
        {

            if (defaultColumns[i].selected){                           
                result[colSelected] = defaultColumns[i];
                colSelected=colSelected+1;                
            };

        }
             
        return result;
    };
    
    $scope.removeColumns= function(defaultColumns, defineColumns){
       //  var tmp = $scope.defineColumns.map(function(el){if (!el.selectedforremove) {return el}});
        var tmp= [];
        var colSelected = 0;

        for (var i =0; i<defineColumns.length; i++)
        {
            if (!defineColumns[i].selectedforremove){
                tmp[colSelected] = defineColumns[i];
                colSelected+=1;
            };

        }
        defineColumns = tmp;
    };

    $scope.moveColumnsUp= function(){
        var tmp= [];
        var colSelected = 0;

        for (var i =0; i<$scope.defineColumns.length; i++)
        {
            if (!$scope.defineColumns[i].selectedforremove){
                tmp[colSelected] = $scope.defineColumns[i];
                colSelected+=1;
            };

        }
        $scope.defineColumns = tmp;
    };

    $scope.moveColumnsDown= function(){
        var tmp= [];
        var colSelected = 0;

        for (var i =0; i<$scope.defineColumns.length; i++)
        {
            if (!$scope.defineColumns[i].selectedforremove){
                tmp[colSelected] = $scope.defineColumns[i];
                colSelected+=1;
            };

        }
        $scope.defineColumns = tmp;
    };
    
    $scope.changeSystemPosition = function(){
       var tmp = $scope.systems[0]; 
        $scope.systems[0] = $scope.systems[1]; 
        $scope.systems[1] = tmp; 
    };            
    
}]);