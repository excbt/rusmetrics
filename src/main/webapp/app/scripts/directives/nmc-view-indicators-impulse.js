/*jslint node: true*/
/*global angular, $*/
'use strict';
angular.module('portalNMC')
    .directive('nmcViewIndicatorsImpulse', function () {
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
            templateUrl: "scripts/directives/templates/nmc-view-indicators-impulse.html",
            controller: ['$scope', 'mainSvc', 'notificationFactory', 'objectSvc', 'indicatorSvc', '$cookies', function ($scope, mainSvc, notificationFactory, objectSvc, indicatorSvc, $cookies) {
                
            }]
        };
    });