/*jslint node: true*/
/*global angular, $*/
'use strict';
angular.module('portalNMC')
    .directive('nmcViewIndicatorsContainer', function () {
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
            templateUrl: "scripts/directives/templates/nmc-view-indicators-container.html",
            controller: ['$scope', 'mainSvc', 'notificationFactory', 'objectSvc', 'indicatorSvc', '$cookies', function ($scope, mainSvc, notificationFactory, objectSvc, indicatorSvc, $cookies) {
                
                $scope.TYPES = {
                    IMPULSE: "IMPULSE",
                    ELECTRICITY: "EL",
                    COMMON: "COMMON"
                };
                
                $scope.indicatorsType = null;
                
                $scope.modelLoadingFlag = false;
                $scope.contObjectLoadingFlag = false;

                function errorCallback(e) {
                    $scope.modelLoadingFlag = false;
                    $scope.contObjectLoadingFlag = false;
                    var errorObj = mainSvc.errorCallbackHandler(e);
                    notificationFactory.errorInfo(errorObj.caption, errorObj.description);
                }
                
                function successLoadDeviceModelCallback(resp) {
                    $scope.modelLoadingFlag = false;
                    var devModel = resp.data;
                    $scope.isImpulse = devModel.isImpulse || devModel.deviceType === objectSvc.HEAT_DISTRIBUTOR;
                    
                    if ($scope.isImpulse) {
                        $scope.indicatorsType = $scope.TYPES.IMPULSE;
                    } else if ($scope.contZpointType === 'el') {
                        $scope.indicatorsType = $scope.TYPES.ELECTRICITY;
                    } else {
                        $scope.indicatorsType = $scope.TYPES.COMMON;
                    }
// console.log($scope.indicatorsType);
                    
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
                
                function successGetContObjectCallback(resp) {
                    $scope.contObjectName = resp.data.fullName;
                    $scope.contObjectLoadingFlag = false;
                    loadDeviceModel($scope.deviceModelId);
                }
                
                function loadContObject(contObjectId) {
                    $scope.contObjectLoadingFlag = true;
// console.log("contObjectId = " + contObjectId);
                    objectSvc.getObject(contObjectId)
                        .then(successGetContObjectCallback, errorCallback);
                }
                
                function initCtrl() {
//                    console.log($scope.contObjectId);
//                    console.log($scope.contZpointId);
//                    console.log($scope.deviceModelId);
                    //loadDeviceModel($scope.deviceModelId);
                    loadContObject($scope.contObjectId);
                }
                
                initCtrl();
                
            }]
        };
    });