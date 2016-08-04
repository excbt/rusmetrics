/*jslint node: true, white: true */
/*global angular */
'use strict';
angular.module('portalNMC')
    .directive('showDeviceModal', function(){
    return {
        restrict: "AE",
        replace: true,
        scope: {
            data: "@",
            readOnly: "@",
            btnClick: "&",
            btnOkCaption: "@",
            showOkButton: "@"
        },
        templateUrl: 'scripts/directives/templates/show-device-modal.html',
        controller: ['$scope', function($scope){
            
            $scope.isDeviceDisabled = function(device){        
                return device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS' || (device.isSaving === true);
            };
            
            $scope.isDeviceProtoLoaded = function(device){        
                return device.exSystemKeyname === 'VZLET' || device.exSystemKeyname === 'LERS';
            };
            
            $scope.changeDeviceModel = function(){
                if (!mainSvc.checkUndefinedNull($scope.data.currentObject.deviceModelId)){
                    $cookies.recentDeviceModelId = $scope.data.currentObject.deviceModelId;
                }
            };
            
            $scope.deviceDatasourceChange = function(){
                $scope.data.currentObject.dataSourceTable1h = null;
                $scope.data.currentObject.dataSourceTable24h = null;
                $scope.data.currentObject.subscrDataSourceAddr = null;
                var curDataSource = null;
                $scope.data.dataSources.some(function(source){
                    if (source.id === $scope.data.currentObject.subscrDataSourceId){
                        curDataSource = source;
                        return true;
                    };
                });
                $scope.data.currentObject.curDatasource = curDataSource;

                if (!mainSvc.checkUndefinedNull($scope.data.currentObject.subscrDataSourceId)){
                    $cookies.recentDataSourceId = $scope.data.currentObject.subscrDataSourceId;
                }
            };
            
            $scope.isAvailableConPropertiesTab = function(){
                if (mainSvc.checkUndefinedNull($scope.data.currentObject.curDatasource)){
                    return false;
                };
                return $scope.data.currentObject.curDatasource.dataSourceType.isRaw === true || $scope.data.currentObject.curDatasource.dataSourceType.isDbTablePair === true || 
                    $scope.data.currentObject.exSystemKeyname === 'VZLET';
            };
            
        }]
    }
})