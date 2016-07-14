'use strict';
angular.module('portalNMC')
.service('objectSvc', ['$http', '$cookies', '$interval', '$rootScope', '$q',
             function($http, $cookies, $interval, $rootScope, $q){
//console.log("Object Service. Run."); 
                 
        var svcObjects = [
            {fullName: "Ошибка. Объекты не были загружены."
            }
        ];
        var SUBSCR_OBJECT_TREE_CONT_OBJECTS = "SUBSCR_OBJECT_TREE_CONT_OBJECTS";
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
        var rmaTreeTemplatesUrl = urlSubscr + '/subscrObjectTreeTemplates';
        var controlTreesUrl = urlSubscr + '/subscrObjectTree/contObjectTreeType1';
        var subscrTreesUrl = urlSubscr + '/subscrObjectTree/contObjectTreeType1';
                 
        var defaultTreeUrl = urlSubscr + '/subscrPrefValue?subscrPrefKeyname=' + SUBSCR_OBJECT_TREE_CONT_OBJECTS;
        
        var rmaTreeTemplates = [];

        var deviceMetadataMeasures = {};
        var zpointMetadataMeasures = {};
                 
        var currentObject = null; //the current selected object at interface

        var objectSvcSettings = {};
        
        //request canceling params
        var requestCanceler = null;
        var httpOptions = null;
        
        function isCancelParamsIncorrect(){
            return checkUndefinedNull(requestCanceler) || checkUndefinedNull(httpOptions);
        }
        function getRequestCanceler () {
            return requestCanceler;
        }
        //////////////////////////////         
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
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
                 
        var getCmOrganizations = function(){
            var url = urlCmOrganizations;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
                 
        var getRsoOrganizationsWithId = function(orgId){
            if (checkUndefinedNull(orgId))
                return getRsoOrganizations();
            var url = urlRsoOrganizations + "?organizationId=" + orgId;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
                 
        var getCmOrganizationsWithId = function(orgId){
            if (checkUndefinedNull(orgId))
                return getCmOrganizations();
            var url = urlCmOrganizations + "?organizationId=" + orgId;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
                 
        var getServiceTypes = function(){
            var url = urlServiceTypes;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
                 
        //universal function
        var getData = function(url){
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
                //Функция для получения эталонного интервала для конкретной точки учета конкретного объекта
        var getRefRangeByObjectAndZpoint = function(object, zpoint){
            var url = urlRefRange + object.id + '/zpoints/' + zpoint.id + '/referencePeriod';                  
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
                 
        var zPointsByObject = [];
        var getZpointsDataByObject = function(obj, mode){ 
            obj.zpoints = [];
            var table = crudTableName + "/" + obj.id + "/contZPoints" + mode;//Ex";
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(table, httpOptions);
        };         
        
        var getDevicesByObject = function(obj){
            var url = crudTableName + "/" + obj.id + urlDeviceObjects;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
        var getAllDevices = function(){
            var url = urlRmaContObjects + urlDeviceObjects;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
        var getDeviceModels = function(){
            var url = urlDeviceModels;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
                 
        var getRmaObjectSubscribers = function(objId){
            var url = urlRmaContObjects + "/" + objId + "/subscribers";
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
                 
        var getDeviceSchedulerSettings = function(objId, devId){
//            /contObjects/%d/deviceObjects/%d/loadingSettings
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + devId + "/subscrDataSource/loadingSettings";
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);            
        };
                 
        var putDeviceSchedulerSettings = function(objId, devId, scheduler){
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + devId + "/subscrDataSource/loadingSettings";
            return $http.put(url, scheduler);            
        };
                 
        var getDeviceDatasources = function(objId, devId){
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + devId + "/subscrDataSource";
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);            
        };
                 
        var getDeviceMetaDataVzlet = function(obj, device){                     
            var url = crudTableName + "/" + obj.id + urlDeviceObjects + "/" + device.id + urlDeviceMetaDataVzlet;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
        var getRmaDeviceMetaDataVzlet = function(objId, device){                     
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + device.id + urlDeviceMetaDataVzlet;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
        var putDeviceMetaDataVzlet = function(device){                     
            var url = crudTableName + "/" + device.contObject.id + urlDeviceObjects + "/" + device.id + urlDeviceMetaDataVzlet;
            var result = $http.put(url, device.metaData);        
            return result;
        };
        
                 //get device measures
        var getRmaMetadataMeasureUnit = function(url, measU, dmm){
//console.log(url);            
//console.log(measU);
//console.log(dmm);            
            var url = url + "?measureUnit=" + dmm.all[measU].keyname;
            if (isCancelParamsIncorrect() == true)
                return null;
            $http.get(url, httpOptions)
            .then(function(resp){
                dmm[dmm.all[measU].keyname] = resp.data;
            }, 
                 function(error){
                console.log(error);
            });
        };         
        
        var getRmaDeviceMetadataMeasures = function(){            
            var url = urlDeviceMetadataMeasures;
            if (isCancelParamsIncorrect() == true)
                return null;
            $http.get(url, httpOptions)
                .then(function(resp){                
                deviceMetadataMeasures.all = resp.data;                              
//                for (var measU in deviceMetadataMeasures.all){
//                    getRmaMetadataMeasureUnit(urlDeviceMetadataMeasures, measU, deviceMetadataMeasures);
//                };
                deviceMetadataMeasures.all.forEach(function(elem, ind){
                    getRmaMetadataMeasureUnit(urlDeviceMetadataMeasures, ind, deviceMetadataMeasures);
                });
//console.log(deviceMetadataMeasures);                
                $rootScope.$broadcast('objectSvc:deviceMetadataMeasuresLoaded');
            },
                     function(err){
                console.log(err);
            });
        };        
                 
        var getRmaDeviceMetadata = function(objId, devId){
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + devId + urlDeviceMetaDataSuffix;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
                 
        //get time zones
        var getTimezones = function(){
            return getData(urlTimezones);
        };
                 
        var getDeviceMetaDataVzletSystemList = function(){                     
//            return $http.get(urlDeviceMetaDataVzletSystemList);
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(urlDeviceMetaDataVzletSystemList, httpOptions);
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
        var getObjectsData = function(contGroupId){                    
            var url = crudTableName;
            if (!checkUndefinedNull(contGroupId)){
                url += "?contGroupId=" + contGroupId;
            };
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
        var getRmaObjectsData = function (contGroupId) {
            var url = getRmaObjectsUrl();
            if (!checkUndefinedNull(contGroupId)){
                url += "?contGroupId=" + contGroupId;
            };
            if (isCancelParamsIncorrect() == true)
                return null;            
            return $http.get(url, httpOptions);
        };

                //get object
        var getObject = function (objId) {
            if (angular.isUndefined(objId) || (objId == null)) {return "Object id is null or undefined"};
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(crudTableName + "/" +objId, httpOptions);
        };
        var getRmaObject = function (objId) {
            if (angular.isUndefined(objId) || (objId == null)) {return "Object id is null or undefined"};
            if (isCancelParamsIncorrect() == true)
                return null;            
            return $http.get(getRmaObjectsUrl() + "/" + objId, httpOptions);
        }; 
                 //Get data for the setting period for the city by objectId
        var getObjectConsumingData = function(settings, objId){
            var url = urlCitiesData + "/" + objId + "/?dateFrom=" + settings.dateFrom + "&dateTo=" + settings.dateTo;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };                 
                //get data for the setting period for one city
        var getCityConsumingData = function(cityFias, settings){
            var url = urlCitiesData + "/city/?dateFrom=" + settings.dateFrom + "&dateTo=" + settings.dateTo + "&cityFias=" + cityFias;   if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };         
                 //get data for the setting period for all cities
        var getCitiesConsumingData = function(settings){
            var url = urlCitiesData + "/?dateFrom=" + settings.dateFrom + "&dateTo=" + settings.dateTo;          
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
                 
        var checkUndefinedNull = function(numvalue){
            var result = false;
            if ((angular.isUndefined(numvalue)) || (numvalue == null)){
                result = true;
            }
            return result;
        };
        
            // Sort object array by some string field
        var sortItemsBy = function(itemArray, sortField){
            if (!angular.isArray(itemArray)){
                return "Input value is no array.";
            };
            if (checkUndefinedNull(sortField)){
                return "Field for sort is undefined or null.";
            };
            itemArray.sort(function(firstItem, secondItem){
                if (checkUndefinedNull(firstItem[sortField]) && checkUndefinedNull(secondItem[sortField])){
                    return 0;
                };
                if (checkUndefinedNull(firstItem[sortField])){
                    return -1;
                };
                if (checkUndefinedNull(secondItem[sortField])){
                    return 1;
                };
                if (firstItem[sortField].toUpperCase() > secondItem[sortField].toUpperCase()){
                    return 1;
                };
                if (firstItem[sortField].toUpperCase() < secondItem[sortField].toUpperCase()){
                    return -1;
                };
                return 0;
            });
        };
                 
            // sort the object array by the fullname
        function sortObjectsByFullName(array){
            sortItemsBy(array, "fullName");
//            if (angular.isUndefined(array) || (array == null) || !angular.isArray(array)){
//                return false;
//            };
//            array.sort(function(a, b){
//                if (checkUndefinedNull(a) || checkUndefinedNull(b) || checkUndefinedNull(a.fullName) || checkUndefinedNull(b.fullName)){         
//                    return 0;
//                };
//                if (a.fullName.toUpperCase() > b.fullName.toUpperCase()){
//                    return 1;
//                };
//                if (a.fullName.toUpperCase() < b.fullName.toUpperCase()){
//                    return -1;
//                };
//                return 0;
//            });
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
                if ((checkUndefinedNull(a.contObject) || checkUndefinedNull(a.contObject.fullName)) && 
                    (checkUndefinedNull(b.contObject) || checkUndefinedNull(b.contObject.fullName))){         
                    return 0;
                };
                if (checkUndefinedNull(a.contObject) || checkUndefinedNull(a.contObject.fullName)){         
                    return -1;
                };
                if (checkUndefinedNull(b.contObject) || checkUndefinedNull(b.contObject.fullName)){         
                    return 1;
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
       var loadData = function(contGroupId){
         promise = getObjectsData(contGroupId);
         rmaPromise = getRmaObjectsData(contGroupId);
       };
                 
       $rootScope.$on('objectSvc:requestReloadData', function(event, args){
//console.log("Reload objects data.");
            var contGroupId = null;
            if (!checkUndefinedNull(args) && !checkUndefinedNull(args.contGroupId)){
                contGroupId = args.contGroupId;
            }
            loadData(contGroupId);
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
//*******************************************************************************************************         
//zpoint metadata
//********************************************************************************************************
        var getZpointMetaSrcProp = function(objId, zpId){
            var url = urlRmaContObjects + '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix + '/srcProp';
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };                 
        var getZpointMetaDestProp = function(objId, zpId){
            var url = urlRmaContObjects + '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix + '/destDb';
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };        
        var getZpointMetadata = function(objId, zpId){
            var url = urlRmaContObjects + '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix;
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions);
        };
        var getZpointMetaMeasureUnits = function(objId, zpId){
//console.log("getZpointMetaMeasureUnits");            
            var url = urlRmaContObjects + '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix + '/measureUnits';
            if (isCancelParamsIncorrect() == true)
                return null;
            return $http.get(url, httpOptions)
            .then(
                function(resp){
                    zpointMetadataMeasures = {};
                    zpointMetadataMeasures.all = resp.data;
//                    for (var measU in zpointMetadataMeasures.all){
//                        getRmaMetadataMeasureUnit(url, measU, zpointMetadataMeasures);
//                    };
                    zpointMetadataMeasures.all.forEach(function(elem, ind){
                        getRmaMetadataMeasureUnit(url, ind, zpointMetadataMeasures);
                    });
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
//****************************************************************************************         
//Objects tree
//****************************************************************************************
        var loadTreeTemplates = function(url){
            $http.get(url).then(function(resp){
                rmaTreeTemplates = angular.copy(resp.data);
            }, function(e){
                console.log(e);
            });
        };
                 
        var loadTreeTemplateItems = function(templateId){
            return $http.get(rmaTreeTemplatesUrl + '/' +templateId + '/items');
        };
                 
        var createTree = function(tree){
            return $http.post(controlTreesUrl, tree);
        };
        
        var loadTrees = function(){
            return $http.get(controlTreesUrl);
        };
        
        var loadTree = function(treeId){
            return $http.get(controlTreesUrl + '/' + treeId);
        };
                 
        var updateTree = function(tree){
            return $http.put(controlTreesUrl + '/' + tree.id, tree);
        };
                 
        var deleteTree = function(treeId){
            return $http.delete(controlTreesUrl + '/' + treeId);
        };
                 
        var deleteTreeNode = function(treeId, nodeId){
            return $http.delete(controlTreesUrl + '/' + treeId + '/node/' + nodeId);
        };
                 
        var loadObjectsByTreeNode = function(treeId, nodeId){            
            return $http.get(controlTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects');
        };
                 
        var putObjectsToTreeNode = function(treeId, nodeId, objIds){            
            return $http.put(controlTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects/add', objIds);
        };
        
        var releaseObjectsFromTreeNode = function(treeId, nodeId, objIds){            
            return $http.put(controlTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects/remove', objIds);
        };
                 
        var loadFreeObjectsByTree = function(treeId){            
            return $http.get(controlTreesUrl + '/' + treeId + '/contObjects/free');
        };
                 
        var loadSubscrTrees = function(){
            return $http.get(subscrTreesUrl);
        };
        
        var loadSubscrTree = function(treeId){
            return $http.get(subscrTreesUrl + '/' + treeId);
        };
        
        var loadSubscrFreeObjectsByTree = function(treeId){            
            return $http.get(subscrTreesUrl + '/' + treeId + '/contObjects/free');
        };
                 
        var loadSubscrObjectsByTreeNode = function(treeId, nodeId){            
            return $http.get(subscrTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects');
        };
                 
        var loadDefaultTreeSetting = function(){
            return $http.get(defaultTreeUrl);
        };
        
        //service initialization
        var initSvc = function(){
            
            requestCanceler = $q.defer();
            httpOptions = {
                timeout: requestCanceler.promise
            }
            
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
            getCmOrganizationsWithId,
            getCurrentObject,
            getDatasourcesUrl,
            getDeviceModels,
            getDeviceSchedulerSettings,
            getObject,
            getObjectConsumingData,
            getObjectSettings,
            getDevicesByObject,
            getRequestCanceler,
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
            getRmaObjectSubscribers,
            getRmaObjectsUrl,
            getRmaPromise,
            getRefRangeByObjectAndZpoint,
            getRsoOrganizations,
            getRsoOrganizationsWithId,
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
            loadDefaultTreeSetting,
            loadFreeObjectsByTree,
            loadObjectsByTreeNode,
            loading,
            loadTree,            
            loadTrees,
            loadTreeTemplateItems,
            loadTreeTemplates,
            loadSubscrFreeObjectsByTree,
            loadSubscrObjectsByTreeNode,
            loadSubscrTree,
            loadSubscrTrees,
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