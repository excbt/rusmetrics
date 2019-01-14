/*jslint node: true*/
/*global angular, $*/
'use strict';
angular.module('portalNMC')
    .directive('nmcViewIndicatorsElectricity', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                contObjectId: "@",
                contObjectName: "@",
                contZpointId: "@",
                contZpointName: "@",
                contZpointType: "@",
                deviceModelId: "@",
                deviceNumber: "@",
                isManual: "@"
            },
            templateUrl: "scripts/directives/templates/nmc-view-indicators-electricity.html",
            controller: ['$scope', 'mainSvc', 'notificationFactory', 'objectSvc', 'indicatorSvc', '$cookies', function ($scope, mainSvc, notificationFactory, objectSvc, indicatorSvc, $cookies) {
                
            }]
        };
    });