'use strict';
var app = angular.module('portalNMK');

app.controller('ParamSetsCtrl',['$scope',function($scope){
    
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
        {"name":"reportSetName", "header":"Название варианта", "class":"col-md-5"}
        ,{"name":"reportSetPeriod", "header":"Интервал", "class":"col-md-2"}
        ,{"name":"reportSetFileType", "header":"Тип файла", "class":"col-md-2"}
        ,{"name":"reportSetCreateDate", "header":"Дата создания", "class":"col-md-2"}
        ,{"name":"reportSetArchiveDate", "header":"Дата архивирования", "class":"col-md-2"}
    ];
    
    $scope.oldObjects = [
        {"reportSetName":"Набор параметров 1"
         ,"reportSetPeriod": "Вчера"
         ,"reportSetFileType": "PDF"
         ,"reportSetCreateDate":"11.11.1111"
        }
        , {"reportSetName":"Набор параметров 2"
         ,"reportSetPeriod": "Прошлый месяц"
         ,"reportSetFileType": "XSL"
        ,"reportSetCreateDate":"21.11.1112"
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