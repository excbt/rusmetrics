'use strict';
angular.module('portalNMC')
.controller('MngmtDevicesCtrl', ['$scope','$http', 'objectSvc', 'notificationFactory', function($scope, $http, objectSvc, notificationFactory){
console.log('Run devices management controller.');
    //settings
    $scope.ctrlSettings = {};
    $scope.ctrlSettings.datasourcesUrl = objectSvc.getDatasourcesUrl();
    //data
    $scope.data = {};
    $scope.data.dataSources = [];
    $scope.data.objects = [];
    $scope.data.devices = [];
    $scope.data.deviceModels = [];
    $scope.data.currentObject = {};
            //get devices
    $scope.getDevices = function(){
        objectSvc.getAllDevices().then(
            function(response){
                $scope.data.devices = response.data;
console.log(response.data);                
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
//console.log($scope.data.deviceModels);                
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
            $scope.data.dataSources = tmp;
//console.log(tmp);            
        },
              function(e){
            console.log(e);
        });
    };
    getDatasources($scope.ctrlSettings.datasourcesUrl);
    
                 //get Objects
    $scope.getObjects = function(){
//        crudGridDataFactory($scope.objectsUrl).query(function(data){
        objectSvc.promise.then(function(response){
            objectSvc.sortObjectsByFullName(response.data);
            $scope.data.objects = response.data;
        });
    };
    $scope.getObjects();
    
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
    
    $scope.deviceDatasourceChange = function(){
        var curDataSource = null;
        $scope.data.dataSources.some(function(source){
            if (source.id === $scope.data.currentObject.subscrDataSourceId){
                curDataSource = source;
                return true;
            };
        });
        $scope.data.currentObject.curDatasource = curDataSource;       
    };
    
    var successCallback = function(response){
        notificationFactory.success();
        $scope.getDevices();
        $('#showDeviceModal').modal('hide');
        $('#deleteObjectModal').modal('hide');
        $scope.data.currentObject = {};
    };
    
    var errorCallback = function(e){
        notificationFactory.errorInfo(e.statusText,e);       
        console.log(e);
    };
    
    $scope.saveDevice = function(device){ 
        //check device data
        var checkDsourceFlag = true;
        if (device.contObjectId==null){
            notificationFactory.errorInfo("Ошибка","Не задан объект учета");
            checkDsourceFlag = false;
        };
        if (checkDsourceFlag === false){
            return;
        };
console.log(device);        
        //send to server
        var paramString = "";
        if (angular.isDefined(device.subscrDataSourceAddr)&&(device.subscrDataSourceAddr!=null)){
                paramString = paramString+"&subscrDataSourceAddr="+device.subscrDataSourceAddr;
        };
        if (angular.isDefined(device.dataSourceTable)&&(device.dataSourceTable!=null)){
            paramString = paramString+"&dataSourceTable="+device.dataSourceTable;
        };
        if (angular.isDefined(device.dataSourceTable1h)&&(device.dataSourceTable1h!=null)){
            paramString = paramString+"&dataSourceTable1h="+device.dataSourceTable1h;
        };
        if (angular.isDefined(device.dataSourceTable24h)&&(device.dataSourceTable24h!=null)){
            paramString = paramString+"&dataSourceTable24h="+device.dataSourceTable24h;
        };
        var targetUrl = objectSvc.getObjectsUrl()+"/"+device.contObjectId+"/deviceObjects";
        if (angular.isDefined(device.id)&&(device.id !=null)){
            targetUrl = targetUrl+"/"+device.id;
            targetUrl = targetUrl+"/?subscrDataSourceId="+device.subscrDataSourceId;
            targetUrl= targetUrl +paramString;
            $http.put(targetUrl, device).then(successCallback,errorCallback);
//            $http({
//                method: 'PUT',
//                params: {subscrDataSourceId: device.subscrDataSourceId,
//                         subscrDataSourceAddr: device.subscrDataSourceAddr,
//                         dataSourceTable: device.dataSourceTable,
//                         dataSourceTable1h: device.dataSourceTable1h,
//                         dataSourceTable24h: device.dataSourceTable24h
//                        },
//                data: device
//            }).then(successCallback,errorCallback);
        }else{
//            targetUrl = targetUrl+"/deviceObjects/";
            targetUrl = targetUrl + paramString;
            $http.post(targetUrl, device).then(successCallback,errorCallback);
//            $http({
//                method: 'POST',
//                params: {subscrDataSourceId: device.subscrDataSourceId,
//                         subscrDataSourceAddr: device.subscrDataSourceAddr,
//                         dataSourceTable: device.dataSourceTable,
//                         dataSourceTable1h: device.dataSourceTable1h,
//                         dataSourceTable24h: device.dataSourceTable24h
//                        },
//                data: device
//            }).then(successCallback,errorCallback);
        };
    };
    
    $scope.deleteObject = function(device){
console.log(device);        
        var targetUrl = objectSvc.getObjectsUrl();
        targetUrl = targetUrl+"/"+device.contObjectInfo.contObjectId+"/deviceObjects/"+device.id;
        $http.delete(targetUrl).then(successCallback,errorCallback);
    };
    
}]);