/*jslint white: true, node: true */
/*global angular, $ */
'use strict';
angular.module('portalNMC')
.directive("nmcViewMeterPeriodsModal", function(){
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
        controller: function ($scope) {
//            $scope.data = {};
//            $scope.data.resourceSystems = [
//                {
//                    keyname: "heat",
//                    caption: "Система отопления",
//                    shortCaption: "СО"
//                },
//                {
//                    keyname: "hw",
//                    caption: "Горячее водоснабжение",
//                    shortCaption: "ГВС"
//                },
//                {
//                    keyname: "cw",
//                    caption: "Холодное водоснабжение",
//                    shortCaption: "ХВС"
//                },
//                {
//                    keyname: "el",
//                    caption: "Электроэнергия",
//                    shortCaption: "Эл-во"
//                }
//            ];
            $('#nmcMeterPeriodsModal').on('shown.bs.modal', function(){
                console.log($scope.contObjectName);
                console.log($scope.showOkButton);
                console.log($scope.resourceSystemList);
                console.log($scope.meterPeriodList);
                console.log($scope.btnClick);
                console.log($scope.btnOkCaption);
                console.log($scope.btnCancelCaption);
                console.log($scope.showCancelButton);
            });
        }
    };
});