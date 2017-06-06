/*jslint node: true, nomen: true*/
/*global angular, $, alert*/
'use strict';
var app = angular.module('portalNMC');

app.controller('documentsObjectPassportCtrl', ['mainSvc', '$scope', '$routeParams', function (mainSvc, $scope, $routeParams) {
    
    $scope.isReadOnly = function () {
        return mainSvc.isReadonly() || $routeParams.active !== "true";
    };
    
    $scope.cancelObjectPassportEdit = function () {
        window.close();
    };
    
    function initCtrl() {
        console.log($routeParams);
    }
    
    initCtrl();
}]);