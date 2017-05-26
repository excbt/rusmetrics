/*jslint node: true, eqeq: true, es5: true*/
/*global angular, $*/
'use strict';
var app = angular.module('portalNMC');
app.controller('MngmtOrganizationsCtrl', ['$rootScope', '$scope', '$http', 'objectSvc', 'notificationFactory', 'crudGridDataFactory', 'mainSvc', function ($rootScope, $scope, $http, objectSvc, notificationFactory, crudGridDataFactory, mainSvc) {
//console.log('Run organization management controller.');
    $rootScope.ctxId = "management_rma_organizations_page";
    $scope.extraProps = {"idColumnName" : "id", "defaultOrderBy" : "organizationName", "nameColumnName" : "organizationName"};
    $scope.orderBy = { field: $scope.extraProps["defaultOrderBy"], asc: true};
    
    //controller settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.rmaUrl = "../api/rma";
    $scope.ctrlSettings.organizationsUrl = "../api/rma/organizations";
    
    //Headers of modal window
    $scope.headers = {};
    
    $scope.ctrlSettings.organizationTypes = {
        rso: {
            caption: "РСО",
            fullCaption: "Ресурсоснабжающая организация"
        },
        cm: {
            caption: "УК",
            fullCaption: "Управляющая компания"
        },
        serv: {
            caption: "СК",
            fullCaption: "Сервисная компания"
        }
        
    };
    
    $scope.isSystemuser = function () {
        return mainSvc.isSystemuser();
    };
    
    if ($scope.isSystemuser() === true) {
        $scope.ctrlSettings.organizationTypes.rma = {
            caption: "РМА",
            fullCaption: "Региональное метрологическое агенство"
        };
    }
    
    //organization columns
    $scope.ctrlSettings.organizationColumns = [
        {
            "name": "organizationName",
            "caption": "Наименование",
            "class": "col-xs-3 col-md-3",
            "type": "name"
        },
//        {
//            "name": "organizationTypeName",
//            "caption": "Тип организации",
//            "class": "col-xs-2 col-md-2"
//        },
        {
            "name": "flagRso",
            "caption": "РСО",
            "class": "col-xs-1 col-md-1",
            "type": "checkbox"
        },
        {
            "name": "flagCm",
            "caption": "УК",
            "class": "col-xs-1 col-md-1",
            "type": "checkbox"
        },
        {
            "name": "organizationDescription",
            "caption": "Описание",
            "class": "col-xs-3 col-md-3"
        },
        {
            "name": "organizationFullAddress",
            "caption": "Полный адрес",
            "class": "col-xs-2 col-md-2"
        }

    ];
    //data
    $scope.data = {};
    $scope.data.organizations = [];
    $scope.data.currentOrganization = {};
    
//    get organizations
    var getOrganizations = function () {
        var targetUrl = $scope.ctrlSettings.organizationsUrl;
        $http.get(targetUrl)
            .then(function (response) {
    //console.log(response.data);
                var respData = angular.copy(response.data);
/*                respData.forEach(function (org) {
                    if (org.flagCm === true) {
                        org.organizationType = "cm";//$scope.ctrlSettings.organizationTypes.cm;
                        org.organizationTypeName = $scope.ctrlSettings.organizationTypes.cm.fullCaption;
                        return true;
                    }
                    if (org.flagRso === true) {
                        org.organizationType = "rso";//$scope.ctrlSettings.organizationTypes.rso;
                        org.organizationTypeName = $scope.ctrlSettings.organizationTypes.rso.fullCaption;
                        return true;
                    }
                    if (org.flagServ === true) {
                        org.organizationType = "serv";//$scope.ctrlSettings.organizationTypes.serv;
                        org.organizationTypeName = $scope.ctrlSettings.organizationTypes.serv.fullCaption;
                        return true;
                    }
                    if (org.flagRma === true) {
                        org.organizationType = "rma";//$scope.ctrlSettings.organizationTypes.rma;
                        org.organizationTypeName = $scope.ctrlSettings.organizationTypes.rma.fullCaption;
                        return true;
                    }
                });
*/
                $scope.data.organizations = respData;
            },
                function (e) {
                    console.log(e);
                });
    };
    
    $scope.selectOrganization = function (organization) {
        $scope.data.currentOrganization = organization;
    };
    $scope.selectedItem = function (item) {
        var curObject = angular.copy(item);
        $scope.data.currentOrganization = curObject;
//console.log($scope.data.currentOrganization);        
    };
    
    $scope.editObjectList =  function (organization) {
        $scope.selectedItem(organization);
        $scope.getAvailableObjects($scope.data.currentOrganization.id);
        $scope.getSelectedObjects($scope.data.currentOrganization.id);
        $scope.showAvailableObjects_flag = false;
    };
    
    $scope.addOrganization = function () {
        $scope.data.currentOrganization = {};
        $('#showOrganizationOptionModal').modal();
    };
    
    $scope.setOrderBy = function (field) {
        var asc = $scope.orderBy.field === field ? !$scope.orderBy.asc : true;
        $scope.orderBy = { field: field, asc: asc };
    };
    
    //data processing
    var successCallback = function (e, cb) {
        notificationFactory.success();
        $('#deleteOrganizationModal').modal('hide');
        $('#showOrganizationOptionModal').modal('hide');
        getOrganizations();
    };
    
    var successPostCallback = function (e) {
        successCallback(e, null);
        getOrganizations();
//        location.reload();
    };

    var errorCallback = function (e) {
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    };
    
    var checkData = function (obj) {
        var result = true;
        if (angular.isUndefined(obj) || (obj == null) || angular.isUndefined(obj.organizationName) || (obj.organizationName == null) || (obj.organizationName == "")) {
            notificationFactory.errorInfo("Ошибка", "Не задано наименование организации!");
            result = false;
        }
        return result;
    };
    
    $scope.sendOrganizationToServer = function (obj) {
        //check data before sending
        if (checkData(obj) == false) {
            return;
        }
/*        
        //prepare flags
        obj.flagCm = false;
        obj.flagRso = false;
        obj.flagServ = false;
        obj.flagRma = false;
        switch (obj.organizationType) {
        case "cm"://$scope.ctrlSettings.organizationTypes.cm
            obj.flagCm = true;
            break;
        case "rso": //$scope.ctrlSettings.organizationTypes.rso
            obj.flagRso = true;
            break;
        case "serv"://$scope.ctrlSettings.organizationTypes.serv
            obj.flagServ = true;
            break;
        case "rma"://$scope.ctrlSettings.organizationTypes.rma
            obj.flagRma = true;
            break;
        }
*/
        var url = $scope.ctrlSettings.organizationsUrl;
        if (angular.isDefined(obj.id) && (obj.id != null)) {
            $scope.updateObject(url, obj);
        } else {
            $scope.saveNewOrganization(url, obj);
        }
    };
    
    $scope.saveNewOrganization = function (url, obj) {
        crudGridDataFactory(url).save(obj, successCallback, errorCallback);
    };

    $scope.deleteObject = function (obj) {
        var url = $scope.ctrlSettings.organizationsUrl;
        crudGridDataFactory(url).delete({ id: obj[$scope.extraProps.idColumnName] }, successCallback, errorCallback);
    };

    $scope.updateObject = function (url, object) {
//        object.organization = null;
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
    
    $scope.isTestMode = function () {
        return mainSvc.isTestMode();
    };
    
    // controller initialization
    $scope.initCtrl = function () {
        getOrganizations();
    };
    
    $scope.initCtrl();
    
}]);