'use strict';
var app = angular.module('portalNMK');
app.controller('TariffsCtrl', ['$scope', '$rootScope', '$resource', 'crudGridDataFactory', 'notificationFactory', function($scope, $rootScope, $resource, crudGridDataFactory, notificationFactory){
    //set default values
    $scope.crudTableName = "../api/subscr/tariff";    
    $scope.columns = [
        {"name":"tariffTypeName", "header" : "Вид услуги", "class":"col-md-1"}
        ,{"name":"tariffPlanName", "header" : "Наименование", "class":"col-md-2"}
        ,{"name":"rso_organization", "header" : "РСО", "class":"col-md-2"}
        ,{"name":"tariff_option", "header" : "Опция", "class":"col-md-3"}
        ,{"name":"tariffPlanValue", "header" : "Значение", "class":"col-md-1"}
        ,{"name":"tariffPlanDescription", "header" : "Описание", "class":"col-md-3"}
    ];
    $scope.extraProps={"idColumnName":"id", "defaultOrderBy" : "tariffPlanName", "deleteConfirmationProp":"tariffPlanName"};
    $scope.objects = [];
    $scope.rsos = [];
    $scope.tariffTypes = [];
    $scope.tariffOptions = [];
    $scope.startDateFormat = null;
    $scope.endDateFormat = null;
    $scope.object = {};
    $scope.currentObject = {};
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
        $scope.startDateFormat = ($scope.currentObject.startDate == null) ? null : new Date($scope.currentObject.startDate);
        $scope.endDateFormat = ($scope.currentObject.endDate == null) ? null : new Date($scope.currentObject.endDate);             
     };
    
    $scope.setOrderBy = function (field) {
        var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
        $scope.orderBy = { field: field, asc: asc };
    };
    
    var successCallback = function (e, cb) {
        notificationFactory.success();
        $('#deleteObjectModal').modal('hide');
        $scope.currentObject={};
        $scope.getTariffs(cb);

    };

    var successPostCallback = function (e) {
        successCallback(e, function () {
           $('#editTariffModal').modal('hide');
            
        });
    };

    var errorCallback = function (e) {
        notificationFactory.error(e.data.ExceptionMessage);
    };

    $scope.addObject = function () {
        crudGridDataFactory($scope.crudTableName).save($scope.object, successPostCallback, errorCallback);
    };

    $scope.deleteObject = function (object) {
        crudGridDataFactory($scope.crudTableName).delete({ id: object[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
    };

//    $scope.updateObject = function (object) {
//        crudGridDataFactory($scope.crudTableName).update({ id: object[$scope.extraProps.idColumnName] }, object, successCallback, errorCallback);
//    };

    $scope.getTariffs = function (cb) {
        var table = $scope.crudTableName+"/default";
        crudGridDataFactory(table).query(function (data) {
            $scope.objects = data;
            if (cb) cb();
        });
    };
    
    $scope.getRSOs = function () {
        var table = $scope.crudTableName+"/rso";
        crudGridDataFactory(table).query(function (data) {
            $scope.rsos = data;         
        });
    };
    
    $scope.getTariffTypes = function () {
        var table = $scope.crudTableName+"/type";
        crudGridDataFactory(table).query(function (data) {
            $scope.tariffTypes = data;     
        });
    };
    
    $scope.getTariffOptions = function () {
        var table = $scope.crudTableName+"/option";
        crudGridDataFactory(table).query(function (data) {
            $scope.tariffOptions = data;     
        });
    };
    
    $scope.getTariffs();
    $scope.getRSOs();
    $scope.getTariffTypes();
    $scope.getTariffOptions();
    
    $scope.saveObject = function(){
//console.log("Before.$scope.currentObject.endDate = "+$scope.currentObject.endDate);    
        $scope.currentObject.tariffOptionKey = $scope.currentObject.tariffOption.keyname;
        //$scope.currentObject.tariffOption = $scope.currentObject.tarriffOptionKey;
        $scope.currentObject.startDate = $scope.startDateFormat==null ? null:(new Date($scope.startDateFormat));// || $scope.currentObject.startDate;
        $scope.currentObject.endDate = $scope.endDateFormat==null ? null: (new Date($scope.endDateFormat));// || $scope.currentObject.endDate;
//console.log("$scope.currentObject.endDate = "+$scope.currentObject.endDate);  
//console.log("$scope.endDateFormat = "+$scope.endDateFormat);  
//console.log("$scope.currentObject.startDate = "+$scope.currentObject.startDate);  
//console.log("$scope.startDateFormat = "+$scope.startDateFormat);   
//console.log("$scope.endDateFormat.getUTCMilliseconds() = "+$scope.currentObject.endDate);           
//console.log("In saving...");
//        
//console.log("$scope.currentObject.tariffOptionKey = "+$scope.currentObject.tariffOptionKey);                   
        if (($scope.currentObject.id != null) && (typeof $scope.currentObject.id != 'undefined')){
//            crudGridDataFactory($scope.crudTableName).update({ rsoOrganizationId: $scope.currentObject.rso.id, tariffTypeId: $scope.currentObject.tariffType.id}, $scope.currentObject, successPostCallback, errorCallback);
        }else{
//console.log($scope.currentObject) ;           
            saveTariffOnServer($scope.crudTableName, $scope.currentObject.rso.id, $scope.currentObject.tariffType.id).save({}, $scope.currentObject, successPostCallback, errorCallback);
        };
    };
    
    $scope.addTariff = function(){
        $scope.currentObject = {};
        $scope.startDateFormat = null;
        $scope.endDateFormat = null;
    };
    
    var saveTariffOnServer = function(url, rsoOrganizationId, tariffTypeId){
//        console.log("New save="+url);
//        console.log((new Date(null)));
        return $resource(url, {},{
            save: {method: 'POST', params:{rsoOrganizationId: rsoOrganizationId, tariffTypeId: tariffTypeId}}
        })
        
    };
    
    $scope.checkRequiredFields = function(){      
        if ((typeof $scope.currentObject.tariffOption=='undefined')||($scope.currentObject.tariffOption==null)){
            return false;
        };       
        return !(($scope.startDateFormat==null) ||
        ($scope.currentObject.tariffOption.keyname==null) ||
        ($scope.currentObject.rso.id==null) ||
        ($scope.currentObject.tariffType.id==null));
    };
    
}]);