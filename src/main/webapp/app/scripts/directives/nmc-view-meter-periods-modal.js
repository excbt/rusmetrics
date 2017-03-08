/*jslint white: true, node: true */
/*global angular, $ */
'use strict';
angular.module('portalNMC')
.directive("nmcViewMeterPeriodsModal", function () {
    return {
        restrict: "AE",
        replace: true, 
        templateUrl: "scripts/directives/templates/nmc-view-meter-periods-modal.html",
        scope: {
            contObjectName: "@",
            meterPeriodList: "=",
            resourceSystemList: "=",
            btnClick: "&",
            btnOkCaption: "@",
            showOkButton: "@",
            btnCancelCaption: "@",
            showCancelButton: "@"
        },
        controller: ['$scope', function ($scope) {
            $('#nmcMeterPeriodsModal').on('shown.bs.modal', function () {
//                console.log($scope.contObjectName);
//                console.log($scope.showOkButton);
//                console.log($scope.resourceSystemList);
//                console.log($scope.meterPeriodList);
//                console.log($scope.btnClick);
//                console.log($scope.btnOkCaption);
//                console.log($scope.btnCancelCaption);
//                console.log($scope.showCancelButton);
            });
        }
    ]};
});