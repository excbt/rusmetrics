/*jslint node: true, nomen: true*/
/*global angular, $, alert*/
'use strict';
var app = angular.module('portalNMC');

app.controller('documentsEnergoPassportCtrl', ['$location', 'mainSvc', 'energoPassportSvc', 'notificationFactory', '$scope', '$routeParams', '$timeout', function ($location, mainSvc, energoPassportSvc, notificationFactory, $scope, $routeParams, $timeout) {
    
    $scope.isReadOnly = function () {
        return mainSvc.isReadonly();
    };
    
    $scope.cancelEnergoPassportEdit = function () {
        $location.path("/documents/energo-passports");
    };
}]);