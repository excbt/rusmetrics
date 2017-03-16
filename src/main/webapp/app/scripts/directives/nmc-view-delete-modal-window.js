/*jslint node: true*/
/*global angular, $*/
'use strict';
var app = angular.module('portalNMC');
app.directive('nmcViewDeleteModalWindow', function () {
    return {
        restrict: "AE",
        replace: false,
        templateUrl: "scripts/directives/templates/nmc-view-delete-modal-window.html",
        scope: {
            message: "@",
            confirmLabel: "@",
            controlCode: "@",
            deleteItemClick: "&",
            isSystemuser: "&"
        },
        controller: ['$scope', function ($scope) {
            $scope.confirmCode = null;
            $('#deleteWindowModal').on('shown.bs.modal', function () {
//console.log(deleteItemClick);                
            });
            $('#deleteWindowModal').on('hidden.bs.modal', function () {
                $scope.confirmCode = null;
            });
        }]
    };
});