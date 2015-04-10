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
    
}]);