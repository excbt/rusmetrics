'use strict';
angular.module('portalNMC')
.service('objectSvc', ['crudGridDataFactory', '$http', '$cookies', '$interval', '$rootScope',
             function(crudGridDataFactory, $http, $cookies, $interval, $rootScope){
console.log("Object Service. Run.");                 
        var svcObjects = [{fullName:"Ошибка. Объекты не были загружены."
        }];
        var loading = true;
        var urlApi ='../api';
        var urlSubscr = urlApi+'/subscr';
        var urlRma = urlApi+'/rma';
        var urlDatasources = urlRma+'/dataSources';
        var crudTableName = urlSubscr+'/contObjects';
        var urlRmaContObjects = urlRma+'/contObjects';                 
        var urlRefRange = urlSubscr+'/contObjects/';
        var urlDeviceObjects = '/deviceObjects';
        var urlDeviceModels = urlRma+urlDeviceObjects+'/deviceModels';
        var urlDeviceMetaData = '/metaVzlet';
        var urlDeviceMetaDataSystemList = urlSubscr+'/deviceObjects/metaVzlet/system';//urlDeviceObjects+urlDeviceMetaData+'/system';
        var urlCitiesData = urlSubscr+'/service/hwater/contObjects/serviceTypeInfo';  
        var urlTimezones = urlApi+'/timezoneDef/all';
        var urlRsoOrganizations = urlRmaContObjects+'/rsoOrganizations';
        var urlCmOrganizations = urlRmaContObjects+'/cmOrganizations';
        var urlServiceTypes = urlRmaContObjects+'/contServiceTypes';//'resource/serviceTypes.json';
        
        
        var objectSvcSettings = {};
        var getObjectSettings = function(){
            return objectSvcSettings;
        };
        
        var setObjectSettings = function(objectSettings){
//            monitorSvcSettings = monitorSettings;
            for (var key in objectSettings){
                objectSvcSettings[key]=objectSettings[key];
            };
        };
        
        var getObjectsUrl = function(){
            return crudTableName;
        };
                 
        var getRmaObjectsUrl = function(){
            return urlRmaContObjects;
        };
        var getSubscrUrl = function(){
            return urlSubscr;
        };
        var getRmaUrl = function(){
            return urlRma;
        };
        
        var getDatasourcesUrl = function(){
            return urlDatasources;
        };
        
        var getLoadingStatus = function(){
            return loading;
        };
        
        var getRsoOrganizations = function(){
            var url = urlRsoOrganizations;
            return $http.get(url);
        };
        var getCmOrganizations = function(){
            var url = urlCmOrganizations;
            return $http.get(url);
        };
                 
        var getServiceTypes = function(){
            var url = urlServiceTypes;
            return $http.get(url);
        };
                 
        //universal function
        var getData = function(url){
            return $http.get(url);
        };
                //Функция для получения эталонного интервала для конкретной точки учета конкретного объекта
        var getRefRangeByObjectAndZpoint = function(object, zpoint){
            var url = urlRefRange + object.id + '/zpoints/' + zpoint.id + '/referencePeriod';                  
            return $http.get(url);
        };
                 
        var zPointsByObject = [];
        var getZpointsDataByObject = function(obj, mode){ 
            obj.zpoints = [];
            var table = crudTableName+"/"+obj.id+"/contZPoints"+mode;//Ex";
            return $http.get(table);
        };         
        
        var getDevicesByObject = function(obj){
            var url = crudTableName+"/"+obj.id+urlDeviceObjects;
            return $http.get(url);
        };
        var getAllDevices = function(){
            var url = urlRmaContObjects+urlDeviceObjects;
            return $http.get(url);
        };
        var getDeviceModels = function(){
            var url = urlDeviceModels;
            return $http.get(url);
        };
                 
        var getDeviceMetaData = function(obj, device){                     
            var url = crudTableName+"/"+obj.id+urlDeviceObjects+"/"+device.id+urlDeviceMetaData;
            return $http.get(url);
        };
        var getRmaDeviceMetaData = function(objId, device){                     
            var url = urlRmaContObjects+"/"+objId+urlDeviceObjects+"/"+device.id+urlDeviceMetaData;
            return $http.get(url);
        };
        var putDeviceMetaData = function(device){                     
            var url = crudTableName+"/"+device.contObject.id+urlDeviceObjects+"/"+device.id+urlDeviceMetaData;
            var result = $http.put(url, device.metaData);
//console.log(result);            
            return result;
        };
                 
        //get time zones
        var getTimezones = function(){
            return getData(urlTimezones);
        };
                 
        var getDeviceMetaDataSystemList = function(){                     
            return $http.get(urlDeviceMetaDataSystemList);
        };
        var vzletSystemList = [];
        var getVzletSystemListFromServer = function(){
            getDeviceMetaDataSystemList()
            .then(
                function(response){
                    vzletSystemList = response.data;
                },
                function(e){
                    console.log(e);
                }
            );
        };         
        getVzletSystemListFromServer();
        var getVzletSystemList = function(){
            return vzletSystemList;
        };
    
        
        //get objects
        var getObjectsData = function () {
console.log("Get data from server");            
           return $http.get(crudTableName);//.then(function(res){console.log(res)});
        };
        var getRmaObjectsData = function () {
           return $http.get(getRmaObjectsUrl());
        };
         
                 //Get data for the setting period for the city by objectId
        var getObjectConsumingData = function(settings, objId){
            var url=urlCitiesData+"/"+objId+"/?dateFrom="+settings.dateFrom+"&dateTo="+settings.dateTo;
            return $http.get(url);
        };
                 
                //get data for the setting period for one city
        var getCityConsumingData = function(cityFias, settings){
            var url=urlCitiesData+"/city/?dateFrom="+settings.dateFrom+"&dateTo="+settings.dateTo+"&cityFias="+cityFias;
            return $http.get(url);
        };         
                 //get data for the setting period for all cities
        var getCitiesConsumingData = function(settings){
            var url=urlCitiesData+"/?dateFrom="+settings.dateFrom+"&dateTo="+settings.dateTo;
            return $http.get(url);
        };
                 
            // sort the object array by the fullname
        function sortObjectsByFullName(array){
            array.sort(function(a, b){
                if (a.fullName>b.fullName){
                    return 1;
                };
                if (a.fullName<b.fullName){
                    return -1;
                };
                return 0;
            }); 
        };
                 
        function sortObjectsByConObjectFullName(array){
            array.sort(function(a, b){
                if (a.contObject.fullName>b.contObject.fullName){
                    return 1;
                };
                if (a.contObject.fullName<b.contObject.fullName){
                    return -1;
                };
                return 0;
            }); 
        };
                 
        function findObjectById(objId, objectArr){
            var obj = null;
            if (!angular.isArray(objectArr)){
                return obj;
            };
            objectArr.some(function(element){
                if (element.id === objId){
                    obj = element;
                    return true;
                }
            });        
            return obj;
        };

       var promise = null;//getObjectsData();
       var rmaPromise = null;//getRmaObjectsData();
       var loadData = function(){
         promise = getObjectsData();
         rmaPromise = getRmaObjectsData();
       };
       loadData();
                 
       $rootScope.$on('objectSvc:requestReloadData', function(){        
           loadData();
       });
        var getPromise = function(){
            return promise;
        };
        var getRmaPromise = function(){
            return rmaPromise;
        };
//       $interval(function(){
//           var time = (new Date()).toLocaleString();
//           document.getElementById('timeOutput').innerHTML="Время: "+time;
//console.log(time);           
//       },10000);
                 
                 //if data loaded
//        promise.then(function(response){
//console.log("objectSvc:loaded");            
//            loading = false;
//            $rootScope.$broadcast('objectSvc:loaded');
//        });
                 
        var sendDeviceToServer = function(device){
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
            var targetUrl = getRmaObjectsUrl()+"/"+device.contObjectId+"/deviceObjects";
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
                return $http.put(targetUrl, device);//.then(successCallback,errorCallback);
            }else{
                return $http.post(targetUrl, device);//.then(successCallback,errorCallback);
            };
            return null;
        };
                    
        return {
            getAllDevices,
            getCityConsumingData,
            getCitiesConsumingData,
            getCmOrganizations,
            getDatasourcesUrl,
            getDeviceModels,
            getObjectConsumingData,
            getObjectSettings,
            getDevicesByObject,
            getDeviceMetaData,
            getDeviceMetaDataSystemList,
            getLoadingStatus,
            getObjectsUrl,
            getPromise,
            getRmaDeviceMetaData,
            getRmaObjectsData,
            getRmaObjectsUrl,
            getRmaPromise,
            getRefRangeByObjectAndZpoint,
            getRsoOrganizations,
            getServiceTypes,
            getSubscrUrl,
            getTimezones,
            getVzletSystemList,
            getZpointsDataByObject,
            findObjectById,
            loading,
            promise,
            putDeviceMetaData,
            rmaPromise,
            sendDeviceToServer,
            setObjectSettings,
            sortObjectsByFullName,
            sortObjectsByConObjectFullName
            
        };
    
}]);