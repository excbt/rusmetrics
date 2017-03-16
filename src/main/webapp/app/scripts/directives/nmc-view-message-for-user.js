/*jslint eqeq: true, node: true */
/*global angular, $ */
'use strict';
var app = angular.module('portalNMC');
app.directive("nmcViewMessageForUser", function () {
    return {
        restrict: "AE",
        replace: true,
        templateUrl: "scripts/directives/templates/nmc-view-message-for-user.html",
        scope: {
            messageForUser: "@",
            btnClick: "&",
            btnOkCaption: "@",
            showOkButton: "@",
            btnCancelCaption: "@",
            showCancelButton: "@"
        },
        controller: ['$scope', function ($scope) {
            $('#messageForUserModal').on('shown.bs.modal', function () {
//                console.log($scope.showOkButton);
//                console.log($scope.messageForUser);
//                console.log($scope.btnClick);
//                console.log($scope.btnOkCaption);
//                console.log($scope.btnCancelCaption);
//                console.log($scope.showCancelButton);
            });
        }]
    };
});