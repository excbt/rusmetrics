'use strict';
angular.module('portalNMC')
    .service('objectSvc', ['crudGridDataFactory', '$http', '$cookies', '$interval', '$rootScope',
             function(crudGridDataFactory, $http, $cookies, $interval, $rootScope)
//             function()
             {
console.log("Object Service. Run.");                 
        var svcObjects = [{fullName:"Ошибка. Объекты не были загружены."
        }];
        var loading = true;
        var urlSubscr = '../api/subscr';
        var crudTableName = urlSubscr+'/contObjects';
        var urlRefRange = urlSubscr+'/contObjects/';
        var urlDeviceObjects = '/deviceObjects';
        var urlDeviceModels = urlSubscr+urlDeviceObjects+'/deviceModels';
        var urlDeviceMetaData = '/metaVzlet';
        var urlDeviceMetaDataSystemList = urlSubscr+'/deviceObjects/metaVzlet/system';//urlDeviceObjects+urlDeviceMetaData+'/system';
        var urlCitiesData = urlSubscr+'/service/hwater/contObjects/serviceTypeInfo';                
        
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
        
        var getLoadingStatus = function(){
            return loading;
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
            var url = crudTableName+urlDeviceObjects;
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
        var putDeviceMetaData = function(device){                     
            var url = crudTableName+"/"+device.contObject.id+urlDeviceObjects+"/"+device.id+urlDeviceMetaData;
            var result = $http.put(url, device.metaData);
//console.log(result);            
            return result;
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
           return $http.get(crudTableName);
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

       var promise = getObjectsData();
//       $interval(function(){
//           var time = (new Date()).toLocaleString();
//           document.getElementById('timeOutput').innerHTML="Время: "+time;
//console.log(time);           
//       },10000);
                 
                 //if data loaded
        promise.then(function(response){
console.log("objectSvc:loaded");            
//            loading = false;
//            $rootScope.$broadcast('objectSvc:loaded');
        });
                    
        return {
            getAllDevices,
            getCityConsumingData,
            getCitiesConsumingData,
            getDeviceModels,
            getObjectConsumingData,
            getObjectSettings,
            getDevicesByObject,
            getDeviceMetaData,
            getDeviceMetaDataSystemList,
            getLoadingStatus,
            getObjectsUrl,
            getRefRangeByObjectAndZpoint,
            getVzletSystemList,
            getZpointsDataByObject,
            findObjectById,
            loading,
            promise,
            putDeviceMetaData,
            setObjectSettings,
            sortObjectsByFullName,
            sortObjectsByConObjectFullName
            
        };
    
}]);