'use strict';
angular.module('portalNMC')
.service('objectSvc', ['crudGridDataFactory', '$http', '$cookies', '$interval', '$rootScope',
             function(crudGridDataFactory, $http, $cookies, $interval, $rootScope){
//console.log("Object Service. Run."); 
                 
        var svcObjects = [
            {fullName: "Ошибка. Объекты не были загружены."
            }
        ];
        var loading = true;
        var urlApi = '../api';
        var urlSubscr = urlApi + '/subscr';
        var urlRma = urlApi + '/rma';
        var urlDatasources = urlRma + '/dataSources';
        var crudTableName = urlSubscr + '/contObjects';
        var urlRmaContObjects = urlRma + '/contObjects';                 
        var urlRefRange = urlSubscr + '/contObjects/';
        var urlDeviceObjects = '/deviceObjects';
        var urlDeviceModels = urlRma+urlDeviceObjects + '/deviceModels';
        var urlDeviceMetaDataVzlet = '/metaVzlet';
        var urlDeviceMetaDataSuffix = '/metadata';
        var urlZpointMetaDataSuffix = '/metadata';                 
        var urlDeviceMetaDataVzletSystemList = urlSubscr + '/deviceObjects/metaVzlet/system';//urlDeviceObjects+urlDeviceMetaDataVzlet+'/system';
        var urlCitiesData = urlSubscr + '/service/hwater/contObjects/serviceTypeInfo';  
        var urlTimezones = urlApi + '/timezoneDef/all';
        var urlRsoOrganizations = urlRmaContObjects + '/rsoOrganizations';
        var urlCmOrganizations = urlRmaContObjects + '/cmOrganizations';
        var urlServiceTypes = urlRmaContObjects + '/contServiceTypes';//'resource/serviceTypes.json';
//                 /contObjects/deviceObjects/metadata/measureUnits
        var urlDeviceMetadataMeasures = urlRmaContObjects + urlDeviceObjects + "/metadata/measureUnits";
        var rmaTreeTemplatesUrl = urlRma + '/subscrObjectTreeTemplates';
        var rmaTreesUrl = urlRma + '/subscrObjectTree/contObjectTreeType1';                 
        
        var rmaTreeTemplates = [];

        var deviceMetadataMeasures = {};
        var zpointMetadataMeasures = {};
                 
        var currentObject = null; //the current selected object at interface

        var objectSvcSettings = {};
                 
        var getCurrentObject = function(){
            return currentObject;
        };
        var setCurrentObject = function(obj){
            currentObject = obj;
        };
                 
        var getObjectSettings = function(){
            return objectSvcSettings;
        };
                 
        var getDeviceMetadataMeasures = function(){
            return deviceMetadataMeasures;
        };
                 
        var getZpointMetadataMeasures = function(){
            return zpointMetadataMeasures;
        };
                 
        var getRmaTreeTemplates = function(){
            return rmaTreeTemplates;
        };
        
        var setObjectSettings = function(objectSettings){
            for (var key in objectSettings){
                objectSvcSettings[key] = objectSettings[key];
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
            var table = crudTableName + "/" + obj.id + "/contZPoints" + mode;//Ex";
            return $http.get(table);
        };         
        
        var getDevicesByObject = function(obj){
            var url = crudTableName + "/" + obj.id + urlDeviceObjects;
            return $http.get(url);
        };
        var getAllDevices = function(){
            var url = urlRmaContObjects + urlDeviceObjects;
            return $http.get(url);
        };
        var getDeviceModels = function(){
            var url = urlDeviceModels;
            return $http.get(url);
        };
                 
        var getDeviceSchedulerSettings = function(objId, devId){
//            /contObjects/%d/deviceObjects/%d/loadingSettings
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + devId + "/loadingSettings";
            return $http.get(url);            
        };
                 
        var putDeviceSchedulerSettings = function(objId, devId, scheduler){
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + devId + "/loadingSettings";
            return $http.put(url, scheduler);            
        };
                 
        var getDeviceDatasources = function(objId, devId){
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + devId + "/subscrDataSource";
            return $http.get(url);            
        };
                 
        var getDeviceMetaDataVzlet = function(obj, device){                     
            var url = crudTableName + "/" + obj.id + urlDeviceObjects + "/" + device.id + urlDeviceMetaDataVzlet;
            return $http.get(url);
        };
        var getRmaDeviceMetaDataVzlet = function(objId, device){                     
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + device.id + urlDeviceMetaDataVzlet;
            return $http.get(url);
        };
        var putDeviceMetaDataVzlet = function(device){                     
            var url = crudTableName + "/" + device.contObject.id + urlDeviceObjects + "/" + device.id + urlDeviceMetaDataVzlet;
            var result = $http.put(url, device.metaData);        
            return result;
        };
        
                 //get device measures
        var getRmaMetadataMeasureUnit = function(url, measU, dmm){
            var url = url + "?measureUnit=" + dmm.all[measU].keyname;
            $http.get(url)
            .then(function(resp){
                dmm[dmm.all[measU].keyname] = resp.data;
            }, 
                 function(error){
                console.log(error);
            });
        };         
        
        var getRmaDeviceMetadataMeasures = function(){                     
            var url = urlDeviceMetadataMeasures;
            $http.get(url)
                .then(function(resp){                
                deviceMetadataMeasures.all = resp.data;
                for (var measU in deviceMetadataMeasures.all){
                    getRmaMetadataMeasureUnit(urlDeviceMetadataMeasures, measU, deviceMetadataMeasures);
                };
//console.log(deviceMetadataMeasures);                
                $rootScope.$broadcast('objectSvc:deviceMetadataMeasuresLoaded');
            },
                     function(err){
                console.log(err);
            });
        };        
                 
        var getRmaDeviceMetadata = function(objId, devId){
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + devId + urlDeviceMetaDataSuffix;
            return $http.get(url);
        };
                 
        //get time zones
        var getTimezones = function(){
            return getData(urlTimezones);
        };
                 
        var getDeviceMetaDataVzletSystemList = function(){                     
            return $http.get(urlDeviceMetaDataVzletSystemList);
        };
        var vzletSystemList = [];
        var getVzletSystemListFromServer = function(){
            getDeviceMetaDataVzletSystemList()
            .then(
                function(response){
                    vzletSystemList = response.data;
                },
                function(e){
                    console.log(e);
                }
            );
        };         
        
        var getVzletSystemList = function(){
            return vzletSystemList;
        };

        //get objects
        var getObjectsData = function () {           
           return $http.get(crudTableName);//.then(function(res){console.log(res)});
        };
        var getRmaObjectsData = function () {
           return $http.get(getRmaObjectsUrl());
        };

                //get object
        var getObject = function (objId) {
           if (angular.isUndefined(objId) || (objId == null)) {return "Object id is null or undefined"};          
           return $http.get(crudTableName + "/" +objId);
        };
        var getRmaObject = function (objId) {
           if (angular.isUndefined(objId) || (objId == null)) {return "Object id is null or undefined"}; 
           return $http.get(getRmaObjectsUrl() + "/" + objId);
        }; 
                 //Get data for the setting period for the city by objectId
        var getObjectConsumingData = function(settings, objId){
            var url = urlCitiesData + "/" + objId + "/?dateFrom=" + settings.dateFrom + "&dateTo=" + settings.dateTo;
            return $http.get(url);
        };                 
                //get data for the setting period for one city
        var getCityConsumingData = function(cityFias, settings){
            var url = urlCitiesData + "/city/?dateFrom=" + settings.dateFrom + "&dateTo=" + settings.dateTo + "&cityFias=" + cityFias;           
            return $http.get(url);
        };         
                 //get data for the setting period for all cities
        var getCitiesConsumingData = function(settings){
            var url = urlCitiesData + "/?dateFrom=" + settings.dateFrom + "&dateTo=" + settings.dateTo;          
            return $http.get(url);
        };
                 
        var checkUndefinedNull = function(numvalue){
            var result = false;
            if ((angular.isUndefined(numvalue)) || (numvalue == null)){
                result = true;
            }
            return result;
        };
                 
            // sort the object array by the fullname
        function sortObjectsByFullName(array){
            if (angular.isUndefined(array) || (array == null) || !angular.isArray(array)){
                return false;
            };
            array.sort(function(a, b){
                if (checkUndefinedNull(a) || checkUndefinedNull(b) || checkUndefinedNull(a.fullName) || checkUndefinedNull(b.fullName)){         
                    return 0;
                };
                if (a.fullName.toUpperCase() > b.fullName.toUpperCase()){
                    return 1;
                };
                if (a.fullName.toUpperCase() < b.fullName.toUpperCase()){
                    return -1;
                };
                return 0;
            });
        };                 
                    // sort the object array by the fullname, where some objects do not have field "fullName"
        function sortObjectsByFullNameEx(array){
            if (angular.isUndefined(array) || (array == null) || !angular.isArray(array)){
                return false;
            };
            var tmpArr = [];
            var tmpBadArr = [];
            array.forEach(function(elem){
                if (!checkUndefinedNull(elem.fullName)){
                    tmpArr.push(elem);
                }else{
                    tmpBadArr.push(elem);
                };
            });
            if (tmpArr.length == 0) {return "No objects with fullName"};
            sortObjectsByFullName(tmpArr);
            Array.prototype.push.apply(tmpBadArr, tmpArr);
            return tmpBadArr;
        };
                 
        function sortObjectsByConObjectFullName(array){
            if (angular.isUndefined(array) || (array == null) || !angular.isArray(array)){
                return false;
            };           
            array.sort(function(a, b){
                if (checkUndefinedNull(a) || checkUndefinedNull(b) || checkUndefinedNull(a.contObject.fullName) || checkUndefinedNull(b.contObject.fullName)){         
                    return 0;
                };
                if (a.contObject.fullName.toUpperCase() > b.contObject.fullName.toUpperCase()){
                    return 1;
                };
                if (a.contObject.fullName.toUpperCase() < b.contObject.fullName.toUpperCase()){
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
                 
       $rootScope.$on('objectSvc:requestReloadData', function(){
//console.log("Reload objects data.");           
           loadData();
       });
        var getPromise = function(){
            return promise;
        };
        var getRmaPromise = function(){
            return rmaPromise;
        };
                 
        var sendDeviceToServer = function(device){
            //send to server
                //create param string
            var params = {};
            if (angular.isDefined(device.subscrDataSourceAddr) && (device.subscrDataSourceAddr != null)){
                params.subscrDataSourceAddr = device.subscrDataSourceAddr;
            };
            if (angular.isDefined(device.dataSourceTable) && (device.dataSourceTable != null)){
                params.dataSourceTable = device.dataSourceTable;
            };
            if (angular.isDefined(device.dataSourceTable1h) && (device.dataSourceTable1h != null)){
                params.dataSourceTable1h = device.dataSourceTable1h;
            };
            if (angular.isDefined(device.dataSourceTable24h) && (device.dataSourceTable24h != null)){
                params.dataSourceTable24h = device.dataSourceTable24h;
            };
            var targetUrl = getRmaObjectsUrl() + "/" + device.contObjectId + "/deviceObjects";
            if (angular.isDefined(device.id) && (device.id != null)){
                targetUrl = targetUrl + "/" + device.id;
            };
                //add url params
            params.subscrDataSourceId=device.subscrDataSourceId;
            device.editDataSourceInfo = params;
            if (angular.isDefined(device.id) && (device.id != null)){
                return $http.put(targetUrl, device);//.then(successCallback,errorCallback);
            }else{
                return $http.post(targetUrl, device);//.then(successCallback,errorCallback);
            };
            return null;
        };
                 
        //zpoint metadata
        var getZpointMetaSrcProp = function(objId, zpId){
            var url = urlRmaContObjects + '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix + '/srcProp';
            return $http.get(url);
        };                 
        var getZpointMetaDestProp = function(objId, zpId){
            var url = urlRmaContObjects + '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix + '/destDb';
            return $http.get(url);
        };        
        var getZpointMetadata = function(objId, zpId){
            var url = urlRmaContObjects + '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix;
            return $http.get(url);
        };
        var getZpointMetaMeasureUnits = function(objId, zpId){
            var url = urlRmaContObjects + '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix + '/measureUnits';
            return $http.get(url).then(
                function(resp){
                    zpointMetadataMeasures = {};
                    zpointMetadataMeasures.all = resp.data;
                    for (var measU in zpointMetadataMeasures.all){
                        getRmaMetadataMeasureUnit(url, measU, zpointMetadataMeasures);
                    };
                    $rootScope.$broadcast('objectSvc:zpointMetadataMeasuresLoaded');
                }, 
                function(e){
                    console.log(e);
                }
            );
        };
                 
        var saveZpointMetadata = function(objId, zpId, metadata){
            var url = urlRmaContObjects + '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix;
            return $http.put(url, metadata);
        };
                 
        //Objects tree
        var loadTreeTemplates = function(url){
            $http.get(url).then(function(resp){
                rmaTreeTemplates = angular.copy(resp.data);
            }, function(e){
                console.log(e);
            });
        };
                 
        var createTree = function(tree){
            return $http.post(rmaTreesUrl, tree);
        };
        
        var loadTrees = function(){
            return $http.get(rmaTreesUrl);
        };
        
        var loadTree = function(treeId){
            return $http.get(rmaTreesUrl + '/' + treeId);
        };
                 
        var updateTree = function(tree){
            return $http.put(rmaTreesUrl + '/' + tree.id, tree);
        };
                 
        var deleteTree = function(treeId){
            return $http.delete(rmaTreesUrl + '/' + treeId);
        };
                 
        var deleteTreeNode = function(treeId, nodeId){
            return $http.delete(rmaTreesUrl + '/' + treeId + '/node/' + nodeId);
        };
                 
        var loadObjectsByTreeNode = function(treeId, nodeId){            
            return $http.get(rmaTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects');
        };
                 
        var putObjectsToTreeNode = function(treeId, nodeId, objIds){            
            return $http.put(rmaTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects/add', objIds);
        };
        
        var releaseObjectsFromTreeNode = function(treeId, nodeId, objIds){            
            return $http.put(rmaTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects/remove', objIds);
//            return $http({
//                method: "DELETE",
//                url: rmaTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects',
//                data: objIds
//            });
        };
                 
        var loadFreeObjectsByTree = function(treeId){            
            return $http.get(rmaTreesUrl + '/' + treeId + '/contObjects/free');
        };
        
        //service initialization
        var initSvc = function(){
            getVzletSystemListFromServer();
            getRmaDeviceMetadataMeasures();
            loadTreeTemplates(rmaTreeTemplatesUrl);
            loadData();
        };

        initSvc();
                    
        return {
            createTree,
            deleteTree,
            deleteTreeNode,
            getAllDevices,
            getCityConsumingData,
            getCitiesConsumingData,
            getCmOrganizations,
            getCurrentObject,
            getDatasourcesUrl,
            getDeviceModels,
            getDeviceSchedulerSettings,
            getObject,
            getObjectConsumingData,
            getObjectSettings,
            getDevicesByObject,
            getRmaDeviceMetadata,
            getDeviceDatasources,
            getDeviceMetadataMeasures,
            getDeviceMetaDataVzlet,
            getDeviceMetaDataVzletSystemList,
            getLoadingStatus,
            getObjectsUrl,
            getPromise,
            getRmaDeviceMetaDataVzlet,
            getRmaMetadataMeasureUnit,
            getRmaObject,
            getRmaObjectsData,
            getRmaObjectsUrl,
            getRmaPromise,
            getRefRangeByObjectAndZpoint,
            getRsoOrganizations,
            getServiceTypes,
            getSubscrUrl,
            getTimezones,
            getRmaTreeTemplates,
            getVzletSystemList,            
            getZpointMetaDestProp,
            getZpointMetadataMeasures,
            getZpointMetaMeasureUnits,
            getZpointMetaSrcProp,
            getZpointMetadata,
            getZpointsDataByObject,
            findObjectById,
            loadFreeObjectsByTree,
            loadObjectsByTreeNode,
            loading,
            loadTree,            
            loadTrees,
            loadTreeTemplates,
            promise,
            putDeviceMetaDataVzlet,
            putDeviceSchedulerSettings,
            putObjectsToTreeNode,
            releaseObjectsFromTreeNode,
            rmaPromise,
            saveZpointMetadata,
            sendDeviceToServer,
            setObjectSettings,
            setCurrentObject,
            sortObjectsByFullName,
            sortObjectsByFullNameEx,            
            sortObjectsByConObjectFullName,
            updateTree
        };
    
}]);