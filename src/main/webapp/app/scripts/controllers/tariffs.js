'use strict';
var app = angular.module('portalNMK');
app.controller('TariffsCtrl', function($scope){
    $scope.welcome = "Tariffs controller";
    
    $scope.columns = [
        {"name":"tariff_plan_name", "header" : "Наименование", "class":"col-md-3"},
        {"name":"tariff_plan_description", "header" : "Описание", "class":"col-md-5"}
        ,{"name":"tariff_plan_comment", "header" : "Комментарий", "class":"col-md-4"}
    ];
    
    $scope.tariffColumns = [
        {"name":"rso_organization_id", "header" : "РСО", "class":"col-md-1"},
        {"name":"tariff_type_id", "header" : "Тип тарифа", "class":"col-md-1"}
        ,{"name":"start_date", "header" : "Действует с", "class":"col-md-1"}
        ,{"name":"end_date", "header" : "по", "class":"col-md-1"}
        ,{"name":"tariff_option", "header" : "Опция", "class":"col-md-1"}
        ,{"name":"tariff_plan_value", "header" : "Значение", "class":"col-md-1"}
    ];
    
    $scope.extraProps={"idColumnName":"id", "defaultOrderBy" : "tariff_plan_name", "deleteConfirmationProp":"tariff_plan_name"};
    
    $scope.objects = [
        {"id":1,
         "subscriber_id":1,
         "rso_organization_id":1,
         "tariff_type_id":1,
         "cont_object_id":1,
         "tariff_plan_name":"default",
         "tariff_plan_description":"Тарифный план по умолчанию",
         "tariff_plan_comment":"no comment",
         "tariff_option":"",
         "tariff_plan_value":0,
         "start_date": 123,
         "end_date": 321
        }
        ,{
          "id":28060274,
         "subscriber_id":728,
         "rso_organization_id":25201856,
            "tariff_type_id":28058413,
         "cont_object_id":null,
         "tariff_plan_name":"Лето",
         "tariff_plan_description":"Тарифный план на лето",
         "tariff_plan_comment":"",
         "tariff_option":"DEFAULT",
         "tariff_plan_value":0,
         "start_date": "2015-04-01 00:00:00",
         "end_date": null  
        }
        ,{
          "id":28060274,
         "subscriber_id":728,
         "rso_organization_id":25201856,
            "tariff_type_id":28058413,
         "cont_object_id":null,
         "tariff_plan_name":"ГВ",
         "tariff_plan_description":"",
         "tariff_plan_comment":"AUTO GENERATED for hw",
         "tariff_option":"DEFAULT",
         "tariff_plan_value":0,
         "start_date": "2015-04-01 00:00:00",
         "end_date": null  
        }
    ];
    
     $scope.tariffObjects = [
        {"id":1,
         "subscriber_id":1,
         "rso_organization_id":"РСО #1",
         "tariff_type_id":"ГВС",
         "cont_object_id":1,
         "tariff_plan_name":"default",
         "tariff_plan_description":"Тарифный план по умолчанию",
         "tariff_plan_comment":"no comment",
         "tariff_option":"Льготный",
         "tariff_plan_value":0,
         "start_date": "2015-04-01 00:00:00",
         "end_date": "2015-04-30 00:00:00"
        }
     ];
    
 //   $scope.lookups = [];
    $scope.object = {};

    $scope.addMode = false;
    $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true };
    $scope.filter = '';

    $scope.toggleAddMode = function () {
        $scope.addMode = !$scope.addMode;
        $scope.object = {};
        if ($scope.addMode) {
            if ($scope.newIdProperty && $scope.newIdValue)
                $scope.object[$scope.newIdProperty] =  $scope.newIdValue;
        }
    };

    $scope.toggleEditMode = function (object) {
        object.editMode = !object.editMode;
    };
    
    $scope.selectedItem = function (item) {
			        var curObject = angular.copy(item);
			        $scope.currentObject = curObject;
     };

    
});