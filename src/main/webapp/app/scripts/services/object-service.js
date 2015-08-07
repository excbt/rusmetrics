'use strict';
angular.module('portalNMC')
    .service('objectSvc', ['crudGridDataFactory', '$http', '$cookies', '$interval',
             function(crudGridDataFactory, $http, $cookies, $interval)
//             function()
             {
console.log("Object Service. Run.");                 
        var svcObjects = [{fullName:"Ошибка. Объекты не были загружены."
        }];
        var loading = true;
        var crudTableName = '../api/subscr/contObjects';
        var urlRefRange = '../api/subscr/contObjects/';
        var urlDeviceObjects = '/deviceObjects';
        var urlDeviceMetaData = '/metaVzlet';
        var urlDeviceMetaDataSystemList = '../api/subscr/deviceObjects/metaVzlet/system';//urlDeviceObjects+urlDeviceMetaData+'/system';
        var urlCitiesData = "../api/subscr/service/hwater/contObjects/serviceTypeInfo";
        
        var getObjectsUrl = function(){
            return crudTableName;
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
         
                 //Get data for the setting period for the city by cityId
        var getObjectConsumingData = function(settings, objId){
            var url=urlCitiesData+"/"+objId+"/?dateFrom="+settings.dateFrom+"&dateTo="+settings.dateTo;
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

       var promise = getObjectsData();
//       $interval(function(){
//           var time = (new Date()).toLocaleString();
//           document.getElementById('timeOutput').innerHTML="Время: "+time;
//console.log(time);           
//       },10000);
                 
        
                    
        return {
//            getObjects,
            getCitiesConsumingData,
            getObjectConsumingData,
            getDevicesByObject,
            getDeviceMetaData,
            getDeviceMetaDataSystemList,
            getObjectsUrl,
            getRefRangeByObjectAndZpoint,
            getVzletSystemList,
            getZpointsDataByObject,
            loading,
            promise,
            putDeviceMetaData,
            sortObjectsByFullName
            
        }
    
}]);