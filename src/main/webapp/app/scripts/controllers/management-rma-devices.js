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
                var tmp = response.data;
                tmp.forEach(function(elem){
                    if (angular.isDefined(elem.contObjectInfo)&&(elem.contObjectInfo!=null)){
                        elem.contObjectId = elem.contObjectInfo.contObjectId;
                    };
                    if (angular.isDefined(elem.activeDataSource)&&(elem.activeDataSource!=null)){
                        elem.subscrDataSourceId = elem.activeDataSource.subscrDataSource.id;
                        elem.curDatasource = elem.activeDataSource.subscrDataSource;
                        elem.subscrDataSourceAddr = elem.activeDataSource.subscrDataSourceAddr;
                        elem.dataSourceTable1h = elem.activeDataSource.dataSourceTable1h;
                        elem.dataSourceTable24h = elem.activeDataSource.dataSourceTable24h;
                    };
                });
                $scope.data.devices = tmp;
console.log(tmp);                
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
        if (device.subscrDataSourceId==null){
            notificationFactory.errorInfo("Ошибка","Не задан источник данных");
            checkDsourceFlag = false;
        };
        if (checkDsourceFlag === false){
            return;
        };
//console.log(device);        
        //send to server
            //create param string
        var paramString = "";
        if (angular.isDefined(device.subscrDataSourceAddr)&&(device.subscrDataSourceAddr!=null)){
                paramString = paramString+"subscrDataSourceAddr="+device.subscrDataSourceAddr;
        };
        if (angular.isDefined(device.dataSourceTable)&&(device.dataSourceTable!=null)){
            if (paramString!=""){
                paramString+="&";
            };
            paramString = paramString+"dataSourceTable="+device.dataSourceTable;
        };
        if (angular.isDefined(device.dataSourceTable1h)&&(device.dataSourceTable1h!=null)){
            if (paramString!=""){
                paramString+="&";
            };
            paramString = paramString+"dataSourceTable1h="+device.dataSourceTable1h;
        };
        if (angular.isDefined(device.dataSourceTable24h)&&(device.dataSourceTable24h!=null)){
            if (paramString!=""){
                paramString+="&";
            };
            paramString = paramString+"dataSourceTable24h="+device.dataSourceTable24h;
        };
        var targetUrl = objectSvc.getRmaObjectsUrl()+"/"+device.contObjectId+"/deviceObjects";
        if (angular.isDefined(device.id)&&(device.id !=null)){
            targetUrl = targetUrl+"/"+device.id;
        };
            //add url params
        targetUrl = targetUrl+"/?subscrDataSourceId="+device.subscrDataSourceId;
        if (paramString!=""){
            paramString="&"+paramString;
        };
        targetUrl= targetUrl +paramString;
        if (angular.isDefined(device.id)&&(device.id !=null)){
            $http.put(targetUrl, device).then(successCallback,errorCallback);
        }else{
            $http.post(targetUrl, device).then(successCallback,errorCallback);
        };
    };
    
    $scope.deleteObject = function(device){
//console.log(device);        
        var targetUrl = objectSvc.getRmaObjectsUrl();
        targetUrl = targetUrl+"/"+device.contObjectInfo.contObjectId+"/deviceObjects/"+device.id;
        $http.delete(targetUrl).then(successCallback,errorCallback);
    };
    
    // device metadata
        //check device: data source vzlet or not?
    $scope.vzletDevice = function(device){
        var result = false;
        if(angular.isDefined(device.activeDataSource)&&(device.activeDataSource != null)){
            if(angular.isDefined(device.activeDataSource.subscrDataSource)&&(device.activeDataSource.subscrDataSource!=null)){
                if (device.activeDataSource.subscrDataSource.dataSourceTypeKey=="VZLET"){
                    result = true;
                };
            };
        };
        return result;
    };
    
    //get device meta data and show it
    $scope.getDeviceMetaData = function(device){
        
        objectSvc.getRmaDeviceMetaData(device.contObjectInfo.contObjectId, device).then(
            function(response){                           
                device.metaData = response.data; 
                $scope.currentDevice =  device;                           
                $('#metaDataEditorModal').modal();
            },
            function(error){
                notificationFactory.errorInfo(error.statusText,error.description);
            }
        );
    };

    $scope.updateDeviceMetaData = function(device){
console.log(device);    
        var method = "";
        if(angular.isDefined(device.metaData.id)&&(device.metaData.id!==null)){
            method = "PUT";
        }else{
            method = "POST";
        };
//        var url = "../api/subscr/contObjects/"+device.contObject.id+"/deviceObjects/"+device.id+"/metaVzlet";
        var url = objectSvc.getRmaObjectsUrl()+"/"+device.contObjectId+"/deviceObjects/"+device.id+"/metaVzlet";
        $http({
            url: url,
            method: method,
            data: device.metaData
        })
//                    $http.put(url, device.metaData)
            .then(
//                    objectSvc.putDeviceMetaData(device).then(
            function(response){
                $scope.currentDevice =  {};
                $('#metaDataEditorModal').modal('hide');
            },
            function(error){
                console.log(error);                            
                notificationFactory.errorInfo(error.statusText,error.description);
            }
        );
    };
    
     //get the list of the systems for meta data editor
    $scope.getVzletSystemList = function(){
        var tmpSystemList = objectSvc.getVzletSystemList();
        if (tmpSystemList.length===0){
            objectSvc.getDeviceMetaDataSystemList()
                .then(
                function(response){
                    $scope.data.vzletSystemList = response.data;                           
                },
                function(e){
                    notificationFactory.errorInfo(e.statusText,e.description);
                }
            );
        }else{
            $scope.data.vzletSystemList =tmpSystemList;
        };
console.log($scope.data.vzletSystemList);        
    };
    $scope.getVzletSystemList();
    
}]);