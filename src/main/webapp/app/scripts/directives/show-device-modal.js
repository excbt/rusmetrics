/*jslint node: true, white: true */
/*global angular */
'use strict';
angular.module('portalNMC')
    .directive('nmcShowDeviceModal', function(){
    return {
        restrict: "AE",
        replace: false,
        scope: {
            currentDevice: "=",
            deviceSources: "=",
            deviceModels: "=",
            contObjects: "=",
            
            readOnly: "@",
            btnClick: "&",
            btnOkCaption: "@",
            showOkButton: "@"
        },
        templateUrl: 'scripts/directives/templates/show-device-modal.html',
        controller: ['$scope', 'mainSvc', function($scope, mainSvc){
            
            $scope.isDeviceDisabled = function(device){        
                return $scope.readOnly || device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS' || (device.isSaving === true);
            };
            
            $scope.isDeviceDataSourceHide = function (device) {
                return device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS' || (device.isSaving === true);
            };
            
            $scope.isDeviceProtoLoaded = function(device){        
                return device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS';
            };
            
            $scope.changeDeviceModel = function(){
console.log("changeDeviceModel");                
                if (!mainSvc.checkUndefinedNull($scope.currentDevice.deviceModelId)){
                    $cookies.recentDeviceModelId = $scope.currentDevice.deviceModelId;
                }
            };
            
            $scope.deviceDatasourceChange = function(){
                $scope.currentDevice.dataSourceTable1h = null;
                $scope.currentDevice.dataSourceTable24h = null;
                $scope.currentDevice.subscrDataSourceAddr = null;
                var curDataSource = null;
                $scope.deviceSources.some(function(source){
                    if (source.id === $scope.currentDevice.subscrDataSourceId){
                        curDataSource = source;
                        return true;
                    };
                });
                $scope.currentDevice.curDatasource = curDataSource;

                if (!mainSvc.checkUndefinedNull($scope.currentDevice.subscrDataSourceId)){
                    $cookies.recentDataSourceId = $scope.currentDevice.subscrDataSourceId;
                }
            };
            
            $scope.isAvailableConPropertiesTab = function(){
//console.log($scope.currentDevice);                
                if (mainSvc.checkUndefinedNull($scope.currentDevice) || mainSvc.checkUndefinedNull($scope.currentDevice.curDatasource)){
                    return false;
                };
                return $scope.currentDevice.curDatasource.dataSourceType.isRaw === true || $scope.currentDevice.curDatasource.dataSourceType.isDbTablePair === true || 
                    $scope.currentDevice.exSystemKeyname === 'VZLET';
            };
            
            $('#showDeviceModal').on('shown.bs.modal', function(){
//                console.log($scope.currentDevice);
//                console.log($scope.deviceSources);
//                
//                console.log($scope.deviceModels);
            });
            
        }]
    }
})