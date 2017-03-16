/*jslint node: true, eqeq: true */
/*global angular, $ */

'use strict';

angular.module('portalNMC')
    .directive('nmcViewDeviceScheduler', [function () {
        return {
            restrict: "AE",
            replace: true,
            templateUrl: "scripts/directives/templates/nmc-view-device-scheduler.html",
            scope: {
                currentDevice: "=",
                currentScheduler: "=",
                showOkButton: "@",
                btnOkClick: "&",
                btnOkCaption: "@",
                readOnly: "@"
            },
            controller: ['$scope', 'mainSvc', function ($scope, mainSvc) {
                
                $scope.checkHHmm = function (hhmmValue) {
                    return mainSvc.checkHHmm(hhmmValue);
                };

                $scope.checkPositiveNumberValue = function (numvalue) {
                    return mainSvc.checkPositiveNumberValue(numvalue);
                };

                $scope.checkScheduler = function (scheduler) {
                    return $scope.checkHHmm(scheduler.loadingInterval)
                        && $scope.checkHHmm(scheduler.loadingRetryInterval)
                        && $scope.checkPositiveNumberValue(scheduler.loadingAttempts);
                };
                
                $scope.checkAutoLoadingDisabled = function () {
                    if (mainSvc.checkUndefinedNull($scope.currentDevice) || mainSvc.checkUndefinedNull($scope.currentDevice.activeDataSource) || mainSvc.checkUndefinedNull($scope.currentDevice.activeDataSource.subscrDataSource)) {
                        return false;
                    }
                    return $scope.currentDevice.activeDataSource.subscrDataSource.rawConnectionType === 'CLIENT' && $scope.currentDevice.activeDataSource.subscrDataSource.rawModemDialEnable !== true;
                };
                
                $scope.isDisabled = function () {
                    return $scope.readOnly;
                };
                
                $('#scheduleEditorModal').on('shown.bs.modal', function () {
//                    console.log($scope.currentDevice);
//                    console.log($scope.currentScheduler);
//                    console.log($scope.data);
                });
                
            }]
        };
        
    }]);