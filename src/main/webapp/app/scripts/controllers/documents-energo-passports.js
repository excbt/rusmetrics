/*jslint node: true, eqeq: true, white: true, nomen: true*/
/*global angular, $*/
'use strict';
var app = angular.module('portalNMC');
app.controller('documentsEnergoPassportsCtrl', ['$rootScope', '$scope', '$http', 'notificationFactory', 'mainSvc', '$timeout', '$interval', 'energoPassportSvc', '$location', function ($rootScope, $scope, $http, notificationFactory, mainSvc, $timeout, $interval, energoPassportSvc, $location) {
    
    $scope.showContents_flag = true;
    
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.loading = true;
    $scope.ctrlSettings.cssMeasureUnit = "em";
    $scope.ctrlSettings.passportLoading = true;
    $scope.ctrlSettings.emptyString = " ";
    
        //model columns
    $scope.ctrlSettings.passportColumns = [
        {
            "name": "id",
            "caption": "id",
            "class": "col-xs-1",
            "type": "id"
        },
        {
            "name": "passportName",
            "caption": "Название",
            "class": "col-xs-3",
            "type": "name"
        },
        {
            "name": "description",
            "caption": "Описание",
            "class": "col-xs-3"
        },
        {
            "name": "passportDate2",
            "caption": "Дата создания",
            "class": "col-xs-1",
            "type": "date"
        }
        

    ];
        
    
//    $timeout(function () {
//        $scope.ctrlSettings.loading = false;
//    }, 1500);
    
    $scope.data = {};
    $scope.data.currentDocument = {};
    
    $scope.createEnergyDocumentInit = function () {
//        $scope.ctrlSettings.passportLoading = true;
        $scope.data.currentDocument = {};        
        //$('#editEnergoPassportModal').modal();
    };
    
    $scope.editPassport = function (passport) {
        $location.path("/documents/energo-passport/" + passport.id);
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
    
    function loadPassports() {
        energoPassportSvc.loadPassports()
            .then(successLoadPassportsCallback, errorCallback);
    }
    
    $scope.createEnergyDocument = function (doc) {
        $location.path("/documents/energo-passport/new");
    }
    
    function initCtrl() {
        loadPassports();
    }
    
    initCtrl();
}]);