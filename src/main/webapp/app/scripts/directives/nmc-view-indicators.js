/*jslint node: true*/
/*global angular, $*/
'use strict';
angular.module('portalNMC')
    .directive('nmcViewIndicators', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                contObjectId: "=",
                contObjectName: "=",
                contZpointId: "=",
                contZpointName: "=",
                contZpointType: "=",
                deviceModelId: "=",
                deviceNumber: "=",
                isManual: "="
            },
            templateUrl: "scripts/directives/templates/nmc-view-indicators.html",
            controller: ['$scope', 'mainSvc', 'notificationFactory', 'objectSvc', 'indicatorSvc', '$cookies', function ($scope, mainSvc, notificationFactory, objectSvc, indicatorSvc, $cookies) {
                
                $scope.modelLoadingFlag = false;

                function errorCallback(e) {
                    $scope.modelLoadingFlag = false;
                    var errorObj = mainSvc.errorCallbackHandler(e);
                    notificationFactory.errorInfo(errorObj.caption, errorObj.description);
                }
                
                function successLoadDeviceModelCallback(resp) {
                    $scope.modelLoadingFlag = false;
                    var devModel = resp.data;
                    $scope.isImpulse = devModel.isImpulse || devModel.deviceType === objectSvc.HEAT_DISTRIBUTOR;
                    $scope.deviceModelName = devModel.modelName;
                    
                    indicatorSvc.setContObjectId($scope.contObjectId);
                    indicatorSvc.setContObjectName($scope.contObjectName);
                    indicatorSvc.setDeviceModel($scope.deviceModelName);
                    indicatorSvc.setDeviceSN($scope.deviceNumber);
                    indicatorSvc.setZpointId($scope.contZpointId);
                    indicatorSvc.setZpointName($scope.contZpointName);
                }
                
                function loadDeviceModel(devModelId) {
                    $scope.modelLoadingFlag = true;
                    objectSvc.getDeviceModel(devModelId)
                        .then(successLoadDeviceModelCallback, errorCallback);
                    
                }
                
                function initCtrl() {
//                    console.log($scope.contObjectId);
//                    console.log($scope.contZpointId);
//                    console.log($scope.deviceModelId);
                    loadDeviceModel($scope.deviceModelId);
                }
                
                initCtrl();
                
            }]
        };
    });