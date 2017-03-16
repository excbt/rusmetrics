/*jslint node: true*/
/*global angular, $*/
'use strict';
var app = angular.module('portalNMC');
app.controller('IdleCtrl', ['$scope', function ($scope) {
    $scope.$on('IdleEnd', function () {
        $('#warnDialogModal').modal('hide');
    });
    $scope.$on('IdleStart', function () {
        $('#warnDialogModal').modal();
    });

    $scope.$on('IdleTimeout', function () {
        window.location.assign('../logout');
    });

    $scope.progressbar = function (progress) {
        var result = Math.round(progress);
        return result;
    };
}]);