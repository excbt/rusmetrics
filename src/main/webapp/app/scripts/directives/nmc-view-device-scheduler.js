/*jslint node: true, white: true */
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
                btnOkClick: "&",
                btnOkCaption: "@"
            },
            controller: ['$scope', 'mainSvc', function($scope, mainSvc){               
                
                $scope.data = {};
                $scope.data.currentObject = $scope.currentDevice;
                $scope.data.currentScheduler = $scope.currentScheduler;
                
                $scope.checkHHmm = function(hhmmValue){
                    return mainSvc.checkHHmm(hhmmValue);
                };

                $scope.checkPositiveNumberValue = function(numvalue){
                    return mainSvc.checkPositiveNumberValue(numvalue);
                };

                $scope.checkScheduler = function(scheduler){
                    return $scope.checkHHmm(scheduler.loadingInterval)
                        && $scope.checkHHmm(scheduler.loadingRetryInterval)
                        && $scope.checkPositiveNumberValue(scheduler.loadingAttempts);
                };
                
                $scope.checkAutoLoadingDisabled = function(){
                    if (mainSvc.checkUndefinedNull($scope.data.currentObject) || mainSvc.checkUndefinedNull($scope.data.currentObject.activeDataSource) || mainSvc.checkUndefinedNull($scope.data.currentObject.activeDataSource.subscrDataSource))
                        {return false;}
                    return $scope.data.currentObject.activeDataSource.subscrDataSource.rawConnectionType === 'CLIENT' && $scope.data.currentObject.activeDataSource.subscrDataSource.rawModemDialEnable !== true;
                };
                
                $('#scheduleEditorModal').on('shown.bs.modal', function(){
                    console.log($scope.currentDevice);
                    console.log($scope.currentScheduler);
                    console.log($scope.data);
                });
                
            }]
        };
        
    }]);