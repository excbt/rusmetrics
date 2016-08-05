/*jslint white: true, node: true */
/*global angular, $ */
'use strict';
angular.module('portalNMC')
.directive("nmcViewMessageForUser", function(){
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
        controller: function($scope){
            $('#messageForUserModal').on('shown.bs.modal', function(){
//                console.log($scope.showOkButton);
//                console.log($scope.messageForUser);
//                console.log($scope.btnClick);
//                console.log($scope.btnOkCaption);
//                console.log($scope.btnCancelCaption);
//                console.log($scope.showCancelButton);
            });
        }
    };
});