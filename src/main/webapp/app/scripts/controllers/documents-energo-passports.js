/*jslint node: true, eqeq: true, white: true, nomen: true*/
/*global angular, $, moment*/
'use strict';
var app = angular.module('portalNMC');
app.controller('documentsEnergoPassportsCtrl', ['$rootScope', '$scope', '$http', 'notificationFactory', 'mainSvc', '$timeout', '$interval', 'energoPassportSvc', '$location', function ($rootScope, $scope, $http, notificationFactory, mainSvc, $timeout, $interval, energoPassportSvc, $location) {
    
    $scope.showContents_flag = true;
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.cssMeasureUnit = "em";
    $scope.ctrlSettings.passportLoading = true;
    $scope.ctrlSettings.emptyString = " ";
    $scope.ctrlSettings.dateFormat = "DD.MM.YYYY";
    
        //model columns
    $scope.ctrlSettings.passportColumns = [        
        {
            "name": "type",
            "caption": "Тип",
            "class": "col-xs-1 nmc-td-for-button nmc-link",
            "type": "doctype"
        },
        {
            "name": "passportName",
            "caption": "Название",
            "class": "col-xs-4 nmc-link",
            "type": "name"
        },
        {
            "name": "description",
            "caption": "Описание",
            "class": "col-xs-5 nmc-link"
        },
        {
            "name": "passportDate2",
            "caption": "Дата документа",
            "class": "col-xs-1 nmc-link",
            "type": "date"
        }
        

    ];
        
    
//    $timeout(function () {
//        $scope.ctrlSettings.loading = false;
//    }, 1500);
    
    $scope.data = {};
    $scope.data.currentDocument = {};
    $scope.data.documentTypes = [
        {
            name: "energydeclair",
            caption: "Энергетическая декларация"
        },
        {
            name: "energypassport",
            caption: "Энергопаспорт"
        },
        {
            name: "project",
            caption: "Проект"
        }
    ];
    
    $scope.emptyString = function (str) {
        return mainSvc.checkUndefinedEmptyNullValue(str);
    };
    
    $scope.createEnergyDocumentInit = function () {
//        $scope.ctrlSettings.passportLoading = true;
        $scope.data.currentDocument = {}; 
        $scope.data.currentDocument.type = $scope.data.documentTypes[0].name;      
        $('#showDocumentPropertiesModal').modal();
        //$('#editEnergoPassportModal').modal();
    };
    
    $scope.editEnergyDocument = function (doc) {       
        $scope.data.currentDocument = angular.copy(doc);
        $scope.data.currentDocument.type = $scope.data.documentTypes[0].name;
        $scope.data.currentDocument.docDateFormatted = moment($scope.data.currentDocument.passportDate2).format($scope.ctrlSettings.dateFormat); 
        $('#showDocumentPropertiesModal').modal();
    };
    
    $scope.openEnergyDocument = function (doc) {
        $location.path("/documents/energo-passport/" + doc.id);
    };
    
    var setConfirmCode = function(useImprovedMethodFlag) {
        $scope.confirmCode = null;
        var tmpCode = mainSvc.getConfirmCode(useImprovedMethodFlag);
        $scope.confirmLabel = tmpCode.label;
        $scope.sumNums = tmpCode.result;                    
    };
    
    $scope.isSystemuser = function(){
        return mainSvc.isSystemuser();
    };
    
    function errorCallback(e) {
        $scope.ctrlSettings.loading = false;
        var errorObj = mainSvc.errorCallbackHandler(e);
        notificationFactory.errorInfo(errorObj.caption, errorObj.description);
    }
    
    function successLoadPassportsCallback(resp) {
//console.log(resp);        
        $scope.data.passports = angular.copy(resp.data);
        $scope.ctrlSettings.loading = false;
    }
    
    function successSavePassportCallback(resp) {
        if (mainSvc.checkUndefinedNull(resp) || mainSvc.checkUndefinedNull(resp.data)) {
            console.error("Incorrect response from server:");
            console.error(resp);
            return false;
        }
        $('#showDocumentPropertiesModal').modal('hide');
    }
    
    function successCreatePassportCallback(resp) {
        successSavePassportCallback(resp);
        $scope.openEnergyDocument(resp.data);
    }
    
    function successDeletePassportCallback(resp) {
        //delete doc from client document array
        $scope.data.passports.some(function (passport, ind) {
            if (passport.id === $scope.data.currentDocument.id) {
                $scope.data.passports.splice(ind, 1);
                return true;
            }
        });
        $scope.data.currentDocument = {};
        //close modal window
        $("#deleteWindowModal").modal('hide');
    }
    
    function loadPassports() {
        energoPassportSvc.loadPassports()
            .then(successLoadPassportsCallback, errorCallback);
    }
    
    $scope.saveEnergyDocument = function (doc) { 
//console.log(doc);
        if (mainSvc.checkUndefinedNull(doc.id)) {
            energoPassportSvc.createPassport(doc.passportName, doc.description)
                .then(successCreatePassportCallback, errorCallback);
    //        $location.path("/documents/energo-passport/new");
        } else {
            energoPassportSvc.updatePassport(doc.passportName, doc.description)
                .then(successSavePassportCallback, errorCallback);
        }
    };
    
    $scope.deleteEnergyDocument = function (doc) {
//console.log(doc);        
        if (mainSvc.checkUndefinedNull(doc)) {
            console.error("Deleting document is undefined or null.");
        }
        energoPassportSvc.deletePassport(doc.id)
            .then(successDeletePassportCallback, errorCallback);
    };    
    
    $scope.deleteEnergyDocumentInit = function (doc) {
        $scope.data.currentDocument = doc;
        $scope.data.currentDeleteMessage = doc.passportName || doc.id;        
        setConfirmCode(true);        
        $("#deleteWindowModal").modal();
    };
        
    $('#showDocumentPropertiesModal').on("shown.bs.modal", function () {
        $("#inputEnergyDocName").focus();
        $('#inputEnergyDocDate').datepicker(mainSvc.getDetepickerSettingsFullView());
    });
    
    function initCtrl() {
        loadPassports();
    }
    
    initCtrl();
}]);