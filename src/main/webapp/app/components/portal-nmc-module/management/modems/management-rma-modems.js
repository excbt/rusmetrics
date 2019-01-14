/*jslint node: true, eqeq: true*/
/*global angular, $*/
'use strict';
var app = angular.module('portalNMC');
app.controller('MngmtModemsCtrl', ['$rootScope', '$scope', '$http', 'objectSvc', 'notificationFactory', 'crudGridDataFactory', 'mainSvc', function ($rootScope, $scope, $http, objectSvc, notificationFactory, crudGridDataFactory, mainSvc) {
//console.log('Run modem management controller.');
    $rootScope.ctxId = "management_rma_modems_page";
    $scope.extraProps = {"idColumnName" : "id", "defaultOrderBy" : "rawModemModelName", "nameColumnName" : "rawModemModelName"};
    $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true};
    
    //controller settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.rmaUrl = "../api/rma";
    $scope.ctrlSettings.modemsUrl = $scope.ctrlSettings.rmaUrl + "/dataSources/rawModemModels";
    $scope.ctrlSettings.modemsIdentityUrl = $scope.ctrlSettings.modemsUrl + "/rawModemModelIdentity";
    
    //Headers of modal window
    $scope.headers = {};
    
    //modem columns
    $scope.ctrlSettings.modemColumns = [
        {
            "name": "rawModemModelName",
            "caption": "Наименование",
            "class": "col-xs-3 col-md-3",
            "type": "name"
        },
        {
            "name": "rawModemTypeCaption",
            "caption": "Тип",
            "class": "col-xs-3 col-md-3"
        },
        {
            "name": "rawModemModelDescription",
            "caption": "Описание",
            "class": "col-xs-2 col-md-2"
        },
        {
            "name": "isDialup",
            "caption": "Поддержка автодозвона",
            "class": "col-xs-1 col-md-1",
            "type": "checkbox"
        }
        

    ];
    //data
    $scope.data = {};
    $scope.data.modems = [];
    $scope.data.currentModem = {};
    
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
    
    $scope.data.modemIdentities = [];
    
    function getModemIdentity() {
        var url = $scope.ctrlSettings.modemsIdentityUrl;
        $http.get(url).then(function (resp) {
            $scope.data.modemIdentities = resp.data;
        }, function (e) {
            console.log(e);
        });
    }
    
//    get modems
    var getModems = function () {
        var targetUrl = $scope.ctrlSettings.modemsUrl;
        $http.get(targetUrl)
            .then(function (response) {
                if (!mainSvc.checkUndefinedNull(response.data) && angular.isArray(response.data) && response.data.length > 0) {
                    //prepare to view modems: set user's caption for modem type
                    response.data.forEach(function (modem) {
                        if (!mainSvc.checkUndefinedNull(modem.rawModemType)) {
                            $scope.data.modemTypes.some(function (mtype) {
                                if (mtype.keyname == modem.rawModemType) {
                                    modem.rawModemTypeCaption = mtype.caption;
                                    return true;
                                }
                            });
                        }
                    });
                    $scope.data.modems = response.data;
                }

            },
                function (e) {
                    console.log(e);
                });
    };
    
    $scope.selectModem = function (modem) {
        $scope.data.currentModem = modem;
    };
    $scope.selectedItem = function (item) {
        var curObject = angular.copy(item);
        $scope.data.currentModem = curObject;
//console.log($scope.data.currentModem);        
    };
    
//    $scope.editObjectList =  function(modem){
//        $scope.selectedItem(modem);
//        $scope.getAvailableObjects($scope.data.currentModem.id);
//        $scope.getSelectedObjects($scope.data.currentModem.id);
//        $scope.showAvailableObjects_flag = false;
//    };
    
    $scope.addModem = function () {
        $scope.data.currentModem = {};
        $scope.data.currentModem.rawModemType = $scope.data.modemTypes[0].keyname;
        $scope.data.currentModem.rawModemModelIdentity = $scope.data.modemIdentities[0].keyname;
        $('#showModemOptionModal').modal();
    };
    
    $scope.setOrderBy = function (field) {
        var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
        $scope.orderBy = { field: field, asc: asc };
    };
    
    //data processing
    var successCallback = function (e, cb) {
        notificationFactory.success();
        $('#deleteModemModal').modal('hide');
        $('#showModemOptionModal').modal('hide');
        getModems();
    };
    
    var successPostCallback = function (e) {
        successCallback(e, null);
        getModems();
//        location.reload();
    };

    var errorCallback = function (e) {
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    var checkData = function (obj) {
        var result = true;
        if (angular.isUndefined(obj) || (obj == null) || angular.isUndefined(obj.rawModemModelName) || (obj.rawModemModelName == null) || (obj.rawModemModelName == "")) {
            notificationFactory.errorInfo("Ошибка", "Не задано наименование модели модема!");
            result = false;
        }
        if (angular.isUndefined(obj) || (obj == null) || angular.isUndefined(obj.rawModemModelIdentity) || (obj.rawModemModelIdentity == null) || (obj.rawModemModelIdentity == "")) {
            notificationFactory.errorInfo("Ошибка", "Не задан вид идентификации!");
            result = false;
        }
        return result;
    };
    
    $scope.sendModemToServer = function (obj) {
        //check data before sending
        if (checkData(obj) == false) {
            return;
        }
        
        var url = $scope.ctrlSettings.modemsUrl;
        if (angular.isDefined(obj.id) && (obj.id != null)) {
            $scope.updateObject(url, obj);
        } else {
            $scope.saveNewModem(url, obj);
        }
    };
    
    $scope.saveNewModem = function (url, obj) {
        crudGridDataFactory(url).save(obj, successCallback, errorCallback);
    };

    $scope.deleteObject = function (obj) {
        var url = $scope.ctrlSettings.modemsUrl;
        crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
    };

    $scope.updateObject = function (url, object) {
        var params = { id: object[$scope.extraProps.idColumnName]};
        crudGridDataFactory(url).update(params, object, successCallback, errorCallback);
    };
    
    $scope.deleteObjectInit = function (object) {
        $scope.selectedItem(object);
        //generation confirm code
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode();
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;
    };
    
    $scope.isSystemuser = function () {
        return mainSvc.isSystemuser();
    };
    
    // controller initialization
    $scope.initCtrl = function () {
        getModemIdentity();
        getModems();
    };
    
    $scope.initCtrl();
    
}]);