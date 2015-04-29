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
            crudGridDataFactory($scope.crudTableName).update({ rsoOrganizationId: $scope.currentObject.rso.id, tariffTypeId: $scope.currentObject.tariffType.id}, $scope.currentObject, successPostCallback, errorCallback);
        }else{
//console.log($scope.currentObject) ;           
            saveTariffOnServer($scope.crudTableName, $scope.currentObject.rso.id, $scope.currentObject.tariffType.id).save({}, $scope.currentObject, successPostCallback, errorCallback);
        };
    };
    
    var activateMainPropertiesTab = function(){
        $('#main_properties_tab').addClass("active");
        $('#set_of_objects_tab').removeClass("active");
        $('#editTariffModal').modal();
    };
    
    $scope.addTariff = function(){
        $scope.availableObjects = [];
        $scope.selectedObjects = [];
        $scope.currentObject = {};
        $scope.startDateFormat = null;
        $scope.endDateFormat = null;
        $scope.getAvailableObjects(0);
        
        $scope.set_of_objects_flag=false;
        
        //settings for activate tab "Main options", when create window opened.        
//        $('#main_properties_tab').addClass("active");
//        $('#set_of_objects_tab').removeClass("active");
//        $('#editTariffModal').modal();
        activateMainPropertiesTab();
    };
    
    $scope.editTariff = function(object){
        $scope.selectedItem(object);
        $scope.getAvailableObjects(object.id);
        $scope.getSelectedObjects();
        
        //settings for activate tab "Main options", when edit window opened. 
        $scope.set_of_objects_flag=false;
        activateMainPropertiesTab();
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
    
    
    //tariff objects
    $scope.availableObjects = [];
    $scope.selectedObjects = [];
    
    $scope.getAvailableObjects = function(tariffId){  
//        $scope.crudTableName1 = "../api/reportParamset";
        var table=$scope.crudTableName+"/"+tariffId+"/contObject/available"; 
console.log(table);        
        crudGridDataFactory(table).query(function(data){           
            $scope.availableObjects = data;    
console.log(data);            
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
    
}]);