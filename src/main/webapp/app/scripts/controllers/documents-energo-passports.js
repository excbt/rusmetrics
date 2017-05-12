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
    $scope.ctrlSettings.dateFormat = "YYYY-MM-DD";//"DD.MM.YYYY"; //moment format
    $scope.ctrlSettings.dateFormatAtTable = "yyyy-MM-dd"; //angular format
    $scope.ctrlSettings.dateFormatForDatepicker = "yy-mm-dd"; //jquery datepicker format
    
    $scope.ctrlSettings.orderBy = {field: 'passportName', asc: true};
    
        //model columns
    $scope.ctrlSettings.passportColumns = [        
        {
            "name": "type",
            "caption": "Тип",
            "class": "col-xs-1 nmc-td-for-button-checkbox nmc-link",
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
        
    $scope.setOrderBy = function (field) {
        var asc = $scope.ctrlSettings.orderBy.field === field ? !$scope.ctrlSettings.orderBy.asc : true;
        $scope.ctrlSettings.orderBy = { field: field, asc: asc };        
    };
    
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
    
    $scope.isSystemuser = function () {
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
        if (successSavePassportCallback(resp) === false) {
            return false;
        }
        $scope.openEnergyDocument(resp.data);
    }
    
    function successUpdatePassportCallback(resp) {
        if (successSavePassportCallback(resp) === false) {
            return false;
        }
        //find and update doc in doc array
        var updatingItem = mainSvc.findItemBy($scope.data.passports, "id", resp.data.id);
        var docIndexAtArr = $scope.data.passports.indexOf(updatingItem);        
        if (docIndexAtArr > -1) {
            $scope.data.passports[docIndexAtArr] = angular.copy(resp.data);
        }
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
    
    function checkDoc(doc) {
        var checkFlag = true;
        if (mainSvc.checkUndefinedNull(doc.passportName) || $scope.emptyString(doc.passportName)) {
            notificationFactory.errorInfo("Ошибка", "Не задано название для документа");
            checkFlag = false;
        }
        if (mainSvc.checkUndefinedNull(doc.type)) {
            notificationFactory.errorInfo("Ошибка", "Не задан тип для документа");
            checkFlag = false;
        }
        if (mainSvc.checkUndefinedNull(doc.docDateFormatted) || $scope.emptyString(doc.docDateFormatted)) {
            notificationFactory.errorInfo("Ошибка", "Не задана дата документа");
            checkFlag = false;
        }
        return checkFlag;
    }
    
    $scope.saveEnergyDocument = function (doc) { 
//console.log(doc);
        if (checkDoc(doc) === false) {
            return false;
        }
        //prepare doc date 
        var tmpDate = moment(doc.docDateFormatted, $scope.ctrlSettings.dateFormat);
//console.log(tmpDate);        
//console.log([tmpDate.year(), tmpDate.month() + 1, tmpDate.date()]);        
//console.log(doc.passportDate);
        doc.passportDate = [tmpDate.year(), tmpDate.month() + 1, tmpDate.date()];
        
        if (mainSvc.checkUndefinedNull(doc.id)) {
            energoPassportSvc.createPassport(doc)
                .then(successCreatePassportCallback, errorCallback);
    //        $location.path("/documents/energo-passport/new");
        } else {
            energoPassportSvc.updatePassport(doc)
                .then(successUpdatePassportCallback, errorCallback);
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
        var viewDateformat = mainSvc.getDetepickerSettingsFullView();
        viewDateformat.dateFormat = $scope.ctrlSettings.dateFormatForDatepicker;
        $('#inputEnergyDocDate').datepicker(viewDateformat);
    });
    
    function initCtrl() {
        loadPassports();
    }
    
    initCtrl();
}]);