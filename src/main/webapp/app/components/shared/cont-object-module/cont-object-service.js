/*global angular, console*/
(function () {
    'use strict';

    angular
        .module('contObjectModule')
        .service('contObjectService', Service);

    Service.$inject = ['$http', '$q', '$rootScope'];

    /* @ngInject */
    function Service($http, $q, $rootScope) {
        
        var CONT_OBJECT_SHORT_INFO_URL = '../api/subscr/cont-objects/short-info';
        var EVENTS = {
            CONT_OBJECTS_SHORT_INFO_LOADED: "contObjectService:contObjectsShortInfoLoaded"
        };
        var requestCanceler = null,
            httpOptions = null,
            contObjectsShortInfoArray = null,
            contObjectsShortInfoHashTable = {},
            contObjectsShortInfoLoading = false,
            contObjectsShortInfoLoaded = false;
        
        var svc = this;
        svc.getContObjectsShortInfoArray = getContObjectsShortInfoArray;
        svc.getContObjectShortInfoById = getContObjectShortInfoById;
        svc.getRequestCanceler = getRequestCanceler;
        svc.getShortinfoLoadedFlag = getShortinfoLoadedFlag;
        svc.loadContObjectsShortInfo = loadContObjectsShortInfo;
        svc.loadContObjectsShortInfoWrap = loadContObjectsShortInfoWrap;
        svc.setContObjectsShortInfoArray = setContObjectsShortInfoArray;        

        ////////////////

        function errorCallback(e) {
            console.log(e);
            contObjectsShortInfoLoading = false;
        }
        
        function getContObjectsShortInfoArray() {
            return contObjectsShortInfoArray;
        }
        
        function getContObjectShortInfoById(objId) {
            if (angular.isUndefined(objId) || !contObjectsShortInfoHashTable.hasOwnProperty(objId)) {
                return null;
            }
            return angular.copy(contObjectsShortInfoHashTable[objId]);
        }
        
        function getRequestCanceler() {
            return requestCanceler;
        }
        
        function getContObjectsShortInfoHashTable() {
            return contObjectsShortInfoHashTable;
        }
        
        function getShortinfoLoadedFlag() {
            return contObjectsShortInfoLoaded;
        }
        
        function loadContObjectsShortInfo() {
            var url = CONT_OBJECT_SHORT_INFO_URL;
            return $http.get(url, httpOptions);
        }
        
        function loadContObjectsShortInfoWrap() {
            contObjectsShortInfoLoading = true;
            contObjectsShortInfoLoaded = false;
            loadContObjectsShortInfo().then(successLoadContObjectsShortInfo, errorCallback);
        }
        
        function setContObjectsShortInfoArray(arr) {
            contObjectsShortInfoArray = arr;
        }
        
        function setContObjectsShortInfoHashTable(hashTbl) {
            contObjectsShortInfoHashTable = hashTbl;
        }
        
        function successLoadContObjectsShortInfo(resp) {
            contObjectsShortInfoLoading = false;
            if (resp === null || resp.data === null) {
                setContObjectsShortInfoArray(null);
                return false;
            }
            setContObjectsShortInfoArray(resp.data);
            //prepare hash table
            var tmpHashTable = {};
            resp.data.forEach(function (contObject) {
                tmpHashTable[contObject.contObjectId] = contObject;
            });
            setContObjectsShortInfoHashTable(tmpHashTable);
            contObjectsShortInfoLoaded = true;
//console.log(getContObjectsShortInfoHashTable());            
            $rootScope.$broadcast(EVENTS.CONT_OBJECTS_SHORT_INFO_LOADED);
        }
        
        //service initialization
        var initSvc = function () {
            
            requestCanceler = $q.defer();
            httpOptions = {
                timeout: requestCanceler.promise
            };            
        };
        initSvc();
    }
})();