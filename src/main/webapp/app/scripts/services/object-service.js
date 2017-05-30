/*jslint node: true, nomen: true, eqeq: true, es5: true*/
/*global angular*/
'use strict';
var app = angular.module('portalNMC');
app.service('objectSvc', ['$http', '$cookies', '$interval', '$rootScope', '$q',
             function ($http, $cookies, $interval, $rootScope, $q) {
//console.log("Object Service. Run."); 
                 
        var svcObjects = [
            {
                fullName: "Ошибка. Объекты не были загружены."
            }
        ];
        var SUBSCR_OBJECT_TREE_CONT_OBJECTS = "SUBSCR_OBJECT_TREE_CONT_OBJECTS",
            OBJECT_PER_SCROLL = 42,
            RECENT_HEATER_TYPE_ARR_LENGTH = 5; //length of recentHeaterTypes
                 
        var BROADCASTS = {};
        BROADCASTS.BUILDING_TYPES_LOADED = "objectSvc:buildingTypesLoaded";
        BROADCASTS.BUILDING_CATEGORIES_LOADED = "objectSvc:buildingCategoriesLoaded";
        var loading = true,
            urlApi = '../api',
            urlSubscr = urlApi + '/subscr',
            urlRma = urlApi + '/rma',
            urlDatasources = urlRma + '/dataSources',
            urlSubscrContObjects = urlSubscr + '/contObjects',
            urlRmaContObjects = urlRma + '/contObjects',
            urlRefRange = urlSubscr + '/contObjects/',
            urlDeviceObjects = '/deviceObjects',
            urlDeviceModels = urlRma + urlDeviceObjects + '/deviceModels',
            urlDeviceModelTypes = urlRma + urlDeviceObjects + '/deviceModelTypes',
            urlImpulseCounterTypes = urlRma + urlDeviceObjects + '/impulseCounterTypes',
            urlHeaterTypes = urlRma + urlDeviceObjects + '/heatRadiatorTypes',
            urlDeviceMetaDataVzlet = '/metaVzlet',
            urlDeviceMetaDataSuffix = '/metadata',
            urlZpointMetaDataSuffix = '/metadata',
            urlDeviceMetaDataVzletSystemList = urlSubscr + '/deviceObjects/metaVzlet/system',//urlDeviceObjects+urlDeviceMetaDataVzlet+'/system';
            urlCitiesData = urlSubscr + '/service/hwater/contObjects/serviceTypeInfo',
            urlTimezones = urlApi + '/timezoneDef/all',
            urlRsoOrganizations = urlRmaContObjects + '/rsoOrganizations',
            urlCmOrganizations = urlRmaContObjects + '/cmOrganizations',
            urlServiceTypes = urlRmaContObjects + '/contServiceTypes',//'resource/serviceTypes.json';
//                 /contObjects/deviceObjects/metadata/measureUnits
            urlDeviceMetadataMeasures = urlRmaContObjects + urlDeviceObjects + "/metadata/measureUnits",
            rmaTreeTemplatesUrl = urlSubscr + '/subscrObjectTreeTemplates',
            controlTreesUrl = urlSubscr + '/subscrObjectTree/contObjectTreeType1',
            subscrTreesUrl = urlSubscr + '/subscrObjectTree/contObjectTreeType1',
            urlBuildingTypes = urlSubscr + '/service/buildingType/list',
            urlBuildingCategories = urlSubscr + '/service/buildingType/category/list',
        //meter periods urls
            meterPeriodSuffix = '/meterPeriodSettings',
            urlSubscrMeterPeriod = urlSubscrContObjects + meterPeriodSuffix;
                 
        var defaultTreeUrl = urlSubscr + '/subscrPrefValue?subscrPrefKeyname=' + SUBSCR_OBJECT_TREE_CONT_OBJECTS;
        
        var rmaTreeTemplates = [];

        var deviceMetadataMeasures = {};
        var zpointMetadataMeasures = {};
                 
        var currentObject = null; //the current selected object at interface

        var objectSvcSettings = {};
        var buildingTypes = [],
            buildingCategories = [],
            recentHeaterTypes = [];
        
        //request canceling params
        var requestCanceler = null;
        var httpOptions = null;
                 
        var checkUndefinedNull = function (numvalue) {
            var result = false;
            if ((angular.isUndefined(numvalue)) || (numvalue === null)) {
                result = true;
            }
            return result;
        };
        
        function isCancelParamsIncorrect() {
            return checkUndefinedNull(requestCanceler) || checkUndefinedNull(httpOptions);
        }
        function getRequestCanceler() {
            return requestCanceler;
        }
        //////////////////////////////  

                //check user: system? - true/false
        var isSystemuser = function () {
            var result = false;
            var userInfo = $rootScope.userInfo;
            if (angular.isDefined(userInfo)) {
                result = userInfo._system;
            }
            return result;
        };
                 
        var getCurrentObject = function () {
            return currentObject;
        };
        var setCurrentObject = function (obj) {
            currentObject = obj;
        };
                 
        var getObjectSettings = function () {
            return objectSvcSettings;
        };
                 
        var getDeviceMetadataMeasures = function () {
            return deviceMetadataMeasures;
        };
                 
        var getZpointMetadataMeasures = function () {
            return zpointMetadataMeasures;
        };
                 
        var getRmaTreeTemplates = function () {
            return rmaTreeTemplates;
        };
        
        var setObjectSettings = function (objectSettings) {
            var key;
            for (key in objectSettings) {
                objectSvcSettings[key] = objectSettings[key];
            }
        };
        
        var getObjectsUrl = function () {
            return urlSubscrContObjects;
        };
                 
        var getRmaObjectsUrl = function () {
            return urlRmaContObjects;
        };
        var getSubscrUrl = function () {
            return urlSubscr;
        };
        var getRmaUrl = function () {
            return urlRma;
        };
        
        var getDatasourcesUrl = function () {
            return urlDatasources;
        };
        
        var getLoadingStatus = function () {
            return loading;
        };
                                  
        function getBuildingCategories() {
            return buildingCategories;
        }
        function getBuildingTypes() {
            return buildingTypes;
        }
        
        var getRsoOrganizations = function () {
            var url = urlRsoOrganizations;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
        var getCmOrganizations = function () {
            var url = urlCmOrganizations;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
        var getRsoOrganizationsWithId = function (orgId) {
            if (checkUndefinedNull(orgId)) {
                return getRsoOrganizations();
            }
            var url = urlRsoOrganizations + "?organizationId=" + orgId;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
        var getCmOrganizationsWithId = function (orgId) {
            if (checkUndefinedNull(orgId)) {
                return getCmOrganizations();
            }
            var url = urlCmOrganizations + "?organizationId=" + orgId;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
        var getServiceTypes = function () {
            var url = urlServiceTypes;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
        //universal function
        var getData = function (url) {
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                //Функция для получения эталонного интервала для конкретной точки учета конкретного объекта
        var getRefRangeByObjectAndZpoint = function (object, zpoint) {
            var url = urlRefRange + object.id + '/zpoints/' + zpoint.id + '/referencePeriod';
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
        var zPointsByObject = [];
        var getZpointsDataByObject = function (obj, mode) {
            obj.zpoints = [];
            var table = urlSubscrContObjects + "/" + obj.id + "/contZPoints" + mode;//Ex";
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(table, httpOptions);
        };
                 
        function loadDeviceById(objId, devId) {
            var url = urlSubscrContObjects + "/" + objId + urlDeviceObjects + "/" + devId;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        }
        
        var getDevicesByObject = function (obj) {
            var url = urlSubscrContObjects + "/" + obj.id + urlDeviceObjects;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
        var getAllDevices = function () {
            var url = urlRmaContObjects + urlDeviceObjects;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
        var getDeviceModels = function () {
            var url = urlDeviceModels;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
//        var getDeviceModelTypes = function(){
//            var url = urlDeviceModelTypes;
//            if (isCancelParamsIncorrect() === true){
//                return null;
//            }
//            return $http.get(url, httpOptions);
//        };
                 
        function getImpulseCounterTypes() {
            var url = urlImpulseCounterTypes;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        }
                 
        function getHeaterTypes() {
            var url = urlHeaterTypes;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        }
                 
        var getRmaObjectSubscribers = function (objId) {
            var url = urlRmaContObjects + "/" + objId + "/subscribers";
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
        var getDeviceSchedulerSettings = function (objId, devId) {
//            /contObjects/%d/deviceObjects/%d/loadingSettings
            var url = urlSubscrContObjects + "/" + objId + urlDeviceObjects + "/" + devId + "/subscrDataSource/loadingSettings";
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
        var putDeviceSchedulerSettings = function (objId, devId, scheduler) {
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + devId + "/subscrDataSource/loadingSettings";
            return $http.put(url, scheduler);
        };
                 
        var getDeviceDatasources = function (objId, devId) {
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + devId + "/subscrDataSource";
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
        var getDeviceMetaDataVzlet = function (obj, device) {
            var url = urlSubscrContObjects + "/" + obj.id + urlDeviceObjects + "/" + device.id + urlDeviceMetaDataVzlet;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
        var getRmaDeviceMetaDataVzlet = function (objId, device) {
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + device.id + urlDeviceMetaDataVzlet;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
        var putDeviceMetaDataVzlet = function (device) {
            var url = urlSubscrContObjects + "/" + device.contObject.id + urlDeviceObjects + "/" + device.id + urlDeviceMetaDataVzlet;
            var result = $http.put(url, device.metaData);
            return result;
        };
        
                 //get device measures
        var getRmaMetadataMeasureUnit = function (murl, measU, dmm) {
//console.log(url);            
//console.log(measU);
//console.log(dmm);            
            var url = murl + "?measureUnit=" + dmm.all[measU].keyname;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            $http.get(url, httpOptions)
                .then(function (resp) {
                    dmm[dmm.all[measU].keyname] = resp.data;
                },
                     function (error) {
                        console.log(error);
                    });
        };
        
        var getRmaDeviceMetadataMeasures = function () {
            var url = urlDeviceMetadataMeasures;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            $http.get(url, httpOptions)
                .then(function (resp) {
                    deviceMetadataMeasures.all = resp.data;
//                for (var measU in deviceMetadataMeasures.all){
//                    getRmaMetadataMeasureUnit(urlDeviceMetadataMeasures, measU, deviceMetadataMeasures);
//                };
                    deviceMetadataMeasures.all.forEach(function (elem, ind) {
                        getRmaMetadataMeasureUnit(urlDeviceMetadataMeasures, ind, deviceMetadataMeasures);
                    });
//console.log(deviceMetadataMeasures);                
                    $rootScope.$broadcast('objectSvc:deviceMetadataMeasuresLoaded');
                },
                    function (err) {
                        console.log(err);
                    });
        };
                 
        var getRmaDeviceMetadata = function (objId, devId) {
            var url = urlRmaContObjects + "/" + objId + urlDeviceObjects + "/" + devId + urlDeviceMetaDataSuffix;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 
        //get time zones
        var getTimezones = function () {
            return getData(urlTimezones);
        };
                 
        var getDeviceMetaDataVzletSystemList = function () {
//            return $http.get(urlDeviceMetaDataVzletSystemList);
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(urlDeviceMetaDataVzletSystemList, httpOptions);
        };
        var vzletSystemList = [];
        var getVzletSystemListFromServer = function () {
            getDeviceMetaDataVzletSystemList()
                .then(
                    function (response) {
                        vzletSystemList = response.data;
                    },
                    function (e) {
                        console.log(e);
                    }
                );
        };
        
        var getVzletSystemList = function () {
            return vzletSystemList;
        };

        //get objects
        var getObjectsData = function (contGroupId) {
            var url = urlSubscrContObjects;
            if (!checkUndefinedNull(contGroupId)) {
                url += "?contGroupId=" + contGroupId;
            }
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
        var getRmaObjectsData = function (contGroupId) {
            var url = getRmaObjectsUrl();
            if (!checkUndefinedNull(contGroupId)) {
                url += "?contGroupId=" + contGroupId;
            }
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };

                //get object
        var getObject = function (objId) {
            if (angular.isUndefined(objId) || (objId === null)) { return "Object id is null or undefined"; }
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(urlSubscrContObjects + "/" + objId, httpOptions);
        };
        var getRmaObject = function (objId) {
            if (angular.isUndefined(objId) || (objId === null)) { return "Object id is null or undefined"; }
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(getRmaObjectsUrl() + "/" + objId, httpOptions);
        };
                 //Get data for the setting period for the city by objectId
        var getObjectConsumingData = function (settings, objId) {
            var url = urlCitiesData + "/" + objId + "/?dateFrom=" + settings.dateFrom + "&dateTo=" + settings.dateTo;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                //get data for the setting period for one city
        var getCityConsumingData = function (cityFias, settings) {
            var url = urlCitiesData + "/city/?dateFrom=" + settings.dateFrom + "&dateTo=" + settings.dateTo + "&cityFias=" + cityFias;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
                 //get data for the setting period for all cities
        var getCitiesConsumingData = function (settings) {
            var url = urlCitiesData + "/?dateFrom=" + settings.dateFrom + "&dateTo=" + settings.dateTo;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
        
            // Sort object array by some string field
        var sortItemsBy = function (itemArray, sortField) {
            if (!angular.isArray(itemArray)) {
                return "Input value is no array.";
            }
            if (checkUndefinedNull(sortField)) {
                return "Field for sort is undefined or null.";
            }
            itemArray.sort(function (firstItem, secondItem) {
                if (checkUndefinedNull(firstItem[sortField]) && checkUndefinedNull(secondItem[sortField])) {
                    return 0;
                }
                if (checkUndefinedNull(firstItem[sortField])) {
                    return -1;
                }
                if (checkUndefinedNull(secondItem[sortField])) {
                    return 1;
                }
                if (firstItem[sortField].toUpperCase() > secondItem[sortField].toUpperCase()) {
                    return 1;
                }
                if (firstItem[sortField].toUpperCase() < secondItem[sortField].toUpperCase()) {
                    return -1;
                }
                return 0;
            });
        };
                 
            // sort the object array by the fullname
        function sortObjectsByFullName(array) {
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
        }
                    // sort the object array by the fullname, where some objects do not have field "fullName"
        function sortObjectsByFullNameEx(array) {
            if (angular.isUndefined(array) || (array === null) || !angular.isArray(array)) {
                return false;
            }
            var tmpArr = [];
            var tmpBadArr = [];
            array.forEach(function (elem) {
                if (!checkUndefinedNull(elem.fullName)) {
                    tmpArr.push(elem);
                } else {
                    tmpBadArr.push(elem);
                }
            });
            if (tmpArr.length === 0) { return "No objects with fullName"; }
            sortObjectsByFullName(tmpArr);
            Array.prototype.push.apply(tmpBadArr, tmpArr);
            return tmpBadArr;
        }
                 
        function sortObjectsByConObjectFullName(array) {
            if (angular.isUndefined(array) || (array === null) || !angular.isArray(array)) {
                return false;
            }
            array.sort(function (a, b) {
                if ((checkUndefinedNull(a.contObject) || checkUndefinedNull(a.contObject.fullName)) &&
                        (checkUndefinedNull(b.contObject) || checkUndefinedNull(b.contObject.fullName))) {
                    return 0;
                }
                if (checkUndefinedNull(a.contObject) || checkUndefinedNull(a.contObject.fullName)) {
                    return -1;
                }
                if (checkUndefinedNull(b.contObject) || checkUndefinedNull(b.contObject.fullName)) {
                    return 1;
                }
                if (a.contObject.fullName.toUpperCase() > b.contObject.fullName.toUpperCase()) {
                    return 1;
                }
                if (a.contObject.fullName.toUpperCase() < b.contObject.fullName.toUpperCase()) {
                    return -1;
                }
                return 0;
            });
        }
                 
//        function sortZpointsInObject (object) {
//            var zps = object.zpoints;
//            zpointOrder = "" + zpoint.contServiceType.serviceOrder + zpoint.customServiceName; 
//        }
                 
        function findObjectById(objId, objectArr) {
            var obj = null;
            if (!angular.isArray(objectArr) || !angular.isNumber(objId)) {
//                console.log("findObjectById: Incorrect input params.");
                return obj;
            }
            objectArr.some(function (element) {
                if (element.id === objId) {
                    obj = element;
                    return true;
                }
            });
            return obj;
        }

        var promise = null,//getObjectsData();
            rmaPromise = null;//getRmaObjectsData();
        var loadData = function (contGroupId, onlySubscrList) {
            console.time("Object loading");
            promise = getObjectsData(contGroupId);
            if (!checkUndefinedNull(onlySubscrList) && onlySubscrList === true) {
                return;
            }
            rmaPromise = getRmaObjectsData(contGroupId);
        };
                 
        $rootScope.$on('objectSvc:requestReloadData', function (event, args) {
//console.log("Reload objects data.");
            var contGroupId = null,
                onlySubscrList = false;
            if (!checkUndefinedNull(args) && !checkUndefinedNull(args.contGroupId)) {
                contGroupId = args.contGroupId;
            }
            if (!checkUndefinedNull(args) && !checkUndefinedNull(args.onlySubscrList)) {
                onlySubscrList = Boolean(args.onlySubscrList);
            }
            loadData(contGroupId, onlySubscrList);
        });
        var getPromise = function () {
            return promise;
        };
        var getRmaPromise = function () {
            return rmaPromise;
        };
                 
        var subscrSendDeviceToServer = function (device) {
            //send to server
                //create param string
            var params = {};
            if (angular.isDefined(device.subscrDataSourceAddr) && (device.subscrDataSourceAddr !== null)) {
//                params.subscrDataSourceAddr = device.subscrDataSourceAddr;
                device.editDataSourceInfo.subscrDataSourceAddr = device.subscrDataSourceAddr;
            }
//            if (angular.isDefined(device.dataSourceTable) && (device.dataSourceTable !== null)) {
//                params.dataSourceTable = device.dataSourceTable;
//            }
//            if (angular.isDefined(device.dataSourceTable1h) && (device.dataSourceTable1h !== null)) {
//                params.dataSourceTable1h = device.dataSourceTable1h;
//            }
//            if (angular.isDefined(device.dataSourceTable24h) && (device.dataSourceTable24h !== null)) {
//                params.dataSourceTable24h = device.dataSourceTable24h;
//            }
            var targetUrl = getObjectsUrl() + "/" + device.contObjectId + "/deviceObjects";
            if (angular.isDefined(device.id) && (device.id !== null)) {
                targetUrl = targetUrl + "/" + device.id;
            }
                //add url params
//            params.subscrDataSourceId = device.subscrDataSourceId;
//            device.editDataSourceInfo = params;
            
            if (angular.isDefined(device.id) && (device.id !== null)) {
                return $http.put(targetUrl, device);//.then(successCallback,errorCallback);
            }
//            else {
//                return $http.post(targetUrl, device);//.then(successCallback,errorCallback);
//            }
//            return null;
        };
                 
        var sendDeviceToServer = function (device) {
            //send to server
                //create param string
            var params = {};
//            if (angular.isDefined(device.editDataSourceInfo) && device.editDataSourceInfo !== null) {
//                params = device.editDataSourceInfo;
//            }
            if (angular.isDefined(device.subscrDataSourceAddr) && (device.subscrDataSourceAddr !== null)) {
                params.subscrDataSourceAddr = device.subscrDataSourceAddr;
            }
            if (angular.isDefined(device.dataSourceTable) && (device.dataSourceTable !== null)) {
                params.dataSourceTable = device.dataSourceTable;
            }
            if (angular.isDefined(device.dataSourceTable1h) && (device.dataSourceTable1h !== null)) {
                params.dataSourceTable1h = device.dataSourceTable1h;
            }
            if (angular.isDefined(device.dataSourceTable24h) && (device.dataSourceTable24h !== null)) {
                params.dataSourceTable24h = device.dataSourceTable24h;
            }
            var targetUrl = getRmaObjectsUrl() + "/" + device.contObjectId + "/deviceObjects";
            if (angular.isDefined(device.id) && (device.id !== null)) {
                targetUrl = targetUrl + "/" + device.id;
            }
                //add url params
//            params.id = device.subscrDataSourceId;
            params.subscrDataSourceId = device.subscrDataSourceId;
            device.editDataSourceInfo = params;
            if (angular.isDefined(device.id) && (device.id !== null)) {
                return $http.put(targetUrl, device);//.then(successCallback,errorCallback);
            } else {
                return $http.post(targetUrl, device);//.then(successCallback,errorCallback);
            }
//            return null;
        };
//*******************************************************************************************************         
//zpoint metadata
//********************************************************************************************************
        var getZpointMetaSrcProp = function (objId, zpId) {
            var url = urlSubscrContObjects;
//            if (isSystemuser()){
//                url = urlRmaContObjects;
//            }
            url += '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix + '/srcProp';
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
        var getZpointMetaDestProp = function (objId, zpId) {
            var url = urlSubscrContObjects;
//            if (isSystemuser()){
//                url = urlRmaContObjects;
//            }
            url += '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix + '/destDb';
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
        var getZpointMetadata = function (objId, zpId) {
            var url = urlSubscrContObjects;
//            if (isSystemuser()){
//                url = urlRmaContObjects;
//            }
            url += '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix;
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions);
        };
        var getZpointMetaMeasureUnits = function (objId, zpId) {
//console.log("getZpointMetaMeasureUnits");            
            var url = urlSubscrContObjects;
//            if (isSystemuser()){
//                url = urlRmaContObjects;
//            }
            url += '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix + '/measureUnits';
            if (isCancelParamsIncorrect() === true) {
                return null;
            }
            return $http.get(url, httpOptions)
                .then(
                    function (resp) {
                        zpointMetadataMeasures = {};
                        zpointMetadataMeasures.all = resp.data;
    //                    for (var measU in zpointMetadataMeasures.all){
    //                        getRmaMetadataMeasureUnit(url, measU, zpointMetadataMeasures);
    //                    };
                        zpointMetadataMeasures.all.forEach(function (elem, ind) {
                            getRmaMetadataMeasureUnit(url, ind, zpointMetadataMeasures);
                        });
                        $rootScope.$broadcast('objectSvc:zpointMetadataMeasuresLoaded');
                    },
                    function (e) {
                        console.log(e);
                    }
                );
        };
                 
        var saveZpointMetadata = function (objId, zpId, metadata) {
            var url = urlRmaContObjects + '/' + objId + '/zpoints/' + zpId + urlZpointMetaDataSuffix;
            return $http.put(url, metadata);
        };
//****************************************************************************************         
//Objects tree
//****************************************************************************************
        var loadTreeTemplates = function (url) {
            $http.get(url).then(function (resp) {
                rmaTreeTemplates = angular.copy(resp.data);
            }, function (e) {
                console.log(e);
            });
        };
                 
        var loadTreeTemplateItems = function (templateId) {
            return $http.get(rmaTreeTemplatesUrl + '/' + templateId + '/items');
        };
                 
        var createTree = function (tree) {
            return $http.post(controlTreesUrl, tree);
        };
        
        var loadTrees = function () {
            return $http.get(controlTreesUrl);
        };
        
        var loadTree = function (treeId) {
            return $http.get(controlTreesUrl + '/' + treeId);
        };
                 
        var updateTree = function (tree) {
            return $http.put(controlTreesUrl + '/' + tree.id, tree);
        };
                 
        var deleteTree = function (treeId) {
            return $http.delete(controlTreesUrl + '/' + treeId);
        };
                 
        var deleteTreeNode = function (treeId, nodeId) {
            return $http.delete(controlTreesUrl + '/' + treeId + '/node/' + nodeId);
        };
                 
        var loadObjectsByTreeNode = function (treeId, nodeId) {
            return $http.get(controlTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects');
        };
                 
        var putObjectsToTreeNode = function (treeId, nodeId, objIds) {
            return $http.put(controlTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects/add', objIds);
        };
        
        var releaseObjectsFromTreeNode = function (treeId, nodeId, objIds) {
            return $http.put(controlTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects/remove', objIds);
        };
                 
        var loadFreeObjectsByTree = function (treeId) {
            return $http.get(controlTreesUrl + '/' + treeId + '/contObjects/free');
        };
                 
        var loadSubscrTrees = function () {
            return $http.get(subscrTreesUrl);
        };
        
        var loadSubscrTree = function (treeId) {
            return $http.get(subscrTreesUrl + '/' + treeId);
        };
        
        var loadSubscrFreeObjectsByTree = function (treeId) {
            return $http.get(subscrTreesUrl + '/' + treeId + '/contObjects/free');
        };
                 
        var loadSubscrObjectsByTreeNode = function (treeId, nodeId) {
            return $http.get(subscrTreesUrl + '/' + treeId + '/node/' + nodeId + '/contObjects');
        };
                 
        var loadDefaultTreeSetting = function () {
            return $http.get(defaultTreeUrl);
        };

// **************************************************************
//      Device methods
// **************************************************************
        function isDirectDevice(device) {
            var result = false;
            if (angular.isDefined(device.activeDataSource) && (device.activeDataSource != null)) {
                if (angular.isDefined(device.activeDataSource.subscrDataSource) && (device.activeDataSource.subscrDataSource != null)) {
                    if (device.activeDataSource.subscrDataSource.dataSourceTypeKey == "DEVICE") {
                        result = true;
                    }
                }
            }
            return result;
        }
// **************************************************************
//     end Device methods
// ************************************************************** 
                 
// **************************************************************
//      Bulding types
// **************************************************************                 
        function loadBuildingTypes() {
            var url = urlBuildingTypes;
            $http.get(url).then(function (resp) {
                $rootScope.$broadcast(BROADCASTS.BUILDING_TYPES_LOADED);
                if (checkUndefinedNull(resp) || checkUndefinedNull(resp.data) || !angular.isArray(resp.data)) {
                    console.log("Building type list is empty!");
                    return false;
                }
                buildingTypes = angular.copy(resp.data);
            }, function (e) {
                console.log(e);
            });
        }
                 
        function loadBuildingCategories() {
            var url = urlBuildingCategories;
            $http.get(url).then(function (resp) {
                $rootScope.$broadcast(BROADCASTS.BUILDING_CATEGORIES_LOADED);
                if (checkUndefinedNull(resp) || checkUndefinedNull(resp.data) || !angular.isArray(resp.data)) {
                    console.log("Building category list is empty!");
                    return false;
                }
                buildingCategories = angular.copy(resp.data);
            }, function (e) {
                console.log(e);
            });
        }
// **************************************************************
//      Bulding types
// **************************************************************
                 
//****************************************************************************************
//Meter periods
//****************************************************************************************
        //get meter periods ?
        function loadObjectMeterPeriods() {
            var url = urlSubscrMeterPeriod;
            return $http.get(url);
        }
        //put meter periods 
        function setMeterPeriods(meterPeriodData) {
            var url = urlSubscrMeterPeriod;
            return $http.put(url, meterPeriodData);
        }
        // get meter period by contObjectId
        function getMeterPeriodByObject(contObjectId) {
            var url = urlSubscrContObjects + '/' + contObjectId + meterPeriodSuffix;
            return $http.get(url);
        }
        // put meter period by contObjectId
        function setMeterPeriodForObject(meterPeriodData) {
            var url = urlSubscrContObjects + '/' + meterPeriodData.contObjectId + meterPeriodSuffix;
            return $http.put(url, meterPeriodData);
        }
/**                 
    Heater types.
    
*/
        function getRecentHeaterTypes() {
            return recentHeaterTypes;
        }
                 
        function addRecentHeaterType(heaterType) {
            if (recentHeaterTypes.length >= RECENT_HEATER_TYPE_ARR_LENGTH) {
                recentHeaterTypes.shift();
            }
            recentHeaterTypes.push(heaterType);
        }
                 
        
        //service initialization
        var initSvc = function () {
            
            requestCanceler = $q.defer();
            httpOptions = {
                timeout: requestCanceler.promise
            };
            
            getVzletSystemListFromServer();
            getRmaDeviceMetadataMeasures();
            loadBuildingTypes();
            loadBuildingCategories();
            loadTreeTemplates(rmaTreeTemplatesUrl);
            loadData();
        };

        initSvc();
                    
        return {
            BROADCASTS,
            OBJECT_PER_SCROLL,
            addRecentHeaterType,
            createTree,
            deleteTree,
            deleteTreeNode,
            getAllDevices,
            getBuildingCategories,
            getBuildingTypes,
            getCityConsumingData,
            getCitiesConsumingData,
            getCmOrganizations,
            getCmOrganizationsWithId,
            getCurrentObject,
            getDatasourcesUrl,
            getDeviceModels,
            /*getDeviceModelTypes,*/
            getDeviceSchedulerSettings,
            getImpulseCounterTypes,
            getHeaterTypes,
            getMeterPeriodByObject,
            getObject,
            getObjectConsumingData,
            getObjectSettings,
            getDevicesByObject,
            getRecentHeaterTypes,
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
            isDirectDevice,            
            loadDefaultTreeSetting,
            loadDeviceById,
            loadFreeObjectsByTree,
            loadObjectsByTreeNode,
            loading,
            loadObjectMeterPeriods,
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
            setCurrentObject,
            setMeterPeriodForObject,
            setMeterPeriods,
            setObjectSettings,            
            sortObjectsByFullName,
            sortObjectsByFullNameEx,            
            sortObjectsByConObjectFullName,
            subscrSendDeviceToServer,
            updateTree
        };
    
}]);