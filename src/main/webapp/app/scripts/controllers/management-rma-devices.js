'use strict';
angular.module('portalNMC')
.controller('MngmtDevicesCtrl', ['$scope', 'objectSvc', function($scope, objectSvc){
console.log('Run devices management controller.');
    //settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.datasourcesUrl = 
    //data
    $scope.data = {};
    $scope.data.devices = [];
    $scope.data.deviceModels = [];
    $scope.data.currentObject = {};
            //get devices
    $scope.getDevices = function(){
        objectSvc.getAllDevices().then(
            function(response){
                $scope.data.devices = response.data;
            },
            function(error){
                notificationFactory.errorInfo(error.statusText,error.description);
            }
        );
    };
    $scope.getDevices();
    
                //get device models
    $scope.getDeviceModels = function(){
        objectSvc.getDeviceModels().then(
            function(response){
                $scope.data.deviceModels = response.data;
console.log($scope.data.deviceModels);                
            },
            function(error){
                notificationFactory.errorInfo(error.statusText,error.description);
            }
        );
    };
    $scope.getDeviceModels();
    
           //get data sources
    var getDatasources = function(url){
        var targetUrl = url;
        $http.get(targetUrl)
        .then(function(response){
            var tmp = response.data;      
//            tmp.forEach(function(dsource){
//                dsource.caption = dsource.dataSourceName
//            });
            $scope.data.dataSources = tmp;
        },
              function(e){
            console.log(e);
        });
    };
    getDatasources($scope.ctrlSettings.datasourcesUrl);
    
    $scope.selectedItem = function(item){
        $scope.data.currentObject = angular.copy(item);
    };
    
    $scope.addDevice = function(){
        $scope.data.currentObject = {};
        $('#showDeviceModal').modal();
    };
    $scope.editDevice = function(device){
        $scope.selectedItem(device);
//        $scope.data.currentObject = angular.copy(dsource);
        $('#showDeviceModal').modal();
    };
    
    
}]);