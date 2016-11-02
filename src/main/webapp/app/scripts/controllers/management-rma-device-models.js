/*jslint node: true, white: true*/
/*global angular, $*/
'use strict';
angular.module('portalNMC')
.controller('MngmtDeviceModelsCtrl', ['$rootScope', '$scope', '$http','objectSvc', 'notificationFactory', 'crudGridDataFactory', 'mainSvc', function($rootScope, $scope, $http, objectSvc, notificationFactory, crudGridDataFactory, mainSvc){
//console.log('Run model management controller.');
    $rootScope.ctxId = "management_rma_device_models_page";
    $scope.extraProps = {"idColumnName" : "id", "defaultOrderBy" : "modelName", "nameColumnName" : "modelName"}; 
    $scope.orderBy = { field: $scope.extraProps.defaultOrderBy, asc: true};
    
    //controller settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.rmaUrl = "../api/rma";
    $scope.ctrlSettings.modelsUrl = $scope.ctrlSettings.rmaUrl + "/deviceObjects/deviceModels";
    
    //Headers of modal window
    $scope.headers = {};
    
    //model columns
    $scope.ctrlSettings.modelColumns =[
        {
            "name": "modelName",
            "caption": "Название",
            "class": "col-xs-3",
            "type": "name"
        },
        {
            "name": "caption",
            "caption": "Название для пользователя",
            "class": "col-xs-3"
        },        
        {
            "name": "keyname",
            "caption": "Системное имя",
            "class": "col-xs-1"
        },
        {
            "name": "exCode",
            "caption": "Внеш. код",
            "class": "col-xs-1"
        },
        {
            "name": "exLabel",
            "caption": "Внеш. метка",
            "class": "col-xs-1"
        },
        {
            "name": "exSystem",
            "caption": "Внеш. система",
            "class": "col-xs-1"
        }
        

    ];
    //data
    $scope.data= {};
    $scope.data.models = [];
    $scope.data.currentModel = {};
    
    $scope.data.modemTypes = [
        {
            keyname: "GPRS-MODEM",
            caption: "GPRS-модем"
        },
        {
            keyname: "ETHERNET-CONVERTER",
            caption: "Ethernet-конвертер"
        }
    ];
    
//    get models
    var getModels = function(){
        var targetUrl = $scope.ctrlSettings.modelsUrl;
        $http.get(targetUrl)
        .then(function(response){            
            if (!mainSvc.checkUndefinedNull(response.data) && angular.isArray(response.data) && response.data.length > 0){               
                $scope.data.models = response.data;
            }
            
        },
             function(e){
            console.log(e);
        });
    };
    
    $scope.selectModel = function(model){
        $scope.data.currentModel = model;
    };
    $scope.selectedItem = function (item) {
        var curObject = angular.copy(item);
        $scope.data.currentModel = curObject;        
    };
    
    $scope.addModel = function(){
        $scope.data.currentModel = {};
        $('#showModelOptionModal').modal();
    };
    
    $scope.setOrderBy = function(field){      
        var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
        $scope.orderBy = { field: field, asc: asc };
    };
    
    //data processing
     var successCallback = function (e, cb) {                    
        notificationFactory.success();
        $('#deleteModelModal').modal('hide');
        $('#showModelOptionModal').modal('hide');
         getModels();
    };
    
    var successPostCallback = function (e) {
        successCallback(e, null);
        getModels();
//        location.reload();
    };

    var errorCallback = function (e) {
//        notificationFactory.errorInfo(e.statusText,e.data.description); 
        console.log(e);
        var errorCode = "-1";
        if (mainSvc.checkUndefinedNull(e) || mainSvc.checkUndefinedNull(e.data)) {
            errorCode = "ERR_CONNECTION";
        }
        if (!mainSvc.checkUndefinedNull(e) 
            && (!mainSvc.checkUndefinedNull(e.resultCode) || (!mainSvc.checkUndefinedNull(e.data) && !mainSvc.checkUndefinedNull(e.data.resultCode)))
           ) {
            errorCode = e.resultCode || e.data.resultCode;
        }
        var errorObj = mainSvc.getServerErrorByResultCode(errorCode);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    var checkData = function(obj){
        var result = true;
        if (angular.isUndefined(obj) || (obj === null) 
            || angular.isUndefined(obj.modelName) || (obj.modelName === null) || (obj.modelName === "")) {
            notificationFactory.errorInfo("Ошибка", "Не задано название модели!");
            result = false;
        }
        
        return result;
    };
    
    $scope.sendModelToServer = function(obj){        
        //check data before sending
        if (checkData(obj) === false){
            return;
        }        
        
        var url = $scope.ctrlSettings.modelsUrl;                    
        if (angular.isDefined(obj.id) && (obj.id != null)){
            $scope.updateObject(url, obj);
        }else{
            $scope.saveNewModel(url, obj);
        }
    };
    
    $scope.saveNewModel = function (url, obj) {       
        crudGridDataFactory(url).save(obj, successCallback, errorCallback);
    };

    $scope.deleteObject = function (obj) {
        var url = $scope.ctrlSettings.modelsUrl;
        crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
    };

    $scope.updateObject = function (url, object) { 
        var params = { id: object[$scope.extraProps.idColumnName]};
        crudGridDataFactory(url).update(params, object, successCallback, errorCallback);
    };
    
    $scope.deleteObjectInit = function(object){
        $scope.selectedItem(object);
        //generation confirm code
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode();
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;
    };
    
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
    
    // device metadata 
    $scope.data.measures = objectSvc.getDeviceMetadataMeasures();   
    $scope.$on('objectSvc:deviceMetadataMeasuresLoaded', function() {
        $scope.data.measures = objectSvc.getDeviceMetadataMeasures();       
    });
    
    // controller initialization
    $scope.initCtrl = function() {
        getModels();
    };
    
    $scope.initCtrl();
    
}]);