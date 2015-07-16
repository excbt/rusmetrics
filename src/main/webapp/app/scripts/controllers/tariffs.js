'use strict';
var app = angular.module('portalNMC');
app.controller('TariffsCtrl', ['$scope', '$rootScope', '$resource', 'crudGridDataFactory', 'notificationFactory', 'objectSvc', function($scope, $rootScope, $resource, crudGridDataFactory, notificationFactory, objectSvc){
    //set default values
    $scope.crudTableName = "../api/subscr/tariff"; 
    $scope.groupUrl = "../api/contGroup";
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
    
    //Headers of modal window
    $scope.headers = {}
    $scope.headers.addObjects = "Доступные объекты";//header of add objects window

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
        notificationFactory.errorInfo(e.statusText,e.data.description);       
    };

    $scope.addObject = function () {
        crudGridDataFactory($scope.crudTableName).save($scope.object, successPostCallback, errorCallback);
    };

    $scope.deleteObject = function (object) {
        crudGridDataFactory($scope.crudTableName).delete({ id: object[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
    };

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
        $scope.currentObject.tariffOptionKey = $scope.currentObject.tariffOption.keyname;
        $scope.currentObject.startDate = $scope.startDateFormat==null ? null:(new Date($scope.startDateFormat));// || 
        $scope.currentObject.endDate = $scope.endDateFormat==null ? null: (new Date($scope.endDateFormat));// || $scope.currentObject.endDate;    
        var tmp = $scope.selectedObjects.map(function(elem){
            return elem.id;
        });
        if (($scope.currentObject.id != null) && (typeof $scope.currentObject.id != 'undefined')){
            crudGridDataFactory($scope.crudTableName).update({ rsoOrganizationId: $scope.currentObject.rso.id, tariffTypeId: $scope.currentObject.tariffType.id, contObjectIds: tmp}, $scope.currentObject, successPostCallback, errorCallback);
        }else{        
            saveTariffOnServer($scope.crudTableName, $scope.currentObject.rso.id, $scope.currentObject.tariffType.id).save({contObjectIds: tmp}, $scope.currentObject, successPostCallback, errorCallback);
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
        $scope.showAvailableObjects_flag = false; // флаг, устанавливающий видимость окна с доступными объектами
        
        //settings for activate tab "Main options", when create window opened.        
        activateMainPropertiesTab();
    };
    
    $scope.editTariff = function(object){
        $scope.selectedItem(object);
        $scope.getAvailableObjects(object.id);
        $scope.getSelectedObjects();
        
        //settings for activate tab "Main options", when edit window opened. 
        $scope.set_of_objects_flag=false;
        $scope.showAvailableObjects_flag = false; // флаг, устанавливающий видимость окна с доступными объектами
        activateMainPropertiesTab();
    };
    
    var saveTariffOnServer = function(url, rsoOrganizationId, tariffTypeId){
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
        ($scope.currentObject.tariffType.id==null))
        &&$scope.checkPositiveNumberValue($scope.currentObject.tariffPlanValue)
        &&$scope.checkDateIntervalWithRightNull($scope.startDateFormat, $scope.endDateFormat);
    };
    
    
    //tariff objects
    $scope.availableObjects = [];
    $scope.availableObjectGroups = [];
    $scope.selectedObjects = [];
    
    $scope.getAvailableObjects = function(tariffId){  
        var table=$scope.crudTableName+"/"+tariffId+"/contObject/available";        
        crudGridDataFactory(table).query(function(data){           
            $scope.availableObjects = data;
            objectSvc.sortObjectsByFullName($scope.availableObjects);
        });        
    };
    $scope.getSelectedObjects = function(){
        var table=$scope.crudTableName+"/"+$scope.currentObject.id+"/contObject";
        crudGridDataFactory(table).query(function(data){
            $scope.selectedObjects = data;
            objectSvc.sortObjectsByFullName($scope.selectedObjects);
        });
    };
    
    $scope.getAvailableObjectGroups = function(){         
        crudGridDataFactory($scope.groupUrl).query(function(data){           
            $scope.availableObjectGroups = data;
        });        
    };
    $scope.getAvailableObjectGroups();
    
    $scope.viewAvailableObjects = function(objectGroupFlag){
        $scope.showAvailableObjects_flag=!$scope.showAvailableObjects_flag;
        $scope.showAvailableObjectGroups_flag=objectGroupFlag;
        if (objectGroupFlag){
            $scope.headers.addObjects = "Доступные группы объектов";
            //prepare the object goups to view in table
            var tmpArr = $scope.availableObjectGroups.map(function(element){
                var result = {};
                result.fullName = element.contGroupName;//set the field, which view entity name in table
                return result;
            });
            $scope.availableEntities = tmpArr;
        }else{
            $scope.headers.addObjects = "Доступные объекты";
            $scope.availableEntities = $scope.availableObjects;
        };
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
    
    $scope.removeSelectedObject = function(object){
        $scope.availableObjects.push(object);
        $scope.selectedObjects.splice($scope.selectedObjects.indexOf(object), 1);
        objectSvc.sortObjectsByFullName($scope.availableObjects);
    }
    
    $scope.addSelectedEntities = function(){
    //console.log($scope.availableObjects);          
        var tmpArray = angular.copy($scope.availableObjects);
        for(var i =0; i<$scope.availableObjects.length; i++){
            var curObject = $scope.availableObjects[i];

            if (curObject.selected){
    //console.log(curObject);                            
    // console.log("curObject is performanced");               
                var elem = angular.copy(curObject);
                elem.selected = null;
    //console.log(tmpArray.indexOf(curObject));  
                var elementIndex = -1;
                tmpArray.some(function(element,index,array){
                    if (element.fullName === curObject.fullName){
                        elementIndex = index;
                        return true;
                    }else{
                        return false;
                    }
                });
                tmpArray.splice(elementIndex, 1);
                $scope.selectedObjects.push(elem);
                curObject.selected = null;
            };
        }
        $scope.availableObjects = tmpArray;
        objectSvc.sortObjectsByFullName($scope.selectedObjects);
        $scope.showAvailableObjects_flag=false;
    };    
    
    $scope.isSystemuser = function(){
        $scope.userInfo = $rootScope.userInfo;
        return $scope.userInfo._system;
    };
    
    $scope.showAddObjectButton = function(){
//console.log('$scope.showAvailableObjects_flag = '+$scope.showAvailableObjects_flag);
//console.log('$scope.set_of_objects_flag = '+$scope.set_of_objects_flag);        
        return !$scope.showAvailableObjects_flag && $scope.set_of_objects_flag;
    }
    
            //checkers            
    $scope.checkEmptyNullValue = function(numvalue){                    
        var result = false;
        if ((numvalue === "") || (numvalue==null)){
            result = true;
            return result;
        }
        return result;
    };

    function isNumeric(n) {
      return !isNaN(parseFloat(n)) && isFinite(n);
    }

    $scope.checkNumericValue = function(numvalue){ 
        var result = true;
        if ($scope.checkEmptyNullValue(numvalue)){
            return result;
        }
        if (!isNumeric(numvalue)){
            result = false;
            return result;
        };
        return result;
    };

    $scope.checkPositiveNumberValue = function(numvalue){                    
        var result = true;
        result = $scope.checkNumericValue(numvalue)
        if (!result){
            //if numvalue is not number -> return false
            return result;
        }
        result = parseInt(numvalue)>=0?true:false;
        return result;
    };
    
    $scope.checkDateIntervalWithRightNull = function(left, right){     
        if (right == null){return true;};
        return right>=left;
    };
    
}]);