/*jslint node: true, eqeq: true*/
/*global angular, $*/
'use strict';
var app  = angular.module('portalNMC');
app.directive('showDatasourceModal', function () {
    return {
        restrict: "AE",
        replace: true,
        scope: {
            currentDatasource: "=",
            dsourceTypes: "=",
            showOkButton: "@",
            btnClick: "&",
            readOnly: "@"
            
        },
        templateUrl: "scripts/directives/templates/show-datasource-modal.html",
        controller: ['$scope', '$http', 'mainSvc', function ($scope, $http, mainSvc) {
            
                //default raw data source params
            var TIMEOUT = 25;
            var SLEEPTIME = 250;
            var RESENDS = 3;
            var RECONNECTS = 1;
            var RECONNECT_TIMEOUT = 60;
            var MODEM_DIAL_TEL_LENGTH = 10;
            var DATA_SOURCE_TYPE_KEY_DEFAULT = "DEVICE";
            var RAW_CONNECTION_TYPE_DEFAULT = "SERVER";
            var rawModemModelsUrl = "../api/rma/dataSources/rawModemModels";
            
            
            function getRawModemModels() {
                $http.get(rawModemModelsUrl).then(function (resp) {
                    $scope.rawModemModels = resp.data;
                }, function (e) {
                    console.log(e);
                });
            }
            getRawModemModels();
            
            $scope.DEVICE_MODES = [
                {
                    caption: "Сервер",
                    keyname: "SERVER"
                },
                {
                    caption: "Клиент",
                    keyname: "CLIENT"
                }
            ];
            
            function findModemIdentity(mmId) {
                if ($scope.rawModemModels.length === 0) {
                    return null;
                }
                var result = null;
                $scope.rawModemModels.some(function (model) {
                    if (model.id === mmId) {
                        result = model;
                        return true;
                    }
                });
                return result;
            }
            
            $scope.changeModemModel = function () {
                var modemModel = findModemIdentity($scope.currentDatasource.rawModemModelId);
                $scope.currentDatasource.rawModemIdentity = modemModel.rawModemModelIdentity;
                $scope.currentDatasource.rawModemDialupAvailable = modemModel.isDialup;
            };
            
            $scope.checkPositiveNumberValue = function (num) {
                return mainSvc.checkPositiveNumberValue(num);
            };
            
            $scope.isDisabled = function () {
                return $scope.readOnly;
            };
            
            $('#showDatasourceModal').on('shown.bs.modal', function () {
                $("#inputDataSourceMode").focus();
        //        inputDSTimeout inputDSCheckInterval inputDSRepeatCount inputDSReconnectionCount inputDSReconnectionInterval
                $("#inputDSTimeout").inputmask();
                $("#inputDSCheckInterval").inputmask();
                $("#inputDSRepeatCount").inputmask();
                $("#inputDSReconnectionCount").inputmask();
                $("#inputDSReconnectionInterval").inputmask();
                
                if (mainSvc.checkUndefinedNull($scope.currentDatasource.rawTimeout)) {
                    $scope.currentDatasource.rawTimeout = TIMEOUT;
                }
                if (mainSvc.checkUndefinedNull($scope.currentDatasource.rawSleepTime)) {
                    $scope.currentDatasource.rawSleepTime = SLEEPTIME;
                }
                if (mainSvc.checkUndefinedNull($scope.currentDatasource.rawResendAttempts)) {
                    $scope.currentDatasource.rawResendAttempts = RESENDS;
                }
                if (mainSvc.checkUndefinedNull($scope.currentDatasource.rawReconnectAttempts)) {
                    $scope.currentDatasource.rawReconnectAttempts = RECONNECTS;
                }
                if (mainSvc.checkUndefinedNull($scope.currentDatasource.rawReconnectTimeout)) {
                    $scope.currentDatasource.rawReconnectTimeout = RECONNECT_TIMEOUT;
                }
                if (mainSvc.checkUndefinedNull($scope.currentDatasource.rawModemIdentity) &&
                        !mainSvc.checkUndefinedNull($scope.currentDatasource.rawModemModelId)) {
                    var modemModel = findModemIdentity($scope.currentDatasource.rawModemModelId);
                    $scope.currentDatasource.rawModemIdentity = modemModel.rawModemModelIdentity;
                    $scope.currentDatasource.rawModemDialupAvailable = modemModel.isDialup;
                }
                $scope.$apply();
//                console.log($scope.currentDatasource);
//                console.log($scope.dsourceTypes);
            });
            
        }]
    };
});